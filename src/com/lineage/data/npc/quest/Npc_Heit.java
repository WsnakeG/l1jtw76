package com.lineage.data.npc.quest;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_NpcChat;

/**
 * 希托 <BR>
 * 70724<BR>
 * 說明:妖精的任務 (妖精45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Heit extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Heit.class);

	private Npc_Heit() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Heit();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.getLawful() < 0) {// 邪惡
				final Random random = new Random();
				if (random.nextInt(100) < 20) {
					npc.broadcastPacketX8(new S_NpcChat(npc, "$4991"));
				}
				return;
			}
			if (pc.isCrown()) {// 王族
				// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));

			} else if (pc.isKnight()) {// 騎士
				// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));

			} else if (pc.isElf()) {// 精靈
				// LV45任務已經完成
				if (pc.getQuest().isEnd(ElfLv45_1.QUEST.get_id())) {
					// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= ElfLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(ElfLv45_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));
						break;

					case 1:// 達到1(任務開始)
							// 詢問需要幫助的事情
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit1"));
						break;

					case 2:// 達到2
					case 3:// 達到3
					case 4:// 達到4
							// 遞給藍色長笛
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit2"));
						break;

					case 5:// 達到5
							// 關於水之豎琴
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit3"));
						break;

					default:// 其他
						// 外婆以前曾告訴過我水之豎琴的事情。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit5"));
						break;
					}

				} else {
					// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));
				}

			} else if (pc.isWizard()) {// 法師
				// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));

			} else {
				// 雖然說吉普賽人受到一般人的輕視與虐待...但他們也是一個珍貴的生命體！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit4"));
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
				case 1:// 達到1
					if (cmd.equalsIgnoreCase("quest 15 heit2")) {// 詢問需要幫助的事情
						// 提升任務進度
						pc.getQuest().set_step(ElfLv45_1.QUEST.get_id(), 2);
						// 遞給藍色長笛
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit2"));
					}
					break;

				case 2:// 達到2
				case 3:// 達到3
				case 4:// 達到4
					if (cmd.equalsIgnoreCase("request mystery shell")) {// 遞給藍色長笛
						final L1ItemInstance item = pc.getInventory().checkItemX(40602, 1);// 藍色長笛
																							// x
																							// 1

						if (item != null) {
							// 刪除道具(藍色長笛 x 1)
							pc.getInventory().removeItem(item, 1);
							// 提升任務進度
							pc.getQuest().set_step(ElfLv45_1.QUEST.get_id(), 5);
							// 謝謝你！幫我們找回藍色長笛。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit3"));

						} else {
							// 需要物件不足
							isCloseList = true;
						}
					}
					break;

				case 5:// 達到5
					if (cmd.equalsIgnoreCase("quest 17 heit5")) {// 關於水之豎琴
						if (pc.getInventory().checkItem(40566)) { // 已經具有物品
							isCloseList = true;

						} else {
							// 提升任務進度
							pc.getQuest().set_step(ElfLv45_1.QUEST.get_id(), 6);
							// 取得任務道具 (神秘貝殼)
							CreateNewItem.getQuestItem(pc, npc, 40566, 1);
							// 外婆以前曾告訴過我水之豎琴的事情。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "heit5"));
						}
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
}
