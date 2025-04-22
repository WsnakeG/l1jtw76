package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.shop.L1Shop;
import com.lineage.server.templates.L1ShopItem;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 商店販賣資料
 * 
 * @author dexc
 */
public class ShopTable {

	private static final Log _log = LogFactory.getLog(ShopTable.class);

	private static ShopTable _instance;

	// 銷售清單
	private static final Map<Integer, L1Shop> _allShops = new HashMap<Integer, L1Shop>();

	// 回收物品
	private static final Map<Integer, Integer> _allShopItem = new HashMap<Integer, Integer>();

	// 不回收的物品
	private static final Map<Integer, Integer> _noBuyList = new HashMap<Integer, Integer>();

	public static ShopTable get() {
		if (_instance == null) {
			_instance = new ShopTable();
		}
		return _instance;
	}

	public void restshop() {
		_allShops.clear();
		_allShopItem.clear();
		_noBuyList.clear();
		load();
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `shop` WHERE `npc_id`=? ORDER BY `item_id`");
			for (final int npcId : enumNpcIds()) {
				ps.setInt(1, npcId);

				rs = ps.executeQuery();

				final L1Shop shop = loadShop(npcId, rs);

				_allShops.put(npcId, shop);
				rs.close();
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs, ps, cn);
		}
		_log.info("載入商店販賣資料數量: " + _allShops.size() + "(" + timer.get() + "ms)");
	}

	private static ArrayList<Integer> enumNpcIds() {
		final ArrayList<Integer> ids = new ArrayList<Integer>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT DISTINCT `npc_id` FROM `shop`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				ids.add(rs.getInt("npc_id"));
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		return ids;
	}

	private static L1Shop loadShop(final int npcId, final ResultSet rs) throws SQLException {
		// 賣出清單
		final List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();

		// 買入清單
		final List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();

		while (rs.next()) {
			final int itemId = rs.getInt("item_id");

			if (ItemTable.get().getTemplate(itemId) == null) {
				_log.error("商店販賣資料錯誤: 沒有這個編號的道具:" + itemId + " 對應NPC編號:" + npcId);
				delete(npcId, itemId);
				continue;
			}
			final int sellingPrice = rs.getInt("selling_price");// 賣出金額
			final int purchasingPrice = rs.getInt("purchasing_price");// 回收金額

			int packCount = rs.getInt("pack_count");// 賣出數量
			// 強化等級 by terry0412
			final int enchantLevel = rs.getInt("enchant_level");
			
			int CheckClass = rs.getInt("check_class");
			
			// 加入出售物品價格查詢清單
			addSellList(itemId, sellingPrice, purchasingPrice, packCount);

			packCount = packCount == 0 ? 1 : packCount;

			if (0 <= sellingPrice) {
				final L1ShopItem item = new L1ShopItem(itemId, sellingPrice, packCount, enchantLevel, CheckClass);
				sellingList.add(item);
			}

			if (0 <= purchasingPrice) {
				final L1ShopItem item = new L1ShopItem(itemId, purchasingPrice, packCount, enchantLevel, CheckClass);
				purchasingList.add(item);
			}
		}
		return new L1Shop(npcId, sellingList, purchasingList);
	}

	/**
	 * 刪除錯誤資料
	 * 
	 * @param clan_id
	 */
	private static void delete(final int npc_id, final int item_id) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `shop` WHERE `npc_id`=? AND `item_id`=?");
			ps.setInt(1, npc_id);
			ps.setInt(2, item_id);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 加入出售物品價格查詢清單
	 * 
	 * @param key 物品編號
	 * @param value1 賣出價
	 * @param value2 回收價
	 * @param packCount 數量
	 */
	private static void addSellList(final int key, final int value1, final int value2, final int packCount) {
		// 已經加入不回收清單 忽略以下判斷
		if (_noBuyList.get(key) != null) {
			return;
		}

		// 是否在回收物品清單中
		final Integer price = _allShopItem.get(new Integer(key));

		double value3 = 0;// 回收價格

		// 回收價格不為0
		if (value2 > 0) {
			if (packCount > 0) {
				// 售價 / 數量 / 2
				value3 = (value1 / packCount) / 2.0;

			} else {
				value3 = value2;
			}

		} else {
			if (value1 > 0) {
				if (packCount > 0) {
					// 售價 / 數量 / 2
					value3 = (value1 / packCount) / 2.0;

				} else {
					// 售價 / 2
					value3 = value1 / 2.0;
				}
			}
		}

		// 計算後回收價格小於1
		if (value3 < 1) {
			_noBuyList.put(new Integer(key), new Integer((int) value3));
			// 已經加入回收物品清單中
			if (price != null) {
				// 移出回收物品列
				_allShopItem.remove(new Integer(key));
			}
			return;
		}

		// 已經加入回收物品清單中
		if (price != null) {
			// 計算後回收價格 小於 列表中紀錄
			if (value3 < price) {
				// 更新回收價格
				_allShopItem.put(new Integer(key), new Integer((int) value3));
			}

			// 尚未加入回收物品清單中
		} else {
			_allShopItem.put(new Integer(key), new Integer((int) value3));
		}
	}

	/**
	 * 傳回出售物品價格
	 * 
	 * @param key
	 * @return
	 */
	public int getPrice(final int key) {
		int tgprice = 1;
		final Integer price = _allShopItem.get(new Integer(key));
		if (price != null) {
			tgprice = price;
		}

		// 不回收物
		if (_noBuyList.get(key) != null) {
			tgprice = 1;
		}

		return tgprice;
	}

	public L1Shop get(final int npcId) {
		return _allShops.get(npcId);
	}
}
