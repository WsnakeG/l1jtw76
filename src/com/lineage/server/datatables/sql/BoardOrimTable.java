package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.DwarfReading;
import com.lineage.server.datatables.storage.BoardOrimStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Rank;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * @author terry0412
 */
public class BoardOrimTable implements BoardOrimStorage {

	private static final Log _log = LogFactory.getLog(BoardOrimTable.class);

	private static final List<L1Rank> _totalList = new CopyOnWriteArrayList<L1Rank>();

	private static int _maxid = 0;

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		final Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.DAY_OF_MONTH) == 28) {
			final Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
			final List<L1Rank> _checkList = new CopyOnWriteArrayList<L1Rank>();
			try {
				co = DatabaseFactory.get().getConnection();
				ps = co.prepareStatement(
						"SELECT * FROM `server_board_orim` WHERE `time_order`<=? ORDER BY `score` DESC");
				ps.setTimestamp(1, timestamp);
				rs = ps.executeQuery();
				while (rs.next()) {
					final String[] partyMember = rs.getString("partyMember").split(",");
					// List<String>
					final List<String> checkList = new CopyOnWriteArrayList<String>();
					for (final String str : partyMember) {
						checkList.add(str);
					}
					final String leader = rs.getString("leader");
					final int score = rs.getInt("score");
					final L1Rank rank = new L1Rank(leader, checkList, score);

					_checkList.add(rank);
				}
				final List<String> _overList = new CopyOnWriteArrayList<String>();
				for (int i = 0, r = 5, n = _checkList.size(); (i < r) && (i < n); i++) {
					int itemCount = 0;
					if (i == 0) {
						itemCount = 3;
					} else if ((i == 1) || (i == 2)) {
						itemCount = 2;
					} else if ((i == 3) || (i == 4)) {
						itemCount = 1;
					}
					final L1Rank rank = _checkList.get(i);
					for (int j = 0, k = rank.getMemberSize(); j < k; j++) {
						String value = null;
						if (!_overList.contains(rank.getPartyLeader())) {
							value = rank.getPartyLeader();
						} else {
							for (final String memberName : rank.getPartyMember()) {
								if (!_overList.contains(memberName)) {
									value = memberName;
								}
							}
						}
						if ((value != null) && (CharObjidTable.get().charObjid(value) > 0)) {
							try {
								// 魔族武器保護卷軸 56256
								final L1ItemInstance item = ItemTable.get().createItem(56256);
								item.setCount(itemCount);
								DwarfReading.get().insertItem(CharacterTable.getAccountName(value), item);
							} catch (final Exception e) {
								_log.error(e.getLocalizedMessage(), e);
							}
							_overList.add(value);
						}
					}
				}
				_checkList.clear();
				_overList.clear();
				try {
					if (null != ps) {
						ps = co.prepareStatement("DELETE FROM `server_board_orim` WHERE `time_order`<=?");
					}
					ps.setTimestamp(1, timestamp);
					ps.execute();
				} catch (final SQLException e) {
					_log.error(e.getLocalizedMessage(), e);
				} finally {
					SQLUtil.close(ps);
					SQLUtil.close(co);
				}
			} catch (final SQLException e) {
				// exception handling
			} finally {
			}
		}
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("SELECT * FROM `server_board_orim` ORDER BY `score` DESC");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int id = rs.getInt("id");
				if (id > _maxid) {
					_maxid = id;
				}
				final String[] partyMember = rs.getString("partyMember").split(",");
				// List<String>
				final List<String> checkList = new CopyOnWriteArrayList<String>();
				for (final String str : partyMember) {
					checkList.add(str);
				}
				String leader = rs.getString("leader");
				final int score = rs.getInt("score");
				for (final L1Rank rank : _totalList) {
					if (leader.equals(rank.getPartyLeader()) || rank.getPartyMember().contains(leader)) {
						leader = null;
						break;
					}
				}
				for (final String check_member : checkList) {
					for (final L1Rank rank : _totalList) {
						if (check_member.equals(rank.getPartyLeader())
								|| rank.getPartyMember().contains(check_member)) {
							checkList.remove(check_member);
							break;
						}
					}
				}
				final L1Rank rank = new L1Rank(leader, checkList, score);

				_totalList.add(rank);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
		_log.info("載入歐林佈告欄資料數量: " + _totalList.size() + "(" + timer.get() + "ms)");
	}

	@Override
	public List<L1Rank> getTotalList() {
		return _totalList;
	}

	@Override
	public int writeTopic(final int score, String leader, final List<String> partyMember) {
		final int length = partyMember.size();
		if (length <= 0) {
			return 0;
		}
		final StringBuilder sbr = new StringBuilder();
		for (int i = 0;;) {
			sbr.append(partyMember.get(i));
			if (++i < length) {
				sbr.append(",");
			} else {
				break;
			}
		}
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement(
					"INSERT INTO `server_board_orim` SET `id`=?,`score`=?,`leader`=?,`partyMember`=?,`time_order`=?");
			ps.setInt(1, ++_maxid);
			ps.setInt(2, score);
			ps.setString(3, leader);
			ps.setString(4, sbr.toString());
			ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			ps.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
		for (final L1Rank rank : _totalList) {
			if (leader.equals(rank.getPartyLeader()) || rank.getPartyMember().contains(leader)) {
				leader = null;
				break;
			}
		}
		for (final String check_member : partyMember) {
			for (final L1Rank rank : _totalList) {
				if (check_member.equals(rank.getPartyLeader())
						|| rank.getPartyMember().contains(check_member)) {
					partyMember.remove(check_member);
					break;
				}
			}
		}
		final L1Rank rank = new L1Rank(leader, partyMember, score);
		final int size = _totalList.size();

		int index = size;
		// 搜索資料
		for (int i = 0; i < size; i++) {
			if (score > _totalList.get(i).getScore()) {
				index = i;
				break;
			}
		}
		_totalList.add(index, rank);
		return index + 1;
	}

	@Override
	public void renewPcName(final String oriName, final String newName) {
		updateLeaderName(oriName, newName);
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("SELECT * FROM `server_board_orim`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final String partyMember = rs.getString("partyMember");
				boolean checkOkay = false;
				for (final String str : partyMember.split(",")) {
					if (oriName.equals(str)) {
						checkOkay = true;
						break;
					}
				}
				if (checkOkay) {
					updateMemberName(partyMember, partyMember.replace(oriName, newName));
				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
		for (final L1Rank rank : _totalList) {
			if (oriName.equals(rank.getPartyLeader())) {
				rank.setPartyLeader(newName);
			} else {
				final int index = rank.getPartyMember().indexOf(oriName);
				if (index >= 0) {
					rank.getPartyMember().set(index, newName);
				}
			}
		}
	}

	private final void updateLeaderName(final String oriName, final String newName) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("UPDATE `server_board_orim` SET `leader`=? WHERE `leader`=?");
			ps.setString(1, newName);
			ps.setString(2, oriName);
			ps.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}

	private final void updateMemberName(final String oriName, final String newName) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("UPDATE `server_board_orim` SET `partyMember`=? WHERE `partyMember`=?");
			ps.setString(1, newName);
			ps.setString(2, oriName);
			ps.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}
}
