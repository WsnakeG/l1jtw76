
package com.lineage.data.item_etcitem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

public class BossTime extends ItemExecutor {

    private static BossTime _instance;

    public static BossTime get() {
        if (_instance == null) {
            _instance = new BossTime();
        }
        return _instance;
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        final String[][] pages = new String[15][30];
        final int[] counter = new int[15];

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/l1jtw76?useUnicode=true&characterEncoding=utf8",
                "root", "root");

            stmt = conn.prepareStatement("SELECT location, next_spawn_time, count, mapid FROM spawnlist_boss");
            rs = stmt.executeQuery();

            while (rs.next()) {
                int mapId = rs.getInt("mapid");
                int area = classifyMapId(mapId);
                if (area < 0 || area >= 15) continue;

                int count = rs.getInt("count");
                if (count <= 0) continue;

                int idx = counter[area];
                if (idx >= 30) continue;

                String name = rs.getString("location");
                Timestamp ts = rs.getTimestamp("next_spawn_time");

                String info;
                if (ts == null || ts.getTime() < System.currentTimeMillis()) {
                    info = "【已重生】" + name;
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(ts.getTime());
                    String time = new SimpleDateFormat("MM/dd HH:mm").format(cal.getTime());
                    info = "【★下次重生: " + time + "】" + name;
                }

                pages[area][idx] = info;
                counter[area]++;
            }

        } catch (Exception e) {
            pages[0][0] = "查詢錯誤：" + e.getMessage();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        // 傳送所有頁面資料（靜態）
        for (int i = 0; i < 15; i++) {
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bosstime_" + i, pages[i]));
        }

        // 顯示主頁面
        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bosstime", new String[30]));
    }

    private int classifyMapId(int mapId) {
        if (mapId == 0) return 0;
        if (mapId == 4) return 1;
        if (mapId >= 7 && mapId <= 13) return 2;
        if (mapId >= 59 && mapId <= 63) return 3;
        if (mapId == 70) return 4;
        if (mapId >= 72 && mapId <= 74) return 5;
        if (mapId == 303) return 6;
        if (mapId == 430) return 7;
        if (mapId >= 452 && mapId <= 537) return 8;
        if (mapId == 558) return 9;
        if (mapId == 782) return 10;
        if (mapId >= 1700 && mapId <= 1703) return 11;
        if (mapId >= 3301 && mapId <= 3310) return 12;
        if (mapId == 37 || mapId == 65 || mapId == 67) return 13;
        return 14;
    }
}
