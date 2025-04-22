package com.lineage.server.model.doll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 娃娃能力 模組:遠距離攻擊相關<BR>
 * 攻擊力提高 參數：TYPE1(遠距離攻擊+)
 * 
 * @author dexc
 */
public class Doll_DmgBow extends L1DollExecutor {

	private static final Log _log = LogFactory.getLog(Doll_DmgBow.class);

	private int _int1;// 值1

	/**
	 * 娃娃效果:遠距離攻擊增加
	 */
	public Doll_DmgBow() {
	}

	public static L1DollExecutor get() {
		return new Doll_DmgBow();
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
			pc.addBowDmgup(_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void removeDoll(final L1PcInstance pc) {
		try {
			pc.addBowDmgup(-_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public boolean is_reset() {
		return false;
	}
}
