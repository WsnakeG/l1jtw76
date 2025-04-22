package com.lineage.server.templates;

/**
 * @author roy 道具強化系統框架
 */
public class L1ServerEtcItem {
	public int id;
	public int itemid;
	public String itemname;
	public int addMaxHP;
	public int addMaxMP;
	public int add_str;
	public int add_con;
	public int add_dex;
	public int add_int;
	public int add_wis;
	public int add_cha;
	public int add_hpr;
	public int add_mpr;
	public int add_sp;
	public int add_ac;
	public int m_def;
	public int dmg_modifier;
	public int bow_dmg_modifier;
	public int double_dmg_chance;
	public int itemtime;
	public boolean deleteafteruse;
	public int magic_reduction_dmg;
	public int reduction_dmg;
	public int gif;
	
	// 新增能力道具系統 八種特殊能力 Erics4179 160829
	public int physicsDmgUp;
	public int magicDmgUp;
	public int physicsDmgDown;
	public int magicDmgDown;
	public int magicHitUp;
	public int magicHitDown;
	public int physicsDoubleHit;
	public int magicDoubleHit;
	
	// 新增幸運度 Erics4179 160901
	public int InfluenceLuck;
}
