package com.lineage.server.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.lock.DwarfForElfReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;

/**
 * 精靈倉庫資料
 * 
 * @author dexc
 */
public class L1DwarfForElfInventory extends L1Inventory {

	private static final Log _log = LogFactory.getLog(L1DwarfForElfInventory.class);

	private static final long serialVersionUID = 1L;

	private final L1PcInstance _owner;

	public L1DwarfForElfInventory(final L1PcInstance owner) {
		_owner = owner;
	}

	/**
	 * 傳回該精靈倉庫資料
	 */
	@Override
	public void loadItems() {
		try {
			final CopyOnWriteArrayList<L1ItemInstance> items = DwarfForElfReading.get()
					.loadItems(_owner.getAccountName());

			if (items != null) {
				_items = items;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 加入精靈倉庫數據
	 */
	@Override
	public void insertItem(final L1ItemInstance item) {
		if (item.getCount() <= 0) {
			return;
		}
		try {
			DwarfForElfReading.get().insertItem(_owner.getAccountName(), item);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 精靈倉庫資料更新(物品數量)
	 */
	@Override
	public void updateItem(final L1ItemInstance item) {
		try {
			DwarfForElfReading.get().updateItem(item);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 精靈倉庫物品資料刪除
	 */
	@Override
	public void deleteItem(final L1ItemInstance item) {
		try {
			_items.remove(item);
			DwarfForElfReading.get().deleteItem(_owner.getAccountName(), item);
			World.get().removeObject(item);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
