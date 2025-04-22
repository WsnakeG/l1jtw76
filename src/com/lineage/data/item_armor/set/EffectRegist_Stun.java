package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 套裝效果:暈眩耐性增加
 * 
 * @author daien
 */
public class EffectRegist_Stun implements ArmorSetEffect {

	private final int _add;// 增加值

	/**
	 * 套裝效果:暈眩耐性增加
	 * 
	 * @param add 增加值
	 */
	public EffectRegist_Stun(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addRegistStun(_add);
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addRegistStun(-_add);
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
