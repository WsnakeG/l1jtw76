package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv15_2;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 歐斯<BR>
 * 70826<BR>
 * 說明:歐斯的先見之明 (妖精15級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Oth extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Oth.class);

	private Npc_Oth() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Oth();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 燃柳村裡常常會聚集擁有壞思想的人類。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth2"));

			} else if (pc.isKnight()) {// 騎士
				// 燃柳村裡常常會聚集擁有壞思想的人類。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth2"));

			} else if (pc.isElf()) {// 精靈
				// 任務已經完成
				if (pc.getQuest().isEnd(ElfLv15_2.QUEST.get_id())) {
					// 關於哈汀
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth5"));

				} else {
					// 等級達成要求
					if (pc.getLevel() >= ElfLv15_2.QUEST.get_questlevel()) {
						// 關於妖魔族的魔法
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth1"));
						// 將任務設置為啟動
						QuestClass.get().startQuest(pc, ElfLv15_2.QUEST.get_id());

					} else {
						// 你好啊。好久沒看過自己的同族，
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth6"));
					}
				}

			} else if (pc.isWizard()) {// 法師
				// 燃柳村裡常常會聚集擁有壞思想的人類。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth2"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 燃柳村裡常常會聚集擁有壞思想的人類。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth2"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 燃柳村裡常常會聚集擁有壞思想的人類。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth2"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 燃柳村裡常常會聚集擁有壞思想的人類。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth2"));

			} else {
				// 燃柳村裡常常會聚集擁有壞思想的人類。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth2"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isElf()) {// 精靈
			// 任務已經完成
			if (pc.getQuest().isEnd(ElfLv15_2.QUEST.get_id())) {
				return;
			}

			if (cmd.equalsIgnoreCase("request dex helmet of elven")) {// 選擇精靈敏捷頭盔
				getItem(pc, 20021);

			} else if (cmd.equalsIgnoreCase("request con helmet of elven")) {// 選擇精靈體質頭盔
				getItem(pc, 20039);
			}

		} else {
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 交換物品
	 * 
	 * @param pc
	 * @param npc
	 * @param srcid
	 * @param getid
	 */
	private void getItem(final L1PcInstance pc, final int getid) {
		// 需要物件不足
		if (CreateNewItem.checkNewItem(pc,
				new int[] { 40609, // 甘地妖魔魔法書
						40610, // 那魯加妖魔魔法書
						40611, // 都達瑪拉妖魔魔法書
						40612,// 阿吐巴妖魔魔法書
				}, new int[] { 1, 1, 1, 1, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));

		} else {// 需要物件充足
			// 收回任務需要物件 給予任務完成物件
			CreateNewItem.createNewItem(pc,
					new int[] { 40609, // 甘地妖魔魔法書
							40610, // 那魯加妖魔魔法書
							40611, // 都達瑪拉妖魔魔法書
							40612,// 阿吐巴妖魔魔法書
					}, new int[] { 1, 1, 1, 1, }, new int[] { getid,// GET
					}, 1, new int[] { 1, });// 給予

			// 將任務設置為結束
			QuestClass.get().endQuest(pc, ElfLv15_2.QUEST.get_id());

			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
