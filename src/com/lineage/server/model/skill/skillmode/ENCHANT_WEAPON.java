package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.pc.ItemTimer;

public class ENCHANT_WEAPON extends SkillMode {

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final L1PcInstance pc = (L1PcInstance) cha;
		if (pc == null) {
			return 0;
		}
		final L1ItemInstance item = pc.getInventory().getItem(pc.getuseitemobjid());
		if ((item != null) && (item.getItem().getType2() == 1)) {
			pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
			setSkillWeaponEnchant(pc, L1SkillId.ENCHANT_WEAPON, integer, item);

		} else {
			pc.sendPackets(new S_ServerMessage(79));
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
