package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharBookStorage;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Bookmarks;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1BookMark;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 記憶座標紀錄資料
 * 
 * @author dexc
 */
public class CharBookTable implements CharBookStorage {

	private static final Log _log = LogFactory.getLog(CharBookTable.class);

	private static final Map<Integer, ArrayList<L1BookMark>> _bookmarkMap = new HashMap<Integer, ArrayList<L1BookMark>>();

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_teleport`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int id = rs.getInt("id");
				final int char_id = rs.getInt("char_id");

				// 檢查該資料所屬是否遺失
				if (CharObjidTable.get().isChar(char_id) != null) {
					final String name = rs.getString("name");
					final int locx = rs.getInt("locx");
					final int locy = rs.getInt("locy");
					final short mapid = rs.getShort("mapid");

					final L1BookMark bookmark = new L1BookMark();
					bookmark.setId(id);
					bookmark.setCharId(char_id);
					bookmark.setName(name);
					bookmark.setLocX(locx);
					bookmark.setLocY(locy);
					bookmark.setMapId(mapid);

					addMap(char_id, bookmark);

				} else {
					delete(char_id);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入記憶座標紀錄資料數量: " + _bookmarkMap.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 刪除遺失記憶座標紀錄資料
	 * 
	 * @param objid 人物OBJID
	 */
	private static void delete(final int objid) {
		// 清空資料庫紀錄
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `character_teleport` WHERE `char_id`=?");
			ps.setInt(1, objid);
			ps.execute();

			_bookmarkMap.remove(objid);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);

		}
	}

	/**
	 * 加入記憶座標紀錄清單
	 * 
	 * @param objId
	 * @param buffTmp
	 */
	private static void addMap(final int objId, final L1BookMark bookmark) {
		ArrayList<L1BookMark> bookmarks = _bookmarkMap.get(objId);
		if (!L1TownLocation.isGambling(bookmark.getLocX(), bookmark.getLocY(), bookmark.getMapId())) {
			if (bookmarks == null) {
				bookmarks = new ArrayList<L1BookMark>();
				bookmarks.add(bookmark);
				_bookmarkMap.put(objId, bookmarks);

			} else {
				bookmarks.add(bookmark);
			}

		} else {
			deleteBookmark(bookmark.getId());
		}
	}

	/**
	 * 刪除記憶座標
	 * 
	 * @param id 座標編號
	 */
	private static void deleteBookmark(final int id) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {

			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM `character_teleport` WHERE `id`=?");
			pstm.setInt(1, id);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 取回保留記憶座標紀錄群
	 * 
	 * @param pc
	 */
	@Override
	public ArrayList<L1BookMark> getBookMarks(final L1PcInstance pc) {
		final ArrayList<L1BookMark> bookmarks = _bookmarkMap.get(pc.getId());
		return bookmarks;
	}

	/**
	 * 取回保留記憶座標紀錄
	 * 
	 * @param pc
	 */
	@Override
	public L1BookMark getBookMark(final L1PcInstance pc, final int i) {
		final ArrayList<L1BookMark> bookmarks = _bookmarkMap.get(pc.getId());
		if (bookmarks != null) {
			final int x = i & 0xffff;
			final int y = (i >> 16) & 0xffff;

			for (final L1BookMark book : bookmarks) {
				if ((book.getLocX() == x) && (book.getLocY() == y)) {
					return book;
				}
			}
		}
		return null;
	}

	/**
	 * 刪除記憶座標
	 * 
	 * @param pc
	 * @param s
	 */
	@Override
	public void deleteBookmark(final L1PcInstance pc, final String s) {
		final ArrayList<L1BookMark> bookmarks = _bookmarkMap.get(pc.getId());
		if (bookmarks != null) {
			for (final L1BookMark book : bookmarks) {
				if (book.getName().equalsIgnoreCase(s)) {
					Connection con = null;
					PreparedStatement pstm = null;
					try {

						con = DatabaseFactory.get().getConnection();
						pstm = con.prepareStatement("DELETE FROM `character_teleport` WHERE `id`=?");
						pstm.setInt(1, book.getId());
						pstm.execute();

						bookmarks.remove(book);

					} catch (final SQLException e) {
						_log.error(e.getLocalizedMessage(), e);

					} finally {
						SQLUtil.close(pstm);
						SQLUtil.close(con);
					}
				}
			}
		}
	}

	/**
	 * 增加記憶座標
	 * 
	 * @param pc
	 * @param s
	 */
	@Override
	public void addBookmark(final L1PcInstance pc, final String s) {
		if (!pc.getMap().isMarkable()) {
			// \f1這個地點不能夠標記。
			pc.sendPackets(new S_ServerMessage(214));
			return;
		}

		ArrayList<L1BookMark> bookmarks = _bookmarkMap.get(pc.getId());
		if (bookmarks == null) {
			bookmarks = new ArrayList<L1BookMark>();
			_bookmarkMap.put(pc.getId(), bookmarks);
		}

		final int size = bookmarks.size();
		if (size > 49) {
			// 676 \f1擁有太多座標記憶點。
			pc.sendPackets(new S_ServerMessage(676));
			return;
		}

		for (final L1BookMark book : bookmarks) {
			if (book.getName().equalsIgnoreCase(s)) {
				// 327 同樣的名稱已經存在。
				pc.sendPackets(new S_ServerMessage(327));
				return;
			}
		}

		final L1BookMark book = new L1BookMark();
		book.setId(IdFactory.get().nextId());
		book.setCharId(pc.getId());
		book.setName(s);
		book.setLocX(pc.getX());
		book.setLocY(pc.getY());
		book.setMapId(pc.getMapId());

		Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO `character_teleport` SET `id`=?,`char_id`=?,`name`=?,`locx`=?,`locy`=?,`mapid`=?");
			pstm.setInt(1, book.getId());
			pstm.setInt(2, book.getCharId());
			pstm.setString(3, book.getName());
			pstm.setInt(4, book.getLocX());
			pstm.setInt(5, book.getLocY());
			pstm.setInt(6, book.getMapId());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		bookmarks.add(book);
		pc.sendPackets(new S_Bookmarks(s, book.getMapId(), book.getLocX(), book.getLocY(), book.getId()));
	}
}
