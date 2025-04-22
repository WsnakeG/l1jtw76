package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:糾正錯誤的觀念 (黑暗妖精45級以上官方任務)
 * 
 * @author daien
 */
public class DarkElfLv45_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(DarkElfLv45_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_d45_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 同族的內爭 </font><BR> <BR>
	 * 尋找位在沉默洞穴裡刺客的精神支柱<font
	 * fg=0000ff>布魯迪卡</font>。與布魯迪卡對話時，他表示因為與反王勾結的刺客，導致黑妖種族引起內爭
	 * ，為了將他們錯誤的想法糾正過來。而要解決此問題的話，必須取得傳說中的武器才行。另外他會建議你到<font
	 * fg=0000ff>侏儒鐵匠－庫普</font>那探聽消息。<BR> 到沉默洞穴找庫普，得知製作傳說中的武器其中一個材料<font
	 * fg=0000ff>"生鏽的刺客之劍"</font>在<font
	 * fg=0000ff>"刺客首領"</font>的手上，而了解此事的人為歐瑞村的<font
	 * fg=0000ff>羅吉</font>，但是庫普要你去找羅吉之前先去眠龍洞穴附近繞繞，可能會有出人意外的收穫。<BR> <BR>
	 * 注意事項：<BR> 布魯迪卡：32802，32824<BR> 庫普：32829，32971<BR> 羅吉：34036，32258<BR> <BR>
	 * 任務目標：<BR> 與布魯迪卡接受任務，去找庫普繼續接受任務<BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.2 刺客之證 </font><BR> <BR>
	 * 接受庫普的建議之後，在眠龍洞穴的附近(32928，32276)找到<font
	 * fg=0000ff>"刺客首領護衛"</font>，與他對話後得知要見到"刺客首領"的方法必需要有刺客之證。 <BR>
	 * 之後便前往尋找歐瑞村的羅吉，
	 * 但是與羅吉對話之後，他卻說明如果要他幫忙，就要先解決最近發生雪怪偷吃村裡家畜的問題。接受他的要求之後，到歐瑞村跟象牙塔之間尋找<font
	 * fg=0000ff>"凶猛的雪怪"</font>並解決掉它，即可得到<font
	 * fg=0000ff>雪怪首級</font>。拿著雪怪首級回去找羅吉，並交給他，即可得到<font fg=0000ff>刺客之證</font>。
	 * <BR> <BR> 注意事項：<BR> 與刺客首領護衛對話時必需變身成刺客，才能與他對話，否則他不會理你。<BR>
	 * 得到刺客之證後必需再度與羅吉對話，否則打死刺客首領後將無法得到刺客首領的箱子。<BR> <BR> 任務目標：<BR>
	 * 與刺客首領護衛探聽消息之後，與羅吉接受任務，狩獵凶猛的雪怪取得它的首級，回去交差可得刺客之證<BR> <BR> 相關怪物：<BR> <font
	 * fg=ffff00>Lv.30 凶猛的雪怪</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>刺客之證 x
	 * 1</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.3 刺客首領
	 * </font><BR> <BR> 隨後前往眠龍洞穴3樓尋找一個特殊的傳送點，站上去後使用刺客之證便會進入一房間，而<font
	 * fg=0000ff>刺客首領</font>就在裡面。遇到刺客首領，將他解決掉之後，即可得到<font
	 * fg=0000ff>刺客首領的箱子</font>。打開刺客首領的箱子， 即可得到<font fg=0000ff>死亡之證</font>和<font
	 * fg=0000ff>生鏽的刺客之劍</font>。<BR> 回去找布魯迪卡，並將死亡之證及刺客之證交給他，即可得到<font
	 * fg=0000ff>布魯迪卡之袋</font>。打開布魯迪卡之袋， 即可得到黑暗精靈水晶(暗影閃避)和影子長靴。 <BR> <BR>
	 * 注意事項：<BR> 在攻擊刺客首領時，若有其他的玩家一起攻擊，將無法得到刺客首領的箱子。<BR> <BR> 任務目標：<BR>
	 * 打敗刺客首領取得任務道具，之後回去和布魯迪卡交差取得獎勵<BR> <BR> 相關物品：<BR> <font fg=ffff00>布魯迪卡之袋 x
	 * 1</font><BR> <font fg=ffff00>影子長靴 x 1</font><BR> <font
	 * fg=ffff00>黑暗精靈水晶(暗影閃避) x 1</font><BR> <font fg=ffff00>刺客首領的箱子 x
	 * 1</font><BR> <font fg=ffff00>生鏽的刺客之劍 x 1</font><BR> <font fg=ffff00>死亡之證
	 * x 1</font><BR> <BR>
	 */
	private DarkElfLv45_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new DarkElfLv45_1();
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
