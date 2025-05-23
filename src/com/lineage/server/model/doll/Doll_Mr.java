package com.lineage.server.model.doll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SPMR;

/**
 * 娃娃能力 模組:抗魔<BR>
 * 抗魔提高 參數：TYPE1(增加質)
 * 
 * @author dexc
 */
public class Doll_Mr extends L1DollExecutor {

	private static final Log _log = LogFactory.getLog(Doll_Mr.class);

	private int _int1;// 值1

	/**
	 * 娃娃效果:抗魔
	 */
	public Doll_Mr() {
	}

	public static L1DollExecutor get() {
		return new Doll_Mr();
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
			pc.addMr(_int1);
			pc.sendPackets(new S_SPMR(pc));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void removeDoll(final L1PcInstance pc) {
		try {
			pc.addMr(-_int1);
			pc.sendPackets(new S_SPMR(pc));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public boolean is_reset() {
		return false;
	}
}
