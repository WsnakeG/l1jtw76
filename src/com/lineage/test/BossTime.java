package com.lineage.test;

/*package com.lineage.data.item_etcitem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<Integer, List<String>> areaInfo = new HashMap<>();
        for (int i = 0; i < 15; i++) {
            areaInfo.put(i, new ArrayList<>());
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/l1jtw76?useUnicode=true&characterEncoding=utf8", "root", "root");
             PreparedStatement stmt = conn.prepareStatement("SELECT location, next_spawn_time, count, mapid FROM spawnlist_boss");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int count = rs.getInt("count");
                if (count <= 0) continue;

                int mapId = rs.getInt("mapid");
                int area = classifyMapId(mapId);

                String name = rs.getString("location");
                Timestamp ts = rs.getTimestamp("next_spawn_time");
                String msg;
                if (ts == null || ts.getTime() < System.currentTimeMillis()) {
                    msg = "\u3010\u5df2\u91cd\u751f\u3011" + name;
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(ts.getTime());
                    String time = new SimpleDateFormat("MM/dd HH:mm").format(cal.getTime());
                    msg = "\u3010\u2605\u4e0b\u6b21\u91cd\u751f: " + time + "\u3011" + name;
                }

                areaInfo.get(area).add(msg);
            }
        } catch (Exception e) {
            areaInfo.get(14).add("\u932f\u8aa4: " + e.getMessage());
        }

        // 將各分類資料保存至 tempID
        for (int i = 0; i < 15; i++) {
            StringBuilder sb = new StringBuilder();
            for (String s : areaInfo.get(i)) {
                sb.append(s).append("<br>");
            }
            pc.setTempID(1000 + i, sb.toString());
        }

        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bosstime"));
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
*/

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
        int area = 0;
        int page = 0;
        if (data != null && data.length > 0) {
            area = data[0];
            if (data.length > 1) page = data[1];
        }

        final String[] info = new String[17];
        int num = 0;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int index = 0;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/l1jtw76?useUnicode=true&characterEncoding=utf8",
                "root", "root");

            stmt = conn.prepareStatement(
                "SELECT location, next_spawn_time, count, mapid FROM spawnlist_boss");
            rs = stmt.executeQuery();

            int skip = page * 15;
            
            while (rs.next()) {
                if (classifyMapId(rs.getInt("mapid")) != area) continue;
                if (index++ < skip) continue;
                if (num >= 15) break;
                int count = rs.getInt("count");
                if (count <= 0) continue;

                int mapId = rs.getInt("mapid");
                if (classifyMapId(mapId) != area) continue;

                String name = rs.getString("location");
                Timestamp ts = rs.getTimestamp("next_spawn_time");

                if (ts == null || ts.getTime() < System.currentTimeMillis()) {
                    info[num] = "【已重生】" + name;
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(ts.getTime());
                    String time = new SimpleDateFormat("MM/DD HH:mm").format(cal.getTime());
                    info[num] = "【★下次重生: " + time + "】" + name;
                }
                num++;
            }

        } catch (Exception e) {
            info[0] = "查詢失敗：" + e.getMessage();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        info[15] = (page > 0) ? "上一頁" : "";
        info[16] = (index > (page + 1) * 15) ? "下一頁" : "";

        String Subpage;
        if (data == null || data.length == 0) {
            Subpage = "bosstime";
        } else {
            Subpage = "bosstime_" + area;
        }
        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), Subpage, info));
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
