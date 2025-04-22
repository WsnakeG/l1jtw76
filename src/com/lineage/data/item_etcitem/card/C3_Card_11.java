package com.lineage.data.item_etcitem.card;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemPowerTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemPower_name;

/**
 * 伏曦易經-震
 * 
 * @author dexc
 */
public class C3_Card_11 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(C3_Card_11.class);

	private static final int _key = 11;

	/**
	 *
	 */
	private C3_Card_11() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new C3_Card_11();
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
		try {
			// 對象OBJID
			final int targObjId = data[0];

			// 目標物品
			final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

			if (tgItem == null) {
				return;
			}

			if (tgItem.isEquipped()) {
				pc.sendPackets(new S_ServerMessage("\\fR你必須先解除物品裝備!"));
				return;
			}

			// 古文字誕生
			switch (tgItem.getItem().getUseType()) {
			case 1:// 武器
			case 2:// 盔甲
			case 18:// T恤
			case 19:// 斗篷
			case 20:// 手套
			case 21:// 靴
			case 22:// 頭盔
			case 25:// 盾牌
				pc.getInventory().removeItem(item, 1);
				tgItem.set_power_name(null);
				CharItemPowerReading.get().delItem(tgItem.getId());
				final L1ItemPower_name power = ItemPowerTable.POWER_NAME.get(_key);
				pc.sendPackets(new S_ServerMessage("\\fT獲得" + power.get_power_name() + " 的力量"));
				tgItem.set_power_name(power);
				CharItemPowerReading.get().storeItem(tgItem.getId(), tgItem.get_power_name());
				pc.sendPackets(new S_ItemStatus(tgItem));
				break;

			default:
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
