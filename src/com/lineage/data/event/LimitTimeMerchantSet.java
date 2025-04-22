package com.lineage.data.event;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;


/**
 * 限購商人系統<BR>
 * 
 * @author hpc20207
 */
public class LimitTimeMerchantSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(LimitTimeMerchantSet.class);

	public static boolean ISOPEN = false;
	
	/**限時商人每日重置時間(小時)*/
	public static int TimeLimit_H;
	
	/**限時商人每日重置時間(分鐘)*/
	public static int TimeLimit_M;
	
	/**限時商人限制購買等級*/
//	public static int TimeLimit_Lv;
	
	private LimitTimeMerchantSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new LimitTimeMerchantSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			
			ISOPEN = true;
			
			final String[] set = event.get_eventother().split(",");
			String[] times = set[0].split(":");
			TimeLimit_H = Integer.parseInt(times[0]);
			TimeLimit_M = Integer.parseInt(times[1]);
			
//			TimeLimit_Lv = Integer.parseInt(set[1]);
			
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
