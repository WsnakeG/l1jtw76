package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:擊退妖魔的契約(全職業15級任務)
 * 
 * @author daien
 */
public class ALv15_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ALv15_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_a15_1";

	/*
	 * <body> <img src="#1210"></img> <font fg=66ff66>步驟.1 簽訂契約 </font><BR> <BR>
	 * 在燃柳村莊商人處的左邊屋子裡，可以找到帶領人們開墾此地的<font
	 * fg=0000ff>萊拉</font>，與他對談會知道妖魔們一直再阻礙他們的開發，希望玩家們能協助掃蕩妖魔。<BR> <BR> 任務目標：<BR>
	 * 找到萊拉，簽訂契約<BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2 擊退妖魔
	 * </font><BR> <BR> 在妖魔森林狩獵妖魔類怪物即可取得圖騰。<BR> <BR> 注意事項：<BR>
	 * 必須單獨擊退妖魔才會掉落圖騰<BR> <BR> 任務目標：<BR> 擊退妖魔，取得圖騰<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.10 甘地妖魔</font><BR> <font fg=ffff00>Lv.13 羅孚妖魔</font><BR>
	 * <font fg=ffff00>Lv.15 阿吐巴妖魔</font><BR> <font fg=ffff00>Lv.15
	 * 都達瑪拉妖魔</font><BR> <font fg=ffff00>Lv.17 那魯加妖魔</font><BR> <BR> 相關物品：<BR>
	 * <font fg=ffff00>阿吐巴圖騰 x 1</font><BR> <font fg=ffff00>那魯加圖騰 x 1</font><BR>
	 * <font fg=ffff00>甘地圖騰 x 1</font><BR> <font fg=ffff00>羅孚圖騰 x 1</font><BR>
	 * <font fg=ffff00>都達瑪拉圖騰 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 領取獎金 </font><BR> <BR> 當你覺得不想繼續收集圖騰時，可以回去村莊裡將<font
	 * fg=0000ff>圖騰</font>交給<font
	 * fg=0000ff>萊拉</font>領取獎金。領完獎金即結束此契約，如想還要還是可以繼續簽訂契約直到下回領完獎金。<BR> <BR>
	 * 注意事項：<BR> <font fg=ffff00>【阿吐巴圖騰】價值：200金幣</font><BR> <font
	 * fg=ffff00>【那魯加圖騰】價值：100金幣</font><BR> <font
	 * fg=ffff00>【甘地圖騰】價值：30金幣</font><BR> <font
	 * fg=ffff00>【羅孚圖騰】價值：50金幣</font><BR> <font
	 * fg=ffff00>【都達瑪拉圖騰】價值：50金幣</font><BR> <BR> 任務目標：<BR> 將圖騰交給萊拉領取獎金 <BR> <BR>
	 * <br> <img src="#331" action="index"> </body>
	 */
	private ALv15_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ALv15_1();
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
