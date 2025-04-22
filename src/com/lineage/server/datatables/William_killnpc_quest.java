/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_TrueTarget;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 擊殺指定NPC - 任務獎勵系統
 * 
 * @author sonykenying
 */

public class William_killnpc_quest {
	private static final Log _log = LogFactory
			.getLog(William_killnpc_quest.class);
	private static William_killnpc_quest _instance;

	public final static HashMap<Integer, William_killnpc_quest> _itemIdIndex = new HashMap<Integer, William_killnpc_quest>();

	public static William_killnpc_quest getInstance() {
		if (_instance == null) {
			_instance = new William_killnpc_quest();
		}
		return _instance;
	}

	@SuppressWarnings("static-access")
	public static void reload() { // Gm.67
		William_killnpc_quest oldInstance = _instance;
		_instance = new William_killnpc_quest();
		oldInstance._itemIdIndex.clear();
	}

	private William_killnpc_quest() {
		loadChackDrop();
	}

	private static void loadChackDrop() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_killnpc_quest");
			rs = pstm.executeQuery();
			fillChackDrop(rs);
		} catch (SQLException e) {
			// _log.log(Level.SEVERE,
			// "error while creating william_killnpc_quest table", e);
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private static void fillChackDrop(ResultSet rs) throws SQLException {
		final PerformanceTimer timer = new PerformanceTimer();
		int dd = 0;
		while (rs.next()) {
			int id = rs.getInt("id");
			String note = rs.getString("note");
			int npcid = rs.getInt("npcid");
			String action = rs.getString("action");
			int checkLevel = rs.getInt("checkLevel");
			int checkClass = rs.getInt("checkClass");
			int checkPoly = rs.getInt("checkPoly");
			int checkHaveKillNpcId = rs.getInt("checkHaveKillNpcId");
			int checkHaveKillCount = rs.getInt("checkHaveKillCount");
			int notHaveKillNpcId = rs.getInt("notHaveKillNpcId");
			int notHaveKillCount = rs.getInt("notHaveKillCount");
			String material = rs.getString("material");
			String materialCount = rs.getString("materialCount");
			String GiveItem = rs.getString("GiveItem");
			String GiveItemCount = rs.getString("GiveItemCount");
			int saveKillNpcId = rs.getInt("saveKillNpcId");
			int saveKillCount = rs.getInt("saveKillCount");
			int npc_gfxid = rs.getInt("npc_gfxid");
			String ShowHtml = rs.getString("ShowHtml");
			String ShowHtmlData = rs.getString("ShowHtmlData");
			String ShowNotHaveHtml = rs.getString("ShowNotHaveHtml");
			String ShowNotHaveHtmlData = rs.getString("ShowNotHaveHtmlData");
			William_killnpc_quest armor_upgrade = new William_killnpc_quest(id,
					note, npcid, action, checkLevel, checkClass, checkPoly,
					checkHaveKillNpcId, checkHaveKillCount, notHaveKillNpcId,
					notHaveKillCount, material, materialCount, GiveItem, GiveItemCount, saveKillNpcId,
					saveKillCount, npc_gfxid, ShowHtml, ShowHtmlData, ShowNotHaveHtml, ShowNotHaveHtmlData);
			_itemIdIndex.put(dd, armor_upgrade);
			dd++;
		}
		_log.info("載入殺怪任務數據數量: " + _itemIdIndex.size() + "(" + timer.get()
				+ "ms)");
	}

	public William_killnpc_quest getTemplate1(int d) {
		return _itemIdIndex.get(d);
	}

	public William_killnpc_quest[] getDissolveList1() {
		return _itemIdIndex.values().toArray(
				new William_killnpc_quest[_itemIdIndex.size()]);
	}

	// 執行對話檔命令任務
	public static boolean forNpcQuest(String s, L1PcInstance pc,
			L1NpcInstance npc, int npcid, int oid) {
		// L1PcQuest quest = pc.getQuest();
		L1Kill_Npc_Quest kill_Npc_Quest = pc.get_kill_npc_Quest();
		int class_id = 0;
		if (pc.isCrown()) {
			class_id = 1;
		} else if (pc.isKnight()) {
			class_id = 2;
		} else if (pc.isWizard()) {
			class_id = 3;
		} else if (pc.isElf()) {
			class_id = 4;
		} else if (pc.isDarkelf()) {
			class_id = 5;
		} else if (pc.isDragonKnight()) { // 龍騎士 sosodemon add
			class_id = 6;
		} else if (pc.isIllusionist()) { // 幻術師 sosodemon add
			class_id = 7;
		} else if (pc.isWarrior()) { // 戰士
			class_id = 8;
		}
		William_killnpc_quest[] william_Online_Reward = William_killnpc_quest
				.getInstance().getDissolveList1();
		William_killnpc_quest william_Online_Reward1 = null;
		William_killnpc_quest william_Online_Reward_ok = null;
		for (int i = 0; i < william_Online_Reward.length; i++) {
			william_Online_Reward1 = William_killnpc_quest.getInstance()
					.getTemplate1(i);

			if (william_Online_Reward1.get_npcid() != npcid) { // 不是該NPC編號 跳過
				continue;
			}
			if (!william_Online_Reward1.get_action().equals(s)) { // 不是該執行命令 跳過
				continue;
			}
			if (william_Online_Reward1.get_checkLevel() != 0) {
				if (william_Online_Reward1.get_checkLevel() > pc.getLevel()) { // 等級不足
																				// 跳過
					continue;
				}
			}
			if (william_Online_Reward1.get_checkClass() != 0) {
				if (william_Online_Reward1.get_checkLevel() != class_id) { // 職業不符
																			// 跳過
					continue;
				}
			}
			if (william_Online_Reward1.get_checkPoly() != 0) {
				if (william_Online_Reward1.get_checkPoly() != pc
						.getTempCharGfx()) { // 變身編號不符 跳過
					continue;
				}
			}
			if (william_Online_Reward1.get_checkHaveKillNpcId() != 0) { // 判定沒有執行過該任務編號者
																		// 跳過
				if (kill_Npc_Quest.get_step(william_Online_Reward1
						.get_checkHaveKillNpcId()) != william_Online_Reward1
						.get_checkHaveKillCount() + 1) {
					continue;
				}
			}
			if (william_Online_Reward1.get_notHaveKillNpcId() != 0) { // 判定有執行過該任務編號者
																		// 跳過
				if (kill_Npc_Quest.get_step(william_Online_Reward1
						.get_notHaveKillNpcId()) == william_Online_Reward1
						.get_notHaveKillCount()) {
					continue;
				}
			}
			william_Online_Reward_ok = william_Online_Reward1;
			break;
		}
		if (william_Online_Reward_ok != null) {
			boolean isCreate = true;

			if (william_Online_Reward_ok.get_material() != null
					&& !william_Online_Reward_ok.get_material().equals("")) {
				int[] materials = (int[]) getArray(
						william_Online_Reward_ok.get_material(), ",", 1);
				int[] counts = (int[]) getArray(
						william_Online_Reward_ok.get_materialCount(), ",", 1);
				for (int j = 0; j < materials.length; j++) {
					if (!pc.getInventory().checkItem(materials[j],
							counts[j])) {
						L1Item temp = ItemTable.get().getTemplate(
								materials[j]);
						pc.sendPackets(new S_ServerMessage(337, temp
								.getName()
								+ "("
								+ (counts[j] - pc.getInventory()
										.countItems(temp.getItemId()))
								+ ")")); // \f1%0不足。
						isCreate = false;
					}
				}
				if (!isCreate) {
					if (william_Online_Reward_ok.get_ShowNotHaveHtml() != null
							&& !william_Online_Reward_ok.get_ShowNotHaveHtmlData().equals("")) {
						pc.sendPackets(new S_NPCTalkReturn(oid,
								william_Online_Reward_ok.get_ShowNotHaveHtml(),
								(String[]) getArray(
										william_Online_Reward_ok.get_ShowNotHaveHtmlData(),
										"TOKEN", 2)));
						return true;
					} else if (william_Online_Reward_ok.get_ShowNotHaveHtml() != null
							&& !william_Online_Reward_ok.get_ShowNotHaveHtml().equals("")) {
						pc.sendPackets(new S_NPCTalkReturn(oid,
								william_Online_Reward_ok.get_ShowNotHaveHtml()));
						return true;
					}
					return true;
				} else {
					for (int k = 0; k < materials.length; k++) {
						pc.getInventory().consumeItem(materials[k],counts[k]);
					}
				}
			}
			
			if (isCreate) {
				if (william_Online_Reward_ok.get_saveKillNpcId() != 0) {
					kill_Npc_Quest.set_step(
							william_Online_Reward_ok.get_saveKillNpcId(),
							william_Online_Reward_ok.get_saveKillCount());
				}
				if (william_Online_Reward_ok.get_GiveItem() != null
						&& !william_Online_Reward_ok.get_GiveItem().equals("")) {
					int[] itemGive = (int[]) getArray(
							william_Online_Reward_ok.get_GiveItem(), ",", 1);
					int[] itemCount = (int[]) getArray(
							william_Online_Reward_ok.get_GiveItemCount(), ",", 1);
					for (int j = 0; j < itemGive.length; j++) {
						CreateNewItem.createNewItem(pc, itemGive[j], itemCount[j]);
					}
				}
				// 顯示對話檔
				if (william_Online_Reward_ok.get_ShowHtmlData() != null
						&& !william_Online_Reward_ok.get_ShowHtmlData().equals("")) {
					pc.sendPackets(new S_NPCTalkReturn(oid,
							william_Online_Reward_ok.get_ShowHtml(),
							(String[]) getArray(
									william_Online_Reward_ok.get_ShowHtmlData(),
									"TOKEN", 2)));
					return true;
				} else if (william_Online_Reward_ok.get_ShowHtml() != null
						&& !william_Online_Reward_ok.get_ShowHtml().equals("")) {
					pc.sendPackets(new S_NPCTalkReturn(oid,
							william_Online_Reward_ok.get_ShowHtml()));
					return true;
				} else {
					pc.sendPackets(new S_NPCTalkReturn(oid, ""));
					return true;
				}
				// 顯示對話檔 end
			}
			
		}
		return false;
	}

	// 檢查玩家所接任務的殺怪流程
	public static void add_kill_npcId(L1PcInstance pc, L1NpcInstance npc) {
		L1Kill_Npc_Quest kill_Npc_Quest = pc.get_kill_npc_Quest();
		William_killnpc_quest[] william_Online_Reward = William_killnpc_quest
				.getInstance().getDissolveList1();
		William_killnpc_quest william_Online_Reward1 = null;
		William_killnpc_quest william_Online_Reward_ok = null;
		for (int i = 0; i < william_Online_Reward.length; i++) {
			william_Online_Reward1 = William_killnpc_quest.getInstance()
					.getTemplate1(i);
			// if
			// (kill_Npc_Quest.get_step(william_Online_Reward1.get_quest_npcid())
			// == 0) {
			// continue;
			// }
			if (kill_Npc_Quest.get_step(william_Online_Reward1
					.get_checkHaveKillNpcId()) == 0) {
				continue;
			}
			if (william_Online_Reward1.get_checkHaveKillNpcId() == npc
					.getNpcTemplate().get_npcId()) {
				william_Online_Reward_ok = william_Online_Reward1;
				break;
			}
		}
		if (william_Online_Reward_ok != null) {
			if (kill_Npc_Quest.get_step(william_Online_Reward_ok
					.get_checkHaveKillNpcId()) < william_Online_Reward_ok
					.get_checkHaveKillCount() + 1) {
				kill_Npc_Quest.add_step(
						william_Online_Reward_ok.get_checkHaveKillNpcId(), 1);
				//pc.sendPackets(new S_SystemMessage("\\aH執行獵殺任務："
				pc.sendPackets(new S_SystemMessage(william_Online_Reward_ok.get_note()
						+ (kill_Npc_Quest.get_step(william_Online_Reward_ok
								.get_checkHaveKillNpcId()) - 1) + " / "
						+ william_Online_Reward_ok.get_checkHaveKillCount()));
			} else {

			}
		}
	}

	// 判定玩家所接的任務編號，並且讓怪物顯示特效
	public static void sendGfx(L1PcInstance pc, L1NpcInstance npc) {
		L1Kill_Npc_Quest kill_Npc_Quest = pc.get_kill_npc_Quest();
		William_killnpc_quest[] william_Online_Reward = William_killnpc_quest
				.getInstance().getDissolveList1();

		William_killnpc_quest william_Online_Reward1 = null;
		William_killnpc_quest william_Online_Reward_ok = null;
		for (int i = 0; i < william_Online_Reward.length; i++) {
			william_Online_Reward1 = William_killnpc_quest.getInstance()
					.getTemplate1(i);
			if (kill_Npc_Quest.get_step(william_Online_Reward1
					.get_checkHaveKillNpcId()) == 0) {
				continue;
			}
			if (william_Online_Reward1.get_checkHaveKillNpcId() == npc
					.getNpcTemplate().get_npcId()) {
				william_Online_Reward_ok = william_Online_Reward1;
				break;
			}
		}
		if (william_Online_Reward_ok != null) {
			if (william_Online_Reward_ok.get_npc_gfxid() != 0) {
				if (kill_Npc_Quest.get_step(william_Online_Reward_ok
						.get_checkHaveKillNpcId()) < william_Online_Reward_ok
						.get_checkHaveKillCount() + 1) {
					pc.sendPackets(new S_TrueTarget(npc.getId(),
							william_Online_Reward_ok.get_npc_gfxid(), 1));
				}
			}
		}

	}

	private int _id;
	private String _note;
	private int _npcid;
	private String _action;
	private int _checkLevel;
	private int _checkClass;
	private int _checkPoly;
	private int _checkHaveKillNpcId;
	private int _checkHaveKillCount;
	private int _notHaveKillNpcId;
	private int _notHaveKillCount;
	private String _material;
	private String _materialCount;
	private String _GiveItem;
	private String _GiveItemCount;
	private int _saveKillNpcId;
	private int _saveKillCount;
	private int _npc_gfxid;
	private String _ShowHtml;
	private String _ShowHtmlData;
	private String _ShowNotHaveHtml;
	private String _ShowNotHaveHtmlData;
	public William_killnpc_quest(int id, String note, int npcid, String action,
			int checkLevel, int checkClass, int checkPoly,
			int checkHaveKillNpcId, int checkHaveKillCount,
			int notHaveKillNpcId, int notHaveKillCount, String material, String materialCount,
			String GiveItem, String GiveItemCount, int saveKillNpcId, int saveKillCount,
			int npc_gfxid, String ShowHtml, String ShowHtmlData, String ShowNotHaveHtml, String ShowNotHaveHtmlData) {
		_id = id;
		_note = note;
		_npcid = npcid;
		_action = action;
		_checkLevel = checkLevel;
		_checkClass = checkClass;
		_checkPoly = checkPoly;
		_checkHaveKillNpcId = checkHaveKillNpcId;
		_checkHaveKillCount = checkHaveKillCount;
		_notHaveKillNpcId = notHaveKillNpcId;
		_notHaveKillCount = notHaveKillCount;
		_material = material;
		_materialCount = materialCount;
		_GiveItem = GiveItem;
		_GiveItemCount = GiveItemCount;
		_saveKillNpcId = saveKillNpcId;
		_saveKillCount = saveKillCount;
		_npc_gfxid = npc_gfxid;
		_ShowHtml = ShowHtml;
		_ShowHtmlData = ShowHtmlData;
		_ShowNotHaveHtml = ShowNotHaveHtml;
		_ShowNotHaveHtmlData = ShowNotHaveHtmlData;
	}

	public int get_id() {
		return _id;
	}

	public String get_note() {
		return _note;
	}

	public int get_npcid() {
		return _npcid;
	}

	public String get_action() {
		return _action;
	}

	public int get_checkLevel() {
		return _checkLevel;
	}

	public int get_checkClass() {
		return _checkClass;
	}

	public int get_checkPoly() {
		return _checkPoly;
	}

	public int get_checkHaveKillNpcId() {
		return _checkHaveKillNpcId;
	}

	public int get_checkHaveKillCount() {
		return _checkHaveKillCount;
	}

	public int get_notHaveKillNpcId() {
		return _notHaveKillNpcId;
	}

	public int get_notHaveKillCount() {
		return _notHaveKillCount;
	}

	public String get_GiveItem() {
		return _GiveItem;
	}

	public String get_GiveItemCount() {
		return _GiveItemCount;
	}

	public int get_saveKillNpcId() {
		return _saveKillNpcId;
	}

	public int get_saveKillCount() {
		return _saveKillCount;
	}

	public int get_npc_gfxid() {
		return _npc_gfxid;
	}

	public String get_ShowHtml() {
		return _ShowHtml;
	}

	public String get_ShowHtmlData() {
		return _ShowHtmlData;
	}

	public String get_material() {
		return _material;
	}
	public String get_materialCount() {
		return _materialCount;
	}
	public String get_ShowNotHaveHtml() {
		return _ShowNotHaveHtml;
	}
	public String get_ShowNotHaveHtmlData() {
		return _ShowNotHaveHtmlData;
	}

	private static Object getArray(String s, String sToken, int iType) {
		StringTokenizer st = new StringTokenizer(s, sToken);
		int iSize = st.countTokens();
		String sTemp = null;
		if (iType == 1) { // int
			int[] iReturn = new int[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				iReturn[i] = Integer.parseInt(sTemp);
			}
			return iReturn;
		}
		if (iType == 2) { // String
			String[] sReturn = new String[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn[i] = sTemp;
			}
			return sReturn;
		}
		if (iType == 3) { // String
			String sReturn = null;
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn = sTemp;
			}
			return sReturn;
		}
		if (iType == 4) { // short
			short[] iReturn = new short[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				iReturn[i] = Short.parseShort(sTemp);
			}
			return iReturn;
		}
		return null;
	}
}
