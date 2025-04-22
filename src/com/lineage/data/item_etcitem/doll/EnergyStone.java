package com.lineage.data.item_etcitem.doll;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Doll;

/**
 * 能量石系列
 * 
 * @author simlin
 */
public class EnergyStone extends ItemExecutor {

	/**
	 *
	 */
	private EnergyStone() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new EnergyStone();
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
		final int itemobj = data[0];
		final L1ItemInstance tgitem = pc.getInventory().getItem(itemobj);
		final int itemId = tgitem.getItemId();
		final L1Doll type = DollPowerTable.get().get_type(itemId);

		if (type == null) {
			// 只有魔法娃娃可以選擇。
			pc.sendPackets(new S_ServerMessage(2477));
			return;
		}

		if (tgitem.getItem().getMaxUseTime() == 0) {
			// 該道具不是充電類型的道具。
			pc.sendPackets(new S_ServerMessage(3329));
			return;
		}

		if (pc.getDoll(tgitem.getId()) != null) {
			// 目前不可對該道具進行充電。
			pc.sendPackets(new S_ServerMessage(3330));
			return;
		}

		int time = tgitem.getRemainingTime();
		if (time >= 1800) {
			// 只能對剩於30分之內的道具進行充電。
			pc.sendPackets(new S_ServerMessage(3331));
			return;
		}
		pc.getInventory().removeItem(item, 1);

		time += _hour * 60 * 60;
		tgitem.setRemainingTime(time);
		pc.getInventory().updateItem(tgitem, L1PcInventory.COL_REMAINING_TIME);
		pc.getInventory().saveItem(tgitem, L1PcInventory.COL_REMAINING_TIME);
		// \f1%0%s暫時有強烈的 %1 能量，充滿著和之前不一樣的感覺。
		pc.sendPackets(new S_ServerMessage(2789, tgitem.getName(), _hour + "小時"));
	}

	private int _hour = 0;

	@Override
	public void set_set(final String[] set) {
		try {
			_hour = Integer.parseInt(set[1]);

		} catch (final Exception e) {
		}
	}
}
