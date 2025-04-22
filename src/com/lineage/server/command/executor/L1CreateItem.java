package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.PowerItemSet;
import com.lineage.server.datatables.ItemPowerTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPower_name;

/**
 * 創造物品(參數:物品編號 - 數量 - 追加質)
 * 
 * @author dexc DELETE FROM `commands` WHERE `name`='item'; INSERT INTO
 *         `commands` VALUES ('item', '200', 'L1CreateItem', '創造物品(參數:物品編號 - 數量
 *         - 追加質 - 古文字代號)', '0');
 */
public class L1CreateItem implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1CreateItem.class);

	private L1CreateItem() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1CreateItem();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			final String nameid = st.nextToken();
			// 數量 60 1 2000
			long count = 1;
			if (st.hasMoreTokens()) {
				count = Long.parseLong(st.nextToken());
			}

			// 強化質
			int enchant = 0;
			if (st.hasMoreTokens()) {
				enchant = Integer.parseInt(st.nextToken());
			}

			// 古文字狀態
			int power_id = 0;
			if (st.hasMoreTokens()) {
				power_id = Integer.parseInt(st.nextToken());
			}

			// 物品編號
			int itemid = 0;
			try {
				itemid = Integer.parseInt(nameid);

			} catch (final NumberFormatException e) {
				itemid = ItemTable.get().findItemIdByNameWithoutSpace(nameid);
				if (itemid == 0) {
					pc.sendPackets(new S_SystemMessage("沒有找到條件吻合的物品。"));
					return;
				}
			}

			// 物品資料
			final L1Item temp = ItemTable.get().getTemplate(itemid);
			if (temp != null) {
				if (temp.isStackable()) {
					// 可以堆疊的物品
					final L1ItemInstance item = ItemTable.get().createItem(itemid);
					item.setEnchantLevel(0);
					item.setCount(count);
					item.setIdentified(true);
					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
						pc.getInventory().storeItem(item);
						// 403:获得0%。
						pc.sendPackets(new S_ServerMessage(403, item.getLogName() + "(ID:" + itemid + ")"));
					}
				} else {
					// 不可以堆疊的物品
					if (count > 10) {
						pc.sendPackets(new S_SystemMessage("不可以堆疊的物品一次創造數量禁止超過10"));
						return;
					}

					L1ItemInstance item = null;
					int createCount;
					for (createCount = 0; createCount < count; createCount++) {
						item = ItemTable.get().createItem(itemid);
						item.setEnchantLevel(enchant);
						item.setIdentified(true);

						if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
							if (PowerItemSet.START) {
								// 古文字誕生
								switch (item.getItem().getUseType()) {
								case 1:// 武器
								case 2:// 盔甲
								case 18:// T恤
								case 19:// 斗篷
								case 20:// 手套
								case 21:// 靴
								case 22:// 頭盔
								case 25:// 盾牌
									final L1ItemPower_name power = ItemPowerTable.POWER_NAME.get(power_id);
									if (power != null) {
										item.set_power_name(power);
										CharItemPowerReading.get().storeItem(item.getId(), power);
									}
									break;
								}
							}
							pc.getInventory().storeItem(item);

						} else {
							break;
						}
					}
					if (createCount > 0) {
						// 403:获得0%。
						pc.sendPackets(new S_ServerMessage(403, item.getLogName() + "(ID:" + itemid + ")"));
					}
				}
			} else {
				pc.sendPackets(new S_SystemMessage("指定ID不存在"));
			}
		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
