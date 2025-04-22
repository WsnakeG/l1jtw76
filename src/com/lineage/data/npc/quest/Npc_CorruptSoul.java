package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.WorldQuest;

/**
 * 墮落的靈魂<BR>
 * 71095<BR>
 * 說明:尋找黑暗之星 (黑暗妖精50級以上官方任務)<BR>
 * 
 * @author dexc
 */
public class Npc_CorruptSoul extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_CorruptSoul.class);

	private Npc_CorruptSoul() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_CorruptSoul();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (!pc.getInventory().checkEquipped(20037)) { // 真實的面具
				return;
			}
			if (pc.isCrown()) {// 王族
				// ....
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq2"));

			} else if (pc.isKnight()) {// 騎士
				// ....
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq2"));

			} else if (pc.isElf()) {// 精靈
				// ....
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq2"));

			} else if (pc.isWizard()) {// 法師
				// ....
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq2"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// LV50任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
					// 擁有邪念並不是墮落，而是存在，願榮耀與你同在...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq3"));
					return;
				}
				// 任務進度
				switch (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id())) {
				case 0:
				case 1:
				case 2:
				case 3:
					// 你的邪念還不夠！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulqn"));
					break;
				case 4:
					// 關於邪念地監
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq1"));
					break;
				default:
					// 擁有邪念並不是墮落，而是存在，願榮耀與你同在...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq3"));
					break;
				}

			} else if (pc.isDragonKnight()) {// 龍騎士
				// ....
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq2"));

			} else if (pc.isIllusionist()) {// 幻術師
				// ....
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq2"));

			} else {
				// ....
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulq2"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isDarkelf()) {// 黑暗精靈
			// LV50任務已經完成
			if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
				isCloseList = true;

			} else {
				if (pc.getInventory().checkItem(20037)) { // 真實的面具
					if (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id()) == 4) {// 進度4
						if (cmd.equalsIgnoreCase("teleportURL")) {// 關於邪念地監
							// 在邪念地監裡有像你一樣被邪念纏繞而墮落的人
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "csoulqs"));

						} else if (cmd.equalsIgnoreCase("teleport evil-dungeon")) {// 往邪念地監
							staraQuest(pc);
							isCloseList = true;
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
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
	 */
	private void staraQuest(final L1PcInstance pc) {
		try {
			// 任務編號
			final int questid = DarkElfLv50_1.QUEST.get_id();

			// 任務地圖編號
			final int mapid = DarkElfLv50_1.MAPID;

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

			quest.set_info(false);// 不公告NPC死亡

			// 取回進入時間限制
			final Integer time = QuestMapTable.get().getTime(mapid);
			if (time != null) {
				quest.set_time(time.intValue());
			}

			// 設置副本參加編號(已經在WorldQuest加入編號)
			// pc.set_showId(showId);
			// 傳送任務執行者
			L1Teleport.teleport(pc, 32748, 32799, (short) mapid, 5, true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}