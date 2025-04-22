package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:艾爾摩戰場的軌跡 (幻術士30級以上官方任務)
 * 
 * @author daien
 */
public class IllusionistLv30_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(IllusionistLv30_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_i30_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 長老賦予的任務 </font><BR> <BR>
	 * 前往希培莉亞找<font fg=0000ff>長老希蓮恩</font>(32772 32811)，她會給予<font
	 * fg=0000ff>希蓮恩的第二次信件</font>和<font fg=0000ff>希蓮恩之袋</font>，可從袋中獲得<font
	 * fg=0000ff>歐瑞村莊瞬間移動卷軸</font>、<font fg=0000ff>生鏽的笛子</font>。<BR>
	 * 並且長老希蓮恩要你去調查艾爾摩大將軍殭屍無法安息的原因。<BR> <BR> 任務目標：<BR> 跟長老希蓮恩接洽任務，並探聽任務地點<BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>生鏽的笛子 x 1</font><BR> <font
	 * fg=ffff00>歐瑞村莊瞬間移動卷軸 x 1</font><BR> <font fg=ffff00>希蓮恩的第二次信件 x
	 * 1</font><BR> <font fg=ffff00>希蓮恩之袋 x 1</font><BR> <BR> <img
	 * src="#1210"></img> <font fg=66ff66>步驟.2 艾爾摩大將軍殭屍 </font><BR> <BR>
	 * 在歐瑞的艾爾摩戰場上找到<font fg=0000ff>艾爾摩大將軍殭屍</font>(34096 32396)，狩獵後可獲得<font
	 * fg=0000ff>艾爾摩將軍之心</font>。<BR> 再到古魯丁購買菊花花束，前往<font
	 * fg=0000ff>古魯丁祭壇</font>祭拜後，可獲得<font fg=0000ff>索夏依卡靈魂之石</font>、<font
	 * fg=0000ff>反王肯恩的權杖</font>。<BR> <BR> 任務目標：<BR> 取得艾爾摩將軍之心，並前往古魯丁進行祭拜<BR>
	 * <BR> 相關怪物：<BR> <font fg=ffff00>Lv.25 艾爾摩大將軍殭屍</font><BR> <BR> 相關物品：<BR>
	 * <font fg=ffff00>菊花花束 x 1</font><BR> <font fg=ffff00>反王肯恩的權杖 x
	 * 1</font><BR> <font fg=ffff00>艾爾摩將軍之心 x 1</font><BR> <font
	 * fg=ffff00>索夏依卡靈魂之石 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 昇華的儀式 </font><BR> <BR> 使用<font
	 * fg=0000ff>索夏依卡靈魂之石</font>修復<font fg=0000ff>生鏽的笛子</font>後，可獲得<font
	 * fg=0000ff>索夏依卡靈魂之笛</font>。<BR> 使用笛子召喚出<font
	 * fg=0000ff>艾爾摩索夏依卡將軍的冤魂</font>，打倒冤魂後可獲得<font fg=0000ff>封印的索夏依卡遺物</font>。
	 * <BR> 再購買<font fg=0000ff>卡拉花束</font>後，使用封印的索夏依卡遺物至<font
	 * fg=0000ff>古魯丁祭壇</font>祭拜可獲得<font fg=0000ff>艾爾摩部隊日記</font>。 <BR> <BR>
	 * 注意事項：<BR> 當召喚出艾爾摩索夏依卡將軍的冤魂，此時會被強迫變身為反王肯恩，而必須使用反王肯恩的權杖方可攻擊造成傷害。<BR>
	 * 使用索夏依卡靈魂之笛時，會短暫時間內無法使用物品欄道具。<BR> 變身為反王肯恩時，無法使用變身卷軸消除。 <BR> <BR> 任務目標：<BR>
	 * 進行第二次祭拜儀式，並取得艾爾摩部隊日記<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.25
	 * 艾爾摩索夏依卡將軍的冤魂</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>卡拉花束 x
	 * 1</font><BR> <font fg=ffff00>索夏依卡靈魂之笛 x 1</font><BR> <font
	 * fg=ffff00>封印的索夏依卡遺物 x 1</font><BR> <font fg=ffff00>艾爾摩部隊日記 x 1</font><BR>
	 * <BR> <img src="#1210"></img> <font fg=66ff66>步驟.4 回報 </font><BR> <BR>
	 * 將艾爾摩部隊日記交給長老希蓮恩，即可獲得<font fg=0000ff>幻術士法書</font>和<font
	 * fg=0000ff>記憶水晶(立方：衝擊)</font>。<BR> <BR> 任務目標：<BR>
	 * 將艾爾摩部隊日記交給長老希蓮恩，並獲得獎勵<BR> <BR> 相關物品：<BR> <font fg=ffff00>記憶水晶(立方：衝擊) x
	 * 1</font><BR> <font fg=ffff00>幻術士法書 x 1</font><BR> <BR>
	 */
	private IllusionistLv30_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new IllusionistLv30_1();
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
