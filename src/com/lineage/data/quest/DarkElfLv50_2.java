package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:暗黑的武器，死神之証 (黑暗妖精50級以上官方任務)
 * 
 * @author daien
 */
public class DarkElfLv50_2 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(DarkElfLv50_2.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_d50_2";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 暗黑的武器 </font><BR> <BR>
	 * 達到限制條件的黑暗妖精，如果到威頓村找<font
	 * fg=0000ff>卡立普</font>的話，會告訴你，他有辦法幫你製作出暗黑系列的武器。為了製作出你想要的暗黑武器
	 * ，必須收集下列對應的材料。<BR> <BR> 製作成品與對應材料：<BR> <img src="#896"></img> 暗黑鋼爪<BR>
	 * ├─幽暗鋼爪 x1<BR> ├─詛咒的皮革(地) x10<BR> ├─龍之心 x1<BR> ├─冰之女王之心 x9<BR> ├─格蘭肯之淚
	 * x3<BR> ├─高品質綠寶石 x3<BR> └─金幣 x100,000<BR> <BR> <img src="#898"></img>
	 * 暗黑雙刀<BR> ├─幽暗雙刀 x1<BR> ├─詛咒的皮革(水) x10<BR> ├─龍之心 x1<BR> ├─冰之女王之心 x9<BR>
	 * ├─格蘭肯之淚 x3<BR> ├─高品質紅寶石 x3<BR> └─金幣 x100,000 <BR> <BR> <img
	 * src="#900"></img> 暗黑十字弓<BR> ├─幽暗十字弓 x1 <BR> ├─詛咒的皮革(風) x10 <BR> ├─龍之心 x1
	 * <BR> ├─冰之女王之心 x9 <BR> ├─格蘭肯之淚 x3<BR> ├─高品質藍寶石 x3 <BR> └─金幣 x100,000<BR>
	 * <BR> 注意事項：<BR> 必須要等級達到50以上的黑暗妖精，卡立普才會幫忙製作暗黑武器<BR> 只能三種武器選擇一種<BR> <BR>
	 * 任務目標：<BR> 蒐集必需的材料，請卡立普幫忙製作暗黑武器<BR> <BR> 相關物品：<BR> <font fg=ffff00>金幣 x
	 * 100000</font><BR> <font fg=ffff00>高品質紅寶石 x 3</font><BR> <font
	 * fg=ffff00>高品質藍寶石 x 3</font><BR> <font fg=ffff00>高品質綠寶石 x 3</font><BR>
	 * <font fg=ffff00>格蘭肯之淚 x 3</font><BR> <font fg=ffff00>冰之女王之心 x
	 * 9</font><BR> <font fg=ffff00>龍之心 x 1</font><BR> <font fg=ffff00>詛咒的皮革(水)
	 * x 10</font><BR> <font fg=ffff00>詛咒的皮革(風) x 10</font><BR> <font
	 * fg=ffff00>詛咒的皮革(地) x 10</font><BR> <font fg=ffff00>幽暗十字弓 x 1</font><BR>
	 * <font fg=ffff00>幽暗 鋼爪 x 1</font><BR> <font fg=ffff00>幽暗 雙刀 x 1</font><BR>
	 * <BR>
	 */
	private DarkElfLv50_2() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new DarkElfLv50_2();
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
