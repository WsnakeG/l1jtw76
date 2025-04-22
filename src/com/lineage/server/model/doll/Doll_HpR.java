package com.lineage.server.model.doll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 娃娃能力 模組:HP 回復相關<BR>
 * HP回復提高 參數：TYPE1
 * 
 * @author dexc
 */
public class Doll_HpR extends L1DollExecutor {

	private static final Log _log = LogFactory.getLog(Doll_HpR.class);

	private int _int1;// 值1

	/**
	 * 娃娃效果:HP增加
	 */
	public Doll_HpR() {
	}

	public static L1DollExecutor get() {
		return new Doll_HpR();
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
			pc.addHpr(_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void removeDoll(final L1PcInstance pc) {
		try {
			pc.addHpr(-_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public boolean is_reset() {
		return false;
	}
}
