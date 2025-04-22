package com.lineage.server.datatables;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Inn;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;

//Npc_BosskeyNpc_Bosskey

public class InnTable
{
  private static Logger _log = Logger.getLogger(InnTable.class.getName());

  private static final Map<Integer, Inn> _dataMap = Maps.newHashMap();
  private static InnTable _instance;

  public static InnTable getInstance()
  {
    if (_instance == null) {
      _instance = new InnTable();
    }
    return _instance;
  }

  private InnTable() {
    load();
  }

  private void load() {
    Connection con = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    Inn inn = null;
    try {
      con = DatabaseFactory.get().getConnection();
      pstm = con.prepareStatement("SELECT * FROM inn");

      rs = pstm.executeQuery();

      while (rs.next()) {
        int key = rs.getInt("npcid");
        if (!_dataMap.containsKey(Integer.valueOf(key))) {
          inn = new Inn();
          _dataMap.put(Integer.valueOf(key), inn);
        } else {
          inn = (Inn)_dataMap.get(Integer.valueOf(key));
        }

        L1Inn l1inn = new L1Inn();
        l1inn.setInnNpcId(rs.getInt("npcid"));
        int roomNumber = rs.getInt("room_number");
        l1inn.setRoomNumber(roomNumber);
        l1inn.setKeyId(rs.getInt("key_id"));
        l1inn.setLodgerId(rs.getInt("lodger_id"));
        l1inn.setHall(rs.getBoolean("hall"));
        l1inn.setDueTime(rs.getTimestamp("due_time"));

        inn._inn.put(Integer.valueOf(roomNumber), l1inn);
      }
    } catch (SQLException e) {
      _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    } finally {
      SQLUtil.close(rs);
      SQLUtil.close(pstm);
      SQLUtil.close(con);
    }
  }

  /**
   * 更新此房間資料數據
   * @param inn
   */
  public void updateInn(L1Inn inn)
  {
    Connection con = null;
    PreparedStatement pstm = null;
    try {
      con = DatabaseFactory.get().getConnection();
      pstm = con.prepareStatement("UPDATE inn SET key_id=?,lodger_id=?,hall=?,due_time=? WHERE npcid=? and room_number=?");

      pstm.setInt(1, inn.getKeyId());
      pstm.setInt(2, inn.getLodgerId());
      pstm.setBoolean(3, inn.isHall());
      pstm.setTimestamp(4, inn.getDueTime());
      pstm.setInt(5, inn.getInnNpcId());
      pstm.setInt(6, inn.getRoomNumber());
      pstm.execute();
    } catch (Exception e) {
      _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    } finally {
      SQLUtil.close(pstm);
      SQLUtil.close(con);
    }
  }
  

  public L1Inn getTemplate(int npcid, int roomNumber) {
    if (_dataMap.containsKey(Integer.valueOf(npcid))) {
      return (L1Inn)((Inn)_dataMap.get(Integer.valueOf(npcid)))._inn.get(Integer.valueOf(roomNumber));
    }
    return null;
  }

  private static class Inn
  {
    private final Map<Integer, L1Inn> _inn = Maps.newHashMap();
  }
}