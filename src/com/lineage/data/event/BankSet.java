package com.lineage.data.event;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.lock.AccountBankReading;
import com.lineage.server.templates.L1Bank;
import com.lineage.server.templates.L1Event;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.RangeLong;

/**
 * 銀行管理員系統<BR>
 * #新增銀行管理系統 DELETE FROM `server_event` WHERE `id`='52'; INSERT INTO
 * `server_event` VALUES ('52', '銀行管理員系統', 'BankSet', '1',
 * '5000000000,6000000000,40308,5,0.05', '說明:銀行儲蓄上限,銀行存款上限(加入利息),發放利息時間
 * (單位:分),銀行利率(%)'); #新增銀行管理員 UPDATE `npc` SET
 * `name`='銀行管理員',`nameid`='銀行管理員',`classname`='event.Npc_Bank',`note`='' WHERE
 * `npcid`='50002';# #新增銀行管理員召換位置 DELETE FROM `server_event_spawn` WHERE
 * `id`='40307'; DELETE FROM `server_event_spawn` WHERE `id`='40308'; INSERT
 * INTO `server_event_spawn` VALUES ('40307', '52', '銀行管理員', '1', '50002', '0',
 * '33449', '32792', '0', '0', '5', '0', '4', '0', '0'); INSERT INTO
 * `server_event_spawn` VALUES ('40308', '52', '銀行管理員', '1', '50002', '0',
 * '33074', '33396', '0', '0', '5', '0', '4', '0', '0');
 * 
 * @author dexc
 */
public class BankSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(BankSet.class);

	// 銀行儲蓄上限
	public static long BANKMAX;

	// 銀行存款達到多少之後不會給利息
	public static long BANK_INTEREST_OVER;

	// 銀行存款達的到具編號
	public static int BANK_ITEMID;

	// 銀行計算利息時間 (單位:分)
	public static int BANK_TIME = 1;

	// 銀行利率 最低值0.1 利息公式 = 目前存款 * BankInterest
	public static double BANK_INTEREST = 0.01;

	/**
	 *
	 */
	private BankSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new BankSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final String[] set = event.get_eventother().split(",");

			try {
				BANKMAX = Long.parseLong(set[0]);

			} catch (final Exception e) {
				BANKMAX = 1900000000;
				_log.error("未設定銀行儲蓄上限(使用預設19億)");
			}

			try {
				BANK_INTEREST_OVER = Long.parseLong(set[1]);

			} catch (final Exception e) {
				BANK_INTEREST_OVER = 2000000000;
				_log.error("未設定利息及現存款總額(使用預設20億)");
			}

			try {
				BANK_ITEMID = Integer.parseInt(set[2]);

			} catch (final Exception e) {
				BANK_ITEMID = 40308;
				_log.error("未設定存款物品編號(使用預設40308)");
			}

			try {
				BANK_TIME = Integer.parseInt(set[3]);

			} catch (final Exception e) {
				BANK_TIME = 60;
				_log.error("未設銀行計算利息時間(使用預設60分鐘)");
			}

			try {
				BANK_INTEREST = Double.parseDouble(set[4]);

			} catch (final Exception e) {
				BANK_INTEREST = 0.01D;
				_log.error("未設銀行利息比率(使用預設0.01%)");
			}
			_log.info("銀行設置\n 帳戶儲蓄上限: " + RangeLong.scount(BANKMAX) + " \n 存款總額限制: "
					+ RangeLong.scount(BANK_INTEREST_OVER) + "\n 存款物品編號: " + RangeLong.scount(BANK_ITEMID)
					+ "\n 利息計算時間: " + BANK_TIME + "\n 利息比率: " + BANK_INTEREST);

			// 載入資料
			AccountBankReading.get().load();

			// 啟動時間軸
			final BankTimer bankTime = new BankTimer();
			bankTime.start();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private class BankTimer extends TimerTask {

		private ScheduledFuture<?> _timer;

		public void start() {
			final int timeMillis = BANK_TIME * 60 * 1000;// 分鐘
			_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
		}

		@Override
		public void run() {
			try {
				final Map<String, L1Bank> map = AccountBankReading.get().map();
				if (map.isEmpty()) {
					return;
				}

				for (final String key : map.keySet()) {
					final L1Bank bank = map.get(key);
					if (bank.isLan()) {
						if (!bank.isEmpty()) {
							final long nwecount = bank.get_adena_count()
									+ Math.round(bank.get_adena_count() * BANK_INTEREST);

							bank.set_adena_count(nwecount);
							if (nwecount >= BANK_INTEREST_OVER) {
								return;
							}
							AccountBankReading.get().updateAdena(bank.get_account_name(), nwecount);
							/*
							 * System.out.println("名稱" + bank.get_account_name()
							 * + "新金額" + nwecount);
							 */
							Thread.sleep(5);
						}
					}
				}

			} catch (final Exception e) {
				_log.error("銀行管理員時間軸異常重啟", e);
				GeneralThreadPool.get().cancel(_timer, false);
				final BankTimer bankTime = new BankTimer();
				bankTime.start();

			} finally {

			}
		}
	}
}