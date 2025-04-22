package com.lineage.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1DeInstance;

/**
 * DE人物暫存區<BR>
 * 
 * @author dexc
 */
public class WorldDe {

	private static final Log _log = LogFactory.getLog(WorldDe.class);

	private static WorldDe _instance;

	private final ConcurrentHashMap<String, L1DeInstance> _isDe;

	private Collection<L1DeInstance> _allDe;

	public static WorldDe get() {
		if (_instance == null) {
			_instance = new WorldDe();
		}
		return _instance;
	}

	private WorldDe() {
		_isDe = new ConcurrentHashMap<String, L1DeInstance>();
	}

	/**
	 * 全部DE玩家
	 * 
	 * @return
	 */
	public Collection<L1DeInstance> all() {
		try {
			final Collection<L1DeInstance> vs = _allDe;
			return (vs != null) ? vs : (_allDe = Collections.unmodifiableCollection(_isDe.values()));
			// return Collections.unmodifiableCollection(_isCrown.values());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * DE玩家清單
	 * 
	 * @return
	 */
	public ConcurrentHashMap<String, L1DeInstance> map() {
		return _isDe;
	}

	/**
	 * DE玩家清單
	 * 
	 * @param key
	 * @return
	 */
	public L1DeInstance getDe(final String key) {
		try {
			return _isDe.get(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 加入DE玩家清單
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final String key, final L1DeInstance value) {
		try {
			_isDe.put(key, value);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出DE玩家清單
	 * 
	 * @param key
	 */
	public void remove(final String key) {
		try {
			_isDe.remove(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
