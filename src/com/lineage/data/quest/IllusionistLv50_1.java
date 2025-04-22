package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:時空彼端的線索 (幻術士50級以上官方任務)
 * 
 * @author daien
 */
public class IllusionistLv50_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(IllusionistLv50_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務地圖(異界奎斯特)
	 */
	public static final int MAPID = 2004;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_i50_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 長老賦予的任務 </font><BR> <BR>
	 * 前往希培莉亞找<font fg=0000ff>長老希蓮恩</font>(32772
	 * 32811)對話，他會要玩家前往調查時空裂痕頻繁出現的原因。<BR> 之後會給你<font
	 * fg=0000ff>希蓮恩的第五次信件</font>，並且要玩家蒐集100個<font
	 * fg=0000ff>時空裂痕碎片</font>過來給他。<BR> <BR> 注意事項：<BR>
	 * 時空裂痕(底比斯)和時空裂痕(提卡爾)，在這兩處狩獵怪物皆可蒐集到時空裂痕碎片<BR> <BR> 任務目標：<BR>
	 * 跟長老希蓮恩接洽任務，並前往時空裂痕<BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2
	 * 異界奎斯特 </font><BR> <BR> 湊齊100個時空裂痕碎片並交給長老希蓮恩，可以獲得<font
	 * fg=0000ff>時空裂痕邪念碎片</font>、<font fg=0000ff>希蓮恩的護身符</font>、<font
	 * fg=0000ff>希蓮恩的第六次信件</font>。<BR> 再來只要使用時空裂痕邪念碎片即可前往異界奎斯特。<BR> <BR>
	 * 注意事項：<BR> 當想要離開異界奎斯特，只需要使用希蓮恩的護身符即可，並且時空裂痕邪念碎片、希蓮恩的護身符可以重複和長老希蓮恩索取<BR>
	 * <BR> 任務目標：<BR> 將100個時空裂痕碎片交給長老希蓮恩，並且前往異界奎斯特<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>時空裂痕邪念碎片 x 1</font><BR> <font fg=ffff00>時空裂痕碎片 x 100</font><BR>
	 * <font fg=ffff00>希蓮恩的護身符 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 藍色靈魂之火</font><BR> <BR> 在異界奎斯特調查尋找<font
	 * fg=0000ff>藍色靈魂之火</font>，得知要求給予<font fg=0000ff>食腐獸之血</font>5個、<font
	 * fg=0000ff>翼龍之血</font>5個。<BR> 之後狩獵食腐獸和翼龍湊齊需求品交給<font
	 * fg=0000ff>藍色靈魂之火</font>，可以獲得<font fg=0000ff>靈魂之火灰燼</font>、<font
	 * fg=0000ff>藍色之火碎片</font>。<BR> <BR> 任務目標：<BR>
	 * 滿足藍色靈魂之火的需求，取得靈魂之火灰燼、藍色之火碎片<BR> <BR> 相關物品：<BR> <font fg=ffff00>靈魂之火灰燼 x
	 * 1</font><BR> <font fg=ffff00>食腐獸之血 x 5</font><BR> <font fg=ffff00>翼龍之血 x
	 * 5</font><BR> <font fg=ffff00>藍色之火碎片 x 1</font><BR> <BR> <img
	 * src="#1210"></img> <font fg=66ff66>步驟.4 吉爾塔斯的部下 </font><BR> <BR>
	 * 在異界奎斯特使用<font fg=0000ff>藍色之火碎片</font>，吉爾塔斯的部下<font
	 * fg=0000ff>塞維斯</font>會現身。<BR> 打倒塞維斯，可以獲得<font
	 * fg=0000ff>塞維斯邪念碎片</font>。<BR> <BR> 任務目標：<BR> 擊敗塞維斯，並取得塞維斯邪念碎片<BR> <BR>
	 * 相關怪物：<BR> <font fg=ffff00>Lv.46 塞維斯</font><BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>塞維斯邪念碎片 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.5 回報 </font><BR> <BR> 將塞維斯邪念碎片帶回去給長老希蓮恩，即可獲得<font
	 * fg=0000ff>希蓮恩的指令書</font>、<font fg=0000ff>特別的原石</font>。<BR>
	 * 將希蓮恩的指令書、特別的原石交給<font fg=0000ff>鐵匠 巴特爾</font>(32803 32812)，即可獲得<font
	 * fg=0000ff>藍寶石奇古獸</font>。<BR> <BR> 任務目標：<BR> 將塞維斯邪念碎片交給長老希蓮恩，並獲得獎勵<BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>藍寶石奇古獸 x 1</font><BR> <font
	 * fg=ffff00>希蓮恩的指令書 x 1</font><BR> <font fg=ffff00>特別的原石 x 1</font><BR>
	 * <BR>
	 */
	private IllusionistLv50_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new IllusionistLv50_1();
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
