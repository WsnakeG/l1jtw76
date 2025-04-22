package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.BLESSED_ARMOR;

import static com.lineage.server.model.skill.L1SkillId.BLESS_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.HOLY_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.SHADOW_FANG;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;

import com.lineage.server.datatables.ExtraAttrWeaponTable;
import com.lineage.server.datatables.ExtraMagicStoneTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.L1EquipmentTimer;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_PacketBoxUpdateGfxid;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1AttrWeapon;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPowerHole_name;
import com.lineage.server.templates.L1ItemPower_name;
import com.lineage.server.templates.L1MagicStone;
import com.lineage.server.templates.L1MagicWeapon;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.utils.RangeLong;
import com.lineage.server.world.World;

// Npc_BosskeyNpc_Bosskey

/**
 * 物品類控制項
 * 
 * @author dexc
 */
public class L1ItemInstance extends L1Object {

	// private static final Log _log = LogFactory.getLog(L1ItemInstance.class);

	private static final long serialVersionUID = 1L;

	private long _count;

	private int _itemId;

	private boolean _isEquipped;

	private boolean _isEquippedTemp;

	private int _enchantLevel; // 物品強化值

	private boolean _isIdentified;

	private int _durability;

	private int _chargeCount;

	private int _remainingTime;

	private int _lastWeight;

	private boolean _isRunning; // 裝備強化時間軸

	private int _bless;

	private int _attrEnchantKind;

	private int _attrEnchantLevel;

	private String _gamno;

	private L1Item _item;

	private Timestamp _lastUsed;

	private final LastStatus _lastStatus = new LastStatus();

	private final HashMap<Integer, Integer> _skilllist = new HashMap<Integer, Integer>(); // 2014

	// private L1PcInstance _pc;

	// private EnchantTimer _timer;

	public L1ItemInstance() {
		_count = 1; // 2014
		_enchantLevel = 0;
	}

	public L1ItemInstance(final L1Item item, final long count) {
		this(); // 2014
		setItem(item);
		setCount(count);
	}

	/**
	 * 傳回鑑定狀態
	 * 
	 * @return 確認済みならtrue、未確認ならfalse。
	 */
	public boolean isIdentified() {
		return _isIdentified;
	}

	/**
	 * 設置鑑定狀態
	 * 
	 * @param identified 確認済みならtrue、未確認ならfalse。
	 */
	public void setIdentified(final boolean identified) {
		_isIdentified = identified;
	}

	/**
	 * 傳回NAMEID
	 * 
	 * @return
	 */
	public String getName() {
		// _item.getName();
		return _item.getNameId();
	}

	/**
	 * 傳回數量
	 * 
	 * @return
	 */
	public long getCount() {
		return _count;
	}

	/**
	 * 數量設置
	 * 
	 * @param count
	 */

	public void setCount(long count) {
		_count = count;
	}

	/**
	 * 場次代號
	 * 
	 * @return
	 */
	public String getGamNo() {
		return _gamno;
	}

	/**
	 * 設定場次代號
	 * 
	 * @param count
	 */
	public void setGamNo(final String gamno) {
		_gamno = gamno;
	}

	/**
	 * 物品裝備狀態
	 * 
	 * @return 已裝備true、未裝備false。
	 */
	public boolean isEquipped() {
		return _isEquipped;
	}

	/**
	 * 設置物品裝備狀態
	 * 
	 * @param equipped 已裝備true、未裝備false。
	 */
	public void setEquipped(final boolean equipped) {
		_isEquipped = equipped;
	}

	public L1Item getItem() {
		return _item;
	}

	public void setItem(final L1Item item) {
		_item = item;
		_itemId = item.getItemId();
	}

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(final int itemId) {
		_itemId = itemId;
	}

	/**
	 * 物品是否可以堆疊
	 * 
	 * @return true:可以 false:不可以
	 */
	public boolean isStackable() {
		return _item.isStackable();
	}

	@Override
	public void onAction(final L1PcInstance player) {
	}

	/**
	 * 物品強化質
	 * 
	 * @return
	 */
	public int getEnchantLevel() {
		return _enchantLevel;
	}

	/**
	 * 設定物品強化質
	 * 
	 * @param enchantLevel
	 */
	public void setEnchantLevel(final int enchantLevel) {
		_enchantLevel = enchantLevel;
	}

	public int get_gfxid() {
		final int gfxid = _item.getGfxId();
		return gfxid;
	}

	public int get_durability() {
		return _durability;
	}

	/**
	 * 傳回可用次數
	 * 
	 * @return
	 */
	public int getChargeCount() {
		return _chargeCount;
	}

	/**
	 * 設置可用次數
	 * 
	 * @param i
	 */
	public void setChargeCount(final int i) {
		_chargeCount = i;
	}

	/**
	 * 剩餘時間
	 * 
	 * @return
	 */
	public int getRemainingTime() {
		return _remainingTime;
	}

	/**
	 * 剩餘時間
	 * 
	 * @param i
	 */
	public void setRemainingTime(final int i) {
		_remainingTime = i;
	}

	public void setLastUsed(final Timestamp t) {
		_lastUsed = t;
	}

	public Timestamp getLastUsed() {
		return _lastUsed;
	}

	public int getLastWeight() {
		return _lastWeight;
	}

	public void setLastWeight(final int weight) {
		_lastWeight = weight;
	}

	/**
	 * 祝福 0/128 一般 1/129 詛咒 2/130 ?? 3/131
	 * 
	 * @param i
	 */
	public void setBless(final int i) {
		_bless = i;
	}

	/**
	 * 祝福 0/128 一般 1/129 詛咒 2/130 ?? 3/131
	 * 
	 * @return
	 */
	public int getBless() {
		return _bless;
	}

	/**
	 * 屬性強化類型
	 * 
	 * @param i
	 */
	public void setAttrEnchantKind(final int i) {
		_attrEnchantKind = i;
	}

	/**
	 * 屬性強化類型
	 * 
	 * @return
	 */
	public int getAttrEnchantKind() {
		return _attrEnchantKind;
	}

	/**
	 * 屬性強化質
	 * 
	 * @param i
	 */
	public void setAttrEnchantLevel(final int i) {
		_attrEnchantLevel = i;
	}

	/**
	 * 屬性強化質
	 * 
	 * @return
	 */
	public int getAttrEnchantLevel() {
		return _attrEnchantLevel;
	}

	/*
	 * 耐久性、0~127まで -の値は許可しない。
	 */
	public void set_durability(int i) {
		if (i < 0) {
			i = 0;
		}

		if (i > 127) {
			i = 127;
		}
		_durability = i;
	}

	public int getWeight() {
		if (getItem().getWeight() == 0) {
			return 0;

		} else {
			return (int) Math.max((getCount() * getItem().getWeight()) / 1000, 1);
		}
	}

	/**
	 * 前回DBへ保存した際のアイテムのステータスを格納するクラス
	 */
	public class LastStatus {

		public long count;

		public int itemId;

		public boolean isEquipped = false;

		public int enchantLevel;

		public boolean isIdentified = true;

		public int durability;

		public int chargeCount;

		public int remainingTime;

		public Timestamp lastUsed = null;

		public int bless;

		public int attrEnchantKind;

		public int attrEnchantLevel;

		// private String gamno;

		public void updateAll() {
			count = getCount();
			itemId = getItemId();
			isEquipped = isEquipped();
			isIdentified = isIdentified();
			enchantLevel = getEnchantLevel();
			durability = get_durability();
			chargeCount = getChargeCount();
			remainingTime = getRemainingTime();
			lastUsed = getLastUsed();
			bless = getBless();
			attrEnchantKind = getAttrEnchantKind();
			attrEnchantLevel = getAttrEnchantLevel();
		}

		public void updateCount() {
			count = getCount();
		}

		public void updateItemId() {
			itemId = getItemId();
		}

		public void updateEquipped() {
			isEquipped = isEquipped();
		}

		public void updateIdentified() {
			isIdentified = isIdentified();
		}

		public void updateEnchantLevel() {
			enchantLevel = getEnchantLevel();
		}

		public void updateDuraility() {
			durability = get_durability();
		}

		public void updateChargeCount() {
			chargeCount = getChargeCount();
		}

		public void updateRemainingTime() {
			remainingTime = getRemainingTime();
		}

		public void updateLastUsed() {
			lastUsed = getLastUsed();
		}

		public void updateBless() {
			bless = getBless();
		}

		public void updateAttrEnchantKind() {
			attrEnchantKind = getAttrEnchantKind();
		}

		public void updateAttrEnchantLevel() {
			attrEnchantLevel = getAttrEnchantLevel();
		}

		/*
		 * public void updateGamno() { this.gamno =
		 * L1ItemInstance.this.getGamNo(); }
		 */
	}

	public LastStatus getLastStatus() {
		return _lastStatus;
	}

	/**
	 * 前回DBに保存した時から変化しているカラムをビット集合として返す。
	 */
	public int getRecordingColumns() {
		int column = 0;

		if (getCount() != _lastStatus.count) {
			column += L1PcInventory.COL_COUNT;
		}
		if (getItemId() != _lastStatus.itemId) {
			column += L1PcInventory.COL_ITEMID;
		}
		if (isEquipped() != _lastStatus.isEquipped) {
			column += L1PcInventory.COL_EQUIPPED;
		}
		if (getEnchantLevel() != _lastStatus.enchantLevel) {
			column += L1PcInventory.COL_ENCHANTLVL;
		}
		if (get_durability() != _lastStatus.durability) {
			column += L1PcInventory.COL_DURABILITY;
		}
		if (getChargeCount() != _lastStatus.chargeCount) {
			column += L1PcInventory.COL_CHARGE_COUNT;
		}
		if (getLastUsed() != _lastStatus.lastUsed) {
			column += L1PcInventory.COL_DELAY_EFFECT;
		}
		if (isIdentified() != _lastStatus.isIdentified) {
			column += L1PcInventory.COL_IS_ID;
		}
		if (getRemainingTime() != _lastStatus.remainingTime) {
			column += L1PcInventory.COL_REMAINING_TIME;
		}
		if (getBless() != _lastStatus.bless) {
			column += L1PcInventory.COL_BLESS;
		}
		if (getAttrEnchantKind() != _lastStatus.attrEnchantKind) {
			column += L1PcInventory.COL_ATTR_ENCHANT_KIND;
		}
		if (getAttrEnchantLevel() != _lastStatus.attrEnchantLevel) {
			column += L1PcInventory.COL_ATTR_ENCHANT_LEVEL;
		}

		return column;
	}

	/**
	 * 背包/倉庫 物件完整名稱取回<br>
	 */
	public String getNonumberViewName() {
		final StringBuilder name = new StringBuilder(getNonumberName());

		if (_time != null) {
			// pandora
			if (_pandora_type > 0) {
				final SimpleDateFormat sdf = new SimpleDateFormat("M-d H:m", Locale.TAIWAN);
				String fm = "1-1 8:0";
				fm = sdf.format(_time);
				name.append(" [" + fm + "]");
			} else {
				final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
				switch (_card_use) {
				case 0:
					name.append("[" + sdf.format(_time) + "]"); // 使用期限
					break;
				case 1:
					name.append("[" + sdf.format(_time) + "]"); // 使用期限
					break;
				case 2:
					name.append("[" + sdf.format(_time) + "]"); // 使用期限
					break;
				}
			}
		}

		/*
		 * if (_power_name != null && _power_name.get_power_name() != null &&
		 * !_power_name.get_power_name().isEmpty()) { name.append("[" +
		 * _power_name.get_power_name() + "]"); }
		 */

		// 凹槽系統
		if (_power_name_hole != null) {
			name.append(" \\fD ");
			// 剩餘孔數判斷值
			int remain_hole_count = _power_name_hole.get_hole_count();
			for (int i = 0; i < remain_hole_count; i++) {
				switch (i) {
				case 0: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_1());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(magicStone.getName());
					} else {
						name.append(" ◎");
					}
					break;
				}
				case 1: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_2());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				case 2: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_3());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				case 3: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_4());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				case 4: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_5());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				}
			}
		}

		switch (_item.getUseType()) {
		default:
			if (isEquippedTemp()) {
				// 防具/武器/道具 類型物件送出使用中物件上方會出現E
				name.append(" ($117)"); // 使用中
			}
			break;

		case -12: // 寵物用具
			if (isEquipped()) {
				// name.append(" ($117)"); // 使用中(Worn)
			}
			break;

		case -4: // 項圈
			final L1Pet pet = PetReading.get().getTemplate(getId());
			if (pet != null) {
				if (pet != null) {
					name.append("[Lv." + pet.get_level() + " " + pet.get_name() + "]");
				}
			}
			break;

		case 1: // 武器
			if (isEquipped()) {
				// 武器 類型物件送出使用中物件上方會出現E
				name.append(" ($9)"); // 揮舞(Armed)
			}
			break;

		case 10: // 照明道具
			if (isNowLighting()) {
				name.append(" ($10)");// 打開
			}
			switch (_item.getItemId()) {
			case 40001: // 燈
			case 40002: // 燈籠
				if (getRemainingTime() <= 0) {
					name.append(" ($11)");// 無油
				}
				break;
			}
			break;

		case 2: // 盔甲
		case 16:
		case 18: // T恤
		case 19: // 斗篷
		case 20: // 手套
		case 21: // 靴
		case 22: // 頭盔
		case 23: // 戒指
		case 24: // 項鍊
		case 25: // 盾牌
		case 37: // 腰帶
		case 40: // 耳環
		case 44:// 副助道具
		case 43:// 輔助格子左
		case 45:// 輔助格子中
		case 47:// 輔助格子左
		case 70:// 脛甲
			if (isEquipped()) {
				// 防具/道具 類型物件送出使用中物件上方會出現E
				name.append(" ($117)"); // 使用中(Worn)
			}
			break;
		}
		return name.toString();
	}

	/**
	 * 背包/倉庫 物件完整名稱取回<br>
	 */
	public String getNumberedViewName(final long count) {
		final StringBuilder name = new StringBuilder(getNumberedName(count, true));

		if (_time != null) {
			// pandora
			if (_pandora_type > 0) {
				final SimpleDateFormat sdf = new SimpleDateFormat("M-d H:m", Locale.TAIWAN);
				String fm = "1-1 8:0";
				fm = sdf.format(_time);
				name.append(" [" + fm + "]");
			} else {
				final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
				switch (_card_use) {
				case 0:
					name.append("[" + sdf.format(_time) + "]"); // 使用期限
					break;
				case 1:
					name.append("[" + sdf.format(_time) + "]"); // 使用期限
					break;
				case 2:
					name.append("[" + sdf.format(_time) + "]"); // 使用期限
					break;
				}
			}
		}

		// 凹槽系統
		if (_power_name_hole != null) {
			// 剩餘孔數判斷值
			int remain_hole_count = _power_name_hole.get_hole_count();
			for (int i = 0; i < remain_hole_count; i++) {
				switch (i) {
				case 0: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_1());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(magicStone.getName());
					} else {
						name.append(" ◎");
					}
					break;
				}
				case 1: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_2());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				case 2: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_3());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				case 3: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_4());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				case 4: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_5());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				}
			}
		}

		switch (_item.getUseType()) {
		default:
			if (isEquippedTemp()) {
				// 防具/武器/道具 類型物件送出使用中物件上方會出現E
				name.append(" ($117)"); // 使用中
			}
			break;

		case -12: // 寵物用具
			if (isEquipped()) {
				name.append(" ($117)"); // 使用中(Worn)
			}
			break;

		case -4: // 項圈
			final L1Pet pet = PetReading.get().getTemplate(getId());
			if (pet != null) {
				final L1Npc npc = NpcTable.get().getTemplate(pet.get_npcid());
				name.append("[Lv." + pet.get_level() + "]" + pet.get_name() + " HP" + pet.get_hp() + " "
						+ npc.get_nameid());
			}
			break;

		case 1: // 武器
			if (isEquipped()) {
				// 武器 類型物件送出使用中物件上方會出現E
				name.append(" ($9)"); // 揮舞(Armed)
			}
			break;

		case 10: // 照明道具
			if (isNowLighting()) {
				name.append(" ($10)");// 打開
			}
			switch (_item.getItemId()) {
			case 40001: // 燈
			case 40002: // 燈籠
				if (getRemainingTime() <= 0) {
					name.append(" ($11)");// 無油
				}
				break;
			}
			break;

		case 2: // 盔甲
		case 16:
		case 18: // T恤
		case 19: // 斗篷
		case 20: // 手套
		case 21: // 靴
		case 22: // 頭盔
		case 23: // 戒指
		case 24: // 項鍊
		case 25: // 盾牌
		case 37: // 腰帶
		case 40: // 耳環
		case 44:// 副助道具
		case 43:// 輔助格子左
		case 45:// 輔助格子中
		case 47:// 輔助格子左
		case 70:// 脛甲
		case 49: // 49494949
		case 81:// 自訂不顯示欄位可裝備道具
		case 82:// 自訂不顯示欄位可裝備道具
		case 83:// 自訂不顯示欄位可裝備道具
		case 84:// 自訂不顯示欄位可裝備道具
		case 85:// 自訂不顯示欄位可裝備道具
		case 86:// 自訂不顯示欄位可裝備道具
		case 87:// 自訂不顯示欄位可裝備道具
		case 88:// 自訂不顯示欄位可裝備道具
		case 89:// 自訂不顯示欄位可裝備道具
		case 90:// 自訂不顯示欄位可裝備道具
		case 91:// 自訂不顯示欄位可裝備道具
		case 92:// 自訂不顯示欄位可裝備道具
		case 93:// 自訂不顯示欄位可裝備道具
			if (isEquipped()) {
				// 防具/道具 類型物件送出使用中物件上方會出現E
				name.append(" ($117)"); // 使用中(Worn)
			}
			break;
		}
		return name.toString();
	}

	/**
	 * 顯示凹槽名稱 by terry0412
	 * 
	 * @param hole
	 * @return
	 */
	private final L1MagicStone set_hole_name(int hole) {
		if (hole <= 0) {
			return null;
		}
		return ExtraMagicStoneTable.getInstance().findStone(hole);
	}

	/**
	 * 背包會倉庫名稱顯示。<br>
	 * 範例: +6 匕首 (揮舞)
	 */
	public String getViewName() {
		return getNumberedViewName(_count);
	}

	/**
	 * 返回顯示名稱。<br>
	 * 範例:強力治癒藥水(50) / +6 匕首
	 */
	public String getLogName() {
		return getNumberedName(_count, true);
	}

	/**
	 * 物件完整名稱取回
	 * 
	 * @param count 數量
	 * @param mode 模式 true:使用NAMEID false:使用中文註解名稱
	 * @return
	 */
	public String getNumberedName(final long count, final boolean mode) {
		final StringBuilder name = new StringBuilder();

		if (isIdentified()) {
			switch (_item.getUseType()) {
			case 1:// 武器
					// 追加值
				if (getEnchantLevel() >= 0) {
					name.append("+" + getEnchantLevel() + " ");

				} else if (getEnchantLevel() < 0) {
					name.append(String.valueOf(getEnchantLevel()) + " ");
				}

				// 附加屬性
				final int attrEnchantLevel = getAttrEnchantLevel();
				if (attrEnchantLevel > 0) {
					name.append(attrEnchantLevel());
				}
				break;

			case 2: // 盔甲
			case 16:
			case 20: // 手套
			case 21: // 靴
			case 22: // 頭盔
			case 18: // T恤
			case 19: // 斗篷
			case 25: // 盾牌
			case 23:// 戒指
			case 24:// 項鍊
			case 37:// 腰帶
			case 40:// 耳環
			case 43:// 輔助格子左
			case 45:// 輔助格子中
			case 47:// 輔助格子左
			case 70:// 脛甲
			case 49: // 49494949
			case 81:// 自訂不顯示欄位可裝備道具
			case 82:// 自訂不顯示欄位可裝備道具
			case 83:// 自訂不顯示欄位可裝備道具
			case 84:// 自訂不顯示欄位可裝備道具
			case 85:// 自訂不顯示欄位可裝備道具
			case 86:// 自訂不顯示欄位可裝備道具
			case 87:// 自訂不顯示欄位可裝備道具
			case 88:// 自訂不顯示欄位可裝備道具
			case 89:// 自訂不顯示欄位可裝備道具
			case 90:// 自訂不顯示欄位可裝備道具
			case 91:// 自訂不顯示欄位可裝備道具
			case 92:// 自訂不顯示欄位可裝備道具
			case 93:// 自訂不顯示欄位可裝備道具
				// 追加值
				if (getEnchantLevel() >= 0) {
					name.append("+" + getEnchantLevel() + " ");

				} else if (getEnchantLevel() < 0) {
					name.append(String.valueOf(getEnchantLevel()) + " ");
				}

				break;
			}
		}

		if (mode) {
			switch (_pandora_type) {
			case 1:
				if (_itemId == 20084) {
					name.append("$13114");// 潘朵拉的 力量花香 妖精T恤
				} else {
					name.append("$13052");// 潘朵拉的 力量T恤
				}
				break;
			case 2:
				if (_itemId == 20084) {
					name.append("$13115");// 潘朵拉的 敏捷花香 妖精T恤
				} else {
					name.append("$13053");// 潘朵拉的 敏捷T恤
				}
				break;
			case 3:
				if (_itemId == 20084) {
					name.append("$13116");// 潘朵拉的 智力花香 妖精T恤
				} else {
					name.append("$13054");// 潘朵拉的 智力T恤
				}
				break;
			case 4:
				if (_itemId == 20084) {
					name.append("$13117");// 潘朵拉的 精神花香 妖精T恤
				} else {
					name.append("$13055");// 潘朵拉的 精神T恤
				}
				break;
			case 5:
				if (_itemId == 20084) {
					name.append("$13118");// 潘朵拉的 體力花香 妖精T恤
				} else {
					name.append("$13056");// 潘朵拉的 體力T恤
				}
				break;
			case 6:
				if (_itemId == 20084) {
					name.append("$13119");// 潘朵拉的 魅力花香 妖精T恤
				} else {
					name.append("$13057");// 潘朵拉的 魅力T恤
				}
				break;
			default:
				name.append(_item.getNameId());
				break;
			}

		} else {
			switch (_pandora_type) {
			case 1:
				if (_itemId == 20084) {
					name.append("$13114");// 潘朵拉的 力量花香 妖精T恤
				} else {
					name.append("$13052");// 潘朵拉的 力量T恤
				}
				break;
			case 2:
				if (_itemId == 20084) {
					name.append("$13115");// 潘朵拉的 敏捷花香 妖精T恤
				} else {
					name.append("$13053");// 潘朵拉的 敏捷T恤
				}
				break;
			case 3:
				if (_itemId == 20084) {
					name.append("$13116");// 潘朵拉的 智力花香 妖精T恤
				} else {
					name.append("$13054");// 潘朵拉的 智力T恤
				}
				break;
			case 4:
				if (_itemId == 20084) {
					name.append("$13117");// 潘朵拉的 精神花香 妖精T恤
				} else {
					name.append("$13055");// 潘朵拉的 精神T恤
				}
				break;
			case 5:
				if (_itemId == 20084) {
					name.append("$13118");// 潘朵拉的 體力花香 妖精T恤
				} else {
					name.append("$13056");// 潘朵拉的 體力T恤
				}
				break;
			case 6:
				if (_itemId == 20084) {
					name.append("$13119");// 潘朵拉的 魅力花香 妖精T恤
				} else {
					name.append("$13057");// 潘朵拉的 魅力T恤
				}
				break;
			default:
				name.append(_item.getName());
				break;
			}
		}

		if (_item.getUseType() == -5) { // 食人妖精競賽票
			name.append("\\f_[" + getGamNo() + "]");
		}

		if (isIdentified()) {
			// 資料庫原始最大可用次數大於0
			if (getItem().getMaxChargeCount() > 0) {
				name.append(" (" + getChargeCount() + ")");

			} else {
				switch (_item.getItemId()) {
				case 20383: // 軍馬頭盔
					name.append(" (" + getChargeCount() + ")");
					break;

				default:
					break;
				}
			}

			if (_time == null) {
				// 武器/防具 具有使用時間
				if (getItem().getMaxUseTime() > 0) {
					name.append(" (" + getRemainingTime() + ")");
				}
			}
		}

		if (count > 1) {
			if (count < 1000000000) {
				name.append(" (" + count + ")");

			} else {
				name.append(" (" + RangeLong.scount(count) + ")");
			}
		}

		return name.toString();
	}

	/**
	 * 不帶數量物件完整名稱取回
	 * 
	 * @param count 數量
	 * @param mode 模式 true:使用NAMEID false:使用中文註解名稱
	 * @return
	 */
	public String getNonumberName() {
		final StringBuilder name = new StringBuilder();

		if (isIdentified()) {
			switch (_item.getUseType()) {
			case 1:// 武器
					// 追加值
				if (getEnchantLevel() >= 0) {
					name.append("+" + getEnchantLevel() + " ");

				} else if (getEnchantLevel() < 0) {
					name.append(String.valueOf(getEnchantLevel()) + " ");
				}
				// 附加屬性
				final int attrEnchantLevel = getAttrEnchantLevel();
				if (attrEnchantLevel > 0) {
					name.append(attrEnchantLevel());
				}
				break;

			case 2: // 盔甲
			case 16:
			case 20: // 手套
			case 21: // 靴
			case 22: // 頭盔

			case 18: // T恤
			case 19: // 斗篷
			case 25: // 盾牌
			case 23:// 戒指
			case 24:// 項鍊
			case 37:// 腰帶
			case 40:// 耳環
				// 追加值
				if (getEnchantLevel() >= 0) {
					name.append("+" + getEnchantLevel() + " ");

				} else if (getEnchantLevel() < 0) {
					name.append(String.valueOf(getEnchantLevel()) + " ");
				}
				break;
			}
		}

		if (isIdentified()) {
			switch (_pandora_type) {
			case 1:
				if (_itemId == 20084) {
					name.append("$13114");// 潘朵拉的 力量花香 妖精T恤
				} else {
					name.append("$13052");// 潘朵拉的 力量T恤
				}
				break;
			case 2:
				if (_itemId == 20084) {
					name.append("$13115");// 潘朵拉的 敏捷花香 妖精T恤
				} else {
					name.append("$13053");// 潘朵拉的 敏捷T恤
				}
				break;
			case 3:
				if (_itemId == 20084) {
					name.append("$13116");// 潘朵拉的 智力花香 妖精T恤
				} else {
					name.append("$13054");// 潘朵拉的 智力T恤
				}
				break;
			case 4:
				if (_itemId == 20084) {
					name.append("$13117");// 潘朵拉的 精神花香 妖精T恤
				} else {
					name.append("$13055");// 潘朵拉的 精神T恤
				}
				break;
			case 5:
				if (_itemId == 20084) {
					name.append("$13118");// 潘朵拉的 體力花香 妖精T恤
				} else {
					name.append("$13056");// 潘朵拉的 體力T恤
				}
				break;
			case 6:
				if (_itemId == 20084) {
					name.append("$13119");// 潘朵拉的 魅力花香 妖精T恤
				} else {
					name.append("$13057");// 潘朵拉的 魅力T恤
				}
				break;
			default:
				name.append(_item.getNameId());
				break;
			}
		} else {
			switch (_pandora_type) {
			case 1:
				if (_itemId == 20084) {
					name.append("$13114");// 潘朵拉的 力量花香 妖精T恤
				} else {
					name.append("$13052");// 潘朵拉的 力量T恤
				}
				break;
			case 2:
				if (_itemId == 20084) {
					name.append("$13115");// 潘朵拉的 敏捷花香 妖精T恤
				} else {
					name.append("$13053");// 潘朵拉的 敏捷T恤
				}
				break;
			case 3:
				if (_itemId == 20084) {
					name.append("$13116");// 潘朵拉的 智力花香 妖精T恤
				} else {
					name.append("$13054");// 潘朵拉的 智力T恤
				}
				break;
			case 4:
				if (_itemId == 20084) {
					name.append("$13117");// 潘朵拉的 精神花香 妖精T恤
				} else {
					name.append("$13055");// 潘朵拉的 精神T恤
				}
				break;
			case 5:
				if (_itemId == 20084) {
					name.append("$13118");// 潘朵拉的 體力花香 妖精T恤
				} else {
					name.append("$13056");// 潘朵拉的 體力T恤
				}
				break;
			case 6:
				if (_itemId == 20084) {
					name.append("$13119");// 潘朵拉的 魅力花香 妖精T恤
				} else {
					name.append("$13057");// 潘朵拉的 魅力T恤
				}
				break;
			default:
				name.append(_item.getName());
				break;
			}
		}

		if (_item.getUseType() == -5) { // 食人妖精競賽票
			name.append("\\f_[" + getGamNo() + "]");
		}

		if (isIdentified()) {
			// 資料庫原始最大可用次數大於0
			if (getItem().getMaxChargeCount() > 0) {
				name.append(" (" + getChargeCount() + ")");

			} else {
				switch (_item.getItemId()) {
				case 20383: // 軍馬頭盔
					name.append(" (" + getChargeCount() + ")");
					break;

				default:
					break;
				}
			}

			if (_time == null) {
				// 武器/防具 具有使用時間
				if (getItem().getMaxUseTime() > 0) {
					name.append(" (" + getRemainingTime() + ")");
				}
			}
		}

		/*
		 * if (count > 1) { if (count < 1000000000) { name.append(" (" + count +
		 * ")"); } else { name.append(" (" + RangeLong.scount(count) + ")"); } }
		 */

		return name.toString();
	}

	// 屬性武器
	/*
	 * private static final String[][] _attrEnchant = new String[][]{ new
	 * String[]{"$6124 ", "$6125 ", "$6126 "},// 地之, 崩裂, 地靈 new String[]{
	 * "$6115 ", "$6116 ", "$6117 "},// 火之, 烈焰, 火靈 new String[]{"$6118 ",
	 * "$6119 ", "$6120 "},// 水之, 海嘯, 水靈 new String[]{"$6121 ", "$6122 ",
	 * "$6123 "},// 風之, 暴風, 風靈 // ADD LOLI new String[]{"光之 ", "閃耀 ", "光靈 "},
	 * new String[]{"暗之 ", "陰影 ", "暗靈 "}, new String[]{"聖之 ", "神聖 ", "聖靈 "}, new
	 * String[]{"邪之 ", "邪惡 ", "邪靈 "}, };
	 */

	/**
	 * 屬性武器
	 * 
	 * @return
	 */
	private StringBuilder attrEnchantLevel() {
		final StringBuilder attrEnchant = new StringBuilder();

		final int attrEnchantLevel = getAttrEnchantLevel();

		/*
		 * int type = 0; switch (this.getAttrEnchantKind()) { case 1: // 地 type
		 * = 0; break; case 2: // 火 type = 1; break; case 4: // 水 type = 2;
		 * break; case 8: // 風 type = 3; break; case 16: // 光 type = 4; break;
		 * case 32: // 暗 type = 5; break; case 64: // 聖 type = 6; break; case
		 * 128: // 邪 type = 7; break; default: break; }
		 * attrEnchant.append(_attrEnchant[type][attrEnchantLevel-1]);
		 */

		// 屬性武器系統(DB自製) by terry0412
		final L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(getAttrEnchantKind(),
				attrEnchantLevel);
		if (attrWeapon != null) {
			attrEnchant.append(attrWeapon.getName());
		}

		return attrEnchant;
	}

	/**
	 * 物品詳細資料
	 */
	public byte[] getStatusBytes() {
		final L1ItemStatus itemInfo = new L1ItemStatus(this);
		return itemInfo.getStatusBytes().getBytes();
	}

	/**
	 * 抗魔
	 * 
	 * @return
	 */
	public int getMr() {
		final L1ItemPower itemPower = new L1ItemPower(this);
		return itemPower.getMr();
	}

	/**
	 * 強化飾品設置
	 */
	public void greater(final L1PcInstance owner, final boolean equipment) {
		final L1ItemPower itemPower = new L1ItemPower(this);
		itemPower.greater(owner, equipment);
	}

	/*
	 * class EnchantTimer extends TimerTask { public EnchantTimer() { }
	 * @Override public void run() { try { final int type = getItem().getType();
	 * final int type2 = getItem().getType2(); final int objid = getId(); if
	 * ((_pc != null) && _pc.getInventory().getItem(objid) != null) { if ((type
	 * == 2) && (type2 == 2) && isEquipped()) { _pc.addAc(3);
	 * _pc.sendPackets(new S_OwnCharStatus(_pc)); } } setAcByMagic(0);
	 * setDmgByMagic(0); setHolyDmgByMagic(0); setHitByMagic(0); // 308 你的 %0%o
	 * 失去了光芒。 _pc.sendPackets(new S_ServerMessage(308, getLogName()));
	 * _isRunning = false; _timer = null; } catch (final Exception e) {
	 * _log.warn("EnchantTimer: " + getItemId()); } } }
	 */

	private int _acByMagic = 0;

	/**
	 * 魔法增加額外防禦力
	 * 
	 * @return
	 */
	public int getAcByMagic() {
		return _acByMagic;
	}

	/**
	 * 魔法增加額外防禦力
	 * 
	 * @param i
	 */
	public void setAcByMagic(final int i) {
		_acByMagic = i;
	}

	private int _dmgByMagic = 0;

	/**
	 * 魔法增加額外攻擊
	 * 
	 * @return
	 */
	public int getDmgByMagic() {
		/*
		 * int adddmg = 0; if (_power_name_hole != null &&
		 * this.getItem().getType2() == 1) { switch
		 * (_power_name_hole.get_hole_1()) { case 9:// 攻 額外攻擊+3 adddmg += 3;
		 * break; } switch (_power_name_hole.get_hole_2()) { case 9:// 攻 額外攻擊+3
		 * adddmg += 3; break; } switch (_power_name_hole.get_hole_3()) { case
		 * 9:// 攻 額外攻擊+3 adddmg += 3; break; } switch
		 * (_power_name_hole.get_hole_4()) { case 9:// 攻 額外攻擊+3 adddmg += 3;
		 * break; } switch (_power_name_hole.get_hole_5()) { case 9:// 攻 額外攻擊+3
		 * adddmg += 3; break; } }
		 */
		return _dmgByMagic/* + adddmg */;
	}

	/**
	 * 魔法增加額外攻擊
	 * 
	 * @param i
	 */
	public void setDmgByMagic(final int i) {
		_dmgByMagic = i;
	}

	private int _holyDmgByMagic = 0;

	public int getHolyDmgByMagic() {
		return _holyDmgByMagic;
	}

	public void setHolyDmgByMagic(final int i) {
		_holyDmgByMagic = i;
	}

	private int _hitByMagic = 0;

	/**
	 * 魔法增加額外命中
	 * 
	 * @return
	 */
	public int getHitByMagic() {
		return _hitByMagic;
	}

	/**
	 * 魔法增加額外命中
	 * 
	 * @param i
	 */
	public void setHitByMagic(final int i) {
		_hitByMagic = i;
	}

	/**
	 * 盔甲強化時間軸
	 * 
	 * @param pc
	 * @param skillId
	 * @param skillTime
	 */
	/*
	 * public void setSkillArmorEnchant(final L1PcInstance pc, final int
	 * skillId, final int skillTime) { final int type = getItem().getType();
	 * final int type2 = getItem().getType2(); if (_isRunning) {
	 * _timer.cancel(); final int objid = this.getId(); if ((pc != null) &&
	 * pc.getInventory().getItem(objid) != null) { if ((type == 2) && (type2 ==
	 * 2) && isEquipped()) { pc.addAc(3); pc.sendPackets(new
	 * S_OwnCharStatus(pc)); } } setAcByMagic(0); _isRunning = false; _timer =
	 * null; } if ((type == 2) && (type2 == 2) && isEquipped()) { pc.addAc(-3);
	 * pc.sendPackets(new S_OwnCharStatus(pc)); } setAcByMagic(3); _pc = pc;
	 * _char_objid = _pc.getId(); _timer = new EnchantTimer(); (new
	 * Timer()).schedule(_timer, skillTime); _isRunning = true; }
	 */

	/**
	 * 武器強化時間軸
	 * 
	 * @param pc
	 * @param skillId
	 * @param skillTime
	 */
	/*
	 * public void setSkillWeaponEnchant(final L1PcInstance pc, final int
	 * skillId, final int skillTime) { if (getItem().getType2() != 1) { return;
	 * } if (_isRunning) { _timer.cancel(); setDmgByMagic(0);
	 * setHolyDmgByMagic(0); setHitByMagic(0); _isRunning = false; _timer =
	 * null; } switch (skillId) { case HOLY_WEAPON: setHolyDmgByMagic(1);
	 * setHitByMagic(1); break; case ENCHANT_WEAPON: setDmgByMagic(2); break;
	 * case BLESS_WEAPON: setDmgByMagic(2); setHitByMagic(2); break; case
	 * SHADOW_FANG: setDmgByMagic(5); break; default: break; } _pc = pc;
	 * _char_objid = _pc.getId(); _timer = new EnchantTimer(); (new
	 * Timer()).schedule(_timer, skillTime); _isRunning = true; }
	 */

	private int _itemOwnerId = 0;

	public int getItemOwnerId() {
		return _itemOwnerId;
	}

	public void setItemOwnerId(final int i) {
		_itemOwnerId = i;
	}

	private L1EquipmentTimer _equipmentTimer;

	public boolean isInTimer() {
		if (_equipmentTimer == null) {
			return false;
		}
		return _equipmentTimer.isRunning();
	}

	/**
	 * 計時物件啟用
	 * 
	 * @param pc
	 */
	public void startEquipmentTimer(final L1PcInstance pc) {
		if (_time != null) {
			return;
		}
		if (getRemainingTime() > 0) {
			_equipmentTimer = new L1EquipmentTimer(pc, this, true);
			final Timer timer = new Timer(true);
			timer.scheduleAtFixedRate(_equipmentTimer, 1000, 1000);
		}
	}

	/**
	 * 計時物件停止計時
	 * 
	 * @param pc
	 */
	public void stopEquipmentTimer(final L1PcInstance pc) {
		if (_time != null) {
			return;
		}
		if (getRemainingTime() > 0) {
			_equipmentTimer.cancel();
			_equipmentTimer = null;
		}
	}

	private boolean _isNowLighting = false;

	public boolean isNowLighting() {
		return _isNowLighting;
	}

	public void setNowLighting(final boolean flag) {
		_isNowLighting = flag;
	}

	/**
	 * 傳回物件使用中
	 * 
	 * @return
	 */
	public boolean isEquippedTemp() {
		return _isEquippedTemp;
	}

	/**
	 * 設置物件使用中
	 * 
	 * @param isEquippedTemp
	 */
	public void set_isEquippedTemp(final boolean isEquippedTemp) {
		_isEquippedTemp = isEquippedTemp;
	}

	private boolean _isMatch = false;

	/**
	 * 完成套裝
	 * 
	 * @param isMatch
	 */
	public void setIsMatch(final boolean isMatch) {
		_isMatch = isMatch;
	}

	/**
	 * 完成套裝
	 * 
	 * @return true:完成套裝 false:未完成套裝
	 */
	public boolean isMatch() {
		return _isMatch;
	}

	// 物品使用者OBJID
	private int _char_objid = -1;

	/**
	 * 設置物品使用者OBJID
	 * 
	 * @param skilltime
	 */
	public void set_char_objid(final int char_objid) {
		_char_objid = char_objid;
	}

	/**
	 * 物品使用者OBJID
	 * 
	 * @return _skilltime
	 */
	public int get_char_objid() {
		return _char_objid;
	}

	// 物品使用期限結束時間
	private Timestamp _time = null;

	/**
	 * 設置物品使用期限結束時間
	 * 
	 * @param skilltime
	 */
	public void set_time(final Timestamp time) {
		_time = time;
	}

	/**
	 * 物品使用期限結束時間
	 * 
	 * @return _skilltime
	 */
	public Timestamp get_time() {
		return _time;
	}

	/**
	 * 技能強化中
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return _isRunning;
	}

	// ADD FIX
	public void addskill(final int skillid, final int time) {
		if (!_skilllist.containsKey(skillid)) {
			switch (skillid) {
			case HOLY_WEAPON:
				setHolyDmgByMagic(1);
				setHitByMagic(1);
				break;
			case ENCHANT_WEAPON:
				setDmgByMagic(2);
				break;
			case BLESS_WEAPON:
				setDmgByMagic(2);
				setHitByMagic(2);
				break;
			case SHADOW_FANG:
				setDmgByMagic(5);
				break;
			case BLESSED_ARMOR:
				final L1PcInstance pc = World.get().getPlayer(_char_objid);
				if (pc == null) {
					return;
				}
				if ((getItem().getType() == 2) && (getItem().getType2() == 2) && isEquipped()) {
					pc.addAc(-3);
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
				setAcByMagic(3);
				break;
			default:
				break;
			}
			_isRunning = true;
		}
		_skilllist.put(skillid, time);
	}

	// ADD FIX 技能疊加技能疊加
	public void removeskill(final int skillid) {
		if (_skilllist.remove(skillid) != 0) {
			final L1PcInstance pc = World.get().getPlayer(_char_objid);
			switch (skillid) {
			case HOLY_WEAPON:
				setHolyDmgByMagic(0);
				setHitByMagic(0);
				break;
			case ENCHANT_WEAPON:
				setDmgByMagic(0);
				break;
			case BLESS_WEAPON:
				setDmgByMagic(0);
				setHitByMagic(0);
				break;
			case SHADOW_FANG:
				setDmgByMagic(0);
				break;
			case BLESSED_ARMOR:
				setAcByMagic(0);
				if (pc == null) {
					return;
				}
				if ((getItem().getType() == 2) && (getItem().getType2() == 2) && isEquipped()) {
					pc.addAc(3);
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
				break;
			default:
				break;
			}
			_isRunning = false;
			pc.sendPackets(new S_ServerMessage(308, getLogName()));
		}
	}

	public int getSkillTime(final int skillid) {
		final int time = _skilllist.get(skillid);
		return time;
	}

	// 古文字
	private L1ItemPower_name _power_name = null;

	// 凹槽系統
	public L1ItemPowerHole_name _power_name_hole = null;

	/**
	 * 古文字
	 * 
	 * @param power_name
	 */
	public void set_power_name(final L1ItemPower_name power_name) {
		_power_name = power_name;
	}

	/**
	 * 古文字
	 * 
	 * @return
	 */
	public L1ItemPower_name get_power_name() {
		return _power_name;
	}

	/**
	 * 凹槽系統
	 * 
	 * @return
	 */
	public L1ItemPowerHole_name get_power_name_hole() {
		return _power_name_hole;
	}

	/**
	 * 凹槽系統
	 * 
	 * @param power_name
	 */
	public void set_power_name_hole(final L1ItemPowerHole_name power_name_hole) {
		_power_name_hole = power_name_hole;
	}

	private int _card_use = 0;// 0:未使用 1:使用中 2:到期

	/**
	 * 0:未使用 1:使用中 2:到期
	 * 
	 * @return
	 */
	public int get_card_use() {
		return _card_use;
	}

	/**
	 * 0:未使用 1:使用中 2:到期
	 * 
	 * @param card_use
	 */
	public void set_card_use(final int card_use) {
		_card_use = card_use;
	}

	// 屬性武器
	/*
	 * private static final String[][] _attrEnchantString = new String[][]{ new
	 * String[]{"地之 ", "崩裂 ", "地靈 "},// 地之, 崩裂, 地靈 new String[]{"火之 ", "烈焰 ",
	 * "火靈 "},// 火之, 烈焰, 火靈 new String[]{"水之 ", "海嘯 ", "水靈 "},// 水之, 海嘯, 水靈 new
	 * String[]{"風之 ", "暴風 ", "風靈 "},// 風之, 暴風, 風靈 // ADD LOLI new String[]{
	 * "光之 ", "閃耀 ", "光靈 "}, new String[]{"暗之 ", "陰影 ", "暗靈 "}, new String[]{
	 * "聖之 ", "神聖 ", "聖靈 "}, new String[]{"邪之 ", "邪惡 ", "邪靈 "}, };
	 */

	public String getNumberedName_to_String() {
		final StringBuilder name = new StringBuilder();
		// 追加值
		if (getEnchantLevel() >= 0) {
			name.append("+" + getEnchantLevel() + " ");

		} else if (getEnchantLevel() < 0) {
			name.append(String.valueOf(getEnchantLevel()) + " ");
		}

		switch (_item.getUseType()) {
		case 1:// 武器
				// 附加屬性
			final int attrEnchantLevel = getAttrEnchantLevel();
			if (attrEnchantLevel > 0) {
				/*
				 * int type = 0; switch (this.getAttrEnchantKind()) { case 1: //
				 * 地 type = 0; break; case 2: // 火 type = 1; break; case 4: // 水
				 * type = 2; break; case 8: // 風 type = 3; break; case 16: // 光
				 * type = 4; break; case 32: // 暗 type = 5; break; case 64: // 聖
				 * type = 6; break; case 128: // 邪 type = 7; break; }
				 * name.append(_attrEnchantString[type][attrEnchantLevel-1]);
				 */

				// 屬性武器系統(DB自製) by terry0412
				final L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(getAttrEnchantKind(),
						attrEnchantLevel);
				if (attrWeapon != null) {
					name.append(attrWeapon.getName());
				}
			}
			break;

		case 2: // 盔甲
		case 16:
		case 20: // 手套
		case 21: // 靴
		case 22: // 頭盔
		case 18: // T恤
		case 19: // 斗篷
		case 25: // 盾牌
		case 23:// 戒指
		case 24:// 項鍊
		case 37:// 腰帶
		case 40:// 耳環
		case 70:// 徑甲
			break;
		}

		name.append(_item.getName());

		/*
		 * if (_power_name != null) { name.append("[" +
		 * _power_name.get_power_name() + "]"); }
		 */

		// 資料庫原始最大可用次數大於0
		if (getItem().getMaxChargeCount() > 0) {
			name.append(" (" + getChargeCount() + ")");

		} else {
			switch (_item.getItemId()) {
			case 20383: // 軍馬頭盔
				name.append(" (" + getChargeCount() + ")");
				break;

			default:
				break;
			}
		}

		// 凹槽系統
		if (_power_name_hole != null) {
			// 剩餘孔數判斷值
			int remain_hole_count = _power_name_hole.get_hole_count();
			for (int i = 0; i < remain_hole_count; i++) {
				switch (i) {
				case 0: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_1());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(magicStone.getName());
					} else {
						name.append(" ◎");
					}
					break;
				}
				case 1: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_2());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				case 2: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_3());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				case 3: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_4());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				case 4: {
					final L1MagicStone magicStone = set_hole_name(_power_name_hole.get_hole_5());
					if (magicStone != null) {
						remain_hole_count -= magicStone.getNeedHole() - 1;
						name.append(" ").append(magicStone.getName());
					} else {
						name.append("◎");
					}
					break;
				}
				}
			}
		}

		final long count = getCount();
		if (count > 1) {
			if (count < 1000000000) {
				name.append(" (" + count + ")");

			} else {
				name.append(" (" + RangeLong.scount(count) + ")");
			}
		}
		return name.toString();
	}

	private int _pandora_type = -1;

	public int get_pandora_type() {
		return _pandora_type;
	}

	public void set_pandora_type(final L1PcInstance pc, final int type) {
		if (pc == null) {
			_pandora_type = type;
		} else {
			_pandora_type = type;

			int gfx = 0;
			switch (type) {
			case 1:
				gfx = 2688;
				break;
			case 2:
				gfx = 2691;
				break;
			case 3:
				gfx = 2685;
				break;
			case 4:
				gfx = 2686;
				break;
			case 5:
				gfx = 2690;
				break;
			case 6:
				gfx = 2689;
				break;
			default:
				gfx = getItem().getGfxId();
				break;
			}

			pc.sendPackets(new S_PacketBoxUpdateGfxid(getId(), gfx));
			pc.sendPackets(new S_ItemStatus(this));
		}
	}

	public void set_pandora_buff(final L1PcInstance pc, final boolean flag) {
		if (flag) {
			switch (_pandora_type) {
			case 1:
				pc.addStr(1);
				break;
			case 2:
				pc.addDex(1);
				break;
			case 3:
				pc.addInt(1);
				break;
			case 4:
				pc.addWis(1);
				break;
			case 5:
				pc.addCon(1);
				break;
			case 6:
				pc.addCha(1);
				break;
			}
		} else {
			switch (_pandora_type) {
			case 1:
				pc.addStr(-1);
				break;
			case 2:
				pc.addDex(-1);
				break;
			case 3:
				pc.addInt(-1);
				break;
			case 4:
				pc.addWis(-1);
				break;
			case 5:
				pc.addCon(-1);
				break;
			case 6:
				pc.addCha(-1);
				break;
			}
		}
		pc.sendPackets(new S_OwnCharStatus2(pc));
	}

	private int _pandora_mark = -1;

	public int get_pandora_mark() {
		return _pandora_mark;
	}

	public void set_pandora_mark(final L1PcInstance pc, final int type) {
		if (pc == null) {
			_pandora_mark = type;
		} else {
			_pandora_mark = type;
			pc.sendPackets(new S_ItemStatus(this));
		}
	}

	public void set_pandora_markbuff(final L1PcInstance pc, final boolean flag) {
		if (flag) {
			switch (_pandora_mark) {
			case 1:
				pc.addFire(10);
				pc.addWater(10);
				pc.addWind(10);
				pc.addEarth(10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				break;
			case 2:
				pc.addRegistStun(10);
				break;
			case 3:
				pc.addRegistSustain(10);
				break;
			case 4:
				pc.addRegistStone(10);
				break;
			case 5:
				pc.addHpr(1);
				pc.addMpr(1);
				break;
			case 6:
				pc.addAc(-1);
				break;
			case 7:
				pc.addMr(10);
				pc.sendPackets(new S_SPMR(pc));
				break;
			case 8:
				pc.addMaxHp(50);
				pc.sendPackets(new S_HPUpdate(pc));
				break;
			case 9:
				pc.addMaxMp(30);
				pc.sendPackets(new S_MPUpdate(pc));
				break;
			}
		} else {
			switch (_pandora_mark) {
			case 1:
				pc.addFire(-10);
				pc.addWater(-10);
				pc.addWind(-10);
				pc.addEarth(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				break;
			case 2:
				pc.addRegistStun(-10);
				break;
			case 3:
				pc.addRegistSustain(-10);
				break;
			case 4:
				pc.addRegistStone(-10);
				break;
			case 5:
				pc.addHpr(-1);
				pc.addMpr(-1);
				break;
			case 6:
				pc.addAc(1);
				break;
			case 7:
				pc.addMr(-10);
				pc.sendPackets(new S_SPMR(pc));
				break;
			case 8:
				pc.addMaxHp(-50);
				pc.sendPackets(new S_HPUpdate(pc));
				break;
			case 9:
				pc.addMaxMp(-30);
				pc.sendPackets(new S_MPUpdate(pc));
				break;
			}
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	private String _creater_name;

	public void set_creater_name(final String creater_name) {
		_creater_name = creater_name;
	}

	public String get_creater_name() {
		return _creater_name;
	}

	private double _random;

	public void set_random(final double i) {
		_random = i;
	}

	public double get_random() {
		return _random;
	}

	private int _extra_random;

	public void set_extra_random(final int i) {
		_extra_random = i;
	}

	public int get_extra_random() {
		return _extra_random;
	}

	// 魔法武器DIY系統(附魔石類型) by terry0412
	private L1MagicWeapon _magic_weapon;

	public final L1MagicWeapon get_magic_weapon() {
		return _magic_weapon;
	}

	public final void set_magic_weapon(final L1MagicWeapon value) {
		_magic_weapon = value;
	}

	// 魔法武器DIY系統(使用期限) by terry0412

	private int _protect;

	/**
	 * 設定道具防爆狀態<br>
	 * 1=沒有任何事情發生<br>
	 * 2=倒退1<br>
	 * 3=歸0<br>
	 * 
	 * @param i
	 */
	public void set_protect_type(final int i) {
		_protect = i;
	}

	public int get_protect_type() {
		return _protect;
	}
	
	private int _keyId = 0;
	private int _innNpcId = 0;
	private boolean _isHall;
	private Timestamp _dueTime;
	
	public int getKeyId() {
		return _keyId;
	}

	public void setKeyId(int i) {
		_keyId = i;
	}

	public int getInnNpcId() {
		return _innNpcId;
	}

	public void setInnNpcId(int i) {
		_innNpcId = i;
	}

	public boolean checkRoomOrHall() {
		return _isHall;
	}

	public void setHall(boolean i) {
		_isHall = i;
	}

	public Timestamp getDueTime() {
		return _dueTime;
	}

	public void setDueTime(Timestamp i) {
		_dueTime = i;
	}
	
	public String getInnKeyName() {
		StringBuilder name = new StringBuilder();
		name.append(" #");
		String chatText = String.valueOf(getKeyId());
		String s1 = "";
		String s2 = "";
		for (int i = 0; i < chatText.length(); i++) {
			if (i >= 5) {
				break;
			}
			s1 = s1 + String.valueOf(chatText.charAt(i));
		}
		name.append(s1);
		for (int i = 0; i < chatText.length(); i++) {
			if (i % 2 == 0) {
				s1 = String.valueOf(chatText.charAt(i));
			} else {
				s2 = s1 + String.valueOf(chatText.charAt(i));
				name.append(Integer.toHexString(Integer.valueOf(s2).intValue()).toLowerCase());
			}
		}
		return name.toString();
	}
}
