package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.DatabaseFactoryLogin;
import com.lineage.config.Config;
import com.lineage.server.datatables.storage.AccountStorage;
import com.lineage.server.templates.L1Account;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 人物帳戶資料
 */
public class AccountTable implements AccountStorage {

	private static final Log _log = LogFactory.getLog(AccountTable.class);

	// 已有人物帳戶名稱資料
	private final Map<String, String> _loginNameList = new HashMap<String, String>();

	/**
	 * 預先加載帳戶名稱
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `accounts`";
			ps = co.prepareStatement(sqlstr);
			rs = ps.executeQuery();

			while (rs.next()) {
				final String login = rs.getString("login").toLowerCase();
				_loginNameList.put(login, login);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		_log.info("載入已有帳戶名稱資料數量: " + _loginNameList.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 傳回指定帳戶資料是否存在
	 * 
	 * @param loginName 帳號名稱
	 * @return true:有該帳號 false:沒有該帳號
	 */
	@Override
	public boolean isAccountUT(final String loginName) {
		return _loginNameList.get(loginName) != null;
	}

	/**
	 * 建立帳號資料
	 * 
	 * @param loginName 帳號
	 * @param pwd 密碼
	 * @param ip IP位置
	 * @param host MAC位置
	 * @param spwd 超級密碼
	 * @return L1Account
	 */
	@Override
	public L1Account create(final String loginName, final String pwd, final String ip, final String host,
			final String spwd) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			final Timestamp lastactive = new Timestamp(System.currentTimeMillis());

			final L1Account value = new L1Account();
			value.set_login(loginName.toLowerCase());
			value.set_password(pwd);
			value.set_lastactive(lastactive);
			value.set_access_level(0);

			value.set_ip(ip);
			value.set_mac(host);
			value.set_character_slot(0);
			value.set_spw(spwd);
			// value.set_warehouse(-256);
			value.set_countCharacters(0);
			value.set_isLoad(false);
			value.set_server_no(0);
			value.set_point(0);

			cn = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "INSERT INTO `accounts` SET `login`=?,`password`=?,"
					+ "`lastactive`=?,`access_level`=?,`ip`=?,"
					+ "`host`=?,`character_slot`=?,`spw`=?,`server_no`=?,`points`=?";

			ps = cn.prepareStatement(sqlstr);
			int i = 0;
			ps.setString(++i, value.get_login().toLowerCase());
			ps.setString(++i, value.get_password());
			ps.setTimestamp(++i, value.get_lastactive());
			ps.setInt(++i, 0);
			ps.setString(++i, value.get_ip());
			ps.setString(++i, value.get_mac());
			ps.setInt(++i, 0);
			ps.setString(++i, value.get_spw());
			ps.setInt(++i, value.get_server_no());
			ps.setInt(++i, value.get_point());
			ps.execute();

			_log.info("新帳號建立: " + value.get_login());

			return value;

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		return null;
	}

	/**
	 * 傳回指定帳戶資料是否存在
	 * 
	 * @param loginName 帳號名稱
	 * @return true:有該帳號 false:沒有該帳號
	 */
	@Override
	public boolean isAccount(final String loginName) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `accounts` WHERE `login`=?";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			rs = ps.executeQuery();

			while (rs.next()) {
				return true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		return false;
	}

	/**
	 * 傳回指定帳戶資料
	 * 
	 * @param loginName 帳號
	 * @return L1Account
	 */
	@Override
	public L1Account getAccount(final String loginName) {
		return getAccountInfo(loginName);
	}

	/**
	 * 取回帳號資料
	 * 
	 * @param loginName
	 * @return
	 */
	private L1Account getAccountInfo(final String loginName) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `accounts` WHERE `login`=?";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());

			rs = ps.executeQuery();

			while (rs.next()) {
				final String login = rs.getString("login").toLowerCase();
				final String password = rs.getString("password");
				final Timestamp lastactive = rs.getTimestamp("lastactive");
				final int access_level = rs.getInt("access_level");
				final String ip = rs.getString("ip");
				final String host = rs.getString("host");
				final int character_slot = rs.getInt("character_slot");
				final String spw = rs.getString("spw");
				final int warehouse = rs.getInt("warehouse");
				final int server_no = rs.getInt("server_no");
				final int points = rs.getInt("points");

				final int countCharacters = getPlayers(login);// 計算已創人物數量

				final L1Account value = new L1Account();
				value.set_login(login);
				value.set_password(password);
				value.set_lastactive(lastactive);
				value.set_access_level(access_level);
				value.set_ip(ip);
				value.set_mac(host);
				value.set_character_slot(character_slot);
				value.set_spw(spw);
				value.set_warehouse(warehouse);
				value.set_countCharacters(countCharacters);
				value.set_server_no(server_no);
				value.set_point(points);

				return value;
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

	/**
	 * 計算該帳戶已創立人物數量
	 * 
	 * @param loginName
	 * @return
	 */
	private static int getPlayers(final String loginName) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int i = 0;
		try {
			co = DatabaseFactory.get().getConnection();
			final String sqlstr = "SELECT * FROM `characters` WHERE `account_name`=?";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			/*
			 * final String sqlstr =
			 * "SELECT * FROM `characters` WHERE `account_name`="+ loginName; ps
			 * = co.prepareStatement(sqlstr);
			 */
			rs = ps.executeQuery();

			while (rs.next()) {
				i++;
			}
			return i;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		// System.out.println("計算該帳戶已創立人物數量:" + i);
		return 0;
	}

	/**
	 * 更新倉庫密碼
	 * 
	 * @param loginName 帳號
	 * @param pwd 密碼
	 */
	@Override
	public void updateWarehouse(final String loginName, final int pwd) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			// System.out.println("更新倉庫密碼 帳號:"+loginName+" 密碼:"+pwd);
			con = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "UPDATE `accounts` SET `warehouse`=? WHERE `login`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, pwd);

			pstm.setString(2, loginName);
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 更新上線時間/IP/MAC
	 * 
	 * @param account 帳戶
	 */
	@Override
	public void updateLastActive(final L1Account account) {
		if (account != null) {
			final Timestamp lastactive = new Timestamp(System.currentTimeMillis());
			account.set_lastactive(lastactive);

			Connection con = null;
			PreparedStatement pstm = null;
			try {
				con = DatabaseFactoryLogin.get().getConnection();
				final String sqlstr = "UPDATE `accounts` SET `lastactive`=?,`ip`=?,`host`=? WHERE `login`=?";
				pstm = con.prepareStatement(sqlstr);
				pstm.setTimestamp(1, lastactive);
				pstm.setString(2, account.get_ip());
				pstm.setString(3, account.get_mac());

				pstm.setString(4, account.get_login());
				pstm.execute();

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}

	/**
	 * 更新帳號可用人物數量
	 * 
	 * @param loginName 帳號
	 * @param count 擴充數量
	 */
	@Override
	public void updateCharacterSlot(final String loginName, final int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "UPDATE `accounts` SET `character_slot`=? WHERE `login`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, count);

			pstm.setString(2, loginName);
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 更新帳號密碼
	 * 
	 * @param loginName 帳號
	 * @param newpwd 新密碼
	 */
	@Override
	public void updatePwd(final String loginName, final String newpwd) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "UPDATE `accounts` SET `password`=? WHERE `login`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, newpwd);

			pstm.setString(2, loginName);

			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 更新帳號連線狀態
	 * 
	 * @param loginName 帳號
	 * @param islan 是否連線
	 */
	@Override
	public void updateLan(final String loginName, final boolean islan) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "UPDATE `accounts` SET `server_no`=? WHERE `login`=?";
			pstm = con.prepareStatement(sqlstr);
			if (islan) {
				pstm.setInt(1, Config.SERVERNO);

			} else {
				pstm.setInt(1, 0);
			}

			pstm.setString(2, loginName);
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void updateLan() {
		// _log.info("更新帳號連線狀態: " + loginName);
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "UPDATE `accounts` SET `server_no`=0";
			pstm = con.prepareStatement(sqlstr);
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public int getPoint(final String loginName) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int i = 0;
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `accounts` WHERE `login`=?";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, loginName);
			rs = ps.executeQuery();

			while (rs.next()) {
				i = rs.getInt("points");
			}
			return i;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		return 0;
	}

	@Override
	public void setPoint(final String loginName, final int point) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "UPDATE `accounts` SET `points`=? WHERE `login`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, point);
			pstm.setString(2, loginName);
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
