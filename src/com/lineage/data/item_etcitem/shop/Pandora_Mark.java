package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 潘朵拉的9種紋樣<BR>
 * 
 * @author simlin
 */
public class Pandora_Mark extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Pandora_Mark.class);

	/**
	 *
	 */
	private Pandora_Mark() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Pandora_Mark();
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
			// 非T恤類型(且無香水狀態)
			if ((tgItem.getItemId() < 20084) || (tgItem.getItemId() > 20085)
					|| (tgItem.get_pandora_type() < 0)) {
				// 沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			// 刪除物件
			pc.getInventory().removeItem(item, 1);

			if ((tgItem.get_time() != null) && (tgItem.get_pandora_mark() > 0) && tgItem.isEquipped()) {
				tgItem.set_pandora_markbuff(pc, false);
			}

			tgItem.set_pandora_mark(pc, _mode);
			CharItemsReading.get().updateItemPandoraType(tgItem);

			if (tgItem.isEquipped()) {
				tgItem.set_pandora_markbuff(pc, true);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 1~9種紋樣
	private int _mode;

	/**
	 * 1:4屬性加成<Br>
	 * 2:昏迷<Br>
	 * 3:支撐<Br>
	 * 4:石化<Br>
	 * 5:體魔回復<Br>
	 * 6:防禦<Br>
	 * 7:魔法防禦<Br>
	 * 8:體力加成<Br>
	 * 9:魔力加成<Br>
	 */
	@Override
	public void set_set(final String[] set) {
		try {
			_mode = Integer.parseInt(set[1]);

		} catch (final Exception e) {
		}
	}
}
