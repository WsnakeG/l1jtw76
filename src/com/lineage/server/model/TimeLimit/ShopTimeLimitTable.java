/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.model.TimeLimit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javolution.util.FastTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ShopItem;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 限時商人<br>
 * 類名稱：ShopTimeLimitTable<br>
 * 修改備註:<br>
 * 
 * @version 2.7c<br>
 */
public class ShopTimeLimitTable {

	private static final Log _log = LogFactory.getLog(ShopTimeLimitTable.class);

	private static ShopTimeLimitTable _instance;

	private static final Map<Integer, L1TimeLimit> _allShops = new HashMap<Integer, L1TimeLimit>();

	public static ShopTimeLimitTable getInstance() {
		if (_instance == null) {
			_instance = new ShopTimeLimitTable();
		}
		return _instance;
	}

	public void reload() {
		_allShops.clear();		
		loadShops();
	}
	
	private List<Integer> enumNpcIds() {
		List<Integer> ids = new ArrayList<Integer>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT DISTINCT npc_id FROM 群版_purchase_limit");
			rs = pstm.executeQuery();

			while (rs.next()) {
				ids.add(rs.getInt("npc_id"));
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		return ids;
	}

	private L1TimeLimit loadShop(int npcId, ResultSet rs) throws SQLException {
		List<L1ShopTimeLimit> sellingList = new ArrayList<L1ShopTimeLimit>();
		List<L1ShopTimeLimit> purchasingList = new ArrayList<L1ShopTimeLimit>();
		while (rs.next()) {
			int itemId = rs.getInt("item_id");
			int selling_price = rs.getInt("selling_price");
			int enlvl = rs.getInt("enlvl");
			int Currency = rs.getInt("Currency");
			int isall = rs.getInt("Isall");
			if (0 <= selling_price) {
				L1ShopTimeLimit item = new L1ShopTimeLimit(itemId, selling_price, enlvl, Currency, isall);
				sellingList.add(item);
			}
		}
		return new L1TimeLimit(npcId, sellingList, purchasingList);
	}

	public void loadShops() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM 群版_purchase_limit WHERE npc_id=? ORDER BY order_id");
			for (int npcId : enumNpcIds()) {
				pstm.setInt(1, npcId);
				rs = pstm.executeQuery();
				L1TimeLimit shop = loadShop(npcId, rs);
				_allShops.put(npcId, shop);
				rs.close();
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
//		_log.info("載入npc_限購商人販賣資料數量: " + _allShops.size() + "(" + timer.get() + "ms)");
		_log.info("Load--->群版_purchase_limit設定, " + "資料共" + _allShops.size() + "筆, " + "花費(" + timer.get() + "ms) OK");
	}

	public L1TimeLimit get(int npcId) {
		return _allShops.get(npcId);
	}

	public Map<Integer, L1TimeLimit> getall() { // 新增
		return _allShops;
	}

	/**
	 * 以物品ID傳回物品售完顯示名稱
	 * 
	 * @param ItemId
	 * @return
	 */
	public String getEndName(final int ItemId) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String endname = null;

		try {
			con = DatabaseFactory.get().getConnection();
			final String sqlstr = "SELECT `endname` FROM `群版_purchase_limit` WHERE `item_id`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, ItemId);
			rs = pstm.executeQuery();

			if (rs.next()) {
				endname = rs.getString("endname");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return endname;
	}

	/**
	 * 以物品ID傳回物品還剩多少數量可以售賣
	 * 
	 * @param ItemId
	 * @return
	 */
	public int getEndCount(final int ItemId) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int endcount = 0;

		try {
			con = DatabaseFactory.get().getConnection();
			final String sqlstr = "SELECT `endcount` FROM `群版_purchase_limit` WHERE `item_id`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, ItemId);
			rs = pstm.executeQuery();

			if (rs.next()) {
				endcount = rs.getInt("endcount");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return endcount;
	}

	/**
	 * 以物品ID更新剩餘可購買數量
	 * 
	 * @param itemid
	 * @param endcount
	 */
	public void updateendcount(final int itemid, final int endcount) {
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = DatabaseFactory.get().getConnection();
			String sqlstr = "UPDATE `群版_purchase_limit` SET `endcount`=? WHERE `item_id`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, endcount);

			pstm.setInt(2, itemid);

			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 以物品ID傳回物品還剩多少數量可以售賣
	 * 
	 * @param ItemId
	 * @return
	 */
	public int getNewcount(final int ItemId) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int newcount = 0;

		try {
			con = DatabaseFactory.get().getConnection();
			final String sqlstr = "SELECT `newcount` FROM `群版_purchase_limit` WHERE `item_id`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, ItemId);
			rs = pstm.executeQuery();

			if (rs.next()) {
				newcount = rs.getInt("newcount");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return newcount;
	}

	/**
	 * 以物品ID傳回物品個人限量購買次數
	 * 
	 * @param ItemId
	 * @return
	 */
	public int getIndividual(final int ItemId) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int Individual = 0;

		try {
			con = DatabaseFactory.get().getConnection();
			final String sqlstr = "SELECT `Individual` FROM `群版_purchase_limit` WHERE `item_id`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, ItemId);
			rs = pstm.executeQuery();
			if (rs.next()) {
				Individual = rs.getInt("Individual");
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return Individual;
	}
	
	/**
	 * 傳回NPC販賣清單
	 * 
	 * @param npcId
	 * @return
	 */
	public L1TimeLimit get1 (final int npcId) {
		final L1TimeLimit list = _allShops.get(new Integer(npcId));
		if (list != null) {
			return list;
		}
		return null;
	}
}
