package com.lineage.server.templates;

/**
 * QUEST NPC召喚資料 暫存
 * 
 * @author daien
 */
public class L1QuestMobSpawn {

	private int _questid;// QUEST編號

	private int _count;// 數量

	private int _npc_templateid;// NPCID

	private int _group_id;// NPC隊伍ID

	private int _locx1;// X1

	private int _locy1;// Y1

	private int _locx2;// X2

	private int _locy2;// Y2

	private int _heading;// 面向

	private int _mapid;// mapid

	private int _round;

	/**
	 * QUEST編號
	 * 
	 * @return
	 */
	public int get_questid() {
		return _questid;
	}

	/**
	 * QUEST編號
	 * 
	 * @param _questid
	 */
	public void set_questid(final int _questid) {
		this._questid = _questid;
	}

	/**
	 * 數量
	 * 
	 * @return
	 */
	public int get_count() {
		return _count;
	}

	/**
	 * 數量
	 * 
	 * @param _count
	 */
	public void set_count(final int _count) {
		this._count = _count;
	}

	/**
	 * NPCID
	 * 
	 * @return
	 */
	public int get_npc_templateid() {
		return _npc_templateid;
	}

	/**
	 * NPCID
	 * 
	 * @param _npc_templateid
	 */
	public void set_npc_templateid(final int _npc_templateid) {
		this._npc_templateid = _npc_templateid;
	}

	/**
	 * NPC隊伍ID
	 * 
	 * @return
	 */
	public int get_group_id() {
		return _group_id;
	}

	/**
	 * NPC隊伍ID
	 * 
	 * @param _group_id
	 */
	public void set_group_id(final int _group_id) {
		this._group_id = _group_id;
	}

	/**
	 * X1
	 * 
	 * @return
	 */
	public int get_locx1() {
		return _locx1;
	}

	/**
	 * X1
	 * 
	 * @param _locx1
	 */
	public void set_locx1(final int _locx1) {
		this._locx1 = _locx1;
	}

	/**
	 * Y1
	 * 
	 * @return
	 */
	public int get_locy1() {
		return _locy1;
	}

	/**
	 * Y1
	 * 
	 * @param _locy1
	 */
	public void set_locy1(final int _locy1) {
		this._locy1 = _locy1;
	}

	/**
	 * X2
	 * 
	 * @return
	 */
	public int get_locx2() {
		return _locx2;
	}

	/**
	 * X2
	 * 
	 * @param _locx2
	 */
	public void set_locx2(final int _locx2) {
		this._locx2 = _locx2;
	}

	/**
	 * Y2
	 * 
	 * @return
	 */
	public int get_locy2() {
		return _locy2;
	}

	/**
	 * Y2
	 * 
	 * @param _locy2
	 */
	public void set_locy2(final int _locy2) {
		this._locy2 = _locy2;
	}

	/**
	 * 面向
	 * 
	 * @return
	 */
	public int get_heading() {
		return _heading;
	}

	/**
	 * 面向
	 * 
	 * @param _heading
	 */
	public void set_heading(final int _heading) {
		this._heading = _heading;
	}

	/**
	 * mapid
	 * 
	 * @return
	 */
	public int get_mapid() {
		return _mapid;
	}

	/**
	 * mapid
	 * 
	 * @param _mapid
	 */
	public void set_mapid(final int _mapid) {
		this._mapid = _mapid;
	}

	/**
	 * 副本怪物 round編號 by terry0412
	 * 
	 * @return
	 */
	public int get_round() {
		return _round;
	}

	public void set_round(final int _round) {
		this._round = _round;
	}
}
