package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:達克馬勒的威脅 (妖精30級以上官方任務)
 * 
 * @author daien
 */
public class ElfLv30_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ElfLv30_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務地圖(達克馬勒地監)
	 */
	public static final int MAPID_1 = 213;

	/**
	 * 任務地圖(黑暗妖精地監)
	 */
	public static final int MAPID_2 = 211;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_e30_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 被偷的精靈書 </font><BR> <BR>
	 * 跟<font fg=0000ff>迷幻森林之母</font>對話得知，都配傑諾被趕走之後，又出現的新的威脅，叫做<font
	 * fg=0000ff>達克馬勒</font>，他正在妖精森林的某處研究著不可告人的魔法，並將妖精森林的精靈書偷走，但是他總是神出鬼沒，只有<font
	 * fg=0000ff>精靈公主</font>可以看到他，因此就把他趕走並取回精靈書當成你的考驗，把他找出來吧！<BR> 與<font
	 * fg=0000f
	 * f>迷幻森林之母</font>對話結束之後接下試煉，找到精靈公主(32970,32442)，她會試著幫你傳送到達克馬勒的房間，但是偶爾會失敗
	 * ，而被傳送到黑暗精靈的洞窟，需想辦法逃脫並重新嘗試，便會抵達達克馬勒的洞窟。<BR> <BR> 任務目標：<BR>
	 * 與迷幻森林之母接受任務，尋找精靈公主傳送到達克馬勒的洞窟<BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.2 擊退達克馬勒 </font><BR> <BR> 達克馬勒擁有強大的魔力，身旁守護著達克馬勒的是強而有力的<font
	 * fg=0000ff>土</font>、<font fg=0000ff>水</font>、<font
	 * fg=0000ff>火</font>、<font
	 * fg=0000ff>風之精靈</font>四種精靈，突破重圍打倒達克馬勒後取得受詛咒的精靈書。將受詛咒的精靈書帶回給迷幻森林之母會獲得妖精族寶物
	 * ，將妖精族寶物點選使用之後，即可得到妖精的試煉道具精靈T恤和精靈水晶-召喚屬性精靈。<BR> <BR> 任務目標：<BR>
	 * 狩獵達克馬勒取得受詛咒的精靈書，帶回去交差領取獎勵<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.27
	 * 達克馬勒45343</font><BR> <font fg=ffff00>Lv.30 達克馬勒之土精靈45306</font><BR> <font
	 * fg=ffff00>Lv.30 達克馬勒之風精靈45305</font><BR> <font fg=ffff00>Lv.30
	 * 達克馬勒之水精靈45304</font><BR> <font fg=ffff00>Lv.30 達克馬勒之火精靈45303</font><BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>受詛咒的精靈書 x 1</font><BR> <font
	 * fg=ffff00>妖精族寶物 x 1</font><BR> <font fg=ffff00>精靈T恤 x 1</font><BR> <font
	 * fg=ffff00>精靈水晶(召喚屬性精靈) x 1</font><BR> <BR>
	 */
	private ElfLv30_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ElfLv30_1();
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
