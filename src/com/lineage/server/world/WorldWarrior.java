package com.lineage.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 世界人物暫存區(區分職業)<BR>
 * 戰士
 * 
 * @author simlin
 */
public class WorldWarrior {

	private static final Log _log = LogFactory.getLog(WorldWarrior.class);

	private static WorldWarrior _instance;

	private final ConcurrentHashMap<Integer, L1PcInstance> _isWarrior;

	private Collection<L1PcInstance> _allPlayer;

	public static WorldWarrior get() {
		if (_instance == null) {
			_instance = new WorldWarrior();
		}
		return _instance;
	}

	private WorldWarrior() {
		_isWarrior = new ConcurrentHashMap<Integer, L1PcInstance>();
	}

	/**
	 * 全部戰士玩家
	 * 
	 * @return
	 */
	public Collection<L1PcInstance> all() {
		try {
			final Collection<L1PcInstance> vs = _allPlayer;
			return (vs != null) ? vs : (_allPlayer = Collections.unmodifiableCollection(_isWarrior.values()));
			// return Collections.unmodifiableCollection(_isCrown.values());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 戰士玩家清單
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, L1PcInstance> map() {
		return _isWarrior;
	}

	/**
	 * 加入戰士玩家清單
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final Integer key, final L1PcInstance value) {
		try {
			_isWarrior.put(key, value);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出戰士玩家清單
	 * 
	 * @param key
	 */
	public void remove(final Integer key) {
		try {
			_isWarrior.remove(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
