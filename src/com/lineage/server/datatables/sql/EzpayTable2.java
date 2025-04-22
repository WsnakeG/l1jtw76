package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.datatables.storage.EzpayStorage;
import com.lineage.server.utils.SQLUtil;

/**
 * 網站購物資料
 * 
 * @author dexc
 */
public class EzpayTable2 implements EzpayStorage {

	private static final Log _log = LogFactory.getLog(EzpayTable2.class);

	/**
	 * 傳回指定帳戶購物資料
	 * 
	 * @param loginName 帳號名稱
	 * @return
	 */
	@Override
	public Map<Integer, int[]> ezpayInfo(final String loginName) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final Map<Integer, int[]> list = new HashMap<Integer, int[]>();
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `shop_user2` WHERE `account`=? ORDER BY `id`";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			rs = ps.executeQuery();

			while (rs.next()) {
				final int[] value = new int[3];
				final int state = rs.getInt("out"); // 狀態 (變更名稱 by erics4179)
				if (state == 0) {
					final int key = rs.getInt("id"); // ID
					final int p_id = rs.getInt("p_id"); // ITEMID
					final int count = rs.getInt("count"); // 數量
					value[0] = key;
					value[1] = p_id;
					value[2] = count;

					list.put(key, value);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		return list;
	}

	/**
	 * 傳回指定帳戶購物資料
	 * 
	 * @param loginName 帳號名稱
	 * @param id 流水號
	 * @return
	 */
	@Override
	public int[] ezpayInfo(final String loginName, final int id) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final int[] info = new int[4];
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `shop_user2` WHERE `account`=? AND `id`=?";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			ps.setInt(2, id);
			rs = ps.executeQuery();

			while (rs.next()) {
				final int state = rs.getInt("out"); // 狀態 (變更名稱 by erics4179)
				if (state == 0) {
					final int p_id = rs.getInt("p_id"); // ITEMID
					final int count = rs.getInt("count"); // 數量
					info[0] = id;
					info[1] = p_id;
					info[2] = count;
					return info;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		return null;
	}

	private boolean is(final String loginName, final int id) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `shop_user2` WHERE `account`=? AND `id`=?";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			ps.setInt(2, id);
			rs = ps.executeQuery();

			while (rs.next()) {
				final int state = rs.getInt("out"); // 狀態 (變更名稱 by erics4179)
				if (state != 0) {
					return false;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		return true;
	}

	/**
	 * 更新資料
	 * 
	 * @param loginName 帳號名稱
	 * @param id ID
	 * @param pcname 領取人物
	 * @param ip IP
	 * @return
	 */
	@Override
	public boolean update(final String loginName, final int id, final String pcname, final String ip) {
		if (!is(loginName, id)) {
			return false;
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			final Timestamp lastactive = new Timestamp(System.currentTimeMillis());

			con = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "UPDATE `shop_user2` SET `out`=1,`play`=?,`time`=?,`ip`=? WHERE `id`=? AND `account`=?";
			pstm = con.prepareStatement(sqlstr);

			pstm.setString(1, pcname); // 領取人物
			pstm.setTimestamp(2, lastactive); // 時間
			pstm.setString(3, ip); // IP位置

			pstm.setInt(4, id); // ID
			pstm.setString(5, loginName); // 帳號名稱

			pstm.execute();
			return true;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}
}