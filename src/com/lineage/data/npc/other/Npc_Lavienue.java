package com.lineage.data.npc.other;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ALv40_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 拉比安尼<BR>
 * 70524
 * 
 * @author dexc
 */
public class Npc_Lavienue extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Lavienue.class);

	/**
	 *
	 */
	private Npc_Lavienue() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Lavienue();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 任務已經完成
			if (pc.getQuest().isEnd(ALv40_1.QUEST.get_id())) {
				return;
			}
			if (pc.getLevel() >= 40) {// 大於40級
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "lavienue9"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			boolean isCloseList = false;

			if (cmd.equalsIgnoreCase("request tear of dark")) {// 製作格蘭肯之淚
				final int[] items = new int[] { 40324, 40524, 40443 };
				final int[] counts = new int[] { 1, 3, 1 };
				final int[] gitems = new int[] { 40525 };
				final int[] gcounts = new int[] { 1 };
				// 可製作數量
				final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
				if (xcount == 1) {
					// 收回需要物件 給予完成物件
					CreateNewItem.createNewItem(pc, items, counts, // 需要
							gitems, 1, gcounts);// 給予

					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, ALv40_1.QUEST.get_id());
					// 將任務設置為結束
					QuestClass.get().endQuest(pc, ALv40_1.QUEST.get_id());

					isCloseList = true;

				} else if (xcount > 1) {
					pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "a1"));

				} else if (xcount < 1) {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("a1")) {// 製作格蘭肯之淚
				final int[] items = new int[] { 40324, 40524, 40443 };
				final int[] counts = new int[] { 1, 3, 1 };
				final int[] gitems = new int[] { 40525 };
				final int[] gcounts = new int[] { 1 };
				// 可製作數量
				final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
				if (xcount >= amount) {
					// 收回需要物件 給予完成物件
					CreateNewItem.createNewItem(pc, items, counts, // 需要
							gitems, amount, gcounts);// 給予

					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, ALv40_1.QUEST.get_id());
					// 將任務設置為結束
					QuestClass.get().endQuest(pc, ALv40_1.QUEST.get_id());
				}
				isCloseList = true;

			} else if (cmd.equalsIgnoreCase("request spellbook62")) {// 換取魔法書(治癒能量風暴)
				final int[] items = new int[] { 40162, 40413, 40409, 40169 };
				final int[] counts = new int[] { 1, 1, 1, 1 };
				final int[] gitems = new int[] { 40208 };
				final int[] gcounts = new int[] { 1 };
				// 可製作數量
				final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
				if (xcount >= 1) {
					// 收回需要物件 給予完成物件
					CreateNewItem.createNewItem(pc, items, counts, // 需要
							gitems, 1, gcounts);// 給予

					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, ALv40_1.QUEST.get_id());
					// 將任務設置為結束
					QuestClass.get().endQuest(pc, ALv40_1.QUEST.get_id());
				}
				isCloseList = true;

			} else if (cmd.equalsIgnoreCase("request spellbook113")) {// 換取魔法書(激勵士氣)
				final int[] items = new int[] { 40162, 40413, 40409, 40169 };
				final int[] counts = new int[] { 1, 1, 1, 1 };
				final int[] gitems = new int[] { 40227 };
				final int[] gcounts = new int[] { 1 };
				// 可製作數量
				final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
				if (xcount >= 1) {
					// 收回需要物件 給予完成物件
					CreateNewItem.createNewItem(pc, items, counts, // 需要
							gitems, 1, gcounts);// 給予

					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, ALv40_1.QUEST.get_id());
					// 將任務設置為結束
					QuestClass.get().endQuest(pc, ALv40_1.QUEST.get_id());
				}
				isCloseList = true;

			} else if (cmd.equalsIgnoreCase("request spellbook116")) {// 換取魔法書(衝擊士氣)
				final int[] items = new int[] { 40162, 40413, 40409, 40169 };
				final int[] counts = new int[] { 2, 2, 2, 2 };
				final int[] gitems = new int[] { 40230 };
				final int[] gcounts = new int[] { 1 };
				// 可製作數量
				final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
				if (xcount >= 1) {
					// 收回需要物件 給予完成物件
					CreateNewItem.createNewItem(pc, items, counts, // 需要
							gitems, 1, gcounts);// 給予

					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, ALv40_1.QUEST.get_id());
					// 將任務設置為結束
					QuestClass.get().endQuest(pc, ALv40_1.QUEST.get_id());
				}
				isCloseList = true;

			} else if (cmd.equalsIgnoreCase("request spellbook114")) {// 換取魔法書(鋼鐵士氣)
				final int[] items = new int[] { 40162, 40413, 40409, 40169 };
				final int[] counts = new int[] { 4, 4, 4, 4 };
				final int[] gitems = new int[] { 40229 };
				final int[] gcounts = new int[] { 1 };
				// 可製作數量
				final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
				if (xcount >= 1) {
					// 收回需要物件 給予完成物件
					CreateNewItem.createNewItem(pc, items, counts, // 需要
							gitems, 1, gcounts);// 給予

					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, ALv40_1.QUEST.get_id());
					// 將任務設置為結束
					QuestClass.get().endQuest(pc, ALv40_1.QUEST.get_id());
				}
				isCloseList = true;

			} else if (cmd.equalsIgnoreCase("request spellbook117")) {// 獲得血盟之書
				final int[] items = new int[] { 40162, 40413, 40409, 40169 };
				final int[] counts = new int[] { 3, 3, 3, 3 };
				final int[] gitems = new int[] { 40231 };
				final int[] gcounts = new int[] { 1 };
				// 可製作數量
				final long xcount = CreateNewItem.checkNewItem(pc, items, counts);
				if (xcount >= 1) {
					// 收回需要物件 給予完成物件
					CreateNewItem.createNewItem(pc, items, counts, // 需要
							gitems, 1, gcounts);// 給予

					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, ALv40_1.QUEST.get_id());
					// 將任務設置為結束
					QuestClass.get().endQuest(pc, ALv40_1.QUEST.get_id());
				}
				isCloseList = true;
			}

			if (isCloseList) {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
