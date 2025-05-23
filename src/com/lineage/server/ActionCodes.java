package com.lineage.server;

/**
 * ActionCodes
 * 
 * @author dexc
 */
public class ActionCodes {

	public ActionCodes() {
	}

	public static final int ACTION_Appear = 4;

	public static final int ACTION_Hide = 11;

	public static final int ACTION_AntharasHide = 20;

	/**
	 * 移動動作
	 */
	public static final int ACTION_Walk = 0;

	/**
	 * 攻擊動作
	 */
	public static final int ACTION_Attack = 1;

	/**
	 * 受傷動作
	 */
	public static final int ACTION_Damage = 2;

	/**
	 * 回復動作
	 */
	public static final int ACTION_Idle = 3;

	/**
	 * 持劍移動
	 */
	public static final int ACTION_SwordWalk = 4;

	public static final int ACTION_SwordAttack = 5;

	public static final int ACTION_SwordDamage = 6;

	public static final int ACTION_SwordIdle = 7;

	/**
	 * 死亡的動作
	 */
	public static final int ACTION_Die = 8;

	/**
	 * 持斧移動
	 */
	public static final int ACTION_AxeWalk = 11;

	public static final int ACTION_AxeAttack = 12;

	public static final int ACTION_AxeDamage = 13;

	public static final int ACTION_AxeIdle = 14;

	public static final int ACTION_HideDamage = 13;

	public static final int ACTION_HideIdle = 14;

	/**
	 * 檢取物品
	 */
	public static final int ACTION_Pickup = 15;

	public static final int ACTION_Throw = 16;

	public static final int ACTION_Wand = 17;

	/**
	 * 技能動作(18)
	 */
	public static final int ACTION_SkillAttack = 18;

	/**
	 * 技能動作(19)
	 */
	public static final int ACTION_SkillBuff = 19;

	/**
	 * 持弓移動
	 */
	public static final int ACTION_BowWalk = 20;

	/**
	 * 射箭動作
	 */
	public static final int ACTION_BowAttack = 21;

	public static final int ACTION_BowDamage = 22;

	public static final int ACTION_BowIdle = 23;

	/**
	 * 持槍移動
	 */
	public static final int ACTION_SpearWalk = 24;

	public static final int ACTION_SpearAttack = 25;

	public static final int ACTION_SpearDamage = 26;

	public static final int ACTION_SpearIdle = 27;

	/**
	 * 開箱
	 */
	public static final int ACTION_On = 28;

	/**
	 * 關箱
	 */
	public static final int ACTION_Off = 29;

	/**
	 * 開門
	 */
	public static final int ACTION_Open = 28;

	/**
	 * 關門
	 */
	public static final int ACTION_Close = 29;

	public static final int ACTION_South = 28;

	public static final int ACTION_West = 29;

	/**
	 * 技能動作(30 - 重擊)
	 */
	public static final int ACTION_AltAttack = 30;

	public static final int ACTION_SpellDirectionExtra = 31;

	/**
	 * 塔損壞(階段1)
	 */
	public static final int ACTION_TowerCrack1 = 32;

	/**
	 * 塔損壞(階段2)
	 */
	public static final int ACTION_TowerCrack2 = 33;

	/**
	 * 塔損壞(階段3)
	 */
	public static final int ACTION_TowerCrack3 = 34;

	/**
	 * 塔損壞(階段4)
	 */
	public static final int ACTION_TowerCrack4 = 35;

	/**
	 * 塔損壞(完全)
	 */
	public static final int ACTION_TowerDie = 36;

	/**
	 * 門損壞(階段1)
	 */
	public static final int ACTION_DoorAction1 = 32;

	/**
	 * 門損壞(階段2)
	 */
	public static final int ACTION_DoorAction2 = 33;

	/**
	 * 門損壞(階段3)
	 */
	public static final int ACTION_DoorAction3 = 34;

	/**
	 * 門損壞(階段4)
	 */
	public static final int ACTION_DoorAction4 = 35;

	/**
	 * 門損壞(階段5)
	 */
	public static final int ACTION_DoorAction5 = 36;

	/**
	 * 門損壞(完全)
	 */
	public static final int ACTION_DoorDie = 37;

	/**
	 * 持魔杖移動
	 */
	public static final int ACTION_StaffWalk = 40;

	public static final int ACTION_StaffAttack = 41;

	public static final int ACTION_StaffDamage = 42;

	public static final int ACTION_StaffIdle = 43;

	public static final int ACTION_Moveup = 44;

	public static final int ACTION_Movedown = 45;

	/**
	 * 持匕首移動
	 */
	public static final int ACTION_DaggerWalk = 46;

	public static final int ACTION_DaggerAttack = 47;

	public static final int ACTION_DaggerDamage = 48;

	public static final int ACTION_DaggerIdle = 49;

	/**
	 * 持雙手劍移動
	 */
	public static final int ACTION_TwoHandSwordWalk = 50;

	public static final int ACTION_TwoHandSwordAttack = 51;

	public static final int ACTION_TwoHandSwordDamage = 52;

	public static final int ACTION_TwoHandSwordIdle = 53;

	/**
	 * 持雙刀移動
	 */
	public static final int ACTION_EdoryuWalk = 54;

	public static final int ACTION_EdoryuAttack = 55;

	public static final int ACTION_EdoryuDamage = 56;

	public static final int ACTION_EdoryuIdle = 57;

	/**
	 * 持雙爪移動
	 */
	public static final int ACTION_ClawWalk = 58;

	public static final int ACTION_ClawAttack = 59;

	public static final int ACTION_ClawIdle = 61;

	public static final int ACTION_ClawDamage = 60;

	/**
	 * 持鐵手甲移動
	 */
	public static final int ACTION_ThrowingKnifeWalk = 62;

	public static final int ACTION_ThrowingKnifeAttack = 63;

	public static final int ACTION_ThrowingKnifeDamage = 64;

	public static final int ACTION_ThrowingKnifeIdle = 65;

	/**
	 * Alt+4 人物動作
	 */
	public static final int ACTION_Think = 66; // Alt+4

	/**
	 * Alt+3 人物動作
	 */
	public static final int ACTION_Aggress = 67; // Alt+3

	/**
	 * Alt+1 人物動作
	 */
	public static final int ACTION_Salute = 68; // Alt+1

	/**
	 * Alt+2 人物動作
	 */
	public static final int ACTION_Cheer = 69; // Alt+2

	/**
	 * 出售物品
	 */
	public static final int ACTION_Shop = 70;

	/**
	 * 釣魚
	 */
	public static final int ACTION_Fishing = 71;

	public static final int ACTION_ChainswordWalk = 83;

	public static final int ACTION_ChainswordAttack = 84;

	public static final int ACTION_ChainswordDamage = 85;

	public static final int ACTION_ChainswordIdle = 86;

}