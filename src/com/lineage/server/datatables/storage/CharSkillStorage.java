package com.lineage.server.datatables.storage;

import java.util.ArrayList;

import com.lineage.server.templates.L1UserSkillTmp;

/**
 * 人物技能紀錄
 * 
 * @author dexc
 */
public interface CharSkillStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 取回該人物技能列表
	 * 
	 * @param pc
	 * @return
	 */
	public ArrayList<L1UserSkillTmp> skills(int playerobjid);

	/**
	 * 增加技能
	 */
	public void spellMastery(int playerobjid, int skillid, String skillname, int active, int time);

	/**
	 * 刪除技能
	 */
	public void spellLost(int playerobjid, int skillid);

	/**
	 * 檢查技能是否重複
	 */
	public boolean spellCheck(int playerobjid, int skillid);

	/**
	 * 設置自動技能狀態
	 */
	public void setAuto(final int mode, final int objid, final int skillid);

}
