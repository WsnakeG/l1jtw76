package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Blend;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 物品融合系統(DB自製)
 * 
 * @author terry0412
 */
public final class BlendTable {

	private static final Log _log = LogFactory.getLog(BlendTable.class);

	private static BlendTable _instance;

	private final HashMap<Integer, L1Blend> _itemIdIndex = new HashMap<Integer, L1Blend>();

	public static BlendTable getInstance() {
		if (_instance == null) {
			_instance = new BlendTable();
		}
		return _instance;
	}

	public final void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM extra_itemblend");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int item_id = rs.getInt("item_id");// 融合道具
				final int checkLevel = rs.getInt("checkLevel");// 判斷等級
				final int checkClass = rs.getInt("checkClass");// 判斷職業
				final int rnd = rs.getInt("rnd");// DB設定合成機率
				final int checkItem = rs.getInt("checkItem");// 身上要有何種道具才可執行
				final int hpConsume = rs.getInt("hpConsume");// 判斷所需血量
				final int mpConsume = rs.getInt("mpConsume");// 判斷所需魔力
				final int material = rs.getInt("material");// 判斷合成媒介
				final int material_count = rs.getInt("material_count");// 判斷合成媒介
																		// 數量
				final int material_2 = rs.getInt("material_2");// 判斷合成媒介2
				final int material_2_count = rs.getInt("material_2_count");// 判斷合成媒介2
																			// 數量
				final int material_3 = rs.getInt("material_3");// 判斷合成媒介3
				final int material_3_count = rs.getInt("material_3_count");// 判斷合成媒介3
																			// 數量
				final int material_4 = rs.getInt("material_4");// 判斷合成媒介3
				final int material_4_count = rs.getInt("material_4_count");// 判斷合成媒介4
																			// 數量
				final int material_5 = rs.getInt("material_5");// 判斷合成媒介3
				final int material_5_count = rs.getInt("material_5_count");// 判斷合成媒介5
																			// 數量
				final int new_item = rs.getInt("new_item");// 合成後的新道具
				final int new_item_counts = rs.getInt("new_item_counts");// 新道具的數量
				final int new_Enchantlvl_SW = rs.getInt("new_Enchantlvl_SW");// 新道具(武器或防具)強化值是否隨機的開關
				// 隨機:1
				// 固定:0
				final int new_item_Enchantlvl = rs.getInt("new_item_Enchantlvl");// 新道具的強化值
				final int removeItem = rs.getInt("removeItem");// 是否刪除融合道具
				final String message = rs.getString("message");// 設定顯示對話
				final int item_Html = rs.getInt("item_Html");// 設定顯示融合所須物品清單

				final L1Blend Item_Blend = new L1Blend(item_id, checkLevel, checkClass, rnd, checkItem,
						hpConsume, mpConsume, material, material_count, material_2, material_2_count,
						material_3, material_3_count, material_4, material_4_count, material_5,
						material_5_count, new_item, new_item_counts, new_Enchantlvl_SW, new_item_Enchantlvl,
						removeItem, message, item_Html);
				_itemIdIndex.put(item_id, Item_Blend);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
		_log.info("載入物品融合資料數量: " + _itemIdIndex.size() + "(" + timer.get() + "ms)");
	}

	public final L1Blend getTemplate(final int itemId) {
		return _itemIdIndex.get(itemId);
	}
}
