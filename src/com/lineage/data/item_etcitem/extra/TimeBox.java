package com.lineage.data.item_etcitem.extra;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemBoxTable;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Box;

/**
 * 時間寶箱判斷
 * 
 * @author terry0412
 */
public class TimeBox extends ItemExecutor {

	/**
	 *
	 */
	private TimeBox() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new TimeBox();
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
		// 容量確認
		if (pc.getInventory().getSize() >= 160) {
			// \f1一個角色最多可攜帶180個道具。
			pc.sendPackets(new S_ServerMessage(263));
			return;
		}

		if (pc.getInventory().getWeight240() >= 180) {
			// 此物品太重了，所以你無法攜帶。
			pc.sendPackets(new S_ServerMessage(82));
			return;
		}

		if (_isRemovable) {
			// 若剩餘次數剩餘1次，再次使用後將自動刪除道具
			if (item.getChargeCount() > 1) {
				item.setChargeCount(item.getChargeCount() - 1);
				pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);

			} else {
				pc.getInventory().removeItem(item, 1);
			}
		}

		final ArrayList<L1Box> list = ItemBoxTable.get().get(pc, item);
		// 成功特效
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_st));
		if (list == null) {
			ItemBoxTable.get().get_all2(pc, item);
		}

		if (item.getItem().get_delayEffect() > 0) {
			final Timestamp ts = new Timestamp(System.currentTimeMillis());
			// 設置使用時間
			item.setLastUsed(ts);
			pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
			pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);
		}
	}

	private boolean _isRemovable;
	private int _gfxid_st; // 成功特效編號

	@Override
	public void set_set(final String[] set) {
		try {
			_isRemovable = Boolean.parseBoolean(set[1]);
			_gfxid_st = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
	}
}
