package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:皮爾斯的憂鬱 (黑暗妖精15級以上官方任務)
 * 
 * @author daien
 */
public class DarkElfLv15_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(DarkElfLv15_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_d15_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 煩惱的皮爾斯 </font><BR> <BR>
	 * 在沉默洞穴找到<font fg=0000ff>皮爾斯</font>(32858,
	 * 32880)，他會跟玩家說由於他需要大量的提煉過的黑魔石，但又礙於自己十分不善長提煉黑魔石
	 * ，因次如果玩家能夠提供二級以上的黑魔石給他，他會拿出私家物品來和你交換。<BR> <BR> 二級黑魔石 → 銀飛刀(1,000)<BR>
	 * 三級黑魔石 → 重飛刀(2,000)<BR> 四級黑魔石 → 皮爾斯的禮物<BR> 五級黑魔石 → 真鐵手甲<BR> <BR> 任務目標：<BR>
	 * 提煉黑魔石來和皮爾斯換取物品<BR> <BR> 相關物品：<BR> <font fg=ffff00>二級黑魔石 x 1<BR> <font
	 * fg=ffff00>三級黑魔石 x 1<BR> <font fg=ffff00>四級黑魔石 x 1<BR> <font
	 * fg=ffff00>五級黑魔石 x 1<BR> <font fg=ffff00>銀飛刀 x 1000<BR> <font
	 * fg=ffff00>重飛刀 x 2000<BR> <font fg=ffff00>真鐵手甲 x 1<BR> <font
	 * fg=ffff00>皮爾斯的禮物 x 1<BR> <BR>
	 */
	private DarkElfLv15_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new DarkElfLv15_1();
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
