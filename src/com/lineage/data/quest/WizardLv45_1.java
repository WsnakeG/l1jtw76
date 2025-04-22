package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:法師的考驗 (法師45級以上官方任務)
 * 
 * @author daien
 */
public class WizardLv45_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(WizardLv45_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_w45_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 神秘的岩石</font><BR> <BR>
	 * 在變形怪出沒的鏡子森林中，突然冒出一個 <font
	 * fg=0000ff>神秘的岩石</font>，而且它還會說話，更奇妙的是它還說明了有關邪惡勢力的事情， <font
	 * fg=0000ff>塔拉斯</font
	 * >認為有必要調查神秘的岩石，因此想請玩家前往調查。到鏡子森林(33780，33278)裡尋找神秘的岩石，和他對話後得知他原來是位法師但是被變成岩石
	 * ，所以他需要你幫他找解除詛咒的兩樣物品，變形怪的血及魔法書( 魔法相消術 )。<BR> <BR> 任務目標：<BR>
	 * 與塔拉斯接受任務，前往鏡子森林與 <font fg=0000ff>神秘的岩石</font>談話，並接受任務<BR> <BR> <img
	 * src="#1210"></img> <font fg=66ff66>步驟.2 變形怪的血</font><BR> <BR> 將 <font
	 * fg=0000ff>伊娃的祝福</font>丟給 <font
	 * fg=0000ff>變形怪</font>(只出現在鏡子森林的33766.33236周圍
	 * )並將他打倒，就會得到變形怪的血。之後準備好魔法書(魔法相消術)一同交給神秘的岩石，得到古代惡魔的記載。<BR> <BR> 任務目標：<BR>
	 * 蒐集變形怪的血和魔法書(魔法相消術)，交給神秘的岩石，得到<font fg=0000ff>古代惡魔的記載</font><BR> <BR>
	 * 相關怪物：<BR> <font fg=ffff00>Lv.45 變形怪</font><BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>變形怪的血 x 1</font><BR> <font fg=ffff00>古代惡魔的記載 x 1</font><BR>
	 * <font fg=ffff00>魔法書(魔法相消術) x 1</font><BR> <BR> <img src="#1210"></img>
	 * <font fg=66ff66>步驟.3 回報塔拉斯</font><BR> <BR>
	 * 回到象牙塔3樓後將調查結果的古代惡魔的記載交給塔拉斯後，得到塔拉斯的魔法袋，點二下後得到瑪那斗篷及古代人的智慧。<BR> <BR>
	 * 任務目標：<BR> 將古代惡魔的記載交給塔拉斯，並領取獎勵<BR> <BR> 相關物品：<BR> <font fg=ffff00>塔拉斯的魔法袋
	 * x 1</font><BR> <font fg=ffff00>古代人的智慧 x 1</font><BR> <font fg=ffff00>瑪那斗篷
	 * x 1</font><BR> <BR>
	 */
	private WizardLv45_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new WizardLv45_1();
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
