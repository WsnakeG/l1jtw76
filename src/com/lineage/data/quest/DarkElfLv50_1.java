package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;
import com.lineage.server.timecontroller.quest.DE50A_Timer;

/**
 * 說明:尋找黑暗之星 (黑暗妖精50級以上官方任務)
 * 
 * @author daien
 */
public class DarkElfLv50_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(DarkElfLv50_1.class);

	/**
	 * 任務資料
	 */
	public static L1Quest QUEST;

	/**
	 * 安迪亞
	 */
	public static final int _endiaId = 71094;

	/**
	 * 萊拉
	 */
	public static final int _tgid = 70811;

	/**
	 * 任務地圖(黑暗妖精試煉地監)
	 */
	public static final int MAPID = 306;

	/**
	 * 任務資料說明HTML
	 */
	private static final String _html = "y_q_d50_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 黑暗的騷動 </font><BR> <BR>
	 * 布魯迪卡說明在很久以前黑暗妖精怨念的母體，為了避免黑暗之星變質，而造成黑暗妖精種族的毀滅，因此布魯迪卡要求找回黑暗之星。<BR>
	 * 前往沉默洞穴找<font fg=0000ff>長老－布魯迪卡</font>(32802，32824)接下尋找<font
	 * fg=0000ff>黑暗之星</font>的任務。<BR> 布魯迪卡表示可以去尋求<font
	 * fg=0000ff>奇馬</font>(32907，32946)的幫助，找到奇馬後發現如果要他幫忙就得先幫他去妖精森林調查。<BR> <BR>
	 * 任務目標：<BR> 與布魯迪卡接受任務，並尋找奇馬探聽消息<BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.2 亡者的訊息 </font><BR> <BR>
	 * 接下奇馬的調查任務後，就前往眠龍洞穴入口附近(32944，32280)找<font
	 * fg=0000ff>安迪亞</font>，他會要求你將他的靈魂帶到<font
	 * fg=0000ff>萊拉</font>的身旁，到達之後他就會消失並給予<font
	 * fg=0000ff>安迪亞之袋</font>，點二下後就可以得到安迪亞之信。<BR> 將安迪亞之信交給奇馬後他會給予<font
	 * fg=0000ff>真實的面具</font>，並且說明要找黑暗之星必需要前往靈魂枯竭的土地上，之後便前往<font
	 * fg=0000ff>食屍地</font>找<font fg=0000ff>墮落的靈魂</font>(32858，32929)傳送到<font
	 * fg=0000ff>邪念地監</font>。 <BR> <BR> 注意事項：<BR> 安迪亞會四處移動，因此找不到的玩家請在附近找找。 <BR>
	 * 與安迪亞對話後需將她帶到萊拉的旁邊，如果玩家死亡、重登或走太快她會被別的黑暗妖精帶走（與騎士45級試練的調查員相同）。 <BR>
	 * 墮落的靈魂需戴上真實的面具才能看的到。並與墮落的靈魂對話後才能傳送到邪念地監。<BR> <BR> 任務目標：<BR>
	 * 尋找安迪亞並接受引導任務，之後取得遺物拿回去給奇馬，得到真實的面具，再尋找墮落的靈魂傳送到邪念地監<BR> <BR> 相關物品：<BR>
	 * <font fg=ffff00>安迪亞之袋 x 1</font><BR> <font fg=ffff00>安迪亞之信 x 1</font><BR>
	 * <font fg=ffff00>真實的面具 x 1</font><BR> <BR> <img src="#1210"></img> <font
	 * fg=66ff66>步驟.3 邪念的地監 </font><BR> <BR> 進入後尋找<font
	 * fg=0000ff>墮落的司祭</font>(32592，32819)，打倒牠後可以獲得墮落鑰匙。 <BR> 之後再尋找<font
	 * fg=0000ff>混沌的司祭</font>(32621，32906)，打倒牠後可以獲得混沌鑰匙。 <BR>
	 * 走到第一排中間的寶箱後面(32619，32909)使用墮落鑰匙將寶箱打開後即可解除暗道的障礙，馬上使用混沌鑰匙傳送到(32591，32813)，
	 * 此時障礙已經解除，通過一片黑色的地方。 <BR>
	 * 之後走到魔法陣裡(32566，32880)，就會傳送到死亡的司祭的房間，打倒牠就可以獲得黑暗之星。 <BR> <BR> 注意事項：<BR>
	 * 邪念地監無法記憶座標，無法使用指定傳送、瞬間移動卷軸、全體傳送術的卷軸。<BR> 邪念地監會持續扣掉角色的體力。 <BR>
	 * 死亡時會回到村莊、重登時會在遠古戰場的邪惡神殿附近出現。 <BR> 在開暗門時由於寶箱及牆上的畫在一定的時間內就會恢復原狀，因此開完後需立即通過。
	 * <BR> 在尚未取得黑暗之星前，不要先購買蘑菇毒液以免影響到流程！ <BR> <BR> 任務目標：<BR>
	 * 依序打倒各種司祭，終將取得黑暗之星<BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.50
	 * 混沌的司祭</font><BR> <font fg=ffff00>Lv.57 墮落的司祭</font><BR> <font
	 * fg=ffff00>Lv.57 死亡的司祭</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>黑暗之星 x
	 * 1</font><BR> <font fg=ffff00>墮落鑰匙 x 1</font><BR> <font fg=ffff00>混沌鑰匙 x
	 * 1</font><BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.4 附毒的武器
	 * </font><BR> <BR> 再來到大陸地監7樓找<font
	 * fg=0000ff>歐林</font>(32813，32803)，向他購買蘑菇毒液。 <BR> 回到沉默洞穴找長老－<font
	 * fg=0000ff>布魯迪卡</font>(32802，32824)，將真實的面具、蘑菇毒液、黑暗之星交給布魯迪卡，就可以獲得死亡之指。 <BR>
	 * <BR> 任務目標：<BR> 購得蘑菇毒液，將任務所得物品全部交給布魯迪卡，並取得死亡之指<BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>蘑菇毒液 x 1</font><BR> <font fg=ffff00>死亡之指 x 1</font><BR> <BR>
	 */
	private DarkElfLv50_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new DarkElfLv50_1();
	}

	@Override
	public void execute(final L1Quest quest) {
		try {
			// 設置任務
			QUEST = quest;

			final DE50A_Timer de50ATimer = new DE50A_Timer();
			de50ATimer.start();

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
