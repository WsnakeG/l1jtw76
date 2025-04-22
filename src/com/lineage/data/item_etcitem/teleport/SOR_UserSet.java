package com.lineage.data.item_etcitem.teleport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTeleportTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;

/**
 * <font color=#00800>自定義傳送捲軸</font><BR>
 * 
 * @author dexc
 */
public class SOR_UserSet extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(SOR_UserSet.class);

	/**
	 *
	 */
	private SOR_UserSet() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new SOR_UserSet();
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
		final int[] loc = ItemTeleportTable.get().getLoc(item.getItemId());

		if (loc != null) {
			final int locX = loc[0];
			final int locY = loc[1];
			final short mapId = (short) loc[2];

			if (pc.getMap().isEscapable()) {

				// 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);

				final TeleportRunnable runnable = new TeleportRunnable(pc, locX, locY, mapId);

				GeneralThreadPool.get().schedule(runnable, 0);

				// 該地圖不允許使用回捲
			} else {
				// 276 \f1在此無法使用傳送。
				pc.sendPackets(new S_ServerMessage(276));
				// 解除傳送鎖定
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
			}
			// 刪除道具
			if (!_notConsume) {
			pc.getInventory().removeItem(item, 1);
			}
			final int time = loc[3];
			if (time > 0) {
				pc.get_other().set_usemap(mapId);
				ServerUseMapTimer.put(pc, time);
				pc.sendPackets(new S_ServerMessage("使用時間限制:" + time + "秒"));
			}
		}
	}

	private class TeleportRunnable implements Runnable {

		private final L1PcInstance _pc;
		private int _locX = 0;
		private int _locY = 0;
		private int _mapid = 0;

		public TeleportRunnable(final L1PcInstance pc, final int x, final int y, final int mapid) {
			_pc = pc;
			_locX = x;
			_locY = y;
			_mapid = mapid;
		}

		@Override
		public void run() {
			try {
				final L1Map map = L1WorldMap.get().getMap((short) _mapid);
				final int r = 10;
				int tryCount = 0;
				int newX = _locX;
				int newY = _locY;
				do {
					tryCount++;
					newX = (_locX + (int) (Math.random() * r)) - (int) (Math.random() * r);
					newY = (_locY + (int) (Math.random() * r)) - (int) (Math.random() * r);
					if (map.isPassable(newX, newY, _pc)) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 5);

				if (tryCount >= 5) {
					L1Teleport.teleport(_pc, _locX, _locY, (short) _mapid, _pc.getHeading(), true);

				} else {
					L1Teleport.teleport(_pc, newX, newY, (short) _mapid, _pc.getHeading(), true);
				}

			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	private boolean _notConsume; // 是否不消耗道具 by Erics4179
	
	@Override
	public void set_set(final String[] set) {
	// 是否不消耗道具 by Erics4179
			try {
				if (set.length > 1) {
					_notConsume = Boolean.parseBoolean(set[1]);
				}

			} catch (final Exception e) {
		}
	}
}