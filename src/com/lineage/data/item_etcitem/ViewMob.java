package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ShopSellList;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
/**
 * 顯示怪物裝備道具
  @author hpc20207
 */
public class ViewMob extends ItemExecutor {
	private ViewMob() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ViewMob();
	}
	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		// TODO 自動生成的方法存根
		final int spellsc_objid = data[0];
		final L1Object target = World.get().findObject(spellsc_objid);
		
		
		if (target != null) {
			if (target instanceof L1MonsterInstance) {
				L1NpcInstance npc = (L1NpcInstance) target;
					int npcid = npc.getNpcId();
					int mapid = npc.getMapId();
					pc.sendPackets(new S_ShopSellList(npcid,mapid, pc));
				} else {
					pc.sendPackets(new S_SystemMessage("你查詢的不是怪物"));
				}
		} else {
			pc.sendPackets(new S_SystemMessage("你查詢的不是怪物"));
		}
	}

}
