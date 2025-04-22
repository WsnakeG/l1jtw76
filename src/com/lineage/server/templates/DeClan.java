package com.lineage.server.templates;

/**
 * 虛擬血盟
 * 
 * @author dexc
 */
public class DeClan {

	private String _clanname;

	private int _clan_id;

	private String _leader_name;

	private byte[] _emblemicon;

	public DeClan(final String clanname, final int clan_id, final String leader_name,
			final byte[] emblemicon) {
		_clanname = clanname;
		_clan_id = clan_id;
		_leader_name = leader_name;
		_emblemicon = emblemicon;
	}

	/**
	 * @param _clanname 對 _clanname 進行設置
	 */
	public void set_clanname(final String _clanname) {
		this._clanname = _clanname;
	}

	/**
	 * @param _clan_id 對 _clan_id 進行設置
	 */
	public void set_clan_id(final int _clan_id) {
		this._clan_id = _clan_id;
	}

	/**
	 * @param _leader_name 對 _leader_name 進行設置
	 */
	public void set_leader_name(final String _leader_name) {
		this._leader_name = _leader_name;
	}

	/**
	 * @param _emblemicon 對 _emblemicon 進行設置
	 */
	public void set_emblemicon(final byte[] _emblemicon) {
		this._emblemicon = _emblemicon;
	}

	/**
	 * @return 傳出 _clanname
	 */
	public String get_clanname() {
		return _clanname;
	}

	/**
	 * @return 傳出 _clan_id
	 */
	public int get_clan_id() {
		return _clan_id;
	}

	/**
	 * @return 傳出 _leader_name
	 */
	public String get_leader_name() {
		return _leader_name;
	}

	/**
	 * @return 傳出 _emblemicon
	 */
	public byte[] get_emblemicon() {
		return _emblemicon;
	}

}
