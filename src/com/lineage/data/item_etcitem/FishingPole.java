package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_FishTime;
import com.lineage.server.serverpackets.S_Fishing;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 高彈力釣竿41484<BR>
 * 裝上繞線輪的高彈力釣竿41495<BR>
 * 
 * @author simlin
 */
public class FishingPole extends ItemExecutor {

	/**
	 *
	 */
	private FishingPole() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new FishingPole();
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
		final int fishX = data[0];
		final int fishY = data[1];
		startFishing(pc, item, fishX, fishY);
	}

	/**
	 * 開始釣魚
	 * 
	 * @param pc
	 * @param item
	 * @param fishX
	 * @param fishY
	 */
	private void startFishing(final L1PcInstance pc, final L1ItemInstance item, final int fishX,
			final int fishY) {
		if (pc.getMapId() != 5490) {
			// 無法在這個地區使用釣竿。
			pc.sendPackets(new S_ServerMessage(1138));
			return;
		}

		if (pc.getMap().isFishingZone(fishX, fishY)) {
			if (pc.getMap().isFishingZone(fishX + 1, fishY) && pc.getMap().isFishingZone(fishX - 1, fishY)
					&& pc.getMap().isFishingZone(fishX, fishY + 1)
					&& pc.getMap().isFishingZone(fishX, fishY - 1)) {
				if ((fishX > (pc.getX() + 5)) || (fishX < (pc.getX() - 5))) {
					// 無法在這個地區使用釣竿。
					pc.sendPackets(new S_ServerMessage(1138));
				} else if ((fishY > (pc.getY() + 5)) || (fishY < (pc.getY() - 5))) {
					// 無法在這個地區使用釣竿。
					pc.sendPackets(new S_ServerMessage(1138));
				} else if (pc.getInventory().consumeItem(41487, 1)) {
					pc.sendPacketsAll(new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY));
					if (item.getItemId() == 41495) {
						final int last = item.getChargeCount() - 1;
						if (last <= 0) {
							pc.getInventory().removeItem(item);
							CreateNewItem.createNewItem(pc, 41484, 1);
						} else {
							item.setChargeCount(last);
							pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
						}
					}
					pc.setFishing(true, fishX, fishY, _time, item.getItemId());
					pc.sendPackets(new S_FishTime(_time));
				} else {
					// 钓鱼就必须要有饵。
					pc.sendPackets(new S_ServerMessage(1137));
				}
			} else {
				// 無法在這個地區使用釣竿。
				pc.sendPackets(new S_ServerMessage(1138));
			}
		} else {
			// 無法在這個地區使用釣竿。
			pc.sendPackets(new S_ServerMessage(1138));
		}
	}

	private int _time;

	@Override
	public void set_set(final String[] set) {
		try {
			_time = Integer.parseInt(set[1]);

		} catch (final Exception e) {
		}
	}
}
