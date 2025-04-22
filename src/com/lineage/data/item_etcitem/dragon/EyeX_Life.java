package com.lineage.data.item_etcitem.dragon;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * <font color=#00800>42522 生命之魔眼</font><BR>
 * 效果時間： 600秒，使用間隔1小時，無限制使用次數 不可轉移 物理攻擊迴避率+10% 魔法傷害減免+50 魔法暴擊率+1 額外攻擊點數+2 防護中毒狀態
 * 
 * @author dexc
 */
public class EyeX_Life extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(EyeX_Life.class);

	/**
	 *
	 */
	private EyeX_Life() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new EyeX_Life();
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

			final int time = L1BuffUtil.cancelDragon(pc);
			if (time != -1) {
				// 1,139：%0 分鐘之內無法使用。
				pc.sendPackets(
						new S_ServerMessage(1139, item.getLogName() + " " + String.valueOf(time / 60)));
				return;
			}

			// pc.getInventory().removeItem(item, 1);
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7678));
			// 設置延遲使用機制
			final Timestamp ts = new Timestamp(System.currentTimeMillis());
			item.setLastUsed(ts);
			pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
			pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);

			// SKILL移轉
			final SkillMode mode = L1SkillMode.get().getSkill(L1SkillId.DRAGON5);
			if (mode != null) {
				mode.start(pc, null, null, 600);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}