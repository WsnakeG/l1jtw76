package com.lineage.test;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BossSpawnTimeChecker {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost/l1jtw76?useUnicode=true&characterEncoding=utf8";
        String user = "root";       // ⬅️ 改成你自己的
        String password = "root";   // ⬅️ 改成你自己的

        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT location, next_spawn_time, count FROM spawnlist_boss");
                ResultSet rs = stmt.executeQuery()) {

               System.out.println("=== Boss 重生狀態列表 ===");

               while (rs.next()) {
                   int count = rs.getInt("count");
                   if (count <= 0) continue; // 忽略沒有出現的 BOSS

                   String name = rs.getString("location");
                   Timestamp ts = rs.getTimestamp("next_spawn_time");

                   if (ts == null) {
                       System.out.println("✅ " + name + "：已重生中");
                   } else {
                       Calendar cal = Calendar.getInstance();
                       cal.setTimeInMillis(ts.getTime());
                       long now = System.currentTimeMillis();
                       long nextTime = cal.getTimeInMillis();

                       String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());

                       if (nextTime > now) {
                           System.out.println("🕒 " + name + "：預計重生時間：" + timeStr);
                       } else {
                           // 時間已過，視為已重生（spawn 尚未觸發不影響）
                           System.out.println("✅ " + name + "：已重生");
                       }
                   }
               }

               System.out.println("=== 結束 ===");

           } catch (Exception e) {
               System.out.println("❌ 查詢失敗：" + e.getMessage());
               e.printStackTrace();
           }
    }
}
