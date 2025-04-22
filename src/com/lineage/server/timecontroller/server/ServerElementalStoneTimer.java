package com.lineage.server.timecontroller.server;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1GroundInventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;

/**
 * 元素石生成 時間軸
 * 
 * @author dexc
 */
public class ServerElementalStoneTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerElementalStoneTimer.class);

	private ScheduledFuture<?> _timer;

	private static final short ELVEN_FOREST_MAPID = 4;
	private static final int MAX_COUNT = ConfigAlt.ELEMENTAL_STONE_AMOUNT; // 設置個数
	private static final int INTERVAL = 3; // 設置間隔 秒
	private static final int SLEEP_TIME = 30; // 設置終了後、再設置までのスリープ時間 秒
	private static final int FIRST_X = 32911;
	private static final int FIRST_Y = 32210;
	private static final int LAST_X = 33141;
	private static final int LAST_Y = 32500;
	private static final int ELEMENTAL_STONE_ID = 40515; // 元素石

	private final ArrayList<L1GroundInventory> _itemList = new ArrayList<L1GroundInventory>(MAX_COUNT);

	private final L1Map _map = L1WorldMap.get().getMap(ELVEN_FOREST_MAPID);

	private final Random _random = new Random();

	private final L1Object _dummy = new L1Object();

	public void start() {
		final int timeMillis = SLEEP_TIME * 1000;// 指定秒數
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	/**
	 * 指定された位置に石を置けるかを返す。
	 */
	private boolean canPut(final L1Location loc) {
		_dummy.setMap(loc.getMap());
		_dummy.setX(loc.getX());
		_dummy.setY(loc.getY());

		// 可視範囲のプレイヤーチェック
		if (World.get().getVisiblePlayer(_dummy).size() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 次の設置ポイントを決める。
	 */
	private Point nextPoint() {
		final int newX = _random.nextInt(LAST_X - FIRST_X) + FIRST_X;
		final int newY = _random.nextInt(LAST_Y - FIRST_Y) + FIRST_Y;

		return new Point(newX, newY);
	}

	/**
	 * 拾われた石をリストから削除する。
	 */
	private void removeItemsPickedUp() {
		for (int i = 0; i < _itemList.size(); i++) {
			final L1GroundInventory gInventory = _itemList.get(i);
			if (!gInventory.checkItem(ELEMENTAL_STONE_ID)) {
				_itemList.remove(i);
				i--;
			}
		}
	}

	/**
	 * 指定された位置へ石を置く。
	 */
	private void putElementalStone(final L1Location loc) {
		final L1GroundInventory gInventory = World.get().getInventory(loc);

		final L1ItemInstance item = ItemTable.get().createItem(ELEMENTAL_STONE_ID);
		item.setEnchantLevel(0);
		item.setCount(1);
		gInventory.storeItem(item);
		_itemList.add(gInventory);
	}

	@Override
	public void run() {
		try {
			removeItemsPickedUp();

			while (_itemList.size() < MAX_COUNT) { // 減っている場合セット
				final L1Location loc = new L1Location(nextPoint(), _map);

				if (!canPut(loc)) {
					// XXX 設置範囲内全てにPCが居た場合無限ループになるが…
					continue;
				}

				putElementalStone(loc);

				Thread.sleep(INTERVAL * 1000); // 一定時間毎に設置
			}

		} catch (final Throwable e) {
			_log.error("元素石生成時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final ServerElementalStoneTimer elementalStoneTimer = new ServerElementalStoneTimer();
			elementalStoneTimer.start();
		}
	}
}
