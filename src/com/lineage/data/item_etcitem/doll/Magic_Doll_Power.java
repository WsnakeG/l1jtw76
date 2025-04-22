package com.lineage.data.item_etcitem.doll;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;

/**
 * 魔法娃娃(額外) UPDATE `etcitem` SET `classname`='doll.Magic_Doll_Power' WHERE
 * `item_id`='55084';#軍師娃娃：小喬 UPDATE `etcitem` SET
 * `classname`='doll.Magic_Doll_Power' WHERE `item_id`='55085';#軍師娃娃：貂蟬 UPDATE
 * `etcitem` SET `classname`='doll.Magic_Doll_Power' WHERE
 * `item_id`='55086';#軍師娃娃：鳳雛 UPDATE `etcitem` SET
 * `classname`='doll.Magic_Doll_Power' WHERE `item_id`='55087';#軍師娃娃：臥龍
 */
public class Magic_Doll_Power extends ItemExecutor {

	/**
	 *
	 */
	private Magic_Doll_Power() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Magic_Doll_Power();
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
		useMagicDoll(pc, itemId, itemobj);
	}

	private void useMagicDoll(final L1PcInstance pc, final int itemId, final int itemObjectId) {
		if (pc.get_power_doll() != null) {
			if (pc.get_power_doll().getItemObjId() == itemObjectId) {
				// 娃娃收回
				pc.get_power_doll().deleteDoll();

			} else {
				pc.sendPackets(new S_ServerMessage(319));
			}
			return;
		}

		boolean iserror = false;
		final L1Doll type = DollPowerTable.get().get_type(itemId);
		if (type != null) {
			if (type.get_need() != null) {
				final int[] itemids = type.get_need();
				final int[] counts = type.get_counts();

				for (int i = 0; i < itemids.length; i++) {
					if (!pc.getInventory().checkItem(itemids[i], counts[i])) {
						final L1Item temp = ItemTable.get().getTemplate(itemids[i]);
						pc.sendPackets(new S_ServerMessage(337, temp.getNameId()));
						iserror = true;
					}
				}

				if (!iserror) {
					for (int i = 0; i < itemids.length; i++) {
						pc.getInventory().consumeItem(itemids[i], counts[i]);
					}
				}

			}

			if (!iserror) {
				final L1Npc template = NpcTable.get().getTemplate(71082);
				new L1DollInstance(template, pc, itemObjectId, type, true);
			}
		}
	}
}
