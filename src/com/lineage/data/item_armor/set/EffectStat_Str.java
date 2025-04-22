package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.serverpackets.ability.S_StrDetails;
import com.lineage.server.serverpackets.ability.S_WeightStatus;

/**
 * 六屬性狀態增加:力量
 * 
 * @author daien
 */
public class EffectStat_Str implements ArmorSetEffect {

	private final int _add;

	/**
	 * 六屬性狀態增加:力量
	 * 
	 * @param add 力量
	 */
	public EffectStat_Str(final int add) {
		_add = add;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		pc.addStr(_add);
		pc.sendDetails();
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		pc.addStr(-_add);
		pc.sendDetails();
	}

	@Override
	public int get_mode() {
		return _add;
	}
}
