package com.lineage.data.npc.quest2;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;

/**
 * 藍色 龍之門扉 [透明傳送物件] 法利昂棲息地
 * 
 * @author terry0412
 */
public class Npc_DragonB2 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_DragonB2.class);

	/** 啟動執行緒 */
	public static final Map<Integer, checkDragonTimer2> _timer = new HashMap<Integer, checkDragonTimer2>();

	private Npc_DragonB2() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_DragonB2();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 存在副本物件
			final L1QuestUser quest = WorldQuest.get().get(pc.get_showId());
			if (quest != null) {
				if (_timer.get(pc.get_showId()) == null) {
					boolean isFound = false;
					if (!quest.npcList().isEmpty()) {
						for (final L1NpcInstance find_npc : quest.npcList()) {
							if ((find_npc.getNpcId() == 71026) || (find_npc.getNpcId() == 71027)
									|| (find_npc.getNpcId() == 71028)) {
								isFound = true;
								break;
							}
						}
					}
					if (!isFound) {
						// 簡易執行緒啟動 by terry0412
						final checkDragonTimer2 timer = new checkDragonTimer2(npc.getMapId(), quest);
						timer.begin();
						// 放入啟動執行緒列表
						_timer.put(pc.get_showId(), timer);
					}
				}
				// 傳送到目的地
				final L1Location loc = new L1Location(32990, 32842, npc.getMapId()).randomLocation(5, false);
				L1Teleport.teleport(pc, loc.getX(), loc.getY(), npc.getMapId(), 4, true);
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
	}

	// 簡易執行緒2 by terry0412
	private class checkDragonTimer2 extends TimerTask {

		private final int mapId;
		private final L1QuestUser quest;

		public checkDragonTimer2(final int mapId, final L1QuestUser quest) {
			this.mapId = mapId;
			this.quest = quest;
		}

		@Override
		public void run() {
			cancel();
			try {
				sendServerMessage(1657);
				Thread.sleep(3000L);

				sendServerMessage(1658);
				Thread.sleep(4000L);

				sendServerMessage(1659);
				Thread.sleep(4000L);

				sendServerMessage(1660);

				// 隨機座標5格範圍內
				final L1Location loc = new L1Location(32958, 32835, mapId).randomLocation(5, true);
				// 新法利昂(1階段)
				L1SpawnUtil.spawn(71026, loc, new Random().nextInt(8), quest.get_id());

				// Thread.sleep(4000L);

				/*
				 * L1NpcInstance localL1NpcInstance = L1SpawnUtil.spawn(103344,
				 * loc.getX(), loc.getY(), this.mapId, 0); Thread.sleep(2000L);
				 * for (int i = 0; i < 5; i++) { localL1Location3 =
				 * localL1Location1.randomLocation(12, false);
				 * L1SpawnUtil.spawn(103349, localL1Location3.getX(),
				 * localL1Location3.getY(), this.mapId, 120000); }
				 * Thread.sleep(2000L); L1Location loc2 = loc.randomLocation(5,
				 * false); L1SpawnUtil.spawn(103347, loc2.getX(), loc2.getY(),
				 * this.mapId, 0); Thread.sleep(2000L); L1Location loc3 =
				 * loc.randomLocation(5, false); L1SpawnUtil.spawn(103348,
				 * loc3.getX(), loc3.getY(), this.mapId, 0);
				 * Thread.sleep(3000L);
				 */

				// this._threadPool.execute(new
				// spawnRepeatNpc(localL1NpcInstance, 103336, 1));
			} catch (final Exception e) {
			} finally {
				_timer.remove(quest.get_id());
			}
		}

		private final void sendServerMessage(final int msgid) {
			if (!quest.pcList().isEmpty()) {
				for (final L1PcInstance pc : quest.pcList()) {
					pc.sendPackets(new S_ServerMessage(msgid));
				}
			}
		}

		public final void begin() {
			GeneralThreadPool.get().schedule(this, 60000L);
		}
	}
}
