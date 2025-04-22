package com.lineage.server.timecontroller.skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1AttackList;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1EffectType;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldEffect;

/**
 * 技能NPC狀態送出時間軸 法師技能(火牢)
 * 
 * @author dexc
 */
public class EffectFirewallTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(EffectFirewallTimer.class);

	private ScheduledFuture<?> _timer;

	private static Random _random = new Random();

	public void start() {
		final int timeMillis = L1EffectInstance.FW_DAMAGE_INTERVAL;
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1EffectInstance> allNpc = WorldEffect.get().all();
			// 不包含元素
			if (allNpc.isEmpty()) {
				return;
			}

			for (final Iterator<L1EffectInstance> iter = allNpc.iterator(); iter.hasNext();) {
				final L1EffectInstance effect = iter.next();
				// 不是法師技能(火牢)
				if (effect.effectType() != L1EffectType.isFirewall) {
					continue;
				}
				// 計算結果
				firewall(effect);
				Thread.sleep(1);
			}

		} catch (final Exception e) {
			_log.error("Npc L1Effect法師技能(火牢)狀態送出時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final EffectFirewallTimer firewallTimer = new EffectFirewallTimer();
			firewallTimer.start();
		}
	}

	/**
	 * 計算結果
	 * 
	 * @param effect
	 */
	private static void firewall(final L1EffectInstance effect) {
		try {
			// 取回火牢使用者
			final L1PcInstance user = (L1PcInstance) effect.getMaster();

			// 取回目標清單
			final ArrayList<L1Character> list = WorldEffect.get().getFirewall(effect);

			for (final L1Character object : list) {
				// 副本ID不相等
				if (effect.get_showId() != object.get_showId()) {
					continue;
				}
				// 對象是PC
				if (object instanceof L1PcInstance) {
					final L1PcInstance tgpc = (L1PcInstance) object;
					topc(user, tgpc);

					// 對象是怪物
				} else if (object instanceof L1MonsterInstance) {
					final L1MonsterInstance tgmob = (L1MonsterInstance) object;
					tonpc(user, tgmob);
				}
			}

		} catch (final Exception e) {
			_log.error("Npc L1Effect法師技能(火牢)狀態送出時間軸發生異常", e);
			effect.deleteMe();
		}
	}

	/**
	 * 對NPC的傷害
	 * 
	 * @param user 施展者
	 * @param objects 對象
	 */
	private static void tonpc(final L1PcInstance user, final L1MonsterInstance tgmob) {
		// 傷害為0
		if (dmg0(tgmob)) {
			return;
		}

		double attrDeffence = 0;// 傷害減免

		final int weakAttr = tgmob.getFire();
		if (weakAttr > 0) {
			attrDeffence = calcAttrResistance(weakAttr);
		}

		final int srcDmg = 19 + _random.nextInt(Math.max(user.getInt() / 2, 1));
		int damage = (int) ((1.0 - attrDeffence) * srcDmg);

		damage = Math.max(damage, 0);

		if (damage <= 0) {
			return;
		}

		tgmob.broadcastPacketX10(new S_DoActionGFX(tgmob.getId(), ActionCodes.ACTION_Damage));
		// 火牢傷害計算直接傳回施展者
		tgmob.receiveDamage(user, damage);
	}

	/**
	 * 對PC的傷害
	 * 
	 * @param user
	 * @param tgpc
	 */
	private static void topc(final L1PcInstance user, final L1PcInstance tgpc) {
		// 相同血盟
		if (user.getClanid() != 0) {
			if (tgpc.getClanid() == user.getClanid()) {
				return;
			}
		}
		// 安全區中
		if (tgpc.isSafetyZone()) {
			return;
		}
		// 傷害為0
		if (dmg0(tgpc)) {
			return;
		}

		double attrDeffence = 0;// 傷害減免

		final int weakAttr = tgpc.getFire();
		if (weakAttr > 0) {
			attrDeffence = calcAttrResistance(weakAttr);
		}

		final int srcDmg = 19 + _random.nextInt(Math.max(user.getInt() / 2, 1));
		int damage = (int) ((1.0 - attrDeffence) * srcDmg);

		damage = Math.max(damage, 0);

		boolean dmgX2 = false;// 傷害除2

		// 取回技能
		if (!tgpc.getSkillisEmpty() && (tgpc.getSkillEffect().size() > 0)) {
			try {
				for (final Integer skillid : L1AttackList.SKD3.keySet()) {
					if (tgpc.hasSkillEffect(skillid)) {
						final Integer integer = L1AttackList.SKD3.get(skillid);
						if (integer != null) {
							if (integer.equals(skillid)) {
								// 技能編號與返回值相等
								dmgX2 = true;

							} else {
								damage += integer;
							}
						}
					}
				}

			} catch (final ConcurrentModificationException e) {
				// 技能取回發生其他線程進行修改
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		if (dmgX2) {
			damage /= 2;
		}

		if (damage <= 0) {
			return;
		}

		tgpc.sendPacketsAll(new S_DoActionGFX(tgpc.getId(), ActionCodes.ACTION_Damage));
		// 火牢傷害計算直接傳回施展者
		tgpc.receiveDamage(user, damage, false, true);
	}

	/**
	 * 抗火屬性傷害減低 attr:0.無属性魔法,1.地魔法,2.火魔法,4.水魔法,8.風魔法(,16.光魔法)
	 */
	private static double calcAttrResistance(final int resist) {
		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;

		} else {
			resistFloor *= -1;
		}

		final double attrDeffence = resistFloor / 32.0;

		return attrDeffence;
	}

	/**
	 * 傷害為0
	 * 
	 * @param pc
	 * @return true 傷害為0
	 */
	private static boolean dmg0(final L1Character character) {
		try {
			if (character == null) {
				return false;
			}

			if (character.getSkillisEmpty()) {
				return false;
			}

			if (character.getSkillEffect().size() <= 0) {
				return false;
			}

			for (final Integer key : character.getSkillEffect()) {
				final Integer integer = L1AttackList.SKM0.get(key);
				if (integer != null) {
					return true;
				}
			}

		} catch (final ConcurrentModificationException e) {
			// 技能取回發生其他線程進行修改
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return false;
	}
}
