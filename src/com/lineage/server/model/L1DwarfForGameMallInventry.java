package com.lineage.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.T_ShopWarehouseModel;
import com.lineage.server.utils.SQLUtil;

/**
 * 商城倉庫資料
 * 
 * @author simlin
 */
public class L1DwarfForGameMallInventry {

	private static final Log _log = LogFactory.getLog(L1DwarfForGameMallInventry.class);

	public final Object _key = new Object();

	protected List<T_ShopWarehouseModel> _wareHouseList = new CopyOnWriteArrayList<T_ShopWarehouseModel>();

	private final L1PcInstance _owner;

	public L1DwarfForGameMallInventry(final L1PcInstance owner) {
		_owner = owner;
	}

	public synchronized void loadItems() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		T_ShopWarehouseModel shopWarehouseModel = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"SELECT * FROM character_warehousegame_mall" + " WHERE accountName = ?");
			pstm.setString(1, _owner.getAccountName());
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int id = rs.getInt("id");
				final String accountName = rs.getString("accountName");
				final int objId = rs.getInt("buyObjId");
				final int itemId = rs.getInt("itemId");
				final String itemName = rs.getString("itemName");
				final int itemCount = rs.getInt("itemCount");
				final int itemBless = rs.getInt("itemBless");
				final int itemEnchantLevel = rs.getInt("itemEnchantLevel");
				shopWarehouseModel = new T_ShopWarehouseModel(id, accountName, objId, itemId, itemName,
						itemCount, itemBless, itemEnchantLevel);
				_wareHouseList.add(shopWarehouseModel);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public synchronized void insertOffLine(final T_ShopWarehouseModel shopWarehouseModel) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("INSERT INTO " + "character_warehousegame_mall SET"
					+ " id=?,accountName=?,buyObjId=?,itemId=?," + "itemName=?,itemCount=?,itemBless=?,"
					+ "itemEnchantLevel=?");
			pstm.setInt(1, shopWarehouseModel.getId());
			pstm.setString(2, shopWarehouseModel.getAccountName());
			pstm.setInt(3, shopWarehouseModel.getObjId());
			pstm.setInt(4, shopWarehouseModel.getItemId());
			pstm.setString(5, shopWarehouseModel.getItemName());
			pstm.setInt(6, shopWarehouseModel.getItemCount());
			pstm.setInt(7, shopWarehouseModel.getItemBless());
			pstm.setInt(8, shopWarehouseModel.getEnchantLevel());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public synchronized void deleteItems(final T_ShopWarehouseModel shopWarehouseModel) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_warehousegame_mall WHERE id = ?");
			pstm.setInt(1, shopWarehouseModel.getId());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_wareHouseList.remove(_wareHouseList.indexOf(shopWarehouseModel));
	}

	public synchronized T_ShopWarehouseModel insertOnLine(final T_ShopWarehouseModel shopWarehouseModel) {
		insertOffLine(shopWarehouseModel);
		_wareHouseList.add(shopWarehouseModel);
		return shopWarehouseModel;
	}

	public List<T_ShopWarehouseModel> getWareHouseList() {
		return _wareHouseList;
	}
}