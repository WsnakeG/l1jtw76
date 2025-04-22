package com.lineage.data.npc.other;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 道具製作^米米<BR>
 * 70938
 * 
 * @author dexc
 */
public class Npc_Sherme extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Sherme.class);

	/**
	 *
	 */
	private Npc_Sherme() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Sherme();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 嗯..原來你也是屠龍者..
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "sherme2"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (cmd.equalsIgnoreCase("a")) {// 製作地龍之魔眼
			final int[] items = new int[] { 42514, 40308 };
			final int[] counts = new int[] { 1, 100000 };
			final int[] gitems = new int[] { 42518 };
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

		} else if (cmd.equalsIgnoreCase("a1")) {
			final int[] items = new int[] { 42514, 40308 };
			final int[] counts = new int[] { 1, 100000 };
			final int[] gitems = new int[] { 42518 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount >= amount) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, amount, gcounts);// 給予
			}
			isCloseList = true;

		} else if (cmd.equalsIgnoreCase("b")) {// 製作水龍之魔眼
			final int[] items = new int[] { 42515, 40308 };
			final int[] counts = new int[] { 1, 100000 };
			final int[] gitems = new int[] { 42519 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount == 1) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;

			} else if (xcount > 1) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "b1"));

			} else if (xcount < 1) {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("b1")) {
			final int[] items = new int[] { 42515, 40308 };
			final int[] counts = new int[] { 1, 100000 };
			final int[] gitems = new int[] { 42519 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount >= amount) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, amount, gcounts);// 給予
			}
			isCloseList = true;

		} else if (cmd.equalsIgnoreCase("c")) {// 製作火龍之魔眼
			final int[] items = new int[] { 42517, 40308 };
			final int[] counts = new int[] { 1, 100000 };
			final int[] gitems = new int[] { 42521 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount == 1) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;

			} else if (xcount > 1) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "c1"));

			} else if (xcount < 1) {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("c1")) {
			final int[] items = new int[] { 42517, 40308 };
			final int[] counts = new int[] { 1, 100000 };
			final int[] gitems = new int[] { 42521 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount >= amount) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, amount, gcounts);// 給予
			}
			isCloseList = true;

		} else if (cmd.equalsIgnoreCase("d")) {// 製作風龍之魔眼
			final int[] items = new int[] { 42516, 40308 };
			final int[] counts = new int[] { 1, 100000 };
			final int[] gitems = new int[] { 42520 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount == 1) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;

			} else if (xcount > 1) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "d1"));

			} else if (xcount < 1) {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("d1")) {
			final int[] items = new int[] { 42516, 40308 };
			final int[] counts = new int[] { 1, 100000 };
			final int[] gitems = new int[] { 42520 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount >= amount) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, amount, gcounts);// 給予
			}
			isCloseList = true;

		} else if (cmd.equalsIgnoreCase("e")) {// 製作誕生之魔眼
			final int[] items = new int[] { 42525, 42526, 40308 };
			final int[] counts = new int[] { 1, 1, 200000 };
			final int[] gitems = new int[] { 42522 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount == 1) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;

			} else if (xcount > 1) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "e1"));

			} else if (xcount < 1) {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("e1")) {
			final int[] items = new int[] { 42525, 42526, 40308 };
			final int[] counts = new int[] { 1, 1, 200000 };
			final int[] gitems = new int[] { 42522 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount >= amount) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, amount, gcounts);// 給予
			}
			isCloseList = true;

		} else if (cmd.equalsIgnoreCase("f")) {// 製作形象之魔眼
			final int[] items = new int[] { 42522, 42527, 40308 };
			final int[] counts = new int[] { 1, 1, 200000 };
			final int[] gitems = new int[] { 42523 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount == 1) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;

			} else if (xcount > 1) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "f1"));

			} else if (xcount < 1) {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("f1")) {
			final int[] items = new int[] { 42522, 42527, 40308 };
			final int[] counts = new int[] { 1, 1, 200000 };
			final int[] gitems = new int[] { 42523 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount >= amount) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, amount, gcounts);// 給予
			}
			isCloseList = true;

		} else if (cmd.equalsIgnoreCase("g")) {// 製作生命之魔眼
			final int[] items = new int[] { 42523, 42528, 40308 };
			final int[] counts = new int[] { 1, 1, 200000 };
			final int[] gitems = new int[] { 42524 };
			final int[] gcounts = new int[] { 1 };
			// 可製作數量
			final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount == 1) {
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;

			} else if (xcount > 1) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "g1"));

			} else if (xcount < 1) {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("g1")) {
			final int[] items = new int[] { 42523, 42528, 40308 };
			final int[] counts = new int[] { 1, 1, 200000 };
			final int[] gitems = new int[] { 42524 };
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
}
