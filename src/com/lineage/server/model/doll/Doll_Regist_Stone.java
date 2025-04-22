package com.lineage.server.model.doll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 娃娃能力:石化耐性增加 石化耐性提高 參數：TYPE1(增加質)
 * 
 * @author daien
 */
public class Doll_Regist_Stone extends L1DollExecutor {

	private static final Log _log = LogFactory.getLog(Doll_Regist_Stone.class);

	private int _int1;// 值1

	/**
	 * 娃娃能力:石化耐性增加
	 */
	public Doll_Regist_Stone() {
	}

	public static L1DollExecutor get() {
		return new Doll_Regist_Stone();
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
			pc.addRegistStone(_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void removeDoll(final L1PcInstance pc) {
		try {
			pc.addRegistStone(-_int1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public boolean is_reset() {
		return false;
	}
}
