package com.lineage.data.item_etcitem.shop;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 魔武機率擴充<BR>
 * 
 * @author simlin
 */
public class Extra_Random extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Extra_Random.class);

	/**
	 *
	 */
	private Extra_Random() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Extra_Random();
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
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		try {
			// 對象OBJID
			final int targObjId = data[0];
			final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
			if (tgItem == null) {
				return;
			}
			// 非武器類型
			if (tgItem.getItem().getType2() != 1) {
				// 沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			
			if (tgItem.get_extra_random() >= 100) {
				pc.sendPackets(new S_SystemMessage("\\aH魔法發動機已提升是最高階段，不能升階了！"));
				return;
			}
			
			// 刪除物件
			pc.getInventory().removeItem(item, 1);

			final Random rnd = new Random();
			if ((rnd.nextInt(1000) + 1) <= _success) {
				tgItem.set_extra_random(tgItem.get_extra_random() + _add);
				CharItemsReading.get().updateItemRandom(tgItem);
				pc.sendPackets(new S_ItemStatus(tgItem));
			} else {
				pc.sendPackets(new S_SystemMessage("\\aD魔法發動機率擴充失敗。"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 擴充的機率
	private int _success;

	// 增加的機率
	private int _add;

	@Override
	public void set_set(final String[] set) {
		try {
			_success = Integer.parseInt(set[1]);
			_add = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
	}
}
