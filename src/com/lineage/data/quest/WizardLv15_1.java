package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:詹姆的請求 (法師15級以上官方任務)
 * 
 * @author daien
 */
public class WizardLv15_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(WizardLv15_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_w15_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 力量轉移研究 </font><BR> <BR>
	 * 到說話之島找<font
	 * fg=0000ff>詹姆</font>，可以得知他正在研究將不死族骨骸上的力量轉移到物品上，此時玩家可以接受收集材料。<BR> <BR>
	 * 任務目標：<BR> 尋找詹姆，並接受此任務<BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.2 狩獵食屍鬼 </font><BR> <BR>
	 * 在說話之島或其他地區，狩獵食屍鬼，有一定機率可獲得食屍鬼的指甲和食屍鬼的牙齒
	 * 。之後拿回去交給交給詹姆，即可得到受詛咒的魔法書。並再次尋問關於魔法能量之書的細節。<BR> <BR> 任務目標：<BR> 搜集<font
	 * fg=0000ff>食屍鬼的指甲</font>和<font
	 * fg=0000ff>食屍鬼的牙齒</font>，帶回去給詹姆領取獎勵，並尋問魔法能量之書的細節。<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.16 食屍鬼</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>食屍鬼的指甲 x
	 * 1</font><BR> <font fg=ffff00>食屍鬼的牙齒 x 1</font><BR> <font
	 * fg=ffff00>受詛咒的魔法書 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 最後的步驟 </font><BR> <BR> 在說話之島或其他地區，狩獵骷髏，有一定機率可獲得<font
	 * fg=0000ff>骷髏頭</font>。之後拿回去交給交給詹姆，即可得到魔法能量之書。<BR> <BR> 任務目標：<BR>
	 * 搜集骷髏頭，帶回去給詹姆，並領取<font fg=0000ff>魔法能量之書</font><BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.10 骷髏</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>骷髏頭 x
	 * 1</font><BR> <font fg=ffff00>魔法能量之書 x 1</font><BR> <BR>
	 */
	private WizardLv15_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new WizardLv15_1();
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
