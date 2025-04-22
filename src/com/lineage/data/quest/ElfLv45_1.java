package com.lineage.data.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Quest;

/**
 * 說明:妖精的任務 (妖精45級以上官方任務)
 * 
 * @author daien
 */
public class ElfLv45_1 extends QuestExecutor {

	private static final Log _log = LogFactory.getLog(ElfLv45_1.class);

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
	private static final String _html = "y_q_e45_1";

	/*
	 * <img src="#1210"></img> <font fg=66ff66>步驟.1 水之豎琴 </font><BR> <BR>
	 * 馬沙為了阻擋邪惡勢力所派出調查員調查，根據調查員傳來的資料顯示，邪惡勢力與水之豎琴有關，
	 * 而水之豎琴的秘密只有吉普賽人知道，所以給予妖精前往了解的任務。<BR> 前往威頓村找<font
	 * fg=0000ff>馬沙</font>(33713，32504)接下調查水之豎琴任務。到吉普賽村找<font
	 * fg=0000ff>希托</font>(33975，32931)對話，他要你幫忙尋回被<font
	 * fg=0000ff>賽菲亞</font>搶走的<font fg=0000ff>藍色長笛</font>。 <BR> <BR> 任務目標：<BR>
	 * 與馬沙接任務，去吉普賽村找希托接受任務<BR> <BR> <img src="#1210"></img> <font fg=66ff66>步驟.2
	 * 賽菲亞之罪 </font><BR> <BR> 前往龍之谷洞穴三樓找到<font fg=0000ff>賽菲亞</font
	 * >(32712，32842)和他對話後得知他的過去，若要幫助他就必需承受他過去的罪行。答應後他會傳送你到<font
	 * fg=0000ff>賽菲亞之罪<
	 * /font>的房間(32737，32859)，將賽菲亞之罪打倒後得到藍色長笛。將藍色長笛交給希托後得到神秘貝殼，並告訴你有關<font
	 * fg=0000ff>水之豎琴</font>的事。 <BR> <BR> 注意事項：<BR> 紅人無法跟希托對話進行試煉<BR>
	 * 賽菲亞之罪會扣除正義值10,000<BR> 所以最好正義值超過10,000在去考，免的結束之後，相性卻變成邪惡的<BR>
	 * 在賽菲亞之罪房間重登會回奇岩村
	 * ，可用傳送回家的卷軸、祝福的瞬間移動卷軸、傳送控制戒指，但無法使用指定傳送、瞬間移動卷軸及魔法「世界樹的呼喚」<BR> <BR>
	 * 任務目標：<BR> 尋找賽菲亞，傳送到洞穴打倒賽菲亞之罪取得藍色長笛，回去跟希托交差，並打聽水之豎琴的情報<BR> <BR> 相關怪物：<BR>
	 * <font fg=ffff00>Lv.10 賽菲亞之罪</font><BR> <BR> 相關物品：<BR> <font
	 * fg=ffff00>藍色長笛 x 1</font><BR> <font fg=ffff00>神秘貝殼 x 1</font><BR> <BR>
	 * <img src="#1210"></img> <font fg=66ff66>步驟.3 古代亡靈 </font><BR> <BR>
	 * 到達<font fg=0000ff>冰鏡湖左邊的魔法陣</font>(33973，32326)後使用<font
	 * fg=0000ff>神秘貝殼</font>，會出現<font
	 * fg=0000ff>古代亡靈</font>，打倒他後得到古代亡靈之袋，點二下後得到<font
	 * fg=0000ff>古代鑰匙</font>及<font
	 * fg=0000ff>水之豎琴</font>。回到威頓村將調查的結果(古代鑰匙及水之豎琴)交給馬沙後得到馬沙之袋
	 * ，點二下後得到保護者手套、精靈水晶(召喚強力屬性精靈)及水之豎琴。 <BR> 注意事項：<BR>
	 * 使用神秘貝殼時需站在魔法陣的中央才能召喚出古代亡靈。<BR> 將水之豎琴鑑定後會變成水精靈之弓。<BR>
	 * 水精靈之弓(水之豎琴)為下一階段任務的必備物品，刪除將無法接到50級試煉<BR> <BR> 任務目標：<BR>
	 * 打倒古代亡靈取得古代鑰匙及水之豎琴，回去和馬沙交差取得獎勵 <BR> <BR> 相關怪物：<BR> <font fg=ffff00>Lv.45
	 * 古代亡靈</font><BR> <BR> 相關物品：<BR> <font fg=ffff00>古代鑰匙(下半部) x 1</font><BR>
	 * <font fg=ffff00>古代亡靈之袋 x 1</font><BR> <font fg=ffff00>馬沙之袋 x 1</font><BR>
	 * <font fg=ffff00>水精靈之弓 x 1</font><BR> <font fg=ffff00>保護者手套 x 1</font><BR>
	 * <font fg=ffff00>精靈水晶(召喚強力屬性精靈) x 1</font><BR> <font fg=ffff00>水之豎琴 x
	 * 1</font><BR> <BR>
	 */
	private ElfLv45_1() {
		// TODO Auto-generated constructor stub
	}

	public static QuestExecutor get() {
		return new ElfLv45_1();
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
