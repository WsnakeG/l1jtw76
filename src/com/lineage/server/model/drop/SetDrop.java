package com.lineage.server.model.drop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.data.event.PowerItemSet;
// import com.lineage.data.event.PowerItemSet;
import com.lineage.server.datatables.DropItemTable;
import com.lineage.server.datatables.ItemLimitation;
import com.lineage.server.datatables.ItemPowerTable;
// import com.lineage.server.datatables.ItemPowerTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.templates.L1Drop;
import com.lineage.server.templates.L1DropMap;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPower_name;
// import com.lineage.server.templates.L1ItemPower_name;
import com.lineage.server.world.World;

/**
 * NPC持有物品取回
 * 
 * @author dexc
 */
public class SetDrop implements SetDropExecutor {

	private static final Log _log = LogFactory.getLog(SetDrop.class);

	private static Map<Integer, ArrayList<L1Drop>> _droplist;

	public static Map<Integer, HashMap<Integer, ArrayList<L1DropMap>>> _droplistX;

	private static final Random _random = new Random();

	/**
	 * 設置掉落資料
	 * 
	 * @param droplists
	 */
	@Override
	public void addDropMap(final Map<Integer, ArrayList<L1Drop>> droplists) {
		if (_droplist != null) {
			_droplist.clear();
		}
		_droplist = droplists;
	}

	/**
	 * 設置指定MAP掉落資料
	 * 
	 * @param droplists
	 */
	@Override
	public void addDropMapX(
			final Map<Integer, HashMap<Integer, ArrayList<L1DropMap>>> droplists) {
		if (_droplistX != null) {
			_droplistX.clear();
		}
		_droplistX = droplists;
	}

	/**
	 * NPC持有物品資料取回
	 * 
	 * @param npc
	 * @param inventory
	 */
	@Override
	public void setDrop(final L1NpcInstance npc, final L1Inventory inventory) {
		setDrop(npc, inventory, 0.0);
	}

	/**
	 * NPC持有物品資料取回
	 * 
	 * @param npc
	 * @param inventory
	 * @param random
	 */
	@Override
	public void setDrop(final L1NpcInstance npc, final L1Inventory inventory,
			final double random) {
		// NPC掉落資料取回
		final int mobId = npc.getNpcTemplate().get_npcId();
		// NPC位置
		final int mapid = npc.getMapId();
		final HashMap<Integer, ArrayList<L1DropMap>> droplistX = _droplistX
				.get(mapid);
		if (droplistX != null) {
			// 160705調整 不指定NPC只針對地圖編號 (mobId/原：mapid/改後)
			final ArrayList<L1DropMap> list = droplistX.get(mapid);
			if (list != null) {
				setDrop(npc, inventory, list);
			}
		}

		final ArrayList<L1Drop> dropList = _droplist.get(mobId);
		if (dropList == null) {
			return;
		}

		// 取回增加倍率
		double droprate = ConfigRate.RATE_DROP_ITEMS;
		if (droprate <= 0) {
			droprate = 0;
		}
		droprate += random;

		double adenarate = ConfigRate.RATE_DROP_ADENA;
		if (adenarate <= 0) {
			adenarate = 0;
		}

		if ((droprate <= 0) && (adenarate <= 0)) {
			return;
		}

		// 幸運值 by terry0412
		int influence_lucky = 0;

		// 取範圍內幸運值最大的玩家
		for (final L1PcInstance pc : World.get().getRecognizePlayer(npc)) {
			if (influence_lucky < pc.getLuckValue()) {
				influence_lucky = pc.getLuckValue();
			}
		}
		if (influence_lucky != 0) {
			droprate += 0.1 * influence_lucky;
		}

		for (final L1Drop drop : dropList) {
			// 掉落物品編號
			final int itemId = drop.getItemid();
			// 物品為金幣掉落數量為0
			if ((adenarate == 0) && (itemId == L1ItemId.ADENA)) {
				continue;
			}

			// 取回隨機機率
			final int randomChance = _random.nextInt(0xf4240) + 1;
			// 地圖增加掉率
			final double rateOfMapId = MapsTable.get().getDropRate(
					npc.getMapId());
			// 指定物品增加掉率
			final double rateOfItem = DropItemTable.get().getDropRate(itemId);

			if ((droprate == 0)
					|| ((drop.getChance() * droprate * rateOfMapId * rateOfItem) < randomChance)) {
				continue;
			}

			// 機率滿足 檢查總量限制
			if (!ItemLimitation.get().checkLimitation(itemId)) {
				continue;
			}

			// ドロップ個数を設定
			final double amount = DropItemTable.get().getDropAmount(itemId);
			final long min = (long) (drop.getMin() * amount);
			final long max = (long) (drop.getMax() * amount);

			long itemCount = min;
			final long addCount = (max - min) + 1;
			if (addCount > 1) {
				itemCount += _random.nextInt((int) addCount);
			}
			// 物件為金幣 加入倍率
			if (itemId == L1ItemId.ADENA) {
				itemCount *= adenarate;
			}
			// 數量為0
			if (itemCount < 0) {
				itemCount = 0;
			}
			// 限制持有數量
			if (itemCount > 2000000000) {
				itemCount = 2000000000;
			}
			if (itemCount > 0) {
				// 隨機強化值 by terry0412
				int enchantLevel = drop.getEnchantMin();

				if (enchantLevel < drop.getEnchantMax()) {
					// 隨機 min~max
					enchantLevel += _random
							.nextInt((drop.getEnchantMax() - enchantLevel) + 1);
				}

				// 加入NPC背包中
				additem(inventory, itemId, enchantLevel, itemCount);

			} else {
				_log.error("NPC加入背包物件數量為0(" + mobId + " itemId: " + itemId
						+ ")");
			}
		}
	}

	/**
	 * 指定地圖NPC持有物品資料取回
	 * 
	 * @param npc
	 * @param inventory
	 * @param dropList
	 */
	private void setDrop(final L1NpcInstance npc, final L1Inventory inventory,
			final ArrayList<L1DropMap> dropList) {
		// 取回增加倍率
		double droprate = ConfigRate.RATE_DROP_ITEMS;
		if (droprate <= 0) {
			droprate = 0;
		}

		double adenarate = ConfigRate.RATE_DROP_ADENA;
		if (adenarate <= 0) {
			adenarate = 0;
		}

		if ((droprate <= 0) && (adenarate <= 0)) {
			return;
		}

		for (final L1DropMap drop : dropList) {
			// 掉落物品編號
			final int itemId = drop.getItemid();
			// 物品為金幣掉落數量為0
			if ((adenarate == 0) && (itemId == L1ItemId.ADENA)) {
				continue;
			}

			// 取回隨機機率
			final int randomChance = _random.nextInt(0xf4240) + 1;
			final double rateOfMapId = MapsTable.get().getDropRate(
					npc.getMapId());
			final double rateOfItem = DropItemTable.get().getDropRate(itemId);

			final boolean noadd = ((drop.getChance() * droprate * rateOfMapId * rateOfItem) < randomChance);
			if ((droprate == 0) || noadd) {
				continue;
			}
			// 指定的物件提高掉落數量
			final double amount = DropItemTable.get().getDropAmount(itemId);
			final long min = (long) (drop.getMin() * amount);
			final long max = (long) (drop.getMax() * amount);

			// 機率滿足 檢查總量限制
			if (!ItemLimitation.get().checkLimitation(itemId)) {
				continue;
			}

			long itemCount = min;
			final long addCount = (max - min) + 1;
			if (addCount > 1) {
				itemCount += _random.nextInt((int) addCount);
			}
			// 物件為金幣 加入倍率
			if (itemId == L1ItemId.ADENA) {
				itemCount *= adenarate;
			}
			// 數量為0
			if (itemCount < 0) {
				itemCount = 0;
			}
			// 限制持有數量
			if (itemCount > 2000000000) {
				itemCount = 2000000000;
			}
			if (itemCount > 0) {
				// System.out.println("add:"+npc.getName() + " droprate:" +
				// droprate +" itemId:"+itemId + " itemCount:"+itemCount);

				// 隨機強化值 by terry0412
				int enchantLevel = drop.getEnchantMin();

				if (enchantLevel < drop.getEnchantMax()) {
					// 隨機 min~max
					enchantLevel += _random
							.nextInt((drop.getEnchantMax() - enchantLevel) + 1);
				}

				// 加入NPC背包中
				additem(inventory, itemId, enchantLevel, itemCount);

			} else {
				_log.error("NPC加入背包物件數量為0(" + npc.getNpcId() + " itemId: "
						+ itemId + ") 指定地圖");
			}
		}
	}

	/**
	 * 對指定背包加入物件
	 * 
	 * @param inventory
	 * @param itemId
	 * @param itemCount
	 */
	private final void additem(final L1Inventory inventory, final int itemId,
			final int enchantLevel, final long itemCount) {
		try {
			final L1Item tmp = ItemTable.get().getTemplate(itemId);
			if (tmp == null) {
				_log.error("掉落物品設置錯誤(無這編號物品): " + itemId);
				return;
			}
			if (tmp.isStackable()) { // 可以堆疊
				// 生除物品
				final L1ItemInstance item = ItemTable.get().createItem(itemId);
				if (item != null) {
					item.setEnchantLevel(enchantLevel); // 隨機強化值 by terry0412
					item.setCount(itemCount);
					// 加入背包
					inventory.storeItem(item);
				}

			} else {
				for (int i = 0; i < itemCount; i++) {
					// 產生物品
					final L1ItemInstance item = ItemTable.get().createItem(
							itemId);
					if (item != null) {
						item.setEnchantLevel(enchantLevel); // 隨機強化值 by
															// terry0412
						item.setCount(1);
						// 加入背包
						inventory.storeItem(item);
						if (PowerItemSet.START) {
							if (ConfigAlt.AncientSetDrop) {
								if (_random.nextBoolean()) {
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
										int index = 0;
										L1ItemPower_name power = null;
										while (index <= 3) {
											if (!ItemPowerTable.POWER_NAME
													.isEmpty()) {
												final int key = _random
														.nextInt(ItemPowerTable.POWER_NAME
																.size()) + 1;
												final L1ItemPower_name v = ItemPowerTable.POWER_NAME
														.get(key);
												if (_random.nextInt(1000) <= v
														.get_dice()) {
													power = v;
												}
											}
											index++;
										}
										if (power != null) {
											item.set_power_name(power);
										}
										break;
									}
								}
							}
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
