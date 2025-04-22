package com.lineage.server.timecontroller;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.server.timecontroller.pc.AutoSkillTimer;
import com.lineage.server.timecontroller.pc.HprTimerCrown;
import com.lineage.server.timecontroller.pc.HprTimerDarkElf;
import com.lineage.server.timecontroller.pc.HprTimerDragonKnight;
import com.lineage.server.timecontroller.pc.HprTimerElf;
import com.lineage.server.timecontroller.pc.HprTimerIllusionist;
import com.lineage.server.timecontroller.pc.HprTimerKnight;
import com.lineage.server.timecontroller.pc.HprTimerWarrior;
import com.lineage.server.timecontroller.pc.HprTimerWizard;
import com.lineage.server.timecontroller.pc.MprTimerCrown;
import com.lineage.server.timecontroller.pc.MprTimerDarkElf;
import com.lineage.server.timecontroller.pc.MprTimerDragonKnight;
import com.lineage.server.timecontroller.pc.MprTimerElf;
import com.lineage.server.timecontroller.pc.MprTimerIllusionist;
import com.lineage.server.timecontroller.pc.MprTimerKnight;
import com.lineage.server.timecontroller.pc.MprTimerWarrior;
import com.lineage.server.timecontroller.pc.MprTimerWizard;
import com.lineage.server.timecontroller.pc.PartyTimer;
import com.lineage.server.timecontroller.pc.PcAutoSaveInventoryTimer;
import com.lineage.server.timecontroller.pc.PcAutoSaveTimer;
import com.lineage.server.timecontroller.pc.PcDeleteTimer;
import com.lineage.server.timecontroller.pc.PcEffectTimer;
import com.lineage.server.timecontroller.pc.PcGhostTimer;
import com.lineage.server.timecontroller.pc.PcHellTimer;
import com.lineage.server.timecontroller.pc.UnfreezingTimer;
import com.lineage.server.timecontroller.pc.UpdateObjectCTimer;
import com.lineage.server.timecontroller.pc.UpdateObjectDKTimer;
import com.lineage.server.timecontroller.pc.UpdateObjectDTimer;
import com.lineage.server.timecontroller.pc.UpdateObjectETimer;
import com.lineage.server.timecontroller.pc.UpdateObjectITimer;
import com.lineage.server.timecontroller.pc.UpdateObjectKTimer;
import com.lineage.server.timecontroller.pc.UpdateObjectOTimer;
import com.lineage.server.timecontroller.pc.UpdateObjectWTimer;

/**
 * PC專用時間軸 初始化啟動
 * 
 * @author dexc
 */
public class StartTimer_Pc {

	public void start() throws InterruptedException {
		// 人物資料自動保存計時器
		if (Config.AUTOSAVE_INTERVAL > 0) {
			final PcAutoSaveTimer save = new PcAutoSaveTimer();
			save.start();
		}
		// 背包物品自動保存計時器
		if (Config.AUTOSAVE_INTERVAL_INVENTORY > 0) {
			final PcAutoSaveInventoryTimer save = new PcAutoSaveInventoryTimer();
			save.start();
		}

		// PC 可見物更新處理 時間軸 XXX
		final UpdateObjectCTimer objectCTimer = new UpdateObjectCTimer();
		objectCTimer.start();
		final UpdateObjectDKTimer objectDKTimer = new UpdateObjectDKTimer();
		objectDKTimer.start();
		final UpdateObjectDTimer objectDTimer = new UpdateObjectDTimer();
		objectDTimer.start();
		final UpdateObjectETimer objectETimer = new UpdateObjectETimer();
		objectETimer.start();
		final UpdateObjectITimer objectITimer = new UpdateObjectITimer();
		objectITimer.start();
		final UpdateObjectKTimer objectKTimer = new UpdateObjectKTimer();
		objectKTimer.start();
		final UpdateObjectWTimer objectWTimer = new UpdateObjectWTimer();
		objectWTimer.start();
		final UpdateObjectOTimer objectOTimer = new UpdateObjectOTimer();
		objectOTimer.start();
		Thread.sleep(50);// 延遲

		final HprTimerCrown hprCrown = new HprTimerCrown();
		hprCrown.start();
		final HprTimerDarkElf hprDarkElf = new HprTimerDarkElf();
		hprDarkElf.start();
		final HprTimerDragonKnight hprDK = new HprTimerDragonKnight();
		hprDK.start();
		final HprTimerElf hprElf = new HprTimerElf();
		hprElf.start();
		final HprTimerIllusionist hprIllusionist = new HprTimerIllusionist();
		hprIllusionist.start();
		final HprTimerKnight hprKnight = new HprTimerKnight();
		hprKnight.start();
		final HprTimerWizard hprWizard = new HprTimerWizard();
		hprWizard.start();
		final HprTimerWarrior hprWarrior = new HprTimerWarrior();
		hprWarrior.start();
		Thread.sleep(50);// 延遲

		final MprTimerCrown mprCrown = new MprTimerCrown();
		mprCrown.start();
		final MprTimerDarkElf mprDarkElf = new MprTimerDarkElf();
		mprDarkElf.start();
		final MprTimerDragonKnight mprDragonKnight = new MprTimerDragonKnight();
		mprDragonKnight.start();
		final MprTimerElf mprElf = new MprTimerElf();
		mprElf.start();
		final MprTimerIllusionist mprIllusionist = new MprTimerIllusionist();
		mprIllusionist.start();
		final MprTimerKnight mprKnight = new MprTimerKnight();
		mprKnight.start();
		final MprTimerWizard mprWizard = new MprTimerWizard();
		mprWizard.start();
		final MprTimerWarrior mprWarrior = new MprTimerWarrior();
		mprWarrior.start();
		Thread.sleep(50);// 延遲

		// PC EXP更新處理 時間軸
		// final ExpTimer expTimer = new ExpTimer();
		// expTimer.start();
		// PC Lawful更新處理 時間軸
		// final LawfulTimer lawfulTimer = new LawfulTimer();
		// lawfulTimer.start();
		// PC 死亡刪除處理 時間軸
		final PcDeleteTimer deleteTimer = new PcDeleteTimer();
		deleteTimer.start();
		// PC 鬼魂模式處理 時間軸
		final PcGhostTimer ghostTimer = new PcGhostTimer();
		ghostTimer.start();
		// PC 解除人物卡點計時時間軸
		final UnfreezingTimer unfreezingTimer = new UnfreezingTimer();
		unfreezingTimer.start();
		// 隊伍更新時間軸
		final PartyTimer partyTimer = new PartyTimer();
		partyTimer.start();
		Thread.sleep(50);// 延遲

		if (ConfigAlt.ALT_PUNISHMENT) {
			final PcHellTimer hellTimer = new PcHellTimer();
			hellTimer.start();
		}

		// PC 特效編號時間軸 by terry0412
		final PcEffectTimer effectTimer = new PcEffectTimer();
		effectTimer.start();

		final AutoSkillTimer ReseAutoTimer = new AutoSkillTimer();
		ReseAutoTimer.start();

	}
}
