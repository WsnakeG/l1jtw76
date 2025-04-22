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

public class L1BlendTable {
	private static final Log _log = LogFactory.getLog(L1BlendTable.class);

	private static L1BlendTable _instance;

	private final Map<String, L1Blend> _CraftIndex = new HashMap<String, L1Blend>();
	
	private final Map<String, String> _CraftList = new HashMap<String, String>();

	public static L1BlendTable getInstance() {
		if (_instance == null) {
			_instance = new L1BlendTable();
		}
		return _instance;
	}

	
	public void loadBlendTable() {
		PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM 火神煉化系統");
			rs = pstm.executeQuery();
			fillBlendTable(rs);
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入道具製造資料數量: " + _CraftIndex.size() + "(" + timer.get() + "ms)");
	}

	private void fillBlendTable(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int npcid = rs.getInt("npcid");								// npc編號
			String action = rs.getString("action");						// 對應的命令
			String note = rs.getString("note");							// 道具名稱
			int checkLevel = rs.getInt("checkLevel");					// 判斷等級
			int checkClass = rs.getInt("checkClass");					// 判斷職業
			int rnd = rs.getInt("rnd");									// DB設定合成機率
			int hpConsume = rs.getInt("hpConsume");						// 判斷所需血量
			int mpConsume = rs.getInt("mpConsume");						// 判斷所需魔力
			int bonusitem = rs.getInt("bonus_item");					// 製造成功時額外獲得道具
			int bonusitemcount = rs.getInt("bonus_item_count"); 		// 額外道具的數量
			int bonusitemenchant = rs.getInt("bonus_item_enchant"); 	// 額外道具的強化值				
			
			// 材料
			String materials = rs.getString("materials").replaceAll(" ", "");
			final String[] needids_tmp = materials.split(",");
			
			final int[] needids = new int[needids_tmp.length];
			for (int i = 0 ; i < needids_tmp.length ; i++) {
				needids[i] = Integer.parseInt(needids_tmp[i]);
			}
			
			// 材料數量
			String materials_count = rs.getString("materials_count").replaceAll(" ", "");
			final String[] needcounts_tmp = materials_count.split(",");
			
			final int[] needcounts = new int[needcounts_tmp.length];	
			for (int i = 0 ; i < needcounts_tmp.length ; i++) {
				needcounts[i] = Integer.parseInt(needcounts_tmp[i]);
			}
			
			// 材料的強化值
			String materials_enchants = rs.getString("materials_enchants").replaceAll(" ", "");
			final String[] needenchants_tmp = materials_enchants.split(",");

			final int[] needenchants = new int[needenchants_tmp.length];
			for (int i = 0 ; i < needenchants_tmp.length ; i++) {
				needenchants[i] = Integer.parseInt(needenchants_tmp[i]);
			}

			int new_item = rs.getInt("new_item");						// 合成後的新道具
			int new_item_counts = rs.getInt("new_item_counts");			// 新道具的數量
			int new_Enchantlvl_SW = rs.getInt("new_Enchantlvl_SW");		// 新道具強化值是否隨機的開關，固定:0 ; 隨機:1
			int new_item_Enchantlvl = rs.getInt("new_item_Enchantlvl");	// 新道具的強化值
			int new_item_bless = rs.getInt("new_item_Bless");			// 新道具的祝福狀態
			int residueitem = rs.getInt("residue_item");				// 失敗後留下的道具
			int residuecount = rs.getInt("residue_count");				// 失敗後留下的道具數量
			int replacement_count = rs.getInt("replacement_count");		// 火神痕跡替代火神契約的需要數量
			boolean inputamount = rs.getBoolean("input_amount");		// 是否導引至數量輸入
			boolean all_in_once = rs.getBoolean("all_in_once");			// 是否一次製造所有道具(只判定一次機率)
			String sucesshtml = rs.getString("sucess_html");			// 製造成功時顯示的網頁
			String failhtml = rs.getString("fail_html");				// 製造失敗時顯示的網頁
			String Allmessage = rs.getString("Allmessage");//訊息
			
			
			L1Blend Item_Blend = new L1Blend();
			Item_Blend.set_npcid(npcid);
			Item_Blend.set_action(action);
			Item_Blend.set_note(note);
			Item_Blend.setCheckLevel(checkLevel);
			Item_Blend.setCheckClass(checkClass);
			Item_Blend.setRandom(rnd);
			Item_Blend.setHpConsume(hpConsume);
			Item_Blend.setMpConsume(mpConsume);
			Item_Blend.setMaterials(needids);
			Item_Blend.setMaterials_count(needcounts);
			Item_Blend.set_materials_enchants(needenchants);
			Item_Blend.setNew_item(new_item);
			Item_Blend.setNew_item_counts(new_item_counts);
			Item_Blend.setNew_Enchantlvl_SW(new_Enchantlvl_SW);
			Item_Blend.setNew_item_Enchantlvl(new_item_Enchantlvl);
			Item_Blend.setNew_item_Bless(new_item_bless);
			Item_Blend.setResidue_Item(residueitem);
			Item_Blend.setResidue_Count(residuecount);
			Item_Blend.setReplacement_count(replacement_count);		
			Item_Blend.setInputAmount(inputamount);
			Item_Blend.setAll_In_Once(all_in_once);
			Item_Blend.setBonus_item(bonusitem);
			Item_Blend.setBonus_item_count(bonusitemcount);	
			Item_Blend.setBonus_item_enchant(bonusitemenchant);
			Item_Blend.set_sucesshtml(sucesshtml);
			Item_Blend.set_failhtml(failhtml);
			Item_Blend.set_Allmessage(Allmessage);
			
			
			String key = npcid + action;			
			_CraftIndex.put(key, Item_Blend);
						
			loadCraftList(npcid);
		}
	}
	
	/**
	 * 載入道具製造清單
	 * @param npcid
	 */
	private void loadCraftList(final int npcid) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `火神煉化系統` WHERE `npcid` =?");
			pstm.setInt(1, npcid);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String action = rs.getString("action");		// 對應的命令
				String note = rs.getString("note");			// 道具名稱
						
				String key = npcid + action;												
				_CraftList.put(key, note);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}		
	}


	/**
	 * 傳回此製造命令的資料
	 * @param craftkey
	 * @return
	 */
	public L1Blend getTemplate(String craftkey) {
		return _CraftIndex.get(craftkey);
	}
	
	/**
	 * 傳回製造清單
	 * @return
	 */
	public Map<String, String> get_craftlist() {
		return _CraftList;		
	}	

}