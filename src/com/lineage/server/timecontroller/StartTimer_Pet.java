package com.lineage.server.timecontroller;

import com.lineage.server.timecontroller.pet.DollAidTimer;
import com.lineage.server.timecontroller.pet.DollGetTimer;
import com.lineage.server.timecontroller.pet.DollHprTimer;
import com.lineage.server.timecontroller.pet.DollMprTimer;
import com.lineage.server.timecontroller.pet.DollTimer;
import com.lineage.server.timecontroller.pet.HierarchMprTimer;
import com.lineage.server.timecontroller.pet.HierarchTimer;
import com.lineage.server.timecontroller.pet.PetHprTimer;
import com.lineage.server.timecontroller.pet.PetMprTimer;
import com.lineage.server.timecontroller.pet.SummonHprTimer;
import com.lineage.server.timecontroller.pet.SummonMprTimer;
import com.lineage.server.timecontroller.pet.SummonTimer;

/**
 * PET專用時間軸 初始化啟動
 * 
 * @author dexc
 */
public class StartTimer_Pet {

	public void start() throws InterruptedException {

		// Pet HP自然回復時間軸異
		final PetHprTimer petHprTimer = new PetHprTimer();
		petHprTimer.start();
		Thread.sleep(50);// 延遲

		// Pet MP自然回復時間軸
		final PetMprTimer petMprTimer = new PetMprTimer();
		petMprTimer.start();
		Thread.sleep(50);// 延遲

		// Summon HP自然回復時間軸
		final SummonHprTimer summonHprTimer = new SummonHprTimer();
		summonHprTimer.start();
		Thread.sleep(50);// 延遲

		// Summon MP自然回復時間軸
		final SummonMprTimer summonMprTimer = new SummonMprTimer();
		summonMprTimer.start();
		Thread.sleep(50);// 延遲

		// 召喚獸處理時間軸
		final SummonTimer summon_Timer = new SummonTimer();
		summon_Timer.start();
		Thread.sleep(50);// 延遲

		// 祭司 mp自然回復時間軸
		final HierarchMprTimer hierarchMprTimer = new HierarchMprTimer();
		hierarchMprTimer.start();
		Thread.sleep(50);// 延遲

		// 祭司處理時間軸
		final HierarchTimer hierarchTimer = new HierarchTimer();
		hierarchTimer.start();
		Thread.sleep(50);// 延遲

		// 魔法娃娃處理時間軸
		final DollTimer dollTimer = new DollTimer();
		dollTimer.start();

		final DollHprTimer dollHpTimer = new DollHprTimer();
		dollHpTimer.start();

		final DollMprTimer dollMpTimer = new DollMprTimer();
		dollMpTimer.start();

		final DollGetTimer dollGetTimer = new DollGetTimer();
		dollGetTimer.start();

		final DollAidTimer dollAidTimer = new DollAidTimer();
		dollAidTimer.start();
	}
}
