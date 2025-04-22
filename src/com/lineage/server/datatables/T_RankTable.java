package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.T_ClanRankModel;
import com.lineage.server.templates.T_KillPlayCountRankModel;
import com.lineage.server.templates.T_LevelRankModel;
import com.lineage.server.templates.T_WeaponRankModel;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 世界排行榜資料庫
 * 
 * @author simlin
 */
public class T_RankTable {

	private static final Log _log = LogFactory.getLog(T_RankTable.class);

	private static T_RankTable _instance;

	public static int _basedTime;

	private ArrayList<String> _levelName = new ArrayList<String>();

	private ArrayList<T_LevelRankModel> _level = new ArrayList<T_LevelRankModel>();

	private ArrayList<String> _clanName = new ArrayList<String>();

	private ArrayList<T_ClanRankModel> _clan = new ArrayList<T_ClanRankModel>();

	private ArrayList<String> _weaponName = new ArrayList<String>();

	private ArrayList<T_WeaponRankModel> _weapon = new ArrayList<T_WeaponRankModel>();

	private ArrayList<String> _wealthName = new ArrayList<String>();

	private ArrayList<String> _consumeName = new ArrayList<String>();

	private ArrayList<String> _killName = new ArrayList<String>();

	private ArrayList<T_KillPlayCountRankModel> _kill = new ArrayList<T_KillPlayCountRankModel>();

	public static synchronized T_RankTable get() {
		if (_instance == null) {
			_instance = new T_RankTable();
		}
		return _instance;
	}

	private T_RankTable() {
		final PerformanceTimer timer = new PerformanceTimer();
		_basedTime = (int) (System.currentTimeMillis() / 1000L);
		rankedLevel(20);
		rankedClan(10);
		rankedWeapon(50);
		rankedWealth(20);
		rankedConsume(20);
		rankedKill(50);
		_log.info("載入世界排行榜資料: (" + timer.get() + "ms)");
	}

	public void rankedLevel(final int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"select char_name,level,exp,type" + " from characters where characters.AccessLevel=0"
							+ " order by level desc,Exp desc,objid limit 0," + count + ";");
			rs = pstm.executeQuery();

			T_LevelRankModel model = null;
			while (rs.next()) {
				final String char_name = rs.getString("char_name");
				final int level = rs.getInt("level");
				final long exp = rs.getLong("exp");
				final int type = rs.getInt("type");
				model = new T_LevelRankModel(level, char_name, exp, 0, type);
				_level.add(model);
				_levelName.add(char_name);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public ArrayList<String> getLevelName() {
		return _levelName;
	}

	public void setLevelName(final ArrayList<String> levelRankNames) {
		_levelName = levelRankNames;
	}

	public ArrayList<T_LevelRankModel> getLevelList() {
		return _level;
	}

	public void setLevelList(final ArrayList<T_LevelRankModel> levelRankModels) {
		_level = levelRankModels;
	}

	public void rankedClan(final int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT characters.ClanID,characters.Clanname,"
					+ "COUNT(*) as count,SUM(`level`) * 25 as fen,clan_data.leader_name"
					+ " FROM characters LEFT JOIN clan_data ON clan_data.clan_id ="
					+ " characters.ClanID where ClanID > 0 GROUP BY ClanID ORDER" + " BY fen DESC LIMIT 0,"
					+ count + ";");
			rs = pstm.executeQuery();

			T_ClanRankModel model = null;
			while (rs.next()) {
				final String clanName = rs.getString("Clanname");
				final String leaderName = rs.getString("leader_name");
				final int menberCount = rs.getInt("count");
				final int fen = rs.getInt("fen");
				model = new T_ClanRankModel(clanName, leaderName, menberCount, fen);
				_clan.add(model);
				_clanName.add(clanName);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public ArrayList<String> getClanName() {
		return _clanName;
	}

	public void setClanName(final ArrayList<String> clanRankNames) {
		_clanName = clanRankNames;
	}

	public ArrayList<T_ClanRankModel> getClanNameList() {
		return _clan;
	}

	public void setClanNameList(final ArrayList<T_ClanRankModel> clanRankModels) {
		_clan = clanRankModels;
	}

	public void rankedWeapon(final int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT weapon.`name`,weapon.name_id,"
					+ "characters.char_name,character_items.enchantlvl,"
					+ "character_items.attr_enchant_kind FROM weapon JOIN character_items"
					+ " on character_items.item_id = weapon.item_id JOIN"
					+ " characters on character_items.char_id=characters.objid"
					+ " and characters.AccessLevel = 0 where weapon.rankIndex != 0"
					+ " order by weapon.rankIndex DESC,character_items.enchantlvl" + " DESC LIMIT 0," + count
					+ ";");
			rs = pstm.executeQuery();

			T_WeaponRankModel model = null;
			while (rs.next()) {
				final String weaponName = rs.getString("name");
				final String weaponNameId = rs.getString("name_id");
				final String weaponMasterName = rs.getString("char_name");
				final int weaponEnchantlevel = rs.getInt("enchantlvl");
				final int weaponAttrKind = rs.getInt("attr_enchant_kind");
				model = new T_WeaponRankModel(weaponName, weaponNameId, weaponEnchantlevel, weaponAttrKind,
						weaponMasterName);
				_weaponName.add(weaponMasterName);
				_weapon.add(model);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public ArrayList<String> getWeaponName() {
		return _weaponName;
	}

	public void setWeaponName(final ArrayList<String> weaponRankNames) {
		_weaponName = weaponRankNames;
	}

	public ArrayList<T_WeaponRankModel> getWeaponNameList() {
		return _weapon;
	}

	public void setWeaponNameList(final ArrayList<T_WeaponRankModel> weaponRankModels) {
		_weapon = weaponRankModels;
	}

	public void rankedWealth(final int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"SELECT character_items.count," + "characters.char_name FROM character_items JOIN "
							+ "characters on character_items.char_id = "
							+ "characters.objid and characters.AccessLevel=0"
							+ " where character_items.item_id = 40308 ORDER BY"
							+ " character_items.count desc LIMIT 0," + count + ";");
			rs = pstm.executeQuery();

			while (rs.next()) {
				_wealthName.add(rs.getString("char_name"));
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public ArrayList<String> getWealthName() {
		return _wealthName;
	}

	public void setWealthName(final ArrayList<String> moneyRankNames) {
		_wealthName = moneyRankNames;
	}

	public void rankedConsume(final int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"SELECT characters.char_name," + "t_game_mall_log.objId,SUM(t_game_mall_log.sumPrice)"
							+ " as sumPrice  FROM t_game_mall_log JOIN characters "
							+ "on t_game_mall_log.objId = characters.objid and "
							+ "characters.AccessLevel=0 GROUP BY t_game_mall_log.objId"
							+ " ORDER BY sumPrice DESC LIMIT 0," + count + ";");
			rs = pstm.executeQuery();

			while (rs.next()) {
				_consumeName.add(rs.getString("char_name"));
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public ArrayList<String> getConsumeName() {
		return _consumeName;
	}

	public void setConsumeName(final ArrayList<String> consumptionRankNames) {
		_consumeName = consumptionRankNames;
	}

	public void rankedKill(final int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT characters.Type,characters.char_name,"
					+ "characters.PKcount FROM characters where characters.AccessLevel=0"
					+ " ORDER BY characters.PKcount DESC LIMIT 0," + count + ";");
			rs = pstm.executeQuery();

			T_KillPlayCountRankModel model = null;
			while (rs.next()) {
				final String name = rs.getString("char_name");
				final int type = rs.getInt("Type");
				final int killCount = rs.getInt("PKcount");
				model = new T_KillPlayCountRankModel(name, type, killCount);
				_killName.add(name);
				_kill.add(model);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public ArrayList<String> getKillName() {
		return _killName;
	}

	public void setKillName(final ArrayList<String> killRankNames) {
		_killName = killRankNames;
	}

	public ArrayList<T_KillPlayCountRankModel> getKillNameList() {
		return _kill;
	}

	public void setKillNameList(final ArrayList<T_KillPlayCountRankModel> killRankModels) {
		_kill = killRankModels;
	}
}