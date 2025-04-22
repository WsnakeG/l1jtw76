package com.lineage.data.item_etcitem.extra;

import com.lineage.data.event.CampSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 增加陣營貢獻度 (固定值) [value]
 * 
 * @author terry0412
 */
public class PowerAddScore2 extends ItemExecutor {

	/**
	 *
	 */
	private PowerAddScore2() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new PowerAddScore2();
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

		pc.getInventory().removeItem(item, 1);

		// 你得到了 %0 積分。
		pc.sendPackets(new S_ServerMessage("\\aL你得到了(" + _value + ")\\aL貢獻值"));
		pc.get_other().add_score(_value);

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

	private int _value;

	@Override
	public void set_set(final String[] set) {
		try {
			_value = Integer.parseInt(set[1]);
		} catch (final Exception e) {
		}
	}
}