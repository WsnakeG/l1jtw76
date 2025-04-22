package com.lineage.data.npc.event;

// import static com.lineage.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static com.lineage.server.model.skill.L1SkillId.AQUA_PROTECTER;
import static com.lineage.server.model.skill.L1SkillId.CONCENTRATION;
import static com.lineage.server.model.skill.L1SkillId.EARTH_SKIN;
import static com.lineage.server.model.skill.L1SkillId.FIRE_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.HASTE;
import static com.lineage.server.model.skill.L1SkillId.INSIGHT;
import static com.lineage.server.model.skill.L1SkillId.NATURES_TOUCH;
import static com.lineage.server.model.skill.L1SkillId.PATIENCE;
import static com.lineage.server.model.skill.L1SkillId.SHINING_AURA;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHOT;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.MagicianSet;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 魔法輔助商人
 * 
 * @author terry0412
 */
public class Npc_BuffSet extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_BuffSet.class);

	private Npc_BuffSet() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_BuffSet();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_01"));
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			if (cmd.equalsIgnoreCase("a")) {
				if (pc.getInventory().consumeItem(MagicianSet.ITEM_ID, MagicianSet.ITEM_COUNT)) {
					final L1SkillUse skillUse = new L1SkillUse();
					skillUse.handleCommands(pc, HASTE, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					/*
					 * skillUse.handleCommands(pc, ADVANCE_SPIRIT, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					 */
//					skillUse.handleCommands(pc, EARTH_SKIN, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, NATURES_TOUCH, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, AQUA_PROTECTER, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, CONCENTRATION, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, PATIENCE, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, INSIGHT, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, FIRE_WEAPON, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, SHINING_AURA, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);

					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_done"));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_adena"));
				}
			} else if (cmd.equalsIgnoreCase("b")) {
				if (pc.getInventory().consumeItem(MagicianSet.ITEM_ID, MagicianSet.ITEM_COUNT)) {
					final L1SkillUse skillUse = new L1SkillUse();
					skillUse.handleCommands(pc, HASTE, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					/*
					 * skillUse.handleCommands(pc, ADVANCE_SPIRIT, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					 */
					skillUse.handleCommands(pc, EARTH_SKIN, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, NATURES_TOUCH, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, AQUA_PROTECTER, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, CONCENTRATION, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, PATIENCE, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, INSIGHT, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
					skillUse.handleCommands(pc, WIND_SHOT, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
//					skillUse.handleCommands(pc, SHINING_AURA, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);

					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_done"));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_adena"));
				}
			} else if (cmd.equalsIgnoreCase("0")) {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_01"));
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
