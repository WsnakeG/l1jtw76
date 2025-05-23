package com.lineage.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;

/**
 * 世界血盟暫存區<BR>
 * 
 * @author dexc
 */
public class WorldClan {

	private static final Log _log = LogFactory.getLog(WorldClan.class);

	private static WorldClan _instance;

	private final ConcurrentHashMap<String, L1Clan> _isClan;

	private final HashMap<Integer, L1Clan> _isShowClan = new HashMap<Integer, L1Clan>();

	private Collection<L1Clan> _allClanValues;

	public static WorldClan get() {
		if (_instance == null) {
			_instance = new WorldClan();
		}
		return _instance;
	}

	private WorldClan() {
		_isClan = new ConcurrentHashMap<String, L1Clan>();
	}

	/**
	 * 全部血盟
	 * 
	 * @return
	 */
	public Collection<L1Clan> getAllClans() {
		try {
			final Collection<L1Clan> vs = _allClanValues;
			return (vs != null) ? vs
					: (_allClanValues = Collections.unmodifiableCollection(_isClan.values()));
			// return Collections.unmodifiableCollection(_isCrown.values());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 加入血盟清單
	 * 
	 * @param clan
	 */
	public void storeClan(final L1Clan clan) {
		final L1Clan temp = this.getClan(clan.getClanName());
		if (temp == null) {
			_isClan.put(clan.getClanName(), clan);
			final int castle_id = clan.getCastleId();
			if (castle_id != 0) {
				if (L1CastleLocation.mapCastle().get(new Integer(castle_id)) == null) {
					L1CastleLocation.putCastle(new Integer(castle_id), clan);
				}
			}
		}
	}

	/**
	 * 移出血盟清單
	 * 
	 * @param clan
	 */
	public void removeClan(final L1Clan clan) {
		final L1Clan temp = this.getClan(clan.getClanName());
		if (temp != null) {
			_isClan.remove(clan.getClanName());
		}
	}

	/**
	 * 傳回血盟數據
	 * 
	 * @param clan_name
	 * @return
	 */
	public L1Clan getClan(final String clan_name) {
		return _isClan.get(clan_name);
	}

	/**
	 * 傳回血盟數據
	 * 
	 * @param clan_iconid
	 * @return
	 */
	public L1Clan getClan(final int clan_iconid) {
		return _isShowClan.get(clan_iconid);
	}

	/**
	 * 血盟清單
	 * 
	 * @return
	 */
	public ConcurrentHashMap<String, L1Clan> map() {
		return _isClan;
	}

	/**
	 * 有城堡的血盟清單
	 * 
	 * @return
	 */
	public HashMap<Integer, String> castleClanMap() {
		// <城堡編號, 血盟名稱>
		final HashMap<Integer, String> isClan = new HashMap<Integer, String>();

		for (final Iterator<L1Clan> iter = getAllClans().iterator(); iter.hasNext();) {
			final L1Clan clan = iter.next();
			if (clan.getCastleId() != 0) {
				isClan.put(clan.getCastleId(), clan.getClanName());
			}
		}
		return isClan;
	}

	/**
	 * 加入血盟清單
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final String key, final L1Clan value) {
		try {
			_isClan.put(key, value);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出血盟清單
	 * 
	 * @param key
	 */
	public void remove(final String key) {
		try {
			_isClan.remove(key);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
