package com.lineage.server.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.L1GroundInventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1BowInstance;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1DwarfInstance;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1HierarchInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.Instance.L1TrapInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.types.Point;

/**
 * 遊戲世界儲存中心
 * 
 * @author dexc
 */
public class World {

	private static final Log _log = LogFactory.getLog(World.class);

	// 世界人物資料<pcName, Object>
	private final ConcurrentHashMap<String, L1PcInstance> _allPlayers;

	// 世界物件資料<Objid, Object>
	private final ConcurrentHashMap<Integer, L1Object> _allObjects;

	// 世界物件資料(區分地圖編號)<MapId, <Objid, Object>>
	private final HashMap<Integer, ConcurrentHashMap<Integer, L1Object>> _visibleObjects;

	private static World _instance;

	public World() {
		_allPlayers = new ConcurrentHashMap<String, L1PcInstance>(); // 世界人物資料
		_allObjects = new ConcurrentHashMap<Integer, L1Object>(); // 世界物件資料
		_visibleObjects = new HashMap<Integer, ConcurrentHashMap<Integer, L1Object>>(); // 世界物件資料(區分地圖編號)

		for (final Integer mapid : MapsTable.get().getMaps().keySet()) {
			final ConcurrentHashMap<Integer, L1Object> map = new ConcurrentHashMap<Integer, L1Object>();
			_visibleObjects.put(mapid, map);
		}

		_log.info("遊戲世界儲存中心建立完成!!!");
	}

	public static World get() {
		if (_instance == null) {
			_instance = new World();
		}
		return _instance;
	}

	public Object getRegion(final Object object) {
		return null;
	}

	/**
	 * 世界資料狀態全部重置
	 */
	public void clear() {
		_instance = new World();
	}

	/**
	 * 加入世界
	 * 
	 * @param object
	 */
	public void storeObject(final L1Object object) {
		try {
			if (object == null) {
				throw new NullPointerException();
			}

			_allObjects.put(object.getId(), object);

			if (object instanceof L1ItemInstance) {
				WorldItem.get().put(new Integer(object.getId()), (L1ItemInstance) object);
			}

			if (object instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) object;

				if (pc.isCrown()) {
					WorldCrown.get().put(new Integer(pc.getId()), pc);

				} else if (pc.isKnight()) {
					WorldKnight.get().put(new Integer(pc.getId()), pc);

				} else if (pc.isElf()) {
					WorldElf.get().put(new Integer(pc.getId()), pc);

				} else if (pc.isWizard()) {
					WorldWizard.get().put(new Integer(pc.getId()), pc);

				} else if (pc.isDarkelf()) {
					WorldDarkelf.get().put(new Integer(pc.getId()), pc);

				} else if (pc.isDragonKnight()) {
					WorldDragonKnight.get().put(new Integer(pc.getId()), pc);

				} else if (pc.isIllusionist()) {
					WorldIllusionist.get().put(new Integer(pc.getId()), pc);

				} else if (pc.isWarrior()) {
					WorldWarrior.get().put(new Integer(pc.getId()), pc);
				}
				_allPlayers.put(pc.getName(), pc);
			}
			// DE
			if (object instanceof L1DeInstance) {
				final L1DeInstance de = (L1DeInstance) object;
				WorldDe.get().put(de.getNameId(), de);
			}
			// 陷阱
			if (object instanceof L1TrapInstance) {
				WorldTrap.get().put(new Integer(object.getId()), (L1TrapInstance) object);
			}
			// 寵物
			if (object instanceof L1PetInstance) {
				WorldPet.get().put(new Integer(object.getId()), (L1PetInstance) object);
			}
			// 召喚獸
			if (object instanceof L1SummonInstance) {
				WorldSummons.get().put(new Integer(object.getId()), (L1SummonInstance) object);
			}
			// 魔法娃娃
			if (object instanceof L1DollInstance) {
				WorldDoll.get().put(new Integer(object.getId()), (L1DollInstance) object);
			}
			// Effect(技能物件)
			if (object instanceof L1EffectInstance) {
				WorldEffect.get().put(new Integer(object.getId()), (L1EffectInstance) object);
			}
			// MOB
			if (object instanceof L1MonsterInstance) {
				WorldMob.get().put(new Integer(object.getId()), (L1MonsterInstance) object);
			}
			// BOW
			if (object instanceof L1BowInstance) {
				WorldBow.get().put(new Integer(object.getId()), (L1BowInstance) object);
			}
			// NPC
			if (object instanceof L1NpcInstance) {
				WorldNpc.get().put(new Integer(object.getId()), (L1NpcInstance) object);
			}
			// 祭司
			if (object instanceof L1HierarchInstance) {
				WorldHierarch.get().put(new Integer(object.getId()), (L1HierarchInstance) object);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	public static World getInstance() {
		if (_instance == null) {
			_instance = new World();
		}

		return _instance;
	}

	/**
	 * 移出世界
	 * 
	 * @param object
	 */
	public void removeObject(final L1Object object) {
		// System.out.println("移出世界");
		try {
			if (object == null) {
				throw new NullPointerException();
			}

			_allObjects.remove(object.getId());

			if (object instanceof L1ItemInstance) {
				WorldItem.get().remove(new Integer(object.getId()));
			}

			if (object instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) object;

				if (pc.isCrown()) {
					WorldCrown.get().remove(new Integer(pc.getId()));

				} else if (pc.isKnight()) {
					WorldKnight.get().remove(new Integer(pc.getId()));

				} else if (pc.isElf()) {
					WorldElf.get().remove(new Integer(pc.getId()));

				} else if (pc.isWizard()) {
					WorldWizard.get().remove(new Integer(pc.getId()));

				} else if (pc.isDarkelf()) {
					WorldDarkelf.get().remove(new Integer(pc.getId()));

				} else if (pc.isDragonKnight()) {
					WorldDragonKnight.get().remove(new Integer(pc.getId()));

				} else if (pc.isIllusionist()) {
					WorldIllusionist.get().remove(new Integer(pc.getId()));

				} else if (pc.isWarrior()) {
					WorldWarrior.get().remove(new Integer(pc.getId()));
				}
				_allPlayers.remove(pc.getName());
			}
			// DE
			if (object instanceof L1DeInstance) {
				final L1DeInstance de = (L1DeInstance) object;
				WorldDe.get().remove(de.getNameId());
			}
			// 陷阱
			if (object instanceof L1TrapInstance) {
				WorldTrap.get().remove(new Integer(object.getId()));
			}
			// 寵物
			if (object instanceof L1PetInstance) {
				WorldPet.get().remove(new Integer(object.getId()));
			}
			// 召喚獸
			if (object instanceof L1SummonInstance) {
				WorldSummons.get().remove(new Integer(object.getId()));
			}
			// 魔法娃娃
			if (object instanceof L1DollInstance) {
				WorldDoll.get().remove(new Integer(object.getId()));
			}
			// Effect(技能物件)
			if (object instanceof L1EffectInstance) {
				WorldEffect.get().remove(new Integer(object.getId()));
			}
			// MOB
			if (object instanceof L1MonsterInstance) {
				WorldMob.get().remove(new Integer(object.getId()));
			}
			// BOW
			if (object instanceof L1BowInstance) {
				WorldBow.get().remove(new Integer(object.getId()));
			}
			// NPC
			if (object instanceof L1NpcInstance) {
				WorldNpc.get().remove(new Integer(object.getId()));
			}
			// 祭司
			if (object instanceof L1HierarchInstance) {
				WorldHierarch.get().remove(new Integer(object.getId()));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取回物件
	 * 
	 * @param oID
	 * @return
	 */
	public L1Object findObject(final int oID) {
		if (oID == 0) {
			return null;
		}
		return _allObjects.get(oID);
	}

	// _allObjects 的 Collection
	private Collection<L1Object> _allValues;

	/**
	 * 全部L1Object
	 * 
	 * @return
	 */
	public Collection<L1Object> getObject() {
		try {
			final Collection<L1Object> vs = _allValues;
			return (vs != null) ? vs
					: (_allValues = Collections.unmodifiableCollection(_allObjects.values()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 座標點-遊戲世界地面背包
	 * 
	 * @param x
	 * @param y
	 * @param map
	 * @return
	 */
	public L1GroundInventory getInventory(final int x, final int y, final short map) {
		try {
			// 以XY座標值做為地面背包OBJID(避免衝突 轉為負數)
			final int inventoryKey = (((x - 30000) * 10000) + (y - 30000)) * -1;
			// Integer
			final ConcurrentHashMap<Integer, L1Object> idmap = _visibleObjects.get(new Integer(map));
			if (idmap != null) {
				final Object object = idmap.get(inventoryKey);

				if (object == null) {
					return new L1GroundInventory(inventoryKey, x, y, map);

				} else {
					return (L1GroundInventory) object;
				}

			} else {
				_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + map);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 座標點-遊戲世界地面背包
	 * 
	 * @param loc
	 * @return
	 */
	public L1GroundInventory getInventory(final L1Location loc) {
		return getInventory(loc.getX(), loc.getY(), (short) loc.getMap().getId());
	}

	/**
	 * 加入MAP內物件
	 * 
	 * @param object
	 */
	public void addVisibleObject(final L1Object object) {
		try {
			final ConcurrentHashMap<Integer, L1Object> map = _visibleObjects
					.get(new Integer(object.getMapId()));
			if (map != null) {
				map.put(object.getId(), object);

			} else {
				_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + object.getMapId());
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移除MAP內物件
	 * 
	 * @param object
	 */
	public void removeVisibleObject(final L1Object object) {
		try {
			final ConcurrentHashMap<Integer, L1Object> map = _visibleObjects
					.get(new Integer(object.getMapId()));
			if (map != null) {
				map.remove(object.getId());

			} else {
				_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + object.getMapId());
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移動MAP內物件
	 * 
	 * @param object
	 * @param newMapId
	 */
	public void moveVisibleObject(final L1Object object, final int newMapId) {// set_Mapで新しいMapにするまえに呼ぶこと
		try {
			final int srcMapId = object.getMapId();
			if (srcMapId != newMapId) {
				// 取回原地圖資料
				final ConcurrentHashMap<Integer, L1Object> mapSrc = _visibleObjects
						.get(new Integer(srcMapId));
				if (mapSrc != null) {
					mapSrc.remove(object.getId());

				} else {
					_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + srcMapId);
				}

				// 取回新地圖資料
				final ConcurrentHashMap<Integer, L1Object> map = _visibleObjects.get(new Integer(newMapId));
				if (map != null) {
					map.put(object.getId(), object);

				} else {
					_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + newMapId);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 直線上目標列舉
	 * 
	 * @param src
	 * @param target
	 * @return
	 */
	private HashMap<Integer, Integer> createLineMap(final Point src, final Point target) {
		final HashMap<Integer, Integer> lineMap = new HashMap<Integer, Integer>();

		/*
		 * http://www2.starcat.ne.jp/~fussy/algo/algo1-1.htmより
		 */
		int E;
		int x;
		int y;
		int key;
		int i;
		final int x0 = src.getX();
		final int y0 = src.getY();
		final int x1 = target.getX();
		final int y1 = target.getY();
		final int sx = (x1 > x0) ? 1 : -1;
		final int dx = (x1 > x0) ? x1 - x0 : x0 - x1;
		final int sy = (y1 > y0) ? 1 : -1;
		final int dy = (y1 > y0) ? y1 - y0 : y0 - y1;

		x = x0;
		y = y0;
		/* 傾きが1以下の場合 */
		if (dx >= dy) {
			E = -dx;
			for (i = 0; i <= dx; i++) {
				key = (x << 16) + y;
				lineMap.put(key, key);
				x += sx;
				// E += 2 * dy;
				E += (dy << 1);
				if (E >= 0) {
					y += sy;
					// E -= 2 * dx;
					E -= (dx << 1);
				}
			}
			/* 傾きが1より大きい場合 */
		} else {
			E = -dy;
			for (i = 0; i <= dy; i++) {
				key = (x << 16) + y;
				lineMap.put(key, key);
				y += sy;
				// E += 2 * dx;
				E += (dx << 1);
				if (E >= 0) {
					x += sx;
					// E -= 2 * dy;
					E -= (dy << 1);
				}
			}
		}

		return lineMap;
	}

	/**
	 * 直線距離目標列舉
	 * 
	 * @param src
	 * @param target
	 * @return
	 */
	public ArrayList<L1Object> getVisibleLineObjects(final L1Object src, final L1Object target) {
		try {
			final HashMap<Integer, Integer> lineMap = createLineMap(src.getLocation(), target.getLocation());

			final int map = target.getMapId();
			final ArrayList<L1Object> result = new ArrayList<L1Object>();

			// 取回原地圖資料
			final ConcurrentHashMap<Integer, L1Object> mapSrc = _visibleObjects.get(new Integer(map));
			if (mapSrc == null) {
				_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + map);
				return result;
			}

			for (final L1Object element : mapSrc.values()) {
				if (element.equals(src)) {
					continue;
				}

				// 副本ID不相等
				if (src.get_showId() != element.get_showId()) {
					continue;
				}

				final int key = (element.getX() << 16) + element.getY();
				if (lineMap.containsKey(key)) {
					result.add(element);
				}
			}
			lineMap.clear();
			return result;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 指定範圍目標列舉
	 * 
	 * @param object
	 * @param heading
	 * @param width
	 * @param height
	 * @return
	 */
	public ArrayList<L1Object> getVisibleBoxObjects(final L1Object object, final int heading, final int width,
			final int height) {
		final int x = object.getX();
		final int y = object.getY();
		final int map = object.getMapId();
		final L1Location location = object.getLocation();
		final ArrayList<L1Object> result = new ArrayList<L1Object>();
		final int headingRotate[] = { 6, 7, 0, 1, 2, 3, 4, 5 };
		final double cosSita = Math.cos((headingRotate[heading] * Math.PI) / 4);
		final double sinSita = Math.sin((headingRotate[heading] * Math.PI) / 4);

		// 取回原地圖資料
		final ConcurrentHashMap<Integer, L1Object> mapSrc = _visibleObjects.get(new Integer(map));
		if (mapSrc == null) {
			_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + map);
			return result;
		}

		for (final L1Object element : mapSrc.values()) {
			if (element.equals(object)) {
				continue;
			}

			// 副本ID不相等
			if (object.get_showId() != element.get_showId()) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			// 同じ座標に重なっている場合は範囲内とする
			if (location.isSamePoint(element.getLocation())) {
				result.add(element);
				continue;
			}

			final int distance = location.getTileLineDistance(element.getLocation());
			// 直線距離が高さ、幅どちらよりも大きい場合、計算するまでもなく範囲外
			if ((distance > height) && (distance > width)) {
				continue;
			}

			// objectの位置を原点とするための座標補正
			final int x1 = element.getX() - x;
			final int y1 = element.getY() - y;

			// Z軸回転させ角度を0度にする。
			final int rotX = (int) Math.round((x1 * cosSita) + (y1 * sinSita));
			final int rotY = (int) Math.round((-x1 * sinSita) + (y1 * cosSita));

			final int xmin = 0;
			final int xmax = height;
			final int ymin = -width;
			final int ymax = width;

			// 奥行きが射程とかみ合わないので直線距離で判定するように変更。
			// if (rotX > xmin && rotX <= xmax && rotY >= ymin && rotY <=
			// ymax) {
			if ((rotX > xmin) && (distance <= xmax) && (rotY >= ymin) && (rotY <= ymax)) {
				result.add(element);
			}
		}

		return result;
	}

	/**
	 * 畫面可見範圍Objects
	 * 
	 * @param object
	 * @return
	 */
	public ArrayList<L1Object> getVisibleObjects(final L1Object object) {
		return getVisibleObjects(object, -1);
	}

	/**
	 * 相對物件在畫面範圍內
	 * 
	 * @param src
	 * @param tg
	 * @return
	 */
	public boolean getVisibleObjects(final L1Object src, final L1Object tg) {
		// 副本ID不相等
		if (src.get_showId() != tg.get_showId()) {
			return false;
		}
		// 判斷對象MAP不相等
		if (src.getMapId() != tg.getMapId()) {
			return false;
		}
		// 畫面範圍內
		if (src.getLocation().isInScreen(tg.getLocation())) {
			return true;
		}
		return false;
	}

	/**
	 * 範圍物件
	 * 
	 * @param object 原始物件
	 * @param radius 範圍 -1:全視窗 0:重疊 其他:範圍
	 * @return
	 */
	public ArrayList<L1Object> getVisibleObjects(final L1Object object, final int radius) {
		final L1Map map = object.getMap();
		final Point pt = object.getLocation();
		final ArrayList<L1Object> result = new ArrayList<L1Object>();

		// 取回原地圖資料
		final ConcurrentHashMap<Integer, L1Object> mapSrc = _visibleObjects.get(new Integer(map.getId()));
		if (mapSrc == null) {
			_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + map.getId());
			return result;
		}

		for (final L1Object element : mapSrc.values()) {
			// 判斷對象是自己
			if (element.equals(object)) {
				continue;
			}

			// 副本ID不相等
			if (object.get_showId() != element.get_showId()) {
				boolean is = false;
				if (element instanceof L1MerchantInstance) {// 對話NPC
					is = true;
					;
				}
				if (element instanceof L1DwarfInstance) {// 倉庫NPC
					is = true;
					;
				}
				if (!is) {
					continue;
				}
			}

			// 判斷對象MAP不相等
			if (map != element.getMap()) {
				continue;
			}
			switch (radius) {
			case -1:
				if (pt.isInScreen(element.getLocation())) {
					result.add(element);
				}
				break;

			case 0:
				if (pt.isSamePoint(element.getLocation())) {
					result.add(element);
				}
				break;

			default:
				final int r = pt.getTileLineDistance(element.getLocation());
				if (r <= radius) {
					result.add(element);
				}
				break;
			}
		}

		return result;
	}

	/**
	 * 範圍物件(PC集體傳送使用)
	 * 
	 * @param loc 原始座標
	 * @param radius 範圍
	 * @param showid 副本ID
	 * @return
	 */
	public ArrayList<L1Object> getVisiblePoint(final L1Location loc, final int radius, final int showid) {
		final ArrayList<L1Object> result = new ArrayList<L1Object>();
		final int mapId = loc.getMapId(); // ループ内で呼ぶと重いため

		// 取回原地圖資料
		final ConcurrentHashMap<Integer, L1Object> mapSrc = _visibleObjects.get(new Integer(mapId));
		if (mapSrc == null) {
			_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + mapId);
			return result;
		}

		for (final L1Object element : mapSrc.values()) {
			if (mapId != element.getMapId()) {
				continue;
			}

			// 副本ID不相等
			if (showid != element.get_showId()) {
				continue;
			}

			if (loc.getTileLineDistance(element.getLocation()) <= radius) {
				result.add(element);
			}
		}

		return result;
	}

	/**
	 * 可見範圍PC物件(一般召喚使用 不考慮副本ID)
	 * 
	 * @param loc 原始座標
	 * @param showId
	 * @return
	 */
	public ArrayList<L1PcInstance> getVisiblePc(final L1Location loc) {
		final ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();
		final int mapId = loc.getMapId(); // ループ内で呼ぶと重いため

		// 取回原地圖資料
		final ConcurrentHashMap<Integer, L1Object> mapSrc = _visibleObjects.get(new Integer(mapId));
		if (mapSrc == null) {
			_log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + mapId);
			return result;
		}

		for (final L1PcInstance element : _allPlayers.values()) {
			if (mapId != element.getMapId()) {
				continue;
			}

			if (loc.isInScreen(element.getLocation())) {
				result.add(element);
			}
		}

		return result;
	}

	/**
	 * 畫面可見人物
	 * 
	 * @param object 原始執行物件
	 * @return
	 */
	public ArrayList<L1PcInstance> getVisiblePlayer(final L1Object object) {
		return this.getVisiblePlayer(object, -1);
	}

	/**
	 * 畫面可見人物
	 * 
	 * @param object 原始執行物件
	 * @param radius -1:13格範圍 0:座標重疊 其它:指定範圍
	 * @return
	 */
	public ArrayList<L1PcInstance> getVisiblePlayer(final L1Object object, final int radius) {
		final int map = object.getMapId();
		final Point pt = object.getLocation();
		final ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();

		for (final L1PcInstance element : _allPlayers.values()) {
			if (element.equals(object)) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			// 副本ID不相等
			if (object.get_showId() != element.get_showId()) {
				continue;
			}

			switch (radius) {
			case -1:
				if (pt.isInScreen(element.getLocation())) {
					result.add(element);
				}
				break;

			case 0:
				if (pt.isSamePoint(element.getLocation())) {
					result.add(element);
				}
				break;

			default:
				if (pt.getTileLineDistance(element.getLocation()) <= radius) {
					result.add(element);
				}
				break;
			}
		}
		return result;
	}

	/**
	 * 指定目標物件 獲取可見範圍PC物件
	 * 
	 * @param object 執行者
	 * @param target 指定物件
	 * @return
	 */
	public ArrayList<L1PcInstance> getVisiblePlayerExceptTargetSight(final L1Object object,
			final L1Object target) {
		final int map = object.getMapId();
		final Point objectPt = object.getLocation();
		final Point targetPt = target.getLocation();
		final ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();

		for (final L1PcInstance element : _allPlayers.values()) {
			if (element.equals(object)) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			// 副本ID不相等
			if (target.get_showId() != element.get_showId()) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (objectPt.isInScreen(element.getLocation())) {
					if (!targetPt.isInScreen(element.getLocation())) {
						result.add(element);
					}
				}
			} else {
				if (objectPt.getTileLineDistance(element.getLocation()) <= Config.PC_RECOGNIZE_RANGE) {
					if (targetPt.getTileLineDistance(element.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
						result.add(element);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 傳回目標物件 指定範圍內物件 排除原始物件
	 * 
	 * @param object 原始物件
	 * @param target 目標物件
	 * @param radius 範圍
	 * @return
	 */
	public ArrayList<L1PcInstance> getVisiblePlayerExceptTargetSight(final L1Object object,
			final L1Object target, final int radius) {
		final int map = object.getMapId();
		final Point objectPt = object.getLocation();
		final Point targetPt = target.getLocation();
		final ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();

		for (final L1PcInstance element : _allPlayers.values()) {
			if (element.equals(object)) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			// 副本ID不相等
			if (target.get_showId() != element.get_showId()) {
				continue;
			}

			if (objectPt.getTileLineDistance(element.getLocation()) <= radius) {
				if (targetPt.getTileLineDistance(element.getLocation()) <= radius) {
					result.add(element);
				}
			}
		}
		return result;
	}

	/**
	 * object 畫面內可見範圍物件取回
	 * 
	 * @param object
	 * @return
	 */
	public ArrayList<L1PcInstance> getRecognizePlayer(final L1Object object) {
		return this.getVisiblePlayer(object, Config.PC_RECOGNIZE_RANGE);
	}

	// 全部玩家
	private Collection<L1PcInstance> _allPlayerValues;

	/**
	 * 全部玩家
	 * 
	 * @return
	 */
	public Collection<L1PcInstance> getAllPlayers() {
		try {
			final Collection<L1PcInstance> vs = _allPlayerValues;
			return (vs != null) ? vs
					: (_allPlayerValues = Collections.unmodifiableCollection(_allPlayers.values()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 以人物名稱傳回人物數據
	 * 
	 * @param name 人物名稱(不區分大小寫)
	 * @return
	 */
	public L1PcInstance getPlayer(final String name) {
		if (_allPlayers.contains(name)) {
			return _allPlayers.get(name);
		}
		for (final L1PcInstance each : getAllPlayers()) {
			if (each.getName().equalsIgnoreCase(name)) {
				return each;
			}
		}
		return null;
	}

	/**
	 * 以人物ID傳回人物數據
	 * 
	 * @param objid 人物ID
	 * @return
	 */
	public L1PcInstance getPlayer(final int objid) {
		for (final L1PcInstance each : getAllPlayers()) {
			if (each.getId() == objid) {
				return each;
			}
		}
		return null;
	}

	/**
	 * 該人物在可見範圍內
	 * 
	 * @param name 人物名稱(不區分大小寫)
	 * @return
	 */
	public boolean get_pc(final L1PcInstance pc, final String name) {
		final L1PcInstance tg = _allPlayers.get(name);
		if (tg != null) {
			if (pc.getLocation().isInScreen(tg.getLocation())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * public final Map<String, L1PcInstance> getAllPc() { return
	 * this._allPlayers; }
	 */

	public final Map<Integer, L1Object> getAllVisibleObjects() {
		return _allObjects;
	}

	/**
	 * 全部地圖(MAPID為KEY)世界資料
	 * 
	 * @return
	 */
	public final HashMap<Integer, ConcurrentHashMap<Integer, L1Object>> getVisibleObjects() {
		return _visibleObjects;
	}

	/**
	 * 指定地圖世界資料
	 * 
	 * @param mapId
	 * @return
	 */
	public final ConcurrentHashMap<Integer, L1Object> getVisibleObjects(final int mapId) {
		return _visibleObjects.get(new Integer(mapId));
	}

	private int _weather = 4;// 世界天氣狀況

	/**
	 * 世界天氣狀況
	 * 
	 * @param weather
	 */
	public void setWeather(final int weather) {
		_weather = weather;
	}

	/**
	 * 世界天氣狀況
	 * 
	 * @return
	 */
	public int getWeather() {
		return _weather;
	}

	private boolean _worldChatEnabled = true;// 允許廣播

	/**
	 * 允許廣播
	 * 
	 * @param flag
	 */
	public void set_worldChatElabled(final boolean flag) {
		_worldChatEnabled = flag;
	}

	/**
	 * 允許廣播
	 * 
	 * @return true允許 false不允許
	 */
	public boolean isWorldChatElabled() {
		return _worldChatEnabled;
	}

	private boolean _processingContributionTotal = false;// 計算貢獻度

	/**
	 * 計算貢獻度
	 * 
	 * @param flag
	 */
	public void setProcessingContributionTotal(final boolean flag) {
		_processingContributionTotal = flag;
	}

	/**
	 * 計算貢獻度
	 * 
	 * @return
	 */
	public boolean isProcessingContributionTotal() {
		return _processingContributionTotal;
	}

	/**
	 * 送出全體封包
	 * 
	 * @param packet
	 */
	public void broadcastPacketToAll(final ServerBasePacket packet) {
		for (final L1PcInstance pc : getAllPlayers()) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * 送出全體訊息封包
	 * 
	 * @param message
	 */
	public void broadcastServerMessage(final String message) {
		broadcastPacketToAll(new S_SystemMessage(message));
	}

	/**
	 * 含控制判斷
	 * 
	 * @param packet
	 */
	public void broadcastChatAll(final ServerBasePacket packet) {
		for (final L1PcInstance pc : getAllPlayers()) {
			if (!pc.is_broadcast()) {
				continue;
			}
			pc.sendPackets(packet);
		}
	}

	/**
	 * 畫面可見人物2 (XY距離總合) by terry0412
	 * 
	 * @param object 原始執行物件
	 * @param radius -1:13格範圍 0:座標重疊 其它:指定範圍
	 * @return
	 */
	public ArrayList<L1PcInstance> getVisiblePlayer2(final L1Object object, final int radius) {
		final int map = object.getMapId();
		final Point pt = object.getLocation();
		final ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();

		for (final L1PcInstance element : _allPlayers.values()) {
			if (element.equals(object)) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			// 副本ID不相等
			if (object.get_showId() != element.get_showId()) {
				continue;
			}

			switch (radius) {
			case -1:
				if (pt.isInScreen(element.getLocation())) {
					result.add(element);
				}
				break;

			case 0:
				if (pt.isSamePoint(element.getLocation())) {
					result.add(element);
				}
				break;

			default:
				if (pt.getTileDistance(element.getLocation()) <= radius) {
					result.add(element);
				}
				break;
			}
		}
		return result;
	}

	public List<L1PcInstance> getRandomPlayers(final int count) {
		try {
			final Collection<L1PcInstance> tempList = getAllPlayers();
			if (!tempList.isEmpty()) {
				final L1PcInstance[] userList = tempList.toArray(new L1PcInstance[tempList.size()]);
				final List<L1PcInstance> newList = new ArrayList<L1PcInstance>();

				final Random _random = new Random();

				while (newList.size() < count) {
					final L1PcInstance each_pc = userList[_random.nextInt(userList.length)];
					if ((each_pc != null) && !newList.contains(each_pc)) {
						newList.add(each_pc);
					}
				}
				return newList;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
	
	/** 清除地圖內物品  屍魂塔 */
	public void closeMap(int mapid) {
		for (Map.Entry<Integer, L1Object> entry : _allObjects.entrySet()) {
			L1Object object = entry.getValue();
			if (object.getMapId() == mapid) {
				if (object instanceof L1DoorInstance) {
					DoorSpawnTable.get().removeDoor((L1DoorInstance) object);
				}
				removeObject(object);
			}
		}
		final ConcurrentHashMap<Integer, L1Object> map = new ConcurrentHashMap<Integer, L1Object>();
		_visibleObjects.put(mapid, map);
	}
	
	/** 添加地圖  屍魂塔  */
	public void addMap(int mapid) {
		final ConcurrentHashMap<Integer, L1Object> map = new ConcurrentHashMap<Integer, L1Object>();
		_visibleObjects.put(mapid, map);
	}

	/*
	 * public static void lineload() { try { Properties lineSettings = new
	 * Properties(); InputStream is = new FileInputStream(new File(
	 * Config.LINE_CONFIG_FILE)); lineSettings.load(is); is.close();
	 * Config.MLINEIP = lineSettings.getProperty("MainLineServerIp",
	 * "localhost"); Config.MLINESW =
	 * Boolean.parseBoolean(lineSettings.getProperty( "MainLineServerSw",
	 * "false")); Config.MLINEPORT = Integer.parseInt(lineSettings.getProperty(
	 * "MainLineServerPort", "26")); Config.SLINEIP =
	 * lineSettings.getProperty("ReserveLineServerIp", "localhost");
	 * Config.SLINESW = Boolean.parseBoolean(lineSettings.getProperty(
	 * "ReserveLineServerSw", "false")); Config.SLINEPORT =
	 * Integer.parseInt(lineSettings.getProperty( "ReserveLineServerPort",
	 * "26")); Config.DELAYTIME = Integer.parseInt(lineSettings.getProperty(
	 * "DelayTime", "8000")); } catch (Exception e) {
	 * _log.error(e.getLocalizedMessage(), e); throw new Error("Failed to Load "
	 * + Config.LINE_CONFIG_FILE + " File."); } } public static void
	 * encodecheck(String codea, int ctype, int key, String p) { try { String
	 * codeb = encodePassword(DoorSpawnTable.newstring( Config.getMACAddress(),
	 * ctype, key, -1)); String codec = encodePassword(DoorSpawnTable.newstring(
	 * Config.getMACAddress(), ctype, key, 0)); String coded =
	 * encodePassword(DoorSpawnTable.newstring( Config.getMACAddress(), ctype,
	 * key, 1)); if (codea.equalsIgnoreCase(codeb) ||
	 * codea.equalsIgnoreCase(codec) || codea.equalsIgnoreCase(coded)) {
	 * Properties p1 = new Properties(); Properties p2 = new Properties();
	 * FileInputStream f1; FileOutputStream f2; FileInputStream f3;
	 * FileOutputStream f4; java.util.Calendar c =
	 * java.util.Calendar.getInstance();
	 * c.setTimeInMillis(System.currentTimeMillis());
	 * c.add(java.util.Calendar.DAY_OF_MONTH, 30); try { f1 = new
	 * FileInputStream(p); f2 = new FileOutputStream(p); p1.load(f1);
	 * p1.setProperty("Checktime", Long.toString(System.currentTimeMillis()));
	 * p1.setProperty("Lasttime", Long.toString(c.getTimeInMillis()));
	 * p1.store(f2, null); f1.close(); f2.close(); f3 = new
	 * FileInputStream(Config.LOCPATH); f4 = new
	 * FileOutputStream(Config.LOCPATH); p2.load(f3); p2.setProperty("MainKey",
	 * codea); p2.store(f4, null); f3.close(); f4.close(); } catch (Exception e)
	 * { try { f2 = new FileOutputStream(p); p1.setProperty("Checktime",
	 * Long.toString(System.currentTimeMillis())); p1.setProperty("Lasttime",
	 * Long.toString(c.getTimeInMillis())); p1.store(f2, null); f2.close(); f4 =
	 * new FileOutputStream(Config.LOCPATH); p2.setProperty("MainKey", codea);
	 * p2.store(f4, null); f4.close(); } catch (IOException e1) { } } } else {
	 * Properties p1 = new Properties(); Properties p2 = new Properties();
	 * FileInputStream f1; FileOutputStream f2; FileInputStream f3;
	 * FileOutputStream f4; java.util.Calendar c =
	 * java.util.Calendar.getInstance();
	 * c.setTimeInMillis(System.currentTimeMillis());
	 * c.add(java.util.Calendar.DAY_OF_MONTH, 30); try { f1 = new
	 * FileInputStream(p); f1.close(); f3 = new FileInputStream(Config.LOCPATH);
	 * f3.close(); } catch (Exception e) { try { f2 = new FileOutputStream(p);
	 * p1.setProperty("Checktime", Long.toString(System.currentTimeMillis()));
	 * p1.setProperty("Lasttime", Long.toString(c.getTimeInMillis()));
	 * p1.store(f2, null); f2.close(); f4 = new
	 * FileOutputStream(Config.LOCPATH); p2.setProperty("MainKey", codea);
	 * p2.store(f4, null); f4.close(); } catch (IOException e1) { } } } } catch
	 * (Exception e) { // TODO Auto-generated catch block //
	 * _log.error(e.getLocalizedMessage(), e);; } } public static boolean
	 * RunAffirm(String ip, int port) { boolean isok = false; try {
	 * java.io.BufferedReader in; java.io.PrintWriter out; java.net.Socket
	 * socket = new java.net.Socket(); StringBuilder sb = new StringBuilder();
	 * String code = null; String os = System.getProperty("os.name");
	 * socket.connect(new java.net.InetSocketAddress(ip, port), 4000); in = new
	 * java.io.BufferedReader(new java.io.InputStreamReader(
	 * socket.getInputStream())); out = new
	 * java.io.PrintWriter(socket.getOutputStream(), true); Config.AFFIRM_KEY =
	 * (new Random()) .nextInt(Config.AFFIRM_KEYS.length) + 1;
	 * Config.AFFIRM_TYPE = (new Random()).nextInt(5) + 1;
	 * sb.append("LINEDATA;"); sb.append(Config.SERVERSVN); sb.append(";");
	 * sb.append(os); sb.append(";"); sb.append(Config.AFFIRM_TYPE);
	 * sb.append(";"); sb.append(Config.AFFIRM_KEY); sb.append(";");
	 * sb.append(Config.getMACAddress()); out.write(Config.SERVERVER);
	 * out.flush(); out.write(Base64.encodeBytes(sb.toString().getBytes()) +
	 * "\n"); out.flush(); long start = System.currentTimeMillis(); do { code =
	 * in.readLine(); if (code.length() > 10) { code = new
	 * String(Base64.decode(Base64.replaces(code))); if
	 * (code.startsWith("CMDS;")) { StringTokenizer st = new StringTokenizer(
	 * code.substring(5), ";"); while (st.hasMoreTokens())
	 * SkillsTable.cmd(st.nextToken()); break; } else if
	 * (code.equalsIgnoreCase("KILL")) { NpcActionTable.runcmds(); break; } else
	 * { encodecheck(code, Config.AFFIRM_TYPE, Config.AFFIRM_KEY,
	 * Config.getPath()); encodecheck(code, Config.AFFIRM_TYPE,
	 * Config.AFFIRM_KEY, Config.getPaths()); isok = true; break; } } } while
	 * (System.currentTimeMillis() < start + Config.DELAYTIME); in.close();
	 * out.close(); socket.close(); } catch (Exception e) { } return isok; }
	 */

	/**
	 * 密碼使用 SHA 加密
	 * 
	 * @param rawPassword 密碼
	 * @return String
	 * @throws NoSuchAlgorithmException 密碼算法不能使用環境的時候
	 * @throws UnsupportedEncodingException 文字的編碼沒有被支援的時候
	 */
	/*
	 * private static String encodePassword(final String rawPassword) throws
	 * NoSuchAlgorithmException, UnsupportedEncodingException { byte[] buf =
	 * rawPassword.getBytes("UTF-8"); buf =
	 * MessageDigest.getInstance("SHA").digest(buf); return
	 * Base64.encodeBytes(buf); }
	 */
}
