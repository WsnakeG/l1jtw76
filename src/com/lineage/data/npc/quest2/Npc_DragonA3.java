package com.lineage.data.npc.quest2;

import static com.lineage.server.model.skill.L1SkillId.DRAGON_BLOOD_3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ADLv80_3;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;

/**
 * 灰色 龍之門扉<BR>
 * 林德拜爾棲息地
 * 
 * @author terry0412
 */
public class Npc_DragonA3 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_DragonA3.class);

	private Npc_DragonA3() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_DragonA3();
	}

	@Override
	public int type() {
		return 1;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.hasSkillEffect(DRAGON_BLOOD_3)) {
				// 龍之血痕已穿透全身，在血痕的氣味消失之前，無法再進入龍之門扉。
				pc.sendPackets(new S_ServerMessage(1626));
			} else {
				// 林德拜爾棲息地
				startQuest(pc, npc);
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 進入副本執行任務(林德拜爾棲息地)
	 * 
	 * @param pc
	 * @return
	 */
	private final void startQuest(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			int showId = npc.get_quest_id();
			L1QuestUser quest = WorldQuest.get().get(showId);
			// 尚未初始化 或是 該副本人員已清空
			if (showId == 0/* || quest == null */) {
				// 取回新的任務副本編號
				showId = WorldQuest.get().nextId();
				// 儲存副本編號資訊
				npc.set_quest_id(showId);
				// 初始化
				set_init(showId);
			}

			// 重新取得任務副本物件
			quest = WorldQuest.get().get(showId);
			if (quest == null) {
				// 初始化
				quest = set_init(showId);
			}

			// 任務地圖編號
			final int mapid = ADLv80_3.MAPID;
			// 進入人數限制
			int users = QuestMapTable.get().getTemplate(mapid);
			if (users == -1) { // 無限制
				users = Byte.MAX_VALUE; // 設置為127
			}
			if (quest.size() >= users) {
				pc.sendPackets(new S_SystemMessage("副本允許進入人數已滿: " + users + "人。"));
				return;
			}
			// 加入副本中
			quest.add(pc);
			// 傳送任務執行者
			L1Teleport.teleport(pc, 32673, 32925, (short) mapid, 5, true);

			// 任務編號
			final int questid = ADLv80_3.QUEST.get_id();

			// 將任務設置為執行中
			QuestClass.get().startQuest(pc, questid);
			// 將任務設置為結束
			QuestClass.get().endQuest(pc, questid);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 各項設定初步重置
	 * 
	 * @param showId
	 * @author terry0412
	 */
	private final L1QuestUser set_init(final int showId) {
		try {
			// 任務編號
			final int questid = ADLv80_3.QUEST.get_id();

			// 任務地圖編號
			final int mapid = ADLv80_3.MAPID;

			// 加入副本執行成員
			final L1QuestUser quest = WorldQuest.get().put(showId, mapid, questid);
			if (quest == null) {
				_log.error("副本設置過程發生異常!!");
				return null;
			}

			// 取回進入時間限制
			final Integer time = QuestMapTable.get().getTime(mapid);
			if (time != null) {
				quest.set_time(time.intValue());
			}

			// 召喚門
			L1SpawnUtil.spawnDoor(quest, 10037, 8011, 32675, 33182, (short) mapid, 0); // A
																						// /
			L1SpawnUtil.spawnDoor(quest, 10038, 8015, 32687, 33125, (short) mapid, 1); // B
																						// \
			L1SpawnUtil.spawnDoor(quest, 10039, 8015, 32729, 33180, (short) mapid, 1); // C
																						// \

			// 移除掉落物
			for (final L1NpcInstance npc : quest.npcList()) {
				if (npc instanceof L1MonsterInstance) {
					final L1MonsterInstance mob = (L1MonsterInstance) npc;
					// 新林德拜爾
					if ((npc.getNpcId() >= 97204) && (npc.getNpcId() <= 97209)) {
						continue;
					}
					mob.set_storeDroped(true);
					mob.getInventory().clearItems();
				}
			}
			return quest;
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
