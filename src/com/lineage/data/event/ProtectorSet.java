package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;

/**
 * 守護者系統
 * 
 * @author terry0412
 */
public class ProtectorSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(ProtectorSet.class);

	public static int ITEM_ID; // 守護者道具編號

	public static int CHANCE; // 掉落獲取機率 (單位:1/10000)

	public static int DROP_LIMIT; // 同時最多存在幾個

	public static int HP_UP; // 增加 體力上限

	public static int MP_UP; // 增加 魔力上限

	public static int DMG_UP; // 增加 額外攻擊點數

	public static int DMG_DOWN; // 增加 傷害減免

	public static int SP_UP; // 增加 魔攻

	public static boolean DEATH_VALUE_EXP; // 死亡時是否掉落經驗值

	public static boolean DEATH_VALUE_ITEM; // 死亡時是否掉落道具

	public static int EFFECT_ID; // 特效編號(每3秒一次)

	/**
	 *
	 */
	private ProtectorSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new ProtectorSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {

			final String[] set = event.get_eventother().split(",");

			ITEM_ID = Integer.parseInt(set[0]);

			CHANCE = Integer.parseInt(set[1]);

			DROP_LIMIT = Integer.parseInt(set[2]);

			HP_UP = Integer.parseInt(set[3]);

			MP_UP = Integer.parseInt(set[4]);

			DMG_UP = Integer.parseInt(set[5]);

			DMG_DOWN = Integer.parseInt(set[6]);

			SP_UP = Integer.parseInt(set[7]);

			DEATH_VALUE_EXP = Boolean.parseBoolean(set[8]);

			DEATH_VALUE_ITEM = Boolean.parseBoolean(set[9]);

			EFFECT_ID = Integer.parseInt(set[10]);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
