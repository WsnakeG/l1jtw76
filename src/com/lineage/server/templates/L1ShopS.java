package com.lineage.server.templates;

import java.sql.Timestamp;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 託售物品暫存
 * 
 * @author daien
 */
public class L1ShopS {

	private int _id;// 編號

	private int _item_obj_id;// 物品OBJID

	private int _user_obj_id;// 出售人OBJID

	private int _adena;// 售價

	private Timestamp _overtime;// 拍賣終止時間

	// 結束模式 0:出售中 1:已售出 2:已售出領回貨幣 3:未售出 4:未售出道具以領回
	private int _end;

	private String _none;// 備註

	private L1ItemInstance _item = null;// 物品資料

	/**
	 * 編號
	 * 
	 * @return the _id
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * 編號
	 * 
	 * @param id the _id to set
	 */
	public void set_id(final int id) {
		_id = id;
	}

	/**
	 * 物品OBJID
	 * 
	 * @return the _item_obj_id
	 */
	public int get_item_obj_id() {
		return _item_obj_id;
	}

	/**
	 * 物品OBJID
	 * 
	 * @param itemObjId the _item_obj_id to set
	 */
	public void set_item_obj_id(final int itemObjId) {
		_item_obj_id = itemObjId;
	}

	/**
	 * 出售人OBJID
	 * 
	 * @return the _user_obj_id
	 */
	public int get_user_obj_id() {
		return _user_obj_id;
	}

	/**
	 * 出售人OBJID
	 * 
	 * @param userObjId the _user_obj_id to set
	 */
	public void set_user_obj_id(final int userObjId) {
		_user_obj_id = userObjId;
	}

	/**
	 * 售價
	 * 
	 * @return the _adena
	 */
	public int get_adena() {
		return _adena;
	}

	/**
	 * 售價
	 * 
	 * @param adena the _adena to set
	 */
	public void set_adena(final int adena) {
		_adena = adena;
	}

	/**
	 * 拍賣終止時間
	 * 
	 * @return the _overtime
	 */
	public Timestamp get_overtime() {
		return _overtime;
	}

	/**
	 * 拍賣終止時間
	 * 
	 * @param overtime the _overtime to set
	 */
	public void set_overtime(final Timestamp overtime) {
		_overtime = overtime;
	}

	/**
	 * 結束模式 0:出售中 1:已售出 2:已售出領回貨幣 3:未售出 4:未售出道具以領回
	 * 
	 * @return the _end
	 */
	public int get_end() {
		return _end;
	}

	/**
	 * 結束模式 0:出售中 1:已售出 2:已售出領回貨幣 3:未售出 4:未售出道具以領回
	 * 
	 * @param end the _end to set
	 */
	public void set_end(final int end) {
		_end = end;
	}

	/**
	 * 備註
	 * 
	 * @return the _none
	 */
	public String get_none() {
		return _none;
	}

	/**
	 * 備註
	 * 
	 * @param none the _none to set
	 */
	public void set_none(final String none) {
		_none = none;
	}

	/**
	 * 物品資料
	 * 
	 * @return
	 */
	public L1ItemInstance get_item() {
		return _item;
	}

	/**
	 * 物品資料
	 * 
	 * @param item
	 */
	public void set_item(final L1ItemInstance item) {
		_item = item;
	}
}
