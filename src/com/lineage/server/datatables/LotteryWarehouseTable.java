package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_LotteryMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShowLottery;
import com.lineage.server.serverpackets.S_ShowLotteryList;
import com.lineage.server.templates.L1LotteryWarehouse;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import java.util.Random;

/**
 * 潘朵拉幸運抽獎系統
 * 
 * @author simlin
 */

public class LotteryWarehouseTable {

	private static final Log _log = LogFactory.getLog(LotteryWarehouseTable.class);

	private static LotteryWarehouseTable _instance;

	private ArrayList<L1LotteryWarehouse> _list = new ArrayList<L1LotteryWarehouse>();

	public ArrayList<L1LotteryWarehouse> getList() {
		return _list;
	}

	public void setList(final ArrayList<L1LotteryWarehouse> lotteryWarehouseList) {
		_list = lotteryWarehouseList;
	}

	public void addList(final L1LotteryWarehouse lottery) {
		if (!_list.contains(lottery)) {
			_list.add(lottery);
		}
	}

	public void removeList(final L1LotteryWarehouse lottery) {
		if (_list.contains(lottery)) {
			_list.remove(lottery);
		}
	}

	public static LotteryWarehouseTable get() {
		_instance = new LotteryWarehouseTable();
		return _instance;
	}

	private int _orderId = 0;

	/**
	 * 取回潘朵拉抽獎倉庫資料
	 * 
	 * @param pc
	 */
	public void getData(final L1PcInstance pc) {
		final int charId = pc.getId();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"select * from character_warehouse_lottery" + " WHERE char_id=? ORDER BY time");
			pstm.setInt(1, charId);
			rs = pstm.executeQuery();

			while (rs.next()) {
				final L1LotteryWarehouse lottery = new L1LotteryWarehouse();
				lottery.setOrderId(rs.getInt(1));
				lottery.setCharId(rs.getInt(2));
				lottery.setItemId(rs.getInt(3));
				lottery.setItemName(rs.getString(4));
				lottery.setItemCount(rs.getInt(5));
				lottery.setEnchantLevel(rs.getInt(6));
				lottery.setTime(rs.getTimestamp(7));
				addList(lottery);
			}
			pc.sendPackets(new S_ShowLotteryList(_list));

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 開啟抽抽樂
	 * 
	 * @param item
	 * @param pc
	 * @param showToall
	 */
	public void startLottery(final L1ItemInstance item, final L1PcInstance pc, final boolean showToall) {
		final L1LotteryWarehouse lottery = new L1LotteryWarehouse();
		lottery.setCharId(pc.getId());
		lottery.setItemId(item.getItemId());
		lottery.setItemName(item.getItem().getName());
		lottery.setItemCount((int) item.getCount());
		lottery.setEnchantLevel(item.getEnchantLevel());
		insertLottery(lottery, pc);
		pc.sendPackets(new S_ShowLottery(item, _list.size() - 1));
		if (showToall) {
			String name = item.getItem().getNameId();
			if (item.getCount() > 1L) {
				name = name + " (" + item.getCount() + ")";
			}
			World.get().broadcastPacketToAll(
					new S_LotteryMessage(3736, name, pc.getName(), item.getItem().getGfxId()));
		}
	}

	/**
	 * 從抽抽樂倉庫取回
	 * 
	 * @param pc
	 * @param item
	 * @param count
	 * @param lottery
	 * @return
	 */
	public synchronized L1ItemInstance getLotteryItem(final L1PcInstance pc, final L1ItemInstance item,
			final long count, final L1LotteryWarehouse lottery) {
		L1ItemInstance tradeItem = null;
		item.setCount(count);
		item.setEnchantLevel(lottery.getEnchantLevel());
		item.setIdentified(true);
		tradeItem = pc.getInventory().storeItem(item);
		if (tradeItem != null) {
			deleteLottery(lottery);
			pc.sendPackets(new S_ServerMessage(3727));
			pc.sendPackets(new S_ShowLotteryList(_list));
		}
		return tradeItem;
	}

	/**
	 * 換取轉運卷軸
	 * 
	 * @param pc
	 * @param lottery
	 * @return
	 */
	public synchronized L1ItemInstance getLotteryTicket(final L1PcInstance pc,
			final L1LotteryWarehouse lottery) {
		L1ItemInstance tradeItem = null;
		tradeItem = pc.getInventory().storeItem(60441, 1);
		if (tradeItem != null) {
			deleteLottery(lottery);
		}
		return tradeItem;
	}

	/**
	 * 存入潘朵拉抽獎倉庫
	 * 
	 * @param lottery
	 * @param pc
	 */
	public void insertLottery(final L1LotteryWarehouse lottery, final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		final ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_warehouse_lottery"
					+ " SET char_id=?, item_id=?, item_name=?, item_count=?,"
					+ " enchantlvl=?, time=?,order_id=?");
			pstm.setInt(1, pc.getId());
			pstm.setInt(2, lottery.getItemId());
			pstm.setString(3, lottery.getItemName());
			pstm.setInt(4, lottery.getItemCount());
			pstm.setInt(5, lottery.getEnchantLevel());
			final Timestamp time = new Timestamp(System.currentTimeMillis());
			lottery.setTime(time);
			pstm.setTimestamp(6, time);
			Random _random = new Random();
			_orderId = _random.nextInt(100000);
			pstm.setInt(7, _orderId);
			lottery.setOrderId(_orderId);
			pstm.execute();
			addList(lottery);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 刪除潘朵拉抽獎倉庫
	 * 
	 * @param lottery
	 */
	private synchronized void deleteLottery(final L1LotteryWarehouse lottery) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"DELETE FROM character_warehouse_lottery" + " WHERE char_id=? AND order_id=?");
			pstm.setInt(1, lottery.getCharId());
			pstm.setInt(2, lottery.getOrderId());
			pstm.execute();
			_list.remove(lottery);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
