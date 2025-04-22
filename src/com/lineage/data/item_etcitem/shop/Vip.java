package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * VIP資格
 * 
 * @author simlin
 */
public class Vip extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Vip.class);

	/**
	 *
	 */
	private Vip() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Vip();
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

			// 刪除物件
			pc.getInventory().removeItem(item);
			// 給予VIP資格
			pc.addVipStatus(_daycount, _level);

			// 送出特效封包
			pc.sendPacketsAll(new S_SkillSound(pc.getId(), _gfxId));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// vip等級
	private int _level;

	// vip天數
	private int _daycount;

	// 使用後出現的特效
	private int _gfxId;

	@Override
	public void set_set(final String[] set) {
		try {
			_level = Integer.parseInt(set[1]);
			_daycount = Integer.parseInt(set[2]);
			_gfxId = Integer.parseInt(set[3]);

		} catch (final Exception e) {
		}
	}
}