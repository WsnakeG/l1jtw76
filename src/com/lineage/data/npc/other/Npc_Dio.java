package com.lineage.data.npc.other;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ShopBuyList;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;

/**
 * 迪歐<BR>
 * 70027
 * 
 * @author dexc
 */
public class Npc_Dio extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Dio.class);

	/**
	 *
	 */
	private Npc_Dio() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Dio();
	}

	@Override
	public int type() {
		return 19;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		if (pc.getLawful() < 0) {// 邪惡
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dio2"));

		} else {// 一般
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dio1"));
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("sell")) {// 賣
			// PC的可賣出物件列表
			pc.sendPackets(new S_ShopBuyList(npc.getId(), pc));
		}
	}

	@Override
	public int workTime() {
		return 19;
	}

	@Override
	public void work(final L1NpcInstance npc) {
		final Work work = new Work(npc);
		work.getStart();
	}

	private static Random _random = new Random();

	private class Work implements Runnable {

		private final L1NpcInstance _npc;

		private final int _spr;

		private final NpcWorkMove _npcMove;

		private final Point[] _point = new Point[] { new Point(33453, 32805), new Point(33454, 32806),
				new Point(33454, 32804), new Point(33453, 32803) };

		private Work(final L1NpcInstance npc) {
			_npc = npc;
			_spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
			_npcMove = new NpcWorkMove(npc);
		}

		/**
		 * 啟動線程
		 */
		public void getStart() {
			GeneralThreadPool.get().schedule(this, 10);
		}

		@Override
		public void run() {
			try {
				Point point = null;
				final int t = _random.nextInt(_point.length);
				if (!_npc.getLocation().isSamePoint(_point[t])) {
					point = _point[t];
				}

				boolean isWork = true;
				while (isWork) {
					Thread.sleep(_spr);

					if (point != null) {
						isWork = _npcMove.actionStart(point);
					} else {
						isWork = false;
					}
					if (_npc.getLocation().isSamePoint(_point[0])) {
						_npc.setHeading(6);
						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));
						Thread.sleep(_spr);
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 7));

					} else if (_npc.getLocation().isSamePoint(_point[1])) {
						_npc.setHeading(2);
						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));

					} else if (_npc.getLocation().isSamePoint(_point[2])) {
						_npc.setHeading(2);
						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));

					} else if (_npc.getLocation().isSamePoint(_point[3])) {
						_npc.setHeading(0);
						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));
					}
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
