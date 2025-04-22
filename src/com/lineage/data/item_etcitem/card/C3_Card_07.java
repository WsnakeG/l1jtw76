package com.lineage.data.item_etcitem.card;

import java.util.Random;

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
 * 伏曦易經-土
 * 
 * @author dexc
 */
public class C3_Card_07 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(C3_Card_07.class);

	private static final int _key = 7;

	/**
	 *
	 */
	private C3_Card_07() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new C3_Card_07();
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
				pc.sendPackets(new S_ServerMessage("\\aE使用前必須先解除物品裝備..."));
				return;
			}
			final Random random = new Random();
			final int rnd = random.nextInt(1000) + 1;
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
				final L1ItemPower_name tgpower = tgItem.get_power_name();

				if (tgpower == null || tgpower.get_power_id() != _key) {
					if (_a >= rnd) {
						pc.getInventory().removeItem(item, 1);
						tgItem.set_power_name(null);
						CharItemPowerReading.get().delItem(tgItem.getId());
						final L1ItemPower_name power = ItemPowerTable.POWER_NAME.get(_key);
						pc.sendPackets(
								new S_ServerMessage("\\aD成功灌注 [" + power.get_power_name() + "] 神秘的力量"));
						tgItem.set_power_name(power);
						CharItemPowerReading.get().storeItem(tgItem.getId(), tgItem.get_power_name());
						pc.sendPackets(new S_ItemStatus(tgItem));

					} else {
						// 1411 對\f1%0附加魔法失敗。
						pc.sendPackets(new S_ServerMessage("\\aD發出強烈神秘的光芒...提煉失敗了"));
						pc.getInventory().removeItem(item, 1);
					}
				} else {
					// 沒有任何事情發生
					pc.sendPackets(new S_ServerMessage("\\aL相同能力無法再進行強化喔!"));
				}

				break;

			default:
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage("\\aH請針對裝備來進行強化..."));
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _a = 0;

	@Override
	public void set_set(String[] set) {

		try {
			_a = Integer.parseInt(set[1]);
		} catch (Exception e) {
		}
	}
}
