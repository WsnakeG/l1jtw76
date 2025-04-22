package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.BROADCAST_CARD;
import static com.lineage.server.model.skill.L1SkillId.CHAT_STOP;
import static com.lineage.server.model.skill.L1SkillId.SILENCE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_SILENCE;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRecord;
import com.lineage.data.event.BroadcastSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.echo.to.TServer;
import com.lineage.server.BroadcastController;
import com.lineage.server.command.GMCommands;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.T_CraftConfigTable;
import com.lineage.server.datatables.T_CraftConfigTable.NewL1NpcMakeItemAction;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.ClanAllianceReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.datatables.lock.LogChatReading;
import com.lineage.server.model.L1Alliance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1ObjectAmount;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.model.npc.action.L1NpcAction;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_ChatClanAlliance;
import com.lineage.server.serverpackets.S_ClanUpdate;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_Expression;
import com.lineage.server.serverpackets.S_GmMessage;
import com.lineage.server.serverpackets.S_ItemCraftList;
import com.lineage.server.serverpackets.S_LotteryMessage;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_NpcChatShouting;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_Party;
import com.lineage.server.serverpackets.S_PassiveSpells;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShowLotteryList;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.serverpackets.ability.S_ChaDetails;
import com.lineage.server.serverpackets.ability.S_ConDetails;
import com.lineage.server.serverpackets.ability.S_DexDetails;
import com.lineage.server.serverpackets.ability.S_IntDetails;
import com.lineage.server.serverpackets.ability.S_StrDetails;
import com.lineage.server.serverpackets.ability.S_WisDetails;
import com.lineage.server.serverpackets.chat.S_ChatResult;
import com.lineage.server.serverpackets.chat.S_ChatText;
import com.lineage.server.serverpackets.doll.S_DollCompoundInit;
import com.lineage.server.serverpackets.doll.S_DollCompoundResult;
import com.lineage.server.serverpackets.doll.S_DollCompoundUseingDoll;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1LotteryWarehouse;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CodedInputStream;
import com.lineage.server.utils.IntRange;
import com.lineage.server.utils.InvalidProtocolBufferException;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.utils.StringUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldNpc;

public class C_ItemCraft extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ItemCraft.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			// 使用者
			final L1PcInstance pc = client.getActiveChar();

			/*
			 * if (pc == null) { // 角色為空 return; }
			 */

			final int type0 = readH();

			if (type0 == 54) {// 驗證客戶端製作物品緩存的雜湊碼
				readH();
				final byte[] sha1 = readCraftB();
				final String sha1String = T_CraftConfigTable.update(sha1);
				if (sha1String.equals(T_CraftConfigTable.get().getSHAkey())) {
					pc.sendPackets(new S_ItemCraftList(3));
				} else {
					final Collection<NewL1NpcMakeItemAction> all = T_CraftConfigTable.get().getNpcMakeItemList().values();
					boolean first = true;
					S_ItemCraftList stemCraftList = null;
					for (final NewL1NpcMakeItemAction l1NpcMakeItemAction : all) {
						if (first) {
							stemCraftList = new S_ItemCraftList(l1NpcMakeItemAction, first);
							pc.sendPackets(stemCraftList);
							first = false;
						} else {
							stemCraftList = new S_ItemCraftList(l1NpcMakeItemAction, false);
							pc.sendPackets(stemCraftList);
						}
					}
					pc.sendPackets(new S_ItemCraftList(null, false));
				}

			} else if (type0 == 56) {// 呼叫製作清單
				readH();
				final int npcObjId = readCraft();
				final L1Object obj = World.get().findObject(npcObjId);
				if ((obj != null) && ((obj instanceof L1NpcInstance))) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final HashMap<Integer, NewL1NpcMakeItemAction> npcMakeItemActionMap = T_CraftConfigTable.get().getNpcMakeItemActionList(npc.getNpcId());
					if (npcMakeItemActionMap != null) {
						pc.sendPackets(new S_ItemCraftList(npcMakeItemActionMap.values()));
					} else {
						pc.sendPackets(new S_ItemCraftList(null));
					}
				} else {
					pc.sendPackets(new S_ItemCraftList(null));
				}

			} else if (type0 == 58) {// 製作物品
				readH();
				final int npcObjId = readCraft();
				final int actionId = readCraft();
				final int changeCount = readCraft();

				final L1Object npc = World.get().findObject(npcObjId);
				if (npc == null) {
					System.out.println("兌換對象不存在世界");
					return;
				}
				if (!(npc instanceof L1NpcInstance)) {
					System.out.println("兌換對象不是NPC");
					return;
				}

				final int difflocx = Math.abs(pc.getX() - npc.getX());
				final int difflocy = Math.abs(pc.getY() - npc.getY());
				if ((pc.getMapId() != npc.getMapId()) || (difflocx > 10) || (difflocy > 10)) {
					System.out.println("與兌換NPC相隔太遠");
					return;
				}
				final L1NpcInstance npcObj = (L1NpcInstance) npc;
				final HashMap<Integer, NewL1NpcMakeItemAction> npcMakeItemActions = T_CraftConfigTable.get().getNpcMakeItemActionList(npcObj.getNpcId());
				if (npcMakeItemActions == null) {
					System.out.println("兌換NPC無對應的兌換配置列表");
					return;
				}
				final T_CraftConfigTable.NewL1NpcMakeItemAction npcMakeItemAction = npcMakeItemActions.get(Integer.valueOf(actionId));
				if (npcMakeItemAction == null) {
					System.out.println("兌換NPC的兌換配置列表取回錯誤");
					return;
				}

				final ArrayList<Integer> polyIds = npcMakeItemAction.getCraftPolyList();
				if ((polyIds != null) && (polyIds.indexOf(Integer.valueOf(pc.getTempCharGfx())) == -1)) {
					System.out.println("玩家變身不符合兌換要求");
					return;
				}

				if (!npcMakeItemAction.getAmountLevelRange().includes(pc.getLevel())) {
					System.out.println("玩家等級不符合兌換要求");
					return;
				}

				if (!npcMakeItemAction.getAmountLawfulRange().includes(pc.getLawful())) {
					System.out.println("玩家正義值不符合兌換要求");
					return;
				}

				if (!npcMakeItemAction.getAmountKarmaRange().includes(pc.getKarma())) {
					System.out.println("玩家友好度不符合兌換要求");
					return;
				}

				final ArrayList<Integer> materialItemDescs = new ArrayList<Integer>();
				final HashMap<Integer, Integer> aidCounts = new HashMap<Integer, Integer>();
				while (true) {
					final byte[] materialItemArray = readCraftB();
					if (materialItemArray == null) {
						break;
					}
					final C_Empty pack = new C_Empty(materialItemArray);
					pack.readCraft();
					final int materialItemDescId = pack.readCraft();
					materialItemDescs.add(Integer.valueOf(materialItemDescId));
					if (!pack.jdField_do()) {
						final int aidCount = pack.readCraft();
						if (aidCount <= 0) {
							continue;
						}
						aidCounts.put(Integer.valueOf(materialItemDescId), Integer.valueOf(aidCount));
					}
				}

				final List<L1ObjectAmount<Integer>> materials = npcMakeItemAction.getAmountMeterialList();
				final List<L1ObjectAmount<Integer>> aidMaterials = npcMakeItemAction.getAmountAidMeterialList();

				final int materialsSize = materials.size();
				final int aidMaterialsSize = aidMaterials.size();
				final int materialsSumCount = materialsSize + aidMaterialsSize;
				if (materialItemDescs.size() != materialsSumCount) {
					System.out.println("玩家材料列表數不符合兌換要求");
					return;
				}

				final ArrayList<L1ObjectAmount<Integer>> materialItemIds = new ArrayList<L1ObjectAmount<Integer>>();
				int descId = 0;
				L1ObjectAmount<Integer> materialObj = null;
				L1ObjectAmount<Integer> substituteObj = null;
				L1ItemInstance materialItemObj = null;
				ArrayList<L1ObjectAmount<Integer>> substitutes = null;
				final ItemTable itemTable = ItemTable.get();
				for (int i = 0; i < materialsSize; i++) {
					descId = materialItemDescs.get(i).intValue();
					materialObj = materials.get(i);
					materialItemObj = itemTable.createItem(materialObj.getObject().intValue());
					if (materialItemObj == null) {
						pc.sendPackets(new S_ItemCraftList(false, null));
						System.out.println("材料不存在");
						return;
					}

					if (descId == materialItemObj.getItem().getItemDescId()) {
						materialItemIds.add(materialObj);
					} else {
						substitutes = materialObj.getAmountList();
						if (substitutes == null) {
							System.out.println("玩家材料列表數不符合兌換要求");
							return;
						}
						for (final L1ObjectAmount<Integer> l1ObjectAmount : substitutes) {
							materialItemObj = itemTable.createItem(l1ObjectAmount.getObject().intValue());
							if (materialItemObj == null) {
								System.out.println("可替代材料不存在");
								return;
							}
							if (descId == materialItemObj.getItem().getItemDescId()) {
								substituteObj = l1ObjectAmount;
								materialItemIds.add(substituteObj);
								break;
							}
						}
						if (substituteObj == null) {
							System.out.println("玩家缺少兌換材料");
							return;
						}
					}
				}

				final ArrayList<L1ObjectAmount<Integer>> aidMaterialItemIds = new ArrayList<L1ObjectAmount<Integer>>();
				for (int i = 0; i < aidMaterialsSize; i++) {
					descId = materialItemDescs.get(materialsSize + i).intValue();
					materialObj = aidMaterials.get(i);
					materialItemObj = itemTable.createItem(materialObj.getObject().intValue());
					if (materialItemObj == null) {
						// i.log(Level.SEVERE,
						// String.format("材料[%d]不存在.玩家名稱:[%s]", new Object[] {
						// materialObj.jdMethod_try(), pc.aD() }));
						return;
					}

					if (descId == materialItemObj.getItem().getItemDescId()) {
						if (aidCounts.get(Integer.valueOf(descId)) != null) {
							aidMaterialItemIds.add(materialObj);
						}
					} else {
						// i.log(Level.SEVERE,
						// String.format("玩家缺少兌換材料.玩家名稱:[%s]", new Object[] {
						// pc.aD() }));
						return;
					}
				}

				final L1PcInventory pcInv = pc.getInventory();
				L1ItemInstance[] items = null;
				L1ItemInstance item = null;
				L1ItemInstance itemTemp = null;
				final ArrayList<L1ObjectAmount<Integer>> delItemObjIds = new ArrayList<L1ObjectAmount<Integer>>();
				boolean flag = false;
				int tempCount;
				for (final L1ObjectAmount<Integer> material : materialItemIds) {
					items = pcInv.findItemsId(material.getObject().intValue());
					itemTemp = ItemTable.get().createItem(material.getObject().intValue());
					if (itemTemp.isStackable()) {
						flag = false;
						for (int i = 0; i < items.length; i++) {
							item = items[i];
							if ((item.getEnchantLevel() != material.getAmountEnchantLevel()) || ((material.getAmountBless() != 3) && (item.getBless() != material.getAmountBless()))
									|| (item.getCount() < (material.getAmount() * changeCount))) {
								continue;
							}
							delItemObjIds.add(new L1ObjectAmount<Integer>(Integer.valueOf(item.getItemId()), material.getAmount() * changeCount));
							flag = true;
							break;
						}
						if (!flag) {
							// i.log(Level.SEVERE,
							// String.format("驗證必要兌換材料不足.玩家名稱:[%s]", new
							// Object[] { pc.aD() }));
							return;
						}
					} else {
						tempCount = 0;
						for (int i = 0; i < items.length; i++) {
							item = items[i];
							if ((item.getEnchantLevel() != material.getAmountEnchantLevel()) || ((material.getAmountBless() != 3) && (item.getBless() != material.getAmountBless()))
									|| (tempCount >= (material.getAmount() * changeCount))) {
								continue;
							}
							delItemObjIds.add(new L1ObjectAmount<Integer>(Integer.valueOf(item.getItemId()), 1));
							tempCount++;
						}

						if (tempCount < (material.getAmount() * changeCount)) {
							// i.log(Level.SEVERE,
							// String.format("驗證必要兌換材料不足.玩家名稱:[%s]", new
							// Object[] { pc.aD() }));
							return;
						}
					}
				}

				ArrayList<L1ObjectAmount<Integer>> delAidItemObjIds = null;
				int tempCount1;
				if ((npcMakeItemAction.getSucceedRandom() < 1000000) && (aidMaterialItemIds != null)) {
					delAidItemObjIds = new ArrayList<L1ObjectAmount<Integer>>();
					for (final L1ObjectAmount<Integer> material : aidMaterialItemIds) {
						items = pcInv.findItemsId(material.getObject().intValue());
						itemTemp = ItemTable.get().createItem(material.getObject().intValue());
						if (itemTemp.isStackable()) {
							flag = false;
							for (int i = 0; i < items.length; i++) {
								item = items[i];
								if ((item.getEnchantLevel() != material.getAmountEnchantLevel()) || ((material.getAmountBless() != 3) && (item.getBless() != material.getAmountBless()))
										|| (item.getCount() < aidCounts.get(Integer.valueOf(itemTemp.getItem().getItemDescId())).intValue())) {
									continue;
								}
								delAidItemObjIds.add(new L1ObjectAmount<Integer>(Integer.valueOf(item.getItemId()), aidCounts.get(Integer.valueOf(itemTemp.getItem().getItemDescId()))
										.intValue()));
								flag = true;
								break;
							}

							if (!flag) {
								pc.sendPackets(new S_SystemMessage("您的加成材料不足."));
								pc.sendPackets(new S_ItemCraftList(false, null));
								// i.log(Level.SEVERE,
								// String.format("驗證加成兌換材料不足.玩家名稱:[%s]", new
								// Object[] { pc.aD() }));
								return;
							}
						} else {
							tempCount1 = 0;
							for (int i = 0; i < items.length; i++) {
								item = items[i];
								if ((item.getEnchantLevel() != material.getAmountEnchantLevel()) || ((material.getAmountBless() != 3) && (item.getBless() != material.getAmountBless()))
										|| (tempCount1 >= aidCounts.get(Integer.valueOf(itemTemp.getItem().getItemDescId())).intValue())) {
									continue;
								}
								delAidItemObjIds.add(new L1ObjectAmount<Integer>(Integer.valueOf(item.getItemId()), 1));
								tempCount1++;
							}

							if (tempCount1 < aidCounts.get(Integer.valueOf(itemTemp.getItem().getItemDescId())).intValue()) {
								pc.sendPackets(new S_SystemMessage("您的加成材料不足."));
								pc.sendPackets(new S_ItemCraftList(false, null));
								// i.log(Level.SEVERE,
								// String.format("驗證加成兌換材料不足.玩家名稱:[%s]", new
								// Object[] { pc.aD() }));
								return;
							}
						}
					}
				}

				for (final L1ObjectAmount<Integer> delItemAmount : delItemObjIds) {
					if (!pcInv.consumeItem(delItemAmount.getObject().intValue(), delItemAmount.getAmount())) {
						pc.sendPackets(new S_SystemMessage("您的材料不足."));
						pc.sendPackets(new S_ItemCraftList(false, null));
						// i.log(Level.SEVERE,
						// String.format("刪除必要兌換材料失敗.玩家名稱:[%s]", new Object[] {
						// pc.aD() }));
						return;
					}
				}
				int sumAidCount = 0;
				if (delAidItemObjIds != null) {
					for (final L1ObjectAmount<Integer> delAidItemAmount : delAidItemObjIds) {
						sumAidCount += delAidItemAmount.getAmount();
						if (!pcInv.consumeItem(delAidItemAmount.getObject().intValue(), delAidItemAmount.getAmount())) {
							pc.sendPackets(new S_SystemMessage("您的加成材料不足."));
							pc.sendPackets(new S_ItemCraftList(false, null));
							// i.log(Level.SEVERE,
							// String.format("刪除加成材料失敗.玩家名稱:[%s]", new Object[]
							// { pc.aD() }));
							return;
						}
					}
				}
				
				final int random = RandomArrayList.getInc(1000000, 1);
				if (random <= (npcMakeItemAction.getSucceedRandom() + (10000 * sumAidCount))) {
					final List<L1ObjectAmount<Integer>> successItems = npcMakeItemAction.getAmountItemList();
					
//					// 每秒打印一次，共三次
//	                for (int i = 1; i <= 3; i++) {
//	                    try {
//	                        // 延遲一秒 (1000 毫秒)
//	                        Thread.sleep(1000);
//	                        pc.sendPackets(new S_SystemMessage("努力製作中！！！"));
//	                    } catch (InterruptedException e) {
//	                        e.printStackTrace();
//	                    }
//	                }
//	                
//	                try {
//                        // 延遲一秒 (1000 毫秒)
//                        Thread.sleep(1500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

					final List<L1ItemInstance> giveItemObjs = a(pc, pcInv, changeCount, successItems, npcMakeItemAction.isAmountBroad(), npcMakeItemAction.getSystemMessage());

					final int r = RandomArrayList.getInt(npcMakeItemAction.getAmountRandom());
					int change = 0;
					for (final L1ObjectAmount<Integer> objectAmount : npcMakeItemAction.getAmountRandomItemList()) {
						change += objectAmount.getAmountRandom();
						if (r < change) {
							giveItemObjs.addAll(a(pc, pcInv, changeCount, successItems, npcMakeItemAction.isAmountBroad(), npcMakeItemAction.getSystemMessage()));
							break;
						}
					}

					pc.sendPackets(new S_ItemCraftList(true, giveItemObjs));

					final L1NpcAction actionOnSucceed = npcMakeItemAction.getAmountSuceedAction();
					if (actionOnSucceed != null) {
						final L1NpcHtml result = actionOnSucceed.execute(String.format("request craft%d", new Object[] { Integer.valueOf(npcMakeItemAction.getAmountActionID()) }), pc,
								npc, null);
						if (result != null) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), result));
						}
					}

					if (npcMakeItemAction.isAmountBroad()) {
						final String msg = npcMakeItemAction.getSystemMessage();
						final int msgId = npcMakeItemAction.getSystemMessageID();
						if ((msg != null) && (msg.length() > 0)) {
							/*
							 * L1Item item1 = ItemTable.get().getTemplate(successItems .getObject().intValue()); World.get().broadcastPacketToAll( new
							 * S_PacketBoxGree(0x02,String.format(msg, new Object[] { pc.getName(), item1.getNameId() })));
							 */

						} else if (msgId != -1) {
							World.get().broadcastPacketToAll(new S_ServerMessage(msgId));
						}
					}
					giveItemObjs.clear();

				} else {
					final List<L1ObjectAmount<Integer>> failItems = npcMakeItemAction.getFailItemList();
//					final List<L1ItemInstance> giveItemObjs = a(pc, pcInv, changeCount, failItems, npcMakeItemAction.isAmountBroad(), npcMakeItemAction.getSystemMessage());
					
//					// 每秒打印一次，共三次
//	                for (int i = 1; i <= 3; i++) {
//	                    try {
//	                        // 延遲一秒 (1000 毫秒)
//	                        Thread.sleep(1000);
//	                        pc.sendPackets(new S_SystemMessage("努力製作中！！！"));
//	                    } catch (InterruptedException e) {
//	                        e.printStackTrace();
//	                    }
//	                }
//	                
//	                try {
//                        // 延遲一秒 (1000 毫秒)
//                        Thread.sleep(1500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
					
					final List<L1ItemInstance> giveItemObjs = a(pc, pcInv, changeCount, failItems, false, npcMakeItemAction.getSystemMessage());
					final int r = RandomArrayList.getInt(npcMakeItemAction.getFailRandom());
					int change = 0;
					for (final L1ObjectAmount<Integer> msg : npcMakeItemAction.getFailAmountRandomItemList()) {
						change += msg.getAmountRandom();
						if (r < change) {
							giveItemObjs.addAll(a(pc, pcInv, changeCount, failItems, npcMakeItemAction.isAmountBroad(), npcMakeItemAction.getSystemMessage()));
							break;
						}
					}
					final L1NpcAction actionOnFail = npcMakeItemAction.getFailAction();
					if (actionOnFail != null) {
						final L1NpcHtml result = actionOnFail.execute(String.format("request craft%d", new Object[] { Integer.valueOf(npcMakeItemAction.getAmountActionID()) }), pc, npc,
								null);
						if (result != null) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), result));
						}
					}

					final String msg1 = npcMakeItemAction.getFailMessage();
					final int msgId = npcMakeItemAction.getFailMessageID();
					if ((msg1 != null) && (msg1.length() > 0)) {
						pc.sendPackets(new S_SystemMessage(msg1));
					} else if (msgId != -1) {
						pc.sendPackets(new S_ServerMessage(msgId));
					}
					pc.sendPackets(new S_ItemCraftList(false, giveItemObjs));
				}

			} else if (type0 == 100) { // 潘朵拉幸運抽獎卷兌換轉運卷
				int size = readH();
				size = Math.abs(size);
				size -= 2;
				size /= 2;
				if (size > 0) {
					readH();
					final ArrayList<L1LotteryWarehouse> lotteryList = pc.getLottery().getList();
					L1LotteryWarehouse lottery = null;
					for (int i = 0; i < size; i++) {
						final int interval = readC();
						if (interval == 16) {
							int index = readC();
							index = Math.abs(index);
							lottery = lotteryList.get(index);
							if (lottery != null) {
								L1ItemInstance item = null;
								item = pc.getLottery().getLotteryTicket(pc, lottery);
								if (item != null) {
									_log.info("玩家[" + pc.getName() + "]將抽獎倉庫中的物品[" + lottery.getItemName() + "]轉換為轉運抽獎卷。");
								} else {
									size--;
								}
							}
						}
					}
					pc.sendPackets(new S_LotteryMessage(3728, size));
					pc.sendPackets(new S_ShowLotteryList(pc.getLottery().getList()));
				}
			} else if (type0 == 0x007a) { // 請求呼叫魔法娃娃合成介面
											// (驗證客戶端alchemyInfo.dat的sha1雜湊碼)
				if (pc != null) {
					// 封包傳遞有效的數據長度
					final int len = this.readH();

					// 例外狀況長度小於等於0
					if (len <= 0) {
						return;
					}

					// 讀取指定長度的數據
					final byte[] data = this.readByte(len);

					// 例外狀況 實際數據不足長度 等等 例外狀況
					if (data == null) {
						return;
					}
					this.checkDollCompoundData(pc, data);
				}
			} else if (type0 == 0x007c) { // 合成魔法娃娃
				if (pc != null) {
					// 封包傳遞有效的數據長度
					final int len = this.readH();

					// 例外狀況長度小於等於0
					if (len <= 0) {
						return;
					}

					// 讀取指定長度的數據
					final byte[] data = this.readByte(len);

					// 例外狀況 實際數據不足長度 等等 例外狀況
					if (data == null) {
						return;
					}
					this.compoundDoll(pc, data);
				}
			} else if (type0 == 319) { // 就是在按 ALT + 1 ALT + 2 ALT + 3 ALT + 4
				if (pc != null) {
					// 封包傳遞有效的數據長度
					final int len = this.readH();

					// 例外狀況長度小於等於0
					if (len <= 0) {
						return;
					}

					// 讀取指定長度的數據
					final byte[] data = this.readByte(len);

					// 例外狀況 實際數據不足長度 等等 例外狀況
					if (data == null) {
						return;
					}
					try {
						this._input.reset(data);
						@SuppressWarnings("unused")
						int var1 = -1;
						int var2 = -1;
						boolean done = false;
						while (!done) {
							final int tag = this._input.readTag();
							switch (tag) {
							case 0:
								done = true;
								break;

							case 8: {
								var1 = _input.readInt32();
								break;
							}

							case 16: {
								var2 = _input.readInt32();
								break;
							}

							default: {
								if (!this._input.mergeFieldFrom(tag, this._input)) {
									done = true;
								}
								break;
							}
							}
						}

						// 如果 done為false 一律不執行
						if (!done) {
							return;
						}

						pc.sendPacketsAll(new S_Expression(pc.getId(), var2));

					} catch (final InvalidProtocolBufferException e) {
						// 發生異常
						_log.error(e.getLocalizedMessage(), e);
					} catch (final Exception e) {
						// 發生異常
						_log.error(e.getLocalizedMessage(), e);
					} finally {

					}
				}
			} else if (type0 == 0x0142) { // 血盟加入
				final int length = readH();
				// int chatIndex = readBigIntAt(1).intValue();
				String clanname = readSAt(1);
				// System.out.println("加入血盟名稱："+clanname);
				if (clanname == null || clanname.length() <= 0) {
					pc.sendPackets(new S_SystemMessage("加入的血盟名稱錯誤！"));
					return;
				}
				L1Clan clan = WorldClan.get().getClan(clanname);
				if (clan == null) {
					pc.sendPackets(new S_SystemMessage("加入的血盟不存在或血盟名稱不正確!"));
					return;
				}
				final int requestType = this.readInteger(); // 10 02
				switch (requestType) {
				case 0:
					if (clan.getJoin_open_state() && clan.getJoin_state() == 0) { // 即時加入
						pc.setClanid(clan.getClanId());
						pc.setClanname(clan.getClanName());
						pc.setClanRank(L1Clan.NORMAL_CLAN_RANK_ATTEND);
						pc.save(); // 資料存檔
						clan.addMemberName(pc.getName());
						pc.sendPackets(new S_ServerMessage(95, clan.getClanName()));
						pc.sendPackets(new S_PassiveSpells(S_PassiveSpells.ClanNameAndRank, pc));
						pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, clan.getShowEmblem()));
						pc.sendPackets(new S_ClanUpdate(pc.getId(), clan.getClanName(), pc.getClanRank()));
					} else if (clan.getJoin_open_state() && clan.getJoin_state() == 1) { // 允許加入
						// 例外狀況長度小於等於0
						if (length <= 0) {
							return;
						}
						// 讀取指定長度的數據
						final byte[] data = this.readByte(length);

						// 例外狀況 實際數據不足長度 等等 例外狀況
						if (data == null) {
							return;
						}
						joinClan(pc, clanname);

					} else if (clan.getJoin_open_state() && clan.getJoin_state() == 2) { // 暗號加入
						pc.sendPackets(new S_PassiveSpells(S_PassiveSpells.CLANJOIN, clan));
					}
					break;
				case 1:
					break;
				case 2:
					this.readC();// 1a
					this.readC();// 00
					final String passworld = this.readPB_SHA(this.readSHA());
					if (passworld.equalsIgnoreCase(clan.getJoin_password())) {
						clan.addMemberName(pc.getName());
						pc.setClanid(clan.getClanId());
						pc.setClanname(clan.getClanName());
						pc.setClanRank(L1Clan.NORMAL_CLAN_RANK_ATTEND);
						pc.save(); // 資料存檔
						pc.sendPackets(new S_ServerMessage(95, clan.getClanName()));
						pc.sendPackets(new S_PassiveSpells(S_PassiveSpells.ClanNameAndRank, pc));
						pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, clan.getShowEmblem()));
						pc.sendPackets(new S_ClanUpdate(pc.getId(), clan.getClanName(), pc.getClanRank()));
					} else {
						pc.sendPackets(new S_SystemMessage("密碼錯誤。"));
					}
					break;

				}

				// if (pc != null) {
				// // 封包傳遞有效的數據長度
				// final int len = this.readH();
				//
				// // 例外狀況長度小於等於0
				// if (len <= 0) {
				// return;
				// }
				//
				// // 讀取指定長度的數據
				// final byte[] data = this.readByte(len);
				//
				// // 例外狀況 實際數據不足長度 等等 例外狀況
				// if (data == null) {
				// return;
				// }
				// joinClan(pc, data);
				// }
			} else if (type0 == 326) { // 血盟設置修改
				int lenth = readH();// 4
				int joinOpenState = readBigIntAt(1).intValue();
				int joinstate = readBigIntAt(2).intValue();
				L1Clan clan = pc.getClan();
				if (clan != null) {
					if (clan.getLeaderId() == pc.getId()) {
						clan.setJoin_open_state(joinOpenState == 1);
						clan.setJoin_state(joinstate);
					}
					if (lenth > 4) {
						byte[] password = readByteAt(3);
						String passwordString = StringUtil.decode(password);
						clan.setJoin_password(passwordString);
					}
					ClanReading.get().updateClan(clan);
					pc.sendPackets(new S_PassiveSpells(S_PassiveSpells.CLANCONFIG, pc));
				}
			} else if (type0 == 338) { // 組隊勳章設定
				if (pc != null) {
					// 封包傳遞有效的數據長度
					final int len = this.readH();

					// 例外狀況長度小於等於0
					if (len <= 0) {
						return;
					}

					// 讀取指定長度的數據
					final byte[] data = this.readByte(len);

					// 例外狀況 實際數據不足長度 等等 例外狀況
					if (data == null) {
						return;
					}
					try {
						this._input.reset(data);
						int var1 = -1;
						int var2 = -1;
						boolean done = false;
						while (!done) {
							final int tag = this._input.readTag();
							switch (tag) {
							case 0:
								done = true;
								break;

							case 8: {
								var1 = _input.readInt32();
								break;
							}

							case 16: {
								var2 = _input.readInt32();
								break;
							}

							default: {
								if (!this._input.mergeFieldFrom(tag, this._input)) {
									done = true;
								}
								break;
							}
							}
						}

						// 如果 done為false 一律不執行
						if (!done) {
							return;
						}

						if (pc.isInParty()) {
							for (L1PcInstance member : pc.getParty().getMembers()) {
								member.sendPackets(new S_Party(var1, var2));
							}
						}

					} catch (final InvalidProtocolBufferException e) {
						// 發生異常
						_log.error(e.getLocalizedMessage(), e);
					} catch (final Exception e) {
						// 發生異常
						_log.error(e.getLocalizedMessage(), e);
					} finally {

					}
				}
			} else if (type0 == 484) { // 請求更新能力資訊
//				System.out.println("test:" + type0);
				// 封包傳遞有效的數據長度
				final int len = this.readH();

				// 例外狀況長度小於等於0
				if (len <= 0) {
					return;
				}

				// 讀取指定長度的數據
				final byte[] data = this.readByte(len);

				// 例外狀況 實際數據不足長度 等等 例外狀況
				if (data == null) {
					return;
				}
				requestChatChangeAbilityDetails(client, pc, data);
			} else if (type0 == 514) { // 使用聊天
				if (pc != null) {
					// 封包傳遞有效的數據長度
					final int len = this.readH();

					// 例外狀況長度小於等於0
					if (len <= 0) {
						return;
					}

					// 讀取指定長度的數據
					final byte[] data = this.readByte(len);

					// 例外狀況 實際數據不足長度 等等 例外狀況
					if (data == null) {
						return;
					}
					this.requestChat(pc, client, data);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	public List<L1ItemInstance> a(final L1PcInstance pc, final L1PcInventory pcInv, final int changeCount, final List<L1ObjectAmount<Integer>> amounts, boolean ok, String msg) {
		L1ItemInstance giveItemObj = null;
		final List<L1ItemInstance> giveItemObjs = new ArrayList<L1ItemInstance>();
		for (final L1ObjectAmount<Integer> giveItem : amounts) {
			giveItemObj = ItemTable.get().createItem(giveItem.getObject().intValue());
			final int count = (int) giveItem.getAmount();
			final int enchantLevel = giveItem.getAmountEnchantLevel();
			final int bless = giveItem.getAmountBless();
			if (giveItemObj != null) {
				if (pcInv.checkAddItem(giveItemObj, count) == 0) {
					if (giveItemObj.isStackable()) {
						giveItemObj.setCount(count * changeCount);
						giveItemObj.setIdentified(true);
						giveItemObj.setBless(bless);

						// giveItemObj.setInv(pcInv); 透冰道具計時用 可無視
						if (giveItemObj.getItem().getMaxUseTime() != 0) {
							giveItemObj.startEquipmentTimer(pc);
						}
						pcInv.storeItem(giveItemObj);
						giveItemObjs.add(giveItemObj);
					} else {
						for (int y = 0; y < (count * changeCount); y++) {
							final L1ItemInstance itemTemp1 = ItemTable.get().createItem(giveItem.getObject().intValue());
							itemTemp1.setCount(1);
							itemTemp1.setIdentified(true);
							itemTemp1.setEnchantLevel(enchantLevel);
							itemTemp1.setBless(bless);

							// itemTemp1.setInv(pcInv);透冰道具計時用 可無視
							if (itemTemp1.getItem().getMaxUseTime() != 0) {
								itemTemp1.startEquipmentTimer(pc);
							}
							pcInv.storeItem(itemTemp1);
							giveItemObjs.add(itemTemp1);
						}
					}
					if (ok) {
						World.get().broadcastPacketToAll(new S_PacketBoxGree(0x02, String.format(msg, new Object[] { pc.getName(), giveItemObj.getLogName() })));
					}

					pc.sendPackets(new S_ServerMessage(403, giveItemObj.getLogName()));
				} else {
					pc.sendPackets(new S_SystemMessage("超過可攜帶物品數量,獲取物品[" + giveItemObj.getLogName() + "(" + count + ")]失敗!請截圖反饋至GM!"));
				}
			}
		}
		return giveItemObjs;
	}

	private final CodedInputStream _input = CodedInputStream.newInstance(new byte[0]);
	private final CodedInputStream _input_child = CodedInputStream.newInstance(new byte[0]);
	private final ArrayList<int[]> _dollMaterialDatas = new ArrayList<int[]>();
	private final ArrayList<L1ItemInstance> _dollMaterialItems = new ArrayList<L1ItemInstance>();

	private void checkDollCompoundData(L1PcInstance pc, byte[] data) {
		try {
			// 死亡
			if (pc.isDead()) {
				return;
			}

			// 鬼魂
			if (pc.isGhost()) {
				return;
			}

			// 非安全區域
			if (!pc.getMap().isSafetyZone(pc.getLocation())) {
				return;
			}

			this._input.reset(data);
			byte[] check = null;
			boolean done = false;
			while (!done) {
				final int tag = this._input.readTag();
				switch (tag) {
				case 0:
					done = true;
					break;
				case 10: {
					check = this._input.readByteArray();
					break;
				}
				default: {
					if (!this._input.mergeFieldFrom(tag, this._input)) {
						done = true;
					}
					break;
				}
				}
			}

			// 如果 done為false 一律不執行
			if (!done) {
				return;
			}

			// 使用中的魔法娃娃objid
			if (!pc.getDolls().isEmpty()) {
				for (final Iterator<L1DollInstance> iter = pc.getDolls().values().iterator(); iter.hasNext();) {
					final L1DollInstance doll = iter.next();
					pc.sendPackets(new S_DollCompoundUseingDoll(doll.getItemObjId()));
				}
			} else {
				pc.sendPackets(new S_DollCompoundUseingDoll(0));
			}

			if (DollPowerTable.isEqual(check)) {
				pc.sendPackets(new S_DollCompoundInit(3));
			} else {
				if (!DollPowerTable.DOLL_PACKET_CACHE.isEmpty()) {
					for (final ServerBasePacket cache : DollPowerTable.DOLL_PACKET_CACHE) {
						pc.sendPackets(cache);
					}
				} else {
					pc.sendPackets(new S_DollCompoundInit(3));
				}
			}
		} catch (final InvalidProtocolBufferException e) {
			// 發生異常
			_log.error(e.getLocalizedMessage(), e);
		} catch (final Exception e) {
			// 發生異常
			_log.error(e.getLocalizedMessage(), e);
		}

	}

	private void compoundDoll(L1PcInstance pc, byte[] data) {
		try {
			// 死亡
			if (pc.isDead()) {
				return;
			}

			// 鬼魂
			if (pc.isGhost()) {
				return;
			}

			// 非安全區域
			if (!pc.getMap().isSafetyZone(pc.getLocation())) {
				return;
			}

			this._input.reset(data);
			int makeLevl = 0;
			boolean done = false;
			while (!done) {
				final int tag = this._input.readTag();
				switch (tag) {
				case 0:
					done = true;
					break;
				case 8: {
					makeLevl = this._input.readInt32();
					break;
				}
				case 18: {
					_input_child.reset(this._input.readByteArray());
					{
						boolean done2 = false;
						int[] material = new int[3];
						while (!done2) {
							final int tag2 = _input_child.readTag();
							switch (tag2) {
							case 0:
								done2 = true;
								break;
							case 8: {
								material[0] = _input_child.readInt32();
								// 異常排序
								if (material[0] < 1 || material[0] > 4) {
									return;
								}
								break;
							}
							case 16: {
								material[1] = _input_child.readInt32();
								break;
							}
							case 24: {
								material[2] = _input_child.readInt32();
								break;
							}

							default: {
								// 嘗試讀取未知的屬性 如果讀取失敗 設置讀取完成
								if (!_input_child.mergeFieldFrom(tag2, _input_child)) {
									done2 = true;
								}
								break;
							}
							}
						}
						if (done2 && material[0] != 0 && material[1] != 0) {
							this._dollMaterialDatas.add(material);
						} else {
							return;
						}
					}
					break;
				}
				default: {
					if (!this._input.mergeFieldFrom(tag, this._input)) {
						done = true;
					}
					break;
				}
				}
			}

			// 如果 done為false 一律不執行
			if (!done) {
				return;
			}

			// 封包傳遞合成材料娃娃等級錯誤 等級在1~4範圍外
			if (!IntRange.includes(makeLevl, 1, 4)) {
				pc.sendPackets(new S_DollCompoundResult(5, 0, 0));
				return;
			}

			final ArrayList<L1Item> dollMaterialLList = DollPowerTable.get().getDollLevelList(makeLevl);

			// 封包傳遞合成材料娃娃等級錯誤 沒有資料
			if (dollMaterialLList == null) {
				pc.sendPackets(new S_DollCompoundResult(5, 0, 0));
				return;
			}

			ArrayList<L1Item> dollMakeLList = DollPowerTable.get().getDollLevelList(makeLevl + 1);

			// 不具有下一等級的製作成功成品群組
			if (dollMakeLList == null) {
				pc.sendPackets(new S_DollCompoundResult(5, 0, 0));
				return;
			}

			// 封包傳遞材料不足2個
			if (this._dollMaterialDatas.size() < 2) {
				pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
				return;
			}

			// 封包傳遞材料超過4個
			if (this._dollMaterialDatas.size() > 4) {
				// STR_ALCHEMY_ERROR_INVALID_INPUT
				pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
				return;
			}

			for (final int[] v : this._dollMaterialDatas) {
				final L1ItemInstance materialItem = pc.getInventory().getItem(v[2]);
				// 找不到物品
				if (materialItem == null) {
					pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
					return;
				}

				// 封印的物品
				if (materialItem.getBless() >= 128) {
					pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
					return;
				}

				// 使用中的魔法娃娃 - 無法當材料
				if (pc.getDoll(materialItem.getId()) != null) {
					pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
					return;
				}

				// 材料所屬等級不是娃娃或是等級錯誤
				if (!dollMaterialLList.contains(materialItem.getItem())) {
					pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
					return;
				}

				this._dollMaterialItems.add(materialItem);

			}

			boolean isCheckOK = true;
			for (final L1ItemInstance material : this._dollMaterialItems) {
				if (pc.getInventory().removeItem(material) <= 0) {
					isCheckOK = false;
				}
			}

			if (isCheckOK) {
				final int[] changes = { 0, 0, 10, 15, 20 };// 0 1 2 3 4
				final int chance = changes[this._dollMaterialItems.size()];
				final int rnd = RandomArrayList.getInt(100);
				// String info = "";
				if (rnd < chance) {// success [level +1]
					// info = "合成成功!";
					boolean isBigSuccess = false;
					if (DollPowerTable.get().isExistDollLevelList(makeLevl + 2)) {
						isBigSuccess = RandomArrayList.getInt(100) < 10;// big
																		// success
																		// [level+2]
																		// 10%機率大成功
					}

					if (isBigSuccess) {
						dollMakeLList = DollPowerTable.get().getDollLevelList(makeLevl + 2);
						// info = "合成大成功!";
					}

					final L1Item l1item = dollMakeLList.get(RandomArrayList.getInt(dollMakeLList.size()));

					final L1ItemInstance newItem = ItemTable.get().createItem(l1item.getItemId());

					pc.sendPackets(new S_DollCompoundResult(isBigSuccess ? 10 : 0, newItem.getId(), l1item.getGfxId()));

					// 加入背包
					pc.getInventory().storeItem(newItem);

					if (makeLevl == 4 || makeLevl == 3 && isBigSuccess) {
						GeneralThreadPool.get().schedule(new AlchemyBroadcast(new S_ServerMessage(0x1151, l1item.getNameId(), pc.getName())), 18 * 1000);
					}

				} else {
					// info = "合成失敗!";
					final L1ItemInstance newItem = this._dollMaterialItems.get(RandomArrayList.getInt(this._dollMaterialItems.size()));
					pc.sendPackets(new S_DollCompoundResult(1, newItem.getId(), newItem.getItem().getGfxId()));
					// 加入背包
					pc.getInventory().storeItem(newItem);
				}
			} else {
				_log.warn("魔法娃娃合成系統發生異常 材料刪除失敗 合成系統使用者:" + pc.getName());
			}

		} catch (final InvalidProtocolBufferException e) {
			// 發生異常
			_log.error(e.getLocalizedMessage(), e);
		} catch (final Exception e) {
			// 發生異常
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			this._dollMaterialDatas.clear();
			this._dollMaterialItems.clear();
		}

	}

	private class AlchemyBroadcast implements Runnable {

		private final Log _log = LogFactory.getLog(AlchemyBroadcast.class);

		private final S_ServerMessage _packet;

		public AlchemyBroadcast(final S_ServerMessage packet) {
			this._packet = packet;
		}

		@Override
		public void run() {
			try {

				World.get().broadcastPacketToAll(this._packet);

			} catch (final Exception e) {
				this._log.error(e.getLocalizedMessage(), e);
			} finally {

			}
		}

	}

	/**
	 * 加入血盟
	 * 
	 * @param pc
	 * @param data
	 */
	private void joinClan(L1PcInstance pc, String clanname) {

		try {
			// 判斷血盟是否存在
			L1Clan clan = WorldClan.get().getClan(clanname);
			if (clan == null)
				return;

			// 判斷血盟盟主是否在線上
			L1PcInstance Leader = World.get().getPlayer(clan.getLeaderName());
			if (Leader == null) {
				pc.sendPackets(new S_ServerMessage(218, clan.getLeaderName()));// 盟主不在線上
				return;
			}

			if (Leader.getId() != clan.getLeaderId()) {
				pc.sendPackets(new S_ServerMessage(92, Leader.getName()));
				return;
			}

			if (pc.getClanid() != 0) {
				if (pc.isCrown()) {
					String player_clan_name = pc.getClanname();
					L1Clan player_clan = WorldClan.get().getClan(player_clan_name);
					if (player_clan == null)
						return;
					if (pc.getId() != player_clan.getLeaderId()) {
						pc.sendPackets(new S_ServerMessage(89));
						return;
					}
					if (player_clan.getCastleId() != 0 || player_clan.getHouseId() != 0) {
						pc.sendPackets(new S_ServerMessage(665));
						return;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(89));
					return;
				}
			}

			Leader.setTempID(pc.getId());
			Leader.sendPackets(new S_Message_YN(97, pc.getName()));
		} catch (final Exception e) {
		} finally {
		}

	}

	/**
	 * 請求更新能力資訊
	 * 
	 * @param client
	 * @param pc
	 * @param data
	 */
	private void requestChatChangeAbilityDetails(final ClientExecutor client, final L1PcInstance pc, final byte[] data) {
		try {
			this._input.reset(data);
			@SuppressWarnings("unused")
			int level = -1;
			int chartype = -1;
			int type = -1;
			int un4 = -1;
			int un5 = -1;
			int str = -1;
			int intel = -1;
			int wis = -1;
			int dex = -1;
			int con = -1;
			int cha = -1;
			boolean done = false;
			while (!done) {
				final int tag = this._input.readTag();
				switch (tag) {
				case 0:
					done = true;
					break;

				case 8: {
					level = _input.readInt32();
					break;
				}

				case 16: {
					chartype = _input.readInt32();
					break;
				}

				case 24: {
					type = _input.readInt32();
					break;
				}

				case 32: {
					un4 = _input.readInt32();
					break;
				}

				case 40: {
					un5 = _input.readInt32();
					break;
				}

				case 48: {
					str = _input.readInt32();
					break;
				}
				case 56: {
					intel = _input.readInt32();
					break;
				}
				case 64: {
					wis = _input.readInt32();
					break;
				}
				case 72: {
					dex = _input.readInt32();
					break;
				}
				case 80: {
					con = _input.readInt32();
					break;
				}
				case 88: {
					cha = _input.readInt32();
					break;
				}

				default: {
					if (!this._input.mergeFieldFrom(tag, this._input)) {
						done = true;
					}
					break;
				}
				}
			}

			// 如果 done為false 一律不執行
			if (!done) {
				return;
			}

			// Type請求類型
			// 1:創建角色初始化能力
			// 2:角色點數重置 升級點能力
			// 8:創建角色 角色點數重置初始化 點選能力
			// 16:遊戲內升級點選能力
			switch (type) {
			case 1:// 1:創建角色 初始化能力 重置點數 初始化能力 提升等級
				if (str != -1) {
					client.out().encrypt(
							new S_StrDetails(2, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str), L1ClassFeature
									.calcAbilityMaxWeight(str, con)));
				}
				if (intel != -1) {
					client.out().encrypt(
							new S_IntDetails(2, L1ClassFeature.calcIntMagicDmg(intel, intel), L1ClassFeature.calcIntMagicHit(intel, intel), L1ClassFeature.calcIntMagicCritical(intel,
									intel), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
				}
				if (wis != -1) {
					int addmp = 0;
					client.out().encrypt(
							new S_WisDetails(2, L1ClassFeature.calcWisMpr(wis, wis), L1ClassFeature.calcWisPotionMpr(wis, wis), L1ClassFeature.calcStatMr(wis)
									+ L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, wis), addmp));
				}
				if (dex != -1) {
					client.out().encrypt(
							new S_DexDetails(2, L1ClassFeature.calcDexDmg(dex, dex), L1ClassFeature.calcDexHit(dex, dex), L1ClassFeature.calcDexDmgCritical(dex, dex), L1ClassFeature
									.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
				}
				if (con != -1) {
					int addhp = 0;
					if (pc != null) {
						if (pc.isInCharReset()) {
							addhp = L1ClassFeature.calcConAddMaxHp(con);
						}
					}
					client.out().encrypt(
							new S_ConDetails(2, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con),
									L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), addhp));
				}
				break;

			case 2:// 2 角色點數重置 升級點能力 XXX(萬能藥使用待確認)
				if (pc == null) {
					return;
				}
				// 點選力量or體質的狀況
				if (un4 != -1 && un5 != -1) {
					boolean error = false;
					if (un4 != 1) {
						_log.warn("請求更新能力資訊單獨點選能力狀況未處理(角色點數重置 升級點能力)unknown_4不等於1:" + un4);
						error = true;
					}

					if (str == -1 || con == -1) {
						_log.warn("請求更新能力資訊單獨點選能力發生異常(角色點數重置 升級點能力):不包含力量以及體質");
						error = true;
					}

					if (error) {
						return;
					}

					switch (un5) {
					case 1:// 點體質
						int addhp = 0;
						if (pc != null) {
							if (pc.isInCharReset()) {
								addhp = L1ClassFeature.calcConAddMaxHp(con);
							}
						}
						pc.sendPackets(new S_ConDetails(4, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con),
								L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), addhp));
						break;
					case 16:// 點力量
						pc.sendPackets(new S_StrDetails(4, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str),
								L1ClassFeature.calcAbilityMaxWeight(str, con)));
						break;
					default:
						_log.warn("請求更新能力資訊單獨點選能力狀況未處理(角色點數重置 升級點能力)unknown_5:" + un5);
						break;
					}
				} else if (str != -1 && con != -1 && intel != -1) {
					if (str != -1) {
						pc.sendPackets(new S_StrDetails(4, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str),
								L1ClassFeature.calcAbilityMaxWeight(str, con)));
					}
					if (intel != -1) {
						pc.sendPackets(new S_IntDetails(4, L1ClassFeature.calcIntMagicDmg(intel, intel), L1ClassFeature.calcIntMagicHit(intel, intel), L1ClassFeature.calcIntMagicCritical(
								intel, intel), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
					}
					if (wis != -1) {
						int addmp = 0;
						if (pc != null) {
							if (pc.isInCharReset()) {
								addmp = L1ClassFeature.calcWisAddMaxMp(wis);
							}
						}
						pc.sendPackets(new S_WisDetails(4, L1ClassFeature.calcWisMpr(wis, wis), L1ClassFeature.calcWisPotionMpr(wis, wis), L1ClassFeature.calcStatMr(wis)
								+ L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, wis), addmp));
					}
					if (dex != -1) {
						pc.sendPackets(new S_DexDetails(4, L1ClassFeature.calcDexDmg(dex, dex), L1ClassFeature.calcDexHit(dex, dex), L1ClassFeature.calcDexDmgCritical(dex, dex),
								L1ClassFeature.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
					}
					if (con != -1) {
						int addhp = 0;
						if (pc != null) {
							if (pc.isInCharReset()) {
								addhp = L1ClassFeature.calcConAddMaxHp(con);
							}
						}
						pc.sendPackets(new S_ConDetails(4, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con),
								L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), addhp));
					}
					if (cha != -1) {
						pc.sendPackets(new S_ChaDetails(4));
					}
				} else {
					if (str != -1) {
						pc.sendPackets(new S_StrDetails(4, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str),
								L1ClassFeature.calcAbilityMaxWeight(str, con)));
					} else if (intel != -1) {
						pc.sendPackets(new S_IntDetails(4, L1ClassFeature.calcIntMagicDmg(intel, intel), L1ClassFeature.calcIntMagicHit(intel, intel), L1ClassFeature.calcIntMagicCritical(
								intel, intel), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
					} else if (wis != -1) {
						int addmp = 0;
						if (pc != null) {
							if (pc.isInCharReset()) {
								addmp = L1ClassFeature.calcWisAddMaxMp(wis);
							}
						}
						pc.sendPackets(new S_WisDetails(4, L1ClassFeature.calcWisMpr(wis, wis), L1ClassFeature.calcWisPotionMpr(wis, wis), L1ClassFeature.calcStatMr(wis)
								+ L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, wis), addmp));
					} else if (dex != -1) {
						pc.sendPackets(new S_DexDetails(4, L1ClassFeature.calcDexDmg(dex, dex), L1ClassFeature.calcDexHit(dex, dex), L1ClassFeature.calcDexDmgCritical(dex, dex),
								L1ClassFeature.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
					} else if (con != -1) {
						int addhp = 0;
						if (pc != null) {
							if (pc.isInCharReset()) {
								addhp = L1ClassFeature.calcConAddMaxHp(con);
							}
						}
						pc.sendPackets(new S_ConDetails(4, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con),
								L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), addhp));
					} else if (cha != -1) {
						pc.sendPackets(new S_ChaDetails(4));
					}
				}
				break;

			case 8:// 8:創建角色 角色點數重置 點選能力

				// 單獨點選力量or體質的狀況
				if (un4 != -1 && un5 != -1) {
					boolean error = false;
					if (un4 != 1) {
						_log.warn("請求更新能力資訊單獨點選能力狀況未處理un4不等於1:" + un4);
						error = true;
					}

					if (str == -1 || con == -1) {
						_log.warn("請求更新能力資訊單獨點選能力發生異常:不包含力量以及體質");
						error = true;
					}

					if (error) {
						return;
					}

					switch (un5) {
					case 1:// 點體質
						client.out().encrypt(
								new S_ConDetails(16, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con),
										L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), 0));
						break;
					case 16:// 點力量
						client.out().encrypt(
								new S_StrDetails(16, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str), L1ClassFeature
										.calcAbilityMaxWeight(str, con)));
						break;
					default:
						_log.warn("請求更新能力資訊單獨點選能力狀況未處理un5:" + un5);
						break;
					}
				} else {
					if (str != -1) {
						client.out().encrypt(
								new S_StrDetails(16, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str), L1ClassFeature
										.calcAbilityMaxWeight(str, con)));
					}
					if (intel != -1) {
						client.out().encrypt(
								new S_IntDetails(16, L1ClassFeature.calcIntMagicDmg(intel, intel), L1ClassFeature.calcIntMagicHit(intel, intel), L1ClassFeature.calcIntMagicCritical(intel,
										intel), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
					}
					if (wis != -1) {
						int addmp = 0;
						client.out().encrypt(
								new S_WisDetails(16, L1ClassFeature.calcWisMpr(wis, wis), L1ClassFeature.calcWisPotionMpr(wis, wis), L1ClassFeature.calcStatMr(wis)
										+ L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, wis), addmp));
					}
					if (dex != -1) {
						client.out().encrypt(
								new S_DexDetails(16, L1ClassFeature.calcDexDmg(dex, dex), L1ClassFeature.calcDexHit(dex, dex), L1ClassFeature.calcDexDmgCritical(dex, dex), L1ClassFeature
										.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
					}
					if (con != -1) {
						client.out().encrypt(
								new S_ConDetails(16, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con),
										L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), 0));
					}
					if (cha != -1) {
						client.out().encrypt(new S_ChaDetails(16));
					}
				}
				break;

			case 16:// 16:遊戲內升級點選能力 XXX 必須判斷是否為 加點 減點
				if (pc == null) {
					return;
				}
				// 點選力量or體質的狀況
				if (un4 != -1 && un5 != -1) {
					boolean error = false;
					if (un4 != 1) {
						_log.warn("請求更新能力資訊單獨點選能力狀況未處理(升級設定能力)unknown_4不等於1:" + un4);
						error = true;
					}

					if (str == -1 || con == -1) {
						_log.warn("請求更新能力資訊單獨點選能力發生異常(升級設定能力):不包含力量以及體質");
						error = true;
					}

					if (error) {
						return;
					}

					switch (un5) {
					case 1:// 點體質
							// _log.info("ReqCon:" + con + " baseCon:" +
							// pc.getBaseCon() + " CurCon:" + pc.getCon());
						final int realBaseCon = pc.getBaseCon() + (con - pc.getCon());
						pc.sendPackets(new S_ConDetails(32, L1ClassFeature.calcConHpr(con, realBaseCon), L1ClassFeature.calcConPotionHpr(con, realBaseCon), L1ClassFeature
								.calcAbilityMaxWeight(str, con), L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, realBaseCon),
								L1ClassFeature.calcConAddMaxHp(realBaseCon)));
						break;
					case 16:// 點力量
						final int realBaseStr = pc.getBaseStr() + (str - pc.getStr());
						pc.sendPackets(new S_StrDetails(32, L1ClassFeature.calcStrDmg(str, realBaseStr), L1ClassFeature.calcStrHit(str, realBaseStr), L1ClassFeature.calcStrDmgCritical(
								str, realBaseStr), L1ClassFeature.calcAbilityMaxWeight(str, con)));
						break;
					default:
						_log.warn("請求更新能力資訊單獨點選能力狀況未處理(升級設定能力)unknown_5:" + un5);
						break;
					}
				} else {
					if (intel != -1) {
						final int realBaseInt = pc.getBaseInt() + (intel - pc.getInt());
						pc.sendPackets(new S_IntDetails(32, L1ClassFeature.calcIntMagicDmg(intel, realBaseInt), L1ClassFeature.calcIntMagicHit(intel, realBaseInt), L1ClassFeature
								.calcIntMagicCritical(intel, realBaseInt), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
					} else if (wis != -1) {
						final int realBaseWis = pc.getBaseWis() + (wis - pc.getWis());
						pc.sendPackets(new S_WisDetails(32, L1ClassFeature.calcWisMpr(wis, realBaseWis), L1ClassFeature.calcWisPotionMpr(wis, realBaseWis), L1ClassFeature.calcStatMr(wis)
								+ L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, realBaseWis), L1ClassFeature
								.calcWisAddMaxMp(realBaseWis)));
					} else if (dex != -1) {
						final int realBaseDex = pc.getBaseDex() + (dex - pc.getDex());
						pc.sendPackets(new S_DexDetails(32, L1ClassFeature.calcDexDmg(dex, realBaseDex), L1ClassFeature.calcDexHit(dex, realBaseDex), L1ClassFeature.calcDexDmgCritical(
								dex, realBaseDex), L1ClassFeature.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
					} else if (cha != -1) {
						pc.sendPackets(new S_ChaDetails(32));
					}
				}
				break;

			default:
				_log.info("請求更新能力資訊未處理type:" + type);
				break;
			}

		} catch (final InvalidProtocolBufferException e) {
			// 發生異常
			_log.error(e.getLocalizedMessage(), e);
		} catch (final Exception e) {
			// 發生異常
			_log.error(e.getLocalizedMessage(), e);
		} finally {

		}
	}

	/**
	 * 使用聊天
	 * 
	 * @param pc
	 * @param data
	 */
	private void requestChat(L1PcInstance pc, final ClientExecutor client, byte[] data) {
		try {
			this._input.reset(data);
			int chatIndex = -1;
			int chatType = -1;
			String chatText = "";
			String tellTargetName = "";
			int severId = -1;
			boolean done = false;
			while (!done) {
				final int tag = this._input.readTag();
				switch (tag) {
				case 0:
					done = true;
					break;

				case 8: {
					chatIndex = _input.readInt32();
					break;
				}

				case 16: {
					chatType = _input.readInt32();
					break;
				}

				case 26: {
					chatText = _input.readString(Config.CLIENT_LANGUAGE_CODE);
					break;
				}

				case 42: {
					tellTargetName = _input.readString(Config.CLIENT_LANGUAGE_CODE);
					break;
				}

				case 48: {
					severId = _input.readInt32();
					break;
				}

				default: {
					if (!this._input.mergeFieldFrom(tag, this._input)) {
						done = true;
					}
					break;
				}
				}
			}

			// 如果 done為false 一律不執行
			if (!done) {
				return;
			}
			final S_GmMessage gm = new S_GmMessage(pc, null, chatType, chatText);
			// 修正對話出現太長的字串會斷線 start
			if ((chatText != null) && (chatText.length() > 52)) {
				chatText = chatText.substring(0, 52);
				// 修正對話出現太長的字串會斷線 end
			}

			for (final L1PcInstance pca : World.get().getAllPlayers()) {
				if (pca.isGm() && (pca != pc)) {
					pca.sendPackets(gm);
				}
			}

			boolean isStop = false;// 停止輸出

			boolean errMessage = false;// 異常訊息

			// AI驗證
			checkAI(pc, chatText, client);

			// 中毒狀態
			if (pc.hasSkillEffect(SILENCE)) {
				if (!pc.isGm()) {
					isStop = true;
				}
			}

			// 中毒狀態
			if (pc.hasSkillEffect(AREA_OF_SILENCE)) {
				if (!pc.isGm()) {
					isStop = true;
				}
			}

			// 中毒狀態
			if (pc.hasSkillEffect(STATUS_POISON_SILENCE)) {
				if (!pc.isGm()) {
					isStop = true;
				}
			}

			// 你從現在被禁止閒談。
			if (pc.hasSkillEffect(STATUS_CHAT_PROHIBITED)) {
				isStop = true;
				errMessage = true;
			}

			// 你從現在被禁止閒談。
			if (pc.hasSkillEffect(CHAT_STOP)) {
				isStop = true;
				errMessage = true;
			}

			if (isStop) {
				if (errMessage) {
					pc.sendPackets(new S_ServerMessage(242));
				}
				return;
			}

			switch (chatType) {
			case 0:// 一般頻道
				if (pc.is_retitle()) {
					re_title(pc, chatText.trim());
					return;
				}
				if (pc.is_repass() != 0) {
					re_repass(pc, chatText.trim());
					return;
				}
				if (pc.is_avenger()) { // 復仇卷軸
					re_avenger(pc, chatText.trim());
					return;
				}
				// 廣播卡判斷時間 by terry0412
				if (pc.hasSkillEffect(BROADCAST_CARD)) {
					pc.killSkillEffectTimer(BROADCAST_CARD);
					check_broadcast(pc, chatText); // 保留空格
					return;
				}
				chatType_0(pc, chatText, chatIndex, chatType, tellTargetName, severId);
				break;

			case 1:// 密語頻道
					// TODO 非法字串長度
				if (chatText.length() > 65) {
					_log.warn("人物: " + pc.getName() + " 對話長度超過限制!! IP: " + pc.getNetConnection().getIp().toString() + " 異常對話長度:" + chatText.length());
					pc.getNetConnection().kick();
					return;
				}
				int result_type = 0;// 使用成功

				// 等級不夠
				if (pc.getLevel() < ConfigAlt.WHISPER_CHAT_LEVEL) {
					// 等級 7 以下無法使用密談。
					result_type = 24;
					// 輸出使用聊天頻道的結果
					pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));
					return;
				}

				// 對話間隔檢查
				if (!pc.isGm()) {

					// 禁言成立
					if (result_type != 0) {
						// 輸出使用聊天頻道的結果
						pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));
						return;
					}
				}

				final L1PcInstance whisperTo = World.get().getPlayer(tellTargetName);
				boolean isNpc = false;// 判斷是否為假人npc
				// 如果密語對象為NULL就是沒對象
				if (whisperTo == null) {
					L1DeInstance de = getDe(tellTargetName);
					if (de != null) {
						isNpc = true;
					}
					if (!isNpc) {
						// 密語對象不存在
						result_type = 5;
					}
					// 自己跟自己說話
				} else if (whisperTo.equals(pc)) {
					result_type = 57;
					// 斷絕密語
				} else if (whisperTo.getExcludingList().contains(pc.getName())) {
					result_type = 23;
					// 關閉密語
				} else if (!whisperTo.isCanWhisper()) {
					result_type = 56;
				}

				// 輸出使用聊天頻道的結果
				pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));

				// 使用未成功
				if (result_type != 0) {
					return;
				}

				// 建立輸出字串訊息
				if (!isNpc) {
					whisperTo.sendPackets(new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.getName(), severId));
				}

				if (ConfigRecord.LOGGING_CHAT_WHISPER) {
					LogChatReading.get().isTarget(pc, whisperTo, chatText, 9);
				}
				break;

			case 2: // 大叫頻道(!)
				chatType_2(pc, chatText, chatIndex, chatType, tellTargetName, severId);
				break;

			case 3: // 廣播頻道
				chatType_3(pc, chatText, chatIndex, chatType, tellTargetName, severId);
				break;

			case 4: // 血盟頻道(@)
				chatType_4(pc, chatText, chatIndex, chatType, tellTargetName, severId);
				break;

			case 11: // 隊伍頻道(#)
				chatType_11(pc, chatText, chatIndex, chatType, tellTargetName, severId);
				break;

			case 12: // 交易頻道
				chatType_12(pc, chatText, chatIndex, chatType, tellTargetName, severId);
				break;

			case 13: // 連盟頻道(%)
				chatType_13(pc, chatText, chatIndex, chatType, tellTargetName, severId);
				break;

			case 14: // 隊伍頻道(聊天)
				chatType_14(pc, chatText, chatIndex, chatType, tellTargetName, severId);
				break;

			case 15: // 同盟頻道(~) by terry0412
				chatType_15(pc, chatText, chatIndex, chatType, tellTargetName, severId);
				break;
			}

			if (!pc.isGm()) {
				pc.checkChatInterval();
			}
		} catch (final InvalidProtocolBufferException e) {
			// 發生異常
			_log.error(e.getLocalizedMessage(), e);
		} catch (final Exception e) {
			// 發生異常
			_log.error(e.getLocalizedMessage(), e);
		} finally {

		}
	}

	/**
	 * 復仇卷軸
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void re_avenger(final L1PcInstance pc, final String chatText) {
		try {
			final String newchatText = chatText.trim();
			if (newchatText.isEmpty() || (newchatText.length() <= 0)) {
				pc.sendPackets(new S_ServerMessage("\\aE請輸入欲復仇的玩家名稱"));
				return;
			}

			// 清除
			pc.re_avenger(false);

			final L1PcInstance find_pc = World.get().getPlayer(newchatText);
			if (find_pc == null) {
				pc.sendPackets(new S_ServerMessage("\\aG該玩家不在線上或是不存在此ID"));
				return;
			}

			if (!find_pc.getMap().isNormalZone(find_pc.getLocation())) {
				pc.sendPackets(new S_ServerMessage("\\aG該玩家在非一般區域內無法傳送"));
				return;
			}

			if (L1CastleLocation.checkInAllWarArea(find_pc.getX(), find_pc.getY(), find_pc.getMapId())) {
				pc.sendPackets(new S_ServerMessage("\\aG該玩家在攻城區域內無法傳送"));
				return;
			}

			if (L1HouseLocation.isInHouse(find_pc.getX(), find_pc.getY(), find_pc.getMapId())) {
				pc.sendPackets(new S_ServerMessage("\\aG該玩家在盟屋區域內無法傳送"));
				return;
			}

			/*
			 * if (!find_pc.getMap().isMarkable()) { pc.sendPackets(new S_ServerMessage("\\fU該玩家在不能到達的區域")); return; }
			 */

			pc.sendPackets(new S_ServerMessage("\\aI傳送至[" + find_pc.getName() + "]身邊成功"));
			find_pc.sendPackets(new S_ServerMessage("\\aI警告! 有人使用[復仇卷軸]飛至你身邊"));

			L1Teleport.teleport(pc, find_pc.getLocation().randomLocation(3, false), pc.getHeading(), true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";

	private void re_repass(final L1PcInstance pc, final String password) {
		try {
			switch (pc.is_repass()) {
			case 1:// 輸入舊密碼
				if (!pc.getNetConnection().getAccount().get_password().equals(password)) {
					// 1,744：密碼錯誤
					pc.sendPackets(new S_ServerMessage(1744));
					return;
				}
				pc.repass(2);
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_pass_01", new String[] { "請輸入您的新密碼" }));
				break;

			case 2:// 輸入新密碼
				boolean iserr = false;
				for (int i = 0; i < password.length(); i++) {
					final String ch = password.substring(i, i + 1);
					if (!_check_pwd.contains(ch.toLowerCase())) {
						// 1,742：帳號或密碼中有無效的字元
						pc.sendPackets(new S_ServerMessage(1742));
						iserr = true;
						break;
					}
				}
				if (password.length() > 13) {
					// 1,742：帳號或密碼中有無效的字元
					pc.sendPackets(new S_ServerMessage(166, "密碼長度過長"));
					iserr = true;
				}
				if (password.length() < 3) {
					// 1,742：帳號或密碼中有無效的字元
					pc.sendPackets(new S_ServerMessage(166, "密碼長度過長"));
					iserr = true;
				}
				if (iserr) {
					return;
				}
				pc.setText(password);
				pc.repass(3);
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_pass_01", new String[] { "請確認您的新密碼" }));
				break;

			case 3:// 確認新密碼
				if (!pc.getText().equals(password)) {
					// 1,982：所輸入的密碼不一致.請重新輸入.
					pc.sendPackets(new S_ServerMessage(1982));
					return;
				}
				pc.sendPackets(new S_CloseList(pc.getId()));
				// 1,985：角色密碼成功地變更.(忘記密碼時請至天堂網站詢問)
				pc.sendPackets(new S_ServerMessage(1985));
				AccountReading.get().updatePwd(pc.getAccountName(), password);
				pc.setText(null);
				pc.repass(0);
				break;
			}

		} catch (final Exception e) {
			pc.sendPackets(new S_CloseList(pc.getId()));
			// 未知的錯誤%d
			pc.sendPackets(new S_ServerMessage(45));
			pc.setText(null);
			pc.repass(0);
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _error = 3;

	/**
	 * AI驗證
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void checkAI(final L1PcInstance pc, final String chatText, final ClientExecutor client) {
		if (pc.getAIsum() == -1) {
			return;
		}
		int sum = -1;
		String msg = "";
		try {
			sum = Integer.parseInt(chatText);
		} catch (final Exception e) {
			msg = "請輸入數字！";
			pc.sendPackets(new S_SystemMessage(msg));
			return;
		}
		if (pc.getAIsum() != sum) {
			_error--;
			msg = "您的答案是 " + sum + " 還有 " + _error + " 次機會，請輸入正確答案。";
		} else {
			pc.setAIsum(-1);
			pc.setCheckAI("");
			pc.setAImsg("");
			pc.setSec(0);
			_error = 3;
			msg = "恭喜您答對了！";
			pc.killSkillEffectTimer(L1SkillId.AICHECK);
		}
		pc.sendPackets(new S_SystemMessage(msg));
		if (_error <= 0) {
			IpReading.get().add(pc.getAccountName(), "AI系統自動封鎖");
			client.kick();
			return;
		}

	}

	/**
	 * 變更封號
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void re_title(final L1PcInstance pc, final String chatText) {
		try {
			final String newchatText = chatText.trim();
			if (newchatText.isEmpty() || (newchatText.length() <= 0)) {
				pc.sendPackets(new S_ServerMessage("\\aI請輸入封號內容"));
				return;
			}
			final int length = Config.LOGINS_TO_AUTOENTICATION ? 18 : 13;
			if (newchatText.getBytes().length > length) {
				pc.sendPackets(new S_ServerMessage("\\aI封號長度過長"));
				return;
			}
			final StringBuilder title = new StringBuilder();
			title.append(newchatText);

			pc.setTitle(title.toString());
			pc.sendPacketsAll(new S_CharTitle(pc.getId(), title));
			pc.save();
			pc.retitle(false);
			pc.sendPackets(new S_ServerMessage("\\aH封號變更完成"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 交易頻道($)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_12(final L1PcInstance pc, final String chatText, final int chatIndex, final int chatType, final String tellTargetName, final int severId) {
		int result_type = 0;// 使用成功
		// 輸出使用聊天頻道的結果
		pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));
		String name = pc.getName();
		// 建立輸出字串訊息
		final S_ChatText s_chatpacket = new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.getName(), severId);

		/*
		 * S_ChatTransaction packet = null; String name = pc.getName(); if (pc.get_outChat() == null) { packet = new S_ChatTransaction(pc, chatText);
		 * 
		 * } else { packet = new S_ChatTransaction(pc.get_outChat(), chatText); name = pc.get_outChat().getNameId(); }
		 */

		for (final L1PcInstance listner : World.get().getAllPlayers()) {
			// 拒絕接收該人物訊息
			if (listner.getExcludingList().contains(name)) {
				continue;
			}
			// 拒絕接收廣播頻道
			if (!listner.isShowTradeChat()) {
				continue;
			}
			listner.sendPackets(s_chatpacket);
		}

		if (ConfigRecord.LOGGING_CHAT_BUSINESS) {
			LogChatReading.get().noTarget(pc, chatText, 12);
		}
	}

	/**
	 * 廣播頻道(&)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_3(final L1PcInstance pc, final String chatText, final int chatIndex, final int chatType, final String tellTargetName, final int severId) {
		// 畫面中央顯示訊息 by terry0412
		if (pc.isGm()) {
			World.get().broadcastPacketToAll(new S_BlueMessage(166, "\\f3" + chatText));
		}
		int result_type = 0;// 使用成功
		// 輸出使用聊天頻道的結果
		pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));

		// 建立輸出字串訊息
		String name = pc.getName();
		final S_ChatText s_chatpacket = new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.isGm() ? "******" : pc.getName(), severId);
		/*
		 * S_ChatGlobal packet = null; String name = pc.getName(); if (pc.get_outChat() == null) { packet = new S_ChatGlobal(pc, chatText); if (pc.isGm()) {
		 * World.get().broadcastPacketToAll(packet); return; }
		 * 
		 * } else { packet = new S_ChatGlobal(pc.get_outChat(), chatText); name = pc.get_outChat().getNameId(); }
		 */

		if (!pc.isGm()) {
			// 廣播扣除金幣或是飽食度(0:飽食度 其他:指定道具編號)
			// 廣播扣除質(set_global設置0:扣除飽食度量 set_global設置其他:扣除指定道具數量)
			switch (ConfigOther.SET_GLOBAL) {
			case 0: // 飽食度
				if (pc.get_food() >= 6) {
					pc.set_food(pc.get_food() - ConfigOther.SET_GLOBAL_COUNT);
					pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));

				} else {
					// 你太過於饑餓以致於無法談話。
					pc.sendPackets(new S_ServerMessage(462));
					return;
				}
				break;

			default: // 指定道具
				final L1ItemInstance item = pc.getInventory().checkItemX(ConfigOther.SET_GLOBAL, ConfigOther.SET_GLOBAL_COUNT);
				if (item != null) {
					pc.getInventory().removeItem(item, ConfigOther.SET_GLOBAL_COUNT);// 刪除指定道具

				} else {
					// 找回物品
					final L1Item itemtmp = ItemTable.get().getTemplate(ConfigOther.SET_GLOBAL);
					pc.sendPackets(new S_ServerMessage(337, itemtmp.getNameId()));
					return;
				}
				break;
			}
		}

		for (final L1PcInstance listner : World.get().getAllPlayers()) {
			// 拒絕接收該人物訊息
			if (listner.getExcludingList().contains(name)) {
				continue;
			}
			// 拒絕接收廣播頻道
			if (!listner.isShowWorldChat()) {
				continue;
			}
			listner.sendPackets(s_chatpacket);
		}

		try {
			// ConfigDescs.get(2) = 服務器名稱
			String text = null;
			if (pc.isGm()) {
				text = "[******] " + chatText;
			} else {
				text = "<" + Config.SERVERNAME + ">" + "[" + pc.getName() + "] " + chatText;
			}
			TServer.get().outServer(text.getBytes("utf-8"));

		} catch (final UnsupportedEncodingException e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		if (ConfigRecord.LOGGING_CHAT_WORLD) {
			LogChatReading.get().noTarget(pc, chatText, 3);
		}
	}

	/**
	 * 同盟頻道(~) by terry0412
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_15(final L1PcInstance pc, final String chatText, final int chatIndex, final int chatType, final String tellTargetName, final int severId) {
		if (pc.getClanid() != 0) {
			final L1Clan clan = pc.getClan();
			if (clan == null) {
				return;
			}

			// 取得指定同盟資料
			final L1Alliance alliance = ClanAllianceReading.get().getAlliance(clan.getClanId());
			if (alliance == null) {
				return;
			}

			final S_ChatClanAlliance chatpacket = new S_ChatClanAlliance(pc, clan.getClanName(), chatText);

			// 對所有締結血盟線上成員發送封包 (遮蔽特定玩家)
			alliance.sendPacketsAll(pc.getName(), chatpacket);

			if (ConfigRecord.LOGGING_CHAT_COMBINED) {
				LogChatReading.get().noTarget(pc, chatText, 15);
			}
		}
	}

	/**
	 * 隊伍頻道(聊天)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_14(final L1PcInstance pc, final String chatText, final int chatIndex, final int chatType, final String tellTargetName, final int severId) {
		int result_type = 0;// 使用成功
		if (pc.isInChatParty()) {
			// 輸出使用聊天頻道的結果
			pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));
			final S_ChatText s_chatpacket = new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.getName(), severId);
			// final S_ChatParty2 chatpacket = new S_ChatParty2(pc, chatText);
			final L1PcInstance[] partyMembers = pc.getChatParty().getMembers();
			for (final L1PcInstance listner : partyMembers) {
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(s_chatpacket);
				}
			}

			if (ConfigRecord.LOGGING_CHAT_CHAT_PARTY) {
				LogChatReading.get().noTarget(pc, chatText, 14);
			}
		}
	}

	/**
	 * 連盟頻道(%)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_13(final L1PcInstance pc, final String chatText, final int chatIndex, final int chatType, final String tellTargetName, final int severId) {
		int result_type = 0;// 使用成功
		if (pc.getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan == null) {
				return;
			}
			// 輸出使用聊天頻道的結果
			pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));

			switch (pc.getClanRank()) {
			case L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN:// 6:守護騎士
			case L1Clan.NORMAL_CLAN_RANK_GUARDIAN:// 9:守護騎士
			case L1Clan.CLAN_RANK_GUARDIAN:// 3:副君主
			case L1Clan.CLAN_RANK_PRINCE:// 4:聯盟君主
			case L1Clan.NORMAL_CLAN_RANK_PRINCE:// 10:聯盟君主
				// final S_ChatClanUnion chatpacket = new S_ChatClanUnion(pc,
				// chatText);
				// 建立輸出字串訊息
				final S_ChatText s_chatpacket = new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.getName(), severId);

				final L1PcInstance[] clanMembers = clan.getOnlineClanMember();
				for (final L1PcInstance listner : clanMembers) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						switch (listner.getClanRank()) {
						case L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN:// 6:守護騎士
						case L1Clan.NORMAL_CLAN_RANK_GUARDIAN:// 9:守護騎士
						case L1Clan.CLAN_RANK_GUARDIAN:// 3:副君主
						case L1Clan.CLAN_RANK_PRINCE:// 4:聯盟君主
						case L1Clan.NORMAL_CLAN_RANK_PRINCE:// 10:聯盟君主
							listner.sendPackets(s_chatpacket);
							break;
						}
					}
				}

				if (ConfigRecord.LOGGING_CHAT_COMBINED) {
					LogChatReading.get().noTarget(pc, chatText, 13);
				}
				break;
			}
		}
	}

	/**
	 * 隊伍頻道(#)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_11(final L1PcInstance pc, final String chatText, final int chatIndex, final int chatType, final String tellTargetName, final int severId) {
		int result_type = 0;// 使用成功

		if (pc.isInParty()) {
			// 輸出使用聊天頻道的結果
			pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));
			final S_ChatText s_chatpacket = new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.getName(), severId);

			// final S_ChatParty chatpacket = new S_ChatParty(pc, chatText);

			final List<L1PcInstance> pcs = pc.getParty().getMemberList();

			if (pcs.isEmpty()) {
				return;
			}
			if (pcs.size() <= 0) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = pcs.iterator(); iter.hasNext();) {
				final L1PcInstance listner = iter.next();
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(s_chatpacket);
				}
			}

			if (ConfigRecord.LOGGING_CHAT_PARTY) {
				LogChatReading.get().noTarget(pc, chatText, 11);
			}
		}
	}

	/**
	 * 血盟頻道(@)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_4(final L1PcInstance pc, final String chatText, final int chatIndex, final int chatType, final String tellTargetName, final int severId) {
		int result_type = 0;// 使用成功
		// 輸出使用聊天頻道的結果
		pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));

		if (pc.getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				// final S_ChatClan chatpacket = new S_ChatClan(pc, chatText);
				// 建立輸出字串訊息
				final S_ChatText s_chatpacket = new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.getName(), severId);
				final L1PcInstance[] clanMembers = clan.getOnlineClanMember();
				for (final L1PcInstance listner : clanMembers) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						listner.sendPackets(s_chatpacket);
					}
				}

				if (ConfigRecord.LOGGING_CHAT_CLAN) {
					LogChatReading.get().noTarget(pc, chatText, 4);
				}
			}
		}
	}

	/**
	 * 大叫頻道(!)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_2(final L1PcInstance pc, final String chatText, final int chatIndex, final int chatType, final String tellTargetName, final int severId) {
		if (pc.isGhost()) {
			return;
		}
		int result_type = 0;// 使用成功
		// S_ChatShouting chatpacket = null;
		// 輸出使用聊天頻道的結果
		pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));

		String name = pc.getName();
		/*
		 * if (pc.get_outChat() == null) { chatpacket = new S_ChatShouting(pc, chatText);
		 * 
		 * } else { chatpacket = new S_ChatShouting(pc.get_outChat(), chatText); name = pc.get_outChat().getNameId(); }
		 */
		// 建立輸出字串訊息
		final S_ChatText s_chatpacket = new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.getName(), severId, pc.getId(), pc.getX(), pc.getY());
		pc.sendPackets(s_chatpacket);
		for (final L1PcInstance listner : World.get().getVisiblePlayer(pc, 50)) {
			if (!listner.getExcludingList().contains(name)) {
				// 副本ID相等
				if (pc.get_showId() == listner.get_showId()) {
					listner.sendPackets(s_chatpacket);
				}
			}
		}

		if (ConfigRecord.LOGGING_CHAT_SHOUT) {
			LogChatReading.get().noTarget(pc, chatText, 2);
		}
		// 變形怪重複對話
		doppelShouting(pc, chatText);
	}

	/**
	 * 一般頻道
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_0(final L1PcInstance pc, final String chatText, final int chatIndex, final int chatType, final String tellTargetName, final int severId) {
		if (pc.isGhost() && !(pc.isGm() || pc.isMonitor())) {
			return;
		}

		// 輸出使用聊天頻道的結果
		int result_type = 0;// 使用成功
		pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));
		if (pc.getAccessLevel() > 0) {
			// GM命令
			if (chatText.startsWith(".")) {
				final String cmd = chatText.substring(1);
				GMCommands.getInstance().handleCommands(pc, cmd);
				return;
			}
		}

		/*
		 * // 產生封包 S_Chat chatpacket = null;
		 * 
		 * String name = pc.getName(); if (pc.get_outChat() == null) { chatpacket = new S_Chat(pc, chatText);
		 * 
		 * } else { chatpacket = new S_Chat(pc.get_outChat(), chatText); name = pc.get_outChat().getNameId(); }
		 */
		// 建立輸出字串訊息
		String name = pc.getName();
		final S_ChatText s_chatpacket = new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.getName(), severId, pc.getId(), pc.getX(), pc.getY());
		pc.sendPackets(s_chatpacket);

		for (final L1PcInstance listner : World.get().getRecognizePlayer(pc)) {
			if (!listner.getExcludingList().contains(name)) {
				// 副本ID相等
				if (pc.get_showId() == listner.get_showId()) {
					listner.sendPackets(s_chatpacket);
				}
			}
		}

		// 對話紀錄
		if (ConfigRecord.LOGGING_CHAT_NORMAL) {
			LogChatReading.get().noTarget(pc, chatText, 0);
		}
		// 變形怪重複對話
		doppelGenerally(pc, chatText);
	}

	/**
	 * 變形怪重複對話(一般頻道)
	 * 
	 * @param pc
	 * @param chatType
	 * @param chatText
	 */
	private void doppelGenerally(final L1PcInstance pc, final String chatText) {
		// 變形怪重複對話
		for (final L1Object obj : pc.getKnownObjects()) {
			if (obj instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) obj;
				if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
					mob.broadcastPacketX8(new S_NpcChat(mob, chatText));
				}
			}
		}
	}

	/**
	 * 變形怪重複對話(大喊頻道)
	 * 
	 * @param pc
	 * @param chatType
	 * @param chatText
	 */
	private void doppelShouting(final L1PcInstance pc, final String chatText) {
		// 變形怪重複對話
		for (final L1Object obj : pc.getKnownObjects()) {
			if (obj instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) obj;
				if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
					mob.broadcastPacketX8(new S_NpcChatShouting(mob, chatText));
				}
			}
		}
	}

	/**
	 * 廣播卡判斷時間 by terry0412
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void check_broadcast(final L1PcInstance pc, final String chatText) {
		try {
			if (chatText.isEmpty() || (chatText.length() <= 0)) {
				pc.sendPackets(new S_SystemMessage("請輸入訊息內容。"));
				return;
			}

			// GM可使用指令進行開關
			if (pc.isGm()) {
				if (chatText.equals("開啟")) {
					BroadcastController.getInstance().setStop(false);
					pc.sendPackets(new S_SystemMessage("廣播系統已開啟。"));
					return;

				} else if (chatText.equals("關閉")) {
					BroadcastController.getInstance().setStop(true);
					pc.sendPackets(new S_SystemMessage("廣播系統已關閉。"));
					return;
				}
			}

			if (chatText.getBytes().length > 50) {
				pc.sendPackets(new S_SystemMessage("廣播訊息長度過長 (不能超過25個中文字)"));
				return;
			}

			// 連結字串
			final StringBuilder message = new StringBuilder();
			message.append("[").append(pc.getName()).append("] ").append(chatText);

			// 檢查背包是否有廣播卡
			final L1ItemInstance item = pc.getInventory().checkItemX(BroadcastSet.ITEM_ID, 1);
			if (item == null) {
				pc.sendPackets(new S_SystemMessage("不具有廣播卡，因此無法發送訊息。"));
				return;
			}

			// 將元素放入佇列
			if (BroadcastController.getInstance().requestWork(message.toString())) {
				// 刪除一個廣播卡道具
				pc.getInventory().removeItem(item, 1);

				pc.sendPackets(new S_SystemMessage("已成功發布廣播訊息。"));

			} else {
				pc.sendPackets(new S_SystemMessage("目前有太多等待訊息，請稍後再嘗試一次。"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 虛擬人物資料
	 * 
	 * @return
	 */
	public static L1DeInstance getDe(final String s) {
		final Collection<L1NpcInstance> allNpc = WorldNpc.get().all();
		// 不包含元素
		if (allNpc.isEmpty()) {
			return null;
		}

		for (final L1NpcInstance npc : allNpc) {
			if (npc instanceof L1DeInstance) {
				final L1DeInstance de = (L1DeInstance) npc;
				// System.out.println("de:" + de.getNameId());
				if (de.getNameId().equalsIgnoreCase(s)) {
					return de;
				}
			}
		}
		return null;
	}

	public String jdMethod_else() {
		return "[C] C_ItemCraft";
	}
}