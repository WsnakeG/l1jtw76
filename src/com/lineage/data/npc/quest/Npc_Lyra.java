package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ALv15_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 萊拉<BR>
 * 70811<BR>
 * 擊退妖魔的契約(全職業15級任務)
 * 
 * @author dexc
 */
public class Npc_Lyra extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Lyra.class);

	private Npc_Lyra() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Lyra();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			boolean isOrg = false;
			if (pc.getTempCharGfx() == 3906) {// 妖魔
				isOrg = true;
			}
			if (pc.getTempCharGfx() == 3860) {// 妖魔弓箭手
				isOrg = true;
			}
			if (pc.getTempCharGfx() == 3864) {// 妖魔鬥士
				isOrg = true;
			}
			if (pc.getTempCharGfx() == 3866) {// 甘地妖魔
				isOrg = true;
			}
			if (pc.getTempCharGfx() == 3869) {// 都達瑪拉妖魔
				isOrg = true;
			}
			if (pc.getTempCharGfx() == 3868) {// 阿吐巴妖魔
				isOrg = true;
			}
			if (pc.getTempCharGfx() == 2323) {// 妖魔巡守
				isOrg = true;
			}

			if (isOrg) {
				// 你們有個叫『妖魔』的種族吧！他們已經幫你賺了這麼多錢，你還來搶我們的東西！！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "lyra11"));
				return;
			}

			// 任務已經完成
			if (pc.getQuest().isEnd(ALv15_1.QUEST.get_id())) {
				// 歡迎！你是一個冒險家嗎？或者是傭兵？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "lyra1"));

			} else {
				// 等級達成要求
				if (pc.getLevel() >= ALv15_1.QUEST.get_questlevel()) {
					if (pc.getQuest().isStart(ALv15_1.QUEST.get_id())) {
						// 如果你同意，你可以一直幫助我們直到戰爭結束嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "lyraev3"));

					} else {
						// 與萊拉訂定契約
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "lyraev1"));
					}

				} else {
					// 歡迎！你是一個冒險家嗎？或者是傭兵？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "lyra1"));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (cmd.equalsIgnoreCase("contract1")) {// 與萊拉訂定契約
			// 將任務設置為啟動
			QuestClass.get().startQuest(pc, ALv15_1.QUEST.get_id());

			// 如同你所見，我的村莊很窮，所以我們不能付你很多錢...
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "lyraev2"));

		} else if (cmd.equalsIgnoreCase("contract1yes")) {// 領取賞金和重新訂契約
			getAdena(pc, npc);

			// 我跟你說過的酬勞在這裡，拿去吧！
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "lyraev5"));

		} else if (cmd.equalsIgnoreCase("contract1no")) {// 領取賞金和終止契約
			if (pc.getQuest().isStart(ALv15_1.QUEST.get_id())) {

				getAdena(pc, npc);
				// 將任務設置為結束
				QuestClass.get().endQuest(pc, ALv15_1.QUEST.get_id());

				// 非常感謝你。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "lyraev4"));

			} else {
				getAdena(pc, npc);
				isCloseList = true;
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 給予金幣<BR>
	 * 阿吐巴的圖騰值２００個金幣<BR>
	 * 那魯加的值１００個<BR>
	 * 羅孚和都達瑪拉的值５０個<BR>
	 * 甘地的值３０個<BR>
	 * 
	 * @param pc
	 * @param npc
	 */
	private void getAdena(final L1PcInstance pc, final L1NpcInstance npc) {
		long adena = 0;
		final L1ItemInstance item1 = pc.getInventory().findItemId(40131);// 甘地圖騰
		if (item1 != null) {
			adena += (30 * pc.getInventory().removeItem(item1));
		}

		final L1ItemInstance item2 = pc.getInventory().findItemId(40132);// 那魯加圖騰
		if (item2 != null) {
			adena += (100 * pc.getInventory().removeItem(item2));
		}

		final L1ItemInstance item3 = pc.getInventory().findItemId(40133);// 都達瑪拉圖騰
		if (item3 != null) {
			adena += (50 * pc.getInventory().removeItem(item3));
		}

		final L1ItemInstance item4 = pc.getInventory().findItemId(40134);// 羅孚圖騰
		if (item4 != null) {
			adena += (50 * pc.getInventory().removeItem(item4));
		}

		final L1ItemInstance item5 = pc.getInventory().findItemId(40135);// 阿吐巴圖騰
		if (item5 != null) {
			adena += (200 * pc.getInventory().removeItem(item5));
		}

		if (adena > 0) {
			CreateNewItem.createNewItem(pc, 40308, adena);
		}
	}
}
