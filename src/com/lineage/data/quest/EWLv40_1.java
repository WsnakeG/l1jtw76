package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:幫助羅伊逃脫(等級40以上官方任務)
 * 
 * @author daien
 */
public class EWLv40_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(EWLv40_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_ew40_1";

	/**
	 * 羅伊-人形殭屍
	 */
	public static final int _roiid = 81209;

	/**
	 * 羅伊
	 */
	public static final int _roi2id = 70957;

	/**
	 * 巴休
	 */
	public static final int _baschid = 70964;

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 困惑的巴休</font><BR> <BR>
	 * 當玩家們來到魔族神殿時，會於入口處附近看到一位騎士不知所措的呆站在一旁，前去打聽，才知道遠征隊有不少人被捉了，並且<font
	 * fg=0000ff>亞丁皇家法師「羅伊」</font>在與他逃難時失散了，希望玩家能幫助他，找到羅伊。<BR> <BR> 任務目標：<BR>
	 * 與巴休探聽消息，並深入魔族神殿探險<BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2
	 * 謎樣的殭屍</font><BR> <BR> 在快到魔族神殿的迷宮途中，突然遇到了一個有名子的人形殭屍()，才驚覺原來是<font
	 * fg=0000ff>巴休</font>提過的<font fg=0000ff>羅伊</font>本人，於是立即幫他施展<font
	 * fg=0000ff>
	 * 魔法相消術</font>，解除他身上的變身詛咒。事後才知道他是在逃亡途中，被名為墮落的高等魔族給變身的，好再有我們的相助，才能以解除困境。<BR>
	 * 之後只要將他帶到巴休身邊，即會取得羅伊的袋子。<BR> <BR> 任務目標：<BR>
	 * 找到人形殭屍，並對他施展魔法相消術，讓羅伊變回人貌，並引導他回到巴休身邊，即可取得報酬<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>羅伊的袋子 x 1</font><BR> <BR>
	 */
	private EWLv40_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new EWLv40_1();
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
