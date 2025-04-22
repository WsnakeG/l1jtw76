package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.storage.ClanStorage;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.WorldClan;

/**
 * 血盟資料
 * 
 * @author dexc
 */
public class ClanTable implements ClanStorage {

	private static final Log _log = LogFactory.getLog(ClanTable.class);

	private final Map<Integer, L1Clan> _clans = new HashMap<Integer, L1Clan>();

	/**
	 * 預先加載血盟資料
	 */
	@Override
	public void load() {
		{
			final PerformanceTimer timer = new PerformanceTimer();
			Connection cn = null;
			PreparedStatement ps = null;
			PreparedStatement ps2 = null; // added by terry0412
			ResultSet rs = null;
			ResultSet rs2 = null; // added by terry0412

			try {
				cn = DatabaseFactory.get().getConnection();
				ps = cn.prepareStatement("SELECT * FROM `clan_data` ORDER BY `clan_id`");

				rs = ps.executeQuery();
				while (rs.next()) {
					final L1Clan clan = new L1Clan();
					final int clan_id = rs.getInt("clan_id");
					clan.setClanId(clan_id);
					clan.setClanName(rs.getString("clan_name"));
					clan.setLeaderId(rs.getInt("leader_id"));
					clan.setLeaderName(rs.getString("leader_name"));
					clan.setCastleId(rs.getInt("hascastle"));
					clan.setHouseId(rs.getInt("hashouse"));
					clan.setEmblemId(rs.getInt("emblem_id"));
					clan.setClanNote("clannote");
					final String shownote = rs.getString("clanshownote");
					clan.setClanShowNote(shownote);
					
					clan.setShowEmblem(rs.getInt("showEmblem"));

					clan.setJoin_open_state(rs.getBoolean("join_open_state"));
					clan.setJoin_state(rs.getInt("join_state"));
					clan.setJoin_password(rs.getString("join_password"));
					
					final boolean clanskill = rs.getBoolean("clanskill");
					// 具有血盟技能
					if (clanskill) {
						clan.set_clanskill(clanskill);
						final Timestamp skilltime = rs.getTimestamp("skilltime");
						clan.set_skilltime(skilltime);
					}

					final int clan_step = rs.getInt("clan_step");
					clan.setClanStep(clan_step);

					ps2 = cn.prepareStatement(
							"SELECT COUNT(*) FROM `characters` WHERE `ClanID`=? AND `LastLogin`>?");
					ps2.setInt(1, clan_id);
					ps2.setTimestamp(2, new Timestamp(System.currentTimeMillis() - 604800000));
					rs2 = ps2.executeQuery();

					if (rs2.next()) {
						clan.setLoginLevel(rs2.getShort(1));
					}

					WorldClan.get().storeClan(clan);
					_clans.put(clan_id, clan);
				}

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(rs2); // added by terry0412
				SQLUtil.close(rs);
				SQLUtil.close(ps2); // added by terry0412
				SQLUtil.close(ps);
				SQLUtil.close(cn);
			}
			_log.info("載入血盟資料資料數量: " + _clans.size() + "(" + timer.get() + "ms)");
		}

		// 加入血盟人員名稱清單
		final Collection<L1Clan> AllClan = WorldClan.get().getAllClans();
		for (final L1Clan clan : AllClan) {
			Connection cn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				cn = DatabaseFactory.get().getConnection();
				ps = cn.prepareStatement("SELECT `char_name` FROM `characters` WHERE `ClanID`=?");
				ps.setInt(1, clan.getClanId());
				rs = ps.executeQuery();

				while (rs.next()) {
					clan.addMemberName(rs.getString("char_name"));
				}

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(ps);
				SQLUtil.close(cn);
			}
		}
		// 加載血盟倉庫資料
		for (final L1Clan clan : AllClan) {
			clan.getDwarfForClanInventory().loadItems();
		}
	}

	/**
	 * 加入虛擬血盟
	 * 
	 * @param integer
	 * @param l1Clan
	 */
	@Override
	public void addDeClan(final Integer integer, final L1Clan l1Clan) {
		WorldClan.get().storeClan(l1Clan);
		_clans.put(integer, l1Clan);
	}

	/**
	 * 建立血盟資料
	 * 
	 * @param player
	 * @param clan_name
	 * @return
	 */
	@Override
	public L1Clan createClan(final L1PcInstance player, final String clan_name) {
		final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
		for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
			final L1Clan oldClans = iter.next();
			if (oldClans.getClanName().equalsIgnoreCase(clan_name)) {// 搜尋名稱
				return null;
			}
		}
		final L1Clan clan = new L1Clan();
		clan.setClanId(IdFactory.get().nextId());
		clan.setClanName(clan_name);
		clan.setLeaderId(player.getId());
		clan.setLeaderName(player.getName());
		clan.setCastleId(0);
		clan.setHouseId(0);
		clan.set_clanskill(false);
		clan.setClanStep(1);
		clan.setClanNote("無");
		clan.setClanShowNote("盟主很懶，什麼都沒有留下！");
		clan.setEmblemId(clan.getClanId());

		Connection cn = null;
		PreparedStatement ps = null;

		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("INSERT INTO `clan_data` SET `clan_id`=?,`clan_name`=?,"
					+ "`leader_id`=?,`leader_name`=?,`hascastle`=?,`hashouse`=?,"
					+ "`clanskill`=?,`skilltime`=?,`clan_step`=?,`emblem_id`=?,`clannote`=?,`clanshownote`=?,join_open_state=?,join_state=?,join_password=?");
			int i = 0;
			ps.setInt(++i, clan.getClanId());
			ps.setString(++i, clan.getClanName());
			ps.setInt(++i, clan.getLeaderId());
			ps.setString(++i, clan.getLeaderName());
			ps.setInt(++i, clan.getCastleId());
			ps.setInt(++i, clan.getHouseId());
			ps.setBoolean(++i, clan.isClanskill());
			ps.setTimestamp(++i, clan.get_skilltime());
			ps.setInt(++i, clan.getClanStep());
			ps.setInt(++i, clan.getEmblemId());
			ps.setString(++i, clan.getClanNote());
			ps.setString(++i, clan.getClanShowNote());
			ps.setBoolean(++i, clan.getJoin_open_state());
			ps.setInt(++i, clan.getJoin_state());
			ps.setString(++i, clan.getJoin_password());
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}

		WorldClan.get().storeClan(clan);
		_clans.put(clan.getClanId(), clan);

		player.setClanid(clan.getClanId());
		player.setClanname(clan.getClanName());
		// 不進行任務直接set 為一般君主
		// 若要set 為聯盟君主，看是否要依照道具來設定 (暫定)
		// if (player.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
		// player.setClanRank(L1Clan.CLAN_RANK_PRINCE);
		// } else {
		// player.setClanRank(L1Clan.NORMAL_CLAN_RANK_PRINCE);
		// }
		player.setClanRank(L1Clan.NORMAL_CLAN_RANK_PRINCE);
		clan.addMemberName(player.getName());
		try {
			// 資料存檔
			player.save();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
		return clan;
	}

	/**
	 * 更新血盟資料
	 * 
	 * @param clan
	 */
	@Override
	public void updateClan(final L1Clan clan) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("UPDATE clan_data SET `clan_id`=?,`leader_id`=?,"
					+ "`leader_name`=?,`hascastle`=?,`hashouse`=?,"
					+ "`clanskill`=?,`skilltime`=?,`clan_step`=?,`emblem_id`=?,`clannote`=?,`clanshownote`=?,`join_open_state`=?,`join_state`=?,`join_password`=? "
					+ "WHERE `clan_name`=?");
			int i = 0;
			ps.setInt(++i, clan.getClanId());
			ps.setInt(++i, clan.getLeaderId());
			ps.setString(++i, clan.getLeaderName());
			ps.setInt(++i, clan.getCastleId());
			ps.setInt(++i, clan.getHouseId());
			ps.setBoolean(++i, clan.isClanskill());
			ps.setTimestamp(++i, clan.get_skilltime());
			ps.setInt(++i, clan.getClanStep());
			ps.setInt(++i, clan.getEmblemId());
			System.out.println("存儲公告：" + clan.getClanShowNote());
			ps.setString(++i, clan.getClanNote());
			ps.setString(++i, clan.getClanShowNote());
			ps.setBoolean(++i, clan.getJoin_open_state());
			ps.setInt(++i, clan.getJoin_state());
			ps.setString(++i, clan.getJoin_password());
			ps.setString(++i, clan.getClanName());
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 刪除血盟資料
	 * 
	 * @param clan_name
	 */
	@Override
	public void deleteClan(final String clan_name) {
		final L1Clan clan = WorldClan.get().getClan(clan_name);
		if (clan == null) {
			return;
		}
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `clan_data` WHERE `clan_name`=?");
			ps.setString(1, clan_name);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		clan.getDwarfForClanInventory().clearItems();
		clan.getDwarfForClanInventory().deleteAllItems();

		WorldClan.get().removeClan(clan);
		_clans.remove(clan.getClanId());
	}

	/**
	 * 指定血盟資料
	 * 
	 * @param clan_id
	 * @return
	 */
	@Override
	public L1Clan getTemplate(final int clan_id) {
		return _clans.get(clan_id);
	}

	/**
	 * 全部血盟資料
	 * 
	 * @return
	 */
	@Override
	public Map<Integer, L1Clan> get_clans() {
		return _clans;
	}

}
