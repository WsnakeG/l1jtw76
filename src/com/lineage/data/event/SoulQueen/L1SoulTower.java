package com.lineage.data.event.SoulQueen;

import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.templates.MapData;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 屍魂塔副本
 * 
 * @author jeLiu
 */
public class L1SoulTower {

	boolean[] mapStat = new boolean[50];
	static L1SoulTower _soulTower;

	private L1SoulTower() {
		for (int i = 1; i < 50; i++) {
			// 複製地圖
			L1WorldMap.get().cloneMap(4001, 4001 + i);
			// 世界加入地圖
			World.get().addMap(4001 + i);
			MapData mapdata = MapsTable.get().getMapData(4001);
			MapsTable.get().setMaps(4001 + i, mapdata);
		}
	}

	public static L1SoulTower get() {
		if (_soulTower == null) {
			_soulTower = new L1SoulTower();
		}
		return _soulTower;
	}

	/**
	 * 人物進入副本
	 * @param pc
	 */
	public void soulTowerStart(final L1PcInstance pc) {
		for (int i = 0; i < mapStat.length; i++) {
			if (!mapStat[i]) {
				mapStat[i] = true;
				final SoulTowerThread thread = new SoulTowerThread(4001 + i, pc);
				//pc.setTempThread(thread);
				GeneralThreadPool.get().execute(thread);
				return;
			}
		}
	}
}
