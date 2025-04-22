package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:同族的背叛 (黑暗妖精30級以上官方任務)
 * 
 * @author daien
 */
public class DarkElfLv30_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(DarkElfLv30_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_d30_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 憤怒的倫得</font><BR> <BR>
	 * 到達30級後就可以去尋找位在沉默洞穴的<font
	 * fg=0000ff>倫得</font>(32862，32891)，對話之後得知他無法原諒那些與反王合作的黑暗妖精
	 * ，所以他會麻煩幫他帶回那些與反王合作的秘密名單，而這份名單流落在一位<font fg=0000ff>叛逃的刺客警衛</font>手中。<BR>
	 * 隨後到燃柳村左邊的邪惡神殿附近繞繞
	 * ，即可找到叛逃的刺客警衛，殺了他之後即可得到秘密名單。回去找倫得並把秘密名單交給他，他會給你暗殺名單之袋，並會請你殺了那些背叛者
	 * ，並帶回死亡之證來。 <BR> <BR> 任務目標：<BR> 與倫得接受任務，狩獵叛逃的刺客警衛取得秘密名單，回去交差繼續接受任務<BR>
	 * <BR> 相關怪物：<BR> <font fg=ffff00>Lv.28 叛逃的刺客警衛</font><BR> <BR> 相關物品：<BR>
	 * <font fg=ffff00>秘密名單 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.2 獵殺背叛者</font><BR> <BR> 在打開暗殺名單之袋，會取得七個暗殺名單，分別要到<font
	 * fg=0000ff>古魯丁村</font>(32620，32641)、<font
	 * fg=0000ff>燃柳村</font>(32730，32426)、<font
	 * fg=0000ff>肯特村</font>(33046，32806)、<font
	 * fg=0000ff>風木村</font>(32580，33260)、<font
	 * fg=0000ff>海音都市</font>(33447，33476)、<font
	 * fg=0000ff>亞丁城鎮</font>(34215，33195)及<font
	 * fg=0000ff>奇岩村</font>(33513，32890)去找尋暗殺的對象。<BR> 取得<font
	 * fg=0000ff>死亡之證</font>之後，回去找倫得，並把死亡誓約交給他，即可得到倫得之袋。打開倫得之袋，
	 * 即可得到黑暗精靈水晶(行走加速)和影子手套。再次與倫得對話，得知等到自己的能力成熟時就可以去尋找布魯迪卡的幫助。 <BR> <BR>
	 * 注意事項：<BR> 進行暗殺動作前必須先接受倫得施放的古代咒術。<BR>
	 * 在村莊各處尋找刺客時，需在魔法陣上使用暗殺名單才能將這些背叛者找出來。<BR>
	 * 咒術為暫時性的，因此當失去效力時，必須再回去接受倫得施放的古代咒術。<BR> <BR>
	 * 在殺完所有的背叛者之後，就會自動取得死亡誓約。由於背叛者並不會做抵抗，所以對付他們並不困難。<BR> <BR> 任務目標：<BR>
	 * 接受倫得施放的古代咒術，並馬上去狩獵投靠反王的黑暗妖精取得死亡誓約，回去交差取得獎勵<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.1 投靠反王的黑暗妖精</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>倫得之袋 x
	 * 1</font><BR> <font fg=ffff00>死亡誓約 x 1</font><BR> <font fg=ffff00>影子手套 x
	 * 1</font><BR> <font fg=ffff00>黑暗精靈水晶(行走加速) x 1</font><BR> <BR>
	 */
	private DarkElfLv30_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new DarkElfLv30_1();
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
