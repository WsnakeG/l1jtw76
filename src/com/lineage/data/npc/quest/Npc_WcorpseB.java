package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.IllusionistLv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 白蟻的屍體(B)<BR>
 * 80144<BR>
 * 說明:白蟻出現的理由 (幻術士45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_WcorpseB extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_WcorpseB.class);

	private Npc_WcorpseB() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_WcorpseB();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// ...................
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));

			} else if (pc.isKnight()) {// 騎士
				// ...................
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));

			} else if (pc.isElf()) {// 精靈
				// ...................
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));

			} else if (pc.isWizard()) {// 法師
				// ...................
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// ...................
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// ...................
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 任務開始狀態
				if (pc.getQuest().isStart(IllusionistLv45_1.QUEST.get_id())) {
					if (!pc.getInventory().checkItem(49195)) { // 不具有物品(第二次記憶碎片
																// 49195)
						// 使用時空裂痕水晶(綠色)
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse5"));

					} else {
						// ...................
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));
					}

				} else {
					// ...................
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));
				}

			} else {
				// ...................
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (cmd.equalsIgnoreCase("a")) {// 使用時空裂痕水晶(綠色)
			// 任務開始狀態
			if (pc.getQuest().isStart(IllusionistLv45_1.QUEST.get_id())) {
				if (!pc.getInventory().checkItem(49195)) { // 不具有物品(第二次記憶碎片
															// 49195)
					final L1ItemInstance item = pc.getInventory().checkItemX(49192, 1);// 時空裂痕水晶(綠色)-3
																						// 49192
					if (item != null) {
						pc.getInventory().removeItem(item, 1);
						// 使用時空裂痕水晶(綠色) ，在第二個白蟻的屍體中完成取得之前所留下的記憶碎片。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse6"));
						// 給予任務道具(第二次記憶碎片 49195)
						CreateNewItem.createNewItem(pc, 49195, 1);
					}

				} else {
					// 沒有任何事情發生
					pc.sendPackets(new S_ServerMessage(79));
					isCloseList = true;
				}

			} else {
				isCloseList = true;
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
