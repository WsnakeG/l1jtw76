package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 六屬性狀態增加:體質
 * 
 * @author daien
 */
public class EffectStat_Con implements ArmorSetEffect {

	private final int _add;

	/**
	 * 六屬性狀態增加:體質
	 * 
	 * @param add 體質
	 */
	public EffectStat_Con(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addCon(_add);
		pc.sendDetails();
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addCon(-_add);
		pc.sendDetails();
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
