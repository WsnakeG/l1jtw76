package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 釣魚小童 80083
 * 
 * @author simlin
 */
public class Npc_Fishing_2 extends NpcExecutor {

	/**
	 *
	 */
	private Npc_Fishing_2() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Fishing_2();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fk_out_1"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equals("a")) {// 要離開釣魚池了
			L1Teleport.teleport(pc, 32608, 32772, (short) 4, 4, true);
			if (pc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
				pc.removeSkillEffect(L1SkillId.SHAPE_CHANGE);
			}
		} else if (cmd.equals("b")) {// 走到中央
			L1Teleport.teleport(pc, 32767, 32831, (short) 5490, 5, true);

		} else if (cmd.equals("c")) {// 走到東邊
			L1Teleport.teleport(pc, 32791, 32868, (short) 5490, 5, true);

		} else if (cmd.equals("d")) {// 走到西邊
			L1Teleport.teleport(pc, 32727, 32809, (short) 5490, 5, true);

		} else if (cmd.equals("e")) {// 走到南邊
			L1Teleport.teleport(pc, 32734, 32871, (short) 5490, 5, true);

		} else if (cmd.equals("f")) {// 走到北邊
			L1Teleport.teleport(pc, 32792, 32794, (short) 5490, 5, true);
		}
	}
}
