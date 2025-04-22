package com.lineage.data.item_etcitem.itemskill;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;

public class ItemSpiritCrystalSC extends ItemExecutor {

	private ItemSpiritCrystalSC() {}

	public static ItemExecutor get() {
		return new ItemSpiritCrystalSC();
	}

	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		if (pc == null || item == null) return;

		if (pc.isInvisble() || pc.isInvisDelay()) {
			pc.sendPackets(new S_ServerMessage(281));
			return;
		}

		final int targetID = data[0];
		final int spellsc_x = data[1];
		final int spellsc_y = data[2];

		if ((targetID == 0) || (targetID == pc.getId())) {
			pc.sendPackets(new S_ServerMessage(281));
			return;
		}

		// 從 classname 中解析 $參數與刪除旗標
		String className = item.getItem().getclassname(); // ex: ItemSpiritCrystalSC $1841 1
		int crystalId = -1;
		int deleteFlag = 1;

		try {
			String[] parts = className.split(" ");
			if (parts.length >= 2) {
				crystalId = Integer.parseInt(parts[1].replace("$", ""));
			}
			if (parts.length >= 3) {
				deleteFlag = Integer.parseInt(parts[2]);
			}
		} catch (Exception e) {
			pc.sendPackets(new S_ServerMessage("技能參數錯誤"));
			return;
		}

		// 對應技能 ID
		int skillId = -1;
		switch (crystalId) {
			case 1841: skillId = 152; break; // 地面障礙
			case 1846: skillId = 157; break; // 大地屏障
			case 1855: skillId = 167; break; // 風之枷鎖
			case 4717: skillId = 173; break; // 污濁之水
			case 4718: skillId = 174; break; // 精準射擊
			default:
				pc.sendPackets(new S_ServerMessage("無對應技能"));
				return;
		}

		// 正式施法
		//L1BuffUtil.cancelAbsoluteBarrier(pc); 解除絕對屏障
		new L1SkillUse().handleCommands(pc, skillId, targetID, spellsc_x, spellsc_y, 0, L1SkillUse.TYPE_SPELLSC);

		if (deleteFlag == 1) {
			pc.getInventory().removeItem(item, 1);
		}
		pc.sendPackets(new S_ServerMessage("手中的精靈水晶散發出耀眼的光芒"));
	}
}
