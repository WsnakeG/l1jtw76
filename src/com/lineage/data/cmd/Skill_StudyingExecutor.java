package com.lineage.data.cmd;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 技能學習成功與否的判斷
 * 
 * @author dexc
 */
public interface Skill_StudyingExecutor {

	/**
	 * 學習技能等級限制 與地圖位置判斷
	 * 
	 * @param pc 人物
	 * @param skillId 技能編號
	 * @param magicAtt 技能等級分組<BR>
	 *            1~10共同魔法<BR>
	 *            11~20精靈魔法<BR>
	 *            21~30王族魔法<BR>
	 *            31~40騎士魔法<BR>
	 *            41~50黑暗精靈魔法<BR>
	 *            51~60龍騎士魔法<BR>
	 *            61~70幻術師魔法<BR>
	 * @param attribute 技能屬性<BR>
	 *            0:中立屬性魔法<BR>
	 *            1:正義屬性魔法<BR>
	 *            2:邪惡屬性魔法<BR>
	 *            3:精靈專屬魔法<BR>
	 *            4:王族專屬魔法<BR>
	 *            5:騎士專屬技能<BR>
	 *            6:黑暗精靈專屬魔法<BR>
	 *            7:龍騎士專屬魔法<BR>
	 *            8:幻術師專屬魔法<BR>
	 * @param itemObj 道具objid(點選的物品)
	 */
	public void magic(L1PcInstance pc, int skillId, int magicAtt, int attribute, int itemObj);
}
