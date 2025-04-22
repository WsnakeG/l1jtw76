package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

public class Npc_Canus extends NpcExecutor
{
  public static NpcExecutor get()
  {
    return new Npc_Canus();
  }

  public int type()
  {
    return 3;
  }

  public void talk(L1PcInstance pc, L1NpcInstance npc)
  {
    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "canus1"));
  }

  public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount)
  {
    if (cmd.equals("a")) {
      L1Teleport.teleport(pc, 32853, 32862, (short) 537, 6, true);
    } else if (cmd.equals("b")) {
      L1Teleport.teleport(pc, 32805, 32869, (short) 537, 6, true);
    } else if (cmd.equals("d")) {
    	 try {
    		 if (pc.getInventory().checkItem(40308, 5000)) {    			
    			 pc.getInventory().consumeItem(40308, 5000);
    			 pc.save();
    			 pc.beginGhost(32853, 32862, (short)537, true, 600);
    		 } else {
    			 pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "canus3")); 
    		 }
    		 } catch (Exception e) { 			 
    		 }
    }
  }
}