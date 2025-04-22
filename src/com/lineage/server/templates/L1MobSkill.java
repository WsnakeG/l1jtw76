package com.lineage.server.templates;

public class L1MobSkill implements Cloneable {

	public static final int TYPE_NONE = 0;

	public static final int TYPE_PHYSICAL_ATTACK = 1;// 物理攻擊

	public static final int TYPE_MAGIC_ATTACK = 2;// 魔法攻擊

	public static final int TYPE_SUMMON = 3;// 召喚屬下

	public static final int TYPE_POLY = 4;// 強制變身

	public static final int AREA_SKILLS = 5; // 群體技能 (changed by terry0412)

	public static final int CHANGE_TARGET_NO = 0;

	public static final int CHANGE_TARGET_COMPANION = 1;

	public static final int CHANGE_TARGET_ME = 2;

	public static final int CHANGE_TARGET_RANDOM = 3;

	private final int skillSize;// 技能數量

	@Override
	public L1MobSkill clone() {
		try {
			return (L1MobSkill) (super.clone());
		} catch (final CloneNotSupportedException e) {
			throw (new InternalError(e.getMessage()));
		}
	}

	/**
	 * 技能數量
	 * 
	 * @return
	 */
	public int getSkillSize() {
		return skillSize;
	}

	/**
	 * 技能數量
	 * 
	 * @param sSize
	 */
	public L1MobSkill(final int sSize) {
		skillSize = sSize;

		type = new int[skillSize];
		triRnd = new int[skillSize];
		triHp = new int[skillSize];
		triCompanionHp = new int[skillSize];
		triRange = new int[skillSize];
		triCount = new int[skillSize];
		changeTarget = new int[skillSize];
		range = new int[skillSize];
		areaWidth = new int[skillSize];
		areaHeight = new int[skillSize];
		leverage = new int[skillSize];
		skillId = new int[skillSize];
		gfxid = new int[skillSize];
		actid = new int[skillSize];
		summon = new int[skillSize];
		summonMin = new int[skillSize];
		summonMax = new int[skillSize];
		polyId = new int[skillSize];
	}

	private int mobid;

	/**
	 * NPC編號
	 * 
	 * @return
	 */
	public int get_mobid() {
		return mobid;
	}

	/**
	 * NPC編號
	 * 
	 * @param i
	 */
	public void set_mobid(final int i) {
		mobid = i;
	}

	private String mobName;

	/**
	 * NPC名稱
	 * 
	 * @return
	 */
	public String getMobName() {
		return mobName;
	}

	/**
	 * NPC名稱
	 * 
	 * @param s
	 */
	public void setMobName(final String s) {
		mobName = s;
	}

	private final int type[];// 技能類型

	/**
	 * 技能類型<BR>
	 * 1 物理攻擊<BR>
	 * 2 魔法攻擊<BR>
	 * 3 召喚屬下<BR>
	 * 4 強制變身<BR>
	 * 5 群體衝暈<BR>
	 * 6 群體相消<BR>
	 * 7 群體壞物<BR>
	 * 
	 * @param idx
	 * @return
	 */
	public int getType(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return type[idx];
	}

	/**
	 * 技能類型<BR>
	 * 1 物理攻擊<BR>
	 * 2 魔法攻擊<BR>
	 * 3 召喚屬下<BR>
	 * 4 強制變身<BR>
	 * 5 群體衝暈<BR>
	 * 6 群體相消<BR>
	 * 7 群體壞物<BR>
	 * 
	 * @param idx
	 * @param i
	 */
	public void setType(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		type[idx] = i;
	}

	private final int triRnd[];

	/**
	 * 發動機率(%)
	 * 
	 * @param idx
	 * @return
	 */
	public int getTriggerRandom(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return triRnd[idx];
	}

	/**
	 * 發動機率(%)
	 * 
	 * @param idx
	 * @param i
	 */
	public void setTriggerRandom(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		triRnd[idx] = i;
	}

	int triHp[];

	/**
	 * HP條件發動(低於設定值)
	 * 
	 * @param idx
	 * @return
	 */
	public int getTriggerHp(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return triHp[idx];
	}

	/**
	 * HP條件發動(HP低於設定值)
	 * 
	 * @param idx
	 * @param i
	 */
	public void setTriggerHp(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		triHp[idx] = i;
	}

	int triCompanionHp[];

	/**
	 * 同族HP條件發動(同族HP低於設定值)
	 * 
	 * @param idx
	 * @return
	 */
	public int getTriggerCompanionHp(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return triCompanionHp[idx];
	}

	/**
	 * 同族HP條件發動(同族HP低於設定值)
	 * 
	 * @param idx
	 * @param i
	 */
	public void setTriggerCompanionHp(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		triCompanionHp[idx] = i;
	}

	int triRange[];

	/**
	 * 設定值小於0 則小於設定距離(轉正整數)發動技能<BR>
	 * 設定值大於0 則超出設定距離發動技能
	 * 
	 * @param idx
	 * @return
	 */
	public int getTriggerRange(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		Math.abs(idx);
		return triRange[idx];
	}

	/**
	 * 設定值小於0 則小於設定距離(轉正整數)發動技能<BR>
	 * 設定值大於0 則超出設定距離發動技能
	 * 
	 * @param idx
	 * @param i
	 */
	public void setTriggerRange(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		triRange[idx] = i;
	}

	/**
	 * 物件距離是否達成施展技能距離
	 * 
	 * @param idx
	 * @param distance
	 * @return
	 */
	public boolean isTriggerDistance(final int idx, final int distance) {
		final int triggerRange = getTriggerRange(idx);

		if (((triggerRange < 0) && (distance <= Math.abs(triggerRange)))
				|| ((triggerRange > 0) && (distance >= triggerRange))) {
			return true;
		}
		return false;
	}

	int triCount[];

	/**
	 * 技能發動次數
	 * 
	 * @param idx
	 * @return
	 */
	public int getTriggerCount(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return triCount[idx];
	}

	/**
	 * 技能發動次數
	 * 
	 * @param idx
	 * @param i
	 */
	public void setTriggerCount(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		triCount[idx] = i;
	}

	int changeTarget[];

	/**
	 * 技能發動時目標判定<BR>
	 * 1:目前攻擊者<BR>
	 * 2:目前攻擊自己的對象<BR>
	 * 3:範圍目標<BR>
	 * 
	 * @param idx
	 * @return
	 */
	public int getChangeTarget(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return changeTarget[idx];
	}

	/**
	 * 技能發動時目標判定<BR>
	 * 1:目前攻擊者<BR>
	 * 2:目前攻擊自己的對象<BR>
	 * 3:範圍目標<BR>
	 * 
	 * @param idx
	 * @param i
	 */
	public void setChangeTarget(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		changeTarget[idx] = i;
	}

	int range[];

	/**
	 * 攻擊距離(物理攻擊設置)<BR>
	 * 物理攻擊必須設定1以上
	 * 
	 * @param idx
	 * @return
	 */
	public int getRange(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return range[idx];
	}

	/**
	 * 攻擊距離(物理攻擊設置)<BR>
	 * 物理攻擊必須設定1以上
	 * 
	 * @param idx
	 * @param i
	 */
	public void setRange(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		range[idx] = i;
	}

	/*
	 * 範囲攻撃の横幅、単体攻撃ならば0を設定、範囲攻撃するならば0以上を設定
	 * WidthとHeightの設定は攻撃者からみて横幅をWidth、奥行きをHeightとする。
	 * Widthは+-あるので、1を指定すれば、ターゲットを中心として左右1までが対象となる。
	 */
	int areaWidth[];

	/**
	 * 攻擊範圍(物理攻擊設置)<BR>
	 * 單體攻擊設置0<BR>
	 * 範圍攻擊必須設定1以上
	 * 
	 * @param idx
	 * @return
	 */
	public int getAreaWidth(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return areaWidth[idx];
	}

	/**
	 * 攻擊範圍(物理攻擊設置)<BR>
	 * 單體攻擊設置0<BR>
	 * 範圍攻擊必須設定1以上
	 * 
	 * @param idx
	 * @param i
	 */
	public void setAreaWidth(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		areaWidth[idx] = i;
	}

	/*
	 * 範囲攻撃の高さ、単体攻撃ならば0を設定、範囲攻撃するならば1以上を設定
	 */
	int areaHeight[];

	/**
	 * 攻擊範圍(物理攻擊設置)<BR>
	 * 單體攻擊設置0<BR>
	 * 範圍攻擊必須設定1以上
	 * 
	 * @param idx
	 * @return
	 */
	public int getAreaHeight(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return areaHeight[idx];
	}

	/**
	 * 攻擊範圍(物理攻擊設置)<BR>
	 * 單體攻擊設置0<BR>
	 * 範圍攻擊必須設定1以上
	 * 
	 * @param idx
	 * @param i
	 */
	public void setAreaHeight(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		areaHeight[idx] = i;
	}

	int leverage[];

	/**
	 * 攻擊倍率(1/10)
	 * 
	 * @param idx
	 * @return
	 */
	public int getLeverage(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return leverage[idx];
	}

	/**
	 * 攻擊倍率(1/10)
	 * 
	 * @param idx
	 * @param i
	 */
	public void setLeverage(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		leverage[idx] = i;
	}

	int skillId[];

	/**
	 * 對應魔法技能編號
	 * 
	 * @param idx
	 * @return
	 */
	public int getSkillId(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return skillId[idx];
	}

	/**
	 * 對應魔法技能編號
	 * 
	 * @param idx
	 * @param i
	 */
	public void setSkillId(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		skillId[idx] = i;
	}

	int gfxid[];

	/**
	 * 物理攻擊使用的技能動畫
	 * 
	 * @param idx
	 * @return
	 */
	public int getGfxid(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return gfxid[idx];
	}

	/**
	 * 物理攻擊使用的技能動畫
	 * 
	 * @param idx
	 * @param i
	 */
	public void setGfxid(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		gfxid[idx] = i;
	}

	int actid[];

	/**
	 * 物理攻擊使用的動作編號
	 * 
	 * @param idx
	 * @return
	 */
	public int getActid(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return actid[idx];
	}

	/**
	 * 物理攻擊使用的動作編號
	 * 
	 * @param idx
	 * @param i
	 */
	public void setActid(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		actid[idx] = i;
	}

	int summon[];

	/**
	 * 召喚技能使用屬下編號
	 * 
	 * @param idx
	 * @return
	 */
	public int getSummon(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return summon[idx];
	}

	/**
	 * 召喚技能使用屬下編號
	 * 
	 * @param idx
	 * @param i
	 */
	public void setSummon(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		summon[idx] = i;
	}

	int summonMin[];

	/**
	 * 召喚最小數量
	 * 
	 * @param idx
	 * @return
	 */
	public int getSummonMin(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return summonMin[idx];
	}

	/**
	 * 召喚最小數量
	 * 
	 * @param idx
	 * @param i
	 */
	public void setSummonMin(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		summonMin[idx] = i;
	}

	int summonMax[];

	/**
	 * 召喚最大數量
	 * 
	 * @param idx
	 * @return
	 */
	public int getSummonMax(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return summonMax[idx];
	}

	/**
	 * 召喚最大數量
	 * 
	 * @param idx
	 * @param i
	 */
	public void setSummonMax(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		summonMax[idx] = i;
	}

	int polyId[];

	/**
	 * 強制變身代號
	 * 
	 * @param idx
	 * @return
	 */
	public int getPolyId(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return polyId[idx];
	}

	/**
	 * 強制變身代號
	 * 
	 * @param idx
	 * @param i
	 */
	public void setPolyId(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		polyId[idx] = i;
	}
}
