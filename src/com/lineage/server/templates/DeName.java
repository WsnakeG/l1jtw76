package com.lineage.server.templates;

/**
 * 虛擬血盟
 * 
 * @author dexc
 */
public class DeName {

	private int _deobjid;

	private String _name;

	private int _type;

	private int _sex;

	private int _clanid;

	public DeName(final int deobjid, final String name, final int type, final int sex, final int clanid) {
		_deobjid = deobjid;
		_name = name;
		_type = type;
		_sex = sex;
		_clanid = clanid;
	}

	/**
	 * @return 傳出 _deobjid
	 */
	public int get_deobjid() {
		return _deobjid;
	}

	/**
	 * @param deobjid 對 _deobjid 進行設置
	 */
	public void set_deobjid(final int deobjid) {
		_deobjid = deobjid;
	}

	/**
	 * @return 傳出 _name
	 */
	public String get_name() {
		return _name;
	}

	/**
	 * @param name 對 _name 進行設置
	 */
	public void set_name(final String name) {
		_name = name;
	}

	/**
	 * @return 傳出 _type
	 */
	public int get_type() {
		return _type;
	}

	/**
	 * @param type 對 _type 進行設置
	 */
	public void set_type(final int type) {
		_type = type;
	}

	/**
	 * @return 傳出 _sex
	 */
	public int get_sex() {
		return _sex;
	}

	/**
	 * @param sex 對 _sex 進行設置
	 */
	public void set_sex(final int sex) {
		_sex = sex;
	}

	/**
	 * @return 傳出 _clanid
	 */
	public int get_clanid() {
		return _clanid;
	}

	/**
	 * @param clanid 對 _clanid 進行設置
	 */
	public void set_clanid(final int clanid) {
		_clanid = clanid;
	}
}
