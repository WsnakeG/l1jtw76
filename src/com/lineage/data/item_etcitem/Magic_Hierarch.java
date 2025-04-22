package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1HierarchInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;

/**
 * 魔法祭司 70471~70473 70532
 * 
 * @author admin
 */
public class Magic_Hierarch extends ItemExecutor {

	/**
	 *
	 */
	private Magic_Hierarch() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Magic_Hierarch();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		final int itemId = item.getItemId();
		final int itemobj = item.getId();
		if (((itemId >= 70471) && (itemId <= 70473)) || (itemId == 70532)) {
			useHierarch(pc, item, itemobj);
		}
	}

	// 使用隨身祭司
	private void useHierarch(final L1PcInstance pc, final L1ItemInstance item, final int itemObjectId) {
		int Hierarch_Id = 0;

		switch (item.getItem().getItemId()) {
		case 70471: // 祭司
			Hierarch_Id = 95401;
			break;
		case 70472: // 祭司
			Hierarch_Id = 95402;
			break;
		case 70473: // 祭司
			Hierarch_Id = 95403;
			break;
		case 70532: // 祭司
			Hierarch_Id = 95432;
			break;
		}

		for (final Object HierarchObject : pc.getPetList().values().toArray()) {
			if (HierarchObject instanceof L1HierarchInstance) {
				final L1HierarchInstance Hierarch = (L1HierarchInstance) HierarchObject;
				Hierarch.setHierarch(2); // 解散
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), ""));
				return;
			}
		}

		if (pc.isInvisble()) { // 隱身
			return;
		}

		if (pc.getInventory().consumeItem(41246, 50)) { // 消耗魔法結晶體 50個
			// final int magicStauas1 = item.getUpdateDmg();
			// final int magicStauas2 = item.getUpdateDmgModifier();
			// final int magicStauas3 = item.getUpdateHitModifier();
			final L1Npc npcTemp = NpcTable.get().getTemplate(Hierarch_Id);

			// L1ItemInstance tgItem = pc.getInventory().getItem(itemObjectId);
			// L1ItemInstance check_item =
			// pc.getInventory().getItem(tgItem.getId());
			// int skilltype = check_item.getUpdateDmg();
			final L1HierarchInstance Hierarch = new L1HierarchInstance(npcTemp, pc, itemObjectId, 0, 0, 0);
			// if (skilltype != 0 && check_item.getRemainingTime() != 0) {
			// check_item.startEquipmentTimer(pc); // 開始倒數
			// Hierarch = new L1HierarchInstance(npcTemp, pc, itemObjectId,
			// skilltype, 0, 0);
			// } else {
			//
			// }

			Hierarch.setCurrentMp(0);
			Hierarch.broadcastPacketAll(new S_SkillSound(Hierarch.getId(), 5935));
			pc.setSkillEffect(12345, 3600 * 1000); // 判斷已擁有隨身祭司
		} else {
			final L1Item temp = ItemTable.get().getTemplate(41246);
			pc.sendPackets(new S_ServerMessage(337, temp.getNameId()));
		}
	}

}
