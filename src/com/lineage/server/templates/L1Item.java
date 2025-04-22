package com.lineage.server.templates;

import java.io.Serializable;

public abstract class L1Item implements Serializable {

	private static final long serialVersionUID = 1L;

	public L1Item() {
	}

	// TODO L1EtcItem,L1Weapon,L1Armor 共用項目

	// 160707 道具顯示內容 erics4179 START
	private String _itemstrings;

	public String get_itemstrings() {
		return _itemstrings;
	}

	public void set_itemstrings(final String itemstrings) {
		_itemstrings = itemstrings;
	}

	private String _itemstrings2;

	public String get_itemstrings2() {
		return _itemstrings2;
	}

	public void set_itemstrings2(final String itemstrings2) {
		_itemstrings2 = itemstrings2;
	}

	private String _itemstrings3;

	public String get_itemstrings3() {
		return _itemstrings3;
	}

	public void set_itemstrings3(final String itemstrings3) {
		_itemstrings3 = itemstrings3;
	}

	private String _itemstrings4;

	public String get_itemstrings4() {
		return _itemstrings4;
	}

	public void set_itemstrings4(final String itemstrings4) {
		_itemstrings4 = itemstrings4;
	}

	// 道具顯示內容END

	private int _type2; // ● 0=L1EtcItem, 1=L1Weapon, 2=L1Armor

	/**
	 * @return 0 if L1EtcItem, 1 if L1Weapon, 2 if L1Armor
	 */
	public int getType2() {
		return _type2;
	}

	/**
	 * 0 if L1EtcItem, 1 if L1Weapon, 2 if L1Armor
	 * 
	 * @param type
	 */
	public void setType2(final int type) {
		_type2 = type;
	}

	private int _itemId; // ● アイテムＩＤ

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(final int itemId) {
		_itemId = itemId;
	}

	private String _name; // ● アイテム名

	public String getName() {
		return _name;
	}

	public void setName(final String name) {
		_name = name;
	}

	private String _classname;

	public String getclassname() {
		return _classname;
	}

	public void setClassname(final String classname) {
		_classname = classname;
	}

	private String _nameId; // ● ネームＩＤ

	public String getNameId() {
		return _nameId;
	}

	public void setNameId(final String nameid) {
		_nameId = nameid;
	}

	private int _type; // ● 詳細なタイプ

	/**
	 * 傳回物品分類<br>
	 * 
	 * @return <p>
	 *         <font color=#ff0000>[etcitem]-道具類型</font><br>
	 *         0:arrow, 1:wand, 2:light, 3:gem, 4:totem, 5:firecracker,
	 *         6:potion, 7:food, 8:scroll, 9:questitem, 10:spellbook,
	 *         11:petitem, 12:other, 13:material, 14:event, 15:sting
	 *         </p>
	 *         <p>
	 *         <font color=#ff0000>[weapon]-武器類型</font><br>
	 *         1: sword <font color=#00800>劍(單手)</font><br>
	 *         2: dagger <font color=#00800>匕首(單手)</font><br>
	 *         3: tohandsword <font color=#00800>雙手劍(雙手)</font><br>
	 *         4: bow <font color=#00800>弓(雙手)</font><br>
	 *         5: spear <font color=#00800>矛(雙手)</font><br>
	 *         6: blunt <font color=#00800>斧(單手)</font><br>
	 *         7: staff <font color=#00800>魔杖(單手)</font><br>
	 *         8: throwingknife <font color=#00800>飛刀</font><br>
	 *         9: arrow <font color=#00800>箭</font><br>
	 *         10: gauntlet <font color=#00800>鐵手甲</font><br>
	 *         11: claw <font color=#00800>鋼爪(雙手)</font><br>
	 *         12: edoryu <font color=#00800>雙刀(雙手)</font><br>
	 *         13: singlebow <font color=#00800>弓(單手)</font><br>
	 *         14: singlespear <font color=#00800>矛(單手)</font><br>
	 *         15: tohandblunt <font color=#00800>雙手斧(雙手)</font><br>
	 *         16: tohandstaff <font color=#00800>魔杖(雙手)</font><br>
	 *         17: kiringku <font color=#00800>奇古獸(單手)</font><br>
	 *         18: chainsword <font color=#00800>鎖鏈劍(單手)</font><br>
	 *         </p>
	 *         <p>
	 *         <font color=#ff0000>[armor]-防具類型</font><br>
	 *         1: helm <font color=#00800>頭盔</font><br>
	 *         2: armor <font color=#00800>盔甲</font><br>
	 *         3: T <font color=#00800>内衣</font><br>
	 *         4: cloak <font color=#00800>斗篷</font><br>
	 *         5: glove <font color=#00800>手套</font><br>
	 *         6: boots <font color=#00800>靴子</font><br>
	 *         7: shield <font color=#00800>盾</font><br>
	 *         8: amulet <font color=#00800>项链</font><br>
	 *         9: ring <font color=#00800>戒指</font><br>
	 *         10: belt <font color=#00800>腰带</font><br>
	 *         11: ring2 <font color=#00800>戒指2</font><br>
	 *         12: earring <font color=#00800>耳环</font> <br>
	 *         13: guarder <font color=#00800>臂甲</font><br>
	 *         </p>
	 */
	public int getType() {
		return _type;
	}

	public void setType(final int type) {
		_type = type;
	}

	private int _type1; // ● タイプ

	/**
	 * 傳地封包影響外型編號<br>
	 * 
	 * @return <p>
	 *         <font color=#ff0000>[weapon]-武器類型</font><br>
	 *         sword: 4 <font color=#00800>劍</font><br>
	 *         dagger: 46 <font color=#00800>匕首</font><br>
	 *         tohandsword: 50 <font color=#00800>雙手劍</font><br>
	 *         bow: 20 <font color=#00800>弓</font><br>
	 *         blunt: 11 <font color=#00800>斧(單手)</font><br>
	 *         spear: 24 <font color=#00800>矛(雙手)</font><br>
	 *         staff: 40 <font color=#00800>魔杖</font><br>
	 *         throwingknife: 2922 <font color=#00800>飛刀</font><br>
	 *         arrow: 66 <font color=#00800>箭</font><br>
	 *         gauntlet: 62 <font color=#00800>鐵手甲</font><br>
	 *         claw: 58 <font color=#00800>鋼爪</font><br>
	 *         edoryu: 54 <font color=#00800>雙刀</font><br>
	 *         singlebow: 20 <font color=#00800>弓(單手)</font><br>
	 *         singlespear: 24 <font color=#00800>矛(單手)</font><br>
	 *         tohandblunt: 11 <font color=#00800>雙手斧</font><br>
	 *         tohandstaff: 40 <font color=#00800>魔杖(雙手)</font><br>
	 *         kiringku: 58 <font color=#00800>奇古獸</font><br>
	 *         chainsword: 24 <font color=#00800>鎖鏈劍</font><br>
	 *         </p>
	 */
	public int getType1() {
		return _type1;
	}

	public void setType1(final int type1) {
		_type1 = type1;
	}

	private int _material; // ● 素材

	/**
	 * アイテムの素材を返す
	 * 
	 * @return 0:none 1:液体 2:web 3:植物性 4:動物性 5:紙 6:布 7:皮 8:木 9:骨 10:竜の鱗 11:鉄
	 *         12:鋼鉄 13:銅 14:銀 15:金 16:プラチナ 17:ミスリル 18:ブラックミスリル 19:ガラス 20:宝石
	 *         21:鉱物 22:オリハルコン
	 */
	public int getMaterial() {
		return _material;
	}

	public void setMaterial(final int material) {
		_material = material;
	}

	private int _weight; // ● 重量

	public int getWeight() {
		return _weight;
	}

	public void setWeight(final int weight) {
		_weight = weight;
	}

	private int _gfxId; // ● インベントリ内のグラフィックＩＤ

	public int getGfxId() {
		return _gfxId;
	}

	public void setGfxId(final int gfxId) {
		_gfxId = gfxId;
	}

	private int _groundGfxId; // ● 地面に置いた時のグラフィックＩＤ

	public int getGroundGfxId() {
		return _groundGfxId;
	}

	public void setGroundGfxId(final int groundGfxId) {
		_groundGfxId = groundGfxId;
	}

	private int _minLevel; // ● 使用、装備可能最小ＬＶ

	private int _itemDescId;

	/**
	 * 鑑定時に表示されるItemDesc.tblのメッセージIDを返す。
	 */
	public int getItemDescId() {
		return _itemDescId;
	}

	public void setItemDescId(final int descId) {
		_itemDescId = descId;
	}

	public int getMinLevel() {
		return _minLevel;
	}

	public void setMinLevel(final int level) {
		_minLevel = level;
	}

	private int _maxLevel; // ● 使用、装備可能最大ＬＶ

	public int getMaxLevel() {
		return _maxLevel;
	}

	public void setMaxLevel(final int maxlvl) {
		_maxLevel = maxlvl;
	}

	private int _bless; // ● 祝福状態

	/**
	 * 屬性
	 * 
	 * @return 0:祝福 1:一般 2:詛咒
	 */
	public int getBless() {
		return _bless;
	}

	public void setBless(final int i) {
		_bless = i;
	}

	private boolean _tradable; // ● トレード可／不可

	/**
	 * 轉移
	 * 
	 * @return true:可以 false:不可以
	 */
	public boolean isTradable() {
		return _tradable;
	}

	public void setTradable(final boolean flag) {
		_tradable = flag;
	}

	private boolean _cantDelete; // ● 削除不可

	/**
	 * 刪除
	 * 
	 * @return true:可以 false:不可以
	 */
	public boolean isCantDelete() {
		return _cantDelete;
	}

	public void setCantDelete(final boolean flag) {
		_cantDelete = flag;
	}

	private boolean _save_at_once;

	/**
	 * 數量變化儲存檔案
	 */
	public boolean isToBeSavedAtOnce() {
		return _save_at_once;
	}

	/**
	 * 數量變化儲存檔案
	 * 
	 * @param flag
	 */
	public void setToBeSavedAtOnce(final boolean flag) {
		_save_at_once = flag;
	}

	private int _maxUseTime = 0; // 物品可使用時間

	/**
	 * 物品可使用時間(能持有的時間)
	 * 
	 * @return
	 */
	public int getMaxUseTime() {
		return _maxUseTime;
	}

	/**
	 * 物品可使用時間(能持有的時間)
	 * 
	 * @param i
	 */
	public void setMaxUseTime(final int i) {
		_maxUseTime = i;
	}

	private int _foodVolume;// 食品類道具飽食度

	/**
	 * 食品類道具飽食度
	 */
	public int getFoodVolume() {
		return _foodVolume;
	}

	/**
	 * 食品類道具飽食度
	 * 
	 * @param volume
	 */
	public void setFoodVolume(final int volume) {
		_foodVolume = volume;
	}

	/**
	 * 照明道具亮度的設置
	 * 
	 * @return
	 */
	public int getLightRange() {
		int light = 0x00;
		switch (_itemId) {
		case 40001: // 燈
			light = 0x0b;
			break;

		case 40002: // 燈籠
			light = 0x0e;
			break;

		case 40004: // 魔法燈籠
			light = 0x16;
			break;

		case 40005: // 蠟燭
			light = 0x08;
			break;
		}
		return light;
	}

	/**
	 * 照明道具可用時間設置
	 */
	public int getLightFuel() {
		int time = 0x00;
		switch (_itemId) {
		case 40001: // 燈
			time = 6000;
			break;

		case 40002: // 燈籠
			time = 12000;
			break;

		case 40003: // 燈油
			time = 12000;
			break;

		case 40004: // 魔法燈籠
			time = 0;
			break;

		case 40005: // 蠟燭
			time = 600;
			break;

		}
		return time;
	}

	private int _useType;// 物品使用封包類型

	/**
	 * 物品使用封包類型
	 * 
	 * @return <p>
	 *         petitem: -14 <font color=#00800>能量石</font><br>
	 *         petitem: -13 <font color=#00800>強化石</font><br>
	 *         petitem: -12 <font color=#00800>寵物道具</font><br>
	 *         other: -11 <font color=#00800>对读取方法调用无法分类的物品</font><br>
	 *         power: -10 <font color=#00800>加速药水</font><br>
	 *         book: -9 <font color=#00800>技术书</font><br>
	 *         makecooking: -8 <font color=#00800>料理书</font><br>
	 *         hpr: -7 <font color=#00800>增HP道具</font><br>
	 *         mpr: -6 <font color=#00800>增MP道具</font><br>
	 *         ticket: -5 <font color=#00800>食人妖精競賽票/死亡競賽票/彩票</font><br>
	 *         petcollar: -4 <font color=#00800>項圈</font><br>
	 *         sting: -3 <font color=#00800>飛刀</font><br>
	 *         arrow: -2 <font color=#00800>箭</font><br>
	 *         none: -1 <font color=#00800>無法使用(材料等)</font><br>
	 *         normal: 0 <font color=#00800>一般物品</font><br>
	 *         weapon: 1 <font color=#00800>武器</font><br>
	 *         armor: 2 <font color=#00800>盔甲</font><br>
	 *         spell_1: 3 <font color=#00800>創造怪物魔杖(無須選取目標 -
	 *         無數量:沒有任何事情發生)</font><br>
	 *         guarder: 4 <font color=#808080>希望魔杖 --- 未使用</font><br>
	 *         spell_long: 5 <font color=#00800>魔杖類型(須選取目標/座標)</font><br>
	 *         ntele: 6 <font color=#00800>瞬間移動卷軸</font><br>
	 *         identify: 7 <font color=#00800>鑑定卷軸</font><br>
	 *         res: 8 <font color=#00800>復活卷軸</font><br>
	 *         home: 9 <font color=#00800>傳送回家的卷軸</font><br>
	 *         light: 10 <font color=#00800>照明道具</font><br>
	 *         guarder: 11 <font color=#808080>未分類的卷軸 --- 未使用</font><br>
	 *         letter: 12 <font color=#00800>信紙</font><br>
	 *         letter_card: 13 <font color=#00800>信紙(寄出)</font><br>
	 *         choice: 14 <font color=#00800>請選擇一個物品(道具欄位)</font><br>
	 *         instrument: 15 <font color=#00800>哨子</font><br>
	 *         sosc: 16 <font color=#00800>變形卷軸</font><br>
	 *         spell_short: 17 <font color=#00800>選取目標 (近距離)</font><br>
	 *         T: 18 <font color=#00800>T恤</font><br>
	 *         cloak: 19 <font color=#00800>斗篷</font><br>
	 *         glove: 20 <font color=#00800>手套</font><br>
	 *         boots: 21 <font color=#00800>靴</font><br>
	 *         helm: 22 <font color=#00800>頭盔</font><br>
	 *         ring: 23 <font color=#00800>戒指</font><br>
	 *         amulet: 24 <font color=#00800>項鍊</font><br>
	 *         shield: 25 <font color=#00800>盾牌</font><br>
	 *         guarder: 25 <font color=#00800>臂甲</font><br>
	 *         dai: 26 <font color=#00800>對武器施法的卷軸</font><br>
	 *         zel: 27 <font color=#00800>對盔甲施法的卷軸</font><br>
	 *         blank: 28 <font color=#00800>空的魔法卷軸</font><br>
	 *         btele: 29 <font color=#00800>瞬間移動卷軸(祝福)</font><br>
	 *         spell_buff: 30 <font color=#00800>選取目標 (對NPC需要Ctrl 遠距離
	 *         無XY座標傳回)</font><br>
	 *         ccard: 31 <font color=#00800>聖誕卡片</font><br>
	 *         ccard_w: 32 <font color=#00800>聖誕卡片(寄出)</font><br>
	 *         vcard: 33 <font color=#00800>情人節卡片</font><br>
	 *         vcard_w: 34 <font color=#00800>情人節卡片(寄出)</font><br>
	 *         wcard: 35 <font color=#00800>白色情人節卡片</font><br>
	 *         wcard_w: 36 <font color=#00800>白色情人節卡片(寄出)</font><br>
	 *         belt: 37 <font color=#00800>腰帶</font><br>
	 *         food: 38 <font color=#00800>食物</font><br>
	 *         spell_long2: 39 <font color=#00800>選取目標 (遠距離)</font><br>
	 *         earring: 40 <font color=#00800>耳環</font><br>
	 *         fishing_rod: 42 <font color=#00800>釣魚杆</font><br>
	 *         aidr: 43 <font color=#00800>輔助右</font><br>
	 *         aidl: 44 <font color=#00800>輔助左</font><br>
	 *         aidm: 45 <font color=#00800>輔助中 </font><br>
	 *         enc: 46 <font color=#ff0000>飾品強化捲軸</font><br>
	 *         aidl2: 47 <font color=#00800>輔助左下</font><br>
	 *         aidr2: 48 <font color=#00800>輔助右下</font><br>
	 *         choice_doll: 55 <font color=#ff0000>請選擇魔法娃娃</font><br>
	 *         </p>
	 */
	public int getUseType() {
		return _useType;
	}

	/**
	 * 物品使用封包類型
	 * 
	 * @param useType
	 */
	public void setUseType(int useType) {
		if (getItemId() == 60440) {
			useType = 62;
		} else if (getItemId() == 60441) {
			useType = 65;
		}
		_useType = useType;
	}

	// TODO L1EtcItem,L1Weapon 共通項目

	private int _dmgSmall = 0; // ● 最小ダメージ

	public int getDmgSmall() {
		return _dmgSmall;
	}

	public void setDmgSmall(final int dmgSmall) {
		_dmgSmall = dmgSmall;
	}

	private int _dmgLarge = 0; // ● 最大ダメージ

	public int getDmgLarge() {
		return _dmgLarge;
	}

	public void setDmgLarge(final int dmgLarge) {
		_dmgLarge = dmgLarge;
	}

	// TODO L1EtcItem,L1Armor 共通項目

	// TODO L1Weapon,L1Armor 共通項目

	private int[] _mode = null; // 套裝附加的效果陣列

	/**
	 * 套裝附加的效果陣列
	 * 
	 * @return
	 */
	public int[] get_mode() {
		return _mode;
	}

	/**
	 * 套裝附加的效果陣列
	 * 
	 * @param mode
	 */
	public void set_mode(final int[] mode) {
		_mode = mode;
	}

	private int _safeEnchant = 0; // 安定值

	/**
	 * 安定值
	 * 
	 * @return
	 */
	public int get_safeenchant() {
		return _safeEnchant;
	}

	/**
	 * 安定值
	 * 
	 * @param safeenchant
	 */
	public void set_safeenchant(final int safeenchant) {
		_safeEnchant = safeenchant;
	}

	private boolean _useRoyal = false; // ● ロイヤルクラスが装備できるか

	public boolean isUseRoyal() {
		return _useRoyal;
	}

	public void setUseRoyal(final boolean flag) {
		_useRoyal = flag;
	}

	private boolean _useKnight = false; // ● ナイトクラスが装備できるか

	public boolean isUseKnight() {
		return _useKnight;
	}

	public void setUseKnight(final boolean flag) {
		_useKnight = flag;
	}

	private boolean _useElf = false; // ● エルフクラスが装備できるか

	public boolean isUseElf() {
		return _useElf;
	}

	public void setUseElf(final boolean flag) {
		_useElf = flag;
	}

	private boolean _useMage = false; // ● メイジクラスが装備できるか

	public boolean isUseMage() {
		return _useMage;
	}

	public void setUseMage(final boolean flag) {
		_useMage = flag;
	}

	private boolean _useDarkelf = false; // ● ダークエルフクラスが装備できるか

	public boolean isUseDarkelf() {
		return _useDarkelf;
	}

	public void setUseDarkelf(final boolean flag) {
		_useDarkelf = flag;
	}

	private boolean _useDragonknight = false; // ● ドラゴンナイト裝備できるか

	public boolean isUseDragonknight() {
		return _useDragonknight;
	}

	public void setUseDragonknight(final boolean flag) {
		_useDragonknight = flag;
	}

	private boolean _useIllusionist = false; // ● イリュージョニスト裝備できるか

	public boolean isUseIllusionist() {
		return _useIllusionist;
	}

	public void setUseIllusionist(final boolean flag) {
		_useIllusionist = flag;
	}

	private boolean _useWarrior = false;

	public boolean isUseWarrior() {
		return _useWarrior;
	}

	public void setUseWarrior(final boolean flag) {
		_useWarrior = flag;
	}

	private byte _addstr = 0; // ● ＳＴＲ補正

	public byte get_addstr() {
		return _addstr;
	}

	public void set_addstr(final byte addstr) {
		_addstr = addstr;
	}

	private byte _adddex = 0; // ● ＤＥＸ補正

	public byte get_adddex() {
		return _adddex;
	}

	public void set_adddex(final byte adddex) {
		_adddex = adddex;
	}

	private byte _addcon = 0; // ● ＣＯＮ補正

	public byte get_addcon() {
		return _addcon;
	}

	public void set_addcon(final byte addcon) {
		_addcon = addcon;
	}

	private byte _addint = 0; // ● ＩＮＴ補正

	public byte get_addint() {
		return _addint;
	}

	public void set_addint(final byte addint) {
		_addint = addint;
	}

	private byte _addwis = 0; // ● ＷＩＳ補正

	public byte get_addwis() {
		return _addwis;
	}

	public void set_addwis(final byte addwis) {
		_addwis = addwis;
	}

	private byte _addcha = 0; // ● ＣＨＡ補正

	public byte get_addcha() {
		return _addcha;
	}

	public void set_addcha(final byte addcha) {
		_addcha = addcha;
	}

	private int _addhp = 0; // ● ＨＰ補正

	public int get_addhp() {
		return _addhp;
	}

	public void set_addhp(final int addhp) {
		_addhp = addhp;
	}

	private int _addmp = 0; // ● ＭＰ補正

	public int get_addmp() {
		return _addmp;
	}

	public void set_addmp(final int addmp) {
		_addmp = addmp;
	}

	private int _addhpr = 0; // ● ＨＰＲ補正

	public int get_addhpr() {
		return _addhpr;
	}

	public void set_addhpr(final int addhpr) {
		_addhpr = addhpr;
	}

	private int _addmpr = 0; // ● ＭＰＲ補正

	public int get_addmpr() {
		return _addmpr;
	}

	public void set_addmpr(final int addmpr) {
		_addmpr = addmpr;
	}

	private int _addsp = 0; // ● ＳＰ補正

	public int get_addsp() {
		return _addsp;
	}

	public void set_addsp(final int addsp) {
		_addsp = addsp;
	}

	private int _mdef = 0; // 抗魔(MR)

	/**
	 * 抗魔(MR)
	 * 
	 * @return
	 */
	public int get_mdef() {
		return _mdef;
	}

	/**
	 * 抗魔(MR)
	 * 
	 * @param i
	 */
	public void set_mdef(final int i) {
		_mdef = i;
	}

	private boolean _isHasteItem = false; // 是否具有加速效果

	/**
	 * 是否具有加速效果
	 * 
	 * @return
	 */
	public boolean isHasteItem() {
		return _isHasteItem;
	}

	/**
	 * 是否具有加速效果
	 * 
	 * @param flag
	 */
	public void setHasteItem(final boolean flag) {
		_isHasteItem = flag;
	}

	// TODO L1EtcItem 專屬項目

	/**
	 * 物品可堆疊
	 * 
	 * @return true:可 false:不可
	 */
	public boolean isStackable() {
		return false;
	}

	/**
	 * 延遲編號
	 * 
	 * @return
	 */
	public int get_delayid() {
		return 0;
	}

	/**
	 * 延遲時間
	 * 
	 * @return
	 */
	public int get_delaytime() {
		return 0;
	}

	/**
	 * 最大可用次數
	 * 
	 * @return
	 */
	public int getMaxChargeCount() {
		return 0;
	}

	private int _delay_effect;// 使用時間限制

	/**
	 * 設定使用時間限制(上次使用到下次使用必須間隔時間)
	 * 
	 * @param delay_effect
	 */
	public void set_delayEffect(final int delay_effect) {
		_delay_effect = delay_effect;
	}

	/**
	 * 傳回使用時間限制(上次使用到下次使用必須間隔時間)
	 * 
	 * @return
	 */
	public int get_delayEffect() {
		return _delay_effect;
	}

	// TODO L1Weapon 專屬項目

	public int get_add_dmg() {
		return 0;
	}

	public int getRange() {
		return 0;
	}

	public int getHitModifier() {
		return 0;
	}

	public int getDmgModifier() {
		return 0;
	}

	public int getDoubleDmgChance() {
		return 0;
	}

	/**
	 * 魔法攻擊增加
	 * 
	 * @return
	 */
	public int getMagicDmgModifier() {
		return 0;
	}

	public int get_canbedmg() {
		return 0;
	}

	public boolean isTwohandedWeapon() {
		return false;
	}

	// TODO L1Armor 專屬項目

	public int get_ac() {
		return 0;
	}

	public int getDamageReduction() {
		return 0;
	}

	public int getWeightReduction() {
		return 0;
	}

	public int getHitModifierByArmor() {
		return 0;
	}

	public int getDmgModifierByArmor() {
		return 0;
	}

	public int getBowHitModifierByArmor() {
		return 0;
	}

	public int getBowDmgModifierByArmor() {
		return 0;
	}

	/**
	 * 增加水屬性
	 * 
	 * @return
	 */
	public int get_defense_water() {
		return 0;
	}

	/**
	 * 增加火屬性
	 * 
	 * @return
	 */
	public int get_defense_fire() {
		return 0;
	}

	/**
	 * 增加地屬性
	 * 
	 * @return
	 */
	public int get_defense_earth() {
		return 0;
	}

	/**
	 * 增加風屬性
	 * 
	 * @return
	 */
	public int get_defense_wind() {
		return 0;
	}

	/**
	 * 昏迷耐性
	 * 
	 * @return
	 */
	public int get_regist_stun() {
		return 0;
	}

	/**
	 * 石化耐性
	 * 
	 * @return
	 */
	public int get_regist_stone() {
		return 0;
	}

	/**
	 * 睡眠耐性
	 * 
	 * @return
	 */
	public int get_regist_sleep() {
		return 0;
	}

	/**
	 * 寒冰耐性
	 * 
	 * @return
	 */
	public int get_regist_freeze() {
		return 0;
	}

	/**
	 * 支撑耐性
	 * 
	 * @return
	 */
	public int get_regist_sustain() {
		return 0;
	}

	/**
	 * 暗黑耐性
	 * 
	 * @return
	 */
	public int get_regist_blind() {
		return 0;
	}

	/**
	 * 飾品強度
	 * 
	 * @return
	 */
	public int get_greater() {
		return 3;
	}

	// 經驗加值倍率 (XX%)
	private int _expPoint;

	public int getExpPoint() {
		return _expPoint;
	}

	public void setExpPoint(final int i) {
		_expPoint = i;
	}

	// 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用)
	private int _campSet;

	public int getCampSet() {
		return _campSet;
	}

	public void setCampSet(final int i) {
		_campSet = i;
	}

	// 是否不能被賣掉 by terry0412
	private boolean _cant_be_sold;

	public boolean cantBeSold() {
		return _cant_be_sold;
	}

	public void cantBeSold(final boolean flag) {
		_cant_be_sold = flag;
	}

	// 最低使用需求 (轉生次數) by terry0412
	private int _meteLevel;

	public int getMeteLevel() {
		return _meteLevel;
	}

	public void setMeteLevel(final int i) {
		_meteLevel = i;
	}

	// 最高使用需求 (轉生次數) by terry0412
	private int _meteLevelMAX;

	public int getMeteLevelMAX() {
		return _meteLevelMAX;
	}

	public void setMeteLevelMAX(final int i) {
		_meteLevelMAX = i;
	}

	/**
	 * 強化值影響的增減魔防值 by terry0412
	 * 
	 * @return
	 */
	public int getInfluenceMr() {
		return 0;
	}

	/**
	 * 強化值影響的增減魔攻值 by terry0412
	 * 
	 * @return
	 */
	public int getInfluenceSp() {
		return 0;
	}

	/**
	 * 強化值影響的增減HP值 by terry0412
	 * 
	 * @return
	 */
	public int getInfluenceHp() {
		return 0;
	}

	/**
	 * 強化值影響的增減MP值 by terry0412
	 * 
	 * @return
	 */
	public int getInfluenceMp() {
		return 0;
	}

	/**
	 * 強化值影響的增減傷害減免值 by terry0412
	 * 
	 * @return
	 */
	public int getInfluenceDmgR() {
		return 0;
	}

	/**
	 * 強化值影響的增減近距離命中以及近距離攻擊值 by terry0412
	 * 
	 * @return
	 */
	public int getInfluenceHitAndDmg() {
		return 0;
	}

	/**
	 * 強化值影響的增減遠距離命中以及遠距離攻擊值 by terry0412
	 * 
	 * @return
	 */
	public int getInfluenceBowHitAndDmg() {
		return 0;
	}

	/**
	 * 強化值影響的增減幸運值 by terry0412
	 * 
	 * @return
	 */
	public int getInfluenceLuck() {
		return 0;
	}

	/**
	 * 是否為活動戒指或收費戒指 by terry0412
	 * 
	 * @return
	 */
	public boolean isActivity() {
		return false;
	}
}
