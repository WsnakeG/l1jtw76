package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 王族的自知 (王族15級以上官方任務)
 * 
 * @author daien
 */
public class CrownLv15_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(CrownLv15_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_c15_1";

	/*
	 * <img src="#1210"></img> <font fg=ffff66>步驟.1 傑羅的試煉：</font><BR> <BR>
	 * 與肯特村的<font fg=0000ff>傑羅</font>對話後，了解到 身為王族必需知道的事情，並且 為了證明你擁有成為王者的能力
	 * ，傑羅將會給予你試煉 。<BR> <BR> 到肯特村與傑羅說話接下試煉後 ，出村尋找黑騎士搜索隊，打倒 後取得搜索狀，將搜索狀交給<font
	 * fg=0000ff>傑 羅</font>後獲得<font fg=0000ff>"紅色斗篷"</font>，並請你前 往尋找<font
	 * fg=0000ff>甘特</font>。<BR> <BR> 注意事項：<BR> 黑騎士搜索隊位在肯特村莊下方 出村莊之後，過橋右邊的荒野，
	 * 那裡又稱 食屍鬼出沒地。<BR> <BR> 任務目標：<BR> 打敗黑騎士搜索隊取得搜索狀， 之後拿回去跟傑羅領取獎勵。<BR> <BR>
	 * 相關怪物：<BR> <font fg=ffff00>Lv.16 黑騎士搜索隊</font><BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>搜索狀 x 1</font><BR> <font fg=ffff00>紅色斗篷 x 1</font><BR> <BR>
	 * <img src="#1210"></img> <font fg=ffff66>步驟.2 甘特的試煉：</font><BR> <BR>
	 * 到甘特的洞穴 (在說話之島冒險洞窟入口上方) ，找到<font fg=0000ff>甘特</font>，甘特會要你獵殺高
	 * 崙石頭怪，將會隨機取得<font fg=0000ff>"生命 的卷軸"</font>，帶著卷軸回來與甘特 交談，可得到 <font
	 * fg=0000ff>魔法書(精準目標)</font>。<BR> <BR> 任務目標：<BR> 打敗高崙石頭怪取得生命的卷軸
	 * ，之後拿回去跟甘特領取獎勵。<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.13 石頭高崙</font><BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>生命的卷軸 x 1</font><BR> <font
	 * fg=ffff00>魔法書(精準目標) x 1</font><BR> <BR>
	 */
	private CrownLv15_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new CrownLv15_1();
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
