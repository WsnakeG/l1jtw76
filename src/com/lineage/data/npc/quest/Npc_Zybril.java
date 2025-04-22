package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv45_2;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 神官知布烈<BR>
 * 71257<BR>
 * 說明:妖精族傳說中的弓 (妖精45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Zybril extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Zybril.class);

	private Npc_Zybril() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Zybril();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你有帶精靈之淚？你是誰阿
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril16"));

			} else if (pc.isKnight()) {// 騎士
				// 你有帶精靈之淚？你是誰阿
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril16"));

			} else if (pc.isElf()) {// 精靈
				// LV45任務已經完成
				if (pc.getQuest().isEnd(ElfLv45_2.QUEST.get_id())) {
					// 你還有其他的事情要找我嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril19"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= ElfLv45_2.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(ElfLv45_2.QUEST.get_id())) {
					case 0:// 任務尚未開始
					case 1:// 達到1(任務開始)
							// 趁著莎爾的詛咒還沒越來越深之前趕快救他吧。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril15"));
						break;

					case 2:// 達到2
							// 有什麼事嗎
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril1"));
						break;

					case 3:// 達到3
							// 我將你需要的材料帶回了
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril7"));
						break;

					case 4:// 達到4
							// 拿出精靈之淚10個。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril8"));
						break;

					case 5:// 達到5
							// 莎爾之戒 41349
						if (pc.getInventory().checkItem(41349, 1)) {
							// 拿出莎爾的戒指
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril18"));

						} else {
							// 好，祝你好運。願伊娃的祝福陪伴著你...
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril17"));
						}
						break;

					default:// 其他
						// 你還有其他的事情要找我嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril19"));
						break;
					}

				} else {
					// 你有帶精靈之淚？你是誰阿
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril16"));
				}

			} else if (pc.isWizard()) {// 法師
				// 你有帶精靈之淚？你是誰阿
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril16"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你有帶精靈之淚？你是誰阿
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril16"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你有帶精靈之淚？你是誰阿
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril16"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你有帶精靈之淚？你是誰阿
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril16"));

			} else {
				// 你有帶精靈之淚？你是誰阿
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril16"));
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
			if (pc.getQuest().isEnd(ElfLv45_2.QUEST.get_id())) {
				return;
			}
			// 任務進度
			switch (pc.getQuest().get_step(ElfLv45_2.QUEST.get_id())) {
			case 0:// 達到0
			case 1:// 達到1
				break;

			case 2:// 達到2
				if (cmd.equals("A")) {// 我有帶羅賓孫的信
					// 羅賓孫的推薦書
					final L1ItemInstance item = pc.getInventory().findItemId(41348);
					if (item != null) {
						pc.getInventory().removeItem(item, 1);// 刪除道具(羅賓孫的推薦書)
						// 提升任務進度
						pc.getQuest().set_step(ElfLv45_2.QUEST.get_id(), 3);
						// 羅賓孫？啊! 那個做弓的人阿？然後呢？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril3"));

					} else {
						// 羅賓孫？那是誰啊...啊! 精靈森林的那個弓匠啊
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril11"));
					}
				}
				break;

			case 3:// 達到3
				if (cmd.equals("B")) {// 我將你需要的材料帶回了
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc,
							// 品質鑽石,品質紅寶石,品質藍寶石,品質綠寶石
							new int[] { 40048, 40049, 40050, 40051 }, new int[] { 10, 10, 10, 10 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						// 去收集
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril12"));

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc,
								// 品質鑽石,品質紅寶石,品質藍寶石,品質綠寶石
								new int[] { 40048, 40049, 40050, 40051 }, new int[] { 10, 10, 10, 10 },
								new int[] { 41353 }, // 伊娃短劍 x 1
								1, new int[] { 1 });// 給予
													// 提升任務進度
						pc.getQuest().set_step(ElfLv45_2.QUEST.get_id(), 4);
						// 拿出精靈之淚10個。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril8"));
					}
				}
				break;

			case 4:// 達到4
				if (cmd.equals("C")) {// 拿出精靈之淚10個。
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc,
							// 精靈之淚,伊娃短劍
							new int[] { 40514, 41353 }, new int[] { 10, 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						// 需要 精靈之淚10個
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril13"));

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc,
								// 精靈之淚,伊娃短劍
								new int[] { 40514, 41353 }, new int[] { 10, 1 }, new int[] { 41354 }, // 伊娃的聖水
																										// x
																										// 1
								1, new int[] { 1 });// 給予
													// 提升任務進度
						pc.getQuest().set_step(ElfLv45_2.QUEST.get_id(), 5);
						// 謝謝你。我還想要再拜託你一件事情。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril9"));
					}
				}
				break;

			case 5:// 達到5
				if (cmd.equals("D")) {// 拿出莎爾的戒指
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc,
							// 莎爾的戒指
							new int[] { 41349 }, new int[] { 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						// 你要解開莎爾的詛咒嗎？我該怎麼相信你呢?
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril14"));

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc,
								// 精靈之淚,伊娃短劍
								new int[] { 41349 }, new int[] { 1 }, new int[] { 41351 }, // 月光之氣息
																							// x
																							// 1
								1, new int[] { 1 });// 給予
													// 提升任務進度
						pc.getQuest().set_step(ElfLv45_2.QUEST.get_id(), 6);
						// 請你接下 <font fg=ffffaf>月光之氣息</font>吧。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zybril10"));
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