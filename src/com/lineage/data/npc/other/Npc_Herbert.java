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
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;

/**
 * 哈巴特<BR>
 * 70641
 * 
 * @author dexc
 */
public class Npc_Herbert extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Herbert.class);

	/**
	 *
	 */
	private Npc_Herbert() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Herbert();
	}

	@Override
	public int type() {
		return 19;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		if (pc.getLawful() < 0) {// 邪惡
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "herbert2"));

		} else {// 一般
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "herbert1"));
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		// 若要製作一件Ｔ恤需要3張紅色布料、2張藍色布料、10張白色布料，另外還得給我30000枚金幣。
		if (cmd.equalsIgnoreCase("request t-shirt")) {// 製作Ｔ恤
			final int[] items = new int[] { 40456, 40455, 40457, 40308 };
			final int[] counts = new int[] { 3, 2, 10, 30000 };
			final int[] gitems = new int[] { 20085 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

			// 製作抗魔法斗篷需要10張紅色布料、2張藍色布料、1張白色布料。另外還得給我1000枚金幣。
		} else if (cmd.equalsIgnoreCase("request cloak of magic resistance")) {// 製作抗魔法斗篷
			final int[] items = new int[] { 40456, 40455, 40457, 40308 };
			final int[] counts = new int[] { 10, 2, 1, 1000 };
			final int[] gitems = new int[] { 20056 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

			// 製作保護者斗篷需要5張紅色布料、5張藍色布料、10張白色布料，另外還要20000枚金幣。
		} else if (cmd.equalsIgnoreCase("request cloak of protection")) {// 製作保護者斗篷
			final int[] items = new int[] { 40456, 40455, 40457, 40308 };
			final int[] counts = new int[] { 5, 5, 10, 20000 };
			final int[] gitems = new int[] { 20063 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

		} else if (cmd.equalsIgnoreCase("request sixth goods of war")) {// 給予棉花線
			// FIXME
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 交換道具
	 * 
	 * @param pc
	 * @param items
	 * @param counts
	 * @return 是否關閉現有視窗
	 */
	private boolean getItem(final L1PcInstance pc, final int[] items, final int[] counts, final int[] gitems,
			final int[] gcounts) {
		// 需要物件不足
		if (CreateNewItem.checkNewItem(pc, items, // 需要物件
				counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
			return true;

		} else {// 需要物件充足
			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, 1, gcounts);// 給予
			return true;
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

		private final Point[] _point = new Point[] { new Point(33504, 32777), new Point(33501, 32777) };

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
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
