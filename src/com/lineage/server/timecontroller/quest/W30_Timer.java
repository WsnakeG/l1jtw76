package com.lineage.server.timecontroller.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.quest.WizardLv30_1;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;

/**
 * 不死族的叛徒 (法師30級以上官方任務)
 * 
 * @author dexc
 */
public class W30_Timer extends TimerTask {

	private static final Log _log = LogFactory.getLog(W30_Timer.class);

	private ScheduledFuture<?> _timer;

	private int _qid = -1;

	public void start() {
		_qid = WizardLv30_1.QUEST.get_id();
		final int timeMillis = 1000;
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final HashMap<Integer, L1Object> mapList = new HashMap<Integer, L1Object>();
			mapList.putAll(World.get().getVisibleObjects(WizardLv30_1.MAPID));
			// 任務地圖內物件

			// 執行中任務副本
			final ArrayList<L1QuestUser> questList = WorldQuest.get().getQuests(_qid);

			for (final Object object : questList.toArray()) {
				final L1QuestUser quest = (L1QuestUser) object;
				int i = 0;
				for (final L1Object obj : mapList.values()) {
					if (obj instanceof L1MerchantInstance) {// 對話NPC
						continue;
					}
					if (obj.get_showId() == quest.get_id()) {
						if ((obj.getX() == 32867) && (obj.getY() == 32912)) {
							i += 1;
						}
						if ((obj.getX() == 32867) && (obj.getY() == 32927)) {
							i += 1;
						}
						if ((obj.getX() == 32860) && (obj.getY() == 32920)) {
							i += 1;
						}
						if ((obj.getX() == 32875) && (obj.getY() == 32920)) {
							i += 1;
						}
					}
				}
				if (i >= 4) {
					for (final L1NpcInstance npc : quest.npcList()) {
						if (npc instanceof L1DoorInstance) {
							final L1DoorInstance door = (L1DoorInstance) npc;
							if (door.getDoorId() == 10003) {
								door.open();
								Thread.sleep(50);
							}
						}
					}
					for (final L1PcInstance pc : quest.pcList()) {
						if ((pc.getX() == 32868) && (pc.getY() == 32919)) {
							// 傳送任務執行者
							L1Teleport.teleport(pc, 32929, 32798, (short) WizardLv30_1.MAPID, 5, true);
							Thread.sleep(50);
						}
					}

				} else {
					for (final L1NpcInstance npc : quest.npcList()) {
						if (npc instanceof L1DoorInstance) {
							final L1DoorInstance door = (L1DoorInstance) npc;
							if (door.getDoorId() == 10003) {
								door.close();
								Thread.sleep(50);
							}
						}
					}
				}
			}
			mapList.clear();
			questList.clear();

		} catch (final Exception e) {
			_log.error("不死族的叛徒 (法師30級以上官方任務)時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final W30_Timer w30Timer = new W30_Timer();
			w30Timer.start();
		}
	}
}
