package com.lineage.server.timecontroller.quest;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;

/**
 * 尋找黑暗之星 (黑暗妖精50級以上官方任務)
 * 
 * @author dexc
 */
public class DE50A_Timer extends TimerTask {

	private static final Log _log = LogFactory.getLog(DE50A_Timer.class);

	private static Random _random = new Random();

	private ScheduledFuture<?> _timer;

	private int _qid = -1;

	public void start() {
		_qid = DarkElfLv50_1.QUEST.get_id();
		final int timeMillis = 1500;
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			// 執行中任務副本
			final ArrayList<L1QuestUser> questList = WorldQuest.get().getQuests(_qid);
			// 不包含元素
			if (questList.isEmpty()) {
				return;
			}

			for (final Object object : questList.toArray()) {
				final L1QuestUser quest = (L1QuestUser) object;
				// 召喚怪物
				for (final L1PcInstance tgpc : quest.pcList()) {
					if (!tgpc.isDead()) {// 45582-45587
						if (_random.nextInt(100) < 10) {
							quest.addNpc(spawn(tgpc));
							Thread.sleep(50);
						}
					}
				}
			}
			questList.clear();

		} catch (final Exception e) {
			_log.error("尋找黑暗之星 (黑暗妖精50級以上官方任務)A時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final DE50A_Timer de50ATimer = new DE50A_Timer();
			de50ATimer.start();
		}
	}

	private static L1MonsterInstance spawn(final L1PcInstance tgpc) {
		try {
			final L1Location loc = tgpc.getLocation().randomLocation(4, false);
			// 登場效果
			tgpc.sendPackets(new S_EffectLocation(loc, 3992));
			L1MonsterInstance mob = null;
			if (_random.nextBoolean()) {
				mob = L1SpawnUtil.spawnX(45582, loc, tgpc.get_showId());

			} else {
				mob = L1SpawnUtil.spawnX(45587, loc, tgpc.get_showId());
			}
			mob.setExp(1);
			return mob;

		} catch (final Exception e) {

		}
		return null;
	}
}
