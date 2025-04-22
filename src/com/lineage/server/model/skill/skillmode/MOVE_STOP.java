package com.lineage.server.model.skill.skillmode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;

/**
 * 雙腳被困
 * 
 * @author dexc
 */
public class MOVE_STOP extends SkillMode {

	private static final Log _log = LogFactory.getLog(MOVE_STOP.class);

	public MOVE_STOP() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		try {
			if (!cha.hasSkillEffect(L1SkillId.MOVE_STOP)) {
				cha.setSkillEffect(L1SkillId.MOVE_STOP, integer * 1000);

				if (cha instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) cha;
					pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
					pc.sendPacketsAll(new S_Poison(pc.getId(), 2));

				} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
						|| (cha instanceof L1PetInstance)) {
					final L1NpcInstance tgnpc = (L1NpcInstance) cha;
					tgnpc.broadcastPacketAll(new S_Poison(tgnpc.getId(), 2));
					tgnpc.setParalyzed(true);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
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
		try {
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
				pc.sendPacketsAll(new S_Poison(pc.getId(), 0));

			} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
					|| (cha instanceof L1PetInstance)) {
				final L1NpcInstance tgnpc = (L1NpcInstance) cha;
				tgnpc.broadcastPacketAll(new S_Poison(tgnpc.getId(), 0));
				tgnpc.setParalyzed(false);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
