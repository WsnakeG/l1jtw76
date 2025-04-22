package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Party;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Party;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 隊伍更新時間軸(優化完成LOLI 2012-05-30)
 * 
 * @author KZK
 */
public class PartyTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(PartyTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 5000;// 5秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> all = World.get().getAllPlayers();
			// 不包含元素
			if (all.isEmpty()) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				if (check(tgpc)) {
					/*
					 * final L1Party party = tgpc.getParty(); final
					 * ConcurrentHashMap<Integer, L1PcInstance> map =
					 * party.partyUsers(); for (final Iterator<Entry<Integer,
					 * L1PcInstance>> iter2 = map.entrySet().iterator();
					 * iter.hasNext();) { final Entry<Integer, L1PcInstance>
					 * info = iter2.next(); //final int key = info.getKey();
					 * final L1PcInstance tgpc2 = info.getValue();
					 * tgpc2.sendPackets(new S_PacketBoxParty(tgpc2.getParty(),
					 * tgpc)); }
					 */
					tgpc.sendPackets(new S_Party(0x6e, tgpc));
					//tgpc.sendPackets(new S_PacketBoxParty(tgpc.getParty(), tgpc));
					Thread.sleep(1);
				}
			}

		} catch (final Exception e) {
			_log.error("隊伍更新時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final PartyTimer partyTimer = new PartyTimer();
			partyTimer.start();
		}
	}

	/**
	 * PC 執行 判斷
	 * 
	 * @param tgpc
	 * @return true:執行 false:不執行
	 */
	private static boolean check(final L1PcInstance tgpc) {
		try {
			// 人物為空
			if (tgpc == null) {
				return false;
			}
			// 人物登出
			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}
			// 中斷連線
			if (tgpc.getNetConnection() == null) {
				return false;
			}

			final L1Party party = tgpc.getParty();
			if (party == null) {
				return false;
			}
			/*
			 * if (!party.isLeader(tgpc)) { return false; }
			 */

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}
}
