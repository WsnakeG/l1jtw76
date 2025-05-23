package com.lineage.server.model.skill.skillmode;

import static com.lineage.server.model.skill.L1SkillId.MIRROR_IMAGE;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxIcon1;

/**
 * 镜像201
 * 
 * @author loli
 */
public class MIRROR_IMAGE extends SkillMode {

	public MIRROR_IMAGE() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;// magic.calcMagicDamage(L1SkillId.UNCANNY_DODGE);
		if (!srcpc.hasSkillEffect(MIRROR_IMAGE)) {
			srcpc.setSkillEffect(MIRROR_IMAGE, integer * 1000);
			srcpc.add_dodge(5); // 閃避率 + 50%
			// 更新閃避率顯示
			srcpc.sendPackets(new S_PacketBoxIcon1(true, srcpc.get_dodge()));
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
		cha.add_dodge(-5); // 閃避率 - 50%
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			// 更新閃避率顯示
			pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));
		}
	}
}
