package com.lineage.data.npc.other;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv15_1;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 亞南<BR>
 * 70802<BR>
 * 瑞奇的抵抗 (騎士15級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Aanon extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Aanon.class);

	/**
	 *
	 */
	private Npc_Aanon() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Aanon();
	}

	@Override
	public int type() {
		return 19;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 歡迎光臨，這次又是什麼東西壞掉啦？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));

			} else if (pc.isKnight()) {// 騎士
				// 任務已經完成
				if (pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
					// 唉呦！歡迎歡迎，這次又是什麼裝備壞了啊？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));
					// 如果你要修理紅騎士武器的話，隨時歡迎你。(包含印記任務)
					// pc.sendPackets(new S_NPCTalkReturn(npc.getId(),
					// "aanoncg"));

				} else {
					// 等級達成要求
					if (pc.getLevel() >= KnightLv15_1.QUEST.get_questlevel()) {

						if (pc.getQuest().get_step(KnightLv15_1.QUEST.get_id()) == 2) {
							// 有關古老的交易文件
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon4"));

						} else {
							// 唉呦！歡迎歡迎，這次又是什麼裝備壞了啊？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));
						}

					} else {
						// 唉呦！歡迎歡迎，這次又是什麼裝備壞了啊？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));
					}
				}

			} else if (pc.isElf()) {// 精靈
				// 歡迎光臨，這次又是什麼東西壞掉啦？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));

			} else if (pc.isWizard()) {// 法師
				// 歡迎光臨，這次又是什麼東西壞掉啦？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 歡迎光臨，這次又是什麼東西壞掉啦？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 歡迎光臨，這次又是什麼東西壞掉啦？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 歡迎光臨，這次又是什麼東西壞掉啦？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isKnight()) {// 騎士
			if (cmd.equalsIgnoreCase("request hood of red knight")) {// 交換紅騎士頭巾
				// 任務已經完成
				if (pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
					return;
				}
				// 任務進度達到2
				if (pc.getQuest().get_step(KnightLv15_1.QUEST.get_id()) == 2) {
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, new int[] { 40540, 40601, 20005 }, // 任務完成需要物件(古老的交易文件
																							// x
																							// 1
																							// 龍龜甲
																							// x
																							// 1
																							// 騎士頭巾
																							// x
																							// 1)
							new int[] { 1, 1, 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, new int[] { 40540, 40601, 20005 },
								new int[] { 1, 1, 1 }, // 需要古老的交易文件
														// x
														// 1
														// 龍龜甲
														// x
														// 1
														// 騎士頭巾
														// x
														// 1
								new int[] { 20027 }, 1, new int[] { 1 });// 給予紅騎士頭巾
																			// x
																			// 1
						// 將任務設置結束
						QuestClass.get().endQuest(pc, KnightLv15_1.QUEST.get_id());
						isCloseList = true;
					}

				} else {
					// 唉呦！歡迎歡迎，這次又是什麼裝備壞了啊？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon1"));
				}
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	@Override
	public int workTime() {
		return 8;
	}

	@Override
	public void work(final L1NpcInstance npc) {
		if (npc.getStatus() != 4) {
			npc.setStatus(4);
			npc.broadcastPacketAll(new S_NPCPack(npc));
		}
		final Work work = new Work(npc);
		work.getStart();
	}

	private class Work implements Runnable {

		private final L1NpcInstance _npc;

		private final int _spr;

		private Work(final L1NpcInstance npc) {
			_npc = npc;
			_spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
		}

		/**
		 * 啟動線程
		 */
		public void getStart() {
			GeneralThreadPool.get().schedule(this, 10);
		}

		@Override
		public void run() {
			try {
				for (int i = 0; i < 5; i++) {
					_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 30));
					Thread.sleep(_spr);
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
