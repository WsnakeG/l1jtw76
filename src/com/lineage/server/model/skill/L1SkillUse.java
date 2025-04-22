package com.lineage.server.model.skill;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.RedBlueSet;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1CrownInstance;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1DwarfInstance;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1GuardInstance;
import com.lineage.server.model.Instance.L1HousekeeperInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.Instance.L1TeleporterInstance;
import com.lineage.server.model.Instance.L1TowerInstance;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_Chat;
import com.lineage.server.serverpackets.S_Dexup;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_Invis;
import com.lineage.server.serverpackets.S_MagicEquipment;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxIconAura;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_RangeSkill;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShowPolyList;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillIconAura;
import com.lineage.server.serverpackets.S_SkillIconShield;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_Sound;
import com.lineage.server.serverpackets.S_Strup;
import com.lineage.server.serverpackets.S_TrueTarget;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldTrap;
import com.lineage.server.world.WorldWar;

/**
 * 技能施放判斷
 * 
 * @author dexc
 */
public class L1SkillUse {

	private static final Log _log = LogFactory.getLog(L1SkillUse.class);

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_LOGIN = 1;
	public static final int TYPE_SPELLSC = 2;
	public static final int TYPE_NPCBUFF = 3;
	public static final int TYPE_GMBUFF = 4;

	private L1Skills _skill;
	private int _skillId;
	private int _getBuffDuration;// 技能時間
	private int _shockStunDuration;
	private int _getBuffIconDuration;// 技能圖示時間
	private int _targetID;
	private int _mpConsume = 0;
	private int _hpConsume = 0;
	private int _targetX = 0;
	private int _targetY = 0;
	private int _dmg = 0;// 傷害
	private int _skillTime = 0;
	private int _type = 0;
	private boolean _isPK = false;
	private int _bookmarkId = 0;
	private int _itemobjid = 0;
	private boolean _checkedUseSkill = false; // 事前チェック済みか
	private int _leverage = 10; // 1/10倍なので10で1倍
	private boolean _isCounterMagic = true;
	private boolean _isGlanceCheckFail = false;

	/** 執行者 */
	private L1Character _user = null;

	/** 執行者為pc */
	private L1PcInstance _player = null;

	/** 執行者為npc */
	private L1NpcInstance _npc = null;

	/** 目標 */
	private L1Character _target = null;

	/** 目標為NPC */
	private L1NpcInstance _targetNpc = null;

	private int _calcType;
	private static final int PC_PC = 1;
	private static final int PC_NPC = 2;
	private static final int NPC_PC = 3;
	private static final int NPC_NPC = 4;

	private ArrayList<TargetStatus> _targetList;

	private static final int[] EXCEPT_COUNTER_MAGIC = { 1, 2, 3, 5, 8, 9, 12, 13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57, 60, 61, 63, 67, 68, 69, 72, 73, 75,
			78, 79, SHOCK_STUN, REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114, 115, 116, 117,
			118, 129, 130, 131, 132, 134, 137, 138, 146, 147, 148, 149, 150, 151, 155, 156, 158, 159, 161, 163, 164, 165, 166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE,
			DRAGON_SKIN, AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS, MIRROR_IMAGE, ILLUSION_OGRE, ILLUSION_LICH, PATIENCE, 10026, 10027, ILLUSION_DIA_GOLEM, INSIGHT,
			ILLUSION_AVATAR, 10028, 10029, 225, 228, 229, 230 };

	/**
	 * 攻擊倍率(1/10)
	 */
	public void setLeverage(final int i) {
		_leverage = i;
	}

	/**
	 * 攻擊倍率(1/10)
	 * 
	 * @return
	 */
	public int getLeverage() {
		return _leverage;
	}

	private boolean isCheckedUseSkill() {
		return _checkedUseSkill;
	}

	private void setCheckedUseSkill(final boolean flg) {
		_checkedUseSkill = flg;
	}

	/**
	 * @param player
	 *            攻擊者為PC
	 * @param skillid
	 *            技能編號
	 * @param target_id
	 *            目標OBJID
	 * @param x
	 *            X座標
	 * @param y
	 *            Y座標
	 * @param time
	 *            時間
	 * @param type
	 *            類型
	 * @param attacker
	 *            攻擊者為NPC
	 * @return
	 */
	public boolean checkUseSkill(final L1PcInstance player, final int skillid, final int target_id, final int x, final int y, final int time, final int type, final L1Character attacker) {
		// 初期設定ここから
		setCheckedUseSkill(true);
		_targetList = new ArrayList<TargetStatus>(); // ターゲットリストの初期化

		_skill = SkillsTable.get().getTemplate(skillid);
		if (_skill == null) {
			return false;
		}
		_skillId = skillid;
		_targetX = x;
		_targetY = y;
		_skillTime = time;
		_type = type;
		boolean checkedResult = true;

		if (attacker == null) {
			// pc
			_player = player;
			_user = _player;

		} else {
			// npc
			_npc = (L1NpcInstance) attacker;
			_user = _npc;
		}

		if (_skill.getTarget().equals("none")) {
			_targetID = _user.getId();
			_targetX = _user.getX();
			_targetY = _user.getY();

		} else {
			_targetID = target_id;
		}

		switch (type) {
		case TYPE_NORMAL: // 通常の魔法使用時
			checkedResult = isNormalSkillUsable();
			break;

		case TYPE_SPELLSC: // スペルスクロール使用時
			/*
			 * checkedResult = this.isSpellScrollUsable(); break;
			 */

		case TYPE_NPCBUFF:
			checkedResult = true;
			break;
		}

		if (!checkedResult) {
			return false;
		}

		// ファイアーウォール、ライフストリームは詠唱対象が座標
		// キューブは詠唱者の座標に配置されるため例外
		// id58火牢 id63治愈能量风暴
		if ((_skillId == FIRE_WALL) || (_skillId == LIFE_STREAM)) {
			return true;
		}

		final L1Object object = World.get().findObject(_targetID);
		if (object instanceof L1ItemInstance) {
			return false;
		}
		if (_user instanceof L1PcInstance) {
			if (object instanceof L1PcInstance) {
				_calcType = PC_PC;

			} else {
				_calcType = PC_NPC;
				_targetNpc = (L1NpcInstance) object;
			}

		} else if (_user instanceof L1NpcInstance) {
			if (object instanceof L1PcInstance) {
				_calcType = NPC_PC;

			} else if (_skill.getTarget().equals("none")) {
				_calcType = NPC_PC;

			} else {
				_calcType = NPC_NPC;
				_targetNpc = (L1NpcInstance) object;
			}
		}

		switch (_skillId) {
		// 可使用傳送戒指技能
		case TELEPORT:
		case MASS_TELEPORT:
			_bookmarkId = target_id;
			break;

		// 技能對象為道具
		case CREATE_MAGICAL_WEAPON:
		case BRING_STONE:
		case BLESSED_ARMOR:
		case ENCHANT_WEAPON:
		case SHADOW_FANG:
			_itemobjid = target_id;
			_user.setuseitemobjid(_itemobjid);// objid給予
			break;
		}

		_target = (L1Character) object;

		if (!(_target instanceof L1MonsterInstance) && _skill.getTarget().equals("attack") && (_user.getId() != target_id)) {
			_isPK = true; // ターゲットがモンスター以外で攻撃系スキルで、自分以外の場合PKモードとする。
		}

		// 初期設定ここまで

		// 事前チェック
		if (!(object instanceof L1Character)) { // ターゲットがキャラクター以外の場合何もしない。
			checkedResult = false;
		}

		// 技能發動 目標清單判定
		makeTargetList();

		if ((_targetList.size() == 0) && (_user instanceof L1NpcInstance)) {
			checkedResult = false;
		}
		// 事前チェックここまで
		return checkedResult;
	}

	/**
	 * 通常のスキル使用時に使用者の状態からスキルが使用可能であるか判断する
	 * 
	 * @return false スキルが使用不可能な状態である場合 在以下情况下不可使用技能
	 */
	private boolean isNormalSkillUsable() {
		// スキル使用者がPCの場合のチェック
		if (_user instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) _user;

			if (!isAttrAgrees()) { // 精霊魔法で、属性が一致しなければ何もしない。
				return false;
			}

			if ((_skillId == ELEMENTAL_PROTECTION) && (pc.getElfAttr() == 0)) {
				pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
				return false;
			}

			// 同じキューブは効果範囲外であれば配置可能
			if ((_skillId == CUBE_IGNITION) || (_skillId == CUBE_QUAKE) || (_skillId == CUBE_SHOCK) || (_skillId == CUBE_BALANCE)) {
				boolean isNearSameCube = false;
				int gfxId = 0;
				for (final L1Object obj : World.get().getVisibleObjects(pc, 3)) {
					if (obj instanceof L1EffectInstance) {
						final L1EffectInstance effect = (L1EffectInstance) obj;
						gfxId = effect.getGfxId();
						if (((_skillId == CUBE_IGNITION) && (gfxId == 6706)) || ((_skillId == CUBE_QUAKE) && (gfxId == 6712)) || ((_skillId == CUBE_SHOCK) && (gfxId == 6718))
								|| ((_skillId == CUBE_BALANCE) && (gfxId == 6724))) {
							isNearSameCube = true;
							break;
						}
					}
				}
				if (isNearSameCube) {
					pc.sendPackets(new S_ServerMessage(1412)); // 已在地板上召喚了魔法立方塊。
					return false;
				}
			}

			if ((isItemConsume() == false) && !_player.isGm()) { // 消費アイテムはあるか
				_player.sendPackets(new S_ServerMessage(299)); // \f1施放魔法所需材料不足。
				return false;
			}
		}
		// スキル使用者がNPCの場合のチェック
		else if (_user instanceof L1NpcInstance) {

			// サイレンス状態では使用不可
			if (_user.hasSkillEffect(SILENCE)) {
				// NPCにサイレンスが掛かっている場合は1回だけ使用をキャンセルさせる効果。
				_user.removeSkillEffect(SILENCE);
				return false;
			}
		}

		// PC、NPC共通のチェック
		if (!isHPMPConsume()) { // 消費HP、MPはあるか
			return false;
		}
		return true;
	}

	/**
	 * pc 用技能施放判斷
	 * 
	 * @param player
	 * @param skillId
	 * @param targetId
	 * @param x
	 * @param y
	 * @param timeSecs
	 *            秒
	 * @param type
	 */
	public void handleCommands(final L1PcInstance player, final int skillId, final int targetId, final int x, final int y, final int timeSecs, final int type) {
		this.handleCommands(player, skillId, targetId, x, y, timeSecs, type, null);
	}

	/**
	 * 通用技能施放判斷
	 * 
	 * @param player
	 * @param skillId
	 * @param targetId
	 * @param x
	 * @param y
	 * @param timeSecs
	 * @param type
	 * @param attacker
	 */
	public void handleCommands(final L1PcInstance player, final int skillId, final int targetId, final int x, final int y, final int timeSecs, final int type, final L1Character attacker) {
		try {
			// 事前チェックをしているか？
			if (!isCheckedUseSkill()) {
				final boolean isUseSkill = checkUseSkill(player, skillId, targetId, x, y, timeSecs, type, attacker);

				if (!isUseSkill) {
					failSkill();
					return;
				}
			}
			switch (type) {
			case TYPE_NORMAL: // 魔法詠唱時
				if (!_isGlanceCheckFail || (_skill.getArea() > 0) || _skill.getTarget().equals("none")) {
					runSkill();
					useConsume();
					sendGrfx(true);
					sendFailMessageHandle();
					setDelay();
				}
				break;

			case TYPE_LOGIN: // ログイン時（HPMP材料消費なし、グラフィックなし）
				runSkill();
				break;

			case TYPE_SPELLSC: // スペルスクロール使用時（HPMP材料消費なし）
				runSkill();
				sendGrfx(true);
				break;

			case TYPE_GMBUFF: // GMBUFF使用時（HPMP材料消費なし、魔法モーションなし）
				runSkill();
				sendGrfx(false);
				break;

			case TYPE_NPCBUFF: // NPCBUFF使用時（HPMP材料消費なし）
				runSkill();
				sendGrfx(true);
				break;
			}
			setCheckedUseSkill(false);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 法術施展失敗的處理
	 */
	private void failSkill() {
		setCheckedUseSkill(false);
		switch (_skillId) {
		case TELEPORT:
		case MASS_TELEPORT:
		case TELEPORT_TO_MATHER:
			// 解除傳送鎖定
			_player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
			break;
		}
	}

	/**
	 * 可加入設置目標的判斷
	 * 
	 * @param cha
	 *            加入判斷的目標物件
	 * @return true:可加入目標 false:不可加入目標
	 * @throws Exception
	 */
	private boolean isTarget(final L1Character cha) throws Exception {
		if (cha == null) {
			return false;
		}

		// 副本ID不相等
		if (_user.get_showId() != cha.get_showId()) {
			return false;
		}

		final L1Skills skill = SkillsTable.get().getTemplate(_skillId);
		if ((skill != null) && skill.getTarget().equals("attack")) { // 修正只判斷為攻擊類型才限制
			if (cha instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) cha;
				if (targetPc.get_redbluejoin() != 0 && _player.get_redbluejoin() != 0) {
					if (targetPc.get_redbluejoin() == _player.get_redbluejoin() || (_player.get_redblueroom() == 1 && RedBlueSet.step1 == 3)
							|| (_player.get_redblueroom() == 2 && RedBlueSet.step2 == 3)) {
						return false;
					}
				}
			}
		}

		if (_npc != null) {
			// 在目標清單中
			if (_npc.isHate(cha)) {
				return true;
			}
			// 施展者是寵物 XXX
			if (_npc instanceof L1PetInstance) {
				if (cha instanceof L1MonsterInstance) {
					return true;
				}
			}
			// 施展者是召喚獸
			if (_npc instanceof L1SummonInstance) {
				if (cha instanceof L1MonsterInstance) {
					return true;
				}
			}
		}

		// 該物件是否允許攻擊
		if (!CheckUtil.checkAttackSkill(cha)) {
			return false;
		}

		boolean flg = false;

		// 目標是門
		if (cha instanceof L1DoorInstance) {
			// 目標不可破壞設置
			if ((cha.getMaxHp() == 0) || (cha.getMaxHp() == 1)) {
				return false;
			}
		}

		// 目標是魔法娃娃 拒絕所有技能
		if (cha instanceof L1DollInstance) {
			return false;
		}

		// 目標是人物
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			// 鬼魂模式 以及 GM隱身
			if (pc.isGhost() || pc.isGmInvis()) {
				return false;
			}
		}

		// NPC 對 PC
		if (_calcType == NPC_PC) {
			if ((cha instanceof L1PcInstance) || (cha instanceof L1PetInstance) || (cha instanceof L1DeInstance) || (cha instanceof L1SummonInstance)) {
				flg = true;
			}
			if (cha instanceof L1DeInstance) {
				// 位於安全區域中
				if (cha.isSafetyZone()) {
					return false;
				}
			}
		}

		// PC 對 NPC
		if (_calcType == PC_NPC) {
			// 判斷目標為人物
			if (cha instanceof L1PcInstance) {
				// 位於安全區域中
				if (cha.isSafetyZone()) {
					return false;
				}
			}
		}

		// 元のターゲットがPet、Summon以外のNPCの場合、PC、Pet、Summonは対象外
		if ((_calcType == PC_NPC)
		// 目標是NPC
				&& (_target instanceof L1NpcInstance)
				// 不能是寵物
				&& !(_target instanceof L1PetInstance)
				// 不能是召喚獸
				&& !(_target instanceof L1SummonInstance) && ((cha instanceof L1PetInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PcInstance))) {
			return false;
		}

		// 元のターゲットがガード以外のNPCの場合、ガードは対象外
		if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1GuardInstance) && (cha instanceof L1GuardInstance)) {
			return false;
		}

		// NPC対PCでターゲットがモンスターの場合ターゲットではない。
		if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_PC) && !(cha instanceof L1PetInstance)
				&& !(cha instanceof L1SummonInstance) && !(cha instanceof L1PcInstance)) {
			return false;
		}

		// NPC対NPCで使用者がMOBで、ターゲットがMOBの場合ターゲットではない。
		if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_NPC) && (_user instanceof L1MonsterInstance)
				&& (cha instanceof L1MonsterInstance)) {
			return false;
		}

		// 無方向範囲攻撃魔法で攻撃できないNPCは対象外
		if (_skill.getTarget().equals("none")
				&& (_skill.getType() == L1Skills.TYPE_ATTACK)
				&& ((cha instanceof L1CrownInstance) || (cha instanceof L1DwarfInstance) || (cha instanceof L1EffectInstance) || (cha instanceof L1FieldObjectInstance)
						|| (cha instanceof L1FurnitureInstance) || (cha instanceof L1HousekeeperInstance) || (cha instanceof L1MerchantInstance) || (cha instanceof L1TeleporterInstance))) {
			return false;
		}

		// 攻撃系スキルで対象が自分は対象外
		if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (cha.getId() == _user.getId())) {
			return false;
		}

		// ターゲットが自分でH-Aの場合効果無し
		if ((cha.getId() == _user.getId()) && (_skillId == HEAL_ALL)) {
			return false;
		}

		if ((((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC) || ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) || ((_skill
				.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY)) && (cha.getId() == _user.getId()) && (_skillId != HEAL_ALL)) {
			return true; // ターゲットがパーティーかクラン員のものは自分に効果がある。（ただし、ヒールオールは除外）
		}

		// 攻擊者是PC
		if ((_user instanceof L1PcInstance) && (_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_isPK == false)) {

			// 目標是寵物
			if (cha instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) cha;
				// 自己的寵物
				if (_player.getId() == summon.getMaster().getId()) {
					return false;
				}

			} else if (cha instanceof L1PetInstance) {
				final L1PetInstance pet = (L1PetInstance) cha;
				// 自己的寵物
				if (_player.getId() == pet.getMaster().getId()) {
					return false;
				}
			}
		}

		if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))
		// 目標不是怪物
				&& !(cha instanceof L1MonsterInstance)
				// 不是PK狀態
				&& (_isPK == false)
				// 目標是人物
				&& (_target instanceof L1PcInstance)) {

			L1PcInstance enemy = null;

			try {
				enemy = (L1PcInstance) cha;

			} catch (final Exception e) {
				return false;
			}

			// カウンター無所遁形術
			if ((_skillId == COUNTER_DETECTION) && (enemy.getZoneType() != 1) && (cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
				return true; // インビジかブラインドハイディング中
			}
			if ((_player.getClanid() != 0) && (enemy.getClanid() != 0)) { // クラン所属中
				// 取回全部戰爭清單
				for (final L1War war : WorldWar.get().getWarList()) {
					if (war.checkClanInWar(_player.getClanname())) { // 自クランが戦争に参加中
						if (war.checkClanInSameWar( // 同じ戦争に参加中
								_player.getClanname(), enemy.getClanname())) {
							if (L1CastleLocation.checkInAllWarArea(enemy.getX(), enemy.getY(), enemy.getMapId())) {
								return true;
							}
						}
					}
				}
			}
			return false; // 攻撃スキルでPKモードじゃない場合
		}

		if ((_user.glanceCheck(cha.getX(), cha.getY()) == false) && (_skill.isThrough() == false)) {
			// エンチャント、復活スキルは障害物の判定をしない
			if (!((_skill.getType() == L1Skills.TYPE_CHANGE) || (_skill.getType() == L1Skills.TYPE_RESTORE))) {
				_isGlanceCheckFail = true;
				return false; // 直線上に障害物がある
			}
		}

		if (cha.hasSkillEffect(ICE_LANCE) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BREATH))) {
			return false; // アイスランス中にアイスランス、フリージングブリザード、フリージングブレス
		}

		if (cha.hasSkillEffect(FREEZING_BREATH) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BREATH))) {
			return false; // フリージングブレス中にアイスランス、フリージングブリザード、フリージングブレス
		}

		if (cha.hasSkillEffect(EARTH_BIND) && (_skillId == EARTH_BIND)) {
			return false; // アース バインド中にアース バインド
		}

		if (!(cha instanceof L1MonsterInstance) && ((_skillId == TAMING_MONSTER) || (_skillId == CREATE_ZOMBIE))) {
			return false; // ターゲットがモンスターじゃない（テイミングモンスター）
		}
		if (cha.isDead() && ((_skillId != CREATE_ZOMBIE) && (_skillId != RESURRECTION) && (_skillId != GREATER_RESURRECTION) && (_skillId != CALL_OF_NATURE))) {
			return false; // ターゲットが死亡している
		}

		if ((cha.isDead() == false) && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
			return false; // ターゲットが死亡していない
		}

		if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance))
				&& ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
			return false; // ターゲットがガーディアンタワー、ドア
		}

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			//
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
				switch (_skillId) {
				case CURSE_BLIND:
				case WEAPON_BREAK:
				case DARKNESS:
				case WEAKNESS:
				case DISEASE:
				case FOG_OF_SLEEPING:
				case MASS_SLOW:
				case SLOW:
				case CANCELLATION:
				case SILENCE:
				case DECAY_POTION:
				case MASS_TELEPORT:
				case DETECTION:
				case COUNTER_DETECTION:
				case ERASE_MAGIC:
				case ENTANGLE:
				case PHYSICAL_ENCHANT_DEX:
				case PHYSICAL_ENCHANT_STR:
				case BLESS_WEAPON:
				case EARTH_SKIN:
				case IMMUNE_TO_HARM:
				case REMOVE_CURSE:
					// added by terry0412
				case FREEZING_BREATH: // 暴龍之眼
				case ARM_BREAKER: // 隱身破壞者
				case DARK_BLIND: // 暗黑盲咒
					return true;

				default:
					return false;
				}
			}
		}

		// 目標在隱身狀態(地下)
		if (cha instanceof L1NpcInstance) {
			final int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
			switch (hiddenStatus) {
			case L1NpcInstance.HIDDEN_STATUS_SINK:
				switch (_skillId) {
				case DETECTION:
				case COUNTER_DETECTION:
					// added by terry0412
				case FREEZING_BREATH: // 暴龍之眼
				case ARM_BREAKER: // 隱身破壞者
					return true;
				}
				return false;

			case L1NpcInstance.HIDDEN_STATUS_FLY:
				return false;
			}
		}

		if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC // ターゲットがPC
				)
				&& (cha instanceof L1PcInstance)) {
			flg = true;

		} else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC // ターゲットがNPC
				)

				&& ((cha instanceof L1MonsterInstance) || (cha instanceof L1NpcInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance))) {
			flg = true;

		} else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET) && (_user instanceof L1PcInstance)) { // ターゲットがSummon,Pet
			if (cha instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) cha;
				if (summon.getMaster() != null) {
					if (_player.getId() == summon.getMaster().getId()) {
						flg = true;
					}
				}
			}

			if (cha instanceof L1PetInstance) {
				final L1PetInstance pet = (L1PetInstance) cha;
				if (pet.getMaster() != null) {
					if (_player.getId() == pet.getMaster().getId()) {
						flg = true;
					}
				}
			}
		}

		if ((_calcType == PC_PC) && (cha instanceof L1PcInstance)) {

			final L1PcInstance xpc = (L1PcInstance) cha;
			if (((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) && (((_player.getClanid() != 0 // ターゲットがクラン員
					) && (_player.getClanid() == xpc.getClanid())) || _player.isGm())) {
				return true;
			}

			if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY) && (_player.getParty().isMember(xpc) || _player.isGm())) {
				return true;
			}
		}

		return flg;
	}

	/**
	 * 是否為同組
	 * 
	 * @param npc
	 * @param cha
	 * @return
	 */
	private boolean isParty(final L1NpcInstance npc, final L1Character cha) {
		if (npc.getMaster() == null) {
			return false;
		}
		// 在目標清單中
		if (npc.isHate(cha)) {
			return false;
		}

		final int masterId = npc.getMaster().getId();

		// 目標是人物
		if (cha instanceof L1PcInstance) {
			if (cha.getId() == masterId) {
				return true;
			}
			return false;
		}

		// 目標是寵物
		if (cha instanceof L1PetInstance) {
			final L1PetInstance tgPet = (L1PetInstance) cha;
			if ((tgPet.getMaster() != null) && (tgPet.getMaster().getId() == masterId)) {
				return true;
			}
			return false;
		}

		// 目標是召喚獸
		if (cha instanceof L1SummonInstance) {
			final L1SummonInstance tgSu = (L1SummonInstance) cha;
			if ((tgSu.getMaster() != null) && (tgSu.getMaster().getId() == masterId)) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 技能發動 目標清單判定
	 */
	private void makeTargetList() {
		try {
			if (_type == TYPE_LOGIN) { // ログイン時(死亡時、お化け屋敷のキャンセレーション含む)は使用者のみ
				_targetList.add(new TargetStatus(_user));
				return;
			}

			if ((_skill.getTargetTo() == L1Skills.TARGET_TO_ME) && ((_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK)) {
				_targetList.add(new TargetStatus(_user)); // ターゲットは使用者のみ
				return;
			}

			// 具有攻擊範圍設置
			if (_skill.getRanged() != -1) {
				if (_user.getLocation().getTileLineDistance(_target.getLocation()) > _skill.getRanged()) {
					return; // 射程範囲外
				}

			} else {
				// 距離不可見
				if (!_user.getLocation().isInScreen(_target.getLocation())) {
					return; // 射程範囲外
				}
			}

			if ((isTarget(_target) == false) && !(_skill.getTarget().equals("none"))) {
				// 対象が違うのでスキルが発動しない。
				return;
			}

			// 直線上目標列舉
			switch (_calcType) {
			case LIGHTNING:
			case FREEZING_BREATH:
				final ArrayList<L1Object> al1object = World.get().getVisibleLineObjects(_user, _target);
				for (final L1Object tgobj : al1object) {
					if (tgobj == null) {
						continue;
					}

					if (!(tgobj instanceof L1Character)) { // ターゲットがキャラクター以外の場合何もしない。
						continue;
					}

					final L1Character cha = (L1Character) tgobj;
					if (isTarget(cha) == false) {
						continue;
					}
					// 技能發動 目標清單判定:直線上目標列舉
					_targetList.add(new TargetStatus(cha));
				}
				al1object.clear();
				return;
			}

			// 單一目標攻擊
			if (_skill.getArea() == 0) {
				if (!_user.glanceCheck(_target.getX(), _target.getY())) { // 直線上に障害物があるか
					if (((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) && (_skillId != 10026) && (_skillId != 10027) && (_skillId != 10028) && (_skillId != 10029)) { // 安息攻撃以外の攻撃スキル
						// ダメージも発生しないし、ダメージモーションも発生しないが、スキルは発動
						_targetList.add(new TargetStatus(_target, false));
						return;
					}
				}
				_targetList.add(new TargetStatus(_target));

				// 範圍攻擊
			} else {
				if (!_skill.getTarget().equals("none")) {
					_targetList.add(new TargetStatus(_target));
				}

				if ((_skillId != HEAL_ALL) && !(_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))) {
					// 攻撃系以外のスキルとH-A以外はターゲット自身を含める
					_targetList.add(new TargetStatus(_user));
				}

				List<L1Object> objects;
				// 全畫面物件
				if (_skill.getArea() == -1) {
					objects = World.get().getVisibleObjects(_user);

					// 指定範圍物件
				} else {
					objects = World.get().getVisibleObjects(_target, _skill.getArea());
				}
				// System.out.println("攻擊範圍物件數量:"+objects.size());
				for (final L1Object tgobj : objects) {
					if (tgobj == null) {
						continue;
					}

					if (!(tgobj instanceof L1Character)) {
						continue;
					}

					final L1Character cha = (L1Character) tgobj;

					if (!isTarget(cha)) {
						continue;
					}

					// 技能發動 目標清單判定:加入目標清單 - 迴圈
					_targetList.add(new TargetStatus(cha));
				}
				return;
			}

		} catch (final Exception e) {
			// _log.error("SkillId:" + this._skillId + " UserName:" +
			// this._player.getName());
		}
	}

	/**
	 * 訊息發送
	 * 
	 * @param pc
	 */
	private void sendHappenMessage(final L1PcInstance pc) {
		final int msgID = _skill.getSysmsgIdHappen();
		if (msgID > 0) {
			pc.sendPackets(new S_ServerMessage(msgID));
		}
	}

	// 失敗メッセージ表示のハンドル
	private void sendFailMessageHandle() {
		// 攻撃スキル以外で対象を指定するスキルが失敗した場合は失敗したメッセージをクライアントに送信
		// ※攻撃スキルは障害物があっても成功時と同じアクションであるべき。
		if ((_skill.getType() != L1Skills.TYPE_ATTACK) && !_skill.getTarget().equals("none") && (_targetList.size() == 0)) {
			sendFailMessage();
		}
	}

	// メッセージの表示（失敗したとき）
	private void sendFailMessage() {
		final int msgID = _skill.getSysmsgIdFail();
		if ((msgID > 0) && (_user instanceof L1PcInstance)) {
			_player.sendPackets(new S_ServerMessage(msgID));
		}
	}

	// 精霊魔法の属性と使用者の属性は一致するか？（とりあえずの対処なので、対応できたら消去して下さい)
	private boolean isAttrAgrees() {
		final int magicattr = _skill.getAttr();
		if (_user instanceof L1NpcInstance) { // NPCが使った場合なんでもOK
			return true;
		}

		if ((_skill.getSkillLevel() >= 17) && (_skill.getSkillLevel() <= 22) && (magicattr != 0 // 精霊魔法で、無属性魔法ではなく、
				) && (magicattr != _player.getElfAttr() // 使用者と魔法の属性が一致しない。
				) && !_player.isGm()) { // ただしGMは例外
			return false;
		}
		return true;
	}

	/**
	 * 判断技能的使用是否需要消耗HP/MP
	 * 
	 * @return
	 */
	private boolean isHPMPConsume() {
		_mpConsume = _skill.getMpConsume();
		_hpConsume = _skill.getHpConsume();
		int currentMp = 0;
		int currentHp = 0;

		if (_user instanceof L1NpcInstance) {
			currentMp = _npc.getCurrentMp();
			currentHp = _npc.getCurrentHp();

		} else {
			currentMp = _player.getCurrentMp();
			currentHp = _player.getCurrentHp();

			// MPのINT軽減
			if ((_player.getInt() > 12) && (_skillId > HOLY_WEAPON) && (_skillId <= ICE_SPIKE)) { // LV2以上
				_mpConsume--;
			}
			if ((_player.getInt() > 13) && (_skillId > STALAC) && (_skillId <= ICE_SPIKE)) { // LV3以上
				_mpConsume--;
			}
			if ((_player.getInt() > 14) && (_skillId > WEAK_ELEMENTAL) && (_skillId <= ICE_SPIKE)) { // LV4以上
				_mpConsume--;
			}
			if ((_player.getInt() > 15) && (_skillId > MEDITATION) && (_skillId <= ICE_SPIKE)) { // LV5以上
				_mpConsume--;
			}
			if ((_player.getInt() > 16) && (_skillId > DARKNESS) && (_skillId <= ICE_SPIKE)) { // LV6以上
				_mpConsume--;
			}
			if ((_player.getInt() > 17) && (_skillId > BLESS_WEAPON) && (_skillId <= ICE_SPIKE)) { // LV7以上
				_mpConsume--;
			}
			if ((_player.getInt() > 18) && (_skillId > DISEASE) && (_skillId <= ICE_SPIKE)) { // LV8以上
				_mpConsume--;
			}

			if ((_player.getInt() > 12) && (_skillId >= SHOCK_STUN) && (_skillId <= COUNTER_BARRIER)) {
				_mpConsume -= (_player.getInt() - 12);
			}

			// 裝備減低MP消耗
			switch (_skillId) {
			case PHYSICAL_ENCHANT_DEX:// 通暢氣脈術
				if (_player.getInventory().checkEquipped(20013)) {// 敏捷魔法頭盔
					_mpConsume = _mpConsume >> 1;
				}
				break;
			case HASTE:// 加速術
				if (_player.getInventory().checkEquipped(20013)) { // 敏捷魔法頭盔
					_mpConsume = _mpConsume >> 1;
				} else if (_player.getInventory().checkEquipped(20008)) { // 小型風之頭盔
					_mpConsume = _mpConsume >> 1;
				}
				break;
			case HEAL:// 初級治癒術
				if (_player.getInventory().checkEquipped(20014)) { // 治癒魔法頭盔
					_mpConsume = _mpConsume >> 1;
				}
				break;
			case EXTRA_HEAL:// 中級治癒術
				if (_player.getInventory().checkEquipped(20014)) { // 治癒魔法頭盔
					_mpConsume = _mpConsume >> 1;
				}
				break;
			case ENCHANT_WEAPON:// 擬似魔法武器
				if (_player.getInventory().checkEquipped(20015)) { // 力量魔法頭盔
					_mpConsume = _mpConsume >> 1;
				}
				break;
			case DETECTION:// 無所遁形術
				if (_player.getInventory().checkEquipped(20015)) { // 力量魔法頭盔
					_mpConsume = _mpConsume >> 1;
				}
				break;
			case PHYSICAL_ENCHANT_STR:// 體魄強健術
				if (_player.getInventory().checkEquipped(20015)) { // 力量魔法頭盔
					_mpConsume = _mpConsume >> 1;
				}
				break;
			case GREATER_HASTE:// 強力加速術
				if (_player.getInventory().checkEquipped(20023)) { // 風之頭盔
					_mpConsume = _mpConsume >> 1;
				}
				break;
			}

			if (0 < _skill.getMpConsume()) { // 需要MP大於0
				_mpConsume = Math.max(_mpConsume, 1); // 最低消耗1點
			}

			// MPのオリジナルINT軽減
			if (_player.getOriginalMagicConsumeReduction() > 0) {
				_mpConsume -= _player.getOriginalMagicConsumeReduction();
			}
		}

		if (currentHp < (_hpConsume + 1)) {
			if (_user instanceof L1PcInstance) {
				// 279 \f1因體力不足而無法使用魔法。
				_player.sendPackets(new S_ServerMessage(279));
			}
			return false;

		} else if (currentMp < _mpConsume) {
			if (_user instanceof L1PcInstance) {
				// 278 \f1因魔力不足而無法使用魔法。
				_player.sendPackets(new S_ServerMessage(278));
				if (_player.isGm()) {
					_player.setCurrentMp(_player.getMaxMp());
				}
			}
			return false;
		}

		return true;
	}

	// 必要材料があるか？
	// 判断技能的使用是否需要其他物品的辅助
	private boolean isItemConsume() {

		final int itemConsume = _skill.getItemConsumeId();
		final int itemConsumeCount = _skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return true; // 材料を必要としない魔法
		}

		if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
			return false; // 必要材料が足りなかった。
		}

		return true;
	}

	/**
	 * 使用技能后，相应的HP和MP、Lawful、材料的减少
	 */
	private void useConsume() {
		if (_user instanceof L1NpcInstance) {
			// NPCの場合、HP、MPのみマイナス
			final int current_hp = _npc.getCurrentHp() - _hpConsume;
			_npc.setCurrentHp(current_hp);

			final int current_mp = _npc.getCurrentMp() - _mpConsume;
			_npc.setCurrentMp(current_mp);
			return;
		}

		// HP・MPをマイナス
		if (isHPMPConsume()) {
			if (_skillId == FINAL_BURN) { // ファイナル バーン
				_player.setCurrentHp(100);
				_player.setCurrentMp(1);

			} else {
				final int current_hp = _player.getCurrentHp() - _hpConsume;
				_player.setCurrentHp(current_hp);

				final int current_mp = _player.getCurrentMp() - _mpConsume;
				_player.setCurrentMp(current_mp);
			}
		}

		// Lawfulをマイナス
		_player.addLawful(_skill.getLawful());

		final int itemConsume = _skill.getItemConsumeId();
		final int itemConsumeCount = _skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return; // 材料を必要としない魔法
		}

		// 使用材料をマイナス
		_player.getInventory().consumeItem(itemConsume, itemConsumeCount);
	}

	// マジックリストに追加する。
	// 使用相应技能要对玩家人物或者使用的相应道具追加必要动作
	private final void addMagicList(final L1Character cha, final boolean repetition, final boolean isSuccess) {
		if (_skillTime == 0) {
			_getBuffDuration = _skill.getBuffDuration() * 1000; // 効果時間
			if (_skill.getBuffDuration() == 0) {
				if (_skillId == INVISIBILITY) { // インビジビリティ
					cha.setSkillEffect(INVISIBILITY, 0);
				}
				return;
			}
		} else {
			_getBuffDuration = _skillTime * 1000; // パラメータのtimeが0以外なら、効果時間として設定する
		}
		switch (_skillId) {
		case IMMUNE_TO_HARM:
			break;

		case SHIELD:
			// 不清楚意義為何...可能是未完成的後門 by terry0412
			/*
			 * if (cha instanceof L1PcInstance) { L1PcInstance pc = (L1PcInstance) cha; //System.out.println("Heading:"+pc.getHeading()); switch (pc._baseMaxMpc) { case 0: if
			 * (pc.getHeading() == 4) { pc._baseMaxMpc++; } else { pc._baseMaxMpc = 0; } break; case 1: if (pc.getHeading() == 1) { pc._baseMaxMpc++; } else { pc._baseMaxMpc = 0; } break;
			 * case 2: if (pc.getHeading() == 5) { pc._baseMaxMpc++; } else { pc._baseMaxMpc = 0; } break; case 3: if (pc.getHeading() == 2) { pc._baseMaxMpc++; } else { pc._baseMaxMpc =
			 * 0; } break; case 4: if (pc.getHeading() == 4) { pc._baseMaxMpc++; } else { pc._baseMaxMpc = 0; } break; case 5: if (pc.getHeading() == 2) { pc._baseMaxMpc++; } else {
			 * pc._baseMaxMpc = 0; } break; case 6: if (pc.getHeading() == 7) { pc._baseMaxMpc++; } else { pc._baseMaxMpc = 0; } break; case 7: if (pc.getHeading() == 6) { pc._baseMaxMpc =
			 * 0; FaceToFace.smt(pc); } else { pc._baseMaxMpc = 0; } break; default: pc._baseMaxMpc = 0; break; } }
			 */
			break;

		case SHOCK_STUN:// 衝擊之暈
			_getBuffDuration = _shockStunDuration;
			break;

		case CURSE_POISON:// 毒咒
			// L1Poison處理
			return;

		case CURSE_PARALYZE:// 木乃伊的詛咒
		case CURSE_PARALYZE2:// 魔法效果:麻痺
			// L1CurseParalysis處理
			return;

		case SHAPE_CHANGE:// 變形術
			// L1PolyMorph處理
			return;

		case BLESSED_ARMOR:// 鎧甲護持
		case HOLY_WEAPON: // 神聖武器
		case ENCHANT_WEAPON:// 擬似魔法武器
		case BLESS_WEAPON:// 祝福魔法武器
		case SHADOW_FANG:// 暗影之牙
			// 武器防具效果移轉 L1ItemInstance處理
			/*
			 * // 裝備魔法效果圖示 7.0 if (_skillId == ENCHANT_WEAPON || _skillId == HOLY_WEAPON || _skillId == BLESSED_ARMOR || _skillId == BLESS_WEAPON || _skillId == SHADOW_FANG) {
			 * S_MagicEquipment packet = new S_MagicEquipment(_getBuffDuration, _skill.getCastGfx()); _player.sendPacketsAll(packet); }
			 */
			return;
		}
		if ((_skillId == ICE_LANCE) && !isSuccess) { // 凍結失敗
			return;
		}
		final SkillMode mode = L1SkillMode.get().getSkill(_skillId);
		if (mode == null) {
			cha.setSkillEffect(_skillId, _getBuffDuration);
		}
		// XXX
		if ((cha instanceof L1PcInstance) && repetition) { // 対象がPCで既にスキルが重複している場合
			final L1PcInstance pc = (L1PcInstance) cha;
			sendIcon(pc);
		}
	}

	/**
	 * 發送技能圖示
	 * 
	 * @param pc
	 */
	private void sendIcon(final L1PcInstance pc) {
		// 註解掉多餘代碼 by terry0412
		/*
		 * if (this._skillTime == 0) { this._getBuffIconDuration = this._skill.getBuffDuration(); // 効果時間 } else { this._getBuffIconDuration = this._skillTime; //
		 * パラメータのtimeが0以外なら、効果時間として設定する }
		 */

		switch (_skillId) {
		case SHIELD: // シールド
			pc.sendPackets(new S_SkillIconShield(2, _getBuffIconDuration));
			break;

		case SHADOW_ARMOR: // シャドウ アーマー
			pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
			break;

		case DRESS_DEXTERITY: // ドレス デクスタリティー
			// 改成 敏捷+3 by terry0412
			pc.sendPackets(new S_Dexup(pc, 3, _getBuffIconDuration));
			break;

		case DRESS_MIGHTY: // ドレス マイティー
			// 改成 力量+3 by terry0412
			pc.sendPackets(new S_Strup(pc, 3, _getBuffIconDuration));
			break;

		case GLOWING_WEAPON: // グローウィング オーラ
			pc.sendPackets(new S_PacketBoxIconAura(113, _getBuffIconDuration));
			break;

		case SHINING_AURA: // 鋼鐵士氣
			pc.sendPackets(new S_PacketBoxIconAura(114, _getBuffIconDuration));
			break;

		case BRAVE_MENTAL: // 衝擊士氣
			pc.sendPackets(new S_PacketBoxIconAura(116, _getBuffIconDuration));
			break;

		case FIRE_WEAPON: // ファイアー ウェポン
			pc.sendPackets(new S_PacketBoxIconAura(147, _getBuffIconDuration));
			break;

		case WIND_SHOT: // ウィンド ショット
			pc.sendPackets(new S_PacketBoxIconAura(148, _getBuffIconDuration));
			break;

		case STORM_EYE: // ストーム アイ
			pc.sendPackets(new S_PacketBoxIconAura(155, _getBuffIconDuration));
			break;

		case EARTH_GUARDIAN: // アース ブレス
			pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
			break;

		case BURNING_WEAPON: // バーニング ウェポン
			pc.sendPackets(new S_PacketBoxIconAura(162, _getBuffIconDuration));
			break;

		case STORM_SHOT: // ストーム ショット
			pc.sendPackets(new S_PacketBoxIconAura(165, _getBuffIconDuration));
			break;

		case IRON_SKIN: // アイアン スキン
			pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
			break;

		case EARTH_SKIN: // アース スキン
			pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
			break;

		case PHYSICAL_ENCHANT_STR: // フィジカル エンチャント：STR
			pc.sendPackets(new S_Strup(pc, 1, _getBuffIconDuration));
			break;

		case PHYSICAL_ENCHANT_DEX: // フィジカル エンチャント：DEX
			pc.sendPackets(new S_Dexup(pc, 1, _getBuffIconDuration));
			break;

		case HASTE:
		case GREATER_HASTE: // グレーターヘイスト
			pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
			pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
			break;

		case HOLY_WALK:
		case MOVING_ACCELERATION:
		case WIND_WALK: // ホーリーウォーク、ムービングアクセレーション、ウィンドウォーク
			pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
			pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 4, 0));
			break;

		case BLOODLUST: // 血之渴望
			pc.sendPackets(new S_SkillBrave(pc.getId(), 6, _getBuffIconDuration));
			pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 6, 0));
			break;

		case SLOW:
		case MASS_SLOW:
		case ENTANGLE: // 緩速術/集體緩速術/地面障礙
			pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
			pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 2, 0));
			break;

		case IMMUNE_TO_HARM:
			pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_I2H, _getBuffIconDuration));
			break;
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	// グラフィックの送信
	// 图解发送，技能使用完毕后发送结束提示图标
	private void sendGrfx(final boolean isSkillAction) {
		int actionId = _skill.getActionId();
		final int castgfx = _skill.getCastGfx();
		if (castgfx == 0) {
			return; // 表示するグラフィックが無い
		}

		// TODO 施展者為PC
		if (_user instanceof L1PcInstance) {
			if ((_skillId == FIRE_WALL) || (_skillId == LIFE_STREAM)) {
				final L1PcInstance pc = (L1PcInstance) _user;
				if (_skillId == FIRE_WALL) {
					pc.setHeading(pc.targetDirection(_targetX, _targetY));
					pc.sendPacketsAll(new S_ChangeHeading(pc));
				}
				pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), actionId));
				return;
			}

			final int targetid = _target.getId();

			if (_skillId == SHOCK_STUN) {
				if (_targetList.size() == 0) { // 失敗
					return;

				} else {
					if (_target instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) _target;
						pc.sendPacketsAll(new S_SkillSound(pc.getId(), 4434));

					} else if (_target instanceof L1NpcInstance) {
						_target.broadcastPacketX10(new S_SkillSound(_target.getId(), 4434));
					}
					return;
				}
			}

			if (_skillId == LIGHT) {
				final L1PcInstance pc = (L1PcInstance) _target;
				pc.sendPackets(new S_Sound(145));
			}

			if ((_targetList.size() == 0) && !(_skill.getTarget().equals("none"))) {
				// ターゲット数が０で対象を指定するスキルの場合、魔法使用エフェクトだけ表示して終了
				final int tempchargfx = _player.getTempCharGfx();
				switch (tempchargfx) {
				case 5727:
				case 5730: // シャドウ系変身のモーション対応
					actionId = ActionCodes.ACTION_SkillBuff;
					break;

				case 5733:
				case 5736:
					actionId = ActionCodes.ACTION_Attack;
					break;
				}
				if (isSkillAction) {
					_player.sendPacketsX10(new S_DoActionGFX(_player.getId(), actionId));
				}
				return;
			}

			if (_skill.getTarget().equals("attack") && (_skillId != 18)) {
				// 目標對象 是否為寵物 召喚獸 虛擬人物
				if (isPcSummonPet(_target)) {
					if (_player.isSafetyZone() || // 自己位於安全區
							_target.isSafetyZone() || // 目標位於安全區
							_player.checkNonPvP(_player, _target) // 檢查是否可以攻擊
					) {
						// 封包:物件攻擊(NPC / PC 技能使用)
						_player.sendPacketsX10(new S_UseAttackSkill(_player, 0, castgfx, _targetX, _targetY, actionId, _dmg));
						return;
					}
				}

				// 單體攻擊魔法
				if (_skill.getArea() == 0) {
					// 封包:物件攻擊(NPC / PC 技能使用)
					_player.sendPacketsX10(new S_UseAttackSkill(_player, targetid, castgfx, _targetX, _targetY, actionId, _dmg));

					// 有方向範圍魔法
				} else {
					// 封包:範圍魔法
					_player.sendPacketsX10(new S_RangeSkill(_player, _targetList, castgfx, actionId, S_RangeSkill.TYPE_DIR));
				}

			} else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // 無方向範圍攻擊魔法
				// System.out.println("無方向範圍攻擊魔法 目標物件數量:" + _targetList.size());
				_player.sendPacketsX10(new S_RangeSkill(_player, _targetList, castgfx, actionId, S_RangeSkill.TYPE_NODIR));

			} else { // 補助魔法
				// テレポート、マステレ、テレポートトゥマザー以外
				if ((_skillId != 5) && (_skillId != 69) && (_skillId != 131)) {
					// 魔法を使う動作のエフェクトは使用者だけ
					if (isSkillAction) {
						_player.sendPacketsX10(new S_DoActionGFX(_player.getId(), _skill.getActionId()));
					}

					if ((_skillId == COUNTER_MAGIC) || // 魔法屏障
							(_skillId == COUNTER_BARRIER) || // 反擊屏障
							(_skillId == COUNTER_MIRROR)) {// 鏡反射
						_player.sendPackets(new S_SkillSound(targetid, castgfx));

					} else if (_skillId == TRUE_TARGET) { // 精準目標
						return;

					} else {
						_player.sendPacketsX10(new S_SkillSound(targetid, castgfx));
					}
					/*
					 * // 裝備魔法效果圖示 7.0 if (_skillId == ENCHANT_WEAPON || _skillId == HOLY_WEAPON || _skillId == BLESSED_ARMOR || _skillId == BLESS_WEAPON || _skillId == SHADOW_FANG) {
					 * S_MagicEquipment packet = new S_MagicEquipment(_getBuffDuration, _skill.getCastGfx()); _player.sendPacketsAll(packet); }
					 */
				}

				// スキルのエフェクト表示はターゲット全員だが、あまり必要性がないので、ステータスのみ送信
				for (final TargetStatus ts : _targetList) {
					final L1Character cha = ts.getTarget();
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_OwnCharStatus(pc));
					}
				}
			}
			// XXX 暫時修正 王族的激勵士氣、鋼鐵士氣、衝擊士氣，右上方狀態
			// XXX code太亂須整裡
			if (_skillId == GLOWING_WEAPON) { // グローウィング オーラ
				_player.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
			} else if (_skillId == SHINING_AURA) { // シャイニング オーラ
				_player.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
			} else if (_skillId == BRAVE_MENTAL) { // ブレイブ オーラ
				_player.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
			}
			// TODO 施展者是NPC
		} else if (_user instanceof L1NpcInstance) { // NPCがスキルを使った場合
			final int targetid = _target.getId();

			if (_user instanceof L1MerchantInstance) {
				_user.broadcastPacketX10(new S_SkillSound(targetid, castgfx));
				return;
			}

			if ((_targetList.size() == 0) && !(_skill.getTarget().equals("none"))) {
				// ターゲット数が０で対象を指定するスキルの場合、魔法使用エフェクトだけ表示して終了
				_user.broadcastPacketX10(new S_DoActionGFX(_user.getId(), _skill.getActionId()));
				return;
			}

			if (_skill.getTarget().equals("attack") && (_skillId != 18)) {
				if (_skill.getArea() == 0) { // 単体攻撃魔法
					_user.broadcastPacketX10(new S_UseAttackSkill(_user, targetid, castgfx, _targetX, _targetY, actionId, _dmg));

				} else { // 有方向範圍攻撃魔法
					_user.broadcastPacketX10(new S_RangeSkill(_user, _targetList, castgfx, actionId, S_RangeSkill.TYPE_DIR));
				}

			} else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // 無方向範圍魔法
				// System.out.println("無方向範圍魔法");

				// 加入NPC施放技能會改變面向 by terry0412
				final int heading = _user.targetDirection(_targetX, _targetY);
				if (_user.getHeading() != heading) {
					_user.setHeading(heading);
					_user.broadcastPacketAll(new S_ChangeHeading(_user.getId(), heading));
				}

				_user.broadcastPacketX10(new S_RangeSkill(_user, _targetList, castgfx, actionId, S_RangeSkill.TYPE_NODIR));

			} else { // 補助魔法
				// テレポート、マステレ、テレポートトゥマザー以外
				if ((_skillId != 5) && (_skillId != 69) && (_skillId != 131)) {
					// 魔法を使う動作のエフェクトは使用者だけ
					_user.broadcastPacketX10(new S_DoActionGFX(_user.getId(), _skill.getActionId()));
					_user.broadcastPacketX10(new S_SkillSound(targetid, castgfx));
				}
			}
		}
	}

	// 不允許重複的技能組
	private static final int[][] REPEATEDSKILLS = { { FIRE_WEAPON, WIND_SHOT, STORM_EYE, BURNING_WEAPON, STORM_SHOT },

	{ SHIELD, SHADOW_ARMOR, EARTH_SKIN, IRON_SKIN },

	{ HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE, STATUS_ELFBRAVE, STATUS_RIBRAVE, BLOODLUST },

	{ HASTE, GREATER_HASTE, STATUS_HASTE },

	{ PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY },

	{ PHYSICAL_ENCHANT_STR, DRESS_MIGHTY }, { ADVANCE_SPIRIT, GIGANTIC } };

	/**
	 * 删除不能重复/同时使用的技能，图标更改为刚使用时的图标
	 * 
	 * @param cha
	 */
	private void deleteRepeatedSkills(final L1Character cha) {
		for (final int[] skills : REPEATEDSKILLS) {
			for (final int id : skills) {
				if (id == _skillId) {
					stopSkillList(cha, skills);
				}
			}
		}
	}

	/**
	 * 删除全部重复的正在使用的技能
	 * 
	 * @param cha
	 * @param repeat_skill
	 */
	private void stopSkillList(final L1Character cha, final int[] repeat_skill) {
		for (final int skillId : repeat_skill) {
			if (skillId != _skillId) {
				cha.removeSkillEffect(skillId);
			}
		}
	}

	// 技能使用延迟的设定
	private void setDelay() {
		if (_skill.getReuseDelay() > 0) {
			L1SkillDelay.onSkillUse(_user, _skill.getReuseDelay());
		}
	}

	/**
	 * 發動技能效果
	 */
	private void runSkill() {
		switch (_skillId) {
		case LIFE_STREAM:// 法師技能(治癒能量風暴)
			L1SpawnUtil.spawnEffect(81169, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, 0);
			return;

		case CUBE_IGNITION:// 幻術師技能(立方：燃燒)
			L1SpawnUtil.spawnEffect(80149, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, _skillId);
			return;

		case CUBE_QUAKE:// 幻術師技能(立方：地裂)
			L1SpawnUtil.spawnEffect(80150, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, _skillId);
			return;

		case CUBE_SHOCK:// 幻術師技能(立方：衝擊)
			L1SpawnUtil.spawnEffect(80151, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, _skillId);
			return;

		case CUBE_BALANCE:// 幻術師技能(立方：和諧)
			L1SpawnUtil.spawnEffect(80152, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, _skillId);
			return;

		case FIRE_WALL:// 法師技能(火牢)
			// System.out.println("法師技能(火牢):"+_targetX+"/"+_targetY);
			L1SpawnUtil.doSpawnFireWall(_user, _targetX, _targetY);
			return;
		}

		// 有使用次数限制的技能使用成功与否的判断
		for (final int skillId : EXCEPT_COUNTER_MAGIC) {
			if (_skillId == skillId) {
				_isCounterMagic = false; // カウンターマジック無効
				break;
			}
		}

		// NPCにショックスタンを使用させるとonActionでNullPointerExceptionが発生するため
		// とりあえずPCが使用した時のみ
		if ((_skillId == SHOCK_STUN) && (_user instanceof L1PcInstance)) {
			_target.onAction(_player);
		}

		if (!isTargetCalc(_target)) {
			return;
		}

		try {
			TargetStatus ts = null;
			L1Character cha = null;
			// int dmg = 0;
			int drainMana = 0;
			int heal = 0;
			boolean isSuccess;
			int undeadType = 0;

			for (final Iterator<TargetStatus> iter = _targetList.iterator(); iter.hasNext();) {
				ts = null;
				cha = null;
				// dmg = 0;
				heal = 0;
				isSuccess = false;
				undeadType = 0;

				ts = iter.next();
				cha = ts.getTarget();

				// System.out.println("發動技能效果");
				if (_npc != null) {
					// 施展者是寵物 XXX
					if (_npc instanceof L1PetInstance) {
						if (isParty(_npc, cha)) {
							ts.isCalc(false);
							_dmg = 0;
							continue;
						}
					}
					// 施展者是召喚獸
					if (_npc instanceof L1SummonInstance) {
						if (isParty(_npc, cha)) {
							ts.isCalc(false);
							_dmg = 0;
							continue;
						}
					}
				}

				if (!ts.isCalc() || !isTargetCalc(cha)) {
					ts.isCalc(false);
					continue; // 計算する必要がない。不需要计算
				}

				final L1Magic magic = new L1Magic(_user, cha);
				magic.setLeverage(getLeverage());

				if (cha instanceof L1MonsterInstance) { // アンデットの判定
					undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
				}

				// 確率系スキルで失敗が確定している場合
				// 概率系技能失败的确定
				if (((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) && isTargetFailure(cha)) {
					iter.remove();
					continue;
				}

				// ターゲットがPCの場合のみアイコンは送信する。
				// if (cha instanceof L1PcInstance) { // 不再僅限於PC
				// 只有在目标为pc的情况下发送图标，代表使用成功
				if (_skillTime == 0) {
					_getBuffIconDuration = _skill.getBuffDuration(); // 効果時間

				} else {
					_getBuffIconDuration = _skillTime; // パラメータのtimeが0以外なら、効果時間として設定する
				}
				// }

				deleteRepeatedSkills(cha); // 删除重复的技能

				// System.out.println("NPC對PC傷害計算 XXX:"+this._skill.getType());
				switch (_skill.getType()) {
				case L1Skills.TYPE_ATTACK:// 攻撃系スキル＆ターゲットが使用者以外であること。
					if (_user.getId() != cha.getId()) {
						if (_skillId == ICE_LANCE) {
							// 凍結機率
							if (_type == TYPE_GMBUFF) {
								isSuccess = true;

							} else {
								isSuccess = magic.calcProbabilityMagic(_skillId);
							}
						}
						// 攻击系技能和使用者除外
						if (isUseCounterMagic(cha)) { // カウンターマジックが発動した場合、リストから削除
							iter.remove();
							continue;
						}
						_dmg = magic.calcMagicDamage(_skillId);
						cha.removeSkillEffect(ERASE_MAGIC); // 魔法消除
					}
					break;

				case L1Skills.TYPE_CURSE:
				case L1Skills.TYPE_PROBABILITY: // 確率系スキル
					isSuccess = magic.calcProbabilityMagic(_skillId);
					if (_type == TYPE_GMBUFF) {
						isSuccess = true;
					}
					if (_skillId != ERASE_MAGIC) {
						cha.removeSkillEffect(ERASE_MAGIC); // 魔法消除
					}

					if (_skillId != FOG_OF_SLEEPING) {
						cha.removeSkillEffect(FOG_OF_SLEEPING); // 沉睡之霧
					}

					if (isSuccess) { // 成功したがカウンターマジックが発動した場合、リストから削除
						if (isUseCounterMagic(cha)) { // カウンターマジックが発動したか
							iter.remove();
							continue;
						}

					} else { // 失敗した場合、リストから削除
						if ((_skillId == FOG_OF_SLEEPING) && (cha instanceof L1PcInstance)) {
							final L1PcInstance pc = (L1PcInstance) cha;
							// 297 你感覺些微地暈眩。
							pc.sendPackets(new S_ServerMessage(297));
						}
						iter.remove();
						continue;
					}
					break;

				case L1Skills.TYPE_HEAL: // 回復系スキル
					// 回復量はマイナスダメージで表現
					_dmg = -1 * magic.calcHealing(_skillId);
					if (cha.hasSkillEffect(WATER_LIFE)) { // 水之元氣(回復量2倍)
						// this._dmg *= 2;
						// (>> 1: 除) (<< 1: 乘)
						_dmg = (_dmg << 1);
					}

					if (cha.hasSkillEffect(POLLUTE_WATER)) { // 污濁之水(回復量1/2倍)
						// this._dmg /= 2;
						// (>> 1: 除) (<< 1: 乘)
						_dmg = (_dmg >> 1);
					}

					if (cha.hasSkillEffect(ADLV80_2_2)) {// 污濁的水流(水龍副本 回復量1/2倍)
						_dmg = (_dmg >> 1);
					}

					if (cha.hasSkillEffect(ADLV80_2_3)) {
						_dmg *= -1;
					}

					// 使用魔法治癒術或生命的祝福(補血量X2倍) by terry0412
					if (cha.hasSkillEffect(MAGIC_ITEM_POWER_B)) {
						// this._dmg *= 2;
						// (>> 1: 除) (<< 1: 乘)
						_dmg = (_dmg << 1);
					}
					break;
				}

				// TODO SKILL移轉
				final SkillMode mode = L1SkillMode.get().getSkill(_skillId);
				if (mode != null) {
					// 施展者是PC
					if (_user instanceof L1PcInstance) {
						switch (_skillId) {
						case TELEPORT:// 指定傳送5
						case MASS_TELEPORT:// 集體傳送術69
							_dmg = mode.start(_player, cha, magic, _bookmarkId);
							break;

						case CALL_CLAN:// 呼喚盟友
						case RUN_CLAN:// 援護盟友118
							_dmg = mode.start(_player, cha, magic, _targetID);
							break;

						default:
							_dmg = mode.start(_player, cha, magic, _getBuffIconDuration);
							break;
						}
					}
					// 施展者是NPC
					if (_user instanceof L1NpcInstance) {
						_dmg = mode.start(_npc, cha, magic, _getBuffIconDuration);
					}

				} else {
					// ■■■■ 個別処理のあるスキルのみ書いてください。 ■■■■
					// 需要个别处理的技能（无法简单以技能的属系做判断）
					// すでにスキルを使用済みの場合なにもしない 重复使用无效的技能
					// ただしショックスタンは重ねがけ出来るため例外 冲击之晕例外
					if (cha.hasSkillEffect(_skillId)) {
						addMagicList(cha, true, isSuccess); // ターゲットに魔法の効果時間を上書き
						if (_skillId != SHAPE_CHANGE) { // 變形術
							continue;
						}
					}
				}

				addMagicList(cha, false, isSuccess); // ターゲットに魔法の効果時間を設定

				if ((_skillId == DETECTION // 無所遁形術
						)
						// added by terry0412
						|| (_skillId == FREEZING_BREATH // 暴龍之眼
						) || (_skillId == ARM_BREAKER)) { // 隱身破壞者
					if (cha instanceof L1NpcInstance) {
						final L1NpcInstance npc = (L1NpcInstance) cha;
						final int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							npc.appearOnGround(_player);
						}
					}

				} else if (_skillId == COUNTER_DETECTION) { // カウンター無所遁形術
					if (cha instanceof L1PcInstance) {
						_dmg = magic.calcMagicDamage(_skillId);

					} else if (cha instanceof L1NpcInstance) {
						final L1NpcInstance npc = (L1NpcInstance) cha;
						final int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							npc.appearOnGround(_player);
						} else {
							_dmg = 0;
						}

					} else {
						_dmg = 0;
					}

					// ★★★ 回復系スキル ★★★ 恢复系技能
				} else if ((_skillId == HEAL) || (_skillId == EXTRA_HEAL) || (_skillId == GREATER_HEAL) || (_skillId == FULL_HEAL) || (_skillId == HEAL_ALL) || (_skillId == NATURES_TOUCH)
						|| (_skillId == NATURES_BLESSING)) {
					if (_user instanceof L1PcInstance) {
						cha.removeSkillEffect(WATER_LIFE);
					}

					// ★★★ 攻撃系スキル ★★★ 攻击系技能
					// チルタッチ、バンパイアリックタッチ
				} else if ((_skillId == CHILL_TOUCH) || (_skillId == VAMPIRIC_TOUCH)) {
					heal = _dmg;

				} else if ((_skillId == 10026) || (_skillId == 10027) || (_skillId == 10028) || (_skillId == 10029)) { // 安息攻撃
					if (_user instanceof L1NpcInstance) {
						_user.broadcastPacketX8(new S_NpcChat(_npc, "$3717")); // さあ、おまえに安息を与えよう。

					} else {
						_player.broadcastPacketX8(new S_Chat(_player, "$3717")); // さあ、おまえに安息を与えよう。
					}

				} else if (_skillId == 10057) { // 引き寄せ
					L1Teleport.teleportToTargetFront(cha, _user, 1);

					// ★★★ 確率系スキル ★★★ 确率系技能
				} else if ((_skillId == SLOW) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE)) { // スロー、マス
					// スロー、エンタングル
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							continue;
						}
					}
					if (cha.getBraveSpeed() == 5) {// 具有強化勇水狀態
						continue;
					}
					switch (cha.getMoveSpeed()) {
					case 0:
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
						}
						cha.broadcastPacketAll(new S_SkillHaste(cha.getId(), 2, _getBuffIconDuration));
						cha.setMoveSpeed(2);
						break;

					case 1:
						int skillNum = 0;
						if (cha.hasSkillEffect(HASTE)) {
							skillNum = HASTE;

						} else if (cha.hasSkillEffect(GREATER_HASTE)) {
							skillNum = GREATER_HASTE;

						} else if (cha.hasSkillEffect(STATUS_HASTE)) {
							skillNum = STATUS_HASTE;
						}

						if (skillNum != 0) {
							cha.removeSkillEffect(skillNum);
							cha.removeSkillEffect(_skillId);
							cha.setMoveSpeed(0);
							continue;
						}
						break;
					}

					/*
					 * } else if ((this._skillId == CURSE_BLIND) || (this._skillId == DARKNESS)) { if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; if
					 * (pc.hasSkillEffect(STATUS_FLOATING_EYE)) { pc.sendPackets(new S_CurseBlind(2)); } else { pc.sendPackets(new S_CurseBlind(1)); } }
					 */

				} else if (_skillId == CURSE_POISON) {
					L1DamagePoison.doInfection(_user, cha, 3000, 5);

					/*
					 * } else if ((this._skillId == CURSE_PARALYZE) || (this._skillId == CURSE_PARALYZE2)) { if (!cha.hasSkillEffect(EARTH_BIND) && !cha.hasSkillEffect(ICE_LANCE) &&
					 * !cha.hasSkillEffect(FREEZING_BLIZZARD) && !cha.hasSkillEffect(FREEZING_BREATH)) { if (cha instanceof L1PcInstance) { L1CurseParalysis.curse(cha, 8000, 16000); } else
					 * if (cha instanceof L1MonsterInstance) { L1CurseParalysis.curse(cha, 0, 16000); } }
					 */

				} else if (_skillId == WEAKNESS) { // ウィークネス
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-5);
						pc.addHitup(-1);
					}

				} else if (_skillId == DISEASE) { // ディジーズ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-6);
						pc.addAc(12);
					}

				} else if ((_skillId == ICE_LANCE) // アイスランス
						|| (_skillId == FREEZING_BREATH)) { // フリージングブレス
					// 計算攻擊是否成功
					// this._isFreeze =
					// magic.calcProbabilityMagic(this._skillId);
					if (isSuccess) {
						// final int time = this._skill.getBuffDuration() *
						// 1000;
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							// 法師技能(冰矛圍籬)
							L1SpawnUtil.spawnEffect(81168, _skill.getBuffDuration(), cha.getX(), cha.getY(), cha.getMapId(), _user, 0);

							pc.sendPacketsAll(new S_Poison(pc.getId(), 2));
							pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));

						} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
							final L1NpcInstance npc = (L1NpcInstance) cha;
							// 法師技能(冰矛圍籬)
							L1SpawnUtil.spawnEffect(81168, _skill.getBuffDuration(), cha.getX(), cha.getY(), cha.getMapId(), _user, 0);

							npc.broadcastPacketAll(new S_Poison(npc.getId(), 2));
							npc.setParalyzed(true);
						}
					}

				} else if (_skillId == EARTH_BIND) { // アースバインド
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_Poison(pc.getId(), 2));
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));

					} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
						final L1NpcInstance npc = (L1NpcInstance) cha;

						npc.broadcastPacketAll(new S_Poison(npc.getId(), 2));
						npc.setParalyzed(true);
					}

					/*
					 * } else if (this._skillId == WIND_SHACKLE) { // ウィンド シャックル if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.sendPackets(new
					 * S_PacketBoxWindShackle(pc.getId(), this._getBuffIconDuration)); }
					 */

					/*
					 * } else if (this._skillId == CANCELLATION) { // 對象是NPC if (cha instanceof L1NpcInstance) { final L1NpcInstance npc = (L1NpcInstance) cha; // 取回NPCID final int npcId =
					 * npc.getNpcTemplate().get_npcId(); switch (npcId) { case KnightLv45_1._searcherid:// 調查員 if (npc.getGfxId() == npc.getTempCharGfx()) { npc.setTempCharGfx(1314);
					 * npc.broadcastPacketAll(new S_ChangeShape(npc, 1314)); return; } else { return; } case ElfLv45_2._npcId:// 獨角獸 if (npc.getGfxId() == npc.getTempCharGfx()) { final int
					 * x = npc.getX(); final int y = npc.getY(); final short m = npc.getMapId(); final int h = npc.getHeading(); npc.deleteMe(); L1SpawnUtil.spawnT(45641, x, y, m, h, 300);
					 * return; } else { return; } case EWLv40_1._roiid:// 羅伊 if (npc.getGfxId() == npc.getTempCharGfx()) { npc.setTempCharGfx(4310); npc.broadcastPacketAll(new
					 * S_ChangeShape(npc, 4310)); return; } else { return; } } } if ((this._player != null) && this._player.isInvisble()) { this._player.delInvis(); } if (!(cha instanceof
					 * L1PcInstance)) { final L1NpcInstance npc = (L1NpcInstance) cha; npc.setMoveSpeed(0); npc.setBraveSpeed(0); npc.broadcastPacketAll(new S_SkillHaste(cha.getId(), 0,
					 * 0)); npc.broadcastPacketAll(new S_SkillBrave(cha.getId(), 0, 0)); npc.setWeaponBreaked(false); npc.setParalyzed(false); npc.setParalysisTime(0); } // スキルの解除 技能解除 for
					 * (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) { if (this.isNotCancelable(skillNum) && !cha.isDead()) { continue; }
					 * cha.removeSkillEffect(skillNum); } // ステータス強化、異常の解除 cha.curePoison(); cha.cureParalaysis(); for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_END; skillNum++) {
					 * if ((skillNum == STATUS_CHAT_PROHIBITED // チャット禁止は解除しない ) || (skillNum == STATUS_CURSE_BARLOG // バルログの呪いは解除しない ) || (skillNum == STATUS_CURSE_YAHEE)) { //
					 * ヤヒの呪いは解除しない continue; } cha.removeSkillEffect(skillNum); } // 料理の解除 for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) { if
					 * (this.isNotCancelable(skillNum)) { continue; } cha.removeSkillEffect(skillNum); } // ヘイストアイテム装備時はヘイスト関連のスキルが何も掛かっていないはずなのでここで解除 if (cha instanceof L1PcInstance) {
					 * final L1PcInstance pc = (L1PcInstance) cha; if (pc.getHasteItemEquipped() > 0) { pc.setMoveSpeed(0); pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0)); } }
					 * cha.removeSkillEffect(STATUS_FREEZE); // Freeze解除 if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.sendPacketsAll(new
					 * S_CharVisualUpdate(pc)); if (pc.isPrivateShop()) { pc.sendPacketsAll(new S_DoActionShop(pc.getId(), pc.getShopChat())); } if (this._user instanceof L1PcInstance) {
					 * L1PinkName.onAction(pc, this._user); } }
					 */

				} else if (_skillId == TURN_UNDEAD) {
					if ((undeadType == 1) || (undeadType == 3)) {
						// ダメージを対象のHPとする。
						_dmg = cha.getCurrentHp();
					}

				} else if (_skillId == MANA_DRAIN) { // マナ ドレイン
					final Random random = new Random();
					final int chance = random.nextInt(10) + 5;
					drainMana = chance + (_user.getInt() / 2);
					if (cha.getCurrentMp() < drainMana) {
						drainMana = cha.getCurrentMp();
					}

				} else if (_skillId == WEAPON_BREAK) { // ウェポン ブレイク
					/*
					 * 対NPCの場合、L1Magicのダメージ算出でダメージ1/2としているので こちらには、対PCの場合しか記入しない。 損傷量は1~(int/3)まで
					 */
					if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							final L1ItemInstance weapon = pc.getWeapon();
							if (weapon != null) {
								final Random random = new Random();
								final int weaponDamage = random.nextInt(_user.getInt() / 3) + 1;
								// \f1あなたの%0が損傷しました。
								pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
								pc.getInventory().receiveDamage(weapon, weaponDamage);
							}
						}
					} else {
						((L1NpcInstance) cha).setWeaponBreaked(true);
					}

				} else if (_skillId == FOG_OF_SLEEPING) {
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
					}
					cha.setSleeped(true);

				} else if (_skillId == GUARD_BRAKE) { // ガードブレイク
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(15);
					}

				} else if (_skillId == HORROR_OF_DEATH) { // ホラーオブデス
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addStr(-5);
						pc.addInt(-5);
					}
				} else if (_skillId == TRUE_TARGET) {
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						if (_player.getClan() == null || _player.getClanid() == pc.getClanid()) {
							return;
						}
						GeneralThreadPool.get().execute(new TrueTarget(_player, pc));

					} else if (cha instanceof L1MonsterInstance) {
						final L1MonsterInstance mob = (L1MonsterInstance) cha;
						if (_player.getClan() == null) {
							return;
						}
						GeneralThreadPool.get().execute(new TrueTarget(_player, mob));
					}
				}

				// ●●●● PCにしか効果のないスキル ●●●● 只允许pc使用的技能
				if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
					// ★★★ 特殊系スキル★★★ 特殊技能
					if (_skillId == CREATE_MAGICAL_WEAPON) { // クリエイト
						// マジカル ウェポン
						final L1PcInstance pc = (L1PcInstance) cha;
						final L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
						if ((item != null) && (item.getItem().getType2() == 1)) {
							final int item_type = item.getItem().getType2();
							final int safe_enchant = item.getItem().get_safeenchant();
							final int enchant_level = item.getEnchantLevel();
							String item_name = item.getName();
							if (safe_enchant < 0) { // 強化不可
								pc.sendPackets( // \f1何も起きませんでした。
								new S_ServerMessage(79));
							} else if (safe_enchant == 0) { // 安全圏+0
								pc.sendPackets( // \f1何も起きませんでした。
								new S_ServerMessage(79));
							} else if ((item_type == 1) && (enchant_level == 0)) {
								if (!item.isIdentified()) {// 未鑑定
									pc.sendPackets( // \f1%0が%2%1光ります。
									new S_ServerMessage(161, item_name, "$245", "$247"));
								} else {
									item_name = "+0 " + item_name;
									pc.sendPackets( // \f1%0が%2%1光ります。
									new S_ServerMessage(161, "+0 " + item_name, "$245", "$247"));
								}
								item.setEnchantLevel(1);
								pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
							} else {
								pc.sendPackets( // \f1何も起きませんでした。
								new S_ServerMessage(79));
							}
						} else {
							pc.sendPackets( // \f1何も起きませんでした。
							new S_ServerMessage(79));
						}

					} else if (_skillId == BRING_STONE) { // ブリング ストーン
						final L1PcInstance pc = (L1PcInstance) cha;
						final Random random = new Random();
						final L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
						if (item != null) {
							final int dark = (int) (10 + (pc.getLevel() * 0.8) + ((pc.getWis() - 6) * 1.2));
							final int brave = (int) (dark / 2.1);
							final int wise = (int) (brave / 2.0);
							final int kayser = (int) (wise / 1.9);
							final int chance = random.nextInt(100) + 1;
							if (item.getItem().getItemId() == 40320) {
								pc.getInventory().removeItem(item, 1);
								if (dark >= chance) {
									pc.getInventory().storeItem(40321, 1);
									pc.sendPackets(new S_ServerMessage(403, "$2475")); // %0を手に入れました。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
								}
							} else if (item.getItem().getItemId() == 40321) {
								pc.getInventory().removeItem(item, 1);
								if (brave >= chance) {
									pc.getInventory().storeItem(40322, 1);
									pc.sendPackets(new S_ServerMessage(403, "$2476")); // %0を手に入れました。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
								}
							} else if (item.getItem().getItemId() == 40322) {
								pc.getInventory().removeItem(item, 1);
								if (wise >= chance) {
									pc.getInventory().storeItem(40323, 1);
									pc.sendPackets(new S_ServerMessage(403, "$2477")); // %0を手に入れました。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
								}
							} else if (item.getItem().getItemId() == 40323) {
								pc.getInventory().removeItem(item, 1);
								if (kayser >= chance) {
									pc.getInventory().storeItem(40324, 1);
									pc.sendPackets(new S_ServerMessage(403, "$2478")); // %0を手に入れました。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
								}
							}
						}

						/*
						 * } else if ((this._skillId == LESSER_ELEMENTAL) || (this._skillId == GREATER_ELEMENTAL)) { // レッサーエレメンタル、グレーターエレメンタル final L1PcInstance pc = (L1PcInstance) cha;
						 * final int attr = pc.getElfAttr(); if (attr != 0) { // 無属性でなければ実行 if (!pc.getMap().isRecallPets()) { // 353：在這附近無法召喚怪物。 pc.sendPackets(new S_ServerMessage(353));
						 * return; } int petcost = 0; final Object[] petlist = pc.getPetList().values() .toArray(); for (final Object pet : petlist) { // 現在のペットコスト petcost +=
						 * ((L1NpcInstance) pet) .getPetcost(); } if (petcost == 0) { // 1匹も所属NPCがいなければ実行 int summonid = 0; int summons[]; if (this._skillId == LESSER_ELEMENTAL) { //
						 * レッサーエレメンタル[地,火,水,風] summons = new int[] { 45306, 45303, 45304, 45305 }; } else { // グレーターエレメンタル[地,火,水,風] summons = new int[] { 81053, 81050, 81051, 81052 }; }
						 * int npcattr = 1; for (int i = 0; i < summons.length; i++) { if (npcattr == attr) { summonid = summons[i]; i = summons.length; } npcattr *= 2; } // 特殊設定の場合ランダムで出現
						 * if (summonid == 0) { final Random random = new Random(); final int k3 = random.nextInt(4); summonid = summons[k3]; } final L1Npc npcTemp = NpcTable.get()
						 * .getTemplate(summonid); final L1SummonInstance summon = new L1SummonInstance( npcTemp, pc); summon.setPetcost(pc.getCha() + 7); // 精霊の他にはNPCを所属させられない } }
						 */

					} else if (_skillId == ABSOLUTE_BARRIER) { // アブソルート
																// バリア
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.stopHpRegeneration();
						pc.stopMpRegeneration();
					}

					// ★★★ 変化系スキル（エンチャント） ★★★ 变化系技能
					if (_skillId == LIGHT) { // ライト
						// addMagicList()後に、turnOnOffLight()でパケット送信

					} else if (_skillId == GLOWING_WEAPON) { // 灼熱武器
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addHitup(5);
						pc.addBowHitup(5);
						pc.sendPackets(new S_PacketBoxIconAura(113, _getBuffIconDuration));

					} else if (_skillId == SHINING_AURA) { // 閃亮之盾
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-8);
						pc.sendPackets(new S_PacketBoxIconAura(114, _getBuffIconDuration));

					} else if (_skillId == BRAVE_MENTAL) { // 勇猛意志
						_player.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));

					} else if (_skillId == SHIELD) { // シールド
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-2);
						pc.sendPackets(new S_SkillIconShield(2, _getBuffIconDuration));

					} else if (_skillId == SHADOW_ARMOR) { // シャドウ アーマー
						final L1PcInstance pc = (L1PcInstance) cha;
						// 魔法防禦值+5 by terry0412
						pc.addMr(5);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));

					} else if (_skillId == DRESS_DEXTERITY) { // ドレス
																// デクスタリティー
						final L1PcInstance pc = (L1PcInstance) cha;
						// 改成 敏捷+3 by terry0412
						pc.addDex((byte) 3);
						pc.sendPackets(new S_Dexup(pc, 3, _getBuffIconDuration));

					} else if (_skillId == DRESS_MIGHTY) { // ドレス マイティー
						final L1PcInstance pc = (L1PcInstance) cha;
						// 改成 力量+3 by terry0412
						pc.addStr((byte) 3);
						pc.sendPackets(new S_Strup(pc, 3, _getBuffIconDuration));

					} else if (_skillId == EARTH_GUARDIAN) { // アース ブレス
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDamageReductionByArmor(2);
						pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));

					} else if (_skillId == RESIST_MAGIC) { // レジスト マジック
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(10);
						pc.sendPackets(new S_SPMR(pc));

					} else if (_skillId == CLEAR_MIND) { // クリアー マインド
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addWis((byte) 3);
						pc.resetBaseMr();

					} else if (_skillId == RESIST_ELEMENTAL) { // レジスト
																// エレメント
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addWind(10);
						pc.addWater(10);
						pc.addFire(10);
						pc.addEarth(10);
						pc.sendPackets(new S_OwnCharAttrDef(pc));

					} else if (_skillId == ELEMENTAL_PROTECTION) { // エレメンタルプロテクション
						final L1PcInstance pc = (L1PcInstance) cha;
						final int attr = pc.getElfAttr();
						if (attr == 1) {
							pc.addEarth(50);
						} else if (attr == 2) {
							pc.addFire(50);
						} else if (attr == 4) {
							pc.addWater(50);
						} else if (attr == 8) {
							pc.addWind(50);
						}
					} else if ((_skillId == INVISIBILITY) || (_skillId == BLIND_HIDING)) { // インビジビリティ、ブラインドハイディング
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Invis(pc.getId(), 1));
						pc.broadcastPacketAll(new S_RemoveObject(pc));

					} else if (_skillId == IRON_SKIN) { // アイアン スキン
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-10);
						pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));

					} else if (_skillId == EARTH_SKIN) { // アース スキン
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-6);
						pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));

					} else if (_skillId == PHYSICAL_ENCHANT_STR) { // フィジカルエンチャント：STR
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addStr((byte) 5);
						pc.sendPackets(new S_Strup(pc, 1, _getBuffIconDuration));

					} else if (_skillId == PHYSICAL_ENCHANT_DEX) { // フィジカルエンチャント：DEX
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDex((byte) 5);
						pc.sendPackets(new S_Dexup(pc, 1, _getBuffIconDuration));

					} else if (_skillId == FIRE_WEAPON) { // ファイアー ウェポン
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.sendPackets(new S_PacketBoxIconAura(147, _getBuffIconDuration));

					} else if (_skillId == DANCING_BLAZE) { // ファイアー ブレス
						final L1PcInstance pc = (L1PcInstance) cha;
						L1BuffUtil.braveStart(pc);
						pc.sendPackets(new S_SkillBrave(pc.getId(), 1, _getBuffIconDuration));
						pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 1, 0));
						pc.setBraveSpeed(1);

					} else if (_skillId == BURNING_WEAPON) { // バーニング ウェポン
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(6);
						pc.addHitup(3);
						pc.sendPackets(new S_PacketBoxIconAura(162, _getBuffIconDuration));

					} else if (_skillId == WIND_SHOT) { // ウィンド ショット
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(6);
						pc.sendPackets(new S_PacketBoxIconAura(148, _getBuffIconDuration));

					} else if (_skillId == STORM_EYE) { // ストーム アイ
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(2);
						pc.addBowDmgup(3);
						pc.sendPackets(new S_PacketBoxIconAura(155, _getBuffIconDuration));

					} else if (_skillId == STORM_SHOT) { // ストーム ショット
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowDmgup(6);
						pc.addBowHitup(3);
						pc.sendPackets(new S_PacketBoxIconAura(165, _getBuffIconDuration));

					} else if (_skillId == BERSERKERS) { // バーサーカー
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(10);
						pc.addDmgup(5);
						pc.addHitup(2);

					} else if (_skillId == SHAPE_CHANGE) { // シェイプ チェンジ
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_ShowPolyList(pc.getId()));
						if (!pc.isShapeChange()) {
							pc.setSummonMonster(false);
							pc.setShapeChange(true);
						}

					} else if (_skillId == GREATER_HASTE) { // グレーター ヘイスト
						final L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							continue;
						}
						if (pc.getMoveSpeed() != 2) { // スロー中以外
							pc.setDrink(false);
							pc.setMoveSpeed(1);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
							pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));

						} else { // スロー中
							int skillNum = 0;
							if (pc.hasSkillEffect(SLOW)) {
								skillNum = SLOW;
							} else if (pc.hasSkillEffect(MASS_SLOW)) {
								skillNum = MASS_SLOW;
							} else if (pc.hasSkillEffect(ENTANGLE)) {
								skillNum = ENTANGLE;
							}
							if (skillNum != 0) {
								pc.removeSkillEffect(skillNum);
								pc.removeSkillEffect(GREATER_HASTE);
								pc.setMoveSpeed(0);
								continue;
							}
						}

					} else if ((_skillId == HOLY_WALK) || (_skillId == MOVING_ACCELERATION) || (_skillId == WIND_WALK)) { // ホーリーウォーク、ムービングアクセレーション、ウィンドウォーク
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.setBraveSpeed(4);
						pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
						pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 4, 0));

						/*
						 * } else if (this._skillId == BLOODLUST) { // ブラッドラスト final L1PcInstance pc = (L1PcInstance) cha; // 強化勇氣的藥水效果 if (pc.hasSkillEffect(STATUS_BRAVE2)) { //
						 * 1,413：目前情況是無法使用。 pc.sendPackets(new S_ServerMessage(1413)); return; } // 具有生命之樹果實效果 if (pc.hasSkillEffect(L1SkillId.STATUS_RIBRAVE)) { // 1,413：目前情況是無法使用。
						 * pc.sendPackets(new S_ServerMessage(1413)); return; } // 勇敢效果 抵銷對應技能 L1BuffUtil.braveStart(pc); pc.setBraveSpeed(6); pc.sendPackets(new S_SkillBrave(pc.getId(),
						 * 6, this._getBuffIconDuration)); pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 6, 0));
						 */

					} else if (_skillId == ILLUSION_OGRE) { // イリュージョン：オーガ
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.addHitup(4);

						/*
						 * } else if (this._skillId == ILLUSION_LICH) { // イリュージョン：リッチ final L1PcInstance pc = (L1PcInstance) cha; pc.addSp(2); pc.sendPackets(new S_SPMR(pc));
						 */

					} else if (_skillId == ILLUSION_DIA_GOLEM) { // イリュージョン：ダイアモンドゴーレム
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-8);

						/*
						 * } else if (this._skillId == INSIGHT) { // インサイト final L1PcInstance pc = (L1PcInstance) cha; pc.addStr((byte) 1); pc.addCon((byte) 1); pc.addDex((byte) 1);
						 * pc.addWis((byte) 1); pc.addInt((byte) 1);
						 */

						/*
						 * } else if (this._skillId == PANIC) {// 恐惧217 final L1PcInstance pc = (L1PcInstance) cha; pc.addStr((byte) -1); pc.addCon((byte) -1); pc.addDex((byte) -1);
						 * pc.addWis((byte) -1); pc.addInt((byte) -1);
						 */

					} else if (_skillId == THUNDER_GRAB) {// 夺命之雷192
						final Random rad = new Random();
						final int i = rad.nextInt(100) + 1;
						if (i <= 60) {
							if (!cha.hasSkillEffect(L1SkillId.MOVE_STOP)) {
								cha.setSkillEffect(L1SkillId.MOVE_STOP, 4000);
								// LOLI 修正為可被攻擊 無法移動
								if (cha instanceof L1PcInstance) {
									final L1PcInstance pc = (L1PcInstance) cha;
									pc.sendPacketsX8(new S_SkillSound(pc.getId(), 4184));
									pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));

								} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
									final L1NpcInstance tgnpc = (L1NpcInstance) cha;
									tgnpc.setParalyzed(true);
								}
							}
							/*
							 * final L1PcInstance pc = (L1PcInstance) cha; pc.setSkillEffect(STATUS_FREEZE, 4000); pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
							 */
						}

					} else if (_skillId == GIGANTIC) {
						final L1PcInstance pc = (L1PcInstance) cha;
						final int HP = (pc.getBaseMaxHp() / 100) * (pc.getLevel() / 2);
						pc.setGiganticHp(HP);
						pc.addMaxHp(pc.getGiganticHp());
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
					}
				}

				// ●●●● NPCにしか効果のないスキル ●●●● npc使用的技能
				if ((_calcType == PC_NPC) || (_calcType == NPC_NPC)) {
					// ★★★ ペット系スキル ★★★ 宠物使用的技能
					if ((_skillId == TAMING_MONSTER) && ((L1MonsterInstance) cha).getNpcTemplate().isTamable()) { // テイミングモンスター
						int petcost = 0;
						final Object[] petlist = _user.getPetList().values().toArray();
						for (final Object pet : petlist) {
							// 現在のペットコスト
							petcost += ((L1NpcInstance) pet).getPetcost();
						}
						int charisma = _user.getCha();
						if (_player.isElf()) { // エルフ
							charisma += 12;

						} else if (_player.isWizard()) { // ウィザード
							charisma += 6;
						}
						charisma -= petcost;
						if (charisma >= 6) { // ペットコストの確認
							final L1SummonInstance summon = new L1SummonInstance(_targetNpc, _user, false);
							_target = summon; // ターゲット入替え

						} else {
							_player.sendPackets(new S_ServerMessage(319)); // \f1これ以上のモンスターを操ることはできません。
						}

					} else if (_skillId == CREATE_ZOMBIE) { // クリエイトゾンビ
						int petcost = 0;
						final Object[] petlist = _user.getPetList().values().toArray();
						for (final Object pet : petlist) {
							// 現在のペットコスト
							petcost += ((L1NpcInstance) pet).getPetcost();
						}
						int charisma = _user.getCha();
						if (_player.isElf()) { // エルフ
							charisma += 12;
						} else if (_player.isWizard()) { // ウィザード
							charisma += 6;
						}
						charisma -= petcost;
						if (charisma >= 6) { // ペットコストの確認
							final L1SummonInstance summon = new L1SummonInstance(_targetNpc, _user, true);
							_target = summon; // ターゲット入替え
						} else {
							_player.sendPackets(new S_ServerMessage(319)); // \f1これ以上のモンスターを操ることはできません。
						}

					} else if (_skillId == WEAK_ELEMENTAL) { // ウィーク エレメンタル
						if (cha instanceof L1MonsterInstance) {
							final L1Npc npcTemp = ((L1MonsterInstance) cha).getNpcTemplate();
							final int weakAttr = npcTemp.get_weakAttr();
							if ((weakAttr & 1) == 1) { // 地
								cha.broadcastPacketX8(new S_SkillSound(cha.getId(), 2169));
							}
							if ((weakAttr & 2) == 2) { // 火
								cha.broadcastPacketX8(new S_SkillSound(cha.getId(), 2167));
							}
							if ((weakAttr & 4) == 4) { // 水
								cha.broadcastPacketX8(new S_SkillSound(cha.getId(), 2166));
							}
							if ((weakAttr & 8) == 8) { // 風
								cha.broadcastPacketX8(new S_SkillSound(cha.getId(), 2168));
							}
						}

					} else if (_skillId == RETURN_TO_NATURE) { // リターントゥネイチャー
						if (cha instanceof L1SummonInstance) {
							final L1SummonInstance summon = (L1SummonInstance) cha;
							summon.broadcastPacketX10(new S_SkillSound(summon.getId(), 2245));
							summon.returnToNature();

						} else {
							if (_user instanceof L1PcInstance) {
								_player.sendPackets(new S_ServerMessage(79));
							}
						}
					}
				}

				// ■■■■ 個別処理ここまで ■■■■

				if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 1)) {
					_dmg *= -1; // もし、アンデットで回復系スキルならばダメージになる。
				}

				if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 3)) {
					_dmg = 0; // もし、アンデット系ボスで回復系スキルならば無効
				}

				if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) && (_dmg < 0)) { // ガーディアンタワー、ドアにヒールを使用
					_dmg = 0;
				}

				// System.out.println("dmg1:"+dmg); XXX
				if ((_dmg != 0) || (drainMana != 0)) {
					// System.out.println("結果質2:(HP) " + this._dmg);
					magic.commit(_dmg, drainMana); // ダメージ系、回復系の値をターゲットにコミットする。
				}

				// System.out.println("dmg2:"+dmg);
				// ヒール系の他に、別途回復した場合（V-Tなど）
				if (heal > 0) {
					// System.out.println("dmg3:"+dmg);
					if ((heal + _user.getCurrentHp()) > _user.getMaxHp()) {
						_user.setCurrentHp(_user.getMaxHp());

					} else {
						_user.setCurrentHp(heal + _user.getCurrentHp());
					}
				}

				if (cha instanceof L1PcInstance) { // ターゲットがPCならば、ACとステータスを送信
					final L1PcInstance pc = (L1PcInstance) cha;
					pc.turnOnOffLight();
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
					sendHappenMessage(pc); // ターゲットにメッセージを送信
				}

				if (cha instanceof L1PcInstance) { // ターゲットがPCならば、ライト状態を更新
					final L1PcInstance pc = (L1PcInstance) cha;
					pc.turnOnOffLight();
				}
			}

			if ((_skillId == DETECTION // 無所遁形術
					)
					|| (_skillId == COUNTER_DETECTION // 強力無所遁形術
					)
					// added by terry0412
					|| (_skillId == FREEZING_BREATH // 暴龍之眼
					) || (_skillId == ARM_BREAKER)) { // 隱身破壞者
				detection(_player);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void detection(final L1PcInstance pc) {
		if (!pc.isGmInvis() && pc.isInvisble()) { // 自分
			pc.delInvis();
			pc.beginInvisTimer();
		}

		for (final L1PcInstance tgt : World.get().getVisiblePlayer(pc)) {
			if (!tgt.isGmInvis() && tgt.isInvisble()) {
				tgt.delInvis();
			}
		}

		// 偵測陷阱的處理
		WorldTrap.get().onDetection(pc);
	}

	/**
	 * 目標判定
	 * 
	 * @param cha
	 * @param cha
	 * @return
	 */
	private boolean isTargetCalc(final L1Character cha) {
		// 攻撃魔法のNon−PvP判定
		if (_skill.getTarget().equals("attack") && (_skillId != TURN_UNDEAD)) { // 攻撃魔法
			if (isPcSummonPet(cha)) { // 対象がPC、サモン、ペット
				if (_player.isSafetyZone() || cha.isSafetyZone() || // 攻撃する側または攻撃される側がセーフティーゾーン
						_player.checkNonPvP(_player, cha)) { // Non-PvP設定
					return false;
				}
			}
		}
		switch (_skillId) {
		// 沉睡之霧
		case FOG_OF_SLEEPING:
			if (_user.getId() == cha.getId()) {
				return false;
			}
			break;

		// 集體緩速術
		case MASS_SLOW:
			if (_user.getId() == cha.getId()) {
				return false;
			}

			if (cha instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) cha;
				if (_user.getId() == summon.getMaster().getId()) {
					return false;
				}

			} else if (cha instanceof L1PetInstance) {
				final L1PetInstance pet = (L1PetInstance) cha;
				if (_user.getId() == pet.getMaster().getId()) {
					return false;
				}
			}
			break;

		// 集體傳送術
		case MASS_TELEPORT:
			if (_user.getId() != cha.getId()) {
				return false;
			}
			break;
		}
		return true;
	}

	/**
	 * 目標對象 是否為寵物 召喚獸 虛擬人物
	 * 
	 * @param cha
	 * @return
	 */
	private boolean isPcSummonPet(final L1Character cha) {
		// PC 對 PC
		switch (_calcType) {
		case PC_PC:
			return true;

			// PC 對 NPC
		case PC_NPC:
			// 目標對象為召喚獸
			if (cha instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) cha;
				// 目標對象具有主人
				if (summon.isExsistMaster()) {
					return true;
				}
			}
			// 目標對象為寵物
			if (cha instanceof L1PetInstance) {
				return true;
			}
			if (cha instanceof L1DeInstance) {
				return true;
			}
			return false;

		default:
			return false;
		}
	}

	// ターゲットに対して必ず失敗になるか返す
	private boolean isTargetFailure(final L1Character cha) {
		boolean isTU = false;
		boolean isErase = false;
		boolean isManaDrain = false;
		int undeadType = 0;
		if ((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) { // ガーディアンタワー、ドアには確率系スキル無効
			return true;
		}

		if (cha instanceof L1PcInstance) { // 対PCの場合
			if ((_calcType == PC_PC) && _player.checkNonPvP(_player, cha)) { // Non-PvP設定
				final L1PcInstance pc = (L1PcInstance) cha;
				if ((_player.getId() == pc.getId()) || ((pc.getClanid() != 0) && (_player.getClanid() == pc.getClanid()))) {
					return false;
				}
				return true;
			}
			return false;
		}

		if (cha instanceof L1MonsterInstance) { // ターンアンデット可能か判定
			isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
		}

		if (cha instanceof L1MonsterInstance) { // イレースマジック可能か判定
			isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
		}

		if (cha instanceof L1MonsterInstance) { // アンデットの判定
			undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
		}

		// マナドレインが可能か？
		if (cha instanceof L1MonsterInstance) {
			isManaDrain = true;
		}
		/*
		 * 成功除外条件１：T-Uが成功したが、対象がアンデットではない。 成功除外条件２：T-Uが成功したが、対象にはターンアンデット無効。 成功除外条件３：スロー、マススロー、マナドレイン、エンタングル、イレースマジック、ウィンドシャックル無効 成功除外条件４：マナドレインが成功したが、モンスター以外の場合
		 */
		if (((_skillId == TURN_UNDEAD) && ((undeadType == 0) || (undeadType == 2)))
				|| ((_skillId == TURN_UNDEAD) && (isTU == false))
				|| (((_skillId == ERASE_MAGIC) || (_skillId == SLOW) || (_skillId == MANA_DRAIN) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE) || (_skillId == WIND_SHACKLE)) && (isErase == false))
				|| ((_skillId == MANA_DRAIN) && (isManaDrain == false))) {
			return true;
		}
		return false;
	}

	// カウンターマジックが発動したか返す
	private boolean isUseCounterMagic(final L1Character cha) {
		// カウンターマジック有効なスキルでカウンターマジック中
		if (_isCounterMagic && cha.hasSkillEffect(COUNTER_MAGIC)) {
			cha.removeSkillEffect(COUNTER_MAGIC);
			final int castgfx = SkillsTable.get().getTemplate(COUNTER_MAGIC).getCastGfx2();
			cha.broadcastPacketX10(new S_SkillSound(cha.getId(), castgfx));
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
			}
			return true;
		}
		return false;
	}

	class TrueTarget implements Runnable {

		private final L1PcInstance _pc;
		private final L1Character _target;

		public TrueTarget(final L1PcInstance pc, final L1Character target) {
			_pc = pc;
			_target = target;
		}

		/**
		 * 精準目標
		 * 
		 * 新特效 12299
		 * 
		 * @param cha
		 * @return
		 */

		@Override
		public void run() {
			try {
				_pc.sendPackets(new S_TrueTarget(_targetID, 12299, 1));

				while (_target.hasSkillEffect(TRUE_TARGET)) {
					if (_target.isDead()) {
						break;
					}
					for (final L1PcInstance pc : World.get().getVisiblePlayer(_target)) {
						if (_pc.getClanid() == pc.getClanid()) {
							pc.sendPackets(new S_TrueTarget(_targetID, 12299, 1));
						}
					}
					Thread.sleep(500);
				}

				for (final L1PcInstance pc : World.get().getVisiblePlayer(_target)) {
					if (_pc.getClanid() == pc.getClanid()) {
						pc.sendPackets(new S_TrueTarget(_targetID, 12299, 0));
					}
				}

			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

}
