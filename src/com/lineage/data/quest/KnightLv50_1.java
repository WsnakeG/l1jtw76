package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 精靈們的騷動 (騎士50級以上官方任務)
 * 
 * @author daien
 */
public class KnightLv50_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(KnightLv50_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_k50_1";

	/*
	 * <img src="#1210"></img> <font fg=ffff66>步驟.1 迪嘉勒廷的委託 <BR> 到象牙塔 3樓尋找<font
	 * fg=0000ff>迪嘉勒廷</font>，他會委託玩家們到大洞穴地區，調查出沒的<font
	 * fg=0000ff>黑暗妖精將軍</font>。<BR> <BR> 注意事項：<BR>
	 * 進行該試煉之前，必須已經完成15級、30級和45級的試煉<BR> <BR> 任務目標：<BR>
	 * 跟迪嘉勒廷接洽任務，並前往大洞穴狩獵黑暗妖精將軍<BR> <BR> <img src="#1210"></img> <font
	 * fg=ffff66>步驟.2 丹特斯的召書 <BR> <BR> 在狩獵黑暗妖精將軍獲得<font
	 * fg=0000ff>丹特斯的召書</font>之後，即可回到象牙塔 3樓尋找迪嘉勒廷。<BR>
	 * 並將丹特斯的召書轉交給迪嘉勒廷，但是由於線索不足。<BR> 之後迪嘉勒廷會委託玩家繼續前往精靈墓穴，蒐集10個精靈的私語。<BR> <BR>
	 * 任務目標：<BR> 將丹特斯的召書交給迪嘉勒廷，並探聽下一個任務<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.43
	 * 黑暗妖精將軍 (1)</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>丹特斯的召書 x
	 * 1</font><BR> <BR> <img src="#1210"></img> <font fg=ffff66>步驟.3 精靈的私語
	 * </font><BR> <BR> 在<font fg=0000ff>精靈墓穴</font>狩獵精靈類怪物湊齊10個<font
	 * fg=0000ff>精靈的私語</font>之後，即可回到象牙塔 3樓尋找迪嘉勒廷。<BR>
	 * 並將10個精靈的私語轉交給迪嘉勒廷，這回才發現不死魔族能夠不斷重生的線索。<BR> 之後迪嘉勒廷會委託玩家前往魔族神殿尋找再生聖殿的入口。<BR>
	 * <BR> 注意事項：<BR> 續接任務請參考不死魔族再生的秘密，該任務為團體試煉，請先湊齊王族、騎士、妖精、法師4名成員。<BR> <BR>
	 * 任務目標：<BR> 將10個精靈的私語交給迪嘉勒廷，並探聽下一個目的地，50級試煉上半部結束<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.40 火之牙</font><BR> <font fg=ffff00>Lv.40 地之牙</font><BR> <font
	 * fg=ffff00>Lv.40 風之牙</font><BR> <font fg=ffff00>Lv.40 水之牙</font><BR> <font
	 * fg=ffff00>Lv.45 風靈之主</font><BR> <font fg=ffff00>Lv.45 水靈之主</font><BR>
	 * <font fg=ffff00>Lv.45 火靈之主</font><BR> <font fg=ffff00>Lv.45
	 * 地靈之主</font><BR> <font fg=ffff00>Lv.55 深淵風靈</font><BR> <font
	 * fg=ffff00>Lv.55 深淵水靈</font><BR> <font fg=ffff00>Lv.55 深淵火靈</font><BR>
	 * <font fg=ffff00>Lv.55 深淵地靈</font><BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>精靈的私語 x 10</font><BR> <BR>
	 */
	private KnightLv50_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new KnightLv50_1();
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
