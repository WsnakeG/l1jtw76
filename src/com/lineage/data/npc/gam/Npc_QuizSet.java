package com.lineage.data.npc.gam;

import java.util.Random;

import com.lineage.config.ConfigAlt;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ExtraQuizSetTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 每日一題管理員
 * 
 * @author terry0412
 */
public class Npc_QuizSet extends NpcExecutor {

	private final Random _random = new Random();

	/**
	 *
	 */
	private Npc_QuizSet() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_QuizSet();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		// 啟動開關
		if (!ConfigAlt.QUIZ_SET_SWITCH || (ExtraQuizSetTable.GS_showQuiz == null)) {
			pc.sendPackets(new S_SystemMessage("題目沒有正常啟動或是正在重置中..."));
			return;
		}
		// 使用任務紀錄來作判斷
		if (pc.getQuest().get_step(81245) == 0) {
			// 尚未答題
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "quiz1"));

		} else {
			// 今天您已經選過了。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "quiz4"));
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		// 啟動開關
		if (!ConfigAlt.QUIZ_SET_SWITCH || (ExtraQuizSetTable.GS_showQuiz == null)) {
			pc.sendPackets(new S_SystemMessage("題目沒有正常啟動或是正在重置中..."));
			return;
		}
		// 答題限制最低等級
		if (pc.getLevel() < ConfigAlt.QUIZ_SET_LEVEL) {
			pc.sendPackets(new S_SystemMessage("等級低於[" + ConfigAlt.QUIZ_SET_LEVEL + "]無法進行答題。"));
			return;
		}
		if (cmd.equals("request show_quiz")) { // 觀看今日題目
			// 顯示題目 內容和選項
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "quiz2",
					new String[] { ExtraQuizSetTable.GS_showQuiz, ExtraQuizSetTable.GS_option[0],
							ExtraQuizSetTable.GS_option[1], ExtraQuizSetTable.GS_option[2],
							ExtraQuizSetTable.GS_option[3] }));

		} else if (cmd.equals("request optionA")) { // 選項 (Ａ)
			// 比對答題結果
			checkResults(pc, npc.getId(), 1);

		} else if (cmd.equals("request optionB")) { // 選項 (Ｂ)
			// 比對答題結果
			checkResults(pc, npc.getId(), 2);

		} else if (cmd.equals("request optionC")) { // 選項 (Ｃ)
			// 比對答題結果
			checkResults(pc, npc.getId(), 3);

		} else if (cmd.equals("request optionD")) { // 選項 (Ｄ)
			// 比對答題結果
			checkResults(pc, npc.getId(), 4);

		}
	}

	/**
	 * 比對答題結果
	 * 
	 * @param pc
	 * @param objid
	 * @param answer
	 */
	private final void checkResults(final L1PcInstance pc, final int objid, final int answer) {
		// 已經選過了
		if (pc.getQuest().get_step(81245) != 0) {
			// 今天您已經選過了。
			pc.sendPackets(new S_NPCTalkReturn(objid, "quiz4"));
			return;
		}
		// 答案符合
		if (ExtraQuizSetTable.GS_answer == answer) {
			/**
			 * 給予獲勝獎勵 (隨機抽取其中之一)
			 */
			final int[] itemGroup = ConfigAlt.QUIZ_SET_LIST[_random.nextInt(ConfigAlt.QUIZ_SET_LIST.length)];

			// 產生新物件
			final L1ItemInstance item = ItemTable.get().createItem(itemGroup[0]);
			if (item != null) {
				item.setCount(itemGroup[1]);

				// 紀錄答題狀態
				pc.getQuest().set_step(81245, 1);

				// store
				pc.getInventory().storeItem(item);

				// 恭喜獲得 <var src="#0"> x<var src="#1"><br>
				pc.sendPackets(new S_NPCTalkReturn(objid, "quiz5",
						new String[] { item.getItem().getName(), String.valueOf(item.getCount()) }));
			}

		} else {
			// 紀錄答題狀態
			pc.getQuest().set_step(81245, 1);

			// 很遺憾，您答錯了，請明天再來。
			pc.sendPackets(new S_NPCTalkReturn(objid, "quiz3"));
		}
	}
}
