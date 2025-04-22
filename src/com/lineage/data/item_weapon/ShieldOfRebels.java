package com.lineage.data.item_weapon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * @author terry0412
 */
public class ShieldOfRebels extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(ShieldOfRebels.class);

	/**
	 *
	 */
	private ShieldOfRebels() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ShieldOfRebels();
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

			switch (data[0]) {
			case 0: // 解除裝備
				pc.set_shieldOfRebels(0, 0);
				break;

			case 1: // 裝備
				final int random = _r + (item.getEnchantLevel() > 0 ? item.getEnchantLevel() * _r_plus : 0);
				pc.set_shieldOfRebels(random, _dmg_reduction);
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _r;

	private int _r_plus;

	private int _dmg_reduction;

	@Override
	public void set_set(final String[] set) {
		try {
			_r = Integer.parseInt(set[1]);
		} catch (final Exception e) {
		}
		try {
			_r_plus = Integer.parseInt(set[2]);
		} catch (final Exception e) {
		}
		try {
			_dmg_reduction = Integer.parseInt(set[3]);
		} catch (final Exception e) {
		}
	}
}
