package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:歐斯的先見之明 (妖精15級以上官方任務)
 * 
 * @author daien
 */
public class ElfLv15_2 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ElfLv15_2.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_e15_2";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 妖魔的魔法 </font><BR> <BR>
	 * 與燃柳村的<font fg=0000ff>歐斯</font>對話，得知他為了防止妖魔入侵妖精森林，所以要先了解他們，
	 * 聽說在妖魔群裡有幾個妖魔會使用魔法，所以我們必需先研究他們的魔法，才能 了解他們的實力。<BR>
	 * 到燃柳村找歐斯對話後，得知他在研究妖魔群裡四種長久歷史的魔法，這些魔法 書分別在都達瑪拉妖魔、甘地妖魔、阿吐巴妖魔及那魯加妖魔手上，之後出村尋
	 * 找這四種妖魔，打倒他們後分別取得都<font fg=0000ff>達瑪拉妖魔法書</font>、<font
	 * fg=0000ff>甘地妖魔魔法書</font>、<font fg=0000ff>阿吐 巴妖魔魔法書</font>、<font
	 * fg=0000ff>那魯加妖魔魔法書</font>，再來就是將這四種魔法交給歐斯就可以獲得 "精靈敏捷頭盔或精靈體質頭盔" 。<BR> <BR>
	 * 注意事項：<BR> 獎勵可以從以下兩樣選取一樣：<BR> 精靈敏捷頭盔<BR> 精靈體質頭盔<BR> <BR> 任務目標：<BR>
	 * 與歐斯接受任務，狩獵都達瑪拉妖魔、甘地妖魔、阿吐巴妖魔、那魯加妖魔取得他們的魔法書，再回去交差領取獎勵<BR> <BR> 相關怪物：<BR>
	 * <font fg=ffff00>Lv.10 甘地妖魔</font><BR> <font fg=ffff00>Lv.15
	 * 阿吐巴妖魔</font><BR> <font fg=ffff00>Lv.15 都達瑪拉妖魔</font><BR> <font
	 * fg=ffff00>Lv.17 那魯加妖魔</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>阿吐巴妖魔魔法書
	 * x 1</font><BR> <font fg=ffff00>都達瑪拉妖魔魔法書 x 1</font><BR> <font
	 * fg=ffff00>甘地妖魔魔法書 x 1</font><BR> <font fg=ffff00>那魯加妖魔魔法書 x 1</font><BR>
	 * <font fg=ffff00>精靈敏捷頭盔 x 1</font><BR> <font fg=ffff00>精靈體質頭盔 x
	 * 1</font><BR> <BR>
	 */
	private ElfLv15_2() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ElfLv15_2();
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
