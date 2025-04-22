package com.lineage.server.timecontroller.event.ranking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.SQLUtil;

/**
 * 英雄風雲榜時間軸
 * 
 * @author dexc
 */
public class RankingHeroTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(RankingHeroTimer.class);

	private ScheduledFuture<?> _timer;

	private static boolean _load;

	private static String[] _userNameAll;

	private static String[] _userNameC;

	private static String[] _userNameK;

	private static String[] _userNameE;

	private static String[] _userNameW;

	private static String[] _userNameD;

	private static String[] _userNameG;

	private static String[] _userNameI;

	public void start() {
		restart();
		final int timeMillis = 600 * 1000; // 10分鐘
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	/**
	 * 全職業風雲榜
	 * 
	 * @return
	 */
	public static String[] userNameAll() {
		if (!_load) {
			load();
		}
		return _userNameAll;
	}

	/**
	 * 王族風雲榜
	 * 
	 * @return
	 */
	public static String[] userNameC() {
		if (!_load) {
			load();
		}
		return _userNameC;
	}

	/**
	 * 騎士風雲榜
	 * 
	 * @return
	 */
	public static String[] userNameK() {
		if (!_load) {
			load();
		}
		return _userNameK;
	}

	/**
	 * 精靈風雲榜
	 * 
	 * @return
	 */
	public static String[] userNameE() {
		if (!_load) {
			load();
		}
		return _userNameE;
	}

	/**
	 * 法師風雲榜
	 * 
	 * @return
	 */
	public static String[] userNameW() {
		if (!_load) {
			load();
		}
		return _userNameW;
	}

	/**
	 * 黑妖風雲榜
	 * 
	 * @return
	 */
	public static String[] userNameD() {
		if (!_load) {
			load();
		}
		return _userNameD;
	}

	/**
	 * 龍騎風雲榜
	 * 
	 * @return
	 */
	public static String[] userNameG() {
		if (!_load) {
			load();
		}
		return _userNameG;
	}

	/**
	 * 幻術風雲榜
	 * 
	 * @return
	 */
	public static String[] userNameI() {
		if (!_load) {
			load();
		}
		return _userNameI;
	}

	@Override
	public void run() {
		try {
			load();

		} catch (final Exception e) {
			_log.error("英雄風雲榜時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final RankingHeroTimer heroTimer = new RankingHeroTimer();
			heroTimer.start();
		}
	}

	private static void load() {
		try {
			_load = true;
			// 重置所有排行
			restart();

			// 全數玩家加入排行 (包含離線玩家, 不含GM角色)
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = DatabaseFactory.get().getConnection();
				// 經驗排行榜
				pstm = con.prepareStatement(
						"SELECT * FROM `characters` WHERE `level` > 0 and `AccessLevel` <= 0 ORDER BY `MeteLevel` DESC,`Exp` DESC");
				rs = pstm.executeQuery();

				while (rs.next()) {
					// 0:王族 1:騎士 2:精靈 3:法師 4:黑妖 5:龍騎 6:幻術
					final String char_name = rs.getString("char_name");
					// final int level = rs.getInt("level");
					final int type = rs.getInt("Type");
					if (type == 0) {
						for (int i = 0, n = 10; i < n; i++) {
							if (_userNameC[i].equals(" ")) {
								final StringBuffer sbr = new StringBuffer().append(char_name);
								// 排行榜是否顯示等級資訊 by terry0412
								/*
								 * if (ConfigAlt.RANK_INFO) { sbr.append(" (Lv."
								 * ) .append(level).append(")"); }
								 */
								_userNameC[i] = sbr.toString();
								break;
							}
						}
					} else if (type == 1) {
						for (int i = 0, n = 10; i < n; i++) {
							if (_userNameK[i].equals(" ")) {
								final StringBuffer sbr = new StringBuffer().append(char_name);
								// 排行榜是否顯示等級資訊 by terry0412
								/*
								 * if (ConfigAlt.RANK_INFO) { sbr.append(" (Lv."
								 * ) .append(level).append(")"); }
								 */
								_userNameK[i] = sbr.toString();
								break;
							}
						}
					} else if (type == 2) {
						for (int i = 0, n = 10; i < n; i++) {
							if (_userNameE[i].equals(" ")) {
								final StringBuffer sbr = new StringBuffer().append(char_name);
								// 排行榜是否顯示等級資訊 by terry0412
								/*
								 * if (ConfigAlt.RANK_INFO) { sbr.append(" (Lv."
								 * ) .append(level).append(")"); }
								 */
								_userNameE[i] = sbr.toString();
								break;
							}
						}
					} else if (type == 3) {
						for (int i = 0, n = 10; i < n; i++) {
							if (_userNameW[i].equals(" ")) {
								final StringBuffer sbr = new StringBuffer().append(char_name);
								// 排行榜是否顯示等級資訊 by terry0412
								/*
								 * if (ConfigAlt.RANK_INFO) { sbr.append(" (Lv."
								 * ) .append(level).append(")"); }
								 */
								_userNameW[i] = sbr.toString();
								break;
							}
						}
					} else if (type == 4) {
						for (int i = 0, n = 10; i < n; i++) {
							if (_userNameD[i].equals(" ")) {
								final StringBuffer sbr = new StringBuffer().append(char_name);
								// 排行榜是否顯示等級資訊 by terry0412
								/*
								 * if (ConfigAlt.RANK_INFO) { sbr.append(" (Lv."
								 * ) .append(level).append(")"); }
								 */
								_userNameD[i] = sbr.toString();
								break;
							}
						}
					} else if (type == 5) {
						for (int i = 0, n = 10; i < n; i++) {
							if (_userNameG[i].equals(" ")) {
								final StringBuffer sbr = new StringBuffer().append(char_name);
								// 排行榜是否顯示等級資訊 by terry0412
								/*
								 * if (ConfigAlt.RANK_INFO) { sbr.append(" (Lv."
								 * ) .append(level).append(")"); }
								 */
								_userNameG[i] = sbr.toString();
								break;
							}
						}
					} else if (type == 6) {
						for (int i = 0, n = 10; i < n; i++) {
							if (_userNameI[i].equals(" ")) {
								final StringBuffer sbr = new StringBuffer().append(char_name);
								// 排行榜是否顯示等級資訊 by terry0412
								/*
								 * if (ConfigAlt.RANK_INFO) { sbr.append(" (Lv."
								 * ) .append(level).append(")"); }
								 */
								_userNameI[i] = sbr.toString();
								break;
							}
						}
					}
					// 全職業排行
					for (int i = 0, n = 10; i < n; i++) {
						if (_userNameAll[i].equals(" ")) {
							final StringBuffer sbr = new StringBuffer().append(char_name);
							// 排行榜是否顯示等級資訊 by terry0412
							/*
							 * if (ConfigAlt.RANK_INFO) { sbr.append(" (Lv."
							 * ).append(level).append(")"); }
							 */
							_userNameAll[i] = sbr.toString();
							break;
						}
					}
					Thread.sleep(1);
				}

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 重置所有排行
	 */
	private static void restart() {
		_userNameAll = new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", };

		_userNameC = new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", };

		_userNameK = new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", };

		_userNameE = new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", };

		_userNameW = new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", };

		_userNameD = new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", };

		_userNameG = new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", };

		_userNameI = new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", };
	}
}
