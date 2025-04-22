package com.lineage.data.item_etcitem.html;

import nick.forMYSQL.ControlTeleportNumber;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 輔助功能開關
 * 
 * @author Nick
 */
// update 20240822

public class Nick_AssistControl extends ItemExecutor {

	private Nick_AssistControl() {
	}

	public static ItemExecutor get() {
		return new Nick_AssistControl();
	}

	public void execute(final int[] arg0, final L1PcInstance pc, final L1ItemInstance item) {

		// 如果角色為空
		if (pc == null) {
			return;
		}

		// 如果道具為空
		if (item == null) {
			return;
		}

		// 如果角色為商店狀態
		if (pc.isPrivateShop()) {
			return;
		}

		// 如果角色為釣魚狀態
		if (pc.isFishing()) {
			return;
		}

		// 如果角色正在交易中
		if (pc.getTradeID() != 0) {
			L1Trade trade = new L1Trade();
			trade.tradeCancel(pc);
		}
		
		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "N_Assist"));
	}
}
