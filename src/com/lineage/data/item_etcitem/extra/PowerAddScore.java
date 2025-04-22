package com.lineage.data.item_etcitem.extra;

import com.lineage.data.event.CampSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 增加陣營貢獻度 [value] 更新為使用獲得貢獻度模式 (隨機值) extra.PowerAddScore 1 100 189
 * 
 * @author Roy 2014/08/07
 */
public class PowerAddScore extends ItemExecutor {

	/**
	 *
	 */
	private PowerAddScore() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new PowerAddScore();
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
		if (pc == null) {
			return;
		}

		final S_SkillSound sound = new S_SkillSound(pc.getId(), _gfxid);
		if (_gfxid > 0) { // 具備動畫
			pc.sendPacketsX8(sound);
		}
		pc.getInventory().removeItem(item, 1);

		int addvalue = _min_value;

		if (_max_value > 0) { // 具備最大值
			addvalue += (int) (Math.random() * _max_value); // 隨機數字範圍
		}

		pc.get_other().add_score(addvalue);

		if (addvalue > 0) {
			// 你覺得舒服多了訊息
			pc.sendPackets(new S_ServerMessage("\\aL得到了" + addvalue + "\\aL積分"));
		}

		if (CampSet.CAMPSTART) {
			// 陣營系統啟用 XXX
			if ((pc.get_c_power() != null) && (pc.get_c_power().get_c1_type() != 0)) {
				final int lv = C1_Name_Type_Table.get().getLv(pc.get_c_power().get_c1_type(),
						pc.get_other().get_score());
				if (lv != pc.get_c_power().get_power().get_c1_id()) {
					pc.get_c_power().set_power(pc, false);
					pc.sendPackets(new S_ServerMessage(
							"\\aF階級變更:" + pc.get_c_power().get_power().get_c1_name_type()));
					pc.sendPacketsAll(new S_ChangeName(pc, true));
				}
			}
		}
	}

	private int _min_value;
	private int _max_value;
	private int _gfxid;

	@Override
	public void set_set(final String[] set) {
		try {
			_min_value = Integer.parseInt(set[1]);
			_max_value = Integer.parseInt(set[2]);
			_gfxid = Integer.parseInt(set[3]);
		} catch (final Exception e) {
		}
	}
}