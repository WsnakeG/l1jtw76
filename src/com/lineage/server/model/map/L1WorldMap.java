package com.lineage.server.model.map;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.MapReader;
import com.lineage.TextMapReader;
import com.lineage.server.utils.PerformanceTimer;

/**
 * 世界地圖資訊
 * 
 * @author dexc
 */
public class L1WorldMap {

	private static final Log _log = LogFactory.getLog(TextMapReader.class);

	private static L1WorldMap _instance;

	// MAPID MAP資訊
	private Map<Integer, L1Map> _maps;

	public static L1WorldMap get() {
		if (_instance == null) {
			_instance = new L1WorldMap();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		_log.info("MAP進行數據加載...");

		final MapReader in = new TextMapReader();

		_maps = in.read();
		if (_maps == null) {
			_log.error("MAP數據載入異常 maps未建立初始化");
			System.exit(0);
			return;
		}

		_log.info("MAP數據加載完成(" + timer.get() + " ms)");
	}

	/**
	 * 指定地圖編號 返回地圖資訊
	 * 
	 * @param mapId 地圖編號
	 * @return 地圖資訊
	 */
	public L1Map getMap(final short mapId) {
		L1Map map = _maps.get((int) mapId);
		if (map == null) {
			map = L1Map.newNull();
		}
		return map;
	}
	
	/**
	 * 創建地圖副本 屍魂塔
	 * 
	 * @param targetId
	 *            目標地圖ID
	 * @param newId
	 *            新地圖ID
	 */
	public void cloneMap(int targetId, int newId) {
		L1Map copymap = null;
		//System.out.println("mapid:"+targetId+"test1:"+((L1Map) this._maps.get(Integer.valueOf(targetId))).isUsableItem());
		copymap = ((L1Map) this._maps.get(Integer.valueOf(targetId))).copyMap(newId);
		
		this._maps.put(Integer.valueOf(newId), copymap);
		//System.out.println("mapid:"+newId+"test2:"+((L1Map) this._maps.get(Integer.valueOf(newId))).isUsableItem());
	}
}
