package com.lineage.server.templates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Object;

public class L1Npc extends L1Object implements Cloneable {

	private static final Log _log = LogFactory.getLog(L1Npc.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public L1Npc clone() {
		try {
			return (L1Npc) (super.clone());

		} catch (final CloneNotSupportedException e) {
			throw (new InternalError(e.getMessage()));
		}
	}

	public L1Npc() {
	}

	private int _npcid;

	public int get_npcId() {
		return _npcid;
	}

	public void set_npcId(final int i) {
		_npcid = i;
	}

	private String _name;

	public String get_name() {
		return _name;
	}

	public void set_name(final String s) {
		_name = s;
	}

	private String _impl;

	public String getImpl() {
		return _impl;
	}

	public void setImpl(final String s) {
		_impl = s;
	}

	private int _level;

	public int get_level() {
		return _level;
	}

	public void set_level(final int i) {
		_level = i;
	}

	private int _hp;

	public int get_hp() {
		return _hp;
	}

	public void set_hp(final int i) {
		_hp = i;
	}

	private int _mp;

	public int get_mp() {
		return _mp;
	}

	public void set_mp(final int i) {
		_mp = i;
	}

	private int _ac;

	public int get_ac() {
		return _ac;
	}

	public void set_ac(final int i) {
		_ac = i;
	}

	private int _str;

	public int get_str() {
		return _str;
	}

	public void set_str(final int i) {
		_str = i;
	}

	private int _con;

	public int get_con() {
		return _con;
	}

	public void set_con(final int i) {
		_con = i;
	}

	private int _dex;

	public int get_dex() {
		return _dex;
	}

	public void set_dex(final int i) {
		_dex = i;
	}

	private int _wis;

	public int get_wis() {
		return _wis;
	}

	public void set_wis(final int i) {
		_wis = i;
	}

	private int _int;

	public int get_int() {
		return _int;
	}

	public void set_int(final int i) {
		_int = i;
	}

	private int _mr;

	public int get_mr() {
		return _mr;
	}

	public void set_mr(final int i) {
		_mr = i;
	}

	private int _exp;

	public int get_exp() {
		return _exp;
	}

	public void set_exp(final int i) {
		_exp = i;
	}

	private int _lawful;

	public int get_lawful() {
		return _lawful;
	}

	public void set_lawful(final int i) {
		_lawful = i;
	}

	private String _size;

	public String get_size() {
		return _size;
	}

	/**
	 * 是小怪
	 * 
	 * @return
	 */
	public boolean isSmall() {
		return _size.equalsIgnoreCase("small");
	}

	/**
	 * 是大怪
	 * 
	 * @return
	 */
	public boolean isLarge() {
		return _size.equalsIgnoreCase("large");
	}

	public void set_size(final String s) {
		_size = s;
	}

	private int _weakAttr;

	/**
	 * NPC害怕屬性
	 * 
	 * @return 0.無属性魔法,1.地魔法,2.火魔法,4.水魔法,8.風魔法
	 */
	public int get_weakAttr() {
		return _weakAttr;
	}

	/**
	 * NPC害怕屬性
	 * 
	 * @param i 0.無属性魔法,1.地魔法,2.火魔法,4.水魔法,8.風魔法
	 */
	public void set_weakAttr(final int i) {
		_weakAttr = i;
	}

	private int _ranged;// 攻擊距離

	/**
	 * 攻擊距離
	 * 
	 * @return
	 */
	public int get_ranged() {
		return _ranged;
	}

	/**
	 * 攻擊距離
	 * 
	 * @param i
	 */
	public void set_ranged(final int i) {
		_ranged = i;
	}

	private boolean _agrososc;

	public boolean is_agrososc() {
		return _agrososc;
	}

	public void set_agrososc(final boolean flag) {
		_agrososc = flag;
	}

	private boolean _agrocoi;

	public boolean is_agrocoi() {
		return _agrocoi;
	}

	public void set_agrocoi(final boolean flag) {
		_agrocoi = flag;
	}

	private boolean _tameable;

	/**
	 * 可以迷魅
	 * 
	 * @return
	 */
	public boolean isTamable() {
		return _tameable;
	}

	public void setTamable(final boolean flag) {
		_tameable = flag;
	}

	private int _passispeed;

	/**
	 * 移動速度
	 * 
	 * @return
	 */
	public int get_passispeed() {
		return _passispeed;
	}

	/**
	 * 移動速度
	 * 
	 * @param i
	 */
	public void set_passispeed(final int i) {
		_passispeed = i;
	}

	private int _atkspeed;

	/**
	 * 攻擊速度
	 * 
	 * @return
	 */
	public int get_atkspeed() {
		return _atkspeed;
	}

	/**
	 * 攻擊速度
	 * 
	 * @param i
	 */
	public void set_atkspeed(final int i) {
		_atkspeed = i;
	}

	private boolean _agro;

	/**
	 * 主動攻擊
	 * 
	 * @return
	 */
	public boolean is_agro() {
		return _agro;
	}

	public void set_agro(final boolean flag) {
		_agro = flag;
	}

	private int _gfxid;

	public int get_gfxid() {
		return _gfxid;
	}

	public void set_gfxid(final int i) {
		_gfxid = i;
	}

	private String _nameid;

	public String get_nameid() {
		return _nameid;
	}

	public void set_nameid(final String s) {
		_nameid = s;
	}

	private int _undead;// NPC屬性系

	/**
	 * NPC屬性系
	 * 
	 * @return <BR>
	 *         0:無 1:不死系 2:惡魔系 3:殭屍系 4:不死系(治療系無傷害/無法使用起死回生) 5:狼人系 6:龍系
	 */
	public int get_undead() {
		return _undead;
	}

	public void set_undead(final int i) {
		_undead = i;
	}

	private int _poisonatk;

	public int get_poisonatk() {
		return _poisonatk;
	}

	public void set_poisonatk(final int i) {
		_poisonatk = i;
	}

	private int _paralysisatk;

	public int get_paralysisatk() {
		return _paralysisatk;
	}

	public void set_paralysisatk(final int i) {
		_paralysisatk = i;
	}

	private int _family;

	public int get_family() {
		return _family;
	}

	public void set_family(final int i) {
		_family = i;
	}

	private int _agrofamily;// 同族幫忙

	/**
	 * 同族幫忙
	 * 
	 * @return 0:無 1:幫助同族 1以上:全部NPC幫助
	 */
	public int get_agrofamily() {
		return _agrofamily;
	}

	/**
	 * 同族幫忙
	 * 
	 * @param i 0:無 1:幫助同族 1以上:全部NPC幫助
	 */
	public void set_agrofamily(final int i) {
		_agrofamily = i;
	}

	private int _agrogfxid1;

	public int is_agrogfxid1() {
		return _agrogfxid1;
	}

	public void set_agrogfxid1(final int i) {
		_agrogfxid1 = i;
	}

	private int _agrogfxid2;

	public int is_agrogfxid2() {
		return _agrogfxid2;
	}

	public void set_agrogfxid2(final int i) {
		_agrogfxid2 = i;
	}

	private boolean _picupitem;

	public boolean is_picupitem() {
		return _picupitem;
	}

	public void set_picupitem(final boolean flag) {
		_picupitem = flag;
	}

	private int _digestitem;

	public int get_digestitem() {
		return _digestitem;
	}

	public void set_digestitem(final int i) {
		_digestitem = i;
	}

	private boolean _bravespeed;

	public boolean is_bravespeed() {
		return _bravespeed;
	}

	public void set_bravespeed(final boolean flag) {
		_bravespeed = flag;
	}

	private int _hprinterval;

	public int get_hprinterval() {
		return _hprinterval;
	}

	public void set_hprinterval(final int i) {
		_hprinterval = (i / 1000);
	}

	private int _hpr;

	public int get_hpr() {
		return _hpr;
	}

	public void set_hpr(final int i) {
		_hpr = i;
	}

	private int _mprinterval;

	public int get_mprinterval() {
		return _mprinterval;
	}

	public void set_mprinterval(final int i) {
		_mprinterval = (i / 1000);
	}

	private int _mpr;

	public int get_mpr() {
		return _mpr;
	}

	public void set_mpr(final int i) {
		_mpr = i;
	}

	private boolean _teleport;

	public boolean is_teleport() {
		return _teleport;
	}

	public void set_teleport(final boolean flag) {
		_teleport = flag;
	}

	private int _randomlevel;

	public int get_randomlevel() {
		return _randomlevel;
	}

	public void set_randomlevel(final int i) {
		_randomlevel = i;
	}

	private int _randomhp;

	public int get_randomhp() {
		return _randomhp;
	}

	public void set_randomhp(final int i) {
		_randomhp = i;
	}

	private int _randommp;

	public int get_randommp() {
		return _randommp;
	}

	public void set_randommp(final int i) {
		_randommp = i;
	}

	private int _randomac;

	public int get_randomac() {
		return _randomac;
	}

	public void set_randomac(final int i) {
		_randomac = i;
	}

	private int _randomexp;

	public int get_randomexp() {
		return _randomexp;
	}

	public void set_randomexp(final int i) {
		_randomexp = i;
	}

	private int _randomlawful;

	public int get_randomlawful() {
		return _randomlawful;
	}

	public void set_randomlawful(final int i) {
		_randomlawful = i;
	}

	private int _damagereduction;

	public int get_damagereduction() {
		return _damagereduction;
	}

	public void set_damagereduction(final int i) {
		_damagereduction = i;
	}

	private boolean _hard;

	public boolean is_hard() {
		return _hard;
	}

	public void set_hard(final boolean flag) {
		_hard = flag;
	}

	private boolean _doppel;

	public boolean is_doppel() {
		return _doppel;
	}

	public void set_doppel(final boolean flag) {
		_doppel = flag;
	}

	private boolean _tu;

	public void set_IsTU(final boolean i) {
		_tu = i;
	}

	public boolean get_IsTU() {
		return _tu;
	}

	private boolean _erase;

	public void set_IsErase(final boolean i) {
		_erase = i;
	}

	public boolean get_IsErase() {
		return _erase;
	}

	private int bowActId = 0;

	public int getBowActId() {
		return bowActId;
	}

	public void setBowActId(final int i) {
		bowActId = i;
	}

	private int _karma;

	public int getKarma() {
		return _karma;
	}

	public void setKarma(final int i) {
		_karma = i;
	}

	private int _transformId;// 死亡變身的目標NPCID

	/**
	 * 死亡變身的目標NPCID
	 * 
	 * @return
	 */
	public int getTransformId() {
		return _transformId;
	}

	/**
	 * 死亡變身的目標NPCID
	 * 
	 * @param transformId
	 */
	public void setTransformId(final int transformId) {
		_transformId = transformId;
	}

	private int _transformGfxId;// 死亡變身的動畫代號

	/**
	 * 死亡變身的動畫代號
	 * 
	 * @return
	 */
	public int getTransformGfxId() {
		return _transformGfxId;
	}

	/**
	 * 死亡變身的動畫代號
	 * 
	 * @param i
	 */
	public void setTransformGfxId(final int i) {
		_transformGfxId = i;
	}

	private int _atkMagicSpeed;// 有方向魔法速度延遲

	/**
	 * 有方向魔法速度延遲
	 * 
	 * @return
	 */
	public int getAtkMagicSpeed() {
		return _atkMagicSpeed;
	}

	/**
	 * 有方向魔法速度延遲
	 * 
	 * @param atkMagicSpeed
	 */
	public void setAtkMagicSpeed(final int atkMagicSpeed) {
		_atkMagicSpeed = atkMagicSpeed;
	}

	private int _subMagicSpeed;// 無方向魔法速度延遲

	/**
	 * 無方向魔法速度延遲
	 * 
	 * @return
	 */
	public int getSubMagicSpeed() {
		return _subMagicSpeed;
	}

	/**
	 * 無方向魔法速度延遲
	 * 
	 * @param subMagicSpeed
	 */
	public void setSubMagicSpeed(final int subMagicSpeed) {
		_subMagicSpeed = subMagicSpeed;
	}

	private int _lightSize;

	public int getLightSize() {
		return _lightSize;
	}

	public void setLightSize(final int lightSize) {
		_lightSize = lightSize;
	}

	private boolean _amountFixed;

	/**
	 * mapidsテーブルで設定されたモンスター量倍率の影響を受けるかどうかを返す。
	 * 
	 * @return 影響を受けないように設定されている場合はtrueを返す。
	 */
	public boolean isAmountFixed() {
		return _amountFixed;
	}

	public void setAmountFixed(final boolean fixed) {
		_amountFixed = fixed;
	}

	private boolean _changeHead;

	public boolean getChangeHead() {
		return _changeHead;
	}

	public void setChangeHead(final boolean changeHead) {
		_changeHead = changeHead;
	}

	private boolean _isCantResurrect;

	/**
	 * 不可以復活
	 * 
	 * @return true:不允許 false:允許
	 */
	public boolean isCantResurrect() {
		return _isCantResurrect;
	}

	/**
	 * 設置為不可以復活
	 * 
	 * @param isCantResurrect
	 */
	public void setCantResurrect(final boolean isCantResurrect) {
		_isCantResurrect = isCantResurrect;
	}

	private String _classname;// 獨立判斷項名稱

	/**
	 * 獨立判斷項名稱
	 * 
	 * @param classname
	 */
	public void set_classname(final String classname) {
		_classname = classname;
	}

	/**
	 * 獨立判斷項名稱
	 * 
	 * @return
	 */
	public String get_classname() {
		return _classname;
	}

	private NpcExecutor _class;// 獨立判斷項

	/**
	 * 獨立判斷項
	 * 
	 * @return
	 */
	public NpcExecutor getNpcExecutor() {
		return _class;
	}

	/**
	 * 獨立判斷項
	 * 
	 * @param _class
	 */
	public void setNpcExecutor(final NpcExecutor _class) {
		try {
			if (_class == null) {
				return;
			}
			this._class = _class;

			int type = _class.type();

			if (type >= 32) {
				_spawn = true;// NPC召喚
				type -= 32;
			}
			if (type >= 16) {
				_work = true;// NPC工作時間
				type -= 16;
			}
			if (type >= 8) {
				_death = true;// NPC死亡
				type -= 8;
			}
			if (type >= 4) {
				_attack = true;// NPC受到攻擊
				type -= 4;
			}
			if (type >= 2) {
				_action = true;// NPC對話執行
				type -= 2;
			}
			if (type >= 1) {
				_talk = true;// NPC對話判斷
				type -= 1;
			}
			if (type > 0) {
				_log.error("獨立判斷項數組設定錯誤:餘數大於0 NpcId: " + _npcid);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private boolean _talk = false;// NPC對話判斷

	/**
	 * NPC對話判斷
	 */
	public boolean talk() {
		return _talk;
	}

	private boolean _action = false;// NPC對話執行

	/**
	 * NPC對話執行
	 */
	public boolean action() {
		return _action;
	}

	private boolean _attack = false;// NPC受到攻擊

	/**
	 * NPC受到攻擊
	 */
	public boolean attack() {
		return _attack;
	}

	private boolean _death = false;// NPC死亡

	/**
	 * NPC死亡
	 */
	public boolean death() {
		return _death;
	}

	private boolean _work = false;// NPC工作時間

	/**
	 * NPC工作時間
	 */
	public boolean work() {
		return _work;
	}

	private boolean _spawn = false;// NPC召喚

	/**
	 * NPC召喚
	 */
	public boolean spawn() {
		return _spawn;
	}

	private boolean _boss = false;// BOSS

	public void set_boss(final boolean boss) {
		_boss = boss;
	}

	public boolean is_boss() {
		return _boss;
	}

	private int _quest_score;

	public int get_quest_score() {
		return _quest_score;
	}

	public void set_quest_score(final int quest_score) {
		_quest_score = quest_score;
	}

	// 淨化藥水 (可對特定BOSS造成傷害) by terry0412
	private boolean _attack_request;

	public final boolean is_attack_request() {
		return _attack_request;
	}

	public final void set_attack_request(final boolean attack_request) {
		_attack_request = attack_request;
	}
	
}
