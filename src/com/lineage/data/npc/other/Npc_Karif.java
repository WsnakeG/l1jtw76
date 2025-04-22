package com.lineage.data.npc.other;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv50_2;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 卡立普<BR>
 * 70762<BR>
 * 說明:暗黑的武器，死神之証 (黑暗妖精50級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Karif extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Karif.class);

	/**
	 *
	 */
	private Npc_Karif() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Karif();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你怎麼知道我在這裡？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));

			} else if (pc.isKnight()) {// 騎士
				// 你怎麼知道我在這裡？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));

			} else if (pc.isElf()) {// 精靈
				// 你怎麼知道我在這裡？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));

			} else if (pc.isWizard()) {// 法師
				// 你怎麼知道我在這裡？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// LV50-2任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv50_2.QUEST.get_id())) {
					// 你怎麼知道我在這裡？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= DarkElfLv50_2.QUEST.get_questlevel()) {
					// 嗯～你收集了足夠的材料，你想製作什麼呢！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif3a"));

				} else {
					// 你怎麼知道我在這裡？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));
				}

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你怎麼知道我在這裡？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你怎麼知道我在這裡？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));

			} else {
				// 你怎麼知道我在這裡？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isDarkelf()) {// 黑暗精靈
			if (cmd.equalsIgnoreCase("quest 32 karif4")) {// 請幫忙製作暗黑雙刀
				isCloseList = getItem1(pc);

			} else if (cmd.equalsIgnoreCase("quest 32 karif5")) {// 請幫忙製作暗黑鋼爪
				isCloseList = getItem2(pc);

			} else if (cmd.equalsIgnoreCase("quest 32 karif6")) {// 請幫忙製作暗黑十字弓
				isCloseList = getItem3(pc);

			} else if (cmd.equalsIgnoreCase("request darkness dualblade")) {// 製作暗黑雙刀
				isCloseList = getItem1(pc);

			} else if (cmd.equalsIgnoreCase("request darkness claw")) {// 製作暗黑鋼爪
				isCloseList = getItem2(pc);

			} else if (cmd.equalsIgnoreCase("request darkness crossbow")) {// 製作暗黑十字弓
				isCloseList = getItem3(pc);
			}
		}
		if (isCloseList) {
			// 你怎麼知道我在這裡？
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));
			return;
		}
		// 一般交換
		if (cmd.equalsIgnoreCase("request karif bag1")) {// 將鑽石送給他
			final int[] items = new int[] { 40044 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49005 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a1");

		} else if (cmd.equalsIgnoreCase("a1")) {
			final int[] items = new int[] { 40044 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49005 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request karif bag2")) {// 將綠寶石送給他
			final int[] items = new int[] { 40047 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49008 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a2");

		} else if (cmd.equalsIgnoreCase("a2")) {
			final int[] items = new int[] { 40047 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49008 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request karif bag3")) {// 將紅寶石送給他
			final int[] items = new int[] { 40045 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49006 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a3");

		} else if (cmd.equalsIgnoreCase("a3")) {
			final int[] items = new int[] { 40045 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49006 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request karif bag4")) {// 將藍寶石送給他
			final int[] items = new int[] { 40046 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49007 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a4");

		} else if (cmd.equalsIgnoreCase("a4")) {
			final int[] items = new int[] { 40046 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49007 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request karif bag5")) {// 將品質鑽石送給他
			final int[] items = new int[] { 40048 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49009 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a5");

		} else if (cmd.equalsIgnoreCase("a5")) {
			final int[] items = new int[] { 40048 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49009 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request karif bag6")) {// 將品質綠寶石送給他
			final int[] items = new int[] { 40051 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49010 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a6");

		} else if (cmd.equalsIgnoreCase("a6")) {
			final int[] items = new int[] { 40051 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49010 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request karif bag7")) {// 將品質紅寶石送給他
			final int[] items = new int[] { 40049 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49011 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a7");

		} else if (cmd.equalsIgnoreCase("a7")) {
			final int[] items = new int[] { 40049 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49011 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request karif bag8")) {// 將品質藍寶石送給他
			final int[] items = new int[] { 40050 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49012 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a8");

		} else if (cmd.equalsIgnoreCase("a8")) {
			final int[] items = new int[] { 40050 };
			final int[] counts = new int[] { 1 };
			final int[] gitems = new int[] { 49012 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 製作暗黑十字弓
	 * 
	 * @param pc
	 * @return
	 */
	private boolean getItem3(final L1PcInstance pc) {
		// LV50-2任務已經完成
		if (pc.getQuest().isEnd(DarkElfLv50_2.QUEST.get_id())) {
			return true;
		}
		// 等級未達成要求
		if (pc.getLevel() < DarkElfLv50_2.QUEST.get_questlevel()) {
			return true;
		}
		final int[] items = new int[] { 40466, 40054, 40308, 40403, 40413, 40525, 177 };
		final int[] counts = new int[] { 1, 3, 100000, 10, 9, 3, 1 };
		final int[] gitems = new int[] { 189 };
		final int[] gcounts = new int[] { 1 };

		return getItem(pc, items, counts, gitems, gcounts, 1);
	}

	/**
	 * 暗黑鋼爪
	 * 
	 * @param pc
	 * @return
	 */
	private boolean getItem2(final L1PcInstance pc) {
		// LV50-2任務已經完成
		if (pc.getQuest().isEnd(DarkElfLv50_2.QUEST.get_id())) {
			return true;
		}
		// 等級未達成要求
		if (pc.getLevel() < DarkElfLv50_2.QUEST.get_questlevel()) {
			return true;
		}
		final int[] items = new int[] { 40466, 40055, 40308, 40404, 40413, 40525, 162 };
		final int[] counts = new int[] { 1, 3, 100000, 10, 9, 3, 1 };
		final int[] gitems = new int[] { 164 };
		final int[] gcounts = new int[] { 1 };

		return getItem(pc, items, counts, gitems, gcounts, 1);
	}

	/**
	 * 製作暗黑雙刀
	 * 
	 * @param pc
	 * @return
	 */
	private boolean getItem1(final L1PcInstance pc) {
		// LV50-2任務已經完成
		if (pc.getQuest().isEnd(DarkElfLv50_2.QUEST.get_id())) {
			return true;
		}
		// 等級未達成要求
		if (pc.getLevel() < DarkElfLv50_2.QUEST.get_questlevel()) {
			return true;
		}
		final int[] items = new int[] { 40466, 40053, 40308, 40402, 40413, 40525, 81 };
		final int[] counts = new int[] { 1, 3, 100000, 10, 9, 3, 1 };
		final int[] gitems = new int[] { 84 };
		final int[] gcounts = new int[] { 1 };

		return getItem(pc, items, counts, gitems, gcounts, 1);
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
			final int[] gcounts, final long amount) {
		// 需要物件不足
		if (CreateNewItem.checkNewItem(pc, items, // 需要物件
				counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
			return true;

		} else {// 需要物件充足
			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, amount, gcounts);// 給予
			// 將任務設置為執行中
			QuestClass.get().startQuest(pc, DarkElfLv50_2.QUEST.get_id());
			// 將任務設置為結束
			QuestClass.get().endQuest(pc, DarkElfLv50_2.QUEST.get_id());
			return true;
		}
	}

	/**
	 * 交換寶石
	 * 
	 * @param pc
	 * @param items
	 * @param counts
	 * @param gitems
	 * @param gcounts
	 * @param amount
	 * @return
	 */
	private boolean getKarifBag(final L1PcInstance pc, final int[] items, final int[] counts,
			final int[] gitems, final int[] gcounts, final long amount) {
		// 可製作數量
		final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
		if (xcount >= amount) {
			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, amount, gcounts);// 給予
		}
		return true;
	}

	/**
	 * 交換寶石
	 * 
	 * @param pc
	 * @param npc
	 * @param items
	 * @param counts
	 * @param gitems
	 * @param gcounts
	 * @param string
	 * @return
	 */
	private boolean requestKarifBag(final L1PcInstance pc, final L1NpcInstance npc, final int[] items,
			final int[] counts, final int[] gitems, final int[] gcounts, final String string) {
		boolean isCloseList = false;
		// 可製作數量
		final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
		if (xcount == 1) {
			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, 1, gcounts);// 給予
			isCloseList = true;

		} else if (xcount > 1) {
			pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, string));

		} else if (xcount < 1) {
			isCloseList = true;
		}
		return isCloseList;
	}
}
