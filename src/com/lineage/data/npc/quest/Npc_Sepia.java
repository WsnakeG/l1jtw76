package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv45_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.WorldQuest;

/**
 * 賽菲亞 <BR>
 * 50031<BR>
 * 說明:妖精的任務 (妖精45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Sepia extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Sepia.class);

	private Npc_Sepia() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Sepia();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族

			} else if (pc.isKnight()) {// 騎士

			} else if (pc.isElf()) {// 精靈
				// LV45任務已經完成
				if (pc.getQuest().isEnd(ElfLv45_1.QUEST.get_id())) {
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= ElfLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(ElfLv45_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
					case 1:// 達到1
							// 你是妖精...過去我也曾經是妖精！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "sepia2"));
						break;

					case 2:// 達到2
					case 3:// 達到3
							// 承受賽菲亞的罪行
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "sepia1"));
						break;

					default:// 其他
						break;
					}

				} else {
					// 你是妖精...過去我也曾經是妖精！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "sepia2"));
				}

			} else if (pc.isWizard()) {// 法師

			} else if (pc.isDarkelf()) {// 黑暗精靈

			} else if (pc.isDragonKnight()) {// 龍騎士

			} else if (pc.isIllusionist()) {// 幻術師

			} else {
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isElf()) {// 精靈
			// LV45任務已經完成
			if (pc.getQuest().isEnd(ElfLv45_1.QUEST.get_id())) {
				return;
			}
			// 任務尚未開始
			if (!pc.getQuest().isStart(ElfLv45_1.QUEST.get_id())) {
				isCloseList = true;

			} else {// 任務已經開始
				// 任務進度
				switch (pc.getQuest().get_step(ElfLv45_1.QUEST.get_id())) {
				case 2:// 達到2
				case 3:// 達到3
					if (cmd.equalsIgnoreCase("teleport sepia-dungen")) {// 承受賽菲亞的罪行
						// 提升任務進度
						pc.getQuest().set_step(ElfLv45_1.QUEST.get_id(), 3);
						staraQuest(pc, ElfLv45_1.MAPID);
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
	 * @param mapid 任務地圖編號
	 * @return
	 */
	public static void staraQuest(final L1PcInstance pc, final int mapid) {
		try {
			// 任務編號
			final int questid = ElfLv45_1.QUEST.get_id();

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
			L1Teleport.teleport(pc, 32745, 32872, (short) mapid, 0, true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
