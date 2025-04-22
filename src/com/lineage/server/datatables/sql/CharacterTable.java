package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.storage.CharacterStorage;
import com.lineage.server.storage.mysql.MySqlCharacterStorage;
import com.lineage.server.templates.L1CharName;
import com.lineage.server.utils.SQLUtil;

/**
 * 人物數據
 * 
 * @author daien
 */
public class CharacterTable {

	private static final Log _log = LogFactory.getLog(CharacterTable.class);

	private final CharacterStorage _charStorage;

	private static CharacterTable _instance;

	private static final Map<String, L1CharName> _charNameList = new HashMap<String, L1CharName>();

	private CharacterTable() {
		_charStorage = new MySqlCharacterStorage();
	}

	public static CharacterTable get() {
		if (_instance == null) {
			_instance = new CharacterTable();
		}
		return _instance;
	}

	public void storeNewCharacter(final L1PcInstance pc) throws Exception {
		synchronized (pc) {
			_charStorage.createCharacter(pc);
			final String name = pc.getName();
			if (!_charNameList.containsKey(name)) {
				final L1CharName cn = new L1CharName();
				cn.setName(name);
				cn.setId(pc.getId());
				_charNameList.put(name, cn);
			}
		}
	}

	public void storeCharacter(final L1PcInstance pc) throws Exception {
		synchronized (pc) {
			_charStorage.storeCharacter(pc);
		}
	}

	public void deleteCharacter(final String accountName, final String charName) throws Exception {
		// 多分、同期は必要ない
		_charStorage.deleteCharacter(accountName, charName);
		if (_charNameList.containsKey(charName)) {
			_charNameList.remove(charName);
		}
		// _log.error("deleteCharacter");
	}

	/**
	 * 傳回人物數據
	 * 
	 * @param charName 人物名稱
	 * @return
	 * @throws Exception
	 */
	public L1PcInstance restoreCharacter(final String charName) throws Exception {
		final L1PcInstance pc = _charStorage.loadCharacter(charName);
		return pc;
	}

	/**
	 * 傳回人物數據
	 * 
	 * @param charName 人物名稱
	 * @return
	 * @throws Exception
	 */
	public L1PcInstance loadCharacter(final String charName) throws Exception {
		L1PcInstance pc = null;
		try {
			pc = restoreCharacter(charName);

			// マップの範囲外ならSKTに移動させる
			final L1Map map = L1WorldMap.get().getMap(pc.getMapId());

			if (!map.isInMap(pc.getX(), pc.getY())) {
				pc.setX(33087);
				pc.setY(33396);
				pc.setMap((short) 4);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
		return pc;

	}

	/**
	 * 變更全部人物狀態為離線
	 */
	public static void clearOnlineStatus() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `characters` SET `OnlineStatus`='0'");
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 變更指定人物連線狀態
	 * 
	 * @param pc
	 */
	public static void updateOnlineStatus(final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `characters` SET `OnlineStatus`=? WHERE `objid`=?");
			pstm.setInt(1, pc.getOnlineStatus());
			pstm.setInt(2, pc.getId());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 清除指定人物婚姻對象
	 * 
	 * @param targetId
	 */
	public static void updatePartnerId(final int targetId) {
		updatePartnerId(targetId, 0);
	}

	/**
	 * 設定人物婚姻對象
	 * 
	 * @param targetId
	 * @param partnerId
	 */
	public static void updatePartnerId(final int targetId, final int partnerId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `characters` SET `PartnerID`=? WHERE `objid`=?");
			pstm.setInt(1, partnerId);
			pstm.setInt(2, targetId);
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 建立人物初始化素質狀態
	 * 
	 * @param pc
	 */
	public static void saveCharStatus(final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `characters` SET `OriginalStr`=?,"
					+ "`OriginalCon`=?,`OriginalDex`=?,`OriginalCha`=?," + "`OriginalInt`=?,`OriginalWis`=?"
					+ " WHERE `objid`=?");

			pstm.setInt(1, pc.getBaseStr());
			pstm.setInt(2, pc.getBaseCon());
			pstm.setInt(3, pc.getBaseDex());
			pstm.setInt(4, pc.getBaseCha());
			pstm.setInt(5, pc.getBaseInt());
			pstm.setInt(6, pc.getBaseWis());

			pstm.setInt(7, pc.getId());
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 取得人物物品資料
	 * 
	 * @param pc
	 */
	public static void restoreInventory(final L1PcInstance pc) {
		// 背包
		pc.getInventory().loadItems();
		// 矮人倉庫
		pc.getDwarfInventory().loadItems();
		// 精靈倉庫
		pc.getDwarfForElfInventory().loadItems();
		// 商城倉庫
		pc.getDwarfForGameMall().loadItems();
	}

	/**
	 * 檢查數據庫中是否有指定人物
	 * 
	 * @param name 人物名稱
	 * @return
	 */
	public static boolean doesCharNameExist(final String name) {
		boolean result = true;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT `account_name` FROM `characters` WHERE `char_name`=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			result = rs.next();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	/**
	 * 變更人物名稱
	 * 
	 * @param objid
	 * @param name
	 */
	public void newCharName(final int objid, final String name) {
		final L1CharName cn = new L1CharName();
		cn.setName(name);
		cn.setId(objid);
		_charNameList.put(name, cn);

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `characters` SET `char_name`=? WHERE `objid`=?");
			pstm.setString(1, name);
			pstm.setInt(2, objid);
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 暫存已用人物名稱
	 */
	public static void loadAllCharName() {
		L1CharName cn = null;
		String name = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `characters`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				cn = new L1CharName();
				name = rs.getString("char_name");
				cn.setName(name);
				cn.setId(rs.getInt("objid"));
				_charNameList.put(name, cn);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 占用陣列紀錄
	 * 
	 * @return
	 */
	public L1CharName[] getCharNameList() {
		return _charNameList.values().toArray(new L1CharName[_charNameList.size()]);
	}

	public static String getAccountName(final String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT `account_name` FROM `characters` WHERE `char_name`=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return "";
	}

	public static int getLevel(final String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT `level` FROM `characters` WHERE `char_name`=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return 0;
	}

	/**
	 * 取回該OBJID人物名稱
	 * 
	 * @param objid
	 * @return
	 */
	public String getCharName(final int objid) {
		for (final L1CharName charName : _charNameList.values()) {
			if (charName.getId() == objid) {
				return charName.getName();
			}
		}
		return null;
	}

	/**
	 * 更新戒指欄位擴充狀態 by terry0412
	 * 
	 * @param pc
	 */
	public static void updateRingsExpansion(final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `characters` SET `RingsExpansion`=? WHERE `objid`=?");
			pstm.setByte(1, pc.getRingsExpansion());
			pstm.setInt(2, pc.getId());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
