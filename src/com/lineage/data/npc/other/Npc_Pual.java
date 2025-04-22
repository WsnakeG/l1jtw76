package com.lineage.data.npc.other;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv30_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 鐵匠^皮爾<BR>
 * 85028<BR>
 * 
 * @author dexc
 */
public class Npc_Pual extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Pual.class);

	private Npc_Pual() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Pual();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 請問有什麼事嗎？
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pual1"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (cmd.equalsIgnoreCase("request halpas symbol")) {// 交出普洛凱爾的礦物袋
			if (pc.getQuest().isStart(DragonKnightLv30_1.QUEST.get_id())) {
				final L1ItemInstance item = pc.getInventory().checkItemX(49215, 1);// 普洛凱爾的礦物袋
				if (item != null) {
					pc.getInventory().removeItem(item, 1);// 刪除道具
					// 給予任務道具(妖魔密使的徽印)
					CreateNewItem.getQuestItem(pc, npc, 49223, 1);

				} else {
					// 337 \f1%0不足%s。
					pc.sendPackets(new S_ServerMessage(337, "$5731(1)"));
				}
			}

			isCloseList = true;

		} else if (cmd.equalsIgnoreCase("request chainsword extinctioner")) {// 製作破滅者鎖鏈劍

			final L1ItemInstance item1 = pc.getInventory().checkItemX(49230, 1);// 塔爾立昂的武器材料清單
			if (item1 != null) {
				pc.getInventory().removeItem(item1);// 刪除道具
			}
			/*
			 * 發光的銀塊1個 米索莉100個 鋼鐵塊10個 高品質鑽石3個
			 */
			final int[] items = new int[] { 49228, 40494, 40779, 40052 };
			final int[] counts = new int[] { 1, 100, 10, 3 };
			final int[] gitems = new int[] { 273 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

		} else if (cmd.equalsIgnoreCase("request lump of steel")) {// 製作鋼鐵塊
			/*
			 * 鋼鐵原石 5個 金屬塊5個 500 個金幣
			 */
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
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "a1"));

			} else if (xcount < 1) {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("a1")) {// 製作鋼鐵塊
			/*
			 * 鋼鐵原石 5個 金屬塊5個 500 個金幣
			 */
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

		} else if (cmd.equalsIgnoreCase("request chainsword destroyer")) {// 製作破滅者鎖鏈劍
			/*
			 * 受封印被遺忘的巨劍1個 芮克妮的網20個 高級皮革20個 火龍鱗1個 1000000個金幣
			 */
			final int[] items = new int[] { 17, 40406, 40503, 40393, 40308 };
			final int[] counts = new int[] { 1, 20, 20, 1, 1000000 };
			final int[] gitems = new int[] { 273 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

		} else if (cmd.equalsIgnoreCase("request guarder of ancient archer")) {// 製作古代神射臂甲
			/*
			 * 受封印被遺忘的皮盔甲1個 安特之樹皮50個 米索莉線50個 芮克妮的蛻皮20個 精靈羽翼20個 黑色米索莉金屬板3個
			 * 1000000個金幣
			 */
			final int[] items = new int[] { 20140, 40445, 40504, 40505, 40521, 40495, 40308 };
			final int[] counts = new int[] { 1, 3, 20, 50, 20, 50, 1000000 };
			final int[] gitems = new int[] { 21105 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);

		} else if (cmd.equalsIgnoreCase("request guarder of ancient champion")) {// 製作古代鬥士臂甲
			/*
			 * 受封印被遺忘的金屬盔甲1個 黑色米索莉金屬板5個 1000000個金幣
			 */
			final int[] items = new int[] { 20143, 40445, 40308 };
			final int[] counts = new int[] { 1, 5, 1000000 };
			final int[] gitems = new int[] { 21106 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getItem(pc, items, counts, gitems, gcounts);
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
}
