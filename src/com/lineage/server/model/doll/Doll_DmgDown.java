package com.lineage.server.model.doll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 娃娃能力 模組:傷害減免相關<BR>
 * 傷害減免提高 參數：TYPE1(傷害減免質+)
 * 
 * @author dexc
 */
public class Doll_DmgDown extends L1DollExecutor {

	private static final Log _log = LogFactory.getLog(Doll_DmgDown.class);

	private int _int1;// 值1

	/**
	 * 娃娃效果:傷害減免增加
	 */
	public Doll_DmgDown() {
	}

	public static L1DollExecutor get() {
		return new Doll_DmgDown();
	}

	@Override
	public void set_power(final int int1, final int int2, final int int3) {
		try {
			_int1 = int1;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void setDoll(final L1PcInstance pc) {
		try {
			pc.addDamageReductionByArmor(_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void removeDoll(final L1PcInstance pc) {
		try {
			pc.addDamageReductionByArmor(-_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public boolean is_reset() {
		return false;
	}
}
