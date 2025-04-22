package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv15_1;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 吉姆<BR>
 * 70555<BR>
 * 說明:拯救被幽禁的吉姆 (騎士30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Jim extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Jim.class);

	private Npc_Jim() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Jim();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.getTempCharGfx() != 2374) {// 骷髏
				// #$@$%#$%．．．#$%@#．．．(吉姆說著你聽不懂的骷髏語言)
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim1"));
				return;
			}

			if (pc.isCrown()) {// 王族
				// 長久的等待...等待有人來幫我解除詛咒
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));

			} else if (pc.isKnight()) {// 騎士
				// LV30任務已經完成
				if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
					// 你能夠忍受這樣長時間的所有試練，我感到很敬佩，願殷海薩祝福你。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimcg"));
					return;
				}
				if (pc.getInventory().checkItem(40529)) { // 已經具有物品 (感謝信)
					// 你能夠忍受這樣長時間的所有試練，我感到很敬佩，願殷海薩祝福你。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimcg"));
					return;
				}
				// 任務尚未開始
				if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
					// 長久的等待...等待有人來幫我解除詛咒
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));

				} else {// 任務已經開始
					if (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id()) < 2) {
						// 提升任務進度
						pc.getQuest().set_step(KnightLv30_1.QUEST.get_id(), 2);
					}
					// 有關吉姆的詛咒
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim2"));
				}

			} else if (pc.isElf()) {// 精靈
				// 長久的等待...等待有人來幫我解除詛咒
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));

			} else if (pc.isWizard()) {// 法師
				// 長久的等待...等待有人來幫我解除詛咒
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 長久的等待...等待有人來幫我解除詛咒
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 長久的等待...等待有人來幫我解除詛咒
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 長久的等待...等待有人來幫我解除詛咒
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));

			} else {
				// 長久的等待...等待有人來幫我解除詛咒
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isKnight()) {// 騎士
			// LV15任務未完成
			if (!pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
				return;
			}
			// LV30任務已經完成
			if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
				return;
			}
			// 任務尚未開始
			if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
				isCloseList = true;

			} else {// 任務已經開始
				if (cmd.equalsIgnoreCase("request letter of gratitude")) {// 遞給返生藥水
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, new int[] { 40607,// 返生藥水
																			// x
																			// 1
					}, new int[] { 1, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						// 關閉對話窗
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, new int[] { 40607,// 返生藥水
						}, new int[] { 1, }, new int[] { 40529,// 感謝信 x
																// 1
						}, 1, new int[] { 1, });// 給予

						// 你能夠忍受這樣長時間的所有試練，我感到很敬佩，願殷海薩祝福你。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimcg"));
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
}
