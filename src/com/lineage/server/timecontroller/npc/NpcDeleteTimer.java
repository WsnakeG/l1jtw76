package com.lineage.server.timecontroller.npc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.npc.quest2.Npc_DragonB1;
import com.lineage.data.npc.quest2.Npc_DragonB2;
import com.lineage.data.npc.quest2.Npc_DragonB3;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldNpc;

/**
 * NPC存在時間時間軸
 * 
 * @author dexc
 */
public class NpcDeleteTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcDeleteTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 1000;// 1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1NpcInstance> allNpc = WorldNpc.get().all();
			// 不包含元素
			if (allNpc.isEmpty()) {
				return;
			}

			for (final Iterator<L1NpcInstance> iter = allNpc.iterator(); iter.hasNext();) {
				final L1NpcInstance npc = iter.next();
				// 不具有存在時間
				if (!npc.is_spawnTime()) {
					continue;
				}

				final int time = npc.get_spawnTime() - 1;
				if (time > 0) {
					// 更新
					npc.set_spawnTime(time);

				} else {
					remove(npc);
				}
				Thread.sleep(10);
			}

		} catch (final Exception e) {
			_log.error("NPC存在時間時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final NpcDeleteTimer npcDeleteTimer = new NpcDeleteTimer();
			npcDeleteTimer.start();
		}
	}

	/**
	 * 刪除NPC(解除召喚)
	 * 
	 * @param tgnpc
	 */
	private static void remove(final L1NpcInstance tgnpc) {
		try {
			boolean isRemove = false;

			// 對象是怪物
			if (tgnpc instanceof L1MonsterInstance) {
				if (tgnpc.getNpcId() == 80034) {// 史巴托(史巴托的復仇)
					tgnpc.outParty(tgnpc);
				}
				isRemove = true;
			}

			// 對象是分身
			if (tgnpc instanceof L1IllusoryInstance) {
				isRemove = true;
			}

			if (isRemove) {
				tgnpc.setCurrentHpDirect(0);
				tgnpc.setDead(true);
				// 解除舊座標障礙宣告
				tgnpc.getMap().setPassable(tgnpc.getLocation(), true);

				tgnpc.setExp(0);
				tgnpc.setKarma(0);
				tgnpc.allTargetClear();
			}

			// 龍之門扉 移除處理 by terry0412
			if (tgnpc.getNpcId() == 70932) { // 地
				tgnpc.broadcastPacketAll(new S_DoActionGFX(tgnpc.getId(), ActionCodes.ACTION_Die));
				// 重新開放新地龍
				if (Npc_DragonB1._timer.containsKey(tgnpc.get_quest_id())) {
					Npc_DragonB1._timer.remove(tgnpc.get_quest_id());
				}
			} else if (tgnpc.getNpcId() == 70937) { // 水
				tgnpc.broadcastPacketAll(new S_DoActionGFX(tgnpc.getId(), ActionCodes.ACTION_Die));
				// 重新開放新水龍
				if (Npc_DragonB2._timer.containsKey(tgnpc.get_quest_id())) {
					Npc_DragonB2._timer.remove(tgnpc.get_quest_id());
				}
			} else if (tgnpc.getNpcId() == 70934) { // 風
				tgnpc.broadcastPacketAll(new S_DoActionGFX(tgnpc.getId(), ActionCodes.ACTION_Die));
				// 重新開放新風龍
				if (Npc_DragonB3._timer.containsKey(tgnpc.get_quest_id())) {
					Npc_DragonB3._timer.remove(tgnpc.get_quest_id());
				}
			} else if (tgnpc.getNpcId() == 70933) { // 火
				tgnpc.broadcastPacketAll(new S_DoActionGFX(tgnpc.getId(), ActionCodes.ACTION_Die));
				// 重新開放新火龍
				/*
				 * if (Npc_DragonB4._timer.containsKey(tgnpc.get_quest_id())) {
				 * Npc_DragonB4._timer.remove(tgnpc.get_quest_id()); }
				 */
			}

			tgnpc.deleteMe();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
