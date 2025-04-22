package com.lineage.server.templates;

import java.util.Calendar;

/**
 * 小屋資料緩存
 * 
 * @author dexc
 */
public class L1House {

	private int _houseId;

	/**
	 * 傳回小屋編號
	 * 
	 * @return
	 */
	public int getHouseId() {
		return _houseId;
	}

	/**
	 * 設置小屋編號
	 * 
	 * @param i
	 */
	public void setHouseId(final int i) {
		_houseId = i;
	}

	private String _houseName;

	/**
	 * 傳回小屋名稱
	 * 
	 * @return
	 */
	public String getHouseName() {
		return _houseName;
	}

	/**
	 * 設置小屋名稱
	 * 
	 * @param s
	 */
	public void setHouseName(final String s) {
		_houseName = s;
	}

	private int _houseArea;

	/**
	 * 傳回大小(坪數)
	 * 
	 * @return
	 */
	public int getHouseArea() {
		return _houseArea;
	}

	/**
	 * 設置大小(坪數)
	 * 
	 * @param i
	 */
	public void setHouseArea(final int i) {
		_houseArea = i;
	}

	private String _location;

	/**
	 * 傳回小屋順序排列
	 * 
	 * @return
	 */
	public String getLocation() {
		return _location;
	}

	/**
	 * 設置小屋順序排列
	 * 
	 * @param s
	 */
	public void setLocation(final String s) {
		_location = s;
	}

	private int _keeperId;

	/**
	 * 傳回女傭編號
	 * 
	 * @return
	 */
	public int getKeeperId() {
		return _keeperId;
	}

	/**
	 * 設置女傭編號
	 * 
	 * @param i
	 */
	public void setKeeperId(final int i) {
		_keeperId = i;
	}

	private boolean _isOnSale;

	/**
	 * 是否在出售狀態
	 * 
	 * @return true:是 false:不是
	 */
	public boolean isOnSale() {
		return _isOnSale;
	}

	/**
	 * 設置是否在出售狀態
	 * 
	 * @param flag
	 */
	public void setOnSale(final boolean flag) {
		_isOnSale = flag;
	}

	private boolean _isPurchaseBasement;

	/**
	 * 是否有地下盟屋
	 * 
	 * @return
	 */
	public boolean isPurchaseBasement() {
		return _isPurchaseBasement;
	}

	/**
	 * 設置是否有地下盟屋
	 * 
	 * @param flag
	 */
	public void setPurchaseBasement(final boolean flag) {
		_isPurchaseBasement = flag;
	}

	private Calendar _taxDeadline;

	/**
	 * 傳回交稅時間
	 * 
	 * @return
	 */
	public Calendar getTaxDeadline() {
		return _taxDeadline;
	}

	/**
	 * 設置交稅時間
	 * 
	 * @param cal
	 */
	public void setTaxDeadline(final Calendar cal) {
		_taxDeadline = cal;
	}

}