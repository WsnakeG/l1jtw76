package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.ItemClass;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.IdFactory;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Armor;
import com.lineage.server.templates.L1EtcItem;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Weapon;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.Random;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 道具,武器,防具資料
 * 
 * @author dexc
 */
public class ItemTable {

	private static final Log _log = LogFactory.getLog(ItemTable.class);

	// 防具類型核心分類
	private static final Map<String, Integer> _armorTypes = new HashMap<String, Integer>();

	// 武器類型核心分類
	private static final Map<String, Integer> _weaponTypes = new HashMap<String, Integer>();

	// 武器類型觸發事件
	private static final Map<String, Integer> _weaponId = new HashMap<String, Integer>();

	// 材質類型核心分類
	private static final Map<String, Integer> _materialTypes = new HashMap<String, Integer>();

	// 道具類型核心分類
	private static final Map<String, Integer> _etcItemTypes = new HashMap<String, Integer>();

	// 道具類型觸發事件
	private static final Map<String, Integer> _useTypes = new HashMap<String, Integer>();

	private static ItemTable _instance;

	private L1Item _allTemplates[];

	private static Map<Integer, L1EtcItem> _etcitems;

	private static Map<Integer, L1Armor> _armors;

	private static Map<Integer, L1Weapon> _weapons;

	private static int _cdescid = 20000;

	public static synchronized int cdescid() {
		return _cdescid++;
	}

	public void reload() {
		_etcitems.clear();
		_armors.clear();
		_weapons.clear();
		load();
	}

	static {
		// 物品類型
		_etcItemTypes.put("arrow", new Integer(0));// 箭
		_etcItemTypes.put("wand", new Integer(1));// 魔杖
		_etcItemTypes.put("light", new Integer(2));// 照明
		_etcItemTypes.put("gem", new Integer(3));// 寶石
		_etcItemTypes.put("totem", new Integer(4));// 圖騰
		_etcItemTypes.put("firecracker", new Integer(5));// 煙火
		_etcItemTypes.put("potion", new Integer(6));// 藥水
		_etcItemTypes.put("food", new Integer(7));// 食物
		_etcItemTypes.put("scroll", new Integer(8));// 卷軸
		_etcItemTypes.put("questitem", new Integer(9));// 任務物品
		_etcItemTypes.put("spellbook", new Integer(10));// 魔法書
		_etcItemTypes.put("petitem", new Integer(11));// 寵物物品
		_etcItemTypes.put("other", new Integer(12));// 其他
		_etcItemTypes.put("material", new Integer(13));// 材料
		_etcItemTypes.put("event", new Integer(14));// 活動物品
		_etcItemTypes.put("sting", new Integer(15));// 飛刀
		_etcItemTypes.put("treasure_box", new Integer(16));// 寶盒

		// 物品使用封包類型
		_useTypes.put("petitem", new Integer(-12)); // 寵物道具
		_useTypes.put("other", new Integer(-11)); // 对读取方法调用无法分类的物品
		_useTypes.put("power", new Integer(-10)); // 加速药水
		_useTypes.put("book", new Integer(-9)); // 技术书
		_useTypes.put("makecooking", new Integer(-8));// 料理书
		_useTypes.put("hpr", new Integer(-7));// 增HP道具
		_useTypes.put("mpr", new Integer(-6));// 增MP道具
		_useTypes.put("ticket", new Integer(-5)); // 食人妖精競賽票/死亡競賽票/彩票
		_useTypes.put("petcollar", new Integer(-4)); // 項圈
		_useTypes.put("sting", new Integer(-3)); // 飛刀
		_useTypes.put("arrow", new Integer(-2)); // 箭
		_useTypes.put("none", new Integer(-1)); // 無法使用(材料等)
		_useTypes.put("normal", new Integer(0));// 一般物品
		_useTypes.put("weapon", new Integer(1));// 武器
		_useTypes.put("armor", new Integer(2));// 盔甲
		_useTypes.put("spell_1", new Integer(3)); // 創造怪物魔杖(無須選取目標 -
													// 無數量:沒有任何事情發生)
		_useTypes.put("4", new Integer(4)); // 希望魔杖 XXX
		_useTypes.put("spell_long", new Integer(5)); // 魔杖類型(須選取目標/座標)
		_useTypes.put("ntele", new Integer(6));// 瞬間移動卷軸
		_useTypes.put("identify", new Integer(7));// 鑑定卷軸
		_useTypes.put("res", new Integer(8));// 復活卷軸
		_useTypes.put("home", new Integer(9)); // 傳送回家的卷軸
		_useTypes.put("light", new Integer(10)); // 照明道具
		_useTypes.put("11", new Integer(11)); // 未分類的卷軸 XXX
		_useTypes.put("letter", new Integer(12));// 信紙
		_useTypes.put("letter_card", new Integer(13)); // 信紙(寄出)
		_useTypes.put("choice", new Integer(14));// 請選擇一個物品(道具欄位)
		_useTypes.put("instrument", new Integer(15));// 哨子
		_useTypes.put("sosc", new Integer(16));// 變形卷軸
		_useTypes.put("spell_short", new Integer(17)); // 選取目標 (近距離)
		_useTypes.put("T", new Integer(18));// T恤
		_useTypes.put("cloak", new Integer(19));// 斗篷
		_useTypes.put("glove", new Integer(20)); // 手套
		_useTypes.put("boots", new Integer(21));// 靴
		_useTypes.put("helm", new Integer(22));// 頭盔
		_useTypes.put("ring", new Integer(23));// 戒指
		_useTypes.put("amulet", new Integer(24));// 項鍊
		_useTypes.put("shield", new Integer(25));// 盾牌
		_useTypes.put("guarder", new Integer(25));// 臂甲
		_useTypes.put("dai", new Integer(26));// 對武器施法的卷軸
		_useTypes.put("zel", new Integer(27));// 對盔甲施法的卷軸
		_useTypes.put("blank", new Integer(28));// 空的魔法卷軸
		_useTypes.put("btele", new Integer(29));// 瞬間移動卷軸(祝福)
		_useTypes.put("spell_buff", new Integer(30)); // 魔法卷軸選取目標 (遠距離 無XY座標傳回)
		_useTypes.put("ccard", new Integer(31));// 聖誕卡片
		_useTypes.put("ccard_w", new Integer(32));// 聖誕卡片(寄出)
		_useTypes.put("vcard", new Integer(33));// 情人節卡片
		_useTypes.put("vcard_w", new Integer(34));// 情人節卡片(寄出)
		_useTypes.put("wcard", new Integer(35));// 白色情人節卡片
		_useTypes.put("wcard_w", new Integer(36));// 白色情人節卡片(寄出)
		_useTypes.put("belt", new Integer(37));// 腰帶
		_useTypes.put("food", new Integer(38)); // 食物
		_useTypes.put("spell_long2", new Integer(39)); // 選取目標 (遠距離)
		_useTypes.put("earring", new Integer(40)); // 耳環
		_useTypes.put("fishing_rod", new Integer(42));// 釣魚杆
		// _useTypes.put("aid", new Integer(44)); // 副助道具
		_useTypes.put("enc", new Integer(46)); // 飾品強化捲軸
		_useTypes.put("up1", new Integer(44));// 7.0輔助
		// _useTypes.put("aidr", new Integer(43));// 3.5TW輔助右
		// _useTypes.put("aidm", new Integer(45));// 3.5TW輔助中
		// _useTypes.put("aidr2", new Integer(48));// 3.5TW輔助右下
		// _useTypes.put("aidl2", new Integer(47));// 3.5TW輔助左下
		
		// 51 6.2C 伊娃的祝福
		_useTypes.put("choice_doll", new Integer(55));// 請選擇魔法娃娃
		_useTypes.put("suona", new Integer(60));// 全頻廣播器
		// 61 6.2C 戰鬥特化卷軸
		_useTypes.put("soscs", new Integer(61));// 變形卷軸
		// _useTypes.put("gaiter", new Integer(88));// 脛甲
		_useTypes.put("gaiter", new Integer(70));// 脛甲
		_useTypes.put("down1", new Integer(43));// 輔助格子 123
		_useTypes.put("down2", new Integer(45));
		_useTypes.put("down3", new Integer(47));
		_useTypes.put("up2", new Integer(49));

		_useTypes.put("armor1", new Integer(81)); // 自訂不顯示欄位裝備
		_useTypes.put("armor2", new Integer(82)); // 自訂不顯示欄位裝備
		_useTypes.put("armor3", new Integer(83)); // 自訂不顯示欄位裝備
		_useTypes.put("armor4", new Integer(84)); // 自訂不顯示欄位裝備
		_useTypes.put("armor5", new Integer(85)); // 自訂不顯示欄位裝備
		_useTypes.put("armor6", new Integer(86)); // 自訂不顯示欄位裝備
		_useTypes.put("armor7", new Integer(87)); // 自訂不顯示欄位裝備
		_useTypes.put("armor8", new Integer(88)); // 自訂不顯示欄位裝備
		_useTypes.put("armor9", new Integer(89)); // 自訂不顯示欄位裝備
		_useTypes.put("armor10", new Integer(90)); // 自訂不顯示欄位裝備
		_useTypes.put("armor11", new Integer(91)); // 自訂不顯示欄位裝備
		_useTypes.put("armor12", new Integer(92)); // 自訂不顯示欄位裝備
		_useTypes.put("armor13", new Integer(93)); // 自訂不顯示欄位裝備
//		_useTypes.put("armor16", new Integer(96));  // 使用後會出現請選擇武器
//		_useTypes.put("armor17", new Integer(97)); // 使用後會出現請選擇武器
//		_useTypes.put("armor18", new Integer(98)); // 使用後會出現請選擇武器
//		_useTypes.put("armor19", new Integer(99)); // 使用後會出現請選擇武器
//		_useTypes.put("armor20", new Integer(100)); // 使用後會出現請選擇武器

		_armorTypes.put("none", new Integer(0));
		_armorTypes.put("helm", new Integer(1));// 頭盔
		_armorTypes.put("armor", new Integer(2));// 盔甲
		_armorTypes.put("T", new Integer(3));// 內衣
		_armorTypes.put("cloak", new Integer(4));// 斗篷
		_armorTypes.put("glove", new Integer(5));// 手套
		_armorTypes.put("boots", new Integer(6));// 長靴
		_armorTypes.put("shield", new Integer(7));// 盾牌
		_armorTypes.put("amulet", new Integer(8));// 項鏈
		_armorTypes.put("ring", new Integer(9));// 戒指
		_armorTypes.put("belt", new Integer(10));// 腰帶
		_armorTypes.put("ring2", new Integer(11));// 戒指2
		_armorTypes.put("earring", new Integer(12));// 耳環
		_armorTypes.put("guarder", new Integer(13));// 臂甲
		_armorTypes.put("up1", new Integer(14)); // 副助道具
		_armorTypes.put("up2", new Integer(23));
		_armorTypes.put("gaiter", new Integer(16));// 脛甲
		_armorTypes.put("down1", new Integer(17));
		_armorTypes.put("down2", new Integer(18));
		_armorTypes.put("down3", new Integer(19));

		
		// ADD
		_armorTypes.put("armor1", new Integer(20)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor2", new Integer(21)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor3", new Integer(22)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor4", new Integer(24)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor5", new Integer(25)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor6", new Integer(26)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor7", new Integer(27)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor8", new Integer(28)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor9", new Integer(29)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor10", new Integer(30)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor11", new Integer(31)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor12", new Integer(32)); // 自訂不顯示欄位裝備
		_armorTypes.put("armor13", new Integer(33)); // 自訂不顯示欄位裝備
//		_armorTypes.put("armor16", new Integer(34)); // 使用後會出現請選擇武器
//		_armorTypes.put("armor17", new Integer(35)); // 使用後會出現請選擇武器
//		_armorTypes.put("armor18", new Integer(36)); // 使用後會出現請選擇武器
//		_armorTypes.put("armor19", new Integer(37)); // 使用後會出現請選擇武器
//		_armorTypes.put("armor20", new Integer(38)); // 使用後會出現請選擇武器

		
		_weaponTypes.put("none", new Integer(0));// 空手
		_weaponTypes.put("sword", new Integer(1));// 劍(單手)
		_weaponTypes.put("dagger", new Integer(2));// 匕首(單手)
		_weaponTypes.put("tohandsword", new Integer(3));// 雙手劍(雙手)
		_weaponTypes.put("bow", new Integer(4));// 弓(雙手)
		_weaponTypes.put("spear", new Integer(5));// 矛(雙手)
		_weaponTypes.put("blunt", new Integer(6));// 斧(單手)
		_weaponTypes.put("staff", new Integer(7));// 魔杖(單手)
		_weaponTypes.put("throwingknife", new Integer(8));// 飛刀
		_weaponTypes.put("arrow", new Integer(9));// 箭
		_weaponTypes.put("gauntlet", new Integer(10));// 鐵手甲
		_weaponTypes.put("claw", new Integer(11));// 鋼爪(雙手)
		_weaponTypes.put("edoryu", new Integer(12));// 雙刀(雙手)
		_weaponTypes.put("singlebow", new Integer(13));// 弓(單手)
		_weaponTypes.put("singlespear", new Integer(14));// 矛(單手)
		_weaponTypes.put("tohandblunt", new Integer(15));// 雙手斧(雙手)
		_weaponTypes.put("tohandstaff", new Integer(16));// 魔杖(雙手)
		_weaponTypes.put("kiringku", new Integer(17));// 奇古獸(單手)
		_weaponTypes.put("chainsword", new Integer(18));// 鎖鏈劍(單手)

		_weaponId.put("sword", new Integer(4));// 劍
		_weaponId.put("dagger", new Integer(46));// 匕首
		_weaponId.put("tohandsword", new Integer(50));// 雙手劍
		_weaponId.put("bow", new Integer(20));// 弓
		_weaponId.put("blunt", new Integer(11));// 斧(單手)
		_weaponId.put("spear", new Integer(24));// 矛(雙手)
		_weaponId.put("staff", new Integer(40));// 魔杖
		_weaponId.put("throwingknife", new Integer(2922));// 飛刀
		_weaponId.put("arrow", new Integer(66));// 箭
		_weaponId.put("gauntlet", new Integer(62));// 鐵手甲
		_weaponId.put("claw", new Integer(58));// 鋼爪
		_weaponId.put("edoryu", new Integer(54));// 雙刀
		_weaponId.put("singlebow", new Integer(20));// 弓(單手)
		_weaponId.put("singlespear", new Integer(24));// 矛(單手)
		_weaponId.put("tohandblunt", new Integer(11));// 雙手斧
		_weaponId.put("tohandstaff", new Integer(40));// 魔杖(雙手)
		_weaponId.put("kiringku", new Integer(58));// 奇古獸
		_weaponId.put("chainsword", new Integer(24));// 鎖鏈劍

		// 材質
		_materialTypes.put("none", new Integer(0));// 無
		_materialTypes.put("liquid", new Integer(1));// 憶體
		_materialTypes.put("web", new Integer(2));// 蠟
		_materialTypes.put("vegetation", new Integer(3));// 植物
		_materialTypes.put("animalmatter", new Integer(4));// 動物
		_materialTypes.put("paper", new Integer(5));// 紙
		_materialTypes.put("cloth", new Integer(6));// 布
		_materialTypes.put("leather", new Integer(7));// 皮革
		_materialTypes.put("wood", new Integer(8));// 木
		_materialTypes.put("bone", new Integer(9));// 骨頭
		_materialTypes.put("dragonscale", new Integer(10));// 龍鱗
		_materialTypes.put("iron", new Integer(11));// 鐵
		_materialTypes.put("steel", new Integer(12));// 鋼
		_materialTypes.put("copper", new Integer(13));// 銅
		_materialTypes.put("silver", new Integer(14));// 銀
		_materialTypes.put("gold", new Integer(15));// 黃金
		_materialTypes.put("platinum", new Integer(16));// 白金
		_materialTypes.put("mithril", new Integer(17));// 米索莉
		_materialTypes.put("blackmithril", new Integer(18));// 黑色米索莉
		_materialTypes.put("glass", new Integer(19));// 玻璃
		_materialTypes.put("gemstone", new Integer(20));// 寶石
		_materialTypes.put("mineral", new Integer(21));// 礦物
		_materialTypes.put("oriharukon", new Integer(22));// 奧里哈魯根
	}

	public static ItemTable get() {
		if (_instance == null) {
			_instance = new ItemTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		_etcitems = allEtcItem();
		_weapons = allWeapon();
		_armors = allArmor();
		buildFastLookupTable();
		_log.info("載入道具,武器,防具資料: " + _etcitems.size() + "+" + _weapons.size() + "+" + _armors.size() + "=" + +(_etcitems.size() + _weapons.size() + _armors.size()) + "(" + timer.get()
				+ "ms)");
	}

	/**
	 * 道具載入
	 * 
	 * @return
	 */
	private Map<Integer, L1EtcItem> allEtcItem() {
		final Map<Integer, L1EtcItem> result = new HashMap<Integer, L1EtcItem>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1EtcItem item = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `etcitem`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				item = new L1EtcItem();
				final int itemid = rs.getInt("item_id");
				item.setItemId(itemid);
				item.setName(rs.getString("name"));
				final String classname = rs.getString("classname");
				item.setClassname(classname);
				item.setNameId(rs.getString("name_id"));
				item.setType((_etcItemTypes.get(rs.getString("item_type"))).intValue());
				item.setUseType(_useTypes.get(rs.getString("use_type")).intValue());
				item.setType2(0);
				item.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());
				item.setWeight(rs.getInt("weight"));
				item.setGfxId(rs.getInt("invgfx"));
				item.setGroundGfxId(rs.getInt("grdgfx"));

				int itemDescId = rs.getInt("itemdesc_id");
				itemDescId = itemDescId <= 0 ? cdescid() : itemDescId;
				item.setItemDescId(itemDescId);

				item.setMinLevel(rs.getInt("min_lvl"));
				item.setMaxLevel(rs.getInt("max_lvl"));
				item.setBless(rs.getInt("bless"));
				item.setTradable(rs.getInt("trade") == 0 ? true : false);
				item.setCantDelete(rs.getInt("cant_delete") == 1 ? true : false);
				item.setDmgSmall(rs.getInt("dmg_small"));
				item.setDmgLarge(rs.getInt("dmg_large"));
				item.set_stackable(rs.getInt("stackable") == 1 ? true : false);
				item.setMaxChargeCount(rs.getInt("max_charge_count"));

				item.set_delayid(rs.getInt("delay_id"));
				item.set_delaytime(rs.getInt("delay_time"));
				item.set_delayEffect(rs.getInt("delay_effect"));
				item.setFoodVolume(rs.getInt("food_volume"));
				item.setToBeSavedAtOnce((rs.getInt("save_at_once") == 1) ? true : false);

				// 職業使用判斷欄位 (1王族.2騎士.4妖精.8法師.16黑妖.32龍騎.64幻術.128戰士.255共用)
				final int use_career = rs.getInt("use_career");
				item.setUseRoyal((use_career & 1) == 1 ? true : false);
				item.setUseKnight((use_career & 2) == 2 ? true : false);
				item.setUseElf((use_career & 4) == 4 ? true : false);
				item.setUseMage((use_career & 8) == 8 ? true : false);
				item.setUseDarkelf((use_career & 16) == 16 ? true : false);
				item.setUseDragonknight((use_career & 32) == 32 ? true : false);
				item.setUseIllusionist((use_career & 64) == 64 ? true : false);
				item.setUseWarrior((use_career & 128) == 128 ? true : false);

				// 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用)
				item.setCampSet(rs.getInt("use_camp"));

				// 是否不能被賣掉 by terry0412
				item.cantBeSold(rs.getBoolean("cant_be_sold"));

				// 最低使用需求 (轉生次數) by terry0412
				item.setMeteLevel(rs.getInt("MeteLevel"));

				// 最高使用需求 (轉生次數) by terry0412
				item.setMeteLevelMAX(rs.getInt("MeteLevelMAX"));

				ItemClass.get().addList(itemid, classname, 0);
				result.put(new Integer(item.getItemId()), item);

				item.setMaxUseTime(rs.getInt("max_use_time"));

				// 160707 道具顯示內容 erics4179
				item.set_itemstrings(rs.getString("item_string"));
				item.set_itemstrings2(rs.getString("item_string2"));
				item.set_itemstrings3(rs.getString("item_string3"));
				item.set_itemstrings4(rs.getString("item_string4"));
			}

		} catch (final NullPointerException e) {
			_log.error("加載失敗: " + item.getItemId(), e);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	/**
	 * 武器載入
	 * 
	 * @return
	 */
	private Map<Integer, L1Weapon> allWeapon() {
		final Map<Integer, L1Weapon> result = new HashMap<Integer, L1Weapon>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1Weapon weapon = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `weapon`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				weapon = new L1Weapon();
				final int itemid = rs.getInt("item_id");
				weapon.setItemId(itemid);
				weapon.setName(rs.getString("name"));
				final String classname = rs.getString("classname");
				weapon.setClassname(classname);
				weapon.setNameId(rs.getString("name_id"));
				weapon.setType((_weaponTypes.get(rs.getString("type"))).intValue());
				weapon.setType1((_weaponId.get(rs.getString("type"))).intValue());
				weapon.setType2(1);
				weapon.setUseType(1);
				weapon.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());
				weapon.setWeight(rs.getInt("weight"));
				weapon.setGfxId(rs.getInt("invgfx"));
				weapon.setGroundGfxId(rs.getInt("grdgfx"));

				int itemDescId = rs.getInt("itemdesc_id");
				itemDescId = itemDescId <= 0 ? cdescid() : itemDescId;
				weapon.setItemDescId(itemDescId);

				weapon.setDmgSmall(rs.getInt("dmg_small"));
				weapon.setDmgLarge(rs.getInt("dmg_large"));
				weapon.setRange(rs.getInt("range"));
				weapon.set_safeenchant(rs.getInt("safenchant"));
				weapon.setUseRoyal(rs.getInt("use_royal") == 0 ? false : true);
				weapon.setUseKnight(rs.getInt("use_knight") == 0 ? false : true);
				weapon.setUseElf(rs.getInt("use_elf") == 0 ? false : true);
				weapon.setUseMage(rs.getInt("use_mage") == 0 ? false : true);
				weapon.setUseDarkelf(rs.getInt("use_darkelf") == 0 ? false : true);
				weapon.setUseDragonknight(rs.getInt("use_dragonknight") == 0 ? false : true);
				weapon.setUseIllusionist(rs.getInt("use_illusionist") == 0 ? false : true);
				weapon.setUseWarrior(rs.getInt("use_warrior") == 0 ? false : true);
				weapon.setHitModifier(rs.getInt("hitmodifier"));
				weapon.setDmgModifier(rs.getInt("dmgmodifier"));
				weapon.set_addstr(rs.getByte("add_str"));
				weapon.set_adddex(rs.getByte("add_dex"));
				weapon.set_addcon(rs.getByte("add_con"));
				weapon.set_addint(rs.getByte("add_int"));
				weapon.set_addwis(rs.getByte("add_wis"));
				weapon.set_addcha(rs.getByte("add_cha"));
				weapon.set_addhp(rs.getInt("add_hp"));
				weapon.set_addmp(rs.getInt("add_mp"));
				weapon.set_addhpr(rs.getInt("add_hpr"));
				weapon.set_addmpr(rs.getInt("add_mpr"));
				weapon.set_addsp(rs.getInt("add_sp"));
				weapon.set_mdef(rs.getInt("m_def"));
				weapon.setDoubleDmgChance(rs.getInt("double_dmg_chance"));
				weapon.setMagicDmgModifier(rs.getInt("magicdmgmodifier"));
				weapon.set_canbedmg(rs.getInt("canbedmg"));
				weapon.setMinLevel(rs.getInt("min_lvl"));
				weapon.setMaxLevel(rs.getInt("max_lvl"));
				weapon.setBless(rs.getInt("bless"));
				weapon.setTradable(rs.getInt("trade") == 0 ? true : false);
				weapon.setCantDelete(rs.getInt("cant_delete") == 1 ? true : false);
				weapon.setHasteItem(rs.getInt("haste_item") == 0 ? false : true);
				weapon.setMaxUseTime(rs.getInt("max_use_time"));
				weapon.setExpPoint(rs.getInt("exp_point"));

				// 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用)
				weapon.setCampSet(rs.getInt("use_camp"));

				// 是否不能被賣掉 by terry0412
				weapon.cantBeSold(rs.getBoolean("cant_be_sold"));

				// 最低使用需求 (轉生次數) by terry0412
				weapon.setMeteLevel(rs.getInt("MeteLevel"));

				// 最高使用需求 (轉生次數) by terry0412
				weapon.setMeteLevelMAX(rs.getInt("MeteLevelMAX"));

				weapon.setInfluenceMr(rs.getInt("influence_mr"));
				weapon.setInfluenceSp(rs.getInt("influence_sp"));
				weapon.setInfluenceHp(rs.getInt("influence_hp"));
				weapon.setInfluenceMp(rs.getInt("influence_mp"));
				weapon.setInfluenceDmgR(rs.getInt("influence_dmgR"));
				weapon.setInfluenceHitAndDmg(rs.getInt("influence_hitAndDmg"));
				weapon.setInfluenceBowHitAndDmg(rs.getInt("influence_bowHitAndDmg"));

				// 170105 武器資料表自定義顯示內容 erics4179
				weapon.set_itemstrings(rs.getString("item_string"));
				weapon.set_itemstrings2(rs.getString("item_string2"));

				ItemClass.get().addList(itemid, classname, 1);
				result.put(new Integer(weapon.getItemId()), weapon);
			}

		} catch (final NullPointerException e) {
			_log.error("加載失敗: " + weapon.getItemId(), e);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	public static void init() {
		_instance = new ItemTable();
	}

	/**
	 * 防具載入
	 * 
	 * @return
	 */
	private Map<Integer, L1Armor> allArmor() {
		final Map<Integer, L1Armor> result = new HashMap<Integer, L1Armor>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1Armor armor = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `armor`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				armor = new L1Armor();
				final int itemid = rs.getInt("item_id");
				armor.setItemId(itemid);
				armor.setName(rs.getString("name"));
				final String classname = rs.getString("classname");
				armor.setClassname(classname);
				armor.setNameId(rs.getString("name_id"));
				armor.setType((_armorTypes.get(rs.getString("type"))).intValue());
				armor.setType2(2);
				armor.setUseType((_useTypes.get(rs.getString("type"))).intValue());
				armor.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());
				armor.setWeight(rs.getInt("weight"));
				armor.setGfxId(rs.getInt("invgfx"));
				armor.setGroundGfxId(rs.getInt("grdgfx"));

				int itemDescId = rs.getInt("itemdesc_id");
				itemDescId = itemDescId <= 0 ? cdescid() : itemDescId;
				armor.setItemDescId(itemDescId);

				armor.set_ac(rs.getInt("ac"));
				armor.set_safeenchant(rs.getInt("safenchant"));
				armor.setUseRoyal(rs.getInt("use_royal") == 0 ? false : true);
				armor.setUseKnight(rs.getInt("use_knight") == 0 ? false : true);
				armor.setUseElf(rs.getInt("use_elf") == 0 ? false : true);
				armor.setUseMage(rs.getInt("use_mage") == 0 ? false : true);
				armor.setUseDarkelf(rs.getInt("use_darkelf") == 0 ? false : true);
				armor.setUseDragonknight(rs.getInt("use_dragonknight") == 0 ? false : true);
				armor.setUseIllusionist(rs.getInt("use_illusionist") == 0 ? false : true);
				armor.setUseWarrior(rs.getInt("use_warrior") == 0 ? false : true);
				armor.set_addstr(rs.getByte("add_str"));
				armor.set_addcon(rs.getByte("add_con"));
				armor.set_adddex(rs.getByte("add_dex"));
				armor.set_addint(rs.getByte("add_int"));
				armor.set_addwis(rs.getByte("add_wis"));
				armor.set_addcha(rs.getByte("add_cha"));
				armor.set_addhp(rs.getInt("add_hp"));
				armor.set_addmp(rs.getInt("add_mp"));
				armor.set_addhpr(rs.getInt("add_hpr"));
				armor.set_addmpr(rs.getInt("add_mpr"));
				armor.set_addsp(rs.getInt("add_sp"));
				armor.setMinLevel(rs.getInt("min_lvl"));
				armor.setMaxLevel(rs.getInt("max_lvl"));
				armor.set_mdef(rs.getInt("m_def"));
				armor.setDamageReduction(rs.getInt("damage_reduction"));
				armor.setWeightReduction(rs.getInt("weight_reduction"));
				armor.setHitModifierByArmor(rs.getInt("hit_modifier"));
				armor.setDmgModifierByArmor(rs.getInt("dmg_modifier"));
				armor.setBowHitModifierByArmor(rs.getInt("bow_hit_modifier"));
				armor.setBowDmgModifierByArmor(rs.getInt("bow_dmg_modifier"));
				armor.setHasteItem(rs.getInt("haste_item") == 0 ? false : true);
				armor.setBless(rs.getInt("bless"));
				armor.setTradable(rs.getInt("trade") == 0 ? true : false);
				armor.setCantDelete(rs.getInt("cant_delete") == 1 ? true : false);
				armor.set_defense_earth(rs.getInt("defense_earth"));
				armor.set_defense_water(rs.getInt("defense_water"));
				armor.set_defense_wind(rs.getInt("defense_wind"));
				armor.set_defense_fire(rs.getInt("defense_fire"));
				armor.set_regist_stun(rs.getInt("regist_stun"));
				armor.set_regist_stone(rs.getInt("regist_stone"));
				armor.set_regist_sleep(rs.getInt("regist_sleep"));
				armor.set_regist_freeze(rs.getInt("regist_freeze"));
				armor.set_regist_sustain(rs.getInt("regist_sustain"));
				armor.set_regist_blind(rs.getInt("regist_blind"));
				armor.setMaxUseTime(rs.getInt("max_use_time"));
				armor.set_greater(rs.getInt("greater"));
				armor.setExpPoint(rs.getInt("exp_point"));

				// 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用)
				armor.setCampSet(rs.getInt("use_camp"));

				// 是否不能被賣掉 by terry0412
				armor.cantBeSold(rs.getBoolean("cant_be_sold"));

				// 最低使用需求 (轉生次數) by terry0412
				armor.setMeteLevel(rs.getInt("MeteLevel"));

				// 最高使用需求 (轉生次數) by terry0412
				armor.setMeteLevelMAX(rs.getInt("MeteLevelMAX"));

				armor.setInfluenceMr(rs.getInt("influence_mr"));
				armor.setInfluenceSp(rs.getInt("influence_sp"));
				armor.setInfluenceHp(rs.getInt("influence_hp"));
				armor.setInfluenceMp(rs.getInt("influence_mp"));
				armor.setInfluenceDmgR(rs.getInt("influence_dmgR"));
				armor.setInfluenceHitAndDmg(rs.getInt("influence_hitAndDmg"));
				armor.setInfluenceBowHitAndDmg(rs.getInt("influence_bowHitAndDmg"));
				armor.setInfluenceLuck(rs.getInt("influence_luck"));

				armor.setActivity(rs.getBoolean("is_activity"));

				ItemClass.get().addList(itemid, classname, 2);
				result.put(new Integer(armor.getItemId()), armor);
			}

		} catch (final NullPointerException e) {
			_log.error("加載失敗: " + armor.getItemId(), e);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	private void buildFastLookupTable() {
		int highestId = 0;

		final Collection<L1EtcItem> items = _etcitems.values();
		for (final L1EtcItem item : items) {
			if (item.getItemId() > highestId) {
				highestId = item.getItemId();
			}
		}

		final Collection<L1Weapon> weapons = _weapons.values();
		for (final L1Weapon weapon : weapons) {
			if (weapon.getItemId() > highestId) {
				highestId = weapon.getItemId();
			}
		}

		final Collection<L1Armor> armors = _armors.values();
		for (final L1Armor armor : armors) {
			if (armor.getItemId() > highestId) {
				highestId = armor.getItemId();
			}
		}

		_allTemplates = new L1Item[highestId + 1];

		for (final Iterator<Integer> iter = _etcitems.keySet().iterator(); iter.hasNext();) {
			final Integer id = iter.next();
			final L1EtcItem item = _etcitems.get(id);
			_allTemplates[id.intValue()] = item;
		}

		for (final Iterator<Integer> iter = _weapons.keySet().iterator(); iter.hasNext();) {
			final Integer id = iter.next();
			final L1Weapon item = _weapons.get(id);
			_allTemplates[id.intValue()] = item;
		}

		for (final Iterator<Integer> iter = _armors.keySet().iterator(); iter.hasNext();) {
			final Integer id = iter.next();
			final L1Armor item = _armors.get(id);
			_allTemplates[id.intValue()] = item;
		}
	}

	/**
	 * 具有套裝設置的物件 加入效果數字陣列
	 */
	public void se_mode() {
		final PerformanceTimer timer = new PerformanceTimer();
		for (final L1Item item : _allTemplates) {
			if (item != null) {
				for (final Integer key : ArmorSet.getAllSet().keySet()) {
					// 套裝資料
					final ArmorSet armorSet = ArmorSet.getAllSet().get(key);
					// 套裝中組件
					if (armorSet.isPartOfSet(item.getItemId())) {
						item.set_mode(armorSet.get_mode());
					}
				}
			}
		}
		_log.info("載入套裝效果數字陣列: " + timer.get() + "ms)");
	}

	/**
	 * 傳回指定編號物品資料
	 * 
	 * @param id
	 * @return
	 */
	public L1Item getTemplate(final int id) {
		try {
			return _allTemplates[id];

		} catch (final Exception e) {
		}
		return null;
	}

	/**
	 * 傳回指定名稱物品資料
	 * 
	 * @param nameid
	 * @return
	 */
	public L1Item getTemplate(final String nameid) {
		for (final L1Item item : _allTemplates) {
			if ((item != null) && item.getNameId().equals(nameid)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 產生新物件
	 * 
	 * @param itemId
	 * @return
	 */
	public L1ItemInstance createItem(final int itemId) {
		final L1Item temp = this.getTemplate(itemId);
		if (temp == null) {
			return null;
		}
		final L1ItemInstance item = new L1ItemInstance();
		item.setId(IdFactory.get().nextId());
		item.setItem(temp);
		item.setBless(temp.getBless());

		World.get().storeObject(item);
		return item;
	}

	/**
	 * 依名稱(NameId)找回itemid
	 * 
	 * @param name
	 * @return
	 */
	public int findItemIdByName(final String name) {
		int itemid = 0;
		for (final L1Item item : _allTemplates) {
			if ((item != null) && item.getNameId().equals(name)) {
				itemid = item.getItemId();
				break;
			}
		}
		return itemid;
	}

	/**
	 * 依名稱(中文)找回itemid
	 * 
	 * @param name
	 * @return
	 */
	public int findItemIdByNameWithoutSpace(final String name) {
		int itemid = 0;
		for (final L1Item item : _allTemplates) {
			if ((item != null) && item.getNameId().replace(" ", "").equals(name)) {
				itemid = item.getItemId();
				break;
			}
		}
		return itemid;
	}

	public L1ItemInstance createItem(final int itemId, final boolean flag) {
		final L1Item temp = this.getTemplate(itemId);
		if (temp == null) {
			return null;
		}
		final L1ItemInstance item = new L1ItemInstance();
		item.setItem(temp);
		if (flag) {
			item.setId(IdFactory.get().nextId());
			item.setBless(temp.getBless());
			World.get().storeObject(item);
		}
		return item;
	}

	public String getItemName(final int itemid) {
		final L1Item temp = this.getTemplate(itemid);
		if (temp == null) {
			return "empty";
		}
		return temp.getName();
	}

	// XXX l1j-tw create new item
	public static L1ItemInstance createNewItemGround(final L1Character target, final int item_id, final int count) {
		return createNewItem(target, item_id, count, 0, null, false, true, 0, 1, true);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count) {
		return createNewItem(pc, item_id, count, 0, null, false, false, 0, 1, true);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count, final boolean isIdentified) {
		return createNewItem(pc, item_id, count, 0, null, isIdentified, false, 0, 1, true);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count, final int enchant, final int bless, final boolean isIdentified) {
		return createNewItem(pc, item_id, count, enchant, null, isIdentified, false, 0, bless, true);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count, final int enchant, final int bless, final boolean isIdentified, final int useDay) {
		return createNewItem(pc, item_id, count, enchant, null, isIdentified, false, useDay, bless, true);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count, final int enchant) {
		return createNewItem(pc, item_id, count, enchant, null, false, false, 0, 1, true);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count, final int enchant, final boolean showMessage) {
		return createNewItem(pc, item_id, count, enchant, null, false, false, 0, 1, showMessage);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count, final int enchant, final boolean isIdentified, final boolean showMessage) {
		return createNewItem(pc, item_id, count, enchant, null, isIdentified, false, 0, 1, showMessage);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count, final int enchant, final int useDay) {
		return createNewItem(pc, item_id, count, enchant, null, false, false, useDay, 1, true);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count, final String name) {
		return createNewItem(pc, item_id, count, 0, name, false, false, 0, 1, true);
	}

	public static L1ItemInstance createNewItem(final L1PcInstance pc, final int item_id, final int count, final int enchant, final String name) {
		return createNewItem(pc, item_id, count, enchant, name, false, false, 0, 1, true);
	}

	private static L1ItemInstance createNewItem(final L1Character target, final int item_id, final int count, final int enchant, final String npcName, final boolean isIdentified,
			final boolean isGround, final int useDay, final int bless, final boolean isMsg) {

		final L1ItemInstance item = ItemTable.get().createItem(item_id);

		if (item == null) {
			// log.log(Level.SEVERE, "ItemTable createNewItem item_id= [" + item_id + "] is null");
			_log.error("ItemTable createNewItem item_id= [" + item_id + "] is null");
			return null;
		}

		if (item.isStackable()) {
			item.setCount(count);
			item.setEnchantLevel(enchant);
			item.setIdentified(isIdentified);
			if (bless != 1) {
				item.setBless(bless);
			}
			// item.setItemEffectLoad();
			if (isGround) {
				World.get().getInventory(target.getX(), target.getY(), target.getMapId()).storeItem(item);
			} else if (target instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) target;
				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
					pc.getInventory().storeItem(item);

				} else {
					World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
				}
			}
		} else {
			for (int i = 0; i < count; i++) {
				L1ItemInstance each_item;
				if (i == 0) {
					each_item = item; // 回传第一个
				} else {
					each_item = ItemTable.get().createItem(item_id);
				}

				// XXX 中古商
				if (enchant == -1) {
					int rnd_enchant = 0;
					final int chance = Random.nextInt(100) + 1;
					if (chance <= 15) {
						rnd_enchant = -2;
					} else if ((chance >= 16) && (chance <= 30)) {
						rnd_enchant = -1;
					} else if ((chance >= 31) && (chance <= 70)) {
						rnd_enchant = 0;
					} else if ((chance >= 71) && (chance <= 87)) {
						rnd_enchant = Random.nextInt(2) + 1;
					} else if ((chance >= 88) && (chance <= 97)) {
						rnd_enchant = Random.nextInt(3) + 3;
					} else if ((chance >= 98) && (chance <= 99)) {
						rnd_enchant = 6;
					} else if (chance == 100) {
						rnd_enchant = 7;
					}
					each_item.setEnchantLevel(rnd_enchant);
				} else {
					each_item.setEnchantLevel(enchant);
				}

				each_item.setIdentified(isIdentified);

				if (bless != 1) {
					each_item.setBless(bless);
				}
				/*
				 * each_item.setItemEffectLoad();
				 * 
				 * // XXX 特例... 一拳手套(1小时) if (each_item.getItemId() == 413) { each_item.setLimitTime(new Timestamp(System.currentTimeMillis() + 60 * 60 * 1000L)); }
				 * 
				 * // has use time limit if (useDay > 0) { final Timestamp limit = new Timestamp(System.currentTimeMillis() + useDay * 24 * 60 * 60 * 1000L); each_item.setLimitTime(limit);
				 * }
				 */

				if (isGround) {
					World.get().getInventory(target.getX(), target.getY(), target.getMapId()).storeItem(each_item);
				} else if (target instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) target;
					if (pc.getInventory().checkAddItem(each_item, count) == L1Inventory.OK) {
						pc.getInventory().storeItem(each_item);
					} else {
						World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(each_item);
					}
				}
			}
		}
		if (target instanceof L1PcInstance && isMsg) {
			final L1PcInstance pc = (L1PcInstance) target;
			if (npcName != null) {
				pc.sendPackets(new S_ServerMessage(143, npcName, item.getLogName())); // ...给你
			} else {
				pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 获得
			}

		}
		return item;
	}
	// XXX l1j-tw create new item -> end

}
