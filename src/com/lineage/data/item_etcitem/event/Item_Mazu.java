package com.lineage.data.item_etcitem.event;

import static com.lineage.server.model.skill.L1SkillId.MAZU_STATUS;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 媽祖祝福<BR>
 * DELETE FROM `etcitem` WHERE `item_id`='49532'; INSERT INTO `etcitem` VALUES
 * (49532, '虔誠祝福', 'event.Item_Mazu', '虔誠祝福', 'other', 'normal', 'gemstone', 0,
 * 2563, 3963, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
 * 
 * @author loli
 */
public class Item_Mazu extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Item_Mazu.class);

	private Item_Mazu() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Item_Mazu();
	}

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
			if (pc.hasSkillEffect(MAZU_STATUS)) {
				pc.sendPackets(
						new S_ServerMessage("\\fV媽祖祝福效果時間尚有" + pc.getSkillEffectTimeSec(MAZU_STATUS) + "秒"));
				return;
			}

			pc.getInventory().removeItem(item, 1);

			pc.setSkillEffect(MAZU_STATUS, 2400 * 1000);
			// 媽祖祝福
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7321));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
