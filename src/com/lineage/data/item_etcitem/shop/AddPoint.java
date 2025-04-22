package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 商城點數增加道具 classname : shop.AddPoint 100 452
 * 
 * @author simlin
 */
public class AddPoint extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(AddPoint.class);

	private int _point;

	/**
	 *
	 */
	private AddPoint() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new AddPoint();
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
		try {
			int points = AccountReading.get().getPoints(pc.getAccountName());
			points += _point;
			AccountReading.get().setPoints(pc.getAccountName(), points);
			pc.getInventory().removeItem(item, 1);
			pc.sendPackets(new S_SystemMessage("\\aE商城點數已增加\\aG請重新登入遊戲！"));
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_s));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _gfxid_s; // 成功特效編號

	@Override
	public void set_set(final String[] set) {
		try {
			_point = Integer.parseInt(set[1]);

			_gfxid_s = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
	}
}