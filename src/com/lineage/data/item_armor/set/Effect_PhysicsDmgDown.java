package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 套裝效果:套裝增加物理傷害減免 %
 * 
 * @author Erics4179
 */
public class Effect_PhysicsDmgDown implements ArmorSetEffect {

	private final int _add;

	/**
	 * 套裝效果:套裝增加物理傷害減免 %
	 * 
	 * @param add 精神
	 */
	public Effect_PhysicsDmgDown(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addPhysicsDmgDown(_add);
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addPhysicsDmgDown(-_add);
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
