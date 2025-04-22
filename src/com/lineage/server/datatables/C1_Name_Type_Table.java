package com.lineage.server.datatables;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.c1.C1Executor;
import com.lineage.server.templates.L1Name_Power;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 陣營階級能力記錄 2014/08/08 新增欄位int6(延伸製作新功能)
 * 
 * @author daien
 */
public class C1_Name_Type_Table {

	private static final Log _log = LogFactory.getLog(C1_Name_Type_Table.class);

	private static final Map<Integer, HashMap<Integer, L1Name_Power>> _types = new HashMap<Integer, HashMap<Integer, L1Name_Power>>();

	// 陣營 / 階級代號 / 需要積分
	private static final Map<Integer, HashMap<Integer, Integer>> _typesLv = new HashMap<Integer, HashMap<Integer, Integer>>();

	// 陣營 / 階級代號 / 減少積分
	private static final Map<Integer, HashMap<Integer, Integer>> _typesLv_down = new HashMap<Integer, HashMap<Integer, Integer>>();

	private static C1_Name_Type_Table _instance;

	public static C1_Name_Type_Table get() {
		if (_instance == null) {
			_instance = new C1_Name_Type_Table();
		}
		return _instance;
	}

	/**
	 * 初始化載入
	 */
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int i = 0;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_c1_name_type`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int c1_id = rs.getInt("c1_id");
				final int c1_type = rs.getInt("c1_type");
				// final String c1_name = rs.getString("c1_name");
				final String c1_name_type = rs.getString("c1_name_type");
				final String c1_classname = rs.getString("c1_classname");
				final int set = rs.getInt("set");
				final int down = rs.getInt("down");
				final int int1 = rs.getInt("int1");
				final int int2 = rs.getInt("int2");
				final int int3 = rs.getInt("int3");
				final int int4 = rs.getInt("int4");
				final int int5 = rs.getInt("int5");
				final int int6 = rs.getInt("int6");
				final int int7 = rs.getInt("int7");
				final int int8 = rs.getInt("int8");
				final int int9 = rs.getInt("int9");
				final int int10 = rs.getInt("int10");
				final int int11 = rs.getInt("int11");
				final int int12 = rs.getInt("int12");
				final int int13 = rs.getInt("int13");
				final int int14 = rs.getInt("int14");

				final L1Name_Power power = new L1Name_Power();
				power.set_c1_id(c1_id);
				power.set_c1_name_type(c1_name_type);
				final C1Executor classname = power(c1_classname, int1, int2, int3, int4, int5, int6, int7,
						int8, int9, int10, int11, int12, int13, int14);
				if (classname == null) {
					continue;
				}
				power.set_c1_classname(classname);
				power.set_set(set);
				power.set_down(down);
				// 禮物寶箱 by terry0412
				power.set_gift_box(rs.getInt("gift_box"));

				// 陣營階級能力資料
				HashMap<Integer, L1Name_Power> types = _types.get(c1_type);
				if (types == null) {
					types = new HashMap<Integer, L1Name_Power>();
				}
				types.put(c1_id, power);

				// 階級需要積分
				HashMap<Integer, Integer> typesLv = _typesLv.get(c1_type);
				if (typesLv == null) {
					typesLv = new HashMap<Integer, Integer>();
				}
				typesLv.put(c1_id, set);

				// 階級死亡減少積分
				HashMap<Integer, Integer> typesLv_down = _typesLv_down.get(c1_type);
				if (typesLv_down == null) {
					typesLv_down = new HashMap<Integer, Integer>();
				}
				typesLv_down.put(c1_id, down);

				_types.put(c1_type, types);
				_typesLv.put(c1_type, typesLv);
				i += 1;
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入陣營階級能力記錄數量: " + i + "(" + timer.get() + "ms)");
	}

	/**
	 * 陣營/等級 相應的能力設置
	 * 
	 * @param key1 陣營
	 * @param key2 等級
	 * @return
	 */
	public L1Name_Power get(final int key1, final int key2) {
		final HashMap<Integer, L1Name_Power> powers = _types.get(key1);
		if (powers != null) {
			return powers.get(key2);
		}
		return null;
	}

	/**
	 * 陣營相應的能力設置群
	 * 
	 * @param key1 陣營
	 * @return
	 */
	public HashMap<Integer, L1Name_Power> get(final int key1) {
		final HashMap<Integer, L1Name_Power> powers = _types.get(key1);
		if (powers != null) {
			return powers;
		}
		return null;
	}

	/**
	 * 陣營相應的等級
	 * 
	 * @param score 積分
	 * @return
	 */
	public int getLv(final int key1, final int score) {
		final HashMap<Integer, Integer> powers = _typesLv.get(key1);
		if (powers == null) {
			return 0;
		}
		for (int i = powers.size(); i > 0; i--) {
			final Integer ps = powers.get(i);
			if (score >= ps) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 加入CLASS清單
	 * 
	 * @param className
	 * @param int1
	 * @param int2
	 * @param int3
	 * @param int4
	 * @return
	 */
	private C1Executor power(final String className, final int int1, final int int2, final int int3,
			final int int4, final int int5, final int int6, final int int7, final int int8, final int int9,
			final int int10, final int int11, final int int12, final int int13, final int int14) {
		if (className.equals("0")) {
			return null;
		}
		try {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("com.lineage.server.model.c1.");
			stringBuilder.append(className);

			final Class<?> cls = Class.forName(stringBuilder.toString());
			final C1Executor exe = (C1Executor) cls.getMethod("get").invoke(null);
			exe.set_power(int1, int2, int3, int4, int5, int6, int7, int8, int9, int10, int11, int12, int13,
					int14);

			return exe;

		} catch (final ClassNotFoundException e) {
			final String error = "發生[陣營階級能力檔案]錯誤, 檢查檔案是否存在:" + className;
			_log.error(error);

		} catch (final IllegalArgumentException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final IllegalAccessException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final InvocationTargetException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final SecurityException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final NoSuchMethodException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
