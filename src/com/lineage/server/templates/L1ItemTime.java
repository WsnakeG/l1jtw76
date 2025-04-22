package com.lineage.server.templates;

/**
 * @author terry0412
 */
public final class L1ItemTime {

	private final int _remain_time;

	private final boolean _equip;

	public L1ItemTime(final int remain_time, final boolean equip) {
		_remain_time = remain_time;
		_equip = equip;
	}

	public int get_remain_time() {
		return _remain_time;
	}

	public boolean is_equipped() {
		return _equip;
	}
}
