package com.lineage.server.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.lock.DwarfForClanReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.world.World;

/**
 * 血盟倉庫資料
 * 
 * @author dexc
 */
public class L1DwarfForClanInventory extends L1Inventory {

	public static final Log _log = LogFactory.getLog(L1DwarfForClanInventory.class);

	private static final long serialVersionUID = 1L;

	private final L1Clan _clan;

	public L1DwarfForClanInventory(final L1Clan clan) {
		_clan = clan;
	}

	/**
	 * 載入血盟倉庫資料
	 */
	@Override
	public synchronized void loadItems() {
		// System.out.println("加入血盟倉庫數據");
		try {
			final CopyOnWriteArrayList<L1ItemInstance> items = DwarfForClanReading.get()
					.loadItems(_clan.getClanName());
			if (items != null) {
				// System.out.println("加入血盟倉庫數據:"+items.size());
				_items = items;
				/*
				 * for (final L1ItemInstance item : _items) {
				 * System.out.println("加入血盟倉庫數據:"+item.getName()); }
				 */
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 加入血盟倉庫數據
	 */
	@Override
	public synchronized void insertItem(final L1ItemInstance item) {
		// System.out.println("加入血盟倉庫數據");
		try {
			DwarfForClanReading.get().insertItem(_clan.getClanName(), item);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 血盟倉庫資料更新(物品數量)
	 */
	@Override
	public synchronized void updateItem(final L1ItemInstance item) {
		// System.out.println("血盟倉庫資料更新(物品數量)");
		try {
			DwarfForClanReading.get().updateItem(item);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 血盟倉庫物品資料刪除
	 */
	@Override
	public synchronized void deleteItem(final L1ItemInstance item) {
		// System.out.println("血盟倉庫物品資料刪除");
		try {
			_items.remove(item);
			DwarfForClanReading.get().deleteItem(_clan.getClanName(), item);
			World.get().removeObject(item);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 血盟解散刪除全物件
	 */
	public synchronized void deleteAllItems() {
		// System.out.println("血盟解散刪除全物件");
		try {
			DwarfForClanReading.get().delUserItems(_clan.getClanName());
			_items.clear();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
