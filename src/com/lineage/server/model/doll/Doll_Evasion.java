package com.lineage.server.model.doll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 娃娃能力 模組:低機率，迴避攻擊一次<BR>
 * 低機率，迴避攻擊一次 參數：TYPE1(1/100)
 * 
 * @author dexc
 */
public class Doll_Evasion extends L1DollExecutor {

	private static final Log _log = LogFactory.getLog(Doll_Evasion.class);

	private int _int1;// 值1

	/**
	 * 娃娃效果:低機率，迴避攻擊一次
	 */
	public Doll_Evasion() {
	}

	public static L1DollExecutor get() {
		return new Doll_Evasion();
	}

	@Override
	public void set_power(final int int1, final int int2, final int int3) {
		try {
			_int1 = int1 * 10;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void setDoll(final L1PcInstance pc) {
		try {
			pc.set_evasion(_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void removeDoll(final L1PcInstance pc) {
		try {
			pc.set_evasion(-_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public boolean is_reset() {
		return false;
	}
}
