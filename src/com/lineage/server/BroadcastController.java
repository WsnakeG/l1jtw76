/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 廣播系統時間軸
 * 
 * @author terry0412
 */
public final class BroadcastController implements Runnable {

	private static final Log _log = LogFactory.getLog(BroadcastController.class);

	private static BroadcastController _instance;

	public static BroadcastController getInstance() {
		if (_instance == null) {
			_instance = new BroadcastController();
		}
		return _instance;
	}

	// 廣播系統啟動開關 (預設為`關閉`)
	private static boolean _isStop;

	// 廣播訊息最大可存在數量
	private static final int MAX_MSG_SIZE = 20;

	// 廣播訊息暫存佇列
	private static final Queue<String> _msgQueue = new ConcurrentLinkedQueue<String>();

	/**
	 * 取回目前廣播訊息等待數量
	 * 
	 * @return
	 */
	public final int getMsgSize() {
		return _msgQueue.size();
	}

	/**
	 * 將元素放入佇列
	 * 
	 * @param data
	 * @return
	 */
	public final boolean requestWork(final String data) {
		// 超過允許存在數量
		if (_msgQueue.size() >= MAX_MSG_SIZE) {
			return false;
		}
		return _msgQueue.offer(data);
	}

	/**
	 * 是否關閉中
	 * 
	 * @return
	 */
	public final boolean isStop() {
		return _isStop;
	}

	/**
	 * 變更啟動開關 (不做同步化處理) (因為不需要很常變動此開關)
	 */
	public final void setStop(final boolean stop) {
		_isStop = stop;
	}

	@Override
	public void run() {
		try {
			// 關閉中，等候再次啟動
			if (_isStop) {
				return;
			}

			// 廣播訊息佇列 不包含元素
			if (_msgQueue.isEmpty()) {
				return;
			}

			final Collection<L1PcInstance> all = World.get().getAllPlayers();
			// 不包含元素
			if (all.isEmpty()) {
				return;
			}

			// 取得下一筆廣播訊息 (可使用`顏色代碼`)
			final String message = _msgQueue.poll();

			// 建立訊息封包 (顯示於對話欄內)
			final ServerBasePacket packet1 = new S_ServerMessage(message);

			// 建立訊息封包 (顯示於畫面上方)
			final ServerBasePacket packet2 = new S_PacketBoxGree(0x02, "\\f=" + message);

			// 執行全PC檢查程序
			for (final Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext();) {
				final L1PcInstance pc = iter.next();
				if (check(pc)) {
					// 發送封包
					pc.sendPackets(packet1);
					pc.sendPackets(packet2);

				}
				Thread.sleep(1);
			}

		} catch (final Exception e) {
			_log.error("廣播系統時間軸異常重啟");
			GeneralThreadPool.get().cancel(_timer, false);
			_instance = new BroadcastController();
			_instance.start();
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

			// 全體聊天(收聽)
			/*
			 * if (!tgpc.isShowWorldChat()) { return false; }
			 */

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	private ScheduledFuture<?> _timer;

	public final void start() {
		// 啟動執行緒
		final int timeMillis = 3000; // 3秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}
}
