package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * MP回復增加
 * 
 * @author daien
 */
public class EffectMpR implements ArmorSetEffect {

	private final int _add;// 增加值

	/**
	 * 套裝效果:MP回復增加
	 * 
	 * @param add 增加值
	 */
	public EffectMpR(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addMpr(_add);
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addMpr(-_add);
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
