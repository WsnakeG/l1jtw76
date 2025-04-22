package com.lineage.server.model.TimeLimit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 人物限時商人暫存個人限購數據
 * 類名稱：TimeLimitCharTable<br>
 * 修改備註:<br>
 * @version 2.7c<br>
 */
public class TimeLimitCharTable {

	private static final Log _log = LogFactory.getLog(TimeLimitCharTable.class);
	
	private static TimeLimitCharTable _instance;

	private static final Map<Integer, CopyOnWriteArrayList<L1TimeLimitChar>> TimeLimit_char = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<L1TimeLimitChar>>();
	
	public static TimeLimitCharTable get() {
		if (_instance == null) {
			_instance = new TimeLimitCharTable();
		}
		return _instance;
	}
	
	public void reload() {
		TimeLimit_char.clear();
		load();
	}
	
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("SELECT * FROM `群版_purchase_limit_log`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int charobjid = rs.getInt("charobjid");
				final int item_id = rs.getInt("item_id");
				L1TimeLimitChar limititem = new L1TimeLimitChar();
				limititem.set_charobjid(charobjid);
				limititem.set_itemid(item_id);
				addcharitem(charobjid, limititem);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
//		_log.info("載入人物暫存個人限購資料數量: " + TimeLimit_char.size() + "(" + timer.get() + "ms)");
		_log.info("Load--->群版_purchase_limit_log設定, " + "資料共" + TimeLimit_char.size() + "筆, " + "花費(" + timer.get() + "ms) OK");
	}
	
	/**
	 * 建立資料
	 * @param objid
	 * @param limitchar
	 */
	private static void addcharitem(final Integer objid, final L1TimeLimitChar limitchar) {
		 CopyOnWriteArrayList<L1TimeLimitChar> list = TimeLimit_char.get(objid);
		 if (list == null) {// 該人物不存在限購數據
			 list = new CopyOnWriteArrayList<L1TimeLimitChar>();
			 if (!list.contains(limitchar)) {// 清單中不包含該人物數據
				 list.add(limitchar);
			 }
		 } else {
			 if (!list.contains(limitchar)) {// 清單中不包含該人物數據
				 list.add(limitchar);
			 }
		 }
		 TimeLimit_char.put(objid, list);
	}
	
	/**
	 * 增加人物限購數據
	 * @param objId
	 * @param limitchar
	 */
	public void storitem(final int objId, final L1TimeLimitChar limitchar) {
		addcharitem(objId, limitchar);
		
        Connection con = null;
        PreparedStatement pstm = null;
        try {
			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO 群版_purchase_limit_log SET charobjid = ?, item_id = ?");
			int i = 0;
            pstm.setInt(++i, limitchar.get_charobjid());
            pstm.setInt(++i, limitchar.get_itemid());
            pstm.execute();
        } catch (final SQLException e) {
        	 _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        ShopTimeLimitTable sss = new ShopTimeLimitTable();
        upCount(sss.getIndividual(limitchar.get_itemid()), objId, limitchar.get_itemid());
	}
	
	/**
	 * 傳回該人物的限購數據
	 * @param objid
	 * @return
	 */
    public CopyOnWriteArrayList<L1TimeLimitChar> loadItems(final Integer objid) {
        CopyOnWriteArrayList<L1TimeLimitChar> list = TimeLimit_char.get(objid);
        if (list != null) {
            return list;
        }
        return null;
    }
    
    /**
     * 傳回指定人物objid對應的物品可購買數量是否大於0
     * @param pcObjid
     * @param itemid
     * @return
     */
    public boolean getUserItems(final Integer pcObjid, final int itemid) {
        CopyOnWriteArrayList<L1TimeLimitChar> list = TimeLimit_char.get(pcObjid);
        if (list != null) {
            for (L1TimeLimitChar limit : list) {
                if (limit.get_itemid() == itemid && getCount(pcObjid, itemid) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
	
	/**
	 * 以物品ID傳回物品個人限量購買次數
	 * @param ItemId
	 * @return
	 */
	public int getCount(final int pcobjid, final int ItemId) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int count = 0;

		try {
			con = DatabaseFactory.get().getConnection();
			final String sqlstr = "SELECT `count` FROM `群版_purchase_limit_log` WHERE `charobjid`=? AND `item_id`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, pcobjid);
			pstm.setInt(2, ItemId);
			rs = pstm.executeQuery();
			if (rs.next()) {
				count = rs.getInt("count");
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return count;
	}

	/**
	 * 以人物OBJID和itemid更新count
	 * @param pcobjid
	 * @param itemid
	 * @param count
	 */
	public void upCount(final int count, final int pcobjid, final int itemid) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("UPDATE `群版_purchase_limit_log` SET `count`=? WHERE `charobjid`=? AND `item_id`=?");
			ps.setInt(1, count);
			ps.setInt(2, pcobjid);
			ps.setInt(3, itemid);
			ps.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}
	
	/**
	 * 刪除表數據並清空暫存
	 */
	public void deleteall() {
		_log.info("群版_purchase_limit_log,數據開始刪除,需要刪除數據：" + TimeLimit_char.size() + "個.");
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("DELETE FROM `群版_purchase_limit_log`");
			ps.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
		TimeLimit_char.clear();
		_log.info("群版_purchase_limit_log,數據刪除完畢,剩餘數據：" + TimeLimit_char.size() + "個.");
	}
}
