package com.lineage.server.templates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SPMR;

/**
 * 古文字能力紀錄
 * 
 * @author daien
 */
public class L1ItemPower_text {

	private static final Log _log = LogFactory.getLog(L1ItemPower_text.class);

	private int _id;

	private int[] _power_ids;// 文字組合

	private int _ac;// 防禦力

	private int _hp;// HP

	private int _mp;// MP

	private int _hpr;// HPR

	private int _mpr;// MPR

	private int _mr;// 抗魔

	private int _str;// 力

	private int _dex;// 敏

	private int _con;// 體

	private int _inter;// 智

	private int _wis;// 精

	private int _cha;// 魅

	private int _sp;// 魔功

	private int _hit;// 命中

	private int _dmgup;// 物理攻擊

	private int _bowhit;// 弓的命中

	private int _bowdmgup;// 弓的攻擊

	private int _dice_dmg;// 機率給予爆擊

	private int _dmg;// 機率給予爆擊質

	private int _dodge;// 迴避攻擊

	private int _dice_hp;// 機率-吸血

	private int _sucking_hp;// 機率-吸血質

	private int _dice_mp;// 機率-吸魔

	private int _sucking_mp;// 機率-吸魔質

	private int _double_dmg;// 機率發動加倍的攻擊力

	private int _lift;// 機率可以將對方的武防裝備解除

	private int _defense_water;// 水

	private int _defense_wind;// 風

	private int _defense_fire;// 火

	private int _defense_earth;// 地

	private int _regist_stun;// 暈眩耐性

	private int _regist_stone;// 石化耐性

	private int _regist_sleep;// 睡眠耐性

	private int _regist_freeze;// 寒冰耐性

	private int _regist_sustain;// 支撐耐性;

	private int _regist_blind;// 暗黑耐性

	private int[] _gfx;// 動畫組合

	private String _msg;// 效果文字

	// Erics4179 160602 古文字系統新增八種能力
	private int _physicsDmgUp; // 物理傷害增加+%

	private int _magicDmgUp; // 魔法傷害增加+%

	private int _physicsDmgDown; // 物理傷害減免+%

	private int _magicDmgDown; // 魔法傷害減免+%

	private int _magicHitUp; // 有害魔法成功率+%

	private int _magicHitDown; // 抵抗有害魔法成功率+%

	private int _physicsDoubleHit; // 物理暴擊發動機率+% (發動後普攻傷害*1.5倍)

	private int _magicDoubleHit; // 魔法暴擊發動機率+% (發動後技能傷害*1.5倍)

	public boolean check_pc(final L1PcInstance pc) {
		try {
			int is = 0;
			final int length = _power_ids.length;
			final int[] ch = new int[length];
			System.arraycopy(_power_ids, 0, ch, 0, length);

			final L1ItemInstance weapon = pc.getWeapon();
			if (check_item(weapon, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor1 = pc.getInventory().getItemEquipped(2, 1);// 頭盔
			if (check_item(armor1, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor2 = pc.getInventory().getItemEquipped(2, 2);// 盔甲
			if (check_item(armor2, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor3 = pc.getInventory().getItemEquipped(2, 3);// 内衣
			if (check_item(armor3, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor4 = pc.getInventory().getItemEquipped(2, 4);// 斗篷
			if (check_item(armor4, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor5 = pc.getInventory().getItemEquipped(2, 5);// 手套
			if (check_item(armor5, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor6 = pc.getInventory().getItemEquipped(2, 6);// 靴子
			if (check_item(armor6, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor7 = pc.getInventory().getItemEquipped(2, 7);// 盾
			if (check_item(armor7, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor8 = pc.getInventory().getItemEquipped(2, 8);// 項鍊
			if (check_item(armor8, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor9 = pc.getInventory().getItemEquipped(2, 9);// 戒指1
			if (check_item(armor9, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor10 = pc.getInventory().getItemEquipped(2, 10);// 腰帶
			if (check_item(armor10, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor11 = pc.getInventory().getItemEquipped(2, 11);// 耳環
			if (check_item(armor11, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor12 = pc.getInventory().getItemEquipped(2, 12);// 戒指2
			if (check_item(armor12, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}
			final L1ItemInstance armor13 = pc.getInventory().getItemEquipped(2, 13);// 臂甲
			if (check_item(armor13, ch)) {
				is += 1;
				if (is == length) {
					return true;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	// 裝備檢查
	private boolean check_item(final L1ItemInstance item, final int[] ch) {
		try {
			if (item == null) {
				return false;
			}
			if (item.get_power_name() != null) {
				for (int i = 0; i < ch.length; i++) {
					if (item.get_power_name().get_power_id() == ch[i]) {
						ch[i] = -1;
						return true;
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 套裝達成 設置能力
	 */
	public void add_pc_power(final L1PcInstance pc) {
		try {
			if (_ac != 0) {
				pc.addAc(_ac);
			}
			if (_hp != 0) {
				pc.addMaxHp(_hp);
			}
			if (_mp != 0) {
				pc.addMaxMp(_mp);
			}
			if (_hpr != 0) {
				pc.addHpr(_hpr);
			}
			if (_mpr != 0) {
				pc.addMpr(_mpr);
			}
			if (_mr != 0) {
				pc.addMr(_mr);
				// 更改人物魔法攻击与魔法防御
				pc.sendPackets(new S_SPMR(pc));
			}
			if (_str != 0) {
				pc.addStr(_str);
			}
			if (_dex != 0) {
				pc.addDex(_dex);
			}
			if (_con != 0) {
				pc.addCon(_con);
			}
			if (_inter != 0) {
				pc.addInt(_inter);
			}
			if (_wis != 0) {
				pc.addWis(_wis);
			}
			if (_cha != 0) {
				pc.addCha(_cha);
			}
			if (_sp != 0) {
				pc.addSp(_sp);
				// 更改人物魔法攻击与魔法防御
				pc.sendPackets(new S_SPMR(pc));
			}
			if (_hit != 0) {
				pc.addHitup(_hit);
			}
			if (_dmgup != 0) {
				pc.addDmgup(_dmgup);
			}
			if (_bowhit != 0) {
				pc.addBowHitup(_bowhit);
			}
			if (_bowdmgup != 0) {
				pc.addBowDmgup(_bowdmgup);
			}
			if ((_dice_dmg != 0) && (_dmg != 0)) {// 機率給予爆擊 / 機率給予爆擊質
				pc.set_dmgAdd(_dice_dmg, _dmg);
			}
			if (_dodge != 0) {// 迴避攻擊
				pc.set_evasion(_dodge);
			}
			if ((_dice_hp != 0) && (_sucking_hp != 0)) {// 機率-吸血 / 機率-吸血質
				pc.add_dice_hp(_dice_hp, _sucking_hp);
			}
			if ((_dice_mp != 0) && (_sucking_mp != 0)) {// 機率-吸魔 / 機率-吸魔質
				pc.add_dice_mp(_dice_mp, _sucking_mp);
			}
			if (_double_dmg != 0) {// 機率發動加倍的攻擊力
				pc.add_double_dmg(_double_dmg);
			}
			if (_lift != 0) {// 機率可以將對方的武防裝備解除
				pc.add_lift(_lift);
			}
			if (_defense_water != 0) {
				pc.addWater(_defense_wind);
			}
			if (_defense_wind != 0) {
				pc.addWind(_defense_wind);
			}
			if (_defense_fire != 0) {
				pc.addFire(_defense_fire);
			}
			if (_defense_earth != 0) {
				pc.addEarth(_defense_earth);
			}
			if (_regist_stun != 0) {
				pc.addRegistStun(_regist_stun);
			}
			if (_regist_stone != 0) {
				pc.addRegistStone(_regist_stone);
			}
			if (_regist_sleep != 0) {
				pc.addRegistSleep(_regist_sleep);
			}
			if (_regist_freeze != 0) {
				pc.addRegistFreeze(_regist_freeze);
			}
			if (_regist_sustain != 0) {
				pc.addRegistSustain(_regist_sustain);
			}
			if (_regist_blind != 0) {
				pc.addRegistBlind(_regist_blind);
			}
			if (_physicsDmgUp != 0) {
				pc.addPhysicsDmgUp(_physicsDmgUp);// 物理傷害增加+%
			}
			if (_magicDmgUp != 0) {
				pc.addMagicDmgUp(_magicDmgUp);// 魔法傷害增加+%
			}
			if (_physicsDmgDown != 0) {
				pc.addPhysicsDmgDown(_physicsDmgDown); // 物理傷害減免+%
			}
			if (_magicDmgDown != 0) {
				pc.addMagicDmgDown(_magicDmgDown); // 魔法傷害減免+%
			}
			if (_magicHitUp != 0) {
				pc.addMagicHitUp(_magicHitUp); // 有害魔法成功率+%
			}
			if (_magicHitDown != 0) {
				pc.addMagicHitDown(_magicHitDown); // 抵抗有害魔法成功率+%
			}
			if (_physicsDoubleHit != 0) {
				pc.addPhysicsDoubleHit(_physicsDoubleHit); // 物理暴擊發動機率+%
															// (發動後普攻傷害*1.5倍)
			}
			if (_magicDoubleHit != 0) {
				pc.addMagicDoubleHit(_magicDoubleHit); // 魔法暴擊發動機率+%
														// (發動後技能傷害*1.5倍)
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void remove_pc_power(final L1PcInstance pc) {
		try {
			if (_ac != 0) {
				pc.addAc(-_ac);
			}
			if (_hp != 0) {
				pc.addMaxHp(-_hp);
			}
			if (_mp != 0) {
				pc.addMaxMp(-_mp);
			}
			if (_hpr != 0) {
				pc.addHpr(-_hpr);
			}
			if (_mpr != 0) {
				pc.addMpr(-_mpr);
			}
			if (_mr != 0) {
				pc.addMr(-_mr);
				// 更改人物魔法攻击与魔法防御
				pc.sendPackets(new S_SPMR(pc));
			}
			if (_str != 0) {
				pc.addStr(-_str);
			}
			if (_dex != 0) {
				pc.addDex(-_dex);
			}
			if (_con != 0) {
				pc.addCon(-_con);
			}
			if (_inter != 0) {
				pc.addInt(-_inter);
			}
			if (_wis != 0) {
				pc.addWis(-_wis);
			}
			if (_cha != 0) {
				pc.addCha(-_cha);
			}
			if (_sp != 0) {
				pc.addSp(-_sp);
				// 更改人物魔法攻击与魔法防御
				pc.sendPackets(new S_SPMR(pc));
			}
			if (_hit != 0) {
				pc.addHitup(-_hit);
			}
			if (_dmgup != 0) {
				pc.addDmgup(-_dmgup);
			}
			if (_bowhit != 0) {
				pc.addBowHitup(-_bowhit);
			}
			if (_bowdmgup != 0) {
				pc.addBowDmgup(-_bowdmgup);
			}
			if ((_dice_dmg != 0) && (_dmg != 0)) {// 機率給予爆擊 / 機率給予爆擊質
				pc.set_dmgAdd(-_dice_dmg, -_dmg);
			}
			if (_dodge != 0) {// 迴避攻擊
				pc.set_evasion(-_dodge);
			}
			if ((_dice_hp != 0) && (_sucking_hp != 0)) {// 機率-吸血 / 機率-吸血質
				pc.add_dice_hp(-_dice_hp, -_sucking_hp);
			}
			if ((_dice_mp != 0) && (_sucking_mp != 0)) {// 機率-吸魔 / 機率-吸魔質
				pc.add_dice_mp(-_dice_mp, -_sucking_mp);
			}
			if (_double_dmg != 0) {// 機率發動加倍的攻擊力
				pc.add_double_dmg(-_double_dmg);
			}
			if (_lift != 0) {// 機率可以將對方的武防裝備解除
				pc.add_lift(-_lift);
			}
			if (_defense_water != 0) {
				pc.addWater(-_defense_wind);
			}
			if (_defense_wind != 0) {
				pc.addWind(-_defense_wind);
			}
			if (_defense_fire != 0) {
				pc.addFire(-_defense_fire);
			}
			if (_defense_earth != 0) {
				pc.addEarth(-_defense_earth);
			}
			if (_regist_stun != 0) {
				pc.addRegistStun(-_regist_stun);
			}
			if (_regist_stone != 0) {
				pc.addRegistStone(-_regist_stone);
			}
			if (_regist_sleep != 0) {
				pc.addRegistSleep(-_regist_sleep);
			}
			if (_regist_freeze != 0) {
				pc.addRegistFreeze(-_regist_freeze);
			}
			if (_regist_sustain != 0) {
				pc.addRegistSustain(-_regist_sustain);
			}
			if (_regist_blind != 0) {
				pc.addRegistBlind(-_regist_blind);
			}
			if (_physicsDmgUp != 0) {
				pc.addPhysicsDmgUp(-_physicsDmgUp);// 物理傷害增加+%
			}
			if (_magicDmgUp != 0) {
				pc.addMagicDmgUp(-_magicDmgUp);// 魔法傷害增加+%
			}
			if (_physicsDmgDown != 0) {
				pc.addPhysicsDmgDown(-_physicsDmgDown); // 物理傷害減免+%
			}
			if (_magicDmgDown != 0) {
				pc.addMagicDmgDown(-_magicDmgDown); // 魔法傷害減免+%
			}
			if (_magicHitUp != 0) {
				pc.addMagicHitUp(-_magicHitUp); // 有害魔法成功率+%
			}
			if (_magicHitDown != 0) {
				pc.addMagicHitDown(-_magicHitDown); // 抵抗有害魔法成功率+%
			}
			if (_physicsDoubleHit != 0) {
				pc.addPhysicsDoubleHit(-_physicsDoubleHit); // 物理暴擊發動機率+%
															// (發動後普攻傷害*1.5倍)
			}
			if (_magicDoubleHit != 0) {
				pc.addMagicDoubleHit(-_magicDoubleHit); // 魔法暴擊發動機率+%
														// (發動後技能傷害*1.5倍)
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public int[] getPower_id() {
		return _power_ids;
	}

	public void setPower_id(final int[] out) {
		_power_ids = out;
	}

	public int getAc() {
		return _ac;
	}

	public void setAc(final int ac) {
		_ac = ac;
	}

	public int getHp() {
		return _hp;
	}

	public void setHp(final int hp) {
		_hp = hp;
	}

	public int getMp() {
		return _mp;
	}

	public void setMp(final int mp) {
		_mp = mp;
	}

	public int getHpr() {
		return _hpr;
	}

	public void setHpr(final int hpr) {
		_hpr = hpr;
	}

	public int getMpr() {
		return _mpr;
	}

	public void setMpr(final int mpr) {
		_mpr = mpr;
	}

	public int getMr() {
		return _mr;
	}

	public void setMr(final int mr) {
		_mr = mr;
	}

	public int getStr() {
		return _str;
	}

	public void setStr(final int str) {
		_str = str;
	}

	public int getDex() {
		return _dex;
	}

	public void setDex(final int dex) {
		_dex = dex;
	}

	public int getCon() {
		return _con;
	}

	public void setCon(final int con) {
		_con = con;
	}

	public int getInt() {
		return _inter;
	}

	public void setInt(final int inter) {
		_inter = inter;
	}

	public int getCha() {
		return _cha;
	}

	public void setCha(final int cha) {
		_cha = cha;
	}

	public int getWis() {
		return _wis;
	}

	public void setWis(final int wis) {
		_wis = wis;
	}

	public int getSp() {
		return _sp;
	}

	public void setSp(final int sp) {
		_sp = sp;
	}

	public int getHit() {
		return _hit;
	}

	public void setHit(final int hit) {
		_hit = hit;
	}

	public int getDmgup() {
		return _dmgup;
	}

	public void setDmgup(final int dmgup) {
		_dmgup = dmgup;
	}

	public int getBowhit() {
		return _bowhit;
	}

	public void setBowhit(final int bowhit) {
		_bowhit = bowhit;
	}

	public int getBowdmgup() {
		return _bowdmgup;
	}

	public void setBowdmgup(final int bowdmgup) {
		_bowdmgup = bowdmgup;
	}

	public int getDice_dmg() {
		return _dice_dmg;
	}

	public void setDice_dmg(final int dice_dmg) {
		_dice_dmg = dice_dmg;
	}

	public int getDmg() {
		return _dmg;
	}

	public void setDmg(final int dmg) {
		_dmg = dmg;
	}

	public int getDodge() {
		return _dodge;
	}

	public void setDodge(final int dodge) {
		_dodge = dodge;
	}

	public int getDice_hp() {
		return _dice_hp;
	}

	public void setDice_hp(final int dice_hp) {
		_dice_hp = dice_hp;
	}

	public int getSucking_hp() {
		return _sucking_hp;
	}

	public void setSucking_hp(final int sucking_hp) {
		_sucking_hp = sucking_hp;
	}

	public int getDice_mp() {
		return _dice_mp;
	}

	public void setDice_mp(final int dice_mp) {
		_dice_mp = dice_mp;
	}

	public int getSucking_mp() {
		return _sucking_mp;
	}

	public void setSucking_mp(final int sucking_mp) {
		_sucking_mp = sucking_mp;
	}

	public int getDouble_dmg() {
		return _double_dmg;
	}

	public void setDouble_dmg(final int double_dmg) {
		_double_dmg = double_dmg;
	}

	public int getLift() {
		return _lift;
	}

	public void setLift(final int lift) {
		_lift = lift;
	}

	public int getDefense_water() {
		return _defense_water;
	}

	public void setDefense_water(final int defense_water) {
		_defense_water = defense_water;
	}

	public int getDefense_wind() {
		return _defense_wind;
	}

	public void setDefense_wind(final int defense_wind) {
		_defense_wind = defense_wind;
	}

	public int getDefense_fire() {
		return _defense_fire;
	}

	public void setDefense_fire(final int defense_fire) {
		_defense_fire = defense_fire;
	}

	public int getDefense_earth() {
		return _defense_earth;
	}

	public void setDefense_earth(final int defense_earth) {
		_defense_earth = defense_earth;
	}

	public int getRegist_stun() {
		return _regist_stun;
	}

	public void setRegist_stun(final int regist_stun) {
		_regist_stun = regist_stun;
	}

	public int getRegist_stone() {
		return _regist_stone;
	}

	public void setRegist_stone(final int regist_stone) {
		_regist_stone = regist_stone;
	}

	public int getRegist_sleep() {
		return _regist_sleep;
	}

	public void setRegist_sleep(final int regist_sleep) {
		_regist_sleep = regist_sleep;
	}

	public int getRegist_freeze() {
		return _regist_freeze;
	}

	public void setRegist_freeze(final int regist_freeze) {
		_regist_freeze = regist_freeze;
	}

	public int getRegist_sustain() {
		return _regist_sustain;
	}

	public void setRegist_sustain(final int regist_sustain) {
		_regist_sustain = regist_sustain;
	}

	public int getRegist_blind() {
		return _regist_blind;
	}

	public void setRegist_blind(final int regist_blind) {
		_regist_blind = regist_blind;
	}

	public int[] getGfx() {
		return _gfx;
	}

	public void setGfx(final int[] out) {
		_gfx = out;
	}

	public String getMsg() {
		return _msg;
	}

	public void setMsg(final String msg) {
		_msg = msg;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(final int _id) {
		this._id = _id;
	}

	// Erics4179 160602 古文字系統新增八種能力
	// 物理傷害增加+%
	public int physicsDmgUp() {
		return _physicsDmgUp;
	}

	public void setphysicsDmgUp(final int physicsDmgUp) {
		_physicsDmgUp = physicsDmgUp;
	}

	// 魔法傷害增加+%
	public int magicDmgUp() {
		return _magicDmgUp;
	}

	public void setmagicDmgUp(final int magicDmgUp) {
		_magicDmgUp = magicDmgUp;
	}

	// 物理傷害減免+%
	public int physicsDmgDown() {
		return _physicsDmgDown;
	}

	public void setphysicsDmgDown(final int physicsDmgDown) {
		_physicsDmgDown = physicsDmgDown;
	}

	// 魔法傷害減免+%
	public int magicDmgDown() {
		return _magicDmgDown;
	}

	public void setmagicDmgDown(final int magicDmgDown) {
		_magicDmgDown = magicDmgDown;
	}

	// 有害魔法成功率+%
	public int magicHitUp() {
		return _magicHitUp;
	}

	public void setmagicHitUp(final int magicHitUp) {
		_magicHitUp = magicHitUp;
	}

	// 抵抗有害魔法成功率+%
	public int magicHitDown() {
		return _magicHitDown;
	}

	public void setmagicHitDown(final int magicHitDown) {
		_magicHitDown = magicHitDown;
	}

	// 物理暴擊發動機率+% (發動後普攻傷害*1.5倍)
	public int physicsDoubleHit() {
		return _physicsDoubleHit;
	}

	public void setphysicsDoubleHit(final int physicsDoubleHit) {
		_physicsDoubleHit = physicsDoubleHit;
	}

	// 魔法暴擊發動機率+% (發動後技能傷害*1.5倍)
	public int magicDoubleHit() {
		return _magicDoubleHit;
	}

	public void setmagicDoubleHit(final int magicDoubleHit) {
		_magicDoubleHit = magicDoubleHit;
	}
}
