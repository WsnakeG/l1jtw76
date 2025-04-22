package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.datatables.lock.CharacterQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:Chapter.0：穿越時空的探險
 * 
 * @author daien
 */
public class Chapter00 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(Chapter00.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "q_cha0_1";

	/*
	 * <body> <img src="#1210"></img> <font fg=66ff66>步驟.1 幻術士的研究</font><BR>
	 * <BR> 到說話之島村莊找<font
	 * fg=0000ff>尤麗婭</font>，他會告訴玩家幻術士們想要研究過去發生的事情來探討世界各地的緣由。<BR> 談話完他會交給你<font
	 * fg=0000ff>時空之甕</font>，並且每次使用會獲得<font fg=0000ff>2個時空之玉</font>。<BR> <BR>
	 * 注意事項：<BR> ※時空之瓶每22小時可以使用1次<BR> <BR> 任務目標：<BR>
	 * 和說話之島村莊的優利愛取得時空之瓶，並且開出時空之玉<BR> <BR> 相關物品：<BR> <font fg=ffff00>時空之甕 x
	 * 1</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2
	 * 象牙塔的秘密研究所</font><BR> <BR> 將1個<font fg=0000ff>時空之玉</font>和<font
	 * fg=0000ff>10,000金幣</font>交給<font fg=0000ff>尤麗婭</font>，會幫你傳送到<font
	 * fg=0000ff>象牙塔的秘密研究所</font>。<BR> 在象牙塔的秘密研究所可以接洽秘譚系列的故事副本。<BR> <BR>
	 * 任務目標：<BR> 將1個時空之玉和10,000金幣交給優利愛傳送到象牙塔的秘密研究所<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>金幣 x 10000</font><BR> <font fg=ffff00>時空之玉 x 1</font><BR> <BR>
	 * <br> <img src="#331" action="index"> </body>
	 */
	private Chapter00() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new Chapter00();
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
				CharacterQuestReading.get().delQuest(pc.getId(), QUEST.get_id());
				final String questName = QUEST.get_questname();
				// 3109:\f1%0 任務完成！
				pc.sendPackets(new S_ServerMessage("\\fT" + questName + "任務完成！"));
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
