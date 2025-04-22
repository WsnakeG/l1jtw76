package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:行跡可疑的妖魔們 (龍騎士15級以上官方任務)
 * 
 * @author daien
 */
public class DragonKnightLv15_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(DragonKnightLv15_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_dk15_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 長老賦予的任務 </font><BR> <BR>
	 * 至貝希摩斯村莊找<font fg=0000ff>長老普洛凱爾</font>(32772 32811)，他會要求你取來3份妖魔密使身上的<font
	 * fg=0000ff>妖魔搜索文件</font>。<BR> <BR> 任務目標：<BR> 跟長老普洛凱爾接任務，並探聽任務地點<BR> <BR>
	 * <img src="#1210"></img> <font fg=66ff66>步驟.2 妖魔密使 </font><BR> <BR>
	 * 分別前往古魯丁村莊祭壇、風木村莊及妖魔森林附近，尋找妖魔密使。<BR> 並且狩獵3個地區的妖魔密使，蒐集3份不同妖魔搜索文件。<BR>
	 * 之後回去找長老普洛凱爾。<BR> <BR> 注意事項：<BR>
	 * 狩獵妖魔密使時，最好直接攻擊妖魔密使，如果先擊退妖魔密使護衛兵，則會發生妖魔密使逃跑的情況<BR> <BR> 任務目標：<BR>
	 * 狩獵妖魔密使，蒐集妖魔搜索文件<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.12
	 * 妖魔密使護衛兵</font><BR> <font fg=ffff00>Lv.17 妖魔密使(妖魔森林)</font><BR> <font
	 * fg=ffff00>Lv.17 妖魔密使(風木)</font><BR> <font fg=ffff00>Lv.17
	 * 妖魔密使(古魯丁)</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>妖魔搜索文件(古魯丁) x
	 * 1</font><BR> <font fg=ffff00>妖魔搜索文件(風木城) x 1</font><BR> <font
	 * fg=ffff00>妖魔搜索文件(妖魔森林) x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 回報 </font><BR> <BR>
	 * 將湊齊的妖魔搜索文件交給長老普洛凱爾，即可獲得龍騎士雙手劍和龍騎士書板(龍之護鎧)。 <BR> 任務目標：<BR>
	 * 將妖魔搜索文件轉交給長老普洛凱爾，並獲得獎勵<BR> <BR> 相關物品：<BR> <font fg=ffff00>龍騎士書板(龍之護鎧) x
	 * 1</font><BR> <font fg=ffff00>龍騎士雙手劍 x 1</font><BR> <BR>
	 */
	private DragonKnightLv15_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new DragonKnightLv15_1();
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
