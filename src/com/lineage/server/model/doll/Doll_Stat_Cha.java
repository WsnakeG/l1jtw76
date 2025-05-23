package com.lineage.server.model.doll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;

/**
 * 娃娃效果:魅力 魅力提高 參數：TYPE1(增加質)
 * 
 * @author daien
 */
public class Doll_Stat_Cha extends L1DollExecutor {

	private static final Log _log = LogFactory.getLog(Doll_Stat_Cha.class);

	private int _int1;// 值1

	/**
	 * 娃娃效果:魅力
	 */
	public Doll_Stat_Cha() {
	}

	public static L1DollExecutor get() {
		return new Doll_Stat_Cha();
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
			pc.addCha(_int1);
			pc.sendPackets(new S_OwnCharStatus2(pc));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void removeDoll(final L1PcInstance pc) {
		try {
			pc.addCha(-_int1);
			pc.sendPackets(new S_OwnCharStatus2(pc));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public boolean is_reset() {
		return false;
	}
}
