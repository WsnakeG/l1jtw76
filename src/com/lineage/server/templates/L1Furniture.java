package com.lineage.server.templates;

/**
 * 置放家具暫時資料
 * 
 * @author daien
 */
public class L1Furniture {

	private int _npcid;// 對應的NPC編號

	private int _item_obj_id;// 對應的物品編號

	private int _locx;// 放置的X座標

	private int _locy;// 放置的Y座標

	private short _mapid;// 放置的地圖編號

	/**
	 * 對應的NPC編號
	 * 
	 * @return the _npcid
	 */
	public int get_npcid() {
		return _npcid;
	}

	/**
	 * 對應的NPC編號
	 * 
	 * @param npcid the _npcid to set
	 */
	public void set_npcid(final int npcid) {
		_npcid = npcid;
	}

	/**
	 * 對應的物品編號
	 * 
	 * @return the _item_obj_id
	 */
	public int get_item_obj_id() {
		return _item_obj_id;
	}

	/**
	 * 對應的物品編號
	 * 
	 * @param itemObjId the _item_obj_id to set
	 */
	public void set_item_obj_id(final int itemObjId) {
		_item_obj_id = itemObjId;
	}

	/**
	 * 放置的X座標
	 * 
	 * @return the _locx
	 */
	public int get_locx() {
		return _locx;
	}

	/**
	 * 放置的X座標
	 * 
	 * @param locx the _locx to set
	 */
	public void set_locx(final int locx) {
		_locx = locx;
	}

	/**
	 * 放置的Y座標
	 * 
	 * @return the _locy
	 */
	public int get_locy() {
		return _locy;
	}

	/**
	 * 放置的Y座標
	 * 
	 * @param locy the _locy to set
	 */
	public void set_locy(final int locy) {
		_locy = locy;
	}

	/**
	 * 放置的地圖編號
	 * 
	 * @return the _mapid
	 */
	public short get_mapid() {
		return _mapid;
	}

	/**
	 * 放置的地圖編號
	 * 
	 * @param mapid the _mapid to set
	 */
	public void set_mapid(final short mapid) {
		_mapid = mapid;
	}
}