package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:妖魔密使首領的情報 (龍騎士30級以上官方任務)
 * 
 * @author daien
 */
public class DragonKnightLv30_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(DragonKnightLv30_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_dk30_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 長老賦予的任務 </font><BR> <BR>
	 * 至貝希摩斯找<font fg=0000ff>長老普洛凱爾</font>(32817 32831)，他會跟你說聽聞海音地監中的<font
	 * fg=0000ff>妖魔密使</font>，好像正在尋找些什麼似的，希望這次你去那邊查一下比較好。 之後長老普洛凱爾會給你普洛凱爾的<font
	 * fg=0000ff>第二次指令書</font>及<font fg=0000ff>普洛凱爾的礦物袋</font>，並要你去做調查的事前準備。<BR>
	 * <BR> 任務目標：<BR> 跟長老普洛凱爾接洽任務，並探聽下一個指示<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>普洛凱爾的礦物袋 x 1</font><BR> <font fg=ffff00>普洛凱爾的第二次指令書 x
	 * 1</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2 事前準備
	 * </font><BR> <BR> 與位於長老普洛凱爾不遠處的愛爾菈絲(32816 32843)對話可取得<font
	 * fg=0000ff>妖魔密使變形卷軸</font>。<BR> 將普洛凱爾的礦物袋交給鐵匠皮爾(32790 32838)可製作<font
	 * fg=0000ff>妖魔密使的徽印</font>。<BR> <BR> 任務目標：<BR> 湊齊妖魔密使變形卷軸和妖魔密使的徽印<BR> <BR>
	 * 相關物品：<BR> <font fg=ffff00>妖魔密使變形卷軸 x 1</font><BR> <font fg=ffff00>妖魔密使的徽印
	 * x 1</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.3 妖魔密使
	 * </font><BR> <BR> 在海音地監 3樓找到妖魔密使，並交付同伴的證明妖魔密使的徽印。<BR>
	 * 之後妖魔密使會給你妖魔密使之笛子。<BR> 在海音地監 3樓使用妖魔密使之笛子後可召喚出妖魔密使首領，將其打倒後即可取得<font
	 * fg=0000ff>妖魔密使首領間諜書</font>。<BR> <BR> 注意事項：<BR>
	 * 與妖魔密使對話前，必須先使用妖魔密使變形卷軸將外表偽裝成妖魔密使<BR> <BR> 任務目標：<BR> 到海音地監
	 * 3樓取得妖魔密使首領間諜書<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.22
	 * 妖魔密使(海音地監)</font><BR> <font fg=ffff00>Lv.28 妖魔密使首領</font><BR> <BR>
	 * 相關物品：<BR> <font fg=ffff00>妖魔密使之笛子 x 1</font><BR> <font
	 * fg=ffff00>妖魔密使首領間諜書 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.4 回報 </font><BR> <BR>
	 * 將獲得的妖魔密使首領間諜書交給長老普洛凱爾，即可獲得普洛凱爾的第一次信件及龍騎士書板(血之渴望)。<BR>
	 * 將普洛凱爾的第一次信件交給塔爾立昂(32828 32844)，即可獲得龍鱗臂甲。<BR> <BR> 任務目標：<BR>
	 * 將妖魔密使首領間諜書轉交給長老普洛凱爾，並獲得獎勵<BR> <BR> 相關物品：<BR> <font fg=ffff00>龍騎士書板(血之渴望)
	 * x 1</font><BR> <font fg=ffff00>龍鱗臂甲 x 1</font><BR> <font
	 * fg=ffff00>普洛凱爾的第一次信件 x 1</font><BR> <BR>
	 */
	private DragonKnightLv30_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new DragonKnightLv30_1();
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
