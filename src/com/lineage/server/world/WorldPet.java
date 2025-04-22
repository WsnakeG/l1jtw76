package com.lineage.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PetInstance;

/**
 * 世界寵物暫存區<BR>
 * 
 * @author dexc
 */
public class WorldPet {

	private static final Log _log = LogFactory.getLog(WorldPet.class);

	private static WorldPet _instance;

	private final ConcurrentHashMap<Integer, L1PetInstance> _isPet;

	private Collection<L1PetInstance> _allPetValues;

	public static WorldPet get() {
		if (_instance == null) {
			_instance = new WorldPet();
		}
		return _instance;
	}

	private WorldPet() {
		_isPet = new ConcurrentHashMap<Integer, L1PetInstance>();
	}

	/**
	 * 全部寵物
	 * 
	 * @return
	 */
	public Collection<L1PetInstance> all() {
		try {
			final Collection<L1PetInstance> vs = _allPetValues;
			return (vs != null) ? vs : (_allPetValues = Collections.unmodifiableCollection(_isPet.values()));
			// return Collections.unmodifiableCollection(_isCrown.values());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 寵物清單
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, L1PetInstance> map() {
		return _isPet;
	}

	/**
	 * 指定寵物數據
	 * 
	 * @param key
	 * @return
	 */
	public L1PetInstance get(final Integer key) {
		try {
			return _isPet.get(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 加入寵物清單
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final Integer key, final L1PetInstance value) {
		try {
			_isPet.put(key, value);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出寵物清單
	 * 
	 * @param key
	 */
	public void remove(final Integer key) {
		try {
			_isPet.remove(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
