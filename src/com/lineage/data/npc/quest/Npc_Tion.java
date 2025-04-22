package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ALv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 傭兵團長 多文<BR>
 * 71198<BR>
 * 說明:毒蛇之牙的名號(全職業45級任務)
 * 
 * @author dexc
 */
public class Npc_Tion extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Tion.class);

	private Npc_Tion() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Tion();
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
				// 哈哈哈。我早就知道你是個可造之才。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion8"));

			} else {
				// 等級達成要求
				if (pc.getLevel() >= ALv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(ALv45_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 你來找我幹麼?
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion1"));
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, ALv45_1.QUEST.get_id());
						break;

					case 1:// 達到1(任務開始)
							// 你來找我幹麼?
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion1"));
						break;

					case 2:// 達到2
					case 3:// 達到3
							// 給他看帝倫的教本
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion5"));
						break;

					case 4:// 達到4
							// 不錯嘛，從現在起我要正式測你的實力
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion6"));
						break;

					case 5:// 達到5
							// 來，這是最後一次了！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion7"));
						break;

					default:
						// 哈哈哈。我早就知道你是個可造之才。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion8"));
						break;
					}

				} else {
					// 你想要穿"訓練騎士披肩"嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion20"));
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

		if (cmd.equalsIgnoreCase("A")) {// 拿出亡者的信件5個
			items = new int[] { 41339 };
			counts = new int[] { 5 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 你竟然有勇氣加入毒蛇，我想你應該不會害怕海音惡靈才對?
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion9"));
				return;
			}

			gitems = new int[] { 41340 };
			gcounts = new int[] { 1 };

			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, 1, gcounts);// 給予
			// 還有兩下子嘛。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion4"));
			// 將任務進度提升為2
			pc.getQuest().set_step(ALv45_1.QUEST.get_id(), 2);

		} else if (cmd.equalsIgnoreCase("B")) {// 給他看帝倫的教本
			items = new int[] { 41341 };
			counts = new int[] { 1 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 帝倫的教本在哪裡呢？如果你還沒有，就到旅館附近去找找吧。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion10"));
				return;
			}
			// gitems = new int[]{41325};
			// gcounts = new int[]{1};
			// 聽說帝倫最近不知道怎麼了
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion5"));

		} else if (cmd.equalsIgnoreCase("C")) {// 拿出法利昂的血痕
			items = new int[] { 41343, 41341 };// 法利昂的血痕 x 1 41343
			counts = new int[] { 1, 1 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 還沒找到原因嗎?
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion12"));
				return;
			}
			gitems = new int[] { 21057 };// 訓練騎士披肩 (1) x 1 21057
			gcounts = new int[] { 1 };

			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, 1, gcounts);// 給予

			// 不錯嘛，從現在起我要正式測你的實力
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion6"));
			// 將任務進度提升為4
			pc.getQuest().set_step(ALv45_1.QUEST.get_id(), 4);

		} else if (cmd.equalsIgnoreCase("D")) {// 交付水中的水
			items = new int[] { 41344, 21057 };// 水中的水 x 1 41344
			counts = new int[] { 1, 1 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 不錯嘛，從現在起我要正式測你的實力
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion17"));
				return;
			}

			gitems = new int[] { 21058 };// 訓練騎士披肩 (2) x 1 21058
			gcounts = new int[] { 1 };

			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, 1, gcounts);// 給予

			// 來，這是最後一次了！
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion7"));
			// 將任務進度提升為5
			pc.getQuest().set_step(ALv45_1.QUEST.get_id(), 5);

		} else if (cmd.equalsIgnoreCase("E")) {// 交付酸性液體
			items = new int[] { 41345, 21058 };// 酸性液體 x 1 41345
			counts = new int[] { 1, 1 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 不錯嘛，從現在起我要正式測你的實力
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion18"));
				return;
			}

			gitems = new int[] { 21059 };// 毒蛇之牙披肩 x 1 21059
			gcounts = new int[] { 1 };

			// 收回需要物件 給予完成物件
			CreateNewItem.createNewItem(pc, items, counts, // 需要
					gitems, 1, gcounts);// 給予

			// 來，這是最後一次了！
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion8"));
			// 結束任務
			pc.getQuest().set_end(ALv45_1.QUEST.get_id());

		} else {
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
