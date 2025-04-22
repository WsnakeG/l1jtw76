package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:遠征隊的遺物 (妖精15級以上官方任務)
 * 
 * @author daien
 */
public class ElfLv15_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ElfLv15_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_e15_1";

	/*
	 * <img src="#1210"></img> <font fg=ffff66>步驟.1 困惑的瑪勒巴 ：</font><BR> <BR>
	 * 在妖精森林裡找到<font fg=0000ff>瑪勒巴</font>(33053, 32315)，和他交談後會給你瑪勒巴的信。<BR>
	 * 並且請玩家將信拿去給阿拉斯。<BR> <BR> 任務目標：<BR> 跟瑪勒巴接洽任務，並探聽下一個步驟<BR> <BR> 相關物品：<BR>
	 * <font fg=ffff00>瑪勒巴的信 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=ffff66>步驟.2 著急的阿拉斯 ：</font><BR> <BR> 在妖精森林的眠龍洞穴入口旁，可以找到<font
	 * fg=0000ff>阿拉斯</font>，並且將瑪勒巴的信交給他。<BR>
	 * 之後，他會請求玩家們幫他蒐集遠征隊的遺物，並給你阿拉斯的護身符。<BR> <BR> 任務目標：<BR> 跟阿拉斯接洽任務，並探聽下一個步驟<BR>
	 * <BR> 相關物品：<BR> <font fg=ffff00>阿拉斯的護身符 x 1</font><BR> <BR> <img
	 * src="#1210"></img> <font fg=ffff66>步驟.3 遠征隊的遺物 ：</font><BR> <BR> 在眠龍洞穴
	 * 1樓和眠龍洞穴 2樓狩獵污濁獨眼巨人，可以獲得遠征隊的遺物。<BR> 打開遠征隊的遺物可以獲得污濁裝備(全部6種)，將污濁裝備拿去給<font
	 * fg=0000ff>阿拉斯</font>，他會給予你同樣款式的遠征隊裝備。<BR> <BR> 污濁的弓 → 遠征隊弓<BR> 污濁的金甲 →
	 * 遠征隊金甲<BR> 污濁的腕甲 → 遠征隊腕甲<BR> 污濁的頭盔 → 遠征隊頭盔<BR> 污濁的鋼靴 → 遠征隊鋼靴<BR> 污濁斗篷 →
	 * 遠征隊斗篷<BR> <BR> 當湊齊六種遠征隊裝備，在與阿拉斯交談以及歸還阿拉斯的護身符。<BR>
	 * 之後他會給予阿拉斯的信，並且跟你說如果想要修好這些裝備就去找瑪勒巴。<BR> <BR> 注意事項：<BR>
	 * 兩樓層所獲得的遠征隊的遺物，能夠得到的污濁裝備不相同，因此要湊齊整套污濁裝備，需要兩個樓層都蒐集<BR> <BR> 任務目標：<BR>
	 * 湊齊遠征隊裝備，並獲得阿拉斯的信<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.14
	 * 污濁獨眼巨人</font><BR> <font fg=ffff00>Lv.18 污濁獨眼巨人</font><BR> <BR> 相關物品：<BR>
	 * <font fg=ffff00>污濁的弓 x 1</font><BR> <font fg=ffff00>污濁的金甲 x 1</font><BR>
	 * <font fg=ffff00>污濁的腕甲 x 1</font><BR> <font fg=ffff00>污濁的頭盔 x 1</font><BR>
	 * <font fg=ffff00>污濁的鋼靴 x 1</font><BR> <font fg=ffff00>污濁斗篷 x 1</font><BR>
	 * <font fg=ffff00>阿拉斯的信 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=ffff66>步驟.4 修復裝備 ：</font><BR> <BR> 將阿拉斯的信交給瑪勒巴，之後<font
	 * fg=0000ff>瑪勒巴</font>就會跟你說修復這些裝備需要那些材料。<BR>
	 * 接下來只要蒐集好材料就能修復遠征隊裝備，並且修好的裝備會顯示出自己的名子，證明修復者的身份。<BR> <BR> 遠征隊弓 → ID‧妖精弓<BR>
	 * 遠征隊金甲 → ID‧妖精金甲<BR> 遠征隊腕甲 → ID‧妖精腕甲<BR> 遠征隊頭盔 → ID‧妖精頭盔<BR> 遠征隊鋼靴 →
	 * ID‧妖精鋼靴<BR> 遠征隊斗篷 → ID‧妖精斗篷<BR> <BR> 任務目標：<BR>
	 * 將阿拉斯的信交給瑪勒巴，即可開始蒐集材料修復裝備<BR> <BR> 相關物品：<BR> <font fg=ffff00>ID‧妖精弓 x
	 * 1</font><BR> <font fg=ffff00>ID‧妖精腕甲 x 1</font><BR> <font
	 * fg=ffff00>ID‧妖精金甲 x 1</font><BR> <font fg=ffff00>ID‧妖精頭盔 x 1</font><BR>
	 * <font fg=ffff00>ID‧妖精鋼靴 x 1</font><BR> <font fg=ffff00>ID‧妖精斗篷 x
	 * 1</font><BR> <BR>
	 */
	private ElfLv15_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ElfLv15_1();
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
