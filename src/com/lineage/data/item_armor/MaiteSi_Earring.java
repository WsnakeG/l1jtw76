package com.lineage.data.item_armor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * MaiteSi_Earring 麥特斯的藍光耳環 參數: 藥水恢復增加比率(1/100)
 */
public class MaiteSi_Earring extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(MaiteSi_Earring.class);

	/**
	 *
	 */
	private MaiteSi_Earring() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new MaiteSi_Earring();
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
			case 0:// 解除裝備
				pc.set_up_hp_potion(pc.get_up_hp_potion() - _up_hp_potion);
				break;

			case 1:// 裝備
				pc.set_up_hp_potion(pc.get_up_hp_potion() + _up_hp_potion);
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _up_hp_potion = 0;

	@Override
	public void set_set(final String[] set) {
		try {
			_up_hp_potion = Integer.parseInt(set[1]);

		} catch (final Exception e) {
		}
	}
}
