package com.lineage.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 世界人物暫存區(區分職業)<BR>
 * 精靈
 * 
 * @author dexc
 */
public class WorldElf {

	private static final Log _log = LogFactory.getLog(WorldElf.class);

	private static WorldElf _instance;

	private final ConcurrentHashMap<Integer, L1PcInstance> _isElf;

	private Collection<L1PcInstance> _allPlayer;

	public static WorldElf get() {
		if (_instance == null) {
			_instance = new WorldElf();
		}
		return _instance;
	}

	private WorldElf() {
		_isElf = new ConcurrentHashMap<Integer, L1PcInstance>();
	}

	/**
	 * 全部精靈玩家
	 * 
	 * @return
	 */
	public Collection<L1PcInstance> all() {
		try {
			final Collection<L1PcInstance> vs = _allPlayer;
			return (vs != null) ? vs : (_allPlayer = Collections.unmodifiableCollection(_isElf.values()));
			// return Collections.unmodifiableCollection(_isCrown.values());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 精靈玩家清單
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, L1PcInstance> map() {
		return _isElf;
	}

	/**
	 * 加入精靈玩家清單
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final Integer key, final L1PcInstance value) {
		try {
			_isElf.put(key, value);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出精靈玩家清單
	 * 
	 * @param key
	 */
	public void remove(final Integer key) {
		try {
			_isElf.remove(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
