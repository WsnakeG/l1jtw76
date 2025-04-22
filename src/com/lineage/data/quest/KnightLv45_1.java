package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 騎士的證明 (騎士45級以上官方任務)
 * 
 * @author daien
 */
public class KnightLv45_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(KnightLv45_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_k45_1";

	/**
	 * 調查員-巨人
	 */
	public static final int _searcherid = 71092;

	/**
	 * 調查員
	 */
	public static final int _searcher2id = 71093;

	/**
	 * 公爵的士兵
	 */
	public static final int _guardid = 70740;

	/*
	 * <img src="#1210"></img> <font fg=ffff66>步驟.1 夜之視野：</font><BR> <BR> <font
	 * fg=0000ff>馬沙</font>派出的<font
	 * fg=0000ff>調查員</font>下落不明，據說最後一次調查員傳來消息的地方為<font
	 * fg=0000ff>黃昏山脈</font>，那是許多可怕強大的巨人聚集地， 為了要救回調查員，馬沙將委託勇敢的騎士前往。<BR>
	 * 前往威頓村找馬沙(33713，32504)接下尋找調查員的任務。之後到黃昏山脈找<font
	 * fg=0000ff>志武</font>(34259，33341)說話，得知要看到巨人守護神必需要有黎明森林的<font
	 * fg=0000ff>強盜頭目</font>(33755，32742周圍)擁有的<font
	 * fg=0000ff>夜之視野</font>。打倒強盜頭目後會獲得夜之視野。<BR> <BR> 任務目標：<BR>
	 * 與馬沙接受任務，與志武對話，獵殺強盜頭目取得夜之視野<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.20
	 * 強盜頭目</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>夜之視野 x 1</font><BR> <BR>
	 * <img src="#1210"></img> <font fg=ffff66>步驟.2 巨人守護神：</font><BR> <BR>
	 * 到達黃昏山脈找<font fg=0000ff>巨人長老</font>(34242，33404)說話之後，他會要求你將<font
	 * fg=0000ff>
	 * 巨人守護神</font>身上的物品交給他。裝備夜之視野後到黃昏山脈找巨人守護神(34248，33363)，打倒他後獲得<font
	 * fg=0000ff>守護神之袋</font>，點二下後會得到破損的調查簿、天空之劍及古代的遺物。將古代的遺物交給巨人長老後，
	 * 得到古代鑰匙並得知調查員被巨人守護神變成巨人的模樣。<BR> <BR> 注意事項：<BR> 巨人守護神必須裝備夜之視野才看的見<BR>
	 * 天空之劍關係到50級試煉，所以千萬不可刪除<BR> <BR> 任務目標：<BR>
	 * 與巨人長老接受任務，擊敗巨人守護神取得守護神之袋，回去交差並探聽消息<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.40 巨人守護神</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>破損的調查簿 x
	 * 1</font><BR> <font fg=ffff00>古代的遺物 x 1</font><BR> <font
	 * fg=ffff00>古代鑰匙(上半部) x 1</font><BR> <font fg=ffff00>天空之劍 x 1</font><BR>
	 * <font fg=ffff00>守護神之袋 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=ffff66>步驟.3 帶回調查員：</font><BR> <BR>
	 * 找到調查員(34242，33356)後請一位有相消術的法師或妖精幫他相消變回人類的樣貌後
	 * ，他會要求你帶他到歐瑞村的商店西方公爵的士兵(34043，32234)旁。路上他會一直跟著你走，到達後他會給你調查簿的缺頁。 <BR>
	 * 回到威頓村後將尋找調查員的結果(破損的調查簿、調查簿的缺頁及夜之視野)交給馬沙後，得到勇敢皮帶。 <BR> <BR> 任務目標：<BR>
	 * 找到調查員，使用魔法相消術將其會復原狀，並帶回公爵的士兵那，之後回去跟馬沙交差，取得獎勵。<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>調查簿的缺頁 x 1</font><BR> <font fg=ffff00>勇敢皮帶 x 1</font><BR> <BR>
	 */
	private KnightLv45_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new KnightLv45_1();
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
