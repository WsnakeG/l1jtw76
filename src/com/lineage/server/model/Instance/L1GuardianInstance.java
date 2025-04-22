package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1NpcTalkData;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.drop.DropShare;
import com.lineage.server.model.drop.DropShareExecutor;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_NpcChatShouting;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CalcExp;
import com.lineage.server.world.World;

/**
 * 對象:精靈守護神 控制項
 * 
 * @author daien
 */
public class L1GuardianInstance extends L1NpcInstance {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1GuardianInstance.class);

	private final Random _random = new Random();
	private final L1GuardianInstance _npc = this;

	/**
	 * @param template
	 */
	public L1GuardianInstance(final L1Npc template) {
		super(template);
	}

	/**
	 * 目標搜尋
	 */
	@Override
	public void searchTarget() {
		// 目標搜尋
		final L1PcInstance targetPlayer = searchTarget(this);

		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	private static L1PcInstance searchTarget(final L1GuardianInstance npc) {
		L1PcInstance targetPlayer = null;

		for (final L1PcInstance pc : World.get().getVisiblePlayer(npc)) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
			if ((pc.getCurrentHp() <= 0) || pc.isDead() || pc.isGm() || pc.isGhost()) {
				continue;
			}

			// 副本ID不相等
			if (npc.get_showId() != pc.get_showId()) {
				continue;
			}
			if (!pc.isInvisble() || npc.getNpcTemplate().is_agrocoi()) { // インビジチェック
				if (!pc.isElf()) { // エルフ以外
					targetPlayer = pc;
					// $804 人類，如果你重視你的生命現在就快離開這神聖的地方。
					npc.wideBroadcastPacket(new S_NpcChatShouting(npc, "$804"));
					break;

				} else if (pc.isElf() && pc.isWantedForElf()) {
					targetPlayer = pc;
					// $815 若殺害同族，必須以自己的生命贖罪。
					npc.wideBroadcastPacket(new S_NpcChat(npc, "$815"));
					break;
				}
			}
		}
		return targetPlayer;
	}

	/**
	 * 攻擊目標設置
	 */
	@Override
	public void setLink(final L1Character cha) {
		// 副本ID不相等
		if (get_showId() != cha.get_showId()) {
			return;
		}
		if ((cha != null) && _hateList.isEmpty()) {
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onAction(final L1PcInstance player) {
		try {
			if ((player.getType() == 2) && (player.getCurrentWeapon() == 0) && player.isElf()) {
				final L1AttackMode attack = new L1AttackPc(player, this);

				if (attack.calcHit()) {
					if (getNpcTemplate().get_npcId() == 70848) { // エント
						final int chance = _random.nextInt(100) + 1;
						if (chance <= 10) {
							player.getInventory().storeItem(40506, 1);
							player.sendPackets(new S_ServerMessage(143, "$755", "$794")); // \f1%0が%1をくれました。
						} else if ((chance <= 60) && (chance > 10)) {
							player.getInventory().storeItem(40507, 1);
							player.sendPackets(new S_ServerMessage(143, "$755", "$763")); // \f1%0が%1をくれました。
						} else if ((chance <= 70) && (chance > 60)) {
							player.getInventory().storeItem(40505, 1);
							player.sendPackets(new S_ServerMessage(143, "$755", "$770")); // \f1%0が%1をくれました。
						}
					}
					if (getNpcTemplate().get_npcId() == 70850) { // パン
						final int chance = _random.nextInt(100) + 1;
						if (chance <= 30) {
							player.getInventory().storeItem(40519, 5);
							player.sendPackets(new S_ServerMessage(143, "$753", "$760" + " (" + 5 + ")")); // \f1%0が%1をくれました。
						}
					}
					if (getNpcTemplate().get_npcId() == 70846) { // アラクネ
						final int chance = _random.nextInt(100) + 1;
						if (chance <= 30) {
							player.getInventory().storeItem(40503, 1);
							player.sendPackets(new S_ServerMessage(143, "$752", "$769")); // \f1%0が%1をくれました。
						}
					}
					attack.calcDamage();
				}
				attack.action();
				attack.commit();
			} else if ((getCurrentHp() > 0) && !isDead()) {
				final L1AttackMode attack = new L1AttackPc(player, this);
				if (attack.calcHit()) {
					attack.calcDamage();
				}
				attack.action();
				attack.commit();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		final int objid = getId();
		final L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(getNpcTemplate().get_npcId());
		final L1Object object = World.get().findObject(getId());
		final L1NpcInstance target = (L1NpcInstance) object;
		// final String htmlid = null;
		// final String[] htmldata = null;

		if (talking != null) {
			final int pcx = player.getX(); // PCのX座標
			final int pcy = player.getY(); // PCのY座標
			final int npcx = target.getX(); // NPCのX座標
			final int npcy = target.getY(); // NPCのY座標

			if ((pcx == npcx) && (pcy < npcy)) {
				setHeading(0);

			} else if ((pcx > npcx) && (pcy < npcy)) {
				setHeading(1);

			} else if ((pcx > npcx) && (pcy == npcy)) {
				setHeading(2);

			} else if ((pcx > npcx) && (pcy > npcy)) {
				setHeading(3);

			} else if ((pcx == npcx) && (pcy > npcy)) {
				setHeading(4);

			} else if ((pcx < npcx) && (pcy > npcy)) {
				setHeading(5);

			} else if ((pcx < npcx) && (pcy == npcy)) {
				setHeading(6);

			} else if ((pcx < npcx) && (pcy < npcy)) {
				setHeading(7);
			}
			broadcastPacketAll(new S_ChangeHeading(this));

			if (player.getLawful() < -1000) { // プレイヤーがカオティック
				player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
			} else {
				player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
			}
			// html表示パケット送信
			/*
			 * if (htmlid != null) { // htmlidが指定されている場合 if (htmldata != null) {
			 * // html指定がある場合は表示 player.sendPackets(new S_NPCTalkReturn(objid,
			 * htmlid, htmldata)); } else { player.sendPackets(new
			 * S_NPCTalkReturn(objid, htmlid)); } } else { if
			 * (player.getLawful() < -1000) { // プレイヤーがカオティック
			 * player.sendPackets(new S_NPCTalkReturn(talking, objid, 2)); }
			 * else { player.sendPackets(new S_NPCTalkReturn(talking, objid,
			 * 1)); } }
			 */

			// 動作暫停
			set_stop_time(REST_MILLISEC);
			setRest(true);
		}
	}

	/**
	 * 受攻擊hp減少計算
	 */
	@Override
	public void receiveDamage(L1Character attacker, final int damage) { // 攻撃でＨＰを減らすときはここを使用
		ISASCAPE = false;
		if ((attacker instanceof L1PcInstance) && (damage > 0)) {
			final L1PcInstance pc = (L1PcInstance) attacker;
			if ((pc.getType() == 2) && // 素手ならダメージなし
					(pc.getCurrentWeapon() == 0)) {
			} else {
				if ((getCurrentHp() > 0) && !isDead()) {
					if (damage >= 0) {
						if (attacker instanceof L1EffectInstance) { // 效果不列入目標
							// this.setHate(attacker, damage);

						} else if (attacker instanceof L1IllusoryInstance) { // 攻擊者是分身不列入目標(設置主人為目標)
							final L1IllusoryInstance ill = (L1IllusoryInstance) attacker;
							attacker = ill.getMaster();
							setHate(attacker, damage);

						} else {
							setHate(attacker, damage);
						}
						// this.setHate(attacker, damage);
					}
					if (damage > 0) {
						removeSkillEffect(FOG_OF_SLEEPING);
					}
					onNpcAI();
					// 互相幫助的判斷
					serchLink(pc, getNpcTemplate().get_family());
					if (damage > 0) {
						pc.setPetTarget(this);
					}

					final int newHp = getCurrentHp() - damage;
					if ((newHp <= 0) && !isDead()) {
						setCurrentHpDirect(0);
						setDead(true);
						setStatus(ActionCodes.ACTION_Die);

						final Death death = new Death(attacker);
						GeneralThreadPool.get().execute(death);
					}
					if (newHp > 0) {
						setCurrentHp(newHp);
					}
				} else if (!isDead()) { // 念のため
					setDead(true);
					setStatus(ActionCodes.ACTION_Die);

					final Death death = new Death(attacker);
					GeneralThreadPool.get().execute(death);
				}
			}
		}
	}

	@Override
	public void setCurrentHp(final int i) {
		final int currentHp = Math.min(i, getMaxHp());

		if (getCurrentHp() == currentHp) {
			return;
		}

		setCurrentHpDirect(currentHp);

		/*
		 * if (this.getMaxHp() > this.getCurrentHp()) {
		 * this.startHpRegeneration(); }
		 */
	}

	@Override
	public void setCurrentMp(final int i) {
		final int currentMp = Math.min(i, getMaxMp());

		if (getCurrentMp() == currentMp) {
			return;
		}

		setCurrentMpDirect(currentMp);

		/*
		 * if (this.getMaxMp() > this.getCurrentMp()) {
		 * this.startMpRegeneration(); }
		 */
	}

	/**
	 * 死亡判斷
	 * 
	 * @author daien
	 */
	class Death implements Runnable {

		L1Character _lastAttacker;

		/**
		 * 死亡判斷
		 * 
		 * @param lastAttacker 攻擊者
		 */
		public Death(final L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			final int targetobjid = getId();
			getMap().setPassable(getLocation(), true);
			broadcastPacketAll(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));

			L1PcInstance player = null;

			// 判斷主要攻擊者
			if (_lastAttacker instanceof L1PcInstance) {// 攻擊者是玩家
				player = (L1PcInstance) _lastAttacker;

			} else if (_lastAttacker instanceof L1PetInstance) {// 攻擊者是寵物
				player = (L1PcInstance) ((L1PetInstance) _lastAttacker).getMaster();

			} else if (_lastAttacker instanceof L1SummonInstance) {// 攻擊者是 召換獸
				player = (L1PcInstance) ((L1SummonInstance) _lastAttacker).getMaster();

			} else if (_lastAttacker instanceof L1IllusoryInstance) {// 攻擊者是 分身
				player = (L1PcInstance) ((L1IllusoryInstance) _lastAttacker).getMaster();

			} else if (_lastAttacker instanceof L1EffectInstance) {// 攻擊者是 技能物件
				player = (L1PcInstance) ((L1EffectInstance) _lastAttacker).getMaster();
			}

			if (player != null) {
				final ArrayList<L1Character> targetList = L1GuardianInstance.this._hateList
						.toTargetArrayList();
				final ArrayList<Integer> hateList = L1GuardianInstance.this._hateList.toHateArrayList();
				final long exp = getExp();
				CalcExp.calcExp(player, targetobjid, targetList, hateList, exp);

				final ArrayList<L1Character> dropTargetList = L1GuardianInstance.this._dropHateList
						.toTargetArrayList();
				final ArrayList<Integer> dropHateList = L1GuardianInstance.this._dropHateList
						.toHateArrayList();
				try {
					// XXX
					final DropShareExecutor dropShareExecutor = new DropShare();
					dropShareExecutor.dropShare(_npc, dropTargetList, dropHateList);

				} catch (final Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
				// カルマは止めを刺したプレイヤーに設定。ペットorサモンで倒した場合も入る。
				player.addKarma((int) (getKarma() * ConfigRate.RATE_KARMA));
			}
			setDeathProcessing(false);

			setKarma(0);
			setExp(0);
			allTargetClear();

			startDeleteTimer(ConfigAlt.NPC_DELETION_TIME);
		}
	}

	@Override
	public void onFinalAction(final L1PcInstance player, final String action) {
	}

	public void doFinalAction(final L1PcInstance player) {
	}
}
