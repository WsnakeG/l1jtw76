package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 套裝效果:地屬性增加
 * 
 * @author daien
 */
public class EffectDefenseEarth implements ArmorSetEffect {

	private final int _add;// 增加值

	/**
	 * 套裝效果:地屬性增加
	 * 
	 * @param add 增加值
	 */
	public EffectDefenseEarth(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addEarth(_add);
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addEarth(-_add);
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
