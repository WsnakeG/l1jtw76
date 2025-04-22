package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:妖魔的侵入 (黑暗妖精15級以上官方任務)
 * 
 * @author daien
 */
public class DarkElfLv15_2 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(DarkElfLv15_2.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_d15_2";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 妖魔長老 </font><BR> <BR>
	 * 首先到沉默洞穴的大橋附近找<font fg=0000ff>警衛隊長－康 </font>(32803 ，
	 * 32895)，得知他為了維護治安而分身乏術以致於無法處理妖魔的入侵，所以他會讓你去獵殺妖魔長老，並且讓你證明你確實有此能力。<BR>
	 * 接受任務之後，只要到沉默洞穴的出入口附近的原野平原找尋外貌與妖魔法師相似的<font fg=0000ff>妖魔長老
	 * </font>，解決掉之後即可得到妖魔長老首級。<BR> 回到大橋附近找康，並把妖魔長老首級交給他，即可得到康之袋。打開康之袋，即可得到<font
	 * fg=0000ff>黑暗精靈水晶(提煉魔石) </font>和<font fg=0000ff>影子面具
	 * </font>。再次與康對話，得知等到自己的能力成熟時就可以去尋找倫得的幫助。<BR> <BR> 任務目標：<BR>
	 * 與康接受任務，狩獵妖魔長老取得它的首級，回去交差取得獎勵<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.12
	 * 妖魔長老 (沉默洞穴) </font><BR> <BR> 相關物品：<BR> <font fg=ffff00>妖魔長老首級 x 1
	 * </font><BR> <font fg=ffff00>康之袋 x 1 </font><BR> <font fg=ffff00>影子面具 x 1
	 * </font><BR> <font fg=ffff00>黑暗精靈水晶(提煉魔石) x 1 </font><BR> <BR>
	 */
	private DarkElfLv15_2() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new DarkElfLv15_2();
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
