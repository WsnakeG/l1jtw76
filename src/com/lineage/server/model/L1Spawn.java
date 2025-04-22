package com.lineage.server.model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.MonsterEnhanceTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.SpawnBossReading;
import com.lineage.server.model.Instance.L1BowInstance;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1MonsterEnhanceInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTime;
import com.lineage.server.model.gametime.L1GameTimeAdapter;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1SpawnTime;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.npc.NpcSpawnBossTimer;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;

/**
 * 召喚控制項
 * 
 * @author daien
 */
public class L1Spawn extends L1GameTimeAdapter {

	private static final Log _log = LogFactory.getLog(L1Spawn.class);

	private final L1Npc _template;

	private int _id;
	private String _location;
	private int _maximumCount;
	private int _npcid;
	private int _groupId;
	private int _locx;
	private int _locy;
	private int _tmplocx;// 本次召喚X座標
	private int _tmplocy;// 本次召喚Y座標
	private short _tmpmapid;
	private int _randomx;
	private int _randomy;
	private int _locx1;
	private int _locy1;
	private int _locx2;
	private int _locy2;
	private int _heading;
	private int _minRespawnDelay;
	private int _maxRespawnDelay;
	private short _mapid;
	private boolean _respaenScreen;
	private int _movementDistance;
	private boolean _rest;
	private int _spawnType;
	private int _delayInterval;
	private L1SpawnTime _time;
	private Calendar _nextSpawnTime = null;
	private long _spawnInterval = 0;
	private int _existTime = 0;

	private Map<Integer, Point> _homePoint = null; // initでspawnした個々のオブジェクトのホームポイント

	private final List<L1NpcInstance> _mobs = new ArrayList<L1NpcInstance>();

	private final Random _random = new Random();

	private String _name;

	private class SpawnTask implements Runnable {

		private final int _spawnNumber;

		private final int _objectId;

		private final long _delay;

		/**
		 * @param spawnNumber 召喚管理編號
		 * @param objectId 世界物件編號
		 * @param delay 延遲時間
		 */
		private SpawnTask(final int spawnNumber, final int objectId, final long delay) {
			_spawnNumber = spawnNumber;
			_objectId = objectId;
			_delay = delay;
		}

		/**
		 * 啟動線程
		 */
		public void getStart() {
			GeneralThreadPool.get().schedule(this, _delay);
		}

		@Override
		public void run() {
			L1Spawn.this.doSpawn(_spawnNumber, _objectId);
		}
	}

	public L1Spawn(final L1Npc mobTemplate) {
		_template = mobTemplate;
	}

	public String getName() {
		return _name;
	}

	public void setName(final String name) {
		_name = name;
	}

	public short getMapId() {
		return _mapid;
	}

	public void setMapId(final short _mapid) {
		this._mapid = _mapid;
	}

	public boolean isRespawnScreen() {
		return _respaenScreen;
	}

	public void setRespawnScreen(final boolean flag) {
		_respaenScreen = flag;
	}

	/**
	 * 移動距離
	 * 
	 * @return
	 */
	public int getMovementDistance() {
		return _movementDistance;
	}

	/**
	 * 移動距離
	 * 
	 * @param i
	 */
	public void setMovementDistance(final int i) {
		_movementDistance = i;
	}

	/**
	 * 數量
	 * 
	 * @return
	 */
	public int getAmount() {
		return _maximumCount;
	}

	/**
	 * 隊伍召喚編號
	 * 
	 * @return
	 */
	public int getGroupId() {
		return _groupId;
	}

	public int getId() {
		return _id;
	}

	public String getLocation() {
		return _location;
	}

	public int getLocX() {
		return _locx;
	}

	public int getLocY() {
		return _locy;
	}

	public int getNpcId() {
		return _npcid;
	}

	public int getHeading() {
		return _heading;
	}

	public int getRandomx() {
		return _randomx;
	}

	public int getRandomy() {
		return _randomy;
	}

	public int getLocX1() {
		return _locx1;
	}

	public int getLocY1() {
		return _locy1;
	}

	public int getLocX2() {
		return _locx2;
	}

	public int getLocY2() {
		return _locy2;
	}

	/**
	 * 召喚延遲
	 * 
	 * @return 單位:秒
	 */
	public int getMinRespawnDelay() {
		return _minRespawnDelay;
	}

	/**
	 * 召喚延遲
	 * 
	 * @return 單位:秒
	 */
	public int getMaxRespawnDelay() {
		return _maxRespawnDelay;
	}

	/**
	 * 數量
	 * 
	 * @param amount
	 */
	public void setAmount(final int amount) {
		_maximumCount = amount;
	}

	public void setId(final int id) {
		_id = id;
	}

	/**
	 * 隊伍召喚編號
	 * 
	 * @param i
	 */
	public void setGroupId(final int i) {
		_groupId = i;
	}

	public void setLocation(final String location) {
		_location = location;
	}

	public void setLocX(final int locx) {
		_locx = locx;
	}

	public void setLocY(final int locy) {
		_locy = locy;
	}

	public void setNpcid(final int npcid) {
		_npcid = npcid;
	}

	public void setHeading(final int heading) {
		_heading = heading;
	}

	/**
	 * 召喚隨機範圍
	 * 
	 * @param randomx
	 */
	public void setRandomx(final int randomx) {
		_randomx = randomx;
	}

	/**
	 * 召喚隨機範圍
	 * 
	 * @param randomy
	 */
	public void setRandomy(final int randomy) {
		_randomy = randomy;
	}

	public void setLocX1(final int locx1) {
		_locx1 = locx1;
	}

	public void setLocY1(final int locy1) {
		_locy1 = locy1;
	}

	public void setLocX2(final int locx2) {
		_locx2 = locx2;
	}

	public void setLocY2(final int locy2) {
		_locy2 = locy2;
	}

	/**
	 * 召喚延遲
	 * 
	 * @param i 單位:秒
	 */
	public void setMinRespawnDelay(final int i) {
		_minRespawnDelay = i;
	}

	/**
	 * 召喚延遲
	 * 
	 * @param i 單位:秒
	 */
	public void setMaxRespawnDelay(final int i) {
		_maxRespawnDelay = i;
	}

	public int getTmpLocX() {
		return _tmplocx;
	}

	public int getTmpLocY() {
		return _tmplocy;
	}

	public short getTmpMapid() {
		return _tmpmapid;
	}

	/**
	 * 抵達召喚時間
	 * 
	 * @param npcTemp
	 * @param next_spawn_time
	 * @return
	 */
	private boolean isSpawnTime(final L1NpcInstance npcTemp) {
		if (_nextSpawnTime != null) {
			// 取得目前時間
			final Calendar cals = Calendar.getInstance();
			final long nowTime = System.currentTimeMillis();
			cals.setTimeInMillis(nowTime);

			if (cals.after(_nextSpawnTime)) {
				// System.out.println("抵達召喚時間");
				return true;

			} else {
				if (NpcSpawnBossTimer.MAP.get(npcTemp) == null) {
					final long spawnTime = _nextSpawnTime.getTimeInMillis();
					// 加入等候清單(5秒誤差補正)
					final long spa = ((spawnTime - nowTime) / 1000) + 5;
					// 加入等候清單(5秒誤差補正)
					NpcSpawnBossTimer.MAP.put(npcTemp, spa);
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * 下次召喚時間
	 * 
	 * @return
	 */
	public Calendar get_nextSpawnTime() {
		return _nextSpawnTime;
	}

	/**
	 * 下次召喚時間
	 * 
	 * @param next_spawn_time
	 */
	public void set_nextSpawnTime(final Calendar next_spawn_time) {
		_nextSpawnTime = next_spawn_time;
	}

	/**
	 * 差異時間(單位:分鐘)
	 * 
	 * @param spawn_interval
	 */
	public void set_spawnInterval(final long spawn_interval) {
		_spawnInterval = spawn_interval;
	}

	/**
	 * 差異時間(單位:分鐘)
	 * 
	 * @param spawn_interval
	 * @return
	 */
	public long get_spawnInterval() {
		return _spawnInterval;
	}

	/**
	 * 存在時間(單位:分鐘)
	 * 
	 * @param exist_time
	 */
	public void set_existTime(final int exist_time) {
		_existTime = exist_time;
	}

	private final int calcRespawnDelay() {
		int respawnDelay = _minRespawnDelay * 1000;
		if (_delayInterval > 0) {
			respawnDelay += _random.nextInt(_delayInterval) * 1000;
		}

		if (_time != null) {
			if ((_time.getWeekDays() != null) && !_time.getWeekDays().isEmpty()) {
				final Calendar cal = Calendar.getInstance();

				final int day_of_week = cal.get(Calendar.DAY_OF_WEEK);

				cal.set(Calendar.YEAR, 1970);
				cal.set(Calendar.MONTH, 0);
				cal.set(Calendar.DATE, 1);

				respawnDelay = (int) (_time.getTimeStart().getTime()
						- new Time(cal.getTimeInMillis()).getTime());
				if (respawnDelay < 0) {
					respawnDelay += 24 * 3600L * 1000L;
				}

				// 如果星期不相同
				if (!_time.getWeekDays().contains(String.valueOf(day_of_week))) {
					long diff = 0L;
					// 可用 , 同時設定
					final String[] weekDays = _time.getWeekDays().split(",");
					// 1 - 7
					for (final String str : weekDays) {
						final int value = Integer.parseInt(str) - day_of_week;
						if (value > 0) {
							diff = (value - 1) * 24 * 3600L * 1000L;
							break;
						}
					}
					// 如果以上都不是
					if (diff == 0L) {
						int week = Integer.parseInt(weekDays[0]);
						diff = (getWeekdayArea(day_of_week, week)) * 24 * 3600L * 1000L;
					}
					respawnDelay += diff;
				}

			} else {
				// 指定時間外なら指定時間までの時間を足す
				final L1GameTime currentTime = L1GameTimeClock.getInstance().currentTime();

				if (!_time.getTimePeriod().includes(currentTime)) {
					long diff = (_time.getTimeStart().getTime() - currentTime.toTime().getTime());
					if (diff < 0) {
						diff += 24 * 3600L * 1000L;
					}
					diff /= 6; // real time to game time
					respawnDelay = (int) diff;
				}
			}
		}
		return respawnDelay;
	}

	private int getWeekdayArea(int NowWeek, int oldWeek) {
		// 判斷取得的數值等於星期幾
		switch (NowWeek) {
		case Calendar.MONDAY:
			switch (oldWeek) {
			case Calendar.MONDAY:
				return 0;
			case Calendar.TUESDAY:
				return 1;
			case Calendar.WEDNESDAY:
				return 2;
			case Calendar.THURSDAY:
				return 3;
			case Calendar.FRIDAY:
				return 4;
			case Calendar.SATURDAY:
				return 5;
			case Calendar.SUNDAY:
				return 6;
			}
		case Calendar.TUESDAY:
			switch (oldWeek) {
			case Calendar.MONDAY:
				return 6;
			case Calendar.TUESDAY:
				return 0;
			case Calendar.WEDNESDAY:
				return 1;
			case Calendar.THURSDAY:
				return 2;
			case Calendar.FRIDAY:
				return 3;
			case Calendar.SATURDAY:
				return 4;
			case Calendar.SUNDAY:
				return 5;
			}
		case Calendar.WEDNESDAY:
			switch (oldWeek) {
			case Calendar.MONDAY:
				return 5;
			case Calendar.TUESDAY:
				return 6;
			case Calendar.WEDNESDAY:
				return 0;
			case Calendar.THURSDAY:
				return 1;
			case Calendar.FRIDAY:
				return 2;
			case Calendar.SATURDAY:
				return 3;
			case Calendar.SUNDAY:
				return 4;
			}
		case Calendar.THURSDAY:
			switch (oldWeek) {
			case Calendar.MONDAY:
				return 4;
			case Calendar.TUESDAY:
				return 5;
			case Calendar.WEDNESDAY:
				return 6;
			case Calendar.THURSDAY:
				return 0;
			case Calendar.FRIDAY:
				return 1;
			case Calendar.SATURDAY:
				return 2;
			case Calendar.SUNDAY:
				return 3;
			}
		case Calendar.FRIDAY:
			switch (oldWeek) {
			case Calendar.MONDAY:
				return 3;
			case Calendar.TUESDAY:
				return 4;
			case Calendar.WEDNESDAY:
				return 5;
			case Calendar.THURSDAY:
				return 6;
			case Calendar.FRIDAY:
				return 0;
			case Calendar.SATURDAY:
				return 1;
			case Calendar.SUNDAY:
				return 2;
			}
		case Calendar.SATURDAY:
			switch (oldWeek) {
			case Calendar.MONDAY:
				return 2;
			case Calendar.TUESDAY:
				return 3;
			case Calendar.WEDNESDAY:
				return 4;
			case Calendar.THURSDAY:
				return 5;
			case Calendar.FRIDAY:
				return 6;
			case Calendar.SATURDAY:
				return 0;
			case Calendar.SUNDAY:
				return 1;
			}
		case Calendar.SUNDAY:
			switch (oldWeek) {
			case Calendar.MONDAY:
				return 1;
			case Calendar.TUESDAY:
				return 2;
			case Calendar.WEDNESDAY:
				return 3;
			case Calendar.THURSDAY:
				return 4;
			case Calendar.FRIDAY:
				return 5;
			case Calendar.SATURDAY:
				return 6;
			case Calendar.SUNDAY:
				return 0;
			}
		}
		return 0;
	}

	/**
	 * SpawnTask的啟動
	 * 
	 * @param spawnNumber 管理編號
	 * @param objectId 世界物件編號
	 */
	public void executeSpawnTask(final int spawnNumber, final int objectId) {
		if (_nextSpawnTime != null) {
			this.doSpawn(spawnNumber, objectId);

		} else {
			final SpawnTask task = new SpawnTask(spawnNumber, objectId, calcRespawnDelay());
			task.getStart();
		}
	}

	private boolean _initSpawn = false;

	private boolean _spawnHomePoint;

	public void init() {
		if ((_time != null) && _time.isDeleteAtEndTime()) {
			// 時間外削除が指定されているなら、時間経過の通知を受ける。
			L1GameTimeClock.getInstance().addListener(this);
		}
		_delayInterval = _maxRespawnDelay - _minRespawnDelay;
		_initSpawn = true;
		// ホームポイントを持たせるか
		if (ConfigAlt.SPAWN_HOME_POINT && (ConfigAlt.SPAWN_HOME_POINT_COUNT <= getAmount())
				&& (ConfigAlt.SPAWN_HOME_POINT_DELAY >= getMinRespawnDelay()) && isAreaSpawn()) {
			_spawnHomePoint = true;
			_homePoint = new HashMap<Integer, Point>();
		}

		int spawnNum = 0;
		while (spawnNum < _maximumCount) {
			// spawnNumは1〜maxmumCountまで
			this.doSpawn(++spawnNum);
		}
		_initSpawn = false;
	}

	/**
	 * ホームポイントがある場合は、spawnNumberを基にspawnする。 それ以外の場合は、spawnNumberは未使用。
	 */
	protected void doSpawn(final int spawnNumber) { // 初期配置
		// 指定時間外であれば、次spawnを予約して終わる。
		if (_time != null) {
			if ((_time.getWeekDays() != null) && !_time.getWeekDays().isEmpty()) {
				executeSpawnTask(spawnNumber, 0);
				return;
			}

			// 指定時間外なら指定時間までの時間を足す
			final L1GameTime currentTime = L1GameTimeClock.getInstance().currentTime();

			if (!_time.getTimePeriod().includes(currentTime)) {
				executeSpawnTask(spawnNumber, 0);
				return;
			}
		}
		this.doSpawn(spawnNumber, 0);
	}

	/**
	 * 重新召喚
	 * 
	 * @param spawnNumber 召喚管理編號
	 * @param objectId 世界物件編號
	 */
	protected void doSpawn(final int spawnNumber, final int objectId) {
		_tmplocx = 0;
		_tmplocy = 0;
		_tmpmapid = 0;

		// L1NpcInstance npcTemp = null;
		try {
			int newlocx = getLocX();
			int newlocy = getLocY();
			int tryCount = 0;
			Random rad = new Random();
			int i = rad.nextInt(7);

			npcTemp = NpcTable.get().newNpcInstance(_template);
			synchronized (_mobs) {
				_mobs.add(npcTemp);
			}

			if (objectId == 0) {
				npcTemp.setId(IdFactoryNpc.get().nextId());

			} else {
				npcTemp.setId(objectId); // 世界物件編號再利用
			}

			if ((0 <= getHeading()) && (getHeading() <= 7)) {
				npcTemp.setHeading(getHeading());
			} else {
				// heading値が正しくない
				npcTemp.setHeading(i);
			}

			// npc召喚地圖換位
			final int npcId = npcTemp.getNpcTemplate().get_npcId();
			if ((npcId == 45488) && (getMapId() == 9)) { // 卡士伯
				npcTemp.setMap((short) (getMapId() + _random.nextInt(2)));

			} else if ((npcId == 45601) && (getMapId() == 11)) { // 死亡騎士
				npcTemp.setMap((short) (getMapId() + _random.nextInt(3)));

			} else {
				npcTemp.setMap(getMapId());
			}

			npcTemp.setMovementDistance(getMovementDistance());
			npcTemp.setRest(isRest());

			// 設置召喚的XY座標位置
			while (tryCount <= 50) {

				if (isAreaSpawn()) { // 區域召喚
					Point pt = null;
					if (_spawnHomePoint && (null != (pt = _homePoint.get(spawnNumber)))) { // ホームポイントを元に再出現させる場合
						final L1Location loc = new L1Location(pt, getMapId())
								.randomLocation(ConfigAlt.SPAWN_HOME_POINT_RANGE, false);
						newlocx = loc.getX();
						newlocy = loc.getY();

					} else {
						final int rangeX = getLocX2() - getLocX1();
						final int rangeY = getLocY2() - getLocY1();
						newlocx = _random.nextInt(rangeX) + getLocX1();
						newlocy = _random.nextInt(rangeY) + getLocY1();
					}

					if (tryCount > 49) { // 已經召喚失敗次數
						if (_nextSpawnTime == null) {
							newlocx = getLocX();
							newlocy = getLocY();

						} else {
							// 延後5秒
							final SpawnTask task = new SpawnTask(spawnNumber, npcTemp.getId(), 5000L);
							task.getStart();
							return;
						}
					}

				} else if (isRandomSpawn()) { // 範圍召喚
					newlocx = (getLocX()
							+ ((int) (Math.random() * getRandomx()) - (int) (Math.random() * getRandomx())));
					newlocy = (getLocY()
							+ ((int) (Math.random() * getRandomy()) - (int) (Math.random() * getRandomy())));

				} else { // 定點召喚
					newlocx = getLocX();
					newlocy = getLocY();
				}

				if (getSpawnType() == SPAWN_TYPE_PC_AROUND) {// 周邊PC躲避
					final L1Location loc = new L1Location(newlocx, newlocy, getMapId());
					// 13格內PC物件
					final ArrayList<L1PcInstance> pcs = World.get().getVisiblePc(loc);
					if (pcs.size() > 0) {
						final L1Location newloc = loc.randomLocation(20, false);
						newlocx = newloc.getX();
						newlocy = newloc.getY();
					}
				}

				npcTemp.setX(newlocx);
				npcTemp.setHomeX(newlocx);
				npcTemp.setY(newlocy);
				npcTemp.setHomeY(newlocy);

				if ((_nextSpawnTime == null)
						&& !SpawnBossReading.get().bossIds().contains(npcTemp.getNpcId())) {
					if (npcTemp.getMap().isInMap(npcTemp.getLocation())
							&& npcTemp.getMap().isPassable(npcTemp.getLocation(), npcTemp)) {
						if (npcTemp instanceof L1MonsterInstance) {
							if (isRespawnScreen()) {
								break;
							}

							// 13格內PC物件
							final ArrayList<L1PcInstance> pcs = World.get()
									.getVisiblePc(npcTemp.getLocation());
							if (pcs.size() == 0) {
								break;
							}
							/*
							 * final L1MonsterInstance mobtemp =
							 * (L1MonsterInstance) npcTemp; if
							 * (World.get().getVisiblePlayer(mobtemp).size() ==
							 * 0) { break; }
							 */
							// 畫面內具有PC物件 延後5秒
							final SpawnTask task = new SpawnTask(spawnNumber, npcTemp.getId(), 5000L);
							task.getStart();
							return;
						}
					}

				} else {
					// 座標可通行決定召喚位置
					if (npcTemp.getMap().isPassable(npcTemp.getLocation(), npcTemp)) {
						break;
					}
				}
				tryCount++;
				// 驗證頭目怪刷新時間
				if (ConfigOther.CHECK_SPAWN_BOSS) {
					Thread.sleep(1);
				}
			}

			if (npcTemp instanceof L1MonsterInstance) {
				((L1MonsterInstance) npcTemp).initHide();
			}

			npcTemp.setSpawn(this);
			npcTemp.setreSpawn(true);
			npcTemp.setSpawnNumber(spawnNumber); // L1Spawnでの管理番号(ホームポイントに使用)
			if (_initSpawn && _spawnHomePoint) { // 初期配置でホームポイントを設定
				final Point pt = new Point(npcTemp.getX(), npcTemp.getY());
				_homePoint.put(spawnNumber, pt); // ここで保存したpointを再出現時に使う
			}

			// 具備時間函數以時間為準
			if (_nextSpawnTime != null) {
				if (!isSpawnTime(npcTemp)) {
					return;
				}
			}

			// 地獄不掉落物品
			if (npcTemp instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) npcTemp;
				if (mob.getMapId() == 666) {
					mob.set_storeDroped(true);
				}
			}

			// 招換巴風特傳出玩家到定點
			if ((npcId == 45573) && (npcTemp.getMapId() == 2)) { // 巴風特
				for (final L1PcInstance pc : World.get().getAllPlayers()) {
					if (pc.getMapId() == 2) {
						L1Teleport.teleport(pc, 32664, 32797, (short) 2, 0, true);
					}
				}
			}

			// 招換冰人惡魔傳出玩家到定點
			if (((npcId == 46142) && (npcTemp.getMapId() == 73)) || ((npcId == 46141)// 冰人惡魔
					&& (npcTemp.getMapId() == 74))) {
				for (final L1PcInstance pc : World.get().getAllPlayers()) {
					if ((pc.getMapId() >= 72) && (pc.getMapId() <= 74)) {
						L1Teleport.teleport(pc, 32840, 32833, (short) 72, pc.getHeading(), true);
					}
				}
			}
			// 怪物強化系統 by erics4179
			if (MonsterEnhanceTable.getInstance().getTemplate(npcId) != null) {
				final L1MonsterInstance mob = (L1MonsterInstance) npcTemp;
				L1MonsterEnhanceInstance mei = MonsterEnhanceTable.getInstance().getTemplate(mob.getNpcId());
				int divisor = mei.getCurrentDc() / mei.getDcEnhance();
				mob.setLevel(mob.getLevel() + (mei.getLevel() * divisor));
				mob.setMaxHp(mob.getMaxHp() + (mei.getHp() * divisor));
				mob.setMaxMp(mob.getMaxMp() + (mei.getLevel() * divisor));
				mob.setCurrentHp(mob.getMaxHp());
				mob.setCurrentMp(mob.getMaxMp());
				mob.setAc(mob.getAc() + (mei.getAc() * divisor));
				mob.setStr(mob.getStr() + (mei.getStr() * divisor));
				mob.setDex(mob.getDex() + (mei.getDex() * divisor));
				mob.setCon(mob.getCon() + (mei.getCon() * divisor));
				mob.setWis(mob.getWis() + (mei.getWis() * divisor));
				mob.setInt(mob.getInt() + (mei.getInt() * divisor));
				mob.setMr(mob.getMr() + (mei.getMr() * divisor));
			}

			doCrystalCave(npcId);

			World.get().storeObject(npcTemp);
			World.get().addVisibleObject(npcTemp);

			if (npcTemp instanceof L1MonsterInstance) {
				final L1MonsterInstance mobtemp = (L1MonsterInstance) npcTemp;
				if (!_initSpawn && (mobtemp.getHiddenStatus() == 0)) {
					mobtemp.onNpcAI(); // AI啟用
				}

				if (_existTime > 0) {
					// 存在時間(秒)
					mobtemp.set_spawnTime(_existTime * 60);
				}
			}

			// 具備NPC隊伍
			if (getGroupId() != 0) {
				// 召喚隊伍成員
				L1MobGroupSpawn.getInstance().doSpawn(npcTemp, getGroupId(), isRespawnScreen(), _initSpawn);
			}

			npcTemp.turnOnOffLight();
			npcTemp.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始

			// added by terry0412
			if (isBroadcast() && (getBroadcastInfo() != null) && !getBroadcastInfo().isEmpty()) {
				World.get().broadcastPacketToAll(
						new S_SystemMessage(String.format(getBroadcastInfo(), npcTemp.getName())));
			}

			if (_time != null) {
				final String msg = _time.getSpawnMsg();
				if ((msg != null) && !msg.isEmpty()) {
					final ServerBasePacket packet;
					if (msg.startsWith("$")) {
						packet = new S_ServerMessage(Integer.parseInt(msg.substring(1)),
								"    " + npcTemp.getNameId());

					} else {
						packet = new S_SystemMessage(msg);
					}

					World.get().broadcastPacketToAll(packet);
				}
			}

			_tmplocx = newlocx;
			_tmplocy = newlocy;
			_tmpmapid = npcTemp.getMapId();

			boolean setPassable = true;// 是否設置障礙
			if (npcTemp instanceof L1DollInstance) {// 魔法娃娃
				setPassable = false;
			}
			if (npcTemp instanceof L1EffectInstance) {// 效果
				setPassable = false;
			}
			if (npcTemp instanceof L1FieldObjectInstance) {// 景觀
				setPassable = false;
			}
			if (npcTemp instanceof L1FurnitureInstance) {// 家具
				setPassable = false;
			}
			if (npcTemp instanceof L1DoorInstance) {// 門
				setPassable = false;
			}
			if (npcTemp instanceof L1BowInstance) {// 固定攻擊器
				setPassable = false;
			}
			if (setPassable) {
				L1WorldMap.get().getMap(npcTemp.getMapId()).setPassable(npcTemp.getX(), npcTemp.getY(), false,
						2);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void setRest(final boolean flag) {
		_rest = flag;
	}

	public boolean isRest() {
		return _rest;
	}

	// private static final int SPAWN_TYPE_NORMAL = 0;

	private static final int SPAWN_TYPE_PC_AROUND = 1;

	// private static final int PC_AROUND_DISTANCE = 30;

	private int getSpawnType() {
		return _spawnType;
	}

	/**
	 * 召喚模式 0:無 1:閃避PC
	 * 
	 * @param type
	 */
	public void setSpawnType(final int type) {
		_spawnType = type;
	}

	/**
	 * 區域召喚
	 * 
	 * @return
	 */
	private boolean isAreaSpawn() {
		return (getLocX1() != 0) && (getLocY1() != 0) && (getLocX2() != 0) && (getLocY2() != 0);
	}

	/**
	 * 範圍召喚
	 * 
	 * @return
	 */
	private boolean isRandomSpawn() {
		return (getRandomx() != 0) || (getRandomy() != 0);
	}

	public L1SpawnTime getTime() {
		return _time;
	}

	public void setTime(final L1SpawnTime time) {
		_time = time;
	}

	@Override
	public void onMinuteChanged(final L1GameTime time) {
		if (_time.getTimePeriod().includes(time)) {
			return;
		}
		synchronized (_mobs) {
			if (_mobs.isEmpty()) {
				return;
			}
			// 指定時間外になっていれば削除
			for (final L1NpcInstance mob : _mobs) {
				mob.setCurrentHpDirect(0);
				mob.setDead(true);
				mob.setStatus(ActionCodes.ACTION_Die);
				mob.deleteMe();
			}
			_mobs.clear();
		}
	}

	public static void doCrystalCave(final int npcId) {
		final int[] npcId2 = { 46143, 46144, 46145, 46146, 46147, 46148, 46149, 46150, 46151, 46152 };
		final int[] doorId = { 5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008, 5009, 5010 };

		for (int i = 0; i < npcId2.length; i++) {
			if (npcId == npcId2[i]) {
				closeDoorInCrystalCave(doorId[i]);
			}
		}
	}

	private L1NpcInstance npcTemp;

	public final L1NpcInstance getNpcTemp() {
		return npcTemp;
	}

	public final void setNpcTemp(final L1NpcInstance npcTemp) {
		this.npcTemp = npcTemp;
	}

	private long deleteTime;

	public final long getDeleteTime() {
		return deleteTime;
	}

	public final void setDeleteTime(final long deleteTime) {
		this.deleteTime = deleteTime;
	}

	private static void closeDoorInCrystalCave(final int doorId) {
		for (final L1Object object : World.get().getObject()) {
			if (object instanceof L1DoorInstance) {
				final L1DoorInstance door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.close();
				}
			}
		}
	}

	private boolean _isBroadcast;

	public final boolean isBroadcast() {
		return _isBroadcast;
	}

	public final void setBroadcast(final boolean isBroadcast) {
		_isBroadcast = isBroadcast;
	}

	private String _broadcastInfo;

	public final String getBroadcastInfo() {
		return _broadcastInfo;
	}

	public final void setBroadcastInfo(final String broadcastInfo) {
		_broadcastInfo = broadcastInfo;
	}
}
