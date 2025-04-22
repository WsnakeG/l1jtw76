package com.lineage.data.item_etcitem.shop;

import static com.lineage.server.model.skill.L1SkillId.SCORE02;
import static com.lineage.server.model.skill.L1SkillId.SCORE03;
import static com.lineage.server.model.skill.L1SkillId.SCORE04;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 44134 陣營經驗2倍藥水 時效20分鐘 UPDATE `etcitem` SET `classname`='shop.Up_Score_02'
 * WHERE `item_id`='44134'; UPDATE `etcitem` SET `classname`='shop.Up_Score_03'
 * WHERE `item_id`='44135';
 */
public class Up_Score_02 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Up_Score_02.class);

	/**
	 *
	 */
	private Up_Score_02() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Up_Score_02();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		try {
			// 例外狀況:物件為空
			if (item == null) {
				return;
			}
			// 例外狀況:人物為空
			if (pc == null) {
				return;
			}
			if (pc.hasSkillEffect(SCORE02)) {
				pc.sendPackets(new S_ServerMessage("\\fR積分加倍(2倍)作用中!"));
				return;
			}
			if (pc.hasSkillEffect(SCORE03)) {
				pc.sendPackets(new S_ServerMessage("\\fR積分加倍(3倍)作用中!"));
				return;
			}
			if (pc.hasSkillEffect(SCORE04)) {
				pc.sendPackets(new S_ServerMessage("\\fR積分加倍(4倍)作用中!"));
				return;
			}
			if (pc.getInventory().removeItem(item, 1) != 1) {
				return;
			}
			pc.sendPackets(new S_ServerMessage("\\fR積分加倍(2倍)啟動!"));
			pc.setSkillEffect(SCORE02, 20 * 60 * 1000);
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 9714));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
