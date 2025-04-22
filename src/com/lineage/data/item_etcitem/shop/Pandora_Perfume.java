package com.lineage.data.item_etcitem.shop;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 潘朵拉的6種香水<BR>
 * 
 * @author simlin
 */
public class Pandora_Perfume extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Pandora_Perfume.class);

	/**
	 *
	 */
	private Pandora_Perfume() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Pandora_Perfume();
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
			// 非T恤類型
			if ((tgItem.getItemId() < 20084) || (tgItem.getItemId() > 20085)) {
				// 沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			// 刪除物件
			pc.getInventory().removeItem(item, 1);

			final long time = System.currentTimeMillis();// 目前時間豪秒
			final long x1 = 2592000;// 指定耗用秒數(30D)
			final long x2 = x1 * 1000;// 轉為豪秒
			final long upTime = x2 + time;// 目前時間 加上指定耗用秒數
			final Timestamp ts = new Timestamp(upTime);// 時間數據

			boolean update = false;
			if (tgItem.get_time() != null) {
				update = true;
			}

			if (update) {
				CharItemsTimeReading.get().updateTime(tgItem.getId(), ts);
			} else {
				CharItemsTimeReading.get().addTime(tgItem.getId(), ts);
			}
			tgItem.set_time(ts);

			if ((tgItem.get_time() != null) && (tgItem.get_pandora_type() > 0) && tgItem.isEquipped()) {
				tgItem.set_pandora_buff(pc, false);
			}

			tgItem.set_pandora_type(pc, _mode);
			CharItemsReading.get().updateItemPandoraType(tgItem);

			if (tgItem.isEquipped()) {
				tgItem.set_pandora_buff(pc, true);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 1~6種香水
	private int _mode;

	/**
	 * 1:str<Br>
	 * 2:dex<Br>
	 * 3:int<Br>
	 * 4:wis<Br>
	 * 5:con<Br>
	 * 6:cha<Br>
	 */
	@Override
	public void set_set(final String[] set) {
		try {
			_mode = Integer.parseInt(set[1]);

		} catch (final Exception e) {
		}
	}
}
