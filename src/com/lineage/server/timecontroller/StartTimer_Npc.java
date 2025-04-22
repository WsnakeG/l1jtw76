package com.lineage.server.timecontroller;

import com.lineage.server.timecontroller.npc.NpcBowTimer;
import com.lineage.server.timecontroller.npc.NpcBoxRepairTimer;
import com.lineage.server.timecontroller.npc.NpcChatTimer;
import com.lineage.server.timecontroller.npc.NpcDeadTimer;
import com.lineage.server.timecontroller.npc.NpcDeleteTimer;
import com.lineage.server.timecontroller.npc.NpcDigestItemTimer;
import com.lineage.server.timecontroller.npc.NpcHprTimer;
import com.lineage.server.timecontroller.npc.NpcMprTimer;
import com.lineage.server.timecontroller.npc.NpcRestTimer;
import com.lineage.server.timecontroller.npc.NpcShopTimer;
import com.lineage.server.timecontroller.npc.NpcSpawnBossTimer;
import com.lineage.server.timecontroller.npc.NpcWorkTimer;

/**
 * NPC專用時間軸 初始化啟動
 * 
 * @author dexc
 */
public class StartTimer_Npc {

	public void start() throws InterruptedException {
		// NPC對話計時器
		final NpcChatTimer npcChatTimeController = new NpcChatTimer();
		npcChatTimeController.start();
		Thread.sleep(50);// 延遲

		// HP 回復
		final NpcHprTimer npcHprTimer = new NpcHprTimer();
		npcHprTimer.start();
		Thread.sleep(50);// 延遲

		// MP 回復
		final NpcMprTimer npcMprTimer = new NpcMprTimer();
		npcMprTimer.start();
		Thread.sleep(50);// 延遲

		// 時間性質NPC
		final NpcDeleteTimer npcDeleteTimer = new NpcDeleteTimer();
		npcDeleteTimer.start();
		Thread.sleep(50);// 延遲

		// 死亡NPC
		final NpcDeadTimer npcDeadTimer = new NpcDeadTimer();
		npcDeadTimer.start();
		Thread.sleep(50);// 延遲

		// NPC消化道具時間
		final NpcDigestItemTimer digestItemTimer = new NpcDigestItemTimer();
		digestItemTimer.start();
		Thread.sleep(50);// 延遲

		// NPC(BOSS)召喚時間時間軸
		final NpcSpawnBossTimer bossTimer = new NpcSpawnBossTimer();
		bossTimer.start();
		Thread.sleep(50);// 延遲

		// Npc 虛擬商店時間軸
		final NpcShopTimer shopTimer = new NpcShopTimer();
		shopTimer.start();
		Thread.sleep(50);// 延遲

		// NPC動作暫停
		final NpcRestTimer restTimer = new NpcRestTimer();
		restTimer.start();
		Thread.sleep(50);// 延遲

		// NPC工作時間軸
		final NpcWorkTimer workTimer = new NpcWorkTimer();
		workTimer.start();
		Thread.sleep(50);// 延遲

		// 箭孔
		final NpcBowTimer bow = new NpcBowTimer();
		bow.start();

		Thread.sleep(50);// 延遲

		// NPC寶箱 by terry0412
		final NpcBoxRepairTimer boxRepair = new NpcBoxRepairTimer();
		boxRepair.start();
	}
}
