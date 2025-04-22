package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
// import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 洗血藥水
 * 
 * @author dexc
 */
public class Level_Down extends ItemExecutor {

	/**
	 *
	 */
	private Level_Down() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Level_Down();
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
		if (pc.getLevel() > 1) {
			pc.setExp(125);// 玩家等级直接变成2级
			pc.onChangeExp();
			pc.sendPackets(new S_ServerMessage("\\aH體內一股神祕力量湧入，全身經脈開通了。"));
			// 刪除道具
			pc.getInventory().removeItem(item, 1);
			// 強制返回村莊
			L1Teleport.teleport(pc, 32800, 32927, (short) 800, pc.getHeading(), true); // 加入喝下洗血藥水返回市場中心

		} else {
			pc.sendPackets(new S_ServerMessage(79));// 没有任何事发生
		}
	}

}
