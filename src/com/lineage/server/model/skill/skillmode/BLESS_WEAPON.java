package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.pc.ItemTimer;

public class BLESS_WEAPON extends SkillMode {

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		if (!(cha instanceof L1PcInstance)) {
			return 0;
		}
		final L1PcInstance pc = (L1PcInstance) cha;
		if (pc.getWeapon() == null) {
			pc.sendPackets(new S_ServerMessage(79));
			return 0;
		}
		for (final L1ItemInstance item : pc.getInventory().getItems()) {
			if (pc.getWeapon().equals(item)) {
				pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
				setSkillWeaponEnchant(pc, L1SkillId.BLESS_WEAPON, integer, item);
				return 0;
			}
		}
		return 0;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	public void start(final L1PcInstance pc, final Object obj) throws Exception {
		// TODO 自动生成的方法存根

	}

	private void setSkillWeaponEnchant(final L1PcInstance pc, final int skillId, final int skillTime,
			final L1ItemInstance item) {
		final ItemTimer itemTimer = new ItemTimer();
		itemTimer.start(item, skillId, skillTime);
	}

}
