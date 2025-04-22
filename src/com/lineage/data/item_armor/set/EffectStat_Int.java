package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 六屬性狀態增加:智力
 * 
 * @author daien
 */
public class EffectStat_Int implements ArmorSetEffect {

	private final int _add;

	/**
	 * 六屬性狀態增加:智力
	 * 
	 * @param add 智力
	 */
	public EffectStat_Int(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addInt(_add);
		pc.sendDetails();
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addInt(-_add);
		pc.sendDetails();
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
