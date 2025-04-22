package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ServerAIMapIdTable;
import com.lineage.server.datatables.ServerAIEffectTable;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 * 特效驗證系統<BR>
 * <font color="#0000FF">需搭配另外的server_ai_effect、server_ai_mapid設定</font><BR>
 * <font color="#0000FF">逞罰類型為1.傳送指定位置2.封鎖IP</font><BR>
 * <font color="#6E8B3D">驗證特效可隨時更換</font><BR>
 * 
 * 說明:固定時間,亂數時間,允許錯誤次數,懲罰類型,每次作答時間,座標x,座標y,地圖編號
 * 
 * @author erics4179
 */

public class EffectAISet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(EffectAISet.class);

	public static boolean START = false;
	public static int AI_TIME = 0;
	public static int AI_TIME_RANDOM = 0;
	public static int AI_ERROR_COUNT = 0;
	public static int AI_ERROR_TYPE = 0;
	public static int AI_ANSWER_TIME = 0;
	public static int AI_LOCX = 0;
	public static int AI_LOCY = 0;
	public static short AI_MAPID = 0;

	private EffectAISet() {
	}

	public static EventExecutor get() {
		return new EffectAISet();
	}

	@Override
	public void execute(L1Event event) {
		try {
			START = true;

			String[] set = event.get_eventother().split(",");

			AI_TIME = Integer.parseInt(set[0]);

			AI_TIME_RANDOM = Integer.parseInt(set[1]);

			AI_ERROR_COUNT = Integer.parseInt(set[2]);

			AI_ERROR_TYPE = Integer.parseInt(set[3]);

			AI_ANSWER_TIME = Integer.parseInt(set[4]);

			AI_LOCX = Integer.parseInt(set[5]);

			AI_LOCY = Integer.parseInt(set[6]);

			AI_MAPID = Short.parseShort(set[7]);

			ServerAIMapIdTable.get();
			ServerAIEffectTable.get();

			EffectTimer time = new EffectTimer();
			time.start();

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private class EffectTimer extends TimerTask {

		private ScheduledFuture<?> _timer;

		public void start() {
			final int timeMillis = 60 * 1000;
			_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
		}

		@Override
		public void run() {
			try {
				Collection<L1PcInstance> all = World.get().getAllPlayers();
				if (all.isEmpty()) {
					return;
				}
				for (Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext();) {
					L1PcInstance tgpc = (L1PcInstance) iter.next();
					if (tgpc != null) {
						int map_id = tgpc.getMapId();
						if (!ServerAIMapIdTable.get().checkCantAI(map_id)) {
							if ((!tgpc.isSafetyZone()) ||

							(ServerAIMapIdTable.get().checkCanAI(map_id))) {
								if (!tgpc.isDead()) {
									if (!tgpc.isTeleport()) {
										if ((!tgpc.isGm()) && (tgpc.getAccessLevel() <= 0)) {
											if (tgpc.getHellTime() <= 0) {
												if (L1CastleLocation.getCastleIdByArea(tgpc) == 0) {
													if (tgpc.getAITimer() > 1) {
														tgpc.addAITimer();
													} else {
														tgpc.sendPackets(new S_BlueMessage(166, "\\f2A I 驗 證 5 秒 後 開 始"));

														tgpc.setSkillEffect(7020, 5000);
														if (AI_TIME_RANDOM != 0) {
															Random _random = new Random();
															tgpc.setAITimer(_random.nextInt(AI_TIME_RANDOM) + AI_TIME);
														} else {
															tgpc.setAITimer(AI_TIME);
														}
														Thread.sleep(1L);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				_log.error("AI驗證處理時間軸異常重啟", e);
				GeneralThreadPool.get().cancel(this._timer, false);
				EffectTimer time = new EffectTimer();
				time.start();
			}
		}
	}
}
