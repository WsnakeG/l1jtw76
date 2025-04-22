package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1ServerEtcItem;
import com.lineage.server.utils.SQLUtil;
import com.lineage.william.EtcItemForChar;

/**
 * 道具強化資料角色記錄 CharacterEtcItemTable <BR>
 * 
 * @author Roy
 */

public class CharacterEtcItemTable {

	private static final Log _log = LogFactory.getLog(CharacterEtcItemTable.class);

	private static CharacterEtcItemTable _instance;

	public static CharacterEtcItemTable get() {
		if (_instance == null) {
			_instance = new CharacterEtcItemTable();
		}
		return _instance;
	}

	public void Add(final L1PcInstance pc, final int itemid, final String itemname, final int forover) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO character_etcitems SET char_id=?,char_name=?,item_id=?,item_name=?,forover=?");
			i++;
			pstm.setInt(i, pc.getId());
			i++;
			pstm.setString(i, pc.getName());
			i++;
			pstm.setInt(i, itemid);
			i++;
			pstm.setString(i, itemname);
			i++;
			pstm.setInt(i, forover);
			pstm.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

//	public void login(final L1PcInstance pc) {
//		Connection con = null;
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		try {
//			con = DatabaseFactory.get().getConnection();
//			pstm = con.prepareStatement(
//					"SELECT * FROM character_etcitems where forover=1 and char_id=" + pc.getId());
//			rs = pstm.executeQuery();
//			while (rs.next()) {
//				final L1ServerEtcItem _etcitem = ServerEtcItemTable.get().getItem(rs.getInt("item_id"));
//				if (_etcitem.itemtime <= 0) {
//					EtcItemForChar.get(pc, _etcitem).loginEffect();
//				}
//			}
//		} catch (final SQLException e) {
//			_log.error(e.getLocalizedMessage(), e);
//		} finally {
//			SQLUtil.close(rs);
//			SQLUtil.close(pstm);
//			SQLUtil.close(con);
//		}
//	}
}
