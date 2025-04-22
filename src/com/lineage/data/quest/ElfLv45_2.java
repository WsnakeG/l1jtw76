package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:妖精族傳說中的弓 (妖精45級以上官方任務)
 * 
 * @author daien
 */
public class ElfLv45_2 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ElfLv45_2.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_e45_2";

	/**
	 * 獨角獸
	 */
	public static final int _npcId = 91123;

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 套出製作秘方 </font><BR> <BR>
	 * 到妖精森林尋找「<font fg=0000ff>羅賓孫</font>(33031 32344)
	 * 」，跟他詢問妖精族傳說中的弓，會很不想理你。<BR> 之後準備1瓶蘋果汁給他套出<font
	 * fg=0000ff>熾炎天使弓</font>的製作方法與材料，即可獲得「<font
	 * fg=0000ff>羅賓孫的推薦書</font>」和「<font fg=0000ff>羅賓孫的便條紙</font>」。<BR> <BR>
	 * 注意事項：<BR> 第一階段製作材料內容：<BR> 神聖獨角獸之角 4個 (夢幻之島)<BR> 火之氣息 30個 (拉斯塔巴德)<BR> 水之氣息
	 * 30個 (拉斯塔巴德)<BR> 土之氣息 30個 (拉斯塔巴德)<BR> 風之氣息 30個 (拉斯塔巴德)<BR> 闇之氣息 30個
	 * (拉斯塔巴德)<BR> 精靈之淚 20個 (妖精森林洞穴)<BR> 月光之氣息 1個 (伊娃神殿)<BR> <BR> 任務目標：<BR>
	 * 找到羅賓孫，並給他一瓶蘋果汁給他套出製作秘方<BR> <BR> 相關物品：<BR> <font fg=ffff00>蘋果 汁 x
	 * 1</font><BR> <font fg=ffff00>羅賓孫的推薦書 x 1</font><BR> <font
	 * fg=ffff00>羅賓孫的便條紙 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.2 尋求月光之氣息 </font><BR> <BR> 到海音地監的伊娃神殿尋找「<font
	 * fg=0000ff>神官知布烈</font>(32740 32800)」請求<font
	 * fg=0000ff>月光之氣息</font>，但他告知需先解救受水龍詛咒的<font fg=0000ff>巫女莎爾</font>。<BR>
	 * 並且神官知布烈告訴你要有<font
	 * fg=0000ff>伊娃的聖水</font>才能解救巫女莎爾，而製作伊娃的聖水需要伊娃短劍和精靈之淚10顆。<BR>
	 * 當蒐集好伊娃短劍和精靈之淚10顆並交給神官知布烈，即可獲得伊娃的聖水。<BR> <BR> 注意事項：<BR> 伊娃短劍製作材料內容：<BR>
	 * 品質鑽石 10個<BR> 品質紅寶石 10個<BR> 品質藍寶石 10個<BR> 品質綠寶石 10個<BR> <BR>
	 * 伊娃的聖水製作材料內容：<BR> 伊娃短劍 1個<BR> 精靈之淚 10個<BR> <BR> 任務目標：<BR>
	 * 到伊娃神殿找神官知布烈，並蒐集材料製作伊娃的聖水<BR> <BR> 相關物品：<BR> <font fg=ffff00>品質鑽石 x
	 * 10</font><BR> <font fg=ffff00>品質紅寶石 x 10</font><BR> <font fg=ffff00>品質藍寶石
	 * x 10</font><BR> <font fg=ffff00>品質綠寶石 x 10</font><BR> <font
	 * fg=ffff00>精靈之淚 x 10</font><BR> <font fg=ffff00>伊娃的聖水 x 1</font><BR> <font
	 * fg=ffff00>伊娃短劍 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 解救巫女莎爾 </font><BR> <BR> 到海音地監4樓的海底去尋找「<font
	 * fg=0000ff>受詛咒的巫女莎爾</font>」，並在攻擊他之前先飲用伊娃的聖水才能解救巫女莎爾。<BR>
	 * 擊敗受詛咒的巫女莎爾，可獲得解救成功的證明「<font fg=0000ff>莎爾之戒</font>」。<BR>
	 * 將莎爾之戒拿回去給伊娃神殿的神官知布烈，即可獲得月光之氣息。<BR> <BR> 注意事項：<BR>
	 * 在攻擊受詛咒的巫女莎爾之前，請務必先飲用伊娃的聖水<BR> <BR> 任務目標：<BR>
	 * 到海底擊敗受詛咒的巫女莎爾，並將獲得的莎爾之戒交給神官知布烈，即可獲得月光之氣息<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.43 受詛咒的巫女莎爾</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>月光之氣息
	 * x 1</font><BR> <font fg=ffff00>伊娃的聖水 x 1</font><BR> <BR> <img
	 * src="#1210"></img> <font fg=66ff66>步驟.4 後續的製作材料 </font><BR> <BR>
	 * 在蒐集完第一階段製作材料之後，回去妖精森林找羅賓孫，他會給你「<font
	 * fg=0000ff>羅賓孫之戒</font>」以及寫有第二批製作材料的「<font fg=0000ff>羅賓孫的便條紙</font>」。<BR>
	 * 將第二批材料蒐集齊全並交給羅賓孫，即可獲得熾炎天使弓。<BR> <BR> 注意事項：<BR> 第二階段製作材料內容：<BR> 格利芬羽毛
	 * 30個<BR> 米索莉線 40個<BR> 覆上奧里哈魯根的角 1個<BR> 奧里哈魯根金屬板 12個<BR> 高品質鑽石 1個<BR>
	 * 高品質紅寶石 1個<BR> 高品質藍寶石 1個<BR> 高品質綠寶石 1個<BR> <BR> 任務目標：<BR>
	 * 將第一批材料交給羅賓孫，之後開始蒐集第二批材料並交給羅賓孫，即可獲得熾炎天使弓<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>高品質鑽石 x 1</font><BR> <font fg=ffff00>高品質紅寶石 x 1</font><BR>
	 * <font fg=ffff00>高品質藍寶石 x 1</font><BR> <font fg=ffff00>高品質綠寶石 x
	 * 1</font><BR> <font fg=ffff00>米索莉線 x 40</font><BR> <font fg=ffff00>格利芬羽毛 x
	 * 30</font><BR> <font fg=ffff00>奧里哈魯根金屬板 x 12</font><BR> <font
	 * fg=ffff00>覆上奧里哈魯根的角 x 1</font><BR> <font fg=ffff00>熾炎天使弓 x 1</font><BR>
	 * <font fg=ffff00>羅賓孫的便條紙 x 1</font><BR> <font fg=ffff00>羅賓孫之戒 x
	 * 1</font><BR> <BR>
	 */
	private ElfLv45_2() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ElfLv45_2();
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
		try {
			// 任務解除必須移除的道具
			final int[] delItemIds = new int[] { 41348, // 羅賓孫的推薦書
					41346, // 羅賓孫的便條紙 1
					41347, // 羅賓孫的便條紙 2
					41354, // 伊娃的聖水
					41353, // 伊娃短劍
					41349, // 莎爾之戒
					41351, // 月光之氣息
					41350,// 羅賓孫之戒
			};

			for (final int delItemId : delItemIds) {
				pc.getInventory().delQuestItem(delItemId);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
