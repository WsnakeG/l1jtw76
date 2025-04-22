/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package com.add.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

public class L1FireSmithCrystalTable {
	
	private static final Log _log = LogFactory.getLog(L1FireSmithCrystalTable.class);

	private static L1FireSmithCrystalTable _instance;

	private final Map<Integer, L1FireCrystal> _FireCrystalIndex = new HashMap<Integer, L1FireCrystal>();

	public static L1FireSmithCrystalTable get() {
		if (_instance == null) {
			_instance = new L1FireSmithCrystalTable();
		}
		return _instance;
	}

	public void load() {
		PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM 火神熔煉系統");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int itemid = rs.getInt("itemid");			// 道具編號
				int enchant_lv0 = rs.getInt("enchant_lv0");
				int enchant_lv1 = rs.getInt("enchant_lv1");
				int enchant_lv2 = rs.getInt("enchant_lv2");
				int enchant_lv3 = rs.getInt("enchant_lv3");				
				int enchant_lv4 = rs.getInt("enchant_lv4");	
				int enchant_lv5 = rs.getInt("enchant_lv5");	
				int enchant_lv6 = rs.getInt("enchant_lv6");	
				int enchant_lv7 = rs.getInt("enchant_lv7");	
				int enchant_lv8 = rs.getInt("enchant_lv8");	
				int enchant_lv9 = rs.getInt("enchant_lv9");	
				int enchant_lv10 = rs.getInt("enchant_lv10");	
				int enchant_lv11 = rs.getInt("enchant_lv11");	
				int enchant_lv12 = rs.getInt("enchant_lv12");	
				int enchant_lv13 = rs.getInt("enchant_lv13");
				int enchant_lv14 = rs.getInt("enchant_lv14");
				
				L1FireCrystal fireCrystal = new L1FireCrystal(itemid, 
						enchant_lv0, enchant_lv1, enchant_lv2 ,enchant_lv3,
						enchant_lv4, enchant_lv5,enchant_lv6, enchant_lv7,
						enchant_lv8, enchant_lv9, enchant_lv10, enchant_lv11, 
						enchant_lv12, enchant_lv13, enchant_lv14);				
				
				_FireCrystalIndex.put(itemid, fireCrystal);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入火神融煉道具資料數量: " + _FireCrystalIndex.size() + "(" + timer.get() + "ms)");
	}

	public L1FireCrystal getTemplate(int itemid) {
		return _FireCrystalIndex.get(itemid);
	}

}