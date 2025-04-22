package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv15_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 瑪勒巴<BR>
 * 71258<BR>
 * 說明:遠征隊的遺物 (妖精15級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Marba extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Marba.class);

	private Npc_Marba() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Marba();
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
				// 眠龍洞穴漸漸被污染了...都是你們這些傢伙幹的吧！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba1"));

			} else {
				if (pc.isCrown()) {// 王族
					// 眠龍洞穴是很久以前在與人類之間的戰爭中所建造的最終基地。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));

				} else if (pc.isKnight()) {// 騎士
					// 眠龍洞穴是很久以前在與人類之間的戰爭中所建造的最終基地。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));

				} else if (pc.isElf()) {// 精靈
					// 任務已經完成
					if (pc.getQuest().isEnd(ElfLv15_1.QUEST.get_id())) {
						// 喔...你來啦？我不會忘掉你那迅捷又果敢的行動。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba15"));

					} else {
						// 等級達成要求
						if (pc.getLevel() >= ElfLv15_1.QUEST.get_questlevel()) {
							// 任務進度
							switch (pc.getQuest().get_step(ElfLv15_1.QUEST.get_id())) {
							case 0:// 任務尚未開始
									// 大事不妙了...眠龍洞穴被污染了！
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba3"));
								break;

							case 1:// 達到1
									// 你就去找阿拉斯看看吧...他人在眠龍洞穴前面。
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba6"));
								break;

							case 2:// 達到2
									// 阿拉斯在眠龍洞穴前面等待著兒子的消息...
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba19"));
								break;

							case 3:// 達到3
									// 聽說阿拉斯拜託你幫他找兒子的遺物是吧？
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba19"));
								break;

							case 4:// 達到4
									// 交出阿拉斯的信
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba17"));
								break;

							case 5:// 達到5
									// 又有什麼要修理的嗎？
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba22"));
								break;
							}

						} else {
							// 眠龍洞穴是很久以前在與人類之間的戰爭中所建造的最終基地。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));
						}
					}

				} else if (pc.isWizard()) {// 法師
					// 眠龍洞穴是很久以前在與人類之間的戰爭中所建造的最終基地。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));

				} else if (pc.isDarkelf()) {// 黑暗精靈
					// 眠龍洞穴是很久以前在與人類之間的戰爭中所建造的最終基地。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));

				} else if (pc.isDragonKnight()) {// 龍騎士
					// 眠龍洞穴是很久以前在與人類之間的戰爭中所建造的最終基地。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));

				} else if (pc.isIllusionist()) {// 幻術師
					// 眠龍洞穴是很久以前在與人類之間的戰爭中所建造的最終基地。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));

				} else {
					// 眠龍洞穴是很久以前在與人類之間的戰爭中所建造的最終基地。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isElf()) {// 精靈
			// 任務進度
			switch (pc.getQuest().get_step(ElfLv15_1.QUEST.get_id())) {
			case 0:// 任務尚未開始
				if (cmd.equals("A")) {// 接受瑪勒巴的信
					CreateNewItem.createNewItem(pc, 40637, 1);// 瑪勒巴的信
					// 將任務設置為執行中
					QuestClass.get().startQuest(pc, ElfLv15_1.QUEST.get_id());
					isCloseList = true;
				}
				break;

			case 1:// 達到1
				break;

			case 2:// 達到2
				break;

			case 3:// 達到3
				break;

			case 4:// 達到4
				if (cmd.equals("B")) {// 交出阿拉斯的信
					final L1ItemInstance item = pc.getInventory().checkItemX(40665, 1);// 需要的物件確認
					if (item != null) {
						pc.getInventory().removeItem(item, 1);// 刪除道具
					}
					// 將任務進度提升為5
					pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 5);
					// 需要哪些材料
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba7"));
				}
				break;

			case 5:// 達到5
				if (cmd.equalsIgnoreCase("1")) {// 修理弓
					final int[] srcid = new int[] { 40699, // 遠征隊弓
							40518, // 品質藍水晶3個
							40516, // 品質綠水晶3個
							40517, // 品質紅水晶3個
							40512, // 污濁安特的樹枝5個
							40495,// 10個米索莉線
					};
					final int[] srccount = new int[] { 1, 3, 3, 3, 5, 10, };

					getItem(pc, npc, srcid, srccount, 214);// ID．妖精弓

				} else if (cmd.equalsIgnoreCase("2")) {// 修理頭盔
					final int[] srcid = new int[] { 40698, // 遠征隊頭盔
							40501, // 紅水晶3個
							40492, // 綠水晶3個
							40523, // 白水晶3個
							40510, // 污濁安特的樹皮5個
							40495,// 10個米索莉線
					};
					final int[] srccount = new int[] { 1, 3, 3, 3, 5, 10, };

					getItem(pc, npc, srcid, srccount, 20389);// ID．妖精頭盔

				} else if (cmd.equalsIgnoreCase("3")) {// 修理金甲
					final int[] srcid = new int[] { 40693, // 遠征隊金甲
							40518, // 品質藍水晶5個
							40516, // 品質綠水晶5個
							40517, // 品質紅水晶5個
							40510, // 污濁安特的樹皮6個
							40508,// 奧里哈魯根100個
					};
					final int[] srccount = new int[] { 1, 5, 5, 5, 6, 100, };

					getItem(pc, npc, srcid, srccount, 20393);// ID．妖精金甲

				} else if (cmd.equalsIgnoreCase("4")) {// 修理腕甲
					final int[] srcid = new int[] { 40697, // 遠征隊腕甲
							40522, // 藍水晶5個
							40523, // 白水晶5個
							40500, // 紫水晶5個
							40510, // 污濁安特的樹皮3個
							40495,// 米索莉線10個
					};
					final int[] srccount = new int[] { 1, 5, 5, 5, 3, 10, };

					getItem(pc, npc, srcid, srccount, 20409);// ID．妖精腕甲

				} else if (cmd.equalsIgnoreCase("5")) {// 修理鋼靴
					final int[] srcid = new int[] { 40695, // 遠征隊鋼靴
							40522, // 藍水晶5個
							40523, // 白水晶5個
							40500, // 紫水晶5個
							40510, // 污濁安特的樹皮3個
							40508,// 奧里哈魯根50個
					};
					final int[] srccount = new int[] { 1, 5, 5, 5, 3, 50, };

					getItem(pc, npc, srcid, srccount, 20406);// ID．妖精鋼靴

				} else if (cmd.equalsIgnoreCase("6")) {// 修理斗篷
					// <P>要修理他的話需要遠征隊斗蓬和綠水晶1個、紫水晶1個、紅水晶1個、米索莉線10個，以及精靈粉末35個，這樣一來我就可以幫你重製為專屬於您的披風。</p>

					final int[] srcid = new int[] { 40694, // 遠征隊斗蓬
							40492, // 綠水晶1個
							40500, // 紫水晶1個
							40501, // 紅水晶1個
							40495, // 米索莉線10個
							40520,// 精靈粉末35個
					};
					final int[] srccount = new int[] { 1, 1, 1, 1, 10, 35, };

					getItem(pc, npc, srcid, srccount, 20401);// ID．妖精斗篷
				}
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

	/**
	 * 交換物品
	 * 
	 * @param pc
	 * @param npc
	 * @param srcid
	 * @param srccount
	 * @param itemid
	 */
	private void getItem(final L1PcInstance pc, final L1NpcInstance npc, final int[] srcid,
			final int[] srccount, final int itemid) {

		// 傳回可交換道具數小於1(需要物件不足)
		if (CreateNewItem.checkNewItem(pc, srcid, srccount) < 1) {
			// 所需要的物品不足，準備好了之後再來找我吧。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba16"));

		} else {// 需要物件充足
			// 收回任務需要物件 給予任務完成物件
			CreateNewItem.createNewItem(pc, srcid, srccount, new int[] { itemid }, // ID．?
					1, new int[] { 1 });// 給予

			if (checkItem(pc)) {
				// 將任務設置為結束
				QuestClass.get().endQuest(pc, ElfLv15_1.QUEST.get_id());
				// 喔...剛好完成適合你的裝備了
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba21"));
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
		final int[] itemids = new int[] { 214, // ID．妖精弓
				20401, // ID．妖精斗篷
				20409, // ID．妖精腕甲
				20393, // ID．妖精金甲
				20406, // ID．妖精鋼靴
				20389,// ID．妖精頭盔
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
