package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.GamblingReading;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.templates.L1Event;
import com.lineage.server.templates.L1Item;
import com.lineage.server.timecontroller.event.GamblingTime;
import com.lineage.server.world.World;

/**
 * 奇岩賭場<BR>
 * # 修正賭場命令設置 DELETE FROM `server_event` WHERE `id`='1'; INSERT INTO
 * `server_event` VALUES ('1', '奇岩賭場系統', 'GamblingSet', '0', '60,1,44070',
 * '說明:每場比賽間隔時間(分鐘),奇岩賭場 下注額,下注物品'); #新增銀行管理系統 DELETE FROM `server_event` WHERE
 * `id`='52'; INSERT INTO `server_event` VALUES ('52', '銀行管理員系統', 'BankSet',
 * '1', '500000000,600000000,40308,5,0.05', '說明:銀行儲蓄上限,銀行存款上限(加入利息),發放利息時間
 * (單位:分),銀行利率(%)');
 * 
 * @author dexc
 */
public class GamblingSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(GamblingSet.class);

	public static int GAMADENATIME;// 每場比賽間隔時間(分鐘)

	public static int ADENAITEM = 40308;// 奇岩賭場 下注使用物品編號(預設金幣40308)

	public static int GAMADENA;// 奇岩賭場 下注額(每張賭票售價)

	public static String GAMADENANAME;// 奇岩賭場 下注物品名稱

	/**
	 *
	 */
	private GamblingSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new GamblingSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final String[] set = event.get_eventother().split(",");

			try {
				GAMADENATIME = Integer.parseInt(set[0]);

			} catch (final Exception e) {
				GAMADENATIME = 30;
				_log.error("未設定每場比賽間隔時間(分鐘)(使用預設30分鐘)");
			}

			try {
				GAMADENA = Integer.parseInt(set[1]);

			} catch (final Exception e) {
				GAMADENA = 5000;
				_log.error("未設定奇岩賭場 下注額(每張賭票售價)(使用預設5000)");
			}

			try {
				ADENAITEM = Integer.parseInt(set[2]);
				final L1Item item = ItemTable.get().getTemplate(ADENAITEM);
				if (item != null) {
					GAMADENANAME = item.getNameId();
				}

			} catch (final Exception e) {
				ADENAITEM = 40308;
				GAMADENANAME = "$4";
				_log.error("未設定奇岩賭場 下注物品編號(使用預設40308)");
			}

			// 賭場紀錄
			GamblingReading.get().load();

			// 賭場計時
			final GamblingTime gamblingTimeController = new GamblingTime();
			gamblingTimeController.start();

			spawnDoor();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 召喚門
	 */
	private void spawnDoor() {
		final int[][] gamDoors = new int[][] {
				// 門的編號 / 圖形編號 / X座標 / Y座標 / 地圖編號 / 門的左端 / 門的右端 / HP / 管理者代號
				new int[] { 51, 1487, 33521, 32861, 4, 1, 33523, 33523, 0, -1 },
				new int[] { 52, 1487, 33519, 32863, 4, 1, 33523, 33523, 0, -1 },
				new int[] { 53, 1487, 33517, 32865, 4, 1, 33523, 33523, 0, -1 },
				new int[] { 54, 1487, 33515, 32867, 4, 1, 33523, 33523, 0, -1 },
				new int[] { 55, 1487, 33513, 32869, 4, 1, 33523, 33523, 0, -1 }, };

		for (final int[] doorInfo : gamDoors) {
			final L1DoorInstance door = (L1DoorInstance) NpcTable.get().newNpcInstance(81158);

			if (door != null) {
				// NPC OBJID
				door.setId(IdFactoryNpc.get().nextId());

				final int id = doorInfo[0];
				final int gfxid = doorInfo[1];
				final int locx = doorInfo[2];
				final int locy = doorInfo[3];
				final int mapid = doorInfo[4];
				final int direction = doorInfo[5];
				final int left_edge_location = doorInfo[6];
				final int right_edge_location = doorInfo[7];
				final int hp = doorInfo[8];
				final int keeper = doorInfo[9];

				door.setDoorId(id);
				door.setGfxId(gfxid);
				door.setX(locx);
				door.setY(locy);
				door.setMap((short) mapid);
				door.setHomeX(locx);
				door.setHomeY(locy);
				door.setDirection(direction);
				door.setLeftEdgeLocation(left_edge_location);
				door.setRightEdgeLocation(right_edge_location);
				door.setMaxHp(hp);
				door.setCurrentHp(hp);
				door.setKeeperId(keeper);

				World.get().storeObject(door);
				World.get().addVisibleObject(door);
			}
		}
	}
}
