package com.lineage.server.model.skill.skillmode;

import java.util.Random;

import com.lineage.config.ConfigRate;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.poison.L1Tomahawk;
import com.lineage.server.serverpackets.S_PacketBox;

/**
 * 戰斧投擲
 * 
 * @author simlin
 */
public class TOMAHAWK extends SkillMode {

	public TOMAHAWK() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final Random rnd = new Random();
		final int dmg = (srcpc.getLevel() * ConfigRate.TOMAHAWK) / 6;
		int probability = 90 + ((srcpc.getLevel() - cha.getLevel()) * 4);
		if (probability > 90) {
			probability = 90;
		}
		if (probability > rnd.nextInt(100)) {
			L1Tomahawk.doInfection(srcpc, cha, 1000, dmg);

			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(S_PacketBox.TOMAHAWK, 570, true));
			}
		}

		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		return 0;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_PacketBox(S_PacketBox.TOMAHAWK, 570, false));
		}
	}
}
