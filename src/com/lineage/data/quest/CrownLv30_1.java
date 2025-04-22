package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 艾莉亞的請求 (王族30級以上官方任務)
 * 
 * @author daien
 */
public class CrownLv30_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(CrownLv30_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務地圖(變種巨蟻地監)
	 */
	public static final int MAPID = 217;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_c30_1";

	/*
	 * <img src="#1210"></img> <font fg=ffff66>步驟.1 村民的煩惱 ：</font><BR> <BR>
	 * 與風木村村長的女兒<font fg=0000ff>艾莉亞</font>對話 中得知，風木村遭受到沙漠中螞 蟻的攻擊，村民也被沙漠中的螞
	 * 蟻抓走，而這些螞蟻的行為都是 受到螞蟻女王來控制，所以希望 你能幫助他們，將螞蟻女王趕出 沙漠，並且救出村民及她的父親 。<BR> <BR>
	 * 到達風木村尋找村長的女兒艾莉 亞得知風木村正受到沙漠中螞蟻 的攻擊，透過對話之後接下任務 ，來到沙漠右下的蟻穴進入洞穴
	 * 遇到一隻搜查螞蟻，但是說著螞 蟻的語言，此時須變身為巨蟻之 後，重新進入遊戲再次與螞蟻對 話，此時才得知攻擊風木村民是 <font
	 * fg=0000ff>變種螞蟻</font>。<BR> <BR> 注意事項：<BR> 必須變身為巨蟻才能和搜查螞蟻 對話<BR> <BR>
	 * 任務目標：<BR> 與艾莉亞接受任務後，前往巨蟻 洞穴尋找搜查螞蟻，並與他對話 接受任務<BR> <BR> <img
	 * src="#1210"></img> <font fg=ffff66>步驟.2 螞蟻的戰爭 ：</font><BR> <BR>
	 * 接下委託之後，找尋附近的看守 螞蟻，此時也必須變身為巨大兵 蟻重登後才能對話，牠會幫你傳 送到變種巨蟻女王的巢穴，裡面
	 * 有變種巨蟻女王，通過並打敗變 種巨蟻女王後，得到了<font fg=0000ff>村民的遺 物</font>。<BR> <BR>
	 * 注意事項：<BR> 建議多帶血盟成員進入幫忙打。 <BR> 必須變身為巨大兵蟻才能和看守 螞蟻對話。<BR> <BR> 任務目標：<BR>
	 * 擊敗變種巨蟻女王取得村民的遺 物。<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.10
	 * 變種巨大兵蟻</font><BR> <font fg=ffff00>Lv.20 變種巨蟻女皇</font><BR> <BR> 相關物品：<BR>
	 * <font fg=ffff00>村民的遺物 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=ffff66>步驟.3 回報村民 ：</font><BR> <BR> 將村民的遺物帶回給風木村村長 女兒<font
	 * fg=0000ff>艾莉亞</font>，為了感謝你對村中 的幫助，便交給你回報的禮物艾 莉亞的回報，將艾莉亞的回報點
	 * 選使用之後，即可得到王族的試 煉道具<font fg=0000ff>君主的威嚴</font>和 <font
	 * fg=0000ff>魔法書(呼喚盟友)</font>。<BR> <BR> 任務目標：<BR> 將村民的遺物交給艾莉亞，取得 獎勵。<BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>君主的威嚴 x 1</font><BR> <font
	 * fg=ffff00>魔法書(呼喚盟友) x 1</font><BR> <BR>
	 */

	private CrownLv30_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new CrownLv30_1();
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
