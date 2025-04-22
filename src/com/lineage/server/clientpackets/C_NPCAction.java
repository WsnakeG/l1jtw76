package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.BLESSED_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.CKEW_LV50;
import static com.lineage.server.model.skill.L1SkillId.DE_LV30;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_PROTECTION;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_BARLOG;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_YAHEE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HASTE;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import nick.forMYSQL.NpcBuffSkills;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.add.system.L1Blend;
import com.add.system.L1BlendTable;
import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.InnTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcActionTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.UBTable;
import com.lineage.server.datatables.William_killnpc_quest;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.datatables.lock.TownReading;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1HauntedHouse;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.model.L1UltimateBattle;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1HousekeeperInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.model.npc.action.L1NpcAction;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_DelSkill;
import com.lineage.server.serverpackets.S_Deposit;
import com.lineage.server.serverpackets.S_Drawal;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_HowManyKey;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxHpMsg;
import com.lineage.server.serverpackets.S_PetList;
import com.lineage.server.serverpackets.S_RetrieveElfList;
import com.lineage.server.serverpackets.S_RetrieveList;
import com.lineage.server.serverpackets.S_RetrievePledgeList;
import com.lineage.server.serverpackets.S_SellHouse;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShopBuyList;
import com.lineage.server.serverpackets.S_ShopSellList;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_TaxRate;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.templates.L1House;
import com.lineage.server.templates.L1Inn;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.templates.L1Town;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 要求物件對話視窗結果
 * 
 * @author daien
 */
public class C_NPCAction extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_NPCAction.class);

	/*
	 * public C_NPCAction() { } public C_NPCAction(final byte[] abyte0, final ClientExecutor client) { super(abyte0); try { this.start(abyte0, client); } catch (final Exception e) {
	 * _log.error(e.getLocalizedMessage(), e); } }
	 */

	private static Random _random = new Random();

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// System.out.println("資料載入");
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}

			if (pc.isDead()) { // 死亡
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			if (pc.isPrivateShop()) { // 商店村模式
				return;
			}

			final int objid = readD();
			final String s = readS();

			int[] materials = null;
			int[] counts = null;
			int[] createitem = null;
			int[] createcount = null;

			String htmlid = null;
			String success_htmlid = null;
			String failure_htmlid = null;
			String[] htmldata = null;

			L1Npc npctemp = null;

			final L1Object obj = World.get().findObject(objid);
			if (obj == null) {
				_log.error("該OBJID編號的 NPC已經不存在世界中: " + objid);
				return;
			}

			// 命令來自於NPC
			if (obj instanceof L1NpcInstance) {
				final L1NpcInstance tmp = (L1NpcInstance) obj;
				// add william.NpcQuest
				if (com.lineage.william.NpcQuest.forNpcQuest(s, pc, ((L1NpcInstance) obj), ((L1NpcInstance) obj).getNpcTemplate().get_npcId(), objid)) {
					return;
				}
				if (NpcBuffSkills.giveBuff(pc, tmp, s)) {
					return;
				}
				if (William_killnpc_quest.forNpcQuest(s, pc, ((L1NpcInstance) obj), ((L1NpcInstance) obj).getNpcTemplate().get_npcId(), objid)) {
					return;
				}
				String s2 = null;
				try {
					if (tmp.getNpcTemplate().get_classname().equalsIgnoreCase("other.Npc_AuctionBoard")) {
						s2 = readS();
					} else if (tmp.getNpcTemplate().get_classname().equalsIgnoreCase("other.Npc_Board")) {
						s2 = readS();
					}
				} catch (final Exception e) {
				}
				if (obj instanceof L1PetInstance) {
					final L1PetInstance npc = (L1PetInstance) obj;
					pc.getActionPet().action(npc, s);
					return;

				} else if (obj instanceof L1SummonInstance) {
					final L1SummonInstance npc = (L1SummonInstance) obj;
					pc.getActionSummon().action(npc, s);
					return;

				} else {
					final L1NpcInstance npc = (L1NpcInstance) obj;

					final int difflocx = Math.abs(pc.getX() - npc.getX());
					final int difflocy = Math.abs(pc.getY() - npc.getY());
					// 3マス以上離れた場合アクション無効 20240926
					if ((difflocx > 5) || (difflocy > 5)) {
						return;
					}

					// /** 顯示可製造的物品列表 */
					// if (s.equalsIgnoreCase("request craft")) {
					// ShowCraftList(pc, npc);
					// return;
					// }
					// /** END */
					//
					// /** 顯示條件清單 */
					// final String craftkey = npctemp.get_npcId() + s;// 製造命令
					// final L1Blend ItemBlend = L1BlendTable.getInstance().getTemplate(craftkey);
					// if (ItemBlend != null) {
					// ItemBlend.ShowCraftHtml(pc, npc, ItemBlend);// 顯示條件清單
					// npc.set_craftkey(craftkey);// 暫存製造命令
					// return;
					// }
					// /** END */
					//
					// /** 確認或取消製造道具 */
					// final String craftkey2 = npc.get_craftkey();// 取回製造命令
					// final L1Blend ItemBlend2 = L1BlendTable.getInstance().getTemplate(craftkey2);
					// if (ItemBlend2 != null) {
					// if (s.equalsIgnoreCase("confirm craft")) {// 確認製造道具
					// ItemBlend2.CheckCraftItem(pc, npc, ItemBlend2, 1, false);
					// return;
					// } else if (s.equalsIgnoreCase("cancel craft")) {// 取消製造道具
					// pc.sendPackets(new S_CloseList(pc.getId()));
					// npc.set_craftkey(null);// 清空製造命令
					// return;
					// }
					// }
					//
					// /** END */

					if (npc.ACTION != null) {
						if ((s2 != null) && (s2.length() > 0)) {
							npc.ACTION.action(pc, npc, s + "," + s2, 0);
							return;
						}
						npc.ACTION.action(pc, npc, s, 0);
						return;
					}

					if (nick.forMYSQL.Npc_ins.forNpcQuest(s, pc, ((L1NpcInstance) obj), ((L1NpcInstance) obj).getNpcTemplate().get_npcId(), objid)) {
						htmlid = "";
						return;
					}

					npc.onFinalAction(pc, s);
				}

				// 命令來自於PC
			} else if (obj instanceof L1PcInstance) {
				final L1PcInstance target = (L1PcInstance) obj;
				target.getAction().action(s, 0);
				return;
			}

			// XML化されたアクション
			final L1NpcAction action = NpcActionTable.getInstance().get(s, pc, obj);
			if (action != null) {
				final L1NpcHtml result = action.execute(s, pc, obj, readByte());
				if (result != null) {
					pc.sendPackets(new S_NPCTalkReturn(obj.getId(), result));
				}
				return;
			}

			/*
			 * 其他命令處理
			 */
			if (s.equalsIgnoreCase("buy")) {// 買
				try {
					// 出售物品列表
					pc.sendPackets(new S_ShopSellList(objid));

				} catch (final Exception e) {

				}

			} else if (s.equalsIgnoreCase("sell")) {// 賣
				final int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
				if ((npcid == 70523) || (npcid == 70805)) { // ラダー or ジュリー
					htmlid = "ladar2";

				} else if ((npcid == 70537) || (npcid == 70807)) { // ファーリン or
																	// フィン
					htmlid = "farlin2";

				} else if ((npcid == 70525) || (npcid == 70804)) { // ライアン or
																	// ジョエル
					htmlid = "lien2";

				} else if ((npcid == 50527) || (npcid == 50505) || (npcid == 50519) || (npcid == 50545) || (npcid == 50531) || (npcid == 50529) || (npcid == 50516) || (npcid == 50538)
						|| (npcid == 50518) || (npcid == 50509) || (npcid == 50536) || (npcid == 50520) || (npcid == 50543) || (npcid == 50526) || (npcid == 50512) || (npcid == 50510)
						|| (npcid == 50504) || (npcid == 50525) || (npcid == 50534) || (npcid == 50540) || (npcid == 50515) || (npcid == 50513) || (npcid == 50528) || (npcid == 50533)
						|| (npcid == 50542) || (npcid == 50511) || (npcid == 50501) || (npcid == 50503) || (npcid == 50508) || (npcid == 50514) || (npcid == 50532) || (npcid == 50544)
						|| (npcid == 50524) || (npcid == 50535) || (npcid == 50521) || (npcid == 50517) || (npcid == 50537) || (npcid == 50539) || (npcid == 50507) || (npcid == 50530)
						|| (npcid == 50502) || (npcid == 50506) || (npcid == 50522) || (npcid == 50541) || (npcid == 50523) || (npcid == 50620) || (npcid == 50623) || (npcid == 50619)
						|| (npcid == 50621) || (npcid == 50622) || (npcid == 50624) || (npcid == 50617) || (npcid == 50614) || (npcid == 50618) || (npcid == 50616) || (npcid == 50615)
						|| (npcid == 50626) || (npcid == 50627) || (npcid == 50628) || (npcid == 50629) || (npcid == 50630) || (npcid == 50631)) { // アジトのNPC
					final String sellHouseMessage = sellHouse(pc, objid, npcid);
					if (sellHouseMessage != null) {
						htmlid = sellHouseMessage;
					}
				} else { // 一般商人
					// 買い取りリスト表示
					pc.sendPackets(new S_ShopBuyList(objid, pc));
				}

			}
			// 祭司設定補血量
			else if (s.equalsIgnoreCase("Hierarch_1")) { // -1
				if (pc.getHierarch() > 0) {
					pc.setHierarch(pc.getHierarch() - 1);
				}
				String msg0 = "";
				final String msg1 = String.valueOf(pc.getHierarch() * 10);
				if (((L1NpcInstance) obj).getHierarch() == 1) {
					msg0 = "輔助";
				} else {
					msg0 = "休息";
				}
				htmldata = new String[] { ((L1NpcInstance) obj).getName(), String.valueOf(((L1NpcInstance) obj).getCurrentMp()), String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0,
						msg1 };
				htmlid = "Hierarch";
			} else if (s.equalsIgnoreCase("Hierarch_2")) { // +1
				if (pc.getHierarch() < 10) {
					pc.setHierarch(pc.getHierarch() + 1);
				}
				String msg0 = "";
				final String msg1 = String.valueOf(pc.getHierarch() * 10);
				if (((L1NpcInstance) obj).getHierarch() == 1) {
					msg0 = "輔助";
				} else {
					msg0 = "休息";
				}
				htmldata = new String[] { ((L1NpcInstance) obj).getName(), String.valueOf(((L1NpcInstance) obj).getCurrentMp()), String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0,
						msg1 };
				htmlid = "Hierarch";
			} else if (s.equalsIgnoreCase("Hierarch_3")) { // 切換狀態

				((L1NpcInstance) obj).setHierarch(1);

				String msg0 = "";
				final String msg1 = String.valueOf(pc.getHierarch() * 10);
				if (((L1NpcInstance) obj).getHierarch() == 1) {
					msg0 = "輔助";
				} else {
					msg0 = "跟隨";
				}
				htmldata = new String[] { ((L1NpcInstance) obj).getName(), String.valueOf(((L1NpcInstance) obj).getCurrentMp()), String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0,
						msg1 };
				htmlid = "Hierarch";
			} else if (s.equalsIgnoreCase("Hierarch_4")) { // 切換狀態
				((L1NpcInstance) obj).setHierarch(0);
				String msg0 = "";
				final String msg1 = String.valueOf(pc.getHierarch() * 10);
				if (((L1NpcInstance) obj).getHierarch() == 1) {
					msg0 = "輔助";
				} else {
					msg0 = "跟隨";
				}
				htmldata = new String[] { ((L1NpcInstance) obj).getName(), String.valueOf(((L1NpcInstance) obj).getCurrentMp()), String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0,
						msg1 };
				htmlid = "Hierarch";
				// 祭司設定補血量 end
			} else if (s.equalsIgnoreCase("retrieve")) { // 「個人倉庫：アイテムを受け取る」
				if (pc.getLevel() >= 5) {
					final int size = pc.getDwarfInventory().getItems().size();
					if (size > 0) {
						// 倉庫密碼。
						final int srcpwd = client.getAccount().get_warehouse();
						if (srcpwd > 0) {
							pc.sendPackets(new S_ServerMessage(834));
							return;
						}
						pc.sendPackets(new S_RetrieveList(objid, pc));

					} else {
						// noitemret
						pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
					}
				}

			} else if (s.equalsIgnoreCase("retrieve-elven")) { // 「エルフ倉庫：荷物を受け取る」
				if ((pc.getLevel() >= 5) && pc.isElf()) {
					final int size = pc.getDwarfForElfInventory().getSize();
					if (size > 0) {
						// 倉庫密碼。
						final int srcpwd = client.getAccount().get_warehouse();
						if (srcpwd > 0) {
							pc.sendPackets(new S_ServerMessage(834));
							return;
						}
						pc.sendPackets(new S_RetrieveElfList(objid, pc));

					} else {
						// noitemret
						pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
					}
				}

			} else if (s.equalsIgnoreCase("retrieve-pledge")) { // 「血盟倉庫：荷物を受け取る」
				if (pc.getLevel() >= 5) {
					if (pc.getClanid() == 0) {
						// \f1血盟倉庫を使用するには血盟に加入していなくてはなりません。
						pc.sendPackets(new S_ServerMessage(208));
						return;
					}

					// final L1Clan clan =
					// WorldClan.get().getClan(pc.getClanname());
					final int size = pc.getClan().getDwarfForClanInventory().getSize();

					if (size > 0) {
						final int rank = pc.getClanRank();
						switch (rank) {
						case L1Clan.CLAN_RANK_PUBLIC:// 2:一般
						case L1Clan.CLAN_RANK_GUARDIAN:// 3:副君主
						case L1Clan.ALLIANCE_CLAN_RANK_ATTEND:// 5:修習騎士
						case L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN:// 6:守護騎士
						case L1Clan.NORMAL_CLAN_RANK_GENERAL:// 7:一般
						case L1Clan.NORMAL_CLAN_RANK_ATTEND:// 8:修習騎士
						case L1Clan.NORMAL_CLAN_RANK_GUARDIAN:// 9:守護騎士
							if (pc.getTitle().equalsIgnoreCase("")) {
								// 只有收到稱謂的人才能使用血盟倉庫。
								pc.sendPackets(new S_ServerMessage(728));
								return;
							}
							break;

						case L1Clan.CLAN_RANK_PRINCE:// 4:聯盟君主
						case L1Clan.NORMAL_CLAN_RANK_PRINCE:// 10:聯盟君主
							break;
						default:
							// 只有收到稱謂的人才能使用血盟倉庫。
							pc.sendPackets(new S_ServerMessage(728));
							return;
						}
						// 倉庫密碼。
						final int srcpwd = client.getAccount().get_warehouse();
						if (srcpwd > 0) {
							pc.sendPackets(new S_ServerMessage(834));
							return;
						}
						pc.sendPackets(new S_RetrievePledgeList(objid, pc));

					} else {
						// noitemret
						pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
					}
				}

			} else if (s.equalsIgnoreCase("get")) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				final int npcId = npc.getNpcTemplate().get_npcId();
				// クーパー or ダンハム
				if ((npcId == 70099) || (npcId == 70796)) {
					final L1ItemInstance item = pc.getInventory().storeItem(20081, 1); // オイルスキンマント
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0が%1をくれました。
					pc.getQuest().set_end(L1PcQuest.QUEST_OILSKINMANT);
					htmlid = ""; // ウィンドウを消す
				}
				// タウンマスター：報酬をもらう
				else if ((npcId == 70528) || (npcId == 70546) || (npcId == 70567) || (npcId == 70594) || (npcId == 70654) || (npcId == 70748) || (npcId == 70774) || (npcId == 70799)
						|| (npcId == 70815) || (npcId == 70860)) {

					if (pc.getHomeTownId() > 0) {

					} else {

					}
				}

			} else if (s.equalsIgnoreCase("room")) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				final int npcId = npc.getNpcTemplate().get_npcId();
				boolean canRent = false;// 是否可以租房
				boolean findRoom = false;// 是否找到空房
				boolean isRented = false;// 是否已有租房
				boolean isHall = false;// 是否為會議室
				int roomNumber = 0;// 房號
				byte roomCount = 0;// 已佔用房數
				for (int i = 0; i < 16; i++) {
					final L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
					if (inn != null) {
						if (inn.isHall()) {// 是會議室
							isHall = true;
						}
						final Timestamp dueTime = inn.getDueTime();
						final Calendar cal = Calendar.getInstance();
						final long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000L;
						if ((inn.getLodgerId() == pc.getId()) && (checkDueTime < 0L)) {// 還有租房資料及剩餘時間
							isRented = true;
							break;
						}
						if (pc.getInventory().checkItem(40312, 1)) {// 身上有旅館鑰匙
							isRented = true;
							break;
						}
						if ((!findRoom) && (!isRented)) {
							if (checkDueTime >= 0L) {// 已超過時間
								canRent = true;
								findRoom = true;
								roomNumber = inn.getRoomNumber();
							} else if (!inn.isHall()) {// 不是會議室
								roomCount = (byte) (roomCount + 1);// 已佔用房數
							}
						}
					}
				}

				if (isRented) {
					if (isHall) {// 是會議室
						htmlid = "inn15";
					} else {
						htmlid = "inn5";
					}
				} else if (roomCount >= 12) {// 已佔用房數
					htmlid = "inn6";
				} else if (canRent) {
					pc.setInnRoomNumber(roomNumber);
					pc.setHall(false);
					pc.sendPackets(new S_HowManyKey(npc, 300, 1, 8, "inn2"));
				}
			} else if ((s.equalsIgnoreCase("hall")) && ((obj instanceof L1MerchantInstance))) {
				if (pc.isCrown()) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final int npcId = npc.getNpcTemplate().get_npcId();
					boolean canRent = false;// 是否可以租房
					boolean findRoom = false;// 是否找到空房
					boolean isRented = false;// 是否已有租房
					boolean isHall = false;// 是否為會議室
					int roomNumber = 0;// 房號
					byte roomCount = 0;// 已佔用房數
					for (int i = 0; i < 16; i++) {
						final L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
						if (inn != null) {
							if (inn.isHall()) {// 是會議室
								isHall = true;
							}
							final Timestamp dueTime = inn.getDueTime();
							final Calendar cal = Calendar.getInstance();
							final long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000L;
							if ((inn.getLodgerId() == pc.getId()) && (checkDueTime < 0L)) {// 還有租房資料及剩餘時間
								isRented = true;
								break;
							}
							if (pc.getInventory().checkItem(40312, 1)) {// 身上有旅館鑰匙
								isRented = true;
								break;
							}
							if ((!findRoom) && (!isRented)) {
								if (checkDueTime >= 0L) {// 已超過時間
									canRent = true;
									findRoom = true;
									roomNumber = inn.getRoomNumber();
								} else if (inn.isHall()) {
									roomCount = (byte) (roomCount + 1);
								}
							}
						}
					}

					if (isRented) {
						if (isHall) {
							htmlid = "inn15";
						} else {
							htmlid = "inn5";
						}
					} else if (roomCount >= 4) {// 已佔用會議室數
						htmlid = "inn16";
					} else if (canRent) {
						pc.setInnRoomNumber(roomNumber);
						pc.setHall(true);
						pc.sendPackets(new S_HowManyKey(npc, 300, 1, 16, "inn12"));
					}
				} else {
					htmlid = "inn10";
				}
			} else if (s.equalsIgnoreCase("return")) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				final int npcId = npc.getNpcTemplate().get_npcId();
				int price = 0;
				boolean isBreak = false;
				L1Inn inn;
				for (int i = 0; i < 16; i++) {
					inn = InnTable.getInstance().getTemplate(npcId, i);
					if ((inn != null) && (inn.getLodgerId() == pc.getId())) {
						final Timestamp dueTime = inn.getDueTime();
						if (dueTime != null) {
							final Calendar cal = Calendar.getInstance();
							if ((cal.getTimeInMillis() - dueTime.getTime()) / 1000L < 0L) {// 尚未到期
								isBreak = true;
								price += 60;// 尚未到期則加60元
							}
						}
						final Timestamp ts = new Timestamp(System.currentTimeMillis());
						inn.setDueTime(ts);
						inn.setLodgerId(0);
						inn.setKeyId(0);
						inn.setHall(false);

						InnTable.getInstance().updateInn(inn);// 更新DB資料
						break;
					}
				}

				for (final L1ItemInstance item : pc.getInventory().getItems()) {
					if (item.getInnNpcId() == npcId) {
						price = (int) (price + 20L * item.getCount());
						InnKeyTable.DeleteKey(item);
						pc.getInventory().removeItem(item);
						isBreak = true;
					}
				}

				if (isBreak) {// 是否退租
					htmldata = new String[] { npc.getName(), String.valueOf(price) };
					htmlid = "inn20";
					pc.getInventory().storeItem(40308, price);
				} else {
					htmlid = "";
				}
			} else if (s.equalsIgnoreCase("enter")) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				final int npcId = npc.getNpcTemplate().get_npcId();
				for (final L1ItemInstance item : pc.getInventory().getItems()) {
					if (item.getInnNpcId() == npcId) {// 對應的npcid相同
						for (int i = 0; i < 16; i++) {
							final L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
							if (inn.getKeyId() == item.getKeyId()) {// 鑰匙編號相同
								final Timestamp dueTime = item.getDueTime();
								if (dueTime != null) {
									final Calendar cal = Calendar.getInstance();
									if ((cal.getTimeInMillis() - dueTime.getTime()) / 1000L < 0L) {// 還未到退房時間
										int[] data = null;
										switch (npcId) {
										case 70012:
											data = new int[] { 32745, 32803, 16384, 32743, 32808, 16896 };
											break;
										case 70019:
											data = new int[] { 32743, 32803, 17408, 32744, 32807, 17920 };
											break;
										case 70031:
											data = new int[] { 32744, 32803, 18432, 32744, 32807, 18944 };
											break;
										case 70065:
											data = new int[] { 32744, 32803, 19456, 32744, 32807, 19968 };
											break;
										case 70070:
											data = new int[] { 32744, 32803, 20480, 32744, 32807, 20992 };
											break;
										case 70075:
											data = new int[] { 32744, 32803, 21504, 32744, 32807, 22016 };
											break;
										case 70084:
											data = new int[] { 32744, 32803, 22528, 32744, 32807, 23040 };
											break;
										case 70054:
											data = new int[] { 32744, 32803, 23552, 32744, 32807, 24064 };
											break;
										case 70096:
											data = new int[] { 32744, 32803, 24576, 32744, 32807, 25088 };
											break;
										}

										if (!item.checkRoomOrHall()) {// 一般房間
											pc.set_showId(item.getKeyId());// 設置副本編號
											L1Teleport.teleport(pc, data[0], data[1], (short) data[2], 6, false);
											break;
										} else {// 會議室
											pc.set_showId(item.getKeyId());// 設置副本編號
											L1Teleport.teleport(pc, data[3], data[4], (short) data[5], 6, false);
											break;
										}
									}
								}
							}
						}
					}
				}
			}

			else if (s.equalsIgnoreCase("room")) { // 部屋を借りる

			} else if (s.equalsIgnoreCase("hall") && (obj instanceof L1MerchantInstance)) { // ホールを借りる

			} else if (s.equalsIgnoreCase("return")) { // 部屋・ホールを返す

			} else if (s.equalsIgnoreCase("enter")) { // 部屋・ホールに入る

			} else if (s.equalsIgnoreCase("openigate")) { // ゲートキーパー / 城門を開ける
				final L1NpcInstance npc = (L1NpcInstance) obj;
				openCloseGate(pc, npc.getNpcTemplate().get_npcId(), true);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("closeigate")) { // ゲートキーパー / 城門を閉める
				final L1NpcInstance npc = (L1NpcInstance) obj;
				openCloseGate(pc, npc.getNpcTemplate().get_npcId(), false);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("askwartime")) { // 近衛兵 /
															// 次の攻城戦いの時間をたずねる
				final L1NpcInstance npc = (L1NpcInstance) obj;
				if (npc.getNpcTemplate().get_npcId() == 60514) { // ケント城近衛兵
					htmldata = makeWarTimeStrings(L1CastleLocation.KENT_CASTLE_ID);
					htmlid = "ktguard7";
				} else if (npc.getNpcTemplate().get_npcId() == 60560) { // オーク近衛兵
					htmldata = makeWarTimeStrings(L1CastleLocation.OT_CASTLE_ID);
					htmlid = "orcguard7";
				} else if (npc.getNpcTemplate().get_npcId() == 60552) { // ウィンダウッド城近衛兵
					htmldata = makeWarTimeStrings(L1CastleLocation.WW_CASTLE_ID);
					htmlid = "wdguard7";
				} else if ((npc.getNpcTemplate().get_npcId() == 60524) || // ギラン街入り口近衛兵(弓)
						(npc.getNpcTemplate().get_npcId() == 60525) || // ギラン街入り口近衛兵
						(npc.getNpcTemplate().get_npcId() == 60529)) { // ギラン城近衛兵
					htmldata = makeWarTimeStrings(L1CastleLocation.GIRAN_CASTLE_ID);
					htmlid = "grguard7";
				} else if (npc.getNpcTemplate().get_npcId() == 70857) { // ハイネ城ハイネガード
					htmldata = makeWarTimeStrings(L1CastleLocation.HEINE_CASTLE_ID);
					htmlid = "heguard7";
				} else if ((npc.getNpcTemplate().get_npcId() == 60530) || // ドワーフ城ドワーフガード
						(npc.getNpcTemplate().get_npcId() == 60531)) {
					htmldata = makeWarTimeStrings(L1CastleLocation.DOWA_CASTLE_ID);
					htmlid = "dcguard7";
				} else if ((npc.getNpcTemplate().get_npcId() == 60533) || // アデン城
																			// ガード
						(npc.getNpcTemplate().get_npcId() == 60534)) {
					htmldata = makeWarTimeStrings(L1CastleLocation.ADEN_CASTLE_ID);
					htmlid = "adguard7";
				} else if (npc.getNpcTemplate().get_npcId() == 81156) { // アデン偵察兵（ディアド要塞）
					htmldata = makeWarTimeStrings(L1CastleLocation.DIAD_CASTLE_ID);
					htmlid = "dfguard3";
				}
			} else if (s.equalsIgnoreCase("inex")) { // 収入/支出の報告を受ける
				// 暫定的に公金をチャットウィンドウに表示させる。
				// メッセージは適当。
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int castle_id = clan.getCastleId();
					if (castle_id != 0) { // 城主クラン
						final L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
						pc.sendPackets(new S_ServerMessage(309, // %0の精算総額は%1アデナです。
								l1castle.getName(), String.valueOf(l1castle.getPublicMoney())));
						htmlid = ""; // ウィンドウを消す
					}
				}
			} else if (s.equalsIgnoreCase("tax")) { // 税率を調節する
				pc.sendPackets(new S_TaxRate(pc.getId()));

			} else if (s.equalsIgnoreCase("withdrawal")) { // 資金を引き出す
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int castle_id = clan.getCastleId();
					if (castle_id != 0) { // 城主クラン
						final L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
						pc.sendPackets(new S_Drawal(pc.getId(), l1castle.getPublicMoney()));
					}
				}
			} else if (s.equalsIgnoreCase("cdeposit")) { // 資金を入金する
				pc.sendPackets(new S_Deposit(pc.getId()));
			} else if (s.equalsIgnoreCase("employ")) { // 傭兵の雇用

			} else if (s.equalsIgnoreCase("arrange")) { // 雇用した傭兵の配置

			} else if (s.equalsIgnoreCase("castlegate")) { // 城門を管理する
				repairGate(pc);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("encw")) { // 武器専門家 / 武器の強化魔法を受ける
				if (pc.getWeapon() == null) {
					pc.sendPackets(new S_ServerMessage(79));
				} else {
					for (final L1ItemInstance item : pc.getInventory().getItems()) {
						if (pc.getWeapon().equals(item)) {
							final L1SkillUse l1skilluse = new L1SkillUse();
							l1skilluse.handleCommands(pc, ENCHANT_WEAPON, item.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
							break;
						}
					}
				}
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("enca")) { // 防具専門家 / 防具の強化魔法を受ける
				final L1ItemInstance item = pc.getInventory().getItemEquipped(2, 2);
				if (item != null) {
					final L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(pc, BLESSED_ARMOR, item.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
				htmlid = ""; // ウィンドウを消す

			} else if (s.equalsIgnoreCase("depositnpc")) { // 「動物を預ける」
				final Object[] petList = pc.getPetList().values().toArray();
				for (final Object petObject : petList) {
					if (petObject instanceof L1PetInstance) { // ペット
						final L1PetInstance pet = (L1PetInstance) petObject;
						pet.collect(true);
						pc.removePet(pet);
						// pc.getPetList().remove(pet.getId());
						pet.deleteMe();
					}
				}
				htmlid = ""; // ウィンドウを消す

			} else if (s.equalsIgnoreCase("withdrawnpc")) { // 「動物を受け取る」
				pc.sendPackets(new S_PetList(objid, pc));

				/*
				 * } else if (s.equalsIgnoreCase("select")) { // 競売掲示板のリストをクリック pc.sendPackets(new S_AuctionBoardRead(objid, s2)); } else if (s.equalsIgnoreCase("map")) { // アジトの位置を確かめる
				 * pc.sendPackets(new S_HouseMap(objid, s2)); } else if (s.equalsIgnoreCase("apply")) { // 競売に参加する final L1Clan clan = WorldClan.get().getClan(pc.getClanname()); if (clan
				 * != null) { if (pc.isCrown() && (pc.getId() == clan.getLeaderId())) { // 君主、かつ、血盟主 if (pc.getLevel() >= 15) { if (clan.getHouseId() == 0) { pc.sendPackets(new
				 * S_ApplyAuction(objid, s2)); } else { pc.sendPackets(new S_ServerMessage(521)); // すでに家を所有しています。 htmlid = ""; // ウィンドウを消す } } else { pc.sendPackets(new
				 * S_ServerMessage(519)); // レベル15未満の君主は競売に参加できません。 htmlid = ""; // ウィンドウを消す } } else { pc.sendPackets(new S_ServerMessage(518)); // この命令は血盟の君主のみが利用できます。 htmlid = ""; //
				 * ウィンドウを消す } } else { pc.sendPackets(new S_ServerMessage(518)); // この命令は血盟の君主のみが利用できます。 htmlid = ""; // ウィンドウを消す }
				 */
			} else if (s.equalsIgnoreCase("open") // ドアを開ける
					|| s.equalsIgnoreCase("close")) { // ドアを閉める
				final L1NpcInstance npc = (L1NpcInstance) obj;
				openCloseDoor(pc, npc, s);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("expel")) { // 外部の人間を追い出す
				final L1NpcInstance npc = (L1NpcInstance) obj;
				expelOtherClan(pc, npc.getNpcTemplate().get_npcId());
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("pay")) { // 税金を納める
				final L1NpcInstance npc = (L1NpcInstance) obj;
				htmldata = makeHouseTaxStrings(pc, npc);
				htmlid = "agpay";
			} else if (s.equalsIgnoreCase("payfee")) { // 税金を納める
				final L1NpcInstance npc = (L1NpcInstance) obj;
				payFee(pc, npc);
				htmlid = "";
			} else if (s.equalsIgnoreCase("name")) { // 家の名前を決める
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int houseId = clan.getHouseId();
					if (houseId != 0) {
						final L1House house = HouseReading.get().getHouseTable(houseId);
						final int keeperId = house.getKeeperId();
						final L1NpcInstance npc = (L1NpcInstance) obj;
						if (npc.getNpcTemplate().get_npcId() == keeperId) {
							pc.setTempID(houseId); // アジトIDを保存しておく
							pc.sendPackets(new S_Message_YN(512)); // 家の名前は？
						}
					}
				}
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("rem")) { // 家の中の家具をすべて取り除く
			} else if (s.equalsIgnoreCase("tel0") // テレポートする(倉庫)
					|| s.equalsIgnoreCase("tel1") // テレポートする(ペット保管所)
					|| s.equalsIgnoreCase("tel2") // テレポートする(贖罪の使者)
					|| s.equalsIgnoreCase("tel3")) { // テレポートする(ギラン市場)
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int houseId = clan.getHouseId();
					if (houseId != 0) {
						final L1House house = HouseReading.get().getHouseTable(houseId);
						final int keeperId = house.getKeeperId();
						final L1NpcInstance npc = (L1NpcInstance) obj;
						if (npc.getNpcTemplate().get_npcId() == keeperId) {
							int[] loc = new int[3];
							if (s.equalsIgnoreCase("tel0")) {
								loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
							} else if (s.equalsIgnoreCase("tel1")) {
								loc = L1HouseLocation.getHouseTeleportLoc(houseId, 1);
							} else if (s.equalsIgnoreCase("tel2")) {
								loc = L1HouseLocation.getHouseTeleportLoc(houseId, 2);
							} else if (s.equalsIgnoreCase("tel3")) {
								loc = L1HouseLocation.getHouseTeleportLoc(houseId, 3);
							}
							L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
						}
					}
				}
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("upgrade")) { // 地下アジトを作る
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int houseId = clan.getHouseId();
					if (houseId != 0) {
						final L1House house = HouseReading.get().getHouseTable(houseId);
						final int keeperId = house.getKeeperId();
						final L1NpcInstance npc = (L1NpcInstance) obj;
						if (npc.getNpcTemplate().get_npcId() == keeperId) {
							if (pc.isCrown() && (pc.getId() == clan.getLeaderId())) { // 君主、かつ、血盟主
								if (house.isPurchaseBasement()) {
									// 既に地下アジトを所有しています。
									pc.sendPackets(new S_ServerMessage(1135));
								} else {
									if (pc.getInventory().consumeItem(L1ItemId.ADENA, 5000000)) {
										house.setPurchaseBasement(true);
										HouseReading.get().updateHouse(house); // DBに書き込み
										// 地下アジトが生成されました。
										pc.sendPackets(new S_ServerMessage(1099));
									} else {
										// 189 \f1金幣不足。
										pc.sendPackets(new S_ServerMessage(189));
									}
								}
							} else {
								// この命令は血盟の君主のみが利用できます。
								pc.sendPackets(new S_ServerMessage(518));
							}
						}
					}
				}
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("hall") && (obj instanceof L1HousekeeperInstance)) { // 地下アジトにテレポートする
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int houseId = clan.getHouseId();
					if (houseId != 0) {
						final L1House house = HouseReading.get().getHouseTable(houseId);
						final int keeperId = house.getKeeperId();
						final L1NpcInstance npc = (L1NpcInstance) obj;
						if (npc.getNpcTemplate().get_npcId() == keeperId) {
							if (house.isPurchaseBasement()) {
								int[] loc = new int[3];
								loc = L1HouseLocation.getBasementLoc(houseId);
								L1Teleport.teleport(pc, loc[0], loc[1], (short) (loc[2]), 5, true);
							} else {
								// 地下アジトがないため、テレポートできません。
								pc.sendPackets(new S_ServerMessage(1098));
							}
						}
					}
				}
				htmlid = ""; // ウィンドウを消す
			}

			// ElfAttr:0.無属性,1.地属性,2.火属性,4.水属性,8.風属性
			else if (s.equalsIgnoreCase("fire")) // エルフの属性変更「火の系列を習う」
			{
				if (pc.isElf()) {
					if (pc.getElfAttr() != 0) {
						return;
					}
					pc.setElfAttr(2);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 1)); // 体の隅々に火の精霊力が染みこんできます。
					htmlid = ""; // ウィンドウを消す
				}
			} else if (s.equalsIgnoreCase("water")) { // エルフの属性変更「水の系列を習う」
				if (pc.isElf()) {
					if (pc.getElfAttr() != 0) {
						return;
					}
					pc.setElfAttr(4);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 2)); // 体の隅々に水の精霊力が染みこんできます。
					htmlid = ""; // ウィンドウを消す
				}
			} else if (s.equalsIgnoreCase("air")) { // エルフの属性変更「風の系列を習う」
				if (pc.isElf()) {
					if (pc.getElfAttr() != 0) {
						return;
					}
					pc.setElfAttr(8);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 3)); // 体の隅々に風の精霊力が染みこんできます。
					htmlid = ""; // ウィンドウを消す
				}
			} else if (s.equalsIgnoreCase("earth")) { // エルフの属性変更「地の系列を習う」
				if (pc.isElf()) {
					if (pc.getElfAttr() != 0) {
						return;
					}
					pc.setElfAttr(1);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 4)); // 体の隅々に地の精霊力が染みこんできます。
					htmlid = ""; // ウィンドウを消す
				}
			} else if (s.equalsIgnoreCase("init")) { // エルフの属性変更「精霊力を除去する」
				if (pc.isElf()) {
					if (pc.getElfAttr() == 0) {
						return;
					}
					for (int cnt = 129; cnt <= 176; cnt++) {// 全エルフ魔法をチェック
						final L1Skills l1skills1 = SkillsTable.get().getTemplate(cnt);
						final int skill_attr = l1skills1.getAttr();
						if (skill_attr != 0) {// 無属性魔法以外のエルフ魔法をDBから削除する
							CharSkillReading.get().spellLost(pc.getId(), l1skills1.getSkillId());
						}
					}
					// エレメンタルプロテクションによって上昇している属性防御をリセット
					if (pc.hasSkillEffect(ELEMENTAL_PROTECTION)) {
						pc.removeSkillEffect(ELEMENTAL_PROTECTION);
					}
					pc.sendPackets(new S_DelSkill(pc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 252, 252, 255, 0, 0, 0, 0, 0, 0)); // 無属性魔法以外のエルフ魔法を魔法ウィンドウから削除する
					pc.setElfAttr(0);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_ServerMessage(678));
					htmlid = ""; // ウィンドウを消す
				}
			} else if (s.equalsIgnoreCase("exp")) { // 「経験値を回復する」
				if (pc.getExpRes() == 1) {
					int cost = 0;
					final int level = pc.getLevel();
					final int lawful = pc.getLawful();
					if (level < 45) {
						cost = level * level * 100;
					} else {
						cost = level * level * 200;
					}
					if (lawful >= 0) {
						cost = (cost / 2);
					}
					pc.sendPackets(new S_Message_YN(738, String.valueOf(cost))); // 経験値を回復するには%0のアデナが必要です。経験値を回復しますか？
				} else {
					pc.sendPackets(new S_ServerMessage(739)); // 今は経験値を回復することができません。
					htmlid = ""; // ウィンドウを消す
				}

			} else if (s.equalsIgnoreCase("ent")) {
				watchUb(pc, 50038);
				// 「お化け屋敷に入る」
				// 「アルティメット バトルに参加する」または
				// 「観覧モードで闘技場に入る」
				// 「ステータス再分配」
				final int npcId = ((L1NpcInstance) obj).getNpcId();
				if ((npcId == 80085) || (npcId == 80086) || (npcId == 80087)) {
					htmlid = enterHauntedHouse(pc);

				} else if ((npcId == 50038) || (npcId == 50042) || (npcId == 50029) || (npcId == 50019) || (npcId == 50062)) { // 副管理人の場合は観戦
					htmlid = watchUb(pc, npcId);

				} else {
					htmlid = enterUb(pc, npcId);
				}
			} else if (s.equalsIgnoreCase("par")) { // UB関連「アルティメット バトルに参加する」
													// 副管理人経由
				htmlid = enterUb(pc, ((L1NpcInstance) obj).getNpcId());
			} else if (s.equalsIgnoreCase("info")) { // 「情報を確認する」「競技情報を確認する」
				final int npcId = ((L1NpcInstance) obj).getNpcId();
				if ((npcId == 80085) || (npcId == 80086) || (npcId == 80087)) {
				} else {
					htmlid = "colos2";
				}
			} else if (s.equalsIgnoreCase("sco")) { // UB関連「高得点者一覧を確認する」
				htmldata = new String[10];
				htmlid = "colos3";
			}

			else if (s.equalsIgnoreCase("haste")) { // ヘイスト師
				final L1NpcInstance l1npcinstance = (L1NpcInstance) obj;
				final int npcid = l1npcinstance.getNpcTemplate().get_npcId();
				if (npcid == 70514) {
					pc.sendPackets(new S_ServerMessage(183));
					pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1600));
					pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
					pc.sendPacketsX8(new S_SkillSound(pc.getId(), 755));
					pc.setMoveSpeed(1);
					pc.setSkillEffect(STATUS_HASTE, 1600 * 1000);
					htmlid = ""; // ウィンドウを消す
				}
			}
			// 変身専門家
			else if (s.equalsIgnoreCase("skeleton nbmorph")) {
				poly(client, 2374);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("lycanthrope nbmorph")) {
				poly(client, 3874);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("shelob nbmorph")) {
				poly(client, 95);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("ghoul nbmorph")) {
				poly(client, 3873);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("ghast nbmorph")) {
				poly(client, 3875);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("atuba orc nbmorph")) {
				poly(client, 3868);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("skeleton axeman nbmorph")) {
				poly(client, 2376);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("troll nbmorph")) {
				poly(client, 3878);
				htmlid = ""; // ウィンドウを消す
			}
			// 長老 ノナメ
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71038) {
				// 「手紙を受け取る」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(41060, 1); // ノナメの推薦書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0が%1をくれました。
					htmlid = "orcfnoname9";
				}
				// 「調査をやめます」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41060, 1)) {
						htmlid = "orcfnoname11";
					}
				}
			}
			// ドゥダ-マラ ブウ
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71039) {
				// 「わかりました、その場所に送ってください」
				if (s.equalsIgnoreCase("teleportURL")) {
					htmlid = "orcfbuwoo2";
				}
			}
			// 調査団長 アトゥバ ノア
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71040) {
				// 「やってみます」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(41065, 1); // 調査団の証書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0が%1をくれました。
					htmlid = "orcfnoa4";
				}
				// 「調査をやめます」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41065, 1)) {
						htmlid = "orcfnoa7";
					}
				}
			}
			// ネルガ フウモ
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71041) {
				// 「調査をします」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(41064, 1); // 調査団の証書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0が%1をくれました。
					htmlid = "orcfhuwoomo4";
				}
				// 「調査をやめます」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41064, 1)) {
						htmlid = "orcfhuwoomo6";
					}
				}
			}
			// ネルガ バクモ
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71042) {
				// 「調査をします」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(41062, 1); // 調査団の証書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0が%1をくれました。
					htmlid = "orcfbakumo4";
				}
				// 「調査をやめます」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41062, 1)) {
						htmlid = "orcfbakumo6";
					}
				}
			}
			// ドゥダ-マラ ブカ
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71043) {
				// 「調査をします」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(41063, 1); // 調査団の証書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0が%1をくれました。
					htmlid = "orcfbuka4";
				}
				// 「調査をやめます」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41063, 1)) {
						htmlid = "orcfbuka6";
					}
				}
			}
			// ドゥダ-マラ カメ
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71044) {
				// 「調査をします」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(41061, 1); // 調査団の証書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0が%1をくれました。
					htmlid = "orcfkame4";
				}
				// 「調査をやめます」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41061, 1)) {
						htmlid = "orcfkame6";
					}
				}
			}
			// ポワール
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71078) {
				// 「入ってみる」
				if (s.equalsIgnoreCase("teleportURL")) {
					htmlid = "usender2";
				}
			}
			// 治安団長アミス
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71080) {
				// 「私がお手伝いしましょう」
				if (s.equalsIgnoreCase("teleportURL")) {
					htmlid = "amisoo2";
				}
			}
			// 空間の歪み
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80048) {
				// 「やめる」
				if (s.equalsIgnoreCase("2")) {
					htmlid = ""; // ウィンドウを消す
				}
			}
			// 揺らぐ者
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80049) {
				// 「バルログの意志を迎え入れる」
				if (s.equalsIgnoreCase("1")) {
					if (pc.getKarma() <= -10000000) {
						pc.setKarma(1000000);
						// バルログの笑い声が脳裏を強打します。
						pc.sendPackets(new S_ServerMessage(1078));
						htmlid = "betray13";
					}
				}
			}
			// ヤヒの執政官
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80050) {
				// 「私の霊魂はヤヒ様へ…」
				if (s.equalsIgnoreCase("1")) {
					htmlid = "meet105";
				}
				// 「私の霊魂をかけてヤヒ様に忠誠を誓います…」
				else if (s.equalsIgnoreCase("2")) {
					if (pc.getInventory().checkItem(40718)) { // ブラッドクリスタルの欠片
						htmlid = "meet106";
					} else {
						htmlid = "meet110";
					}
				}
				// 「ブラッドクリスタルの欠片を1個捧げます」
				else if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().consumeItem(40718, 1)) {
						pc.addKarma((int) (-100 * ConfigRate.RATE_KARMA));
						// ヤヒの姿がだんだん近くに感じられます。
						pc.sendPackets(new S_ServerMessage(1079));
						htmlid = "meet107";
					} else {
						htmlid = "meet104";
					}
				}
				// 「ブラッドクリスタルの欠片を10個捧げます」
				else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().consumeItem(40718, 10)) {
						pc.addKarma((int) (-1000 * ConfigRate.RATE_KARMA));
						// ヤヒの姿がだんだん近くに感じられます。
						pc.sendPackets(new S_ServerMessage(1079));
						htmlid = "meet108";
					} else {
						htmlid = "meet104";
					}
				}
				// 「ブラッドクリスタルの欠片を100個捧げます」
				else if (s.equalsIgnoreCase("c")) {
					if (pc.getInventory().consumeItem(40718, 100)) {
						pc.addKarma((int) (-10000 * ConfigRate.RATE_KARMA));
						// ヤヒの姿がだんだん近くに感じられます。
						pc.sendPackets(new S_ServerMessage(1079));
						htmlid = "meet109";
					} else {
						htmlid = "meet104";
					}
				}
				// 「ヤヒ様に会わせてください」
				else if (s.equalsIgnoreCase("d")) {
					if (pc.getInventory().checkItem(40615) // 影の神殿2階の鍵
							|| pc.getInventory().checkItem(40616)) { // 影の神殿3階の鍵
						htmlid = "";
					} else {
						L1Teleport.teleport(pc, 32683, 32895, (short) 608, 5, true);
					}
				}
			}
			// ヤヒの軍師
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80052) {
				// 私に力をくださいますよう・・・
				if (s.equalsIgnoreCase("a")) {
					if (pc.hasSkillEffect(DE_LV30)) {
						pc.removeSkillEffect(DE_LV30);
					}
					if (pc.hasSkillEffect(CKEW_LV50)) {
						pc.removeSkillEffect(CKEW_LV50);
					}
					if (pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
						// 沒有任何事情發生
						pc.sendPackets(new S_ServerMessage(79));
					} else {
						pc.setSkillEffect(STATUS_CURSE_BARLOG, 1500 * 1000);
						pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7246));
						// pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(),
						// 1020));
						// pc.sendPacketsX8(new S_SkillSound(pc.getId(), 750));
						pc.sendPackets(new S_ServerMessage(1127));
					}
				}
			}
			// ヤヒの鍛冶屋
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80053) {
				final int karmaLevel = pc.getKarmaLevel();
				// 「材料すべてを用意できました」
				if (s.equalsIgnoreCase("a")) {
					// バルログのツーハンド ソード / ヤヒの鍛冶屋
					int aliceMaterialId = 0;
					final int[] aliceMaterialIdList = { 40991, 196, 197, 198, 199, 200, 201, 202, 203 };
					for (final int id : aliceMaterialIdList) {
						if (pc.getInventory().checkItem(id)) {
							aliceMaterialId = id;
							break;
						}
					}
					if (aliceMaterialId == 0) {
						htmlid = "alice_no";
					} else if (aliceMaterialId == 40991) {
						if (karmaLevel <= -1) {
							materials = new int[] { 40995, 40718, 40991 };
							counts = new int[] { 100, 100, 1 };
							createitem = new int[] { 196 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_1";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "aliceyet";
						}
					} else if (aliceMaterialId == 196) {
						if (karmaLevel <= -2) {
							materials = new int[] { 40997, 40718, 196 };
							counts = new int[] { 100, 100, 1 };
							createitem = new int[] { 197 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_2";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_1";
						}
					} else if (aliceMaterialId == 197) {
						if (karmaLevel <= -3) {
							materials = new int[] { 40990, 40718, 197 };
							counts = new int[] { 100, 100, 1 };
							createitem = new int[] { 198 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_3";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_2";
						}
					} else if (aliceMaterialId == 198) {
						if (karmaLevel <= -4) {
							materials = new int[] { 40994, 40718, 198 };
							counts = new int[] { 50, 100, 1 };
							createitem = new int[] { 199 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_4";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_3";
						}
					} else if (aliceMaterialId == 199) {
						if (karmaLevel <= -5) {
							materials = new int[] { 40993, 40718, 199 };
							counts = new int[] { 50, 100, 1 };
							createitem = new int[] { 200 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_5";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_4";
						}
					} else if (aliceMaterialId == 200) {
						if (karmaLevel <= -6) {
							materials = new int[] { 40998, 40718, 200 };
							counts = new int[] { 50, 100, 1 };
							createitem = new int[] { 201 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_6";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_5";
						}
					} else if (aliceMaterialId == 201) {
						if (karmaLevel <= -7) {
							materials = new int[] { 40996, 40718, 201 };
							counts = new int[] { 10, 100, 1 };
							createitem = new int[] { 202 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_7";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_6";
						}
					} else if (aliceMaterialId == 202) {
						if (karmaLevel <= -8) {
							materials = new int[] { 40992, 40718, 202 };
							counts = new int[] { 10, 100, 1 };
							createitem = new int[] { 203 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_8";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_7";
						}
					} else if (aliceMaterialId == 203) {
						htmlid = "alice_8";
					}
				}
			}

			/*
			 * // ヤヒの補佐官 else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80055) { final L1NpcInstance npc = (L1NpcInstance) obj; htmlid = getYaheeAmulet(pc, npc, s); }
			 */

			// 業の管理者
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80056) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				if (pc.getKarma() <= -10000000) {
					getBloodCrystalByKarma(pc, npc, s);
				}
				htmlid = "";
			}
			// 次元の扉(バルログの部屋)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80063) {
				// 「中に入る」
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(40921)) { // 元素の支配者
						L1Teleport.teleport(pc, 32674, 32832, (short) 603, 2, true);
					} else {
						htmlid = "gpass02";
					}
				}
			}
			// バルログの執政官
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80064) {
				// 「私の永遠の主はバルログ様だけです…」
				if (s.equalsIgnoreCase("1")) {
					htmlid = "meet005";
				}
				// 「私の霊魂をかけてバルログ様に忠誠を誓います…」
				else if (s.equalsIgnoreCase("2")) {
					if (pc.getInventory().checkItem(40678)) { // ソウルクリスタルの欠片
						htmlid = "meet006";
					} else {
						htmlid = "meet010";
					}
				}
				// 「ソウルクリスタルの欠片を1個捧げます」
				else if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().consumeItem(40678, 1)) {
						pc.addKarma((int) (100 * ConfigRate.RATE_KARMA));
						// バルログの笑い声が脳裏を強打します。
						pc.sendPackets(new S_ServerMessage(1078));
						htmlid = "meet007";
					} else {
						htmlid = "meet004";
					}
				}
				// 「ソウルクリスタルの欠片を10個捧げます」
				else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().consumeItem(40678, 10)) {
						pc.addKarma((int) (1000 * ConfigRate.RATE_KARMA));
						// バルログの笑い声が脳裏を強打します。
						pc.sendPackets(new S_ServerMessage(1078));
						htmlid = "meet008";
					} else {
						htmlid = "meet004";
					}
				}
				// 「ソウルクリスタルの欠片を100個捧げます」
				else if (s.equalsIgnoreCase("c")) {
					if (pc.getInventory().consumeItem(40678, 100)) {
						pc.addKarma((int) (10000 * ConfigRate.RATE_KARMA));
						// バルログの笑い声が脳裏を強打します。
						pc.sendPackets(new S_ServerMessage(1078));
						htmlid = "meet009";
					} else {
						htmlid = "meet004";
					}
				}
				// 「バルログ様に会わせてください」
				else if (s.equalsIgnoreCase("d")) {
					if (pc.getInventory().checkItem(40909) // 地の通行証
							|| pc.getInventory().checkItem(40910) // 水の通行証
							|| pc.getInventory().checkItem(40911) // 火の通行証
							|| pc.getInventory().checkItem(40912) // 風の通行証
							|| pc.getInventory().checkItem(40913) // 地の印章
							|| pc.getInventory().checkItem(40914) // 水の印章
							|| pc.getInventory().checkItem(40915) // 火の印章
							|| pc.getInventory().checkItem(40916) // 風の印章
							|| pc.getInventory().checkItem(40917) // 地の支配者
							|| pc.getInventory().checkItem(40918) // 水の支配者
							|| pc.getInventory().checkItem(40919) // 火の支配者
							|| pc.getInventory().checkItem(40920) // 風の支配者
							|| pc.getInventory().checkItem(40921)) { // 元素の支配者
						htmlid = "";
					} else {
						L1Teleport.teleport(pc, 32674, 32832, (short) 602, 2, true);
					}
				}
			}
			// 揺らめく者
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80066) {
				// 「カヘルの意志を受け入れる」
				if (s.equalsIgnoreCase("1")) {
					if (pc.getKarma() >= 10000000) {
						pc.setKarma(-1000000);
						// ヤヒの姿がだんだん近くに感じられます。
						pc.sendPackets(new S_ServerMessage(1079));
						htmlid = "betray03";
					}
				}
			}

			// バルログの補佐官
			/*
			 * else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80071) { final L1NpcInstance npc = (L1NpcInstance) obj; htmlid = getBarlogEarring(pc, npc, s); }
			 */

			// バルログの軍師
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80073) {
				// 私に力をくださいますよう・・・
				if (s.equalsIgnoreCase("a")) {
					if (pc.hasSkillEffect(DE_LV30)) {
						pc.removeSkillEffect(DE_LV30);
					}
					if (pc.hasSkillEffect(CKEW_LV50)) {
						pc.removeSkillEffect(CKEW_LV50);
					}
					if (pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
						// 沒有任何事情發生
						pc.sendPackets(new S_ServerMessage(79));

					} else {
						pc.setSkillEffect(STATUS_CURSE_YAHEE, 1020 * 1000);
						pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7247));
						// pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(),
						// 1020));
						// pc.sendPacketsX8(new S_SkillSound(pc.getId(), 750));
						pc.sendPackets(new S_ServerMessage(1127));
					}
				}
			}
			// バルログの鍛冶屋
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80072) {
				final int karmaLevel = pc.getKarmaLevel();
				if (s.equalsIgnoreCase("0")) {
					htmlid = "lsmitha";
				} else if (s.equalsIgnoreCase("1")) {
					htmlid = "lsmithb";
				} else if (s.equalsIgnoreCase("2")) {
					htmlid = "lsmithc";
				} else if (s.equalsIgnoreCase("3")) {
					htmlid = "lsmithd";
				} else if (s.equalsIgnoreCase("4")) {
					htmlid = "lsmithe";
				} else if (s.equalsIgnoreCase("5")) {
					htmlid = "lsmithf";
				} else if (s.equalsIgnoreCase("6")) {
					htmlid = "";
				} else if (s.equalsIgnoreCase("7")) {
					htmlid = "lsmithg";
				} else if (s.equalsIgnoreCase("8")) {
					htmlid = "lsmithh";
				}
				// ヤヒのシャツ / バルログの鍛冶屋
				else if (s.equalsIgnoreCase("a") && (karmaLevel >= 1)) {
					materials = new int[] { 20158, 40669, 40678 };
					counts = new int[] { 1, 50, 100 };
					createitem = new int[] { 20083 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithaa";
				}
				// ヤヒのアーマー / バルログの鍛冶屋
				else if (s.equalsIgnoreCase("b") && (karmaLevel >= 2)) {
					materials = new int[] { 20144, 40672, 40678 };
					counts = new int[] { 1, 50, 100 };
					createitem = new int[] { 20131 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithbb";
				}
				// ヤヒのアーマー / バルログの鍛冶屋
				else if (s.equalsIgnoreCase("c") && (karmaLevel >= 3)) {
					materials = new int[] { 20075, 40671, 40678 };
					counts = new int[] { 1, 50, 100 };
					createitem = new int[] { 20069 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithcc";
				}
				// ヤヒのグローブ / バルログの鍛冶屋
				else if (s.equalsIgnoreCase("d") && (karmaLevel >= 4)) {
					materials = new int[] { 20183, 40674, 40678 };
					counts = new int[] { 1, 20, 100 };
					createitem = new int[] { 20179 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithdd";
				}
				// ヤヒのブーツ / バルログの鍛冶屋
				else if (s.equalsIgnoreCase("e") && (karmaLevel >= 5)) {
					materials = new int[] { 20190, 40674, 40678 };
					counts = new int[] { 1, 40, 100 };
					createitem = new int[] { 20209 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithee";
				}
				// ヤヒのリング / バルログの鍛冶屋
				else if (s.equalsIgnoreCase("f") && (karmaLevel >= 6)) {
					materials = new int[] { 20078, 40674, 40678 };
					counts = new int[] { 1, 5, 100 };
					createitem = new int[] { 20290 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithff";
				}
				// ヤヒのアミュレット / バルログの鍛冶屋
				else if (s.equalsIgnoreCase("g") && (karmaLevel >= 7)) {
					materials = new int[] { 20078, 40670, 40678 };
					counts = new int[] { 1, 1, 100 };
					createitem = new int[] { 20261 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithgg";
				}
				// ヤヒのヘルム / バルログの鍛冶屋
				else if (s.equalsIgnoreCase("h") && (karmaLevel >= 8)) {
					materials = new int[] { 40719, 40673, 40678 };
					counts = new int[] { 1, 1, 100 };
					createitem = new int[] { 20031 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithhh";
				}
			}
			// 業の管理者
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80074) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				if (pc.getKarma() >= 10000000) {
					getSoulCrystalByKarma(pc, npc, s);
				}
				htmlid = "";
			}
			// アルフォンス
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80057) {
				htmlid = karmaLevelToHtmlId(pc.getKarmaLevel());
				htmldata = new String[] { String.valueOf(pc.getKarmaPercent()) };
			}
			// 次元の扉(土風水火)
			else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80059) || (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80060)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80061) || (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80062)) {
				htmlid = talkToDimensionDoor(pc, (L1NpcInstance) obj, s);
			}

			// 最近の物価について
			// パンドラ、コルド、バルシム、メリン、グレン
			else if (s.equalsIgnoreCase("pandora6") || s.equalsIgnoreCase("cold6") || s.equalsIgnoreCase("balsim3") || s.equalsIgnoreCase("mellin3") || s.equalsIgnoreCase("glen3")) {
				htmlid = s;
				final int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
				final int taxRatesCastle = L1CastleLocation.getCastleTaxRateByNpcId(npcid);
				htmldata = new String[] { String.valueOf(taxRatesCastle) };
			}
			// タウンマスター（この村の住民に登録する）
			else if (s.equalsIgnoreCase("set")) {
				if (obj instanceof L1NpcInstance) {
					final int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
					final int town_id = L1TownLocation.getTownIdByNpcid(npcid);

					if ((town_id >= 1) && (town_id <= 10)) {
						if (pc.getHomeTownId() == -1) {
							// \f1新しく住民登録を行なうには時間がかかります。時間を置いてからまた登録してください。
							pc.sendPackets(new S_ServerMessage(759));
							htmlid = "";
						} else if (pc.getHomeTownId() > 0) {
							// 既に登録してる
							if (pc.getHomeTownId() != town_id) {
								final L1Town town = TownReading.get().getTownTable(pc.getHomeTownId());
								if (town != null) {
									// 現在、あなたが住民登録している場所は%0です。
									pc.sendPackets(new S_ServerMessage(758, town.get_name()));
								}
								htmlid = "";
							} else {
								// ありえない？
								htmlid = "";
							}
						} else if (pc.getHomeTownId() == 0) {
							// 登録
							if (pc.getLevel() < 10) {
								// \f1住民登録ができるのはレベル10以上のキャラクターです。
								pc.sendPackets(new S_ServerMessage(757));
								htmlid = "";
							} else {
								final int level = pc.getLevel();
								final int cost = level * level * 10;
								if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
									pc.setHomeTownId(town_id);
									pc.setContribution(0); // 念のため
									pc.save();
								} else {
									// アデナが不足しています。
									pc.sendPackets(new S_ServerMessage(337, "$4"));
								}
								htmlid = "";
							}
						}
					}
				}
			}
			// タウンマスター（住民登録を取り消す）
			else if (s.equalsIgnoreCase("clear")) {
				if (obj instanceof L1NpcInstance) {
					final int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
					final int town_id = L1TownLocation.getTownIdByNpcid(npcid);
					if (town_id > 0) {
						if (pc.getHomeTownId() > 0) {
							if (pc.getHomeTownId() == town_id) {
								pc.setHomeTownId(-1);
								pc.setContribution(0); // 貢献度クリア
								pc.save();
							} else {
								// \f1あなたは他の村の住民です。
								pc.sendPackets(new S_ServerMessage(756));
							}
						}
						htmlid = "";
					}
				}
			}
			// タウンマスター（村の村長が誰かを聞く）
			else if (s.equalsIgnoreCase("ask")) {
				if (obj instanceof L1NpcInstance) {
					final int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
					final int town_id = L1TownLocation.getTownIdByNpcid(npcid);

					if ((town_id >= 1) && (town_id <= 10)) {
						final L1Town town = TownReading.get().getTownTable(town_id);
						final String leader = town.get_leader_name();
						if ((leader != null) && (leader.length() != 0)) {
							htmlid = "owner";
							htmldata = new String[] { leader };
						} else {
							htmlid = "noowner";
						}
					}
				}
			}
			// タウンアドバイザー
			else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70534) || (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70556)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70572) || (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70631)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70663) || (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70761)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70788) || (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70806)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70830) || (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70876)) {
				// タウンアドバイザー（収入に関する報告）
				if (s.equalsIgnoreCase("r")) {
					if (obj instanceof L1NpcInstance) {
					}
				}
				// タウンアドバイザー（税率変更）
				else if (s.equalsIgnoreCase("t")) {

				}
				// タウンアドバイザー（報酬をもらう）
				else if (s.equalsIgnoreCase("c")) {

				}
			}

			// 治療師（歌う島の中：ＨＰのみ回復）
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70512) {
				// 治療を受ける("fullheal"でリクエストが来ることはあるのか？)
				if (s.equalsIgnoreCase("0") || s.equalsIgnoreCase("fullheal")) {
					final int hp = _random.nextInt(21) + 70;
					pc.setCurrentHp(pc.getCurrentHp() + hp);

					// 你覺得舒服多了訊息
					pc.sendPackets(new S_PacketBoxHpMsg());
					pc.sendPackets(new S_SkillSound(pc.getId(), 830));

					pc.sendPackets(new S_HPUpdate(pc));
					htmlid = ""; // ウィンドウを消す
				}
			}
			// 治療師（訓練場：HPMP回復）
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71037) {
				if (s.equalsIgnoreCase("0")) {
					pc.setCurrentHp(pc.getMaxHp());
					pc.setCurrentMp(pc.getMaxMp());

					// 你覺得舒服多了訊息
					pc.sendPackets(new S_PacketBoxHpMsg());
					pc.sendPackets(new S_SkillSound(pc.getId(), 830));

					pc.sendPackets(new S_HPUpdate(pc));
					pc.sendPackets(new S_MPUpdate(pc));
				}
			}
			// 治療師（西部）
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71030) {
				if (s.equalsIgnoreCase("fullheal")) {
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 5)) { // check
						pc.getInventory().consumeItem(L1ItemId.ADENA, 5); // del
						pc.setCurrentHp(pc.getMaxHp());
						pc.setCurrentMp(pc.getMaxMp());

						// 你覺得舒服多了訊息
						pc.sendPackets(new S_PacketBoxHpMsg());
						pc.sendPackets(new S_SkillSound(pc.getId(), 830));

						pc.sendPackets(new S_HPUpdate(pc));
						pc.sendPackets(new S_MPUpdate(pc));

						if (pc.isInParty()) { // パーティー中
							pc.getParty().updateMiniHP(pc);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(337, "$4")); // アデナが不足しています。
					}
				}
			}
			// キャンセレーション師
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71002) {
				// キャンセレーション魔法をかけてもらう
				if (s.equalsIgnoreCase("0")) {
					if (pc.getLevel() <= 13) {
						final L1SkillUse skillUse = new L1SkillUse();
						skillUse.handleCommands(pc, CANCELLATION, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_NPCBUFF, (L1NpcInstance) obj);
						htmlid = ""; // ウィンドウを消す
					}
				}
			}
			// ケスキン(歌う島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71025) {
				if (s.equalsIgnoreCase("0")) {
					final int[] item_ids = { 41225, };
					final int[] item_amounts = { 1, };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
					}
					htmlid = "jpe0083";
				}
			}
			// ルケイン(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71055) {
				// アイテムを受け取る
				if (s.equalsIgnoreCase("0")) {
					final int[] item_ids = { 40701, };
					final int[] item_amounts = { 1, };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
					}
					pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 1);
					htmlid = "lukein8";
				} else if (s.equalsIgnoreCase("2")) {
					htmlid = "lukein12";
					pc.getQuest().set_step(L1PcQuest.QUEST_RESTA, 3);
				}
			}
			// 小さな箱-1番目
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71063) {
				if (s.equalsIgnoreCase("0")) {
					materials = new int[] { 40701 }; // 小さな宝の地図
					counts = new int[] { 1 };
					createitem = new int[] { 40702 }; // 小さな袋
					createcount = new int[] { 1 };
					htmlid = "maptbox1";
					pc.getQuest().set_end(L1PcQuest.QUEST_TBOX1);
					final int[] nextbox = { 1, 2, 3 };
					final int pid = _random.nextInt(nextbox.length);
					final int nb = nextbox[pid];
					if (nb == 1) { // b地点
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 2);
					} else if (nb == 2) { // c地点
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 3);
					} else if (nb == 3) { // d地点
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 4);
					}
				}
			}
			// 小さな箱-2番目
			else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71064) || (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71065)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71066)) {
				if (s.equalsIgnoreCase("0")) {
					materials = new int[] { 40701 }; // 小さな宝の地図
					counts = new int[] { 1 };
					createitem = new int[] { 40702 }; // 小さな袋
					createcount = new int[] { 1 };
					htmlid = "maptbox1";
					pc.getQuest().set_end(L1PcQuest.QUEST_TBOX2);
					final int[] nextbox2 = { 1, 2, 3, 4, 5, 6 };
					final int pid = _random.nextInt(nextbox2.length);
					final int nb2 = nextbox2[pid];
					if (nb2 == 1) { // e地点
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 5);
					} else if (nb2 == 2) { // f地点
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 6);
					} else if (nb2 == 3) { // g地点
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 7);
					} else if (nb2 == 4) { // h地点
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 8);
					} else if (nb2 == 5) { // i地点
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 9);
					} else if (nb2 == 6) { // j地点
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 10);
					}
				}
			}
			// シミズ(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71056) {
				// 息子を捜す
				if (s.equalsIgnoreCase("a")) {
					pc.getQuest().set_step(L1PcQuest.QUEST_SIMIZZ, 1);
					htmlid = "SIMIZZ7";
				} else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().checkItem(40661) && pc.getInventory().checkItem(40662) && pc.getInventory().checkItem(40663)) {
						htmlid = "SIMIZZ8";
						pc.getQuest().set_step(L1PcQuest.QUEST_SIMIZZ, 2);
						materials = new int[] { 40661, 40662, 40663 };
						counts = new int[] { 1, 1, 1 };
						createitem = new int[] { 20044 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "SIMIZZ9";
					}
				} else if (s.equalsIgnoreCase("d")) {
					htmlid = "SIMIZZ12";
					pc.getQuest().set_step(L1PcQuest.QUEST_SIMIZZ, L1PcQuest.QUEST_END);
				}
			}
			// ドイル(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71057) {
				// ラッシュについて聞く
				if (s.equalsIgnoreCase("3")) {
					htmlid = "doil4";
				} else if (s.equalsIgnoreCase("6")) {
					htmlid = "doil6";
				} else if (s.equalsIgnoreCase("1")) {
					if (pc.getInventory().checkItem(40714)) {
						htmlid = "doil8";
						materials = new int[] { 40714 };
						counts = new int[] { 1 };
						createitem = new int[] { 40647 };
						createcount = new int[] { 1 };
						pc.getQuest().set_step(L1PcQuest.QUEST_DOIL, L1PcQuest.QUEST_END);
					} else {
						htmlid = "doil7";
					}
				}
			}
			// ルディアン(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71059) {
				// ルディアンの頼みを受け入れる
				if (s.equalsIgnoreCase("A")) {
					htmlid = "rudian6";
					final int[] item_ids = { 40700 };
					final int[] item_amounts = { 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
					}
					pc.getQuest().set_step(L1PcQuest.QUEST_RUDIAN, 1);
				} else if (s.equalsIgnoreCase("B")) {
					if (pc.getInventory().checkItem(40710)) {
						htmlid = "rudian8";
						materials = new int[] { 40700, 40710 };
						counts = new int[] { 1, 1 };
						createitem = new int[] { 40647 };
						createcount = new int[] { 1 };
						pc.getQuest().set_step(L1PcQuest.QUEST_RUDIAN, L1PcQuest.QUEST_END);
					} else {
						htmlid = "rudian9";
					}
				}
			}
			// レスタ(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71060) {
				// 仲間たちについて
				if (s.equalsIgnoreCase("A")) {
					if (pc.getQuest().get_step(L1PcQuest.QUEST_RUDIAN) == L1PcQuest.QUEST_END) {
						htmlid = "resta6";
					} else {
						htmlid = "resta4";
					}
				} else if (s.equalsIgnoreCase("B")) {
					htmlid = "resta10";
					pc.getQuest().set_step(L1PcQuest.QUEST_RESTA, 2);
				}
			}
			// カドムス(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71061) {
				// 地図を組み合わせてください
				if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(40647, 3)) {
						htmlid = "cadmus6";
						pc.getInventory().consumeItem(40647, 3);
						pc.getQuest().set_step(L1PcQuest.QUEST_CADMUS, 2);
					} else {
						htmlid = "cadmus5";
						pc.getQuest().set_step(L1PcQuest.QUEST_CADMUS, 1);
					}
				}
			}
			// カミーラ(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71036) {
				if (s.equalsIgnoreCase("a")) {
					htmlid = "kamyla7";
					pc.getQuest().set_step(L1PcQuest.QUEST_KAMYLA, 1);
				} else if (s.equalsIgnoreCase("c")) {
					htmlid = "kamyla10";
					pc.getInventory().consumeItem(40644, 1);
					pc.getQuest().set_step(L1PcQuest.QUEST_KAMYLA, 3);
				} else if (s.equalsIgnoreCase("e")) {
					htmlid = "kamyla13";
					pc.getInventory().consumeItem(40630, 1);
					pc.getQuest().set_step(L1PcQuest.QUEST_KAMYLA, 4);
				} else if (s.equalsIgnoreCase("i")) {
					htmlid = "kamyla25";
				} else if (s.equalsIgnoreCase("b")) { // カーミラ（フランコの迷宮）
					if (pc.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 1) {
						L1Teleport.teleport(pc, 32679, 32742, (short) 482, 5, true);
					}
				} else if (s.equalsIgnoreCase("d")) { // カーミラ（ディエゴの閉ざされた牢）
					if (pc.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 3) {
						L1Teleport.teleport(pc, 32736, 32800, (short) 483, 5, true);
					}
				} else if (s.equalsIgnoreCase("f")) { // カーミラ（ホセ地下牢）
					if (pc.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 4) {
						L1Teleport.teleport(pc, 32746, 32807, (short) 484, 5, true);
					}
				}
			}
			// フランコ(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71089) {
				// カミーラにあなたの潔白を証明しましょう
				if (s.equalsIgnoreCase("a")) {
					htmlid = "francu10";
					final int[] item_ids = { 40644 };
					final int[] item_amounts = { 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
						pc.getQuest().set_step(L1PcQuest.QUEST_KAMYLA, 2);
					}
				}
			}
			// 試練のクリスタル2(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71090) {
				// はい、武器とスクロールをください
				if (s.equalsIgnoreCase("a")) {
					htmlid = "";
					final int[] item_ids = { 246, 247, 248, 249, 40660 };
					final int[] item_amounts = { 1, 1, 1, 1, 5 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
						pc.getQuest().set_step(L1PcQuest.QUEST_CRYSTAL, 1);
					}
				} else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().checkEquipped(246) || pc.getInventory().checkEquipped(247) || pc.getInventory().checkEquipped(248) || pc.getInventory().checkEquipped(249)) {
						htmlid = "jcrystal5";
					} else if (pc.getInventory().checkItem(40660)) {
						htmlid = "jcrystal4";
					} else {
						pc.getInventory().consumeItem(246, 1);
						pc.getInventory().consumeItem(247, 1);
						pc.getInventory().consumeItem(248, 1);
						pc.getInventory().consumeItem(249, 1);
						pc.getInventory().consumeItem(40620, 1);
						pc.getQuest().set_step(L1PcQuest.QUEST_CRYSTAL, 2);
						L1Teleport.teleport(pc, 32801, 32895, (short) 483, 4, true);
					}
				} else if (s.equalsIgnoreCase("c")) {
					if (pc.getInventory().checkEquipped(246) || pc.getInventory().checkEquipped(247) || pc.getInventory().checkEquipped(248) || pc.getInventory().checkEquipped(249)) {
						htmlid = "jcrystal5";
					} else {
						pc.getInventory().checkItem(40660);
						final L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40660);
						final long sc = l1iteminstance.getCount();
						if (sc > 0) {
							pc.getInventory().consumeItem(40660, sc);
						} else {
						}
						pc.getInventory().consumeItem(246, 1);
						pc.getInventory().consumeItem(247, 1);
						pc.getInventory().consumeItem(248, 1);
						pc.getInventory().consumeItem(249, 1);
						pc.getInventory().consumeItem(40620, 1);
						pc.getQuest().set_step(L1PcQuest.QUEST_CRYSTAL, 0);
						L1Teleport.teleport(pc, 32736, 32800, (short) 483, 4, true);
					}
				}
			}
			// 試練のクリスタル2(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71091) {
				// さらば！！
				if (s.equalsIgnoreCase("a")) {
					htmlid = "";
					pc.getInventory().consumeItem(40654, 1);
					pc.getQuest().set_step(L1PcQuest.QUEST_CRYSTAL, L1PcQuest.QUEST_END);
					L1Teleport.teleport(pc, 32744, 32927, (short) 483, 4, true);
				}
			}
			// リザードマンの長老(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71074) {
				// その戦士は今どこらへんにいるんですか？
				if (s.equalsIgnoreCase("A")) {
					htmlid = "lelder5";
					pc.getQuest().set_step(L1PcQuest.QUEST_LIZARD, 1);
					// 宝を取り戻してきます
				} else if (s.equalsIgnoreCase("B")) {
					htmlid = "lelder10";
					pc.getInventory().consumeItem(40633, 1);
					pc.getQuest().set_step(L1PcQuest.QUEST_LIZARD, 3);
				} else if (s.equalsIgnoreCase("C")) {
					htmlid = "lelder13";
					if (pc.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == L1PcQuest.QUEST_END) {
					}
					materials = new int[] { 40634 };
					counts = new int[] { 1 };
					createitem = new int[] { 20167 }; // リザードマングローブ
					createcount = new int[] { 1 };
					pc.getQuest().set_step(L1PcQuest.QUEST_LIZARD, L1PcQuest.QUEST_END);
				}
			}
			// 占星術師ケプリシャ
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80079) {
				// ケプリシャと魂の契約を結ぶ
				if (s.equalsIgnoreCase("0")) {
					if (!pc.getInventory().checkItem(41312)) { // 占星術師の壺
						final L1ItemInstance item = pc.getInventory().storeItem(41312, 1);
						if (item != null) {
							pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId())); // \f1%0が%1をくれました。
							pc.getQuest().set_step(L1PcQuest.QUEST_KEPLISHA, L1PcQuest.QUEST_END);
						}
						htmlid = "keplisha7";
					}
				}
				// 援助金を出して運勢を見る
				else if (s.equalsIgnoreCase("1")) {
					if (!pc.getInventory().checkItem(41314)) { // 占星術師のお守り
						if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
							materials = new int[] { L1ItemId.ADENA, 41313 }; // アデナ、占星術師の玉
							counts = new int[] { 1000, 1 };
							createitem = new int[] { 41314 }; // 占星術師のお守り
							createcount = new int[] { 1 };
							final int htmlA = _random.nextInt(3) + 1;
							final int htmlB = _random.nextInt(100) + 1;
							switch (htmlA) {
							case 1:
								htmlid = "horosa" + htmlB; // horosa1 ~
								// horosa100
								break;
							case 2:
								htmlid = "horosb" + htmlB; // horosb1 ~
								// horosb100
								break;
							case 3:
								htmlid = "horosc" + htmlB; // horosc1 ~
								// horosc100
								break;
							default:
								break;
							}
						} else {
							htmlid = "keplisha8";
						}
					}
				}
				// ケプリシャから祝福を受ける
				else if (s.equalsIgnoreCase("2")) {
					if (pc.getTempCharGfx() != pc.getClassId()) {
						htmlid = "keplisha9";
					} else {
						if (pc.getInventory().checkItem(41314)) { // 占星術師のお守り
							pc.getInventory().consumeItem(41314, 1); // 占星術師のお守り
							final int html = _random.nextInt(9) + 1;
							final int PolyId = 6180 + _random.nextInt(64);
							polyByKeplisha(client, PolyId);
							switch (html) {
							case 1:
								htmlid = "horomon11";
								break;
							case 2:
								htmlid = "horomon12";
								break;
							case 3:
								htmlid = "horomon13";
								break;
							case 4:
								htmlid = "horomon21";
								break;
							case 5:
								htmlid = "horomon22";
								break;
							case 6:
								htmlid = "horomon23";
								break;
							case 7:
								htmlid = "horomon31";
								break;
							case 8:
								htmlid = "horomon32";
								break;
							case 9:
								htmlid = "horomon33";
								break;
							default:
								break;
							}
						}
					}
				}
				// 壺を割って契約を破棄する
				else if (s.equalsIgnoreCase("3")) {
					if (pc.getInventory().checkItem(41312)) { // 占星術師の壺
						pc.getInventory().consumeItem(41312, 1);
						htmlid = "";
					}
					if (pc.getInventory().checkItem(41313)) { // 占星術師の玉
						pc.getInventory().consumeItem(41313, 1);
						htmlid = "";
					}
					if (pc.getInventory().checkItem(41314)) { // 占星術師のお守り
						pc.getInventory().consumeItem(41314, 1);
						htmlid = "";
					}
				}
			}

			// 怪しいオーク商人 パルーム
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {
				// 「資源リストをもらう」
				if (s.equalsIgnoreCase("q")) {
					if (pc.getInventory().checkItem(41356, 1)) {
						htmlid = "rparum4";
					} else {
						final L1ItemInstance item = pc.getInventory().storeItem(41356, 1);
						if (item != null) {
							pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId())); // \f1%0が%1をくれました。
						}
						htmlid = "rparum3";
					}
				}
			}
			// アデン騎馬団員
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80105) {
				// 恢復軍馬頭盔可用次數
				if (s.equalsIgnoreCase("c")) {
					if (pc.isCrown()) {
						if (pc.getInventory().checkItem(20383, 1)) {// 軍馬頭盔
							if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000)) {
								final L1ItemInstance item = pc.getInventory().findItemId(20383);
								if ((item != null) && (item.getChargeCount() != 50)) {
									item.setChargeCount(50);
									pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
									pc.getInventory().consumeItem(L1ItemId.ADENA, 100000);
									htmlid = "";
								}
							} else {
								pc.sendPackets(new S_ServerMessage(337, "$4")); // アデナが不足しています。
							}
						}
					}
				}
			}
			// 補佐官イリス
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71126) {
				// 「はい。私がご協力しましょう」
				if (s.equalsIgnoreCase("B")) {
					if (pc.getInventory().checkItem(41007, 1)) { // イリスの命令書：霊魂の安息
						htmlid = "eris10";
					} else {
						final L1NpcInstance npc = (L1NpcInstance) obj;
						final L1ItemInstance item = pc.getInventory().storeItem(41007, 1);
						final String npcName = npc.getNpcTemplate().get_name();
						final String itemName = item.getItem().getNameId();
						pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
						htmlid = "eris6";
					}
				} else if (s.equalsIgnoreCase("C")) {
					if (pc.getInventory().checkItem(41009, 1)) { // イリスの命令書：同盟の意思
						htmlid = "eris10";
					} else {
						final L1NpcInstance npc = (L1NpcInstance) obj;
						final L1ItemInstance item = pc.getInventory().storeItem(41009, 1);
						final String npcName = npc.getNpcTemplate().get_name();
						final String itemName = item.getItem().getNameId();
						pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
						htmlid = "eris8";
					}
				} else if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(41007, 1)) { // イリスの命令書：霊魂の安息
						if (pc.getInventory().checkItem(40969, 20)) { // ダークエルフ魂の結晶体
							htmlid = "eris18";
							materials = new int[] { 40969, 41007 };
							counts = new int[] { 20, 1 };
							createitem = new int[] { 41008 }; // イリスのバック
							createcount = new int[] { 1 };
						} else {
							htmlid = "eris5";
						}
					} else {
						htmlid = "eris2";
					}
				} else if (s.equalsIgnoreCase("E")) {
					if (pc.getInventory().checkItem(41010, 1)) { // イリスの推薦書
						htmlid = "eris19";
					} else {
						htmlid = "eris7";
					}
				} else if (s.equalsIgnoreCase("D")) {
					if (pc.getInventory().checkItem(41010, 1)) { // イリスの推薦書
						htmlid = "eris19";
					} else {
						if (pc.getInventory().checkItem(41009, 1)) { // イリスの命令書：同盟の意思
							if (pc.getInventory().checkItem(40959, 1)) { // 冥法軍王の印章
								htmlid = "eris17";
								materials = new int[] { 40959, 41009 }; // 冥法軍王の印章
								counts = new int[] { 1, 1 };
								createitem = new int[] { 41010 }; // イリスの推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40960, 1)) { // 魔霊軍王の印章
								htmlid = "eris16";
								materials = new int[] { 40960, 41009 }; // 魔霊軍王の印章
								counts = new int[] { 1, 1 };
								createitem = new int[] { 41010 }; // イリスの推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40961, 1)) { // 魔獣霊軍王の印章
								htmlid = "eris15";
								materials = new int[] { 40961, 41009 }; // 魔獣軍王の印章
								counts = new int[] { 1, 1 };
								createitem = new int[] { 41010 }; // イリスの推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40962, 1)) { // 暗殺軍王の印章
								htmlid = "eris14";
								materials = new int[] { 40962, 41009 }; // 暗殺軍王の印章
								counts = new int[] { 1, 1 };
								createitem = new int[] { 41010 }; // イリスの推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40635, 10)) { // 魔霊軍のバッジ
								htmlid = "eris12";
								materials = new int[] { 40635, 41009 }; // 魔霊軍のバッジ
								counts = new int[] { 10, 1 };
								createitem = new int[] { 41010 }; // イリスの推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40638, 10)) { // 魔獣軍のバッジ
								htmlid = "eris11";
								materials = new int[] { 40638, 41009 }; // 魔霊軍のバッジ
								counts = new int[] { 10, 1 };
								createitem = new int[] { 41010 }; // イリスの推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40642, 10)) { // 冥法軍のバッジ
								htmlid = "eris13";
								materials = new int[] { 40642, 41009 }; // 冥法軍のバッジ
								counts = new int[] { 10, 1 };
								createitem = new int[] { 41010 }; // イリスの推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40667, 10)) { // 暗殺軍のバッジ
								htmlid = "eris13";
								materials = new int[] { 40667, 41009 }; // 暗殺軍のバッジ
								counts = new int[] { 10, 1 };
								createitem = new int[] { 41010 }; // イリスの推薦書
								createcount = new int[] { 1 };
							} else {
								htmlid = "eris8";
							}
						} else {
							htmlid = "eris7";
						}
					}
				}
			}
			// 倒れた航海士
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80076) {
				if (s.equalsIgnoreCase("A")) {
					final int[] diaryno = { 49082, 49083 };
					final int pid = _random.nextInt(diaryno.length);
					final int di = diaryno[pid];
					if (di == 49082) { // 奇数ページ抜け
						htmlid = "voyager6a";
						final L1NpcInstance npc = (L1NpcInstance) obj;
						final L1ItemInstance item = pc.getInventory().storeItem(di, 1);
						final String npcName = npc.getNpcTemplate().get_name();
						final String itemName = item.getItem().getNameId();
						pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					} else if (di == 49083) { // 偶数ページ抜け
						htmlid = "voyager6b";
						final L1NpcInstance npc = (L1NpcInstance) obj;
						final L1ItemInstance item = pc.getInventory().storeItem(di, 1);
						final String npcName = npc.getNpcTemplate().get_name();
						final String itemName = item.getItem().getNameId();
						pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					}
				}
			}
			// 錬金術師 ペリター
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71128) {
				if (s.equals("A")) {
					if (pc.getInventory().checkItem(41010, 1)) { // イリスの推薦書
						htmlid = "perita2";
					} else {
						htmlid = "perita3";
					}
				} else if (s.equals("p")) {
					// 呪われたブラックイアリング判別
					if (pc.getInventory().checkItem(40987, 1) // ウィザードクラス
							&& pc.getInventory().checkItem(40988, 1) // ナイトクラス
							&& pc.getInventory().checkItem(40989, 1)) { // ウォーリアクラス
						htmlid = "perita43";
					} else if (pc.getInventory().checkItem(40987, 1) // ウィザードクラス
							&& pc.getInventory().checkItem(40989, 1)) { // ウォーリアクラス
						htmlid = "perita44";
					} else if (pc.getInventory().checkItem(40987, 1) // ウィザードクラス
							&& pc.getInventory().checkItem(40988, 1)) { // ナイトクラス
						htmlid = "perita45";
					} else if (pc.getInventory().checkItem(40988, 1) // ナイトクラス
							&& pc.getInventory().checkItem(40989, 1)) { // ウォーリアクラス
						htmlid = "perita47";
					} else if (pc.getInventory().checkItem(40987, 1)) { // ウィザードクラス
						htmlid = "perita46";
					} else if (pc.getInventory().checkItem(40988, 1)) { // ナイトクラス
						htmlid = "perita49";
					} else if (pc.getInventory().checkItem(40987, 1)) { // ウォーリアクラス
						htmlid = "perita48";
					} else {
						htmlid = "perita50";
					}
				} else if (s.equals("q")) {
					// ブラックイアリング判別
					if (pc.getInventory().checkItem(41173, 1) // ウィザードクラス
							&& pc.getInventory().checkItem(41174, 1) // ナイトクラス
							&& pc.getInventory().checkItem(41175, 1)) { // ウォーリアクラス
						htmlid = "perita54";
					} else if (pc.getInventory().checkItem(41173, 1) // ウィザードクラス
							&& pc.getInventory().checkItem(41175, 1)) { // ウォーリアクラス
						htmlid = "perita55";
					} else if (pc.getInventory().checkItem(41173, 1) // ウィザードクラス
							&& pc.getInventory().checkItem(41174, 1)) { // ナイトクラス
						htmlid = "perita56";
					} else if (pc.getInventory().checkItem(41174, 1) // ナイトクラス
							&& pc.getInventory().checkItem(41175, 1)) { // ウォーリアクラス
						htmlid = "perita58";
					} else if (pc.getInventory().checkItem(41174, 1)) { // ウィザードクラス
						htmlid = "perita57";
					} else if (pc.getInventory().checkItem(41175, 1)) { // ナイトクラス
						htmlid = "perita60";
					} else if (pc.getInventory().checkItem(41176, 1)) { // ウォーリアクラス
						htmlid = "perita59";
					} else {
						htmlid = "perita61";
					}
				} else if (s.equals("s")) {
					// ミステリアス ブラックイアリング判別
					if (pc.getInventory().checkItem(41161, 1) // ウィザードクラス
							&& pc.getInventory().checkItem(41162, 1) // ナイトクラス
							&& pc.getInventory().checkItem(41163, 1)) { // ウォーリアクラス
						htmlid = "perita62";
					} else if (pc.getInventory().checkItem(41161, 1) // ウィザードクラス
							&& pc.getInventory().checkItem(41163, 1)) { // ウォーリアクラス
						htmlid = "perita63";
					} else if (pc.getInventory().checkItem(41161, 1) // ウィザードクラス
							&& pc.getInventory().checkItem(41162, 1)) { // ナイトクラス
						htmlid = "perita64";
					} else if (pc.getInventory().checkItem(41162, 1) // ナイトクラス
							&& pc.getInventory().checkItem(41163, 1)) { // ウォーリアクラス
						htmlid = "perita66";
					} else if (pc.getInventory().checkItem(41161, 1)) { // ウィザードクラス
						htmlid = "perita65";
					} else if (pc.getInventory().checkItem(41162, 1)) { // ナイトクラス
						htmlid = "perita68";
					} else if (pc.getInventory().checkItem(41163, 1)) { // ウォーリアクラス
						htmlid = "perita67";
					} else {
						htmlid = "perita69";
					}
				} else if (s.equals("B")) {
					// 浄化のポーション
					if (pc.getInventory().checkItem(40651, 10) // 火の息吹
							&& pc.getInventory().checkItem(40643, 10) // 水の息吹
							&& pc.getInventory().checkItem(40618, 10) // 大地の息吹
							&& pc.getInventory().checkItem(40645, 10) // 風の息吹
							&& pc.getInventory().checkItem(40676, 10) // 闇の息吹
							&& pc.getInventory().checkItem(40442, 5) // プロッブの胃液
							&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
						htmlid = "perita7";
						materials = new int[] { 40651, 40643, 40618, 40645, 40676, 40442, 40051 };
						counts = new int[] { 10, 10, 10, 10, 20, 5, 1 };
						createitem = new int[] { 40925 }; // 浄化のポーション
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita8";
					}
				} else if (s.equals("G") || s.equals("h") || s.equals("i")) {
					// ミステリアス ポーション：１段階
					if (pc.getInventory().checkItem(40651, 5) // 火の息吹
							&& pc.getInventory().checkItem(40643, 5) // 水の息吹
							&& pc.getInventory().checkItem(40618, 5) // 大地の息吹
							&& pc.getInventory().checkItem(40645, 5) // 風の息吹
							&& pc.getInventory().checkItem(40676, 5) // 闇の息吹
							&& pc.getInventory().checkItem(40675, 5) // 闇の鉱石
							&& pc.getInventory().checkItem(40049, 3) // 高級ルビー
							&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
						htmlid = "perita27";
						materials = new int[] { 40651, 40643, 40618, 40645, 40676, 40675, 40049, 40051 };
						counts = new int[] { 5, 5, 5, 5, 10, 10, 3, 1 };
						createitem = new int[] { 40926 }; // ミステリアスポーション：１段階
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita28";
					}
				} else if (s.equals("H") || s.equals("j") || s.equals("k")) {
					// ミステリアス ポーション：２段階
					if (pc.getInventory().checkItem(40651, 10) // 火の息吹
							&& pc.getInventory().checkItem(40643, 10) // 水の息吹
							&& pc.getInventory().checkItem(40618, 10) // 大地の息吹
							&& pc.getInventory().checkItem(40645, 10) // 風の息吹
							&& pc.getInventory().checkItem(40676, 20) // 闇の息吹
							&& pc.getInventory().checkItem(40675, 10) // 闇の鉱石
							&& pc.getInventory().checkItem(40048, 3) // 高級ダイアモンド
							&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
						htmlid = "perita29";
						materials = new int[] { 40651, 40643, 40618, 40645, 40676, 40675, 40048, 40051 };
						counts = new int[] { 10, 10, 10, 10, 20, 10, 3, 1 };
						createitem = new int[] { 40927 }; // ミステリアスポーション：２段階
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita30";
					}
				} else if (s.equals("I") || s.equals("l") || s.equals("m")) {
					// ミステリアス ポーション：３段階
					if (pc.getInventory().checkItem(40651, 20) // 火の息吹
							&& pc.getInventory().checkItem(40643, 20) // 水の息吹
							&& pc.getInventory().checkItem(40618, 20) // 大地の息吹
							&& pc.getInventory().checkItem(40645, 20) // 風の息吹
							&& pc.getInventory().checkItem(40676, 30) // 闇の息吹
							&& pc.getInventory().checkItem(40675, 10) // 闇の鉱石
							&& pc.getInventory().checkItem(40050, 3) // 高級サファイア
							&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
						htmlid = "perita31";
						materials = new int[] { 40651, 40643, 40618, 40645, 40676, 40675, 40050, 40051 };
						counts = new int[] { 20, 20, 20, 20, 30, 10, 3, 1 };
						createitem = new int[] { 40928 }; // ミステリアスポーション：３段階
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita32";
					}
				} else if (s.equals("J") || s.equals("n") || s.equals("o")) {
					// ミステリアス ポーション：４段階
					if (pc.getInventory().checkItem(40651, 30) // 火の息吹
							&& pc.getInventory().checkItem(40643, 30) // 水の息吹
							&& pc.getInventory().checkItem(40618, 30) // 大地の息吹
							&& pc.getInventory().checkItem(40645, 30) // 風の息吹
							&& pc.getInventory().checkItem(40676, 30) // 闇の息吹
							&& pc.getInventory().checkItem(40675, 20) // 闇の鉱石
							&& pc.getInventory().checkItem(40052, 1) // 最高級ダイアモンド
							&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
						htmlid = "perita33";
						materials = new int[] { 40651, 40643, 40618, 40645, 40676, 40675, 40052, 40051 };
						counts = new int[] { 30, 30, 30, 30, 30, 20, 1, 1 };
						createitem = new int[] { 40928 }; // ミステリアスポーション：４段階
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita34";
					}
				} else if (s.equals("K")) { // １段階イアリング(霊魂のイアリング)
					int earinga = 0;
					int earingb = 0;
					if (pc.getInventory().checkEquipped(21014) || pc.getInventory().checkEquipped(21006) || pc.getInventory().checkEquipped(21007)) {
						htmlid = "perita36";
					} else if (pc.getInventory().checkItem(21014, 1)) { // ウィザードクラス
						earinga = 21014;
						earingb = 41176;
					} else if (pc.getInventory().checkItem(21006, 1)) { // ナイトクラス
						earinga = 21006;
						earingb = 41177;
					} else if (pc.getInventory().checkItem(21007, 1)) { // ウォーリアクラス
						earinga = 21007;
						earingb = 41178;
					} else {
						htmlid = "perita36";
					}
					if (earinga > 0) {
						materials = new int[] { earinga };
						counts = new int[] { 1 };
						createitem = new int[] { earingb };
						createcount = new int[] { 1 };
					}
				} else if (s.equals("L")) { // ２段階イアリング(知恵のイアリング)
					if (pc.getInventory().checkEquipped(21015)) {
						htmlid = "perita22";
					} else if (pc.getInventory().checkItem(21015, 1)) {
						materials = new int[] { 21015 };
						counts = new int[] { 1 };
						createitem = new int[] { 41179 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita22";
					}
				} else if (s.equals("M")) { // ３段階イアリング(真実のイアリング)
					if (pc.getInventory().checkEquipped(21016)) {
						htmlid = "perita26";
					} else if (pc.getInventory().checkItem(21016, 1)) {
						materials = new int[] { 21016 };
						counts = new int[] { 1 };
						createitem = new int[] { 41182 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita26";
					}
				} else if (s.equals("b")) { // ２段階イアリング(情熱のイアリング)
					if (pc.getInventory().checkEquipped(21009)) {
						htmlid = "perita39";
					} else if (pc.getInventory().checkItem(21009, 1)) {
						materials = new int[] { 21009 };
						counts = new int[] { 1 };
						createitem = new int[] { 41180 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita39";
					}
				} else if (s.equals("d")) { // ３段階イアリング(名誉のイアリング)
					if (pc.getInventory().checkEquipped(21012)) {
						htmlid = "perita41";
					} else if (pc.getInventory().checkItem(21012, 1)) {
						materials = new int[] { 21012 };
						counts = new int[] { 1 };
						createitem = new int[] { 41183 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita41";
					}
				} else if (s.equals("a")) { // ２段階イアリング(憤怒のイアリング)
					if (pc.getInventory().checkEquipped(21008)) {
						htmlid = "perita38";
					} else if (pc.getInventory().checkItem(21008, 1)) {
						materials = new int[] { 21008 };
						counts = new int[] { 1 };
						createitem = new int[] { 41181 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita38";
					}
				} else if (s.equals("c")) { // ３段階イアリング(勇猛のイアリング)
					if (pc.getInventory().checkEquipped(21010)) {
						htmlid = "perita40";
					} else if (pc.getInventory().checkItem(21010, 1)) {
						materials = new int[] { 21010 };
						counts = new int[] { 1 };
						createitem = new int[] { 41184 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita40";
					}
				}
			}
			// 宝石細工師 ルームィス
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71129) {
				if (s.equals("Z")) {
					htmlid = "rumtis2";
				} else if (s.equals("Y")) {
					if (pc.getInventory().checkItem(41010, 1)) { // イリスの推薦書
						htmlid = "rumtis3";
					} else {
						htmlid = "rumtis4";
					}
				} else if (s.equals("q")) {
					htmlid = "rumtis92";
				} else if (s.equals("A")) {
					if (pc.getInventory().checkItem(41161, 1)) {
						// ミステリアスブラックイアリング
						htmlid = "rumtis6";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("B")) {
					if (pc.getInventory().checkItem(41164, 1)) {
						// ミステリアスウィザードイアリング
						htmlid = "rumtis7";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("C")) {
					if (pc.getInventory().checkItem(41167, 1)) {
						// ミステリアスグレーウィザードイアリング
						htmlid = "rumtis8";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("T")) {
					if (pc.getInventory().checkItem(41167, 1)) {
						// ミステリアスホワイトウィザードイアリング
						htmlid = "rumtis9";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("w")) {
					if (pc.getInventory().checkItem(41162, 1)) {
						// ミステリアスブラックイアリング
						htmlid = "rumtis14";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("x")) {
					if (pc.getInventory().checkItem(41165, 1)) {
						// ミステリアスナイトイアリング
						htmlid = "rumtis15";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("y")) {
					if (pc.getInventory().checkItem(41168, 1)) {
						// ミステリアスグレーナイトイアリング
						htmlid = "rumtis16";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("z")) {
					if (pc.getInventory().checkItem(41171, 1)) {
						// ミステリアスホワイトナイトイアリング
						htmlid = "rumtis17";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("U")) {
					if (pc.getInventory().checkItem(41163, 1)) {
						// ミステリアスブラックイアリング
						htmlid = "rumtis10";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("V")) {
					if (pc.getInventory().checkItem(41166, 1)) {
						// ミステリアスウォーリアイアリング
						htmlid = "rumtis11";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("W")) {
					if (pc.getInventory().checkItem(41169, 1)) {
						// ミステリアスグレーウォーリアイアリング
						htmlid = "rumtis12";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("X")) {
					if (pc.getInventory().checkItem(41172, 1)) {
						// ミステリアスホワイウォーリアイアリング
						htmlid = "rumtis13";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("D") || s.equals("E") || s.equals("F") || s.equals("G")) {
					int insn = 0;
					int bacn = 0;
					int me = 0;
					int mr = 0;
					int mj = 0;
					int an = 0;
					int men = 0;
					int mrn = 0;
					int mjn = 0;
					int ann = 0;
					if (pc.getInventory().checkItem(40959, 1) // 冥法軍王の印章
							&& pc.getInventory().checkItem(40960, 1) // 魔霊軍王の印章
							&& pc.getInventory().checkItem(40961, 1) // 魔獣軍王の印章
							&& pc.getInventory().checkItem(40962, 1)) { // 暗殺軍王の印章
						insn = 1;
						me = 40959;
						mr = 40960;
						mj = 40961;
						an = 40962;
						men = 1;
						mrn = 1;
						mjn = 1;
						ann = 1;
					} else if (pc.getInventory().checkItem(40642, 10) // 冥法軍のバッジ
							&& pc.getInventory().checkItem(40635, 10) // 魔霊軍のバッジ
							&& pc.getInventory().checkItem(40638, 10) // 魔獣軍のバッジ
							&& pc.getInventory().checkItem(40667, 10)) { // 暗殺軍のバッジ
						bacn = 1;
						me = 40642;
						mr = 40635;
						mj = 40638;
						an = 40667;
						men = 10;
						mrn = 10;
						mjn = 10;
						ann = 10;
					}
					if (pc.getInventory().checkItem(40046, 1) // サファイア
							&& pc.getInventory().checkItem(40618, 5) // 大地の息吹
							&& pc.getInventory().checkItem(40643, 5) // 水の息吹
							&& pc.getInventory().checkItem(40645, 5) // 風の息吹
							&& pc.getInventory().checkItem(40651, 5) // 火の息吹
							&& pc.getInventory().checkItem(40676, 5)) { // 闇の息吹
						if ((insn == 1) || (bacn == 1)) {
							htmlid = "rumtis60";
							materials = new int[] { me, mr, mj, an, 40046, 40618, 40643, 40651, 40676 };
							counts = new int[] { men, mrn, mjn, ann, 1, 5, 5, 5, 5, 5 };
							createitem = new int[] { 40926 }; // 加工されたサファイア：１段階
							createcount = new int[] { 1 };
						} else {
							htmlid = "rumtis18";
						}
					}
				}
			}
			// アタロゼ
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71119) {
				// 「ラスタバドの歴史書1章から8章まで全部渡す」
				if (s.equalsIgnoreCase("request las history book")) {
					materials = new int[] { 41019, 41020, 41021, 41022, 41023, 41024, 41025, 41026 };
					counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1 };
					createitem = new int[] { 41027 };
					createcount = new int[] { 1 };
					htmlid = "";
				}
			}
			// 長老随行員クロレンス
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71170) {
				// 「ラスタバドの歴史書を渡す」
				if (s.equalsIgnoreCase("request las weapon manual")) {
					materials = new int[] { 41027 };
					counts = new int[] { 1 };
					createitem = new int[] { 40965 };
					createcount = new int[] { 1 };
					htmlid = "";
				}
			}
			// 真冥王 ダンテス
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71168) {
				// 「異界の魔物がいる場所へ送ってください」
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(41028, 1)) {
						L1Teleport.teleport(pc, 32648, 32921, (short) 535, 6, true);
						pc.getInventory().consumeItem(41028, 1);
					}
				}
			}
			// 諜報員(欲望の洞窟側)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80067) {
				// 「動揺しつつも承諾する」
				if (s.equalsIgnoreCase("n")) {
					htmlid = "";
					poly(client, 6034);
					final int[] item_ids = { 41132, 41133, 41134 };
					final int[] item_amounts = { 1, 1, 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
						pc.getQuest().set_step(L1PcQuest.QUEST_DESIRE, 1);
					}
					// 「そんな任務はやめる」
				} else if (s.equalsIgnoreCase("d")) {
					htmlid = "minicod09";
					pc.getInventory().consumeItem(41130, 1);
					pc.getInventory().consumeItem(41131, 1);
					// 「初期化する」
				} else if (s.equalsIgnoreCase("k")) {
					htmlid = "";
					pc.getInventory().consumeItem(41132, 1); // 血痕の堕落した粉
					pc.getInventory().consumeItem(41133, 1); // 血痕の無力した粉
					pc.getInventory().consumeItem(41134, 1); // 血痕の我執した粉
					pc.getInventory().consumeItem(41135, 1); // カヘルの堕落した精髄
					pc.getInventory().consumeItem(41136, 1); // カヘルの無力した精髄
					pc.getInventory().consumeItem(41137, 1); // カヘルの我執した精髄
					pc.getInventory().consumeItem(41138, 1); // カヘルの精髄
					pc.getQuest().set_step(L1PcQuest.QUEST_DESIRE, 0);
					// 精髄を渡す
				} else if (s.equalsIgnoreCase("e")) {
					if ((pc.getQuest().get_step(L1PcQuest.QUEST_DESIRE) == L1PcQuest.QUEST_END) || (pc.getKarmaLevel() >= 1)) {
						htmlid = "";
					} else {
						if (pc.getInventory().checkItem(41138)) {
							htmlid = "";
							pc.addKarma((int) (1600 * ConfigRate.RATE_KARMA));
							pc.getInventory().consumeItem(41130, 1); // 血痕の契約書
							pc.getInventory().consumeItem(41131, 1); // 血痕の指令書
							pc.getInventory().consumeItem(41138, 1); // カヘルの精髄
							pc.getQuest().set_step(L1PcQuest.QUEST_DESIRE, L1PcQuest.QUEST_END);
						} else {
							htmlid = "minicod04";
						}
					}
					// プレゼントをもらう
				} else if (s.equalsIgnoreCase("g")) {
					htmlid = "";
					final int[] item_ids = { 41130 }; // 血痕の契約書
					final int[] item_amounts = { 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
					}
				}
			}
			// 諜報員(影の神殿側)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81202) {
				// 「頭にくるが承諾する」
				if (s.equalsIgnoreCase("n")) {
					htmlid = "";
					poly(client, 6035);
					final int[] item_ids = { 41123, 41124, 41125 };
					final int[] item_amounts = { 1, 1, 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
						pc.getQuest().set_step(L1PcQuest.QUEST_SHADOWS, 1);
					}
					// 「そんな任務はやめる」
				} else if (s.equalsIgnoreCase("d")) {
					htmlid = "minitos09";
					pc.getInventory().consumeItem(41121, 1);
					pc.getInventory().consumeItem(41122, 1);
					// 「初期化する」
				} else if (s.equalsIgnoreCase("k")) {
					htmlid = "";
					pc.getInventory().consumeItem(41123, 1); // カヘルの堕落した粉
					pc.getInventory().consumeItem(41124, 1); // カヘルの無力した粉
					pc.getInventory().consumeItem(41125, 1); // カヘルの我執した粉
					pc.getInventory().consumeItem(41126, 1); // 血痕の堕落した精髄
					pc.getInventory().consumeItem(41127, 1); // 血痕の無力した精髄
					pc.getInventory().consumeItem(41128, 1); // 血痕の我執した精髄
					pc.getInventory().consumeItem(41129, 1); // 血痕の精髄
					pc.getQuest().set_step(L1PcQuest.QUEST_SHADOWS, 0);
					// 精髄を渡す
				} else if (s.equalsIgnoreCase("e")) {
					if ((pc.getQuest().get_step(L1PcQuest.QUEST_SHADOWS) == L1PcQuest.QUEST_END) || (pc.getKarmaLevel() >= 1)) {
						htmlid = "";
					} else {
						if (pc.getInventory().checkItem(41129)) {
							htmlid = "";
							pc.addKarma((int) (-1600 * ConfigRate.RATE_KARMA));
							pc.getInventory().consumeItem(41121, 1); // カヘルの契約書
							pc.getInventory().consumeItem(41122, 1); // カヘルの指令書
							pc.getInventory().consumeItem(41129, 1); // 血痕の精髄
							pc.getQuest().set_step(L1PcQuest.QUEST_SHADOWS, L1PcQuest.QUEST_END);
						} else {
							htmlid = "minitos04";
						}
					}
					// 素早く受取る
				} else if (s.equalsIgnoreCase("g")) {
					htmlid = "";
					final int[] item_ids = { 41121 }; // カヘルの契約書
					final int[] item_amounts = { 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
					}
				}
			}
			// ゾウのストーンゴーレム
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71252) {
				int weapon1 = 0;
				int weapon2 = 0;
				int newWeapon = 0;
				if (s.equalsIgnoreCase("A")) {
					weapon1 = 5; // +7エルヴンダガー
					weapon2 = 10; // +7ラスタバドダガー
					newWeapon = 259; // マナバーラード
					htmlid = "joegolem9";
				} else if (s.equalsIgnoreCase("B")) {
					weapon1 = 145; // +7バーサーカーアックス
					weapon2 = 148; // +7グレートアックス
					newWeapon = 260; // レイジングウィンド
					htmlid = "joegolem10";
				} else if (s.equalsIgnoreCase("C")) {
					weapon1 = 52; // +7ツーハンドソード
					weapon2 = 64; // +7グレートソード
					newWeapon = 262; // ディストラクション
					htmlid = "joegolem11";
				} else if (s.equalsIgnoreCase("D")) {
					weapon1 = 125; // +7ソーサリースタッフ
					weapon2 = 129; // +7メイジスタッフ
					newWeapon = 261; // アークメイジスタッフ
					htmlid = "joegolem12";
				} else if (s.equalsIgnoreCase("E")) {
					weapon1 = 99; // +7エルブンスピアー
					weapon2 = 104; // +7フォチャード
					newWeapon = 263; // フリージングランサー
					htmlid = "joegolem13";
				} else if (s.equalsIgnoreCase("F")) {
					weapon1 = 32; // +7グラディウス
					weapon2 = 42; // +7レイピア
					newWeapon = 264; // ライトニングエッジ
					htmlid = "joegolem14";
				}
				if (pc.getInventory().checkEnchantItem(weapon1, 7, 1) && pc.getInventory().checkEnchantItem(weapon2, 7, 1) && pc.getInventory().checkItem(41246, 1000) // 結晶体
						&& pc.getInventory().checkItem(49143, 10)) { // 勇気の結晶
					pc.getInventory().consumeEnchantItem(weapon1, 7, 1);
					pc.getInventory().consumeEnchantItem(weapon2, 7, 1);
					pc.getInventory().consumeItem(41246, 1000);
					pc.getInventory().consumeItem(49143, 10);
					final L1ItemInstance item = pc.getInventory().storeItem(newWeapon, 1);
					pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getNameId()));
				} else {
					htmlid = "joegolem15";
					if (!pc.getInventory().checkEnchantItem(weapon1, 7, 1)) {
						pc.sendPackets(new S_ServerMessage(337, "+7 " + ItemTable.get().getTemplate(weapon1).getNameId())); // \f1%0が不足しています。
					}
					if (!pc.getInventory().checkEnchantItem(weapon2, 7, 1)) {
						pc.sendPackets(new S_ServerMessage(337, "+7 " + ItemTable.get().getTemplate(weapon2).getNameId())); // \f1%0が不足しています。
					}
					if (!pc.getInventory().checkItem(41246, 1000)) {
						long itemCount = 0;
						itemCount = 1000 - pc.getInventory().countItems(41246);
						pc.sendPackets(new S_ServerMessage(337, ItemTable.get().getTemplate(41246).getNameId() + "(" + itemCount + ")")); // \f1%0が不足しています。
					}
					if (!pc.getInventory().checkItem(49143, 10)) {
						long itemCount = 0;
						itemCount = 10 - pc.getInventory().countItems(49143);
						pc.sendPackets(new S_ServerMessage(337, ItemTable.get().getTemplate(49143).getNameId() + "(" + itemCount + ")")); // \f1%0が不足しています。
					}
				}
			}
			// ゾウのストーンゴーレム テーベ砂漠
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71253) {
				// 「歪みのコアを作る」
				if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(49101, 100)) {
						materials = new int[] { 49101 };
						counts = new int[] { 100 };
						createitem = new int[] { 49092 };
						createcount = new int[] { 1 };
						htmlid = "joegolem18";
					} else {
						htmlid = "joegolem19";
					}
				} else if (s.equalsIgnoreCase("B")) {
					if (pc.getInventory().checkItem(49101, 1)) {
						pc.getInventory().consumeItem(49101, 1);
						L1Teleport.teleport(pc, 33966, 33253, (short) 4, 5, true);
						htmlid = "";
					} else {
						htmlid = "joegolem20";
					}
				}
			}
			// テーベ オシリス祭壇のキーパー
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71255) {
				// 「テーベオシリス祭壇の鍵を持っているなら、オシリスの祭壇にお送りしましょう。」
				if (s.equalsIgnoreCase("e")) {
					if (pc.getInventory().checkItem(49242, 1)) { // 鍵のチェック(20人限定/時の歪みが現れてから2h30は未実装)
						pc.getInventory().consumeItem(49242, 1);
						L1Teleport.teleport(pc, 32735, 32831, (short) 782, 2, true);
						htmlid = "";
					} else {
						htmlid = "tebegate3";
						// 「上限人数に達している場合は」
						// htmlid = "tebegate4";
					}
				}
			}

			// 屍魂塔
			else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 111483)) { // 天堂與地獄的境界->進入屍魂塔封印空間
				if (s.equalsIgnoreCase("b")) {
					if (!pc.getInventory().checkItem(49479, 1)) {// 封印的屍魂水晶
						if (pc.getInventory().consumeItem(40308, 150000) // 金幣15萬
								&& pc.getInventory().consumeItem(49478, 1)) { // 藍色屍魂水晶
							CreateNewItem.createNewItem(pc, 49479, 1); // 給予封印的屍魂水晶
							// L1Teleport.teleport(pc, 32808, 32800, (short) 4000, 4, true);
							L1Teleport.teleport(pc, 32806, 32800, (short) 4000, 4, true);
							htmlid = "";
						}
					} else {
						htmlid = "shhon1";
					}
				}

			} else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 111480)) { // 屍魂塔傳送師->奇岩村
				if (s.equalsIgnoreCase("a")) {
					L1Teleport.teleport(pc, 33448, 32793, (short) 4, 4, true);
				}

			} else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 111482)) { // 屍魂塔入口->進入天堂與地獄的境界
				if (s.equalsIgnoreCase("a")) {
					// L1Teleport.teleport(pc, 32757, 32845, (short) 2400, 4, true);
					L1Teleport.teleport(pc, 32758, 32832, (short) 2400, 4, true);
				}
			}
			// 屍魂塔end

			// 治安団長ラルソン
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80099) {
				if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(40308, 300)) {
						pc.getInventory().consumeItem(40308, 300);
						pc.getInventory().storeItem(41315, 1);
						pc.getQuest().set_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 1);
						htmlid = "rarson16";
					} else if (!pc.getInventory().checkItem(40308, 300)) {
						htmlid = "rarson7";
					}
				} else if (s.equalsIgnoreCase("B")) {
					if ((pc.getQuest().get_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 1) && (pc.getInventory().checkItem(41325, 1))) {
						pc.getInventory().consumeItem(41325, 1);
						pc.getInventory().storeItem(40308, 2000);
						pc.getInventory().storeItem(41317, 1);
						pc.getQuest().set_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 2);
						htmlid = "rarson9";
					} else {
						htmlid = "rarson10";
					}
				} else if (s.equalsIgnoreCase("C")) {
					if ((pc.getQuest().get_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 4) && (pc.getInventory().checkItem(41326, 1))) {
						pc.getInventory().storeItem(40308, 30000);
						pc.getInventory().consumeItem(41326, 1);
						htmlid = "rarson12";
						pc.getQuest().set_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 5);
					} else {
						htmlid = "rarson17";
					}
				} else if (s.equalsIgnoreCase("D")) {
					if ((pc.getQuest().get_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) <= 1) || (pc.getQuest().get_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 5)) {
						if (pc.getInventory().checkItem(40308, 300)) {
							pc.getInventory().consumeItem(40308, 300);
							pc.getInventory().storeItem(41315, 1);
							pc.getQuest().set_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 1);
							htmlid = "rarson16";
						} else if (!pc.getInventory().checkItem(40308, 300)) {
							htmlid = "rarson7";
						}
					} else if ((pc.getQuest().get_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) >= 2) && (pc.getQuest().get_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) <= 4)) {
						if (pc.getInventory().checkItem(40308, 300)) {
							pc.getInventory().consumeItem(40308, 300);
							pc.getInventory().storeItem(41315, 1);
							htmlid = "rarson16";
						} else if (!pc.getInventory().checkItem(40308, 300)) {
							htmlid = "rarson7";
						}
					}
				}
			}
			// クエン
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80101) {
				if (s.equalsIgnoreCase("request letter of kuen")) {
					if ((pc.getQuest().get_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 2) && (pc.getInventory().checkItem(41317, 1))) {
						pc.getInventory().consumeItem(41317, 1);
						pc.getInventory().storeItem(41318, 1);
						pc.getQuest().set_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 3);
						htmlid = "";
					} else {
						htmlid = "";
					}
				} else if (s.equalsIgnoreCase("request holy mithril dust")) {
					if ((pc.getQuest().get_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 3) && (pc.getInventory().checkItem(41315, 1)) && pc.getInventory().checkItem(40494, 30)
							&& pc.getInventory().checkItem(41318, 1)) {
						pc.getInventory().consumeItem(41315, 1);
						pc.getInventory().consumeItem(41318, 1);
						pc.getInventory().consumeItem(40494, 30);
						pc.getInventory().storeItem(41316, 1);
						pc.getQuest().set_step(L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 4);
						htmlid = "";
					} else {
						htmlid = "";
					}
				}
			}

			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70077) {
				int consumeItem = 0;
				int consumeItemCount = 0;
				int petNpcId = 0;
				int petItemId = 0;
				int upLv = 0;
				long lvExp = 0L;
				String msg = "";
				if (s.equalsIgnoreCase("buy 1")) {
					petNpcId = 45042;
					consumeItem = 40308;
					consumeItemCount = 50000;
					petItemId = 40314;
					upLv = 5;
					lvExp = ExpTable.getExpByLevel(upLv);
					msg = "金幣";
				} else if (s.equalsIgnoreCase("buy 2")) {
					petNpcId = 45687;
					consumeItem = 40308;
					consumeItemCount = 50000;
					petItemId = 40316;
					upLv = 5;
					lvExp = ExpTable.getExpByLevel(upLv);
					msg = "金幣";
				} else if (s.equalsIgnoreCase("buy 3")) {
					petNpcId = 45046;
					consumeItem = 40308;
					consumeItemCount = 50000;
					petItemId = 40314;
					upLv = 5;
					lvExp = ExpTable.getExpByLevel(upLv);
					msg = "金幣";
				} else if (s.equalsIgnoreCase("buy 4")) {
					petNpcId = 45047;
					consumeItem = 40308;
					consumeItemCount = 50000;
					petItemId = 40314;
					upLv = 5;
					lvExp = ExpTable.getExpByLevel(upLv);
					msg = "金幣";
				}
				if (petNpcId > 0) {
					if (!pc.getInventory().checkItem(consumeItem, consumeItemCount)) {
						pc.sendPackets(new S_ServerMessage(337, msg));
					} else if (pc.getInventory().getSize() > 180) {
						pc.sendPackets(new S_ServerMessage(337, "身上空間"));
					} else if (pc.getInventory().checkItem(consumeItem, consumeItemCount)) {
						pc.getInventory().consumeItem(consumeItem, consumeItemCount);
						final L1PcInventory inv = pc.getInventory();
						final L1ItemInstance petamu = inv.storeItem(petItemId, 1L);
						if (petamu != null) {
							PetReading.get().buyNewPet(petNpcId, petamu.getId() + 1, petamu.getId(), upLv, lvExp);
							pc.sendPackets(new S_ItemName(petamu));
							pc.sendPackets(new S_ServerMessage(403, petamu.getName()));
						}
					}
				} else {
					pc.sendPackets(new S_SystemMessage("對話檔版本不符，請下載更新"));
					htmlid = "";
				}
			}

			// else System.out.println("C_NpcAction: " + s);
			if ((htmlid != null) && htmlid.equalsIgnoreCase("colos2")) {
				htmldata = makeUbInfoStrings(((L1NpcInstance) obj).getNpcTemplate().get_npcId());
			}
			if (createitem != null) { // アイテム精製
				boolean isCreate = true;
				for (int j = 0; j < materials.length; j++) {
					if (!pc.getInventory().checkItemNotEquipped(materials[j], counts[j])) {
						final L1Item temp = ItemTable.get().getTemplate(materials[j]);
						pc.sendPackets(new S_ServerMessage(337, temp.getNameId())); // \f1%0が不足しています。
						isCreate = false;
					}
				}

				if (isCreate) {
					// 容量と重量の計算
					int create_count = 0; // アイテムの個数（纏まる物は1個）
					int create_weight = 0;
					for (int k = 0; k < createitem.length; k++) {
						final L1Item temp = ItemTable.get().getTemplate(createitem[k]);
						if (temp.isStackable()) {
							if (!pc.getInventory().checkItem(createitem[k])) {
								create_count += 1;
							}
						} else {
							create_count += createcount[k];
						}
						create_weight += (temp.getWeight() * createcount[k]) / 1000;
					}
					// 容量確認
					if ((pc.getInventory().getSize() + create_count) > 180) {
						pc.sendPackets(new S_ServerMessage(263)); // 263
																	// \f1一個角色最多可攜帶180個道具。
						return;
					}
					// 重量確認
					if (pc.getMaxWeight() < (pc.getInventory().getWeight() + create_weight)) {
						pc.sendPackets(new S_ServerMessage(82)); // 82
																	// 此物品太重了，所以你無法攜帶。
						return;
					}

					for (int j = 0; j < materials.length; j++) {
						// 材料消費
						pc.getInventory().consumeItem(materials[j], counts[j]);
					}
					for (int k = 0; k < createitem.length; k++) {
						final L1ItemInstance item = pc.getInventory().storeItem(createitem[k], createcount[k]);
						if (item != null) {
							final String itemName = ItemTable.get().getTemplate(createitem[k]).getNameId();
							String createrName = "";
							if (obj instanceof L1NpcInstance) {
								createrName = ((L1NpcInstance) obj).getNpcTemplate().get_name();
							}
							if (createcount[k] > 1) {
								pc.sendPackets(new S_ServerMessage(143, createrName, itemName + " (" + createcount[k] + ")")); // \f1%0が%1をくれました。
							} else {
								pc.sendPackets(new S_ServerMessage(143, createrName, itemName)); // \f1%0が%1をくれました。
							}
						}
					}
					if (success_htmlid != null) { // html指定がある場合は表示
						pc.sendPackets(new S_NPCTalkReturn(objid, success_htmlid, htmldata));
					}
				} else { // 精製失敗
					if (failure_htmlid != null) { // html指定がある場合は表示
						pc.sendPackets(new S_NPCTalkReturn(objid, failure_htmlid, htmldata));
					}
				}
			}

			if (htmlid != null) { // html指定がある場合は表示
				pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	private String karmaLevelToHtmlId(final int level) {
		if ((level == 0) || (level < -7) || (7 < level)) {
			return "";
		}
		String htmlid = "";
		if (0 < level) {
			htmlid = "vbk" + level;
		} else if (level < 0) {
			htmlid = "vyk" + Math.abs(level);
		}
		return htmlid;
	}

	private String watchUb(final L1PcInstance pc, final int npcId) {
		final L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		final L1Location loc = ub.getLocation();
		if (pc.getInventory().consumeItem(L1ItemId.ADENA, 100)) {
			try {
				pc.save();
				pc.beginGhost(loc.getX(), loc.getY(), (short) loc.getMapId(), true);

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}

		} else {
			pc.sendPackets(new S_ServerMessage(189)); // 189 \f1金幣不足。
		}
		return "";
	}

	private String enterUb(final L1PcInstance pc, final int npcId) {
		final L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		if (!ub.isActive() || !ub.canPcEnter(pc)) { // 時間外
			return "colos2";
		}
		if (ub.isNowUb()) { // 競技中
			return "colos1";
		}
		if (ub.getMembersCount() >= ub.getMaxPlayer()) { // 定員オーバー
			return "colos4";
		}

		ub.addMember(pc); // メンバーに追加
		final L1Location loc = ub.getLocation().randomLocation(10, false);
		L1Teleport.teleport(pc, loc.getX(), loc.getY(), ub.getMapId(), 5, true);
		return "";
	}

	private String enterHauntedHouse(final L1PcInstance pc) {
		if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_PLAYING) { // 競技中
			pc.sendPackets(new S_ServerMessage(1182)); // もうゲームは始まってるよ。
			return "";
		}
		if (L1HauntedHouse.getInstance().getMembersCount() >= 10) { // 定員オーバー
			pc.sendPackets(new S_ServerMessage(1184)); // お化け屋敷は人でいっぱいだよ。
			return "";
		}

		L1HauntedHouse.getInstance().addMember(pc); // メンバーに追加
		L1Teleport.teleport(pc, 32722, 32830, (short) 5140, 2, true);
		return "";
	}

	/**
	 * 寵物競賽
	 * 
	 * @param pc
	 * @param objid2
	 * @return
	 */
	/*
	 * private String enterPetMatch(final L1PcInstance pc, final int objid2) { final Object[] petlist = pc.getPetList().values().toArray(); if (petlist.length > 0) { // 1187 寵物項鍊正在使用中。
	 * pc.sendPackets(new S_ServerMessage(1187)); return ""; } if (!L1PetMatch.getInstance().enterPetMatch(pc, objid2)) { // 1182 遊戲已經開始了 pc.sendPackets(new S_ServerMessage(1182)); }
	 * return ""; }
	 */

	private void poly(final ClientExecutor clientthread, final int polyId) {
		final L1PcInstance pc = clientthread.getActiveChar();

		if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
			pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);
		} else {
			pc.sendPackets(new S_ServerMessage(337, "$4")); // アデナが不足しています。
		}
	}

	private void polyByKeplisha(final ClientExecutor clientthread, final int polyId) {
		final L1PcInstance pc = clientthread.getActiveChar();

		if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
			pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_KEPLISHA);
		} else {
			pc.sendPackets(new S_ServerMessage(337, "$4")); // アデナが不足しています。
		}
	}

	private String sellHouse(final L1PcInstance pc, final int objectId, final int npcId) {
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan == null) {
			return ""; // ウィンドウを消す
		}
		final int houseId = clan.getHouseId();
		if (houseId == 0) {
			return ""; // ウィンドウを消す
		}

		final L1House house = HouseReading.get().getHouseTable(houseId);
		final int keeperId = house.getKeeperId();
		if (npcId != keeperId) {
			return ""; // ウィンドウを消す
		}
		if (!pc.isCrown()) {
			pc.sendPackets(new S_ServerMessage(518)); // この命令は血盟の君主のみが利用できます。
			return ""; // ウィンドウを消す
		}
		if (pc.getId() != clan.getLeaderId()) {
			pc.sendPackets(new S_ServerMessage(518)); // この命令は血盟の君主のみが利用できます。
			return ""; // ウィンドウを消す
		}
		if (house.isOnSale()) {
			return "agonsale";
		}

		pc.sendPackets(new S_SellHouse(objectId, String.valueOf(houseId)));
		return null;
	}

	private void openCloseDoor(final L1PcInstance pc, final L1NpcInstance npc, final String s) {
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan != null) {
			final int houseId = clan.getHouseId();
			if (houseId != 0) {
				final L1House house = HouseReading.get().getHouseTable(houseId);
				final int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					L1DoorInstance door1 = null;
					L1DoorInstance door2 = null;
					L1DoorInstance door3 = null;
					L1DoorInstance door4 = null;
					for (final L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
						if (door.getKeeperId() == keeperId) {
							if (door1 == null) {
								door1 = door;
								continue;
							}
							if (door2 == null) {
								door2 = door;
								continue;
							}
							if (door3 == null) {
								door3 = door;
								continue;
							}
							if (door4 == null) {
								door4 = door;
								break;
							}
						}
					}
					if (door1 != null) {
						if (s.equalsIgnoreCase("open")) {
							door1.open();
						} else if (s.equalsIgnoreCase("close")) {
							door1.close();
						}
					}
					if (door2 != null) {
						if (s.equalsIgnoreCase("open")) {
							door2.open();
						} else if (s.equalsIgnoreCase("close")) {
							door2.close();
						}
					}
					if (door3 != null) {
						if (s.equalsIgnoreCase("open")) {
							door3.open();
						} else if (s.equalsIgnoreCase("close")) {
							door3.close();
						}
					}
					if (door4 != null) {
						if (s.equalsIgnoreCase("open")) {
							door4.open();
						} else if (s.equalsIgnoreCase("close")) {
							door4.close();
						}
					}
				}
			}
		}
	}

	private void openCloseGate(final L1PcInstance pc, final int keeperId, final boolean isOpen) {
		boolean isNowWar = false;
		int pcCastleId = 0;
		if (pc.getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				pcCastleId = clan.getCastleId();
			}
		}
		if ((keeperId == 70656) || (keeperId == 70549) || (keeperId == 70985)) { // ケント城
			if (isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(L1CastleLocation.KENT_CASTLE_ID);
		} else if (keeperId == 70600) { // OT
			if (isExistDefenseClan(L1CastleLocation.OT_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.OT_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(L1CastleLocation.OT_CASTLE_ID);
		} else if ((keeperId == 70778) || (keeperId == 70987) || (keeperId == 70687)) { // WW城
			if (isExistDefenseClan(L1CastleLocation.WW_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.WW_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(L1CastleLocation.WW_CASTLE_ID);
		} else if ((keeperId == 70817) || (keeperId == 70800) || (keeperId == 70988) || (keeperId == 70990) || (keeperId == 70989) || (keeperId == 70991)) { // ギラン城
			if (isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(L1CastleLocation.GIRAN_CASTLE_ID);
		} else if ((keeperId == 70863) || (keeperId == 70992) || (keeperId == 70862)) { // ハイネ城
			if (isExistDefenseClan(L1CastleLocation.HEINE_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.HEINE_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(L1CastleLocation.HEINE_CASTLE_ID);
		} else if ((keeperId == 70995) || (keeperId == 70994) || (keeperId == 70993)) { // ドワーフ城
			if (isExistDefenseClan(L1CastleLocation.DOWA_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.DOWA_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(L1CastleLocation.DOWA_CASTLE_ID);
		} else if (keeperId == 70996) { // アデン城
			if (isExistDefenseClan(L1CastleLocation.ADEN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.ADEN_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(L1CastleLocation.ADEN_CASTLE_ID);
		}

		for (final L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
			if (door.getKeeperId() == keeperId) {
				if (isNowWar && (door.getMaxHp() > 1)) { // 戦争中は城門開閉不可
				} else {
					if (isOpen) { // 開
						door.open();
					} else { // 閉
						door.close();
					}
				}
			}
		}
	}

	private boolean isExistDefenseClan(final int castleId) {
		boolean isExistDefenseClan = false;
		final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
		for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
			final L1Clan clan = iter.next();
			if (castleId == clan.getCastleId()) {
				isExistDefenseClan = true;
				break;
			}
		}
		return isExistDefenseClan;
	}

	private void expelOtherClan(final L1PcInstance clanPc, final int keeperId) {
		int houseId = 0;
		final Collection<L1House> houseList = HouseReading.get().getHouseTableList().values();
		for (final L1House house : houseList) {
			if (house.getKeeperId() == keeperId) {
				houseId = house.getHouseId();
			}
		}
		if (houseId == 0) {
			return;
		}

		int[] loc = new int[3];
		for (final L1PcInstance pc : World.get().getAllPlayers()) {
			if (L1HouseLocation.isInHouseLoc(houseId, pc.getX(), pc.getY(), pc.getMapId()) && (clanPc.getClanid() != pc.getClanid())) {
				loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
				if (pc != null) {
					L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
				}
			}
		}
	}

	private void repairGate(final L1PcInstance pc) {
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan != null) {
			final int castleId = clan.getCastleId();
			if (castleId != 0) { // 城主クラン
				if (!ServerWarExecutor.get().isNowWar(castleId)) {
					// 城門を元に戻す
					for (final L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
						if (L1CastleLocation.checkInWarArea(castleId, door)) {
							door.repairGate();
						}
					}
					pc.sendPackets(new S_ServerMessage(990)); // 城門自動修理を命令しました。
				} else {
					pc.sendPackets(new S_ServerMessage(991)); // 城門自動修理命令を取り消しました。
				}
			}
		}
	}

	private void payFee(final L1PcInstance pc, final L1NpcInstance npc) {
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan != null) {
			final int houseId = clan.getHouseId();
			if (houseId != 0) {
				final L1House house = HouseReading.get().getHouseTable(houseId);
				final int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					if (pc.getInventory().checkItem(L1ItemId.ADENA, ConfigAlt.HOUSE_TAX_ADENA)) {
						pc.getInventory().consumeItem(L1ItemId.ADENA, ConfigAlt.HOUSE_TAX_ADENA);
						final TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
						final Calendar cal = Calendar.getInstance(tz);
						cal.add(Calendar.DATE, ConfigAlt.HOUSE_TAX_INTERVAL);
						cal.set(Calendar.MINUTE, 0); // 分、秒は切り捨て
						cal.set(Calendar.SECOND, 0);
						house.setTaxDeadline(cal);
						HouseReading.get().updateHouse(house); // DBに書き込み
					} else {
						pc.sendPackets(new S_ServerMessage(189)); // 189
																	// \f1金幣不足。
					}
				}
			}
		}
	}

	private String[] makeHouseTaxStrings(final L1PcInstance pc, final L1NpcInstance npc) {
		final String name = npc.getNpcTemplate().get_name();
		String[] result;
		result = new String[] { name, "2000", "1", "1", "00" };
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan != null) {
			final int houseId = clan.getHouseId();
			if (houseId != 0) {
				final L1House house = HouseReading.get().getHouseTable(houseId);
				final int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					final Calendar cal = house.getTaxDeadline();
					final int month = cal.get(Calendar.MONTH) + 1;
					final int day = cal.get(Calendar.DATE);
					final int hour = cal.get(Calendar.HOUR_OF_DAY);
					result = new String[] { name, "2000", String.valueOf(month), String.valueOf(day), String.valueOf(hour) };
				}
			}
		}
		return result;
	}

	private String[] makeWarTimeStrings(final int castleId) {
		final L1Castle castle = CastleReading.get().getCastleTable(castleId);
		if (castle == null) {
			return null;
		}
		final Calendar warTime = castle.getWarTime();
		final int year = warTime.get(Calendar.YEAR);
		final int month = warTime.get(Calendar.MONTH) + 1;
		final int day = warTime.get(Calendar.DATE);
		final int hour = warTime.get(Calendar.HOUR_OF_DAY);
		final int minute = warTime.get(Calendar.MINUTE);
		String[] result;
		if (castleId == L1CastleLocation.OT_CASTLE_ID) {
			result = new String[] { String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(minute) };
		} else {
			result = new String[] { "", String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(minute) };
		}
		return result;
	}

	/*
	 * private String getYaheeAmulet(final L1PcInstance pc, final L1NpcInstance npc, final String s) { final int[] amuletIdList = { 20358, 20359, 20360, 20361, 20362, 20363, 20364, 20365
	 * }; int amuletId = 0; L1ItemInstance item = null; String htmlid = null; if (s.equalsIgnoreCase("1")) { amuletId = amuletIdList[0]; } else if (s.equalsIgnoreCase("2")) { amuletId =
	 * amuletIdList[1]; } else if (s.equalsIgnoreCase("3")) { amuletId = amuletIdList[2]; } else if (s.equalsIgnoreCase("4")) { amuletId = amuletIdList[3]; } else if
	 * (s.equalsIgnoreCase("5")) { amuletId = amuletIdList[4]; } else if (s.equalsIgnoreCase("6")) { amuletId = amuletIdList[5]; } else if (s.equalsIgnoreCase("7")) { amuletId =
	 * amuletIdList[6]; } else if (s.equalsIgnoreCase("8")) { amuletId = amuletIdList[7]; } if (amuletId != 0) { item = pc.getInventory().storeItem(amuletId, 1); if (item != null) {
	 * pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName())); // \f1%0が%1をくれました。 } for (final int id : amuletIdList) { if (id == amuletId) { break; }
	 * if (pc.getInventory().checkItem(id)) { pc.getInventory().consumeItem(id, 1); } } htmlid = ""; } return htmlid; }
	 * 
	 * private String getBarlogEarring(final L1PcInstance pc, final L1NpcInstance npc, final String s) { final int[] earringIdList = { 21020, 21021, 21022, 21023, 21024, 21025, 21026,
	 * 21027 }; int earringId = 0; L1ItemInstance item = null; String htmlid = null; if (s.equalsIgnoreCase("1")) { earringId = earringIdList[0]; } else if (s.equalsIgnoreCase("2")) {
	 * earringId = earringIdList[1]; } else if (s.equalsIgnoreCase("3")) { earringId = earringIdList[2]; } else if (s.equalsIgnoreCase("4")) { earringId = earringIdList[3]; } else if
	 * (s.equalsIgnoreCase("5")) { earringId = earringIdList[4]; } else if (s.equalsIgnoreCase("6")) { earringId = earringIdList[5]; } else if (s.equalsIgnoreCase("7")) { earringId =
	 * earringIdList[6]; } else if (s.equalsIgnoreCase("8")) { earringId = earringIdList[7]; } if (earringId != 0) { item = pc.getInventory().storeItem(earringId, 1); if (item != null) {
	 * pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName())); // \f1%0が%1をくれました。 } for (final int id : earringIdList) { if (id == earringId) { break;
	 * } if (pc.getInventory().checkItem(id)) { pc.getInventory().consumeItem(id, 1); } } htmlid = ""; } return htmlid; }
	 */

	private String[] makeUbInfoStrings(final int npcId) {
		final L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		return ub.makeUbInfoStrings();
	}

	private String talkToDimensionDoor(final L1PcInstance pc, final L1NpcInstance npc, final String s) {
		String htmlid = "";
		int protectionId = 0;
		int sealId = 0;
		int locX = 0;
		int locY = 0;
		short mapId = 0;
		if (npc.getNpcTemplate().get_npcId() == 80059) { // 次元の扉(土)
			protectionId = 40909;
			sealId = 40913;
			locX = 32773;
			locY = 32835;
			mapId = 607;
		} else if (npc.getNpcTemplate().get_npcId() == 80060) { // 次元の扉(風)
			protectionId = 40912;
			sealId = 40916;
			locX = 32757;
			locY = 32842;
			mapId = 606;
		} else if (npc.getNpcTemplate().get_npcId() == 80061) { // 次元の扉(水)
			protectionId = 40910;
			sealId = 40914;
			locX = 32830;
			locY = 32822;
			mapId = 604;
		} else if (npc.getNpcTemplate().get_npcId() == 80062) { // 次元の扉(火)
			protectionId = 40911;
			sealId = 40915;
			locX = 32835;
			locY = 32822;
			mapId = 605;
		}

		// 「中に入ってみる」「元素の支配者を近づけてみる」「通行証を使う」「通過する」
		if (s.equalsIgnoreCase("a")) {
			L1Teleport.teleport(pc, locX, locY, mapId, 5, true);
			htmlid = "";
		}
		// 「絵から突出部分を取り除く」
		else if (s.equalsIgnoreCase("b")) {
			final L1ItemInstance item = pc.getInventory().storeItem(protectionId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			htmlid = "";
		}
		// 「通行証を捨てて、この地をあきらめる」
		else if (s.equalsIgnoreCase("c")) {
			htmlid = "wpass07";
		}
		// 「続ける」
		else if (s.equalsIgnoreCase("d")) {
			if (pc.getInventory().checkItem(sealId)) { // 地の印章
				final L1ItemInstance item = pc.getInventory().findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
		}
		// 「そのままにする」「慌てて拾う」
		else if (s.equalsIgnoreCase("e")) {
			htmlid = "";
		}
		// 「消えるようにする」
		else if (s.equalsIgnoreCase("f")) {
			if (pc.getInventory().checkItem(protectionId)) { // 地の通行証
				pc.getInventory().consumeItem(protectionId, 1);
			}
			if (pc.getInventory().checkItem(sealId)) { // 地の印章
				final L1ItemInstance item = pc.getInventory().findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
			htmlid = "";
		}
		return htmlid;
	}

	private void getBloodCrystalByKarma(final L1PcInstance pc, final L1NpcInstance npc, final String s) {
		L1ItemInstance item = null;

		// 「ブラッドクリスタルの欠片を1個ください」
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (500 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// ヤヒの姿を記憶するのが難しくなります。
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// 「ブラッドクリスタルの欠片を10個ください」
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (5000 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// ヤヒの姿を記憶するのが難しくなります。
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// 「ブラッドクリスタルの欠片を100個ください」
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (50000 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// ヤヒの姿を記憶するのが難しくなります。
			pc.sendPackets(new S_ServerMessage(1081));
		}
	}

	private void getSoulCrystalByKarma(final L1PcInstance pc, final L1NpcInstance npc, final String s) {
		L1ItemInstance item = null;

		// 「ソウルクリスタルの欠片を1個ください」
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (-500 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// バルログの冷笑を感じ悪寒が走ります。
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// 「ソウルクリスタルの欠片を10個ください」
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (-5000 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// バルログの冷笑を感じ悪寒が走ります。
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// 「ソウルクリスタルの欠片を100個ください」
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (-50000 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// バルログの冷笑を感じ悪寒が走ります。
			pc.sendPackets(new S_ServerMessage(1080));
		}
	}

	/*
	 * private boolean usePolyScroll(final L1PcInstance pc, final int itemId, final String s) { int time = 0; if ((itemId == 40088) || (itemId == 40096)) { // 変身スクロール、象牙の塔の変身スクロール time =
	 * 1800; } else if (itemId == 140088) { // 祝福された変身スクロール time = 2100; } final L1PolyMorph poly = PolyTable.get().getTemplate(s); final L1ItemInstance item =
	 * pc.getInventory().findItemId(itemId); boolean isUseItem = false; if ((poly != null) || s.equals("none")) { if (s.equals("none")) { if ((pc.getTempCharGfx() == 6034) ||
	 * (pc.getTempCharGfx() == 6035)) { isUseItem = true; } else { pc.removeSkillEffect(SHAPE_CHANGE); isUseItem = true; } } else if ((poly.getMinLevel() <= pc.getLevel()) || pc.isGm()) {
	 * L1PolyMorph.doPoly(pc, poly.getPolyId(), time, L1PolyMorph.MORPH_BY_ITEMMAGIC); isUseItem = true; } } if (isUseItem) { pc.getInventory().removeItem(item, 1); } else {
	 * pc.sendPackets(new S_ServerMessage(181)); // \f1そのようなモンスターには変身できません。 } return isUseItem; }
	 */

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

	/**
	 * 顯示道具製造清單
	 *
	 * @param pc
	 * @param npc
	 */
	private void ShowCraftList(final L1PcInstance pc, final L1NpcInstance npc) {
		String msg0 = "";
		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";
		String msg5 = "";
		String msg6 = "";
		String msg7 = "";
		String msg8 = "";
		String msg9 = "";
		String msg10 = "";
		String msg11 = "";
		String msg12 = "";
		String msg13 = "";
		String msg14 = "";
		String msg15 = "";
		String msg16 = "";
		String msg17 = "";
		String msg18 = "";
		String msg19 = "";
		String msg20 = "";
		String msg21 = "";
		String msg22 = "";
		String msg23 = "";
		String msg24 = "";
		String msg25 = "";
		String msg26 = "";
		String msg27 = "";
		String msg28 = "";
		String msg29 = "";
		String msg30 = "";
		String msg31 = "";
		String msg32 = "";
		String msg33 = "";
		String msg34 = "";
		String msg35 = "";
		String msg36 = "";
		String msg37 = "";
		String msg38 = "";
		String msg39 = "";
		String msg40 = "";

		final int npcid = npc.getNpcId();

		final Map<String, String> craftlist = L1BlendTable.getInstance().get_craftlist();

		if (!craftlist.isEmpty()) {
			msg0 = craftlist.get(npcid + "A");
			msg1 = craftlist.get(npcid + "B");
			msg2 = craftlist.get(npcid + "C");
			msg3 = craftlist.get(npcid + "D");
			msg4 = craftlist.get(npcid + "E");
			msg5 = craftlist.get(npcid + "F");
			msg6 = craftlist.get(npcid + "G");
			msg7 = craftlist.get(npcid + "H");
			msg8 = craftlist.get(npcid + "I");
			msg9 = craftlist.get(npcid + "J");
			msg10 = craftlist.get(npcid + "K");
			msg11 = craftlist.get(npcid + "L");
			msg12 = craftlist.get(npcid + "M");
			msg13 = craftlist.get(npcid + "N");
			msg14 = craftlist.get(npcid + "O");
			msg15 = craftlist.get(npcid + "P");
			msg16 = craftlist.get(npcid + "Q");
			msg17 = craftlist.get(npcid + "R");
			msg18 = craftlist.get(npcid + "S");
			msg19 = craftlist.get(npcid + "T");
			msg20 = craftlist.get(npcid + "U");
			msg21 = craftlist.get(npcid + "V");
			msg22 = craftlist.get(npcid + "W");
			msg23 = craftlist.get(npcid + "X");
			msg24 = craftlist.get(npcid + "Y");
			msg25 = craftlist.get(npcid + "Z");
			msg26 = craftlist.get(npcid + "a1");
			msg27 = craftlist.get(npcid + "a2");
			msg28 = craftlist.get(npcid + "a3");
			msg29 = craftlist.get(npcid + "a4");
			msg30 = craftlist.get(npcid + "a5");
			msg31 = craftlist.get(npcid + "a6");
			msg32 = craftlist.get(npcid + "a7");
			msg33 = craftlist.get(npcid + "a8");
			msg34 = craftlist.get(npcid + "a9");
			msg35 = craftlist.get(npcid + "a10");
			msg36 = craftlist.get(npcid + "a11");
			msg37 = craftlist.get(npcid + "a12");
			msg38 = craftlist.get(npcid + "a13");
			msg39 = craftlist.get(npcid + "a14");
			msg40 = craftlist.get(npcid + "a15");
		}

		final String msgs[] = { msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11, msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21, msg22,
				msg23, msg24, msg25, msg26, msg27, msg28, msg29, msg30, msg31, msg32, msg33, msg34, msg35, msg36, msg37, msg38, msg39, msg40 };

		if (msg0 != null) {// 至少有設定一項道具製造資料
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "smithitem1", msgs));
		} else {
			pc.sendPackets(new S_SystemMessage("沒有可以製作的道具。"));
			return;
		}

	}
}
