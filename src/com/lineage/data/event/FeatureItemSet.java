package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;

/**
 * 特殊屬性<BR>
 * 除原先地水火風四大屬性之外 外加光屬性 暗屬性 聖屬性 邪屬性<BR>
 * <BR>
 * <font color=#00800>地屬性:束縛效果</font><BR>
 * 地之:1%束縛敵人0.8秒<BR>
 * 崩裂:2%束縛敵人1.0秒<BR>
 * 地靈:3%束縛敵人1.5秒<BR>
 * <BR>
 * <font color=#00800>水屬性:攻擊加倍效果</font><BR>
 * 火之:1%發動1.2倍傷害<BR>
 * 烈焰:2%發動1.4倍傷害<BR>
 * 火靈:3%發動1.6倍傷害<BR>
 * <BR>
 * <font color=#00800>火屬性:吸血吸魔效果</font><BR>
 * 水之:1%吸血吸魔(吸血質傷害*0.2 吸魔質隨機1~5)<BR>
 * 海嘯:2%吸血吸魔(吸血質傷害*0.4 吸魔質隨機2~10)<BR>
 * 水靈:3%吸血吸魔(吸血質傷害*0.6 吸魔質隨機3~15)<BR>
 * <BR>
 * <font color=#00800>風屬性:範圍傷害效果</font><BR>
 * 風之:1%發動1格範圍傷害(傷害:50+(0~99隨機))<BR>
 * 暴風:2%發動2格範圍傷害(傷害:50+(0~99隨機))<BR>
 * 風靈:3%發動3格範圍傷害(傷害:50+(0~99隨機))<BR>
 * <BR>
 * <font color=#00800>光屬性:附加究極光裂術</font><BR>
 * 光之:1%召喚光裂(依人物魔功智力產生傷害)<BR>
 * 閃耀:2%召喚光裂(依人物魔功智力產生傷害)<BR>
 * 光靈:3%召喚光裂(依人物魔功智力產生傷害)<BR>
 * <BR>
 * <font color=#00800>暗屬性:附加闇盲咒術</font><BR>
 * 暗之:1%施展闇盲<BR>
 * 陰影:2%施展闇盲<BR>
 * 暗靈:3%施展闇盲<BR>
 * <BR>
 * <font color=#00800>聖屬性:附加魔法封印</font><BR>
 * 聖之:1%施展魔法封印(封印時間:5秒)<BR>
 * 神聖:2%施展魔法封印(封印時間:8秒)<BR>
 * 聖靈:3%施展魔法封印(封印時間:10秒)<BR>
 * <BR>
 * <font color=#00800>邪屬性:附加變形術</font><BR>
 * 邪之:1%施展變形術(目標變形:狼人,妖魔鬥士)<BR>
 * 邪惡:2%施展變形術(目標變形:狼人,妖魔鬥士,人形殭屍)<BR>
 * 邪靈:3%施展變形術(目標變形:狼人,妖魔鬥士,人形殭屍,紙人)<BR>
 * <BR>
 * DELETE FROM `server_event` WHERE `id`='44'; INSERT INTO `server_event` VALUES
 * ('44', '特殊屬性', 'FeatureItemSet', '1', 'true', '說明:啟動特殊屬性攻擊'); DELETE FROM
 * `etcitem` WHERE `item_id`='44117'; DELETE FROM `etcitem` WHERE
 * `item_id`='44118'; DELETE FROM `etcitem` WHERE `item_id`='44119'; DELETE FROM
 * `etcitem` WHERE `item_id`='44120'; INSERT INTO `etcitem` VALUES ('44117',
 * '光之武器強化卷軸', 'reel.ScrollEnchantS1Weapon', '光之武器強化卷軸', 'scroll', 'dai',
 * 'paper', '630', '3702', '3963', '0', '1', '0', '0', '0', '0', '0', '1', '0',
 * '0', '0', '0', '0', '0', '0'); INSERT INTO `etcitem` VALUES ('44118',
 * '暗之武器強化卷軸', 'reel.ScrollEnchantS2Weapon', '暗之武器強化卷軸', 'scroll', 'dai',
 * 'paper', '630', '3703', '3963', '0', '1', '0', '0', '0', '0', '0', '1', '0',
 * '0', '0', '0', '0', '0', '0'); INSERT INTO `etcitem` VALUES ('44119',
 * '聖之武器強化卷軸', 'reel.ScrollEnchantS3Weapon', '聖之武器強化卷軸', 'scroll', 'dai',
 * 'paper', '630', '3700', '3963', '0', '1', '0', '0', '0', '0', '0', '1', '0',
 * '0', '0', '0', '0', '0', '0'); INSERT INTO `etcitem` VALUES ('44120',
 * '邪之武器強化卷軸', 'reel.ScrollEnchantS4Weapon', '邪之武器強化卷軸', 'scroll', 'dai',
 * 'paper', '630', '3701', '3963', '0', '1', '0', '0', '0', '0', '0', '1', '0',
 * '0', '0', '0', '0', '0', '0');
 * 
 * @author dexc
 */
public class FeatureItemSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(FeatureItemSet.class);

	// 啟用特效攻擊
	public static boolean POWER_START = false;

	/**
	 *
	 */
	private FeatureItemSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new FeatureItemSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final String[] set = event.get_eventother().split(",");

			POWER_START = Boolean.parseBoolean(set[0]);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
