package com.lineage.data.item_etcitem.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.DragonKnightLv50_1;
import com.lineage.data.quest.IllusionistLv50_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.WorldQuest;

/**
 * 49202 時空裂痕邪念碎片
 */
public class ThoughtPieceTime extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(ThoughtPieceTime.class);

	/**
	 *
	 */
	private ThoughtPieceTime() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ThoughtPieceTime();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		if (pc.isDragonKnight()) {// 龍騎士
			if (pc.getQuest().isStart(DragonKnightLv50_1.QUEST.get_id())) {
				pc.getInventory().removeItem(item, 1);// 移除道具
				staraQuest(pc, DragonKnightLv50_1.QUEST.get_id(), DragonKnightLv50_1.MAPID);

			} else {
				// 內容顯示
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "cot_ep1st"));
			}

		} else if (pc.isIllusionist()) {// 幻術師
			if (pc.getQuest().isStart(IllusionistLv50_1.QUEST.get_id())) {
				pc.getInventory().removeItem(item, 1);// 移除道具
				staraQuest(pc, IllusionistLv50_1.QUEST.get_id(), IllusionistLv50_1.MAPID);

			} else {
				// 內容顯示
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "cot_ep1st"));
			}

		} else {
			// 內容顯示
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "cot_ep1st"));
		}
	}

	/**
	 * 進入副本執行任務
	 * 
	 * @param pc
	 * @param questid 任務編號
	 * @param questid 任務地圖編號
	 * @return
	 */
	private void staraQuest(final L1PcInstance pc, final int questid, final int mapid) {
		try {
			// 取回新的任務副本編號
			final int showId = WorldQuest.get().nextId();

			// 進入人數限制
			int users = QuestMapTable.get().getTemplate(mapid);
			if (users == -1) {// 無限制
				users = Byte.MAX_VALUE;// 設置為127
			}

			// 加入副本執行成員
			final L1QuestUser quest = WorldQuest.get().put(showId, mapid, questid, pc);

			if (quest == null) {
				_log.error("副本設置過程發生異常!!");
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			// 取回進入時間限制
			final Integer time = QuestMapTable.get().getTime(mapid);
			if (time != null) {
				quest.set_time(time.intValue());
			}

			// 設置副本參加編號(已經在WorldQuest加入編號)
			// pc.set_showId(showId);
			// 傳送任務執行者
			L1Teleport.teleport(pc, 32729, 32831, (short) mapid, 2, true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
