package com.lineage.server.templates;

/**
 * 道具狀態系統
 * 
 */
public class L1ItemBuff {

	private String _startmsg;
	private String _stopmsg;
	private int _viplevel;
	private int _type;
	private int _type_mod;
	private int _buff_time;
	private int _buff_gfx;
	private int _buff_iconid;
	private int _buff_stringid;
	private int _open_string;
	private boolean _buff_save;
	private int _poly;
	private int _str;
	private int _dex;
	private int _con;
	private int _intel;
	private int _wis;
	private int _cha;
	private int _ac;
	private int _hp;
	private int _mp;
	private int _hpr;
	private int _mpr;
	private int _mr;
	private int _sp;
	private int _dmg;
	private int _bow_dmg;
	private int _hit;
	private int _bow_hit;
	private int _dmg_r;
	private int _magic_r;
	private int _fire;
	private int _wind;
	private int _earth;
	private int _water;
	private int _stun;
	private int _stone;
	private int _sleep;
	private int _freeze;
	private int _sustain;
	private int _blind;
	private int _exp;
	private int _pvpdmg;
	private int _pvpdmg_r;
	
	public boolean _deleteafteruse;
	public int _double_dmg_chance;
	public int _physicsDmgUp;
	public int _magicDmgUp;
	public int _physicsDmgDown;
	public int _magicDmgDown;
	public int _magicHitUp;
	public int _magicHitDown;
	public int _physicsDoubleHit;
	public int _magicDoubleHit;
	public int _InfluenceLuck;

	public L1ItemBuff() {
		this._startmsg = null;
		this._stopmsg = null;
		this._viplevel = 0;
		this._type = 0;
		this._type_mod = 0;
		this._buff_time = 0;
		this._buff_gfx = 0;
		this._buff_iconid = 0;
		this._buff_stringid = 0;
		this._open_string = 0;
		this._buff_save = false;
		this._poly = 0;
		this._str = 0;
		this._dex = 0;
		this._con = 0;
		this._intel = 0;
		this._wis = 0;
		this._cha = 0;
		this._ac = 0;
		this._hp = 0;
		this._mp = 0;
		this._hpr = 0;
		this._mpr = 0;
		this._mr = 0;
		this._sp = 0;
		this._dmg = 0;
		this._bow_dmg = 0;
		this._hit = 0;
		this._bow_hit = 0;
		this._dmg_r = 0;
		this._magic_r = 0;
		this._fire = 0;
		this._wind = 0;
		this._earth = 0;
		this._water = 0;
		this._stun = 0;
		this._stone = 0;
		this._sleep = 0;
		this._freeze = 0;
		this._sustain = 0;
		this._blind = 0;
		this._exp = 0;
		this._pvpdmg = 0;
		this._pvpdmg_r = 0;
		
		this._deleteafteruse = false;
		this._double_dmg_chance = 0;
		this._physicsDmgUp = 0;
		this._magicDmgUp = 0;
		this._physicsDmgDown = 0;
		this._magicDmgDown = 0;
		this._magicHitUp = 0;
		this._magicHitDown = 0;
		this._physicsDoubleHit = 0;
		this._magicDoubleHit = 0;
		this._InfluenceLuck = 0;
	}

	public String getStartMsg() {
		return this._startmsg;
	}

	public void setStartMsg(final String startmsg) {
		this._startmsg = startmsg;
	}

	public String getStopMsg() {
		return this._stopmsg;
	}

	public void setStopMsg(final String stopmsg) {
		this._stopmsg = stopmsg;
	}

	public int getVipLevel() {
		return this._viplevel;
	}

	public void setVipLevel(final int i) {
		this._viplevel = i;
	}

	public int get_type() {
		return this._type;
	}

	public void set_type(final int i) {
		this._type = i;
	}

	public int get_type_mod() {
		return this._type_mod;
	}

	public void set_type_mod(final int i) {
		this._type_mod = i;
	}

	public int get_buff_time() {
		return this._buff_time;
	}

	public void set_buff_time(final int i) {
		this._buff_time = i;
	}

	public int get_buff_gfx() {
		return this._buff_gfx;
	}

	public void set_buff_gfx(final int i) {
		this._buff_gfx = i;
	}

	public int get_buff_iconid() {
		return this._buff_iconid;
	}

	public void set_buff_iconid(final int i) {
		this._buff_iconid = i;
	}

	public int get_buff_stringid() {
		return this._buff_stringid;
	}

	public void set_buff_stringid(final int i) {
		this._buff_stringid = i;
	}

	public int get_open_string() {
		return this._open_string;
	}

	public void set_open_string(final int i) {
		this._open_string = i;
	}

	public boolean is_buff_save() {
		return this._buff_save;
	}

	public void set_buff_save(final boolean i) {
		this._buff_save = i;
	}

	public int get_poly() {
		return this._poly;
	}

	public void set_poly(final int i) {
		this._poly = i;
	}

	public int get_str() {
		return this._str;
	}

	public void set_str(final int add_str) {
		this._str = add_str;
	}

	public int get_dex() {
		return this._dex;
	}

	public void set_dex(final int add_dex) {
		this._dex = add_dex;
	}

	public int get_con() {
		return this._con;
	}

	public void set_con(final int add_con) {
		this._con = add_con;
	}

	public int get_intel() {
		return this._intel;
	}

	public void set_intel(final int add_int) {
		this._intel = add_int;
	}

	public int get_wis() {
		return this._wis;
	}

	public void set_wis(final int add_wis) {
		this._wis = add_wis;
	}

	public int get_cha() {
		return this._cha;
	}

	public void set_cha(final int add_cha) {
		this._cha = add_cha;
	}

	public int get_ac() {
		return this._ac;
	}

	public void set_ac(final int add_ac) {
		this._ac = add_ac;
	}

	public int get_hp() {
		return this._hp;
	}

	public void set_hp(final int add_hp) {
		this._hp = add_hp;
	}

	public int get_mp() {
		return this._mp;
	}

	public void set_mp(final int add_mp) {
		this._mp = add_mp;
	}

	public int get_hpr() {
		return this._hpr;
	}

	public void set_hpr(final int add_hpr) {
		this._hpr = add_hpr;
	}

	public int get_mpr() {
		return this._mpr;
	}

	public void set_mpr(final int add_mpr) {
		this._mpr = add_mpr;
	}

	public int get_mr() {
		return this._mr;
	}

	public void set_mr(final int add_mr) {
		this._mr = add_mr;
	}

	public int get_sp() {
		return this._sp;
	}

	public void set_sp(final int add_sp) {
		this._sp = add_sp;
	}

	public int get_dmg() {
		return this._dmg;
	}

	public void set_dmg(final int add_dmg) {
		this._dmg = add_dmg;
	}

	public int get_bow_dmg() {
		return this._bow_dmg;
	}

	public void set_bow_dmg(final int add_bow_dmg) {
		this._bow_dmg = add_bow_dmg;
	}

	public int get_hit() {
		return this._hit;
	}

	public void set_hit(final int add_hit) {
		this._hit = add_hit;
	}

	public int get_bow_hit() {
		return this._bow_hit;
	}

	public void set_bow_hit(final int add_bow_hit) {
		this._bow_hit = add_bow_hit;
	}

	public int get_dmg_r() {
		return this._dmg_r;
	}

	public void set_dmg_r(final int add_dmg_r) {
		this._dmg_r = add_dmg_r;
	}

	public int get_magic_r() {
		return this._magic_r;
	}

	public void set_magic_r(final int add_magic_r) {
		this._magic_r = add_magic_r;
	}

	public int get_fire() {
		return this._fire;
	}

	public void set_fire(final int add_fire) {
		this._fire = add_fire;
	}

	public int get_wind() {
		return this._wind;
	}

	public void set_wind(final int add_wind) {
		this._wind = add_wind;
	}

	public int get_earth() {
		return this._earth;
	}

	public void set_earth(final int add_earth) {
		this._earth = add_earth;
	}

	public int get_water() {
		return this._water;
	}

	public void set_water(final int add_water) {
		this._water = add_water;
	}

	public int get_stun() {
		return this._stun;
	}

	public void set_stun(final int add_stun) {
		this._stun = add_stun;
	}

	public int get_stone() {
		return this._stone;
	}

	public void set_stone(final int add_stone) {
		this._stone = add_stone;
	}

	public int get_sleep() {
		return this._sleep;
	}

	public void set_sleep(final int add_sleep) {
		this._sleep = add_sleep;
	}

	public int get_freeze() {
		return this._freeze;
	}

	public void set_freeze(final int add_freeze) {
		this._freeze = add_freeze;
	}

	public int get_sustain() {
		return this._sustain;
	}

	public void set_sustain(final int add_sustain) {
		this._sustain = add_sustain;
	}

	public int get_blind() {
		return this._blind;
	}

	public void set_blind(final int add_blind) {
		this._blind = add_blind;
	}

	public int get_exp() {
		return this._exp;
	}

	public void set_exp(final int add_exp) {
		this._exp = add_exp;
	}

	public int get_pvpdmg() {
		return this._pvpdmg;
	}

	public void set_pvpdmg(final int i) {
		this._pvpdmg = i;
	}

	public int get_pvpdmg_r() {
		return this._pvpdmg_r;
	}

	public void set_pvpdmg_r(final int i) {
		this._pvpdmg_r = i;
	}
	
	public boolean get_deleteafteruse() {
		return this._deleteafteruse;
	}

	public void set_deleteafteruse(final boolean i) {
		this._deleteafteruse = i;
	}
	
	public int get_double_dmg_chance() {
		return this._double_dmg_chance;
	}

	public void set_double_dmg_chance(final int i) {
		this._double_dmg_chance = i;
	}
	
	public int get_physicsDmgUp() {
		return this._physicsDmgUp;
	}

	public void set_physicsDmgUp(final int i) {
		this._physicsDmgUp = i;
	}
	
	public int get_magicDmgUp() {
		return this._magicDmgUp;
	}

	public void set_magicDmgUp(final int i) {
		this._magicDmgUp = i;
	}
	
	public int get_physicsDmgDown() {
		return this._physicsDmgDown;
	}

	public void set_physicsDmgDown(final int i) {
		this._physicsDmgDown = i;
	}
	
	public int get_magicDmgDown() {
		return this._magicDmgDown;
	}

	public void set_magicDmgDown(final int i) {
		this._magicDmgDown = i;
	}
	
	public int get_magicHitUp() {
		return this._magicHitUp;
	}

	public void set_magicHitUp(final int i) {
		this._magicHitUp = i;
	}
	
	public int get_magicHitDown() {
		return this._magicHitDown;
	}

	public void set_magicHitDown(final int i) {
		this._magicHitDown = i;
	}
	
	public int get_physicsDoubleHit() {
		return this._physicsDoubleHit;
	}

	public void set_physicsDoubleHit(final int i) {
		this._physicsDoubleHit = i;
	}
	
	public int get_magicDoubleHit() {
		return this._magicDoubleHit;
	}

	public void set_magicDoubleHit(final int i) {
		this._magicDoubleHit = i;
	}
	
	public int get_InfluenceLuck() {
		return this._InfluenceLuck;
	}

	public void set_InfluenceLuck(final int i) {
		this._InfluenceLuck = i;
	}
	
}
