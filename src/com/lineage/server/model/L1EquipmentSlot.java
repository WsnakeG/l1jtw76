package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.BLIND_HIDING;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.DETECTION;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.EXTRA_HEAL;
import static com.lineage.server.model.skill.L1SkillId.GREATER_HASTE;
import static com.lineage.server.model.skill.L1SkillId.HASTE;
import static com.lineage.server.model.skill.L1SkillId.HEAL;
import static com.lineage.server.model.skill.L1SkillId.INVISIBILITY;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE;
import static com.lineage.server.model.skill.L1SkillId.WEAPON_SETS_GFX;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.data.ItemClass;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.datatables.ExtraMagicStoneTable;
import com.lineage.server.datatables.ItemPowerTable;
import com.lineage.server.datatables.ItemTimeTable;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Ability;
import com.lineage.server.serverpackets.S_AddSkill;
import com.lineage.server.serverpackets.S_DelSkill;
import com.lineage.server.serverpackets.S_Invis;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPowerHole_name;
import com.lineage.server.templates.L1ItemPower_text;
import com.lineage.server.templates.L1ItemTime;
import com.lineage.server.templates.L1MagicStone;

/**
 * 防具武器的使用
 * 
 * @author dexc
 */
public class L1EquipmentSlot {

	public static final Log _log = LogFactory.getLog(L1EquipmentSlot.class);

	// 執行人物
	private final L1PcInstance _owner;

	// 作用中套裝
	private final ArrayList<ArmorSet> _currentArmorSet;

	// 使用中防具
	private final ArrayList<L1ItemInstance> _armors;

	// 作用中武器
	private L1ItemInstance _weapon;

	/**
	 * 防具武器的使用
	 * 
	 * @param owner
	 *            執行人物
	 */
	public L1EquipmentSlot(final L1PcInstance owner) {
		_owner = owner;

		_armors = new ArrayList<L1ItemInstance>();
		_currentArmorSet = new ArrayList<ArmorSet>();
	}

	/**
	 * 使用中武器
	 * 
	 * @return
	 */
	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	/**
	 * 使用中防具清單
	 * 
	 * @return
	 */
	public ArrayList<L1ItemInstance> getArmors() {
		return _armors;
	}

	/**
	 * 武器裝備
	 * 
	 * @param weapon
	 */
	private void setWeapon(final L1ItemInstance weapon) {
		_owner.setWeapon(weapon);
		if (_owner.isWarrior()) {
			if (_owner.getWeaponWarrior() != null) {
				_owner.setCurrentWeapon(88);
			} else {
				_owner.setCurrentWeapon(weapon.getItem().getType1());
			}
		} else {
			_owner.setCurrentWeapon(weapon.getItem().getType1());
		}
		weapon.startEquipmentTimer(_owner);
		_weapon = weapon;
		if (_weapon.getItem().getMagicDmgModifier() != 0) {
			_owner.add_magic_modifier_dmg(_weapon.getItem().getMagicDmgModifier());
		}
		if (ConfigAlt.WEAPON_EFFECT_DELAY > 0) {
			_owner.setSkillEffect(WEAPON_SETS_GFX, ConfigAlt.WEAPON_EFFECT_DELAY * 1000);
		}
	}

	/**
	 * 武器解除
	 * 
	 * @param weapon
	 */
	private void removeWeapon(final L1ItemInstance weapon) {
		_owner.setWeapon(null);
		_owner.setCurrentWeapon(0);
		weapon.stopEquipmentTimer(_owner);
		if (_weapon.getItem().getMagicDmgModifier() != 0) {
			_owner.add_magic_modifier_dmg(-_weapon.getItem().getMagicDmgModifier());
		}
		_weapon = null;
		if (_owner.hasSkillEffect(COUNTER_BARRIER)) {
			_owner.removeSkillEffect(COUNTER_BARRIER);
		}
		if (_owner.hasSkillEffect(WEAPON_SETS_GFX)) {
			_owner.killSkillEffectTimer(WEAPON_SETS_GFX);
		}
	}

	/**
	 * 防具穿著裝備
	 * 
	 * @param armor
	 */
	private void setArmor(final L1ItemInstance armor) {
		final L1Item item = armor.getItem();
		final int itemId = armor.getItem().getItemId();

		// 取得物件觸發事件
		final int addac = addac_armor(armor);
		final int use_type = armor.getItem().getUseType();

		switch (use_type) {
		case 2:// 盔甲
			_owner.addAc(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac);
			break;

		case 22:// 頭盔
			_owner.addAc(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac);
			break;

		case 20:// 手套
			_owner.addAc(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac);
			break;

		case 21:// 長靴
			_owner.addAc(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac);
			break;

		case 18:// T恤
		case 19:// 斗篷
		case 25:// 盾牌
			_owner.addAc(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac);
			break;

		case 23:// 戒指
		case 24:// 項鍊
		case 37:// 腰帶
		case 40:// 耳環
			if (item.get_ac() != 0) {
				_owner.addAc(item.get_ac());
			}
			if (armor.getItem().get_greater() != 3) {
				armor.greater(_owner, true);
			}
			break;

		case 44:// 副助道具
		case 43:// 輔助格子左
		case 45:// 輔助格子中
		case 47:// 輔助格子左
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
			if (item.get_ac() != 0) {
				_owner.addAc(item.get_ac());
			}
			break;

		case 70:// 脛甲
			if (item.get_ac() != 0) {
				_owner.addAc((item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic()));
			}
			break;

		default:
			break;
		}
		set_time_item(armor);

		_owner.addDamageReductionByArmor(item.getDamageReduction() + (armor.getEnchantLevel() * item.getInfluenceDmgR()));
		_owner.addWeightReduction(item.getWeightReduction());

		final int hit = item.getHitModifierByArmor();// Hit(攻擊成功)
		final int dmg = item.getDmgModifierByArmor();// DG(攻擊力)

		_owner.addHitModifierByArmor(hit + (armor.getEnchantLevel() * item.getInfluenceHitAndDmg()));
		_owner.addDmgModifierByArmor(dmg + (armor.getEnchantLevel() * item.getInfluenceHitAndDmg()));

		_owner.addBowHitModifierByArmor(item.getBowHitModifierByArmor() + (armor.getEnchantLevel() * item.getInfluenceBowHitAndDmg()));
		_owner.addBowDmgModifierByArmor(item.getBowDmgModifierByArmor() + (armor.getEnchantLevel() * item.getInfluenceBowHitAndDmg()));

		// 幸運值
		if (item.getInfluenceLuck() != 0) {
			_owner.setLuckValue(_owner.getLuckValue() + (armor.getEnchantLevel() * item.getInfluenceLuck()));
		}

		// pandora
		if (armor.get_pandora_type() > 0) {
			armor.set_pandora_buff(_owner, true);
		}
		// pandora mark
		if (armor.get_pandora_mark() > 0) {
			armor.set_pandora_markbuff(_owner, true);
		}

		final int addFire = item.get_defense_fire();
		_owner.addFire(addFire);// 增加火屬性
		final int addWater = item.get_defense_water();
		_owner.addWater(addWater);// 增加水屬性
		final int addWind = item.get_defense_wind();
		_owner.addWind(addWind);// 增加風屬性
		final int addEarth = item.get_defense_earth();
		_owner.addEarth(addEarth);// 增加地屬性

		final int addRegistFreeze = item.get_regist_freeze();
		_owner.addRegistFreeze(addRegistFreeze);// 寒冰耐性
		final int addRegistStone = item.get_regist_stone();
		_owner.addRegistStone(addRegistStone);// 石化耐性
		final int addRegistSleep = item.get_regist_sleep();
		_owner.addRegistSleep(addRegistSleep);// 睡眠耐性
		final int addRegistBlind = item.get_regist_blind();
		_owner.addRegistBlind(addRegistBlind);// 暗黑耐性
		final int addRegistStun = item.get_regist_stun();
		_owner.addRegistStun(addRegistStun);// 昏迷耐性
		final int addRegistSustain = item.get_regist_sustain();
		_owner.addRegistSustain(addRegistSustain);// 支撑耐性

		_armors.add(armor);

		// 取回全部套裝
		for (final Integer key : ArmorSet.getAllSet().keySet()) {
			// 套裝資料
			final ArmorSet armorSet = ArmorSet.getAllSet().get(key);
			// 套裝中組件 並且 完成套裝
			if (armorSet.isPartOfSet(itemId) && armorSet.isValid(_owner)) {
				if (armor.getItem().getUseType() == 23) {// 戒指
					if (!armorSet.isEquippedRingOfArmorSet(_owner)) {
						armorSet.giveEffect(_owner);
						_currentArmorSet.add(armorSet);
						_owner.getInventory().setPartMode(armorSet, true);
					}

				} else {
					armorSet.giveEffect(_owner);
					_currentArmorSet.add(armorSet);
					_owner.getInventory().setPartMode(armorSet, true);
				}
			}
		}
		// 計時物件啟用
		armor.startEquipmentTimer(_owner);
	}

	private int addac_armor(final L1ItemInstance armor) {
		int addac = 0;
		if (armor.get_power_name_hole() != null) {
			final L1ItemPowerHole_name power = armor.get_power_name_hole();
			switch (power.get_hole_1()) {
			case 10:// 防 防禦-2
				addac += 2;
				break;
			}
			switch (power.get_hole_2()) {
			case 10:// 防 防禦-2
				addac += 2;
				break;
			}
			switch (power.get_hole_3()) {
			case 10:// 防 防禦-2
				addac += 2;
				break;
			}
			switch (power.get_hole_4()) {
			case 10:// 防 防禦-2
				addac += 2;
				break;
			}
			switch (power.get_hole_5()) {
			case 10:// 防 防禦-2
				addac += 2;
				break;
			}
		}
		return addac;
	}

	/**
	 * 給予時間限制物品
	 * 
	 * @param item
	 */
	private void set_time_item(final L1ItemInstance item) {
		if (item.get_time() == null) {
			final L1ItemTime itemTime = ItemTimeTable.TIME.get(item.getItemId());
			if ((itemTime != null) && itemTime.is_equipped()) {
				// 目前時間 加上指定天數耗用秒數
				final long upTime = System.currentTimeMillis() + (itemTime.get_remain_time() * 60 * 1000);

				// 時間數據
				final Timestamp ts = new Timestamp(upTime);
				item.set_time(ts);

				// 人物背包物品使用期限資料
				CharItemsTimeReading.get().addTime(item.getId(), ts);
				_owner.sendPackets(new S_ItemName(item));
			}
		}
	}

	/**
	 * 解除裝備
	 * 
	 * @param armor
	 */
	private void removeArmor(final L1ItemInstance armor) {
		final L1Item item = armor.getItem();
		final int itemId = armor.getItem().getItemId();
		// 取得物件觸發事件
		final int use_type = armor.getItem().getUseType();
		final int addac = addac_armor(armor); // 凹槽系統
		switch (use_type) {
		case 2:// 盔甲
			_owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac));
			break;

		case 22:// 頭盔
			_owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac));
			break;

		case 20:// 手套
			_owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac));
			break;

		case 21:// 長靴
			_owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac));
			break;

		case 18:// T恤
		case 19:// 斗篷
		case 25:// 盾牌
			_owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() - addac));
			break;

		case 23:// 戒指
		case 24:// 項鍊
		case 37:// 腰帶
		case 40:// 耳環
			if (item.get_ac() != 0) {
				_owner.addAc(-item.get_ac());
			}
			if (armor.getItem().get_greater() != 3) {
				armor.greater(_owner, false);
			}
			break;

		case 44:// 副助道具
		case 43:// 輔助格子左
		case 45:// 輔助格子中
		case 47:// 輔助格子左
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
			if (item.get_ac() != 0) {
				_owner.addAc(-item.get_ac());
			}
			break;

		case 70:// 脛甲
			if (item.get_ac() != 0) {
				_owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic()));
			}
			break;

		default:
			break;
		}

		_owner.addDamageReductionByArmor(-(item.getDamageReduction() + (armor.getEnchantLevel() * item.getInfluenceDmgR())));
		_owner.addWeightReduction(-item.getWeightReduction());

		final int hit = item.getHitModifierByArmor();// Hit(攻擊成功)
		final int dmg = item.getDmgModifierByArmor();// DG(攻擊力)

		_owner.addHitModifierByArmor(-(hit + (armor.getEnchantLevel() * item.getInfluenceHitAndDmg())));
		_owner.addDmgModifierByArmor(-(dmg + (armor.getEnchantLevel() * item.getInfluenceHitAndDmg())));

		_owner.addBowHitModifierByArmor(-(item.getBowHitModifierByArmor() + (armor.getEnchantLevel() * item.getInfluenceBowHitAndDmg())));
		_owner.addBowDmgModifierByArmor(-(item.getBowDmgModifierByArmor() + (armor.getEnchantLevel() * item.getInfluenceBowHitAndDmg())));

		// 幸運值
		if (item.getInfluenceLuck() != 0) {
			_owner.setLuckValue(-(_owner.getLuckValue() + (+armor.getEnchantLevel() * item.getInfluenceLuck())));
		}

		// pandora
		if (armor.get_pandora_type() > 0) {
			armor.set_pandora_buff(_owner, false);
		}
		// pandora mark
		if (armor.get_pandora_mark() > 0) {
			armor.set_pandora_markbuff(_owner, false);
		}

		final int addFire = item.get_defense_fire();
		_owner.addFire(-addFire);// 增加火屬性
		final int addWater = item.get_defense_water();
		_owner.addWater(-addWater);// 增加水屬性
		final int addWind = item.get_defense_wind();
		_owner.addWind(-addWind);// 增加風屬性
		final int addEarth = item.get_defense_earth();
		_owner.addEarth(-addEarth);// 增加地屬性

		final int addRegistFreeze = item.get_regist_freeze();
		_owner.addRegistFreeze(-addRegistFreeze);// 寒冰耐性
		final int addRegistStone = item.get_regist_stone();
		_owner.addRegistStone(-addRegistStone);// 石化耐性
		final int addRegistSleep = item.get_regist_sleep();
		_owner.addRegistSleep(-addRegistSleep);// 睡眠耐性
		final int addRegistBlind = item.get_regist_blind();
		_owner.addRegistBlind(-addRegistBlind);// 暗黑耐性
		final int addRegistStun = item.get_regist_stun();
		_owner.addRegistStun(-addRegistStun);// 昏迷耐性
		final int addRegistSustain = item.get_regist_sustain();
		_owner.addRegistSustain(-addRegistSustain);// 支撑耐性

		// 取回全部套裝
		for (final Integer key : ArmorSet.getAllSet().keySet()) {
			// 套裝資料
			final ArmorSet armorSet = ArmorSet.getAllSet().get(key);
			// 套裝中組件 作用中套裝具有該套裝資料 並且套裝未完成
			if (armorSet.isPartOfSet(itemId) && _currentArmorSet.contains(armorSet) && !armorSet.isValid(_owner)) {
				armorSet.cancelEffect(_owner);
				// 移除作用中套裝
				_currentArmorSet.remove(armorSet);
				_owner.getInventory().setPartMode(armorSet, false);
			}
		}

		// 計時物件停止計時
		armor.stopEquipmentTimer(_owner);

		_armors.remove(armor);
	}

	/**
	 * 設置物品裝備
	 * 
	 * @param eq
	 */
	public void set(final L1ItemInstance eq) {
		final L1Item item = eq.getItem();
		if (item.getType2() == 0) {
			return;
		}

		int addhp = item.get_addhp();
		int addmp = item.get_addmp();

		int get_addstr = item.get_addstr();
		int get_adddex = item.get_adddex();
		int get_addcon = item.get_addcon();
		int get_addwis = item.get_addwis();
		int get_addint = item.get_addint();
		int get_addcha = item.get_addcha();
		int addMr = 0;
		int addSp = item.get_addsp();

		// if (eq.get_power_name_hole() != null) {
		// final L1ItemPowerHole_name power = eq.get_power_name_hole();
		// switch (power.get_hole_1()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// switch (power.get_hole_2()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// switch (power.get_hole_3()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// switch (power.get_hole_4()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// switch (power.get_hole_5()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// }

		// 凹槽顯示 by terry0412
		if (eq.get_power_name_hole() != null) {
			final L1ItemPowerHole_name power = eq.get_power_name_hole();

			int pw_sH1 = 0;
			int pw_sD1 = 0;
			int pw_sH2 = 0;
			int pw_sD2 = 0;
			int addAc = 0; // +AC
			int addFire = 0; // 火屬性防禦
			int addWater = 0; // 水屬性防禦
			int addWind = 0; // 風屬性防禦
			int addEarth = 0; // 地屬性防禦
			int addRegistFreeze = 0; // 寒冰耐性
			int addRegistStone = 0; // 石化耐性
			int addRegistSleep = 0; // 睡眠耐性
			int addRegistBlind = 0; // 暗黑耐性
			int addRegistStun = 0; // 昏迷耐性
			int addRegistSustain = 0; // 支撑耐性
			int value_1 = 0;
			int value_2 = 0;
			int value_3 = 0;
			int value_4 = 0;
			int value_5 = 0;
			int value_6 = 0;
			int value_7 = 0;
			int value_8 = 0;

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
					addhp += magicStone.getAddHp(); // +HP
					addmp += magicStone.getAddMp(); // +MP
					pw_sH1 += magicStone.getHitModifier(); // 攻擊成功
					pw_sD1 += magicStone.getDmgModifier(); // 額外攻擊
					pw_sH2 += magicStone.getBowHitModifier(); // 弓命中追加
					pw_sD2 += magicStone.getBowDmgModifier(); // 弓傷害追加
					addAc += magicStone.getAddAc(); // +AC
					addMr += magicStone.getMdef(); // MR(抗魔)
					addSp += magicStone.getAddSp(); // SP(魔攻)
					addFire += magicStone.getDefenseFire(); // 火屬性防禦
					addWater += magicStone.getDefenseWater(); // 水屬性防禦
					addWind += magicStone.getDefenseWind(); // 風屬性防禦
					addEarth += magicStone.getDefenseEarth(); // 地屬性防禦
					addRegistFreeze += magicStone.getRegistFreeze(); // 寒冰耐性
					addRegistStone += magicStone.getRegistStone(); // 石化耐性
					addRegistSleep += magicStone.getRegistSleep(); // 睡眠耐性
					addRegistBlind += magicStone.getRegistBlind(); // 暗黑耐性
					addRegistStun += magicStone.getRegistStun(); // 昏迷耐性
					addRegistSustain += magicStone.getRegistSustain(); // 支撑耐性
					value_1 += magicStone.getPhysicsDmgUp();
					value_2 += magicStone.getMagicDmgUp();
					value_3 += magicStone.getPhysicsDmgDown();
					value_4 += magicStone.getMagicDmgDown();
					value_5 += magicStone.getMagicHitUp();
					value_6 += magicStone.getMagicHitDown();
					value_7 += magicStone.getMagicDoubleHit();
					value_8 += magicStone.getPhysicsDoubleHit();
				}
			}

			if (pw_sH1 != 0) {
				_owner.addHitModifierByArmor(pw_sH1);
			}
			if (pw_sD1 != 0) {
				_owner.addDmgModifierByArmor(pw_sH1);
			}
			if (pw_sH2 != 0) {
				_owner.addBowHitModifierByArmor(pw_sH1);
			}
			if (pw_sD2 != 0) {
				_owner.addBowDmgModifierByArmor(pw_sH1);
			}
			if (addAc != 0) {
				_owner.addAc(addAc);
			}
			if (addSp != 0) {
				_owner.addSp(addSp);
			}
			if (addFire != 0) {
				_owner.addFire(addFire);
			}
			if (addWater != 0) {
				_owner.addWater(addWater);
			}
			if (addWind != 0) {
				_owner.addWind(addWind);
			}
			if (addEarth != 0) {
				_owner.addEarth(addEarth);
			}
			if (addRegistFreeze != 0) {
				_owner.addRegistFreeze(addRegistFreeze);
			}
			if (addRegistStone != 0) {
				_owner.addRegistStone(addRegistStone);
			}
			if (addRegistSleep != 0) {
				_owner.addRegistSleep(addRegistSleep);
			}
			if (addRegistBlind != 0) {
				_owner.addRegistBlind(addRegistBlind);
			}
			if (addRegistStun != 0) {
				_owner.addRegistStun(addRegistStun);
			}
			if (addRegistSustain != 0) {
				_owner.addRegistSustain(addRegistSustain);
			}
			if (value_1 != 0) {
				_owner.addPhysicsDmgUp(value_1);
			}
			if (value_2 != 0) {
				_owner.addMagicDmgUp(value_2);
			}
			if (value_3 != 0) {
				_owner.addPhysicsDmgDown(value_3);
			}
			if (value_4 != 0) {
				_owner.addMagicDmgDown(value_4);
			}
			if (value_5 != 0) {
				_owner.addMagicHitUp(value_5);
			}
			if (value_6 != 0) {
				_owner.addMagicHitDown(value_6);
			}
			if (value_7 != 0) {
				_owner.addMagicDoubleHit(value_7);
			}
			if (value_8 != 0) {
				_owner.addPhysicsDoubleHit(value_8);
			}
		}

		final int influenceHp = item.getInfluenceHp();
		if ((addhp != 0) || (influenceHp != 0)) {
			_owner.addMaxHp(addhp + (eq.getEnchantLevel() * influenceHp));// +HP
		}

		final int influenceMp = item.getInfluenceMp();
		if ((addmp != 0) || (influenceMp != 0)) {
			_owner.addMaxMp(addmp + (eq.getEnchantLevel() * influenceMp));// +MP
		}

		_owner.addStr(get_addstr);// 力量

		_owner.addDex(get_adddex);// 敏捷

		_owner.addCon(get_addcon);// 體質

		_owner.addWis(get_addwis);// 精神
		if (get_addwis != 0) {
			_owner.resetBaseMr();
		}

		_owner.addInt(get_addint);// 智力

		_owner.addCha(get_addcha);// 魅力

		addMr += eq.getMr();
		// 精靈盾牌
		if (eq.getName().equals("$187") && _owner.isElf()) {
			addMr += 5;
		}
		if (addMr != 0) {
			_owner.addMr(addMr);
			_owner.sendPackets(new S_SPMR(_owner));
		}

		final int addSp1 = item.get_addsp();
		final int influenceSp = item.getInfluenceSp();
		if ((addSp1 != 0) || (influenceSp != 0)) {
			_owner.addSp(addSp1 + (eq.getEnchantLevel() * influenceSp));
			_owner.sendPackets(new S_SPMR(_owner));
		}

		// 具備加速
		final boolean isHasteItem = item.isHasteItem();
		if (isHasteItem) {
			_owner.addHasteItemEquipped(1);
			_owner.removeHasteSkillEffect();
			if (_owner.getMoveSpeed() != 1) {
				_owner.setMoveSpeed(1);
				_owner.sendPackets(new S_SkillHaste(_owner.getId(), 1, -1));
				_owner.broadcastPacketAll(new S_SkillHaste(_owner.getId(), 1, 0));
			}
		}

		switch (item.getType2()) {
		case 1:// 武器
			setWeapon(eq);
			ItemClass.get().item_weapon(true, _owner, eq);
			break;

		case 2:// 防具
			setArmor(eq);
			setMagic(eq);
			ItemClass.get().item_armor(true, _owner, eq);
			break;
		}

		// 取回全部古文字組合 (fixed by terry0412)
		if ((eq.get_power_name() != null) && (eq.get_power_name().get_power_name() != null)) {
			for (final L1ItemPower_text values : ItemPowerTable.POWER_TEXT.values()) {
				if (values.check_pc(_owner)) {
					_owner.add_power(values);
				}
			}
		}
		// 經驗加倍指數 (以%計算) by terry0412
		_owner.setExpPoint(_owner.getExpPoint() + item.getExpPoint());

		if (get_addstr != 0 || get_adddex != 0 || get_addcon != 0 || get_addwis != 0 || get_addint != 0 || item.getWeightReduction() != 0) {
			_owner.sendDetails();
		}
	}

	/**
	 * 解除物品裝備
	 * 
	 * @param eq
	 */
	public void remove(final L1ItemInstance eq) {
		final L1Item item = eq.getItem();
		if (item.getType2() == 0) {
			return;
		}

		int addhp = item.get_addhp();
		int addmp = item.get_addmp();

		int get_addstr = item.get_addstr();
		int get_adddex = item.get_adddex();
		int get_addcon = item.get_addcon();
		int get_addwis = item.get_addwis();
		int get_addint = item.get_addint();
		int get_addcha = item.get_addcha();
		int addMr = 0;
		int addSp = item.get_addsp();

		// if (eq.get_power_name_hole() != null) {
		// final L1ItemPowerHole_name power = eq.get_power_name_hole();
		// switch (power.get_hole_1()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// switch (power.get_hole_2()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// switch (power.get_hole_3()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// switch (power.get_hole_4()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// switch (power.get_hole_5()) {
		// case 1:// 力 力+1
		// get_addstr += 1;
		// break;
		// case 2:// 敏 敏+1
		// get_adddex += 1;
		// break;
		// case 3:// 體 體+1 血+25
		// get_addcon += 1;
		// addhp += 25;
		// break;
		// case 4:// 精 精+1 魔+25
		// get_addwis += 1;
		// addmp += 25;
		// break;
		// case 5:// 智 智力+1
		// get_addint += 1;
		// break;
		// case 6:// 魅 魅力+1
		// get_addcha += 1;
		// break;
		// case 7:// 血 血+100
		// addhp += 100;
		// break;
		// case 8:// 魔 魔+100
		// addmp += 100;
		// break;
		// case 9:// 額 額外攻擊+3
		// break;
		// case 10:// 防禦-2
		// break;
		// case 11:// 抗 抗魔+3
		// addMr += 3;
		// break;
		// }
		// }

		// 凹槽顯示 by terry0412
		if (eq.get_power_name_hole() != null) {
			final L1ItemPowerHole_name power = eq.get_power_name_hole();

			int pw_sH1 = 0;
			int pw_sD1 = 0;
			int pw_sH2 = 0;
			int pw_sD2 = 0;
			int addAc = 0; // +AC
			int addFire = 0; // 火屬性防禦
			int addWater = 0; // 水屬性防禦
			int addWind = 0; // 風屬性防禦
			int addEarth = 0; // 地屬性防禦
			int addRegistFreeze = 0; // 寒冰耐性
			int addRegistStone = 0; // 石化耐性
			int addRegistSleep = 0; // 睡眠耐性
			int addRegistBlind = 0; // 暗黑耐性
			int addRegistStun = 0; // 昏迷耐性
			int addRegistSustain = 0; // 支撑耐性
			int value_1 = 0;
			int value_2 = 0;
			int value_3 = 0;
			int value_4 = 0;
			int value_5 = 0;
			int value_6 = 0;
			int value_7 = 0;
			int value_8 = 0;

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
					addhp += magicStone.getAddHp(); // +HP
					addmp += magicStone.getAddMp(); // +MP
					pw_sH1 += magicStone.getHitModifier(); // 攻擊成功
					pw_sD1 += magicStone.getDmgModifier(); // 額外攻擊
					pw_sH2 += magicStone.getBowHitModifier(); // 弓命中追加
					pw_sD2 += magicStone.getBowDmgModifier(); // 弓傷害追加
					addAc += magicStone.getAddAc(); // +AC
					addMr += magicStone.getMdef(); // MR(抗魔)
					addSp += magicStone.getAddSp(); // SP(魔攻)
					addFire += magicStone.getDefenseFire(); // 火屬性防禦
					addWater += magicStone.getDefenseWater(); // 水屬性防禦
					addWind += magicStone.getDefenseWind(); // 風屬性防禦
					addEarth += magicStone.getDefenseEarth(); // 地屬性防禦
					addRegistFreeze += magicStone.getRegistFreeze(); // 寒冰耐性
					addRegistStone += magicStone.getRegistStone(); // 石化耐性
					addRegistSleep += magicStone.getRegistSleep(); // 睡眠耐性
					addRegistBlind += magicStone.getRegistBlind(); // 暗黑耐性
					addRegistStun += magicStone.getRegistStun(); // 昏迷耐性
					addRegistSustain += magicStone.getRegistSustain(); // 支撑耐性
					value_1 += magicStone.getPhysicsDmgUp();
					value_2 += magicStone.getMagicDmgUp();
					value_3 += magicStone.getPhysicsDmgDown();
					value_4 += magicStone.getMagicDmgDown();
					value_5 += magicStone.getMagicHitUp();
					value_6 += magicStone.getMagicHitDown();
					value_7 += magicStone.getMagicDoubleHit();
					value_8 += magicStone.getPhysicsDoubleHit();
				}
			}

			if (pw_sH1 != 0) {
				_owner.addHitModifierByArmor(-pw_sH1);
			}
			if (pw_sD1 != 0) {
				_owner.addDmgModifierByArmor(-pw_sH1);
			}
			if (pw_sH2 != 0) {
				_owner.addBowHitModifierByArmor(-pw_sH1);
			}
			if (pw_sD2 != 0) {
				_owner.addBowDmgModifierByArmor(-pw_sH1);
			}
			if (addAc != 0) {
				_owner.addAc(-addAc);
			}
			if (addSp != 0) {
				_owner.addSp(-addSp);
			}
			if (addFire != 0) {
				_owner.addFire(-addFire);
			}
			if (addWater != 0) {
				_owner.addWater(-addWater);
			}
			if (addWind != 0) {
				_owner.addWind(-addWind);
			}
			if (addEarth != 0) {
				_owner.addEarth(-addEarth);
			}
			if (addRegistFreeze != 0) {
				_owner.addRegistFreeze(-addRegistFreeze);
			}
			if (addRegistStone != 0) {
				_owner.addRegistStone(-addRegistStone);
			}
			if (addRegistSleep != 0) {
				_owner.addRegistSleep(-addRegistSleep);
			}
			if (addRegistBlind != 0) {
				_owner.addRegistBlind(-addRegistBlind);
			}
			if (addRegistStun != 0) {
				_owner.addRegistStun(-addRegistStun);
			}
			if (addRegistSustain != 0) {
				_owner.addRegistSustain(-addRegistSustain);
			}
			if (value_1 != 0) {
				_owner.addPhysicsDmgUp(-value_1);
			}
			if (value_2 != 0) {
				_owner.addMagicDmgUp(-value_2);
			}
			if (value_3 != 0) {
				_owner.addPhysicsDmgDown(-value_3);
			}
			if (value_4 != 0) {
				_owner.addMagicDmgDown(-value_4);
			}
			if (value_5 != 0) {
				_owner.addMagicHitUp(-value_5);
			}
			if (value_6 != 0) {
				_owner.addMagicHitDown(-value_6);
			}
			if (value_7 != 0) {
				_owner.addMagicDoubleHit(-value_7);
			}
			if (value_8 != 0) {
				_owner.addPhysicsDoubleHit(-value_8);
			}
		}

		final int influenceHp = item.getInfluenceHp();
		if ((addhp != 0) || (influenceHp != 0)) {
			_owner.addMaxHp(-(addhp + (eq.getEnchantLevel() * influenceHp)));// +HP
		}

		final int influenceMp = item.getInfluenceMp();
		if ((addmp != 0) || (influenceMp != 0)) {
			_owner.addMaxMp(-(addmp + (eq.getEnchantLevel() * influenceMp)));// +MP
		}

		_owner.addStr((byte) -get_addstr);// 力量
		_owner.addDex((byte) -get_adddex);// 敏捷
		_owner.addCon((byte) -get_addcon);// 體質
		_owner.addWis((byte) -get_addwis);// 精神
		if (get_addwis != 0) {
			_owner.resetBaseMr();
		}
		_owner.addInt((byte) -get_addint);// 智力
		_owner.addCha((byte) -get_addcha);// 魅力

		addMr += eq.getMr();
		// 精靈盾牌
		if (eq.getName().equals("$187") && _owner.isElf()) {
			addMr += 5;
		}
		if (addMr != 0) {
			_owner.addMr(-addMr);
			_owner.sendPackets(new S_SPMR(_owner));
		}

		final int addSp1 = item.get_addsp();
		final int influenceSp = item.getInfluenceSp();
		if ((addSp1 != 0) || (influenceSp != 0)) {
			_owner.addSp(-(addSp1 + (eq.getEnchantLevel() * influenceSp)));
			_owner.sendPackets(new S_SPMR(_owner));
		}

		// 具備加速
		final boolean isHasteItem = item.isHasteItem();

		if (isHasteItem) {
			_owner.addHasteItemEquipped(-1);
			if (_owner.getHasteItemEquipped() == 0) {
				_owner.setMoveSpeed(0);
				_owner.sendPacketsAll(new S_SkillHaste(_owner.getId(), 0, 0));
			}
		}

		switch (item.getType2()) {
		case 1:// 武器
			removeWeapon(eq);
			ItemClass.get().item_weapon(false, _owner, eq);
			break;

		case 2:// 防具
			removeMagic(_owner.getId(), eq);
			removeArmor(eq);
			ItemClass.get().item_armor(false, _owner, eq);
			break;
		}

		// 移除古文字效果 (fixed by terry0412)
		if ((eq.get_power_name() != null) && (eq.get_power_name().get_power_name() != null)) {
			for (final L1ItemPower_text power : _owner.get_powers().values()) {
				if (!power.check_pc(_owner)) {
					_owner.remove_power(power);
				}
			}
		}
		// 經驗加倍指數 (以%計算) by terry0412
		_owner.setExpPoint(_owner.getExpPoint() - item.getExpPoint());

		if (get_addstr != 0 || get_adddex != 0 || get_addcon != 0 || get_addwis != 0 || get_addint != 0 || item.getWeightReduction() != 0) {
			_owner.sendDetails();
		}
	}

	/**
	 * 設置(魔法道具)
	 * 
	 * @param item
	 */
	private void setMagic(final L1ItemInstance item) {
		switch (item.getItemId()) {
		case 20077: // 隱身斗篷
		case 120077: // 隱身斗篷
		case 20062: // 炎魔的血光斗篷
			if (!_owner.hasSkillEffect(INVISIBILITY)) {
				_owner.killSkillEffectTimer(BLIND_HIDING);
				_owner.setSkillEffect(INVISIBILITY, 0);
				_owner.sendPackets(new S_Invis(_owner.getId(), 1));
				_owner.broadcastPacketAll(new S_RemoveObject(_owner));
			}
			break;

		case 20288: // 傳送控制戒指
			_owner.sendPackets(new S_Ability(1, true));
			break;

		case 20281: // 變形控制戒指
			// this._owner.sendPackets(new S_Ability(2, true));
			break;
		case 20284:
			_owner.sendPackets(new S_Ability(5, true));
			break;

		case 20383: // 軍馬頭盔
			if (item.getChargeCount() != 0) {
				// 可用次數 -1
				item.setChargeCount(item.getChargeCount() - 1);
				_owner.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
			}
			if (_owner.hasSkillEffect(STATUS_BRAVE)) {
				_owner.killSkillEffectTimer(STATUS_BRAVE);
				_owner.sendPacketsAll(new S_SkillBrave(_owner.getId(), 0, 0));
				_owner.setBraveSpeed(0);
			}
			break;

		case 20013: // 敏捷魔法頭盔
			if (!_owner.isSkillMastery(PHYSICAL_ENCHANT_DEX)) {
				_owner.sendPackets(new S_AddSkill(_owner, PHYSICAL_ENCHANT_DEX));
			}
			if (!_owner.isSkillMastery(HASTE)) {
				_owner.sendPackets(new S_AddSkill(_owner, HASTE));
			}
			break;

		case 20014: // 治癒魔法頭盔
			if (!_owner.isSkillMastery(HEAL)) {
				_owner.sendPackets(new S_AddSkill(_owner, HEAL));
			}
			if (!_owner.isSkillMastery(EXTRA_HEAL)) {
				_owner.sendPackets(new S_AddSkill(_owner, EXTRA_HEAL));
			}
			break;

		case 20015: // 力量魔法頭盔
			if (!_owner.isSkillMastery(ENCHANT_WEAPON)) {
				_owner.sendPackets(new S_AddSkill(_owner, ENCHANT_WEAPON));
			}
			if (!_owner.isSkillMastery(DETECTION)) {
				_owner.sendPackets(new S_AddSkill(_owner, DETECTION));
			}
			if (!_owner.isSkillMastery(PHYSICAL_ENCHANT_STR)) {
				_owner.sendPackets(new S_AddSkill(_owner, PHYSICAL_ENCHANT_STR));
			}
			break;

		case 20008: // 小型風之頭盔
			if (!_owner.isSkillMastery(HASTE)) {
				_owner.sendPackets(new S_AddSkill(_owner, HASTE));
			}
			break;

		case 20023: // 風之頭盔
			if (!_owner.isSkillMastery(GREATER_HASTE)) {
				_owner.sendPackets(new S_AddSkill(_owner, GREATER_HASTE));
			}
			break;

		default:// 其他道具
			break;
		}
	}

	/**
	 * 解除(魔法道具)
	 * 
	 * @param objectId
	 * @param item
	 */
	private void removeMagic(final int objectId, final L1ItemInstance item) {
		switch (item.getItemId()) {
		case 20077: // 隱身斗篷
		case 120077: // 隱身斗篷
		case 20062: // 炎魔的血光斗篷
			_owner.delInvis(); // 隱身解除
			break;

		case 20288: // 傳送控制戒指
			_owner.sendPackets(new S_Ability(1, false));
			break;

		case 20281: // 變形控制戒指
			// this._owner.sendPackets(new S_Ability(2, false));
			break;
		case 20284:
			_owner.sendPackets(new S_Ability(5, false));
			break;

		case 20383: // 軍馬頭盔
			break;

		case 20013: // 敏捷魔法頭盔
			if (!CharSkillReading.get().spellCheck(objectId, PHYSICAL_ENCHANT_DEX)) {
				_owner.sendPackets(new S_DelSkill(_owner, PHYSICAL_ENCHANT_DEX));
			}
			if (!CharSkillReading.get().spellCheck(objectId, HASTE)) {
				_owner.sendPackets(new S_DelSkill(_owner, HASTE));
			}
			break;

		case 20014: // 治癒魔法頭盔
			if (!CharSkillReading.get().spellCheck(objectId, HEAL)) {
				_owner.sendPackets(new S_DelSkill(_owner, HEAL));
			}
			if (!CharSkillReading.get().spellCheck(objectId, EXTRA_HEAL)) {
				_owner.sendPackets(new S_DelSkill(_owner, EXTRA_HEAL));
			}
			break;

		case 20015: // 力量魔法頭盔
			if (!CharSkillReading.get().spellCheck(objectId, ENCHANT_WEAPON)) {
				_owner.sendPackets(new S_DelSkill(_owner, ENCHANT_WEAPON));
			}
			if (!CharSkillReading.get().spellCheck(objectId, DETECTION)) {
				_owner.sendPackets(new S_DelSkill(_owner, DETECTION));
			}
			if (!CharSkillReading.get().spellCheck(objectId, PHYSICAL_ENCHANT_STR)) {
				_owner.sendPackets(new S_DelSkill(_owner, PHYSICAL_ENCHANT_STR));
			}
			break;

		case 20008: // 小型風之頭盔
			if (!CharSkillReading.get().spellCheck(objectId, HASTE)) {
				_owner.sendPackets(new S_DelSkill(_owner, HASTE));
			}
			break;

		case 20023: // 風之頭盔
			if (!CharSkillReading.get().spellCheck(objectId, GREATER_HASTE)) {
				_owner.sendPackets(new S_DelSkill(_owner, GREATER_HASTE));
			}
			break;

		default:// 其他道具
			break;
		}
	}
}