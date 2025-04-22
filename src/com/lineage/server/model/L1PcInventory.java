package com.lineage.server.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigRate;
import com.lineage.data.event.ProtectorSet;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_AddItem;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_CreateName;
import com.lineage.server.serverpackets.S_DeleteInventoryItem;
import com.lineage.server.serverpackets.S_EquipmentSlot;
import com.lineage.server.serverpackets.S_ItemColor;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Weapon;

/**
 * 人物背包數據
 * 
 * @author dexc
 */
public class L1PcInventory extends L1Inventory {

	private static final Log _log = LogFactory.getLog(L1PcInventory.class);

	private static final long serialVersionUID = 1L;

	private static final int MAX_SIZE = 180;// 最大容量

	private final L1PcInstance _owner; // 背包所有者

	private int _arrowId; // 優先使用的箭ItemID

	private int _stingId; // 優先使用的飛刀ItemID

	public L1PcInventory(final L1PcInstance owner) {
		_owner = owner;
		_arrowId = 0;
		_stingId = 0;
	}

	public L1PcInstance getOwner() {
		return _owner;
	}

	/**
	 * 傳回240階段重量(XXX 7.6準備棄用)
	 * 
	 * @return
	 */
	public int getWeight240() {
		return calcWeight240(getWeight());
	}

	/**
	 * 240階段重量計算(XXX 7.6準備棄用)
	 * 
	 * @param weight
	 * @return
	 */
	public int calcWeight240(final long weight) {
		int weight240 = 0;
		if (ConfigRate.RATE_WEIGHT_LIMIT != 0) {
			final double maxWeight = _owner.getMaxWeight();
			if (weight > maxWeight) {
				weight240 = 240;

			} else {
				double wpTemp = (((weight * 100) / maxWeight) * 240.00) / 100.00;
				final DecimalFormat df = new DecimalFormat("00.##");
				df.format(wpTemp);
				wpTemp = Math.round(wpTemp);
				weight240 = (int) (wpTemp);
			}

		} else { // ウェイトレートが０なら重量常に０
			weight240 = 0;
		}

		return weight240;
	}

	/**
	 * 傳回100階段重量 XXX  7.6c add
	 * 
	 * @return
	 */
	public int getWeight100() {
		return calcWeight100(getWeight());
	}
	
	/**
	 * 100階段重量計算  XXX  7.6c add
	 * 
	 * @param weight
	 * @return
	 */
	public int calcWeight100(int calcWeight) {
		int weight = 0;
		if (ConfigRate.RATE_WEIGHT_LIMIT != 0) {
			// 返回角色最大負重程度
			final int maxWeight = (int) this._owner.getMaxWeight();
			if (calcWeight > maxWeight) {
				weight = 100;
			} else {
				weight = calcWeight * 100 / maxWeight;
			}
		} else { // 如果重量始終為0
			weight = 0;
		}
		return weight;
	}
	
	/**
	 * 增加物品是否成功(背包)
	 * 
	 * @param item 物品
	 * @param count 數量
	 * @return 0:成功 1:超過可攜帶數量 2:超過可攜帶重量 3:超過LONG最大質
	 */
	@Override
	public int checkAddItem(final L1ItemInstance item, final long count) {
		return this.checkAddItem(item, count, true);
	}

	/**
	 * 增加物品是否成功(背包)
	 * 
	 * @param item 物品數據
	 * @param count 數量
	 * @param message 發送訊息
	 * @return 0:成功 1:超過可攜帶數量 2:超過可攜帶重量 3:超過LONG最大質
	 */
	public int checkAddItem(final L1Item item, final long count) {
		if (item == null) {
			return -1;
		}

		boolean isMaxSize = false;// 容量數據異常
		boolean isWeightOver = false;// 重量數據異常

		// 可以堆疊
		if (item.isStackable()) {
			// 身上不具備該物件
			if (!this.checkItem(item.getItemId())) {
				// 超過可攜帶數量
				if ((getSize() + 1) >= MAX_SIZE) {
					isMaxSize = true;
				}
			}

			// 不可以堆疊
		} else {
			// 超過可攜帶數量
			if ((getSize() + 1) >= MAX_SIZE) {
				isMaxSize = true;
			}
		}

		if (isMaxSize) {
			// 263 \f1一個角色最多可攜帶180個道具。
			sendOverMessage(263);
			return SIZE_OVER;
		}

		// 現有重量 + (物品重量 * 數量 / 1000) + 1
		final long weight = getWeight() + ((item.getWeight() * count) / 1000) + 1;

		// 重量數據異常 (重量計算表示小於0)
		if ((weight < 0) || (((item.getWeight() * count) / 1000) < 0)) {
			isWeightOver = true;
		}

		// 超過可攜帶重量
		if ((calcWeight240(weight) >= 240) && !isWeightOver) {
			isWeightOver = true;
		}

		if (isWeightOver) {
			// 82 此物品太重了，所以你無法攜帶。
			sendOverMessage(82);
			return WEIGHT_OVER;
		}
		return OK;
	}

	/**
	 * 增加物品是否成功(背包)
	 * 
	 * @param item 物品(物品已加入世界)
	 * @param count 數量
	 * @param message 發送訊息
	 * @return 0:成功 1:超過可攜帶數量 2:超過可攜帶重量 3:超過LONG最大質
	 */
	public int checkAddItem(final L1ItemInstance item, final long count, final boolean message) {
		if (item == null) {
			return -1;
		}
		if (count <= 0) {
			return -1;
		}

		boolean isMaxSize = false;// 容量數據異常
		boolean isWeightOver = false;// 重量數據異常

		// 可以堆疊
		if (item.isStackable()) {
			// 身上不具備該物件
			if (!this.checkItem(item.getItem().getItemId())) {
				// 超過可攜帶數量
				if ((getSize() + 1) >= MAX_SIZE) {
					isMaxSize = true;
				}
			}

			// 不可以堆疊
		} else {
			// 超過可攜帶數量
			if ((getSize() + 1) >= MAX_SIZE) {
				isMaxSize = true;
			}
		}

		if (isMaxSize) {
			if (message) {
				// 263 \f1一個角色最多可攜帶180個道具。
				sendOverMessage(263);
			}
			return SIZE_OVER;
		}

		// 現有重量 + (物品重量 * 數量 / 1000) + 1
		final long weight = getWeight() + ((item.getItem().getWeight() * count) / 1000) + 1;

		// 重量數據異常 (重量計算表示小於0)
		if ((weight < 0) || (((item.getItem().getWeight() * count) / 1000) < 0)) {
			isWeightOver = true;
		}

		// 超過可攜帶重量
		if ((calcWeight240(weight) >= 240) && !isWeightOver) {
			isWeightOver = true;
		}

		if (isWeightOver) {
			if (message) {
				// 82 此物品太重了，所以你無法攜帶。
				sendOverMessage(82);
			}
			return WEIGHT_OVER;
		}
		return OK;
	}

	public void sendOverMessage(final int message_id) {
		_owner.sendPackets(new S_ServerMessage(message_id));
	}

	/**
	 * 初始化人物背包資料
	 */
	@Override
	public void loadItems() {
		try {
			final CopyOnWriteArrayList<L1ItemInstance> items = CharItemsReading.get()
					.loadItems(_owner.getId());

			if (items != null) {
				_items = items;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * LIST物品資料新增
	 */
	@Override
	public void insertItem(final L1ItemInstance item) {
		if (item.getCount() <= 0) {
			return;
		}

		_owner.sendPackets(new S_AddItem(item));
		if (item.getItem().getWeight() != 0) {
		    // 重量
			//_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, getWeight240()));
			// XXX 7.6 重量程度資訊
			_owner.sendPackets(new S_WeightStatus(_owner.getInventory().getWeight() * 100 / (int)_owner.getMaxWeight(), _owner.getInventory().getWeight(), (int)_owner.getMaxWeight()));
		}

		if (item.get_creater_name() != null) {
			_owner.sendPackets(new S_CreateName(item, _owner));
		}
		if (item.getItemId() == 44070) {
			_owner.updateGameMallMoney();
		}

		// 設置使用者OBJID
		item.set_char_objid(_owner.getId());

		try {
			CharItemsReading.get().storeItem(_owner.getId(), item);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static final int COL_ATTR_ENCHANT_LEVEL = 2048;

	public static final int COL_ATTR_ENCHANT_KIND = 1024;

	public static final int COL_BLESS = 512;

	public static final int COL_REMAINING_TIME = 256;

	public static final int COL_CHARGE_COUNT = 128;

	public static final int COL_ITEMID = 64;

	public static final int COL_DELAY_EFFECT = 32;

	public static final int COL_COUNT = 16;

	public static final int COL_EQUIPPED = 8;

	public static final int COL_ENCHANTLVL = 4;

	public static final int COL_IS_ID = 2;

	public static final int COL_DURABILITY = 1;

	@Override
	public void updateItem(final L1ItemInstance item) {
		this.updateItem(item, COL_COUNT);
		if (item.getItem().isToBeSavedAtOnce()) {
			saveItem(item, COL_COUNT);
		}
	}

	/**
	 * 背包內物件狀態更新
	 * 
	 * @param item 需要更新的物件
	 * @param column 更新種類
	 */
	@Override
	public void updateItem(final L1ItemInstance item, int column) {
		if (column >= COL_ATTR_ENCHANT_LEVEL) { // 属性強化数
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ATTR_ENCHANT_LEVEL;
		}

		if (column >= COL_ATTR_ENCHANT_KIND) { // 属性強化の種類
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ATTR_ENCHANT_KIND;
		}

		if (column >= COL_BLESS) { // 祝福・封印
			_owner.sendPackets(new S_ItemColor(item));
			column -= COL_BLESS;
		}

		if (column >= COL_REMAINING_TIME) { // 殘餘可用時間
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_REMAINING_TIME;
		}

		if (column >= COL_CHARGE_COUNT) { // 殘餘可用次數
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_CHARGE_COUNT;
		}

		if (column >= COL_ITEMID) { // 別のアイテムになる場合(便箋を開封したときなど)
			_owner.sendPackets(new S_ItemStatus(item));
			_owner.sendPackets(new S_ItemColor(item));
			//_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, getWeight240()));
			_owner.sendPackets(new S_WeightStatus(_owner.getInventory().getWeight() * 100 / (int)_owner.getMaxWeight(), _owner.getInventory().getWeight(), (int)_owner.getMaxWeight()));
			column -= COL_ITEMID;
		}

		if (column >= COL_DELAY_EFFECT) { // 効果ディレイ
			column -= COL_DELAY_EFFECT;
		}

		if (column >= COL_COUNT) { // カウント
			_owner.sendPackets(new S_ItemStatus(item));

			final int weight = item.getWeight();
			if (weight != item.getLastWeight()) {
				item.setLastWeight(weight);
				_owner.sendPackets(new S_ItemStatus(item));

			} else {
				_owner.sendPackets(new S_ItemName(item));
			}
			if (item.getItem().getWeight() != 0) {
				// XXX 240段階のウェイトが変化しない場合は送らなくてよい
				//_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, getWeight240()));
				_owner.sendPackets(new S_WeightStatus(_owner.getInventory().getWeight() * 100 / (int)_owner.getMaxWeight(), _owner.getInventory().getWeight(), (int)_owner.getMaxWeight()));
			}
			if (item.getItemId() == 44070) {
				_owner.updateGameMallMoney();
			}
			column -= COL_COUNT;
		}

		if (column >= COL_EQUIPPED) { // 装備状態
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_EQUIPPED;
		}

		if (column >= COL_ENCHANTLVL) { // エンチャント
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ENCHANTLVL;
		}

		if (column >= COL_IS_ID) { // 確認状態
			_owner.sendPackets(new S_ItemStatus(item));
			_owner.sendPackets(new S_ItemColor(item));
			column -= COL_IS_ID;
		}

		if (column >= COL_DURABILITY) { // 耐久性
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_DURABILITY;
		}
	}

	/**
	 * 背包內資料更新(SQL)
	 * 
	 * @param item - 更新対象のアイテム
	 * @param column - 更新するステータスの種類
	 */
	public void saveItem(final L1ItemInstance item, int column) {
		if (column == 0) {
			return;
		}

		try {
			if (column >= COL_ATTR_ENCHANT_LEVEL) { // 属性強化数
				CharItemsReading.get().updateItemAttrEnchantLevel(item);
				column -= COL_ATTR_ENCHANT_LEVEL;
			}

			if (column >= COL_ATTR_ENCHANT_KIND) { // 属性強化の種類
				CharItemsReading.get().updateItemAttrEnchantKind(item);
				column -= COL_ATTR_ENCHANT_KIND;
			}

			if (column >= COL_BLESS) { // 祝福・封印
				CharItemsReading.get().updateItemBless(item);
				column -= COL_BLESS;
			}

			if (column >= COL_REMAINING_TIME) { // 使用可能な残り時間
				CharItemsReading.get().updateItemRemainingTime(item);
				column -= COL_REMAINING_TIME;
			}

			if (column >= COL_CHARGE_COUNT) { // チャージ数
				CharItemsReading.get().updateItemChargeCount(item);
				column -= COL_CHARGE_COUNT;
			}

			if (column >= COL_ITEMID) { // 別のアイテムになる場合(便箋を開封したときなど)
				CharItemsReading.get().updateItemId(item);
				column -= COL_ITEMID;
			}

			if (column >= COL_DELAY_EFFECT) { // 効果ディレイ
				CharItemsReading.get().updateItemDelayEffect(item);
				column -= COL_DELAY_EFFECT;
			}

			if (column >= COL_COUNT) { // カウント
				CharItemsReading.get().updateItemCount(item);
				column -= COL_COUNT;
			}

			if (column >= COL_EQUIPPED) { // 装備状態
				CharItemsReading.get().updateItemEquipped(item);
				column -= COL_EQUIPPED;
			}

			if (column >= COL_ENCHANTLVL) { // エンチャント
				CharItemsReading.get().updateItemEnchantLevel(item);
				column -= COL_ENCHANTLVL;
			}

			if (column >= COL_IS_ID) { // 確認状態
				CharItemsReading.get().updateItemIdentified(item);
				column -= COL_IS_ID;
			}

			if (column >= COL_DURABILITY) { // 耐久性
				CharItemsReading.get().updateItemDurability(item);
				column -= COL_DURABILITY;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * LIST物品資料移除
	 */
	@Override
	public void deleteItem(final L1ItemInstance item) {
		try {
			CharItemsReading.get().deleteItem(_owner.getId(), item);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		if (item.isEquipped()) {
			if (item.getItem().getType2() == 1) {
				if (_owner.isWarrior()) {
					if (_owner.getWeaponWarrior() != null) {
						if (_owner.getWeaponWarrior().equals(item)) {
							setWarriorEquipped(_owner.getWeaponWarrior(), false, false);
							_owner.sendPacketsAll(
									new S_CharVisualUpdate(_owner.getId(), _owner.getCurrentWeapon()));
						} else {
							setEquipped(item, false);
							final L1ItemInstance nextWeapon = _owner.getWeaponWarrior();
							setWarriorEquipped(nextWeapon, false, false);
							setEquipped(nextWeapon, true, false, false);
						}
					} else {
						setEquipped(item, false);
					}
				} else {
					setEquipped(item, false);
				}
			} else {
				setEquipped(item, false);
			}
		}

		if (item != null) {
			_owner.sendPackets(new S_DeleteInventoryItem(item));
			_items.remove(item);
			if (item.getItem().getWeight() != 0) {
				//_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, getWeight240()));
				_owner.sendPackets(new S_WeightStatus(_owner.getInventory().getWeight() * 100 / (int)_owner.getMaxWeight(), _owner.getInventory().getWeight(), (int)_owner.getMaxWeight()));
			}
			if (item.getItemId() == 44070) {
				_owner.updateGameMallMoney();
			}
		}
	}

	/**
	 * アイテムを装着脱着させる（L1ItemInstanceの変更、補正値の設定、character_itemsの更新、パケット送信まで管理）
	 * 
	 * @param item
	 * @param equipped
	 */
	public void setEquipped(final L1ItemInstance item, final boolean equipped) {
		this.setEquipped(item, equipped, false, false);
	}

	public void setEquipped(final L1ItemInstance item, final boolean equipped, final boolean loaded,
			final boolean changeWeapon) {
		if (item.isEquipped() != equipped) { // 設定値と違う場合だけ処理
			final L1Item temp = item.getItem();
			if (equipped) { // 装着
				item.setEquipped(true);
				// 裝備穿著效果判斷
				_owner.getEquipSlot().set(item);
			} else { // 脱着
				if (!loaded) {
					// インビジビリティクローク バルログブラッディクローク装備中でインビジ状態の場合はインビジ状態の解除
					if ((temp.getItemId() == 20077) || (temp.getItemId() == 20062)
							|| (temp.getItemId() == 120077)) {
						if (_owner.isInvisble()) {
							_owner.delInvis();
							return;
						}
					}
				}
				item.setEquipped(false);
				// 裝備脫除效果判斷
				_owner.getEquipSlot().remove(item);
			}

			if (!loaded) { // 最初の読込時はＤＢパケット関連の処理はしない
				// System.out.println("物品裝備狀態");
				// XXX:意味のないセッター
				_owner.setCurrentHp(_owner.getCurrentHp());
				_owner.setCurrentMp(_owner.getCurrentMp());
				this.updateItem(item, COL_EQUIPPED);
				_owner.sendPackets(new S_OwnCharStatus(_owner));
				// 武器の場合はビジュアル更新。ただし、武器の持ち替えで武器を脱着する時は更新しない
				if ((temp.getType2() == 1) && (changeWeapon == false)) {
					_owner.sendPacketsAll(new S_CharVisualUpdate(_owner));
				}
			}
			CheckType(_owner, item, equipped);
		}

		if (item.getItem() instanceof L1Weapon) {
			boolean check = false;
			int range = 1;
			int type = 1;
			final L1ItemInstance weapon = _owner.getWeapon();
			if (weapon == null) {
				_owner.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 0, check));
			} else {
				if (weapon.getItem().getType() == 4) {
					range = 15;
				} else if ((weapon.getItem().getType() == 10) || (weapon.getItem().getType() == 13)) {
					range = 14;
				} else if ((weapon.getItem().getType() == 5) || (weapon.getItem().getType() == 14)
						|| (weapon.getItem().getType() == 18)) {
					range = 1;
					final int polyId = _owner.getTempCharGfx();
					if ((polyId == 11330) || (polyId == 11344) || (polyId == 11351) || (polyId == 11368)
							|| (polyId == 12240) || (polyId == 12237) || (polyId == 11447)
							|| (polyId == 11408) || (polyId == 11409) || (polyId == 11410)
							|| (polyId == 11411) || (polyId == 11418) || (polyId == 11419)
							|| (polyId == 12613) || (polyId == 12614)) {
						range = 2;
					} else if (!_owner.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
						range = 2;
					}
				}

				if (_owner.isKnight()) {
					if (weapon.getItem().getType() == 3) {
						check = true;
					}
				} else if (_owner.isElf()) {
					if (_owner.hasSkillEffect(L1SkillId.DANCING_BLAZE)) {
						check = true;
					}
					if (((weapon.getItem().getType() == 4) || (weapon.getItem().getType() == 13))
							&& (weapon.getItem().getType1() == 20)) {
						type = 3;
						check = true;
					}
				} else if (_owner.isDragonKnight()) {
					check = true;
					if ((weapon.getItem().getType() == 14) || (weapon.getItem().getType() == 18)) {
						type = 10;
					}
				}

				if ((weapon.getItem().getType1() != 20) && (weapon.getItem().getType1() != 62)) {
					_owner.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, type, check));
				} else {
					_owner.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 3, check));
				}
				_owner.setRange(range);
			}
		}
	}

	/**
	 * 取回身上指定道具的數量 (道具編號) by terry0412
	 * 
	 * @param id 道具編號
	 * @return 道具數量
	 */
	public final int getEquippedCountByItemId(final int id) {
		int equippedCount = 0;
		// 背包道具查找
		for (final L1ItemInstance item : _items) {
			// 物品在使用中並且編號相同
			if (item.isEquipped() && (item.getItem().getItemId() == id)) {
				equippedCount++;
			}
		}
		return equippedCount;
	}

	/**
	 * 取回身上指定道具的數量 (活動戒指或收費戒指) by terry0412
	 * 
	 * @return 道具數量
	 */
	public final int getEquippedCountByActivityItem() {
		int equippedCount = 0;
		// 背包道具查找
		for (final L1ItemInstance item : _items) {
			// 物品在使用中並且是活動戒指或收費戒指
			if (item.isEquipped() && item.getItem().isActivity()) {
				equippedCount++;
			}
		}
		return equippedCount;
	}

	/**
	 * 裝備具有指定編號道具
	 * 
	 * @param id 物品編號
	 * @return 傳回該物品
	 */
	public L1ItemInstance checkEquippedItem(final int id) {
		try {
			for (final L1ItemInstance item : _items) {
				// 物品編號相同 並且在使用中
				if ((item.getItem().getItemId() == id) && item.isEquipped()) {
					return item;
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return null;
	}

	/**
	 * 裝備具有指定編號道具
	 * 
	 * @param id 物品編號
	 * @return true:使用中 false:非使用中
	 */
	public boolean checkEquipped(final int id) {
		try {
			for (final L1ItemInstance item : _items) {
				// 物品編號相同 並且在使用中
				if ((item.getItem().getItemId() == id) && item.isEquipped()) {
					return true;
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return false;
	}

	/**
	 * 裝備具有指定編號月卡[vip]
	 * 
	 * @param itemid 物品編號
	 * @return true:使用中 false:非使用中
	 */
	public boolean checkCardEquipped(final int itemid) {
		try {
			for (final L1ItemInstance item : _items) {
				// 物品編號相同 並且在使用中(具備使用期限)
				if ((item.getItem().getItemId() == itemid) && (item.get_card_use() == 1)) {
					return true;
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return false;
	}

	/**
	 * 裝備具有指定名稱道具
	 * 
	 * @param nameid 物品名稱
	 * @return true:使用中 false:非使用中
	 */
	public boolean checkEquipped(final String nameid) {
		try {
			for (final L1ItemInstance item : _items) {
				// 物品名稱相同 並且在使用中
				if ((item.getName().equals(nameid)) && item.isEquipped()) {
					return true;
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return false;
	}

	/**
	 * 裝備具有指定編號道具群(套裝)
	 * 
	 * @param ids
	 * @return
	 */
	public boolean checkEquipped(final int[] ids) {
		try {
			for (final int id : ids) {
				if (!this.checkEquipped(id)) {
					return false;
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return true;
	}

	/**
	 * 裝備具有指定名稱道具群(套裝)
	 * 
	 * @param names
	 * @return
	 */
	public boolean checkEquipped(final String[] names) {
		try {
			for (final String name : names) {
				if (!this.checkEquipped(name)) {
					return false;
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return true;
	}

	/**
	 * 裝備中指定類型物品數量
	 * 
	 * @param type2 類型
	 * @param type 物品分類
	 * @return 裝備中指定類型物品數量
	 */
	public int getTypeEquipped(final int type2, final int type) {
		int equipeCount = 0;// 裝備中指定位置物品數量
		try {
			for (final L1ItemInstance item : _items) {
				// 物品類型相等 物品分類相等 並且在使用中
				if ((item.getItem().getType2() == type2) && (item.getItem().getType() == type)
						&& item.isEquipped()) {
					equipeCount++;// 使用數量+1
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return equipeCount;
	}

	/**
	 * 裝備中指定類型物品
	 * 
	 * @param type2 類型
	 * @param type 物品分類
	 * @return 裝備中指定類型物品
	 */
	public L1ItemInstance getItemEquipped(final int type2, final int type) {
		L1ItemInstance equipeitem = null;
		try {
			for (final L1ItemInstance item : _items) {
				// 物品類型相等 物品分類相等 並且在使用中
				if ((item.getItem().getType2() == type2) && (item.getItem().getType() == type)
						&& item.isEquipped()) {
					equipeitem = item;
					break;
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return equipeitem;
	}

	/**
	 * 設置 顯示/消除 套裝效果 XXX
	 * 
	 * @param armorSet 套裝
	 * @param isMode 是否顯示 額外屬性
	 */
//	public void setPartMode(final ArmorSet armorSet, final boolean isMode) {
//		final int tgItemId = armorSet.get_ids()[0];// 取回套裝第一樣物品ID
//		final L1ItemInstance[] tgItems = findItemsId(tgItemId);
//		for (final L1ItemInstance tgItem : tgItems) {
//			tgItem.setIsMatch(isMode);
//			_owner.sendPackets(new S_ItemStatus(tgItem));
//		}
//	}
	public void setPartMode(ArmorSet armorSet, boolean isMode) {
		int[] tgItemId = armorSet.get_ids();
		int len = tgItemId.length;

		for (int i = 0; i < len; i++) {
			L1ItemInstance[] tgItems = findItemsId(tgItemId[i]);
			for (L1ItemInstance tgItem : tgItems) {
				tgItem.setIsMatch(isMode);
				this._owner.sendPackets(new S_ItemStatus(tgItem));
			}
		}
	}

	/**
	 * 裝備中戒指陣列 (修正戒指擴充欄位 by terry0412)
	 * 
	 * @return
	 */
	public final L1ItemInstance[] getRingEquipped() {
		final List<L1ItemInstance> equipeItem = new ArrayList<L1ItemInstance>();
		// 背包道具查找
		for (final L1ItemInstance item : _items) {
			// 物品在使用中並且為戒指
			if (item.isEquipped() && (item.getItem().getUseType() == 23)) { // 戒指
				equipeItem.add(item);
			}
		}
		return equipeItem.toArray(new L1ItemInstance[equipeItem.size()]);
	}

	// 変身時に装備できない装備を外す
	public void takeoffEquip(final int polyid) {
		takeoffWeapon(polyid);
		takeoffArmor(polyid);
	}

	// 変身時に装備できない武器を外す
	private void takeoffWeapon(final int polyid) {
		if (_owner.getWeapon() == null) { // 素手
			return;
		}

		boolean takeoff = false;
		final int weapon_type = _owner.getWeapon().getItem().getType();
		// 装備出来ない武器を装備してるか？
		takeoff = !L1PolyMorph.isEquipableWeapon(polyid, weapon_type);

		if (takeoff) {
			this.setEquipped(_owner.getWeapon(), false, false, false);

			if (_owner.isWarrior() && (_owner.getWeaponWarrior() != null)) {
				setWarriorEquipped(_owner.getWeaponWarrior(), false, false);
			}
		}
	}

	// 変身時に装備できない防具を外す
	private final void takeoffArmor(final int polyid) {
		L1ItemInstance armor = null;

		// ヘルムからガーダーまでチェックする
		for (int type = 0; type <= 13; type++) {
			// 装備していて、装備不可の場合は外す
			if ((getTypeEquipped(2, type) != 0) && !L1PolyMorph.isEquipableArmor(polyid, type)) {
				if (type == 9) { // リングの場合は、両手分外す
					for (int i = 0; i < 4; i++) {
						armor = getItemEquipped(2, type);
						if (armor != null) {
							this.setEquipped(armor, false, false, false);
						}
					}

				} else {
					armor = getItemEquipped(2, type);
					if (armor != null) {
						this.setEquipped(armor, false, false, false);
					}
				}
			}
		}
	}

	/**
	 * 使用的箭
	 * 
	 * @return
	 */
	public L1ItemInstance getArrow() {
		return getBullet(-2);
	}

	/**
	 * 使用的飛刀
	 * 
	 * @return
	 */
	public L1ItemInstance getSting() {
		return getBullet(-3);
	}

	/**
	 * @param useType
	 * @return
	 */
	private L1ItemInstance getBullet(final int useType) {
		L1ItemInstance bullet;
		int priorityId = 0;
		if (useType == -2) {
			if (_owner.getWeapon().getItemId() == 192) {// 水精靈之弓
				bullet = this.findItemId(40742);// 古代之箭
				if (bullet == null) {
					// 329：\f1沒有具有 %0%o。
					_owner.sendPackets(new S_ServerMessage(329, "$2377"));
				}
				return bullet;

			} else {
				priorityId = _arrowId; // 箭
			}
		}

		if (useType == -3) {
			priorityId = _stingId; // 飛刀
		}

		if (priorityId > 0) {// 優先する弾があるか
			bullet = this.findItemId(priorityId);
			if (bullet != null) {
				return bullet;

			} else {// なくなっていた場合は優先を消す
				if (useType == -2) {
					_arrowId = 0;
				}
				if (useType == -3) {
					_stingId = 0;
				}
			}
		}

		for (final Object itemObject : _items) {// 弾を探す
			bullet = (L1ItemInstance) itemObject;
			if (bullet.getItem().getUseType() == useType) {
				if (useType == -2) {// 箭
					_arrowId = bullet.getItem().getItemId(); // 優先にしておく
				}

				if (useType == -3) {
					_stingId = bullet.getItem().getItemId(); // 優先にしておく
				}
				return bullet;
			}
		}
		return null;
	}

	// 優先するアローの設定
	public void setArrow(final int id) {
		_arrowId = id;
	}

	// 優先するスティングの設定
	public void setSting(final int id) {
		_stingId = id;
	}

	/**
	 * 装備 hp自然回復補正
	 * 
	 * @return
	 */
	public int hpRegenPerTick() {
		int hpr = 0;
		try {
			for (final Object itemObject : _items) {
				final L1ItemInstance item = (L1ItemInstance) itemObject;
				if (item.isEquipped()) {
					hpr += item.getItem().get_addhpr();
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return hpr;
	}

	/**
	 * 装備 mp自然回復補正
	 * 
	 * @return
	 */
	public int mpRegenPerTick() {
		int mpr = 0;
		try {
			for (final Object itemObject : _items) {
				final L1ItemInstance item = (L1ItemInstance) itemObject;
				if (item.isEquipped()) {
					mpr += item.getItem().get_addmpr();
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return mpr;
	}

	/**
	 * 傳回隨機掉落物品
	 * 
	 * @return
	 */
	public L1ItemInstance caoPenalty() {
		try {
			final Random random = new Random();
			final int rnd = random.nextInt(_items.size());
			final L1ItemInstance penaltyItem = _items.get(rnd);
			// 貨幣
			if (penaltyItem.getItem().getItemId() == 44070) {
				return null;
			}

			// 金幣
			if (penaltyItem.getItem().getItemId() == L1ItemId.ADENA) {
				return null;
			}

			// 不可刪除物品
			if (penaltyItem.getItem().isCantDelete()) {
				return null;
			}

			// 不可轉移物品
			if (!penaltyItem.getItem().isTradable()) {
				return null;
			}

			// 具有時間限制
			if (penaltyItem.get_time() != null) {
				return null;
			}

			// 寵物項圈
			final Object[] petlist = _owner.getPetList().values().toArray();
			for (final Object petObject : petlist) {
				if (petObject instanceof L1PetInstance) {
					final L1PetInstance pet = (L1PetInstance) petObject;
					if (penaltyItem.getId() == pet.getItemObjId()) {
						return null;
					}
				}
			}

			// 取回娃娃
			if (_owner.getDoll(penaltyItem.getId()) != null) {
				return null;
			}

			// 超級娃娃
			if (_owner.get_power_doll() != null) {
				if (penaltyItem.getId() == _owner.get_power_doll().getItemObjId()) {
					return null;
				}
			}

			// 解除使用狀態
			this.setEquipped(penaltyItem, false);
			return penaltyItem;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 移除全部指定編號道具
	 * 
	 * @param itemId
	 */
	public void delQuestItem(final int itemId) {
		try {
			final Random random = new Random();
			for (final L1ItemInstance item : _items) {
				if (item.getItemId() == itemId) {
					removeItem(item);
					// 445：\f1%0%s 漸漸變熱之後燃燒成灰燼。
					// 446：\f1%0%s 凍結之後破碎。
					// 447：\f1%0%s 經過狂烈的震動之後變成土。
					// 448：\f1%0%s 漸漸腐蝕之後被風吹散。
					_owner.sendPackets(new S_ServerMessage(random.nextInt(4) + 445, item.getName()));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public int checkAddItem(final int itemId, final int count) {
		return checkAddItem(ItemTable.get().createItem(itemId, false), count, true);
	}

	public void setWarriorEquipped(final L1ItemInstance item, final boolean equipped, final boolean isview) {
		if (equipped) {
			item.setEquipped(true);
			_owner.setWeaponWarrior(item);
			_owner.setCurrentWeapon(88);
		} else {
			item.setEquipped(false);
			_owner.setWeaponWarrior(null);
			if (_owner.getWeapon() != null) {
				_owner.setCurrentWeapon(_owner.getWeapon().getItem().getType1());
			}
		}

		updateItem(item, COL_EQUIPPED);

		_owner.sendPackets(new S_EquipmentSlot(item.getId(), 8, isview));
	}

	public int getGarderEquipped(final int type2, final int type, final int gd) {
		int equipeCount = 0;
		L1ItemInstance item = null;
		for (final Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if ((item.getItem().getType2() == type2) && (item.getItem().getType() == type)
					&& (item.getItem().getUseType() != gd) && item.isEquipped()) {
				equipeCount++;
			}
		}
		return equipeCount;
	}

	public void viewItem() {
		final List<L1ItemInstance> itemlist = new CopyOnWriteArrayList<L1ItemInstance>();
		for (final L1ItemInstance item : _items) {
			if (item.isEquipped()) {
				item.setEquipped(false);

				if ((item.getItem().getType2() == 0) && (item.getItem().getType() == 2)) { // 照明道具
					item.setRemainingTime(item.getItem().getLightFuel());
				}
				if (item.getItemId() == ProtectorSet.ITEM_ID) {
					_owner.giveProtector(true);
				}
				itemlist.add(item);
			}
		}
		boolean weaponck = false;
		for (final L1ItemInstance item : itemlist) {
			if (!_owner.isWarrior()) {
				setEquipped(item, true, true, false);
			} else {
				if (item.getItem().getType2() == 1) {
					if (weaponck) {
						setWarriorEquipped(item, true, true);
					} else {
						setEquipped(item, true, true, false);
						weaponck = true;
					}
				} else {
					setEquipped(item, true, true, false);
				}
			}
		}
		itemlist.clear();
	}

	private final HashMap<Integer, L1ItemInstance> _RingList = new HashMap<Integer, L1ItemInstance>();

	private final HashMap<Integer, L1ItemInstance> _EarringList = new HashMap<Integer, L1ItemInstance>();

	private final HashMap<Integer, L1ItemInstance> _RuneList = new HashMap<Integer, L1ItemInstance>();

	public void CheckType(final L1PcInstance pc, final L1ItemInstance item, final boolean isEq) {
		final L1Item temp = item.getItem();
		int type = 0;
		if (temp.getType2() == 1) {
			type = 9;
		} else if (temp.getType2() == 2) {
			if ((temp.getType() >= 1) && (temp.getType() <= 4)) {
				type = temp.getType();
			} else if ((temp.getType() == 5) || (temp.getType() == 6)) {
				type = temp.getType() == 5 ? 7 : 6;
			} else if ((temp.getType() == 7) || (temp.getType() == 13)) {
				type = 8;
			} else if ((temp.getType() == 8) || (temp.getType() == 10)) {
				type = temp.getType() == 8 ? 11 : 12;
			} else if (temp.getType() == 17) {
				type = 15;
			} else if (temp.getType() == 18) {
				type = 16;
			} else if (temp.getType() == 19) {
				type = 18;
			} else if (temp.getType() == 23) {
				type = 27; // 49494949

			}else if (temp.getType() == 16) {
				type = 5;
			} else if (temp.getType() == 9) {
				if (!isEq) {
					for (int i = 0; i < 4; i++) {
						if ((_RingList.get(i) != null) && (_RingList.get(i) == item)) {
							_RingList.remove(i);
							type = 19 + i;
							break;
						}
					}
				} else {
					for (int i = 0; i < 4; i++) {
						if (_RingList.get(i) == null) {
							_RingList.put(i, item);
							type = 19 + i;
							break;
						}
					}
					for (int i = 0; i < 4; i++) {
						if ((_RingList.get(i) != null) && (_RingList.get(i) == item)) {
							type = 19 + i;
							break;
						}
					}
				}
			} else if (temp.getType() == 12) {
				if (isEq) {
					for (int i = 0; i < 2; i++) {
						if (_EarringList.get(i) == null) {
							_EarringList.put(i, item);
							type = 13;
							if (i == 1) {
								type += 13;
							}
							break;
						}
					}
				} else {
					for (int i = 0; i < 2; i++) {
						if ((_EarringList.get(i) != null) && (_EarringList.get(i) == item)) {
							_EarringList.remove(i);
							type = 13;
							if (i == 1) {
								type += 13;
							}
							break;
						}
					}
				}
			} else if (temp.getType() == 14) {
				if (isEq) {
					for (int i = 0; i < 2; i++) {
						if (_RuneList.get(i) == null) {
							_RuneList.put(i, item);
							type = 23;
							if (i == 1) {
								type += 4;
							}
							break;
						}
					}
				} else {
					for (int i = 0; i < 2; i++) {
						if (_RuneList.get(i) != null && _RuneList.get(i) == item) {
							_RuneList.remove(i);
							type = 23;
							if (i == 1) {
								type += 4;
							}
							break;
						}
					}
				}
			}
		}
		pc.sendPackets(new S_EquipmentSlot(item.getId(), type, isEq));
	}

	/**
	 * 7.0 login
	 */
	public void equippedLoad() {
		int count = 0;
		for (final L1ItemInstance item : _items) {
			if (item.isEquipped()) {
				count++;
			}
		}
		_owner.sendPackets(new S_EquipmentSlot(_owner, count));
	}
	
	/** 檢查已裝備的相同道具數量 **/
	public int getTypeAndItemIdEquipped(int type2, int type, int itemId) {
		int equipeCount = 0;
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.getItemId() == itemId
					&& item.isEquipped()) {
				equipeCount++;
			}
		}
		return equipeCount;
	}
}
