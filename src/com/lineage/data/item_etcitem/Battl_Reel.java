package com.lineage.data.item_etcitem;

import static com.lineage.server.model.skill.L1SkillId.LOWER_FLOOR_GREATER_BATTLE_SCROLL;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 下層戰鬥強化卷軸 屍魂塔副本
 * 
 * @author XXX
 */
public class Battl_Reel extends ItemExecutor {
	public static ItemExecutor get() {
		return new Battl_Reel();
	}

	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (item == null) {
			return;
		}

		if (pc == null) {
			return;
		}

		if (pc.getMapId() >= 4001 && pc.getMapId() <= 4050) {
			if (!pc.hasSkillEffect(LOWER_FLOOR_GREATER_BATTLE_SCROLL)) {// 尚未有此效果
				pc.setSkillEffect(LOWER_FLOOR_GREATER_BATTLE_SCROLL, 600 * 1000);
				pc.getInventory().removeItem(item, 1L);
				pc.addHitup(30); // 攻擊成功 +30
				pc.addDmgup(30); // 額外攻擊點數 +30
				pc.addBowHitup(30); // 遠距離命中率 +30
				pc.addBowDmgup(30); // 遠距離攻擊力 +30
				pc.addSp(30); // 魔攻 +30
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_InventoryIcon(5985, true, 4067, 4067, 600));
				pc.sendPacketsAll(new S_SkillSound(pc.getId(), 11101));
			} else {
				int time = pc.getSkillEffectTimeSec(LOWER_FLOOR_GREATER_BATTLE_SCROLL);
				pc.sendPackets(new S_SystemMessage(item.getName() + " 剩餘時間(秒):" + time));
			}
		} else {
			pc.sendPackets(new S_SystemMessage("在此地區無法使用。"));// 在此地區無法使用。
		}
	}

}
