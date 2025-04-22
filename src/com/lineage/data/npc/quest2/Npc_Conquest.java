package com.lineage.data.npc.quest2;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;

/**
 * @author terry0412
 */
public class Npc_Conquest extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Conquest.class);

	private Npc_Conquest() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Conquest();
	}

	@Override
	public int type() {
		return 8;
	}

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
			if (pc != null) {
				final ArrayList<L1Character> targetList = npc.getHateList().toTargetArrayList();
				if (!targetList.isEmpty()) {
					for (final L1Character cha : targetList) {
						if (cha instanceof L1PcInstance) {
							final L1PcInstance find_pc = (L1PcInstance) cha;
							if (find_pc != null) {
								find_pc.sendPacketsX10(new S_SkillSound(find_pc.getId(), 7783));
							}
						}
					}

					for (final L1Character cha : targetList) {
						if (cha instanceof L1PcInstance) {
							final L1PcInstance find_pc = (L1PcInstance) cha;
							if (_checkDead && ((find_pc.getCurrentHp() <= 0) || find_pc.isDead())) {
								continue;
							}
							CreateNewItem.createNewItem(find_pc, _itemId, 1);
						}
					}

					World.get()
							.broadcastPacketToAll(new S_ServerMessage(3320, npc.getNameId(),
									targetList.size() >= 1 ? targetList.get(0).getName() : "",
									targetList.size() >= 2 ? targetList.get(1).getName() : "",
									targetList.size() >= 3 ? targetList.get(2).getName() : ""));
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	private int _itemId;

	private boolean _checkDead;

	@Override
	public void set_set(final String[] set) {
		try {
			_itemId = Integer.parseInt(set[1]);

		} catch (final Exception e) {
		}
		try {
			_checkDead = Boolean.parseBoolean(set[2]);

		} catch (final Exception e) {
		}
	}
}
