package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 六屬性狀態增加:魅力
 * 
 * @author daien
 */
public class EffectStat_Cha implements ArmorSetEffect {

	private final int _add;

	/**
	 * 六屬性狀態增加:魅力
	 * 
	 * @param add 魅力
	 */
	public EffectStat_Cha(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addCha(_add);
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addCha(-_add);
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
