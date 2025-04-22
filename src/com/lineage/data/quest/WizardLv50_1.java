package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:取回間諜的報告書 (法師50級以上官方任務)
 * 
 * @author daien
 */
public class WizardLv50_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(WizardLv50_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_w50_1";

	/**
	 * 迪嘉勒廷的男間諜(歐姆民兵)
	 */
	public static final int _npcId = 80013;

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 迪嘉勒廷的委託 </font><BR> <BR>
	 * 到象牙塔 3樓尋找<font fg=0000ff>迪嘉勒廷</font>，他會委託玩家們到魔族神殿，尋找<font
	 * fg=0000ff>失聯的間諜</font>(32884, 32947)附近。<BR> <BR> 注意事項：<BR>
	 * 進行該試煉之前，必須已經完成15級、30級和45級的試煉<BR> <BR> 任務目標：<BR> 跟迪嘉勒廷接洽任務，並前往魔族神殿<BR>
	 * <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2 失聯的間諜 </font><BR> <BR>
	 * 前往魔族神殿尋找偽裝成<font fg=0000ff>歐姆民兵</font>樣子的間諜，並且需使用<font
	 * fg=0000ff>魔法相消術</font>來分辨原來的樣子。<BR> 和間諜談過之後，才知道<font
	 * fg=0000ff>間諜報告書</font>似乎被魔族神殿的怪物們撿走了。<BR> 因此需要玩家們幫忙找回來。<BR> <BR>
	 * 注意事項：<BR> 不能攻擊的歐姆民兵(重裝歐姆外型)，就是要尋找的間諜，需要使用魔法相消術之後(變成警衛)才能對話<BR> <BR>
	 * 任務目標：<BR> 找到失聯的間諜，並探聽下一個任務<BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 間諜報告書 </font><BR> <BR> 狩獵魔族神殿的怪物，直到找回間諜報告書，即可回到象牙塔
	 * 3樓尋找迪嘉勒廷。<BR> 並將間諜報告書轉交給迪嘉勒廷，這回才發現不死魔族能夠不斷重生的線索。<BR>
	 * 之後迪嘉勒廷會委託玩家前往魔族神殿尋找再生聖殿的入口。 <BR> <BR> 注意事項：<BR>
	 * 續接任務請參考不死魔族再生的秘密，該任務為團體試煉，請先湊齊王族、騎士、妖精、法師4名成員。<BR> <BR> 任務目標：<BR>
	 * 將間諜報告書交給迪嘉勒廷，並探聽下一個目的地，50級試煉上半部結束<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.37 炎魔的思克巴</font><BR> <font fg=ffff00>Lv.41
	 * 炎魔的思克巴女皇</font><BR> <font fg=ffff00>Lv.44 炎魔的小惡魔</font><BR> <font
	 * fg=ffff00>Lv.51 墮落的司祭 (烈炎蛙)</font><BR> <font fg=ffff00>Lv.51
	 * 炎魔的巴風特</font><BR> <font fg=ffff00>Lv.51 墮落的司祭 (暗殺者)</font><BR> <font
	 * fg=ffff00>Lv.53 墮落的司祭 (鐮刀手)</font><BR> <font fg=ffff00>Lv.53
	 * 炎魔的巴列斯</font><BR> <font fg=ffff00>Lv.54 墮落的司祭 (噴毒獸)</font><BR> <font
	 * fg=ffff00>Lv.56 墮落的司祭 (三頭魔)</font><BR> <font fg=ffff00>Lv.57
	 * 炎魔的分身</font><BR> <font fg=ffff00>Lv.61 炎魔的惡魔</font><BR> <BR> 相關物品：<BR>
	 * <font fg=ffff00>間諜報告書 x 1 </font><BR> <BR>
	 */
	private WizardLv50_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new WizardLv50_1();
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
