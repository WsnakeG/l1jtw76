package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.serverpackets.S_GameMall;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.T_GameMallModel;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 商城資料庫
 * 
 * @author simlin
 */
public class T_GameMallTable {

	private static final Log _log = LogFactory.getLog(T_GameMallTable.class);

	private static T_GameMallTable _instance;

	private final HashMap<Integer, T_GameMallModel> _mallList = new HashMap<Integer, T_GameMallModel>();

	private final ArrayList<ServerBasePacket> _paketList = new ArrayList<ServerBasePacket>();

	public static final int _unit = 44070;// 商城單位

	public static T_GameMallTable get() {
		if (_instance == null) {
			_instance = new T_GameMallTable();
		}
		return _instance;
	}

	private T_GameMallTable() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM t_game_mall");
			rs = pstm.executeQuery();

			T_GameMallModel model = null;
			L1ItemInstance item = null;
			while (rs.next()) {
				final int id = rs.getInt("id");
				final int itemId = rs.getInt("itemId");
				final int itemCount = rs.getInt("itemCount");
				final int enchantLevel = rs.getInt("enchantLevel");
				final int bless = rs.getInt("bless");
				final int shopItemDesc = rs.getInt("shopItemDesc");
				final int itemPrice = rs.getInt("itemPrice");
				final int itemSort = rs.getInt("itemSort");
				final boolean newItem = rs.getBoolean("newItem");
				final int vipLevel = rs.getInt("vipLevel");
				final boolean hotItem = rs.getBoolean("hotItem");
				item = ItemTable.get().createItem(itemId, false);
				if (item != null) {
					item.setCount(itemCount);
					item.setEnchantLevel(enchantLevel);
					item.setBless(bless);
					model = new T_GameMallModel(id, item, shopItemDesc, itemPrice, itemSort, newItem,
							vipLevel, hotItem);
					_mallList.put(Integer.valueOf(id), model);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入遊戲商城設置數量: " + _mallList.size() + "(" + timer.get() + "ms)");
	}

	public T_GameMallModel getMallList(final int id) {
		return _mallList.get(id);
	}

	public ArrayList<ServerBasePacket> getPaketList() {
		if (_paketList.isEmpty() && !_mallList.isEmpty()) {
			final Collection<T_GameMallModel> allValues = _mallList.values();
			int index = 0;
			int page = 0;
			final int modelSize = _mallList.size();
			final int sumPage = (modelSize % 127) == 0 ? modelSize / 127 : (modelSize / 127) + 1;
			final HashMap<Integer, T_GameMallModel> pageBuff = new HashMap<Integer, T_GameMallModel>();
			for (final T_GameMallModel gameMallModel : allValues) {
				pageBuff.put(gameMallModel.getMallId(), gameMallModel);
				index++;
				if (index == 127) {
					_paketList.add(new S_GameMall(pageBuff, sumPage, page));
					pageBuff.clear();
					index = 0;
					page++;
				}
			}
			if (!pageBuff.isEmpty()) {
				_paketList.add(new S_GameMall(pageBuff, sumPage, page));
			}
		}
		return _paketList;
	}

	public void insertMallRecord(final int objId, final int targetId, final int itemId, final String itemName,
			final int buyCount, final int sumPrice) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("INSERT INTO t_game_mall_log SET objId=?,"
					+ "targetObjId=?,itemId=?,itemName=?,buyCount=?,sumPrice=?,buyTime=?");
			pstm.setInt(1, objId);
			pstm.setInt(2, targetId);
			pstm.setInt(3, itemId);
			pstm.setString(4, itemName);
			pstm.setInt(5, buyCount);
			pstm.setInt(6, sumPrice);
			pstm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}