package com.lineage.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1HierarchInstance;

/**
 * 世界祭司暫存區<BR>
 * 
 * @author KZK
 */
public class WorldHierarch {

	private static final Log _log = LogFactory.getLog(WorldHierarch.class);

	private static WorldHierarch _instance;

	private final ConcurrentHashMap<Integer, L1HierarchInstance> _isHierarchs;

	private Collection<L1HierarchInstance> _allHierarchValues;

	public static WorldHierarch get() {
		if (_instance == null) {
			_instance = new WorldHierarch();
		}
		return _instance;
	}

	private WorldHierarch() {
		_isHierarchs = new ConcurrentHashMap<Integer, L1HierarchInstance>();
	}

	/**
	 * 全部祭司
	 * 
	 * @return
	 */
	public Collection<L1HierarchInstance> all() {
		try {
			final Collection<L1HierarchInstance> vs = _allHierarchValues;
			return (vs != null) ? vs
					: (_allHierarchValues = Collections.unmodifiableCollection(_isHierarchs.values()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 祭司清單
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, L1HierarchInstance> map() {
		return _isHierarchs;
	}

	/**
	 * 加入祭司清單
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final Integer key, final L1HierarchInstance value) {
		try {
			_isHierarchs.put(key, value);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出祭司清單
	 * 
	 * @param key
	 */
	public void remove(final Integer key) {
		try {
			_isHierarchs.remove(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得世界祭司數量
	 * 
	 * @return
	 */
	public int getWorldHierarchAmount() {
		return _isHierarchs.size();
	}
}
