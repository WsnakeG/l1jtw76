package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;
import com.lineage.server.timecontroller.quest.DE50A_Timer;

/**
 * 說明:不死魔族再生的秘密 (王族,騎士,妖精,法師50級以上官方任務-50級後半段)
 * 
 * @author daien
 */
public class CKEWLv50_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(CKEWLv50_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務地圖(再生聖殿 1樓/2樓/3樓)
	 */
	public static final int MAPID = 2000;

	/**
	 * 成員判斷用數字
	 */
	public static final int USER = 15;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_ckew50_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 隱藏的再生聖殿 </font><BR> <BR>
	 * 再經過一連串的調查，確認到不死魔族似乎存在著再生聖殿來達到不死。<BR> 為了擊退不死魔族，必定要先斷絕他們不斷再生的根源。<BR>
	 * 根據調查的線索，可以在魔族神殿尋找到再生聖殿的入口。<BR> <BR> 注意事項：<BR> 四種職業的再生聖殿入口位置並不相同<BR>
	 * 進入前<font fg=0000ff>必須以王族為隊長預先組隊</font><BR> 王族傳送水晶：魔族神殿(32926, 32830)<BR>
	 * 騎士傳送水晶：魔族神殿(32849, 32830)<BR> 法師傳送水晶：魔族神殿(32854, 32924)<BR>
	 * 妖精傳送水晶：魔族神殿(32884, 32951)<BR> <BR> 在魔族神殿裡：<BR> 王族沒有限定武器<BR> 騎士需使用天空之劍<BR>
	 * 妖精需使用水精靈之弓<BR> 法師需使用古代人的智慧<BR> 至於水精靈之弓所使用的古代之箭，聖殿裡的怪物會掉落<BR> <BR>
	 * 任務目標：<BR> 接受<font fg=0000ff>迪嘉勒廷公爵</font>的指示，前往魔族神殿進入再生聖殿<BR> <BR>
	 * 相關物品：<BR> <font fg=ffff00>古代之箭 x 1</font><BR> <font fg=ffff00>水精靈之弓 x
	 * 1</font><BR> <font fg=ffff00>古代人的智慧 x 1</font><BR> <font fg=ffff00>天空之劍 x
	 * 1</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2 再生聖殿 1樓~2樓
	 * </font><BR> <BR> 在再生聖殿1樓請擊退該樓層的不死怪物直到取得通往2樓的鑰匙，即可前往再生聖殿2樓。<BR>
	 * 抵達再生聖殿2樓之後，直走到底會遇見高潔的意志鬥士。<BR> 擊退<font
	 * fg=0000ff>高潔的意志鬥士</font>可以獲得前往3樓的鑰匙。<BR> <BR> 注意事項：<BR>
	 * 高潔的意志戰士會隨機掉落古代之箭，供妖精使用<BR> 2樓出沒的奇諾之監視者，會掉落相當於終極治癒藥水的監視者之眼<BR> <BR>
	 * 任務目標：<BR> 取得通往下一樓層的鑰匙，直到抵達再生聖殿3樓<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.30
	 * 高潔的意志戰士</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>古代之箭 x 1</font><BR>
	 * <font fg=ffff00>聖殿2樓鑰匙 x 1</font><BR> <font fg=ffff00>聖殿3樓鑰匙 x
	 * 1</font><BR> <font fg=ffff00>監視者之眼 x 1</font><BR> <BR> <img
	 * src="#1210"></img> <font fg=66ff66>步驟.3 再生聖殿 3樓 </font><BR> <BR>
	 * 抵達再生聖殿3樓之後，擊退高潔意志的守護者可以獲得<font fg=0000ff>魔之角笛</font>。<BR>
	 * 之後尋找NPC的被遺棄的肉身和他交談會與給你<font fg=0000ff>消滅之意志</font>。<BR>
	 * 使用消滅之意志可以抵達對面的祭壇房間。<BR> 在祭壇房間的中央使用魔之角笛可以呼喚出神官奇諾。<BR>
	 * 將神官奇諾擊退之後能夠獲得破壞之秘藥。<BR> 飲用破壞之秘藥將再生祭壇擊毀，之後能獲得祭壇的碎片。<BR> <BR> 注意事項：<BR>
	 * 請務必飲用破壞之秘藥才能有效攻擊再生祭壇<BR> 四個職業必須都在副本任務中聖殿鑰匙才具備傳送能力。<BR>
	 * 四個職業其中之一死亡離開副本，其餘職業將被傳送離開。<BR> <font fg=0000ff>再生祭壇除王族外限定各職業使用指定武器攻擊。<BR>
	 * 精靈：水精靈之弓<BR> 法師：古代人的智慧<BR> 騎士：天空之劍</font><BR> <BR> 任務目標：<BR>
	 * 經過一連串的挑戰，將再生祭壇毀損後，取得毀損的證據「祭壇的碎片」<BR> <BR> 相關物品：<BR> <font fg=ffff00>消滅之意志
	 * x 1</font><BR> <font fg=ffff00>魔之角笛 x 1</font><BR> <font fg=ffff00>破壞之秘藥
	 * x 1</font><BR> <font fg=ffff00>祭壇的碎片 x 1</font><BR> <BR> <img
	 * src="#1210"></img> <font fg=66ff66>步驟.4 回報任務達成 </font><BR> <BR>
	 * 事成之後回到象牙塔3樓向迪嘉勒廷報告。<BR> 並且將祭壇的碎片交給迪嘉勒廷，即可領取獎勵。<BR> <BR> 注意事項：<BR>
	 * 交換的獎勵依照職業不同而異：<BR> 王族：黃金權杖<BR> 騎士：黑燄之劍<BR> 妖精：赤焰之弓或赤焰之劍(二選一)<BR>
	 * 法師：瑪那水晶球<BR> <BR> 任務目標：<BR> 交付祭壇的碎片給迪嘉勒廷，換取獎勵<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>黃金權杖 x 1</font><BR> <font fg=ffff00>赤焰之弓 x 1</font><BR> <font
	 * fg=ffff00>赤焰之劍 x 1</font><BR> <font fg=ffff00>黑燄之劍 x 1</font><BR> <font
	 * fg=ffff00>瑪那水晶球 x 1</font><BR> <font fg=ffff00>祭壇的碎片 x 1</font><BR> <BR>
	 */
	private CKEWLv50_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new CKEWLv50_1();
	}

	@Override
	public void execute(final L1Quest quest) {
		try {
			// 設置任務
			QUEST = quest;

			final DE50A_Timer de50ATimer = new DE50A_Timer();
			de50ATimer.start();

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
