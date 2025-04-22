package com.lineage.server.model.Instance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.lineage.server.datatables.ArmorSetTable;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.ExtraMagicStoneTable;
import com.lineage.server.datatables.PetItemTable;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemBuff;
import com.lineage.server.templates.L1ItemPowerHole_name;
import com.lineage.server.templates.L1MagicStone;
import com.lineage.server.templates.L1MagicWeapon;
import com.lineage.server.templates.L1PetItem;
import com.lineage.server.utils.BinaryOutputStream;

/**
 * 物品詳細資料
 * 
 * @author dexc
 */
public class L1ItemStatus {

	private final L1ItemInstance _itemInstance;

	private final L1Item _item;

	private final BinaryOutputStream _os;

	private final L1ItemPower _itemPower;

	/**
	 * 物品詳細資料
	 * 
	 * @param itemInstance
	 *            L1ItemInstance
	 */
	public L1ItemStatus(final L1ItemInstance itemInstance) {
		_itemInstance = itemInstance;
		_item = itemInstance.getItem();
		_os = new BinaryOutputStream();
		_itemPower = new L1ItemPower(_itemInstance);
	}

	/**
	 * 物品詳細資料
	 * 
	 * @param template
	 *            L1Item
	 */
	public L1ItemStatus(final L1Item template) {
		_itemInstance = new L1ItemInstance();
		_itemInstance.setItem(template);
		_item = template;
		_os = new BinaryOutputStream();
		_itemPower = new L1ItemPower(_itemInstance);
	}

	public L1ItemStatus(final L1Item template, final int enchantLevel) {
		this(template);
		_itemInstance.setEnchantLevel(enchantLevel);
	}

	public BinaryOutputStream getStatusBytes() {
		// 分類
		final int use_type = _item.getUseType();
		switch (use_type) {
		case -11: // 对读取方法调用无法分类的物品
		case -10: // 加速药水
		case -9: // 技术书
		case -8: // 料理书
		case -7: // 增HP道具
		case -6: // 增MP道具
		case -5: // 食人妖精競賽票
		case -4: // 項圈
		case -1: // 無法使用(材料等)
		case 0: // 一般物品
		case 3: // 創造怪物魔杖(無須選取目標 - 無數量:沒有任何事情發生)
		case 5: // 魔杖類型(須選取目標)
		case 6: // 瞬間移動卷軸
		case 7: // 鑑定卷軸
		case 9: // 傳送回家的卷軸
		case 8: // 復活卷軸
		case 12: // 信紙
		case 13: // 信紙(寄出)
		case 14: // 請選擇一個物品(道具欄位)
		case 15: // 哨子
		case 16: // 變形卷軸
		case 17: // 選取目標 (近距離)
		case 26: // 對武器施法的卷軸
		case 27: // 對盔甲施法的卷軸
		case 28: // 空的魔法卷軸
		case 29: // 瞬間移動卷軸(祝福)
		case 30: // 魔法卷軸選取目標 (遠距離 無XY座標傳回)
		case 31: // 聖誕卡片
		case 32: // 聖誕卡片(寄出)
		case 33: // 情人節卡片
		case 34: // 情人節卡片(寄出)
		case 35: // 白色情人節卡片
		case 36: // 白色情人節卡片(寄出)
		case 39: // 選取目標 (遠距離)
		case 42: // 釣魚杆
		case 46: // 飾品強化捲軸
		case 55: // 請選擇魔法娃娃
		case 60: // 全頻廣播器
		case 61: // 強化變身
		case 62: // 潘朵拉幸運抽獎券
		case 65: // 潘朵拉轉運抽獎券
			final String classname = _item.getclassname();
			if (classname.startsWith("shop.VIP_Card_")) {
				return etcitem_card(classname);
			}
			return etcitem();

		case -12: // 寵物用具
			final L1PetItem petItem = PetItemTable.get().getTemplate(_item.getItemId());
			// 武器
			if (petItem.isWeapom()) {
				return petweapon(petItem);
				// 防具
			} else {
				return petarmor(petItem);
			}

		case -3: // 飛刀
		case -2: // 箭
			return arrow();

		case 38: // 食物
			return fooditem();

		case 10: // 照明道具
			return lightitem();

		case 2: // 盔甲
		case 18: // T恤
		case 19: // 斗篷
		case 20: // 手套
		case 21: // 靴
		case 22: // 頭盔
		case 25: // 盾牌
		case 70: // 脛甲
			return armor();

		case 40: // 耳環
		case 23: // 戒指
		case 24: // 項鍊
		case 37: // 腰帶
			return accessories();

		case 44: // 輔助道具(1.2)
		case 43: // 輔助格子 左
		case 45: // 輔助格子 中
		case 47: // 輔助格子 右
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
			return accessories2();

		case 1: // 武器
			return weapon();
		}
		return null;
	}

	private BinaryOutputStream etcitem_card(final String classname) {
		int card_id = 0;
		try {
			final String cardmode = classname.substring(14);
			card_id = Integer.parseInt(cardmode);
		} catch (final Exception e) {
			final String cardmode = classname.substring(15);
			card_id = Integer.parseInt(cardmode);
		}
		if (card_id == 0) {
			return _os;
		}
		// 寒冰耐性
		int freeze = 0;
		// 石化耐性
		int stone = 0;
		// 睡眠耐性
		int sleep = 0;
		// 暗黑耐性
		int blind = 0;
		// 昏迷耐性
		int stun = 0;
		// 支撑耐性
		int sustain = 0;

		// 力量
		int addstr = 0;
		// 敏捷
		int adddex = 0;
		// 體質
		int addcon = 0;
		// 精神
		int addwis = 0;
		// 智力
		int addint = 0;
		// 魅力
		int addcha = 0;

		// DG(攻擊力)
		int pw_sDg = 0;
		// Hit(攻擊成功)
		int pw_sHi = 0;

		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		switch (card_id) {
		case 1:// # 44129 三國普卡：經驗質+10% 晕眩耐性+3 寒冰耐性+3 石化耐性+3 睡眠耐性+3
			msg1 = "Exp +10%";
			msg2 = "死亡不掉經驗值";
			stun = 5;// 暈眩耐性
			freeze = 5;// 寒冰耐性
			stone = 5;// 石化耐性
			sleep = 5;// 睡眠耐性
			break;

		case 2:// # 44130 三國金卡：經驗質+20% 全能力+1
			msg1 = "Exp +20%";
			msg2 = "死亡不掉積分";
			addstr = 1;
			adddex = 1;
			addcon = 1;
			addwis = 1;
			addint = 1;
			addcha = 1;
			break;

		case 3:// # 44131 三國白金卡：經驗質+30% 全能力+2
			msg1 = "Exp +30%";
			msg2 = "死亡不掉物品";
			addstr = 2;
			adddex = 2;
			addcon = 2;
			addwis = 2;
			addint = 2;
			addcha = 2;
			break;

		case 4:// # 44132 三國白金限量卡：經驗質+40% 經驗保護
			msg1 = "Exp +40%";
			msg2 = "$5539 +5";// 回血
			msg3 = "$5541 +5";// 回魔
			addstr = 3;
			adddex = 3;
			addcon = 3;
			addwis = 3;
			addint = 3;
			addcha = 3;
			break;

		case 5:// # 44133 三國獨特限量卡：經驗質+50% 物品保護 積分保護
			msg1 = "Exp +50%";
			msg2 = "$5539 +10";// 回血
			msg3 = "$5541 +10";// 回魔
			addstr = 4;
			adddex = 4;
			addcon = 4;
			addwis = 4;
			addint = 4;
			addcha = 4;
			break;
		case 6:// # 44193 VIP特卡(月卡) || 44194 VIP特卡(日卡)
				// 死亡後不會損失等級、陣營經驗、道具)
				// 狩獵經驗值+150% 全能力+5
				// 回血+10 回魔+10 暈眩.寒冰.石化.睡眠耐性+5
			msg1 = "保護(經驗.積分.道具)";
			msg2 = "Exp +150%";
			msg3 = "$5539 +10";// 回血
			msg4 = "$5541 +10";// 回魔
			addstr = 5;
			adddex = 5;
			addcon = 5;
			addwis = 5;
			addint = 5;
			addcha = 5;
			stun = 5;// 暈眩耐性
			freeze = 5;// 寒冰耐性
			stone = 5;// 石化耐性
			sleep = 5;// 睡眠耐性
			pw_sDg = 5;// DG(攻擊力)
			pw_sHi = 2;// Hit(攻擊成功)
			break;
		}

		if (msg1.length() > 0) {
			_os.writeC(0x27);
			_os.writeS(msg1);
		}
		if (msg2.length() > 0) {
			_os.writeC(0x27);
			_os.writeS(msg2);
		}
		if (msg3.length() > 0) {
			_os.writeC(0x27);
			_os.writeS(msg3);
		}
		if (msg4.length() > 0) {
			_os.writeC(0x27);
			_os.writeS(msg4);
		}

		// 力量
		if (addstr != 0) {
			_os.writeC(0x08);
			_os.writeC(addstr);
		}
		// 敏捷
		if (adddex != 0) {
			_os.writeC(0x09);
			_os.writeC(adddex);
		}
		// 體質
		if (addcon != 0) {
			_os.writeC(0x0a);
			_os.writeC(addcon);
		}
		// 精神
		if (addwis != 0) {
			_os.writeC(0x0b);
			_os.writeC(addwis);
		}
		// 智力
		if (addint != 0) {
			_os.writeC(0x0c);
			_os.writeC(addint);
		}
		// 魅力
		if (addcha != 0) {
			_os.writeC(0x0d);
			_os.writeC(addcha);
		}

		if (freeze != 0) {
			_os.writeC(0x21);
			_os.writeC(0x01);
			_os.writeH(freeze);
		}
		if (stone != 0) {
			_os.writeC(0x21);
			_os.writeC(0x02);
			_os.writeH(stone);
		}
		if (sleep != 0) {
			_os.writeC(0x21);
			_os.writeC(0x03);
			_os.writeH(sleep);
		}
		if (blind != 0) {
			_os.writeC(0x21);
			_os.writeC(0x04);
			_os.writeH(blind);
		}
		if (stun != 0) {
			_os.writeC(0x21);
			_os.writeC(0x05);
			_os.writeH(stun);
		}
		if (sustain != 0) {
			_os.writeC(0x21);
			_os.writeC(0x06);
			_os.writeH(sustain);
		}
		// 攻撃成功
		if (pw_sHi != 0) {
			_os.writeC(0x05);
			_os.writeC(pw_sHi);
		}

		// 追加打撃
		if (pw_sDg != 0) {
			_os.writeC(0x06);
			_os.writeC(pw_sDg);
		}

		return _os;
	}

	/**
	 * 道具狀態系統
	 * 
	 * @param power
	 * @return
	 */
	private BinaryOutputStream etcitem_Buff(L1ItemBuff power) {
		if (power.get_ac() != 0) { // 防禦
			_os.writeC(56);
			_os.writeC(power.get_ac());
		}
		if (power.get_str() != 0) { // 力量
			_os.writeC(8);
			_os.writeC(power.get_str());
		}
		if (power.get_dex() != 0) { // 敏捷
			_os.writeC(9);
			_os.writeC(power.get_dex());
		}
		if (power.get_con() != 0) { // 體質
			_os.writeC(10);
			_os.writeC(power.get_con());
		}
		if (power.get_wis() != 0) { // 精神
			_os.writeC(11);
			_os.writeC(power.get_wis());
		}
		if (power.get_intel() != 0) { // 智力
			_os.writeC(12);
			_os.writeC(power.get_intel());
		}
		if (power.get_cha() != 0) { // 魅力
			_os.writeC(13);
			_os.writeC(power.get_cha());
		}
		if (power.get_hp() != 0) { // 血量
			_os.writeC(14);
			_os.writeH(power.get_hp());
		}
		if (power.get_mp() != 0) { // 魔量
			_os.writeC(0x20);
			_os.writeH(power.get_mp());
		}
		if (power.get_mr() != 0) { // 魔法防禦
			_os.writeC(15);
			_os.writeH(power.get_mr());
		}
		if (power.get_sp() != 0) { // 魔攻
			_os.writeC(17);
			_os.writeC(power.get_sp());
		}
		if (power.get_dmg() != 0) { // 近戰傷害
			_os.writeC(47);
			_os.writeC(power.get_dmg());
		}
		if (power.get_bow_dmg() != 0) { // 遠攻傷害
			_os.writeC(35);
			_os.writeC(power.get_bow_dmg());
		}
		if (power.get_hit() != 0) { // 近戰命中
			_os.writeC(48);
			_os.writeC(power.get_hit());
		}
		if (power.get_bow_hit() != 0) { // 遠攻命中
			_os.writeC(24);
			_os.writeC(power.get_bow_hit());
		}
		if (power.get_dmg_r() != 0) { // 物理減傷
			_os.writeC(39);
			_os.writeS("物理減傷 +" + power.get_dmg_r());
		}
		if (power.get_magic_r() != 0) { // 魔法減傷
			_os.writeC(39);
			_os.writeS("魔法減傷 +" + power.get_magic_r());
		}
		if (power.get_fire() != 0) { // 火屬性
			_os.writeC(27);
			_os.writeC(power.get_fire());
		}
		if (power.get_water() != 0) { // 水屬性
			_os.writeC(28);
			_os.writeC(power.get_water());
		}
		if (power.get_wind() != 0) { // 風屬性
			_os.writeC(29);
			_os.writeC(power.get_wind());
		}
		if (power.get_earth() != 0) { // 地屬性
			_os.writeC(30);
			_os.writeC(power.get_earth());
		}

		if (power.get_freeze() != 0) { // 冰凍耐性
			_os.writeC(33);
			_os.writeC(1);
			_os.writeC(power.get_freeze());
		}
		if (power.get_stone() != 0) { // 石化耐性
			_os.writeC(33);
			_os.writeC(2);
			_os.writeC(power.get_stone());
		}
		if (power.get_sleep() != 0) { // 睡眠耐性
			_os.writeC(33);
			_os.writeC(3);
			_os.writeC(power.get_sleep());
		}
		if (power.get_blind() != 0) { // 暗黑耐性
			_os.writeC(33);
			_os.writeC(4);
			_os.writeC(power.get_blind());
		}
		if (power.get_stun() != 0) { // 昏迷耐性
			_os.writeC(33);
			_os.writeC(5);
			_os.writeC(power.get_stun());
		}
		if (power.get_sustain() != 0) { // 支撐耐性
			_os.writeC(33);
			_os.writeC(6);
			_os.writeC(power.get_sustain());
		}
		if (power.get_pvpdmg() != 0) { // 增加PVP傷害
			_os.writeC(59);
			_os.writeC(power.get_pvpdmg());
		}
		if (power.get_pvpdmg_r() != 0) { // 減免PVP傷害
			_os.writeC(60);
			_os.writeC(power.get_pvpdmg_r());
		}
		if (power.get_hpr() != 0) { // 體力回復量
			_os.writeC(37);
			_os.writeC(power.get_hpr());
		}
		if (power.get_mpr() != 0) { // 魔力回復量
			_os.writeC(38);
			_os.writeC(power.get_mpr());
		}
		if (power.get_exp() != 0) { // 狩獵經驗值
			if (power.get_exp() <= 120) {
				_os.writeC(36);
				_os.writeC(power.get_exp());
			} else {
				_os.writeC(0x27);
				_os.writeS("$6134 " + power.get_exp() + "%");
			}
		}
		if (power.get_buff_time() != 0) {
			_os.writeC(39);
			_os.writeS("時效 " + power.get_buff_time() + "秒");
		}

		if (power.getVipLevel() != 0) {
			_os.writeC(39);
			_os.writeS("使用VIP " + power.getVipLevel());
		}

		if (power.is_buff_save()) {
			_os.writeC(39);
			_os.writeS("重登狀態保留");
		} else {
			_os.writeC(39);
			_os.writeS("重登狀態消失");
		}

		this._os.writeC(0x17); // 材質
		this._os.writeC(this._item.getMaterial());
		this._os.writeD(this._itemInstance.getWeight());

		return _os;
	}

	/**
	 * 飛刀 箭
	 * 
	 * @return
	 */
	private BinaryOutputStream arrow() {
		_os.writeC(0x01); // 打撃値
		_os.writeC(_item.getDmgSmall());
		_os.writeC(_item.getDmgLarge());
		_os.writeC(_item.getMaterial());
		_os.writeD(_itemInstance.getWeight());
		return _os;
	}

	/**
	 * 食物
	 * 
	 * @return
	 */
	private BinaryOutputStream fooditem() {
		_os.writeC(0x15);
		// 栄養
		_os.writeH(_item.getFoodVolume());
		_os.writeC(_item.getMaterial());
		_os.writeD(_itemInstance.getWeight());
		return _os;
	}

	/**
	 * 照明道具
	 * 
	 * @return
	 */
	private BinaryOutputStream lightitem() {
		_os.writeC(0x16);
		_os.writeH(_item.getLightRange());
		_os.writeC(_item.getMaterial());
		_os.writeD(_itemInstance.getWeight());
		return _os;
	}

	/**
	 * 防具類
	 * 
	 * @return
	 */
	private BinaryOutputStream armor() {
		// AC
		_os.writeC(0x13);
		int ac = _item.get_ac();
		// pandora
		if (_itemInstance.get_pandora_mark() == 6) {
			ac -= 1;
		}
		if (ac < 0) {
			ac = Math.abs(ac);
		}
		_os.writeC(ac);

		_os.writeC(_item.getMaterial());
		_os.writeC(_item.get_greater());// CNOP修正
		_os.writeD(_itemInstance.getWeight());

		// 強化数
		if (_itemInstance.getEnchantLevel() != 0) {
			_os.writeC(0x02);
			_os.writeC(_itemInstance.getEnchantLevel());
		}
		// 損傷度
		if (_itemInstance.get_durability() != 0) {
			_os.writeC(0x03);
			_os.writeC(_itemInstance.get_durability());
		}

		int pw_s1 = _item.get_addstr();// 力量
		int pw_s2 = _item.get_adddex();// 敏捷
		int pw_s3 = _item.get_addcon();// 體質
		int pw_s4 = _item.get_addwis();// 精神
		int pw_s5 = _item.get_addint();// 智力
		int pw_s6 = _item.get_addcha();// 魅力

		// pandora
		final int pd_type = _itemInstance.get_pandora_type();
		switch (pd_type) {
		case 1:// str
			pw_s1 += 1;
			break;
		case 2:// dex
			pw_s2 += 1;
			break;
		case 3:// int
			pw_s5 += 1;
			break;
		case 4:// wis
			pw_s4 += 1;
			break;
		case 5:// con
			pw_s3 += 1;
			break;
		case 6:// cha
			pw_s6 += 1;
			break;
		}

		int pw_sHp = _itemPower.getHp();// +HP
		int pw_sMp = _itemPower.getMp();// +MP
		int pw_sMr = _itemPower.getMr();// MR(抗魔)
		int pw_sSp = _itemPower.getSp();// SP(魔攻)

		int pw_sDg = _item.getDmgModifierByArmor();// DG(攻擊力)
		int pw_sHi = _item.getHitModifierByArmor();// Hit(攻擊成功)
		int pw_sH2 = 0; // 弓命中追加
		int pw_sD2 = 0; // 弓傷害追加

		int pw_d4_1 = _item.get_defense_fire();// 火屬性
		int pw_d4_2 = _item.get_defense_water();// 水屬性
		int pw_d4_3 = _item.get_defense_wind();// 風屬性
		int pw_d4_4 = _item.get_defense_earth();// 地屬性

		int pw_k6_1 = _item.get_regist_freeze();// 寒冰耐性
		int pw_k6_2 = _item.get_regist_stone();// 石化耐性
		int pw_k6_3 = _item.get_regist_sleep();// 睡眠耐性
		int pw_k6_4 = _item.get_regist_blind();// 暗黑耐性
		int pw_k6_5 = _item.get_regist_stun();// 昏迷耐性
		int pw_k6_6 = _item.get_regist_sustain();// 支撑耐性

		int pw_k7_1 = _item.get_addhpr();// hpr
		int pw_k7_2 = _item.get_addmpr();// mpr

		// int value_1 = 0;// 物理傷害%
		// int value_2 = 0;// 魔法傷害%
		// int value_3 = 0;// 物傷減免%
		// int value_4 = 0;// 魔傷減免%
		// int value_5 = 0;// 有害魔法命中%
		// int value_6 = 0;// 有害魔法抵抗%
		// int value_7 = 0;// 物理爆擊傷害%(1.5)
		// int value_8 = 0;// 魔法爆擊傷害%(1.5)

		// 凹槽顯示 by terry0412
		if (_itemInstance.get_power_name_hole() != null) {
			final L1ItemPowerHole_name power = _itemInstance.get_power_name_hole();
			for (int i = 0, n = 5; i < n; i++) {
				L1MagicStone magicStone = null;
				switch (i) {
				case 0:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_1());
					break;
				case 1:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_2());
					break;
				case 2:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_3());
					break;
				case 3:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_4());
					break;
				case 4:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_5());
					break;
				}
				if (magicStone != null) {
					pw_s1 += magicStone.getAddStr(); // 力量
					pw_s2 += magicStone.getAddDex(); // 敏捷
					pw_s3 += magicStone.getAddCon(); // 體質
					pw_s4 += magicStone.getAddWis(); // 精神
					pw_s5 += magicStone.getAddInt(); // 智力
					pw_s6 += magicStone.getAddCha(); // 魅力
					pw_sHp += magicStone.getAddHp(); // +HP
					pw_sMp += magicStone.getAddMp(); // +MP
					pw_sHi += magicStone.getHitModifier(); // 攻擊成功
					pw_sDg += magicStone.getDmgModifier(); // 額外攻擊
					pw_sH2 += magicStone.getBowHitModifier(); // 弓命中追加
					pw_sD2 += magicStone.getBowDmgModifier(); // 弓傷害追加
					ac += magicStone.getAddAc(); // +AC
					pw_sMr += magicStone.getMdef(); // MR(抗魔)
					pw_sSp += magicStone.getAddSp(); // SP(魔攻)
					pw_d4_1 += magicStone.getDefenseFire(); // 火屬性防禦
					pw_d4_2 += magicStone.getDefenseWater(); // 水屬性防禦
					pw_d4_3 += magicStone.getDefenseWind(); // 風屬性防禦
					pw_d4_4 += magicStone.getDefenseEarth(); // 地屬性防禦
					pw_k6_1 += magicStone.getRegistFreeze(); // 寒冰耐性
					pw_k6_2 += magicStone.getRegistStone(); // 石化耐性
					pw_k6_3 += magicStone.getRegistSleep(); // 睡眠耐性
					pw_k6_4 += magicStone.getRegistBlind(); // 暗黑耐性
					pw_k6_5 += magicStone.getRegistStun(); // 昏迷耐性
					pw_k6_6 += magicStone.getRegistSustain(); // 支撑耐性
					// value_1 += magicStone.getPhysicsDmgUp();// 物理傷害%
					// value_2 += magicStone.getMagicDmgUp();// 魔法傷害%
					// value_3 += magicStone.getPhysicsDmgDown();// 物傷減免%
					// value_4 += magicStone.getMagicDmgDown();// 魔傷減免%
					// value_5 += magicStone.getMagicHitUp();// 有害魔法命中%
					// value_6 += magicStone.getMagicHitDown();// 有害魔法抵抗%
					// value_7 += magicStone.getMagicDoubleHit();// 物理爆擊傷害%(1.5)
					// value_8 += magicStone.getPhysicsDoubleHit();//
					// 魔法爆擊傷害%(1.5)
				}
			}
		}

		final int pdm = _itemInstance.get_pandora_mark();
		switch (pdm) {
		case 1:
			pw_d4_1 += 10;
			pw_d4_2 += 10;
			pw_d4_3 += 10;
			pw_d4_4 += 10;
			break;
		case 2:
			pw_k6_5 += 10;
			break;
		case 3:
			pw_k6_6 += 10;
			break;
		case 4:
			pw_k6_2 += 10;
			break;
		case 5:
			pw_k7_1 += 1;
			pw_k7_2 += 1;
			break;
		case 6:
			// 上移 AC+1
			break;
		case 7:
			pw_sMr += 10;
			break;
		case 8:
			pw_sHp += 50;
			break;
		case 9:
			pw_sMp += 30;
			break;
		}

		if (pw_k7_1 != 0) {
			_os.writeC(37);
			_os.writeC(pw_k7_1);
		}
		if (pw_k7_2 != 0) {
			_os.writeC(38);
			_os.writeC(pw_k7_2);
		}

		if (_item.get_safeenchant() > -1) {
			_os.writeC(0x27);
			_os.writeS("安定值: " + _item.get_safeenchant());
		}

		// 攻撃成功
		if ((pw_sHi != 0) || (_item.getInfluenceHitAndDmg() != 0)) {
			_os.writeC(0x05);
			_os.writeC(pw_sHi + (_itemInstance.getEnchantLevel() * _item.getInfluenceHitAndDmg()));
		}

		// 追加打撃
		if ((pw_sDg != 0) || (_item.getInfluenceHitAndDmg() != 0)) {
			_os.writeC(0x06);
			_os.writeC(pw_sDg + (_itemInstance.getEnchantLevel() * _item.getInfluenceHitAndDmg()));
		}

		// 使用可能
		int bit = 0;
		bit |= _item.isUseRoyal() ? 1 : 0;
		bit |= _item.isUseKnight() ? 2 : 0;
		bit |= _item.isUseElf() ? 4 : 0;
		bit |= _item.isUseMage() ? 8 : 0;
		bit |= _item.isUseDarkelf() ? 16 : 0;
		bit |= _item.isUseDragonknight() ? 32 : 0;
		bit |= _item.isUseIllusionist() ? 64 : 0;
		bit |= _item.isUseWarrior() ? 128 : 0;
		_os.writeC(0x07);
		_os.writeC(bit);

		// 弓命中追加
		if (this._item.getBowHitModifierByArmor() != 0 || _item.getInfluenceBowHitAndDmg() != 0 || pw_sH2 != 0) {
			this._os.writeC(0x18);
			this._os.writeC(this._item.getBowHitModifierByArmor() + _itemInstance.getEnchantLevel() * _item.getInfluenceBowHitAndDmg());
		}

		// 弓傷害追加
		if (this._item.getBowDmgModifierByArmor() != 0 || _item.getInfluenceBowHitAndDmg() != 0 || pw_sD2 != 0) {
			this._os.writeC(0x23);
			this._os.writeC(this._item.getBowDmgModifierByArmor() + _itemInstance.getEnchantLevel() * _item.getInfluenceBowHitAndDmg());
		}

		// 特別定義套裝
		int s6_1 = 0;// 力量
		int s6_2 = 0;// 敏捷
		int s6_3 = 0;// 體質
		int s6_4 = 0;// 精神
		int s6_5 = 0;// 智力
		int s6_6 = 0;// 魅力
		int aH_1 = 0;// +HP
		int aM_1 = 0;// +MP
		int aMR_1 = 0;// MR(抗魔)
		int aSP_1 = 0;// SP(魔攻)
		int aSS_1 = 0;// 加速效果
		int d4_1 = 0;// 火屬性
		int d4_2 = 0;// 水屬性
		int d4_3 = 0;// 風屬性
		int d4_4 = 0;// 地屬性
		int k6_1 = 0;// 寒冰耐性
		int k6_2 = 0;// 石化耐性
		int k6_3 = 0;// 睡眠耐性
		int k6_4 = 0;// 暗黑耐性
		int k6_5 = 0;// 昏迷耐性
		int k6_6 = 0;// 支撑耐性

		// 161228 新增8特裝特殊能力
		int power_1 = 0;// %
		int power_2 = 0;// %
		int power_3 = 0;// %
		int power_4 = 0;// %
		int power_5 = 0;// %
		int power_6 = 0;// %
		int power_7 = 0;// %
		int power_8 = 0;// %

//		if (_itemInstance.isMatch() && _itemInstance.isEquipped()) {// 完成套裝
//			s6_1 = _item.get_mode()[0];// 力量
//			s6_2 = _item.get_mode()[1];// 敏捷
//			s6_3 = _item.get_mode()[2];// 體質
//			s6_4 = _item.get_mode()[3];// 精神
//			s6_5 = _item.get_mode()[4];// 智力
//			s6_6 = _item.get_mode()[5];// 魅力
//			aH_1 = _item.get_mode()[6];// +HP
//			aM_1 = _item.get_mode()[7];// +MP
//			aMR_1 = _item.get_mode()[8];// MR(抗魔)
//			aSP_1 = _item.get_mode()[9];// SP(魔攻)
//			aSS_1 = _item.get_mode()[10];// 加速效果
//			d4_1 = _item.get_mode()[11];// 火屬性
//			d4_2 = _item.get_mode()[12];// 水屬性
//			d4_3 = _item.get_mode()[13];// 風屬性
//			d4_4 = _item.get_mode()[14];// 地屬性
//			k6_1 = _item.get_mode()[15];// 寒冰耐性
//			k6_2 = _item.get_mode()[16];// 石化耐性
//			k6_3 = _item.get_mode()[17];// 睡眠耐性
//			k6_4 = _item.get_mode()[18];// 暗黑耐性
//			k6_5 = _item.get_mode()[19];// 昏迷耐性
//			k6_6 = _item.get_mode()[20];// 支撑耐性
//
//			power_1 += _item.get_mode()[21];
//			power_2 += _item.get_mode()[22];
//			power_3 += _item.get_mode()[23];
//			power_4 += _item.get_mode()[24];
//			power_5 += _item.get_mode()[25];
//			power_6 += _item.get_mode()[26];
//			power_7 += _item.get_mode()[27];
//			power_8 += _item.get_mode()[28];
//		}

		// 古文字顯示 防具類

		if (_itemInstance.get_power_name() != null) {
			_os.writeC(0x27);
			_os.writeS(_itemInstance.get_power_name().get_power_name());
		}

		/*
		 * if (_itemInstance.get_power_name_hole() != null) { final L1ItemPowerHole_name power = _itemInstance.get_power_name_hole(); switch (power.get_hole_1()) { case 1:// 力 力+1 s6_1 +=
		 * 1; break; case 2:// 敏 敏+1 s6_2 += 1; break; case 3:// 體 體+1 血+25 s6_3 += 1; aH_1 += 25; break; case 4:// 精 精+1 魔+25 s6_4 += 1; aM_1 += 25; break; case 5:// 智 智力+1 s6_5 += 1;
		 * break; case 6:// 魅 魅力+1 s6_6 += 1; break; case 7:// 血 血+100 aH_1 += 100; break; case 8:// 魔 魔+100 aM_1 += 100; break; case 9:// 攻 break; case 10:// 防 防禦-2 break; case 11:// 抗
		 * 抗魔+3 aMR_1 += 3; break; } switch (power.get_hole_2()) { case 1:// 力 力+1 s6_1 += 1; break; case 2:// 敏 敏+1 s6_2 += 1; break; case 3:// 體 體+1 血+25 s6_3 += 1; aH_1 += 25; break;
		 * case 4:// 精 精+1 魔+25 s6_4 += 1; aM_1 += 25; break; case 5:// 智 智力+1 s6_5 += 1; break; case 6:// 魅 魅力+1 s6_6 += 1; break; case 7:// 血 血+100 aH_1 += 100; break; case 8:// 魔 魔+100
		 * aM_1 += 100; break; case 9:// 攻 break; case 10:// 防 防禦-2 break; case 11:// 抗 抗魔+3 aMR_1 += 3; break; } switch (power.get_hole_3()) { case 1:// 力 力+1 s6_1 += 1; break; case 2://
		 * 敏 敏+1 s6_2 += 1; break; case 3:// 體 體+1 血+25 s6_3 += 1; aH_1 += 25; break; case 4:// 精 精+1 魔+25 s6_4 += 1; aM_1 += 25; break; case 5:// 智 智力+1 s6_5 += 1; break; case 6:// 魅 魅力+1
		 * s6_6 += 1; break; case 7:// 血 血+100 aH_1 += 100; break; case 8:// 魔 魔+100 aM_1 += 100; break; case 9:// 攻 break; case 10:// 防 防禦-2 break; case 11:// 抗 抗魔+3 aMR_1 += 3; break; }
		 * switch (power.get_hole_4()) { case 1:// 力 力+1 s6_1 += 1; break; case 2:// 敏 敏+1 s6_2 += 1; break; case 3:// 體 體+1 血+25 s6_3 += 1; aH_1 += 25; break; case 4:// 精 精+1 魔+25 s6_4 +=
		 * 1; aM_1 += 25; break; case 5:// 智 智力+1 s6_5 += 1; break; case 6:// 魅 魅力+1 s6_6 += 1; break; case 7:// 血 血+100 aH_1 += 100; break; case 8:// 魔 魔+100 aM_1 += 100; break; case 9://
		 * 攻 break; case 10:// 防 防禦-2 break; case 11:// 抗 抗魔+3 aMR_1 += 3; break; } switch (power.get_hole_5()) { case 1:// 力 力+1 s6_1 += 1; break; case 2:// 敏 敏+1 s6_2 += 1; break; case
		 * 3:// 體 體+1 血+25 s6_3 += 1; aH_1 += 25; break; case 4:// 精 精+1 魔+25 s6_4 += 1; aM_1 += 25; break; case 5:// 智 智力+1 s6_5 += 1; break; case 6:// 魅 魅力+1 s6_6 += 1; break; case 7://
		 * 血 血+100 aH_1 += 100; break; case 8:// 魔 魔+100 aM_1 += 100; break; case 9:// 攻 break; case 10:// 防 防禦-2 break; case 11:// 抗 抗魔+3 aMR_1 += 3; break; } }
		 */

		// 力量
		final int addstr = pw_s1 + s6_1;
		if (addstr != 0) {
			_os.writeC(0x08);
			_os.writeC(addstr);
			// _os.writeC(0x27);
			// _os.writeS("\\aH套裝-力+" + addstr);
		}
		// 敏捷
		final int adddex = pw_s2 + s6_2;
		if (adddex != 0) {
			_os.writeC(0x09);
			_os.writeC(adddex);
		}
		// 體質
		final int addcon = pw_s3 + s6_3;
		if (addcon != 0) {
			_os.writeC(0x0a);
			_os.writeC(addcon);
		}
		// 精神
		final int addwis = pw_s4 + s6_4;
		if (addwis != 0) {
			_os.writeC(0x0b);
			_os.writeC(addwis);
		}
		// 智力
		final int addint = pw_s5 + s6_5;
		if (addint != 0) {
			_os.writeC(0x0c);
			_os.writeC(addint);
		}
		// 魅力
		final int addcha = pw_s6 + s6_6;
		if (addcha != 0) {
			_os.writeC(0x0d);
			_os.writeC(addcha);
		}
		// +HP
		final int addhp = pw_sHp + aH_1;
		if (addhp != 0) {
			/*
			 * _os.writeC(0x0e); _os.writeH(addhp);
			 */
			_os.writeC(0x27);
			_os.writeS("HpMax +" + addhp);
		}
		// +MP
		final int addmp = pw_sMp + aM_1;
		if (addmp != 0) {
			/*
			 * if (addmp <= 120) { _os.writeC(0x20); _os.writeC(addmp);
			 * 
			 * } else { _os.writeC(0x27); _os.writeS("魔力上限 +" + addmp); }
			 */
			_os.writeC(0x27);
			_os.writeS("MpMax +" + addmp);
		}

		// 寒冰耐性
		final int freeze = pw_k6_1 + k6_1;
		// System.out.println("寒冰耐性:"+freeze);
		if (freeze != 0) {
			_os.writeC(0x21);
			_os.writeC(0x01);
			_os.writeH(freeze);
		}
		// 石化耐性
		final int stone = pw_k6_2 + k6_2;
		// System.out.println("石化耐性:"+stone);
		if (stone != 0) {
			_os.writeC(0x21);
			_os.writeC(0x02);
			_os.writeH(stone);
		}
		// 睡眠耐性
		final int sleep = pw_k6_3 + k6_3;
		// System.out.println("睡眠耐性:"+sleep);
		if (sleep != 0) {
			_os.writeC(0x21);
			_os.writeC(0x03);
			_os.writeH(sleep);
		}
		// 暗黑耐性
		final int blind = pw_k6_4 + k6_4;
		// System.out.println("暗黑耐性:"+blind);
		if (blind != 0) {
			_os.writeC(0x21);
			_os.writeC(0x04);
			_os.writeH(blind);
		}
		// 昏迷耐性
		final int stun = pw_k6_5 + k6_5;
		// System.out.println("昏迷耐性:"+stun);
		if (stun != 0) {
			_os.writeC(0x21);
			_os.writeC(0x05);
			_os.writeH(stun);
		}
		// 支撑耐性
		final int sustain = pw_k6_6 + k6_6;
		// System.out.println("支撑耐性:"+sustain);
		if (sustain != 0) {
			_os.writeC(0x21);
			_os.writeC(0x06);
			_os.writeH(sustain);
		}
		// MR(抗魔)
		final int addmr = pw_sMr + aMR_1;
		if (addmr != 0) {
			_os.writeC(0x0f);
			_os.writeH(addmr);
		}
		// SP(魔攻)
		final int addsp = pw_sSp + aSP_1;
		if (addsp != 0) {
			_os.writeC(0x11);
			_os.writeC(addsp);
		}
		// 具備加速效果
		boolean haste = _item.isHasteItem();

		if (aSS_1 == 1) {
			haste = true;
		}
		if (haste) {
			_os.writeC(0x12);
		}
		// 增加火屬性
		final int fire = pw_d4_1 + d4_1;
		if (fire != 0) {
			_os.writeC(0x1b);
			_os.writeC(fire);
		}
		// 增加水屬性
		final int water = pw_d4_2 + d4_2;
		if (water != 0) {
			_os.writeC(0x1c);
			_os.writeC(water);
		}
		// 增加風屬性
		final int wind = pw_d4_3 + d4_3;
		if (wind != 0) {
			_os.writeC(0x1d);
			_os.writeC(wind);
		}
		// 增加地屬性
		final int earth = pw_d4_4 + d4_4;
		if (earth != 0) {
			_os.writeC(0x1e);
			_os.writeC(earth);
		}

		// 幸運值 by terry0412
		if (_item.getInfluenceLuck() != 0) {
			_os.writeC(20);
			_os.writeC(_itemInstance.getEnchantLevel() * _item.getInfluenceLuck());
		}

		// 傷害減免 by terry0412
		if ((_item.getDamageReduction() != 0) || (_item.getInfluenceDmgR() != 0)) {
			_os.writeC(0x27);
			_os.writeS("傷害減免 +" + (_item.getDamageReduction() + (_itemInstance.getEnchantLevel() * _item.getInfluenceDmgR())));
		}

		// 套裝-物理傷害 %
		if (power_1 != 0) {
			_os.writeC(0x27);
			_os.writeS("\\aH物理傷害+" + power_1 + "%");
		}

		// 套裝-魔法傷害 %
		if (power_2 != 0) {
			_os.writeC(0x27);
			_os.writeS("\\aH魔法傷害+" + power_2 + "%");
		}

		// 套裝-物傷減免 %
		if (power_3 != 0) {
			_os.writeC(0x27);
			_os.writeS("\\aH物傷減免+" + power_3 + "%");
		}

		// 套裝-物傷減免 %
		if (power_4 != 0) {
			_os.writeC(0x27);
			_os.writeS("\\aH魔傷減免+" + power_4 + "%");
		}

		// 套裝-有害魔法命中 %
		if (power_5 != 0) {
			_os.writeC(0x27);
			_os.writeS("\\aH有害魔法命中+" + power_5 + "%");
		}

		// 套裝-有害魔法抵抗 %
		if (power_6 != 0) {
			_os.writeC(0x27);
			_os.writeS("\\aH有害魔法抵抗+" + power_6 + "%");
		}

		// 套裝-物理傷害爆擊(1.5倍) %
		if (power_7 != 0) {
			_os.writeC(0x27);
			_os.writeS("\\aH物理爆擊+" + power_7 + "%");
		}

		// 套裝-魔法傷害爆擊(1.5倍) %
		if (power_8 != 0) {
			_os.writeC(0x27);
			_os.writeS("\\aH魔法爆擊+" + power_8 + "%");
		}

		final int expPoint = _item.getExpPoint();
		if (expPoint != 0) {
			if (expPoint <= 120) {
				_os.writeC(0x24);
				_os.writeC(expPoint);

			} else {
				_os.writeC(0x27);
				_os.writeS("$6134 " + expPoint + "%");
			}
		}

		/*
		 * if (value_1 != 0) { this._os.writeC(0x27); this._os.writeS("\\aH物理傷害 +" + value_1 + "%"); } if (value_2 != 0) { this._os.writeC(0x27); this._os.writeS("\\aH魔法傷害 +" + value_2 +
		 * "%"); } if (value_3 != 0) { this._os.writeC(0x27); this._os.writeS("\\aH物理傷害減免 +" + value_3 + "%"); } if (value_4 != 0) { this._os.writeC(0x27); this._os.writeS("\\aH魔法傷害減免 +" +
		 * value_4 + "%"); } if (value_5 != 0) { this._os.writeC(0x27); this._os.writeS("\\aH有害魔法命中 +" + value_5 + "%"); } if (value_6 != 0) { this._os.writeC(0x27);
		 * this._os.writeS("\\aH有害魔法抵抗 +" + value_6 + "%"); } if (value_7 != 0) { this._os.writeC(0x27); this._os.writeS("\\aH魔法傷害暴擊 +" + value_7 + "%"); } if (value_8 != 0) {
		 * this._os.writeC(0x27); this._os.writeS("\\aH物理傷害暴擊 +" + value_8 + "%"); }
		 */

//		// 使用陣營 by terry0412
//		final int use_camp = _item.getCampSet();
//		if (use_camp > 0) {
//			final StringBuilder extra_str = new StringBuilder();
//			extra_str.append("\\aE使用陣營: ");
//
//			// 取得陣營列表
//			final Map<Integer, String> mapList = C1_Name_Table.get().getMapList();
//
//			// 檢查 是否達到 "共用"
//			int counter = 0;
//			for (final Entry<Integer, String> value : mapList.entrySet()) {
//				if ((use_camp & value.getKey()) == value.getKey()) {
//					counter++;
//				}
//			}
//			if (counter >= mapList.size()) {
//				extra_str.append("[共用]");
//
//			} else {
//				// 檢查 各陣營使用判斷
//				for (final Entry<Integer, String> value : mapList.entrySet()) {
//					if ((use_camp & value.getKey()) == value.getKey()) {
//						extra_str.append("[").append(value.getValue()).append("]");
//					}
//				}
//			}
//			_os.writeC(0x27);
//			_os.writeS(extra_str.toString());
//		}
		
		checkArmorSet(); // 套裝能力顯示

		return _os;
	}

	/**
	 * 飾品類
	 * 
	 * @return
	 */
	private BinaryOutputStream accessories() {
		// AC
		_os.writeC(0x13);
		int ac = _item.get_ac();
		if (ac < 0) {
			ac = Math.abs(ac);
		}
		_os.writeC(ac);

		_os.writeC(_item.getMaterial());
		_os.writeC(_item.get_greater());// 飾品等級
		_os.writeD(_itemInstance.getWeight());

		final int pw_s1 = _item.get_addstr();// 力量
		final int pw_s2 = _item.get_adddex();// 敏捷
		final int pw_s3 = _item.get_addcon();// 體質
		final int pw_s4 = _item.get_addwis();// 精神
		final int pw_s5 = _item.get_addint();// 智力
		final int pw_s6 = _item.get_addcha();// 魅力

		final int pw_sHp = _itemPower.getHp();// +HP
		final int pw_sMp = _itemPower.getMp();// +MP
		final int pw_sMr = _itemPower.getMr();// MR(抗魔)
		final int pw_sSp = _itemPower.getSp();// SP(魔攻)

		final int pw_sDg = _item.getDmgModifierByArmor();// DG(攻擊力)
		final int pw_sHi = _item.getHitModifierByArmor();// Hit(攻擊成功)

		final int pw_d4_1 = _item.get_defense_fire();// 火屬性
		final int pw_d4_2 = _item.get_defense_water();// 水屬性
		final int pw_d4_3 = _item.get_defense_wind();// 風屬性
		final int pw_d4_4 = _item.get_defense_earth();// 地屬性

		final int pw_k6_1 = _item.get_regist_freeze();// 寒冰耐性
		final int pw_k6_2 = _item.get_regist_stone();// 石化耐性
		final int pw_k6_3 = _item.get_regist_sleep();// 睡眠耐性
		final int pw_k6_4 = _item.get_regist_blind();// 暗黑耐性
		final int pw_k6_5 = _item.get_regist_stun();// 昏迷耐性
		final int pw_k6_6 = _item.get_regist_sustain();// 支撑耐性

		// 攻撃成功
		if ((pw_sHi != 0) || (_item.getInfluenceHitAndDmg() != 0)) {
			_os.writeC(0x05);
			_os.writeC(pw_sHi + (_itemInstance.getEnchantLevel() * _item.getInfluenceHitAndDmg()));
		}

		// 追加打撃
		if ((pw_sDg != 0) || (_item.getInfluenceHitAndDmg() != 0)) {
			_os.writeC(0x06);
			_os.writeC(pw_sDg + (_itemInstance.getEnchantLevel() * _item.getInfluenceHitAndDmg()));
		}

		// 体力恢复率 by eric4179
		if (_item.get_addhpr() != 0) {
			_os.writeC(37);
			_os.writeC(_item.get_addhpr());
		}
		// 魔力恢复率 by eric4179
		if (_item.get_addmpr() != 0) {
			_os.writeC(38);
			_os.writeC(_item.get_addmpr());
		}
		// 使用可能
		int bit = 0;
		bit |= _item.isUseRoyal() ? 1 : 0;
		bit |= _item.isUseKnight() ? 2 : 0;
		bit |= _item.isUseElf() ? 4 : 0;
		bit |= _item.isUseMage() ? 8 : 0;
		bit |= _item.isUseDarkelf() ? 16 : 0;
		bit |= _item.isUseDragonknight() ? 32 : 0;
		bit |= _item.isUseIllusionist() ? 64 : 0;
		bit |= _item.isUseWarrior() ? 128 : 0;
		_os.writeC(0x07);
		_os.writeC(bit);

		// 弓命中追加
		if ((_item.getBowHitModifierByArmor() != 0) || (_item.getInfluenceBowHitAndDmg() != 0)) {
			_os.writeC(0x18);
			_os.writeC(_item.getBowHitModifierByArmor() + (_itemInstance.getEnchantLevel() * _item.getInfluenceBowHitAndDmg()));
		}

		// 弓傷害追加
		if ((_item.getBowDmgModifierByArmor() != 0) || (_item.getInfluenceBowHitAndDmg() != 0)) {
			_os.writeC(0x23);
			_os.writeC(_item.getBowDmgModifierByArmor() + (_itemInstance.getEnchantLevel() * _item.getInfluenceBowHitAndDmg()));
		}

		// 特別定義套裝
		int s6_1 = 0;// 力量
		int s6_2 = 0;// 敏捷
		int s6_3 = 0;// 體質
		int s6_4 = 0;// 精神
		int s6_5 = 0;// 智力
		int s6_6 = 0;// 魅力
		int aH_1 = 0;// +HP
		int aM_1 = 0;// +MP
		int aMR_1 = 0;// MR(抗魔)
		int aSP_1 = 0;// SP(魔攻)
		int aSS_1 = 0;// 加速效果
		int d4_1 = 0;// 火屬性
		int d4_2 = 0;// 水屬性
		int d4_3 = 0;// 風屬性
		int d4_4 = 0;// 地屬性
		int k6_1 = 0;// 寒冰耐性
		int k6_2 = 0;// 石化耐性
		int k6_3 = 0;// 睡眠耐性
		int k6_4 = 0;// 暗黑耐性
		int k6_5 = 0;// 昏迷耐性
		int k6_6 = 0;// 支撑耐性

//		if (_itemInstance.isMatch() && _itemInstance.isEquipped()) {// 完成套裝
//			s6_1 = _item.get_mode()[0];// 力量
//			s6_2 = _item.get_mode()[1];// 敏捷
//			s6_3 = _item.get_mode()[2];// 體質
//			s6_4 = _item.get_mode()[3];// 精神
//			s6_5 = _item.get_mode()[4];// 智力
//			s6_6 = _item.get_mode()[5];// 魅力
//			aH_1 = _item.get_mode()[6];// +HP
//			aM_1 = _item.get_mode()[7];// +MP
//			aMR_1 = _item.get_mode()[8];// MR(抗魔)
//			aSP_1 = _item.get_mode()[9];// SP(魔攻)
//			aSS_1 = _item.get_mode()[10];// 加速效果
//			d4_1 = _item.get_mode()[11];// 火屬性
//			d4_2 = _item.get_mode()[12];// 水屬性
//			d4_3 = _item.get_mode()[13];// 風屬性
//			d4_4 = _item.get_mode()[14];// 地屬性
//			k6_1 = _item.get_mode()[15];// 寒冰耐性
//			k6_2 = _item.get_mode()[16];// 石化耐性
//			k6_3 = _item.get_mode()[17];// 睡眠耐性
//			k6_4 = _item.get_mode()[18];// 暗黑耐性
//			k6_5 = _item.get_mode()[19];// 昏迷耐性
//			k6_6 = _item.get_mode()[20];// 支撑耐性
//		}

		// 力量
		final int addstr = pw_s1 + s6_1;
		if (addstr != 0) {
			_os.writeC(0x08);
			_os.writeC(addstr);
		}
		// 敏捷
		final int adddex = pw_s2 + s6_2;
		if (adddex != 0) {
			_os.writeC(0x09);
			_os.writeC(adddex);
		}
		// 體質
		final int addcon = pw_s3 + s6_3;
		if (addcon != 0) {
			_os.writeC(0x0a);
			_os.writeC(addcon);
		}
		// 精神
		final int addwis = pw_s4 + s6_4;
		if (addwis != 0) {
			_os.writeC(0x0b);
			_os.writeC(addwis);
		}
		// 智力
		final int addint = pw_s5 + s6_5;
		if (addint != 0) {
			_os.writeC(0x0c);
			_os.writeC(addint);
		}
		// 魅力
		final int addcha = pw_s6 + s6_6;
		if (addcha != 0) {
			_os.writeC(0x0d);
			_os.writeC(addcha);
		}

		// +HP MR 火 水 風 地 HP MP MR SP HPR MPR
		final int addhp = pw_sHp + greater()[4] + aH_1;
		if (addhp != 0) {
			// _os.writeC(0x0e);
			// _os.writeH(addhp);
			_os.writeC(0x27);
			_os.writeS("HpMax +" + addhp);
		}

		// +MP MR 火 水 風 地 HP MP MR SP HPR MPR
		final int addmp = pw_sMp + greater()[5] + aM_1;
		if (addmp != 0) {
			/*
			 * if (addmp <= 120) { _os.writeC(0x20); _os.writeC(addmp);
			 * 
			 * } else { _os.writeC(0x27); _os.writeS("魔力上限 +" + addmp); }
			 */
			_os.writeC(0x27);
			_os.writeS("MpMax +" + addmp);
		}

		// 寒冰耐性
		final int freeze = pw_k6_1 + k6_1;
		if (freeze != 0) {
			_os.writeC(0x21);
			_os.writeC(0x01);
			_os.writeH(freeze);
		}

		// 石化耐性
		final int stone = pw_k6_2 + k6_2;
		if (stone != 0) {
			_os.writeC(0x21);
			_os.writeC(0x02);
			_os.writeH(stone);
		}

		// 睡眠耐性
		final int sleep = pw_k6_3 + k6_3;
		if (sleep != 0) {
			_os.writeC(0x21);
			_os.writeC(0x03);
			_os.writeH(sleep);
		}

		// 暗黑耐性
		final int blind = pw_k6_4 + k6_4;
		if (blind != 0) {
			_os.writeC(0x21);
			_os.writeC(0x04);
			_os.writeH(blind);
		}

		// 昏迷耐性
		final int stun = pw_k6_5 + k6_5;
		if (stun != 0) {
			_os.writeC(0x21);
			_os.writeC(0x05);
			_os.writeH(stun);
		}

		// 支撑耐性
		final int sustain = pw_k6_6 + k6_6;
		if (sustain != 0) {
			_os.writeC(0x21);
			_os.writeC(0x06);
			_os.writeH(sustain);
		}

		// MR(抗魔) MR 火 水 風 地 HP MP MR SP HPR MPR
		final int addmr = pw_sMr + greater()[6] + aMR_1;
		if (addmr != 0) {
			_os.writeC(0x0f);
			_os.writeH(addmr);
		}
		// SP(魔攻)火 水 風 地 HP MP MR SP HPR MPR
		final int addsp = pw_sSp + greater()[7] + aSP_1;
		if (addsp != 0) {
			_os.writeC(0x11);
			_os.writeC(addsp);
		}

		// 具備加速效果
		boolean haste = _item.isHasteItem();
		if (aSS_1 == 1) {
			haste = true;
		}
		if (haste) {
			_os.writeC(0x12);
		}

		// 增加火屬性
		final int defense_fire = pw_d4_1 + greater()[0] + d4_1;
		if (defense_fire != 0) {
			_os.writeC(0x1b);
			_os.writeC(defense_fire);
		}

		// 增加水屬性
		final int defense_water = pw_d4_2 + greater()[1] + d4_2;
		if (defense_water != 0) {
			_os.writeC(0x1c);
			_os.writeC(defense_water);
		}

		// 增加風屬性
		final int defense_wind = pw_d4_3 + greater()[2] + d4_3;
		if (defense_wind != 0) {
			_os.writeC(0x1d);
			_os.writeC(defense_wind);
		}

		// 增加地屬性
		final int defense_earth = pw_d4_4 + greater()[3] + d4_4;
		if (defense_earth != 0) {
			_os.writeC(0x1e);
			_os.writeC(defense_earth);
		}

		// 幸運值 by terry0412
		if (_item.getInfluenceLuck() != 0) {
			_os.writeC(20);
			_os.writeC(_itemInstance.getEnchantLevel() * _item.getInfluenceLuck());
		}

		// 傷害減免 by terry0412
		if ((_item.getDamageReduction() != 0) || (_item.getInfluenceDmgR() != 0)) {
			_os.writeC(0x27);
			_os.writeS("傷害減免 +" + (_item.getDamageReduction() + (_itemInstance.getEnchantLevel() * _item.getInfluenceDmgR())));
		}

		final int expPoint = _item.getExpPoint();
		if (expPoint != 0) {
			if (expPoint <= 120) {
				_os.writeC(0x24);
				_os.writeC(expPoint);

			} else {
				_os.writeC(0x27);
				_os.writeS("$6134 " + expPoint + "%");
			}
		}

		// 使用陣營 by terry0412
//		final int use_camp = _item.getCampSet();
//		if (use_camp > 0) {
//			final StringBuilder extra_str = new StringBuilder();
//			extra_str.append("\\aE使用陣營: ");
//
//			// 取得陣營列表
//			final Map<Integer, String> mapList = C1_Name_Table.get().getMapList();
//
//			// 檢查 是否達到 "共用"
//			int counter = 0;
//			for (final Entry<Integer, String> value : mapList.entrySet()) {
//				if ((use_camp & value.getKey()) == value.getKey()) {
//					counter++;
//				}
//			}
//			if (counter >= mapList.size()) {
//				extra_str.append("[共用]");
//
//			} else {
//				// 檢查 各陣營使用判斷
//				for (final Entry<Integer, String> value : mapList.entrySet()) {
//					if ((use_camp & value.getKey()) == value.getKey()) {
//						extra_str.append("[").append(value.getValue()).append("]");
//					}
//				}
//			}
//			_os.writeC(0x27);
//			_os.writeS(extra_str.toString());
//		}

		checkArmorSet(); // 套裝能力顯示
		
		return _os;
	}

	/**
	 * 副助道具
	 * 
	 * @return
	 */
	private BinaryOutputStream accessories2() {
		// AC
		_os.writeC(0x13);
		int ac = _item.get_ac();
		if (ac < 0) {
			ac = Math.abs(ac);
		}
		_os.writeC(ac);

		_os.writeC(_item.getMaterial());
		_os.writeC(_item.get_greater());// 飾品等級
		_os.writeD(_itemInstance.getWeight());

		final int pw_s1 = _item.get_addstr();// 力量
		final int pw_s2 = _item.get_adddex();// 敏捷
		final int pw_s3 = _item.get_addcon();// 體質
		final int pw_s4 = _item.get_addwis();// 精神
		final int pw_s5 = _item.get_addint();// 智力
		final int pw_s6 = _item.get_addcha();// 魅力

		final int pw_sHp = _itemPower.getHp();// +HP
		final int pw_sMp = _itemPower.getMp();// +MP
		final int pw_sMr = _itemPower.getMr();// MR(抗魔)
		final int pw_sSp = _itemPower.getSp();// SP(魔攻)

		final int pw_sDg = _item.getDmgModifierByArmor();// DG(攻擊力)
		final int pw_sHi = _item.getHitModifierByArmor();// Hit(攻擊成功)

		final int pw_d4_1 = _item.get_defense_fire();// 火屬性
		final int pw_d4_2 = _item.get_defense_water();// 水屬性
		final int pw_d4_3 = _item.get_defense_wind();// 風屬性
		final int pw_d4_4 = _item.get_defense_earth();// 地屬性

		final int pw_k6_1 = _item.get_regist_freeze();// 寒冰耐性
		final int pw_k6_2 = _item.get_regist_stone();// 石化耐性
		final int pw_k6_3 = _item.get_regist_sleep();// 睡眠耐性
		final int pw_k6_4 = _item.get_regist_blind();// 暗黑耐性
		final int pw_k6_5 = _item.get_regist_stun();// 昏迷耐性
		final int pw_k6_6 = _item.get_regist_sustain();// 支撑耐性

		// 攻撃成功
		if ((pw_sHi != 0) || (_item.getInfluenceHitAndDmg() != 0)) {
			_os.writeC(0x05);
			_os.writeC(pw_sHi + (_itemInstance.getEnchantLevel() * _item.getInfluenceHitAndDmg()));
		}

		// 追加打撃
		if ((pw_sDg != 0) || (_item.getInfluenceHitAndDmg() != 0)) {
			_os.writeC(0x06);
			_os.writeC(pw_sDg + (_itemInstance.getEnchantLevel() * _item.getInfluenceHitAndDmg()));
		}

		// 使用可能
		int bit = 0;
		bit |= _item.isUseRoyal() ? 1 : 0;
		bit |= _item.isUseKnight() ? 2 : 0;
		bit |= _item.isUseElf() ? 4 : 0;
		bit |= _item.isUseMage() ? 8 : 0;
		bit |= _item.isUseDarkelf() ? 16 : 0;
		bit |= _item.isUseDragonknight() ? 32 : 0;
		bit |= _item.isUseIllusionist() ? 64 : 0;
		bit |= _item.isUseWarrior() ? 128 : 0;
		_os.writeC(0x07);
		_os.writeC(bit);

		// 弓命中追加
		if ((_item.getBowHitModifierByArmor() != 0) || (_item.getInfluenceBowHitAndDmg() != 0)) {
			_os.writeC(0x18);
			_os.writeC(_item.getBowHitModifierByArmor() + (_itemInstance.getEnchantLevel() * _item.getInfluenceBowHitAndDmg()));
		}

		// 弓傷害追加
		if ((_item.getBowDmgModifierByArmor() != 0) || (_item.getInfluenceBowHitAndDmg() != 0)) {
			_os.writeC(0x23);
			_os.writeC(_item.getBowDmgModifierByArmor() + (_itemInstance.getEnchantLevel() * _item.getInfluenceBowHitAndDmg()));
		}

		// 力量
		final int addstr = pw_s1;
		if (addstr != 0) {
			_os.writeC(0x08);
			_os.writeC(addstr);
		}
		// 敏捷
		final int adddex = pw_s2;
		if (adddex != 0) {
			_os.writeC(0x09);
			_os.writeC(adddex);
		}
		// 體質
		final int addcon = pw_s3;
		if (addcon != 0) {
			_os.writeC(0x0a);
			_os.writeC(addcon);
		}
		// 精神
		final int addwis = pw_s4;
		if (addwis != 0) {
			_os.writeC(0x0b);
			_os.writeC(addwis);
		}
		// 智力
		final int addint = pw_s5;
		if (addint != 0) {
			_os.writeC(0x0c);
			_os.writeC(addint);
		}
		// 魅力
		final int addcha = pw_s6;
		if (addcha != 0) {
			_os.writeC(0x0d);
			_os.writeC(addcha);
		}

		// +HP MR 火 水 風 地 HP MP MR SP HPR MPR
		final int addhp = pw_sHp;
		if (addhp != 0) {
			/*
			 * _os.writeC(0x0e); _os.writeH(addhp);
			 */
			_os.writeC(0x27);
			_os.writeS("HpMax +" + addhp);
		}

		// +MP MR 火 水 風 地 HP MP MR SP HPR MPR
		final int addmp = pw_sMp;
		if (addmp != 0) {
			/*
			 * if (addmp <= 120) { _os.writeC(0x20); _os.writeC(addmp);
			 * 
			 * } else { _os.writeC(0x27); _os.writeS("魔力上限 +" + addmp); }
			 */
			_os.writeC(0x27);
			_os.writeS("MpMax +" + addmp);
		}

		// 寒冰耐性
		final int freeze = pw_k6_1;
		if (freeze != 0) {
			_os.writeC(0x21);
			_os.writeC(0x01);
			_os.writeH(freeze);
		}

		// 石化耐性
		final int stone = pw_k6_2;
		if (stone != 0) {
			_os.writeC(0x21);
			_os.writeC(0x02);
			_os.writeH(stone);
		}

		// 睡眠耐性
		final int sleep = pw_k6_3;
		if (sleep != 0) {
			_os.writeC(0x21);
			_os.writeC(0x03);
			_os.writeH(sleep);
		}

		// 暗黑耐性
		final int blind = pw_k6_4;
		if (blind != 0) {
			_os.writeC(0x21);
			_os.writeC(0x04);
			_os.writeH(blind);
		}

		// 昏迷耐性
		final int stun = pw_k6_5;
		if (stun != 0) {
			_os.writeC(0x21);
			_os.writeC(0x05);
			_os.writeH(stun);
		}

		// 支撑耐性
		final int sustain = pw_k6_6;
		if (sustain != 0) {
			_os.writeC(0x21);
			_os.writeC(0x06);
			_os.writeH(sustain);
		}

		// MR(抗魔) MR 火 水 風 地 HP MP MR SP HPR MPR
		final int addmr = pw_sMr;
		if (addmr != 0) {
			_os.writeC(0x0f);
			_os.writeH(addmr);
		}
		// SP(魔攻)火 水 風 地 HP MP MR SP HPR MPR
		final int addsp = pw_sSp;
		if (addsp != 0) {
			_os.writeC(0x11);
			_os.writeC(addsp);
		}

		// 具備加速效果
		final boolean haste = _item.isHasteItem();
		if (haste) {
			_os.writeC(0x12);
		}

		// 增加火屬性
		final int defense_fire = pw_d4_1;
		if (defense_fire != 0) {
			_os.writeC(0x1b);
			_os.writeC(defense_fire);
		}

		// 增加水屬性
		final int defense_water = pw_d4_2;
		if (defense_water != 0) {
			_os.writeC(0x1c);
			_os.writeC(defense_water);
		}

		// 增加風屬性
		final int defense_wind = pw_d4_3;
		if (defense_wind != 0) {
			_os.writeC(0x1d);
			_os.writeC(defense_wind);
		}

		// 增加地屬性
		final int defense_earth = pw_d4_4;
		if (defense_earth != 0) {
			_os.writeC(0x1e);
			_os.writeC(defense_earth);
		}

		// 幸運值 by terry0412
		if (_item.getInfluenceLuck() != 0) {
			_os.writeC(20);
			_os.writeC(_itemInstance.getEnchantLevel() * _item.getInfluenceLuck());
		}

		// 傷害減免 by terry0412
		if ((_item.getDamageReduction() != 0) || (_item.getInfluenceDmgR() != 0)) {
			_os.writeC(0x27);
			_os.writeS("傷害減免 +" + (_item.getDamageReduction() + (_itemInstance.getEnchantLevel() * _item.getInfluenceDmgR())));
		}

		final int expPoint = _item.getExpPoint();
		if (expPoint != 0) {
			if (expPoint <= 120) {
				_os.writeC(0x24);
				_os.writeC(expPoint);

			} else {
				_os.writeC(0x27);
				_os.writeS("$6134 " + expPoint + "%");
			}
		}

		// 使用陣營 by terry0412
		final int use_camp = _item.getCampSet();
		if (use_camp > 0) {
			final StringBuilder extra_str = new StringBuilder();
			extra_str.append("\\aE使用陣營: ");

			// 取得陣營列表
			final Map<Integer, String> mapList = C1_Name_Table.get().getMapList();

			// 檢查 是否達到 "共用"
			int counter = 0;
			for (final Entry<Integer, String> value : mapList.entrySet()) {
				if ((use_camp & value.getKey()) == value.getKey()) {
					counter++;
				}
			}
			if (counter >= mapList.size()) {
				extra_str.append("[共用]");

			} else {
				// 檢查 各陣營使用判斷
				for (final Entry<Integer, String> value : mapList.entrySet()) {
					if ((use_camp & value.getKey()) == value.getKey()) {
						extra_str.append("[").append(value.getValue()).append("]");
					}
				}
			}
			_os.writeC(0x27);
			_os.writeS(extra_str.toString());
		}

		return _os;
	}

	/**
	 * 武器
	 * 
	 * @return
	 */
	private BinaryOutputStream weapon() {
		// 打撃値
		_os.writeC(0x01);
		_os.writeC(_item.getDmgSmall());
		_os.writeC(_item.getDmgLarge());

		_os.writeC(_item.getMaterial());
		_os.writeD(_itemInstance.getWeight());

		// 強化数
		if (_itemInstance.getEnchantLevel() != 0) {
			_os.writeC(0x02);
			_os.writeC(_itemInstance.getEnchantLevel());
		}
		// 損傷度
		if (_itemInstance.get_durability() != 0) {
			_os.writeC(0x03);
			_os.writeC(_itemInstance.get_durability());
		}

		// 両手武器
		if (_item.isTwohandedWeapon()) {
			_os.writeC(0x04);
		}
		// 体力恢复率 by eric4179
		if (_item.get_addhpr() != 0) {
			_os.writeC(37);
			_os.writeC(_item.get_addhpr());
		}
		// 魔力恢复率 by eric4179
		if (_item.get_addmpr() != 0) {
			_os.writeC(38);
			_os.writeC(_item.get_addmpr());
		}
		int get_addstr = _item.get_addstr();// 力量
		int get_adddex = _item.get_adddex();// 敏捷
		int get_addcon = _item.get_addcon();// 體質
		int get_addwis = _item.get_addwis();// 精神
		int get_addint = _item.get_addint();// 智力
		int get_addcha = _item.get_addcha();// 魅力

		int get_addhp = _itemPower.getHp();// +HP
		int get_addmp = _itemPower.getMp();// +MP
		int mr = _itemPower.getMr();// MR(抗魔)

		int addWeaponSp = _itemPower.getSp();// SP(魔攻)
		int addDmgModifier = _item.getDmgModifier();// DG(攻擊力)
		int addHitModifier = _item.getHitModifier();// Hit(攻擊成功)
		int pw_sH2 = 0; // 弓命中追加
		int pw_sD2 = 0; // 弓傷害追加

		int pw_d4_1 = _item.get_defense_fire();// 火屬性
		int pw_d4_2 = _item.get_defense_water();// 水屬性
		int pw_d4_3 = _item.get_defense_wind();// 風屬性
		int pw_d4_4 = _item.get_defense_earth();// 地屬性

		int pw_k6_1 = _item.get_regist_freeze();// 寒冰耐性
		int pw_k6_2 = _item.get_regist_stone();// 石化耐性
		int pw_k6_3 = _item.get_regist_sleep();// 睡眠耐性
		int pw_k6_4 = _item.get_regist_blind();// 暗黑耐性
		int pw_k6_5 = _item.get_regist_stun();// 昏迷耐性
		int pw_k6_6 = _item.get_regist_sustain();// 支撑耐性

		// int value_1 = 0;
		// int value_2 = 0;
		// int value_3 = 0;
		// int value_4 = 0;
		// int value_5 = 0;
		// int value_6 = 0;
		// int value_7 = 0;
		// int value_8 = 0;

		// 凹槽顯示 by terry0412
		if (_itemInstance.get_power_name_hole() != null) {
			final L1ItemPowerHole_name power = _itemInstance.get_power_name_hole();
			for (int i = 0, n = 5; i < n; i++) {
				L1MagicStone magicStone = null;
				switch (i) {
				case 0:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_1());
					break;
				case 1:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_2());
					break;
				case 2:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_3());
					break;
				case 3:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_4());
					break;
				case 4:
					magicStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_5());
					break;
				}
				if (magicStone != null) {
					get_addstr += magicStone.getAddStr(); // 力量
					get_adddex += magicStone.getAddDex(); // 敏捷
					get_addcon += magicStone.getAddCon(); // 體質
					get_addwis += magicStone.getAddWis(); // 精神
					get_addint += magicStone.getAddInt(); // 智力
					get_addcha += magicStone.getAddCha(); // 魅力
					get_addhp += magicStone.getAddHp(); // +HP
					get_addmp += magicStone.getAddMp(); // +MP
					addHitModifier += magicStone.getHitModifier(); // 攻擊成功
					addDmgModifier += magicStone.getDmgModifier(); // 額外攻擊
					pw_sH2 += magicStone.getBowHitModifier(); // 弓命中追加
					pw_sD2 += magicStone.getBowDmgModifier(); // 弓傷害追加
					mr += magicStone.getMdef(); // MR(抗魔)
					addWeaponSp += magicStone.getAddSp(); // SP(魔攻)
					pw_d4_1 += magicStone.getDefenseFire(); // 火屬性防禦
					pw_d4_2 += magicStone.getDefenseWater(); // 水屬性防禦
					pw_d4_3 += magicStone.getDefenseWind(); // 風屬性防禦
					pw_d4_4 += magicStone.getDefenseEarth(); // 地屬性防禦
					pw_k6_1 += magicStone.getRegistFreeze(); // 寒冰耐性
					pw_k6_2 += magicStone.getRegistStone(); // 石化耐性
					pw_k6_3 += magicStone.getRegistSleep(); // 睡眠耐性
					pw_k6_4 += magicStone.getRegistBlind(); // 暗黑耐性
					pw_k6_5 += magicStone.getRegistStun(); // 昏迷耐性
					pw_k6_6 += magicStone.getRegistSustain(); // 支撑耐性
					// value_1 += magicStone.getPhysicsDmgUp();
					// value_2 += magicStone.getMagicDmgUp();
					// value_3 += magicStone.getPhysicsDmgDown();
					// value_4 += magicStone.getMagicDmgDown();
					// value_5 += magicStone.getMagicHitUp();
					// value_6 += magicStone.getMagicHitDown();
					// value_7 += magicStone.getMagicDoubleHit();
					// value_8 += magicStone.getPhysicsDoubleHit();
				}
			}
		}

		// 古文字顯示 武器類

		if (_itemInstance.get_power_name() != null) {
			_os.writeC(0x27);
			_os.writeS(_itemInstance.get_power_name().get_power_name());
		}

		/*
		 * if (_itemInstance.get_power_name_hole() != null) { final L1ItemPowerHole_name power = _itemInstance.get_power_name_hole(); // 力量 switch (power.get_hole_1()) { case 1:// 力 力+1
		 * get_addstr += 1; break; case 2:// 敏 敏+1 get_adddex += 1; break; case 3:// 體 體+1 血+25 get_addcon += 1; get_addhp += 25; break; case 4:// 精 精+1 魔+25 get_addwis += 1; get_addmp +=
		 * 25; break; case 5:// 智 智力+1 get_addint += 1; break; case 6:// 魅 魅力+1 get_addcha += 1; break; case 7:// 血 血+100 get_addhp += 100; break; case 8:// 魔 魔+100 get_addmp += 100;
		 * break; case 9:// 攻 額外攻擊+3 addDmgModifier += 3; break; } // 力量 switch (power.get_hole_2()) { case 1:// 力 力+1 get_addstr += 1; break; case 2:// 敏 敏+1 get_adddex += 1; break; case
		 * 3:// 體 體+1 血+25 get_addcon += 1; get_addhp += 25; break; case 4:// 精 精+1 魔+25 get_addwis += 1; get_addmp += 25; break; case 5:// 智 智力+1 get_addint += 1; break; case 6:// 魅 魅力+1
		 * get_addcha += 1; break; case 7:// 血 血+100 get_addhp += 100; break; case 8:// 魔 魔+100 get_addmp += 100; break; case 9:// 攻 額外攻擊+3 addDmgModifier += 3; break; } // 力量 switch
		 * (power.get_hole_3()) { case 1:// 力 力+1 get_addstr += 1; break; case 2:// 敏 敏+1 get_adddex += 1; break; case 3:// 體 體+1 血+25 get_addcon += 1; get_addhp += 25; break; case 4:// 精
		 * 精+1 魔+25 get_addwis += 1; get_addmp += 25; break; case 5:// 智 智力+1 get_addint += 1; break; case 6:// 魅 魅力+1 get_addcha += 1; break; case 7:// 血 血+100 get_addhp += 100; break;
		 * case 8:// 魔 魔+100 get_addmp += 100; break; case 9:// 攻 額外攻擊+3 addDmgModifier += 3; break; } // 力量 switch (power.get_hole_4()) { case 1:// 力 力+1 get_addstr += 1; break; case 2://
		 * 敏 敏+1 get_adddex += 1; break; case 3:// 體 體+1 血+25 get_addcon += 1; get_addhp += 25; break; case 4:// 精 精+1 魔+25 get_addwis += 1; get_addmp += 25; break; case 5:// 智 智力+1
		 * get_addint += 1; break; case 6:// 魅 魅力+1 get_addcha += 1; break; case 7:// 血 血+100 get_addhp += 100; break; case 8:// 魔 魔+100 get_addmp += 100; break; case 9:// 攻 額外攻擊+3
		 * addDmgModifier += 3; break; } // 力量 switch (power.get_hole_5()) { case 1:// 力 力+1 get_addstr += 1; break; case 2:// 敏 敏+1 get_adddex += 1; break; case 3:// 體 體+1 血+25 get_addcon
		 * += 1; get_addhp += 25; break; case 4:// 精 精+1 魔+25 get_addwis += 1; get_addmp += 25; break; case 5:// 智 智力+1 get_addint += 1; break; case 6:// 魅 魅力+1 get_addcha += 1; break;
		 * case 7:// 血 血+100 get_addhp += 100; break; case 8:// 魔 魔+100 get_addmp += 100; break; case 9:// 攻 額外攻擊+3 addDmgModifier += 3; break; } }
		 */

		_os.writeC(0x27);
		_os.writeS("安定值: " + _item.get_safeenchant());

		// 攻撃成功
		// int addHitModifier = this._item.getHitModifier() + pw_sHi;
		if ((addHitModifier != 0) || (_item.getInfluenceHitAndDmg() != 0)) {
			_os.writeC(0x05);
			_os.writeC(addHitModifier + (_itemInstance.getEnchantLevel() * _item.getInfluenceHitAndDmg()));
		}

		// 追加打撃
		// int addDmgModifier = this._item.getDmgModifier() + pw_sDg;
		if ((addDmgModifier != 0) || (_item.getInfluenceHitAndDmg() != 0)) {
			_os.writeC(0x06);
			_os.writeC(addDmgModifier + (_itemInstance.getEnchantLevel() * _item.getInfluenceHitAndDmg()));
		}

		// 使用可能
		int bit = 0;
		bit |= _item.isUseRoyal() ? 1 : 0;
		bit |= _item.isUseKnight() ? 2 : 0;
		bit |= _item.isUseElf() ? 4 : 0;
		bit |= _item.isUseMage() ? 8 : 0;
		bit |= _item.isUseDarkelf() ? 16 : 0;
		bit |= _item.isUseDragonknight() ? 32 : 0;
		bit |= _item.isUseIllusionist() ? 64 : 0;
		bit |= _item.isUseWarrior() ? 128 : 0;
		_os.writeC(0x07);
		_os.writeC(bit);

		// 弓命中追加...???
		if (this._item.getBowHitModifierByArmor() != 0 || _item.getInfluenceBowHitAndDmg() != 0 || pw_sH2 != 0) {
			this._os.writeC(0x18);
			this._os.writeC(this._item.getBowHitModifierByArmor() + _itemInstance.getEnchantLevel() * _item.getInfluenceBowHitAndDmg() + pw_sH2);
		}

		// 弓傷害追加...???
		if (this._item.getBowDmgModifierByArmor() != 0 || _item.getInfluenceBowHitAndDmg() != 0 || pw_sD2 != 0) {
			this._os.writeC(0x23);
			this._os.writeC(this._item.getBowDmgModifierByArmor() + _itemInstance.getEnchantLevel() * _item.getInfluenceBowHitAndDmg() + pw_sD2);
		}

		// MP吸収
		if ((_itemInstance.getItemId() == 126) || (_itemInstance.getItemId() == 127)) {
			_os.writeC(0x10);
		}
		// HP吸収
		if (_itemInstance.getItemId() == 262) {
			_os.writeC(0x22);
		}

		// int get_addstr = this._item.get_addstr();
		// STR~CHA
		if (get_addstr != 0) {
			_os.writeC(0x08);
			_os.writeC(get_addstr);
		}

		// int get_adddex = this._item.get_adddex();
		if (get_adddex != 0) {
			_os.writeC(0x09);
			_os.writeC(get_adddex);
		}

		// int get_addcon = this._item.get_addcon();
		if (get_addcon != 0) {
			_os.writeC(0x0a);
			_os.writeC(get_addcon);
		}

		// int get_addwis = this._item.get_addwis();
		if (get_addwis != 0) {
			_os.writeC(0x0b);
			_os.writeC(get_addwis);
		}

		// int get_addint = this._item.get_addint();
		if (get_addint != 0) {
			_os.writeC(0x0c);
			_os.writeC(get_addint);
		}

		// int get_addcha = this._item.get_addcha();
		if (get_addcha != 0) {
			_os.writeC(0x0d);
			_os.writeC(get_addcha);
		}

		// HP, MP

		// int get_addhp = this._item.get_addhp();
		if (get_addhp != 0) {
			// _os.writeC(0x0e);
			// _os.writeH(get_addhp);
			_os.writeC(0x27);
			_os.writeS("HpMax +" + get_addhp);
		}

		// int get_addmp = this._item.get_addmp();
		if (get_addmp != 0) {
			/*
			 * if (get_addmp <= 120) { _os.writeC(0x20); _os.writeC(get_addmp);
			 * 
			 * } else { _os.writeC(0x27); _os.writeS("魔力上限 +" + get_addmp); }
			 */
			_os.writeC(0x27);
			_os.writeS("MpMax +" + get_addmp);
		}

		// MR
		// final int mr = this._itemPower.getMr();
		if (mr != 0) {
			_os.writeC(0x0f);
			_os.writeH(mr);
		}
		// SP(魔法攻擊力)
		// int addWeaponSp = this._item.get_addsp() + pw_sSp;
		if (addWeaponSp != 0) {
			_os.writeC(0x11);
			_os.writeC(addWeaponSp);
		}
		// 具備加速效果
		if (_item.isHasteItem()) {
			_os.writeC(0x12);
		}
		// 增加火屬性
		if (pw_d4_1 != 0) {
			_os.writeC(0x1b);
			_os.writeC(pw_d4_1);
		}
		// 增加水屬性
		if (pw_d4_2 != 0) {
			_os.writeC(0x1c);
			_os.writeC(pw_d4_2);
		}
		// 增加風屬性
		if (pw_d4_3 != 0) {
			_os.writeC(0x1d);
			_os.writeC(pw_d4_3);
		}
		// 增加地屬性
		if (pw_d4_4 != 0) {
			_os.writeC(0x1e);
			_os.writeC(pw_d4_4);
		}

		// 凍結耐性
		if (pw_k6_1 != 0) {
			/*
			 * _os.writeC(0x0f); _os.writeH(pw_k6_1); _os.writeC(0x21); _os.writeC(0x01);
			 */
			_os.writeC(33);
			_os.writeC(1);
			_os.writeH(pw_k6_1);
		}
		// 石化耐性
		if (pw_k6_2 != 0) {
			/*
			 * _os.writeC(0x0f); _os.writeH(pw_k6_2); _os.writeC(0x21); _os.writeC(0x02);
			 */
			_os.writeC(33);
			_os.writeC(2);
			_os.writeH(pw_k6_2);
		}
		// 睡眠耐性
		if (pw_k6_3 != 0) {
			/*
			 * _os.writeC(0x0f); _os.writeH(pw_k6_3); _os.writeC(0x21); _os.writeC(0x03);
			 */
			_os.writeC(33);
			_os.writeC(3);
			_os.writeH(pw_k6_3);
		}
		// 暗闇耐性
		if (pw_k6_4 != 0) {
			/*
			 * _os.writeC(0x0f); _os.writeH(pw_k6_4); _os.writeC(0x21); _os.writeC(0x04);
			 */
			_os.writeC(33);
			_os.writeC(4);
			_os.writeH(pw_k6_4);
		}
		// 昏迷耐性
		if (pw_k6_5 != 0) {
			/*
			 * _os.writeC(0x0f); _os.writeH(pw_k6_5); _os.writeC(0x21); _os.writeC(0x05);
			 */
			_os.writeC(33);
			_os.writeC(5);
			_os.writeC(pw_k6_5);
		}
		// 支撑耐性
		if (pw_k6_6 != 0) {
			/*
			 * _os.writeC(0x0f); _os.writeH(pw_k6_6); _os.writeC(0x21); _os.writeC(0x06);
			 */
			_os.writeC(33);
			_os.writeC(6);
			_os.writeC(pw_k6_6);
		}

		final int expPoint = _item.getExpPoint();
		if (expPoint != 0) {
			if (expPoint <= 120) {
				_os.writeC(0x24);
				_os.writeC(expPoint);

			} else {
				_os.writeC(0x27);
				_os.writeS("$6134 " + expPoint + "%");
			}
		}

		// 傷害減免 by terry0412
		if ((_item.getDamageReduction() != 0) || (_item.getInfluenceDmgR() != 0)) {
			_os.writeC(0x27);
			_os.writeS("傷害減免 +" + (_item.getDamageReduction() + (_itemInstance.getEnchantLevel() * _item.getInfluenceDmgR())));
		}

		/*
		 * if (value_1 != 0) { this._os.writeC(0x27); this._os.writeS("物理傷害 +" + value_1 + "%"); } if (value_2 != 0) { this._os.writeC(0x27); this._os.writeS("魔法傷害 +" + value_2 + "%"); }
		 * if (value_3 != 0) { this._os.writeC(0x27); this._os.writeS("物理傷害減免 +" + value_3 + "%"); } if (value_4 != 0) { this._os.writeC(0x27); this._os.writeS("魔法傷害減免 +" + value_4 + "%");
		 * } if (value_5 != 0) { this._os.writeC(0x27); this._os.writeS("有害魔法命中 +" + value_5 + "%"); } if (value_6 != 0) { this._os.writeC(0x27); this._os.writeS("有害魔法抵抗 +" + value_6 +
		 * "%"); } if (value_7 != 0) { this._os.writeC(0x27); this._os.writeS("魔法傷害暴擊 +" + value_7 + "%"); } if (value_8 != 0) { this._os.writeC(0x27); this._os.writeS("物理傷害暴擊 +" + value_8
		 * + "%"); }
		 */

		// 使用陣營 by terry0412
		final int use_camp = _item.getCampSet();
		if (use_camp > 0) {
			final StringBuilder extra_str = new StringBuilder();
			extra_str.append("\\aE使用陣營: ");

			// 取得陣營列表
			final Map<Integer, String> mapList = C1_Name_Table.get().getMapList();

			// 檢查 是否達到 "共用"
			int counter = 0;
			for (final Entry<Integer, String> value : mapList.entrySet()) {
				if ((use_camp & value.getKey()) == value.getKey()) {
					counter++;
				}
			}
			if (counter >= mapList.size()) {
				extra_str.append("[共用]");

			} else {
				// 檢查 各陣營使用判斷
				for (final Entry<Integer, String> value : mapList.entrySet()) {
					if ((use_camp & value.getKey()) == value.getKey()) {
						extra_str.append("[").append(value.getValue()).append("]");
					}
				}
			}
			_os.writeC(0x27);
			_os.writeS(extra_str.toString());
		}

		// 魔法武器DIY系統 by terry0412
		final L1MagicWeapon magicWeapon = _itemInstance.get_magic_weapon();
		if (magicWeapon != null) {
			_os.writeC(0x27);
			final StringBuilder name = new StringBuilder().append(magicWeapon.getSkillName());
			// 使用期限
			if ((magicWeapon.getMaxUseTime() > 0) && (_itemInstance.get_time() != null)) {
				name.append(sdf.format(_itemInstance.get_time()));
			}
			_os.writeS(name.toString());
		}

		// 170105 武器資料表自定義顯示內容 erics4179
		if (_item.get_itemstrings() != null) {
			_os.writeC(0x27);
			_os.writeS(_item.get_itemstrings());
		}
		if (_item.get_itemstrings2() != null) {
			_os.writeC(0x27);
			_os.writeS(_item.get_itemstrings2());
		}
		return _os;
	}

	// 記錄時間格式 by terry0412
	private static final SimpleDateFormat sdf = new SimpleDateFormat("-[MM-dd HH:mm]");

	/**
	 * 一般道具
	 * 
	 * @return
	 */
	private BinaryOutputStream etcitem() {
		// 使用職業 by terry0412
		if (_item.isUseRoyal() && _item.isUseKnight() && _item.isUseElf() && _item.isUseMage() && _item.isUseDarkelf() && _item.isUseDragonknight() && _item.isUseIllusionist()
				&& _item.isUseWarrior()) {

		} else {
			final StringBuilder extra_str = new StringBuilder();
			extra_str.append("使用職業: ");

			if (_item.isUseRoyal()) {
				extra_str.append("[王族]");
			}
			if (_item.isUseKnight()) {
				extra_str.append("[騎士]");
			}
			if (_item.isUseElf()) {
				extra_str.append("[妖精]");
			}
			if (_item.isUseMage()) {
				extra_str.append("[法師]");
			}
			if (_item.isUseDarkelf()) {
				extra_str.append("[黑妖]");
			}
			if (_item.isUseDragonknight()) {
				extra_str.append("[龍騎]");
			}
			if (_item.isUseIllusionist()) {
				extra_str.append("[幻術]");
			}
			if (_item.isUseWarrior()) {
				extra_str.append("[戰士]");
			}
			_os.writeC(0x27);
			_os.writeS(extra_str.toString());
		}

		// 使用陣營 by terry0412
		final int use_camp = _item.getCampSet();
		if (use_camp > 0) {
			final StringBuilder extra_str = new StringBuilder();
			extra_str.append("\\aE使用陣營: ");

			// 取得陣營列表
			final Map<Integer, String> mapList = C1_Name_Table.get().getMapList();

			// 檢查 是否達到 "共用"
			int counter = 0;
			for (final Entry<Integer, String> value : mapList.entrySet()) {
				if ((use_camp & value.getKey()) == value.getKey()) {
					counter++;
				}
			}
			if (counter >= mapList.size()) {
				extra_str.append("[共用]");

			} else {
				// 檢查 各陣營使用判斷
				for (final Entry<Integer, String> value : mapList.entrySet()) {
					if ((use_camp & value.getKey()) == value.getKey()) {
						extra_str.append("[").append(value.getValue()).append("]");
					}
				}
			}
			_os.writeC(0x27);
			_os.writeS(extra_str.toString());
		}

		_os.writeC(0x17); // 材質
		_os.writeC(_item.getMaterial());
		_os.writeD(_itemInstance.getWeight());

		// 160707 道具顯示內容 erics4179
		if (_item.get_itemstrings() != null) {
			_os.writeC(0x27);
			_os.writeS(_item.get_itemstrings());
		}
		if (_item.get_itemstrings2() != null) {
			_os.writeC(0x27);
			_os.writeS(_item.get_itemstrings2());
		}
		if (_item.get_itemstrings3() != null) {
			_os.writeC(0x27);
			_os.writeS(_item.get_itemstrings3());
		}
		if (_item.get_itemstrings4() != null) {
			_os.writeC(0x27);
			_os.writeS(_item.get_itemstrings4());
		}

		return _os;
	}

	/**
	 * 寵物防具
	 * 
	 * @return
	 */
	private BinaryOutputStream petarmor(final L1PetItem petItem) {
		_os.writeC(0x13);
		int ac = petItem.getAddAc();
		if (ac < 0) {
			ac = Math.abs(ac);
		}
		_os.writeC(ac);
		_os.writeC(_item.getMaterial());
		_os.writeD(_itemInstance.getWeight());

		if (petItem.getHitModifier() != 0) {
			_os.writeC(5);
			_os.writeC(petItem.getHitModifier());
		}

		if (petItem.getDamageModifier() != 0) {
			_os.writeC(6);
			_os.writeC(petItem.getDamageModifier());
		}

		if (petItem.isHigher()) {
			_os.writeC(7);
			_os.writeC(128);
		}

		if (petItem.getAddStr() != 0) {
			_os.writeC(8);
			_os.writeC(petItem.getAddStr());
		}
		if (petItem.getAddDex() != 0) {
			_os.writeC(9);
			_os.writeC(petItem.getAddDex());
		}
		if (petItem.getAddCon() != 0) {
			_os.writeC(10);
			_os.writeC(petItem.getAddCon());
		}
		if (petItem.getAddWis() != 0) {
			_os.writeC(11);
			_os.writeC(petItem.getAddWis());
		}
		if (petItem.getAddInt() != 0) {
			_os.writeC(12);
			_os.writeC(petItem.getAddInt());
		}

		// HP, MP
		if (petItem.getAddHp() != 0) {
			_os.writeC(14);
			_os.writeH(petItem.getAddHp());
		}
		if (petItem.getAddMp() != 0) {
			_os.writeC(32);
			_os.writeC(petItem.getAddMp());
		}
		// MR
		if (petItem.getAddMr() != 0) {
			_os.writeC(15);
			_os.writeH(petItem.getAddMr());
		}
		// SP(魔力)
		if (petItem.getAddSp() != 0) {
			_os.writeC(17);
			_os.writeC(petItem.getAddSp());
		}
		return _os;
	}

	/**
	 * 寵物武器
	 * 
	 * @return
	 */
	private BinaryOutputStream petweapon(final L1PetItem petItem) {
		_os.writeC(0x01); // 打撃値
		_os.writeC(0x00);
		_os.writeC(0x00);
		_os.writeC(_item.getMaterial());
		_os.writeD(_itemInstance.getWeight());

		if (petItem.isHigher()) {
			_os.writeC(7);
			_os.writeC(128);
		}

		if (petItem.getAddStr() != 0) {
			_os.writeC(8);
			_os.writeC(petItem.getAddStr());
		}
		if (petItem.getAddDex() != 0) {
			_os.writeC(9);
			_os.writeC(petItem.getAddDex());
		}
		if (petItem.getAddCon() != 0) {
			_os.writeC(10);
			_os.writeC(petItem.getAddCon());
		}
		if (petItem.getAddWis() != 0) {
			_os.writeC(11);
			_os.writeC(petItem.getAddWis());
		}
		if (petItem.getAddInt() != 0) {
			_os.writeC(12);
			_os.writeC(petItem.getAddInt());
		}

		// HP, MP
		if (petItem.getAddHp() != 0) {
			_os.writeC(14);
			_os.writeH(petItem.getAddHp());
		}
		if (petItem.getAddMp() != 0) {
			_os.writeC(32);
			_os.writeC(petItem.getAddMp());
		}
		// MR
		if (petItem.getAddMr() != 0) {
			_os.writeC(15);
			_os.writeH(petItem.getAddMr());
		}
		return _os;
	}

	/**
	 * 飾品能力顯示 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
	 */
	private int[] greater() {
		final int level = _itemInstance.getEnchantLevel();

		int[] rint = new int[10];
		switch (_itemInstance.getItem().get_greater()) {
		case 0:// 高等
			switch (level) {
			case 0:
				break;
			case 1:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 };
				break;
			case 2:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 2, 2, 2, 2, 0, 0, 0, 0, 0, 0 };
				break;
			case 3:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 3, 3, 3, 3, 0, 0, 0, 0, 0, 0 };
				break;
			case 4:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 4, 4, 4, 4, 0, 0, 0, 0, 0, 0 };
				break;
			case 5:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 5, 5, 5, 5, 0, 0, 0, 0, 0, 0 };
				break;
			case 6:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 6, 6, 6, 6, 0, 0, 0, 0, 1, 1 };
				break;
			case 7:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 10, 10, 10, 10, 0, 0, 0, 0, 3, 3 };
				break;
			default:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 15, 15, 15, 15, 0, 0, 0, 0, 3, 3 };
				break;
			}
			break;

		case 1:// 中等
			switch (level) {
			case 0:
				break;
			case 1:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 5, 0, 0, 0, 0, 0 };
				break;
			case 2:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 10, 0, 0, 0, 0, 0 };
				break;
			case 3:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 15, 0, 0, 0, 0, 0 };
				break;
			case 4:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 20, 0, 0, 0, 0, 0 };
				break;
			case 5:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 25, 0, 0, 0, 0, 0 };
				break;
			case 6:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 30, 0, 2, 0, 0, 0 };
				break;
			case 7:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 40, 0, 7, 0, 0, 0 };
				break;
			default:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 40, 0, 12, 0, 0, 0 };
				break;
			}
			break;

		case 2:// 初等
			switch (level) {
			case 0:
				break;
			case 1:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 0, 3, 0, 0, 0, 0 };
				break;
			case 2:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 0, 6, 0, 0, 0, 0 };
				break;
			case 3:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 0, 9, 0, 0, 0, 0 };
				break;
			case 4:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 0, 12, 0, 0, 0, 0 };
				break;
			case 5:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 0, 15, 0, 0, 0, 0 };
				break;
			case 6:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 0, 25, 0, 1, 0, 0 };
				break;
			case 7:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 0, 40, 0, 2, 0, 0 };
				break;
			default:
				// 火, 水, 風, 地, HP, MP, MR, SP, HPR, MPR
				rint = new int[] { 0, 0, 0, 0, 0, 40, 0, 3, 0, 0 };
				break;
			}
			break;

		default:
			break;
		}
		return rint;
	}
	
	/** 完成套裝 */
	public boolean isMatch() {
		return _itemInstance.isMatch();
	}

	/**
	 * 套裝能力顯示
	 * <p>
	 * 適用↓↓↓<br>
	 * 防具-飾品<br>
	 * </p>
	 */
	private void checkArmorSet() {
		if ((ArmorSetTable.get().checkArmorSet(_itemInstance.getItemId())) && (
				   _item.get_mode()[0] != 0 // 套裝效果:力量增加
				|| _item.get_mode()[1] != 0 // 套裝效果:敏捷增加
				|| _item.get_mode()[2] != 0 // 套裝效果:體質增加
				|| _item.get_mode()[3] != 0 // 套裝效果:精神增加
				|| _item.get_mode()[4] != 0 // 套裝效果:智力增加
				|| _item.get_mode()[5] != 0 // 套裝效果:魅力增加
				|| _item.get_mode()[6] != 0 // 套裝效果:HP增加
				|| _item.get_mode()[7] != 0 // 套裝效果:MP增加
				|| _item.get_mode()[8] != 0 // 套裝效果:抗魔增加
				|| _item.get_mode()[9] != 0 // 套裝效果:魔攻
				|| _item.get_mode()[10] != 0 // 套裝效果:加速效果
				|| _item.get_mode()[11] != 0 // 套裝效果:火屬性增加
				|| _item.get_mode()[12] != 0 // 套裝效果:水屬性增加
				|| _item.get_mode()[13] != 0 // 套裝效果:風屬性增加
				|| _item.get_mode()[14] != 0 // 套裝效果:地屬性增加
				|| _item.get_mode()[15] != 0 // 套裝效果:寒冰耐性增加
				|| _item.get_mode()[16] != 0 // 套裝效果:石化耐性增加
				|| _item.get_mode()[17] != 0 // 套裝效果:睡眠耐性增加
				|| _item.get_mode()[18] != 0 // 套裝效果:暗闇耐性增加
				|| _item.get_mode()[19] != 0 // 套裝效果:暈眩耐性增加
				|| _item.get_mode()[20] != 0 // 套裝效果:支撐耐性增加
				|| _item.get_mode()[21] != 0 // 套裝效果:回血量增加
				|| _item.get_mode()[22] != 0 // 套裝效果:回魔量增加
				|| _item.get_mode()[23] != 0 // 套裝效果:套裝增加物理傷害=近距離傷害
				|| _item.get_mode()[24] != 0 // 套裝效果:套裝減免物理傷害=傷害減免
				|| _item.get_mode()[25] != 0 // 套裝效果:套裝增加魔法傷害=魔法傷害
				|| _item.get_mode()[26] != 0 // 套裝效果:套裝減免魔法傷害=魔法傷害減免
				|| _item.get_mode()[27] != 0 // 套裝效果:套裝增加弓的物理傷害=遠距離攻擊
				|| _item.get_mode()[28] != 0 // 套裝效果:套裝增加近距離命中率=近距離命中率
				|| _item.get_mode()[29] != 0 // 套裝效果:套裝增加遠距離命中率=遠距離命中率
				|| _item.get_mode()[30] != 0 // 套裝效果:套裝增加魔法爆擊率
//				|| _item.get_mode()[31] != 0 // 套裝效果:套裝增加防禦
				)) {
			_os.writeC(69); // 額外組合
			if (isMatch()) {
				_os.writeC(1); // 黃色
			} else {
				_os.writeC(2); // 灰色
			}

			if (_item.get_mode()[30] != 0) { // 防禦
				_os.writeC(56);// 額外防禦
				_os.writeC(-_item.get_mode()[30]);// 要多個 - 號
			}
			if (_item.get_mode()[0] != 0) { // 力量
				_os.writeC(0x08);
				_os.writeC(_item.get_mode()[0]);
			}
			if (_item.get_mode()[1] != 0) { // 敏捷
				_os.writeC(0x09);
				_os.writeC(_item.get_mode()[1]);
			}
			if (_item.get_mode()[2] != 0) { // 體質
				_os.writeC(0x0a);
				_os.writeC(_item.get_mode()[2]);
			}
			if (_item.get_mode()[3] != 0) { // 精神
				_os.writeC(0x0b);
				_os.writeC(_item.get_mode()[3]);
			}
			if (_item.get_mode()[4] != 0) { // 智力
				_os.writeC(0x0c);
				_os.writeC(_item.get_mode()[4]);
			}
			if (_item.get_mode()[5] != 0) { // 魅力
				_os.writeC(0x0d);
				_os.writeC(_item.get_mode()[5]);
			}
			if (_item.get_mode()[6] != 0) { // 血量上限
				_os.writeC(0x0e);
				_os.writeH(_item.get_mode()[6]);
			}
			if (_item.get_mode()[7] != 0) { // 魔量上限
				_os.writeC(0x20);
				_os.writeH(_item.get_mode()[7]);
			}
			if (_item.get_mode()[8] != 0) { // 抗魔
				_os.writeC(0x0f);
				_os.writeH(_item.get_mode()[8]);
			}
			if (_item.get_mode()[9] != 0) { // 魔攻
				_os.writeC(0x11);
				_os.writeC(_item.get_mode()[9]);
			}
			if (_item.get_mode()[10] != 0) { // 加速效果
				_os.writeC(0x12);
			}
			if (_item.get_mode()[11] != 0) { // 火屬性
				_os.writeC(0x1b);
				_os.writeC(_item.get_mode()[11]);
			}
			if (_item.get_mode()[12] != 0) { // 水屬性
				_os.writeC(0x1c);
				_os.writeC(_item.get_mode()[12]);
			}
			if (_item.get_mode()[13] != 0) { // 風屬性
				_os.writeC(0x1d);
				_os.writeC(_item.get_mode()[13]);
			}
			if (_item.get_mode()[14] != 0) { // 地屬性
				_os.writeC(0x1e);
				_os.writeC(_item.get_mode()[14]);
			}
			if (_item.get_mode()[15] != 0) { // 寒冰耐性
				_os.writeC(33);
				_os.writeC(1);
				_os.writeC(_item.get_mode()[15]);
			}
			if (_item.get_mode()[16] != 0) { // 石化耐性
				_os.writeC(33);
				_os.writeC(2);
				_os.writeC(_item.get_mode()[16]);
			}
			if (_item.get_mode()[17] != 0) { // 睡眠耐性
				_os.writeC(33);
				_os.writeC(3);
				_os.writeC(_item.get_mode()[17]);
			}
			if (_item.get_mode()[18] != 0) { // 暗黑耐性
				_os.writeC(33);
				_os.writeC(4);
				_os.writeC(_item.get_mode()[18]);
			}
			if (_item.get_mode()[19] != 0) { // 昏迷耐性
				_os.writeC(33);
				_os.writeC(5);
				_os.writeC(_item.get_mode()[19]);
			}
			if (_item.get_mode()[20] != 0) { // 支撐耐性
				_os.writeC(33);
				_os.writeC(6);
				_os.writeC(_item.get_mode()[20]);
			}
			if (_item.get_mode()[21] != 0) { // 體力回覆量
				_os.writeC(37);
				_os.writeC(_item.get_mode()[21]);
			}
			if (_item.get_mode()[22] != 0) { // 魔力回覆量
				_os.writeC(38);
				_os.writeC(_item.get_mode()[22]);
			}
			if (_item.get_mode()[23] != 0) { // 近距離傷害
				_os.writeC(47);
				_os.writeC(_item.get_mode()[23]);
			}
//			if (_item.get_mode()[27] != 0) { // 遠距離傷害
//				_os.writeC(35);
//				_os.writeC(_item.get_mode()[27]);
//			}
//			if (_item.get_mode()[28] != 0) { // 近距離命中率
//				_os.writeC(48);
//				_os.writeC(_item.get_mode()[28]);
//			}
//			if (_item.get_mode()[29] != 0) { // 遠距離命中率
//				_os.writeC(24);
//				_os.writeC(_item.get_mode()[29]);
//			}
			if (_item.get_mode()[24] != 0) { // 傷害減免
				_os.writeC(63);
				_os.writeC(_item.get_mode()[24]);
			}
			if (_item.get_mode()[25] != 0) { // 增加魔法傷害
				_os.writeC(39);
				_os.writeS("魔法增傷 " + _item.get_mode()[25]);
			}
			if (_item.get_mode()[26] != 0) { // 減免魔法傷害
				_os.writeC(39);
				_os.writeS("魔法減傷 " + _item.get_mode()[26]);
			}
//			if (_item.get_mode()[30] != 0) { // 魔法爆擊率
//				_os.writeC(50);
//				_os.writeC(_item.get_mode()[30]);
//			}
			// 結尾
			_os.writeC(69);
			_os.writeC(0);
		}
	}
}
