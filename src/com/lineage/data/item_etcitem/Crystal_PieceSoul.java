package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>靈魂水晶40576</font><BR>
 * <font color=#00800>靈魂水晶40577</font><BR>
 * <font color=#00800>靈魂水晶40578</font><BR>
 * Crystal Piece of Soul
 * 
 * @see 使用者死亡 並產生任務道具
 * @author dexc
 */
public class Crystal_PieceSoul extends ItemExecutor {

	/**
	 *
	 */
	private Crystal_PieceSoul() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Crystal_PieceSoul();
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
		final int itemId = item.getItemId();
		// 可使用判斷
		boolean notUse = false;
		// 物件編號
		switch (itemId) {

		case 40576:// 靈魂水晶(白)妖精
			if (!pc.isElf()) {
				notUse = true;
			}
			break;

		case 40577:// 靈魂水晶(黑)法師
			if (!pc.isWizard()) {
				notUse = true;
			}
			break;

		case 40578:// 靈魂水晶(紅)騎士
			if (!pc.isKnight()) {
				notUse = true;
			}
			break;
		}

		if (notUse) {
			// 264 \f1你的職業無法使用此裝備。
			pc.sendPackets(new S_ServerMessage(264));

		} else {

			final String itenName = item.getLogName();

			if (pc.castleWarResult() == true) { // 戰爭中
				// 330 \f1無法使用 %0%o。
				pc.sendPackets(new S_ServerMessage(403, itenName));

			} else if (pc.getMapId() == 303) { // 夢幻之島
				// 330 \f1無法使用 %0%o。
				pc.sendPackets(new S_ServerMessage(403, itenName));

			} else {
				// 使用者死亡
				pc.death(null);
				// 刪除道具
				pc.getInventory().removeItem(item, 1);
				final int newItemId = item.getItemId() - 3;
				// 取得任務道具
				CreateNewItem.createNewItem(pc, newItemId, 1);
			}
		}
	}
}
