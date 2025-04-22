package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ItemUpdateTable;
import com.lineage.server.templates.L1Event;

/**
 * 升級裝備<BR>
 * # 新增升級裝備系統 DELETE FROM `server_event` WHERE `id`='37'; INSERT INTO
 * `server_event` VALUES ('37', '升級裝備系統', 'ItemUpdateSet', '1', 'true',
 * '說明:是否提供原始裝備附加屬性保留 true:是 false:否(不提供可堆疊物件交換)'); # 建立升級NPC DELETE FROM `npc`
 * WHERE `npcid`='91141'; INSERT INTO `npc` VALUES ('91141', '物品升級專員', '物品升級專員',
 * 'shop.NPC_ItemUpdate', '', 'L1Merchant', '6757', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '', '1', '-1', '-1', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0',
 * '0', '-1', '0', '0', '0', '0', '0');
 * 
 * @author dexc
 */
public class ItemUpdateSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(ItemUpdateSet.class);

	/**
	 * 是否提供原始裝備附加屬性保留<BR>
	 * true:是 false:否
	 */
	public static boolean MODE = false;// 是否提供原始裝備附加屬性保留

	/**
	 *
	 */
	private ItemUpdateSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new ItemUpdateSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final String[] set = event.get_eventother().split(",");

			MODE = Boolean.parseBoolean(set[0]);

			ItemUpdateTable.get().load();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
