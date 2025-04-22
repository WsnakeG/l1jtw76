package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;
import com.lineage.server.timecontroller.quest.W30_Timer;

/**
 * 說明:不死族的叛徒 (法師30級以上官方任務)
 * 
 * @author daien
 */
public class WizardLv30_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(WizardLv30_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務地圖(法師試煉地監)
	 */
	public static final int MAPID = 201;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_w30_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 不死族的背叛者 </font><BR> <BR>
	 * 前往說話之島與<font fg=0000ff>吉倫</font>對話得知，他的研究中需要<font
	 * fg=0000ff>不死族的骨頭</font>，
	 * 但此骨頭只有死亡騎士才有，但死亡騎士的身邊出現了背叛者，把死亡騎士的骨頭偷走了，你必須利用這個好機會
	 * ，幫助吉倫完成！接受此任務，並前往冒險洞穴地下一樓，獵殺背叛的骷髏警衛兵取得不死族的鑰匙，在冒險洞穴地下一樓與吉倫的徒弟<font
	 * fg=0000ff>迪隆</font>對話，他將會帶你前往不死族的地監。<BR> <BR> 注意事項：<BR>
	 * 不死族的地監允許進入一個人(無法組隊前往)<BR> <BR> 任務目標：<BR>
	 * 尋找吉倫接受任務，並獵殺背叛的骷髏警衛兵取得不死族的鑰匙，之後找迪隆前往不死族的地監<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.10 背叛的骷髏警衛兵</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>不死族的鑰匙
	 * x 1</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2 不死族的地監
	 * </font><BR> <BR> 不死族的地監必須通過四道關卡： 第一道關卡： 使用不死族的鑰匙，即可通過。 第二道關卡：
	 * 通過第一個門之後，打倒人形僵屍，就可以獲得僵屍鑰匙，即可通過第二道門。 第三道關卡：
	 * 進入第三個房間時，會發現阿魯巴，必須對他使用相消術，阿魯巴會變回骷髏，打倒牠，就可以獲得骷髏鑰匙，通過第三道門。 第四道關卡：
	 * 通過第四道門進入房間之後
	 * ，會看到3個人形僵屍分別站在3個角落，玩家必須要將妖魔僵屍打死，並且使用造屍術，將妖魔僵屍變成人形僵屍，並且讓牠到沒有怪物停留的第4個角落
	 * ，這時位在房間中間通往的門就會開啟。<BR> <BR> 注意事項：<BR>
	 * 在不死族地監的區域中，無法喝水、不能裝備武器及防具、魔力不會自動回復，但可以攜帶寵物
	 * ，不過在這裡無法召喚怪物，如果在進入地監前召喚，是可以帶進去，在其中可用魔法迷魅或抓取寵物。<BR> <BR> 任務目標：<BR>
	 * 通過4個關卡，獵取不死族背叛者取得不死族骨頭<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.6
	 * 人形殭屍</font><BR> <font fg=ffff00>Lv.10 妖魔殭屍</font><BR> <font
	 * fg=ffff00>Lv.27 不死族背叛者</font><BR> <font fg=ffff00>Lv.35 骷髏</font><BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>僵屍鑰匙 x 1</font><BR> <font fg=ffff00>骷髏鑰匙 x
	 * 1</font><BR> <font fg=ffff00>不死族骨頭 x 1</font><BR> <BR> <img
	 * src="#1210"></img> <font fg=66ff66>步驟.3 缺少的材料 </font><BR> <BR>
	 * 拿到不死族的骨頭前往說話之島與吉倫對話
	 * ，才發現，原本吉倫要交給試煉者的神秘道具缺少神秘水晶球當作材料…輾轉得知，這貴重的神秘的水晶球是由在古魯丁地下監獄7樓的歐林所販賣
	 * ，得到所有物品之後交給吉倫，可以得到神秘魔杖與不死族的骨頭碎片。<BR> <BR> 注意事項：<BR>
	 * 神秘水晶球在完成不死族的地監任務之前，請先不要購買，以免流程錯誤造成系統判斷試煉錯誤。<BR> <BR> 任務目標：<BR>
	 * 購買神秘水晶球，拿會去交給吉倫，取得神秘魔杖與骨頭材料的碎片<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>不死族的骨頭碎片 x 1</font><BR> <font fg=ffff00>神秘水晶球 x 1</font><BR>
	 * <font fg=ffff00>神秘魔杖 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.4 神秘魔杖的秘密 </font><BR> <BR> 前往象牙塔3樓拜訪<font
	 * fg=0000ff>塔拉斯</font
	 * >，得知手上的神秘魔杖並沒有完成。給他由吉倫給你的神秘魔杖與不死族的骨頭碎片，他會幫你把神秘魔杖強化，即可得到水晶魔杖。<BR> <BR>
	 * 任務目標：<BR> 將神秘魔杖與不死族的骨頭碎片交給塔拉斯，並取得獎勵<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>水晶魔杖 x 1</font><BR> <BR>
	 */
	private WizardLv30_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new WizardLv30_1();
	}

	@Override
	public void execute(final L1Quest quest) {
		try {
			// 設置任務
			QUEST = quest;

			// 任務時間軸
			final W30_Timer w30Timer = new W30_Timer();
			w30Timer.start();

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
