package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import nick.AutoControl.AutoAttack.Node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigKill;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.config.ConfigRecord;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.CampSet;
import com.lineage.data.event.EffectAISet;
import com.lineage.data.event.OnlineGiftSet;
import com.lineage.data.event.ProtectorSet;
import com.lineage.data.event.RewardSet;
import com.lineage.data.item_etcitem.extra.Reward;
import com.lineage.data.quest.Chapter01R;
import com.lineage.echo.ClientExecutor;
import com.lineage.echo.EncryptExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.clientpackets.AcceleratorChecker;
import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.datatables.CharApprenticeTable;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.ExtraItemStealTable;
import com.lineage.server.datatables.ExtraMeteAbilityTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.L1Kill_Npc_Quest;
import com.lineage.server.datatables.LotteryWarehouseTable;
import com.lineage.server.datatables.MapLevelTable;
import com.lineage.server.datatables.ServerAIEffectTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.datatables.VipSetsTable;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.CharBuffReading;
import com.lineage.server.datatables.lock.CharMapsTimeReading;
import com.lineage.server.datatables.lock.CharOtherReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1ActionPc;
import com.lineage.server.model.L1ActionPet;
import com.lineage.server.model.L1ActionSummon;
import com.lineage.server.model.L1Apprentice;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1AttackThread;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1ChatParty;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1DwarfForElfInventory;
import com.lineage.server.model.L1DwarfForGameMallInventry;
import com.lineage.server.model.L1DwarfInventory;
import com.lineage.server.model.L1EquipmentSlot;
import com.lineage.server.model.L1ExcludingList;
import com.lineage.server.model.L1ExcludingMailList;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Karma;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1PinkName;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.model.L1War;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.model.monitor.L1PcInvisDelay;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_Bonusstats;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_ChangeShape;
import com.lineage.server.serverpackets.S_DelSkill;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_DoActionShop;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_Fishing;
import com.lineage.server.serverpackets.S_GameMallItemMoney;
import com.lineage.server.serverpackets.S_HPMeter;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_Invis;
import com.lineage.server.serverpackets.S_Karma;
import com.lineage.server.serverpackets.S_Lawful;
import com.lineage.server.serverpackets.S_Liquor;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_OtherCharPacks;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharPack;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxCharEr;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_PacketBoxProtection;
import com.lineage.server.serverpackets.S_PinkName;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_Teleport;
import com.lineage.server.serverpackets.S_VipShow;
import com.lineage.server.serverpackets.S_VipTime;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.serverpackets.ability.S_BaseAbility;
import com.lineage.server.serverpackets.ability.S_BaseAbilityDetails;
import com.lineage.server.serverpackets.ability.S_ConDetails;
import com.lineage.server.serverpackets.ability.S_DexDetails;
import com.lineage.server.serverpackets.ability.S_ElixirCount;
import com.lineage.server.serverpackets.ability.S_IntDetails;
import com.lineage.server.serverpackets.ability.S_StrDetails;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.serverpackets.ability.S_WisDetails;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemBuff;
import com.lineage.server.templates.L1ItemPower_text;
import com.lineage.server.templates.L1ItemSteal;
import com.lineage.server.templates.L1MeteAbility;
import com.lineage.server.templates.L1Name_Power;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.templates.L1PcOtherList;
import com.lineage.server.templates.L1PrivateShopBuyList;
import com.lineage.server.templates.L1PrivateShopSellList;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.templates.L1TradeItem;
import com.lineage.server.templates.L1User_Power;
import com.lineage.server.templates.L1Vip;
import com.lineage.server.templates.L1WilliamGfxIdOrginal;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.event.WorldChatTimer;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.CalcInitHpMp;
import com.lineage.server.utils.CalcStat;
import com.lineage.server.utils.DoubleUtil;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldQuest;
import com.lineage.server.world.WorldWar;

/**
 * 對象:PC 控制項
 * 
 * @author dexc
 */
public class L1PcInstance extends L1Character {

	private static final Log _log = LogFactory.getLog(L1PcInstance.class);

	private static final long serialVersionUID = 1L;

	/** 騎士(男) */
	public static final int CLASSID_KNIGHT_MALE = 61;
	/** 騎士(女) */
	public static final int CLASSID_KNIGHT_FEMALE = 48;

	/** 精靈(男) */
	public static final int CLASSID_ELF_MALE = 138;
	/** 精靈(女) */
	public static final int CLASSID_ELF_FEMALE = 37;

	/** 法師(男) */
	public static final int CLASSID_WIZARD_MALE = 734;
	/** 法師(女) */
	public static final int CLASSID_WIZARD_FEMALE = 1186;

	/** 黑妖(男) */
	public static final int CLASSID_DARK_ELF_MALE = 2786;
	/** 黑妖(女) */
	public static final int CLASSID_DARK_ELF_FEMALE = 2796;

	/** 王族(男) */
	public static final int CLASSID_PRINCE = 0;
	/** 王族(女) */
	public static final int CLASSID_PRINCESS = 1;

	/** 龍騎(男) */
	public static final int CLASSID_DRAGON_KNIGHT_MALE = 6658;
	/** 龍騎(女) */
	public static final int CLASSID_DRAGON_KNIGHT_FEMALE = 6661;

	/** 幻術(男) */
	public static final int CLASSID_ILLUSIONIST_MALE = 6671;
	/** 幻術(女) */
	public static final int CLASSID_ILLUSIONIST_FEMALE = 6650;

	/** 戰士(男) */
	public static final int CLASSID_WARRIOR_MALE = 12490;
	/** 戰士(女) */
	public static final int CLASSID_WARRIOR_FEMALE = 12494;

	private static Random _random = new Random();

	private static final SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public S_Teleport s_Teleport = new S_Teleport(this);

	// public int tt_clanid = -1;
	// public int tt_partyid = -1;
	// public int tt_level = 0;

	/**
	 * 設定原始質資訊
	 */
	public void load_src() {
		_old_exp = getExp();
		_old_lawful = getLawful();
		_old_karma = getKarma();
	}

	private int _redbluejoin = 0;

	public int get_redbluejoin() {
		return this._redbluejoin;
	}

	public void set_redbluejoin(int redbluejoin) {
		this._redbluejoin = redbluejoin;
	}

	private int _redblueroom = 0;

	public int get_redblueroom() {
		return this._redblueroom;
	}

	public void set_redblueroom(int redblueroom) {
		this._redblueroom = redblueroom;
	}

	private int _redblueleader = 0;

	public int get_redblueleader() {
		return this._redblueleader;
	}

	public void set_redblueleader(int redblueleader) {
		this._redblueleader = redblueleader;
	}

	private int _redbluepoint = 0;

	public int get_redbluepoint() {
		return this._redbluepoint;
	}

	public void set_redbluepoint(int redbluepoint) {
		this._redbluepoint = redbluepoint;
	}

	private int _aistay = 0;

	public int get_aistay() {
		return this._aistay;
	}

	public void set_aistay(int aistay) {
		this._aistay = aistay;
	}

	private int[] _aixyz = null;

	public int[] get_aixyz() {
		return this._aixyz;
	}

	public void set_aixyz(int[] aixyz) {
		this._aixyz = aixyz;
	}

	private int _ai_timer = 0;

	public void setAITimer(int time) {
		this._ai_timer = time;
	}

	public void addAITimer() {
		this.setAITimer(this._ai_timer - 1);
	}

	public int getAITimer() {
		return this._ai_timer;
	}

	private int _ai_error = 0;

	public void setAIERROR() {
		this._ai_error = 0;
	}

	public void addAIERROR() {
		this._ai_error += 1;
	}

	public int getAIERROR() {
		return this._ai_error;
	}

	private boolean _isKill = false;

	public boolean is_isKill() {
		return _isKill;
	}

	public void set_isKill(final boolean _isKill) {
		this._isKill = _isKill;
	}

	private short _hpr = 0;

	private short _trueHpr = 0;

	public short getHpr() {
		return _hpr;
	}

	/**
	 * 增加(減少)HP回復量
	 * 
	 * @param i
	 */
	public void addHpr(final int i) {
		_trueHpr += i;
		_hpr = (short) Math.max(0, _trueHpr);
	}

	private short _mpr = 0;
	private short _trueMpr = 0;

	public short getMpr() {
		return _mpr;
	}

	/**
	 * 增加(減少)MP回復量
	 * 
	 * @param i
	 */
	public void addMpr(final int i) {
		_trueMpr += i;
		_mpr = (short) Math.max(0, _trueMpr);
	}

	public short _originalHpr = 0; // ● オリジナルCON HPR

	public short getOriginalHpr() {

		return _originalHpr;
	}

	public short _originalMpr = 0; // ● オリジナルWIS MPR

	public short getOriginalMpr() {
		return _originalMpr;
	}

	private boolean _mpRegenActive;
	private boolean _hpRegenActive;

	private int _hpRegenType = 0;
	private int _hpRegenState = 4;

	public int getHpRegenState() {
		return _hpRegenState;
	}

	public void set_hpRegenType(final int hpRegenType) {
		_hpRegenType = hpRegenType;
	}

	public int hpRegenType() {
		return _hpRegenType;
	}

	private int regenMax() {
		final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9, 3, 2 };

		int regenLvl = Math.min(10, getLevel());
		if ((30 <= getLevel()) && isKnight()) {
			regenLvl = 11;
		}
		return lvlTable[regenLvl - 1] << 2;
	}

	/**
	 * HP回復成立
	 * 
	 * @return
	 */
	public boolean isRegenHp() {
		if (!_hpRegenActive) {
			return false;
		}

		// 飽食度不足
		if ((_food < 3)) {
			return false;
		}

		// 取得角色負重指數
		final int weight = _inventory.getWeight240();
		// 超過５０％
		if (weight >= 120) {
			// 負重超過５０％，亦能繼續回覆體力及魔力 的技能
			if (hasSkillEffect(EXOTIC_VITALIZE) || hasSkillEffect(ADDITIONAL_FIRE) || hasSkillEffect(AWAKEN_FAFURION)) {
				// 超過一定的限制後即無法發揮效果
				if (weight >= 197) {
					return false;
				}

			} else {
				return false;
			}
		}
		return _hpRegenType >= regenMax();
	}

	private int _mpRegenType = 0;
	private int _mpRegenState = 4;

	public int getMpRegenState() {
		return _mpRegenState;
	}

	public void set_mpRegenType(final int hpmpRegenType) {
		_mpRegenType = hpmpRegenType;
	}

	public int mpRegenType() {
		return _mpRegenType;
	}

	/**
	 * MP回復成立
	 * 
	 * @return
	 */
	public boolean isRegenMp() {
		if (!_mpRegenActive) {
			return false;
		}

		// 飽食度不足
		if ((_food < 3)) {
			return false;
		}

		// 取得角色負重指數
		final int weight = _inventory.getWeight240();
		// 超過５０％
		if (weight >= 120) {
			// 負重超過５０％，亦能繼續回覆體力及魔力 的技能
			if (hasSkillEffect(EXOTIC_VITALIZE) || hasSkillEffect(ADDITIONAL_FIRE) || hasSkillEffect(AWAKEN_FAFURION)) {
				// 超過一定的限制後即無法發揮效果
				if (weight >= 197) {
					return false;
				}

			} else {
				return false;
			}
		}
		// 法師加速
		if (isWizard()) {
			return _mpRegenType >= 40;
		}
		return _mpRegenType >= 64;
	}

	// HP自然回復 MP自然回復

	/** 無動作 */
	public static final int REGENSTATE_NONE = 4;

	/** 移動中 */
	public static final int REGENSTATE_MOVE = 2;

	/** 攻擊中 */
	public static final int REGENSTATE_ATTACK = 1;

	public void setRegenState(final int state) {
		_mpRegenState = state;
		_hpRegenState = state;
	}

	/**
	 * HP自然回復啟用
	 */
	public void startHpRegeneration() {
		if (!_hpRegenActive) {
			_hpRegenActive = true;
		}
	}

	/**
	 * HP自然回復停止
	 */
	public void stopHpRegeneration() {
		if (_hpRegenActive) {
			_hpRegenActive = false;
		}
	}

	/**
	 * HP自然回復狀態
	 * 
	 * @return
	 */
	public boolean getHpRegeneration() {
		return _hpRegenActive;
	}

	/**
	 * MP自然回復啟用
	 */
	public void startMpRegeneration() {
		if (!_mpRegenActive) {
			_mpRegenActive = true;
		}
	}

	/**
	 * MP自然回復停止
	 */
	public void stopMpRegeneration() {
		if (_mpRegenActive) {
			_mpRegenActive = false;
		}
	}

	/**
	 * MP自然回復狀態
	 * 
	 * @return
	 */
	public boolean getMpRegeneration() {
		return _mpRegenActive;
	}

	/**
	 * 加入PC 可見物更新處理清單
	 */
	public void startObjectAutoUpdate() {
		removeAllKnownObjects();
	}

	/**
	 * 移出各種處理清單
	 */
	public void stopEtcMonitor() {
		// 移出PC 鬼魂模式處理清單
		set_ghostTime(-1);
		setGhost(false);
		setGhostCanTalk(true);
		setReserveGhost(false);

		if (ServerUseMapTimer.MAP.get(this) != null) {
			// 移出計時地圖處理清單
			ServerUseMapTimer.MAP.remove(this);
		}

		// 移出在線獎勵清單
		OnlineGiftSet.remove(this);

		// 清空清單資料
		ListMapUtil.clear(_skillList);
		ListMapUtil.clear(_sellList);
		ListMapUtil.clear(_buyList);
		ListMapUtil.clear(_trade_items);
		ListMapUtil.clear(_powers);
	}

	private int _old_lawful;

	/**
	 * 原始Lawful
	 * 
	 * @return
	 */
	public int getLawfulo() {
		return _old_lawful;
	}

	/**
	 * 更新Lawful
	 */
	public void onChangeLawful() {
		if (_old_lawful != getLawful()) {
			_old_lawful = getLawful();
			sendPacketsAll(new S_Lawful(this));
			// 戰鬥特化效果
			lawfulUpdate();
		}
	}

	private int _old_karma;

	/**
	 * 原始karma
	 * 
	 * @return
	 */
	public int getKarmalo() {
		return _old_karma;
	}

	/**
	 * 更新karma
	 */
	public void onChangeKarma() {
		if (_old_karma != getKarma()) {
			_old_karma = getKarma();
			sendPackets(new S_Karma(this));
		}
	}

	private boolean _jl1 = false;// 正義的守護 Lv.1
	private boolean _jl2 = false;// 正義的守護 Lv.2
	private boolean _jl3 = false;// 正義的守護 Lv.3
	private boolean _el1 = false;// 邪惡的守護 Lv.1
	private boolean _el2 = false;// 邪惡的守護 Lv.2
	private boolean _el3 = false;// 邪惡的守護 Lv.3

	/**
	 * TODO 戰鬥特化<BR>
	 */
	public void lawfulUpdate() {
		final int l = getLawful();

		if ((l >= 10000) && (l <= 19999)) {
			if (!_jl1) {
				overUpdate();
				_jl1 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L1, 1));
				sendPackets(new S_OwnCharAttrDef(this));
				sendPackets(new S_SPMR(this));
			}

		} else if ((l >= 20000) && (l <= 29999)) {
			if (!_jl2) {
				overUpdate();
				_jl2 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L2, 1));
				sendPackets(new S_OwnCharAttrDef(this));
				sendPackets(new S_SPMR(this));
			}

		} else if ((l >= 30000) && (l <= 39999)) {
			if (!_jl3) {
				overUpdate();
				_jl3 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L3, 1));
				sendPackets(new S_OwnCharAttrDef(this));
				sendPackets(new S_SPMR(this));
			}

		} else if ((l >= -19999) && (l <= -10000)) {
			if (!_el1) {
				overUpdate();
				_el1 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L1, 1));
				sendPackets(new S_SPMR(this));
			}

		} else if ((l >= -29999) && (l <= -20000)) {
			if (!_el2) {
				overUpdate();
				_el2 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L2, 1));
				sendPackets(new S_SPMR(this));
			}

		} else if ((l >= -39999) && (l <= -30000)) {
			if (!_el3) {
				overUpdate();
				_el3 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L3, 1));
				sendPackets(new S_SPMR(this));
			}

		} else {
			if (overUpdate()) {
				sendPackets(new S_OwnCharAttrDef(this));
				sendPackets(new S_SPMR(this));
			}
		}
	}

	/**
	 * TODO 戰鬥特化<BR>
	 * 
	 * @return
	 */
	private boolean overUpdate() {
		if (_jl1) {
			_jl1 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L1, 0));
			return true;

		} else if (_jl2) {
			_jl2 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L2, 0));
			return true;

		} else if (_jl3) {
			_jl3 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L3, 0));
			return true;

		} else if (_el1) {
			_el1 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L1, 0));
			return true;

		} else if (_el2) {
			_el2 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L2, 0));
			return true;

		} else if (_el3) {
			_el3 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L3, 0));
			return true;
		}
		return false;
	}

	/**
	 * TODO 戰鬥特化<BR>
	 * <FONT COLOR="#0000ff">遭遇的守護 </FONT>20級以下角色 被超過10級以上的玩家攻擊而死亡時，不會失去經驗值，也不會掉落物品<BR>
	 * 
	 * @return
	 */
	private boolean isEncounter() {
		if (getLevel() <= ConfigOther.ENCOUNTER_LV) {
			return true;
		}
		return false;
	}

	/**
	 * TODO 戰鬥特化<BR>
	 * <FONT COLOR="#0000ff">正義的守護 Lv.1 </FONT>善惡值 10,000 ~ 19,999 (防御：-2 / 魔防+3)<BR>
	 * <FONT COLOR="#0000ff">正義的守護 Lv.2 </FONT>善惡值 20,000 ~ 29,999 (防御：-4 / 魔防+6)<BR>
	 * <FONT COLOR="#0000ff">正義的守護 Lv.3 </FONT>善惡值 30,000 ~ 32,767 (防御：-6 / 魔防+9)<BR>
	 * <FONT COLOR="#0000ff">邪惡的守護 Lv.1 </FONT>善惡值 -10,000 ~ -19,999 (近/遠距離攻擊力+1 / 魔攻+1)<BR>
	 * <FONT COLOR="#0000ff">邪惡的守護 Lv.2 </FONT>善惡值 -20,000 ~ -29,999 (近/遠距離攻擊力+3 / 魔攻+2)<BR>
	 * <FONT COLOR="#0000ff">邪惡的守護 Lv.3 </FONT>善惡值 -30,000 ~ -32,767 (近/遠距離攻擊力+5 / 魔攻+3)<BR>
	 * <FONT COLOR="#0000ff">遭遇的守護 </FONT>20級以下角色 被超過10級以上的玩家攻擊而死亡時，不會失去經驗值，也不會掉落物品<BR>
	 */
	public int guardianEncounter() {
		if (_jl1) {
			return 0;

		} else if (_jl2) {
			return 1;

		} else if (_jl3) {
			return 2;

		} else if (_el1) {
			return 3;

		} else if (_el2) {
			return 4;

		} else if (_el3) {
			return 5;
		}
		return -1;
	}

	private long _old_exp;

	/**
	 * 原始EXP
	 * 
	 * @return
	 */
	public long getExpo() {
		return _old_exp;
	}

	/**
	 * 更新EXP
	 */
	public void onChangeExp() {
		if (_old_exp != getExp()) {
			_old_exp = getExp();

			final int level = ExpTable.getLevelByExp(getExp());
			final int char_level = getLevel();
			final int gap = level - char_level;

			if (gap == 0) {
				sendPackets(new S_OwnCharStatus(this));
				return;
			}

			if (gap != 0) {
				try {
					if (gap > 0) {
						WriteLogTxt.Recording("升級記錄", "玩家" + getName() + "#" + getId() + "#準備升級，升級前經驗值為" + _old_exp + "等級為" + getLevel() + "空身血量為" + getBaseMaxHp() + "空身魔量為"
								+ getBaseMaxMp());
						levelUp(gap);
						WriteLogTxt.Recording("升級記錄", "玩家" + getName() + "#" + getId() + "#升級完畢當前經驗值為" + getExp() + "升級後等級為" + getLevel() + "空身血量為" + getBaseMaxHp() + "空身魔量為"
								+ getBaseMaxMp());
						save();
					} else if (gap < 0) {
						WriteLogTxt.Recording("降級記錄", "玩家" + getName() + "#" + getId() + "#準備降級，降級前經驗值為" + _old_exp + "等級為" + getLevel() + "空身血量為" + getBaseMaxHp() + "空身魔量為"
								+ getBaseMaxMp());
						levelDown(gap);
						WriteLogTxt.Recording("降級記錄", "玩家" + getName() + "#" + getId() + "#降級完畢當前經驗值為" + getExp() + "升級後等級為" + getLevel() + "空身血量為" + getBaseMaxHp() + "空身魔量為"
								+ getBaseMaxMp());
					}
				} catch (final Exception e) {
				}
			}

			if (getLevel() > 20) {// LOLI 戰鬥特化
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.ENCOUNTER, 0));

			} else {
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.ENCOUNTER, 1));
			}
		}
	}

	/**
	 * TODO 接觸資訊
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			if (isGmInvis() || isGhost() || isInvisble()) {
				return;
			}

			// 副本ID不相等 不相護顯示
			if (perceivedFrom.get_showId() != get_showId()) {
				return;
			}

			perceivedFrom.addKnownObject(this);
			// 發送自身資訊給予接觸人物
			perceivedFrom.sendPackets(new S_OtherCharPacks(this));

			// 隊伍成員HP狀態發送
			if (isInParty()) {
				if (getParty().isMember(perceivedFrom)) {// 對象是隊員
					perceivedFrom.sendPackets(new S_HPMeter(this));
				}
			}
			if (_isFishing) {
				perceivedFrom.sendPackets(new S_Fishing(getId(), ActionCodes.ACTION_Fishing, get_fishX(), get_fishY()));
			}

			if (isPrivateShop()) {
				final int mapId = getMapId();
				if ((mapId != 340) && (mapId != 350) && (mapId != 360) && (mapId != 370) && (mapId != 800)) {
					getSellList().clear();
					getBuyList().clear();

					setPrivateShop(false);
					sendPacketsAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Idle));

				} else {
					perceivedFrom.sendPackets(new S_DoActionShop(getId(), getShopChat()));
				}
			}

			if (get_vipLevel() > 0) {
				final S_VipShow vipShow = new S_VipShow(getId(), get_vipLevel());
				sendPacketsAll(vipShow);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 清除離開可視範圍物件
	 */
	private void removeOutOfRangeObjects() {
		for (final L1Object known : getKnownObjects()) {
			if (known == null) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (!getLocation().isInScreen(known.getLocation())) { // 画面外
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}

			} else {
				if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			}
		}
	}

	/**
	 * 可見物更新處理
	 */
	public void updateObject() {
		if (getOnlineStatus() != 1) {
			return;
		}
		removeOutOfRangeObjects();

		// 指定可視範圍資料更新
		for (final L1Object visible : World.get().getVisibleObjects(this, Config.PC_RECOGNIZE_RANGE)) {
			if (visible instanceof L1MerchantInstance) {// 對話NPC
				if (!knownsObject(visible)) {
					final L1MerchantInstance npc = (L1MerchantInstance) visible;
					// 未認知物件 執行物件封包發送
					npc.onPerceive(this);
				}
				continue;
			}

			if (visible instanceof L1DwarfInstance) {// 倉庫NPC
				if (!knownsObject(visible)) {
					final L1DwarfInstance npc = (L1DwarfInstance) visible;
					// 未認知物件 執行物件封包發送
					npc.onPerceive(this);
				}
				continue;
			}

			if (visible instanceof L1FieldObjectInstance) {// 景觀
				if (!knownsObject(visible)) {
					final L1FieldObjectInstance npc = (L1FieldObjectInstance) visible;
					// 未認知物件 執行物件封包發送
					npc.onPerceive(this);
				}
				continue;
			}

			// 副本ID不相等 不相護顯示
			if (visible.get_showId() != get_showId()) {
				continue;
			}

			if (!knownsObject(visible)) {
				// 未認知物件 執行物件封包發送
				visible.onPerceive(this);
			} else {
				if (visible instanceof L1NpcInstance) {
					final L1NpcInstance npc = (L1NpcInstance) visible;
					if (getLocation().isInScreen(npc.getLocation()) && (npc.getHiddenStatus() != 0)) {
						npc.approachPlayer(this);
					}
				}
			}
			/*
			 * if (visible instanceof L1NpcInstance) { final L1NpcInstance npc = (L1NpcInstance) visible; // 天刀要得顯示特效 William_killnpc_quest.sendGfx(L1PcInstance.this, npc); }
			 */
			// 一般人物 HP可見設置
			if (isHpBarTarget(visible)) {
				final L1Character cha = (L1Character) visible;
				cha.broadcastPacketHP(this);
			}

			// GM HP 查看設置
			if (hasSkillEffect(GMSTATUS_HPBAR)) {
				if (isGmHpBarTarget(visible)) {
					final L1Character cha = (L1Character) visible;
					cha.broadcastPacketHP(this);
				}
			}
		}

		if ((EffectAISet.START) && (this.hasSkillEffect(7021))) {
			this.sendPackets(new S_EffectLocation(this.get_aixyz()[0], this.get_aixyz()[1], ServerAIEffectTable.getEffectId()));
			try {
				if (this.getX() != this.get_aixyz()[0] || this.getY() != this.get_aixyz()[1]) {
					if (this.get_aistay() > 0) {
						this.set_aistay(0);
						String msg = "請 勿 在 驗 證 完 畢 前 移 動。";
						this.sendPackets(new S_BlueMessage(166, "\\f3" + msg));
						this.sendPackets(new S_ServerMessage(msg));
					}
				}
				switch (this.get_aistay()) {
				case 0:
					if (this.getX() == this.get_aixyz()[0] && this.getY() == this.get_aixyz()[1]) {
						this.set_aistay(1);
						String msg = "正在進行中  驗證倒數...3";
						// this.sendPackets(new S_BlueMessage(166, "\\f3" +
						// msg));
						this.sendPackets(new S_ServerMessage("\\fUAI" + msg));
					}
					break;
				case 1:
					if (this.getX() == this.get_aixyz()[0] && this.getY() == this.get_aixyz()[1]) {
						this.set_aistay(2);
						String msg = "正在進行中  驗證倒數...2";
						// this.sendPackets(new S_BlueMessage(166, "\\f3" +
						// msg));
						this.sendPackets(new S_ServerMessage("\\fUAI" + msg));
					}
					break;
				case 2:
					if (this.getX() == this.get_aixyz()[0] && this.getY() == this.get_aixyz()[1]) {
						this.set_aistay(3);
						String msg = "正在進行中  驗證倒數...1";
						// this.sendPackets(new S_BlueMessage(166, "\\f3" +
						// msg));
						this.sendPackets(new S_ServerMessage("\\fUAI" + msg));
					}
					break;
				case 3:
					if (this.getX() == this.get_aixyz()[0] && this.getY() == this.get_aixyz()[1]) {
						this.set_aixyz(null);
						this.set_aistay(0);
						this.sendPackets(new S_ServerMessage("\\fUAI驗證完畢，您可以自由活動了！"));
						this.killSkillEffectTimer(7021);
					}
					break;
				}
				Thread.sleep(1000);
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * 可以觀看HP的對象(特別定義)
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isHpBarTarget(final L1Object obj) {
		if (obj instanceof L1PcInstance) {// 加入陣營戰活動同隊血條顯示
			final L1PcInstance tgpc = (L1PcInstance) obj;
			if (this.get_redbluejoin() != 0) {
				if (this.get_redbluejoin() == tgpc.get_redbluejoin()) {
					return true;
				}
			}
		}
		// 所在地圖位置
		switch (getMapId()) {
		case 400:// 大洞穴/大洞穴抵抗軍/隱遁者地區
			if (obj instanceof L1FollowerInstance) {
				final L1FollowerInstance follower = (L1FollowerInstance) obj;
				if (follower.getMaster().equals(this)) {
					return true;
				}
			}
			break;
		}
		return false;
	}

	/**
	 * GM HPBAR 可以觀看HP的對象
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isGmHpBarTarget(final L1Object obj) {
		if (obj instanceof L1MonsterInstance) {
			return true;
		}
		if (obj instanceof L1PcInstance) {
			return true;
		}
		if (obj instanceof L1SummonInstance) {
			return true;
		}
		if (obj instanceof L1PetInstance) {
			return true;
		}
		if (obj instanceof L1DeInstance) {
			return true;
		}
		if (obj instanceof L1FollowerInstance) {
			return true;
		}
		return false;
	}

	private void sendVisualEffect() {
		int poisonId = 0;
		if (getPoison() != null) { // 毒状態
			poisonId = getPoison().getEffectId();
		}
		if (getParalysis() != null) { // 麻痺状態
			// 麻痺エフェクトを優先して送りたい為、poisonIdを上書き。
			poisonId = getParalysis().getEffectId();
		}
		if (poisonId != 0) { // このifはいらないかもしれない
			sendPacketsAll(new S_Poison(getId(), poisonId));
		}
	}

	public void sendVisualEffectAtLogin() {
		sendVisualEffect();
	}

	private boolean _isCHAOTIC = false;

	public boolean isCHAOTIC() {
		return _isCHAOTIC;
	}

	public void setCHAOTIC(final boolean flag) {
		_isCHAOTIC = flag;
	}

	public void sendVisualEffectAtTeleport() {
		if (isDrink()) { // 醉酒效果
			this.sendPackets(new S_Liquor(getId()));
		}
		if (isCHAOTIC()) { // 混亂效果
			this.sendPackets(new S_Liquor(getId(), 2));
		}
		sendVisualEffect();
	}

	// 可用技能編號列表
	private final ArrayList<Integer> _skillList = new ArrayList<Integer>();

	/**
	 * 加入技能編號列表
	 * 
	 * @param skillid
	 */
	public void setSkillMastery(final int skillid) {
		if (!_skillList.contains(new Integer(skillid))) {
			_skillList.add(new Integer(skillid));
		}
	}

	/**
	 * 移出技能編號列表
	 * 
	 * @param skillid
	 */
	public void removeSkillMastery(final int skillid) {
		if (_skillList.contains(new Integer(skillid))) {
			_skillList.remove(new Integer(skillid));
		}
	}

	/**
	 * 傳回是否具有該技能使用權
	 * 
	 * @param skillid
	 * @return
	 */
	public boolean isSkillMastery(final int skillid) {
		return _skillList.contains(new Integer(skillid));
	}

	/**
	 * 清空
	 */
	public void clearSkillMastery() {
		_skillList.clear();
	}

	/**
	 * TODO 起始設置
	 */
	public L1PcInstance() {
		_accessLevel = 0;
		_currentWeapon = 0;
		_inventory = new L1PcInventory(this);
		_dwarf = new L1DwarfInventory(this);
		_dwarfForElf = new L1DwarfForElfInventory(this);
		_dwarfForMALL = new L1DwarfForGameMallInventry(this);
		_quest = new L1PcQuest(this);
		_action = new L1ActionPc(this);
		_actionPet = new L1ActionPet(this);
		_actionSummon = new L1ActionSummon(this);
		_equipSlot = new L1EquipmentSlot(this);
		_speed = new AcceleratorChecker(this);
		_kill_npc_Quest = new L1Kill_Npc_Quest(this); // 紀錄殺怪次數
	}

	/**
	 * 娃娃跟隨主人變更移動/速度狀態
	 */
	public void setNpcSpeed() {
		try {
			// 取回娃娃
			if (!getDolls().isEmpty()) {
				for (final Object obj : getDolls().values().toArray()) {
					final L1DollInstance doll = (L1DollInstance) obj;
					if (doll != null) {
						doll.setNpcMoveSpeed();
					}
				}
			}
			// 取回娃娃
			if (get_power_doll() != null) {
				get_power_doll().setNpcMoveSpeed();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void setCurrentHp(final int i) {
		/*
		 * int currentHp = Math.min(i, getMaxHp());
		 * 
		 * if (getCurrentHp() == currentHp) { return; }
		 * 
		 * if (currentHp <= 0) { if (isGm()) { currentHp = getMaxHp();
		 * 
		 * } else { if (!isDead()) { death(null); // HP小於1死亡 } } }
		 * 
		 * setCurrentHpDirect(currentHp); this.sendPackets(new S_HPUpdate(currentHp, getMaxHp())); if (isInParty()) { // 隊伍狀態 getParty().updateMiniHP(this); }
		 */
		if (this.getCurrentHp() == i) {
			return;
		}
		int currentHp = i;
		if (currentHp >= (int) this.getMaxHp()) {
			currentHp = (int) this.getMaxHp();
		}
		this.setCurrentHpDirect(currentHp);
		this.sendPackets(new S_HPUpdate(currentHp, (int) getMaxHp()));
		if (this.isInParty()) { // 隊伍中
			this.getParty().updateMiniHP(this);
		}
	}

	@Override
	public void setCurrentMp(final int i) {
		final int currentMp = Math.min(i, getMaxMp());

		if (getCurrentMp() == currentMp) {
			return;
		}

		setCurrentMpDirect(currentMp);

		this.sendPackets(new S_MPUpdate(currentMp, getMaxMp()));
	}

	@Override
	public L1PcInventory getInventory() {
		return _inventory;
	}

	public L1DwarfInventory getDwarfInventory() {
		return _dwarf;
	}

	public L1DwarfForElfInventory getDwarfForElfInventory() {
		return _dwarfForElf;
	}

	public boolean isGmInvis() {
		return _gmInvis;
	}

	public void setGmInvis(final boolean flag) {
		_gmInvis = flag;
	}

	public int getCurrentWeapon() {
		return _currentWeapon;
	}

	public void setCurrentWeapon(final int i) {
		_currentWeapon = i;
	}

	/**
	 * 0:王族 1:騎士 2:精靈 3:法師 4:黑妖 5:龍騎 6:幻術
	 * 
	 * @return
	 */
	public int getType() {
		return _type;
	}

	/**
	 * 0:王族 1:騎士 2:精靈 3:法師 4:黑妖 5:龍騎 6:幻術
	 * 
	 * @param i
	 */
	public void setType(final int i) {
		_type = i;
		_classFeature = L1ClassFeature.newClassFeature(i); // XXX add 7.6
	}

	public short getAccessLevel() {
		return _accessLevel;
	}

	public void setAccessLevel(final short i) {
		_accessLevel = i;
	}

	public int getClassId() {
		return _classId;
	}

	public void setClassId(final int i) {
		_classId = i;
		// XXX del 7.6
		// _classFeature = L1ClassFeature.newClassFeature(i);
	}

	private L1ClassFeature _classFeature = null;

	public L1ClassFeature getClassFeature() {
		return _classFeature;
	}

	@Override
	public synchronized long getExp() {
		return _exp;
	}

	@Override
	public synchronized void setExp(final long i) {
		_exp = i;
	}

	private int _PKcount; // ● PKカウント

	public int get_PKcount() {
		return _PKcount;
	}

	public void set_PKcount(final int i) {
		_PKcount = i;
	}

	private int _PkCountForElf; // ● PKカウント(エルフ用)

	public int getPkCountForElf() {
		return _PkCountForElf;
	}

	public void setPkCountForElf(final int i) {
		_PkCountForElf = i;
	}

	private int _clanid; // 血盟ID

	public int getClanid() {
		return _clanid;
	}

	public void setClanid(final int i) {
		_clanid = i;
	}

	private String clanname; // 血盟名稱

	public String getClanname() {
		return clanname;
	}

	public void setClanname(final String s) {
		clanname = s;
	}

	/**
	 * 血盟資料
	 * 
	 * @return
	 */
	public L1Clan getClan() {
		return WorldClan.get().getClan(getClanname());
	}

	private int _clanRank; // ● クラン内のランク(血盟君主、ガーディアン、一般、見習い)

	/**
	 * 血盟階級
	 * 
	 * @return 2:一般 3:副君主 4:聯盟君主 5:修習騎士 6:守護騎士 7:一般 8:修習騎士 9:守護騎士 10:聯盟君主
	 */
	public int getClanRank() {
		return _clanRank;
	}

	/**
	 * 血盟階級<BR>
	 * 2:一般 3:副君主 4:聯盟君主 5:修習騎士 6:守護騎士 7:一般 8:修習騎士 9:守護騎士 10:聯盟君主
	 * 
	 * @param i
	 */
	public void setClanRank(final int i) {
		_clanRank = i;
	}

	private byte _sex; // ● 性別

	/**
	 * 性別
	 * 
	 * @return
	 */
	public byte get_sex() {
		return _sex;
	}

	/**
	 * 性別
	 * 
	 * @param i
	 */
	public void set_sex(final int i) {
		_sex = (byte) i;
	}

	public boolean isGm() {
		return _gm;
	}

	public void setGm(final boolean flag) {
		_gm = flag;
	}

	public boolean isMonitor() {
		return _monitor;
	}

	public void setMonitor(final boolean flag) {
		_monitor = flag;
	}

	private L1PcInstance getStat() {
		return null;
	}

	public void reduceCurrentHp(final double d, final L1Character l1character) {
		getStat().reduceCurrentHp(d, l1character);
	}

	/**
	 * 指定されたプレイヤー群にログアウトしたことを通知する
	 * 
	 * @param playersList
	 *            通知するプレイヤーの配列
	 */
	private void notifyPlayersLogout(final List<L1PcInstance> playersArray) {
		for (final L1PcInstance player : playersArray) {
			if (player.knownsObject(this)) {
				player.removeKnownObject(this);
				player.sendPackets(new S_RemoveObject(this));
			}
		}
	}

	public void logout() {
		// 保留技能紀錄
		CharBuffReading.get().deleteBuff(this);
		CharBuffReading.get().saveBuff(this);
		

		// 解除舊座標障礙宣告
		getMap().setPassable(getLocation(), true);

		if (getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(getClanname());
			if (clan != null) {
				if (clan.getWarehouseUsingChar() == getId()) {
					clan.setWarehouseUsingChar(0); // 解除血盟倉庫目前使用者
				}
			}
		}
		notifyPlayersLogout(getKnownPlayers());

		// 正在參加副本
		if (get_showId() != -1) {
			// 副本編號 是執行中副本
			if (WorldQuest.get().isQuest(get_showId())) {
				// 移出副本
				WorldQuest.get().remove(get_showId(), this);
			}
		}
		// 重置副本編號
		set_showId(-1);

		World.get().removeVisibleObject(this);
		World.get().removeObject(this);
		notifyPlayersLogout(World.get().getRecognizePlayer(this));

		// this._inventory.clearItems();
		// this._dwarf.clearItems();

		removeAllKnownObjects();
		stopHpRegeneration();
		stopMpRegeneration();
		setDead(true); // 使い方おかしいかもしれないけど、ＮＰＣに消滅したことをわからせるため
		setNetConnection(null);
		setPacketOutput(null);
	}

	public ClientExecutor getNetConnection() {
		return _netConnection;
	}

	public void setNetConnection(final ClientExecutor clientthread) {
		_netConnection = clientthread;
	}

	/**
	 * 是否再隊伍中
	 * 
	 * @return
	 */
	public boolean isInParty() {
		return getParty() != null;
	}

	/**
	 * 傳回隊伍
	 * 
	 * @return
	 */
	public L1Party getParty() {
		return _party;
	}

	/**
	 * 設置隊伍
	 * 
	 * @param p
	 */
	public void setParty(final L1Party p) {
		_party = p;
	}

	public boolean isInChatParty() {
		return getChatParty() != null;
	}

	public L1ChatParty getChatParty() {
		return _chatParty;
	}

	public void setChatParty(final L1ChatParty cp) {
		_chatParty = cp;
	}

	public int getPartyID() {
		return _partyID;
	}

	public void setPartyID(final int partyID) {
		_partyID = partyID;
	}

	public int getTradeID() {
		return _tradeID;
	}

	public void setTradeID(final int tradeID) {
		_tradeID = tradeID;
	}

	public void setTradeOk(final boolean tradeOk) {
		_tradeOk = tradeOk;
	}

	public boolean getTradeOk() {
		return _tradeOk;
	}

	/**
	 * 傳回暫時紀錄的objid
	 * 
	 * @return
	 */
	public int getTempID() {
		return _tempID;
	}

	/**
	 * 設置暫時紀錄的objid
	 * 
	 * @param tempID
	 */
	public void setTempID(final int tempID) {
		_tempID = tempID;
	}

	/**
	 * 是否為傳送狀態中
	 * 
	 * @return
	 */
	public boolean isTeleport() {
		return _isTeleport;
	}

	/**
	 * 設置傳送狀態中
	 * 
	 * @param flag
	 */
	public void setTeleport(final boolean flag) {
		if (flag) {
			setNowTarget(null);// 解除目前攻擊目標設置
		}
		_isTeleport = flag;
	}

	/**
	 * 醉酒狀態
	 * 
	 * @return
	 */
	public boolean isDrink() {
		return _isDrink;
	}

	/**
	 * 醉酒狀態
	 * 
	 * @param flag
	 */
	public void setDrink(final boolean flag) {
		_isDrink = flag;
	}

	public boolean isGres() {
		return _isGres;
	}

	public void setGres(final boolean flag) {
		_isGres = flag;
	}

	/**
	 * 紅名狀態
	 * 
	 * @return
	 */
	public boolean isPinkName() {
		return _isPinkName;
	}

	/**
	 * 紅名狀態
	 * 
	 * @param flag
	 */
	public void setPinkName(final boolean flag) {
		_isPinkName = flag;
	}

	// 賣出物品清單
	private final ArrayList<L1PrivateShopSellList> _sellList = new ArrayList<L1PrivateShopSellList>();

	/**
	 * 傳回賣出物品清單
	 * 
	 * @return
	 */
	public ArrayList<L1PrivateShopSellList> getSellList() {
		return _sellList;
	}

	// 回收物品清單
	private final ArrayList<L1PrivateShopBuyList> _buyList = new ArrayList<L1PrivateShopBuyList>();

	/**
	 * 傳回回收物品清單
	 * 
	 * @return
	 */
	public ArrayList<L1PrivateShopBuyList> getBuyList() {
		return _buyList;
	}

	private byte[] _shopChat;

	public void setShopChat(final byte[] chat) {
		_shopChat = chat;
	}

	public byte[] getShopChat() {
		return _shopChat;
	}

	private boolean _isPrivateShop = false;

	/**
	 * 傳回商店模式
	 * 
	 * @return
	 */
	public boolean isPrivateShop() {
		return _isPrivateShop;
	}

	/**
	 * 設置商店模式
	 * 
	 * @param flag
	 */
	public void setPrivateShop(final boolean flag) {
		_isPrivateShop = flag;
	}

	// 正在執行個人商店交易
	private boolean _isTradingInPrivateShop = false;

	/**
	 * 正在執行個人商店交易
	 * 
	 * @return
	 */
	public boolean isTradingInPrivateShop() {
		return _isTradingInPrivateShop;
	}

	/**
	 * 正在執行個人商店交易
	 * 
	 * @param flag
	 */
	public void setTradingInPrivateShop(final boolean flag) {
		_isTradingInPrivateShop = flag;
	}

	private int _partnersPrivateShopItemCount = 0; // 出售物品種類數量

	/**
	 * 傳回出售物品種類數量
	 * 
	 * @return
	 */
	public int getPartnersPrivateShopItemCount() {
		return _partnersPrivateShopItemCount;
	}

	/**
	 * 設置出售物品種類數量
	 * 
	 * @param i
	 */
	public void setPartnersPrivateShopItemCount(final int i) {
		_partnersPrivateShopItemCount = i;
	}

	private EncryptExecutor _out;// 封包加密管理

	/**
	 * 設置封包加密管理
	 * 
	 * @param out
	 */
	public void setPacketOutput(final EncryptExecutor out) {
		_out = out;
	}

	/**
	 * 發送單體封包
	 * 
	 * @param packet
	 *            封包
	 */
	public void sendPackets(final ServerBasePacket packet) {
		if (_out == null) {
			return;
		}
		// System.out.println(packet.toString());
		try {
			_out.encrypt(packet);

		} catch (final Exception e) {
			logout();
			close();
		}
	}

	/**
	 * 發送單體封包 與可見範圍發送封包
	 * 
	 * @param packet
	 *            封包
	 */
	public void sendPacketsAll(final ServerBasePacket packet) {
		if (_out == null) {
			return;
		}

		try {
			// 自己
			_out.encrypt(packet);
			if (!isGmInvis() && !isInvisble()) {
				broadcastPacketAll(packet);
			}

		} catch (final Exception e) {
			logout();
			close();
		}
	}

	/**
	 * 發送單體封包 與指定範圍發送封包(範圍8)
	 * 
	 * @param packet
	 *            封包
	 */
	public void sendPacketsX8(final ServerBasePacket packet) {
		if (_out == null) {
			return;
		}

		try {
			// 自己
			_out.encrypt(packet);
			if (!isGmInvis() && !isInvisble()) {
				broadcastPacketX8(packet);
			}

		} catch (final Exception e) {
			logout();
			close();
		}
	}

	/**
	 * 發送單體封包 與指定範圍發送封包(範圍10)
	 * 
	 * @param packet
	 *            封包
	 */
	public void sendPacketsX10(final ServerBasePacket packet) {
		if (_out == null) {
			return;
		}

		try {
			// 自己
			_out.encrypt(packet);
			if (!isGmInvis() && !isInvisble()) {
				broadcastPacketX10(packet);
			}

		} catch (final Exception e) {
			logout();
			close();
		}
	}

	/**
	 * 發送單體封包 與可見指定範圍發送封包
	 * 
	 * @param packet
	 *            封包
	 * @param r
	 *            範圍
	 */
	public void sendPacketsXR(final ServerBasePacket packet, final int r) {
		if (_out == null) {
			return;
		}

		try {
			// 自己
			_out.encrypt(packet);
			if (!isGmInvis() && !isInvisble()) {
				broadcastPacketXR(packet, r);
			}

		} catch (final Exception e) {
			logout();
			close();
		}
	}

	@Override
	public void broadcastPacketForWeapon(final ServerBasePacket packet) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this)) {
				if (!pc.is_send_weapon_gfxid()) {
					continue;
				}
				if (!isGmInvis() && !isInvisble()) {
					if (pc.get_showId() == get_showId()) {
						pc.sendPackets(packet);
					}
				}
				// 副本ID相等
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 關閉連線線程
	 */
	private void close() {
		try {
			getNetConnection().close();
		} catch (final Exception e) {

		}
	}

	/**
	 * 對該物件攻擊的調用
	 * 
	 * @param attacker
	 *            攻擊方
	 */
	@Override
	public void onAction(final L1PcInstance attacker) {
		// NullPointerException回避。onActionの引数の型はL1Characterのほうが良い？
		if (attacker == null) {
			return;
		}
		// テレポート処理中
		if (isTeleport()) {
			return;
		}

		// 雙方之一 位於安全區域 僅送出動作資訊
		if (isSafetyZone() || attacker.isSafetyZone()) {
			// 攻撃モーション送信
			final L1AttackMode attack_mortion = new L1AttackPc(attacker, this);
			attack_mortion.action();
			return;
		}

		// 禁止PK服務器 僅送出動作資訊
		if (checkNonPvP(this, attacker) == true) {
			final L1AttackMode attack_mortion = new L1AttackPc(attacker, this);
			attack_mortion.action();
			return;
		}

		if ((getCurrentHp() > 0) && !isDead()) {
			// 攻擊行為產生解除隱身
			attacker.delInvis();

			boolean isCounterBarrier = false;
			// 開始計算攻擊
			final L1AttackMode attack = new L1AttackPc(attacker, this);
			if (attack.calcHit()) {
				if (hasSkillEffect(COUNTER_BARRIER)) {
					final L1Magic magic = new L1Magic(this, attacker);
					final boolean isProbability = magic.calcProbabilityMagic(COUNTER_BARRIER);
					final boolean isShortDistance = attack.isShortDistance();
					if (isProbability && isShortDistance) {
						isCounterBarrier = true;
					}
				}
				if (!isCounterBarrier) {
					attacker.setPetTarget(this);

					attack.calcDamage();
				}
			}
			if (isCounterBarrier) {
				attack.commitCounterBarrier();

			} else {
				attack.action();
				attack.commit();
			}
		}
	}

	/**
	 * 檢查是否可以攻擊
	 * 
	 * @param pc
	 * @param target
	 * @return
	 */
	public boolean checkNonPvP(final L1PcInstance pc, final L1Character target) {
		L1PcInstance targetpc = null;
		if (target instanceof L1PcInstance) {
			targetpc = (L1PcInstance) target;

		} else if (target instanceof L1PetInstance) {
			targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster();

		} else if (target instanceof L1SummonInstance) {
			targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster();
		}
		if (targetpc == null) {
			return false; // 相手がPC、サモン、ペット以外
		}

		if (!ConfigAlt.ALT_NONPVP) { // Non-PvP設定
			if (getMap().isCombatZone(getLocation())) {
				return false;
			}

			// 取回全部戰爭清單
			for (final L1War war : WorldWar.get().getWarList()) {
				if ((pc.getClanid() != 0) && (targetpc.getClanid() != 0)) { // 共にクラン所属中
					final boolean same_war = war.checkClanInSameWar(pc.getClanname(), targetpc.getClanname());
					if (same_war == true) { // 同じ戦争に参加中
						return false;
					}
				}
			}
			// Non-PvP設定でも戦争中は布告なしで攻撃可能
			if (target instanceof L1PcInstance) {
				final L1PcInstance targetPc = (L1PcInstance) target;
				if (isInWarAreaAndWarTime(pc, targetPc)) {
					return false;
				}
			}
			return true;

		} else {
			return false;
		}
	}

	/**
	 * 戰爭旗幟座標內
	 * 
	 * @param pc
	 * @param target
	 * @return
	 */
	private boolean isInWarAreaAndWarTime(final L1PcInstance pc, final L1PcInstance target) {
		// pcとtargetが戦争中に戦争エリアに居るか
		final int castleId = L1CastleLocation.getCastleIdByArea(pc);
		final int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
		if ((castleId != 0) && (targetCastleId != 0) && (castleId == targetCastleId)) {
			if (ServerWarExecutor.get().isNowWar(castleId)) {
				return true;
			}
		}
		return false;
	}

	private static boolean _debug = Config.DEBUG;

	/**
	 * 設置 寵物/召換獸/分身/護衛 攻擊目標
	 * 
	 * @param target
	 */
	public void setPetTarget(final L1Character target) {
		if (target == null) {
			return;
		}
		if (target.isDead()) {
			return;
		}
		final Map<Integer, L1NpcInstance> petList = getPetList();

		// 有寵物元素
		try {
			if (!petList.isEmpty()) {// 有寵物元素
				for (final Iterator<L1NpcInstance> iter = petList.values().iterator(); iter.hasNext();) {
					final L1NpcInstance pet = iter.next();
					if (pet != null) {
						if (pet instanceof L1PetInstance) {// 寵物
							final L1PetInstance pets = (L1PetInstance) pet;
							pets.setMasterTarget(target);

						} else if (pet instanceof L1SummonInstance) {// 召換獸
							final L1SummonInstance summon = (L1SummonInstance) pet;
							summon.setMasterTarget(target);
						}
					}
				}
			}

		} catch (final Exception e) {
			if (_debug) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		final Map<Integer, L1IllusoryInstance> illList = get_otherList().get_illusoryList();

		// 有分身元素
		try {
			if (!illList.isEmpty()) {// 有分身元素
				// 控制分身攻擊
				if (getId() != target.getId()) {
					for (final Iterator<L1IllusoryInstance> iter = illList.values().iterator(); iter.hasNext();) {
						final L1IllusoryInstance ill = iter.next();
						if (ill != null) {
							ill.setLink(target);
						}
					}
				}
			}

		} catch (final Exception e) {
			if (_debug) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * 解除隱身術/暗隱術
	 */
	public void delInvis() {
		if (hasSkillEffect(INVISIBILITY)) { // 隱身術
			killSkillEffectTimer(INVISIBILITY);
			this.sendPackets(new S_Invis(getId(), 0));
			broadcastPacketAll(new S_OtherCharPacks(this));
		}
		if (hasSkillEffect(BLIND_HIDING)) { // 暗隱術
			killSkillEffectTimer(BLIND_HIDING);
			this.sendPackets(new S_Invis(getId(), 0));
			broadcastPacketAll(new S_OtherCharPacks(this));
		}
	}

	/**
	 * 解除暗隱術
	 */
	public void delBlindHiding() {
		killSkillEffectTimer(BLIND_HIDING);
		this.sendPackets(new S_Invis(getId(), 0));
		broadcastPacketAll(new S_OtherCharPacks(this));
	}

	/**
	 * 魔法具有属性傷害使用 (魔法抗性處理) attr:0.無属性魔法,1.地魔法,2.火魔法,4.水魔法,8.風魔法 (武器技能使用)
	 * 
	 * @param attacker
	 * @param damage
	 * @param attr
	 */
	public void receiveDamage(final L1Character attacker, double damage, final int attr) {
		final int player_mr = getMr();
		final int rnd = _random.nextInt(300) + 1;
		if (player_mr >= rnd) {
			damage /= 2.0;
		}

		int resist = 0;
		switch (attr) {
		case L1Skills.ATTR_EARTH:
			resist = getEarth();
			break;

		case L1Skills.ATTR_FIRE:
			resist = getFire();
			break;

		case L1Skills.ATTR_WATER:
			resist = getWater();
			break;

		case L1Skills.ATTR_WIND:
			resist = getWind();
			break;
		}

		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;

		} else {
			resistFloor *= -1;
		}

		final double attrDeffence = resistFloor / 32.0;

		final double coefficient = ((1.0 - attrDeffence) + (3.0 / 32.0));// 0.09375

		if (coefficient > 0) {
			damage *= coefficient;
		}
		this.receiveDamage(attacker, damage, false, false);
	}

	/**
	 * 受攻擊mp減少計算
	 * 
	 * @param attacker
	 * @param mpDamage
	 */
	public void receiveManaDamage(final L1Character attacker, final int mpDamage) {
		if ((mpDamage > 0) && !isDead()) {
			delInvis();
			if (attacker instanceof L1PcInstance) {
				L1PinkName.onAction(this, attacker);
			}
			if ((attacker instanceof L1PcInstance) && ((L1PcInstance) attacker).isPinkName()) {
				// ガードが画面内にいれば、攻撃者をガードのターゲットに設定する
				for (final L1Object object : World.get().getVisibleObjects(attacker)) {
					if (object instanceof L1GuardInstance) {
						final L1GuardInstance guard = (L1GuardInstance) object;
						guard.setTarget(((L1PcInstance) attacker));
					}
				}
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp > getMaxMp()) {
				newMp = getMaxMp();
			}
			newMp = Math.max(newMp, 0);

			setCurrentMp(newMp);
		}
	}

	public long _oldTime = 0; // 連續魔法減低損傷使用

	private static final Map<Long, Double> _magicDamagerList = new HashMap<Long, Double>();

	/**
	 * 連續魔法減低損傷質預先載入 特殊定義道具 預先載入
	 */
	public static void load() {
		double newdmg = 100.00;
		for (long i = 2000; i > 0; i--) {
			if ((i % 100) == 0) {
				newdmg -= 3.33;
			}
			_magicDamagerList.put(i, newdmg);
		}
	}

	/**
	 * 連續魔法減低損傷
	 * 
	 * @param damage
	 * @return
	 */
	public double isMagicDamager(final double damage) {
		final long nowTime = System.currentTimeMillis();
		final long interval = nowTime - _oldTime;

		double newdmg = 0;
		if (damage < 0) {
			newdmg = damage;

		} else {
			final Double tmpnewdmg = _magicDamagerList.get(interval);
			if (tmpnewdmg != null) {
				newdmg = (damage * tmpnewdmg) / 100;

			} else {
				newdmg = damage;
			}
			newdmg = Math.max(newdmg, 0);

			_oldTime = nowTime; // 次回時間紀錄
		}
		return newdmg;
	}

	/**
	 * 受攻擊hp減少計算
	 * 
	 * @param attacker
	 *            攻擊者
	 * @param damage
	 *            傷害
	 * @param isMagicDamage
	 *            連續魔法傷害減低
	 * @param isCounterBarrier
	 *            這個傷害是否不執行反饋 true:不執行反饋 false:執行反饋
	 */
	public void receiveDamage(final L1Character attacker, double damage, final boolean isMagicDamage, final boolean isCounterBarrier) {
		if ((getCurrentHp() > 0) && !isDead()) {

			if (attacker != null) {
				if (attacker != this) {
					if (!(attacker instanceof L1EffectInstance) && !knownsObject(attacker) && (attacker.getMapId() == getMapId())) {
						attacker.onPerceive(this);
					}
				}

				// 連續魔法傷害減低
				if (isMagicDamage == true) {
					damage = isMagicDamager(damage);
				}

				// 攻擊者定義
				L1PcInstance attackPc = null;
				L1NpcInstance attackNpc = null;

				if (attacker instanceof L1PcInstance) {
					if (getId() != attacker.getId()) {
						attackPc = (L1PcInstance) attacker;// 攻擊者為PC
					}

				} else if (attacker instanceof L1NpcInstance) {
					attackNpc = (L1NpcInstance) attacker;// 攻擊者為NPC
				}

				// 傷害大於等於0(小於0回復HP)
				if (damage > 0) {
					// 解除隱身
					delInvis();
					// 解除沉睡之霧
					removeSkillEffect(FOG_OF_SLEEPING);
					// 解除冥想術 by terry0412
					killSkillEffectTimer(MEDITATION);

					if (attackPc != null) {
						L1PinkName.onAction(this, attackPc);
						if (attackPc.isPinkName()) {
							// 警衛對攻擊者的處分
							for (final L1Object object : World.get().getVisibleObjects(attacker)) {
								if (object instanceof L1GuardInstance) {
									final L1GuardInstance guard = (L1GuardInstance) object;
									guard.setTarget(((L1PcInstance) attacker));
								}
							}
						}
					}
				}

				if (!isCounterBarrier) {// false:執行反饋
					// 致命身軀(自身具有效果)
					if (hasSkillEffect(MORTAL_BODY)) {
						if (getId() != attacker.getId()) {
							final int rnd = _random.nextInt(100) + 1;
							if ((damage > 0) && (rnd <= 18)) {// 2011-11-26 0-15
								final int dmg = attacker.getLevel();
								// SRC DMG = 50
								if (attackPc != null) {
									attackPc.sendPacketsX10(new S_DoActionGFX(attackPc.getId(), ActionCodes.ACTION_Damage));
									attackPc.receiveDamage(this, dmg, false, true);

								} else if (attackNpc != null) {
									attackNpc.broadcastPacketX10(new S_DoActionGFX(attackNpc.getId(), ActionCodes.ACTION_Damage));
									attackNpc.receiveDamage(this, dmg);
								}
							}
						}
					}
					// 林德拜爾的反屏
					if (!isMagicDamage && (attackPc != null) && (_elitePlateMail_Lindvior > 0)) {
						final L1ItemInstance weapon = attackPc.getWeapon();
						if ((weapon != null) && ((weapon.getItem().getType1() == 20) || (weapon.getItem().getType1() == 62))) {
							if (_random.nextInt(1000) <= _elitePlateMail_Lindvior) {
								sendPacketsX8(new S_SkillSound(getId(), 10419));
								final int nowDamage = _random.nextInt((_lindvior_dmgmax - _lindvior_dmgmin) + 1) + _lindvior_dmgmin;
								attackPc.receiveDamage(this, nowDamage, false, true);
							}
						}
					}
					// 魔化黑帝斯的反屏
					if (!isMagicDamage && attackPc != null && _elitePlateMail_Hades > 0) {
						final L1ItemInstance weapon = attackPc.getWeapon();
						if (weapon != null) {
							if (_random.nextInt(1000) <= _elitePlateMail_Hades) {
								this.sendPacketsX8(new S_SkillSound(this.getId(), 16152)); // 特效新增
								final int nowDamage = _Hades_dmg;
								attackPc.receiveDamage(this, nowDamage, false, true);
							}
						}
					}
				}
			}

			// 裝備使自己傷害加深的裝備
			if (getInventory().checkEquipped(145) // 狂戰士斧
					|| getInventory().checkEquipped(149)) { // 牛人斧頭
				damage *= 1.5; // 傷害提高1.5倍
			}

			// 幻覺：化身219
			if (hasSkillEffect(ILLUSION_AVATAR)) {
				damage *= 1.15; // 傷害提高1.15倍 20151102
			}

			int addhp = 0;
			if (_elitePlateMail_Fafurion > 0) {
				if (_random.nextInt(1000) <= _elitePlateMail_Fafurion) {
					sendPacketsX8(new S_EffectLocation(getX(), getY(), 2187));
					addhp = _random.nextInt((_fafurion_hpmax - _fafurion_hpmin) + 1) + _fafurion_hpmin;// 受到攻擊時，4%的機率會恢復體力72~86點。
				}
			}
			if (_shieldOfRebels_Chance > 0) {
				if (_random.nextInt(100) < _shieldOfRebels_Chance) {
					sendPacketsX8(new S_EffectLocation(getX(), getY(), 6320));
					if (damage > 0) {
						damage = Math.max(0, damage - _shieldOfRebels_dmg_reduction);
					}
				}
			}

			int newHp = (getCurrentHp() - (int) (damage)) + addhp;
			if (newHp > getMaxHp()) {
				newHp = getMaxHp();
			}
			if (newHp <= 0) {
				if (!isGm()) {
					death(attacker);
				} else {
					newHp = getMaxHp();
					// _log.error("GM是不死的");
				}
			}

			setCurrentHp(newHp);

		} else if (!isDead()) {
			_log.error("人物hp減少處理失敗 可能原因: 初始hp為0");
			death(attacker);
		}
	}

	/**
	 * 死亡的處理
	 * 
	 * @param lastAttacker
	 *            攻擊致死的攻擊者
	 */
	public void death(final L1Character lastAttacker) {
		synchronized (this) {
			if (isDead()) {
				return;
			}
			setNowTarget(null);// 解除目前攻擊目標設置
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);

			// 死亡執行緒 (變更位置 by terry0412)
			GeneralThreadPool.get().execute(new Death(lastAttacker));
		}

	}

	/**
	 * 人物死亡的處理
	 * 
	 * @author dexc
	 */
	private class Death implements Runnable {

		private L1Character _lastAttacker;

		private Death(final L1Character cha) {
			_lastAttacker = cha;
		}

		@Override
		public void run() {
			final L1Character lastAttacker = _lastAttacker;
			_lastAttacker = null;
			setCurrentHp(0);
			setGresValid(false); // EXPロストするまでG-RES無効

			if (get_redbluejoin() > 0 && get_redbluepoint() > 0) {
				if (lastAttacker instanceof L1PcInstance) {
					L1PcInstance killer = (L1PcInstance) lastAttacker;
					if (get_redblueleader() == 0) {
						int nplus = ConfigOther.RedBlueNormal_point;
						if (nplus >= get_redbluepoint()) {
							nplus = get_redbluepoint();
						}
						set_redbluepoint(get_redbluepoint() - nplus);
						killer.set_redbluepoint(get_redbluepoint() + nplus);
						killer.sendPackets(new S_ServerMessage("\\aG你殺死[敵軍成員]獲得\\aD" + nplus + "點\\aG積分！"));
					} else if (get_redblueleader() > 0) {
						int lplus = ConfigOther.RedBlueLeader_point;
						if (lplus >= get_redbluepoint()) {
							lplus = get_redbluepoint();
						}
						set_redbluepoint(get_redbluepoint() - lplus);
						killer.set_redbluepoint(get_redbluepoint() + lplus);
						killer.sendPackets(new S_ServerMessage("\\aG你殺死[敵軍隊長]獲得\\aD" + lplus + "點\\aG積分了！"));
					}
				}
			}

			while (isTeleport()) { // 傳送狀態中延遲
				try {
					Thread.sleep(300);

				} catch (final Exception e) {
				}
			}

			// 加入死亡清單
			set_delete_time(300);

			// 娃娃刪除
			if (!getDolls().isEmpty()) {
				for (final Object obj : getDolls().values().toArray()) {
					final L1DollInstance doll = (L1DollInstance) obj;
					doll.deleteDoll();
				}
			}

			// 超級娃娃
			if (get_power_doll() != null) {
				get_power_doll().deleteDoll();
			}

			stopHpRegeneration();
			stopMpRegeneration();

			final int targetobjid = getId();
			getMap().setPassable(getLocation(), true);

			// 死亡時具有變身狀態
			int tempchargfx = 0;
			if (hasSkillEffect(SHAPE_CHANGE)) {
				tempchargfx = getTempCharGfx();
				setTempCharGfxAtDead(tempchargfx);

			} else {
				setTempCharGfxAtDead(getClassId());
			}

			// 死亡時 現有技能消除
			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(L1PcInstance.this, CANCELLATION, getId(), getX(), getY(), 0, L1SkillUse.TYPE_LOGIN);

			// シャドウ系変身中に死亡するとクライアントが落ちるため暫定対応
			if ((tempchargfx == 5727) || (tempchargfx == 5730) || (tempchargfx == 5733) || (tempchargfx == 5736)) {
				tempchargfx = 0;
			}

			if (tempchargfx == 7351) {
				tempchargfx = getClassId();
				setTempCharGfx(tempchargfx);
			}

			if (tempchargfx != 0) {
				// System.out.println("tempchargfx: " + tempchargfx);
				sendPacketsAll(new S_ChangeShape(L1PcInstance.this, tempchargfx));

			} else {
				// シャドウ系変身中に攻撃しながら死亡するとクライアントが落ちるためディレイを入れる
				try {
					Thread.sleep(1000);
				} catch (final Exception e) {
				}
			}

			// boolean isSafetyZone = false;// 是否為安全區中

			boolean isCombatZone = false;// 是否為戰鬥區中

			boolean isWar = false;// 是否參戰

			/*
			 * if (L1PcInstance.this.isSafetyZone()) { isSafetyZone = true; }
			 */
			if (isCombatZone() && (lastAttacker instanceof L1PcInstance)) {
				isCombatZone = true;
			}

			// 殺人次數的減少
			if (lastAttacker instanceof L1GuardInstance) {
				if (get_PKcount() > 0) {
					set_PKcount(get_PKcount() - 1);
				}
				L1PcInstance.this.setLastPk(null);
			}

			if (lastAttacker instanceof L1GuardianInstance) {
				if (getPkCountForElf() > 0) {
					setPkCountForElf(getPkCountForElf() - 1);
				}
				L1PcInstance.this.setLastPkForElf(null);
			}

			// 檢查攻擊者是否為PC(寵物 定義為主人)
			L1PcInstance fightPc = null;

			if (lastAttacker instanceof L1PcInstance) {// 攻擊者是玩家
				fightPc = (L1PcInstance) lastAttacker;

			} else if (lastAttacker instanceof L1PetInstance) {// 攻擊者是寵物
				final L1PetInstance npc = (L1PetInstance) lastAttacker;
				if (npc.getMaster() != null) {
					fightPc = (L1PcInstance) npc.getMaster();
				}

			} else if (lastAttacker instanceof L1SummonInstance) {// 攻擊者是 召換獸
				final L1SummonInstance npc = (L1SummonInstance) lastAttacker;
				if (npc.getMaster() != null) {
					fightPc = (L1PcInstance) npc.getMaster();
				}

			} else if (lastAttacker instanceof L1IllusoryInstance) {// 攻擊者是 分身
				final L1IllusoryInstance npc = (L1IllusoryInstance) lastAttacker;
				if (npc.getMaster() != null) {
					fightPc = (L1PcInstance) npc.getMaster();
				}

			} else if (lastAttacker instanceof L1EffectInstance) {// 攻擊者是 技能物件
				final L1EffectInstance npc = (L1EffectInstance) lastAttacker;
				if (npc.getMaster() != null) {
					fightPc = (L1PcInstance) npc.getMaster();
				}
			}

			L1PcInstance.this.sendPacketsAll(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			L1NpcInstance tgef = L1SpawnUtil.spawnEffect(81400, 5, L1PcInstance.this.getX(), L1PcInstance.this.getY(), L1PcInstance.this.getMapId(), L1PcInstance.this, 0);

			tgef.broadcastPacketAll(new S_DoActionGFX(tgef.getId(), ActionCodes.ACTION_SwordWalk));

			if (fightPc != null) {
				// 決鬥中
				if ((getFightId() == fightPc.getId()) && (fightPc.getFightId() == getId())) {
					setFightId(0);
					L1PcInstance.this.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					fightPc.setFightId(0);
					fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					return;
				}

				// 效果: 被超過10級以上的玩家攻擊而死亡時，不會失去經驗值，也不會掉落物品
				if (isEncounter()) {// 遭遇的守護
					if (fightPc.getLevel() > getLevel()) {
						if ((fightPc.getLevel() - getLevel()) >= 10) {
							return;
						}
					}
				}

				// 攻城戰爭進行狀態
				if (castleWarResult()) {
					isWar = true;
				}

				// 血盟戰爭進行狀態
				if (simWarResult(lastAttacker)) {
					isWar = true;
				}

				// 攻城戰進行狀態
				if (isInWarAreaAndWarTime(L1PcInstance.this, fightPc)) {
					isWar = true;
				}

				// 死亡公告 2014/08/08 by Roy 加入新的公告於畫面中間顯示
				// 殺人公告 2014/08/22 by Roy 加入殺人公告(使用武器版)
				if (getLevel() >= ConfigKill.KILLLEVEL) {
					if (!fightPc.isGm()) {
						boolean isShow = false;// 是否公告
						if (isWar) {// 戰爭中
							isShow = false;

						} else {// 非戰爭中
							// 非戰鬥區
							if (!isCombatZone) {
								isShow = true;
							}
						}
						if (isShow) {
							final String x1 = ConfigKill.KILL_TEXT_LIST.get(_random.nextInt(ConfigKill.KILL_TEXT_LIST.size()) + 1);
							final String lasttxt = String.format(x1, "\\f=【\\f>" + fightPc.getName() + "\\f=】\\f2使用 +" + fightPc.getWeapon().getEnchantLevel()
									+ fightPc.getWeapon().getName() + " ", "\\f=【\\f>" + getName() + "\\f=】\\f=");
							// 殺人特效 2015/11/22
							try {
								final S_SkillSound sound = new S_SkillSound(fightPc.getId(), 440);
								fightPc.sendPacketsX8(sound);
								S_SkillSound sound1 = new S_SkillSound(fightPc.getId(), 452);
								fightPc.sendPackets(sound1);
								WorldChatTimer.addchat(lasttxt);
							} catch (final InterruptedException e) {
								e.printStackTrace();
							}
							// 下列註解不同步顯示於對話窗世界頻道
							/*
							 * World.get().broadcastPacketToAll( new S_KillMessage(fightPc.getViewName(), L1PcInstance.this.getViewName()));
							 */
							fightPc.get_other().add_killCount(1);
							get_other().add_deathCount(1);
						}
					}
				}
			}

			// 安全區中
			/*
			 * if (isSafetyZone) { return; }
			 */
			// 戰鬥區中
			if (isCombatZone) {
				return;
			}
			// 死亡逞罰
			if (!getMap().isEnabledDeathPenalty()) {
				return;
			}

			// 正義質未滿
			if (getLawful() < 32767) {
				// 守護者系統 (死亡時是否掉落道具) by terry0412
				if (isProtector() && !ProtectorSet.DEATH_VALUE_ITEM) {

				} else {
					// 物品掉落判斷
					lostRate();

					// 技能掉落的判斷
					lostSkillRate();
				}
			}

			// 參戰中
			if (isWar) {
				return;
			}

			final boolean castle_area = L1CastleLocation.checkInAllWarArea(getX(), getY(), getMapId());
			if (castle_area) { // 戰爭旗中
				return;
			}

			// 積分掉落的判斷
			c1TypeRate(fightPc);

			// 經驗值掉落的判斷
			expRate();

			// 道具奪取系統 by terry0412
			if (fightPc != null) {
				checkItemSteal(fightPc);
			}

			if (fightPc != null) {
				if ((fightPc.getClan() != null) && (getClan() != null)) {
					if (WorldWar.get().isWar(fightPc.getClan().getClanName(), getClan().getClanName())) {
						return;
					}
				}
				if (fightPc.isSafetyZone()) {
					return;
				}
				if (fightPc.isCombatZone()) {
					return;
				}
				if ((getLawful() >= 0) && (isPinkName() == false)) {
					boolean isChangePkCount = false;
					// boolean isChangePkCountForElf = false;
					// アライメントが30000未満の場合はPKカウント増加
					if (fightPc.getLawful() < 30000) {
						fightPc.set_PKcount(fightPc.get_PKcount() + 1);
						isChangePkCount = true;
						if (fightPc.isElf() && isElf()) {
							fightPc.setPkCountForElf(fightPc.getPkCountForElf() + 1);
							// isChangePkCountForElf = true;
						}
					}
					fightPc.setLastPk();
					if (fightPc.isElf() && isElf()) {
						fightPc.setLastPkForElf();
					}

					// アライメント処理
					// 公式の発表および各LVでのPKからつじつまの合うように変更
					// （PK側のLVに依存し、高LVほどリスクも高い）
					// 48あたりで-8kほど DKの時点で10k強
					// 60で約20k強 65で30k弱
					int lawful;

					if (fightPc.getLevel() < 50) {
						// lawful = -1 * (int) ((Math.pow(fightPc.getLevel(), 2)
						// * 4));
						lawful = -1 * (((int) Math.pow(fightPc.getLevel(), 2)) << 2);

					} else {
						lawful = -1 * (int) ((Math.pow(fightPc.getLevel(), 3) * 0.08));
					}
					// もし(元々のアライメント-1000)が計算後より低い場合
					// 元々のアライメント-1000をアライメント値とする
					// （連続でPKしたときにほとんど値が変わらなかった記憶より）
					// これは上の式よりも自信度が低いうろ覚えですので
					// 明らかにこうならない！という場合は修正お願いします
					if ((fightPc.getLawful() - 1000) < lawful) {
						lawful = fightPc.getLawful() - 1000;
					}

					if (lawful <= -32768) {
						lawful = -32768;
					}
					fightPc.setLawful(lawful);

					fightPc.sendPacketsAll(new S_Lawful(fightPc));

					if (ConfigAlt.ALT_PUNISHMENT) {
						if (isChangePkCount && (fightPc.get_PKcount() >= 5) && (fightPc.get_PKcount() < 100)) {
							// あなたのPK回数が%0になりました。回数が%1になると地獄行きです。
							fightPc.sendPackets(new S_BlueMessage(551, String.valueOf(fightPc.get_PKcount()), "100"));

						} else if (isChangePkCount && (fightPc.get_PKcount() >= 100)) {
							fightPc.beginHell(true);
						}
					}

				} else {
					sendPacketsAll(new S_PinkName(getId(), 0));
					setPinkName(false);
				}
			}
		}

		private void c1TypeRate(L1PcInstance fightPc) {
			if (CampSet.CAMPSTART) {
				// 陣營系統啟用 XXX
				if ((_c_power != null) && (_c_power.get_c1_type() != 0)) {
					if (_c_power.get_c1_type() != 0) {
						// 月卡積分保護
						if (_vip_2) {
							sendPackets(new S_ServerMessage("\\aD你已經啟動積分保護！"));
							return;
						}
						// 預設可保護陣營積分物品
						final L1ItemInstance item1 = getInventory().checkItemX(44165, 1);// 44165
																							// 貢獻度護身符
						if (item1 != null) {
							getInventory().removeItem(item1, 1);// 删除1个药水
							sendPackets(new S_ServerMessage("\\aD你身上帶有" + item1.getName() + ",陣營積分受到守護!"));
							return;
						}
						final L1Name_Power power = _c_power.get_power();
						final int score = _other.get_score() - power.get_down();
						if (_other.get_score() > 0) {
							sendPackets(new S_ServerMessage("\\aF您的陣營積分損失了:" + power.get_down()));
						}
						if (score > 0) {
							_other.set_score(score);
						} else {
							_other.set_score(0);
						}

						final int lv = C1_Name_Type_Table.get().getLv(_c_power.get_c1_type(), _other.get_score());
						if (lv != _c_power.get_power().get_c1_id()) {
							_c_power.set_power(L1PcInstance.this, false);
							sendPackets(new S_ServerMessage("\\aD您的位階將變更為:" + _c_power.get_power().get_c1_name_type()));
							sendPacketsAll(new S_ChangeName(L1PcInstance.this, true));
						}
						try {
							// Smile 加入威望搶奪機制 150428
							if (fightPc != null && ConfigOther.Prestigesnatch) {
								if (_c_power.get_c1_type() == fightPc._c_power.get_c1_type()) {
									final int fightPc_addscore = (int) (power.get_down() * ConfigOther.camp1); // 相同陣營
									fightPc.get_other().add_score(fightPc_addscore);
									fightPc.sendPackets(new S_ServerMessage("\\aL您殺死<相同陣營者>獲得了積分:" + fightPc_addscore));
								} else {
									final int fightPc_addscore = (int) (power.get_down() * ConfigOther.camp2); // 不同陣營
									fightPc.get_other().add_score(fightPc_addscore);
									fightPc.sendPackets(new S_ServerMessage("\\aH您殺死<不同陣營者>獲得了積分:" + fightPc_addscore));
								}

								final int fightPc_lv = C1_Name_Type_Table.get().getLv(fightPc._c_power.get_c1_type(), fightPc.get_other().get_score());
								if (fightPc_lv != fightPc._c_power.get_power().get_c1_id()) {
									fightPc._c_power.set_power(fightPc, false);
									fightPc.sendPackets(new S_ServerMessage("\\aD您的陣營位階變更為:" + fightPc._c_power.get_power().get_c1_name_type()));
									fightPc.sendPacketsAll(new S_ChangeName(fightPc, true));
								}
							}
						} catch (Exception e) {
							System.out.println(e.getStackTrace());
						}

					}
				}
			}
		}

		/**
		 * 道具奪取判斷 by terry0412
		 */
		private void checkItemSteal(final L1PcInstance fightPc) {
			// 沒有設置列表...
			if (ExtraItemStealTable.getInstance().getList().isEmpty()) {
				return;
			}

			// 目前時間
			final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			for (final L1ItemSteal itemSteal : ExtraItemStealTable.getInstance().getList()) {
				// 檢查身上是否有可被奪取的道具
				final L1ItemInstance steal_item = getInventory().findItemId(itemSteal.getItemId());
				if (steal_item == null) {
					continue;
				}

				// 限制可奪取玩家最低等級
				if (getLevel() < itemSteal.getLevel()) {
					continue;
				}

				// 限制可奪取玩家最低轉生數
				if (getMeteLevel() < itemSteal.getMeteLevel()) {
					continue;
				}

				// 死亡被奪取機率
				if (_random.nextInt(100) >= itemSteal.getStealChance()) {
					continue;
				}

				// 檢查身上是否有防止奪取的道具
				if ((itemSteal.getAntiStealItemId() > 0) && getInventory().consumeItem(itemSteal.getAntiStealItemId(), 1)) {
					sendPackets(new S_SystemMessage("由於身上有[" + ItemTable.get().getTemplate(itemSteal.getAntiStealItemId()).getNameId() + "] 使你免於被對方奪取: " + steal_item.getLogName()));
					continue;
				}

				// 計算奪取數量
				long steal_count;

				// 可重疊物品
				if (steal_item.isStackable()) {
					// 設定隨機數量
					steal_count = _random.nextInt(Math.max(itemSteal.getMaxStealCount() - itemSteal.getMinStealCount(), 0) + 1) + itemSteal.getMinStealCount();
					// 檢查擁有數量
					steal_count = steal_item.getCount() >= steal_count ? steal_count : steal_item.getCount();

				} else {
					// 非重疊物品永遠數量1
					steal_count = 1L;
					// 解除使用狀態
					getInventory().setEquipped(steal_item, false);
				}

				// 您損失了 %0。
				sendPackets(new S_ServerMessage(638, steal_item.getNumberedViewName(steal_count)));

				// 被奪取道具是否掉落在地面 (1=掉地面, 0=掉在攻擊者身上)
				if (itemSteal.isDropOnFloor()) {
					steal_item.set_showId(get_showId());
					// 轉移地面
					getInventory().tradeItem(steal_item, steal_count, World.get().getInventory(getX(), getY(), getMapId()));

					// 是否廣播
					if (itemSteal.isBroadcast()) {
						World.get().broadcastPacketToAll(new S_SystemMessage("玩家[" + getViewName() + "]死亡後, 不小心把[" + steal_item.getNumberedViewName(steal_count) + "]掉在地板上"));
					}

					// 記錄文件檔 by terry0412
					ConfigRecord.recordToFiles("死亡奪取物品", "IP(" + getNetConnection().getIp() + ")玩家【" + getName() + "】的【" + steal_item.getNumberedViewName(steal_count) + ", (ObjId: "
							+ steal_item.getId() + ")】死亡後掉在地板上, 時間:(" + timestamp + ")", timestamp);

				} else {
					// 轉移攻擊者身上
					getInventory().tradeItem(steal_item, steal_count, fightPc.getInventory());

					// 獲得%0%o 。
					fightPc.sendPackets(new S_ServerMessage(403, steal_item.getNumberedViewName(steal_count)));

					// 是否廣播
					if (itemSteal.isBroadcast()) {
						World.get().broadcastPacketToAll(
								new S_SystemMessage("玩家[" + getViewName() + "]死亡後, 不小心被玩家[" + fightPc.getViewName() + "]搶走了[" + steal_item.getNumberedViewName(steal_count) + "]"));
					}

					// 記錄文件檔 by terry0412
					ConfigRecord.recordToFiles("死亡奪取物品", "IP(" + getNetConnection().getIp() + ")玩家【" + getName() + "】的【" + steal_item.getNumberedViewName(steal_count) + ", (ObjId: "
							+ steal_item.getId() + ")】死亡後被玩家【" + fightPc.getName() + "】奪走, 時間:(" + timestamp + ")", timestamp);
				}
			}
		}

		/**
		 * <FONT COLOR="#0000ff">經驗值掉落判斷</FONT>
		 */
		private void expRate() {
			// 守護者系統 (死亡時是否掉落經驗值) by terry0412
			if (isProtector()) {
				// 移除道具
				final L1ItemInstance item = getInventory().findItemId(ProtectorSet.ITEM_ID);
				if (item != null) {
					getInventory().removeItem(item, 1);

					// 可惜XXXXX死亡他的守護者靈魂消失了
					World.get().broadcastPacketToAll(new S_PacketBoxGree(0x02, "可惜 [" + getName() + "] 死亡後他的守護者靈魂消失了"));
				}

				if (!ProtectorSet.DEATH_VALUE_EXP) {
					return;
				}
			}
			// 月卡經驗保護
			if (_vip_1) {
				sendPackets(new S_ServerMessage("\\aG你已經啟動 經驗 保護！"));
				return;
			}
			final L1ItemInstance item1 = getInventory().checkItemX(80003, 1);
			if (item1 != null) {// 44164 諸葛妙計錦囊
				getInventory().removeItem(item1, 1);// 删除1个药水
				sendPackets(new S_ServerMessage("\\aD你身上帶有" + item1.getName() + ",剛剛死掉沒有掉%!"));
				return;
			}
			final L1ItemInstance item2 = getInventory().checkItemX(44164, 1);
			if (item2 != null) {// 44164 死亡守護經驗之書
				getInventory().removeItem(item2, 1);// 删除1个药水
				sendPackets(new S_ServerMessage("\\aD你身上帶有" + item2.getName() + ",剛剛死掉沒有掉%!"));
				return;
			} else if (L1WilliamGfxIdOrginal.DeadExp(getTempCharGfx())) { // 寫入變身特化外觀不掉落經驗
				setExpRes(1);
				return;
			}

			deathPenalty(); // 經驗質逞罰

			setGresValid(true); // EXPロストしたらG-RES有効

			if (getExpRes() == 0) {
				setExpRes(1);
			}

			// onChangeExp();
		}

		/**
		 * <FONT COLOR="#0000ff">物品掉落判斷</FONT>
		 */
		private void lostRate() {
			// 月卡物品保護
			if (_vip_3) {
				sendPackets(new S_ServerMessage("\\aD你已經啟動物品保護！"));
				return;
			}
			final L1ItemInstance item1 = getInventory().checkItemX(80000, 1);
			if (item1 != null) {// 44163 神聖護身符
				getInventory().removeItem(item1, 1);
				sendPackets(new S_ServerMessage("\\aD你身上帶有" + item1.getName() + ",剛剛死掉沒有噴裝!"));
				return;
			}
			final L1ItemInstance item2 = getInventory().checkItemX(44163, 1);
			if (item2 != null) {// 44163 死亡道具守護書
				getInventory().removeItem(item2, 1);
				sendPackets(new S_ServerMessage("\\aD你身上帶有" + item2.getName() + ",剛剛死掉沒有噴裝!"));
				return;
			}

			// 產生物品掉落機率
			// 正義質32000以上0%、每-1000增加0.4%
			// 正義質小於0 每-1000增加0.8%
			// 正義質-32000以下 最高51.2%掉落率
			int lostRate = ((int) (((getLawful() + 32768D) / 1000D) - 65D)) << 2;

			if (lostRate < 0) {
				lostRate *= -1;
				if (getLawful() < 0) {
					// lostRate *= 2;
					lostRate = lostRate << 1;
				}
				final int rnd = _random.nextInt(1000) + 1;
				if (rnd <= lostRate) {
					int count = 0;
					final int lawful = getLawful();
					if (lawful <= -32768) {// 小於-30000掉落1~10件
						count = _random.nextInt(10) + 1;

					} else if ((lawful > -32768) && (lawful <= -30000)) {// 小於-30000掉落1~8件
						count = _random.nextInt(8) + 1;

					} else if ((lawful > -30000) && (lawful <= -20000)) {// 小於-20000掉落1~6件
						count = _random.nextInt(6) + 1;

					} else if ((lawful > -20000) && (lawful <= -10000)) {// 小於-10000掉落1~4件
						count = _random.nextInt(4) + 1;

					} else if ((lawful > -10000) && (lawful <= -0)) {// 小於0掉落2件
						count = _random.nextInt(2) + 1;
					}

					if (count > 0) {
						caoPenaltyResult(count);
					}
				}
			}
		}

		/**
		 * <FONT COLOR="#0000ff">死亡技能遺失判斷</FONT>
		 */
		private void lostSkillRate() {
			// 月卡物品保護
			if (_vip_4) {
				sendPackets(new S_ServerMessage("\\aD你已經啟動技能保護！"));
				return;
			}
			// 人物擁有技能數量
			final int skillCount = _skillList.size();

			// 技能數量大於0
			if (skillCount > 0) {
				// 預計掉落技能數量
				int count = 0;
				// 人物正義質
				final int lawful = getLawful();

				// 引用隨機質 0 ~ 199
				final int random = _random.nextInt(200);

				if (lawful <= -32768) {
					count = _random.nextInt(8) + 1;// 隨機質 小於 技能數量

				} else if ((lawful > -32768) && (lawful <= -30000)) {
					if (random <= (skillCount + 1)) {
						count = _random.nextInt(5) + 1;// 隨機質 小於 技能數量
					}

				} else if ((lawful > -30000) && (lawful <= -20000)) {
					if (random <= ((skillCount >> 1) + 1)) {// 隨機質 小於 (技能數量 / 2)
						count = _random.nextInt(3) + 1;
					}

				} else if ((lawful > -20000) && (lawful <= -10000)) {
					if (random <= ((skillCount >> 2) + 1)) {// 隨機質 小於 (技能數量 / 4)
						count = 1;
					}
				}

				if (count > 0) {
					delSkill(count);
				}
			}
		}
	}

	/**
	 * <FONT COLOR="#0000ff">死亡掉落物品</FONT>
	 * 
	 * @param count
	 *            掉落數量
	 */
	private void caoPenaltyResult(final int count) {
		for (int i = 0; i < count; i++) {
			final L1ItemInstance item = getInventory().caoPenalty();
			if (item != null) {
				if (item.getBless() >= 128) { // 封印装備
					_log.warn("封印装備 死亡噴出遺失:" + item.getId() + "/" + item.getItem().getName());
					getInventory().deleteItem(item);

				} else {
					_log.warn("死亡噴出物品:" + item.getId() + "/" + item.getItem().getName());
					WriteLogTxt.Recording("死亡噴出遺失", "玩家死亡噴出遺失" + getName() + "#" + +item.getItemId() + "流水號: " + item.getId() + "/" + item.getItem().getName());
					item.set_showId(get_showId());

					final int x = getX();
					final int y = getY();
					final short m = getMapId();
					getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1, // 物件不可堆疊
																								// 數量:1
																								// 可堆疊
																								// 數量:全部
							World.get().getInventory(x, y, m));
				}
				// 您損失了 %0。
				sendPackets(new S_ServerMessage(638, item.getLogName()));
			}
		}
	}

	/**
	 * <FONT COLOR="#0000ff">死亡技能遺失</FONT>
	 * 
	 * @param count
	 *            掉落數量
	 */
	private void delSkill(final int count) {
		for (int i = 0; i < count; i++) {
			// 隨機取得 INDEX 位置點
			final int index = _random.nextInt(_skillList.size());
			// 取回隨機位置點技能編號
			final Integer skillid = _skillList.get(index);

			if (_skillList.remove(skillid)) {
				this.sendPackets(new S_DelSkill(this, skillid));
				CharSkillReading.get().spellLost(getId(), skillid);
			}
		}
	}

	/**
	 * <FONT COLOR="#0000ff">復活移出死亡清單</FONT>
	 */
	public void stopPcDeleteTimer() {
		setDead(false);
		set_delete_time(0);
	}

	/**
	 * <FONT COLOR="#0000ff">是否在參加攻城戰中</FONT>
	 * 
	 * @return true:是 false:不是
	 */
	public boolean castleWarResult() {
		if ((getClanid() != 0) && isCrown()) { // 具有血盟的王族
			final L1Clan clan = WorldClan.get().getClan(getClanname());
			if (clan.getCastleId() == 0) {
				// 取回全部戰爭清單
				for (final L1War war : WorldWar.get().getWarList()) {
					final int warType = war.getWarType();
					final boolean isInWar = war.checkClanInWar(getClanname());
					final boolean isAttackClan = war.checkAttackClan(getClanname());
					if ((getId() == clan.getLeaderId()) && // 攻城戰中 攻擊方盟主死亡
															// 退出戰爭
							(warType == 1) && isInWar && isAttackClan) {
						final String enemyClanName = war.getEnemyClanName(getClanname());
						if (enemyClanName != null) {
							war.ceaseWar(getClanname(), enemyClanName); // 結束
						}
						break;
					}
				}
			}
		}

		int castleId = 0;
		boolean isNowWar = false;
		castleId = L1CastleLocation.getCastleIdByArea(this);
		if (castleId != 0) { // 戰爭範圍旗幟內城堡ID
			isNowWar = ServerWarExecutor.get().isNowWar(castleId);
		}
		return isNowWar;
	}

	/**
	 * <FONT COLOR="#0000ff">是否在參加血盟戰爭中</FONT>
	 * 
	 * @param lastAttacker
	 * @return true:是 false:不是
	 */
	public boolean simWarResult(final L1Character lastAttacker) {
		if (getClanid() == 0) { // 具有血盟
			return false;
		}

		L1PcInstance attacker = null;
		String enemyClanName = null;
		boolean sameWar = false;

		// 判斷主要攻擊者
		if (lastAttacker instanceof L1PcInstance) {// 攻擊者是玩家
			attacker = (L1PcInstance) lastAttacker;

		} else if (lastAttacker instanceof L1PetInstance) {// 攻擊者是寵物
			attacker = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();

		} else if (lastAttacker instanceof L1SummonInstance) {// 攻擊者是 召換獸
			attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();

		} else if (lastAttacker instanceof L1IllusoryInstance) {// 攻擊者是 分身
			attacker = (L1PcInstance) ((L1IllusoryInstance) lastAttacker).getMaster();

		} else if (lastAttacker instanceof L1EffectInstance) {// 攻擊者是 技能物件(火牢)
			attacker = (L1PcInstance) ((L1EffectInstance) lastAttacker).getMaster();

		} else {
			return false;
		}

		final L1Clan clan = WorldClan.get().getClan(getClanname());
		// 取回全部戰爭清單
		for (final L1War war : WorldWar.get().getWarList()) {
			final int warType = war.getWarType();
			if (warType == 1) {// 攻城戰
				continue;
			}
			final boolean isInWar = war.checkClanInWar(getClanname());
			if (isInWar) {
				if ((attacker != null) && (attacker.getClanid() != 0)) { // 兇手不為空
																			// 並且具有血盟
					sameWar = war.checkClanInSameWar(getClanname(), attacker.getClanname());
				}

				if (getId() == clan.getLeaderId()) {// 戰爭中盟主
					enemyClanName = war.getEnemyClanName(getClanname());
					if (enemyClanName != null) {
						war.ceaseWar(getClanname(), enemyClanName); // 盟主死亡結束
					}
				}

				if ((warType == 2) && sameWar) {// 戰爭中模擬戰
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 經驗質恢復
	 */
	public void resExp() {
		final int oldLevel = getLevel();
		final long needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		long exp = 0;
		switch (oldLevel) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
		case 35:
		case 36:
		case 37:
		case 38:
		case 39:
		case 40:
		case 41:
		case 42:
		case 43:
		case 44:
			exp = (long) (needExp * 0.05);
			break;

		case 45:
			exp = (long) (needExp * 0.045);
			break;

		case 46:
			exp = (long) (needExp * 0.04);
			break;

		case 47:
			exp = (long) (needExp * 0.035);
			break;

		case 48:
			exp = (long) (needExp * 0.03);
			break;

		case 49:
		case 50:
		case 51:
		case 52:
		case 53:
		case 54:
		case 55:
		case 56:
		case 57:
		case 58:
		case 59:
		case 60:
		case 61:
		case 62:
		case 63:
		case 64:
		case 65:
		case 66:
		case 67:
		case 68:
		case 69:
		case 70:
		case 71:
		case 72:
		case 73:
		case 74:
		case 75:
		case 76:
		case 77:
		case 78:
		case 79:
		case 80:
		case 81:
		case 82:
		case 83:
		case 84:
		case 85:
		case 86:
		case 87:
		case 88:
		case 89:
			exp = (long) (needExp * 0.025);
			break;

		default:
			exp = (long) (needExp * 0.025);// 2012-08-08 0.025
			break;
		}

		if (exp == 0) {
			return;
		}
		addExp(exp);
	}

	/**
	 * 經驗質逞罰
	 * 
	 * @return
	 */
	private long deathPenalty() {
		final int oldLevel = getLevel();
		final long needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		long exp = 0;
		switch (oldLevel) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			exp = 0;
			break;

		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
		case 35:
		case 36:
		case 37:
		case 38:
		case 39:
		case 40:
		case 41:
		case 42:
		case 43:
		case 44:
			exp = (long) (needExp * 0.1);
			break;

		case 45:
			exp = (long) (needExp * 0.09);
			break;

		case 46:
			exp = (long) (needExp * 0.08);
			break;

		case 47:
			exp = (long) (needExp * 0.07);
			break;

		case 48:
			exp = (long) (needExp * 0.06);
			break;

		case 49:
			exp = (long) (needExp * 0.05);
			break;

		default:
			exp = (long) (needExp * 0.05);
			break;
		}

		if (exp == 0) {
			return 0;
		}
		addExp(-exp);
		return exp;
	}

	private int _originalEr = 0; // ● オリジナルDEX ER補正

	public int getOriginalEr() {

		return _originalEr;
	}

	public int getEr() {
		if (hasSkillEffect(STRIKER_GALE)) {
			return 0;
		}

		int er = 0;
		if (isKnight() || isWarrior()) {
			er = getLevel() >> 2;// /4 // ナイト

		} else if (isCrown()) {
			// 王族的基本能力設定變更。
			// 每6級，ER+1 (repaired by terry0412)
			er = getLevel() / 6; // 君主

		} else if (isElf()) {
			er = getLevel() >> 3; // エルフ

		} else if (isDarkelf()) {
			er = getLevel() / 6; // ダークエルフ

		} else if (isWizard()) {
			er = getLevel() / 10; // ウィザード

		} else if (isDragonKnight()) {
			er = getLevel() / 7; // ドラゴンナイト

		} else if (isIllusionist()) {
			er = getLevel() / 9; // イリュージョニスト
		}

		er += (getDex() - 8) >> 1;// / 2;

		er += getOriginalEr();

		if (hasSkillEffect(DRESS_EVASION)) {// 閃避提升
			er += 12;
		}

		if (hasSkillEffect(SOLID_CARRIAGE)) {// 堅固防護
			er += 15;
		}

		if (hasSkillEffect(ADLV80_1)) {// 卡瑞的祝福(地龍副本)
			er += 30;
		}

		if (hasSkillEffect(ADLV80_2)) {// 莎爾的祝福(水龍副本)
			er += 15;
		}
		return er;
	}

	/**
	 * 使用的武器
	 * 
	 * @return
	 */
	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	/**
	 * 使用的武器
	 * 
	 * @param weapon
	 */
	public void setWeapon(final L1ItemInstance weapon) {
		_weapon = weapon;
	}

	/**
	 * 傳回任務狀態類
	 * 
	 * @return
	 */
	public L1PcQuest getQuest() {
		return _quest;
	}

	/**
	 * 傳回選單命令執行類
	 * 
	 * @return
	 */
	public L1ActionPc getAction() {
		return _action;
	}

	/**
	 * 傳回寵物選單命令執行類
	 * 
	 * @return
	 */
	public L1ActionPet getActionPet() {
		return _actionPet;
	}

	/**
	 * 傳回召喚獸選單命令執行類
	 * 
	 * @return
	 */
	public L1ActionSummon getActionSummon() {
		return _actionSummon;
	}

	/**
	 * 王族
	 * 
	 * @return
	 */
	public boolean isCrown() {
		return ((getClassId() == CLASSID_PRINCE) || (getClassId() == CLASSID_PRINCESS));
	}

	/**
	 * 騎士
	 * 
	 * @return
	 */
	public boolean isKnight() {
		return ((getClassId() == CLASSID_KNIGHT_MALE) || (getClassId() == CLASSID_KNIGHT_FEMALE));
	}

	/**
	 * 精靈
	 * 
	 * @return
	 */
	public boolean isElf() {
		return ((getClassId() == CLASSID_ELF_MALE) || (getClassId() == CLASSID_ELF_FEMALE));
	}

	/**
	 * 法師
	 * 
	 * @return
	 */
	public boolean isWizard() {
		return ((getClassId() == CLASSID_WIZARD_MALE) || (getClassId() == CLASSID_WIZARD_FEMALE));
	}

	/**
	 * 黑暗精靈
	 * 
	 * @return
	 */
	public boolean isDarkelf() {
		return ((getClassId() == CLASSID_DARK_ELF_MALE) || (getClassId() == CLASSID_DARK_ELF_FEMALE));
	}

	/**
	 * 龍騎士
	 * 
	 * @return
	 */
	public boolean isDragonKnight() {
		return ((getClassId() == CLASSID_DRAGON_KNIGHT_MALE) || (getClassId() == CLASSID_DRAGON_KNIGHT_FEMALE));
	}

	/**
	 * 幻術師
	 * 
	 * @return
	 */
	public boolean isIllusionist() {
		return ((getClassId() == CLASSID_ILLUSIONIST_MALE) || (getClassId() == CLASSID_ILLUSIONIST_FEMALE));
	}

	/**
	 * 戰士
	 * 
	 * @return
	 */
	public boolean isWarrior() {
		return (getClassId() == CLASSID_WARRIOR_MALE) || (getClassId() == CLASSID_WARRIOR_FEMALE);
	}

	private ClientExecutor _netConnection = null;
	private int _classId;
	private int _type;
	private long _exp;
	private final L1Karma _karma = new L1Karma();
	private boolean _gm;
	private boolean _monitor;
	private boolean _gmInvis;
	private short _accessLevel;
	private int _currentWeapon;
	private final L1PcInventory _inventory;
	private final L1DwarfInventory _dwarf;
	private final L1DwarfForElfInventory _dwarfForElf;
	private L1ItemInstance _weapon;
	private L1Party _party;
	private L1ChatParty _chatParty;
	private int _partyID;
	private int _tradeID;
	private boolean _tradeOk;
	private int _tempID;
	private boolean _isTeleport = false;
	private boolean _isDrink = false;
	private boolean _isGres = false;
	private boolean _isPinkName = false;
	private final L1PcQuest _quest;
	private final L1ActionPc _action;
	private final L1ActionPet _actionPet;
	private final L1ActionSummon _actionSummon;
	// public short _temp;
	private final LotteryWarehouseTable _lottery = LotteryWarehouseTable.get();

	public LotteryWarehouseTable getLottery() {
		return _lottery;
	}

	private final L1EquipmentSlot _equipSlot;

	private String _accountName; // ● アカウントネーム

	public String getAccountName() {
		return _accountName;
	}

	public void setAccountName(final String s) {
		_accountName = s;
	}

	private short _baseMaxHp = 0; // ● ＭＡＸＨＰベース（1〜32767）

	/**
	 * 基礎HP
	 * 
	 * @return
	 */
	public short getBaseMaxHp() {
		return _baseMaxHp;
	}

	/**
	 * 基礎HP
	 * 
	 * @param i
	 */
	public void addBaseMaxHp(short i) {
		i += _baseMaxHp;
		if (i >= 32767) {
			i = 32767;

		} else if (i < 1) {
			i = 1;
		}
		addMaxHp(i - _baseMaxHp);
		_baseMaxHp = i;
	}

	private short _baseMaxMp = 0; // ● ＭＡＸＭＰベース（0〜32767）
	public short _baseMaxMpc;

	/**
	 * 基礎MP
	 * 
	 * @return
	 */
	public short getBaseMaxMp() {
		return _baseMaxMp;
	}

	/**
	 * 基礎MP
	 * 
	 * @param i
	 */
	public void addBaseMaxMp(short i) {
		i += _baseMaxMp;
		if (i >= 32767) {
			i = 32767;

		} else if (i < 1) {
			i = 1;
		}
		addMaxMp(i - _baseMaxMp);
		_baseMaxMp = i;
	}

	private int _baseAc = 0; // ● ＡＣベース（-128〜127）

	public int getBaseAc() {
		return _baseAc;
	}

	private int _originalAc = 0; // ● オリジナルDEX ＡＣ補正

	public int getOriginalAc() {
		return _originalAc;
	}

	private int _baseStr = 0; // ● ＳＴＲベース（1〜127）

	/**
	 * 原始力量(內含素質提升/萬能藥)
	 * 
	 * @return
	 */
	public int getBaseStr() {
		return _baseStr;
	}

	/**
	 * 原始力量(內含素質提升/萬能藥)
	 * 
	 * @param i
	 */
	public void addBaseStr(int i) {
		i += _baseStr;
		if (i >= 254) {
			i = 254;

		} else if (i < 1) {
			i = 1;
		}
		addStr((i - _baseStr));
		_baseStr = i;
	}

	private int _baseCon = 0; // ● ＣＯＮベース（1〜127）

	/**
	 * 原始體質(內含素質提升/萬能藥)
	 * 
	 * @return
	 */
	public int getBaseCon() {
		return _baseCon;
	}

	/**
	 * 原始體質(內含素質提升/萬能藥)
	 * 
	 * @param i
	 */
	public void addBaseCon(int i) {
		i += _baseCon;
		if (i >= 254) {
			i = 254;

		} else if (i < 1) {
			i = 1;
		}
		addCon((i - _baseCon));
		_baseCon = i;
	}

	private int _baseDex = 0; // ● ＤＥＸベース（1〜127）

	/**
	 * 原始敏捷(內含素質提升/萬能藥)
	 * 
	 * @return
	 */
	public int getBaseDex() {
		return _baseDex;
	}

	/**
	 * 原始敏捷(內含素質提升/萬能藥)
	 * 
	 * @param i
	 */
	public void addBaseDex(int i) {
		i += _baseDex;
		if (i >= 254) {
			i = 254;

		} else if (i < 1) {
			i = 1;
		}
		addDex((i - _baseDex));
		_baseDex = i;
	}

	private int _baseCha = 0; // ● ＣＨＡベース（1〜127）

	/**
	 * 原始魅力(內含素質提升/萬能藥)
	 * 
	 * @return
	 */
	public int getBaseCha() {
		return _baseCha;
	}

	/**
	 * 原始魅力(內含素質提升/萬能藥)
	 * 
	 * @param i
	 */
	public void addBaseCha(int i) {
		i += _baseCha;
		if (i >= 254) {
			i = 254;

		} else if (i < 1) {
			i = 1;
		}
		addCha((i - _baseCha));
		_baseCha = i;
	}

	private int _baseInt = 0; // ● ＩＮＴベース（1〜127）

	/**
	 * 原始智力(內含素質提升/萬能藥)
	 * 
	 * @return
	 */
	public int getBaseInt() {
		return _baseInt;
	}

	/**
	 * 原始智力(內含素質提升/萬能藥)
	 * 
	 * @param i
	 */
	public void addBaseInt(int i) {
		i += _baseInt;
		if (i >= 254) {
			i = 254;

		} else if (i < 1) {
			i = 1;
		}
		addInt((i - _baseInt));
		_baseInt = i;
	}

	private int _baseWis = 0; // ● ＷＩＳベース（1〜127）

	/**
	 * 原始精神(內含素質提升/萬能藥)
	 * 
	 * @return
	 */
	public int getBaseWis() {
		return _baseWis;
	}

	/**
	 * 原始精神(內含素質提升/萬能藥)
	 * 
	 * @param i
	 */
	public void addBaseWis(int i) {
		i += _baseWis;
		if (i >= 254) {
			i = 254;

		} else if (i < 1) {
			i = 1;
		}
		addWis((i - _baseWis));
		_baseWis = i;
	}

	// //////////////////////////////////////////////////////////////////////////////////////

	private int _originalStr = 0; // ● オリジナル STR

	/**
	 * 原始力量(人物出生)
	 * 
	 * @return
	 */
	public int getOriginalStr() {
		return _originalStr;
	}

	/**
	 * 原始力量(人物出生)
	 * 
	 * @param i
	 */
	public void setOriginalStr(final int i) {
		_originalStr = i;
	}

	private int _originalCon = 0; // ● オリジナル CON

	/**
	 * 原始體質(人物出生)
	 * 
	 * @return
	 */
	public int getOriginalCon() {
		return _originalCon;
	}

	/**
	 * 原始體質(人物出生)
	 * 
	 * @param i
	 */
	public void setOriginalCon(final int i) {
		_originalCon = i;
	}

	private int _originalDex = 0; // ● オリジナル DEX

	/**
	 * 原始敏捷(人物出生)
	 * 
	 * @return
	 */
	public int getOriginalDex() {
		return _originalDex;
	}

	/**
	 * 原始敏捷(人物出生)
	 * 
	 * @param i
	 */
	public void setOriginalDex(final int i) {
		_originalDex = i;
	}

	private int _originalCha = 0; // ● オリジナル CHA

	/**
	 * 原始魅力(人物出生)
	 * 
	 * @return
	 */
	public int getOriginalCha() {
		return _originalCha;
	}

	/**
	 * 原始魅力(人物出生)
	 * 
	 * @param i
	 */
	public void setOriginalCha(final int i) {
		_originalCha = i;
	}

	private int _originalInt = 0; // ● オリジナル INT

	/**
	 * 原始智力(人物出生)
	 * 
	 * @return
	 */
	public int getOriginalInt() {
		return _originalInt;
	}

	/**
	 * 原始智力(人物出生)
	 * 
	 * @param i
	 */
	public void setOriginalInt(final int i) {
		_originalInt = i;
	}

	private int _originalWis = 0; // ● オリジナル WIS

	/**
	 * 原始精神(人物出生)
	 * 
	 * @return
	 */
	public int getOriginalWis() {
		return _originalWis;
	}

	/**
	 * 原始精神(人物出生)
	 * 
	 * @param i
	 */
	public void setOriginalWis(final int i) {
		_originalWis = i;
	}

	private int _originalDmgup = 0; // ● オリジナルSTR ダメージ補正

	public int getOriginalDmgup() {
		return _originalDmgup;
	}

	private int _originalBowDmgup = 0; // ● オリジナルDEX 弓ダメージ補正

	public int getOriginalBowDmgup() {
		return _originalBowDmgup;
	}

	private int _originalHitup = 0; // ● オリジナルSTR 命中補正

	public int getOriginalHitup() {
		return _originalHitup;
	}

	private int _originalBowHitup = 0; // ● オリジナルDEX 命中補正

	public int getOriginalBowHitup() {
		return _originalHitup + _originalBowHitup;
	}

	private int _originalMr = 0; // ● オリジナルWIS 魔法防御

	public int getOriginalMr() {
		return _originalMr;
	}

	private int _originalMagicHit = 0; // ● オリジナルINT 魔法命中

	/**
	 * 智力(依職業)附加魔法命中
	 * 
	 * @return
	 */
	public int getOriginalMagicHit() {
		return _originalMagicHit;
	}

	private int _originalMagicCritical = 0; // ● オリジナルINT 魔法クリティカル

	public int getOriginalMagicCritical() {
		return _originalMagicCritical;
	}

	private int _originalMagicConsumeReduction = 0; // ● オリジナルINT 消費MP軽減

	public int getOriginalMagicConsumeReduction() {
		return _originalMagicConsumeReduction;
	}

	private int _originalMagicDamage = 0; // ● オリジナルINT 魔法ダメージ

	/**
	 * 魔攻
	 * 
	 * @return
	 */
	public int getOriginalMagicDamage() {
		return _originalMagicDamage;
	}

	private int _originalHpup = 0; // ● オリジナルCON HP上昇値補正

	/**
	 * 體質 HP上昇値補正
	 * 
	 * @return
	 */
	public int getOriginalHpup() {
		return _originalHpup;
	}

	private int _originalMpup = 0; // ● オリジナルWIS MP上昇値補正

	/**
	 * 精神 MP上昇値補正
	 * 
	 * @return
	 */
	public int getOriginalMpup() {
		return _originalMpup;
	}

	private int _baseDmgup = 0; // ● ダメージ補正ベース（-128〜127）

	public int getBaseDmgup() {
		return _baseDmgup;
	}

	private int _baseBowDmgup = 0; // ● 弓ダメージ補正ベース（-128〜127）

	public int getBaseBowDmgup() {
		return _baseBowDmgup;
	}

	private int _baseHitup = 0; // ● 命中補正ベース（-128〜127）

	/**
	 * 命中補正
	 * 
	 * @return
	 */
	public int getBaseHitup() {
		return _baseHitup;
	}

	private int _baseBowHitup = 0; // ● 弓命中補正ベース（-128〜127）

	/**
	 * 弓命中補正
	 * 
	 * @return
	 */
	public int getBaseBowHitup() {
		return _baseBowHitup;
	}

	private int _baseMr = 0; // ● 魔法防御ベース（0〜）

	/**
	 * 魔法防御
	 * 
	 * @return
	 */
	public int getBaseMr() {
		return _baseMr;
	}

	private int _advenHp; // 暫時增加的HP

	/**
	 * 暫時增加的HP
	 * 
	 * @return
	 */
	public int getAdvenHp() {
		return _advenHp;
	}

	/**
	 * 暫時增加的HP
	 * 
	 * @param i
	 */
	public void setAdvenHp(final int i) {
		_advenHp = i;
	}

	private int _advenMp; // 暫時增加的MP

	/**
	 * 暫時增加的MP
	 * 
	 * @return
	 */
	public int getAdvenMp() {
		return _advenMp;
	}

	/**
	 * 暫時增加的MP
	 * 
	 * @param i
	 */
	public void setAdvenMp(final int i) {
		_advenMp = i;
	}

	private int _highLevel; // ● 過去最高レベル

	public int getHighLevel() {
		return _highLevel;
	}

	public void setHighLevel(final int i) {
		_highLevel = i;
	}

	private int _bonusStats; // 升級點數使用次數

	/**
	 * 升級點數使用次數
	 * 
	 * @return
	 */
	public int getBonusStats() {
		return _bonusStats;
	}

	/**
	 * 設置升級點數使用次數
	 * 
	 * @param i
	 */
	public void setBonusStats(final int i) {
		_bonusStats = i;
	}

	private int _elixirStats; // 萬能藥使用次數

	/**
	 * 萬能藥使用次數
	 * 
	 * @return
	 */
	public int getElixirStats() {
		return _elixirStats;
	}

	/**
	 * 設置萬能藥使用次數
	 * 
	 * @param i
	 */
	public void setElixirStats(final int i) {
		_elixirStats = i;
	}

	private int _elfAttr; // ● エルフの属性

	/**
	 * 精靈屬性
	 * 
	 * @return
	 */
	public int getElfAttr() {
		return _elfAttr;
	}

	public void setElfAttr(final int i) {
		_elfAttr = i;
	}

	private int _expRes; // ● EXP復旧

	public int getExpRes() {
		return _expRes;
	}

	public void setExpRes(final int i) {
		_expRes = i;
	}

	private int _partnerId; // ● 結婚相手

	public int getPartnerId() {
		return _partnerId;
	}

	public void setPartnerId(final int i) {
		_partnerId = i;
	}

	private int _onlineStatus; // 人物連線狀態

	/**
	 * 人物連線狀態
	 * 
	 * @return
	 */
	public int getOnlineStatus() {
		return _onlineStatus;
	}

	/**
	 * 設置人物連線狀態
	 * 
	 * @param i
	 */
	public void setOnlineStatus(final int i) {
		_onlineStatus = i;
	}

	private int _homeTownId; // ● ホームタウン

	public int getHomeTownId() {
		return _homeTownId;
	}

	public void setHomeTownId(final int i) {
		_homeTownId = i;
	}

	private int _contribution; // 貢獻度

	/**
	 * 貢獻度
	 * 
	 * @return
	 */
	public int getContribution() {
		return _contribution;
	}

	/**
	 * 貢獻度
	 * 
	 * @param i
	 */
	public void setContribution(final int i) {
		_contribution = i;
	}

	private int _hellTime;// 地獄滯留時間

	/**
	 * 地獄滯留時間
	 * 
	 * @return
	 */
	public int getHellTime() {
		return _hellTime;
	}

	/**
	 * 地獄滯留時間
	 * 
	 * @param i
	 */
	public void setHellTime(final int i) {
		_hellTime = i;
	}

	private boolean _banned; // ● 凍結

	public boolean isBanned() {
		return _banned;
	}

	public void setBanned(final boolean flag) {
		_banned = flag;
	}

	private int _food; // ● 満腹度

	public int get_food() {
		return _food;
	}

	public void set_food(int i) {
		if (i > 225) {
			i = 225;
		}
		_food = i;
		if (_food == 225) {// LOLI 生存吶喊
			final Calendar cal = Calendar.getInstance();
			final long h_time = cal.getTimeInMillis() / 1000;// 換算為秒
			set_h_time(h_time);// 紀錄吃飽時間

		} else {
			set_h_time(-1);// 紀錄吃飽時間
		}
	}

	public L1EquipmentSlot getEquipSlot() {
		return _equipSlot;
	}

	/**
	 * 加載指定PC資料
	 * 
	 * @param charName
	 *            PC名稱
	 * @return
	 */
	public static L1PcInstance load(final String charName) {
		L1PcInstance result = null;
		try {
			result = CharacterTable.get().loadCharacter(charName);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * 人物資料存檔
	 * 
	 * @throws Exception
	 */
	public void save() throws Exception {
		if (isGhost()) {
			return;
		}

		if (isInCharReset()) {
			return;
		}

		// 其它事件紀錄
		if (_other != null) {
			CharOtherReading.get().storeOther(getId(), _other);
		}

		CharacterTable.get().storeCharacter(this);
	}

	/**
	 * 背包資料存檔
	 */
	public void saveInventory() {
		for (final L1ItemInstance item : getInventory().getItems()) {
			getInventory().saveItem(item, item.getRecordingColumns());
		}
	}

	public double getMaxWeight() {
		final int str = getStr();
		final int con = getCon();
		// double maxWeight = (150 * (Math.floor((0.6 * str) + (0.4 * con) +
		// 1)))
		// * get_weightUP();
		// XXX 7.6 公式更新
		// 本身負重能力
		double maxWeight = L1ClassFeature.calcAbilityMaxWeight(this.getStr(), this.getCon());

		double weightReductionByArmor = getWeightReduction(); // 減重設置
		weightReductionByArmor /= 100;

		int weightReductionByMagic = 0;
		if (hasSkillEffect(DECREASE_WEIGHT) || hasSkillEffect(REDUCE_WEIGHT)) {
			weightReductionByMagic = 180;
		}

		// XXX 7.6取消計算初始能力負重減免
		// double originalWeightReduction = 0; // オリジナルステータスによる重量軽減
		// originalWeightReduction += 0.04 * (getOriginalStrWeightReduction() +
		// getOriginalConWeightReduction());

		final double weightReduction = 1 + weightReductionByArmor
		/* + originalWeightReduction */;

		maxWeight *= weightReduction;

		maxWeight += weightReductionByMagic;

		maxWeight *= ConfigRate.RATE_WEIGHT_LIMIT; // 服務器提高設置

		return maxWeight;
	}

	/**
	 * 神聖疾走效果 行走加速效果 風之疾走效果 生命之樹果實效果
	 * 
	 * @return
	 */
	public boolean isFastMovable() {
		return (hasSkillEffect(HOLY_WALK) || hasSkillEffect(MOVING_ACCELERATION) || hasSkillEffect(WIND_WALK) || hasSkillEffect(STATUS_RIBRAVE));
	}

	/**
	 * 血之渴望效果
	 * 
	 * @return
	 */
	public boolean isFastAttackable() {
		return hasSkillEffect(BLOODLUST);
	}

	/**
	 * 勇敢藥水效果
	 * 
	 * @return
	 */
	public boolean isBrave() {
		return hasSkillEffect(STATUS_BRAVE);
	}

	/**
	 * 精靈餅乾效果
	 * 
	 * @return
	 */
	public boolean isElfBrave() {
		return hasSkillEffect(STATUS_ELFBRAVE);
	}

	/**
	 * 巧克力蛋糕效果
	 * 
	 * @return
	 */
	public boolean isBraveX() {
		return hasSkillEffect(STATUS_BRAVE3);
	}

	/**
	 * 加速效果
	 * 
	 * @return
	 */
	public boolean isHaste() {
		return (hasSkillEffect(STATUS_HASTE) || hasSkillEffect(HASTE) || hasSkillEffect(GREATER_HASTE) || (getMoveSpeed() == 1));
	}

	private int invisDelayCounter = 0;

	public boolean isInvisDelay() {
		return (invisDelayCounter > 0);
	}

	private final Object _invisTimerMonitor = new Object();

	public void addInvisDelayCounter(final int counter) {
		synchronized (_invisTimerMonitor) {
			invisDelayCounter += counter;
		}
	}

	private static final long DELAY_INVIS = 3000L;

	/**
	 * 啟用隱身時間軸設置
	 */
	public void beginInvisTimer() {
		addInvisDelayCounter(1);
		GeneralThreadPool.get().pcSchedule(new L1PcInvisDelay(getId()), DELAY_INVIS);
	}

	public synchronized void addLawful(final int i) {
		int lawful = getLawful() + i;
		if (lawful > 32767) {
			lawful = 32767;

		} else if (lawful < -32768) {
			lawful = -32768;
		}
		setLawful(lawful);
		onChangeLawful();
	}

	/**
	 * 更新經驗質
	 * 
	 * @param exp
	 */
	public synchronized void addExp(final long exp) {
		_exp += exp;

		if (_exp > ExpTable.MAX_EXP) {
			_exp = ExpTable.MAX_EXP;
		}
		onChangeExp();
	}

	/**
	 * 增加貢獻度
	 * 
	 * @param contribution
	 */
	public synchronized void addContribution(final int contribution) {
		_contribution += contribution;
	}

	/**
	 * 等級提升的判斷
	 * 
	 * @param gap
	 */
	private void levelUp(final int gap) {
		resetLevel();
		for (int i = 0; i < gap; i++) {
			final short randomHp = CalcStat.calcStatHp(getType(), getBaseMaxHp(), getBaseCon(), getOriginalHpup());
			final short randomMp = CalcStat.calcStatMp(getType(), getBaseMaxMp(), getBaseWis(), getOriginalMpup());
			addBaseMaxHp(randomHp);
			addBaseMaxMp(randomMp);
		}
		if (ConfigAlt.METE_GIVE_POTION && (getLevel() >= ConfigAlt.METE_LEVEL) && (getHighLevel() < ConfigAlt.METE_LEVEL)) {
			try {
				final L1Item l1item = ItemTable.get().getTemplate(43000);
				if ((l1item != null) && (getInventory().checkAddItem(l1item, 1) == L1Inventory.OK)) {
					getInventory().storeItem(43000, 1);
					sendPackets(new S_ServerMessage(403, l1item.getName()));
				} else {
					sendPackets(new S_SystemMessage("無法獲得轉生藥水。可能此道具不存在！"));
				}
			} catch (final Exception e) {
				sendPackets(new S_SystemMessage("無法獲得轉生藥水。可能此道具不存在！"));
			}
		}

		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();
		if (getLevel() > getHighLevel()) {
			setHighLevel(getLevel());
		}

		setCurrentHp(getMaxHp());
		setCurrentMp(getMaxMp());

		try {
			// 人物資料存檔
			save();

			// 等級獎勵系統
			if (RewardSet.RewardSTART) {
				Reward.getItem(this);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			// 更新人物資訊
			sendPackets(new S_OwnCharStatus(this));

			// 地圖等級限制判斷
			MapLevelTable.get().get_level(getMapId(), this);
			showWindows();

			if (ConfigAlt.APPRENTICE_SWITCH) {
				if ((getApprentice() != null) && (getApprentice().getMaster().getId() != getId())) {
					if (getLevel() >= ConfigAlt.APPRENTICE_LEVEL) {
						for (final L1PcInstance l1char : getApprentice().getTotalList()) {
							if (l1char.getId() == getId()) {
								getApprentice().getTotalList().remove(l1char);
								break;
							}
						}
						CharApprenticeTable.getInstance().updateApprentice(getId(), getApprentice().getTotalList());
						setApprentice(null);

						if (!getInventory().checkItem(ConfigAlt.APPRENTICE_ITEM_ID)) {
							CreateNewItem.createNewItem(this, ConfigAlt.APPRENTICE_ITEM_ID, 1);
						}
					}
				}
			}
			if (getLevel() >= 51 && (getLevel() - 50 > getBonusStats()) || (getLevel() >= 51 && (getLevel() - 50 > getBonusStats() - 49))) {
				if ((getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt() + getBaseWis() + getBaseCha()) < (ConfigAlt.POWER * 6)) {
					// sendPackets(new S_bonusstats(getId(), 1));
					int bonus = (getLevel() - 50) - getBonusStats();// 可以點的點數
																	// XXX 7.6C
																	// ADD
					sendPackets(new S_Message_YN(479, bonus));
				}
			}
			// XXX 能力基本資訊-力量
			this.sendPackets(new S_StrDetails(2, L1ClassFeature.calcStrDmg(this.getStr(), this.getBaseStr()), L1ClassFeature.calcStrHit(this.getStr(), this.getBaseStr()), L1ClassFeature
					.calcStrDmgCritical(this.getStr(), this.getBaseStr()), L1ClassFeature.calcAbilityMaxWeight(this.getStr(), this.getCon())));

			// XXX 重量程度資訊
			this.sendPackets(new S_WeightStatus(this.getInventory().getWeight100(), this.getInventory().getWeight(), (int) this.getMaxWeight()));

			// XXX 能力基本資訊-智力
			this.sendPackets(new S_IntDetails(2, L1ClassFeature.calcIntMagicDmg(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicHit(this.getInt(), this.getBaseInt()),
					L1ClassFeature.calcIntMagicCritical(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicBonus(this.getType(), this.getInt()), L1ClassFeature
							.calcIntMagicConsumeReduction(this.getInt())));

			// XXX 能力基本資訊-精神
			this.sendPackets(new S_WisDetails(2, L1ClassFeature.calcWisMpr(this.getWis(), this.getBaseWis()), L1ClassFeature.calcWisPotionMpr(this.getWis(), this.getBaseWis()),
					L1ClassFeature.calcStatMr(this.getWis()) + L1ClassFeature.newClassFeature(this.getType()).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(this.getType(),
							this.getBaseWis())));

			// XXX 能力基本資訊-敏捷
			this.sendPackets(new S_DexDetails(2, L1ClassFeature.calcDexDmg(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexHit(this.getDex(), this.getBaseDex()), L1ClassFeature
					.calcDexDmgCritical(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexAc(this.getDex()), L1ClassFeature.calcDexEr(this.getDex())));

			// XXX 能力基本資訊-體質
			this.sendPackets(new S_ConDetails(2, L1ClassFeature.calcConHpr(this.getCon(), this.getBaseCon()), L1ClassFeature.calcConPotionHpr(this.getCon(), this.getBaseCon()),
					L1ClassFeature.calcAbilityMaxWeight(this.getStr(), this.getCon()), L1ClassFeature.calcBaseClassLevUpHpUp(this.getType())
							+ L1ClassFeature.calcBaseConLevUpExtraHpUp(this.getType(), this.getBaseCon())));

			// XXX 重量程度資訊
			this.sendPackets(new S_WeightStatus(this.getInventory().getWeight100(), this.getInventory().getWeight(), (int) this.getMaxWeight()));
		}
	}

	/**
	 * 判斷是否展示視窗<BR>
	 * 能力質/任務
	 */
	public void showWindows() {
		if (power()) {
			this.sendPackets(new S_Bonusstats(getId()));
		}
	}

	/**
	 * 展示任務室窗
	 */
	public void isWindows() {
		// 判斷是否出現能力選取視窗
		if (power()) {// 是
			this.sendPackets(new S_NPCTalkReturn(getId(), "y_qs_10"));

		} else {// 不是
			this.sendPackets(new S_NPCTalkReturn(getId(), "y_qs_00"));
		}
	}

	/**
	 * 判斷是否出現能力選取視窗
	 * 
	 * @return
	 */
	public boolean power() {
		if (getLevel() >= 51) {
			if ((getLevel() - 50) > getBonusStats()) {
				final int power = getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt() + getBaseWis() + getBaseCha();
				if (power < (ConfigAlt.POWER * 6)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 等級下降
	 * 
	 * @param gap
	 */
	private void levelDown(final int gap) {
		resetLevel();

		for (int i = 0; i > gap; i--) {
			// レベルダウン時はランダム値をそのままマイナスする為に、base値に0を設定
			final short randomHp = CalcStat.calcStatHp(getType(), 0, getBaseCon(), getOriginalHpup());
			final short randomMp = CalcStat.calcStatMp(getType(), 0, getBaseWis(), getOriginalMpup());
			addBaseMaxHp((short) -randomHp);
			addBaseMaxMp((short) -randomMp);
		}

		if (getLevel() == 1) {
			final int initHp = CalcInitHpMp.calcInitHp(this);
			final int initMp = CalcInitHpMp.calcInitMp(this);
			addBaseMaxHp((short) -getBaseMaxHp());
			addBaseMaxHp((short) initHp);
			setCurrentHp((short) initHp);
			addBaseMaxMp((short) -getBaseMaxMp());
			addBaseMaxMp((short) initMp);
			setCurrentMp((short) initMp);
		}

		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();

		try {
			// 存入資料
			save();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			// 更新人物資訊
			sendPackets(new S_OwnCharStatus(this));

			// 地圖等級限制判斷
			MapLevelTable.get().get_level(getMapId(), this);

			if (ConfigAlt.APPRENTICE_SWITCH) {
				if ((getApprentice() != null) && (getApprentice().getMaster().getId() == getId())) {
					if (getLevel() < ConfigAlt.APPRENTICE_LEVEL) {
						final L1Apprentice apprentice = CharApprenticeTable.getInstance().getApprentice(this);
						if (apprentice != null) {
							CharApprenticeTable.getInstance().deleteApprentice(getId());
							setApprentice(null);
						}
					}
				}
			}
		}
	}

	private boolean _ghost = false; // 鬼魂狀態

	/**
	 * 鬼魂狀態
	 * 
	 * @return
	 */
	public boolean isGhost() {
		return _ghost;
	}

	/**
	 * 設置鬼魂狀態
	 * 
	 * @param flag
	 */
	private void setGhost(final boolean flag) {
		_ghost = flag;
	}

	private int _ghostTime = -1; // 鬼魂狀態時間

	/**
	 * 鬼魂狀態時間
	 * 
	 * @return
	 */
	public int get_ghostTime() {
		return _ghostTime;
	}

	/**
	 * 設置鬼魂狀態時間
	 * 
	 * @param ghostTime
	 */
	public void set_ghostTime(final int ghostTime) {
		_ghostTime = ghostTime;
	}

	private boolean _ghostCanTalk = true; // 鬼魂狀態NPC對話允許

	/**
	 * 鬼魂狀態NPC對話允許
	 * 
	 * @return
	 */
	public boolean isGhostCanTalk() {
		return _ghostCanTalk;
	}

	/**
	 * 設置鬼魂狀態NPC對話允許
	 * 
	 * @param flag
	 */
	private void setGhostCanTalk(final boolean flag) {
		_ghostCanTalk = flag;
	}

	private boolean _isReserveGhost = false; // 準備鬼魂狀態解除

	/**
	 * 準備鬼魂狀態解除
	 * 
	 * @return
	 */
	public boolean isReserveGhost() {
		return _isReserveGhost;
	}

	/**
	 * 準備鬼魂狀態解除
	 * 
	 * @param flag
	 */
	private void setReserveGhost(final boolean flag) {
		_isReserveGhost = flag;
	}

	/**
	 * 鬼魂模式傳送
	 * 
	 * @param locx
	 * @param locy
	 * @param mapid
	 * @param canTalk
	 */
	public void beginGhost(final int locx, final int locy, final short mapid, final boolean canTalk) {
		this.beginGhost(locx, locy, mapid, canTalk, 0);
	}

	/**
	 * 鬼魂模式傳送
	 * 
	 * @param locx
	 * @param locy
	 * @param mapid
	 * @param canTalk
	 * @param sec
	 */
	public void beginGhost(final int locx, final int locy, final short mapid, final boolean canTalk, final int sec) {
		if (isGhost()) {
			return;
		}
		setGhost(true);
		_ghostSaveLocX = getX();
		_ghostSaveLocY = getY();
		_ghostSaveMapId = getMapId();
		_ghostSaveHeading = getHeading();
		setGhostCanTalk(canTalk);
		L1Teleport.teleport(this, locx, locy, mapid, 5, true);
		if (sec > 0) {
			set_ghostTime(sec);
		}
	}

	/**
	 * 離開鬼魂模式(傳送回出發點)
	 */
	public void makeReadyEndGhost() {
		setReserveGhost(true);
		L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY, _ghostSaveMapId, _ghostSaveHeading, true);
	}

	/**
	 * 結束鬼魂模式
	 */
	public void endGhost() {
		set_ghostTime(-1);
		setGhost(false);
		setGhostCanTalk(true);
		setReserveGhost(false);
	}

	private int _ghostSaveLocX = 0;
	private int _ghostSaveLocY = 0;
	private short _ghostSaveMapId = 0;
	private int _ghostSaveHeading = 0;

	/**
	 * 地獄以外に居るときは地獄へ強制移動
	 * 
	 * @param isFirst
	 */
	public void beginHell(final boolean isFirst) {
		// 地獄以外に居るときは地獄へ強制移動
		if (getMapId() != 666) {
			final int locx = 32701;
			final int locy = 32777;
			final short mapid = 666;
			L1Teleport.teleport(this, locx, locy, mapid, 5, false);
		}

		if (isFirst) {
			if (get_PKcount() <= 10) {
				setHellTime(300);

			} else {
				setHellTime((300 * (get_PKcount() - 10)) + 300);
			}
			// 552 因為你已經殺了 %0 人所以被打入地獄。 你將在這裡停留 %1 分鐘。
			this.sendPackets(new S_BlueMessage(552, String.valueOf(get_PKcount()), String.valueOf(getHellTime() / 60)));

		} else {
			// 637 你必須在此地停留 %0 秒。
			this.sendPackets(new S_BlueMessage(637, String.valueOf(getHellTime())));
		}
	}

	/**
	 * 地獄時間終止
	 */
	public void endHell() {
		// 地獄時間終止 返回然柳村
		final int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
		L1Teleport.teleport(this, loc[0], loc[1], (short) loc[2], 5, true);

		try {
			save();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void setPoisonEffect(final int effectId) {
		this.sendPackets(new S_Poison(getId(), effectId));

		if (!isGmInvis() && !isGhost() && !isInvisble()) {
			broadcastPacketAll(new S_Poison(getId(), effectId));
		}
	}

	@Override
	public void healHp(final int pt) {
		super.healHp(pt);

		this.sendPackets(new S_HPUpdate(this));
	}

	@Override
	public int getKarma() {
		return _karma.get();
	}

	@Override
	public void setKarma(final int i) {
		_karma.set(i);
	}

	public void addKarma(final int i) {
		synchronized (_karma) {
			_karma.add(i);
			onChangeKarma();
		}
	}

	public int getKarmaLevel() {
		return _karma.getLevel();
	}

	public int getKarmaPercent() {
		return _karma.getPercent();
	}

	private Timestamp _lastPk;

	/**
	 * プレイヤーの最終PK時間を返す。
	 * 
	 * @return _lastPk
	 */
	public Timestamp getLastPk() {
		return _lastPk;
	}

	/**
	 * プレイヤーの最終PK時間を設定する。
	 * 
	 * @param time
	 *            最終PK時間（Timestamp型） 解除する場合はnullを代入
	 */
	public void setLastPk(final Timestamp time) {
		_lastPk = time;
	}

	/**
	 * プレイヤーの最終PK時間を現在の時刻に設定する。
	 */
	public void setLastPk() {
		_lastPk = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * プレイヤーが手配中であるかを返す。
	 * 
	 * @return 手配中であれば、true
	 */
	public boolean isWanted() {
		if (_lastPk == null) {
			return false;

			// 距離PK時間超過1小時
		} else if ((System.currentTimeMillis() - _lastPk.getTime()) > (1 * 3600 * 1000)) {
			this.setLastPk(null);
			return false;
		}
		return true;
	}

	private Timestamp _lastPkForElf;

	public Timestamp getLastPkForElf() {
		return _lastPkForElf;
	}

	public void setLastPkForElf(final Timestamp time) {
		_lastPkForElf = time;
	}

	public void setLastPkForElf() {
		_lastPkForElf = new Timestamp(System.currentTimeMillis());
	}

	public boolean isWantedForElf() {
		if (_lastPkForElf == null) {
			return false;

		} else if ((System.currentTimeMillis() - _lastPkForElf.getTime()) > (24 * 3600 * 1000)) {
			this.setLastPkForElf(null);
			return false;
		}
		return true;
	}

	private Timestamp _deleteTime; // キャラクター削除までの時間

	public Timestamp getDeleteTime() {
		return _deleteTime;
	}

	public void setDeleteTime(final Timestamp time) {
		_deleteTime = time;
	}

	/**
	 * 職業魔法等級
	 */
	@Override
	public int getMagicLevel() {
		return getClassFeature().getMagicLevel(getLevel());
	}

	private double _weightUP = 1.0D;// 負重提高%

	/**
	 * 負重提高%
	 * 
	 * @return
	 */
	public double get_weightUP() {
		return _weightUP;
	}

	/**
	 * 負重提高%
	 * 
	 * @param i
	 */
	public void add_weightUP(final int i) {
		_weightUP += (i / 100D);
	}

	private int _weightReduction = 0;// 減重

	/**
	 * 減重
	 * 
	 * @return
	 */
	public int getWeightReduction() {
		return _weightReduction;
	}

	/**
	 * 減重
	 * 
	 * @param i
	 */
	public void addWeightReduction(final int i) {
		_weightReduction += i;
	}

	private int _originalStrWeightReduction = 0; // ● オリジナルSTR 重量軽減

	public int getOriginalStrWeightReduction() {
		return _originalStrWeightReduction;
	}

	private int _originalConWeightReduction = 0; // ● オリジナルCON 重量軽減

	public int getOriginalConWeightReduction() {
		return _originalConWeightReduction;
	}

	private int _hasteItemEquipped = 0;// 裝備有加速能力裝備(裝備數量)

	/**
	 * 裝備有加速能力裝備(裝備數量)
	 * 
	 * @return
	 */
	public int getHasteItemEquipped() {
		return _hasteItemEquipped;
	}

	/**
	 * 裝備有加速能力裝備(裝備數量)
	 * 
	 * @param i
	 */
	public void addHasteItemEquipped(final int i) {
		_hasteItemEquipped += i;
	}

	public void removeHasteSkillEffect() {
		if (hasSkillEffect(SLOW)) {
			removeSkillEffect(SLOW);
		}

		if (hasSkillEffect(MASS_SLOW)) {
			removeSkillEffect(MASS_SLOW);
		}

		if (hasSkillEffect(ENTANGLE)) {
			removeSkillEffect(ENTANGLE);
		}

		if (hasSkillEffect(HASTE)) {
			removeSkillEffect(HASTE);
		}

		if (hasSkillEffect(GREATER_HASTE)) {
			removeSkillEffect(GREATER_HASTE);
		}

		if (hasSkillEffect(STATUS_HASTE)) {
			removeSkillEffect(STATUS_HASTE);
		}
	}

	private int _damageReductionByArmor = 0; // 防具增加傷害減免

	public int getDamageReductionByArmor() {
		int damageReduction = 0;
		if (_damageReductionByArmor > 10) {
			damageReduction = 10 + (_random.nextInt((_damageReductionByArmor - 10)) + 1);

		} else {
			damageReduction = _damageReductionByArmor;
		}
		return damageReduction;
	}

	public void addDamageReductionByArmor(final int i) {
		_damageReductionByArmor += i;
	}

	private int _hitModifierByArmor = 0; // 防具增加物理命中

	public int getHitModifierByArmor() {
		return _hitModifierByArmor;
	}

	public void addHitModifierByArmor(final int i) {
		_hitModifierByArmor += i;
	}

	private int _dmgModifierByArmor = 0; // 防具增加物理傷害

	public int getDmgModifierByArmor() {
		return _dmgModifierByArmor;
	}

	public void addDmgModifierByArmor(final int i) {
		_dmgModifierByArmor += i;
	}

	private int _bowHitModifierByArmor = 0; // 防具增加遠距離物理命中

	public int getBowHitModifierByArmor() {
		return _bowHitModifierByArmor;
	}

	public void addBowHitModifierByArmor(final int i) {
		_bowHitModifierByArmor += i;
	}

	private int _bowDmgModifierByArmor = 0; // 防具增加遠距離物理傷害

	public int getBowDmgModifierByArmor() {
		return _bowDmgModifierByArmor;
	}

	public void addBowDmgModifierByArmor(final int i) {
		_bowDmgModifierByArmor += i;
	}

	private boolean _gresValid; // G-RESが有効か

	private void setGresValid(final boolean valid) {
		_gresValid = valid;
	}

	public boolean isGresValid() {
		return _gresValid;
	}

	private boolean _isFishing = false;

	public boolean isFishing() {
		return _isFishing;
	}

	private int _fishX = -1;

	public int get_fishX() {
		return _fishX;
	}

	private int _fishY = -1;

	public int get_fishY() {
		return _fishY;
	}

	private int _fishTime = -1;

	public int get_fishTime() {
		return _fishTime;
	}

	private int _pole = -1;

	public int get_pole() {
		return _pole;
	}

	public void setFishing(final boolean flag, final int fishX, final int fishY, final int time, final int pole) {
		_isFishing = flag;
		_fishX = fishX;
		_fishY = fishY;
		_fishTime = time;
		_pole = pole;

		if (_isFishing) {
			setSkillEffect(FISHING, _fishTime * 1000);
		} else {
			killSkillEffectTimer(FISHING);
		}
	}

	private int _cookingId = 0;

	public int getCookingId() {
		return _cookingId;
	}

	public void setCookingId(final int i) {
		_cookingId = i;
	}

	private int _dessertId = 0;

	public int getDessertId() {
		return _dessertId;
	}

	public void setDessertId(final int i) {
		_dessertId = i;
	}

	/**
	 * LVによる命中ボーナスを設定する LVが変動した場合などに呼び出せば再計算される
	 * 
	 * @return
	 */
	public void resetBaseDmgup() {
		int newBaseDmgup = 0;
		int newBaseBowDmgup = 0;
		if (isKnight() || isDarkelf() || isDragonKnight() || isWarrior()) { // ナイト、ダークエルフ、ドラゴンナイト
			newBaseDmgup = getLevel() / 10;
			newBaseBowDmgup = 0;

		} else if (isElf()) { // エルフ
			newBaseDmgup = 0;
			newBaseBowDmgup = getLevel() / 10;
		}
		addDmgup(newBaseDmgup - _baseDmgup);
		addBowDmgup(newBaseBowDmgup - _baseBowDmgup);
		_baseDmgup = newBaseDmgup;
		_baseBowDmgup = newBaseBowDmgup;
	}

	/**
	 * LVによる命中ボーナスを設定する LVが変動した場合などに呼び出せば再計算される
	 * 
	 * @return
	 */
	public void resetBaseHitup() {
		int newBaseHitup = 0;
		int newBaseBowHitup = 0;
		if (isCrown()) { // プリ
			// 王族的基本能力設定變更。
			// 每4級，近距離命中率+1 (repaired by terry0412)
			newBaseHitup = getLevel() / 4;
			newBaseBowHitup = getLevel() / 4;

		} else if (isKnight() || isWarrior()) { // ナイト
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;

		} else if (isWizard()) { // ウィザード
			// 法師的基本能力設定變更。
			// 每8級，近距離命中率+1 (repaired by terry0412)
			newBaseHitup = getLevel() / 8;
			newBaseBowHitup = getLevel() / 8;

		} else if (isElf()) { // エルフ
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;

		} else if (isDarkelf()) { // ダークエルフ
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;

		} else if (isDragonKnight()) { // ドラゴンナイト
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;

		} else if (isIllusionist()) { // イリュージョニスト
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		}

		addHitup(newBaseHitup - _baseHitup);
		addBowHitup(newBaseBowHitup - _baseBowHitup);
		_baseHitup = newBaseHitup;
		_baseBowHitup = newBaseBowHitup;
	}

	/**
	 * キャラクターステータスからACを再計算して設定する 初期設定時、LVUP,LVDown時などに呼び出す
	 */
	public void resetBaseAc() {
		final int newAc = CalcStat.calcAc(getLevel(), getBaseDex());
		addAc(newAc - _baseAc);
		_baseAc = newAc;
	}

	/**
	 * キャラクターステータスから素のMRを再計算して設定する 初期設定時、スキル使用時やLVUP,LVDown時に呼び出す
	 */
	public void resetBaseMr() {
		int newMr = 0;
		if (isCrown() || isWarrior()) { // プリ
			newMr = 10;

		} else if (isElf()) { // エルフ
			newMr = 25;

		} else if (isWizard()) { // ウィザード
			newMr = 15;

		} else if (isDarkelf()) { // ダークエルフ
			newMr = 10;

		} else if (isDragonKnight()) { // ドラゴンナイト
			newMr = 18;

		} else if (isIllusionist()) { // イリュージョニスト
			newMr = 20;
		}
		newMr += CalcStat.calcStatMr(getWis()); // WIS分のMRボーナス
		newMr += getLevel() / 2; // LVの半分だけ追加
		addMr(newMr - _baseMr);
		_baseMr = newMr;
	}

	/**
	 * 重新設置等級為目前經驗質所屬
	 */
	public void resetLevel() {
		setLevel(ExpTable.getLevelByExp(_exp));
	}

	/**
	 * 初期ステータスから現在のボーナスを再計算して設定する 初期設定時、再配分時に呼び出す
	 */
	public void resetOriginalHpup() {
		_originalHpup = L1PcOriginal.resetOriginalHpup(this);
	}

	public void resetOriginalMpup() {
		_originalMpup = L1PcOriginal.resetOriginalMpup(this);
	}

	public void resetOriginalStrWeightReduction() {
		_originalStrWeightReduction = L1PcOriginal.resetOriginalStrWeightReduction(this);
	}

	public void resetOriginalDmgup() {
		_originalDmgup = L1PcOriginal.resetOriginalDmgup(this);
	}

	public void resetOriginalConWeightReduction() {
		_originalConWeightReduction = L1PcOriginal.resetOriginalConWeightReduction(this);
	}

	public void resetOriginalBowDmgup() {
		_originalBowDmgup = L1PcOriginal.resetOriginalBowDmgup(this);
	}

	public void resetOriginalHitup() {
		_originalHitup = L1PcOriginal.resetOriginalHitup(this);
	}

	public void resetOriginalBowHitup() {
		_originalBowHitup = L1PcOriginal.resetOriginalBowHitup(this);
	}

	public void resetOriginalMr() {
		_originalMr = L1PcOriginal.resetOriginalMr(this);
		addMr(_originalMr);
	}

	public void resetOriginalMagicHit() {
		_originalMagicHit = L1PcOriginal.resetOriginalMagicHit(this);
	}

	public void resetOriginalMagicCritical() {
		_originalMagicCritical = L1PcOriginal.resetOriginalMagicCritical(this);
	}

	public void resetOriginalMagicConsumeReduction() {
		_originalMagicConsumeReduction = L1PcOriginal.resetOriginalMagicConsumeReduction(this);
	}

	public void resetOriginalMagicDamage() {
		_originalMagicDamage = L1PcOriginal.resetOriginalMagicDamage(this);
	}

	public void resetOriginalAc() {
		_originalAc = L1PcOriginal.resetOriginalAc(this);
		// System.out.println("_originalAc:"+_originalAc);
		addAc(0 - _originalAc);
	}

	public void resetOriginalEr() {
		_originalEr = L1PcOriginal.resetOriginalEr(this);
	}

	public void resetOriginalHpr() {
		_originalHpr = L1PcOriginal.resetOriginalHpr(this);
	}

	public void resetOriginalMpr() {
		_originalMpr = L1PcOriginal.resetOriginalMpr(this);
	}

	/**
	 * 全屬性重置
	 */
	public void refresh() {
		resetLevel();
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseMr();
		resetBaseAc();
		resetOriginalHpup();
		resetOriginalMpup();
		resetOriginalDmgup();
		resetOriginalBowDmgup();
		resetOriginalHitup();
		resetOriginalBowHitup();
		resetOriginalMr();
		resetOriginalMagicHit();
		resetOriginalMagicCritical();
		resetOriginalMagicConsumeReduction();
		resetOriginalMagicDamage();
		resetOriginalAc();
		resetOriginalEr();
		resetOriginalHpr();
		resetOriginalMpr();
		resetOriginalStrWeightReduction();
		resetOriginalConWeightReduction();
	}

	// 人物訊息拒絕清單
	private final L1ExcludingList _excludingList = new L1ExcludingList();

	/**
	 * 人物訊息拒絕清單
	 * 
	 * @return
	 */
	public L1ExcludingList getExcludingList() {
		return _excludingList;
	}

	private final L1ExcludingMailList _excludingMailList = new L1ExcludingMailList();// 信件黑名單

	/**
	 * 傳回一個信件的黑名單
	 * 
	 * @return
	 */
	public L1ExcludingMailList getExcludingMailList() {
		return _excludingMailList;
	}

	private int _teleportX = 0;// 傳送目的座標X

	/**
	 * 傳送目的座標X
	 * 
	 * @return
	 */
	public int getTeleportX() {
		return _teleportX;
	}

	/**
	 * 傳送目的座標X
	 * 
	 * @param i
	 */
	public void setTeleportX(final int i) {
		_teleportX = i;
	}

	private int _teleportY = 0;// 傳送目的座標Y

	/**
	 * 傳送目的座標Y
	 * 
	 * @return
	 */
	public int getTeleportY() {
		return _teleportY;
	}

	/**
	 * 傳送目的座標Y
	 * 
	 * @param i
	 */
	public void setTeleportY(final int i) {
		_teleportY = i;
	}

	private short _teleportMapId = 0;// 傳送目的座標MAP

	/**
	 * 傳送目的座標MAP
	 * 
	 * @return
	 */
	public short getTeleportMapId() {
		return _teleportMapId;
	}

	/**
	 * 傳送目的座標MAP
	 * 
	 * @param i
	 */
	public void setTeleportMapId(final short i) {
		_teleportMapId = i;
	}

	private int _teleportHeading = 0;// 傳送後面向

	/**
	 * 傳送後面向
	 * 
	 * @return
	 */
	public int getTeleportHeading() {
		return _teleportHeading;
	}

	/**
	 * 傳送後面向
	 * 
	 * @param i
	 */
	public void setTeleportHeading(final int i) {
		_teleportHeading = i;
	}

	private int _tempCharGfxAtDead;// 死亡時外型代號

	/**
	 * 死亡時外型代號
	 * 
	 * @return
	 */
	public int getTempCharGfxAtDead() {
		return _tempCharGfxAtDead;
	}

	/**
	 * 死亡時外型代號
	 * 
	 * @param i
	 */
	private void setTempCharGfxAtDead(final int i) {
		_tempCharGfxAtDead = i;
	}

	private boolean _isCanWhisper = true;// 全秘密語(收聽)

	/**
	 * 全秘密語(收聽)
	 * 
	 * @return flag true:接收 false:拒絕
	 */
	public boolean isCanWhisper() {
		return _isCanWhisper;
	}

	/**
	 * 全秘密語(收聽)
	 * 
	 * @param flag
	 *            flag true:接收 false:拒絕
	 */
	public void setCanWhisper(final boolean flag) {
		_isCanWhisper = flag;
	}

	private boolean _isShowTradeChat = true;// 買賣頻道(收聽)

	/**
	 * 買賣頻道(收聽)
	 * 
	 * @return flag true:接收 false:拒絕
	 */
	public boolean isShowTradeChat() {
		return _isShowTradeChat;
	}

	/**
	 * 買賣頻道(收聽)
	 * 
	 * @param flag
	 *            true:接收 false:拒絕
	 */
	public void setShowTradeChat(final boolean flag) {
		_isShowTradeChat = flag;
	}

	private boolean _isShowWorldChat = true;// 全體聊天(收聽)

	/**
	 * 全體聊天(收聽)
	 * 
	 * @return flag true:接收 false:拒絕
	 */
	public boolean isShowWorldChat() {
		return _isShowWorldChat;
	}

	/**
	 * 全體聊天(收聽)
	 * 
	 * @param flag
	 *            flag true:接收 false:拒絕
	 */
	public void setShowWorldChat(final boolean flag) {
		_isShowWorldChat = flag;
	}

	private int _fightId;// 決鬥對象OBJID

	/**
	 * 決鬥對象OBJID
	 * 
	 * @return
	 */
	public int getFightId() {
		return _fightId;
	}

	/**
	 * 決鬥對象OBJID
	 * 
	 * @param i
	 */
	public void setFightId(final int i) {
		_fightId = i;
	}

	private byte _chatCount = 0;// 對話檢查次數

	private long _oldChatTimeInMillis = 0L;// 對話檢查毫秒差

	/**
	 * 對話檢查(洗畫面)
	 */
	public void checkChatInterval() {
		final long nowChatTimeInMillis = System.currentTimeMillis();
		if (_chatCount == 0) {
			_chatCount++;
			_oldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		final long chatInterval = nowChatTimeInMillis - _oldChatTimeInMillis;
		// 時間差異2秒以上
		if (chatInterval > 2000) {
			_chatCount = 0;
			_oldChatTimeInMillis = 0;

		} else {
			if (_chatCount >= 3) {
				setSkillEffect(STATUS_CHAT_PROHIBITED, 120 * 1000);
				this.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, 120));
				// \f3因洗畫面的關係，2分鐘之內無法聊天。
				this.sendPackets(new S_ServerMessage(153));
				_chatCount = 0;
				_oldChatTimeInMillis = 0;
			}
			_chatCount++;
		}
	}

	private int _callClanId;// 呼喚盟友(對象OBJID)

	/**
	 * 傳回呼喚盟友(對象OBJID)
	 * 
	 * @return
	 */
	public int getCallClanId() {
		return _callClanId;
	}

	/**
	 * 設置呼喚盟友(對象OBJID)
	 * 
	 * @param i
	 */
	public void setCallClanId(final int i) {
		_callClanId = i;
	}

	private int _callClanHeading;// 設置呼喚盟友(自己的面向)

	/**
	 * 設置呼喚盟友(自己的面向)
	 * 
	 * @return
	 */
	public int getCallClanHeading() {
		return _callClanHeading;
	}

	/**
	 * 傳回呼喚盟友(自己的面向)
	 * 
	 * @return
	 */
	public void setCallClanHeading(final int i) {
		_callClanHeading = i;
	}

	private boolean _isInCharReset = false;// 執行人物重設狀態

	/**
	 * 傳回執行人物重設狀態
	 * 
	 * @return
	 */
	public boolean isInCharReset() {
		return _isInCharReset;
	}

	/**
	 * 設置執行人物重設狀態
	 * 
	 * @param flag
	 */
	public void setInCharReset(final boolean flag) {
		_isInCharReset = flag;
	}

	private int _tempLevel = 1;// 人物重置等級暫存(最低)

	/**
	 * 人物重置等級暫存(最低)
	 * 
	 * @return
	 */
	public int getTempLevel() {
		return _tempLevel;
	}

	/**
	 * 人物重置等級暫存(最低)
	 * 
	 * @param i
	 */
	public void setTempLevel(final int i) {
		_tempLevel = i;
	}

	private int _tempMaxLevel = 1;// 人物重置等級暫存(最高)

	/**
	 * 人物重置等級暫存(最高)
	 * 
	 * @return
	 */
	public int getTempMaxLevel() {
		return _tempMaxLevel;
	}

	/**
	 * 人物重置等級暫存(最高)
	 * 
	 * @param i
	 */
	public void setTempMaxLevel(final int i) {
		_tempMaxLevel = i;
	}

	private boolean _isSummonMonster = false;// 是否展開召喚控制選單

	/**
	 * 設置是否展開召喚控制選單
	 * 
	 * @param SummonMonster
	 */
	public void setSummonMonster(final boolean SummonMonster) {
		_isSummonMonster = SummonMonster;
	}

	/**
	 * 是否展開召喚控制選單
	 * 
	 * @return
	 */
	public boolean isSummonMonster() {
		return _isSummonMonster;
	}

	private boolean _isShapeChange = false;// 是否展開變身控制選單

	/**
	 * 設置是否展開變身控制選單
	 * 
	 * @param isShapeChange
	 */
	public void setShapeChange(final boolean isShapeChange) {
		_isShapeChange = isShapeChange;
	}

	/**
	 * 是否展開變身控制選單
	 * 
	 * @return
	 */
	public boolean isShapeChange() {
		return _isShapeChange;
	}

	private String _text;// 暫存文字串

	/**
	 * 設置暫存文字串(收件者)
	 * 
	 * @param text
	 */
	public void setText(final String text) {
		_text = text;
	}

	/**
	 * 傳回暫存文字串(收件者)
	 * 
	 * @return
	 */
	public String getText() {
		return _text;
	}

	private byte[] _textByte = null;// 暫存byte[]陣列

	/**
	 * 設定暫存byte[]陣列
	 * 
	 * @param textByte
	 */
	public void setTextByte(final byte[] textByte) {
		_textByte = textByte;
	}

	/**
	 * 傳回暫存byte[]陣列
	 * 
	 * @return
	 */
	public byte[] getTextByte() {
		return _textByte;
	}

	private L1PcOther _other;// 額外紀錄資料

	/**
	 * 額外紀錄資料
	 * 
	 * @param other
	 */
	public void set_other(final L1PcOther other) {
		_other = other;
	}

	/**
	 * 額外紀錄資料
	 * 
	 * @return
	 */
	public L1PcOther get_other() {
		return _other;
	}

	private L1PcOtherList _otherList;// 額外清單紀錄資料

	/**
	 * 額外清單紀錄資料
	 * 
	 * @param other
	 */
	public void set_otherList(final L1PcOtherList other) {
		_otherList = other;
	}

	/**
	 * 額外清單紀錄資料
	 * 
	 * @return
	 */
	public L1PcOtherList get_otherList() {
		return _otherList;
	}

	private int _oleLocX;// 移動前座標暫存X

	/**
	 * 移動前座標暫存X
	 * 
	 * @param oleLocx
	 */
	public void setOleLocX(final int oleLocx) {
		_oleLocX = oleLocx;
	}

	/**
	 * 移動前座標暫存X
	 * 
	 * @return
	 */
	public int getOleLocX() {
		return _oleLocX;
	}

	private int _oleLocY;// 移動前座標暫存Y

	/**
	 * 移動前座標暫存Y
	 * 
	 * @param oleLocy
	 */
	public void setOleLocY(final int oleLocy) {
		_oleLocY = oleLocy;
	}

	/**
	 * 移動前座標暫存Y
	 * 
	 * @return
	 */
	public int getOleLocY() {
		return _oleLocY;
	}

	private L1PcInstance _target = null;

	/**
	 * 設置目前攻擊對象
	 * 
	 * @param target
	 */
	public void setNowTarget(final L1PcInstance target) {
		_target = target;
	}

	/**
	 * 傳回目前攻擊對象
	 */
	public L1PcInstance getNowTarget() {
		return _target;
	}

	private int _dmgDown = 0;

	/**
	 * 副助道具傷害減免
	 * 
	 * @param dmgDown
	 */
	public void set_dmgDown(final int dmgDown) {
		_dmgDown = dmgDown;
	}

	/**
	 * 副助道具傷害減免
	 * 
	 * @return
	 */
	public int get_dmgDown() {
		return _dmgDown;
	}

	/**
	 * 保存寵物目前模式
	 * 
	 * @param pc
	 */
	public void setPetModel() {
		try {
			// 寵物的跟隨移動
			for (final L1NpcInstance petNpc : getPetList().values()) {
				if (petNpc != null) {
					if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
						final L1SummonInstance summon = (L1SummonInstance) petNpc;
						summon.set_tempModel();

					} else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
						final L1PetInstance pet = (L1PetInstance) petNpc;
						pet.set_tempModel();
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 恢復寵物目前模式
	 * 
	 * @param pc
	 */
	public void getPetModel() {
		try {
			// 寵物的跟隨移動
			for (final L1NpcInstance petNpc : getPetList().values()) {
				if (petNpc != null) {
					if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
						final L1SummonInstance summon = (L1SummonInstance) petNpc;
						summon.get_tempModel();

					} else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
						final L1PetInstance pet = (L1PetInstance) petNpc;
						pet.get_tempModel();
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private L1DeInstance _outChat = null;

	/**
	 * 設置對話輸出
	 * 
	 * @param b
	 */
	public void set_outChat(final L1DeInstance b) {
		_outChat = b;
	}

	/**
	 * 對話輸出模式
	 * 
	 * @return
	 */
	public L1DeInstance get_outChat() {
		return _outChat;
	}

	private long _h_time;// 生存吶喊時間

	/**
	 * 生存吶喊時間
	 * 
	 * @return
	 */
	public long get_h_time() {
		return _h_time;
	}

	/**
	 * 生存吶喊時間
	 * 
	 * @param h_time
	 */
	public void set_h_time(final long time) {
		_h_time = time;
	}

	private int _int1;// 機率增加攻擊力
	private int _int2;// 機率(1/100)

	/**
	 * 機率增加攻擊力
	 * 
	 * @param int1
	 * @param int2
	 */
	public void set_dmgAdd(final int int1, final int int2) {
		_int1 += int1;
		_int2 += int2;
	}

	/**
	 * 傳回機率增加的攻擊力
	 * 
	 * @return
	 */
	public int dmgAdd() {
		if (_int2 == 0) {
			return 0;
		}
		if ((_random.nextInt(100) + 1) <= _int2) {
			if (!getDolls().isEmpty()) {
				for (final L1DollInstance doll : getDolls().values()) {
					doll.show_action(1);
				}
			}
			return _int1;
		}
		return 0;
	}

	private int _evasion;// 迴避機率(1/1000)

	/**
	 * 迴避機率
	 * 
	 * @param int1
	 */
	public void set_evasion(final int int1) {
		_evasion += int1;
	}

	/**
	 * 傳回迴避機率
	 * 
	 * @return
	 */
	public int get_evasion() {
		return _evasion;
	}

	private double _expadd = 0.0D;// 經驗值增加

	/**
	 * 經驗值增加
	 * 
	 * @param int1
	 */
	public void set_expadd(final int int1) {
		_expadd += (int1 / 100D);
	}

	/**
	 * 經驗值增加
	 * 
	 * @return
	 */
	public double getExpAdd() {
		return _expadd;
	}

	private int _dd1;// 機率傷害減免
	private int _dd2;// 機率(1/100)

	/**
	 * 機率傷害減免
	 * 
	 * @param int1
	 * @param int2
	 */
	public void set_dmgDowe(final int int1, final int int2) {
		_dd1 += int1;
		_dd2 += int2;
	}

	/**
	 * 傳回機率傷害減免
	 * 
	 * @return
	 */
	public int dmgDowe() {
		if (_dd2 == 0) {
			return 0;
		}
		if ((_random.nextInt(100) + 1) <= _dd2) {
			if (!getDolls().isEmpty()) {
				for (final L1DollInstance doll : getDolls().values()) {
					doll.show_action(2);
				}
			}
			return _dd1;
		}
		return 0;
	}

	private boolean _isFoeSlayer = false;

	/**
	 * 是否使用屠宰者
	 * 
	 * @return
	 */
	public boolean isFoeSlayer() {
		return _isFoeSlayer;
	}

	/**
	 * 是否使用屠宰者
	 */
	public void isFoeSlayer(final boolean isFoeSlayer) {
		_isFoeSlayer = isFoeSlayer;
	}

	private int _weaknss;
	private long _weaknss_t; // 時間

	/**
	 * 弱點曝光時間
	 * 
	 * @return
	 */
	public long get_weaknss_t() {
		return _weaknss_t;
	}

	/**
	 * 弱點曝光階段
	 * 
	 * @return
	 */
	public int get_weaknss() {
		return _weaknss;
	}

	/**
	 * 弱點曝光階段
	 * 
	 * @param lv
	 */
	public void set_weaknss(final int lv, final long t) {
		_weaknss = lv;
		_weaknss_t = t;
	}

	private int _actionId = -1; // 角色表情動作代號

	/**
	 * 角色表情動作代號
	 * 
	 * @param actionId
	 */
	public void set_actionId(final int actionId) {
		_actionId = actionId;
	}

	/**
	 * 角色表情動作代號
	 * 
	 * @return
	 */
	public int get_actionId() {
		return _actionId;
	}

	private Chapter01R _hardin; // 哈汀副本線程

	/**
	 * 哈汀副本線程
	 * 
	 * @param hardin
	 */
	public void set_hardinR(final Chapter01R hardin) {
		_hardin = hardin;
	}

	/**
	 * 哈汀副本線程
	 * 
	 * @return
	 */
	public Chapter01R get_hardinR() {
		return _hardin;
	}

	private final Map<Integer, L1ItemPower_text> _powers = new ConcurrentHashMap<Integer, L1ItemPower_text>();

	public void add_power(final L1ItemPower_text value) {
		if (!_powers.containsKey(value.get_id())) {
			_powers.put(value.get_id(), value);
			value.add_pc_power(this);
			sendPackets(new S_ServerMessage("\\aD" + value.getMsg()));
			// 套裝效果動畫
			if (value.getGfx() != null) {
				for (final int gfx : value.getGfx()) {
					// 動畫效果
					sendPacketsX8(new S_SkillSound(getId(), gfx));
				}
			}
		}
	}

	public void remove_power(final L1ItemPower_text value) {
		if (_powers.containsKey(value.get_id())) {
			_powers.remove(value.get_id());
			value.remove_pc_power(this);
			sendPackets(new S_ServerMessage("\\aH失去 " + value.getMsg() + " 效果"));
		}
	}

	public boolean get_power_contains(final L1ItemPower_text value) {
		return _powers.containsValue(value);
	}

	public Map<Integer, L1ItemPower_text> get_powers() {
		return _powers;
	}

	// changed by terry0412
	/*
	 * private final Map<Integer, Integer> _powers = new ConcurrentHashMap<Integer, Integer>(); public void add_power(L1ItemPower_text value) { //value.add_pc_power(this); // 效果可重疊 by
	 * terry0412 if (!_powers.containsKey(value.get_id())) { _powers.put(value.get_id(), 1); //value.add_pc_power(this); // cancelled by terry0412 sendPackets(new S_ServerMessage("\\fW" +
	 * value.getMsg())); // 套裝效果動畫 if (value.getGfx() != null) { for (int gfx : value.getGfx()) { // 動畫效果 sendPacketsX8(new S_SkillSound(getId(), gfx)); } } } else {
	 * _powers.put(value.get_id(), _powers.get(value.get_id()) + 1); } } public void remove_power(L1ItemPower_text value) { // 效果可重疊 by terry0412 value.remove_pc_power(this); if
	 * (_powers.containsKey(value.get_id())) { //_powers.remove(value.get_id()); // cancelled by terry0412 //value.remove_pc_power(this); // cancelled by terry0412 if
	 * (_powers.get(value.get_id()) <= 1) { _powers.remove(value.get_id()); sendPackets(new S_ServerMessage("\\fY失去 " + value.getMsg() + " 效果")); } else { _powers.put(value.get_id(),
	 * _powers.get(value.get_id()) - 1); } } } public boolean get_power_contains(L1ItemPower_text value) { return _powers.containsValue(value); } public Map<Integer, Integer> get_powers()
	 * { return _powers; }
	 */

	private int _unfreezingTime; // 解除人物卡點

	public void set_unfreezingTime(final int i) {
		_unfreezingTime = i;
	}

	public int get_unfreezingTime() {
		return _unfreezingTime;
	}

	private L1User_Power _c_power; // 陣營

	public void set_c_power(final L1User_Power power) {
		_c_power = power;
	}

	public L1User_Power get_c_power() {
		return _c_power;
	}

	private int _dice_hp;
	private int _sucking_hp;

	/**
	 * 增加機率吸血
	 * 
	 * @param dice_hp
	 * @param sucking_hp
	 */
	public void add_dice_hp(final int dice_hp, final int sucking_hp) {
		_dice_hp += dice_hp;
		_sucking_hp += sucking_hp;
	}

	public int dice_hp() {
		return _dice_hp;
	}

	public int sucking_hp() {
		return _sucking_hp;
	}

	private int _dice_mp;
	private int _sucking_mp;

	/**
	 * 增加機率吸魔
	 * 
	 * @param dice_mp
	 * @param sucking_mp
	 */
	public void add_dice_mp(final int dice_mp, final int sucking_mp) {
		_dice_mp += dice_mp;
		_sucking_mp += sucking_mp;
	}

	public int dice_mp() {
		return _dice_mp;
	}

	public int sucking_mp() {
		return _sucking_mp;
	}

	private int _double_dmg;

	/**
	 * 增加機率雙倍打擊
	 * 
	 * @param double_dmg
	 */
	public void add_double_dmg(final int double_dmg) {
		_double_dmg += double_dmg;
	}

	public int double_dmg() {
		return _double_dmg;
	}

	private int _lift;

	/**
	 * 增加機率防禦解除
	 * 
	 * @param lift
	 */
	public void add_lift(final int lift) {
		_lift += lift;
	}

	public int lift() {
		return _lift;
	}

	private int _magic_modifier_dmg = 0;// 套裝增加魔法傷害

	public void add_magic_modifier_dmg(final int add) {
		_magic_modifier_dmg += add;
	}

	public int get_magic_modifier_dmg() {
		return _magic_modifier_dmg;
	}

	private int _magic_reduction_dmg = 0;// 套裝減免魔法傷害

	public void add_magic_reduction_dmg(final int add) {
		_magic_reduction_dmg += add;
	}

	public int get_magic_reduction_dmg() {
		return _magic_reduction_dmg;
	}

	private boolean _rname = false;// 重設名稱

	/**
	 * 重設名稱
	 * 
	 * @param b
	 */
	public void rename(final boolean b) {
		_rname = b;
	}

	/**
	 * 重設名稱
	 * 
	 * @return
	 */
	public boolean is_rname() {
		return _rname;
	}

	private boolean _retitle = false;// 重設封號

	/**
	 * 重設封號
	 * 
	 * @return
	 */
	public boolean is_retitle() {
		return _retitle;
	}

	/**
	 * 重設封號
	 * 
	 * @param b
	 */
	public void retitle(final boolean b) {
		_retitle = b;
	}

	private int _repass = 0;// 重設密碼

	/**
	 * 重設密碼
	 * 
	 * @return
	 */
	public int is_repass() {
		return _repass;
	}

	/**
	 * 重設密碼
	 * 
	 * @param b
	 */
	public void repass(final int b) {
		_repass = b;
	}

	// 交易物品暫存
	private final List<L1TradeItem> _trade_items = new CopyOnWriteArrayList<L1TradeItem>();

	/**
	 * 加入交易物品暫存
	 * 
	 * @param info
	 */
	public void add_trade_item(final L1TradeItem info) {
		if (_trade_items.size() == 16) {
			return;
		}
		_trade_items.add(info);
	}

	/**
	 * 交易物品暫存
	 * 
	 * @return
	 */
	public List<L1TradeItem> get_trade_items() {
		return _trade_items;
	}

	/**
	 * 清空交易物品暫存
	 */
	public void get_trade_clear() {
		_tradeID = 0;
		_trade_items.clear();
	}

	private int _mode_id = 0;// 記錄選取位置

	/**
	 * 記錄選取位置
	 * 
	 * @param mode
	 */
	public void set_mode_id(final int mode) {
		_mode_id = mode;
	}

	/**
	 * 記錄選取位置
	 * 
	 * @return
	 */
	public int get_mode_id() {
		return _mode_id;
	}

	private boolean _check_item = false;

	public void set_check_item(final boolean b) {
		_check_item = b;
	}

	public boolean get_check_item() {
		return _check_item;
	}

	private boolean _vip_1 = false;

	/**
	 * VIP1月卡 (死亡保護 true:不執行 false:執行)
	 * 
	 * @param b
	 */
	public void set_VIP1(final boolean b) {
		_vip_1 = b;
	}

	private boolean _vip_2 = false;

	/**
	 * VIP2月卡 (積分保護 true:不執行 false:執行)
	 * 
	 * @param b
	 */
	public void set_VIP2(final boolean b) {
		_vip_2 = b;
	}

	private boolean _vip_3 = false;

	/**
	 * VIP3月卡 (物品保護 true:不執行 false:執行)
	 * 
	 * @param b
	 */
	public void set_VIP3(final boolean b) {
		_vip_3 = b;
	}

	private boolean _vip_4 = false;

	/**
	 * VIP4月卡 (技能保護 true:不執行 false:執行)
	 * 
	 * @param b
	 */
	public void set_VIP4(final boolean b) {
		_vip_4 = b;
	}

	private long _global_time = 0;

	public long get_global_time() {
		return _global_time;
	}

	public void set_global_time(final long global_time) {
		_global_time = global_time;
	}

	// DOLL 指定時間HP恢復

	private int _doll_hpr = 0;

	public int get_doll_hpr() {
		return _doll_hpr;
	}

	public void set_doll_hpr(final int hpr) {
		_doll_hpr = hpr;
	}

	private int _doll_hpr_time = 0;// 計算用時間(秒)

	public int get_doll_hpr_time() {
		return _doll_hpr_time;
	}

	public void set_doll_hpr_time(final int time) {
		_doll_hpr_time = time;
	}

	private int _doll_hpr_time_src = 0;// 恢復時間(秒)

	public int get_doll_hpr_time_src() {
		return _doll_hpr_time_src;
	}

	public void set_doll_hpr_time_src(final int time) {
		_doll_hpr_time_src = time;
	}

	// DOLL 指定時間MP恢復

	private int _doll_mpr = 0;

	public int get_doll_mpr() {
		return _doll_mpr;
	}

	public void set_doll_mpr(final int mpr) {
		_doll_mpr = mpr;
	}

	private int _doll_mpr_time = 0;// 計算用時間(秒)

	public int get_doll_mpr_time() {
		return _doll_mpr_time;
	}

	public void set_doll_mpr_time(final int time) {
		_doll_mpr_time = time;
	}

	private int _doll_mpr_time_src = 0;// 恢復時間(秒)

	public int get_doll_mpr_time_src() {
		return _doll_mpr_time_src;
	}

	public void set_doll_mpr_time_src(final int time) {
		_doll_mpr_time_src = time;
	}

	// DOLL 指定時間給予物品

	private final int[] _doll_get = new int[2];

	public int[] get_doll_get() {
		return _doll_get;
	}

	public void set_doll_get(final int itemid, final int count) {
		_doll_get[0] = itemid;
		_doll_get[1] = count;
	}

	private int _doll_get_time = 0;// 計算用時間(秒)

	public int get_doll_get_time() {
		return _doll_get_time;
	}

	public void set_doll_get_time(final int time) {
		_doll_get_time = time;
	}

	private int _doll_get_time_src = 0;// 給予時間(秒)

	public int get_doll_get_time_src() {
		return _doll_get_time_src;
	}

	public void set_doll_get_time_src(final int time) {
		_doll_get_time_src = time;
	}

	// 留言版使用
	private String _board_title;// 暫存文字串

	public void set_board_title(final String text) {
		_board_title = text;
	}

	public String get_board_title() {
		return _board_title;
	}

	private String _board_content;// 暫存文字串

	public void set_board_content(final String text) {
		_board_content = text;
	}

	public String get_board_content() {
		return _board_content;
	}

	// 封包接收速度紀錄
	private long _spr_move_time = 0;// 移動

	public void set_spr_move_time(final long spr_time) {
		_spr_move_time = spr_time;
	}

	public long get_spr_move_time() {
		return _spr_move_time;
	}

	private long _spr_attack_time = 0;// 攻擊

	public void set_spr_attack_time(final long spr_time) {
		_spr_attack_time = spr_time;
	}

	public long get_spr_attack_time() {
		return _spr_attack_time;
	}

	private long _spr_skill_time = 0;// 技能

	public void set_spr_skill_time(final long spr_time) {
		_spr_skill_time = spr_time;
	}

	public long get_spr_skill_time() {
		return _spr_skill_time;
	}

	// 死亡相關

	private int _delete_time = 0;// 死亡時間

	public void set_delete_time(final int time) {
		_delete_time = time;
	}

	public int get_delete_time() {
		return _delete_time;
	}

	// 藥水使用HP恢復增加

	private int _up_hp_potion = 0;

	/**
	 * 藥水使用HP恢復增加(1/100)
	 * 
	 * @param up_hp_potion
	 */
	public void set_up_hp_potion(final int up_hp_potion) {
		_up_hp_potion = up_hp_potion;
	}

	/**
	 * 藥水使用HP恢復增加(1/100)
	 * 
	 * @return
	 */
	public int get_up_hp_potion() {
		return _up_hp_potion;
	}

	private int _elitePlateMail_Fafurion = 0;
	private int _fafurion_hpmin = 0;
	private int _fafurion_hpmax = 0;

	public void set_elitePlateMail_Fafurion(final int r, final int hpmin, final int hpmax) {
		_elitePlateMail_Fafurion = r;
		_fafurion_hpmin = hpmin;
		_fafurion_hpmax = hpmax;
	}

	private int _shieldOfRebels_Chance;
	private int _shieldOfRebels_dmg_reduction;

	public void set_shieldOfRebels(final int chance, final int dmg_reduction) {
		_shieldOfRebels_Chance = chance;
		_shieldOfRebels_dmg_reduction = dmg_reduction;
	}

	// 毒性抵抗效果
	int _venom_resist = 0;

	/**
	 * 毒性抵抗效果(裝備)
	 * 
	 * @param i
	 *            裝備數量
	 */
	public void set_venom_resist(final int i) {
		_venom_resist += i;
	}

	/**
	 * 毒性抵抗效果(裝備)
	 * 
	 * @return
	 */
	public int get_venom_resist() {
		return _venom_resist;
	}

	public final int getEmblemId() {
		if (isProtector() || (getClanid() <= 0)) {
			return 0;
		}
		final L1Clan clan = getClan();
		if (clan == null) {
			return 0;
		}
		return clan.getEmblemId();
	}

	// 加速檢測器
	private final AcceleratorChecker _speed;

	/**
	 * 加速檢測器
	 * 
	 * @return
	 */
	public AcceleratorChecker speed_Attack() {
		return _speed;
	}

	private int _arena = 0;// 競技場得分

	/**
	 * 競技場得分
	 * 
	 * @param i
	 */
	public void set_arena(final int i) {
		_arena = i;
	}

	/**
	 * 競技場得分
	 * 
	 * @return
	 */
	public int get_arena() {
		return _arena;
	}

	private int _temp_adena = 0;// 本次使用貨幣類型

	/**
	 * 本次使用貨幣類型
	 * 
	 * @param itemid
	 */
	public void set_temp_adena(final int itemid) {
		_temp_adena = itemid;
	}

	/**
	 * 本次使用貨幣類型
	 * 
	 * @return
	 */
	public int get_temp_adena() {
		return _temp_adena;
	}

	// 三國

	private long _ss_time = 0;// 陣營經驗啟動時間

	/**
	 * 陣營經驗啟動時間
	 * 
	 * @return
	 */
	public long get_ss_time() {
		return _ss_time;
	}

	/**
	 * 陣營經驗啟動時間
	 * 
	 * @param ss_time
	 */
	public void set_ss_time(final long ss_time) {
		_ss_time = ss_time;
	}

	private int _ss = 0;// 陣營經驗加倍

	/**
	 * 陣營經驗加倍
	 * 
	 * @return
	 */
	public int get_ss() {
		return _ss;
	}

	/**
	 * 陣營經驗加倍
	 * 
	 * @param ss
	 */
	public void set_ss_time(final int ss) {
		_ss = ss;
	}

	private int killCount;

	/**
	 * @return the killCount
	 */
	public final int getKillCount() {
		return killCount;
	}

	/**
	 * @param killCount
	 *            the killCount to set
	 */
	public final void setKillCount(final int killCount) {
		this.killCount = killCount;
	}

	// 轉生次數 by terry0412
	private int _meteLevel;

	public int getMeteLevel() {
		return _meteLevel;
	}

	public void setMeteLevel(final int i) {
		_meteLevel = i;
	}

	// 轉生附加能力系統 by terry0412
	private L1MeteAbility _meteAbility;

	public final L1MeteAbility getMeteAbility() {
		return _meteAbility;
	}

	public final void resetMeteAbility() {
		if (_meteAbility != null) {
			ExtraMeteAbilityTable.effectBuff(this, _meteAbility, -1);
		}
		_meteAbility = ExtraMeteAbilityTable.getInstance().get(getMeteLevel(), getType());
		if (_meteAbility != null) {
			ExtraMeteAbilityTable.effectBuff(this, _meteAbility, 1);
		}
	}

	// 守護者系統 by terry0412
	private boolean _isProtector;

	public final boolean isProtector() {
		return _isProtector;
	}

	public final void setProtector(final boolean checkFlag) {
		if (_isProtector != checkFlag) {

			giveProtector(checkFlag);

			sendPackets(new S_OwnCharPack(this));
			sendPackets(new S_HPUpdate(this));
			if (isInParty()) {
				getParty().updateMiniHP(this);
			}
			sendPackets(new S_MPUpdate(this));
			sendPackets(new S_SPMR(this));
			removeAllKnownObjects();
			updateObject();
		}
	}

	// 給予輔助效果
	public final void giveProtector(final boolean checkFlag) {
		_isProtector = checkFlag;
		if (checkFlag) {
			addMaxHp(ProtectorSet.HP_UP);
			addMaxMp(ProtectorSet.MP_UP);
			addDmgup(ProtectorSet.DMG_UP);
			addBowDmgup(ProtectorSet.DMG_UP);
			addDamageReductionByArmor(ProtectorSet.DMG_DOWN);
			addSp(ProtectorSet.SP_UP);
			sendPackets(new S_PacketBox(S_PacketBox.PROTECTOR_ICON, 1));
		} else {
			addMaxHp(-ProtectorSet.HP_UP);
			addMaxMp(-ProtectorSet.MP_UP);
			addDmgup(-ProtectorSet.DMG_UP);
			addBowDmgup(-ProtectorSet.DMG_UP);
			addDamageReductionByArmor(-ProtectorSet.DMG_DOWN);
			addSp(-ProtectorSet.SP_UP);
			sendPackets(new S_PacketBox(S_PacketBox.PROTECTOR_ICON, 0));
		}
	}

	private L1Apprentice _apprentice;

	public final L1Apprentice getApprentice() {
		return _apprentice;
	}

	public final void setApprentice(final L1Apprentice apprentice) {
		_apprentice = apprentice;
	}

	private int _tempType;

	public final void checkEffect() {
		int checkType = 0;
		if ((getApprentice() != null) && (getApprentice().getMaster() != null)) {
			final L1PcInstance master = World.get().getPlayer(getApprentice().getMaster().getName());
			if (master != null) {
				final L1Party party = getParty();
				if (party != null) {
					checkType = party.checkMentor(getApprentice());
				} else {
					checkType = 1;
				}
			}
		}
		if (_tempType != checkType) {
			if (_tempType > 0) {
				sendEffectBuff(_tempType, -1);
			}
			if (checkType > 0) {
				sendEffectBuff(checkType, 1);
			}
			sendPackets(new S_SPMR(this));
			sendPackets(new S_OwnCharStatus(this));
			sendPackets(new S_PacketBox(S_PacketBox.EVASION_UPDATE, getEr()));
			if (checkType <= 0) {
				sendPackets(new S_PacketBox(S_PacketBox.APPRENTICE_ICON, 0, Math.max(_tempType - 1, 0)));
			} else {
				sendPackets(new S_PacketBox(S_PacketBox.APPRENTICE_ICON, checkType == 0 ? 0 : 1, Math.max(checkType - 1, 0)));
			}
			_tempType = checkType;
		}
	}

	public void addOriginalEr(final int i) {
		_originalEr += i;
	}

	private final void sendEffectBuff(final int buffType, final int negative) {
		switch (buffType) {
		case 1:
			addAc(-1 * negative);
			break;
		case 2:
			addAc(-1 * negative);
			addMr(1 * negative);
			break;
		case 3:
			addAc(-1 * negative);
			addMr(1 * negative);
			addWater(2 * negative);
			addWind(2 * negative);
			addFire(2 * negative);
			addEarth(2 * negative);
			break;
		case 4:
			addAc(-1 * negative);
			addMr(1 * negative);
			addWater(2 * negative);
			addWind(2 * negative);
			addFire(2 * negative);
			addEarth(2 * negative);
			addOriginalEr(1 * negative);
			break;
		case 5:
			addAc(-3 * negative);
			break;
		case 6:
			addAc(-3 * negative);
			addMr(3 * negative);
			break;
		case 7:
			addAc(-3 * negative);
			addMr(3 * negative);
			addWater(6 * negative);
			addWind(6 * negative);
			addFire(6 * negative);
			addEarth(6 * negative);
			break;
		case 8:
			addAc(-3 * negative);
			addMr(3 * negative);
			addWater(6 * negative);
			addWind(6 * negative);
			addFire(6 * negative);
			addEarth(6 * negative);
			addOriginalEr(3 * negative);
			break;
		}
	}

	private Timestamp _punishTime;

	public final Timestamp getPunishTime() {
		return _punishTime;
	}

	public final void setPunishTime(final Timestamp timestamp) {
		_punishTime = timestamp;
	}

	@Override
	public final String getViewName() {
		final StringBuffer sbr = new StringBuffer();
		if (isProtector()) {
			sbr.append("**守護者**");
		} else {
			sbr.append(getName());
			if (ConfigAlt.SHOW_SP_TITLE) {
				// 陣營
				if ((get_c_power() != null) && (get_c_power().get_c1_type() != 0)) {
					final String type = get_c_power().get_power().get_c1_name_type();
					sbr.append(type);
				}
				// 色系
				if (get_other().get_color() != 0) {
					sbr.append(get_other().color());
				}
				// 轉生
				if (_meteAbility != null) {
					sbr.append(_meteAbility.getTitle());
				}
			}
		}
		return sbr.toString();
	}

	/*
	 * public final String getOtherViewName() { final StringBuffer sbr = new StringBuffer(); if (isProtector()) { sbr.append("**守護者**"); } else { // 陣營 if ((get_c_power() != null) &&
	 * (get_c_power().get_c1_type() != 0)) { final String type = get_c_power().get_power().get_c1_name_type(); sbr.append(type); } // 色系 if (get_other().get_color() != 0) {
	 * sbr.append(get_other().color()); } sbr.append(getName()); // 轉生 if (_meteAbility != null) { sbr.append(_meteAbility.getTitle()); } } return sbr.toString(); }
	 */

	// 額外魔法傷害 by terry0412
	private int _magicDmgModifier;

	public int getMagicDmgModifier() {
		return _magicDmgModifier;
	}

	public void addMagicDmgModifier(final int i) {
		_magicDmgModifier += i;
	}

	// 魔法傷害減免 by terry0412
	private int _magicDmgReduction;

	public int getMagicDmgReduction() {
		return _magicDmgReduction;
	}

	public void addMagicDmgReduction(final int i) {
		_magicDmgReduction += i;
	}

	// 林德拜爾的弓箭反屏(1/1000)

	private int _elitePlateMail_Lindvior;
	private int _lindvior_dmgmin;
	private int _lindvior_dmgmax;

	/**
	 * 林德拜爾的弓箭反屏(1/1000)
	 * 
	 * @param r
	 */
	public void set_elitePlateMail_Lindvior(final int r, final int dmgmin, final int dmgmax) {
		_elitePlateMail_Lindvior = r;
		_lindvior_dmgmin = dmgmin;
		_lindvior_dmgmax = dmgmax;
	}

	// 魔化黑帝斯的反屏(1/1000)

	private int _elitePlateMail_Hades;
	private int _Hades_dmg;

	/**
	 * 魔化黑帝斯的反屏(1/1000)
	 * 
	 * @param r
	 */
	public void set_elitePlateMail_Hades(final int r, final int dmg) {
		_elitePlateMail_Hades = r;
		_Hades_dmg = dmg;
	}

	/**
	 * 增加狩獵經驗值(%計算)
	 * 
	 * @param Erics4179
	 */
	private int _expPoint;

	public int getExpPoint() {
		return _expPoint;
	}

	public void setExpPoint(final int i) {
		_expPoint = i;
	}

	// 特效編號 (每XX秒出現1次) by terry0412
	private int _effectId;

	public int getEffectId() {
		return _effectId;
	}

	public void setEffectId(final int i) {
		_effectId = i;
	}

	// XX色霸氣 (效果) by terry0412
	private int _value;

	public int getValue() {
		return _value;
	}

	public void setValue(final int i) {
		_value = i;
	}

	private Map<Integer, Integer> _mapsList;

	public final void setMapsList(final HashMap<Integer, Integer> list) {
		_mapsList = list;
	}

	public final int getMapsTime(final int key) {
		if ((_mapsList == null) || !_mapsList.containsKey(key)) {
			return 0;
		}
		return _mapsList.get(key);
	}

	public void putMapsTime(final int key, final int value) {
		if (_mapsList == null) {
			_mapsList = CharMapsTimeReading.get().addTime(getId(), key, value);
		}
		_mapsList.put(key, value);
	}

	// 城堡額外附加能力 by terry0412
	private ArrayList<Integer> _castleAbility;

	public final boolean isCastleAbility(final int value) {
		if (_castleAbility == null) {
			_castleAbility = new ArrayList<Integer>();
		}
		return _castleAbility.contains(Integer.valueOf(value));
	}

	public final void addCastleAbility(final int value) {
		if (_castleAbility == null) {
			_castleAbility = new ArrayList<Integer>();
		}
		_castleAbility.add(Integer.valueOf(value));
	}

	public final void removeCastleAbility(final int value) {
		if (_castleAbility == null) {
			_castleAbility = new ArrayList<Integer>();
		}
		_castleAbility.remove(Integer.valueOf(value));
	}

	private long _shopAdenaRecord;

	public final long getShopAdenaRecord() {
		return _shopAdenaRecord;
	}

	public final void setShopAdenaRecord(final long i) {
		_shopAdenaRecord = i;
	}

	// 幸運值 by terry0412
	private int _luckValue;

	public int getLuckValue() {
		return _luckValue;
	}

	public void setLuckValue(final int i) {
		_luckValue = i;
	}

	// 戒指欄位擴充紀錄 by terry0412
	private byte _ringsExpansion;

	public final byte getRingsExpansion() {
		return _ringsExpansion;
	}

	public final void setRingsExpansion(final byte i) {
		_ringsExpansion = i;
	}
	
	// 飾品開啟欄位判斷
		private int _Slot = 0;

		public void setSlot(final int i) {
			_Slot = i;
		}

		public int getSlot() {
			return _Slot;
		}

	public final int getWeaponType(final int Type1) {
		if (Type1 == ActionCodes.ACTION_ChainswordWalk) {
			if (SprTable.get().containsChainswordSpr(getTempCharGfx())) {
				return ActionCodes.ACTION_ChainswordWalk;

			} else {
				return ActionCodes.ACTION_SpearWalk;
			}
		}
		return Type1;
	}

	private boolean _re_avenger;

	/**
	 * 復仇卷軸
	 * 
	 * @return
	 */
	public boolean is_avenger() {
		return _re_avenger;
	}

	/**
	 * 復仇卷軸
	 * 
	 * @param b
	 */
	public void re_avenger(final boolean b) {
		_re_avenger = b;
	}

	// 淨化藥水 - 允許攻擊BOSS列表 (可對特定BOSS造成傷害) by terry0412
	private List<Integer> _allow_list;

	public final void set_allow_list(final List<Integer> list) {
		_allow_list = list;
	}

	public final boolean check_allow_list(final int bossId) {
		return (_allow_list != null) && _allow_list.contains(bossId);
	}

	// TODO Roy 直接給予 Exp 值
	public synchronized void setExp_Direct(final long i) {
		setExp(i);
		onChangeExp();
	}

	private String _ischeckAI = "";

	public void setCheckAI(final String lg) {
		_ischeckAI = lg;
	}

	public String isCheckAI() {
		return _ischeckAI;
	}

	private int _sum = -1;

	public void setAIsum(final int lg) {
		_sum = lg;
	}

	public int getAIsum() {
		return _sum;
	}

	private int _sec;

	public void setSec(final int sec) {
		_sec = sec;
	}

	public int getSec() {
		return _sec;
	}

	private String _AImsg = "";

	public void setAImsg(final String lg) {
		_AImsg = lg;
	}

	public String getAImsg() {
		return _AImsg;
	}

	private int _error = 3;

	public void addError(final int i) {
		_error += i;
	}

	public void setError(final int i) {
		_error = i;
	}

	/** Etcitem Extra Settings Start */
	private int double_dmg_chance = 0;

	public void set_double_dmg_chance(final int i) {
		double_dmg_chance = i;
	}

	public void add_double_dmg_chance(final int i) {
		double_dmg_chance += i;
	}

	public int get_double_dmg_chance() {
		return double_dmg_chance;
	}

	private int _reduction_dmg = 0;

	public void add_reduction_dmg(final int add) {
		_reduction_dmg += add;
	}

	public int get_reduction_dmg() {
		return _reduction_dmg;
	}

	/** Etcitem Extra Settings End */

	public int getError() {
		return _error;
	}

	private Date _birthday;

	public void setBirthday(final String time) {
		Date date = new Date();
		try {
			date = new SimpleDateFormat("yyyyMMdd").parse(time);
			_birthday = date;
		} catch (final ParseException e) {
			_log.error(e.getLocalizedMessage(), e);
		}

	}

	public Date getBirthDay() {
		return _birthday;
	}

	// weapon skill send control test , default as true
	private boolean _send_weapon_gfxid = true;

	// control set
	public void set_send_weapon_gfxid(final boolean flag) {
		_send_weapon_gfxid = flag;
	}

	// get control
	public boolean is_send_weapon_gfxid() {
		return _send_weapon_gfxid;
	}

	private boolean _is_broadcast = true;

	public void set_broadcast(final boolean flag) {
		_is_broadcast = flag;
	}

	public boolean is_broadcast() {
		return _is_broadcast;
	}

	private boolean _is_ask_teleport = true;

	public void set_ask_teleport(final boolean flag) {
		_is_ask_teleport = flag;
	}

	public boolean is_ask_teleport() {
		return _is_ask_teleport;
	}

	/** 傷害顯示設置 預設關閉 */
	private boolean _is_attack_view = false;

	public void set_is_attack_view(final boolean flag) {
		_is_attack_view = flag;
	}

	public boolean is_attack_view() {
		return _is_attack_view;
	}

	private long _consume_point = 0;

	public void set_consume_point(final long count) {
		_consume_point = count;
	}

	public long get_consume_point() {
		return _consume_point;
	}

	private String _clanMemberNotes; // 血盟成員備註

	public String getClanMemberNotes() {
		return _clanMemberNotes;
	}

	public void setClanMemberNotes(final String s) {
		_clanMemberNotes = s;
	}

	private int _clanMemberId; // 血盟成員Id

	public int getClanMemberId() {
		return _clanMemberId;
	}

	public void setClanMemberId(final int i) {
		_clanMemberId = i;
	}

	private int _vipLevel;

	private Timestamp _startTime;

	private Timestamp _endTime;

	public int get_vipLevel() {
		return _vipLevel;
	}

	public void set_vipLevel(final int vipLevel) {
		_vipLevel = vipLevel;
	}

	private int _giftIndex;

	private boolean _isWaitEnd;

	public int getOnlineGiftIndex() {
		return _giftIndex;
	}

	public void setOnlineGiftIndex(final int onlineGiftIndex) {
		_giftIndex = onlineGiftIndex;
	}

	public boolean isOnlineGiftWiatEnd() {
		return _isWaitEnd;
	}

	public void setOnlineGiftWiatEnd(final boolean onlineGiftWiatEnd) {
		_isWaitEnd = onlineGiftWiatEnd;
	}

	public Timestamp getVipStartTime() {
		return _startTime;
	}

	public void setVipStartTime(final Timestamp vipStartTime) {
		_startTime = vipStartTime;
	}

	public Timestamp getVipEndTime() {
		return _endTime;
	}

	public void setVipEndTime(final Timestamp vipEndTime) {
		_endTime = vipEndTime;
	}

	public void setVipStatus() {
		if ((_startTime != null) && (_endTime != null)) {
			final long t = _endTime.getTime() - System.currentTimeMillis();
			if (t > 0L) {
				final L1Vip tmp = VipSetsTable._list_vip.get(_vipLevel);
				if (tmp != null) {
					addMaxHp(tmp.get_add_hp());
					addHpr(tmp.get_add_hpr());
					addMaxMp(tmp.get_add_mp());
					addMpr(tmp.get_add_mpr());
					addDmgup(tmp.get_add_dmg());
					addBowDmgup(tmp.get_add_bowdmg());
					addHitup(tmp.get_add_hit());
					addBowHitup(tmp.get_add_bowhit());
					addSp(tmp.get_add_sp());
					addMr(tmp.get_add_mr());
				} else {
					this.sendPackets(new S_SystemMessage("VIP能力錯誤，請告知線上GM處理。"));
				}
				sendPackets(new S_VipTime(_vipLevel, _startTime.getTime(), _endTime.getTime()));
				sendPacketsAll(new S_VipShow(getId(), 1));
				sendPackets(new S_OwnCharStatus(this));
				sendPackets(new S_SPMR(this));
				setSkillEffect(L1SkillId.VIP, (int) t);
			} else {
				_startTime = null;
				_endTime = null;
			}
		}
	}

	public void addVipStatus(final int dayCount, final int level) {
		if ((_endTime != null) && ((_endTime.getTime() - System.currentTimeMillis()) > 0L)) {
			removeSkillEffect(L1SkillId.VIP);
		}
		final long t = System.currentTimeMillis();
		_startTime = new Timestamp(t);
		_endTime = new Timestamp(t + (86400000L * dayCount));
		_vipLevel = level;

		setVipStatus();
	}

	public void endVipStatus() {
		final L1Vip tmp = VipSetsTable._list_vip.get(_vipLevel);
		if (tmp != null) {
			addMaxHp(-tmp.get_add_hp());
			addHpr(-tmp.get_add_hpr());
			addMaxMp(-tmp.get_add_mp());
			addMpr(-tmp.get_add_mpr());
			addDmgup(-tmp.get_add_dmg());
			addBowDmgup(-tmp.get_add_bowdmg());
			addHitup(-tmp.get_add_hit());
			addBowHitup(-tmp.get_add_bowhit());
			addSp(-tmp.get_add_sp());
			addMr(-tmp.get_add_mr());
		} else {
			this.sendPackets(new S_SystemMessage("VIP能力錯誤，請告知線上GM處理。"));
		}

		sendPackets(new S_VipTime(0, 0L, 0L));
		sendPacketsAll(new S_VipShow(getId(), 0));
		sendPackets(new S_OwnCharStatus(this));
		sendPackets(new S_SPMR(this));

		_startTime = null;
		_endTime = null;
		_vipLevel = 0;
	}

	private final L1DwarfForGameMallInventry _dwarfForMALL;

	public L1DwarfForGameMallInventry getDwarfForGameMall() {
		return _dwarfForMALL;
	}

	public void updateGameMallMoney() {
		long money = 0;
		if (Config.ISPOINT) {
			money = AccountReading.get().getPoints(getAccountName());
		} else {
			final L1ItemInstance moneyItem = getInventory().checkItemX(44070, 1);
			money = moneyItem == null ? 0 : moneyItem.getCount();
		}
		sendPackets(new S_GameMallItemMoney(money));
	}

	public void sendPackets(final ArrayList<ServerBasePacket> packs) {
		if (getNetConnection() == null) {
			return;
		}
		try {
			int i = 0;
			for (final int length = packs.size(); i < length; i++) {
				sendPackets(packs.get(i));
			}

		} catch (final Exception e) {
			logout();
			close();
		}
	}

	/**
	 * 設置攻擊距離
	 */
	public void getWeaponRange() {
		boolean check = false;
		int range = 1;
		int type = 1;
		final L1ItemInstance weapon = getWeapon();
		if (weapon == null) {
			sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 0, check));
		} else {
			if (weapon.getItem().getType() == 4) { // 弓(雙手)
				range = 17;
			} else if ((weapon.getItem().getType() == 10) // 鐵手甲
					|| (weapon.getItem().getType() == 13)) { // 弓(單手)
				range = 14;
			} else if ((weapon.getItem().getType() == 5) // 矛(雙手)
					|| (weapon.getItem().getType() == 14) // 矛(單手)
					|| (weapon.getItem().getType() == 18)) { // 鎖鏈劍(單手)
				range = 1;
				final int polyId = getTempCharGfx();
				if ((polyId == 11330) || (polyId == 11344) || (polyId == 11351) || (polyId == 11368) || (polyId == 12240) || (polyId == 12237) || (polyId == 11447) || (polyId == 11408)
						|| (polyId == 11409) || (polyId == 11410) || (polyId == 11411) || (polyId == 11418) || (polyId == 15531) || (polyId == 15832) || (polyId == 15833)
						|| (polyId == 15539) || (polyId == 15537) || (polyId == 15534) || (polyId == 15599) || (polyId == 13152) || (polyId == 13153) || (polyId == 12681)
						|| (polyId == 12702) || (polyId == 11419) || (polyId == 12613) || (polyId == 12614) || (polyId == 12613) || (polyId == 13715) || (polyId == 13717)
						|| (polyId == 13735) || (polyId == 13737) || (polyId == 14491) || (polyId == 15483) || (polyId == 16056) || (polyId == 16008) || (polyId == 13719)
						|| (polyId == 13721) || (polyId == 13723) || (polyId == 13725) || (polyId == 13727) || (polyId == 13729) || (polyId == 17545) || (polyId == 12613)
						|| (polyId == 17549) || (polyId == 13743) || (polyId == 13745) || (polyId == 12614) || (polyId == 16614)) {
					range = 2;
				} else if (!hasSkillEffect(SHAPE_CHANGE)) {
					range = 2;
				}
			}

			if (isKnight()) {
				if (weapon.getItem().getType() == 3) { // 雙手劍(雙手)
					check = true;
				}
			} else if (isElf()) {
				if (hasSkillEffect(DANCING_BLAZE)) {
					check = true;
				}
				if (((weapon.getItem().getType() == 4) // 弓(雙手)
						|| (weapon.getItem().getType() == 13)) // 弓(單手)
						&& (weapon.getItem().getType1() == 20)) { // 弓
					type = 3;
					check = true;
				}
			} else if (isDragonKnight()) {
				check = true;
				if ((weapon.getItem().getType() == 14) // 矛(單手)
						|| (weapon.getItem().getType() == 18)) { // 鎖鏈劍(單手)
					type = 10;
				}
			}

			if ((weapon.getItem().getType1() != 20) && (weapon.getItem().getType1() != 62)) {
				sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, type, check));
			} else {
				sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 3, check));
			}
			setRange(range);
		}
	}

	public int getPolyStatus() {
		int poly = 0;
		if (getLevel() <= 29) {
			poly = 0;
		} else if ((getLevel() >= 30) && (getLevel() <= 44)) {
			poly = 1;
		} else if ((getLevel() >= 45) && (getLevel() <= 49)) {
			poly = 2;
		} else if (getLevel() == 50) {
			poly = 3;
		} else if (getLevel() == 51) {
			poly = 4;
		} else if ((getLevel() >= 52) && (getLevel() <= 54)) {
			poly = 5;
		} else if ((getLevel() >= 55) && (getLevel() <= 59)) {
			poly = 6;
		} else if ((getLevel() >= 60) && (getLevel() <= 64)) {
			poly = 7;
		} else if ((getLevel() >= 65) && (getLevel() <= 69)) {
			poly = 8;
		} else if ((getLevel() >= 70) && (getLevel() <= 74)) {
			poly = 9;
		} else if ((getLevel() >= 75) && (getLevel() <= 79)) {
			poly = 10;
		} else if (getLevel() >= 80) {
			poly = 11;
		}
		return poly;
	}

	private int[] _gfxids;

	private int _times;

	public void set_armorsets_gfx(final int[] _gfxids) {
		this._gfxids = _gfxids;
	}

	public int[] get_armorsets_gfx() {
		return _gfxids;
	}

	public void set_gfx_times(final int _times) {
		this._times = _times;
	}

	public int get_gfx_times() {
		return _times;
	}

	private L1ItemInstance _weaponWarrior;

	public L1ItemInstance getWeaponWarrior() {
		return _weaponWarrior;
	}

	public void setWeaponWarrior(final L1ItemInstance weapon) {
		_weaponWarrior = weapon;
	}

	public int colcTitanDmg() {
		if (getWeapon() == null) {
			return 0;
		}
		L1ItemInstance weapon = getWeapon();
		// 7.0 warrior slayer
		if ((getWeaponWarrior() != null) && is_change_weapon()) {
			weapon = getWeaponWarrior();
		}
		int dmg = weapon.getItem().getDmgLarge();
		dmg += weapon.getItem().getDmgModifier();
		dmg += weapon.getEnchantLevel();
		dmg *= 2;
		return dmg;
	}

	public boolean isCrystal() {
		if (getInventory().consumeItem(41246, 10)) {
			return true;
		}
		return false;
	}

	public boolean isCRASH() {
		if (isSkillMastery(PASSIVE_CRASH)) {
			return true;
		}
		return false;
	}

	public boolean isFURY() {
		if (isSkillMastery(PASSIVE_FURY)) {
			return true;
		}
		return false;
	}

	public boolean isSLAYER() {
		if (isSkillMastery(PASSIVE_SLAYER)) {
			return true;
		}
		return false;
	}

	public boolean isTITANROCK() {
		if (isSkillMastery(PASSIVE_TITANROCK)) {
			return true;
		}
		return false;
	}

	public boolean isTITANBULLET() {
		if (isSkillMastery(PASSIVE_TITANBULLET)) {
			return true;
		}
		return false;
	}

	public boolean isARMORGARDE() {
		if (isSkillMastery(PASSIVE_ARMORGARDE)) {
			return true;
		}
		return false;
	}

	public boolean isTITANMAGIC() {
		if (isSkillMastery(PASSIVE_TITANMAGIC)) {
			return true;
		}
		return false;
	}

	private boolean _change_weapon = false;

	public boolean is_change_weapon() {
		return _change_weapon;
	}

	public void set_change_weapon(final boolean flag) {
		_change_weapon = flag;
	}

	private int _giganticHp;

	public int getGiganticHp() {
		return _giganticHp;
	}

	public void setGiganticHp(final int i) {
		_giganticHp = i;
	}

	private String _polyname = "";

	public void setpolyname(final String name) {
		_polyname = name;
	}

	public String getpolyname() {
		return _polyname;
	}

	public L1AttackThread _attackthread;

	public L1AttackThread getAttackThread() {
		return _attackthread;
	}

	public void setAttackThread(final L1AttackThread thread) {
		_attackthread = thread;
	}

	private int _attacktargetid;

	public void setAttackTargetId(final int objid) {
		_attacktargetid = objid;
	}

	public int getAttackTargetId() {
		return _attacktargetid;
	}

	private int _range = 0;

	public void setRange(final int range) {
		_range = range;
	}

	public int getRange() {
		return _range;
	}

	public int getLastConsume(final int dayCount) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int sumConsume = 0;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT SUM(sumPrice) as sumConsume " + "FROM t_game_mall_log WHERE objId=? and buyTime >= ?;");
			pstm.setInt(1, getId());
			pstm.setString(2, _sdf.format(new Timestamp(System.currentTimeMillis() - (86400000L * dayCount))));
			rs = pstm.executeQuery();
			if (rs.next()) {
				sumConsume = rs.getInt("sumConsume");
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return sumConsume;
	}

	// 物理傷害增加+% by terry0412
	private int _physicsDmgUp;

	public final int getPhysicsDmgUp() {
		return _physicsDmgUp;
	}

	public final void addPhysicsDmgUp(final int i) {
		_physicsDmgUp += i;
	}

	// 魔法傷害增加+% by terry0412
	private int _magicDmgUp;

	public final int getMagicDmgUp() {
		return _magicDmgUp;
	}

	public final void addMagicDmgUp(final int i) {
		_magicDmgUp += i;
	}

	// 物理傷害減免+% by terry0412
	private int _physicsDmgDown;

	public final int getPhysicsDmgDown() {
		return _physicsDmgDown;
	}

	public final void addPhysicsDmgDown(final int i) {
		_physicsDmgDown += i;
	}

	// 魔法傷害減免+% by terry0412
	private int _magicDmgDown;

	public final int getMagicDmgDown() {
		return _magicDmgDown;
	}

	public final void addMagicDmgDown(final int i) {
		_magicDmgDown += i;
	}

	// 有害魔法成功率+% by terry0412
	private int _magicHitUp;

	public final int getMagicHitUp() {
		return _magicHitUp;
	}

	public final void addMagicHitUp(final int i) {
		_magicHitUp += i;
	}

	// 抵抗有害魔法成功率+% by terry0412
	private int _magicHitDown;

	public final int getMagicHitDown() {
		return _magicHitDown;
	}

	public final void addMagicHitDown(final int i) {
		_magicHitDown += i;
	}

	// 魔法暴擊發動機率+% (發動後技能傷害*1.5倍) by terry0412
	private int _magicDoubleHit;

	public final int getMagicDoubleHit() {
		return _magicDoubleHit;
	}

	public final void addMagicDoubleHit(final int i) {
		_magicDoubleHit += i;
	}

	// 物理暴擊發動機率+% (發動後普攻傷害*1.5倍) by terry0412
	private int _physicsDoubleHit;

	public final int getPhysicsDoubleHit() {
		return _physicsDoubleHit;
	}

	public final void addPhysicsDoubleHit(final int i) {
		_physicsDoubleHit += i;
	}

	// 新增幸運度 Erics4179 160901
	private int _InfluenceLuck;

	public final int getInfluenceLuck() {
		return _InfluenceLuck;
	}

	public final void addInfluenceLuck(final int i) {
		_InfluenceLuck += i;
	}

	public void sendDetails() {
		// XXX 7.6 ADD
		this.sendPackets(new S_PacketBoxCharEr(this));// 角色迴避率更新

		// XXX 7.6 能力基本資訊-力量
		this.sendPackets(new S_StrDetails(2, L1ClassFeature.calcStrDmg(this.getStr(), this.getBaseStr()), L1ClassFeature.calcStrHit(this.getStr(), this.getBaseStr()), L1ClassFeature
				.calcStrDmgCritical(this.getStr(), this.getBaseStr()), L1ClassFeature.calcAbilityMaxWeight(this.getStr(), this.getCon())));

		// XXX 7.6 重量程度資訊
		this.sendPackets(new S_WeightStatus(this.getInventory().getWeight100(), this.getInventory().getWeight(), (int) this.getMaxWeight()));

		// XXX 7.6 能力基本資訊-智力
		this.sendPackets(new S_IntDetails(2, L1ClassFeature.calcIntMagicDmg(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicHit(this.getInt(), this.getBaseInt()),
				L1ClassFeature.calcIntMagicCritical(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicBonus(this.getType(), this.getInt()), L1ClassFeature
						.calcIntMagicConsumeReduction(this.getInt())));

		// XXX 7.6 能力基本資訊-精神
		this.sendPackets(new S_WisDetails(2, L1ClassFeature.calcWisMpr(this.getWis(), this.getBaseWis()), L1ClassFeature.calcWisPotionMpr(this.getWis(), this.getBaseWis()), L1ClassFeature
				.calcStatMr(this.getWis()) + L1ClassFeature.newClassFeature(this.getType()).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(this.getType(), this.getBaseWis())));

		// XXX 7.6 能力基本資訊-敏捷
		this.sendPackets(new S_DexDetails(2, L1ClassFeature.calcDexDmg(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexHit(this.getDex(), this.getBaseDex()), L1ClassFeature
				.calcDexDmgCritical(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexAc(this.getDex()), L1ClassFeature.calcDexEr(this.getDex())));

		// XXX 7.6 能力基本資訊-體質
		this.sendPackets(new S_ConDetails(2, L1ClassFeature.calcConHpr(this.getCon(), this.getBaseCon()), L1ClassFeature.calcConPotionHpr(this.getCon(), this.getBaseCon()), L1ClassFeature
				.calcAbilityMaxWeight(this.getStr(), this.getCon()), L1ClassFeature.calcBaseClassLevUpHpUp(this.getType())
				+ L1ClassFeature.calcBaseConLevUpExtraHpUp(this.getType(), this.getBaseCon())));

		// XXX 7.6 重量程度資訊
		this.sendPackets(new S_WeightStatus(this.getInventory().getWeight() * 100 / (int) this.getMaxWeight(), this.getInventory().getWeight(), (int) this.getMaxWeight()));

		// XXX 7.6 純能力詳細資訊 階段:25
		this.sendPackets(new S_BaseAbilityDetails(25));

		// XXX 7.6 純能力詳細資訊 階段:35
		this.sendPackets(new S_BaseAbilityDetails(35));

		// XXX 7.6 純能力詳細資訊 階段:45
		this.sendPackets(new S_BaseAbilityDetails(45));

		// XXX 7.6 純能力資訊
		this.sendPackets(new S_BaseAbility(this.getBaseStr(), this.getBaseInt(), this.getBaseWis(), this.getBaseDex(), this.getBaseCon(), this.getBaseCha()));

		// XXX 7.6 萬能藥使用數量
		this.sendPackets(new S_ElixirCount(this.getElixirStats()));
	}

	private L1Kill_Npc_Quest _kill_npc_Quest; // 紀錄殺怪次數

	public L1Kill_Npc_Quest get_kill_npc_Quest() {
		return _kill_npc_Quest;
	}

	Timestamp _clanJoinTime;

	/** 加入血盟時間 */
	public Timestamp getClanJoinTime() {
		return _clanJoinTime;
	}

	/** 加入血盟時間 */
	public void setClanJoinTime(Timestamp _clanJoinTime) {
		this._clanJoinTime = _clanJoinTime;
	}

	private int _other_ReductionDmg = 0;

	public int getother_ReductionDmg() {
		return _other_ReductionDmg;
	}

	public void setother_ReductionDmg(final int i) {
		_other_ReductionDmg = i;
	}

	public void addother_ReductionDmg(final int i) {
		this._other_ReductionDmg += i;
	}

	private int _PVPdmg = 0;

	public int getPVPdmg() {
		return this._PVPdmg;
	}

	public void setPVPdmg(final int i) {
		this._PVPdmg = i;
	}

	public void addPVPdmg(final int i) {
		this._PVPdmg += i;
	}

	private int _PVPdmgReduction = 0;

	public int getPVPdmgReduction() {
		return this._PVPdmgReduction;
	}

	public void setPVPdmgReduction(final int i) {
		this._PVPdmgReduction = i;
	}

	public void addPVPdmgReduction(final int i) {
		this._PVPdmgReduction += i;
	}

	/**
	 * 經驗值增加
	 */
	private double _expRateToPc = 0.0;

	public void addExpRateToPc(final int s) {
		if (s > 0) {
			_expRateToPc = DoubleUtil.sum(_expRateToPc, s / 100D);
		} else
			_expRateToPc = DoubleUtil.sub(_expRateToPc, s * -1 / 100D);
	}

	public double getExpRateToPc() {
		if (_expRateToPc < 0.0D) {
			return 0.0D;
		} else {
			return _expRateToPc;
		}
	}

	private boolean _showWearingCloudsArrows = true;

	public boolean get_showWearingCloudsArrows() {
		return this._showWearingCloudsArrows;
	}

	public void set_showWearingCloudsArrows(final boolean i) {
		this._showWearingCloudsArrows = i;
	}

	// ■■■■■■■■■■■■■■■■■■■■■■■■■■內掛專用■■■■■■■■■■■■■■■■■■■■■■■■■■■
	private int Atktimer = 30;
	private boolean onAtktimer = false;

	public void upAtktimer(int i) {
		Atktimer -= i;
		if (Atktimer == 0)
			setonAtktimer(true);
	}

	public int getAtktimer() {
		return Atktimer;
	}

	public boolean getonAtktimer() {
		return onAtktimer;
	}

	public void setonAtktimer(boolean is) {
		onAtktimer = is;
	}
	
	private ArrayList<Integer> _autoattack = new ArrayList<Integer>();

	/**
	 * 檢查是否已經存在內掛自動施放技能
	 * 
	 * @param id
	 * @return
	 */
	public boolean isAttackSkillList(Integer id) {
		return _autoattack.contains(new Integer(id));
	}

	/**
	 * 查詢目前已經紀錄技能數量
	 * 
	 * @return
	 */
	public int AttackSkillSize() {
		return _autoattack.size();
	}

	/**
	 * 傳回隨機技能id
	 * 
	 * @return
	 */
	public int AttackSkillId() {
		int id = (_random.nextInt(_autoattack.size()));
		if (id == _autoattack.size()) {
			id--;
		}
		int skillid = _autoattack.get(id);
		return skillid;
	}

	/**
	 * 清空內掛自動施放技能陣列
	 */
	public void clearAttackSkillList() {
		_autoattack.clear();
	}

	/**
	 * 加入內掛自動施放技能陣列
	 * 
	 * @param id
	 */
	public void setAttackSkillList(Integer id) {
		if (!_autoattack.contains(new Integer(id)))
			_autoattack.add(new Integer(id));
	}

	public ArrayList<Integer> AttackSkillList() {
		return _autoattack;
	}
	
	/**
	 * 詳細設置種類
	 * 
	 * @author hpc20207
	 */
	private int _settype = 0;

	/**
	 * get 詳細設置種類
	 * 
	 * @return 1 開怪MP<br>
	 *         2 開怪CD<br>
	 *         3 技能MP<br>
	 *         4 技能CD<br>
	 *         5 範圍技能<br>
	 *         6 範圍怪物數量<br>
	 *         7 範圍MP<br>
	 *         8 範圍CD<br>
	 *         9 購買藥劑名稱<br>
	 *         10 購買藥劑數量<br>
	 *         11 購買加速藥劑名稱<br>
	 *         12 購買加速藥劑數量<br>
	 *         13 購買二段加速藥劑名稱<br>
	 *         14 購買二段加速藥劑數量<br>
	 *         15 購買三段加速藥劑名稱<br>
	 *         16 購買三段加速藥劑數量<br>
	 */
	public int getInfoType() {
		return _settype;
	}

	/**
	 * set 詳細設置種類
	 * 
	 * @return 1 開怪MP<br>
	 *         2 開怪CD<br>
	 *         3 技能MP<br>
	 *         4 技能CD<br>
	 *         5 範圍技能<br>
	 *         6 範圍怪物數量<br>
	 *         7 範圍MP<br>
	 *         8 範圍CD<br>
	 *         9 購買藥劑名稱<br>
	 *         10 購買藥劑數量<br>
	 *         11 購買加速藥劑名稱<br>
	 *         12 購買加速藥劑數量<br>
	 *         13 購買二段加速藥劑名稱<br>
	 *         14 購買二段加速藥劑數量<br>
	 *         15 購買三段加速藥劑名稱<br>
	 *         16 購買三段加速藥劑數量<br>
	 */
	public void setInfoType(final int i) {
		_settype = i;
	}
	
	private int _ContAttack = 0;

	/** 連續攻擊次數 */
	public int get_Add_ContAttack() {
		return (_ContAttack > 3 ? 3 : _ContAttack);
	}

	/** 連續攻擊次數 */
	public void add_Add_ContAttack(final int i) {
		_ContAttack += i;
	}

	private int _ContAttackRnd = 0;

	/** 連續攻擊機率 */
	public int get_Add_ContAttackRnd() {
		return _ContAttackRnd;
	}

	/** 連續攻擊機率 */
	public void add_Add_ContAttackRnd(final int i) {
		_ContAttackRnd += i;
	}
	
	/** 仇人陣列 */
	private ArrayList<String> _attackenemy = new ArrayList<String>();
	
	/**
	 * 是否在已知仇人陣列內
	 * 
	 * @param id
	 *            = 輸入的玩家名稱
	 * @return true存在 false不存在
	 */
	public boolean isInEnemyList(String id) {
		return _attackenemy.contains(new String(id));
	}

	/** 返回仇人陣列 ArrayList<String> */
	public ArrayList<String> InEnemyList() {
		return _attackenemy;
	}

	/** 清空仇人陣列 */
	public void clearInEnemyList() {
		_attackenemy.clear();
	}

	/** 被攻擊名單陣列 */
	private ArrayList<String> _beattacked = new ArrayList<String>();

	/**
	 * 加入被攻擊陣列
	 * 
	 * @param id
	 *            = 輸入的玩家名稱
	 */
	public void setInAtkList(String id) {
		if (!_beattacked.contains(new String(id)))
			_beattacked.add(new String(id));
	}

	/**
	 * 是否在被攻擊陣列內
	 * 
	 * @param id
	 *            = 輸入的玩家名稱
	 * @return true存在 false不存在
	 */
	public boolean isInAtkList(String id) {
		return _beattacked.contains(new String(id));
	}

	/** 清空被攻擊陣列陣列 */
	public void clearInAtkList() {
		_beattacked.clear();
	}
	
	/** 掛機專用PVP狀態 */
	boolean _AutoPvP = false;

	/**
	 * 掛機專用PvP檢測
	 * 
	 * @param pc
	 * @return
	 */
	public boolean getAutoPvP() {
		return _AutoPvP;
	}

	/**
	 * 設置掛機專用PvP狀態
	 * 
	 * @param pc
	 * @return
	 */
	public void setAutoPvP(final boolean i) {
		_AutoPvP = i;
	}
	
	/**
	 * 查詢目前已經紀錄技能數量
	 * 
	 * @return
	 */
	public int BuffSkillSize() {
		return _autobuff.size();
	}

	/**
	 * 清空內掛自動施放技能陣列
	 */
	public void clearBuffSkillList() {
		_autobuff.clear();
	}

	/**
	 * 加入內掛自動施放技能陣列
	 * 
	 * @param id
	 */
	public void setBuffSkillList(Integer id) {
		if (!_autobuff.contains(new Integer(id)))
			_autobuff.add(new Integer(id));
	}
	
	private boolean _istel = false;

		/** 是否內掛中使用順移 */
		public boolean Istel() {
			return _istel;
		}

		/** 是否內掛中使用順移 */
		public void settel(boolean b) {
			_istel = b;
		}
	
	ArrayList<Node> _parentList = new ArrayList<Node>();

	public void add_parentList(final Node node) {
		_parentList.add(node);
	}

	public void clear_parentList() {
		_parentList.clear();
	}

	public void re_parentList() {
		Collections.reverse(_parentList);
		_parentList.remove(0);
	}

	public ArrayList<Node> get_parentList() {
		return _parentList;
	}

	public int get_parentListSize() {
		return _parentList.size();
	}

	public void remove_parentListfirst() {
		_parentList.remove(0);
	}

	public Node get_parentListfirst() {
		return _parentList.get(0);
	}

	public Node get_autoparent() {
		Node temp = _parentList.get(0);
		_parentList.remove(0);
		return temp;
	}
	
	private ArrayList<Integer> _autobuff = new ArrayList<Integer>();

	/**
	 * 檢查是否已經存在內掛自動施放技能
	 * 
	 * @param id
	 * @return
	 */
	public boolean isBuffSkillList(Integer id) {
		return _autobuff.contains(new Integer(id));
	}

	public ArrayList<Integer> BuffSkillList() {
		return _autobuff;
	}
	
	private int _resetautosec = 0;

	/**
	 * 內掛幾分鐘後進行線程重置
	 * 
	 * @return
	 */
	public int getRestartAuto() {
		return _resetautosec;
	}

	/**
	 * 內掛幾分鐘後進行線程重置
	 * 
	 * @param i
	 */
	public void setRestartAuto(int i) {
		_resetautosec = i;
	}

	private int _resetautostartsec = 0;

	/**
	 * 內掛重置處理秒數
	 * 
	 * @return
	 */
	public int getRestartAutoStartSec() {
		return _resetautostartsec;
	}

	/**
	 * 內掛重置處理秒數
	 * 
	 * @param i
	 */
	public void setRestartAutoStartSec(int i) {
		_resetautostartsec = i;
	}

	private boolean _isauto = false;

	/**
	 * 是否開啟自動練功
	 * 
	 * @return
	 */
	public boolean IsAuto() {
		return _isauto;
	}

	/**
	 * 是否開啟自動練功
	 * 
	 * @param b
	 */
	public void setIsAuto(boolean b) {
		_isauto = b;
	}

	/** 範圍技能間隔 by hpc20207 */
	private long _rngskill_cd = 1;

	/** get 範圍技能間隔 */
	public long getRngskill_cd() {
		return _rngskill_cd;
	}

	/** set 範圍技能間隔 */
	public void setRngskill_cd(final int i) {
		_rngskill_cd = i;
	}

	/** 範圍技能MP百分比 by hpc20207 */
	private int _rngskill_mp = 0;

	/** get 範圍技能MP */
	public int getRngskill_mp() {
		return _rngskill_mp;
	}

	/** set 範圍技能MP */
	public void setRngskill_mp(final int i) {
		_rngskill_mp = i;
	}

	/** 範圍技能 判斷幾格 by hpc20207 */
	private int _rngskill_rng = 0;

	/** get 範圍技能判斷幾格 */
	public int getRngskill_rng() {
		return _rngskill_rng;
	}

	/** set 範圍技能判斷幾格 */
	public void setRngskill_rng(final int i) {
		_rngskill_rng = i;
	}

	/** 範圍技能怪物數量 by hpc20207 */
	private int _rngskill_mob = 0;

	/** get 範圍技能怪物數量 */
	public int getRngskill_mob() {
		return _rngskill_mob;
	}

	/** set 範圍技能怪物數量 */
	public void setRngskill_mob(final int i) {
		_rngskill_mob = i;
	}

	/** 範圍技能距離 by hpc20207 */
	private int _rngskill_skillrng = 0;

	/** get 範圍技能距離 */
	public int get_rngskill_skillrng() {
		return _rngskill_skillrng;
	}

	/** set 範圍技能距離 */
	public void set_rngskill_skillrng(final int i) {
		_rngskill_skillrng = i;
	}

	/** 攻擊技能 by hpc20207 */
	private int _atkskill_id = 0;

	/** get 攻擊技能 */
	public int getAtkskill_id() {
		return _atkskill_id;
	}

	/** set 攻擊技能 */
	public void setAtkskill_id(final int i) {
		_atkskill_id = i;
	}

	/** get 攻擊技能間隔 */
	public long getAtkskill_cd() {
		return _atkskill_cd;
	}

	/** set 攻擊技能間隔 */
	public void setAtkskill_cd(final int i) {
		_atkskill_cd = i;
	}

	/** 攻擊技能MP百分比 by hpc20207 */
	private int _atkskill_mp = 0;

	/** get 攻擊技能 */
	public int getAtkskill_mp() {
		return _atkskill_mp;
	}

	/** set 攻擊技能 */
	public void setAtkskill_mp(final int i) {
		_atkskill_mp = i;
	}

	/** 攻擊技能距離 by hpc20207 */
	private int _atkskill_rng = 0;

	/** get 攻擊技能距離 */
	public int getAtkskill_rng() {
		return _atkskill_rng;
	}

	/** set 攻擊技能距離 */
	public void setAtkskill_rng(final int i) {
		_atkskill_rng = i;
	}

	/** 範圍技能 by hpc20207 */
	private int _rngskill_id = 0;

	/** get 範圍技能 */
	public int getRngskill_id() {
		return _rngskill_id;
	}

	/** set 範圍技能 */
	public void setRngskill_id(final int i) {
		_rngskill_id = i;
	}

	/**
	 * 技能設置種類
	 * 
	 * @author hpc20207
	 */
	private int _setSkillType = 0;

	/**
	 * get 技能設置頁回傳種類
	 * 
	 * @return 1 開怪<br>
	 *         2 攻擊<br>
	 *         3 範圍<br>
	 *         4 輔助<br>
	 */
	public int getSkillType() {
		return _setSkillType;
	}

	/**
	 * set 技能設置頁回傳種類
	 * 
	 * @return 1 開怪<br>
	 *         2 攻擊<br>
	 *         3 範圍<br>
	 *         4 輔助<br>
	 */
	public void setSkillType(final int i) {
		_setSkillType = i;
	}

	/** 開怪技能 by hpc20207 */
	private int _openskill_id = 0;

	/** get 開怪技能 */
	public int getOpenskill_id() {
		return _openskill_id;
	}

	/** set 開怪技能 */
	public void setOpenskill_id(final int i) {
		_openskill_id = i;
	}

	/** 開怪技能距離 by hpc20207 */
	private int _openskill_rng = 0;

	/** get 開怪技能距離 */
	public int getOpenskill_rng() {
		return _openskill_rng;
	}

	/** set 開怪技能距離 */
	public void setOpenskill_rng(final int i) {
		_openskill_rng = i;
	}

	/** 開怪技能MP百分比 by hpc20207 */
	private int _openskill_mp = 0;

	/** get 開怪技能MP */
	public int getOpenskill_mp() {
		return _openskill_mp;
	}

	/** set 開怪技能MP */
	public void setOpenskill_mp(final int i) {
		_openskill_mp = i;
	}

	/** 開怪技能間隔 by hpc20207 */
	private long _openskill_cd = 1;

	/** get 開怪技能間隔 */
	public long getOpenskill_cd() {
		return _openskill_cd;
	}

	/** set 開怪技能間隔 */
	public void setOpenskill_cd(final int i) {
		_openskill_cd = i;
	}

	private int _openskill_timer = 1; // 開怪時間軸
	private boolean _openskill_implement = false; // 開怪開關

	public int get_openskill_timer() {
		return _openskill_timer;
	}

	public void init_openskill_timer() {
		_openskill_timer = (int) _openskill_cd;
	}

	public void up_openskill_timer(final int i) {
		_openskill_timer -= i;
		if (_openskill_timer == 0) {
			set_openskill_implement(true);
		}
	}

	public boolean get_openskill_implement() {
		return _openskill_implement;
	}

	public void set_openskill_implement(final boolean i) {
		_openskill_implement = i;
		if (i == false)
			init_openskill_timer();
	}

	private int _atkskill_timer = 1; // 攻擊技能時間軸
	private boolean _atkskill_implement = false; // 攻擊技能開關

	public int get_atkskill_timer() {
		return _atkskill_timer;
	}

	/** 攻擊技能間隔 by hpc20207 */
	private long _atkskill_cd = 1;

	public void init_atkskill_timer() {
		_atkskill_timer = (int) _atkskill_cd;
	}

	public void up_atkskill_timer(final int i) {
		_atkskill_timer -= i;
		if (_atkskill_timer == 0) {
			set_atkskill_implement(true);
		}
	}

	public boolean get_atkskill_implement() {
		return _atkskill_implement;
	}

	public void set_atkskill_implement(final boolean i) {
		_atkskill_implement = i;
		if (i == false)
			init_atkskill_timer();
	}

	private int _Rngskill_timer = 1; // 範圍技能時間軸
	private boolean _Rngskill_implement = false; // 範圍技能開關

	public int get_Rngskill_timer() {
		return _Rngskill_timer;
	}

	public void init_Rngskill_timer() {
		_Rngskill_timer = (int) _rngskill_cd;
	}

	public void up_Rngskill_timer(final int i) {
		_Rngskill_timer -= i;
		if (_Rngskill_timer == 0) {
			set_Rngskill_implement(true);
		}
	}

	public boolean get_Rngskill_implement() {
		return _Rngskill_implement;
	}

	public void set_Rngskill_implement(final boolean i) {
		_Rngskill_implement = i;
		if (i == false)
			init_Rngskill_timer();
	}

	/** 掛機頁面暫存 */
	String _TalkPage = "home";

	/** get 掛機頁面暫存 用於返回調用 */
	public String getTalkPage() {
		return _TalkPage;
	}

	/** set 掛機頁面暫存 用於返回調用 */
	public void setTalkPage(final String i) {
		_TalkPage = i;
	}

	private int _ornge = 0;

	/** 定點巡邏範圍 */
	public int getOutRange() {
		return _ornge;
	}

	/** 定點巡邏範圍增加 */
	public void addOutRange(int i) {
		_ornge += i;
		if (_ornge > 50) {
			_ornge = 50;
		}
	}

	/** 定點巡邏範圍減少 */
	public void minusOutRange(int i) {
		_ornge -= i;
		if (_ornge < 0) {
			_ornge = 0;
		}
	}

	private int _orx = 0;

	/** 範圍定點回練功點X */
	public int getOutReturnX() {
		return _orx;
	}

	/** 範圍定點回練功點X */
	public void setOutReturnX(int i) {
		_orx = i;
	}

	private int _ory = 0;

	/**
	 * 範圍定點回練功點X
	 * 
	 * @return
	 */
	public int getOutReturnY() {
		return _ory;
	}

	/*
	 * 範圍定點回練功點X
	 * 
	 * @param i
	 */
	public void setOutReturnY(int i) {
		_ory = i;
	}

	private int _Orm = 0;

	/**
	 * 範圍定點練功點map
	 * 
	 * @return
	 */
	public int getOutReturnMap() {
		return _Orm;
	}

	/**
	 * 範圍定點練功點map
	 * 
	 * @param i
	 */
	public void setOutReturnMap(int i) {
		_Orm = i;
	}

	// ------新增定點內掛定點巡航 END

	private boolean _buyteleport = false;

	/**
	 * 內掛自動購買瞬移
	 * 
	 * @return
	 */
	public boolean IsBuyTeleport() {
		return _buyteleport;
	}

	/**
	 * 內掛自動購買瞬移
	 * 
	 * @param b
	 */
	public void setBuyTeleport(boolean b) {
		_buyteleport = b;
	}

	private boolean _buyarrow = false;

	/**
	 * 內掛自動購買箭
	 * 
	 * @return
	 */
	public boolean IsBuyArrow() {
		return _buyarrow;
	}

	/**
	 * 內掛自動購買箭
	 * 
	 * @param b
	 */
	public void setBuyArrow(boolean b) {
		_buyarrow = b;
	}

	private boolean _teleportauto = false;

	/**
	 * 內掛_無目標順移
	 * 
	 * @return
	 */
	public boolean IsTeleportAuto() {
		return _teleportauto;
	}

	/**
	 * 內掛_無目標順移
	 * 
	 * @param b
	 */
	public void setTeleportAuto(boolean b) {
		_teleportauto = b;
	}

	private boolean _attackteleport = false;

	/**
	 * 是否開啟內掛被攻擊瞬移狀態
	 * 
	 * @return
	 */
	public boolean IsAttackTeleport() {
		return _attackteleport;
	}

	/**
	 * 是否開啟內掛被攻擊瞬移狀態
	 * 
	 * @param b
	 */
	public void setIsAttackTeleport(boolean b) {
		_attackteleport = b;
	}

	private boolean _attackteleporthp = false;

	/**
	 * 內掛被玩家攻擊瞬移
	 * 
	 * @return
	 */
	public boolean IsAttackTeleportHp() {
		return _attackteleporthp;
	}

	/**
	 * 內掛被玩家攻擊瞬移
	 * 
	 * @param b
	 */
	public void setIsAttackTeleportHp(boolean b) {
		_attackteleporthp = b;
	}

	private ArrayList<Integer> _targetId = new ArrayList<Integer>();

	// private HashMap<Integer,Integer> _target = new HashMap<Integer, Integer>();

	/**
	 * 內掛專用<br>
	 * 是否存在於不要被搜尋到的目標對象
	 * 
	 * @param skillid
	 * @return
	 */
	public boolean InTargetList(Integer id) {
		return _targetId.contains(new Integer(id));
	}

	/**
	 * 內掛專用<br>
	 * 抓取不要搜尋到的目標對象
	 * 
	 * @return
	 */
	public ArrayList<Integer> TargetList() {
		return _targetId;
	}

	/**
	 * 內掛專用<br>
	 * 清空目標對象暫存
	 */
	public void clearTargetList() {
		_targetId.clear();
	}

	public int TargetListSize() {
		return _targetId.size();
	}

	/**
	 * 內掛專用<br>
	 * 目標對象加入暫存
	 */
	public void addTargetList(Integer id) {
		if (!_targetId.contains(new Integer(id)))
			_targetId.add(new Integer(id));
	}

	private boolean _enemyteleport = false;

	/** 看到仇人瞬移 */
	public boolean IsEnemyTeleport() {
		return _enemyteleport;
	}

	/** set 看到仇人瞬移 */
	public void setIsEnemyTeleport(boolean b) {
		_enemyteleport = b;
	}

	private boolean _bossteleport = false;

	/** 看到BOSS瞬移 */
	public boolean IsbossTeleport() {
		return _bossteleport;
	}

	/** set 看到BOSS瞬移 */
	public void setIsbossTeleport(boolean b) {
		_bossteleport = b;
	}
	
	private int _bosspage;

	public void set_bosspage(int page) {
	    _bosspage = page;
	}

	private int _bossarea;


	public int get_bosspage() {
	    return _bosspage;
	}

	public void set_bossarea(int area) {
	    _bossarea = area;
	}

	public int get_bossarea() {
	    return _bossarea;
	}
	
	private final String[] _bossAreaInfo = new String[15];

	public void setBossAreaInfo(int index, String value) {
	    _bossAreaInfo[index] = value;
	}

	public String getBossAreaInfo(int index) {
	    return _bossAreaInfo[index];
	}
	
	//////////////
	private final Map<Integer, String> _tempBossInfo = new HashMap<>();

	public void setTempID(int id, String value) {
	    _tempBossInfo.put(id, value);
	}

	public String getTempID(int id) {
	    return _tempBossInfo.get(id);
	}
	//////////////
	// ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
}