package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1TowerInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 返生術
 * 
 * @author dexc
 */
public class RESURRECTION extends SkillMode {

	public RESURRECTION() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;// magic.calcMagicDamage(L1SkillId.CURE_POISON);
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			if (srcpc.getId() != pc.getId()) {
				if (World.get().getVisiblePlayer(pc, 0).size() > 0) {
					for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(pc, 0)) {
						if (!visiblePc.isDead()) {
							// 592 復活失敗，因為這個位置已被佔據
							srcpc.sendPackets(new S_ServerMessage(592));
							return 0;
						}
					}
				}
				if (pc.isDead()) {
					if (pc.getMap().isUseResurrection()) {
						pc.setGres(false);
						pc.setTempID(srcpc.getId());
						// 322 是否要復活？ (Y/N)
						pc.sendPackets(new S_Message_YN(321));
					}
				}
			}
		}
		if (cha instanceof L1NpcInstance) {
			if (!(cha instanceof L1TowerInstance)) {
				final L1NpcInstance npc = (L1NpcInstance) cha;
				// 不允許復活
				if (npc.getNpcTemplate().isCantResurrect()) {
					return 0;
				}
				if ((npc instanceof L1PetInstance) && (World.get().getVisiblePlayer(npc, 0).size() > 0)) {
					for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(npc, 0)) {
						if (!visiblePc.isDead()) {
							// 592 復活失敗，因為這個位置已被佔據
							srcpc.sendPackets(new S_ServerMessage(592));
							return 0;
						}
					}
				}
				if (npc.isDead()) {
					npc.resurrect(npc.getMaxHp() / 4);
					npc.setResurrect(true);
				}
			}
		}
		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

		return dmg;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		// TODO Auto-generated method stub
	}
}
