package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 瑞奇的抵抗 (騎士15級以上官方任務)
 * 
 * @author daien
 */
public class KnightLv15_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(KnightLv15_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_k15_1";

	/*
	 * <img src="#1210"></img> <font fg=ffff66>步驟.1 反王的動向 ：</font><BR> <BR>
	 * 與銀騎士之村的<font fg=0000ff>瑞奇</font>對話後，了 解到反王的忠臣黑騎士手上有一 份誓約書，此誓約書的內容寫明
	 * 反王的動向，瑞奇指出若要預防 反王的企圖，必需取的這份誓約 書。<BR> <BR> 與銀騎士之村的瑞奇對話後，他 會要求你取得<font
	 * fg=0000ff>黑騎士的誓約</font>，來 了解反王的動向，出村後尋找黑 騎士，打倒後取得黑騎士的誓約 ，將誓約書交給瑞奇後他會送你
	 * <font fg=0000ff>騎士頭巾</font>。<BR> <BR> 任務目標：<BR> 與瑞奇接受任務，狩獵黑騎士取
	 * 得黑騎士的誓約，回去交差換取 獎勵。<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.16
	 * 黑騎士</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>黑騎士的誓約 x 1</font><BR> <font
	 * fg=ffff00>騎士頭巾 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=ffff66>步驟.2 強化騎士頭巾 ：</font><BR> <BR> 此時再與瑞奇對話，得知他有位 叫<font
	 * fg=0000ff>亞南</font>的鐵匠朋友，若是你將<font fg=0000ff>古 老的交易文件</font>及<font
	 * fg=0000ff>龍龜甲</font>交給他， 他就能幫你加強騎士頭巾，出村 後尋找黑騎士以及龍龜，打倒他 們後會分別取得古老的交易文件
	 * 及龍龜甲，將這兩樣物品交給亞 南後，可以將騎士頭巾加強成 <font fg=0000ff>"紅騎士頭巾"</font>。<BR> <BR>
	 * 任務目標：<BR> 與亞南接受任務，獵取黑騎士和 龍龜取得古老的交易文件和龍龜 甲，回去交差換取獎勵。<BR> <BR> 相關怪物：<BR>
	 * <font fg=ffff00>Lv.16 黑騎士</font><BR> <font fg=ffff00>Lv.24 龍龜</font><BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>古老的交易文件 x 1</font><BR> <font fg=ffff00>龍龜甲
	 * x 1</font><BR> <font fg=ffff00>紅騎士頭巾 x 1</font><BR> <BR>
	 */
	private KnightLv15_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new KnightLv15_1();
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
