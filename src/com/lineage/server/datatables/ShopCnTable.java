package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.TimeLimit.L1TimeLimit;
import com.lineage.server.templates.L1ShopItem;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 特殊商店販賣資料
 * 
 * @author dexc
 */
public class ShopCnTable {

	private static final Log _log = LogFactory.getLog(ShopCnTable.class);

	private static ShopCnTable _instance;

	private static final Map<Integer, ArrayList<L1ShopItem>> _shopList = new HashMap<Integer, ArrayList<L1ShopItem>>();

	public static ShopCnTable get() {
		if (_instance == null) {
			_instance = new ShopCnTable();
		}
		return _instance;
	}

	public void restshopCn() {
		_shopList.clear();
		load();
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `shop_cn` ORDER BY `id`");
			rs = ps.executeQuery();

			L1ShopItem item;
			while (rs.next()) {
				final int id = rs.getInt("id");
				
				final int npcId = rs.getInt("npc_id");
				final int itemId = rs.getInt("item_id");

				if (ItemTable.get().getTemplate(itemId) == null) {
					_log.error("特殊商店販賣資料錯誤: 沒有這個編號的道具:" + itemId + " 對應NPC編號:" + npcId);
					delete(npcId, itemId);
					continue;
				}
				final Timestamp time = rs.getTimestamp("time");
				if ((time != null) && time.before(new Timestamp(System.currentTimeMillis()))) {
					_log.info("特殊商店販賣期限已過： 道具編號" + itemId + "停止販售");
					continue;
				}
				final int sellingPrice = rs.getInt("selling_price");
				final int packCount = rs.getInt("pack_count");
				// 強化等級 by terry0412
				final int enchantLevel = rs.getInt("enchant_level");
				// added by terry0412
				final int purchasing_price = rs.getInt("purchasing_price");
				
				item = new L1ShopItem(itemId, sellingPrice, packCount, enchantLevel, purchasing_price);

				addShopItem(npcId, item);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs, ps, cn);
		}
		_log.info("載入特殊商店販賣資料數量: " + _shopList.size() + "(" + timer.get() + "ms)");
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
			ps = cn.prepareStatement("DELETE FROM `shop_cn` WHERE `npc_id`=? AND `item_id`=?");
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
	 * 加入販賣物品
	 * 
	 * @param npcId
	 * @param item
	 */
	private void addShopItem(final int npcId, final L1ShopItem item) {
		ArrayList<L1ShopItem> list = _shopList.get(new Integer(npcId));
		if (list == null) {
			list = new ArrayList<L1ShopItem>();
			list.add(item);
			_shopList.put(npcId, list);

		} else {
			list.add(item);
		}
	}
	
	public Map<Integer, ArrayList<L1ShopItem>> getaaa() { // 新增
		return _shopList;
	}

	/**
	 * 傳回NPC販賣清單
	 * 
	 * @param npcId
	 * @return
	 */
	public ArrayList<L1ShopItem> get(final int npcId) {
		final ArrayList<L1ShopItem> list = _shopList.get(new Integer(npcId));
		if (list != null) {
			return list;
		}
		return null;
	}
}
