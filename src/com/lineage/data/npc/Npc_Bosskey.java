package com.lineage.data.npc;

import java.sql.Timestamp;

import java.util.Calendar;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.InnTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Inn;
import com.lineage.server.world.World;

// Npc_BosskeyNpc_Bosskey

public class Npc_Bosskey extends NpcExecutor {
	public static NpcExecutor get() {
		return new Npc_Bosskey();
	}

	public int type() {
		return 3;
	}

	public void talk(L1PcInstance pc, L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bosskey1"));
	}

	public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {

		if (cmd.equalsIgnoreCase("1")) {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bosskey4"));
			return;
		}

		if (cmd.equalsIgnoreCase("2") || cmd.equalsIgnoreCase("3") || cmd.equalsIgnoreCase("4")) {

			switch (cmd) {
			case "2":
				amount = 4;
				break;
			case "3":
				amount = 8;
				break;
			case "4":
				amount = 16;
				break;
			}

			int npcId = npc.getNpcTemplate().get_npcId();
			boolean canRent = false;// 是否可以租房
			boolean findRoom = false;// 是否找到空房
			boolean isRented = false;// 是否已有租房
			int roomNumber = 0;// 空房的房號
			byte roomCount = 0;// 已佔用房數
			for (int i = 0; i < 16; i++) {
				L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
				if (inn != null) {
					Timestamp dueTime = inn.getDueTime();
					Calendar cal = Calendar.getInstance();
					long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000L;
					/*
					 * if ((inn.getLodgerId() == pc.getId()) && (checkDueTime < 0L)) {//還有時間 isRented = true; break; }
					 */
					if (pc.getInventory().checkItem(82503, 1)) {// 身上有訓練所鑰匙
						isRented = true;
						break;
					}
					if ((!findRoom) && (!isRented)) {
						if (checkDueTime >= 0L) {// 已超過時間
							canRent = true;
							findRoom = true;
							roomNumber = inn.getRoomNumber();
						} else if (inn.getLodgerId() == pc.getId()) {// 自己租過的房間
							canRent = true;
							findRoom = true;
							roomNumber = inn.getRoomNumber();
						} else {
							roomCount = (byte) (roomCount + 1);// 已佔用房數
						}
					}
				}
			}

			if (isRented) {// 已租用訓練所
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bosskey6"));

			} else if (roomCount >= 16) {// 已佔用房數
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bosskey3"));

			} else if (canRent) {
				if (pc.getInventory().checkItem(L1ItemId.ADENA, (300 * amount))) {
					L1Inn inn = InnTable.getInstance().getTemplate(npcId, roomNumber);
					if (inn != null) {
						// 租用時間 4小時
						Timestamp ts = new Timestamp(System.currentTimeMillis() + (60 * 60 * 4 * 1000));
						// 登入旅館資料
						L1ItemInstance item = ItemTable.get().createItem(82503); // 訓練所鑰匙
						if (item != null) {
							item.setKeyId(item.getId()); // 鑰匙編號
							item.setInnNpcId(npcId); // 旅館NPC
							item.setHall(false); // 判斷租房間 or 會議室
							item.setDueTime(ts); // 租用時間
							item.setCount(amount); // 鑰匙數量

							inn.setKeyId(item.getKeyId()); // 旅館鑰匙
							inn.setLodgerId(pc.getId()); // 租用人
							inn.setHall(false); // 判斷租房間 or 會議室
							inn.setDueTime(ts); // 租用時間
							// DB更新
							InnTable.getInstance().updateInn(inn);

							pc.getInventory().consumeItem(L1ItemId.ADENA, (300 * amount)); // 扣除金幣

							// 給予鑰匙並登入鑰匙資料
							L1Inventory inventory;
							if (pc.getInventory().checkAddItem(item, amount) == L1Inventory.OK) {
								inventory = pc.getInventory();
							} else {
								inventory = World.get().getInventory(pc.getLocation());
							}
							inventory.storeItem(item);

							if (InnKeyTable.checkey(item)) {// 鑰匙資料已存在
								InnKeyTable.DeleteKey(item);
								InnKeyTable.StoreKey(item);
							} else {
								InnKeyTable.StoreKey(item);
							}

							pc.sendPackets(new S_ServerMessage(143, npc.getName(), item.getLogName())); // \f1%0%s 給你 %1%o 。
							String[] msg = { npc.getName() };
							// 要一起使用房間的話，請把鑰匙給其他人，往旁邊的樓梯上去即可。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bosskey7", msg));

						}
					}
				} else {
					String[] msg = { npc.getName() };
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bosskey5", msg));
				}

			}
		} else if (cmd.equalsIgnoreCase("6")) {// 進入訓練所
			int npcId = npc.getNpcTemplate().get_npcId();
			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (item.getInnNpcId() == npcId) {// 對應的npcid相同
					for (int i = 0; i < 16; i++) {
						L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
						if (inn.getKeyId() == item.getKeyId()) {// 鑰匙編號相同
							Timestamp dueTime = item.getDueTime();
							if (dueTime != null) {// 具有租約時間
								Calendar cal = Calendar.getInstance();
								if ((cal.getTimeInMillis() - dueTime.getTime()) / 1000L < 0L) {// 還未到退房時間
									pc.set_showId(item.getKeyId());// 設置副本編號
									L1Teleport.teleport(pc, 32899, 32818, (short) 1400, pc.getHeading(), true);
									break;
								}
							}
						}
					}
				}
			}
		}

	}
}