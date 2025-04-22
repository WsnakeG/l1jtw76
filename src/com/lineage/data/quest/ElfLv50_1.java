package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:協助間諜大逃亡 (妖精50級以上官方任務)
 * 
 * @author daien
 */
public class ElfLv50_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ElfLv50_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務地圖(賽菲亞地監)
	 */
	public static final int MAPID = 302;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_e50_1";

	/**
	 * 迪嘉勒廷的女間諜
	 */
	public static final int _npcId = 80012;

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 迪嘉勒廷的委託 </font><BR> <BR>
	 * 到象牙塔 3樓尋找<font fg=0000ff>迪嘉勒廷</font>，他會委託玩家們到沙漠的巨蟻洞，調查出沒的<font
	 * fg=0000ff>巨蟻</font>和<font fg=0000ff>巨大兵蟻</font>。<BR> <BR> 注意事項：<BR>
	 * 進行該試煉之前，必須已經完成15級、30級和45級的試煉<BR> <BR> 任務目標：<BR>
	 * 跟迪嘉勒廷接洽任務，並前往巨蟻洞狩獵巨蟻和巨大兵蟻<BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.2 古代黑妖之秘笈 </font><BR> <BR> 在狩獵巨蟻和巨大兵蟻獲得<font
	 * fg=0000ff>古代黑妖之秘笈</font>之後，即可回到象牙塔 3樓尋找迪嘉勒廷。<BR>
	 * 並將巨蟻和巨大兵蟻轉交給迪嘉勒廷，但是由於線索不足。<BR> 之後迪嘉勒廷會委託玩家繼續前往大洞穴，幫助潛伏的間諜逃亡。<BR> <BR>
	 * 任務目標：<BR> 將古代黑妖之秘笈交給迪嘉勒廷，並探聽下一個任務<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.12 巨蟻</font><BR> <font fg=ffff00>Lv.20 巨大兵蟻</font><BR> <BR>
	 * 相關物品：<BR> <font fg=ffff00>古代黑妖之秘笈 x 1</font><BR> <BR> <img
	 * src="#1210"></img> <font fg=66ff66>步驟.3 協助間諜大逃亡 </font><BR> <BR>
	 * 在大洞穴抵抗軍通往古代巨人之墓的傳送點附近可以找到<font fg=0000ff>迪嘉勒廷的女間諜</font>(32864,
	 * 32818)。<BR> 將迪嘉勒廷的女間諜帶領到抵抗軍村落的路途上，魔族暗殺團可能也會來攻擊。<BR>
	 * 安全帶到抵抗軍村落時將會收到密封的情報書，之後再將<font fg=0000ff>密封的情報書</font>拿回去給迪嘉勒廷。<BR> <BR>
	 * 注意事項：<BR> 由於迪嘉勒廷的女間諜如果遭受攻擊，進而失血致死，將必須重新護送一次<BR> <BR> 任務目標：<BR>
	 * 協助間諜逃亡到抵抗軍村落，獲得密封的情報書，並回去找迪嘉勒廷<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.53
	 * 魔族暗殺團</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>密封的情報書 x 1</font><BR>
	 * <BR> <img src="#1210"></img> <font fg=66ff66>步驟.4 嶄新的線索 </font><BR> <BR>
	 * 將密封的情報書交給迪嘉勒廷，這回才發現不死魔族能夠不斷重生的線索。<BR> 之後迪嘉勒廷會委託玩家前往魔族神殿尋找再生聖殿的入口。<BR>
	 * <BR> 注意事項：<BR> 續接任務請參考不死魔族再生的秘密，該任務為團體試煉，請先湊齊王族、騎士、妖精、法師4名成員。<BR> <BR>
	 * 任務目標：<BR> 將密封的情報書交給迪嘉勒廷，並探聽下一個目的地，50級試煉上半部結束<BR> <BR>
	 */
	private ElfLv50_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ElfLv50_1();
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
