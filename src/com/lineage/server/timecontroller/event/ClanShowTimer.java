package com.lineage.server.timecontroller.event;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;

/**
 * 血盟技能光環 顯示處理
 * 
 * @author dexc
 */
public class ClanShowTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ClanShowTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 20 * 1000;// 20秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> all = World.get().getAllPlayers();
			// 不包含元素
			if (all.isEmpty()) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				if (check(tgpc)) {
					// 檢查城堡戰爭狀態
					if (ServerWarExecutor.get().checkCastleWar() <= 0) {
						if (checkC(tgpc)) {
							showClan(tgpc);
						}
					}
					Thread.sleep(10);
				}
			}

		} catch (final Exception e) {
			_log.error("血盟技能光環 顯示處理時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final ClanShowTimer clanShowTimer = new ClanShowTimer();
			clanShowTimer.start();
		}
	}

	/**
	 * 主判斷
	 * 
	 * @param tgpc
	 * @return true:執行 false:不執行
	 */
	private static boolean check(final L1PcInstance tgpc) {
		try {
			if (tgpc == null) {
				return false;
			}
			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}
			if (tgpc.getNetConnection() == null) {
				return false;
			}
			if (tgpc.isTeleport()) {
				return false;
			}
			if (tgpc.isDead()) {// 死亡
				return false;
			}
			if (tgpc.getCurrentHp() <= 0) {// 目前HP小於0
				return false;
			}
			switch (tgpc.getMapId()) {
			case 4:// 大陸地圖
			case 340:// 古鲁丁商店村
			case 350:// 奇岩商店村
			case 360:// 欧瑞商店村
			case 370:// 银骑士商店村
			case 800:// 新市場中心
				return false;
			}

		} catch (final Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 血盟技能光環系統 判斷
	 * 
	 * @return true:執行 false:不執行
	 */
	private static boolean checkC(final L1PcInstance tgpc) {
		try {
			if (tgpc.getClan() == null) {// 無血盟
				return false;
			}

			if (!tgpc.getClan().isClanskill()) {// 血盟無血盟技能
				return false;
			}

			final int count = tgpc.getClan().getOnlineClanMemberSize();
			if (count < 20) {// 血盟人數20人以下
				return false;
			}

			if (tgpc.get_other().get_clanskill() == 0) {// 無血盟技能
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * 展示效果
	 */
	private static void showClan(final L1PcInstance tgpc) {
		try {
			final int count = tgpc.getClan().getOnlineClanMemberSize();

			if (count >= 35) {// 血盟人數35人以上
				// 送出封包 血盟技能特效-20人
				tgpc.sendPacketsX8(new S_SkillSound(tgpc.getId(), ConfigAlt.showClanskill1)); // 4267 16171

			} else {
				// 送出封包 血盟技能特效-35人
				tgpc.sendPacketsX8(new S_SkillSound(tgpc.getId(), ConfigAlt.showClanskill2)); // 5295 16169
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
