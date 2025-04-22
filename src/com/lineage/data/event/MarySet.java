package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.lock.MaryReading;
import com.lineage.server.templates.L1Event;

/**
 * 小瑪莉<BR>
 * SET FOREIGN_KEY_CHECKS=0; -- ---------------------------- -- 新增資料表
 * `server_mary` -- ---------------------------- DROP TABLE IF EXISTS
 * `server_mary`; CREATE TABLE `server_mary` ( `id` int(10) NOT NULL
 * AUTO_INCREMENT, `all_stake` bigint(10) unsigned NOT NULL DEFAULT '0' COMMENT
 * '累積賭注', `all_user_prize` bigint(10) unsigned NOT NULL DEFAULT '0' COMMENT
 * '累積中獎金額', `out_prize` int(10) NOT NULL DEFAULT '80' COMMENT '輸出獎金百分比',
 * `item_id` int(10) NOT NULL DEFAULT '40308' COMMENT '需要下注的物品編號', `count`
 * int(10) unsigned NOT NULL DEFAULT '1' COMMENT '已使用次數', `x_a1` int(10) NOT
 * NULL DEFAULT '100' COMMENT '大BAR', `x_a2` int(10) NOT NULL DEFAULT '50'
 * COMMENT '小BAR', `x_b1` int(10) NOT NULL DEFAULT '40' COMMENT '大半瓜', `x_b2`
 * int(10) NOT NULL DEFAULT '2' COMMENT '小半瓜', `x_c1` int(10) NOT NULL DEFAULT
 * '30' COMMENT '大蘋果', `x_c2` int(10) NOT NULL DEFAULT '2' COMMENT '小蘋果', `x_d1`
 * int(10) NOT NULL DEFAULT '20' COMMENT '大西瓜', `x_d2` int(10) NOT NULL DEFAULT
 * '2' COMMENT '小西瓜', `x_e1` int(10) NOT NULL DEFAULT '15' COMMENT '大香蕉', `x_e2`
 * int(10) NOT NULL DEFAULT '2' COMMENT '小香蕉', `x_f1` int(10) NOT NULL DEFAULT
 * '10' COMMENT '大檸檬', `x_f2` int(10) NOT NULL DEFAULT '2' COMMENT '小檸檬', `x_g1`
 * int(10) NOT NULL DEFAULT '5' COMMENT '大橘子', `x_g2` int(10) NOT NULL DEFAULT
 * '2' COMMENT '小橘子', `note` varchar(255) DEFAULT NULL COMMENT '備註', PRIMARY KEY
 * (`id`), KEY `id` (`id`) ) ENGINE=MyISAM AUTO_INCREMENT=91387 DEFAULT
 * CHARSET=utf8; -- ---------------------------- -- 建立資料表內容 --
 * ---------------------------- INSERT INTO `server_mary` VALUES ('1', '0', '0',
 * '80', '40308', '1', '100', '50', '40', '2', '30', '2', '20', '2', '15', '2',
 * '10', '2', '5', '2', 'id:無作用 all_stake:累積賭注 all_user_prize:累積中獎金額
 * out_prize:輸出彩金百分比 count:已使用次數 x_a:BAR x_b:半瓜 x_c:蘋果 x_d:西瓜 x_e:香蕉 x_f:檸檬
 * x_g:橘子(1:大的2:小的)'); # 新增小瑪莉系統 DELETE FROM `server_event` WHERE `id`='26';
 * INSERT INTO `server_event` VALUES ('26', '小瑪莉系統', 'MarySet', '1', '0',
 * '說明:系統相關設置均在server_mary資料表中'); # 新增小瑪莉管理員 DELETE FROM `npc` WHERE
 * `npcid`='91175' ; INSERT INTO `npc` VALUES ('91175', '小瑪莉管理員', '小瑪莉管理員',
 * 'gam.Npc_Mary', '', 'L1Merchant', '8592', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '', '0', '-1', '-1', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1',
 * '0', '14', '0', '1', '0'); #新增銀行管理員召換位置 DELETE FROM `server_event_spawn`
 * WHERE `id`='40309'; DELETE FROM `server_event_spawn` WHERE `id`='40310';
 * DELETE FROM `server_event_spawn` WHERE `id`='40311'; INSERT INTO
 * `server_event_spawn` VALUES ('40309', '26', '小瑪莉管理員', '1', '91175', '0',
 * '33513', '32851', '0', '0', '4', '0', '4', '0', '0'); INSERT INTO
 * `server_event_spawn` VALUES ('40310', '26', '小瑪莉管理員', '1', '91175', '0',
 * '33508', '32851', '0', '0', '4', '0', '4', '0', '0'); INSERT INTO
 * `server_event_spawn` VALUES ('40311', '26', '小瑪莉管理員', '1', '91175', '0',
 * '33503', '32851', '0', '0', '4', '0', '4', '0', '0');
 * 
 * @author dexc
 */
public class MarySet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(MarySet.class);

	public static boolean START = false;

	/**
	 *
	 */
	private MarySet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new MarySet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			// final String[] set = event.get_eventother().split(",");
			START = true;
			// 載入資料
			MaryReading.get().load();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
