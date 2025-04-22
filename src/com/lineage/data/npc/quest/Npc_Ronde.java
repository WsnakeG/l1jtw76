package com.lineage.data.npc.quest;

import static com.lineage.server.model.skill.L1SkillId.CKEW_LV50;
import static com.lineage.server.model.skill.L1SkillId.DE_LV30;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_BARLOG;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_YAHEE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv15_2;
import com.lineage.data.quest.DarkElfLv30_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 倫得<BR>
 * 70892<BR>
 * 說明:同族的背叛 (黑暗妖精30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Ronde extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Ronde.class);

	private Npc_Ronde() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Ronde();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 這裡只能容納身上流著黑暗妖精之血的人，其他種族只有死路一條。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde6"));

			} else if (pc.isKnight()) {// 騎士
				// 這裡只能容納身上流著黑暗妖精之血的人，其他種族只有死路一條。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde6"));

			} else if (pc.isElf()) {// 精靈
				// 這裡只能容納身上流著黑暗妖精之血的人，其他種族只有死路一條。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde6"));

			} else if (pc.isWizard()) {// 法師
				// 這裡只能容納身上流著黑暗妖精之血的人，其他種族只有死路一條。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde6"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// LV15任務未完成
				if (!pc.getQuest().isEnd(DarkElfLv15_2.QUEST.get_id())) {
					// 你的實力還不足以讓我放心的將事情託付給你，多去鍛練鍛練吧！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde7"));
					return;
				}
				// LV30任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv30_1.QUEST.get_id())) {
					// 從現在開始你不再是普通黑暗妖精
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde5"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= DarkElfLv30_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(DarkElfLv30_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 接受倫得的建議
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde1"));
						break;

					case 1:// 達到1(任務開始)
							// 交出秘密名單
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde2"));
						break;

					case 2:// 達到2
							// 死亡誓約40596
						if (pc.getInventory().checkItem(40596, 1)) {
							// 交出死亡誓約
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde4"));

						} else {
							// 接受古代刺客咒術
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde3"));
						}
						break;
					}

				} else {
					// 你的實力還不足以讓我放心的將事情託付給你，多去鍛練鍛練吧！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde7"));
				}

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 這裡只能容納身上流著黑暗妖精之血的人，其他種族只有死路一條。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde6"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 這裡只能容納身上流著黑暗妖精之血的人，其他種族只有死路一條。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde6"));

			} else {
				// 這裡只能容納身上流著黑暗妖精之血的人，其他種族只有死路一條。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde6"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isDarkelf()) {// 黑暗精靈
			// LV30任務已經完成
			if (pc.getQuest().isEnd(DarkElfLv30_1.QUEST.get_id())) {
				return;
			}
			// 任務進度
			switch (pc.getQuest().get_step(DarkElfLv30_1.QUEST.get_id())) {
			case 0:// 達到0
				if (cmd.equalsIgnoreCase("quest 13 ronde2")) {// 接受倫得的建議
					// 將任務設置為執行中
					QuestClass.get().startQuest(pc, DarkElfLv30_1.QUEST.get_id());
					// 交出秘密名單
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde2"));
				}
				break;

			case 1:// 達到1
				if (cmd.equalsIgnoreCase("request close list of assassination")) {// 交出秘密名單
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc,
							// 秘密名單
							new int[] { 40554 }, new int[] { 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc,
								// 秘密名單
								new int[] { 40554 }, new int[] { 1 }, new int[] { 40556 }, // 暗殺名單之袋
																							// x
																							// 1
								1, new int[] { 1 });// 給予
													// 提升任務進度
						pc.getQuest().set_step(DarkElfLv30_1.QUEST.get_id(), 2);
						// 我們已正確掌握到與反王勾結的黑暗妖精。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde3"));
					}
				}
				break;

			case 2:// 達到2
				if (cmd.equalsIgnoreCase("quest 15 ronde4")) {// 接受古代刺客咒術
					if (pc.hasSkillEffect(CKEW_LV50)) {
						pc.removeSkillEffect(CKEW_LV50);
					}
					if (pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
						pc.removeSkillEffect(STATUS_CURSE_YAHEE);
					}
					if (pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
						pc.removeSkillEffect(STATUS_CURSE_BARLOG);
					}
					pc.setSkillEffect(DE_LV30, 1500 * 1000);
					pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7245));
					// 1454:倫得施放的古代咒術力量環繞全身
					pc.sendPackets(new S_ServerMessage(1454));
					// 聽說與反王勾結的黑暗妖精藏身在人類的村莊裡。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde4"));

				} else if (cmd.equalsIgnoreCase("request rondebag")) {// 交出死亡誓約
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc,
							// 死亡誓約
							new int[] { 40596 }, new int[] { 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc,
								// 死亡誓約
								new int[] { 40596 }, new int[] { 1 }, new int[] { 40545 }, // 倫得之袋
																							// x
																							// 1
								1, new int[] { 1 });// 給予
													// 將任務設置為結束
						QuestClass.get().endQuest(pc, DarkElfLv30_1.QUEST.get_id());
						// 從現在開始你不再是普通黑暗妖精
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde5"));

						// 移除任務完成後多餘道具
						final int[] removeItemId = new int[] { 40557, // 暗殺名單(古魯丁村)
								40558, // 暗殺名單(奇巖村)
								40559, // 暗殺名單(亞丁城鎮)
								40560, // 暗殺名單(風木村)
								40561, // 暗殺名單(肯特村)
								40562, // 暗殺名單(海音村)
								40563,// 暗殺名單(燃柳村)
						};
						for (final int itemId : removeItemId) {
							final L1ItemInstance item = pc.getInventory().checkItemX(itemId, 1);
							if (item != null) {
								// 刪除道具
								pc.getInventory().removeItem(item, 1);
							}
						}
					}
				}
				break;

			default:// 其他
				isCloseList = true;
				break;
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