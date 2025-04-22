package com.lineage.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class L1UbPattern {

	private boolean _isFrozen = false;

	private final Map<Integer, ArrayList<L1UbSpawn>> _groups = new HashMap<Integer, ArrayList<L1UbSpawn>>();

	public void addSpawn(final int groupNumber, final L1UbSpawn spawn) {
		if (_isFrozen) {
			return;
		}

		ArrayList<L1UbSpawn> spawnList = _groups.get(groupNumber);
		if (spawnList == null) {
			spawnList = new ArrayList<L1UbSpawn>();
			_groups.put(groupNumber, spawnList);
		}

		spawnList.add(spawn);
	}

	public void freeze() {
		if (_isFrozen) {
			return;
		}

		for (final ArrayList<L1UbSpawn> spawnList : _groups.values()) {
			Collections.sort(spawnList);
		}

		_isFrozen = true;
	}

	public boolean isFrozen() {
		return _isFrozen;
	}

	public ArrayList<L1UbSpawn> getSpawnList(final int groupNumber) {
		if (!_isFrozen) {
			return null;
		}

		return _groups.get(groupNumber);
	}
}
