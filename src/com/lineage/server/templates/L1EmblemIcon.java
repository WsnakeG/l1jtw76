package com.lineage.server.templates;

public class L1EmblemIcon {

	private int _clanid;

	private byte[] _clanIcon;

	private int _update;

	/**
	 * @return the _clanid
	 */
	public int get_clanid() {
		return _clanid;
	}

	/**
	 * @param _clanid the _clanid to set
	 */
	public void set_clanid(final int clanid) {
		_clanid = clanid;
	}

	/**
	 * @return the _clanIcon
	 */
	public byte[] get_clanIcon() {
		return _clanIcon;
	}

	/**
	 * @param icon the _clanIcon to set
	 */
	public void set_clanIcon(final byte[] icon) {
		_clanIcon = icon;
	}

	public int get_update() {
		return _update;
	}

	public void set_update(final int update) {
		_update = update;
	}
}
