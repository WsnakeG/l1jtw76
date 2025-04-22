package com.lineage.server.templates;

/**
 * 神兵排行榜暫存
 * 
 * @author simlin
 */
public class T_WeaponRankModel {

	private String _weaponName;

	private String _weaponNameId;

	private int _weaponEnchantlevel;

	private int _weaponAttrKind;

	private String _weaponMasterName;

	public T_WeaponRankModel(final String weaponName, final String weaponNameId, final int weaponEnchantlevel,
			final int weaponAttrKind, final String weaponMasterName) {
		_weaponName = weaponName;
		_weaponNameId = weaponNameId;
		_weaponEnchantlevel = weaponEnchantlevel;
		_weaponAttrKind = weaponAttrKind;
		_weaponMasterName = weaponMasterName;
	}

	public String getWeaponName() {
		return _weaponName;
	}

	public void setWeaponName(final String weaponName) {
		_weaponName = weaponName;
	}

	public String getWeaponNameId() {
		return _weaponNameId;
	}

	public void setWeaponNameId(final String weaponNameId) {
		_weaponNameId = weaponNameId;
	}

	public int getWeaponEnchantlevel() {
		return _weaponEnchantlevel;
	}

	public void setWeaponEnchantlevel(final int weaponEnchantlevel) {
		_weaponEnchantlevel = weaponEnchantlevel;
	}

	public int getWeaponAttrKind() {
		return _weaponAttrKind;
	}

	public void setWeaponAttrKind(final int weaponAttrKind) {
		_weaponAttrKind = weaponAttrKind;
	}

	public String getWeaponMasterName() {
		return _weaponMasterName;
	}

	public void setWeaponMasterName(final String weaponMasterName) {
		_weaponMasterName = weaponMasterName;
	}
}