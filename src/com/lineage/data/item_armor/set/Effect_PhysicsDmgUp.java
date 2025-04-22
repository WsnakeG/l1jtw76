package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 套裝效果:套裝增加物理傷害 %
 * 
 * @author Erics4179
 */
public class Effect_PhysicsDmgUp implements ArmorSetEffect {

	private final int _add;

	/**
	 * 套裝效果:套裝增加物理傷害 %
	 * 
	 * @param add 精神
	 */
	public Effect_PhysicsDmgUp(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addPhysicsDmgUp(_add);
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addPhysicsDmgUp(-_add);
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
