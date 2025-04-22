package com.lineage.data.executor;

import com.lineage.server.templates.L1Event;

/**
 * Event(活動) 設置執行
 */
public abstract class EventExecutor {

	/**
	 * Event(活動) 設置執行
	 * 
	 * @param event
	 */
	public abstract void execute(L1Event event);
}
