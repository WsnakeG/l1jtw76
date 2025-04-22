package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.data.item_etcitem.extra.Reward;
import com.lineage.server.templates.L1Event;

/**
 * 自動等級獎勵系統
 * 
 * @author Roy
 */
public class RewardSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(RewardSet.class);

	public static boolean RewardSTART = false;

	/**
	 *
	 */
	private RewardSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new RewardSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			RewardSTART = true;

			// 等級給予獎勵紀錄
			Reward.get();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
