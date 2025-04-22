package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 套裝效果:支撐耐性增加
 * 
 * @author daien
 */
public class EffectRegist_Sustain implements ArmorSetEffect {

	private final int _add;// 增加值

	/**
	 * 套裝效果:支撐耐性增加
	 * 
	 * @param add 增加值
	 */
	public EffectRegist_Sustain(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addRegistSustain(_add);
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addRegistSustain(-_add);
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
