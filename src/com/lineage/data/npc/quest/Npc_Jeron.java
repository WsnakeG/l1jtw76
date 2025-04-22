package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ALv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 騎士團長 帝倫<BR>
 * 71199<BR>
 * 說明:毒蛇之牙的名號(全職業45級任務)
 * 
 * @author dexc
 */
public class Npc_Jeron extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Jeron.class);

	private Npc_Jeron() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Jeron();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 任務已經完成
			if (pc.getQuest().isEnd(ALv45_1.QUEST.get_id())) {
				// 傭兵隊長多文介紹你來的？我要怎麼相信你呢?
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron10"));

			} else {
				// 等級達成要求
				if (pc.getLevel() >= ALv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(ALv45_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
					case 1:
						// 傭兵隊長多文介紹你來的？我要怎麼相信你呢?
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron10"));
						break;

					case 2:// 達到2
							// 你來找我幹麼?
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron1"));
						break;

					case 3:// 達到3
							// 我不久前才把書給你了吧。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron7"));
						break;

					default:// 達到2以上
						// 傭兵隊長多文介紹你來的？我要怎麼相信你呢?
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron10"));
						break;
					}

				} else {
					// 傭兵隊長多文介紹你來的？我要怎麼相信你呢?
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron10"));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		int[] items = null;
		int[] counts = null;
		int[] gitems = null;
		int[] gcounts = null;

		if (cmd.equalsIgnoreCase("A")) {// 我是傭兵團長多文介紹過來的。
			items = new int[] { 41340 };// 傭兵團長多文的推薦書 41340
			counts = new int[] { 1 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 傭兵隊長多文介紹你來的？我要怎麼相信你呢?
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron10"));
				return;
			}
			// 請告訴我關於毒蛇之牙的事情
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron2"));

		} else if (cmd.equalsIgnoreCase("B")) {// 花100萬金幣買下這本書
			items = new int[] { 40308 };
			counts = new int[] { 1000000 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 你沒有百萬金幣...如果沒有錢，可以聽我一個要求嗎
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron8"));
				return;
			}

			gitems = new int[] { 41341 };// 帝倫的教本
			gcounts = new int[] { 1 };

			final L1ItemInstance item = pc.getInventory().checkItemX(41340, 1);// 傭兵團長多文的推薦書
																				// 41340
			if (item != null) {
				pc.getInventory().removeItem(item, 1);
			}
			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, 1, gcounts);// 給予

			// 將任務進度提升為3
			pc.getQuest().set_step(ALv45_1.QUEST.get_id(), 3);
			// 哈哈哈哈。這是你的了。算你厲害。不錯。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron6"));

		} else if (cmd.equalsIgnoreCase("C")) {// 拿出梅杜莎之血。
			items = new int[] { 41342 };// 梅杜莎之血 x 1 41342
			counts = new int[] { 1 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 你可以梅杜莎之血來換。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron9"));
				return;
			}

			gitems = new int[] { 41341 };// 帝倫的教本
			gcounts = new int[] { 1 };

			final L1ItemInstance item = pc.getInventory().checkItemX(41340, 1);// 傭兵團長多文的推薦書
																				// 41340
			if (item != null) {
				pc.getInventory().removeItem(item, 1);
			}

			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, 1, gcounts);// 給予

			// 將任務進度提升為3
			pc.getQuest().set_step(ALv45_1.QUEST.get_id(), 3);
			// 嗯~ 太棒了。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jeron5"));

		} else {
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
