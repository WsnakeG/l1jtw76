package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;

/**
 * 守門人<BR>
 * 70795<BR>
 * 說明:拯救被幽禁的吉姆 (騎士30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Qguard extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Qguard.class);

	private Npc_Qguard() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Qguard();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 只有傑瑞德允許的人才能進入。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));

			} else if (pc.isKnight()) {// 騎士
				// 等級達成要求
				if (pc.getLevel() >= KnightLv30_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id())) {
					case 5:// 達到5
							// 試鍊地監是一些想要成為真正紅騎士的騎士們修鍊的地方。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard"));
						break;

					default:// 其他
						// 只有傑瑞德允許的人才能進入。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));
						break;
					}

				} else {
					// 只有傑瑞德允許的人才能進入。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));
				}

			} else if (pc.isElf()) {// 精靈
				// 只有傑瑞德允許的人才能進入。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));

			} else if (pc.isWizard()) {// 法師
				// 只有傑瑞德允許的人才能進入。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 只有傑瑞德允許的人才能進入。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 只有傑瑞德允許的人才能進入。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 只有傑瑞德允許的人才能進入。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));

			} else {
				// 只有傑瑞德允許的人才能進入。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isKnight()) {// 騎士
			// LV30任務已經完成
			if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
				return;
			}
			// 任務尚未開始
			if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
				isCloseList = true;

			} else {// 任務已經開始
				// 任務進度
				switch (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id())) {
				case 5:// 達到5
					if (cmd.equalsIgnoreCase("teleportURL")) {// 往傑瑞德的試鍊地監
						// 請記住，在地監裡只能使用紅騎士之劍與翡翠藥水。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguards"));

					} else if (cmd.equalsIgnoreCase("teleport gerard-dungen")) {// 進入傑瑞德的試鍊地監
						staraQuest(pc);
						isCloseList = true;
					}
					break;

				default:// 其他
					isCloseList = true;
					break;
				}
			}

		} else {
			isCloseList = true;
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
	private void staraQuest(final L1PcInstance pc) {
		try {
			// 任務編號
			final int questid = KnightLv30_1.QUEST.get_id();

			// 任務地圖編號
			final int mapid = KnightLv30_1.MAPID;

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

			// 召喚門0:/ 1:\ ↓Y←X
			L1SpawnUtil.spawnDoor(quest, 10004, 89, 32769, 32778, (short) mapid, 1);

			// 召喚主要蛇女
			final L1Location loc = new L1Location(32774, 32778, mapid);
			final L1NpcInstance mob = L1SpawnUtil.spawn(81107, loc, 5, showId);
			mob.getInventory().storeItem(40544, 1);// 蛇女之鱗 x 1
			quest.addNpc(mob);

			// 使用牛的代號脫除全部裝備
			pc.getInventory().takeoffEquip(945);
			// 設置副本參加編號(已經在WorldQuest加入編號)
			// pc.set_showId(showId);
			// 傳送任務執行者
			L1Teleport.teleport(pc, 32769, 32768, (short) mapid, 4, true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
