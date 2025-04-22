package com.lineage.data.item_etcitem.magicreel;

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
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 49335 火之徽章
 */
public class FireBadge extends ItemExecutor {

	private final Random _random = new Random();

	/**
	 *
	 */
	private FireBadge() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new FireBadge();
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
		if (pc == null) {
			return;
		}
		if (item == null) {
			return;
		}

		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		if (pc.isInvisble()) {// 隱身狀態
			pc.delInvis(); // 解除隱身状態
		}

		// 送出動作封包
		pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));

		// 可用次數
		final int chargeCount = item.getChargeCount();

		if (chargeCount <= 0) {
			if (pc.getInventory().removeItem(item, 1) == 1) {
				// 154：\f1這個卷軸散開了。
				// 79 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(154));
			}

		} else {
			// 建立新次數(可用次數-1)
			item.setChargeCount(item.getChargeCount() - 1);
			// 更新可用次數
			pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);

			final int x = pc.getX();
			final int y = pc.getY();
			final int mapId = pc.getMapId();

			pc.sendPacketsXR(new S_EffectLocation(new L1Location(x + 1, y + 1, mapId), 1819), 7);
			pc.sendPacketsXR(new S_EffectLocation(new L1Location(x - 1, y + 1, mapId), 1819), 7);
			pc.sendPacketsXR(new S_EffectLocation(new L1Location(x - 1, y - 1, mapId), 1819), 7);
			pc.sendPacketsXR(new S_EffectLocation(new L1Location(x + 1, y - 1, mapId), 1819), 7);

			final L1PcInstance tgpc = pc.getNowTarget();
			if (tgpc != null) {
				if (!tgpc.isSafetyZone()) {
					// 被攻擊者受傷(傷害為160-220)
					final double dmg = (_random.nextInt(60) + 160) * 1.0D;
					tgpc.receiveDamage(pc, dmg, false, false);
					// 週邊對象血盟成員傷害
					for (final L1PcInstance tgClanPc : World.get().getVisiblePlayer(tgpc, 4)) {
						if (tgClanPc.getClanid() == tgpc.getClanid()) {
							if (!tgpc.isSafetyZone()) {
								// 被攻擊者受傷
								tgClanPc.receiveDamage(pc, (dmg * 0.8), false, false);
								// 受傷動作
								tgClanPc.broadcastPacketX8(
										new S_DoActionGFX(tgClanPc.getId(), ActionCodes.ACTION_Damage));
							}
						}
					}
				}
			}

			// 週邊對象MOB傷害
			for (final L1Object object : World.get().getVisibleObjects(pc, 5)) {
				if (object instanceof L1MonsterInstance) {
					final L1MonsterInstance mob = (L1MonsterInstance) object;
					// 被攻擊者受傷(傷害為160-220)
					final int dmg = _random.nextInt(60) + 160;
					// 被攻擊者受傷
					mob.receiveDamage(pc, dmg);
					// 受傷動作
					mob.broadcastPacketX8(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Damage));
				}
			}
		}
	}
}
