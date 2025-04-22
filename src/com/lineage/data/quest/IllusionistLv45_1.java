package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:艾爾摩戰場的軌跡 (幻術士30級以上官方任務)
 * 
 * @author daien
 */
public class IllusionistLv45_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(IllusionistLv45_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_i45_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 長老賦予的任務 </font><BR> <BR>
	 * 前往希培莉亞找<font fg=0000ff>長老希蓮恩</font>(32772,
	 * 32811)對話，他會要玩家前往調查久違的白蟻復出的原因。<BR> 之後長老希蓮恩會給予<font
	 * fg=0000ff>希蓮恩的第三次信件</font>和<font fg=0000ff>希蓮恩之袋</font>。<BR>
	 * 打開長老所給的袋子獲得<font fg=0000ff>風木村莊瞬間移動卷軸</font>、3個<font
	 * fg=0000ff>時空裂痕水晶(綠色)</font>。<BR> <BR> 任務目標：<BR> 跟長老希蓮恩接洽任務，並探聽任務地點<BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>時空裂痕水晶(綠色) x 3</font><BR> <font
	 * fg=ffff00>風木村莊瞬間移動卷軸 x 1</font><BR> <font fg=ffff00>希蓮恩之袋 x 1</font><BR>
	 * <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2 白蟻的屍體 </font><BR> <BR>
	 * 前往沙漠的巨蟻洞尋找3具白蟻的屍體(32785, 32748)(32808, 32725)(32728, 32782)，並且對其使用<font
	 * fg=0000ff>時空裂痕水晶(綠色)</font>。<BR> 之後可以獲得<font
	 * fg=0000ff>第一次記憶碎片</font>、<font fg=0000ff>第二次記憶碎片</font>、<font
	 * fg=0000ff>第三次記憶碎片</font>。<BR> 將這3塊記憶碎片帶回去給長老希蓮恩，之後會再給玩家們3個<font
	 * fg=0000ff>時空裂痕水晶(藍色)</font>。<BR> 並命令玩家前往火龍窟調查。<BR> <BR> 任務目標：<BR>
	 * 在巨蟻洞湊齊3塊記憶碎片，之後和長老希蓮恩換取3個時空裂痕水晶(藍色)<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>第一次記憶碎片 x 1</font><BR> <font fg=ffff00>第二次記憶碎片 x 1</font><BR>
	 * <font fg=ffff00>第三次記憶碎片 x 1</font><BR> <font fg=ffff00>時空裂痕水晶(藍色) x
	 * 3</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.3 白蟻的蹤跡
	 * </font><BR> <BR> 前往火龍窟尋找<font fg=0000ff>白蟻的痕跡(土壤)</font>(33695,
	 * 32422)、<font fg=0000ff>白蟻的痕跡(酸性液)</font>(33769, 32348)、<font
	 * fg=0000ff>白蟻的痕跡(蛋殼)</font>(33796, 32431)。<BR> 之後對著這些地方使用<font
	 * fg=0000ff>時空裂痕水晶(藍色)</font>，可以獲得<font fg=0000ff>第一次邪念碎片</font>、<font
	 * fg=0000ff>第二次邪念碎片</font>、<font fg=0000ff>第三次邪念碎片</font>。<BR> <BR>
	 * 任務目標：<BR> 在火龍窟湊齊3塊邪念碎片<BR> <BR> 相關物品：<BR> <font fg=ffff00>第一次邪念碎片 x
	 * 1</font><BR> <font fg=ffff00>第二次邪念碎片 x 1</font><BR> <font
	 * fg=ffff00>第三次邪念碎片 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.4 時空裂痕 </font><BR> <BR> 先使用<font
	 * fg=0000ff>第一次邪念碎片</font>、<font fg=0000ff>第二次邪念碎片</font>，組合出<font
	 * fg=0000ff>未完成的時間水晶球</font>。<BR> 再使用<font fg=0000ff>第三次邪念碎片</font>、<font
	 * fg=0000ff>未完成的時間水晶球</font>，組合出<font fg=0000ff>完整的時間水晶球</font>。<BR>
	 * 於火龍窟使用完整的時間水晶球，可以打開一個<font fg=0000ff>時空裂痕</font>，將其打碎可獲得<font
	 * fg=0000ff>時空裂痕邪念碎片</font>。<BR> <BR> 注意事項：<BR>
	 * 組合時間水晶球請一定要依照順序，以免出錯，造成無法繼續進行任務<BR> <BR> 任務目標：<BR>
	 * 呼喚出時空裂痕，並打碎取得時空裂痕邪念碎片<BR> <BR> 相關物品：<BR> <font fg=ffff00>未完成的時間水晶球 x
	 * 1</font><BR> <font fg=ffff00>時間水晶球 x 1</font><BR> <font
	 * fg=ffff00>時空裂痕邪念碎片 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.5 回報 </font><BR> <BR> 將時空裂痕邪念碎片帶回去給長老希蓮恩，即可獲得<font
	 * fg=0000ff>幻術士斗篷</font>。<BR> <BR> 任務目標：<BR> 將時空裂痕邪念碎片交給長老希蓮恩，並獲得獎勵<BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>幻術士斗篷 x 1</font><BR> <BR>
	 */
	private IllusionistLv45_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new IllusionistLv45_1();
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
