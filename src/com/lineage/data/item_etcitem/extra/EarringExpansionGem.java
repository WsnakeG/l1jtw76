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
public class EarringExpansionGem extends ItemExecutor {

	/**
	 *
	 */
	private EarringExpansionGem() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new EarringExpansionGem();
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

		if (pc.getQuest().get_step(58001) != 1 && pc.getLevel() >= 59) {	
			pc.getQuest().set_step(58001, 1);
			pc.sendPackets(new S_SystemMessage("您已經成功擴充[耳環欄位]。"));
			pc.getInventory().removeItem(item, 1);		
		} else if (pc.getLevel() < 59) {
			pc.sendPackets(new S_SystemMessage("等級不足。"));
		} else if (pc.getQuest().get_step(58001) == 1) {
			pc.sendPackets(new S_SystemMessage("您已擴充過耳環欄位。"));
		}
	}
}	
