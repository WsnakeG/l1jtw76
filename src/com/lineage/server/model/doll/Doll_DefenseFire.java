package com.lineage.server.model.doll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;

/**
 * 娃娃效果:火屬性增加 火屬性增加 參數：TYPE1
 * 
 * @author daien
 */
public class Doll_DefenseFire extends L1DollExecutor {

	private static final Log _log = LogFactory.getLog(Doll_DefenseFire.class);

	private int _int1;// 值1

	/**
	 * 娃娃效果:火屬性增加
	 */
	public Doll_DefenseFire() {
	}

	public static L1DollExecutor get() {
		return new Doll_DefenseFire();
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
			pc.addFire(_int1);
			pc.sendPackets(new S_OwnCharAttrDef(pc));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void removeDoll(final L1PcInstance pc) {
		try {
			pc.addFire(-_int1);
			pc.sendPackets(new S_OwnCharAttrDef(pc));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public boolean is_reset() {
		return false;
	}
}
