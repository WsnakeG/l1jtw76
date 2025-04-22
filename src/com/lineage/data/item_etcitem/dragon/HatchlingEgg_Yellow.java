package com.lineage.data.item_etcitem.dragon;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>42511 頑皮幼龍蛋</font><BR>
 * 
 * @author dexc
 */
public class HatchlingEgg_Yellow extends ItemExecutor {

	/**
	 *
	 */
	private HatchlingEgg_Yellow() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new HatchlingEgg_Yellow();
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
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}

		int petcost = 0;
		final Object[] petList = pc.getPetList().values().toArray();
		if (petList.length > 2) {
			// 489：你無法一次控制那麼多寵物。
			pc.sendPackets(new S_ServerMessage(489));
			return;
		}
		for (final Object pet : petList) {
			final int nowpetcost = ((L1NpcInstance) pet).getPetcost();
			petcost += nowpetcost;
		}

		int charisma = pc.getCha();

		if (pc.isCrown()) {// 王族
			charisma += 6;

		} else if (pc.isKnight()) {// 騎士

		} else if (pc.isElf()) {// 精靈
			charisma += 12;

		} else if (pc.isWizard()) {// 法師
			charisma += 6;

		} else if (pc.isDarkelf()) {// 黑暗精靈
			charisma += 6;

		} else if (pc.isDragonKnight()) {// 龍騎士
			charisma += 6;

		} else if (pc.isIllusionist()) {// 幻術師
			charisma += 6;
		}

		charisma -= petcost;

		if (charisma <= 0) {
			// 489：你無法一次控制那麼多寵物。
			pc.sendPackets(new S_ServerMessage(489));
			return;
		}

		final L1PcInventory inv = pc.getInventory();
		if (inv.getSize() < 180) {
			final L1ItemInstance petamu = inv.storeItem(40314, 1); // 項圈
			if (petamu != null) {
				new L1PetInstance(71020, pc, petamu.getId());
				pc.sendPackets(new S_ItemName(petamu));

				// 刪除道具
				pc.getInventory().removeItem(item, 1);
			}
		}
	}
}