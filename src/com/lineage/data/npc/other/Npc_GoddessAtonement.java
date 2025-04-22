package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 贖罪的神女<BR>
 * 70530,70611,70658,70757,70781,70801,70823
 * 
 * @author dexc
 */
public class Npc_GoddessAtonement extends NpcExecutor {

	/**
	 *
	 */
	private Npc_GoddessAtonement() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_GoddessAtonement();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "restore1pk"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("pk")) { // 贖罪
			if (pc.getLawful() < 30000) {
				// \f1您目前的條件不符合贖罪的資格，請再次確認您的PK次數、正義值及金幣是否符合。
				pc.sendPackets(new S_ServerMessage(559));

			} else if (pc.get_PKcount() < 5) {
				// \f1你目前PK的次數還不需要去減輕你的惡行。
				pc.sendPackets(new S_ServerMessage(560));

			} else {
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, 700000)) {
					pc.set_PKcount(pc.get_PKcount() - 5);
					// 你的PK次數剩餘%0次。
					pc.sendPackets(new S_ServerMessage(561, String.valueOf(pc.get_PKcount())));

				} else {
					// 189 \f1金幣不足。
					pc.sendPackets(new S_ServerMessage(189));
				}
			}
		}
	}
}
