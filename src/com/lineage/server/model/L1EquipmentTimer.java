package com.lineage.server.model;

import java.util.TimerTask;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 計時物件使用時間軸
 * 
 * @author dexc
 */
public class L1EquipmentTimer extends TimerTask {

	private final L1PcInstance _pc;// 擁有者

	private final L1ItemInstance _item;// 計時物件

	private boolean _isRunning;

	public L1EquipmentTimer(final L1PcInstance pc, final L1ItemInstance item, final boolean status) {
		_pc = pc;
		_item = item;
		_isRunning = status;
	}

	@Override
	public void run() {
		if ((_item.getRemainingTime() - 1) > 0) {
			_item.setRemainingTime(_item.getRemainingTime() - 1);
			_pc.getInventory().updateItem(_item, L1PcInventory.COL_REMAINING_TIME);
			if (_pc.getOnlineStatus() == 0) {
				_isRunning = false;
				cancel();
			}
		} else {
			if (_item.getItem().getType2() != 0) {
				_pc.getInventory().removeItem(_item, 1);
			} else {
				if (_pc.getDoll(_item.getId()) != null) {
					_pc.getDoll(_item.getId()).deleteDoll();
				}
			}
			_isRunning = false;
			cancel();
		}
	}

	public boolean isRunning() {
		return _isRunning;
	}
}
