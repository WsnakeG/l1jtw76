package com.lineage.server.templates;

public class L1EtcItem extends L1Item {

	private static final long serialVersionUID = 1L;

	public L1EtcItem() {
	}

	private boolean _stackable;

	private int _delay_id;

	private int _delay_time;

	private int _delay_effect;

	private int _maxChargeCount;

	@Override
	public boolean isStackable() {
		return _stackable;
	}

	public void set_stackable(final boolean stackable) {
		_stackable = stackable;
	}

	public void set_delayid(final int delay_id) {
		_delay_id = delay_id;
	}

	/**
	 * 延遲ID
	 */
	@Override
	public int get_delayid() {
		return _delay_id;
	}

	public void set_delaytime(final int delay_time) {
		_delay_time = delay_time;
	}

	/**
	 * 延遲時間
	 */
	@Override
	public int get_delaytime() {
		return _delay_time;
	}

	@Override
	public void set_delayEffect(final int delay_effect) {
		_delay_effect = delay_effect;
	}

	@Override
	public int get_delayEffect() {
		return _delay_effect;
	}

	public void setMaxChargeCount(final int i) {
		_maxChargeCount = i;
	}

	@Override
	public int getMaxChargeCount() {
		return _maxChargeCount;
	}

}
