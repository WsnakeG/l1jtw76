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
 * 海克特<BR>
 * 70642
 * 
 * @author dexc
 */
public class Npc_Hector extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Hector.class);

	/**
	 *
	 */
	private Npc_Hector() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Hector();
	}

	@Override
	public int type() {
		return 19;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		if (pc.getLawful() < 0) {// 邪惡
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hector2"));

		} else {// 一般
			// 我可以將你的防具加強讓它更加堅固。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hector1"));
			// 媽的！我也很難養活我自己．．到底叫我怎辦啊？
			// pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hector10"));
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (cmd.equalsIgnoreCase("request fifth goods of war")) {// 給予材料
			// FIXME

			// <p>要製造鋼鐵手套，需要手套1雙和金屬塊150塊。加工費是25000枚金幣。
		} else if (cmd.equalsIgnoreCase("request iron gloves")) {// 製造鋼鐵手套
			final int[] items = new int[] { 20182, 40408, 40308 };
			final int[] counts = new int[] { 1, 150, 25000 };
			final int[] gitems = new int[] { 20163 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

			// <p>要製造鋼鐵頭盔，需要騎士面甲1頂和金屬塊120塊。加工費是16500枚金幣。
		} else if (cmd.equalsIgnoreCase("request iron visor")) {// 製造鋼鐵頭盔
			final int[] items = new int[] { 20006, 40408, 40308 };
			final int[] counts = new int[] { 1, 120, 16500 };
			final int[] gitems = new int[] { 20003 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

			// <p>要製造鋼鐵盾牌，需要塔盾1面和金屬塊200塊。加工費是16000枚金幣。
		} else if (cmd.equalsIgnoreCase("request iron shield")) {// 製造鋼鐵盾牌
			final int[] items = new int[] { 20231, 40408, 40308 };
			final int[] counts = new int[] { 1, 200, 16000 };
			final int[] gitems = new int[] { 20220 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

			// <p>要製造鋼鐵長靴，需要長靴1雙與金屬塊160塊。加工費是8000枚金幣。
		} else if (cmd.equalsIgnoreCase("request iron boots")) {// 製造鋼鐵長靴
			final int[] items = new int[] { 20205, 40408, 40308 };
			final int[] counts = new int[] { 1, 160, 8000 };
			final int[] gitems = new int[] { 20194 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

			// <p>若要製造鋼鐵金屬盔甲，需要金屬盔甲1件和金屬塊450個。而且需要付30000枚金幣的加工費。
		} else if (cmd.equalsIgnoreCase("request iron plate mail")) {// 製造鋼鐵金屬盔甲
			final int[] items = new int[] { 20154, 40408, 40308 };
			final int[] counts = new int[] { 1, 450, 30000 };
			final int[] gitems = new int[] { 20091 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

			// 製作薄金屬板需要金屬塊 10 個與 500 金幣
		} else if (cmd.equalsIgnoreCase("request slim plate")) {// 製作薄金屬板
			final int[] items = new int[] { 40408, 40308 };
			final int[] counts = new int[] { 10, 500 };
			final int[] gitems = new int[] { 40526 };
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

		} else if (cmd.equalsIgnoreCase("a1")) {// 製作薄金屬板
			final int[] items = new int[] { 40408, 40308 };
			final int[] counts = new int[] { 10, 500 };
			final int[] gitems = new int[] { 40526 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount >= amount) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, amount, gcounts);// 給予
			}
			isCloseList = true;

			// 製作一個鋼鐵塊，需要：鋼鐵原石 5 個、金屬塊 5 個、以及 500 金幣。
		} else if (cmd.equalsIgnoreCase("request lump of steel")) {// 製作鋼鐵塊
			final int[] items = new int[] { 40899, 40408, 40308 };
			final int[] counts = new int[] { 5, 5, 500 };
			final int[] gitems = new int[] { 40779 };
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

		} else if (cmd.equalsIgnoreCase("a2")) {// 製作鋼鐵塊
			final int[] items = new int[] { 40899, 40408, 40308 };
			final int[] counts = new int[] { 5, 5, 500 };
			final int[] gitems = new int[] { 40779 };
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
		return 15;
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

		private final Point[] _point = new Point[] { new Point(33471, 32775), new Point(33471, 32773), // 翻爐
				new Point(33468, 32774) // 打鐵
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
					if (_npc.getLocation().isSamePoint(_point[0])) {
						_npc.setHeading(4);
						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));
						Thread.sleep(_spr);
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 7));

					} else if (_npc.getLocation().isSamePoint(_point[1])) {
						_npc.setHeading(4);
						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));
						Thread.sleep(_spr);
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 19));
						Thread.sleep(_spr);
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 19));
						Thread.sleep(_spr);
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 19));

					} else if (_npc.getLocation().isSamePoint(_point[2])) {
						_npc.setHeading(4);
						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));
						Thread.sleep(_spr);
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 17));
						Thread.sleep(_spr);
						_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 18));
					}
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
