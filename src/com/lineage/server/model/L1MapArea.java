package com.lineage.server.model;

import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.types.Rectangle;

public class L1MapArea extends Rectangle {
	private L1Map _map = L1Map.newNull();

	public L1Map getMap() {
		return _map;
	}

	public void setMap(final L1Map map) {
		_map = map;
	}

	public int getMapId() {
		return _map.getId();
	}

	public L1MapArea(final int left, final int top, final int right, final int bottom, final int mapId) {
		super(left, top, right, bottom);

		_map = L1WorldMap.get().getMap((short) mapId);
	}

	public boolean contains(final L1Location loc) {
		return (_map.getId() == loc.getMap().getId()) && super.contains(loc);
	}
}
