package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 六屬性狀態增加:敏捷
 * 
 * @author daien
 */
public class EffectStat_Dex implements ArmorSetEffect {

	private final int _add;

	/**
	 * 六屬性狀態增加:敏捷
	 * 
	 * @param add 敏捷
	 */
	public EffectStat_Dex(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addDex(_add);
		pc.sendDetails();
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addDex(-_add);
		pc.sendDetails();
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
