package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 釣魚小童 80082
 * 
 * @author dexc
 */
public class Npc_Fishing_1 extends NpcExecutor {

	/**
	 *
	 */
	private Npc_Fishing_1() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Fishing_1();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fk_in_1"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		// 151218新增限制等級低於60以上不能進入釣魚池
		if (pc.getLevel() < 60) {
			pc.sendPackets(new S_ServerMessage("\\aG等級太低你還不能進入釣魚池。"));
			return;
		}
		if (cmd.equals("a")) {
			L1Teleport.teleport(pc, 32794, 32795, (short) 5490, 6, true);
			if (pc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
				pc.removeSkillEffect(L1SkillId.SHAPE_CHANGE);
			}
		}
	}
}
