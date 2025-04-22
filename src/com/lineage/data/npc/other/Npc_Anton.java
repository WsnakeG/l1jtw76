package com.lineage.data.npc.other;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;

/**
 * 安東<BR>
 * 70614
 * 
 * @author dexc
 */
public class Npc_Anton extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Anton.class);

	/**
	 *
	 */
	private Npc_Anton() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Anton();
	}

	@Override
	public int type() {
		return 19;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		// 請你不要妨礙我，我正在全力以赴的完成我一生中最偉大的作品。
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "anton1"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (cmd.equalsIgnoreCase("request ancient plate mail2")) {// 製作黑暗執行者金屬盔甲
			// 可製作數量
			final long count = CreateNewItem.checkNewItem(pc, new int[] { 20095, 49015 }, // 古老的金屬盔甲
																							// x
																							// 1
																							// 黑色米索莉溶液
																							// x
																							// 1
					new int[] { 1, 1 });

			// 需要物件不足
			if (count < 1) {// 傳回可交換道具數小於1(需要物件不足)
				isCloseList = true;

			} else {// 需要物件充足
				// 收回任務需要物件 給予任務完成物件
				CreateNewItem.createNewItem(pc, new int[] { 20095, 49015 }, new int[] { 1, 1 }, // 古老的金屬盔甲
																								// x
																								// 1
																								// 黑色米索莉溶液
																								// x
																								// 1
						new int[] { 20133 }, 1, new int[] { 1 });// 黑暗執行者金屬盔甲 x
																	// 1
				isCloseList = true;
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	@Override
	public int workTime() {
		return 20;
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

		private final Point[] _point = new Point[] { new Point(33451, 32741), new Point(33449, 32743),
				new Point(33448, 32742) // 雕刻
		};

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
					if (_npc.getLocation().isSamePoint(_point[2])) {
						_npc.setHeading(6);
						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));
						Thread.sleep(_spr);
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 17));
					}
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
