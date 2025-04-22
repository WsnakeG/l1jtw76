package com.lineage.server.model.map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1GuardInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;

public class L1V1Map extends L1Map {

	private static final Log _log = LogFactory.getLog(L1V1Map.class);

	private int _mapId;

	private int _worldTopLeftX;

	private int _worldTopLeftY;

	private int _worldBottomRightX;

	private int _worldBottomRightY;

	private byte _map[][];

	private boolean _isUnderwater;

	private boolean _isMarkable;

	private boolean _isTeleportable;

	private boolean _isEscapable;

	private boolean _isUseResurrection;

	private boolean _isUsePainwand;

	private boolean _isEnabledDeathPenalty;

	private boolean _isTakePets;

	private boolean _isRecallPets;

	private boolean _isUsableItem;

	private boolean _isUsableSkill;

	private int _isUsableShop;
	
	 private boolean _isArrows;
	 
	 private boolean _isAutoBot;

	/**
	 * Mobなどの通行不可能になるオブジェクトがタイル上に存在するかを示すビットフラグ
	 */
	private static final byte BITFLAG_IS_IMPASSABLE = (byte) 128; // 1000 0000

	protected L1V1Map() {

	}

	public L1V1Map(final int mapId, final byte map[][], final int worldTopLeftX, final int worldTopLeftY,
			final boolean underwater, final boolean markable, final boolean teleportable,
			final boolean escapable, final boolean useResurrection, final boolean usePainwand,
			final boolean enabledDeathPenalty, final boolean takePets, final boolean recallPets,
			final boolean usableItem, final boolean usableSkill, final int usableShop, final boolean isArrows, final boolean isautoBot) {
		_mapId = mapId;
		_map = map;
		_worldTopLeftX = worldTopLeftX;// 起點X
		_worldTopLeftY = worldTopLeftY;// 起點Y

		_worldBottomRightX = (worldTopLeftX + map.length) - 1;// 終點X
		_worldBottomRightY = (worldTopLeftY + map[0].length) - 1;// 終點Y

		_isUnderwater = underwater;
		_isMarkable = markable;
		_isTeleportable = teleportable;
		_isEscapable = escapable;
		_isUseResurrection = useResurrection;
		_isUsePainwand = usePainwand;
		_isEnabledDeathPenalty = enabledDeathPenalty;
		_isTakePets = takePets;
		_isRecallPets = recallPets;
		_isUsableItem = usableItem;
		_isUsableSkill = usableSkill;
		_isUsableShop = usableShop;
		_isArrows = isArrows;
		_isAutoBot = isautoBot;
	}

	public L1V1Map(final L1V1Map map) {
		_mapId = map._mapId;

		// _mapをコピー
		_map = new byte[map._map.length][];
		for (int i = 0; i < map._map.length; i++) {
			_map[i] = map._map[i].clone();
		}

		_worldTopLeftX = map._worldTopLeftX;
		_worldTopLeftY = map._worldTopLeftY;
		_worldBottomRightX = map._worldBottomRightX;
		_worldBottomRightY = map._worldBottomRightY;

	}

	private int accessTile(final int x, final int y) {
		if (!this.isInMap(x, y)) { // とりあえずチェックする。これは良くない。
			return 0;
		}
		return _map[x - _worldTopLeftX][y - _worldTopLeftY];
	}

	private int accessOriginalTile(final int x, final int y) {
		return accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE);
	}

	private void setTile(final int x, final int y, final int tile) {
		if (!this.isInMap(x, y)) { // とりあえずチェックする。これは良くない。
			return;
		}
		_map[x - _worldTopLeftX][y - _worldTopLeftY] = (byte) tile;
	}

	/*
	 * ものすごく良くない気がする
	 */
	public byte[][] getRawTiles() {
		return _map;
	}

	@Override
	public int getId() {
		return _mapId;
	}

	@Override
	public int getX() {
		return _worldTopLeftX;
	}

	@Override
	public int getY() {
		return _worldTopLeftY;
	}

	@Override
	public int getWidth() {
		return (_worldBottomRightX - _worldTopLeftX) + 1;
	}

	@Override
	public int getHeight() {
		return (_worldBottomRightY - _worldTopLeftY) + 1;
	}

	@Override
	public int getTile(final int x, final int y) {
		final short tile = _map[x - _worldTopLeftX][y - _worldTopLeftY];
		if (0 != (tile & BITFLAG_IS_IMPASSABLE)) {
			return 300;
		}
		return accessOriginalTile(x, y);
	}

	@Override
	public int getOriginalTile(final int x, final int y) {
		return accessOriginalTile(x, y);
	}

	@Override
	public boolean isInMap(final Point pt) {
		return this.isInMap(pt.getX(), pt.getY());
	}

	@Override
	public boolean isInMap(final int x, final int y) {
		// フィールドの茶色エリアの判定
		if ((_mapId == 4) && ((x < 32520) || (y < 32070) || ((y < 32190) && (x < 33950)))) {
			return false;
		}
		return ((_worldTopLeftX <= x) && (x <= _worldBottomRightX) && (_worldTopLeftY <= y)
				&& (y <= _worldBottomRightY));
	}

	// 指定座標通行可能
	@Override
	public boolean isPassable(final Point pt, final L1Character cha) {
		return this.isPassable(pt.getX(), pt.getY(), cha);
	}

	// 指定座標通行可能
	@Override
	public boolean isPassable(final int x, final int y, final L1Character cha) {
		return this.isPassable(x, y - 1, 4, cha) || this.isPassable(x + 1, y, 6, cha)
				|| this.isPassable(x, y + 1, 0, cha) || this.isPassable(x - 1, y, 2, cha);
	}

	// 指定座標通行可能
	@Override
	public boolean isPassable(final Point pt, final int heading, final L1Character cha) {
		return this.isPassable(pt.getX(), pt.getY(), heading, cha);
	}

	// 正向
	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	@Override
	public boolean isPassable(final int x, final int y, final int heading, final L1Character cha) {
		try {
			// 目前座標
			final int tile1 = accessTile(x, y);

			final int locx = x + HEADING_TABLE_X[heading];
			final int locy = y + HEADING_TABLE_Y[heading];
			// 前往方向座標
			final int tile2 = accessTile(locx, locy);
			if (tile2 == 0x00) {
				return false;
			}
			if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
				return false;
			}
			if (cha != null) {// NPC
				switch (_mapId) {
				case 0:// 說話之島
					switch (tile2) {
					case 47:
						return true;
					case 46:
						return true;
					case 42:
						return true;
					case 26:
						return true;
					case 31:
						return true;
					case 21:
						return true;
					}
					return set_map(tile2, 0x01);

				case 4:// 大陸地區
				case 57:// 歌唱之島
				case 58:// 隱龍之地
				case 68:// 歌唱之島
				case 69:// 隱藏之谷
				case 70:// 遺忘之島
				case 303:// 夢幻之島
				case 430:// 精靈墓穴
				case 440:// 海賊島
				case 445:
				case 480:// 海賊島後半部
				case 613:// 奇怪之村落
				case 621:// 詭異村落
				case 630:// 天空之城
				case 9101:// 歐林副本地圖 by terry0412
				case 9102: // 歐林副本地圖 by terry0412
					switch (tile2) {
					case 28:
						return false;
					case 44:
						return false;
					case 21:
						return false;
					case 26:
						return false;
					case 29:
						return false;
					case 12:
						return false;
					}
					return set_map(tile2, 0x08);

				default:
					return set_map(tile2, 0x03);
				}

			} else {// PC
				int tile3;
				int tile4;
				switch (heading) {
				case 0:
					return (tile1 & 0x02) == 0x02;

				case 1:
					tile3 = accessTile(x, y - 1);
					tile4 = accessTile(x + 1, y);
					return ((tile3 & 0x01) == 0x01) || ((tile4 & 0x02) == 0x02);

				case 2:
					return (tile1 & 0x01) == 0x01;

				case 3:
					tile3 = accessTile(x, y + 1);
					return (tile3 & 0x01) == 0x01;

				case 4:
					return (tile2 & 0x02) == 0x02;

				case 5:
					return ((tile2 & 0x01) == 0x01) || ((tile2 & 0x02) == 0x02);

				case 6:
					return (tile2 & 0x01) == 0x01;

				case 7:
					tile3 = accessTile(x - 1, y);
					return (tile3 & 0x02) == 0x02;
				}
			}

		} catch (final Exception e) {
		}
		return false;
	}
	
	public boolean isPassable3(Point pt) {
		return isPassable3(pt.getX(), pt.getY());
	}

	public boolean isPassable3(int x, int y) {
		return isPassable3(x, y - 1, 4) || isPassable3(x + 1, y, 6)
				|| isPassable3(x, y + 1, 0) || isPassable3(x - 1, y, 2);
	}

	public boolean isPassable3(Point pt, int heading) {
		return isPassable3(pt.getX(), pt.getY(), heading);
	}
	
	public boolean isPassable3(int x, int y, int heading) {
		// 現在
		int tile1 = accessTile(x, y);
		// 移動予定
		//int tile2;
		
		// 修正怪物穿牆
		if (!((tile1 & 0x02) == 0x02 || (tile1 & 0x01) == 0x01)) {
			return false;
		}
		// ~修正怪物穿牆
		if(tile1!=0){ //12障礙?
			return true;
		}
	
		return false;
	}

	@Override
	public boolean isPassableDna(final int x, final int y, final int heading) {
		try {
			final int locx = x + HEADING_TABLE_X[heading];
			final int locy = y + HEADING_TABLE_Y[heading];
			// 前往方向座標
			final int tile2 = accessTile(locx, locy);
			if (tile2 == 0x00) {
				return false;
			}
			switch (_mapId) {// XXX
			case 0:// 說話之島
				switch (tile2) {
				case 47:
					return true;
				case 46:
					return true;
				case 42:
					return true;
				case 26:
					return true;
				case 31:
					return true;
				case 21:
					return true;
				}
				return set_map(tile2, 0x01);

			case 4:// 大陸地區
			case 57:// 歌唱之島
			case 58:// 隱龍之地
			case 68:// 歌唱之島
			case 69:// 隱藏之谷
			case 70:// 遺忘之島
			case 303:// 夢幻之島
			case 430:// 精靈墓穴
			case 440:// 海賊島
			case 445:
			case 480:// 海賊島後半部
			case 613:// 奇怪之村落
			case 621:// 詭異村落
			case 630:// 天空之城
			case 9101:// 歐林副本地圖 by terry0412
			case 9102: // 歐林副本地圖 by terry0412
				switch (tile2) {
				case 28:
					return false;
				case 44:
					return false;
				case 21:
					return false;
				case 26:
					return false;
				case 29:
					return false;
				case 12:
					return false;
				}
				return set_map(tile2, 0x08);

			default:
				return set_map(tile2, 0x03);
			}

		} catch (final Exception e) {
		}
		return false;
	}

	private boolean set_map(final int tile2, final int i) {
		return (tile2 & i) != 0x00;
	}

	@Override
	public boolean isDoorPassable(final int x, final int y, final int heading, final L1NpcInstance npc) {
		try {
			if (heading == -1) {
				return false;
			}
			final int locx = x + HEADING_TABLE_X[heading];
			final int locy = y + HEADING_TABLE_Y[heading];
			final int tile2 = accessTile(locx, locy);
			if (npc != null) {
				if (tile2 == 0x03) {// 關閉的門
					// 無首要目標
					if (npc.is_now_target() == null) {
						return false;
					}
					for (final L1Object object : World.get().getVisibleObjects(npc, 2)) {
						if (object instanceof L1DoorInstance) {// 障礙者是 門
							final L1DoorInstance door = (L1DoorInstance) object;
							switch (door.getDoorId()) {
							case 6006:// 黃金鑰匙
							case 6007:// 銀鑰匙
							case 10000:// 不死族的鑰匙
							case 10001:// 僵屍鑰匙
							case 10002:// 骷髏鑰匙
							case 10003:// 機關門(說明:不死族的叛徒 (法師30級以上官方任務))
							case 10004:// 蛇女房間鑰匙
							case 10005:// 安塔瑞斯洞穴
							case 10006:// 安塔瑞斯洞穴
							case 10007:// 安塔瑞斯洞穴
							case 10008:// 法利昂洞穴
							case 10009:// 法利昂洞穴
							case 10010:// 法利昂洞穴
							case 10011:// 法利昂洞穴
							case 10012:// 法利昂洞穴
							case 10013:// 法利昂洞穴
							case 10019:// 魔法師．哈汀(故事) 禁開
							case 10036:// 魔法師．哈汀(故事) 禁開
							case 10037:// 林德拜爾洞穴 added by terry0412
							case 10038:// 林德拜爾洞穴 added by terry0412
							case 10039:// 林德拜爾洞穴 added by terry0412
							case 10015:// 魔法師．哈汀(故事)// NO 1
							case 10016:// 魔法師．哈汀(故事)// NO 2
							case 10017:// 魔法師．哈汀(故事)// NO 2
							case 10020:// 魔法師．哈汀(故事)// NO 4
								return false;

							default:
								if (door.getOpenStatus() == ActionCodes.ACTION_Close) {// 關閉的門
									if (npc instanceof L1GuardInstance) {// 警衛
										door.open();
										return true;

									} else {
										if (door.getKeeperId() == 0) {// 沒有管家的門
											door.open();
											return true;
										}
									}

								} else {// 開啟的門
									return true;
								}
							}
						}
					}
					return false;

				} else {// 開啟的門
					return true;
				}
			}

		} catch (final Exception e) {
		}
		return true;
	}

	// 設定座標障礙宣告
	@Override
	public void setPassable(final Point pt, final boolean isPassable) {
		this.setPassable(pt.getX(), pt.getY(), isPassable, 0x02);
	}

	@Override
	public void setPassable(final int x, final int y, final boolean isPassable, final int door) {
		switch (door) {
		case 0x00:// 0:門／
			set_door_0(x, y, isPassable);
			break;

		case 0x01:// 1:門＼
			set_door_1(x, y, isPassable);
			break;

		default:// 2+:一般
			if (isPassable) {
				setTile(x, y, (short) (accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE)));

			} else {
				setTile(x, y, (short) (accessTile(x, y) | BITFLAG_IS_IMPASSABLE));
			}
			break;
		}
	}
	
	// 屍魂塔
	@Override
	public L1V1Map copyMap(final int newMapId) {
		return clone(newMapId);
	}
	
	// 屍魂塔
	private L1V1Map clone(final int newMapId) {

		final L1V1Map map = new L1V1Map(this);
		map._mapId = newMapId;

		map._isUnderwater = _isUnderwater;
		map._isMarkable = _isMarkable;
		map._isTeleportable = _isTeleportable;
		map._isEscapable = _isEscapable;
		map._isUseResurrection = _isUseResurrection;
		map._isUsePainwand = _isUsePainwand;
		map._isEnabledDeathPenalty = _isEnabledDeathPenalty;
		map._isTakePets = _isTakePets;
		map._isRecallPets = _isRecallPets;
		map._isUsableItem = _isUsableItem;
		map._isUsableSkill = _isUsableSkill;
		map._isAutoBot = _isAutoBot;
		return map;
	}

	/*
	 * 門／
	 */
	private void set_door_0(final int x, final int y, final boolean isPassable) {
		try {
			if (isPassable) {// 可通過
				_map[x - _worldTopLeftX][y - _worldTopLeftY] = 0x2f;

			} else {// 不可通過
				_map[x - _worldTopLeftX][y - _worldTopLeftY] = 0x03;
				_map[(x - 1) - _worldTopLeftX][y - _worldTopLeftY] = 0x03;
				_map[(x + 1) - _worldTopLeftX][y - _worldTopLeftY] = 0x03;
			}

		} catch (final Exception e) {
			_log.error("X:" + x + " Y:" + y + " MAP:" + _mapId, e);
		}
	}

	/*
	 * 1:門
	 */
	private void set_door_1(final int x, final int y, final boolean isPassable) {
		try {
			if (isPassable) {// 可通過
				_map[x - _worldTopLeftX][y - _worldTopLeftY] = 0x2f;

			} else {// 不可通過
				_map[x - _worldTopLeftX][y - _worldTopLeftY] = 0x03;
				_map[x - _worldTopLeftX][(y - 1) - _worldTopLeftY] = 0x03;
				_map[x - _worldTopLeftX][(y + 1) - _worldTopLeftY] = 0x03;
			}

		} catch (final Exception e) {
			_log.error("X:" + x + " Y:" + y + " MAP:" + _mapId, e);
		}
	}

	@Override
	public boolean isSafetyZone(final Point pt) {
		return this.isSafetyZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isSafetyZone(final int x, final int y) {
		final int tile = accessOriginalTile(x, y);
		return (tile & 0x30) == 0x10;
	}

	@Override
	public boolean isCombatZone(final Point pt) {
		return this.isCombatZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isCombatZone(final int x, final int y) {
		final int tile = accessOriginalTile(x, y);
		return (tile & 0x30) == 0x20;
	}

	@Override
	public boolean isNormalZone(final Point pt) {
		return this.isNormalZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isNormalZone(final int x, final int y) {
		final int tile = accessOriginalTile(x, y);
		return (tile & 0x30) == 0x00;
	}

	@Override
	public boolean isArrowPassable(final int x, final int y, final int heading) {
		int[][] diff = { { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 },
				{ -1, -1 } };
		int targetX = x + diff[heading][0];
		int targetY = y + diff[heading][1];

		if (isExistDoor(targetX, targetY)) {
			return false;
		}
		return isVaild(x, y);
	}
	
	public int isExistDoor1(final int x, final int y) {
		try {
			return this._map[x - this._worldTopLeftX][y - this._worldTopLeftY];
		} catch (final Exception e) {
		}
		return 0x00;
	}

	@Override
	public boolean isExistDoor(int x, int y) {
		for (L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
			if (_mapId != door.getMapId()) {
				continue;
			}
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				continue;
			}
			if (door.isDead()) {
				continue;
			}
			int leftEdgeLocation = door.getLeftEdgeLocation();
			int rightEdgeLocation = door.getRightEdgeLocation();
			int size = rightEdgeLocation - leftEdgeLocation;
			if (size == 0) { // 1マス分の幅のドア
				if ((x == door.getX()) && (y == door.getY())) {
					return true;
				}
			} else { // 2マス分以上の幅があるドア
				if (door.getDirection() == 0) { // ／向き
					for (int doorX = leftEdgeLocation; doorX <= rightEdgeLocation; doorX++) {
						if ((x == doorX) && (y == door.getY())) {
							return true;
						}
					}
				} else { // ＼向き
					for (int doorY = leftEdgeLocation; doorY <= rightEdgeLocation; doorY++) {
						if ((x == door.getX()) && (y == doorY)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isVaild(int x, int y) {
		int tile = accessTile(x, y);
		return (tile & 0xff) > 0;
	}

	@Override
	public boolean isUnderwater() {
		return _isUnderwater;
	}

	@Override
	public boolean isMarkable() {
		return _isMarkable;
	}

	@Override
	public boolean isTeleportable() {
		return _isTeleportable;
	}

	@Override
	public boolean isEscapable() {
		return _isEscapable;
	}

	@Override
	public boolean isUseResurrection() {
		return _isUseResurrection;
	}

	@Override
	public boolean isUsePainwand() {
		return _isUsePainwand;
	}

	@Override
	public boolean isEnabledDeathPenalty() {
		return _isEnabledDeathPenalty;
	}

	@Override
	public boolean isTakePets() {
		return _isTakePets;
	}

	@Override
	public boolean isRecallPets() {
		return _isRecallPets;
	}

	@Override
	public boolean isUsableItem() {
		return _isUsableItem;
	}

	@Override
	public boolean isUsableSkill() {
		return _isUsableSkill;
	}

	@Override
	public boolean isFishingZone(final int x, final int y) {
		return accessOriginalTile(x, y) == 16;
	}

	@Override
	public int isUsableShop() {
		return _isUsableShop;
	}

	@Override
	public String toString(final Point pt) {
		return "" + getOriginalTile(pt.getX(), pt.getY());
	}
	
	 @Override
		public boolean isArrows() {
			return this._isArrows;
		}
	 @Override
		public boolean isPassable(final int x, final int y, final int heading) {
			// 現在
			final int tile1 = accessTile(x, y);
			// 移動予定
			int tile2;

			int newX;
			int newY;

			if (heading == 0) {
				tile2 = accessTile(x, y - 1);
				newX = x;
				newY = y - 1;
			} else if (heading == 1) {
				tile2 = accessTile(x + 1, y - 1);
				newX = x + 1;
				newY = y - 1;
			} else if (heading == 2) {
				tile2 = accessTile(x + 1, y);
				newX = x + 1;
				newY = y;
			} else if (heading == 3) {
				tile2 = accessTile(x + 1, y + 1);
				newX = x + 1;
				newY = y + 1;
			} else if (heading == 4) {
				tile2 = accessTile(x, y + 1);
				newX = x;
				newY = y + 1;
			} else if (heading == 5) {
				tile2 = accessTile(x - 1, y + 1);
				newX = x - 1;
				newY = y + 1;
			} else if (heading == 6) {
				tile2 = accessTile(x - 1, y);
				newX = x - 1;
				newY = y;
			} else if (heading == 7) {
				tile2 = accessTile(x - 1, y - 1);
				newX = x - 1;
				newY = y - 1;
			} else {
				return false;
			}

			if (isExistDoor1(newX, newY) == 3) {
				// if (isExistDoor(newX, newY)) {
				return false;
			}

			if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
				return false;
			}

			if (!((tile2 & 0x02) == 0x02 || (tile2 & 0x01) == 0x01)) {
				return false;
			}

			if (heading == 0) {
				return (tile1 & 0x02) == 0x02;
			} else if (heading == 1) {
				final int tile3 = accessTile(x, y - 1);
				final int tile4 = accessTile(x + 1, y);
				return ((tile3 & 0x01) == 0x01) || ((tile4 & 0x02) == 0x02);
			} else if (heading == 2) {
				return (tile1 & 0x01) == 0x01;
			} else if (heading == 3) {
				final int tile3 = accessTile(x, y + 1);
				return (tile3 & 0x01) == 0x01;
			} else if (heading == 4) {
				return (tile2 & 0x02) == 0x02;
			} else if (heading == 5) {
				return ((tile2 & 0x01) == 0x01) || ((tile2 & 0x02) == 0x02);
			} else if (heading == 6) {
				return (tile2 & 0x01) == 0x01;
			} else if (heading == 7) {
				final int tile3 = accessTile(x - 1, y);
				return (tile3 & 0x02) == 0x02;
			}

			return false;
		}
	 
	 @Override
		public boolean isAutoBot() {
			return _isAutoBot;
		}
}