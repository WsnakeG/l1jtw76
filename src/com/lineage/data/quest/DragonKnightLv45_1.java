package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:與幻術士締結同盟 (龍騎士45級以上官方任務)
 * 
 * @author daien
 */
public class DragonKnightLv45_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(DragonKnightLv45_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_dk45_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 長老賦予的任務 </font><BR> <BR>
	 * 至貝希摩斯找長老<font fg=0000ff>普洛凱爾</font>(32817 32831)，他會要求你前往<font
	 * fg=0000ff>希培莉亞</font>與<font fg=0000ff>幻術士</font>們進行結盟任務。<BR>
	 * 之後長老普洛凱爾會給予你<font fg=0000ff>普洛凱爾的第三次指令書</font>與長老<font
	 * fg=0000ff>普洛凱爾的信件</font>及<font fg=0000ff>結盟瞬間移動卷軸</font>。<BR> <BR>
	 * 任務目標：<BR> 跟長老普洛凱爾接洽任務，並探聽任務地點<BR> <BR> 相關物品：<BR> <font fg=ffff00>結盟瞬間移動卷軸
	 * x 1</font><BR> <font fg=ffff00>長老普洛凱爾的信件 x 1</font><BR> <font
	 * fg=ffff00>普洛凱爾的第三次指令書 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.2 前往希培莉亞 </font><BR> <BR> 使用結盟瞬間移動卷軸即會立即傳送至希培莉亞。<BR>
	 * 進入希培莉亞後將長老普洛凱爾的信件交給長老<font fg=0000ff>希蓮恩</font>(32772 32811)。<BR>
	 * 長老希蓮恩會通知你要先通過考驗才肯結盟，並給你<font fg=0000ff>希蓮恩的指令書</font>。<BR> <BR> 任務目標：<BR>
	 * 跟長老希蓮恩會面，並探聽結盟考驗的內容<BR> <BR> 相關物品：<BR> <font fg=ffff00>希蓮恩的指令書 x
	 * 1</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.3 狩獵雪怪
	 * </font><BR> <BR> 在歐瑞附近狩獵雪怪，湊齊10個<font fg=0000ff>雪怪之心</font>。<BR>
	 * 之後將10個雪怪之心交給長老希蓮恩，即可獲得<font fg=0000ff>幻術士同盟徽印</font>。<BR>
	 * 在完成結盟手續之後，即可回去貝希摩斯找長老普洛凱爾。<BR> <BR> 任務目標：<BR>
	 * 將10個雪怪之心交給長老希蓮恩，並取得幻術士同盟徽印<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.30
	 * 雪怪</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>幻術士同盟徽印 x 1</font><BR> <font
	 * fg=ffff00>雪怪之心 x 10</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.4 回報 </font><BR> <BR> 將幻術士同盟徽印拿回去給長老普洛凱爾，可獲得<font
	 * fg=0000ff>普洛凱爾的第二次信件</font>。<BR> 之後將普洛凱爾的第二次信件交給塔爾立昂(32828
	 * 32844)，即可獲得<font fg=0000ff>龍騎士斗篷</font>。<BR> <BR> 任務目標：<BR>
	 * 將幻術士同盟徽印交給長老普洛凱爾，並取得獎勵<BR> <BR> 相關物品：<BR> <font fg=ffff00>龍騎士斗篷 x
	 * 1</font><BR> <font fg=ffff00>普洛凱爾的第二次信件 x 1</font><BR> <BR>
	 */
	private DragonKnightLv45_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new DragonKnightLv45_1();
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
