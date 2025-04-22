package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 王族的信念 (王族45級以上官方任務)
 * 
 * @author daien
 */
public class CrownLv45_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(CrownLv45_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_c45_1";

	/*
	 * <img src="#1210"></img> <font fg=ffff66>步驟.1 遺失的信念 ：</font><BR> <BR>
	 * 很久之前的亞丁王國有個只傳給 王族的王族徽章，但後來卻被反 王肯恩給破壞遺失， 馬沙深信王 族徽章是反抗反王肯恩的一種信
	 * 念，想要對抗強大勢力的反王肯 恩就必須尋找出王族徽章。<BR> <BR> 前往威頓村尋找<font
	 * fg=0000ff>馬沙</font>(33713 32504) ，他會要求你尋找王族徽章，並 請你找肯特村的<font
	 * fg=0000ff>李察</font>(33078 32765) ，只有他知道王族徽章的下落。<BR> <BR> 任務目標：<BR>
	 * 與馬沙接受任務<BR> <BR> <img src="#1210"></img> <font fg=ffff66>步驟.2 徽章的下落
	 * ：</font><BR> <BR> 到達肯特村找<font fg=0000ff>李察</font>(33078 32765)
	 * 後，得知其中一塊王族徽章的碎 片在眠龍洞穴三樓裡的背叛的妖 魔隊長手中。到眠龍洞穴三樓找 到背叛的妖魔隊長後，打死他可
	 * 獲得王族徽章的碎片。<BR> 回到肯特後<font fg=0000ff>李察</font>(33078 32765) 會告訴你，風木村的麥知道另一
	 * 塊王族徽章的碎片的下落。<BR> <BR> 任務目標：<BR> 與李察探聽消息，擊敗背叛的妖 魔隊長取得王族徽章的碎片，回
	 * 去與李察探聽新消息<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.14 背叛的妖魔巡守</font><BR>
	 * <font fg=ffff00>Lv.18 背叛的妖魔隊長</font><BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>王族徽章的碎片 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=ffff66>步驟.3 另一片徽章 ：</font><BR> <BR> 在風木村遇見<font
	 * fg=0000ff>麥</font>，他會告訴你先 幫忙解決象牙塔發生的靈魂狩獵 問題才肯給你另一塊王族徽章的 碎片。<BR> <BR>
	 * 到象牙塔3樓找到白魔法師皮爾塔後，他會要你去解放被靈魂的 獵食者奪取的靈魂。到象牙塔8 樓找到靈魂的獵食者後，打死他 可獲得失去光明的靈魂，拿去給
	 * 白魔法師皮爾塔可以獲得神秘的 袋子。<BR> <BR> 將神秘的袋子點二下後會出現三 個靈魂水晶，將 <font
	 * fg=0000ff>靈魂水晶（紅色）交給騎士</font>、 <font fg=0000ff>靈魂水晶（白色）交給妖精</font>、 <font
	 * fg=0000ff>靈魂水晶（黑色）交給法師使用</font> ，使用會死亡並下降經驗值，使 用後可分別得到 <font
	 * fg=0000ff>靈魂之證（紅色）</font>、 <font fg=0000ff>靈魂之證（白色）</font>、 <font
	 * fg=0000ff>靈魂之證（黑色）</font>， 再將三個靈魂之證由王族交給風 木村的<font
	 * fg=0000ff>麥</font>，他會給你另一塊王族 徽章的碎片。<BR> <BR> 注意事項：<BR> 注意：<BR> <font
	 * fg=ffff00>靈魂水晶(紅色)：騎士</font><BR> <font fg=ffff00>靈魂水晶(白色)：妖精</font><BR>
	 * <font fg=ffff00>靈魂水晶(黑色)：法師</font><BR> <BR> 任務目標：<BR> 與麥探聽消息，先去解決象牙塔
	 * 的靈魂狩獵問題，照上述方法取 得靈魂之證，拿回去給麥換取徽 章<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.10
	 * 靈魂的獵食者</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>靈魂水晶(白) x 1</font><BR>
	 * <font fg=ffff00>靈魂之證(白) x 1</font><BR> <font fg=ffff00>神秘的袋子 x
	 * 1</font><BR> <font fg=ffff00>王族徽章的碎片 x 1</font><BR> <font
	 * fg=ffff00>靈魂水晶(紅) x 1</font><BR> <font fg=ffff00>靈魂水晶(黑) x 1</font><BR>
	 * <font fg=ffff00>靈魂水晶(黑) x 1</font><BR> <font fg=ffff00>靈魂之證(紅) x
	 * 1</font><BR> <font fg=ffff00>靈魂之證(黑) x 1</font><BR> <font
	 * fg=ffff00>失去光明的靈魂 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=ffff66>步驟.4 回報 ：</font><BR> <BR> 將二塊王族徽章的碎片交給威頓 村的<font
	 * fg=0000ff>馬沙</font>，將獲得守護者的戒指 ，此時完成45級試煉，血盟系統 會自動轉成聯盟系統，聯盟君主
	 * 可以多加收聯盟成員，並給予聯 盟成員職位名稱，且新增聯盟頻 道可使用。<BR> <BR> 任務目標：<BR>
	 * 將兩塊王族徽章的碎片交給馬沙，換取獎勵。<BR> <BR> 相關物品：<BR> <font fg=ffff00>守護者的戒指 x
	 * 1</font><BR> <BR>
	 */

	private CrownLv45_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new CrownLv45_1();
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
