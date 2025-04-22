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
 * 莫麗雅<BR>
 * 70598
 * 
 * @author dexc
 */
public class Npc_Moria extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Moria.class);

	/**
	 *
	 */
	private Npc_Moria() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Moria();
	}

	@Override
	public int type() {
		return 19;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 冶鍊術具有無限之可能性，你想不想嘗試一下未知的力量？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "moria4"));

			} else if (pc.isKnight()) {// 騎士
				// 冶鍊術具有無限之可能性，你想不想嘗試一下未知的力量？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "moria4"));

			} else if (pc.isElf()) {// 精靈
				// 冶鍊術具有無限之可能性，你想不想嘗試一下未知的力量？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "moria4"));

			} else if (pc.isWizard()) {// 法師
				// 冶鍊術具有無限之可能性，你想不想嘗試一下未知的力量？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "moria1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 冶鍊術具有無限之可能性，你想不想嘗試一下未知的力量？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "moria4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 冶鍊術具有無限之可能性，你想不想嘗試一下未知的力量？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "moria4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 冶鍊術具有無限之可能性，你想不想嘗試一下未知的力量？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "moria4"));

			} else {
				// 冶鍊術具有無限之可能性，你想不想嘗試一下未知的力量？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "moria4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isWizard()) {// 法師
			if (cmd.equalsIgnoreCase("request magician dress")) {// 製作魔法師長袍
				final int[] items = new int[] { 40054, 40318, 40457, 40455 };
				final int[] counts = new int[] { 1, 25, 2, 4 };
				final int[] gitems = new int[] { 20111 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = getItem(pc, items, counts, gitems, gcounts);

			} else if (cmd.equalsIgnoreCase("request magician cap")) {// 製作魔法師之帽
				final int[] items = new int[] { 40051, 40318, 40457, 40456, 40455 };
				final int[] counts = new int[] { 2, 20, 1, 1, 1 };
				final int[] gitems = new int[] { 20012 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = getItem(pc, items, counts, gitems, gcounts);
			}
		}
		if (cmd.equalsIgnoreCase("request swap potion")) {// 製作黑色米索莉溶液
			final int[] items = new int[] { 40443, 40397, 40398, 40399, 40400, 40016 };
			final int[] counts = new int[] { 5, 1000, 1000, 1000, 1000, 100 };
			final int[] gitems = new int[] { 49015 };
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

		} else if (cmd.equalsIgnoreCase("a1")) {// 製作黑色米索莉溶液
			final int[] items = new int[] { 40443, 40397, 40398, 40399, 40400, 40016 };
			final int[] counts = new int[] { 5, 1000, 1000, 1000, 1000, 100 };
			final int[] gitems = new int[] { 49015 };
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
		return 18;
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

		private final Point[] _point = new Point[] { new Point(33405, 32836), // 藥劑實驗
				new Point(33406, 32833), new Point(33404, 32834) };

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
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 17));
					}
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
