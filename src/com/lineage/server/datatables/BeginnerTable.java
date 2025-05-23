package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Beginner;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 新手物品資料
 * 
 * @author dexc
 */
public class BeginnerTable {

	public static final Log _log = LogFactory.getLog(BeginnerTable.class);

	private static final Map<String, ArrayList<L1Beginner>> _beginnerList = new HashMap<String, ArrayList<L1Beginner>>();

	private static BeginnerTable _instance;

	public static BeginnerTable get() {
		if (_instance == null) {
			_instance = new BeginnerTable();
		}
		return _instance;
	}

	private BeginnerTable() {
	}
	
	public void restBeginnerTable() {
		_beginnerList.clear();
		load();
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		int i = 0;
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `beginner`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final String activate = rs.getString("activate").toUpperCase();
				final int itemid = rs.getInt("item_id");
				final int count = rs.getInt("count");
				final int enchantlvl = rs.getInt("enchantlvl");
				final int charge_count = rs.getInt("charge_count");
				final int time = rs.getInt("time");

				if (count > 0) {
					final L1Beginner beginner = new L1Beginner();
					beginner.set_activate(activate);
					beginner.set_itemid(itemid);
					beginner.set_count(count);
					beginner.set_enchantlvl(enchantlvl);
					beginner.set_charge_count(charge_count);
					beginner.set_time(time);

					add(beginner);
					i++;
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入新手物品資料數量: " + _beginnerList.size() + "/" + i + "(" + timer.get() + "ms)");
	}

	private void add(final L1Beginner beginner) {
		final String key = beginner.get_activate();
		ArrayList<L1Beginner> list = _beginnerList.get(key);
		if (list == null) {
			list = new ArrayList<L1Beginner>();
			list.add(beginner);

		} else {
			list.add(beginner);
		}
		_beginnerList.put(key, list);
	}

	public void giveItem(final L1PcInstance pc) {
		String key = "A";
		if (pc.isCrown()) {// 王族
			key = "P";

		} else if (pc.isKnight()) {// 騎士
			key = "K";

		} else if (pc.isElf()) {// 精靈
			key = "E";

		} else if (pc.isWizard()) {// 法師
			key = "W";

		} else if (pc.isDarkelf()) {// 黑妖
			key = "D";

		} else if (pc.isDragonKnight()) {// 龍騎士
			key = "R";

		} else if (pc.isIllusionist()) {// 幻術師
			key = "I";

		} else if (pc.isWarrior()) {// 戰士
			key = "O";
		}

		final ArrayList<L1Beginner> list = _beginnerList.get(key);
		if (list != null) {
			if (!list.isEmpty()) {
				for (final L1Beginner beginner : list) {
					get_item(pc.getId(), beginner);
				}
			}
		}

		final ArrayList<L1Beginner> listAll = _beginnerList.get("A");
		if (listAll != null) {
			if (!listAll.isEmpty()) {
				for (final L1Beginner beginner : listAll) {
					get_item(pc.getId(), beginner);
				}
			}
		}
	}

	/**
	 * 給予物品
	 * 
	 * @param objid
	 * @param beginner
	 */
	private void get_item(final int objid, final L1Beginner beginner) {
		try {
			final L1ItemInstance item = ItemTable.get().createItem(beginner.get_itemid());
			if (item != null) {
				item.setCount(beginner.get_count());
				item.setEnchantLevel(beginner.get_enchantlvl());
				item.setChargeCount(beginner.get_charge_count());
				item.setBless(item.getBless());

				CharItemsReading.get().storeItem(objid, item);
				if (beginner.get_time() > 0) {
					final long time = System.currentTimeMillis();// 目前時間豪秒
					final long x1 = beginner.get_time() * 60 * 60;// 指定小時耗用秒數
					final long x2 = x1 * 1000;// 轉為豪秒
					final long upTime = x2 + time;// 目前時間 加上指定天數耗用秒數

					// 時間數據
					final Timestamp ts = new Timestamp(upTime);
					item.set_time(ts);

					// 人物背包物品使用期限資料
					CharItemsTimeReading.get().addTime(item.getId(), ts);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}