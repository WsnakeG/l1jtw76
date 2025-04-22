package com.lineage.data.item_etcitem.teleport;

import nick.AutoControl.AutoAttackUpdate;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.Shutdown;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 掛機開關
 * 
 * @author Nick
 */
public class Nick_AutoMatic extends ItemExecutor {

	private Nick_AutoMatic() {

	}

	public static ItemExecutor get() {

		return new Nick_AutoMatic();
	}

	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {

		if (item == null) {
			return;
		}

		if (pc == null) {
			return;
		}

		if (pc.isPrivateShop()) {
			return;
		}

		if (pc.isFishing()) {
			return;
		}

		if (pc.getTradeID() != 0) {
			L1Trade trade = new L1Trade();
			trade.tradeCancel(pc);
		}
		
//		if (Shutdown.isSHUTDOWN) {
//			pc.sendPackets(new S_SystemMessage("目前服務器準備重啟狀態，無法使用。"));
//			return;
//		}

		AutoAttackUpdate.get().StartMsg(pc);

	}

}
