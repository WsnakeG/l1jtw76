package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:魔法書的合成(全職業40級任務)
 * 
 * @author daien
 */
public class ALv40_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ALv40_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_a40_1";

	/*
	 * <body> <img src="#1210"></img> <font fg=66ff66>步驟.1 煩惱的拉比安尼 </font><BR>
	 * <BR> 到說話之島(32558，32965)找拉比安尼對話後得知他為了研究的材料而在煩惱。<BR>
	 * 只要幫他蒐集特定材料，就會贈送稀有的魔法書給玩家。<BR> <BR> 任務目標：<BR> 跟拉比安尼探聽換取魔法書的材料<BR> <BR>
	 * <img src="#1210"></img> <font fg=66ff66>步驟.2 治癒能量風暴 </font><BR> <BR>
	 * 拉比安尼指出如果想要學習治癒能量風暴，只要湊齊下列材料給他，他可以用該魔法書做為報酬。<BR>
	 * 材料：1個不死鳥之心、1個冰之女王之心、1個高崙之心、1個飛龍之心。<BR> <BR> 任務目標：<BR>
	 * 湊齊材料和拉比安尼交換魔法書(治癒能量風暴)<BR> <BR> 相關物品：<BR> <font fg=ffff00>不死鳥之心 x
	 * 1</font><BR> <font fg=ffff00>冰之女王之心 x 1</font><BR> <font fg=ffff00>高崙之心 x
	 * 1</font><BR> <font fg=ffff00>飛龍之心 x 1</font><BR> <font
	 * fg=ffff00>魔法書(治癒能量風暴) x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 激勵士氣 </font><BR> <BR>
	 * 拉比安尼指出如果想要學習激勵士氣，只要湊齊下列材料給他，他可以用該魔法書做為報酬。<BR>
	 * 材料：1個不死鳥之心、1個冰之女王之心、1個高崙之心、1個飛龍之心。<BR> <BR> 任務目標：<BR>
	 * 湊齊材料和拉比安尼交換魔法書(激勵士氣)<BR> <BR> 相關物品：<BR> <font fg=ffff00>不死鳥之心 x
	 * 1</font><BR> <font fg=ffff00>冰之女王之心 x 1</font><BR> <font fg=ffff00>高崙之心 x
	 * 1</font><BR> <font fg=ffff00>飛龍之心 x 1</font><BR> <font
	 * fg=ffff00>魔法書(激勵士氣) x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.4 衝擊士氣 </font><BR> <BR>
	 * 拉比安尼指出如果想要學習衝擊士氣，只要湊齊下列材料給他，他可以用該魔法書做為報酬。<BR>
	 * 材料：2個不死鳥之心、2個冰之女王之心、2個高崙之心、2個飛龍之心。<BR> <BR> 任務目標：<BR>
	 * 湊齊材料和拉比安尼交換魔法書(衝擊士氣)<BR> <BR> 相關物品：<BR> <font fg=ffff00>不死鳥之心 x
	 * 2</font><BR> <font fg=ffff00>冰之女王之心 x 2</font><BR> <font fg=ffff00>高崙之心 x
	 * 2</font><BR> <font fg=ffff00>飛龍之心 x 2</font><BR> <font
	 * fg=ffff00>魔法書(衝擊士氣) x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.5 鋼鐵士氣 </font><BR> <BR>
	 * 拉比安尼指出如果想要學習鋼鐵士氣，只要湊齊下列材料給他，他可以用該魔法書做為報酬。<BR>
	 * 材料：4個不死鳥之心、4個冰之女王之心、4個高崙之心、4個飛龍之心。<BR> <BR> 任務目標：<BR>
	 * 湊齊材料和拉比安尼交換魔法書(鋼鐵士氣)<BR> <BR> 相關物品：<BR> <font fg=ffff00>不死鳥之心 x
	 * 4</font><BR> <font fg=ffff00>冰之女王之心 x 4</font><BR> <font fg=ffff00>高崙之心 x
	 * 4</font><BR> <font fg=ffff00>飛龍之心 x 4</font><BR> <font
	 * fg=ffff00>魔法書(鋼鐵士氣) x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.6 格蘭肯之淚</font><BR> <BR>
	 * 拉比安尼指出要製作那個具有邪惡氣息的水晶的話，需要很多貴重的材料。<BR> 材料：1個五級黑魔石、1個黑色米索莉、3個黑色血痕。<BR> <BR>
	 * 任務目標：<BR> 湊齊材料和拉比安尼交換格蘭肯之淚<BR> <BR> 相關物品：<BR> <font fg=ffff00>五級黑魔石 x
	 * 1</font><BR> <font fg=ffff00>黑色米索莉 x 1</font><BR> <font fg=ffff00>黑色血痕 x
	 * 3</font><BR> <BR> <br> <img src="#331" action="index"> </body>
	 */
	private ALv40_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ALv40_1();
	}

	@Override
	public void execute(final L1Quest quest) {
		try {
			// 設置任務
			QUEST = quest;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			// _log.info("任務啟用:" + QUEST.get_note());
		}
	}

	@Override
	public void startQuest(final L1PcInstance pc) {
		try {
			// 判斷職業
			if (QUEST.check(pc)) {
				// 判斷等級
				if (pc.getLevel() >= QUEST.get_questlevel()) {
					// 任務尚未開始 設置為開始
					if (pc.getQuest().get_step(QUEST.get_id()) != 1) {
						pc.getQuest().set_step(QUEST.get_id(), 1);
					}

				} else {
					// 該等級 無法執行此任務
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_q_not1"));
				}

			} else {
				// 該職業無法執行此任務
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_q_not2"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void endQuest(final L1PcInstance pc) {
		try {
			// 任務尚未結束 設置為結束
			if (!pc.getQuest().isEnd(QUEST.get_id())) {
				pc.getQuest().set_end(QUEST.get_id());

				final String questName = QUEST.get_questname();
				// 3109:\f1%0 任務完成！
				pc.sendPackets(new S_ServerMessage("\\fT" + questName + "任務完成！"));
				// 任務可以重複
				if (QUEST.is_del()) {
					// 3110:請注意這個任務可以重複執行，需要重複任務，請在任務管理員中執行解除。
					pc.sendPackets(new S_ServerMessage("\\fT請注意這個任務可以重複執行，需要重複任務，請在任務管理員中執行解除。"));

				} else {
					// 3111:請注意這個任務不能重複執行，無法在任務管理員中解除執行。
					new S_ServerMessage("\\fR請注意這個任務不能重複執行，無法在任務管理員中解除執行。");
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void showQuest(final L1PcInstance pc) {
		try {
			// 展示任務說明
			if (_html != null) {
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), _html));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void stopQuest(final L1PcInstance pc) {
		// TODO Auto-generated method stub

	}
}
