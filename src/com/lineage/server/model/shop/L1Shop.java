package com.lineage.server.model.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lineage.config.ConfigRate;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.datatables.lock.TownReading;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1TaxCalculator;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopItem;
import com.lineage.server.utils.RangeInt;
import com.lineage.server.world.World;

/**
 * 購物相關
 * 
 * @author dexc
 */
public class L1Shop {

	private final int _npcId;

	private final List<L1ShopItem> _sellingItems;

	private final List<L1ShopItem> _purchasingItems;

	public L1Shop(final int npcId, final List<L1ShopItem> sellingItems,
			final List<L1ShopItem> purchasingItems) {
		if ((sellingItems == null) || (purchasingItems == null)) {
			throw new NullPointerException();
		}

		_npcId = npcId;
		_sellingItems = sellingItems;
		_purchasingItems = purchasingItems;
	}

	public int getNpcId() {
		return _npcId;
	}

	public List<L1ShopItem> getSellingItems() {
		return _sellingItems;
	}

	/**
	 * この商店で、指定されたアイテムが買取可能な状態であるかを返す。
	 * 
	 * @param item
	 * @return アイテムが買取可能であればtrue
	 */
	private boolean isPurchaseableItem(final L1ItemInstance item) {
		if (item == null) {
			return false;
		}
		if (item.isEquipped()) { // 装備中不可
			return false;
		}
		if (item.getEnchantLevel() != 0) { // 強化(or弱化)不可
			return false;
		}
		if (item.getBless() >= 128) { // 封印装備
			return false;
		}
		if (item.getItem().cantBeSold()) {
			return false;
		}

		return true;
	}

	private L1ShopItem getPurchasingItem(final int itemId) {
		for (final L1ShopItem shopItem : _purchasingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	public L1AssessedItem assessItem(final L1ItemInstance item) {
		final L1ShopItem shopItem = getPurchasingItem(item.getItemId());
		if (shopItem == null) {
			return null;
		}
		return new L1AssessedItem(item.getId(), getAssessedPrice(shopItem));
	}

	private int getAssessedPrice(final L1ShopItem item) {
		return (int) ((item.getPrice() * ConfigRate.RATE_SHOP_PURCHASING_PRICE) / item.getPackCount());
	}

	/**
	 * インベントリ内の買取可能アイテムを査定する。
	 * 
	 * @param inv 査定対象のインベントリ
	 * @return 査定された買取可能アイテムのリスト
	 */
	public List<L1AssessedItem> assessItems(final L1PcInventory inv) {
		final List<L1AssessedItem> result = new ArrayList<L1AssessedItem>();
		for (final L1ShopItem item : _purchasingItems) {
			for (final L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
				if (!isPurchaseableItem(targetItem)) {
					continue;
				}

				result.add(new L1AssessedItem(targetItem.getId(), getAssessedPrice(item)));
			}
		}
		return result;
	}

	/**
	 * プレイヤーへアイテムを販売できることを保証する。
	 * 
	 * @return 何らかの理由でアイテムを販売できない場合、false
	 */
	private boolean ensureSell(final L1PcInstance pc, final L1ShopBuyOrderList orderList) {
		final int price = orderList.getTotalPriceTaxIncluded();
		// 檢查販賣物品總價
		if (!RangeInt.includes(price, 0, 2000000000)) {
			// 904 總共販賣價格無法超過 %d金幣。
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		int unit = 40308;
		if (_npcId == 81461) {
			unit = 41496;
		}
		// 檢查背包金幣數量
		if (!pc.getInventory().checkItem(unit, price)) {
			if (unit == 41496) {
				pc.sendPackets(new S_SystemMessage("貝利不足。"));
			} else {
				// 189 \f1金幣不足。
				pc.sendPackets(new S_ServerMessage(189));
			}
			return false;
		}
		// 檢查重量限制
		final int currentWeight = pc.getInventory().getWeight() * 1000;
		if ((currentWeight + orderList.getTotalWeight()) > (pc.getMaxWeight() * 1000)) {
			// 82 此物品太重了，所以你無法攜帶。
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 檢查買入數量
		int totalCount = pc.getInventory().getSize();
//		for (final L1ShopBuyOrder order : orderList.getList()) {
//			final L1Item temp = order.getItem().getItem();
//			if (temp.isStackable()) {
//				if (!pc.getInventory().checkItem(temp.getItemId())) {
//					totalCount += 1;
//				}
//			} else {
//				totalCount += 1;
//			}
//		}
		for (L1ShopBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			int checkClass = order.getItem().getCheckClass();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId()))
					totalCount++;
			} else {
				totalCount++;
			}
			if (checkClass != 0) {
				boolean check = false;
				if (checkClass >= 128) {
					if (pc.isWarrior()) {
						check = true;
					}
					checkClass -= 64;
				}
				if (checkClass >= 64) {
					if (pc.isIllusionist()) {
						check = true;
					}
					checkClass -= 64;
				}
				if (checkClass >= 32) {
					if (pc.isDragonKnight()) {
						check = true;
					}
					checkClass -= 32;
				}
				if (checkClass >= 16) {
					if (pc.isDarkelf()) {
						check = true;
					}
					checkClass -= 16;
				}
				if (checkClass >= 8) {
					if (pc.isElf()) {
						check = true;
					}
					checkClass -= 8;
				}
				if (checkClass >= 4) {
					if (pc.isWizard()) {
						check = true;
					}
					checkClass -= 4;
				}
				if (checkClass >= 2) {
					if (pc.isKnight()) {
						check = true;
					}
					checkClass -= 2;
				}
				if (checkClass >= 1) {
					if (pc.isCrown()) {
						check = true;
					}
					checkClass -= 1;
				}

				if (!check) {
					pc.sendPackets(new S_ServerMessage("你的角色職業無法買入"+order.getItem().getItem().getNameId()+"。"));
					return false;
				}
			}
		}
		
		
		
		
		
		
		
		if (totalCount > 180) {
			// 263 \f1一個角色最多可攜帶180個道具。
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		return true;
	}

	/**
	 * 地域税納税処理 アデン城・ディアド要塞を除く城はアデン城へ国税として10%納税する
	 * 
	 * @param orderList
	 */
	private void payCastleTax(final L1ShopBuyOrderList orderList) {
		final L1TaxCalculator calc = orderList.getTaxCalculator();

		final int price = orderList.getTotalPrice();

		final int castleId = L1CastleLocation.getCastleIdByNpcid(_npcId);
		int castleTax = calc.calcCastleTaxPrice(price);
		int nationalTax = calc.calcNationalTaxPrice(price);
		// アデン城・ディアド城の場合は国税なし
		if ((castleId == L1CastleLocation.ADEN_CASTLE_ID) || (castleId == L1CastleLocation.DIAD_CASTLE_ID)) {
			castleTax += nationalTax;
			nationalTax = 0;
		}

		if ((castleId != 0) && (castleTax > 0)) {
			final L1Castle castle = CastleReading.get().getCastleTable(castleId);

			synchronized (castle) {
				long money = castle.getPublicMoney();
				money = money + castleTax;
				castle.setPublicMoney(money);
				CastleReading.get().updateCastle(castle);
			}

			if (nationalTax > 0) {
				final L1Castle aden = CastleReading.get().getCastleTable(L1CastleLocation.ADEN_CASTLE_ID);
				synchronized (aden) {
					long money = aden.getPublicMoney();
					money = money + nationalTax;
					aden.setPublicMoney(money);
					CastleReading.get().updateCastle(aden);
				}
			}
		}
	}

	/**
	 * ディアド税納税処理 戦争税の10%がディアド要塞の公金となる。
	 * 
	 * @param orderList
	 */
	private void payDiadTax(final L1ShopBuyOrderList orderList) {
		final L1TaxCalculator calc = orderList.getTaxCalculator();

		final int price = orderList.getTotalPrice();

		// ディアド税
		final int diadTax = calc.calcDiadTaxPrice(price);
		if (diadTax <= 0) {
			return;
		}

		final L1Castle castle = CastleReading.get().getCastleTable(L1CastleLocation.DIAD_CASTLE_ID);
		synchronized (castle) {
			long money = castle.getPublicMoney();
			money = money + diadTax;
			castle.setPublicMoney(money);
			CastleReading.get().updateCastle(castle);
		}
	}

	/**
	 * 町税納税処理
	 * 
	 * @param orderList
	 */
	private void payTownTax(final L1ShopBuyOrderList orderList) {
		final int price = orderList.getTotalPrice();
		// 町の売上
		if (!World.get().isProcessingContributionTotal()) {
			final int town_id = L1TownLocation.getTownIdByNpcid(_npcId);
			if ((town_id >= 1) && (town_id <= 10)) {
				TownReading.get().addSalesMoney(town_id, price);
			}
		}
	}

	// XXX 納税処理はこのクラスの責務では無い気がするが、とりあえず
	private void payTax(final L1ShopBuyOrderList orderList) {
		payCastleTax(orderList);
		payTownTax(orderList);
		payDiadTax(orderList);
	}

	/**
	 * 取回買入物品
	 */
	private void sellItems(final L1PcInventory inv, final L1ShopBuyOrderList orderList) {
		if (orderList == null) {
			return;
		}
		final int priceTax = orderList.getTotalPriceTaxIncluded();
		if (priceTax <= 0) {
			return;
		}
		int unit = 40308;
		if (_npcId == 81461) {
			unit = 41496;
		}
		if (!inv.consumeItem(unit, priceTax)) {
			return;
		}
		for (final L1ShopBuyOrder order : orderList.getList()) {
			final int itemId = order.getItem().getItemId();
			final long amount = order.getCount();
			if (amount <= 0) {
				continue;
			}
			final L1ItemInstance item = ItemTable.get().createItem(itemId);
			item.setCount(amount);
			if ((_npcId == 70068) || (_npcId == 70020)) {// 隨機給予強化質的NPC
				item.setIdentified(false);
				final Random random = new Random();
				final int chance = random.nextInt(100) + 1;
				if (chance <= 15) {
					item.setEnchantLevel(-2);

				} else if ((chance >= 16) && (chance <= 30)) {
					item.setEnchantLevel(-1);

				} else if ((chance >= 31) && (chance <= 70)) {
					item.setEnchantLevel(0);

				} else if ((chance >= 71) && (chance <= 87)) {
					item.setEnchantLevel(random.nextInt(2) + 1);

				} else if ((chance >= 88) && (chance <= 97)) {
					item.setEnchantLevel(random.nextInt(3) + 3);

				} else if ((chance >= 98) && (chance <= 99)) {
					item.setEnchantLevel(6);

				} else if (chance == 100) {
					item.setEnchantLevel(7);
				}
			} else if (order.getItem().getEnchantLevel() != 0) {
				item.setEnchantLevel(order.getItem().getEnchantLevel());
			}
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	/**
	 * プレイヤーに、L1ShopBuyOrderListに記載されたアイテムを販売する。
	 * 
	 * @param pc 販売するプレイヤー
	 * @param orderList 販売すべきアイテムが記載されたL1ShopBuyOrderList
	 */
	public void sellItems(final L1PcInstance pc, final L1ShopBuyOrderList orderList) {
		if (orderList.isEmpty()) {
			return;
		}
		if (!ensureSell(pc, orderList)) {
			return;
		}

		this.sellItems(pc.getInventory(), orderList);
		if (_npcId != 81461) {
			payTax(orderList);
		}
	}

	/**
	 * L1ShopSellOrderListに記載されたアイテムを買い取る。
	 * 
	 * @param orderList 買い取るべきアイテムと価格が記載されたL1ShopSellOrderList
	 */
	public void buyItems(final L1ShopSellOrderList orderList) {
		final L1PcInventory inv = orderList.getPc().getInventory();
		int totalPrice = 0;
		for (final L1ShopSellOrder order : orderList.getList()) {
			final int price = order.getItem().getAssessedPrice();
			if (price <= 0) {
				continue;
			}
			final long count = inv.removeItem(order.getItem().getTargetId(), order.getCount());
			totalPrice += price * count;
		}

		totalPrice = RangeInt.ensure(totalPrice, 0, 2000000000);
		if (0 < totalPrice) {
			int unit = 40308;
			if (_npcId == 81461) {
				unit = 41496;
			}
			inv.storeItem(unit, totalPrice);
		}
	}

	public L1ShopBuyOrderList newBuyOrderList() {
		return new L1ShopBuyOrderList(this);
	}

	public L1ShopSellOrderList newSellOrderList(final L1PcInstance pc) {
		return new L1ShopSellOrderList(this, pc);
	}
}
