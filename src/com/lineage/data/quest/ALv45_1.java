package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:毒蛇之牙的名號(全職業45級任務)
 * 
 * @author daien
 */
public class ALv45_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ALv45_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_a45_1";

	/*
	 * <body> <img src="#1210"></img> <font fg=66ff66>步驟.1 取得騎士團的信任</font><BR>
	 * <BR> 前往海音村找傭兵團長多文(33592 33230)對話，他會要玩家前往調查海音地監惡靈騷動的原因。<BR>
	 * 到海音地監1樓~3樓，狩獵海音的惡靈，蒐集5個亡者的信件。<BR> 將亡者的信件帶回去交給傭兵團長多文，可以獲得傭兵團長多文的推薦書。<BR>
	 * <BR> 注意事項：<BR> ※要重複該任務身上不能有毒蛇之牙披肩<BR> <BR> 任務目標：<BR>
	 * 完成傭兵團長多文的委託，取得傭兵團長多文的推薦書，好跟騎士團長帝倫接洽任務<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.25 海音的惡靈</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>亡者的信件 x
	 * 5</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2
	 * 騎士團的試煉</font><BR> <BR> 前往海音村旅館旁找騎士團長帝倫(33590 33280)對話，將傭兵團長多文的推薦書交給他。<BR>
	 * 騎士團長帝倫會跟玩家說，需要花100萬或是用梅杜莎之血證明實力來換取教本。<BR> 到海音地監2樓，狩獵受詛咒的
	 * 梅杜莎，可以獲得梅杜莎之血。<BR> 將100萬或梅杜莎之血交給騎士團長帝倫，可以獲得帝倫之教本。<BR> <BR> 任務目標：<BR>
	 * 完成騎士團長帝倫的委託，取得梅杜莎之血，好跟傭兵團長多文進行下一階段任務<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.36 受詛咒的 梅杜莎</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>金幣 x
	 * 1000000</font><BR> <font fg=ffff00>梅杜莎之血 x 1</font><BR> <font
	 * fg=ffff00>帝倫之教本 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 訓練騎士的資格(1)</font><BR> <BR> 前往海音村找傭兵團長多文(33592
	 * 33230)對話，將帝倫之教本交給他。<BR> 傭兵團長多文會跟玩家要求取得法利昂的血痕，來當作加入訓練騎士的資格證明。<BR>
	 * 狩獵海音地監的怪物，可以獲得法利昂的血痕。<BR> 將法利昂的血痕交給傭兵團長多文，可以獲得訓練騎士披肩 (1)。<BR> <BR>
	 * 任務目標：<BR> 將帝倫之教本交給傭兵團長多文，取得法利昂的血痕換取訓練騎士披肩 (1)<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.20 受詛咒的 蟑螂人</font><BR> <font fg=ffff00>Lv.22 受詛咒的
	 * 穴居人</font><BR> <font fg=ffff00>Lv.24 受詛咒的 巨鼠</font><BR> <font
	 * fg=ffff00>Lv.28 受詛咒的 鼠人</font><BR> <font fg=ffff00>Lv.30 受詛咒的
	 * 多眼怪</font><BR> <font fg=ffff00>Lv.32 受詛咒的 蛇女 (藍)</font><BR> <font
	 * fg=ffff00>Lv.32 受詛咒的 蛇女 (綠)</font><BR> <font fg=ffff00>Lv.35 受詛咒的
	 * 蜥蜴人</font><BR> <font fg=ffff00>Lv.36 受詛咒的 梅杜莎</font><BR> <font
	 * fg=ffff00>Lv.42 受詛咒的 人魚</font><BR> <font fg=ffff00>Lv.45 水之精靈</font><BR>
	 * <font fg=ffff00>Lv.45 受詛咒的 水精靈王</font><BR> <font fg=ffff00>Lv.50
	 * 卡普</font><BR> <font fg=ffff00>Lv.65 巨大蜈蚣</font><BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>訓練騎士披肩 (1) x 1</font><BR> <font fg=ffff00>法利昂的血痕 x 1</font><BR>
	 * <BR> <img src="#1210"></img> <font fg=66ff66>步驟.4 訓練騎士的資格(2)</font><BR>
	 * <BR> 傭兵團長多文會跟玩家要求取得水中的水，來當作訓練騎士的升級試煉。<BR> 到海音地監 4樓(海底)狩獵水之精靈
	 * ，可以獲得水中的水。<BR> 將水中的水交給傭兵團長多文，可以獲得訓練騎士披肩 (2)。<BR> <BR> 任務目標：<BR>
	 * 狩獵水之精靈，取得水中的水跟傭兵團長多文換取訓練騎士披肩 (2)<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.45
	 * 水之精靈</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>訓練騎士披肩 (2) x 1</font><BR>
	 * <font fg=ffff00>水中的水 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.5 毒蛇之牙的名號</font><BR> <BR>
	 * 傭兵團長多文會跟玩家要求取得酸性液體，來當作取得毒蛇之牙名號的證明。<BR> 到海音地監 4樓(海底)狩獵巨大蜈蚣 ，可以獲得酸性液體。<BR>
	 * 將酸性液體交給傭兵團長多文，可以獲得毒蛇之牙披肩，完成取得毒蛇之牙名號的任務。<BR> <BR> 任務目標：<BR>
	 * 狩獵巨大蜈蚣，取得酸性液體跟傭兵團長多文換取毒蛇之牙披肩，完成任務<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.65 巨大蜈蚣</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>毒蛇之牙披肩 x
	 * 1</font><BR> <font fg=ffff00>酸性液體 x 1</font><BR> <BR> <br> <img
	 * src="#331" action="index"> </body>
	 */
	private ALv45_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ALv45_1();
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
