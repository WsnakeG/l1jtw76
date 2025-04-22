package com.lineage.server.model.doll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.ability.S_WeightStatus;

/**
 * 娃娃能力 模組:負重相關<BR>
 * 防禦力提高 參數：TYPE1(/100) TYPE2(%)
 * 
 * @author dexc
 */
public class Doll_Weight extends L1DollExecutor {

	private static final Log _log = LogFactory.getLog(Doll_Weight.class);

	private int _int1;// 值1(現有負重 /值1)

	private int _int2;// 值2(負重能力+值2%)

	/**
	 * 娃娃效果:負重增加
	 */
	public Doll_Weight() {
	}

	public static L1DollExecutor get() {
		return new Doll_Weight();
	}

	@Override
	public void set_power(final int int1, final int int2, final int int3) {
		try {
			_int1 = int1;
			_int2 = int2;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void setDoll(final L1PcInstance pc) {
		try {
			if (_int1 != 0) {
				pc.addWeightReduction(_int1);
			}
			if (_int2 != 0) {
				pc.add_weightUP(_int2);
			}
			//pc.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, pc.getInventory().getWeight240()));
			pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int)pc.getMaxWeight(), pc.getInventory().getWeight(), (int)pc.getMaxWeight()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void removeDoll(final L1PcInstance pc) {
		try {
			if (_int1 != 0) {
				pc.addWeightReduction(-_int1);
			}
			if (_int2 != 0) {
				pc.add_weightUP(-_int2);
			}
			//pc.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, pc.getInventory().getWeight240()));
			pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int)pc.getMaxWeight(), pc.getInventory().getWeight(), (int)pc.getMaxWeight()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public boolean is_reset() {
		return false;
	}
}
