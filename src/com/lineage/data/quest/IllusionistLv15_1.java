package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:眠龍洞穴污染的來源 (幻術士15級以上官方任務)
 * 
 * @author daien
 */
public class IllusionistLv15_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(IllusionistLv15_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_i15_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 長老賦予的任務</font><BR> <BR>
	 * 在希培莉亞找<font fg=0000ff>長老希蓮恩</font>(32772
	 * 32811)，她會要求你至眠龍洞穴調查洞穴遭受污染的原因。<BR> <BR> 任務目標：<BR> 跟長老希蓮恩接任務，並前往眠龍洞穴<BR>
	 * <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2 蒐集證物</font><BR> <BR>
	 * 在眠龍洞穴狩獵怪物，直到湊齊所需的證物。<BR> 證物包括了<font fg=0000ff>10個污濁妖魔之心</font>、<font
	 * fg=0000ff>1個污濁精靈核晶</font>、<font fg=0000ff>1個污濁安特的水果</font>、<font
	 * fg=0000ff>1個污濁安特的樹枝</font>、<font fg=0000ff>1個污濁安特的樹皮</font>。<BR>
	 * 之後再回去報告長老希蓮恩。<BR> <BR> 任務目標：<BR> 在眠龍洞穴蒐集證物，再回去找長老希蓮恩<BR> <BR> 相關怪物：<BR>
	 * <font fg=ffff00>Lv.12 污濁妖魔弓箭手</font><BR> <font fg=ffff00>Lv.12
	 * 污濁妖魔</font><BR> <font fg=ffff00>Lv.13 污濁 妖魔戰士</font><BR> <font
	 * fg=ffff00>Lv.15 污濁精靈</font><BR> <font fg=ffff00>Lv.15 污濁安特</font><BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>污濁安特的樹皮 x 1</font><BR> <font
	 * fg=ffff00>污濁安特的樹枝 x 1</font><BR> <font fg=ffff00>污濁安特的水果 x 1</font><BR>
	 * <font fg=ffff00>污濁妖魔之心 x 10</font><BR> <font fg=ffff00>污濁精靈核晶 x
	 * 1</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.3
	 * 回報</font><BR> <BR> 將湊齊的證物交給長老希蓮恩，即可獲得幻術士魔杖和記憶水晶(立方：燃燒)。<BR> <BR>
	 * 任務目標：<BR> 將證物轉交給長老希蓮恩，並獲得獎勵<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>記憶水晶(立方：燃燒) x 1</font><BR> <font fg=ffff00>幻術士魔杖 x 1</font><BR>
	 * <BR>
	 */
	private IllusionistLv15_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new IllusionistLv15_1();
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
