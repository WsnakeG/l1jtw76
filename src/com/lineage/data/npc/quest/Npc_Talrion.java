package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv30_1;
import com.lineage.data.quest.DragonKnightLv45_1;
import com.lineage.data.quest.DragonKnightLv50_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 塔爾立昂<BR>
 * 85020<BR>
 * 
 * @author dexc
 */
public class Npc_Talrion extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Talrion.class);

	private Npc_Talrion() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Talrion();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 我是護衛 <font fg=ffff0>貝希摩斯</font>的龍騎士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));

			} else if (pc.isKnight()) {// 騎士
				// 我是護衛 <font fg=ffff0>貝希摩斯</font>的龍騎士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));

			} else if (pc.isElf()) {// 精靈
				// 我是護衛 <font fg=ffff0>貝希摩斯</font>的龍騎士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));

			} else if (pc.isWizard()) {// 法師
				// 我是護衛 <font fg=ffff0>貝希摩斯</font>的龍騎士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 我是護衛 <font fg=ffff0>貝希摩斯</font>的龍騎士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// LV50任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv50_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(49228)) {// 發光的銀塊
						if (!pc.getInventory().checkItem(49230)) {// 沒有清單
							// 交出發光的銀塊
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion10"));
							return;
						}
					}
				}
				// LV45任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv45_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(49214)) {// 普洛凱爾的第二次信件
						// 普洛凱爾的第二次信件
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion9"));
						return;
					}
				}
				// LV30任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv30_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(49213)) {// 普洛凱爾的第一次信件
						// 交出普洛凱爾的第一次信件
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion1"));
						return;
					}
				}
				// 我是護衛 <font fg=ffff0>貝希摩斯</font>的龍騎士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 我是護衛 <font fg=ffff0>貝希摩斯</font>的龍騎士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));

			} else {
				// 我是護衛 <font fg=ffff0>貝希摩斯</font>的龍騎士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isDragonKnight()) {// 龍騎士
			if (cmd.equalsIgnoreCase("a")) {// 交出普洛凱爾的第一次信件
				final L1ItemInstance item = pc.getInventory().checkItemX(49213, 1);// 普洛凱爾的第一次信件
				if (item != null) {
					pc.getInventory().removeItem(item, 1);// 刪除道具
					// 給予任務道具(龍鱗臂甲)
					CreateNewItem.getQuestItem(pc, npc, 21103, 1);
					// 恭喜～<font fg=fff00>普洛凱爾</font>下了賜予你<font
					// fg=ffffaf>龍鱗臂甲</font>的命令。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion2"));

				} else {
					// <font fg=ffffaf>普洛凱爾的第一次信件</font>在哪呢？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion3"));
				}

			} else if (cmd.equalsIgnoreCase("b")) {// 普洛凱爾的第二次信件
				final L1ItemInstance item = pc.getInventory().checkItemX(49214, 1);// 普洛凱爾的第二次信件
				if (item != null) {
					pc.getInventory().removeItem(item, 1);// 刪除道具
					// 給予任務道具(龍騎士斗篷)
					CreateNewItem.getQuestItem(pc, npc, 21102, 1);
					// 恭喜～<font fg=fff00>普洛凱爾</font>賜予你 <font
					// fg=ffffaf>龍騎士之斗篷</font>了。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion7"));

				} else {
					// <font fg=ffffaf>普洛凱爾的第二次信件</font>在哪呢？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion8"));
				}
			} else if (cmd.equalsIgnoreCase("c")) {// 交出發光的銀塊
				final L1ItemInstance item = pc.getInventory().checkItemX(49228, 1);// 發光的銀塊
				if (item != null) {
					// 給予任務道具(塔爾立昂的武器材料清單)
					CreateNewItem.getQuestItem(pc, npc, 49230, 1);
					// 恭喜～長老 <font fg=fff00>普洛凱爾</font>稱讚你的實力。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion5"));

				} else {
					// <font fg=ffffaf>發光的銀塊</font>在哪呢？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion6"));
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

	@Override
	public void attack(final L1PcInstance pc, final L1NpcInstance npc) {
		// TODO Auto-generated method stub
	}

	@Override
	public void work(final L1NpcInstance npc) {
		// TODO Auto-generated method stub
	}
}
