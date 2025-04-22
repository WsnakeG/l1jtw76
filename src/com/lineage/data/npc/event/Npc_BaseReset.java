package com.lineage.data.npc.event;

import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_CharReset;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 71251 回憶蠟燭嚮導露露
 * 
 * @author loli
 */
public class Npc_BaseReset extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_BaseReset.class);

	private Npc_BaseReset() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_BaseReset();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "baseReset"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			if (cmd.equalsIgnoreCase("ent")) {// 點燃回憶蠟燭

				if (!pc.getInventory().checkItem(49142)) { // 回憶蠟燭
					pc.sendPackets(new S_ServerMessage(1290)); // 沒有角色初始化所需要的道具。
					return;
				}

				// 消除現有技能狀態
				final L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(pc, CANCELLATION, pc.getId(), pc.getX(), pc.getY(), 0,
						L1SkillUse.TYPE_LOGIN);

				pc.getInventory().takeoffEquip(945); // 脫除全部裝備

				// 傳送至轉生用地圖
				L1Teleport.teleport(pc, 32737, 32789, (short) 997, 4, false);

				final int initStatusPoint = 75 + pc.getElixirStats();
				int pcStatusPoint = pc.getBaseStr() + pc.getBaseInt() + pc.getBaseWis() + pc.getBaseDex()
						+ pc.getBaseCon() + pc.getBaseCha();

				if (pc.getLevel() > 50) {
					pcStatusPoint += (pc.getLevel() - 50 - pc.getBonusStats());
				}

				final int diff = pcStatusPoint - initStatusPoint;
				/**
				 * [50級以上] 目前點數 - 初始點數 = 人物應有等級 - 50 -> 人物應有等級 = 50 + (目前點數 -
				 * 初始點數)
				 */
				int maxLevel = 1;

				if (diff > 0) {
					// 最高到99級:也就是?不支援轉生
					maxLevel = Math.min(50 + diff, 200);

				} else {
					maxLevel = pc.getLevel();
				}

				pc.setTempMaxLevel(maxLevel);
				pc.setTempLevel(1);
				pc.setInCharReset(true);
				pc.sendPackets(new S_CharReset(pc));
			}
			pc.sendPackets(new S_CloseList(pc.getId()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
