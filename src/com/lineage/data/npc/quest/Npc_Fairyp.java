package com.lineage.data.npc.quest;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv30_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.WorldQuest;

/**
 * 精靈公主<BR>
 * 70853<BR>
 * <BR>
 * 說明:達克馬勒的威脅 (妖精30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Fairyp extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Fairyp.class);

	private static Random _random = new Random();

	private Npc_Fairyp() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Fairyp();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你好，我受到迷幻森林之母指示
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));

			} else if (pc.isKnight()) {// 騎士
				// 你好，我受到迷幻森林之母指示
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));

			} else if (pc.isElf()) {// 精靈
				// LV30任務已經完成
				if (pc.getQuest().isEnd(ElfLv30_1.QUEST.get_id())) {
					// 你好，我受到迷幻森林之母指示
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= ElfLv30_1.QUEST.get_questlevel()) {
					// 任務尚未開始
					if (!pc.getQuest().isStart(ElfLv30_1.QUEST.get_id())) {
						// 你好，我受到迷幻森林之母指示
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));

					} else {// 任務已經開始
						if (_random.nextInt(100) < 40) {
							// teleport darkmar-dungen
							// 嗨，你也是來參加迷幻森林之母所主持的成人儀式嗎？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp1"));

						} else {
							// teleport dark-elf-dungen
							// 嗨，你也是來參加迷幻森林之母所主持的成人儀式嗎？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp2"));
						}
					}

				} else {
					// 你好，我受到迷幻森林之母指示
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));
				}

			} else if (pc.isWizard()) {// 法師
				// 你好，我受到迷幻森林之母指示
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你好，我受到迷幻森林之母指示
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你好，我受到迷幻森林之母指示
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你好，我受到迷幻森林之母指示
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));

			} else {
				// 你好，我受到迷幻森林之母指示
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fairyp3"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isElf()) {// 精靈
			// 任務已經完成
			if (pc.getQuest().isEnd(ElfLv30_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("teleport darkmar-dungen")) {// 前往達克馬勒的隱身處
				staraQuest(pc, ElfLv30_1.MAPID_1);
				isCloseList = true;

			} else if (cmd.equalsIgnoreCase("teleport dark-elf-dungen")) {// 前往達克馬勒的隱身處
				staraQuest(pc, ElfLv30_1.MAPID_2);
				isCloseList = true;
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
	 * @param mapid 任務地圖編號
	 * @return
	 */
	public static void staraQuest(final L1PcInstance pc, final int mapid) {
		try {
			// 任務編號
			final int questid = ElfLv30_1.QUEST.get_id();

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

			// 傳送任務執行者
			L1Teleport.teleport(pc, 32744, 32794, (short) mapid, 5, true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
