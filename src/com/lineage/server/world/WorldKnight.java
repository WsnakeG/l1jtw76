package com.lineage.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 世界人物暫存區(區分職業)<BR>
 * 騎士
 * 
 * @author dexc
 */
public class WorldKnight {

	private static final Log _log = LogFactory.getLog(WorldKnight.class);

	private static WorldKnight _instance;

	private final ConcurrentHashMap<Integer, L1PcInstance> _isKnight;

	private Collection<L1PcInstance> _allPlayer;

	public static WorldKnight get() {
		if (_instance == null) {
			_instance = new WorldKnight();
		}
		return _instance;
	}

	private WorldKnight() {
		_isKnight = new ConcurrentHashMap<Integer, L1PcInstance>();
	}

	/**
	 * 全部騎士玩家
	 * 
	 * @return
	 */
	public Collection<L1PcInstance> all() {
		try {
			final Collection<L1PcInstance> vs = _allPlayer;
			return (vs != null) ? vs : (_allPlayer = Collections.unmodifiableCollection(_isKnight.values()));
			// return Collections.unmodifiableCollection(_isCrown.values());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 騎士玩家清單
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, L1PcInstance> map() {
		return _isKnight;
	}

	/**
	 * 加入騎士玩家清單
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final Integer key, final L1PcInstance value) {
		try {
			_isKnight.put(key, value);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出騎士玩家清單
	 * 
	 * @param key
	 */
	public void remove(final Integer key) {
		try {
			_isKnight.remove(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
