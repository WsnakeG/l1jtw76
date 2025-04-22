package com.lineage.server.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.CharOtherReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.storage.CharacterStorage;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.utils.SQLUtil;

/**
 * PC資料
 * 
 * @author daien
 */
public class MySqlCharacterStorage implements CharacterStorage {

	private static final Log _log = LogFactory
			.getLog(MySqlCharacterStorage.class);

	/**
	 * 載入PC資料
	 */
	@Override
	public L1PcInstance loadCharacter(final String charName) {
		L1PcInstance pc = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM characters WHERE char_name=?");
			pstm.setString(1, charName);

			rs = pstm.executeQuery();
			if (!rs.next()) {
				/*
				 * SELECTが結果を返さなかった。
				 */
				return null;
			}
			pc = new L1PcInstance();
			final String loginName = rs.getString("account_name").toLowerCase();
			pc.setAccountName(loginName);

			final int objid = rs.getInt("objid");
			pc.setId(objid);
			// 副本編號預先以-1為準
			pc.set_showId(-1);
			// TODO 額外紀錄項次
			L1PcOther other = CharOtherReading.get().getOther(pc);
			if (other == null) {
				other = new L1PcOther();
				other.set_objid(objid);
			}
			pc.set_other(other);

			pc.setName(rs.getString("char_name"));
			pc.setHighLevel(rs.getInt("HighLevel"));
			pc.setExp(rs.getLong("Exp"));
			pc.addBaseMaxHp(rs.getShort("MaxHp"));
			short currentHp = rs.getShort("CurHp");
			if (currentHp < 1) {
				currentHp = 1;
			}
			pc.setDead(false);
			pc.setCurrentHpDirect(currentHp);
			pc.setStatus(0);
			pc.addBaseMaxMp(rs.getShort("MaxMp"));
			pc.setCurrentMpDirect(rs.getShort("CurMp"));
			pc.addBaseStr(rs.getInt("Str"));
			pc.addBaseCon(rs.getInt("Con"));
			pc.addBaseDex(rs.getInt("Dex"));
			pc.addBaseCha(rs.getInt("Cha"));
			pc.addBaseInt(rs.getInt("Intel"));
			pc.addBaseWis(rs.getInt("Wis"));
			final int status = rs.getInt("Status");
			pc.setCurrentWeapon(status);
			final int classId = rs.getInt("Class");
			pc.setClassId(classId);
			pc.setTempCharGfx(classId);
			pc.setGfxId(classId);
			pc.set_sex(rs.getInt("Sex"));
			pc.setType(rs.getInt("Type"));
			int head = rs.getInt("Heading");
			if (head > 7) {
				head = 0;
			}
			pc.setHeading(head);

			pc.setX(rs.getInt("locX"));
			pc.setY(rs.getInt("locY"));
			pc.setMap(rs.getShort("MapID"));
			pc.set_food(rs.getInt("Food"));
			pc.setLawful(rs.getInt("Lawful"));
			pc.setTitle(rs.getString("Title"));
			pc.setClanid(rs.getInt("ClanID"));
			pc.setClanname(rs.getString("Clanname"));
			pc.setClanRank(rs.getInt("ClanRank"));
			pc.setBonusStats(rs.getInt("BonusStatus"));
			pc.setElixirStats(rs.getInt("ElixirStatus"));
			pc.setElfAttr(rs.getInt("ElfAttr"));
			pc.set_PKcount(rs.getInt("PKcount"));
			pc.setPkCountForElf(rs.getInt("PkCountForElf"));
			pc.setExpRes(rs.getInt("ExpRes"));
			pc.setPartnerId(rs.getInt("PartnerID"));
			pc.setAccessLevel(rs.getShort("AccessLevel"));

			if (pc.getAccessLevel() >= 200) {
				pc.setGm(true);
				pc.setMonitor(false);

			} else if (pc.getAccessLevel() == 100) {
				pc.setGm(false);
				pc.setMonitor(true);

			} else {
				pc.setGm(false);
				pc.setMonitor(false);
			}

			pc.setOnlineStatus(rs.getInt("OnlineStatus"));
			pc.setHomeTownId(rs.getInt("HomeTownID"));
			pc.setContribution(rs.getInt("Contribution"));
			pc.setHellTime(rs.getInt("HellTime"));
			pc.setBanned(rs.getBoolean("Banned"));
			pc.setKarma(rs.getInt("Karma"));
			pc.setLastPk(rs.getTimestamp("LastPk"));
			pc.setLastPkForElf(rs.getTimestamp("LastPkForElf"));
			pc.setDeleteTime(rs.getTimestamp("DeleteTime"));
			pc.setBirthday(rs.getString("CreateTime"));
			pc.setOriginalStr(rs.getInt("OriginalStr"));
			pc.setOriginalCon(rs.getInt("OriginalCon"));
			pc.setOriginalDex(rs.getInt("OriginalDex"));
			pc.setOriginalCha(rs.getInt("OriginalCha"));
			pc.setOriginalInt(rs.getInt("OriginalInt"));
			pc.setOriginalWis(rs.getInt("OriginalWis"));
			// 轉生次數 by terry0412
			pc.setMeteLevel(rs.getInt("MeteLevel"));
			// 師徒系統 - 懲罰時間 by terry0412
			pc.setPunishTime(rs.getTimestamp("PunishTime"));
			pc.setRingsExpansion(rs.getByte("RingsExpansion"));
			pc.setClanMemberNotes(rs.getString("ClanMemberNotes"));
			pc.setOnlineGiftIndex(rs.getInt("OnlineGiftIndex"));
			pc.setOnlineGiftWiatEnd(rs.getBoolean("OnlineGiftWiatEnd"));
			pc.setVipStartTime(rs.getTimestamp("VipStartTime"));
			pc.setVipEndTime(rs.getTimestamp("VipEndTime"));
			pc.set_vipLevel(rs.getInt("VipLevel"));
			// 特效驗證時間
			pc.setAITimer(rs.getInt("AI_TIMES"));

			pc.refresh();
			pc.setMoveSpeed(0);
			pc.setBraveSpeed(0);
			pc.setGmInvis(false);

			// _log.finest("restored char data: ");

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
			return null;

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return pc;
	}

	@Override
	public void createCharacter(final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO characters SET account_name=?,objid=?,"
							+ "char_name=?,level=?,HighLevel=?,Exp=?,MaxHp=?,CurHp=?,"
							+ "MaxMp=?,CurMp=?,Ac=?,Str=?,Con=?,Dex=?,Cha=?,Intel=?,"
							+ "Wis=?,Status=?,Class=?,Sex=?,Type=?,Heading=?,LocX=?,"
							+ "LocY=?,MapID=?,Food=?,Lawful=?,Title=?,ClanID=?,Clanname=?,"
							+ "ClanRank=?,BonusStatus=?,ElixirStatus=?,ElfAttr=?,PKcount=?,"
							+ "PkCountForElf=?,ExpRes=?,PartnerID=?,AccessLevel=?,OnlineStatus=?,"
							+ "HomeTownID=?,Contribution=?,Pay=?,HellTime=?,Banned=?,Karma=?,"
							+ "LastPk=?,LastPkForElf=?,DeleteTime=?,CreateTime=?,ClanMemberNotes=?,AI_TIMES=?");
			pstm.setString(++i, pc.getAccountName());
			pstm.setInt(++i, pc.getId());
			pstm.setString(++i, pc.getName());
			pstm.setInt(++i, pc.getLevel());
			pstm.setInt(++i, pc.getHighLevel());
			pstm.setLong(++i, pc.getExp());
			pstm.setInt(++i, pc.getBaseMaxHp());
			int hp = pc.getCurrentHp();
			if (hp < 1) {
				hp = 1;
			}
			pstm.setInt(++i, hp);
			pstm.setInt(++i, pc.getBaseMaxMp());
			pstm.setInt(++i, pc.getCurrentMp());
			pstm.setInt(++i, pc.getAc());
			pstm.setInt(++i, pc.getBaseStr());
			pstm.setInt(++i, pc.getBaseCon());
			pstm.setInt(++i, pc.getBaseDex());
			pstm.setInt(++i, pc.getBaseCha());
			pstm.setInt(++i, pc.getBaseInt());
			pstm.setInt(++i, pc.getBaseWis());
			pstm.setInt(++i, pc.getCurrentWeapon());
			pstm.setInt(++i, pc.getClassId());
			pstm.setInt(++i, pc.get_sex());
			pstm.setInt(++i, pc.getType());
			pstm.setInt(++i, pc.getHeading());
			pstm.setInt(++i, pc.getX());
			pstm.setInt(++i, pc.getY());
			pstm.setInt(++i, pc.getMapId());
			pstm.setInt(++i, pc.get_food());
			pstm.setInt(++i, pc.getLawful());
			pstm.setString(++i, pc.getTitle());
			pstm.setInt(++i, pc.getClanid());
			pstm.setString(++i, pc.getClanname());
			pstm.setInt(++i, pc.getClanRank());
			pstm.setInt(++i, pc.getBonusStats());
			pstm.setInt(++i, pc.getElixirStats());
			pstm.setInt(++i, pc.getElfAttr());
			pstm.setInt(++i, pc.get_PKcount());
			pstm.setInt(++i, pc.getPkCountForElf());
			pstm.setInt(++i, pc.getExpRes());
			pstm.setInt(++i, pc.getPartnerId());
			short leve = pc.getAccessLevel();
			if (leve >= 20000) {
				leve = 0;
			}
			pstm.setShort(++i, leve);
			pstm.setInt(++i, pc.getOnlineStatus());
			pstm.setInt(++i, pc.getHomeTownId());
			pstm.setInt(++i, pc.getContribution());
			pstm.setInt(++i, 0);
			pstm.setInt(++i, pc.getHellTime());
			pstm.setBoolean(++i, pc.isBanned());
			pstm.setInt(++i, pc.getKarma());
			pstm.setTimestamp(++i, pc.getLastPk());
			pstm.setTimestamp(++i, pc.getLastPkForElf());
			pstm.setTimestamp(++i, pc.getDeleteTime());

			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			final String times = sdf.format(System.currentTimeMillis());
			final int time = Integer.parseInt(times.replace("-", ""));
			pstm.setInt(++i, time);

			pstm.setString(++i, pc.getClanMemberNotes());
			pstm.setInt(++i, pc.getAITimer());
			pstm.execute();

			// _log.finest("stored char data: " + pc.getName());

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void deleteCharacter(final String accountName, final String charName)
			throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm1 = null;
		PreparedStatement pstm2 = null;
		PreparedStatement pstm3 = null;
		PreparedStatement pstm4 = null;
		PreparedStatement pstm5 = null;
		PreparedStatement pstm6 = null;
		PreparedStatement pstm7 = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM characters WHERE account_name=? AND char_name=?");
			pstm.setString(1, accountName);
			pstm.setString(2, charName);
			rs = pstm.executeQuery();

			if (!rs.next()) {
				throw new RuntimeException("could not delete character");
			}

			final int objid = CharObjidTable.get().charObjid(charName);

			if (objid != 0) {
				// 刪除人物背包資料
				CharItemsReading.get().delUserItems(objid);
			}

			pstm1 = con
					.prepareStatement("DELETE FROM character_buddys WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm1.setString(1, charName);
			pstm1.execute();

			pstm2 = con
					.prepareStatement("DELETE FROM character_buff WHERE char_obj_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm2.setString(1, charName);
			pstm2.execute();

			pstm3 = con
					.prepareStatement("DELETE FROM character_config WHERE object_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm3.setString(1, charName);
			pstm3.execute();

			pstm4 = con
					.prepareStatement("DELETE FROM character_quests WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm4.setString(1, charName);
			pstm4.execute();

			pstm5 = con
					.prepareStatement("DELETE FROM character_skills WHERE char_obj_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm5.setString(1, charName);
			pstm5.execute();

			pstm6 = con
					.prepareStatement("DELETE FROM character_teleport WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm6.setString(1, charName);
			pstm6.execute();

			pstm7 = con
					.prepareStatement("DELETE FROM characters WHERE char_name=?");
			pstm7.setString(1, charName);
			pstm7.execute();
			// System.out.println("num7");
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm3);
			SQLUtil.close(pstm4);
			SQLUtil.close(pstm5);
			SQLUtil.close(pstm6);
			SQLUtil.close(pstm7);
			SQLUtil.close(con);
		}
	}

	@Override
	public void storeCharacter(final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("UPDATE characters SET level=?,HighLevel=?,Exp=?,"
							+ "MaxHp=?,CurHp=?,MaxMp=?,CurMp=?,Ac=?,Str=?,"
							+ "Con=?,Dex=?,Cha=?,Intel=?,Wis=?,Status=?,"
							+ "Class=?,Sex=?,Type=?,Heading=?,LocX=?,LocY=?,"
							+ "MapID=?,Food=?,Lawful=?,Title=?,ClanID=?,"
							+ "Clanname=?,ClanRank=?,BonusStatus=?,"
							+ "ElixirStatus=?,ElfAttr=?,PKcount=?,PkCountForElf=?,"
							+ "ExpRes=?,PartnerID=?,AccessLevel=?,OnlineStatus=?,"
							+ "HomeTownID=?,Contribution=?,HellTime=?,Banned=?,"
							+ "Karma=?,LastPk=?,LastPkForElf=?,"
							+ "DeleteTime=?,ClanMemberNotes=?,MeteLevel=?,PunishTime=?,"
							+ "OnlineGiftIndex=?,OnlineGiftWiatEnd=?,VipLevel=?,VipStartTime=?,VipEndTime=?,AI_TIMES=? WHERE objid=?");
			pstm.setInt(++i, pc.getLevel());
			pstm.setInt(++i, pc.getHighLevel());
			pstm.setLong(++i, pc.getExp());
			pstm.setInt(++i, pc.getBaseMaxHp());
			int hp = pc.getCurrentHp();
			if (hp < 1) {
				hp = 1;
			}
			pstm.setInt(++i, hp);
			pstm.setInt(++i, pc.getBaseMaxMp());
			pstm.setInt(++i, pc.getCurrentMp());
			pstm.setInt(++i, pc.getAc());
			pstm.setInt(++i, pc.getBaseStr());
			pstm.setInt(++i, pc.getBaseCon());
			pstm.setInt(++i, pc.getBaseDex());
			pstm.setInt(++i, pc.getBaseCha());
			pstm.setInt(++i, pc.getBaseInt());
			pstm.setInt(++i, pc.getBaseWis());
			pstm.setInt(++i, pc.getCurrentWeapon());
			pstm.setInt(++i, pc.getClassId());
			pstm.setInt(++i, pc.get_sex());
			pstm.setInt(++i, pc.getType());
			pstm.setInt(++i, pc.getHeading());
			pstm.setInt(++i, pc.getX());
			pstm.setInt(++i, pc.getY());
			pstm.setInt(++i, pc.getMapId());
			pstm.setInt(++i, pc.get_food());
			pstm.setInt(++i, pc.getLawful());
			pstm.setString(++i, pc.getTitle());
			pstm.setInt(++i, pc.getClanid());
			pstm.setString(++i, pc.getClanname());
			pstm.setInt(++i, pc.getClanRank());
			pstm.setInt(++i, pc.getBonusStats());
			pstm.setInt(++i, pc.getElixirStats());
			pstm.setInt(++i, pc.getElfAttr());
			pstm.setInt(++i, pc.get_PKcount());
			pstm.setInt(++i, pc.getPkCountForElf());
			pstm.setInt(++i, pc.getExpRes());
			pstm.setInt(++i, pc.getPartnerId());
			short leve = pc.getAccessLevel();
			if (leve >= 20000) {
				leve = 0;
			}
			pstm.setShort(++i, leve);
			pstm.setInt(++i, pc.getOnlineStatus());
			pstm.setInt(++i, pc.getHomeTownId());
			pstm.setInt(++i, pc.getContribution());
			pstm.setInt(++i, pc.getHellTime());
			pstm.setBoolean(++i, pc.isBanned());
			pstm.setInt(++i, pc.getKarma());
			pstm.setTimestamp(++i, pc.getLastPk());
			pstm.setTimestamp(++i, pc.getLastPkForElf());
			pstm.setTimestamp(++i, pc.getDeleteTime());
			// System.out.println("存儲備註：" + pc.getClanMemberNotes());
			pstm.setString(++i, pc.getClanMemberNotes());
			// 轉生次數 by terry0412
			pstm.setInt(++i, pc.getMeteLevel());
			// 師徒系統 - 懲罰時間 by terry0412
			pstm.setTimestamp(++i, pc.getPunishTime());
			pstm.setInt(++i, pc.getOnlineGiftIndex());
			pstm.setBoolean(++i, pc.isOnlineGiftWiatEnd());
			pstm.setInt(++i, pc.get_vipLevel());
			pstm.setTimestamp(++i, pc.getVipStartTime());
			pstm.setTimestamp(++i, pc.getVipEndTime());
			pstm.setInt(++i, pc.getAITimer());
			pstm.setInt(++i, pc.getId());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
