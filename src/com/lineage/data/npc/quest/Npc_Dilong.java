package com.lineage.data.npc.quest;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv30_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;

/**
 * 迪隆<BR>
 * 50014<BR>
 * 說明:不死族的叛徒 (法師30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Dilong extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Dilong.class);

	private Npc_Dilong() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Dilong();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		/*
		 * if (true) { staraQuest(pc); return; }
		 */
		try {
			if (pc.isCrown()) {// 王族
				// 請問...你是冒險家嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong2"));

			} else if (pc.isKnight()) {// 騎士
				// 請問...你是冒險家嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong2"));

			} else if (pc.isElf()) {// 精靈
				// 請問...你是冒險家嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong2"));

			} else if (pc.isWizard()) {// 法師
				// LV30任務已經完成
				if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
					// 請問...你認識塔拉斯嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= WizardLv30_1.QUEST.get_questlevel()) {
					// 任務尚未開始
					if (!pc.getQuest().isStart(WizardLv30_1.QUEST.get_id())) {
						// 請問...你認識塔拉斯嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong3"));

					} else {// 任務已經開始
						// 請問...您是我的吉倫師父所提到的人吧？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong1"));
					}

				} else {
					// 請問...你是冒險家嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong2"));
				}

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 請問...你是冒險家嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong2"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 請問...你是冒險家嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong2"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 請問...你是冒險家嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong2"));

			} else {
				// 請問...你是冒險家嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilong2"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		final boolean isCloseList = false;

		if (pc.isWizard()) {// 法師
			// LV30任務已經完成
			if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
				return;
			}
			// 任務尚未開始
			if (!pc.getQuest().isStart(WizardLv30_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("teleportURL")) {// 往不死族的地監
				// 沒有不死族的鑰匙
				if (!pc.getInventory().checkItem(40581)) {
					// 啊，想要通過魔力之門需要有魔法材料...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilongn"));
					return;
				}
				// 已經攜帶 不死族的骨頭(40579)
				if (pc.getInventory().checkItem(40579)) {
					// 啊，想要通過魔力之門需要有魔法材料...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilongn"));

				} else {
					// 那麼準備好了嗎？以後成功時，請告訴我地監內部的事情。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dilongs"));
				}

			} else if (cmd.equalsIgnoreCase("teleport mage-quest-dungen")) {// 通過魔力之門
				staraQuest(pc);
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 進入副本執行任務
	 * 
	 * @param pc
	 * @return
	 */
	public static void staraQuest(final L1PcInstance pc) {
		try {
			// 任務編號
			final int questid = WizardLv30_1.QUEST.get_id();

			// 任務地圖編號(法師試煉地監)
			final int mapid = WizardLv30_1.MAPID;

			// 取回新的任務副本編號
			final int showId = WorldQuest.get().nextId();

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

			final List<L1NpcInstance> npcs = quest.npcList(81109);// 骷髏
			if (npcs != null) {
				for (final L1NpcInstance npc : npcs) {
					// 改變骷髏外型為阿魯巴(時效30分鐘)
					L1PolyMorph.doPoly(npc, 1128, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				}
			}

			// 召喚門0:/ 1:\ ↓Y←X
			L1SpawnUtil.spawnDoor(quest, 10000, 89, 32809, 32795, (short) mapid, 1);
			L1SpawnUtil.spawnDoor(quest, 10001, 88, 32812, 32909, (short) mapid, 0);// 88
			L1SpawnUtil.spawnDoor(quest, 10002, 89, 32825, 32920, (short) mapid, 1);
			L1SpawnUtil.spawnDoor(quest, 10003, 90, 32868, 32919, (short) mapid, 0);
			// 設置副本參加編號(已經在WorldQuest加入編號)
			// pc.set_showId(showId);

			// 使用牛的代號脫除全部裝備
			pc.getInventory().takeoffEquip(945);
			// 傳送任務執行者
			L1Teleport.teleport(pc, 32791, 32788, (short) mapid, 5, true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
