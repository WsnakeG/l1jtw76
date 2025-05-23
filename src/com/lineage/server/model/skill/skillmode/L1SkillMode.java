package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 技能施放管理
 * 
 * @author daien
 */
public abstract class L1SkillMode {

	/**
	 * 技能施放管理(PC)
	 * 
	 * @param pc 施展者
	 * @param cha 目標
	 * @param skillid 技能編號
	 * @param x 座標
	 * @param y 座標
	 * @param message 應用文字串
	 * @return
	 * @throws Exception
	 */
	public abstract int start(final L1PcInstance pc, final L1Character cha, final int skillid, final int x,
			final int y, final String message) throws Exception;

	/**
	 * 技能施放管理(NPC)
	 * 
	 * @param npc 施展者
	 * @param cha 目標
	 * @param skillid 技能編號
	 * @param x 座標
	 * @param y 座標
	 * @param message 應用文字串
	 * @return
	 * @throws Exception
	 */
	public abstract int start(final L1NpcInstance npc, final L1Character cha, final int skillid, final int x,
			final int y, final String message) throws Exception;

	/**
	 * 技能使用相關限制(1)<BR>
	 * 該技能所需耗用的HP/MP/材料/正義質
	 * 
	 * @param user
	 * @param skillid
	 */
	public void useMode(final L1Character user, final int skillid) {

	}

	/**
	 * 技能使用相關限制(2)
	 * 
	 * @param user
	 * @param targ
	 * @param skillid
	 */
	public void useLoc(final L1Character user, final L1Character targ, final int skillid) {

	}

	/**
	 * 技能結束應用
	 * 
	 * @param cha
	 * @throws Exception
	 */
	public abstract void stop(final L1Character cha) throws Exception;

}
