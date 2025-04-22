package com.lineage.server.templates;

/**
 * 新手物品資料<BR>
 * 
 * @author dexc
 */
public class L1Beginner {

	private String _activate;
	private int _itemid;
	private int _count;
	private int _enchantlvl;
	private int _charge_count;
	private int _time;

	/**
	 * 傳出分組
	 * 
	 * @return 傳出 _activate
	 */
	public String get_activate() {
		return _activate;
	}

	/**
	 * 設置分組
	 * 
	 * @param activate 對 _activate 進行設置
	 */
	public void set_activate(final String activate) {
		_activate = activate;
	}

	/**
	 * 傳出物品編號
	 * 
	 * @return 傳出 _itemid
	 */
	public int get_itemid() {
		return _itemid;
	}

	/**
	 * 設置物品編號
	 * 
	 * @param itemid 對 _itemid 進行設置
	 */
	public void set_itemid(final int itemid) {
		_itemid = itemid;
	}

	/**
	 * 傳出給予數量
	 * 
	 * @return 傳出 _count
	 */
	public int get_count() {
		return _count;
	}

	/**
	 * 設置給予數量
	 * 
	 * @param count 對 _count 進行設置
	 */
	public void set_count(final int count) {
		_count = count;
	}

	/**
	 * 傳出追加值
	 * 
	 * @return 傳出 _enchantlvl
	 */
	public int get_enchantlvl() {
		return _enchantlvl;
	}

	/**
	 * 設置追加值
	 * 
	 * @param enchantlvl 對 _enchantlvl 進行設置
	 */
	public void set_enchantlvl(final int enchantlvl) {
		_enchantlvl = enchantlvl;
	}

	/**
	 * 傳出可用次數
	 * 
	 * @return 傳出 _charge_count
	 */
	public int get_charge_count() {
		return _charge_count;
	}

	/**
	 * 設置可用次數
	 * 
	 * @param charge_count 對 _charge_count 進行設置
	 */
	public void set_charge_count(final int charge_count) {
		_charge_count = charge_count;
	}

	/**
	 * 使用時間限制(小時)
	 * 
	 * @return
	 */
	public int get_time() {
		return _time;
	}

	/**
	 * 使用時間限制(小時)
	 * 
	 * @param time
	 */
	public void set_time(final int time) {
		_time = time;
	}
}