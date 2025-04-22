package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Quest;
import com.lineage.server.timecontroller.quest.AD80_3_Timer;

/**
 * 林德拜爾棲息地 (全職業80級任務副本)
 * 
 * @author terry0412
 */
public class ADLv80_3 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ADLv80_3.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務地圖(林德拜爾 洞穴)
	 */
	public static final int MAPID = 1017;

	/**
	 * 任務資料說明HTML
	 */
	// private static final String _html = "y_q_ad80_2";
	/*
	 * <body> <img src="#1210"></img> <font fg=66ff66>步驟.1 尋找卡娜</font><BR> <BR>
	 * 找到奇怪的村落居民<font fg=0000ff>卡娜</font>並完成委託。<BR> <BR> 注意事項：<BR> 卡娜：34036,
	 * 32258<BR> <BR> 任務目標：<BR> 找到卡娜接受任務，並完成委託<BR> <BR> <img src="#1210"></img>
	 * <font fg=66ff66>步驟.2 尋找龍之門扉</font><BR> <BR> 在找到龍之門扉，聚集<font
	 * fg=0000ff>5名以上玩家組成隊伍</font>與NPC對話，由隊長執行任務。 <BR> <BR> 任務目標：<BR>
	 * 進入法利昂棲息地執行副本<BR> <BR> 注意事項：<BR> 完成卡娜委託任務後奇怪的村落的其他居民(NPC)才會願意與你說話<BR>
	 * 副本每次服務器重開可以執行一次<BR> <BR> <br> <img src="#331" action="index"> </body>
	 */
	private ADLv80_3() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ADLv80_3();
	}

	@Override
	public void execute(final L1Quest quest) {
		try {
			// 設置任務
			QUEST = quest;

			final AD80_3_Timer ad80_3Timer = new AD80_3_Timer();
			ad80_3Timer.start();

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

				/*
				 * final String questName = QUEST.get_questname(); // 3109:\f1%0
				 * 任務完成！ pc.sendPackets(new S_ServerMessage("\\fT" + questName +
				 * "任務完成！")); // 任務可以重複 if (QUEST.is_del()) { //
				 * 3110:請注意這個任務可以重複執行，需要重複任務，請在任務管理員中執行解除。 pc.sendPackets(new
				 * S_ServerMessage("\\fT請注意這個任務可以重複執行，需要重複任務，請在任務管理員中執行解除。")); }
				 * else { // 3111:請注意這個任務不能重複執行，無法在任務管理員中解除執行。 new
				 * S_ServerMessage("\\fR請注意這個任務不能重複執行，無法在任務管理員中解除執行。"); }
				 */
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void showQuest(final L1PcInstance pc) {
		try {
			// 展示任務說明
			/*
			 * if (_html != null) { pc.sendPackets(new
			 * S_NPCTalkReturn(pc.getId(), _html)); }
			 */
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void stopQuest(final L1PcInstance pc) {
		// TODO Auto-generated method stub
	}
}
