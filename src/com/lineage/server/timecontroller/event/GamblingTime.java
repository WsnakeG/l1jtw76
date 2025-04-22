package com.lineage.server.timecontroller.event;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.GamblingSet;
import com.lineage.data.event.gambling.Gambling;
import com.lineage.data.event.gambling.GamblingNpc;
import com.lineage.server.datatables.lock.GamblingReading;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1GamInstance;
import com.lineage.server.model.Instance.L1GamblingInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NpcChatShouting;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Gambling;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerRestartTimer;
import com.lineage.server.world.World;

/**
 * 奇岩賭場<BR>
 * 70035賽西<BR>
 * 70041波金<BR>
 * 70042波麗
 * 
 * @author dexc
 */
public class GamblingTime extends TimerTask {

	private static final Log _log = LogFactory.getLog(GamblingTime.class);

	private static Gambling _gambling;

	private static boolean _issystem = true;// 是否可以進行比賽

	private static boolean _isStart = false;// 比賽開始

	private static int _gamblingNo = 100;// 場次編號

	// private static Random _random = new Random();

	// private static final Map<Integer, Integer> _xmap = new
	// ConcurrentHashMap<Integer, Integer>();

	private ScheduledFuture<?> _timer;

	public void start() {
		_gamblingNo = GamblingReading.get().maxId();
		final int timeMillis = GamblingSet.GAMADENATIME * 60 * 1000;// 間隔時間
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	/**
	 * 傳回場次編號
	 * 
	 * @return
	 */
	public static int get_gamblingNo() {
		return _gamblingNo;
	}

	/**
	 * 傳回比賽模組
	 * 
	 * @return
	 */
	public static Gambling get_gambling() {
		return _gambling;
	}

	/**
	 * 傳回是否已開始比賽
	 * 
	 * @return
	 */
	public static boolean isStart() {
		return _isStart;
	}

	/**
	 * 傳回是否可以進行比賽
	 */
	public static void set_status(final boolean b) {
		_issystem = b;
	}

	@Override
	public void run() {
		try {
			if (!_issystem) {
				return;
			}
			if (_gambling != null) {
				return;
			}
			// 重新啟動時間不足30分鐘
			if (ServerRestartTimer.isRtartTime()) {
				return;
			}

			doorOpen(false);

			final long previous = 0;
			/*
			 * final L1Gambling previouss =
			 * GamblingReading.get().getGambling(_gamblingNo - 1); if (previouss
			 * != null) { if (GamblingSet.NOWODDS > 0D) { // 上次賭金總數的15% previous
			 * = (long) Math.max((previouss.get_adena() * GamblingSet.NOWODDS),
			 * 0); } } else { previous = 1000000;// 無上次獎金資料 預設 100萬 } if
			 * (previous <= 0) { previous = 1000000;// 上次獎金資料為0 預設 100萬 }
			 */

			// 產生訊息封包 (3033 奇岩競技場比賽即將開始，本場基本將金達: %0 元。)
			for (final Iterator<L1PcInstance> iter = World.get().getAllPlayers().iterator(); iter
					.hasNext();) {
				final L1PcInstance listner = iter.next();
				// 拒絕接收廣播頻道
				if (!listner.isShowWorldChat()) {
					continue;
				}
				// 奇岩競技場比賽即將開始，本場基本將金達: %0 元。
				listner.sendPackets(new S_ServerMessage("奇岩競技場比賽即將開始"));
			}
			/*
			 * for (final L1PcInstance listner : World.get().getAllPlayers()) {
			 * // 拒絕接收廣播頻道 if (!listner.isShowWorldChat()) { continue; } //
			 * 奇岩競技場比賽即將開始，本場基本將金達: %0 元。 listner.sendPackets(new
			 * S_ServerMessage("奇岩競技場比賽即將開始")); }
			 */

			// 召喚比賽者
			_gambling = new Gambling();
			_gambling.set_gmaNpc(previous);

			// 廣播比賽
			boolean is5m = true;// 5分鐘計時

			int timeS = 300;
			while (is5m) {
				Thread.sleep(1000);// 1秒
				// System.out.println(timeM + "分鐘");
				switch (timeS) {
				case 300:// 時間5分鐘
				case 240:// 時間4分鐘
				case 180:// 時間3分鐘
				case 120:// 時間2分鐘
				case 60:// 時間1分鐘
					npcChat((timeS / 60), 0, null);
					break;

				case 10:// 時間10秒
				case 9:// 時間9秒
				case 8:// 時間8秒
				case 7:// 時間7秒
				case 6:// 時間6秒
				case 5:// 時間5秒
				case 4:// 時間4秒
				case 3:// 時間3秒
				case 2:// 時間2秒
					npcChat(timeS, 1, null);
					break;

				case 1:// 時間1秒
					_isStart = true;
					npcChat(0, 2, null);
					break;

				case 0:// 時間0秒
					doorOpen(true);

					npcChat(0, 3, null);
					_gambling.startGam();
					;// 啟動比賽
					is5m = false;
					break;
				}
				timeS--;
			}

			// 計算賠率
			_gambling.set_allRate();

			Thread.sleep(2000);

			for (final GamblingNpc gamblingNpc : _gambling.get_allNpc().values()) {
				// 宣告賠率
				npcChat(0, 4, gamblingNpc);
				Thread.sleep(1000);// 100
			}

			while (_gambling.get_oneNpc() == null) {
				Thread.sleep(100);// 100
			}

			if (_gambling.get_oneNpc() != null) {
				final GamblingNpc one = _gambling.get_oneNpc();
				// 宣告優勝
				npcChat(0, 5, one);

				// $375 第 $366 場比賽的優勝者是
				final String onename = one.get_npc().getNameId();
				for (final L1PcInstance listner : World.get().getAllPlayers()) {
					// 拒絕接收廣播頻道
					if (!listner.isShowWorldChat()) {
						continue;
					}
					listner.sendPackets(new S_ServerMessage(166, "$375 " + _gamblingNo + " $366 " + onename
							+ " " + GamblingSet.GAMADENATIME + ":" + (one.get_adena() * 2)));
				}

				_log.info("奇岩賭場:" + _gamblingNo + " 優勝者:" + one.get_npc().getName() + "/" + one.get_xId()
						+ "-" + _gambling.WIN + " (" + one.get_adena() + ")");
				/*
				 * int value = 1; if (_xmap.get(one.get_xId()) != null) { value
				 * += _xmap.get(one.get_xId()); } _xmap.put(one.get_xId(),
				 * value); for (Integer key : _xmap.keySet()) { _log.info(key +
				 * " (" + _xmap.get(key) + ")"); }
				 */

				// 取回勝者紀錄
				final int npcid = _gambling.get_oneNpc().get_npc().getNpcId();
				// 獲勝NPC賠率
				final double rate = 2.00;// _gambling.get_oneNpc().get_rate();
				// 本場總下注金額
				final long adena = _gambling.get_allAdena();
				// 獲勝下注數量
				final int outcount = (int) (_gambling.get_oneNpc().get_adena() / GamblingSet.GAMADENA);

				final L1Gambling gambling = new L1Gambling();
				gambling.set_id(_gamblingNo);
				gambling.set_adena(adena);
				gambling.set_rate(rate);
				gambling.set_gamblingno(_gamblingNo + "-" + npcid);
				gambling.set_outcount(outcount);

				GamblingReading.get().add(gambling);
			}
			synchronized (this) {
				_gamblingNo++;
			}
			Thread.sleep(20000);// 100

		} catch (final Exception e) {
			_log.error("奇岩賭場時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final GamblingTime timerTask = new GamblingTime();
			timerTask.start();

		} finally {
			if (_gambling == null) {
				return;
			}

			// 刪除參賽者
			_gambling.delAllNpc();
			_gambling.clear();

			_gambling = null;

			if (!_isStart) {
				return;
			}
			// 給予奇岩城稅收(給予大於0D)
			/*
			 * if (GamblingSet.GET_GIRAN_CASTLE > 0D) { // 上次賭金總數的2% long
			 * previous = (long) Math.max((gambling.get_adena() *
			 * GamblingSet.GET_GIRAN_CASTLE), 0); getCastle(previous); }
			 */
			_isStart = false;
		}
	}

	/**
	 * 給予奇岩城稅金2%
	 * 
	 * @param previous
	 */
	/*
	 * private void getCastle(long previous) { final L1Castle l1castle =
	 * CastleReading.get().getCastleTable(L1CastleLocation.GIRAN_CASTLE_ID);
	 * synchronized (l1castle) { long money = l1castle.getPublicMoney(); money
	 * += previous; l1castle.setPublicMoney(money);
	 * CastleReading.get().updateCastle(l1castle); } }
	 */

	/**
	 * 打開/關閉 賭場門
	 * 
	 * @param isOpen false:關閉 true:打開
	 */
	private void doorOpen(final boolean isOpen) {
		L1DoorInstance.openGam(isOpen);
	}

	// private static final String[] _msg = new String[] {
	// "剛剛喝到的是綠水嗎?等等跑就知道~^^~", "隔壁跑道聽說昨天踩到釘子.....",
	// "買我啦!!看我的臉就知道我贏!!", "快點跑完我也想去打一下副本~~", "你在看我嗎??你可以在靠近一點...",
	// "飛龍都不一定跑贏我!看我強壯的雞腿!", "那個誰誰誰!!等等不要跑超過我黑...",
	// "有沒騎士在場阿?給瓶勇水喝喝~~", "地球是很危險的...", "誰給我來一下祝福!加持!加持~",
	// "咦~~有一個參賽者是傳說中的跑道之王...", "沒事!沒事!!哥只是個傳說~~",
	// "隔壁的~你剛剛喝什麼?你是不是作弊??", "肚子好餓...沒吃飯能贏嗎??", "哇靠~~今天感覺精力充沛耶!!",
	// "隔壁的!!你控制一下不要一直放屁!!",
	// "嗯......嗯......其他幾個是憋三,我會贏....", "我剛剛好像喝多了...頭還在暈...", "昨晚的妞真正丫，喝綠水算三小。",
	// "肚子餓死了，跑不動了。", "輸贏都行啦，娛樂而已。",
	// "小賭怡情，大賭傷身。", "我要放點水。經常贏都有點不好意思了。", "【強化勇氣的藥水】是幹嘛的？我這有一罐。",
	// "昨晚被吵死了，現在都覺得好累。", "阿幹....不要看我啦!!會影響我心情!!",
	// "說什麼呢~~你們不想我贏阿!!!", "小賭可以養家活口!!大賭可以興邦建國!!", "賭是不好的....不賭是萬萬不行的....", };

	/**
	 * NPC對話內容
	 * 
	 * @param i
	 * @param mode
	 * @param gamblingNpc
	 */
	private void npcChat(final int i, final int mode, final GamblingNpc gamblingNpc) {
		final Collection<L1Object> allObj = World.get().getObject();
		for (final L1Object obj : allObj) {
			if (!(obj instanceof L1GamInstance)) {
				continue;
			}
			// final L1GamInstance npc = (L1GamInstance) obj;
			// final int npcId = npc.getNpcId();
			switch (mode) {
			case 0:
				/*
				 * if (_random.nextInt(100) < 20) { // NPC對話 String msg =
				 * _msg[_random.nextInt(_msg.length)];
				 * npc.broadcastPacketX10(new S_NpcChat(npc, msg)); for (final
				 * L1PcInstance listner : World.get().getAllPlayers()) { //
				 * 拒絕接收廣播頻道 if (!listner.isShowWorldChat()) { continue; }
				 * listner.sendPackets(new S_KillMessage(npc.getNameId(), msg,
				 * 0)); } }
				 */
				break;
			}
		}

		for (final L1Object obj : allObj) {
			if (!(obj instanceof L1GamblingInstance)) {
				continue;
			}
			final L1GamblingInstance npc = (L1GamblingInstance) obj;
			final int npcId = npc.getNpcId();

			switch (mode) {
			case 0:
				// $376 剩餘時間： $377分鐘！
				npc.broadcastPacketX10(new S_NpcChatShouting(npc, "$376 " + i + "$377"));
				break;

			case 1:
				if (npcId != 91172) {
					// 秒數倒數
					npc.broadcastPacketX10(new S_NpcChatShouting(npc, String.valueOf(i)));
				}
				break;

			case 2:
				if (npcId != 91172) {
					// $363 準備
					npc.broadcastPacketX10(new S_NpcChatShouting(npc, "$363"));
				}
				break;

			case 3:
				if (npcId != 91172) {
					// $364 GO！
					npc.broadcastPacketX10(new S_NpcChatShouting(npc, "$364"));
				}
				break;

			case 4:
				if (npcId != 91172) {
					// $402 的賠率為
					final String npcname = gamblingNpc.get_npc().getNameId();
					/*
					 * final Double rate = gamblingNpc.get_rate(); String rates
					 * = String.valueOf(rate); final int index =
					 * rates.indexOf("."); if (index != -1) { final int length =
					 * rates.length(); if (length >= (index + 3)) { rates =
					 * rates.substring(0, index + 3); } else { rates =
					 * rates.substring(0, index + 2); rates += "0"; } } else {
					 * rates += ".00"; }
					 */
					npc.broadcastPacketX10(new S_NpcChatShouting(npc,
							npcname + " $402 2.0"/*
													 * + rates
													 */));
				}
				break;

			case 5:
				// $375 第 $366 場比賽的優勝者是
				final String onename = gamblingNpc.get_npc().getNameId();
				npc.broadcastPacketX10(
						new S_NpcChatShouting(npc, "$375 " + _gamblingNo + " $366 " + onename));
				break;
			}
		}
	}
}
