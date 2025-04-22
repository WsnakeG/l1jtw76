package com.lineage.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.serverpackets.S_CharAmount;
import com.lineage.server.serverpackets.S_CharPacks;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.WorldClan;

/**
 * ◎登入端服務端共用-要求顯示人物列表
 * 
 * @author daien
 */
public class C_CommonClick extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CommonClick.class);

	/*
	 * public C_CommonClick() { } public C_CommonClick(final byte[] abyte0,
	 * final ClientExecutor client) { super(abyte0); try { this.start(abyte0,
	 * client); } catch (final Exception e) {
	 * _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			deleteCharacter(client); // 指定人物刪除時間抵達 執行人物刪除

			final int amountOfChars = client.getAccount().get_countCharacters();

			client.out().encrypt(new S_CharAmount(amountOfChars, client));

			// 未知
			// client.out().encrypt(new S_Unknown_B());

			if (amountOfChars > 0) {
				sendCharPacks(client);
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	private void deleteCharacter(final ClientExecutor client) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			conn = DatabaseFactory.get().getConnection();
			pstm = conn
					.prepareStatement("SELECT * FROM `characters` WHERE `account_name`=? ORDER BY `objid`");
			pstm.setString(1, client.getAccountName());
			rs = pstm.executeQuery();

			while (rs.next()) {
				final String name = rs.getString("char_name");
				final String clanname = rs.getString("Clanname");

				final Timestamp deleteTime = rs.getTimestamp("DeleteTime");
				if (deleteTime != null) {
					final Calendar cal = Calendar.getInstance();
					final long checkDeleteTime = ((cal.getTimeInMillis() - deleteTime.getTime()) / 1000)
							/ 3600;
					if (checkDeleteTime >= 0) {
						final L1Clan clan = WorldClan.get().getClan(clanname);
						if (clan != null) {
							clan.delMemberName(name);
						}
						// 已創人物數量
						final int countCharacters = client.getAccount().get_countCharacters();
						client.getAccount().set_countCharacters(countCharacters - 1);

						// 移出已用名稱清單
						CharObjidTable.get().charRemove(name);
						// 刪除人物
						CharacterTable.get().deleteCharacter(client.getAccountName(), name);
					}
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	private void sendCharPacks(final ClientExecutor client) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			conn = DatabaseFactory.get().getConnection();
			pstm = conn
					.prepareStatement("SELECT * FROM `characters` WHERE `account_name`=? ORDER BY `objid`");
			pstm.setString(1, client.getAccountName());
			rs = pstm.executeQuery();

			while (rs.next()) {
				final String name = rs.getString("char_name");
				final String clanname = rs.getString("Clanname");
				final int type = rs.getInt("Type");
				final byte sex = rs.getByte("Sex");
				final int lawful = rs.getInt("Lawful");

				int currenthp = rs.getInt("CurHp");
				if (currenthp < 1) {
					currenthp = 1;
				} else if (currenthp > 32767) {
					currenthp = 32767;
				}

				int currentmp = rs.getInt("CurMp");
				if (currentmp < 1) {
					currentmp = 1;
				} else if (currentmp > 32767) {
					currentmp = 32767;
				}

				int lvl = rs.getInt("level");
				if (lvl < 1) {
					lvl = 1;
				}

				final int ac = rs.getInt("Ac");
				final int str = rs.getInt("Str");
				final int dex = rs.getInt("Dex");
				final int con = rs.getInt("Con");
				final int wis = rs.getInt("Wis");
				final int cha = rs.getInt("Cha");
				final int intel = rs.getInt("Intel");
				final int time = rs.getInt("CreateTime");

				final S_CharPacks cpk = new S_CharPacks(name, clanname, type, sex, lawful, currenthp,
						currentmp, ac, lvl, str, dex, con, wis, cha, intel, time);

				client.out().encrypt(cpk);
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}