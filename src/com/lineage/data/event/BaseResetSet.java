package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;

/**
 * 回憶蠟燭系統<BR>
 * # 回憶蠟燭系統 DELETE FROM `server_event` WHERE `id`='55'; INSERT INTO
 * `server_event` VALUES ('55', '回憶蠟燭系統', 'BaseResetSet', '1', '30', '說明:回憶蠟燭系統
 * 使用後HP/MP保留百分比(1/100)'); # 更新回憶蠟燭嚮導露露 DELETE FROM `npcaction` WHERE
 * `npcid`='71251'; UPDATE `npc` SET `classname`='event.Npc_BaseReset' WHERE
 * `npcid`='71251';# 露露 #新增回憶蠟燭嚮導露露召換位置 DELETE FROM `server_event_spawn` WHERE
 * `eventid`='55'; DELETE FROM `server_event_spawn` WHERE `id`='40170'; INSERT
 * INTO `server_event_spawn` VALUES (40170, 55, '回憶蠟燭嚮導露露', 1, 71251, 0, 32610,
 * 32775, 0, 0, 4, 0, 4, 0, 1);
 * 
 * @author dexc
 */
public class BaseResetSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(BaseResetSet.class);

	public static int RETAIN = 0;// 1/100

	/**
	 *
	 */
	private BaseResetSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new BaseResetSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final String[] set = event.get_eventother().split(",");

			try {
				RETAIN = Integer.parseInt(set[0]);

			} catch (final Exception e) {
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
