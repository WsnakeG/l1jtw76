package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.MailStorage;
import com.lineage.server.templates.L1Mail;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 信件資料
 * 
 * @author dexc
 */
public class MailTable implements MailStorage {

	private static final Log _log = LogFactory.getLog(MailTable.class);

	// 加入TreeMap#id 排序 by terry0412
	private static final Map<Integer, L1Mail> _allMail = new TreeMap<Integer, L1Mail>();

	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `character_mail`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final L1Mail mail = new L1Mail();
				final int id = rs.getInt("id");
				mail.setId(id);
				mail.setType(rs.getInt("type"));
				final String sender = rs.getString("sender");
				mail.setSenderName(sender);

				final String receiver = rs.getString("receiver");
				mail.setReceiverName(receiver);
				mail.setDate(rs.getTimestamp("date"));
				mail.setReadStatus(rs.getInt("read_status"));
				mail.setSubject(rs.getBytes("subject"));
				mail.setContent(rs.getBytes("content"));
				// 信件發送類型 (0:發信 1:收信) by terry0412
				mail.setSendType(rs.getInt("send_type"));

				// 檢查名稱是否已被使用
				if (CharObjidTable.get().charObjid(receiver) != 0) {
					_allMail.put(id, mail);

				} else {
					deleteMail(receiver);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入信件資料數量: " + _allMail.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 收件人遺失 刪除信件
	 * 
	 * @param receiver
	 */
	private void deleteMail(final String receiver) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM `character_mail` WHERE `receiver`=?");
			pstm.setString(1, receiver);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void setReadStatus(final int mailId) {
		final L1Mail mail = _allMail.get(mailId);
		if (mail != null) {
			mail.setReadStatus(1);

			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = DatabaseFactory.get().getConnection();
				rs = con.createStatement()
						.executeQuery("SELECT * FROM `character_mail` WHERE `id`=" + mailId);
				if ((rs != null) && rs.next()) {
					pstm = con.prepareStatement(
							"UPDATE `character_mail` SET `read_status`=? WHERE `id`=" + mailId);
					pstm.setInt(1, 1);
					pstm.execute();

				}
			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}

	@Override
	public void setMailType(final int mailId, final int type) {
		final L1Mail mail = _allMail.get(mailId);
		if (mail != null) {
			mail.setType(type);

			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = DatabaseFactory.get().getConnection();
				rs = con.createStatement()
						.executeQuery("SELECT * FROM `character_mail` WHERE `id`=" + mailId);
				if ((rs != null) && rs.next()) {
					pstm = con.prepareStatement("UPDATE `character_mail` SET `type`=? WHERE `id`=" + mailId);
					pstm.setInt(1, type);
					pstm.execute();
				}

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}

	@Override
	public void deleteMail(final int mailId) {
		final L1Mail mail = _allMail.remove(mailId);
		if (mail != null) {
			Connection con = null;
			PreparedStatement pstm = null;
			try {
				con = DatabaseFactory.get().getConnection();
				pstm = con.prepareStatement("DELETE FROM `character_mail` WHERE `id`=?");
				pstm.setInt(1, mailId);
				pstm.execute();

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}

	/**
	 * 寫入信件 by terry0412
	 * 
	 * @param type
	 * @param receiver
	 * @param writer
	 * @param text
	 * @param sendType 信件發送類型 (0:發信 1:收信)
	 * @return
	 */
	@Override
	public L1Mail writeMail(final int type, final String receiver, final String writer, final byte[] text,
			final int sendType) { // 信件發送類型
									// (0:發信
									// 1:收信)
									// by
									// terry0412
		final int readStatus = 0;

		// 發信日期 (修正 by terry0412)
		final Timestamp date = new Timestamp(System.currentTimeMillis());

		// subjectとcontentの区切り(0x00 0x00)位置を見つける
		int spacePosition1 = 0;
		int spacePosition2 = 0;
		for (int i = 0; i < text.length; i += 2) {
			if ((text[i] == 0) && (text[i + 1] == 0)) {
				if (spacePosition1 == 0) {
					spacePosition1 = i;
				} else if ((spacePosition1 != 0) && (spacePosition2 == 0)) {
					spacePosition2 = i;
					break;
				}
			}
		}

		// mailテーブルに書き込む
		final int subjectLength = spacePosition1 + 2;
		int contentLength = spacePosition2 - spacePosition1;
		if (contentLength <= 0) {
			contentLength = 1;
		}
		final byte[] subject = new byte[subjectLength];
		final byte[] content = new byte[contentLength];
		System.arraycopy(text, 0, subject, 0, subjectLength);
		System.arraycopy(text, subjectLength, content, 0, contentLength);

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO `character_mail` SET " + "`id`=?,`type`=?,`sender`=?,`receiver`=?,"
							+ "`date`=?,`read_status`=?,`subject`=?,`content`=?,`send_type`=?");
			final int id = IdFactory.get().nextId();
			pstm.setInt(1, id);
			pstm.setInt(2, type);
			pstm.setString(3, writer);
			pstm.setString(4, receiver);
			pstm.setTimestamp(5, date); // 日期類型變更 by terry0412
			pstm.setInt(6, readStatus);
			pstm.setBytes(7, subject);
			pstm.setBytes(8, content);
			pstm.setInt(9, sendType); // 信件發送類型 (0:發信 1:收信) by terry0412
			pstm.execute();

			final L1Mail mail = new L1Mail();
			mail.setId(id);
			mail.setType(type);
			mail.setSenderName(writer);
			mail.setReceiverName(receiver);
			mail.setDate(date); // 日期類型變更 by terry0412
			mail.setSubject(subject);
			mail.setContent(content);
			mail.setReadStatus(readStatus);
			// 信件發送類型 (0:發信 1:收信) by terry0412
			mail.setSendType(sendType);

			_allMail.put(id, mail);
			return mail;

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return null;
	}

	@Override
	public Map<Integer, L1Mail> getAllMail() {
		return _allMail;
	}

	@Override
	public L1Mail getMail(final int mailId) {
		return _allMail.get(mailId);
	}

	/**
	 * 信箱系統 - 更新玩家名稱 (用於更名卡修正) by terry0412
	 * 
	 * @param ori_name
	 * @param new_name
	 */
	@Override
	public void renewPcName(final String ori_name, final String new_name) {
		// 更換暫存資料
		for (final L1Mail mail : _allMail.values()) {
			if (mail.getSenderName().equalsIgnoreCase(ori_name)) {
				mail.setSenderName(new_name);
			}
			if (mail.getReceiverName().equalsIgnoreCase(ori_name)) {
				mail.setReceiverName(new_name);
			}
		}
		// 更新至資料庫
		Connection con = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm2 = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `character_mail` SET `sender`=? WHERE `sender`=?");
			pstm.setString(1, new_name);
			pstm.setString(2, ori_name);
			pstm.execute();

			pstm2 = con.prepareStatement("UPDATE `character_mail` SET `receiver`=? WHERE `receiver`=?");
			pstm2.setString(1, new_name);
			pstm2.setString(2, ori_name);
			pstm2.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
