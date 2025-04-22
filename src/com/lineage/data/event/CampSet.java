package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.datatables.lock.CharacterC1Reading;
import com.lineage.server.templates.L1Event;

/**
 * 陣營系統<BR>
 * #新增陣營系統 DELETE FROM `server_event` WHERE `id`='41'; INSERT INTO
 * `server_event` VALUES ('41', '陣營系統', 'CampSet', '1', '0', '說明:啟動陣營系統');
 * #新增陣營管理員 UPDATE `npc` SET `name`=
 * '陣營管理員',`nameid`='陣營管理員',`classname`='event.Npc_Camp',`note`='',`gfxid`='4306
 * ' WHERE `npcid`='60037';#陣營管理員(王族外型) #新增陣營管理員召換位置 DELETE FROM
 * `server_event_spawn` WHERE `eventid`='41'; DELETE FROM `server_event_spawn`
 * WHERE `id`='40309'; #DELETE FROM `server_event_spawn` WHERE `id`='40310';
 * INSERT INTO `server_event_spawn` VALUES ('40309', '41', '陣營管理員', '1',
 * '60037', '0', '32777', '32840', '0', '0', '6', '0', '340', '0', '0'); #INSERT
 * INTO `server_event_spawn` VALUES ('40310', '41', '陣營管理員', '1', '60037', '0',
 * '33070', '33402', '0', '0', '4', '0', '4', '0', '0');
 * 
 * @author dexc
 */
public class CampSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(CampSet.class);

	public static boolean CAMPSTART = false;

	/**
	 *
	 */
	private CampSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new CampSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			CAMPSTART = true;

			// 國度名稱記錄
			C1_Name_Table.get().load();

			// 國度階級能力記錄
			C1_Name_Type_Table.get().load();

			// 人物陣營紀錄
			CharacterC1Reading.get().load();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
