package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;

import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.world.World;
import java.util.ArrayList;

/**
 * 魔法魔杖(下層雷擊爆彈)
 * 
 * @author XXX
 * 
 */
public class Firestorml_Magic_Wand extends ItemExecutor {
	public static ItemExecutor get() {
		return new Firestorml_Magic_Wand();
	}

	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (item.getChargeCount() == 0) {
			pc.getInventory().deleteItem(item);
			return;
		}
		pc.sendPacketsX8(new S_DoActionGFX(pc.getId(), 17));

		int useCount = item.getChargeCount() - 1;
		if (useCount > 0) {
			item.setChargeCount(useCount);
			pc.getInventory().updateItem(item, 128);
		} else {
			pc.getInventory().deleteItem(item);
		}
		ArrayList<L1Object> list = World.get().getVisibleObjects(pc, 5);
		if (list == null) {
			return;
		}
		for (L1Object object : list) {
			if ((object instanceof L1MonsterInstance)) {
				L1MonsterInstance dota = (L1MonsterInstance) object;
				// 只能對此NPC使用
				//if (((dota.getNpcId() >= 190029) && (dota.getNpcId() <= 190041)) || (dota.getNpcId() == 190044)) {
				if (((dota.getNpcId() >= 111493) && (dota.getNpcId() <= 111512)) || (dota.getNpcId() == 111485)) {
					dota.broadcastPacketAll(new S_DoActionGFX(dota.getId(), 2));
					dota.receiveDamage(pc, 250);
				}
			}
		}
		pc.sendPacketsAll(new S_SkillSound(pc.getId(), 10407));
	}
}
