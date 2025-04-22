package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 拯救被幽禁的吉姆 (騎士30級以上官方任務)
 * 
 * @author daien
 */
public class KnightLv30_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(KnightLv30_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務地圖(傑瑞德的試煉地監)
	 */
	public static final int MAPID = 22;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_k30_1";

	/*
	 * <img src="#1210"></img> <font fg=ffff66>步驟.1 解救吉姆 ：</font><BR> <BR>
	 * 到沙漠商人的附近與<font fg=0000ff>馬克</font>對話，出發到大陸地下監獄，在地下監獄7樓獵殺<font
	 * fg=0000ff>食人妖精</font>，取得密室鑰匙，到達<font
	 * fg=0000ff>歐林</font>房後對著歐林面前的魔法書使用鑰匙，便會立刻到達密室。但是<font
	 * fg=0000ff>吉姆</font>卻是講著怪物的語言，此時必須變身為<font
	 * fg=0000ff>骷髏</font>，重新登入遊戲再次進入密室與吉姆對話。<BR> <BR> 注意事項：<BR>
	 * 必須變身骷髏才能與吉姆交談<BR> <BR> 任務目標：<BR> 與馬克接受任務，獵殺食人妖精取得密室鑰匙，與吉姆交談。<BR> <BR>
	 * 相關怪物：<BR> <font fg=ffff00>Lv.22 食人妖精</font><BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>密室鑰匙 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=ffff66>步驟.2 紅騎士的試煉 ：</font><BR> <BR> 與吉姆對話後，前往說話之島找<font
	 * fg=0000ff>甘特</font>。甘特要求獵殺島上的<font fg=0000ff>楊果里恩</font>，取得<font
	 * fg=0000ff>楊果里恩之爪</font>，帶回來給他便會獲得紅騎士之劍，他同時叫騎士前往大陸學習更高深的戰鬥技巧。<BR> <BR>
	 * 任務目標：<BR> 與甘特接受任務，獵殺楊果里恩取得楊果里恩之爪，並回去交差換取獎勵。<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.18 楊果里恩</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>楊果里恩之爪 x
	 * 1</font><BR> <font fg=ffff00>紅騎士之劍 x 1</font><BR> <BR> <img
	 * src="#1210"></img> <font fg=ffff66>步驟.3 銀騎士的試煉 ：</font><BR> <BR>
	 * 前往大陸銀騎士之村，找尋<font fg=0000ff>傑瑞德</font>與他對話接受試煉，通知去找<font
	 * fg=0000ff>守門人</font>，進入試煉地監，進入之後獵殺守護在外的蛇女得到進入蛇女房間的鑰匙，進入房間擊倒<font
	 * fg=0000ff>蛇女</font>，獲得蛇女之鱗，之後帶回給傑瑞德便可以得到返生藥水。將返生藥水交給地下監獄密室中的<font
	 * fg=0000ff>吉姆</font>，可以獲得感謝信，把感謝信交給傑瑞德之後，即可得到騎士的試煉道具紅騎士之盾。<BR>
	 * 與吉姆交談之後才知道這是在測試玩家們是否有資格成為紅騎士的考驗。<BR> <BR> 任務目標：<BR>
	 * 與傑瑞德接受任務，獵取傑瑞德試煉地監的蛇女取得蛇女之鱗，回去交差獲得返生藥水，去找吉姆讓他恢復原貌，之後再回去找傑瑞德交差，領取獎勵。<BR>
	 * <BR> 相關怪物：<BR> <font fg=ffff00>Lv.22 蛇女</font><BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>蛇女房間鑰匙 x 1</font><BR> <font fg=ffff00>蛇女之鱗 x 1</font><BR> <font
	 * fg=ffff00>返生藥水 x 1</font><BR> <font fg=ffff00>感謝信 x 1</font><BR> <font
	 * fg=ffff00>紅騎士盾牌 x 1</font><BR> <BR>
	 */
	private KnightLv30_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new KnightLv30_1();
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
