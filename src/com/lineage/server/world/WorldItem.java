package com.lineage.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 世界ITEM暫存區<BR>
 * 
 * @author dexc
 */
public class WorldItem {

	private static final Log _log = LogFactory.getLog(WorldItem.class);

	private static WorldItem _instance;

	private final ConcurrentHashMap<Integer, L1ItemInstance> _isItem;

	private Collection<L1ItemInstance> _allItemValues;

	public static WorldItem get() {
		if (_instance == null) {
			_instance = new WorldItem();
		}
		return _instance;
	}

	private WorldItem() {
		_isItem = new ConcurrentHashMap<Integer, L1ItemInstance>();
	}

	/**
	 * 全部ITEM
	 * 
	 * @return
	 */
	public Collection<L1ItemInstance> all() {
		try {
			final Collection<L1ItemInstance> vs = _allItemValues;
			return (vs != null) ? vs
					: (_allItemValues = Collections.unmodifiableCollection(_isItem.values()));
			// return Collections.unmodifiableCollection(_isCrown.values());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * ITEM清單
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, L1ItemInstance> map() {
		return _isItem;
	}

	/**
	 * 加入ITEM清單
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final Integer key, final L1ItemInstance value) {
		try {
			_isItem.put(key, value);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出ITEM清單
	 * 
	 * @param key
	 */
	public void remove(final Integer key) {
		try {
			_isItem.remove(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 傳回ITEM數據
	 * 
	 * @param clan_name
	 * @return
	 */
	public L1ItemInstance getItem(final Integer key) {
		return _isItem.get(key);
	}
}
