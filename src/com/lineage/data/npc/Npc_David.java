package com.lineage.data.npc;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

public class Npc_David extends NpcExecutor
{
  public static NpcExecutor get()
  {
    return new Npc_David();
  }

  public int type()
  {
    return 3;
  }

  public void talk(L1PcInstance pc, L1NpcInstance npc)
  {
	if (pc.getInventory().checkItem(49031, 1)) {//冰之結晶
		  if (pc.getInventory().checkItem(21081, 1)) {//冰之女王的耳環LV.0
			  pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout1"));			  
		  } else if (pc.getInventory().checkItem(21082, 1)) {//冰之女王的耳環LV.1 
			  pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout2"));
		  } else if (pc.getInventory().checkItem(21083, 1)) {//冰之女王的耳環LV.2
			  pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout3"));
		  } else if (pc.getInventory().checkItem(21084, 1)) {//冰之女王的耳環LV.3 
			  pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout4"));
		  } else if (pc.getInventory().checkItem(21085, 1)) {//冰之女王的耳環LV.4 
			  pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout5"));
		  } else if (pc.getInventory().checkItem(21086, 1)) {//冰之女王的耳環LV.5
			  pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout6"));
		  } else if (pc.getInventory().checkItem(21087, 1)) {//冰之女王的耳環LV.6 
			  pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout7"));
		  } else if (pc.getInventory().checkItem(21088, 1)) {//冰之女王的耳環LV.7 
			  pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout8"));
		  } else {//身上沒有耳環
			  pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout17"));
		  }	
		  
	} else {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gemout0"));
	}
    
  }

  public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {	 
	  String npcName = npc.getNpcTemplate().get_name();
	  
      if (cmd.equalsIgnoreCase("b")) {
    	  if (pc.getInventory().consumeItem(21081, 1L) //冰之女王的耳環LV.0
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21082, 1);
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      } else if (cmd.equalsIgnoreCase("c")) {
    	  if (pc.getInventory().consumeItem(21082, 1L) //冰之女王的耳環LV.1
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21083, 1);
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      } else if (cmd.equalsIgnoreCase("d")) {
    	  if (pc.getInventory().consumeItem(21083, 1L) //冰之女王的耳環LV.2
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21084, 1);
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      } else if (cmd.equalsIgnoreCase("e")) {
    	  if (pc.getInventory().consumeItem(21084, 1L) //冰之女王的耳環LV.3
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21085, 1);
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      } else if (cmd.equalsIgnoreCase("f")) {
    	  if (pc.getInventory().consumeItem(21085, 1L) //冰之女王的耳環LV.4
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21086, 1);
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      } else if (cmd.equalsIgnoreCase("g")) {
    	  if (pc.getInventory().consumeItem(21086, 1L) //冰之女王的耳環LV.5
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21087, 1);
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      } else if (cmd.equalsIgnoreCase("h")) {
    	  if (pc.getInventory().consumeItem(21087, 1L) //冰之女王的耳環LV.6
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21088, 1);
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      } else if (cmd.equalsIgnoreCase("i")) {
    	  if (pc.getInventory().consumeItem(21088, 1L) //冰之女王的耳環LV.7
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21089, 1);//冰之女王的耳環LV.8(力量)
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      } else if (cmd.equalsIgnoreCase("j")) {
    	  if (pc.getInventory().consumeItem(21088, 1L) //冰之女王的耳環LV.7
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21090, 1);//冰之女王的耳環LV.8(敏捷)
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      } else if (cmd.equalsIgnoreCase("k")) {
    	  if (pc.getInventory().consumeItem(21088, 1L) //冰之女王的耳環LV.7
    			  && pc.getInventory().consumeItem(49031, 1L)) { //冰之結晶        	
                CreateNewItem.createNewItem_NPC(pc, npcName, 21091, 1);//冰之女王的耳環LV.8(精神)
                pc.sendPackets(new S_CloseList(pc.getId()));
    	  }
      }
      
  }
}