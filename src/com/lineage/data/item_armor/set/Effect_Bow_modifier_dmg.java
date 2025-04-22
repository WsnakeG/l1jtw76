package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 套裝效果:套裝增加弓的物理傷害
 * 
 * @author daien
 */
public class Effect_Bow_modifier_dmg implements ArmorSetEffect {

	private final int _add;

	/**
	 * 套裝效果:套裝增加弓的物理傷害
	 * 
	 * @param add 精神
	 */
	public Effect_Bow_modifier_dmg(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addBowDmgup(_add);
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addBowDmgup(-_add);
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
