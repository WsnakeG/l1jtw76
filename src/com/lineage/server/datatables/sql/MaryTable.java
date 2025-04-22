package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.npc.gam.Npc_Mary;
import com.lineage.server.datatables.storage.MaryStorage;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 小瑪莉資料紀錄
 * 
 * @author dexc
 */
public class MaryTable implements MaryStorage {

	private static final Log _log = LogFactory.getLog(MaryTable.class);

	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `server_mary`");
			rs = ps.executeQuery();

			while (rs.next()) {
				// int key = rs.getInt("id");
				final long all_stake = rs.getLong("all_stake"); // 累積賭注
				Npc_Mary.set_all_stake(all_stake);

				final long all_user_prize = rs.getLong("all_user_prize"); // 累積中獎金額
				Npc_Mary.set_all_user_prize(all_user_prize);

				final int out_prize = rs.getInt("out_prize"); // 輸出獎金百分比
				Npc_Mary.set_out_prize(out_prize);

				final int item_id = rs.getInt("item_id"); // 需要下注的物品編號
				Npc_Mary.set_itemid(item_id);

				final int count = rs.getInt("count"); // 已使用次數
				Npc_Mary.set_count(count);

				final int x_a1 = rs.getInt("x_a1"); // 大BAR
				Npc_Mary.set_x_a1(x_a1);

				final int x_a2 = rs.getInt("x_a2"); // 小BAR
				Npc_Mary.set_x_a2(x_a2);

				final int x_b1 = rs.getInt("x_b1"); // 大半瓜
				Npc_Mary.set_x_b1(x_b1);

				final int x_b2 = rs.getInt("x_b2"); // 小半瓜
				Npc_Mary.set_x_b2(x_b2);

				final int x_c1 = rs.getInt("x_c1"); // 大蘋果
				Npc_Mary.set_x_c1(x_c1);

				final int x_c2 = rs.getInt("x_c2"); // 小蘋果
				Npc_Mary.set_x_c2(x_c2);

				final int x_d1 = rs.getInt("x_d1"); // 大西瓜
				Npc_Mary.set_x_d1(x_d1);

				final int x_d2 = rs.getInt("x_d2"); // 小西瓜
				Npc_Mary.set_x_d2(x_d2);

				final int x_e1 = rs.getInt("x_e1"); // 大香蕉
				Npc_Mary.set_x_e1(x_e1);

				final int x_e2 = rs.getInt("x_e2"); // 小香蕉
				Npc_Mary.set_x_e2(x_e2);

				final int x_f1 = rs.getInt("x_f1"); // 大檸檬
				Npc_Mary.set_x_f1(x_f1);

				final int x_f2 = rs.getInt("x_f2"); // 小檸檬
				Npc_Mary.set_x_f2(x_f2);

				final int x_g1 = rs.getInt("x_g1"); // 大橘子
				Npc_Mary.set_x_g1(x_g1);

				final int x_g2 = rs.getInt("x_g2"); // 小橘子
				Npc_Mary.set_x_g2(x_g2);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入小瑪莉設置資料 (" + timer.get() + "ms)");
	}

	@Override
	public void update(final long all_stake, final long all_user_prize, final int count) {
		Connection co = null;
		PreparedStatement pm = null;
		try {
			co = DatabaseFactory.get().getConnection();
			pm = co.prepareStatement(
					"UPDATE `server_mary` SET `all_stake`=?,`all_user_prize`=?,`count`=? WHERE `id`=1");
			pm.setLong(1, all_stake);
			pm.setLong(2, all_user_prize);
			pm.setInt(3, count);
			pm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pm);
			SQLUtil.close(co);
		}
	}
}
