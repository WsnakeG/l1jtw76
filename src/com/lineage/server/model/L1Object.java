package com.lineage.server.model;

import java.io.Serializable;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;

/**
 * 世界物件共用容器
 */
public class L1Object implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 傳回地圖編號
	 * 
	 * @return 地圖編號
	 */
	public short getMapId() {
		return (short) _loc.getMap().getId();
	}

	/**
	 * 設置地圖編號
	 * 
	 * @param mapId 地圖編號
	 */
	public void setMap(final short mapId) {
		_loc.setMap(L1WorldMap.get().getMap(mapId));
	}

	/**
	 * 傳回 L1Map
	 */
	public L1Map getMap() {
		return _loc.getMap();
	}

	/**
	 * 設置 L1Map
	 * 
	 * @param map オブジェクトが存在するマップを保持するL1Mapオブジェクト
	 */
	public void setMap(final L1Map map) {
		if (map == null) {
			throw new NullPointerException();
		}
		_loc.setMap(map);
	}

	private int _id = 0;

	/**
	 * 世界識別用編號(OBJID)
	 * 
	 * @return 識別用編號
	 */
	public int getId() {
		return _id;
	}

	/**
	 * 設置世界識別用編號(OBJID)
	 * 
	 * @param id 識別用編號
	 */
	public void setId(final int id) {
		_id = id;
	}

	/**
	 * 物件目前X座標值
	 * 
	 * @return 座標X値
	 */
	public int getX() {
		return _loc.getX();
	}

	/**
	 * 設置物件目前X座標值
	 * 
	 * @param x 座標X値
	 */
	public void setX(final int x) {
		_loc.setX(x);
	}

	/**
	 * 物件目前Y座標值
	 * 
	 * @return 座標Y値
	 */
	public int getY() {
		return _loc.getY();
	}

	/**
	 * 設置物件目前Y座標值
	 * 
	 * @param y 座標Y値
	 */
	public void setY(final int y) {
		_loc.setY(y);
	}

	private final L1Location _loc = new L1Location();// 物件座標資訊

	/**
	 * 返回物件的 L1Location
	 * 
	 * @return L1Location
	 */
	public L1Location getLocation() {
		return _loc;
	}

	/**
	 * 設置物件座標資料
	 * 
	 * @param loc
	 */
	public void setLocation(final L1Location loc) {
		_loc.setX(loc.getX());
		_loc.setY(loc.getY());
		_loc.setMap(loc.getMapId());
	}

	/**
	 * 設置物件座標資料
	 * 
	 * @param x
	 * @param y
	 * @param mapid
	 */
	public void setLocation(final int x, final int y, final int mapid) {
		_loc.setX(x);
		_loc.setY(y);
		_loc.setMap(mapid);
	}

	/**
	 * 指定されたオブジェクトまでの直線距離を返す。
	 */
	public double getLineDistance(final L1Object obj) {
		return getLocation().getLineDistance(obj.getLocation());
	}

	/**
	 * 指定座標直線距離(相對位置最大距離)
	 */
	public int getTileLineDistance(final L1Object obj) {
		return getLocation().getTileLineDistance(obj.getLocation());
	}

	/**
	 * 指定座標距離(XY距離總合)
	 */
	public int getTileDistance(final L1Object obj) {
		return getLocation().getTileDistance(obj.getLocation());
	}

	/**
	 * 周邊遭遇人物的調用
	 * 
	 * @param perceivedFrom 遭遇的人物
	 */
	public void onPerceive(final L1PcInstance perceivedFrom) {
	}

	/**
	 * 對該物件攻擊的調用
	 * 
	 * @param actionFrom 攻擊者
	 */
	public void onAction(final L1PcInstance actionFrom) {
	}

	/**
	 * 具備對話能力NPC的對話顯示的調用
	 * 
	 * @param talkFrom 對話的人物
	 */
	public void onTalkAction(final L1PcInstance talkFrom) {
	}

	private int _showId = -1; // 可見(副本)編號

	/**
	 * 可見(副本)編號
	 * 
	 * @return
	 */
	public int get_showId() {
		return _showId;
	}

	/**
	 * 可見(副本)編號<BR>
	 * 副本編號設置必須在加入世界物件之前<BR>
	 * 避免部分封包發送失敗<BR>
	 * 
	 * @param showId
	 */
	public void set_showId(final int showId) {
		_showId = showId;
	}
}
