package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.LeavesTime;

/**
 * 殷海薩的祝福-休息系統<BR>
 * # 殷海薩的祝福-休息系統 DELETE FROM `server_event` WHERE `id`='56'; INSERT INTO
 * `server_event` VALUES ('56', '殷海薩的祝福-休息系統', 'LeavesSet', '1', '15,4000',
 * '說明:休息或登出的時間增加1%(單位:分鐘),設置時間增加的經驗質(EXP)');
 * 
 * @author dexc
 */
public class LeavesSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(LeavesSet.class);

	/** 殷海薩的祝福-休息系統 */
	public static boolean START = false;

	/** 說明:休息或登出的時間增加1%(單位:分鐘) */
	public static int TIME = 0;

	/** 說明:設置時間增加的經驗質(EXP) */
	public static int EXP = 0;

	/** 可增加EXP最大質 */
	public static int MAXEXP = 400000;

	/**
	 *
	 */
	private LeavesSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new LeavesSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			START = true;
			final String[] set = event.get_eventother().split(",");

			try {
				TIME = Integer.parseInt(set[0]);

			} catch (final Exception e) {
				TIME = 15;
				_log.error("未設定時間(使用預設15分鐘)");
			}

			try {
				EXP = Integer.parseInt(set[1]);

			} catch (final Exception e) {
				EXP = 4000;
				_log.error("未設定增加的經驗質(使用預設4000)");
			}

			MAXEXP = EXP * ConfigOther.LEAVES_MAXEXP; //預設500

			final LeavesTime leavesTime = new LeavesTime();
			leavesTime.start();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
