package com.lineage.data.item_weapon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 30324 法利昂的力量 30325 法利昂的魅惑 30326 法利昂的泉源 30327 法利昂的霸氣
 */
public class ElitePlateMail_Fafurion extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(ElitePlateMail_Fafurion.class);

	/**
	 *
	 */
	private ElitePlateMail_Fafurion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ElitePlateMail_Fafurion();
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
				pc.set_elitePlateMail_Fafurion(0, 0, 0);
				break;

			case 1:// 裝備
				pc.set_elitePlateMail_Fafurion(_r, _hp_min, _hp_max);
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _r = 0;
	private int _hp_min = 0;
	private int _hp_max = 0;

	@Override
	public void set_set(final String[] set) {
		try {
			_r = Integer.parseInt(set[1]);
		} catch (final Exception e) {
		}
		try {
			_hp_min = Integer.parseInt(set[2]);
		} catch (final Exception e) {
		}
		try {
			_hp_max = Integer.parseInt(set[3]);
		} catch (final Exception e) {
		}
	}
}
