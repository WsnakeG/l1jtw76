package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv15_1;
import com.lineage.data.quest.CrownLv30_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;

/**
 * 看守螞蟻<BR>
 * 70779<BR>
 * 說明:艾莉亞的請求 (王族30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Ants extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Ants.class);

	private Npc_Ants() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Ants();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			boolean isTak = false;
			if (pc.getTempCharGfx() == 1039) {// 巨大兵蟻
				isTak = true;
			}
			if (pc.getTempCharGfx() == 1037) {// 巨蟻
				// 雖然我有點笨，但我還是能夠分辨你不是我們族人。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ants3"));
				return;
			}
			if (!isTak) {
				// #$@$%#$%．．．#$%@#
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ants2"));
				return;
			}
			if (pc.isCrown()) {// 王族
				// LV15任務未完成
				if (!pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
					// 糟糕了。戰鬥兵蟻必須趕快回來...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

				} else {
					// LV30任務已經完成
					if (pc.getQuest().isEnd(CrownLv30_1.QUEST.get_id())) {
						// 糟糕了。戰鬥兵蟻必須趕快回來...
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

					} else {
						// 等級達成要求
						if (pc.getLevel() >= CrownLv30_1.QUEST.get_questlevel()) {
							// 任務尚未開始
							if (!pc.getQuest().isStart(CrownLv30_1.QUEST.get_id())) {
								// 糟糕了。戰鬥兵蟻必須趕快回來...
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

							} else {// 任務已經開始
								if (pc.getInventory().checkItem(40547)) { // 已經具有物品
																			// 村民的遺物
									// 糟糕了。戰鬥兵蟻必須趕快回來...
									pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

								} else {
									// 終於來了啊！我正在等你。
									pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ants1"));
								}
							}

						} else {
							// 糟糕了。戰鬥兵蟻必須趕快回來...
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));
						}
					}
				}

			} else if (pc.isKnight()) {// 騎士
				// 糟糕了。戰鬥兵蟻必須趕快回來...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

			} else if (pc.isElf()) {// 精靈
				// 糟糕了。戰鬥兵蟻必須趕快回來...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

			} else if (pc.isWizard()) {// 法師
				// 糟糕了。戰鬥兵蟻必須趕快回來...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 糟糕了。戰鬥兵蟻必須趕快回來...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 糟糕了。戰鬥兵蟻必須趕快回來...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 糟糕了。戰鬥兵蟻必須趕快回來...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));

			} else {
				// 糟糕了。戰鬥兵蟻必須趕快回來...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antsn"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isCrown()) {// 王族
			// LV15任務未完成
			if (!pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
				return;
			}
			// LV30任務已經完成
			if (pc.getQuest().isEnd(CrownLv30_1.QUEST.get_id())) {
				return;
			}
			// 任務尚未開始
			if (!pc.getQuest().isStart(CrownLv30_1.QUEST.get_id())) {
				isCloseList = true;

			} else {// 任務已經開始
				if (pc.getInventory().checkItem(40547)) { // 已經具有物品 村民的遺物
					isCloseList = true;

				} else {
					if (cmd.equalsIgnoreCase("teleportURL")) {// 前往變種螞蟻地監
						// 戰鬥兵蟻，請您消滅他們吧!
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "antss"));

					} else if (cmd.equalsIgnoreCase("teleport mutant-dungen")) {// 前往變種螞蟻地監
						staraQuest(pc);
						isCloseList = true;
					}
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
			final int questid = CrownLv30_1.QUEST.get_id();

			// 任務地圖編號
			final int mapid = CrownLv30_1.MAPID;

			// 取回新的任務副本編號
			final int showId = WorldQuest.get().nextId();

			// 進入人數限制
			int users = QuestMapTable.get().getTemplate(mapid);
			if (users == -1) {// 無限制
				users = Byte.MAX_VALUE;// 設置為127
			}

			int i = 0;
			// 3格以內血盟成員
			for (final L1PcInstance otherPc : World.get().getVisiblePlayer(pc, 3)) {
				if (otherPc.getClanid() == 0) {// 沒有血盟
					continue;
				}
				if ((otherPc.getClanid() == pc.getClanid()) && (otherPc.getId() != pc.getId())) {
					if (i <= (users - 1)) {
						// 加入副本執行成員
						WorldQuest.get().put(showId, mapid, questid, otherPc);
						// 設置副本參加編號(已經在WorldQuest加入編號)
						// otherPc.set_showId(showId);
						// 傳送成員
						L1Teleport.teleport(otherPc, 32662, 32786, (short) mapid, 3, true);
					}
					i++;
				}
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
			L1Teleport.teleport(pc, 32662, 32786, (short) mapid, 3, true);
			// 將任務進度提升為2
			pc.getQuest().set_step(CrownLv30_1.QUEST.get_id(), 2);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
