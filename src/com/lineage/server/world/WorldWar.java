package com.lineage.server.world;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1War;

/**
 * 世界戰爭暫存區<BR>
 * 
 * @author dexc
 */
public class WorldWar {

	private static final Log _log = LogFactory.getLog(WorldWar.class);

	private static WorldWar _instance;

	private final CopyOnWriteArrayList<L1War> _allWars;

	private List<L1War> _allWarList;

	public static WorldWar get() {
		if (_instance == null) {
			_instance = new WorldWar();
		}
		return _instance;
	}

	private WorldWar() {
		_allWars = new CopyOnWriteArrayList<L1War>(); // 全部戰爭
	}

	/**
	 * 加入戰爭清單
	 * 
	 * @param war
	 */
	public void addWar(final L1War war) {
		try {
			if (!_allWars.contains(war)) {
				_allWars.add(war);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出戰爭清單
	 * 
	 * @param war
	 */
	public void removeWar(final L1War war) {
		try {
			if (_allWars.contains(war)) {
				_allWars.remove(war);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 全部戰爭
	 * 
	 * @return
	 */
	public List<L1War> getWarList() {
		try {
			final List<L1War> vs = _allWarList;
			return (vs != null) ? vs : (_allWarList = Collections.unmodifiableList(_allWars));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 是否為對戰中血盟
	 * 
	 * @param clanname
	 * @param tgclanname
	 * @return
	 */
	public boolean isWar(final String clanname, final String tgclanname) {
		try {
			for (final L1War war : _allWars) {
				if (war.isWarTimerDelete()) {
					_allWars.remove(war);
					continue;
				}
				final boolean isInWar = war.checkClanInWar(clanname);
				if (isInWar) {
					final boolean isInWarTg = war.checkClanInWar(tgclanname);
					if (isInWarTg) {
						return true;
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}
}
