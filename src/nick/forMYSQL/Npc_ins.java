package nick.forMYSQL;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigRecord;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGame;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.world.World;

//import l1j.william.L1WilliamSystemMessage;

@SuppressWarnings({ "rawtypes" })
public class Npc_ins {

	// private static ArrayList aData = new ArrayList();

	private static ArrayList<ArrayList<Object>> aData = new ArrayList<ArrayList<Object>>();

	private static boolean NO_GET_DATA = false;

	public static final String TOKEN = ",";

	/*
	 * public static void main(String a[]) { while(true) { try { Server.main(null); } catch(Exception ex) { } } }
	 */

	public static boolean forNpcQuest(String s, L1PcInstance pc, L1NpcInstance npc, int npcid, int oid) {
		// L1ItemInstance tgitem = pc.getInventory().getItem(q);
		ArrayList aTempData = null;
		if (!NO_GET_DATA) {
			NO_GET_DATA = true;
			getData();
		}
		/*
		 * byte class_id = 0; String msg = ""; if (pc.isCrown()) class_id = 1; else if (pc.isKnight()) class_id = 2; else if (pc.isWizard()) class_id = 3; else if (pc.isElf()) class_id =
		 * 4; else if (pc.isDarkelf()) class_id = 5; else if (pc.isDragonKnight()) class_id = 6; else if (pc.isIllusionist()) { class_id = 7; }
		 */

		L1PcQuest quest = pc.getQuest();

		for (int i = 0; i < aData.size(); i++) {
			aTempData = (ArrayList) aData.get(i);

			if ((aTempData.get(0) != null && ((Integer) aTempData.get(0)).intValue() == npcid)
					&& ((String) aTempData.get(1)).equals(s) // 確認選項
					&& (((int[]) aTempData.get(11) != null && (int[]) aTempData.get(12) != null && !pc.getInventory().checkItem((int[]) aTempData.get(11), (int[]) aTempData.get(12))) || (int[]) aTempData
							.get(11) == null && (int[]) aTempData.get(12) == null)
					&& (((int[]) aTempData.get(9) != null && (int[]) aTempData.get(10) != null && pc.getInventory().checkItem((int[]) aTempData.get(9), (int[]) aTempData.get(10))) || (int[]) aTempData
							.get(9) == null && (int[]) aTempData.get(10) == null)) {
				if ((((Integer) aTempData.get(2)).intValue() != 0) && pc.getLevel() < ((Integer) aTempData.get(2)).intValue()) {
					pc.sendPackets(new S_SystemMessage("等級最低需求" + ((Integer) aTempData.get(2)).intValue() + "才可使用"));
					return false;
				}
				if (aTempData.get(0) != null && (((Integer) aTempData.get(0)).intValue() == 220179) && ((String) aTempData.get(1)).equals(s) && !pc.getDolls().isEmpty()) {
					pc.sendPackets(new S_ServerMessage("請先解除魔法娃娃。"));
					return false;
				}
				if (((Integer) aTempData.get(3)).intValue() != 0) {
					byte class_id = 0;
					String msg = "";
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
					} else if (pc.isDragonKnight()) {
						class_id = 6;
					} else if (pc.isIllusionist()) {
						class_id = 7;
					} else if (pc.isWarrior()) {
						class_id = 8;
					}
					switch (((Integer) aTempData.get(3)).intValue()) {
					case 1:
						msg = "王族";
						break;
					case 2:
						msg = "騎士";
						break;
					case 3:
						msg = "法師";
						break;
					case 4:
						msg = "妖精";
						break;
					case 5:
						msg = "黑暗妖精";
						break;
					case 6:
						msg = "龍騎士";
						break;
					case 7:
						msg = "幻術士";
						break;
					case 8:
						msg = "戰士";
						break;
					}
					if (((Integer) aTempData.get(3)).intValue() != class_id) {
						pc.sendPackets(new S_SystemMessage("你的職業無法使用" + msg + "的專屬道具。"));
						return false;
					}
				}
				if (pc.getQuest().get_step(((Integer) aTempData.get(5)).intValue()) == 255) {
					pc.sendPackets(new S_SystemMessage("您已經執行過上限1次。"));
					return false;
				}
				if ((((Integer) aTempData.get(6)).intValue() != 0) && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == ((Integer) aTempData.get(6)).intValue()) {
					pc.sendPackets(new S_SystemMessage("您已經執行過上限" + ((Integer) aTempData.get(6)).intValue() + "次。"));
					return false;
				}
				if (pc.getMapId() == 8544 || pc.getMapId() == 99 || pc.getMapId() == 300 || pc.getMapId() >= 8700 && pc.getMapId() <= 8726) {
					pc.sendPackets(new S_ServerMessage(166, "\\fU" + ("此地區無法進行離開!!")));
					return false;
				}

				boolean isGet = false;
				boolean isCreate = true;

				// 檢查身上的物品
				if ((int[]) aTempData.get(13) != null && (int[]) aTempData.get(14) != null) {

					int[] materials = (int[]) aTempData.get(13);
					int[] counts = (int[]) aTempData.get(14);

					for (int j = 0; j < materials.length; j++) {
						if (!pc.getInventory().checkItem(materials[j], counts[j])) {
							L1Item temp = ItemTable.get().getTemplate(materials[j]);
							pc.sendPackets(new S_ServerMessage(337, temp.getName() + "(" + (counts[j] - pc.getInventory().countItems(temp.getItemId())) + ")")); // \f1%0不足。
							isCreate = false;
						}
					}

					if (!isCreate) {
						if ((String[]) aTempData.get(23) != null) {
							pc.sendPackets(new S_NPCTalkReturn(oid, (String) aTempData.get(22), (String[]) aTempData.get(23)));
						} else if (aTempData.get(22) != null) {
							pc.sendPackets(new S_NPCTalkReturn(oid, (String) aTempData.get(22)));
						}
						return true;
					}

					if (isCreate && ((Integer) aTempData.get(15)).intValue() == 0) { // 刪除確認的道具、並給予任務道具
						// 刪除道具
						for (int k = 0; k < materials.length; k++) {
							pc.getInventory().consumeItem(materials[k], counts[k]);
						}
					}
				}

				// 給予道具
				if (((Integer) aTempData.get(15)).intValue() == 0 && (int[]) aTempData.get(16) != null && (int[]) aTempData.get(17) != null) {

					int[] giveMaterials = (int[]) aTempData.get(16);
					int[] giveCounts = (int[]) aTempData.get(17);

					for (int l = 0; l < giveMaterials.length; l++) {
						L1ItemInstance item = ItemTable.get().createItem(giveMaterials[l]);

						if (item.getBless() >= 4) { // 封印装備
							// \f1%0%d是不可轉移的…
							pc.sendPackets(new S_SystemMessage("該裝備受到封印卷軸的影響。"));
							return false;
						}

						if (item.isStackable()) {// 可重疊
							item.setCount(giveCounts[l]);// 數量
						} else {
							item.setCount(1);
						}

						if (item != null) {
							isGet = true;
							if (pc.getInventory().checkAddItem(item, (giveCounts[l])) == L1Inventory.OK) {
								pc.getInventory().storeItem(item);
								ConfigRecord.recordToFiles("Npc_ins兌換記錄", "IP" + "(" + pc.getNetConnection().getIp() + ")" + "玩家" + ":【" + pc.getName() + "】" + "兌換" + "【" + item.getName()
										+ "】" + "數量(" + (giveCounts[l]) + ")," + "時間:(" + new Timestamp(System.currentTimeMillis()) + ")。");

							} else { // 持場合地面落 處理（不正防止）
								World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
							}

							pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
						}
					}
				}

				// 紀錄任務
				/*
				 * if (((Integer)aTempData.get(18)).intValue() != 0 && ((Integer)aTempData.get(19)).intValue() != 0 && (isGet || isCreate)) { pc.getQuest().set_step(((Integer)
				 * aTempData.get(18)).intValue(), ((Integer) aTempData.get(19)).intValue()); return true; }
				 */
				if (((Integer) aTempData.get(18)).intValue() != 0 && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == 1 && (isGet || isCreate)) { // 2
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), 2);
				} else if (((Integer) aTempData.get(18)).intValue() != 0 && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == 2 && (isGet || isCreate)) { // 3
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), 3);
				} else if (((Integer) aTempData.get(18)).intValue() != 0 && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == 3 && (isGet || isCreate)) { // 4
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), 4);
				} else if (((Integer) aTempData.get(18)).intValue() != 0 && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == 4 && (isGet || isCreate)) { // 5
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), 5);
				} else if (((Integer) aTempData.get(18)).intValue() != 0 && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == 5 && (isGet || isCreate)) { // 6
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), 6);
				} else if (((Integer) aTempData.get(18)).intValue() != 0 && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == 6 && (isGet || isCreate)) { // 6
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), 7);
				} else if (((Integer) aTempData.get(18)).intValue() != 0 && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == 7 && (isGet || isCreate)) { // 6
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), 8);
				} else if (((Integer) aTempData.get(18)).intValue() != 0 && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == 8 && (isGet || isCreate)) { // 6
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), 9);
				} else if (((Integer) aTempData.get(18)).intValue() != 0 && pc.getQuest().get_step(((Integer) aTempData.get(18)).intValue()) == 9 && (isGet || isCreate)) { // 6
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), 10);
				} else {
					pc.getQuest().set_step(((Integer) aTempData.get(18)).intValue(), ((Integer) aTempData.get(19)).intValue());
				}
				if (((String) aTempData.get(28) != null)) {
					pc.sendPackets(new S_SystemMessage("\\fY" + (String) aTempData.get(28) + ""));
				}
				if ((((Integer) aTempData.get(27)).intValue() != 0) && (isGet || isCreate)) {
					// if(pc.get_other().get_usemap() > 0){
					pc.get_other().set_usemap(-1);
					pc.get_other().set_usemapTime(0);
					// pc.sendPackets(new S_PacketBoxGame(72));
					// ServerUseMapTimer.MAP.remove(pc);
					// }

					L1Teleport.teleport(pc, ((Integer) aTempData.get(24)).intValue(), ((Integer) aTempData.get(25)).intValue(), (short) ((Integer) aTempData.get(26)).intValue(), 5, true);
					pc.get_other().set_usemap(((Integer) aTempData.get(26)).intValue());
					ServerUseMapTimer.put(pc, ((Integer) aTempData.get(27)).intValue());

					pc.sendPackets(new S_ServerMessage("時間限制:" + ((Integer) aTempData.get(27)).intValue() + "秒"));
					// pc.sendPackets(new
					// S_SystemMessage("\\fY"+(String)aTempData.get(28) + ""));
					return true;
				}

				// 顯示對話檔
				if ((String[]) aTempData.get(21) != null && (isGet || isCreate)) {
					pc.sendPackets(new S_NPCTalkReturn(oid, (String) aTempData.get(20), (String[]) aTempData.get(21)));
					return true;
				} else if (aTempData.get(20) != null && (isGet || isCreate)) {
					pc.sendPackets(new S_NPCTalkReturn(oid, (String) aTempData.get(20)));
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

	private static void getData() {
		java.sql.Connection con = null;
		try {
			con = DatabaseFactory.get().getConnection();
			Statement stat = con.createStatement();
			ResultSet rset = stat.executeQuery("SELECT * FROM z_npc_ins");
			ArrayList<Object> aReturn = null;
			String sTemp = null;
			if (rset != null)
				while (rset.next()) {
					aReturn = new ArrayList<Object>();
					aReturn.add(0, new Integer(rset.getInt("npcid"))); // NPC編號
					sTemp = rset.getString("action");
					aReturn.add(1, sTemp);
					aReturn.add(2, Integer.valueOf(rset.getInt("checkLevel"))); // 確認等級
					aReturn.add(3, Integer.valueOf(rset.getInt("checkClass"))); // 確認職業
					aReturn.add(4, new Integer(rset.getInt("checkPoly"))); // 確認變身
					aReturn.add(5, new Integer(rset.getInt("check_QuestId"))); // 確認任務編號
					aReturn.add(6, new Integer(rset.getInt("check_Quest_set"))); // 確認任務流程
					aReturn.add(7, new Integer(rset.getInt("notHaveQuestId"))); // 確認任務編號
					aReturn.add(8, new Integer(rset.getInt("notHaveQuestOrder"))); // 確認任務流程

					if (rset.getString("checkItem") != null && !rset.getString("checkItem").equals("") && !rset.getString("checkItem").equals("0")) // 確認道具
						aReturn.add(9, getArray(rset.getString("checkItem"), TOKEN, 1));
					else
						aReturn.add(9, null);

					if (rset.getString("checkItemCount") != null && !rset.getString("checkItemCount").equals("") && !rset.getString("checkItemCount").equals("0")) // 確認道具數量
						aReturn.add(10, getArray(rset.getString("checkItemCount"), TOKEN, 1));
					else
						aReturn.add(10, null);

					if (rset.getString("notHaveItem") != null && !rset.getString("notHaveItem").equals("") && !rset.getString("notHaveItem").equals("0")) // 確認道具
						aReturn.add(11, getArray(rset.getString("notHaveItem"), TOKEN, 1));
					else
						aReturn.add(11, null);

					if (rset.getString("notHaveItemCount") != null && !rset.getString("notHaveItemCount").equals("") && !rset.getString("notHaveItemCount").equals("0")) // 確認道具數量
						aReturn.add(12, getArray(rset.getString("notHaveItemCount"), TOKEN, 1));
					else
						aReturn.add(12, null);

					if (rset.getString("material") != null && !rset.getString("material").equals("") && !rset.getString("material").equals("0")) // 確認道具
						aReturn.add(13, getArray(rset.getString("material"), TOKEN, 1));
					else
						aReturn.add(13, null);

					if (rset.getString("materialCount") != null && !rset.getString("materialCount").equals("") && !rset.getString("materialCount").equals("0")) // 確認道具數量
						aReturn.add(14, getArray(rset.getString("materialCount"), TOKEN, 1));
					else
						aReturn.add(14, null);

					aReturn.add(15, new Integer(rset.getInt("justCheckMaterial"))); // 只確認道具、不刪除確認的道具

					if (rset.getString("GiveItem") != null && !rset.getString("GiveItem").equals("") && !rset.getString("GiveItem").equals("0")) // 給予道具
						aReturn.add(16, getArray(rset.getString("GiveItem"), TOKEN, 1));
					else
						aReturn.add(16, null);

					if (rset.getString("GiveItemCount") != null && !rset.getString("GiveItemCount").equals("") && !rset.getString("GiveItemCount").equals("0")) // 給予道具數量
						aReturn.add(17, getArray(rset.getString("GiveItemCount"), TOKEN, 1));
					else
						aReturn.add(17, null);

					aReturn.add(18, new Integer(rset.getInt("save_QuestId"))); // 紀錄任務編號
					aReturn.add(19, new Integer(rset.getInt("save_Questset"))); // 紀錄任務流程

					if (rset.getString("ShowHtml") != null && !rset.getString("ShowHtml").equals(""))
						aReturn.add(20, new String(rset.getString("ShowHtml"))); // 顯示對話檔
					else
						aReturn.add(20, null);

					if (rset.getString("ShowHtmlData") != null && !rset.getString("ShowHtmlData").equals(""))
						aReturn.add(21, getArray(rset.getString("ShowHtmlData"), TOKEN, 2)); // 顯示對話內容
					else
						aReturn.add(21, null);

					if (rset.getString("ShowNotHaveHtml") != null && !rset.getString("ShowNotHaveHtml").equals(""))
						aReturn.add(22, new String(rset.getString("ShowNotHaveHtml"))); // 顯示對話檔
					else
						aReturn.add(22, null);

					if (rset.getString("ShowNotHaveHtmlData") != null && !rset.getString("ShowNotHaveHtmlData").equals(""))
						aReturn.add(23, getArray(rset.getString("ShowNotHaveHtmlData"), TOKEN, 2)); // 顯示對話內容
					else
						aReturn.add(23, null);

					aReturn.add(24, new Integer(rset.getInt("tele_x")));
					aReturn.add(25, new Integer(rset.getInt("tele_y")));
					aReturn.add(26, new Integer(rset.getInt("tele_mapid")));
					aReturn.add(27, new Integer(rset.getInt("time")));
					aReturn.add(28, rset.getString("pc_message"));

					aData.add(aReturn);
				}
			if (con != null && !con.isClosed())
				con.close();
		} catch (Exception ex) {
		}
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
		return null;
	}

}