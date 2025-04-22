package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 休格琳特<BR>
 * 81375<BR>
 * 說明:穿越時空的探險(秘譚)
 * 
 * @author dexc
 */
public class Npc_Xiugelinte extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Xiugelinte.class);

	private Npc_Xiugelinte() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Xiugelinte();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.getInventory().checkItem(56235)) { // 魔幻之水
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hugrint5"));
			} else {
				// 據說在新發現的地監出現了各種過去的物品
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hugrint1"));
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			if (cmd.equalsIgnoreCase("0")) { // 購買魔法氣息。
				// 身上有魔法氣息
				if (pc.getInventory().checkItem(49335) || pc.getInventory().checkItem(49336)) {
					// 你已經有魔法氣息了
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hugrint4"));
					return;
				}
				// 需要的物件確認
				final L1ItemInstance item = pc.getInventory().checkItemX(40308, 1000);
				if (item != null) {
					pc.getInventory().removeItem(item, 1000);// 刪除道具
					// 給予道具(魔法氣息 49312)
					CreateNewItem.createNewItem(pc, 49335, 1);
					// 好好的使用它。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hugrint2"));
				} else {
					// 還是要補貼一點研究費
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hugrint3"));
				}
			} else if (cmd.equalsIgnoreCase("1")) { // 魔幻之水交換高級魔法氣息。
				// 身上有魔法氣息
				if (pc.getInventory().checkItem(49335) || pc.getInventory().checkItem(49336)) {
					// 你已經有魔法氣息了
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hugrint4"));
					return;
				}
				// 需要的物件確認
				final L1ItemInstance item = pc.getInventory().checkItemX(56235, 5);
				if (item != null) {
					pc.getInventory().removeItem(item, 5); // 刪除道具
					// 給予道具(高級魔法氣息 49336)
					CreateNewItem.createNewItem(pc, 49336, 1);
					// 好好的使用它。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hugrint2"));
				} else {
					// 數量不夠
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hugrint6"));
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
