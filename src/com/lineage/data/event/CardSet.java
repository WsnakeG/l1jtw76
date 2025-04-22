package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.templates.L1Event;

/**
 * 月卡系統<BR>
 * #新增月卡系統 DELETE FROM `server_event` WHERE `id`='42'; INSERT INTO
 * `server_event` VALUES ('42', '月卡系統', 'CardSet', '1', '720,24',
 * '說明:月卡使用期限720小時 = 30天(單位:小時),日卡使用期限24小時 = 1天(單位:小時)'); DELETE FROM
 * `server_event_spawn` WHERE `eventid`='42'; UPDATE `etcitem` SET
 * `classname`='shop.VIP_Card_A1' WHERE `item_id`='44188';# 三國日卡[普] UPDATE
 * `etcitem` SET `classname`='shop.VIP_Card_A2' WHERE `item_id`='44189';#
 * 三國日卡[金] UPDATE `etcitem` SET `classname`='shop.VIP_Card_A3' WHERE
 * `item_id`='44190';# 三國日卡[白金] UPDATE `etcitem` SET
 * `classname`='shop.VIP_Card_A4' WHERE `item_id`='44191';# 三國日卡[白金限量] UPDATE
 * `etcitem` SET `classname`='shop.VIP_Card_A5' WHERE `item_id`='44192';#
 * 三國日卡[獨特限量] 2014/07/22 by erics4179 修改普卡(耐性原+3修改為+5)裝備與解除皆有修改
 * 
 * @author dexc
 */
public class CardSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(CardSet.class);

	// 月卡系統
	public static boolean START = false;

	// 月卡使用期限
	public static int USE_TIME;

	// 日卡使用期限
	public static int USE_TIME2;

	/**
	 *
	 */
	private CardSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new CardSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			START = true;
			final String[] set = event.get_eventother().split(",");

			try {
				USE_TIME = Integer.parseInt(set[0]);

			} catch (final Exception e) {
				USE_TIME = 720;
				_log.error("未設定月卡使用期限(使用預設720小時)");
			}

			try {
				USE_TIME2 = Integer.parseInt(set[1]);

			} catch (final Exception e) {
				USE_TIME2 = 24;
				_log.error("未設定日卡使用期限(使用預設24小時)");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 人物登入
	 * 
	 * @param pc
	 */
	public static void load_card_mode(final L1PcInstance pc) {
		try {
			for (final L1ItemInstance item : pc.getInventory().getItems()) {
				final String classname = item.getItem().getclassname();
				if (!classname.startsWith("shop.VIP_Card_")) {
					continue;
				}
				if (item.get_card_use() != 1) {
					item.setEquipped(false);
					continue;
				}
				int card_id = 0;
				try {
					final String cardmode = classname.substring(14);
					card_id = Integer.parseInt(cardmode);
				} catch (final Exception e) {
					final String cardmode = classname.substring(15);
					card_id = Integer.parseInt(cardmode);
				}
				if (card_id == 0) {
					return;
				}

				item.setEquipped(true);
				switch (card_id) {
				case 1:// # 44129 普卡 狩獵經驗值+10% 暈眩耐性+3 寒冰耐性+3 石化耐性+3 睡眠耐性+3
						// 死亡後不會掉落經驗
					pc.set_VIP1(true);
					pc.set_expadd(10);
					pc.addRegistStun(5);// 暈眩耐性
					pc.addRegistFreeze(5);// 寒冰耐性
					pc.addRegistStone(5);// 石化耐性
					pc.addRegistSleep(5);// 睡眠耐性
					break;

				case 2:// # 44130 金卡 狩獵經驗值+20% 力量+1 體質+1 敏捷+1 精神+1 智慧+1 魅力+1
						// 死亡後不會掉落陣營經驗
					pc.set_VIP2(true);
					pc.set_expadd(20);
					pc.addStr(1);// 力量
					pc.addDex(1);// 敏捷
					pc.addCon(1);// 體質
					pc.addWis(1);// 精神
					pc.addInt(1);// 智力
					pc.addCha(1);// 魅力
					break;

				case 3:// # 44131 白金卡 狩獵經驗值+30% 力量+2 體質+2 敏捷+2 精神+2 智慧+2 魅力+2
						// 死亡後不會掉落道具
					pc.set_VIP3(true);
					pc.set_expadd(30);
					pc.addStr(2);// 力量
					pc.addDex(2);// 敏捷
					pc.addCon(2);// 體質
					pc.addWis(2);// 精神
					pc.addInt(2);// 智力
					pc.addCha(2);// 魅力
					break;

				case 4:// # 44132 白金限量卡 狩獵經驗值+40% 力量+3 體質+3 敏捷+3 精神+3 智慧+3 魅力+3
						// 回血+5 回魔+5
					pc.set_expadd(40);
					pc.addStr(3);// 力量
					pc.addDex(3);// 敏捷
					pc.addCon(3);// 體質
					pc.addWis(3);// 精神
					pc.addInt(3);// 智力
					pc.addCha(3);// 魅力
					pc.addHpr(5);
					pc.addMpr(5);
					break;

				case 5:// # 44133 獨特限量卡 狩獵經驗值+50% 力量+4 體質+4 敏捷+4 精神+4 智慧+4 魅力+4
						// 回血+10 回魔+10
					pc.set_VIP4(true);
					pc.set_expadd(50);
					pc.addStr(4);// 力量
					pc.addDex(4);// 敏捷
					pc.addCon(4);// 體質
					pc.addWis(4);// 精神
					pc.addInt(4);// 智力
					pc.addCha(4);// 魅力
					pc.addHpr(10);
					pc.addMpr(10);
					break;
				}
				// 更改人物状态
				pc.sendPackets(new S_OwnCharStatus(pc));
				// 更改人物魔法攻击与魔法防御
				pc.sendPackets(new S_SPMR(pc));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 物品使用
	 * 
	 * @param pc
	 * @param item
	 */
	public static void set_card_mode(final L1PcInstance pc, final L1ItemInstance item) {
		if (!START) {
			return;
		}
		try {
			final String classname = item.getItem().getclassname();
			if (!classname.startsWith("shop.VIP_Card_")) {
				return;
			}
			int card_id = 0;
			try {
				final String cardmode = classname.substring(14);
				card_id = Integer.parseInt(cardmode);

			} catch (final Exception e) {
				final String cardmode = classname.substring(15);
				card_id = Integer.parseInt(cardmode);
			}
			if (card_id == 0) {
				return;
			}

			item.setEquipped(true);
			switch (card_id) {
			case 1:// # 44129 普卡 狩獵經驗值+10% 暈眩耐性+3 寒冰耐性+3 石化耐性+3 睡眠耐性+3 死亡後不會掉落經驗
				pc.set_VIP1(true);
				pc.set_expadd(10);
				pc.addRegistStun(5);// 暈眩耐性
				pc.addRegistFreeze(5);// 寒冰耐性
				pc.addRegistStone(5);// 石化耐性
				pc.addRegistSleep(5);// 睡眠耐性
				break;

			case 2:// # 44130 金卡 狩獵經驗值+20% 力量+1 體質+1 敏捷+1 精神+1 智慧+1 魅力+1
					// 死亡後不會掉落陣營經驗
				pc.set_VIP2(true);
				pc.set_expadd(20);
				pc.addStr(1);// 力量
				pc.addDex(1);// 敏捷
				pc.addCon(1);// 體質
				pc.addWis(1);// 精神
				pc.addInt(1);// 智力
				pc.addCha(1);// 魅力
				break;

			case 3:// # 44131 白金卡 狩獵經驗值+30% 力量+2 體質+2 敏捷+2 精神+2 智慧+2 魅力+2
					// 死亡後不會掉落道具
				pc.set_VIP3(true);
				pc.set_expadd(30);
				pc.addStr(2);// 力量
				pc.addDex(2);// 敏捷
				pc.addCon(2);// 體質
				pc.addWis(2);// 精神
				pc.addInt(2);// 智力
				pc.addCha(2);// 魅力
				break;

			case 4:// # 44132 白金限量卡 狩獵經驗值+40% 力量+3 體質+3 敏捷+3 精神+3 智慧+3 魅力+3 回血+5
					// 回魔+5
				pc.set_expadd(40);
				pc.addStr(3);// 力量
				pc.addDex(3);// 敏捷
				pc.addCon(3);// 體質
				pc.addWis(3);// 精神
				pc.addInt(3);// 智力
				pc.addCha(3);// 魅力
				pc.addHpr(5);
				pc.addMpr(5);
				break;

			case 5:// # 44133 獨特限量卡 狩獵經驗值+50% 力量+4 體質+4 敏捷+4 精神+4 智慧+4 魅力+4
					// 回血+10 回魔+10
				pc.set_VIP4(true);
				pc.set_expadd(50);
				pc.addStr(4);// 力量
				pc.addDex(4);// 敏捷
				pc.addCon(4);// 體質
				pc.addWis(4);// 精神
				pc.addInt(4);// 智力
				pc.addCha(4);// 魅力
				pc.addHpr(10);
				pc.addMpr(10);
				break;
			}
			// 更改人物状态
			pc.sendPackets(new S_OwnCharStatus(pc));
			// 更改人物魔法攻击与魔法防御
			pc.sendPackets(new S_SPMR(pc));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 解除使用
	 * 
	 * @param pc
	 * @param item
	 */
	public static void remove_card_mode(final L1PcInstance pc, final L1ItemInstance item) {
		if (!START) {
			return;
		}
		try {
			final String classname = item.getItem().getclassname();
			if (!classname.startsWith("shop.VIP_Card_")) {
				return;
			}
			int card_id = 0;
			try {
				final String cardmode = classname.substring(14);
				card_id = Integer.parseInt(cardmode);
			} catch (final Exception e) {
				final String cardmode = classname.substring(15);
				card_id = Integer.parseInt(cardmode);
			}
			if (card_id == 0) {
				return;
			}
			item.setEquipped(false);
			switch (card_id) {
			case 1:// # 44129 普卡 狩獵經驗值+10% 暈眩耐性+3 寒冰耐性+3 石化耐性+3 睡眠耐性+3 死亡後不會掉落經驗
				pc.set_VIP1(false);
				pc.set_expadd(-10);
				pc.addRegistStun(-5);// 暈眩耐性
				pc.addRegistFreeze(-5);// 寒冰耐性
				pc.addRegistStone(-5);// 石化耐性
				pc.addRegistSleep(-5);// 睡眠耐性
				break;

			case 2:// # 44130 金卡 狩獵經驗值+20% 力量+1 體質+1 敏捷+1 精神+1 智慧+1 魅力+1
					// 死亡後不會掉落陣營經驗
				pc.set_VIP2(false);
				pc.set_expadd(-20);
				pc.addStr(-1);// 力量
				pc.addDex(-1);// 敏捷
				pc.addCon(-1);// 體質
				pc.addWis(-1);// 精神
				pc.addInt(-1);// 智力
				pc.addCha(-1);// 魅力
				break;

			case 3:// # 44131 白金卡 狩獵經驗值+30% 力量+2 體質+2 敏捷+2 精神+2 智慧+2 魅力+2
					// 死亡後不會掉落道具
				pc.set_VIP3(false);
				pc.set_expadd(-30);
				pc.addStr(-2);// 力量
				pc.addDex(-2);// 敏捷
				pc.addCon(-2);// 體質
				pc.addWis(-2);// 精神
				pc.addInt(-2);// 智力
				pc.addCha(-2);// 魅力
				break;

			case 4:// # 44132 白金限量卡 狩獵經驗值+40% 力量+3 體質+3 敏捷+3 精神+3 智慧+3 魅力+3 回血+5
					// 回魔+5
				pc.set_expadd(-40);
				pc.addStr(-3);// 力量
				pc.addDex(-3);// 敏捷
				pc.addCon(-3);// 體質
				pc.addWis(-3);// 精神
				pc.addInt(-3);// 智力
				pc.addCha(-3);// 魅力
				pc.addHpr(-5);
				pc.addMpr(-5);
				break;

			case 5:// # 44133 獨特限量卡 狩獵經驗值+50% 力量+4 體質+4 敏捷+4 精神+4 智慧+4 魅力+4
					// 回血+10 回魔+10
				pc.set_VIP4(false);
				pc.set_expadd(-50);
				pc.addStr(-4);// 力量
				pc.addDex(-4);// 敏捷
				pc.addCon(-4);// 體質
				pc.addWis(-4);// 精神
				pc.addInt(-4);// 智力
				pc.addCha(-4);// 魅力
				pc.addHpr(-10);
				pc.addMpr(-10);
				break;
			}
			// 更改人物状态
			pc.sendPackets(new S_OwnCharStatus(pc));
			// 更改人物魔法攻击与魔法防御
			pc.sendPackets(new S_SPMR(pc));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
