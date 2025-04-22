package com.lineage.server.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.datatables.QuestTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1QuestUser;

/**
 * 世界副本任務執行管理器<BR>
 * 
 * @author dexc
 */
public class WorldQuest {

	private static final Log _log = LogFactory.getLog(WorldQuest.class);

	private final Lock _lock;

	private static WorldQuest _instance;

	private final AtomicInteger _nextId;

	private final ConcurrentHashMap<Integer, L1QuestUser> _isQuest;

	private Collection<L1QuestUser> _allQuestValues;

	public static WorldQuest get() {
		if (_instance == null) {
			_instance = new WorldQuest();
		}
		return _instance;
	}

	private WorldQuest() {
		_lock = new ReentrantLock(true);
		_isQuest = new ConcurrentHashMap<Integer, L1QuestUser>();
		_nextId = new AtomicInteger(100);
	}

	/**
	 * 取回新的副本編號
	 * 
	 * @return
	 */
	public int nextId() {
		_lock.lock();
		try {
			final int objid = _nextId.getAndIncrement();
			return objid;

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 目前的副本編號使用數字
	 * 
	 * @return
	 */
	public int maxId() {
		_lock.lock();
		try {
			return _nextId.get();

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 全部執行中副本
	 * 
	 * @return
	 */
	public Collection<L1QuestUser> all() {
		try {
			final Collection<L1QuestUser> vs = _allQuestValues;
			return (vs != null) ? vs
					: (_allQuestValues = Collections.unmodifiableCollection(_isQuest.values()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 副本清單
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, L1QuestUser> map() {
		return _isQuest;
	}

	/**
	 * 執行中指定任務編號副本數據
	 * 
	 * @param questId 任務編號
	 * @return 副本清單
	 */
	public ArrayList<L1QuestUser> getQuests(final int questId) {
		try {

			final ArrayList<L1QuestUser> questList = new ArrayList<L1QuestUser>();
			if (_isQuest.size() > 0) {
				for (final Iterator<L1QuestUser> iter = all().iterator(); iter.hasNext();) {
					final L1QuestUser quest = iter.next();
					// for (L1QuestUser quest : _isQuest.values()) {
					if (quest.get_questid() == questId) {
						questList.add(quest);
					}
				}
			}
			return questList;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 指定副本數據
	 * 
	 * @param key 副本編號
	 * @return
	 */
	public L1QuestUser get(final int key) {
		try {
			return _isQuest.get(new Integer(key));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 加入副本清單<BR>
	 * 該ID副本不存將會建立新的副本
	 * 
	 * @param key 副本編號
	 * @param mapid 副本地圖編號
	 * @param questid 副本任務編號
	 * @param pc 執行者
	 * @return
	 */
	public L1QuestUser put(final int key, final int mapid, final int questid, final L1PcInstance pc) {
		try {
			// 不是副本地圖
			if (!QuestMapTable.get().isQuestMap(mapid)) {
				_log.error("副本地圖編號錯誤: 原因-找不到這個副本地圖的設置 MapId:" + mapid);
				return null;
			}
			// 不是任務副本編號
			if (QuestTable.get().getTemplate(questid) == null) {
				_log.error("副本地圖編號錯誤: 原因-找不到這個副本任務的設置 questid:" + questid);
				return null;
			}

			L1QuestUser value = _isQuest.get(new Integer(key));
			if (value != null) {
				pc.set_showId(key);
				value.add(pc);

			} else {
				// 初始化建立副本
				value = new L1QuestUser(key, mapid, questid);
				pc.set_showId(key);
				value.add(pc);

				// 召喚副本怪物
				value.spawnQuestMob();
			}
			_isQuest.put(new Integer(key), value);
			return value;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	public L1QuestUser put(final int key, final int mapid, final int questid) {
		try {
			// 不是副本地圖
			if (!QuestMapTable.get().isQuestMap(mapid)) {
				_log.error("副本地圖編號錯誤: 原因-找不到這個副本地圖的設置 MapId:" + mapid);
				return null;
			}
			// 不是任務副本編號
			if (QuestTable.get().getTemplate(questid) == null) {
				_log.error("副本地圖編號錯誤: 原因-找不到這個副本任務的設置 questid:" + questid);
				return null;
			}

			L1QuestUser value = _isQuest.get(new Integer(key));
			if (value == null) {
				// 初始化建立副本
				value = new L1QuestUser(key, mapid, questid);

				// 召喚副本怪物
				value.spawnQuestMob();
				_isQuest.put(new Integer(key), value);
			}
			return value;
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 移出副本清單(怪物)
	 * 
	 * @param key 副本編號
	 * @param npc 移除的怪物
	 */
	public void remove(final int key, final L1NpcInstance npc) {
		try {
			final L1QuestUser value = _isQuest.get(new Integer(key));
			if (value != null) {
				value.removeMob(npc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出副本清單<BR>
	 * 當副本人數小於等於0將會結束副本
	 * 
	 * @param key 副本編號
	 * @param pc 執行者
	 */
	public void remove(final int key, final L1PcInstance pc) {
		try {
			final L1QuestUser value = _isQuest.get(new Integer(key));
			if (value != null) {
				pc.set_showId(-1);
				value.remove(pc);

				boolean isRemove = false;
				if (value.is_outStop()) {
					isRemove = true;
				}
				if (value.size() <= 0) {
					isRemove = true;
				}
				if (isRemove) {
					// 人數為0移除副本
					_isQuest.remove(new Integer(key));
					// 任務失敗
					value.endQuest();
					// 移除副本中怪物
					value.removeMob();
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 副本編號 是執行中副本
	 * 
	 * @param key 副本編號
	 * @return true:是 false:不是<BR>
	 *         傳回false並非代表該編號無使用價值<BR>
	 *         亦有可能是 農場使用編號
	 */
	public boolean isQuest(final int key) {
		try {
			final L1QuestUser value = _isQuest.get(new Integer(key));
			if (value != null) {
				return true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}
}
