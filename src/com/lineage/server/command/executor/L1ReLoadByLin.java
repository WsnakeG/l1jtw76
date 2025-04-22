package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.BeginnerTable;
import com.lineage.server.datatables.CommandsTable;
import com.lineage.server.datatables.DropItemTable;
import com.lineage.server.datatables.DropMapTable;
import com.lineage.server.datatables.DropTable;
import com.lineage.server.datatables.ItemBoxTable;
import com.lineage.server.datatables.ItemMsgTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.MobSkillTable;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.datatables.NpcScoreTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.ShopXTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class L1ReLoadByLin implements L1CommandExecutor {

	public static L1CommandExecutor getInstance() {
		return new L1ReLoadByLin();
	}

	// 1:item 2:box 3:npc 4:掉落 5:商店 6:商城
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		if (arg.equalsIgnoreCase("1")) {
			pc.sendPackets(new S_ServerMessage("重新讀取'etcitem', 'weapon', 'armor', 'server_msg_item_id'表 。"));
			ItemTable.get().load();
			ItemMsgTable.get().reload();
		} else if (arg.equalsIgnoreCase("2")) {
			pc.sendPackets(new S_ServerMessage("重新讀取'etcitem_box', 'etcitem_boxs', 'etcitem_box_key'表 。"));
			ItemBoxTable.get().reload();
		} else if (arg.equalsIgnoreCase("3")) {
			pc.sendPackets(new S_ServerMessage("重新讀取'npc', 'npcaction', 'npcscore', 'mobskill', 'npcaction_teleport'表 。"));
			NpcTable.get().reload();
			try {
				Thread.sleep(1000);
				NPCTalkDataTable.get().reload();
				NpcScoreTable.get().reload();
				MobSkillTable.getInstance().reload();
				NpcTeleportTable.get().reload();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (Exception e) {
			}
		} else if (arg.equalsIgnoreCase("4")) {
			pc.sendPackets(new S_ServerMessage("重新讀取'droplist', 'droplist_map', 'drop_item'表 。"));
			DropTable.get().restDropTable();
			DropMapTable.get().restDropMapTable();
			DropItemTable.get().restDropItemTable();
		} else if (arg.equalsIgnoreCase("5")) {
			pc.sendPackets(new S_ServerMessage("重新讀取'shop', 'shop_cn', 'server_shopx'表。"));
			ShopTable.get().restshop();
			ShopCnTable.get().restshopCn();
			ShopXTable.get().load();
		} else if (arg.equalsIgnoreCase("6")) {
			pc.sendPackets(new S_ServerMessage("重新讀取'beginner', 'skills'表。"));
			BeginnerTable.get().restBeginnerTable();
			SkillsTable.get().load();
		} else if (arg.equalsIgnoreCase("16888")) {
			pc.sendPackets(new S_ServerMessage("重新讀取'commands'表。"));
			CommandsTable.get().restcommands();
		} else {
			pc.sendPackets(new S_ServerMessage("\n\r1:item相關" + "\n\r2:box相關" + "\n\r3:npc相關" + "\n\r4:drop相關" + "\n\r5:shop相關" + "\n\r6:char相關"));
		}
	}
}