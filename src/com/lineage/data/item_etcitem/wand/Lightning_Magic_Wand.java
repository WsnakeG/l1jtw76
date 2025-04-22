package com.lineage.data.item_etcitem.wand;

import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.world.World;

/**
 * 闪电魔杖40007
 */
public class Lightning_Magic_Wand extends ItemExecutor {

	private static int _gfxid = 10;// 閃電動畫編號

	/**
	 *
	 */
	private Lightning_Magic_Wand() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Lightning_Magic_Wand();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		final int spellsc_objid = data[0];
		final int spellsc_x = data[1];
		final int spellsc_y = data[2];

		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		final int chargeCount = item.getChargeCount();
		if (chargeCount <= 0) {
			// 没有任何事情发生。
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}

		final L1Object target = World.get().findObject(spellsc_objid);

		if (target != null) {
			doWandAction(pc, target);

		} else {
			pc.sendPacketsXR(
					new S_EffectLocation(new L1Location(spellsc_x, spellsc_y, pc.getMapId()), _gfxid), 7);
		}

		item.setChargeCount(item.getChargeCount() - 1);
		pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
	}

	private void doWandAction(final L1PcInstance user, final L1Object target) {
		final Random _random = new Random();
		if (user.getId() == target.getId()) {
			return; // 攻击对象为使用者
		}
		if (user.glanceCheck(target.getX(), target.getY()) == false) {
			return; // 两人物间有阻碍物
		}

		int dmg = (_random.nextInt(11) - 5) + user.getInt();
		dmg = Math.max(1, dmg);

		if (target instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) target;
			if (pc.getMap().isSafetyZone(pc.getLocation()) || user.checkNonPvP(user, pc)) {
				return;
			}
			if ((pc.hasSkillEffect(50) == true) || (pc.hasSkillEffect(78) == true)
					|| (pc.hasSkillEffect(157) == true)) {
				return;
			}

			final int newHp = pc.getCurrentHp() - dmg;

			if (newHp <= 0) {
				if (!pc.isGm()) {
					pc.death(user); // HP小於1死亡
				}
			}
			pc.setCurrentHp(newHp);

		} else if (target instanceof L1MonsterInstance) {
			final L1MonsterInstance mob = (L1MonsterInstance) target;
			switch (mob.getNpcId()) {
			case 71100:// 死亡後召喚 91069
			case 91072:// 中秋(活動)月兔
				user.sendPacketsXR(new S_EffectLocation(
						new L1Location(target.getX(), target.getY(), user.getMapId()), _gfxid), 7);
				return;
			}
			mob.receiveDamage(user, dmg);

		}

		// 重新設置面向
		user.setHeading(user.targetDirection(target.getX(), target.getY()));
		user.sendPacketsX10(new S_ChangeHeading(user));
		// 送出封包(動作)
		user.sendPacketsX10(new S_DoActionGFX(user.getId(), ActionCodes.ACTION_Wand));

		// 目標物件是PC
		if (target instanceof L1PcInstance) {
			final L1PcInstance tgpc = (L1PcInstance) target;
			// 產生動畫
			tgpc.sendPacketsX10(new S_SkillSound(tgpc.getId(), _gfxid));
			// 送出封包(受傷動作)
			tgpc.sendPacketsX10(new S_DoActionGFX(tgpc.getId(), ActionCodes.ACTION_Damage));

		} else if (target instanceof L1MonsterInstance) {
			final L1MonsterInstance mob = (L1MonsterInstance) target;
			// 產生動畫
			mob.broadcastPacketX10(new S_SkillSound(mob.getId(), _gfxid));
			// 送出封包(受傷動作)
			mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Damage));

		} else {
			// 產生動畫(地點)
			user.sendPacketsXR(new S_EffectLocation(
					new L1Location(target.getX(), target.getY(), user.getMapId()), _gfxid), 7);
		}
	}
}
