package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.config.Config;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.templates.DeClan;
import com.lineage.server.templates.DeName;
import com.lineage.server.utils.SQLUtil;

/**
 * 虛擬血盟資料表
 * 
 * @author dexc
 */
public class DeClanTable {

	private static final Log _log = LogFactory.getLog(DeClanTable.class);

	private static final Map<String, DeClan> _cdeclanList = new HashMap<String, DeClan>();

	private static final ArrayList<byte[]> _emblemiconList = new ArrayList<byte[]>();

	private static DeClanTable _instance;

	// 重建分配盟輝
	private static boolean _reclanIcon = false;

	public static DeClanTable get() {
		if (_instance == null) {
			_instance = new DeClanTable();
		}
		return _instance;
	}

	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `de_clan`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				String clanname = "";
				if (Config.CLIENT_LANGUAGE == 3) {
					clanname = rs.getString("clannamebig5");
				} else {
					clanname = rs.getString("clanname");
				}
				final int clan_id = rs.getInt("clan_id");
				String leader_name = "";
				if (Config.CLIENT_LANGUAGE == 3) {
					leader_name = rs.getString("leader_namebig5");
				} else {
					leader_name = rs.getString("leader_name");
				}
				final byte[] emblemicon = rs.getBytes("emblemicon");

				final DeClan deClan = new DeClan(clanname, clan_id, leader_name, emblemicon);
				_cdeclanList.put(leader_name, deClan);

				// 設置虛擬血盟資料
				final DeName de = DeNameTable.get().getDeName(leader_name);

				final L1Clan clan = new L1Clan();
				clan.setClanId(clan_id);
				clan.setClanName(clanname);
				clan.setLeaderId(de.get_deobjid());
				clan.setLeaderName(leader_name);
				clan.setCastleId(0);
				clan.setHouseId(0);

				// 建立盟輝資料
				ClanEmblemReading.get().add(clan_id, emblemicon);
				// 加入世界
				ClanReading.get().addDeClan(clan_id, clan);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		if (_reclanIcon) {
			loadIcon();
		}
	}

	public void loadIcon() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `de_emblem_tmp`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final byte[] icon = rs.getBytes("emblemicon");
				_emblemiconList.add(icon);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		// 分配盟輝
		getEmblemicon();
	}

	private final static Random _random = new Random();

	private void getEmblemicon() {
		_log.info("啟動盟輝分配: " + _emblemiconList.size());
		for (final DeClan deClan : _cdeclanList.values()) {
			final int index = _random.nextInt(_emblemiconList.size());
			final byte[] emblemicon = _emblemiconList.get(index);
			deClan.set_emblemicon(emblemicon);
			_emblemiconList.remove(index);

			updata_deClan(deClan);
		}
		_log.info("啟動盟輝分配完成: " + _emblemiconList.size());

	}

	/**
	 * 更新虛擬血盟資料
	 * 
	 * @param deName
	 */
	private void updata_deClan(final DeClan deClan) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactoryLogin.get().getConnection();
			ps = cn.prepareStatement("UPDATE `de_clan` SET `emblemicon`=? WHERE `clan_id`=?");
			int i = 0;
			ps.setBytes(++i, deClan.get_emblemicon());
			ps.setInt(++i, deClan.get_clan_id());
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 加入無領導者虛擬血盟領導者人物資料
	 */
	public void check() {
		for (final String name : _cdeclanList.keySet()) {
			final DeName de = DeNameTable.get().getDeName(name);
			if (de == null) {
				Connection cn = null;
				PreparedStatement ps = null;
				try {
					final DeClan xx = _cdeclanList.get(name);
					cn = DatabaseFactoryLogin.get().getConnection();
					ps = cn.prepareStatement(
							"INSERT INTO `de_name` SET `" + "dename`=?,`type`=?,`sex`=?,`clanid`=?");
					int i = 0;
					ps.setString(++i, xx.get_leader_name());
					ps.setInt(++i, 0);
					ps.setInt(++i, 0);
					ps.setInt(++i, xx.get_clan_id());
					ps.setString(++i, xx.get_clanname());
					ps.execute();

				} catch (final SQLException e) {
					_log.error(e.getLocalizedMessage(), e);

				} finally {
					SQLUtil.close(ps);
					SQLUtil.close(cn);
				}
			}
		}
	}

	/**
	 * 指定虛擬人物 是否為盟主
	 * 
	 * @param name
	 * @return
	 */
	public DeClan get(final String name) {
		return _cdeclanList.get(name);
	}

	/**
	 * 全部虛擬血盟
	 * 
	 * @return
	 */
	public Collection<DeClan> getList() {
		return _cdeclanList.values();
	}
}
