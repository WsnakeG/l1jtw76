package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;

/**
 * 人物已用OBJID預先加載 血盟已用OBJID預先加載
 * 
 * @author dexc
 */
public class CharObjidTable {

	private static final Log _log = LogFactory.getLog(CharObjidTable.class);

	// 改用同步化Map by terry0412
	private static final Map<Integer, String> _objList = new ConcurrentHashMap<Integer, String>();

	// 改用同步化Map by terry0412
	private static final Map<Integer, String> _objClanList = new ConcurrentHashMap<Integer, String>();

	private static CharObjidTable _instance;

	public static CharObjidTable get() {
		if (_instance == null) {
			_instance = new CharObjidTable();
		}
		return _instance;
	}

	/**
	 * 初始化載入
	 */
	public void load() {
		loadPc();
		loadClan();
	}

	private void loadClan() {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `clan_data`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int clan_id = rs.getInt("clan_id");
				final String clan_name = rs.getString("clan_name");// 血盟名稱

				_objClanList.put(clan_id, clan_name);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	private void loadPc() {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `characters`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int objid = rs.getInt("objid");
				final String char_name = rs.getString("char_name");// 人物名稱

				_objList.put(objid, char_name);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	// PC

	/**
	 * 更改已用
	 * 
	 * @param objid
	 * @param char_name
	 */
	public void reChar(final int objid, final String char_name) {
		_objList.put(objid, char_name);
	}

	/**
	 * 加入該OBJID
	 * 
	 * @param objid
	 * @param char_name
	 */
	public void addChar(final int objid, final String char_name) {
		final int tmp = charObjid(char_name);
		if (tmp == 0) {
			_objList.put(objid, char_name);
		}
	}

	/**
	 * 是否具有該OBJID
	 * 
	 * @param objid
	 * @return
	 */
	public String isChar(final int objid) {
		final String username = _objList.get(objid);
		if (username != null) {
			return username;
		}
		return null;
	}

	/**
	 * 該名稱所屬的OBJID
	 * 
	 * @param char_name
	 * @return
	 */
	public int charObjid(final String char_name) {
		for (final Integer integer : _objList.keySet()) {
			final String tgname = _objList.get(integer);
			if (char_name.equalsIgnoreCase(tgname)) {
				return integer;
			}
		}
		return 0;
	}

	/**
	 * 移除已使用名稱
	 * 
	 * @param objid
	 * @return
	 */
	public void charRemove(final String char_name) {
		final int objid = charObjid(char_name);
		final String username = _objList.get(objid);
		if (username != null) {
			_objList.remove(objid);
		}
	}

	// CLAN

	/**
	 * 加入該OBJID
	 * 
	 * @param clan_id
	 * @param clan_name
	 */
	public void addClan(final int clan_id, final String clan_name) {
		final int tmp = clanObjid(clan_name);
		if (tmp == 0) {
			_objClanList.put(clan_id, clan_name);
		}
	}

	/**
	 * 是否具有該OBJID
	 * 
	 * @param objid
	 * @return
	 */
	public boolean isClan(final int clan_id) {
		final String username = _objClanList.get(clan_id);
		if (username != null) {
			return true;
		}
		return false;
	}

	/**
	 * 該名稱所屬的OBJID
	 * 
	 * @param char_name
	 * @return
	 */
	public int clanObjid(final String clan_name) {
		for (final Integer integer : _objClanList.keySet()) {
			final String tgname = _objClanList.get(integer);
			if (clan_name.equalsIgnoreCase(tgname)) {
				return integer;
			}
		}
		return 0;
	}
}
