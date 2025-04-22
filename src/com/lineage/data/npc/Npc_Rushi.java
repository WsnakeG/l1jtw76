package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Npc_Rushi extends NpcExecutor {
	public static NpcExecutor get() {
		return new Npc_Rushi();
	}

	public int type() {
		return 3;
	}

	public void talk(L1PcInstance pc, L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "rushi05"));
	}

	public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
		/**
		 * <font fg=ffffaf>舊武器:</font><br>
		 * 武士刀<br>
		 * 大馬士革刀<br>
		 * 細劍<br>
		 * 雙手劍<br>
		 * 十字弓<br>
		 * 尤米弓<br>
		 * 力量魔法杖<br>
		 * 精靈匕首<br>
		 * 拉斯塔巴德短劍<br>
		 * 狂戰士斧<br>
		 * 巨斧<br>
		 * 巨劍<br>
		 * 巫術魔法杖<br>
		 * 美基魔法杖<br>
		 * 精靈之矛<br>
		 * 法丘<br>
		 * 侵略者之劍<br>
		 */
		int[] oldweapon = { 41, 37, 42, 52, 180, 181, 131, 5, 6, 145, 148, 64, 125, 129, 99, 104, 32 };
		int newWeapon = 0;
		boolean success = false;
		if (cmd.equalsIgnoreCase("A")) {
			newWeapon = 259;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L))
						&& (pc.getInventory().checkItem(40308, 5000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
					pc.getInventory().consumeItem(40308, 5000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(7);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("B")) {
			newWeapon = 410132;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L))
						&& (pc.getInventory().checkItem(40308, 5000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
					pc.getInventory().consumeItem(40308, 5000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(7);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("C")) {
			newWeapon = 410157;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L))
						&& (pc.getInventory().checkItem(40308, 5000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
					pc.getInventory().consumeItem(40308, 5000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(7);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("D")) {
			newWeapon = 410131;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L))
						&& (pc.getInventory().checkItem(40308, 5000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
					pc.getInventory().consumeItem(40308, 5000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(7);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("E")) {
			newWeapon = 410133;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L))
						&& (pc.getInventory().checkItem(40308, 5000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
					pc.getInventory().consumeItem(40308, 5000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(7);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("F")) {
			newWeapon = 410134;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L))
						&& (pc.getInventory().checkItem(40308, 5000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
					pc.getInventory().consumeItem(40308, 5000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(7);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("G")) {
			newWeapon = 410135;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L))
						&& (pc.getInventory().checkItem(40308, 5000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
					pc.getInventory().consumeItem(40308, 5000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(7);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("H")) {
			newWeapon = 259;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L))
						&& (pc.getInventory().checkItem(40308, 10000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
					pc.getInventory().consumeItem(40308, 10000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(8);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("I")) {
			newWeapon = 410132;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L))
						&& (pc.getInventory().checkItem(40308, 10000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
					pc.getInventory().consumeItem(40308, 10000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(8);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("J")) {
			newWeapon = 410157;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L))
						&& (pc.getInventory().checkItem(40308, 10000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
					pc.getInventory().consumeItem(40308, 10000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(8);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("K")) {
			newWeapon = 410131;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L))
						&& (pc.getInventory().checkItem(40308, 10000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
					pc.getInventory().consumeItem(40308, 10000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(8);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("L")) {
			newWeapon = 410133;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L))
						&& (pc.getInventory().checkItem(40308, 10000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
					pc.getInventory().consumeItem(40308, 10000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(8);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("M")) {
			newWeapon = 410134;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L))
						&& (pc.getInventory().checkItem(40308, 10000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
					pc.getInventory().consumeItem(40308, 10000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(8);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("N")) {
			newWeapon = 410135;
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L))
						&& (pc.getInventory().checkItem(40308, 10000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
					pc.getInventory().consumeItem(40308, 10000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(8);
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("A1")) {//+7 疾風斧頭
			newWeapon = 863;//20180720修改編號
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L))
						&& (pc.getInventory().checkItem(40308, 5000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
					pc.getInventory().consumeItem(40308, 5000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(7);//+7
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}
		} else if (cmd.equalsIgnoreCase("A2")) {//+8 疾風斧頭
			newWeapon = 863;//20180720修改編號
			for (int i = 0; i < oldweapon.length; i++) {
				if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L))
						&& (pc.getInventory().checkItem(40308, 10000000L))) {
					pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
					pc.getInventory().consumeItem(40308, 10000000L);
					final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
					item.setEnchantLevel(8);//+8
					item.setIdentified(true);
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
					success = true;
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}
			}

		}

		if (!success) {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "rushi04"));
		}
	}
}