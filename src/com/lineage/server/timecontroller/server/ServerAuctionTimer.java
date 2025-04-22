package com.lineage.server.timecontroller.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.data.event.MarySet;
import com.lineage.data.npc.gam.Npc_Mary;
import com.lineage.server.datatables.lock.AuctionBoardReading;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.datatables.sql.AuctionBoardTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1AuctionBoardTmp;
import com.lineage.server.templates.L1House;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 小屋拍賣公告欄 更新計時器
 * 
 * @author dexc
 */
public class ServerAuctionTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerAuctionTimer.class);

	private ScheduledFuture<?> _timer;

	private static final Queue<Integer> _removeList = new ConcurrentLinkedQueue<Integer>();

	public void start() {
		final int timeMillis = 5 * 60 * 1000;// 5分鐘
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			// 小瑪莉系統資料紀錄
			if (MarySet.START) {
				Npc_Mary.update();
			}

			checkAuctionDeadline();

		} catch (final Exception e) {
			_log.error("小屋拍賣公告欄時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final ServerAuctionTimer restart = new ServerAuctionTimer();
			restart.start();

		} finally {
			ListMapUtil.clear(_removeList);
		}
	}

	/**
	 * 取回現在時間
	 * 
	 * @return
	 */
	private static Calendar getRealTime() {
		final TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		final Calendar cal = Calendar.getInstance(tz);
		return cal;
	}

	private static void checkAuctionDeadline() {
		try {
			final Collection<L1AuctionBoardTmp> boardList = AuctionBoardReading.get()
					.getAuctionBoardTableList().values();
			// int i = 0;
			for (final L1AuctionBoardTmp board : boardList) {
				// 判断此 Calendar 表示的时间是否在指定 Object 表示的时间之前
				if (board.getDeadline().before(getRealTime())) {
					endAuction(board);
					// i++;
				}
			}

			// 移出清單包含元素
			if (!_removeList.isEmpty()) {
				remove();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static void endAuction(final L1AuctionBoardTmp board) {
		try {
			final int houseId = board.getHouseId();// 小屋編號
			final long price = board.getPrice();// 售價
			final int oldOwnerId = board.getOldOwnerId();// 原屋主OBJID
			final String bidder = board.getBidder();// 新屋主名稱
			final int bidderId = board.getBidderId();// 新屋主OBJID

			if ((oldOwnerId != 0) && (bidderId != 0)) { // 具有屋主
				final L1PcInstance oldOwnerPc = (L1PcInstance) World.get().findObject(oldOwnerId);
				final long payPrice = (int) (price * 0.9);

				// 原屋主在線
				if (oldOwnerPc != null) { // 具有屋主
					oldOwnerPc.getInventory().storeItem(L1ItemId.ADENA, payPrice);
					// 以 %1金幣賣出您所擁有的房子。因此給您扣掉%n手續費 10%%的金額金幣 %0。%n謝謝。%n%n
					oldOwnerPc.sendPackets(new S_ServerMessage(527, String.valueOf(payPrice)));

					// 原屋主離線
				} else { // 屋主取回售出金幣
					try {
						CharItemsReading.get().getAdenaCount(oldOwnerId, price);

					} catch (final Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}
				}

				final L1PcInstance bidderPc = (L1PcInstance) World.get().findObject(bidderId);
				if (bidderPc != null) { // 得標者在線
					// 恭喜。%n你在拍賣會上以 %0金幣成交。%n現在去您的血盟小屋後，可利用多樣的設備。%n謝謝。%n%n
					bidderPc.sendPackets(new S_ServerMessage(524, String.valueOf(price), bidder));
				}

				deleteHouseInfo(houseId);
				setHouseInfo(houseId, bidderId);

				_removeList.add(houseId);
				// this.deleteNote(houseId);

			} else if ((oldOwnerId == 0) && (bidderId != 0)) { // 沒有屋主
				final L1PcInstance bidderPc = (L1PcInstance) World.get().findObject(bidderId);
				if (bidderPc != null) { // 得標者在線
					// 恭喜。%n你在拍賣會上以 %0金幣成交。%n現在去您的血盟小屋後，可利用多樣的設備。%n謝謝。%n%n
					bidderPc.sendPackets(new S_ServerMessage(524, String.valueOf(price), bidder));
				}
				setHouseInfo(houseId, bidderId);

				_removeList.add(houseId);
				// this.deleteNote(houseId);

			} else if ((oldOwnerId != 0) && (bidderId == 0)) { // 具有屋主
				final L1PcInstance oldOwnerPc = (L1PcInstance) World.get().findObject(oldOwnerId);
				if (oldOwnerPc != null) { // 屋主在線
					// 在拍賣期間並沒有出現提出適當價格的人，所以拍賣取消。%n因此所有權還在您那裡。%n謝謝。%n%n
					oldOwnerPc.sendPackets(new S_ServerMessage(528));
				}

				_removeList.add(houseId);
				// this.deleteNote(houseId);

			} else if ((oldOwnerId == 0) && (bidderId == 0)) { // 沒有屋主
				// 拍賣延後5日
				final Calendar cal = getRealTime();
				cal.add(Calendar.DATE, 5); // 5日後
				cal.set(Calendar.MINUTE, 0); // 分、秒は切り捨て
				cal.set(Calendar.SECOND, 0);
				board.setDeadline(cal);
				final AuctionBoardTable boardTable = new AuctionBoardTable();
				boardTable.updateAuctionBoard(board);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 以前の所有者のアジトを消す
	 * 
	 * @param houseId
	 * @return
	 */
	private static void deleteHouseInfo(final int houseId) {
		try {
			final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
			for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
				final L1Clan clan = iter.next();
				if (clan.getHouseId() == houseId) {
					clan.setHouseId(0);
					ClanReading.get().updateClan(clan);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 設置得標血盟 小屋編號
	 * 
	 * @param houseId bidderId
	 * @return
	 */
	private static void setHouseInfo(final int houseId, final int bidderId) {
		try {
			final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
			for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
				final L1Clan clan = iter.next();
				if (clan.getLeaderId() == bidderId) {
					clan.setHouseId(houseId);
					ClanReading.get().updateClan(clan);
					break;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移除對象
	 */
	private static void remove() {
		try {
			for (final Iterator<Integer> iter = _removeList.iterator(); iter.hasNext();) {
				final Integer houseId = iter.next();// 返回迭代的下一个元素。
				// 从迭代器指向的 collection 中移除迭代器返回的最后一个元素
				iter.remove();

				// 檢查對象
				deleteNote(houseId);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 結束拍賣 公告移除
	 * 
	 * @param houseId
	 * @return
	 */
	private static void deleteNote(final int houseId) {
		try {
			// 結束拍賣
			final L1House house = HouseReading.get().getHouseTable(houseId);
			house.setOnSale(false);

			final Calendar cal = getRealTime();
			// System.out.println(cal.getTime());
			cal.add(Calendar.DATE, ConfigAlt.HOUSE_TAX_INTERVAL);
			cal.set(Calendar.MINUTE, 0); // 分、秒は切り捨て
			cal.set(Calendar.SECOND, 0);

			house.setTaxDeadline(cal);

			HouseReading.get().updateHouse(house);

			AuctionBoardReading.get().deleteAuctionBoard(houseId);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
