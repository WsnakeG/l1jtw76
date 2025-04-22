package com.lineage.data.item_etcitem.shop;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.CardSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;

/**
 * 44192 三國日卡[獨特限量]
 */
public class VIP_Card_A5 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(VIP_Card_A5.class);

	/**
	 *
	 */
	private VIP_Card_A5() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new VIP_Card_A5();
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
		try {
			// 例外狀況:物件為空
			if (item == null) {
				return;
			}
			// 例外狀況:人物為空
			if (pc == null) {
				return;
			}

			if (!CardSet.START) {
				pc.sendPackets(new S_ServerMessage("\\fR目前未開放VIP道具使用"));
				return;
			}
			if (item.get_card_use() == 2) {// 到期
				pc.sendPackets(new S_ServerMessage("\\fT這張卡片已經到期!"));
				return;
			}
			if (item.get_card_use() == 1) {// 使用中
				pc.sendPackets(new S_ServerMessage("\\fT這張卡片已經開啟 正在使用中!"));
				return;
			}
			// 44133 三國[獨特限量卡]
			// 44192 三國日卡[獨特限量]
			final int[] itemids = new int[] { 44133, 44192 };
			for (final int itemid : itemids) {
				if (pc.getInventory().checkCardEquipped(itemid)) {
					final L1Item temp = ItemTable.get().getTemplate(itemid);
					// 已經開啟使用(相同道具)
					pc.sendPackets(new S_ServerMessage("\\fR你已經啟動了一個" + temp.getName()));
					return;
				}
			}

			final long time = System.currentTimeMillis();// 目前時間豪秒
			final long x1 = CardSet.USE_TIME2 * 60 * 60;// 指定耗用秒數
			final long x2 = x1 * 1000;// 轉為豪秒
			final long upTime = x2 + time;// 目前時間 加上指定耗用秒數

			// 時間數據
			final Timestamp ts = new Timestamp(upTime);
			item.set_time(ts);

			item.set_card_use(1);
			// 人物背包物品使用期限資料
			CharItemsTimeReading.get().addTime(item.getId(), ts);
			pc.sendPackets(new S_ItemName(item));

			CardSet.set_card_mode(pc, item);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
