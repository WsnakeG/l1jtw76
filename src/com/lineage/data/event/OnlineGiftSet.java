package com.lineage.data.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Event;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.world.World;

/**
 * 連線獎勵系統<BR>
 * <font color="#0000FF">參數1:給予時間(單位分鐘)</font><BR>
 * <font color="#0000FF">參數2:以下設置每3組為一個單位#分隔每項設置(狀態#物品編號#給予數量)</font><BR>
 * <font color="#6E8B3D"> 條件一:給予狀態 1代表商店狀態 2代表釣魚狀態 3代表全部狀態<BR>
 * 條件二:物品編號<BR>
 * 條件三:數量<BR>
 * </font> 狀態判斷說明<BR>
 * 吻合1 將不會發放 2跟3<BR>
 * 吻合2 將不會發放 3<BR>
 * 其餘狀態均判斷為3<BR>
 * <BR>
 * 其餘設定類推<BR>
 * <font color="#FF0000"> 警告:<BR>
 * 不能堆疊物品將被排除<BR>
 * 錯誤的設定參數將導致人物無法取得在線獎勵<BR>
 * 相同的狀態編號代表給予該排序中的物品<BR>
 * 例如 2個設置狀態編號都是1<BR>
 * 人物抵達在線時間並且是商店狀態可以取得2種獎勵</font><BR>
 * <BR>
 * #SQL增加代碼: DELETE FROM `server_event` WHERE `id`='11' ; INSERT INTO
 * `server_event` VALUES ('11', '連線獎勵系統', 'OnlineGiftSet', '1', '20, 3#44067#1',
 * '說明:連線獎勵系統');
 * 
 * @author loli
 */
public class OnlineGiftSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(OnlineGiftSet.class);

	public static boolean START = false;// 系統啟動

	// 時間
	private static int _time = 0;

	// 狀態/物品表
	private static final Map<Integer, ArrayList<GetItemData>> _giftList = new HashMap<Integer, ArrayList<GetItemData>>();

	// 線上人物/連線時間(分鐘)
	private static final Map<L1PcInstance, Integer> _getMap = new ConcurrentHashMap<L1PcInstance, Integer>();

	/**
	 *
	 */
	private OnlineGiftSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new OnlineGiftSet();
	}

	/**
	 * 加入在線獎勵清單
	 * 
	 * @param tgpc
	 */
	public static void add(final L1PcInstance tgpc) {
		if (!START) {
			return;
		}
		if (_time == 0) {
			return;
		}
		if (_getMap.get(tgpc) == null) {
			_getMap.put(tgpc, _time);
		}
	}

	/**
	 * 移出在線獎勵清單
	 */
	public static void remove(final L1PcInstance tgpc) {
		if (!START) {
			return;
		}
		if (_time == 0) {
			return;
		}
		// 移出在線獎勵清單
		_getMap.remove(tgpc);
	}

	private class GetItemData {
		public int _getItemId = 40308;// 給予物品編號
		public int _getAmount = 1;// 給予數量
	}

	/**
	 * 給予物品的處理
	 * 
	 * @param tgpc
	 */
	private static void getitem(final L1PcInstance tgpc) {
		try {
			if (check(tgpc)) {
				ArrayList<GetItemData> value = null;
				if (tgpc.isPrivateShop()) {// 商店狀態
					value = _giftList.get(new Integer(1));
				} else if (tgpc.isFishing()) {// 釣魚狀態
					value = _giftList.get(new Integer(2));
				} else {
					value = _giftList.get(new Integer(3));
				}
				if (value == null) {
					value = _giftList.get(new Integer(3));
				}
				if (value == null) {
					return;
				}
				for (final GetItemData iteminfo : value) {
					if (iteminfo == null) {
						continue;
					}
					final L1ItemInstance item = ItemTable.get().createItem(iteminfo._getItemId);
					item.setCount(iteminfo._getAmount);
					if (item != null) {
						// 加入背包成功
						if (tgpc.getInventory().checkAddItem(item, 1) == 0) {
							tgpc.getInventory().storeItem(item);
							// 送出訊息
							tgpc.sendPackets(new S_ServerMessage("獲得連線獎勵: " + item.getLogName()));
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 判斷
	 * 
	 * @param tgpc
	 * @return true:執行 false:不執行
	 */
	public static boolean check(final L1PcInstance tgpc) {
		try {
			if (tgpc == null) {
				return false;
			}

			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}

			if (tgpc.getNetConnection() == null) {
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public void execute(final L1Event event) {
		START = true;// 系統啟動
		final PerformanceTimer timer = new PerformanceTimer();
		boolean isError = false;
		try {
			final String[] set = event.get_eventother().split(",");

			try {
				_time = Integer.parseInt(set[0]);
				if (_time <= 0) {
					_log.error("設定給予獎勵的時間(分鐘)異常 - 將不啟用本項設置");
					isError = true;
					return;
				}

			} catch (final Exception e) {
				_log.error("設定給予獎勵的時間(分鐘)異常 - 將不啟用本項設置");
				isError = true;
				return;
			}

			try {
				for (int i = 1; i < set.length; i++) {
					final String[] setItem = set[i].split("#");
					final GetItemData itemData = new GetItemData();

					int type = Integer.parseInt(setItem[0]);// 取回狀態設置
					switch (type) {
					case 1:
					case 2:
						break;

					default:
						type = 3;
						break;
					}

					itemData._getItemId = Integer.parseInt(setItem[1]);

					final L1Item item = ItemTable.get().getTemplate(itemData._getItemId);
					if (item == null) {
						_log.error("設定給予獎勵物品異常 - 將不啟用本項設置 - 找不到這個編號的物品:" + itemData._getItemId);
						isError = true;
						break;
					}
					if (!item.isStackable()) {
						_log.error("設定給予獎勵物品異常 - 這個編號的物品無發堆疊:" + itemData._getItemId);
						continue;
					}
					itemData._getAmount = Integer.parseInt(setItem[2]);
					if (itemData._getAmount <= 0) {
						_log.error("設定給予獎勵物品異常 - 將不啟用本項設置 - 數量小於等於0:" + itemData._getItemId + " 預設將只給1個");
						itemData._getAmount = 1;
					}

					if (_giftList.get(new Integer(type)) == null) {
						final ArrayList<GetItemData> value = new ArrayList<GetItemData>();
						value.add(itemData);
						_giftList.put(new Integer(type), value);

					} else {
						_giftList.get(new Integer(type)).add(itemData);
					}
				}

			} catch (final Exception e) {
				_log.error("設定給予獎勵物品異常 - 請檢查#內的設置是否吻合3項(狀態#物品編號#給予數量)");
				isError = true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			if (!isError) {
				final GetItemTimer getItemTimer = new GetItemTimer();
				getItemTimer.start();
				_log.info("載入給予獎勵物品: " + _giftList.size() + "(" + timer.get() + "ms)");

			} else {
				_time = 0;
				_giftList.clear();
			}
		}
	}

	private class GetItemTimer extends TimerTask {

		private ScheduledFuture<?> _timer;

		public void start() {
			final int timeMillis = 1000 * 60;// 1分鐘
			_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
		}

		@Override
		public void run() {
			try {
				// 包含元素
				if (!_getMap.isEmpty()) {
					for (final L1PcInstance tgpc : _getMap.keySet()) {
						Thread.sleep(1);
						if (World.get().getPlayer(tgpc.getName()) == null) {
							// 移出在線獎勵清單
							_getMap.remove(tgpc);
							continue;
						}
						Integer time = _getMap.get(tgpc);
						if (time != null) {
							time--;
							if (time <= 0) {
								getitem(tgpc);
								_getMap.put(tgpc, _time);

							} else {
								_getMap.put(tgpc, time);
							}
						}
					}
				}

			} catch (final Exception e) {
				_log.error("在線獎勵清單時間軸異常重啟", e);
				GeneralThreadPool.get().cancel(_timer, false);
				final GetItemTimer getItemTimer = new GetItemTimer();
				getItemTimer.start();
			}
		}
	}
}
