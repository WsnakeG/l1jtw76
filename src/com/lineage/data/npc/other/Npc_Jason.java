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
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;

/**
 * 傑森<BR>
 * 70040
 * 
 * @author dexc
 */
public class Npc_Jason extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Jason.class);

	/**
	 *
	 */
	private Npc_Jason() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Jason();
	}

	@Override
	public int type() {
		return 19;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		if (pc.getLawful() < 0) {// 邪惡
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jason2"));

		} else {// 一般
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jason1"));
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (cmd.equalsIgnoreCase("request timber")) {// 切割原木
			// 4根原木 = 1根木材
			final int[] items = new int[] { 42502 };// 原木
			final int[] counts = new int[] { 4 };
			final int[] gitems = new int[] { 42503 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount == 1) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;

			} else if (xcount > 1) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "a1"));

			} else if (xcount < 1) {
				isCloseList = true;
			}

		} else if (cmd.equals("a1")) {// 切割原木
			final int[] items = new int[] { 42502 };// 原木
			final int[] counts = new int[] { 4 };
			final int[] gitems = new int[] { 42503 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount >= amount) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, amount, gcounts);// 給予
			}
			isCloseList = true;

		} else if (cmd.equalsIgnoreCase("request blank box")) {// 製作木箱
			// 5根木材，及手工費500元
			final int[] items = new int[] { 42502, 40308 };// 原木
			final int[] counts = new int[] { 5, 500 };
			final int[] gitems = new int[] { 42504 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount == 1) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;

			} else if (xcount > 1) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "a2"));

			} else if (xcount < 1) {
				isCloseList = true;
			}

		} else if (cmd.equals("a2")) {// 製作木箱
			// 5根木材，及手工費500元
			final int[] items = new int[] { 42502, 40308 };// 原木
			final int[] counts = new int[] { 5, 500 };
			final int[] gitems = new int[] { 42504 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount >= amount) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, amount, gcounts);// 給予
			}
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	@Override
	public int workTime() {
		return 25;
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

		private final Point[] _point = new Point[] { new Point(33449, 32763), new Point(33449, 32762), // 砍材
				new Point(33450, 32764), // 鋸木
				new Point(33452, 32765) };

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
					if (_npc.getLocation().isSamePoint(_point[1])) {
						_npc.setHeading(6);
						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));
						Thread.sleep(_spr);
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 18));

					} else if (_npc.getLocation().isSamePoint(_point[2])) {
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
