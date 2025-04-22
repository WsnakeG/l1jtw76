package com.lineage.data.item_etcitem;

import static com.lineage.server.model.skill.L1SkillId.LOWER_FLOOR_GREATER_DEFENSE_SCROLL;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 下層防禦強化卷軸 屍魂塔副本
 * 
 * @author XXX
 */
public class Battt_Reel extends ItemExecutor {
	public static ItemExecutor get() {
		return new Battt_Reel();
	}

	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (item == null) {
			return;
		}

		if (pc == null) {
			return;
		}

		if (pc.getMapId() >= 4001 && pc.getMapId() <= 4050) {
			if (!pc.hasSkillEffect(LOWER_FLOOR_GREATER_DEFENSE_SCROLL)) {// 尚未有此效果
				pc.setSkillEffect(LOWER_FLOOR_GREATER_DEFENSE_SCROLL, 600 * 1000);
				pc.getInventory().removeItem(item, 1L);
				pc.addAc(-50);// 防禦
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_InventoryIcon(5984, true, 4080, 4080, 600));
				pc.sendPacketsAll(new S_SkillSound(pc.getId(), 11101));
			} else {
				int time = pc.getSkillEffectTimeSec(LOWER_FLOOR_GREATER_DEFENSE_SCROLL);
				pc.sendPackets(new S_SystemMessage(item.getName() + " 剩餘時間(秒):" + time));
			}
		} else {
			pc.sendPackets(new S_SystemMessage("在此地區無法使用。"));// 在此地區無法使用。
		}
	}
}
