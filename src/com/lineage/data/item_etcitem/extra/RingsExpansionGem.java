package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 戒指擴充水晶
 * 
 * @author smile 改寫架構 150424 限制等級76可開左下戒指/限制等級81可開右下戒指
 */
public class RingsExpansionGem extends ItemExecutor {

	/**
	 *
	 */
	private RingsExpansionGem() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new RingsExpansionGem();
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
		// 檢查可擴充的戒指欄位 by terry0412
		int count = 0;

		if ((pc.getRingsExpansion() & 1) != 1) { // 左邊戒指
			count += 1;
		}

		if ((pc.getRingsExpansion() & 2) != 2) { // 右邊戒指
			count += 2;
		}

		// 沒有符合的欄位
		if (count == 0) {
			pc.sendPackets(new S_SystemMessage("很抱歉您!無法再擴充戒指欄位。"));
			return;
		}

		// 異常值保護處理 (最多為3) (左右均開啟)
		if ((count & 1) == 1) {
			if (pc.getLevel() < 76) {
				pc.sendPackets(new S_SystemMessage("您的等級未達76級。"));
				return;
			}
			pc.getInventory().removeItem(item, 1);
			pc.setRingsExpansion((byte) Math.min(pc.getRingsExpansion() + 1, 3));
			pc.sendPackets(new S_SystemMessage("您已經成功擴充 - [左邊戒指欄位]。"));
			final S_SkillSound sound = new S_SkillSound(pc.getId(), 12003);
			pc.getQuest().set_step(58003, 1);
			pc.sendPacketsX8(sound);

		} else {
			if (pc.getLevel() < 81) {
				pc.sendPackets(new S_SystemMessage("您的等級未達81級。"));
				return;
			}
			pc.getInventory().removeItem(item, 1);
			pc.setRingsExpansion((byte) Math.min(pc.getRingsExpansion() + 2, 3));
			pc.sendPackets(new S_SystemMessage("您已經成功擴充 - [右邊戒指欄位]。"));
			final S_SkillSound sound = new S_SkillSound(pc.getId(), 12003);
			pc.getQuest().set_step(58002, 1);
			pc.sendPacketsX8(sound);
		}

		// 更新戒指欄位擴充狀態
		CharacterTable.updateRingsExpansion(pc);
	}
}
