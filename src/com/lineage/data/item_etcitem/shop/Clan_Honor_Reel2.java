package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 經驗兌換卷44156 $1499：經驗值 $1487：魔法卷軸 DELETE FROM `etcitem` WHERE
 * `item_id`='44156'; INSERT INTO `etcitem` VALUES (44156, '經驗值 魔法卷軸',
 * 'shop.Clan_Honor_Reel', '$1499 $1487', 'scroll', 'normal', 'paper', 0, 3069,
 * 3963, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1);
 */
public class Clan_Honor_Reel2 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Clan_Honor_Reel2.class);

	/**
	 *
	 */
	private Clan_Honor_Reel2() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Clan_Honor_Reel2();
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
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}

		if (pc.getLevel() >= 99) {
			return;
		}

		pc.addExp(_exp);// 每次可獲得的EXP
		pc.getInventory().removeItem(item, 1);
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));

		try {
			pc.save();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _exp;

	private int _gfxid;

	@Override
	public void set_set(final String[] set) {
		try {
			_exp = Integer.parseInt(set[1]);
			_gfxid = Integer.parseInt(set[2]);
		} catch (final Exception e) {
		}
	}

}
