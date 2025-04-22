package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv15_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 阿拉斯<BR>
 * 71259<BR>
 * 說明:遠征隊的遺物 (妖精15級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Aras extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Aras.class);

	private Npc_Aras() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Aras();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 正義質低於500
			if (pc.getLawful() < -500) {
				// 嬴嬴...頂 嬴菟...頂 嬴菟..
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras12"));

			} else {
				if (pc.isCrown()) {// 王族
					// ..什麼？我現在沒有心情跟你閒聊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));

				} else if (pc.isKnight()) {// 騎士
					// ..什麼？我現在沒有心情跟你閒聊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));

				} else if (pc.isElf()) {// 精靈
					// 任務已經完成
					if (pc.getQuest().isEnd(ElfLv15_1.QUEST.get_id())) {
						// 你不是幫我找回兒子遺物的人嗎？我想有你在，我兒子應該也能夠安心永眠了...
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras9"));

					} else {
						// 等級達成要求
						if (pc.getLevel() >= ElfLv15_1.QUEST.get_questlevel()) {
							// 任務進度
							switch (pc.getQuest().get_step(ElfLv15_1.QUEST.get_id())) {
							case 0:// 任務尚未開始
									// 明明叫你不要去那裡...
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras7"));
								break;

							case 1:// 達到1
									// 我幫你找回來
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras1"));
								break;

							case 2:// 達到2
									// 有沒有找到什麼？
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras3"));
								break;

							case 3:// 達到3
									// 謝謝...謝謝...已將全部找回我兒子的遺物
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras10"));
								break;

							case 4:// 達到4
									// 趕快將這封信交給瑪勒巴！
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras13"));
								break;

							case 5:// 達到5
									// 有去找過瑪勒巴大人嗎？
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras8"));
								break;
							}

						} else {
							// ..什麼？我現在沒有心情跟你閒聊。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));
						}
					}

				} else if (pc.isWizard()) {// 法師
					// ..什麼？我現在沒有心情跟你閒聊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));

				} else if (pc.isDarkelf()) {// 黑暗精靈
					// ..什麼？我現在沒有心情跟你閒聊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));

				} else if (pc.isDragonKnight()) {// 龍騎士
					// ..什麼？我現在沒有心情跟你閒聊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));

				} else if (pc.isIllusionist()) {// 幻術師
					// ..什麼？我現在沒有心情跟你閒聊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));

				} else {
					// ..什麼？我現在沒有心情跟你閒聊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		final boolean isCloseList = false;

		if (pc.isElf()) {// 精靈
			// 任務進度
			switch (pc.getQuest().get_step(ElfLv15_1.QUEST.get_id())) {
			case 0:// 任務尚未開始
				break;

			case 1:// 達到1
				if (cmd.equalsIgnoreCase("A")) {// 我幫你找回來
					final L1ItemInstance item = pc.getInventory().checkItemX(40637, 1);// 瑪勒巴的信
																						// x
																						// 1
					if (item != null) {
						pc.getInventory().removeItem(item, 1);// 刪除道具
					}
					CreateNewItem.createNewItem(pc, 40664, 1);// 阿拉斯的護身符 x 1
					// 將任務進度提升為2
					pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 2);

					// 我真的不知道該說些什麼來表達我的感激之意
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras2"));
				}
				break;

			case 2:// 達到2
				try {
					// 數字選項
					if (cmd.matches("[0-9]+")) {
						status2(pc, npc, Integer.valueOf(cmd).intValue());
					}

				} catch (final Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
				break;

			case 3:// 達到3
				if (cmd.equalsIgnoreCase("B")) {// 收到了信
					final L1ItemInstance item = pc.getInventory().checkItemX(40664, 1);// 阿拉斯的護身符
																						// x
																						// 1
					if (item != null) {
						pc.getInventory().removeItem(item, 1);// 刪除道具
						// 趕快將這封信交給瑪勒巴！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras13"));

					} else {
						// 啊...你把護身符弄丟了！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras14"));
					}
					CreateNewItem.createNewItem(pc, 40665, 1);// 給予阿拉斯的信 x 1
					// 將任務進度提升為4
					pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 4);
				}
				break;

			case 4:// 達到4
				break;

			case 5:// 達到5
				break;
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 達到2(交換遺物)
	 * 
	 * @param pc
	 * @param npc
	 * @param intValue
	 */
	private void status2(final L1PcInstance pc, final L1NpcInstance npc, final int intValue) {
		switch (intValue) {
		case 1:// 找到了污濁弓
				// 污濁的弓 → 遠征隊弓
			getItem(pc, npc, 40684, 40699);
			break;

		case 2:// 找到了污濁頭盔
				// 污濁的頭盔 → 遠征隊頭盔
			getItem(pc, npc, 40683, 40698);
			break;

		case 3:// 找到了污濁金甲
				// 污濁的金甲 → 遠征隊金甲
			getItem(pc, npc, 40679, 40693);
			break;

		case 4:// 找到了污濁手套
				// 污濁的腕甲 → 遠征隊腕甲
			getItem(pc, npc, 40682, 40697);
			break;

		case 5:// 找到了污濁鋼靴
				// 污濁的鋼靴 → 遠征隊鋼靴
			getItem(pc, npc, 40681, 40695);
			break;

		case 6:// 找到了污濁斗蓬
				// 污濁斗篷 → 遠征隊斗篷
			getItem(pc, npc, 40680, 40694);
			break;

		case 7:// 找到了遠征隊的所有遺物
				// 需要物件不足
			if (CreateNewItem.checkNewItem(pc,
					new int[] { 40684, // 污濁的弓
							40683, // 污濁的頭盔
							40679, // 污濁的金甲
							40682, // 污濁的腕甲
							40681, // 污濁的鋼靴
							40680,// 污濁斗篷
					}, new int[] { 1, // 污濁的弓 x 1
							1, // 污濁的頭盔 x 1
							1, // 污濁的金甲 x 1
							1, // 污濁的腕甲 x 1
							1, // 污濁的鋼靴 x 1
							1,// 污濁斗篷 x 1
					}) < 1) {// 傳回可交換道具數小於1(需要物件不足)

				// 謝謝...不過這好像不是我兒子的遺物...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras5"));

			} else {// 需要物件充足
				// 收回任務需要物件 給予任務完成物件
				CreateNewItem.createNewItem(pc,
						new int[] { 40684, // 污濁的弓
								40683, // 污濁的頭盔
								40679, // 污濁的金甲
								40682, // 污濁的腕甲
								40681, // 污濁的鋼靴
								40680,// 污濁斗篷
						}, new int[] { 1, // 污濁的弓 x 1
								1, // 污濁的頭盔 x 1
								1, // 污濁的金甲 x 1
								1, // 污濁的腕甲 x 1
								1, // 污濁的鋼靴 x 1
								1,// 污濁斗篷 x 1
						}, new int[] { 40699, // 遠征隊弓
								40698, // 遠征隊頭盔
								40693, // 遠征隊金甲
								40697, // 遠征隊腕甲
								40695, // 遠征隊鋼靴
								40694,// 遠征隊斗篷
						}, 1, new int[] { 1, // 遠征隊弓 x 1
								1, // 遠征隊頭盔 x 1
								1, // 遠征隊金甲 x 1
								1, // 遠征隊腕甲 x 1
								1, // 遠征隊鋼靴 x 1
								1,// 遠征隊斗篷 x 1
						});// 給予

				// 將任務進度提升為3
				pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 3);

				// 謝謝...謝謝...已將全部找回我兒子的遺物
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras10"));
			}
			break;
		}
	}

	/**
	 * 交換物品
	 * 
	 * @param pc
	 * @param npc
	 * @param srcid
	 * @param getid
	 */
	private void getItem(final L1PcInstance pc, final L1NpcInstance npc, final int srcid, final int getid) {
		// 需要物件不足
		if (CreateNewItem.checkNewItem(pc, srcid, // 任務完成需要物件
				1) < 1) {// 傳回可交換道具數小於1(需要物件不足)

			// 謝謝...不過這好像不是我兒子的遺物...
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras5"));

		} else {// 需要物件充足
			// 收回任務需要物件 給予任務完成物件
			CreateNewItem.createNewItem(pc, srcid, 1, // 需要 x 1
					getid, 1);// 給予 x 1

			if (checkItem(pc)) {
				// 將任務進度提升為3
				pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 3);

				// 謝謝...謝謝...已將全部找回我兒子的遺物
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras10"));

			} else {
				// 這裡也有找到其他東西
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras4"));
			}
		}
	}

	/**
	 * 檢查PC背包物件
	 * 
	 * @param pc
	 * @return 物品已經齊全
	 */
	private boolean checkItem(final L1PcInstance pc) {
		int i = 0;
		final int[] itemids = new int[] { 40699, // 遠征隊弓
				40698, // 遠征隊頭盔
				40693, // 遠征隊金甲
				40697, // 遠征隊腕甲
				40695, // 遠征隊鋼靴
				40694,// 遠征隊斗篷
		};
		for (final int itemid : itemids) {
			final L1ItemInstance item = pc.getInventory().checkItemX(itemid, 1);// 需要的物件確認
			if (item != null) {
				i++;
			}
		}
		if (i >= 6) {// 物品已經齊全
			return true;

		} else {
			return false;
		}
	}
}
