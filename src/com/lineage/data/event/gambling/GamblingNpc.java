package com.lineage.data.event.gambling;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * 參賽者移動控制
 * 
 * @author dexc
 */
public class GamblingNpc implements Runnable {

	private static final Log _log = LogFactory.getLog(GamblingNpc.class);

	// 比賽控制數據
	private final Gambling _gambling;

	// 參賽者
	private L1NpcInstance _npc;

	// 比賽結速
	private boolean _isOver = false;

	private Point[] _tgLoc;

	// 跑道
	private int _xId;

	// 標點進度
	private int _sId = 1;

	// 移動格數
	// private int _move = 0;

	// 押注累積金額
	private long _adena;

	// 賠率
	private double _rate;

	private Point _loc;

	private final Random _random = new Random();

	private int _randomTime;

	public GamblingNpc(final Gambling gambling) {
		_gambling = gambling;
	}

	/**
	 * 傳回比賽控制數據
	 */
	public Gambling get_gambling() {
		return _gambling;
	}

	/**
	 * 設置該NPC賠率
	 */
	public void set_rate(final double rate) {
		_rate = rate;
	}

	/**
	 * 傳回該NPC賠率
	 * 
	 * @return
	 */
	public double get_rate() {
		return _rate;
	}

	/**
	 * 設置該NPC累積押注金額
	 * 
	 * @param npc
	 */
	public void add_adena(final long adena) {
		_adena += adena;
	}

	/**
	 * 傳回該NPC押注累積金額
	 * 
	 * @param npc
	 */
	public long get_adena() {
		return _adena;
	}

	/**
	 * 刪除NPC
	 * 
	 * @param npc
	 */
	public void delNpc() {
		_npc.deleteMe();
	}

	/**
	 * 傳回NPC
	 * 
	 * @param npc
	 * @return
	 */
	public L1NpcInstance get_npc() {
		return _npc;
	}

	/**
	 * 傳回NPC跑道
	 * 
	 * @param npc
	 * @return
	 */
	public int get_xId() {
		return _xId;
	}

	/**
	 * 產生NPC
	 * 
	 * @param npcid
	 * @param i
	 */
	public void showNpc(final int npcid, final int i) {
		_xId = i;

		final L1Npc npc = NpcTable.get().getTemplate(npcid);
		if (npc != null) {
			_tgLoc = GamblingConfig.TGLOC[i];
			final int x = _tgLoc[0].getX();
			final int y = _tgLoc[0].getY();
			final short mapid = 4;
			final int heading = 6;

			int[] gfxids;
			if (GamblingConfig.ISGFX) {// 賽狗外型
				gfxids = GamblingConfig.GFX[i];

			} else {// 食人妖寶寶外型
				gfxids = GamblingConfig.GFXD[i];
			}

			final int gfxid = gfxids[_random.nextInt(gfxids.length)];

			_npc = L1SpawnUtil.spawn(npcid, x, y, mapid, heading, gfxid);
			// System.out.println("比賽NPC編號: "+npcid + " 排序:" + i);
		}
	}

	/**
	 * 啟動
	 */
	public void getStart() {
		GeneralThreadPool.get().schedule(this, 10);
	}

	@Override
	public void run() {
		try {
			_loc = _tgLoc[1];
			while (!_isOver) {
				if (_xId == _gambling.WIN) {
					_randomTime = 150;
				} else {
					_randomTime = 190;
				}

				int ss = 190;
				if (_random.nextInt(100) < 25) {
					ss = 150;
				}
				final int speed = ss + _random.nextInt(_randomTime);

				Thread.sleep(speed); // 休息時間
				actionStart();
			}

		} catch (final InterruptedException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * 往指定面向移動1格(無障礙設置)
	 */
	private void setDirectionMove(final int heading) {
		// System.out.println("往指定面向移動1格: "+_npc.getNpcId() + " 面向:" + dir);
		if (heading >= 0) {
			int locx = _npc.getX();
			int locy = _npc.getY();
			locx += HEADING_TABLE_X[heading];
			locy += HEADING_TABLE_Y[heading];
			_npc.setHeading(heading);

			_npc.setX(locx);
			_npc.setY(locy);
			_npc.broadcastPacketAll(new S_MoveCharPacket(_npc));
			// this._move++;
		}
	}

	private static final int[][] xx = new int[5][1];

	private void actionStart() {
		final int x = _loc.getX();
		final int y = _loc.getY();
		try {
			// 取回行進方向
			final int dir = _npc.targetDirection(x, y);
			setDirectionMove(dir);
			if (_npc.getLocation().getTileLineDistance(_loc) < 2) {
				_loc = _tgLoc[_sId];
				_sId++;
			}

		} catch (final Exception e) {
			if (_gambling.get_oneNpc() == null) {
				_gambling.set_oneNpc(this);

				xx[_xId][0] += 1;
				// System.out.println("獲勝跑道: "+_xId + "(" + xx[_xId][0] + ")" +
				// GamblingTimeController.get_gamblingNo()+ "場");
			}

			// 取回行進方向
			final int dir = _npc.targetDirection(x, y);
			setDirectionMove(dir);
			// System.out.println("移動停止: "+_npc.getNpcId());
			_isOver = true;
		}
	}
}
