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
 * 綠色 龍之門扉 [透明傳送物件] 安塔瑞斯棲息地
 * 
 * @author terry0412
 */
public class Npc_DragonB1 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_DragonB1.class);

	/** 啟動執行緒 */
	public static final Map<Integer, checkDragonTimer1> _timer = new HashMap<Integer, checkDragonTimer1>();

	private Npc_DragonB1() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_DragonB1();
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
							if ((find_npc.getNpcId() == 71014) || (find_npc.getNpcId() == 71015)
									|| (find_npc.getNpcId() == 71016)) {
								isFound = true;
								break;
							}
						}
					}
					if (!isFound) {
						// 簡易執行緒啟動 by terry0412
						final checkDragonTimer1 timer = new checkDragonTimer1(npc.getMapId(), quest);
						timer.begin();
						// 放入啟動執行緒列表
						_timer.put(pc.get_showId(), timer);
					}
				}
				// 傳送到目的地
				final L1Location loc = new L1Location(32795, 32662, npc.getMapId()).randomLocation(5, false);
				L1Teleport.teleport(pc, loc.getX(), loc.getY(), npc.getMapId(), 4, true);
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
	}

	// 簡易執行緒1 by terry0412
	private class checkDragonTimer1 extends TimerTask {

		private final int mapId;
		private final L1QuestUser quest;

		public checkDragonTimer1(final int mapId, final L1QuestUser quest) {
			this.mapId = mapId;
			this.quest = quest;
		}

		@Override
		public void run() {
			cancel();
			try {
				sendServerMessage(1570);
				Thread.sleep(3000L);

				sendServerMessage(1571);
				Thread.sleep(4000L);

				sendServerMessage(1572);

				// this._threadPool.execute(new spawnEffectTrap(32786, 32689,
				// this.mapId, 7331, 1085));
				Thread.sleep(4000L);

				// 隨機座標5格範圍內
				final L1Location loc = new L1Location(32786, 32689, mapId).randomLocation(5, true);
				// 新安塔瑞斯(1階段)
				L1SpawnUtil.spawn(71014, loc, new Random().nextInt(8), quest.get_id());
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
