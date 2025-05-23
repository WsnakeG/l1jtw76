package com.lineage.server.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1EtcItem;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 物件使用延遲
 * 
 * @author daien
 */
public class L1ItemDelay {

	private static final Log _log = LogFactory.getLog(L1ItemDelay.class);

	/**
	 * 500:武器禁止使用
	 */
	public static final int WEAPON = 500;// 500:武器禁止使用

	/**
	 * 501:防具禁止使用
	 */
	public static final int ARMOR = 501;// 501:防具禁止使用

	/**
	 * 502:道具禁止使用
	 */
	public static final int ITEM = 502;// 502:道具禁止使用

	/**
	 * 503:變身禁止使用
	 */
	public static final int POLY = 503;// 503:變身禁止使用

	private L1ItemDelay() {
	}

	/**
	 * 建立物件使用延遲計時器
	 * 
	 * @author daien
	 */
	static class ItemDelayTimer implements Runnable {

		private final int _delayId;

		private final int _delayTime;

		private final L1Character _cha;

		public ItemDelayTimer(final L1Character cha, final int id, final int time) {
			_cha = cha;
			_delayId = id;
			_delayTime = time;
		}

		@Override
		public void run() {
			stopDelayTimer(_delayId);
		}

		public int get_delayTime() {
			return _delayTime;
		}

		/**
		 * 停止該物件使用延遲
		 * 
		 * @param delayId
		 */
		public void stopDelayTimer(final int delayId) {
			_cha.removeItemDelay(delayId);
		}
	}

	/**
	 * 建立物件使用延遲
	 * 
	 * @param pc 執行人物
	 * @param delayId 延遲編號<BR>
	 *            500:武器禁止使用<BR>
	 *            501:防具禁止使用<BR>
	 *            502:道具禁止使用<BR>
	 *            503:變身禁止使用<BR>
	 *            504:禁止移動<BR>
	 * @param delayTime 延遲毫秒
	 */
	public static void onItemUse(final L1PcInstance pc, final int delayId, final int delayTime) {
		try {
			if ((delayId != 0) && (delayTime != 0)) {
				final ItemDelayTimer timer = new ItemDelayTimer(pc, delayId, delayTime);

				pc.addItemDelay(delayId, timer);
				GeneralThreadPool.get().schedule(timer, delayTime);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 建立物件使用延遲
	 * 
	 * @param client 執行連線端
	 * @param item 物件
	 */
	public static void onItemUse(final ClientExecutor client, final L1ItemInstance item) {
		try {
			final L1PcInstance pc = client.getActiveChar();
			if (pc != null) {
				onItemUse(pc, item);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 建立物件使用延遲
	 * 
	 * @param pc 執行人物
	 * @param item 物件
	 */
	public static void onItemUse(final L1PcInstance pc, final L1ItemInstance item) {
		try {
			int delayId = 0;
			int delayTime = 0;
			switch (item.getItem().getType2()) {
			case 0:
				// 種別：道具
				delayId = ((L1EtcItem) item.getItem()).get_delayid();
				delayTime = ((L1EtcItem) item.getItem()).get_delaytime();
				break;

			case 1:
				// 種別：武器
				return;

			case 2:
				// 種別：防具
				switch (item.getItemId()) {
				case 20077: // 隱身斗篷
				case 120077: // 隱身斗篷
				case 20062: // 炎魔的血光斗篷
					// 裝備使用中 並且 非隱身狀態
					if (item.isEquipped() && !pc.isInvisble()) {
						pc.beginInvisTimer();
					}
					break;

				default:// 其他道具
					return;
				}
				break;
			}

			if ((delayId != 0) && (delayTime != 0)) {
				final ItemDelayTimer timer = new ItemDelayTimer(pc, delayId, delayTime);

				pc.addItemDelay(delayId, timer);
				GeneralThreadPool.get().schedule(timer, delayTime);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
