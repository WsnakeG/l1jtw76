package com.lineage.data.quest;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1GroundInventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;

/**
 * 說明:魔法師．哈汀(故事)
 * 
 * @author loli
 */
public class Chapter01R implements Runnable {

	private static final Log _log = LogFactory.getLog(Chapter01R.class);

	public static final Random _random = new Random();

	private static final int _mapid = Chapter01.MAPID;

	private static final int _count_spawn = 25; // 召喚怪物數量

	private static final int _sleep = 5; // 休眠時間(中場休息)

	private static final byte _h_x[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte _h_y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	private static int[] _spawn_01 = new int[] { 91340, 91341, 91342 };// 隊長

	private static int[] _spawn_02 = new int[] { 91265, 91266, 91267, 91268 };// 骷髏房

	private static int[] _attack_01 = new int[] { 91265, 91266, 91267, 91268, 91270 };// 第1波
	private static int[] _attack_02 = new int[] { 91265, 91266, 91267, 91268, 91270, 91271 };// 第2波
	private static int[] _attack_03 = new int[] { 91270, 91271, 91272, 91273, 91268 };// 第3波
	private static int[] _attack_04 = new int[] { 91274, 91275, 91276, 91272, 91273, 91268 };// 第4波
	private static int[] _attack_05 = new int[] { 91274, 91275, 91276, 91277, 91278, 91279 };// 第5波
	private static int[] _attack_06 = new int[] { 91280, 91282, 91281, 91282, 91283, 91284 };// 第6波
	private static int[] _attack_07 = new int[] { 91286, 91287, 91288, 91300, 91301, 91302 };// 第7波
	private static int[] _attack_08 = new int[] { 91299, 91300, 91302, 91280, 91279, 91305 };// 第8波
	private static int[] _attack_09 = new int[] { 91299, 91300, 91302, 91283, 91284, 91305 };// 第9波
	private static int[] _attack_10 = new int[] { 91299, 91300, 91302, 91301, 91302, 91303, 91305 };// 第10波
	private static int[] _attack_11 = new int[] { 91299, 91300, 91302, 91301, 91302, 91303, 91304 };// 第11波
	private static int[] _attack_12 = new int[] { 91299, 91300, 91302, 91301, 91302, 91303, 91305, 91304 };// 第12波

	private static int[][] _light_loc1 = new int[][] { { 32803, 32866 }, { 32801, 32865 }, { 32800, 32864 },
			{ 32798, 32866 }, { 32799, 32872 }, { 32800, 32873 }, { 32803, 32873 }, };// 8
																						// 個紅地標
																						// 座標

	private static int[] _light_loc2 = new int[] { 32801, 32869 // 黑地標
	};// 1個黑地標 座標

	private static final L1Location _loc1 = new L1Location(32702, 32837, _mapid);

	private static final L1Location _loc2 = new L1Location(32714, 32836, _mapid);

	private static final L1Location _loc3 = new L1Location(32713, 32855, _mapid);

	private static final L1Location _loc4 = new L1Location(32699, 32856, _mapid);

	private final ArrayList<L1DoorInstance> _doors = new ArrayList<L1DoorInstance>();// 黑門的清單

	private final ArrayList<L1FieldObjectInstance> _dooropens = new ArrayList<L1FieldObjectInstance>();// 黑牆的清單

	private final ConcurrentHashMap<Integer, L1NpcInstance> _npclist = new ConcurrentHashMap<Integer, L1NpcInstance>();

	private int _user_count = 4;// 傳送條件(人數)

	private int _showid = -1;

	private int _qid = -1;

	private L1NpcInstance _hardin; // 哈汀

	private int _hardin_tak = 0; // 哈汀說話

	private L1NpcInstance _cerenis; // 賽尼斯

	private int _cerenis_tak = 0; // 願意聽賽尼斯說話

	private L1Party _party = null;

	private boolean _start = true;

	private boolean _msgInLeader = false;// 訊息包含隊長

	private boolean _gfx_01 = false;// 第一個光圈

	private int _door_01 = 0;// 第1個門 時間

	private int _door_02 = 0;// 第2個門 時間

	private int _door_03 = 0;// 第3個門 時間

	private int _door_04 = 0;// 第4個門 時間

	private int _door_04open = 0;// 第4個門被開啟 時間

	public boolean DOOR_1 = false;// 第1個門

	public boolean DOOR_2 = false;// 第2個門

	public boolean DOOR_3 = false;// 第3個門

	public boolean DOOR_4 = false;// 第4個門

	public boolean DOOR_4OPEN = false;// 第4個門打開

	private boolean _boss_a_death = false;// BOSS A

	private boolean _boss_b_death = false;// BOSS B

	private int _spawn = 0;// 召喚BOSS模式

	private int _time = 0;

	private int _time12 = 0;// 12階段進行時間

	private int _time_over = 0;// 黑門進行時間(休眠時間)

	private int _mode_s = 0;// 賽尼斯耐心程度

	private int _mode12 = 0;// 12階段進行狀態

	private int _count_01 = 0;// 1階段殘留怪物數量記錄
	private int _count_02 = 0;// 2階段殘留怪物數量記錄
	private int _count_03 = 0;// 3階段殘留怪物數量記錄
	private int _count_04 = 0;// 4階段殘留怪物數量記錄
	private int _count_05 = 0;// 5階段殘留怪物數量記錄
	private int _count_06 = 0;// 6階段殘留怪物數量記錄
	private int _count_07 = 0;// 7階段殘留怪物數量記錄
	private int _count_08 = 0;// 8階段殘留怪物數量記錄
	private int _count_09 = 0;// 9階段殘留怪物數量記錄
	private int _count_10 = 0;// 10階段殘留怪物數量記錄
	private int _count_11 = 0;// 11階段殘留怪物數量記錄
	private int _count_12 = 0;// 12階段殘留怪物數量記錄

	/**
	 * 召喚地標
	 */
	private void spawn_light() {
		for (final int[] locyx : _light_loc1) {
			L1SpawnUtil.spawn(_showid, 6708, locyx[0], locyx[1], _mapid, 0);// 紅地標
		}
	}

	/**
	 * 隊員與地標重疊檢查
	 */
	private void spawn_light_check() {
		int ch = 0;
		for (final L1PcInstance pc : _party.getMemberList()) {
			if (pc.getMapId() == _mapid) {
				if ((pc.getX() == _light_loc1[0][0]) && (pc.getY() == _light_loc1[0][1])) {
					pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7578));
					ch += 1;
				}
				if ((pc.getX() == _light_loc1[1][0]) && (pc.getY() == _light_loc1[1][1])) {
					pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7578));
					ch += 1;
				}
				if ((pc.getX() == _light_loc1[2][0]) && (pc.getY() == _light_loc1[2][1])) {
					pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7578));
					ch += 1;
				}
				if ((pc.getX() == _light_loc1[3][0]) && (pc.getY() == _light_loc1[3][1])) {
					pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7578));
					ch += 1;
				}
				if ((pc.getX() == _light_loc1[4][0]) && (pc.getY() == _light_loc1[4][1])) {
					pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7578));
					ch += 1;
				}
				if ((pc.getX() == _light_loc1[5][0]) && (pc.getY() == _light_loc1[5][1])) {
					pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7578));
					ch += 1;
				}
				if ((pc.getX() == _light_loc1[6][0]) && (pc.getY() == _light_loc1[6][1])) {
					pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7578));
					ch += 1;
				}
			}
		}
		if (ch >= _user_count) {
			L1SpawnUtil.spawn(_showid, 6709, _light_loc2[0], _light_loc2[1], _mapid, 0);// 裝飾
			_hardin_tak = 52;
		}
	}

	/**
	 * 中央光圈的檢查
	 */
	private void spawn_light_check2() {
		for (final L1PcInstance pc : _party.getMemberList()) {
			if (pc.getMapId() == _mapid) {
				if ((pc.getX() == _light_loc2[0]) && (pc.getY() == _light_loc2[1])) {
					outWindows1();
					pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7579));
					_hardin_tak = 53;
				}
			}
		}
	}

	public void boss_a_death() {
		try {
			_boss_a_death = true;
			// 8703 哈汀：呼…終於擋住了。加油！
			outMsgWindows("$8703");

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void boss_b_death() {
		try {
			_boss_b_death = true;
			// 7707 好厲害喔，把【賽尼斯】用昏了。。
			outMsgWindows("$7707");

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 加入NPC清單
	 * 
	 * @param mob
	 */
	private void add(final L1NpcInstance value) {
		try {
			_npclist.put(value.getId(), value);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 移出NPC清單
	 * 
	 * @param mob
	 */
	private void remove(final L1NpcInstance value) {
		try {
			_npclist.remove(value.getId());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 降低關卡怪物數量
	 */
	public void down_count(final L1NpcInstance npc) {
		try {
			remove(npc);
			String wmsg = null;
			final int i = _npclist.size();
			switch (_mode12) {
			case 1:
				_count_01 = _npclist.size();
				break;
			case 2:
				_count_02 = _npclist.size();
				break;
			case 3:
				_count_03 = _npclist.size();
				break;
			case 4:
				_count_04 = _npclist.size();
				break;
			case 5:
				_count_05 = _npclist.size();
				break;
			case 6:
				_count_06 = _npclist.size();
				break;
			case 7:
				_count_07 = _npclist.size();
				break;
			case 8:
				_count_08 = _npclist.size();
				break;
			case 9:
				_count_09 = _npclist.size();
				break;
			case 10:
				_count_10 = _npclist.size();
				break;
			case 11:
				_count_11 = _npclist.size();
				break;
			case 12:
				_count_12 = _npclist.size();
				break;
			}

			if (_mode12 < 1) {
				return;
			}
			if (_mode12 > 12) {
				return;
			}
			switch (i) {
			case 18:
				wmsg = "$8689";// 8689封印解除一點 [1/4]
				break;
			case 12:
				wmsg = "$8690";// 8690封印解除一半 [2/4]
				break;
			case 6:
				wmsg = "$8691";// 8691封印解除一半以上 [3/4]
				break;
			case 0:
				wmsg = "$8692";// 8692封印完全解除[4/4]
				break;
			}
			if (wmsg != null) {
				outMsgWindows(wmsg);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public Chapter01R(final L1Party party, final int qid, final int i) {
		_party = party;
		for (final L1PcInstance pc : _party.getMemberList()) {
			pc.set_hardinR(this);

			int count = 4;
			if (pc.getParty().isLeader(pc) && pc.isGm()) {
				count = 1;
			}
			_user_count = count;
		}
		_showid = qid;
		_qid = i;
	}

	public void startR() {
		GeneralThreadPool.get().execute(this);
	}

	@Override
	public void run() {
		try {
			while (_start) {
				// 隊伍
				checkParty();

				// 隊長
				getLeader();

				// 隊員
				getMembers();

				if (!checkParty()) {
					_start = false;
				}

				if (WorldQuest.get().get(_showid) == null) {
					_start = false;
				}

				Thread.sleep(1000);
				_time++;
			}
			Thread.sleep(3000);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			// 傳出成員
			outUser();
			_doors.clear();// 黑門的清單
			_dooropens.clear();// 黑牆的清單
			_npclist.clear();
		}
	}

	private boolean checkParty() {
		int user = 0;
		try {
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					user++;
				}
			}

		} catch (final Exception e) {
		}
		return user >= _user_count;
	}

	/**
	 * 執行時間
	 * 
	 * @return
	 */
	public int get_time() {
		return _time;
	}

	/**
	 * 視窗訊息
	 */
	private void outMsgWindows(final String msg) {
		try {// $7597 哈汀
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					if (_msgInLeader) { // 是否包含隊長
						pc.sendPackets(new S_PacketBoxGree(0x02, msg));
					} else {
						if (!_party.isLeader(pc)) {
							pc.sendPackets(new S_PacketBoxGree(0x02, msg));
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 隊員訊息
	 */
	private void outMsg(final String msg) {
		try {
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					pc.sendPackets(new S_NpcChat(_hardin, msg));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 視窗特效
	 */
	private void outWindows2() {
		try {
			outWindows1();
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					pc.sendPackets(new S_PacketBoxGree(0x01));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 震動
	 */
	private void outWindows1() {
		try {
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					pc.sendPackets(new S_PacketBoxGree(0x02));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 隊員檢查
	 */
	private void getMembers() {
		try {
			if (_hardin == null) {
				// 隊長可視範圍內
				for (final L1Object object : World.get().getVisibleObjects(_party.getLeader())) {
					// for (L1Object object :
					// World.get().getVisibleObjects(_party.partyUser())) {
					if (object instanceof L1NpcInstance) {
						final L1NpcInstance npc = (L1NpcInstance) object;
						if (npc.getNpcId() == 91330) {// 哈汀
							_hardin = npc;
						}
					}
				}

			} else {
				String msg = null;
				String wmsg = null;
				switch (_hardin_tak) {
				case 0:
					if (_time >= 5) {
						msg = "$8693";// 開始最後檢查？
						_hardin_tak = 1;
					}
					break;

				case 1:
					if (all_ok()) {
						_hardin_tak = 12;// 全部回應
					} else {
						msg = "$8702";// 那麼開始啊？
						_hardin_tak = 2;// 未完成回應
					}
					break;

				case 2:
				case 3:
				case 4:
					if (all_ok()) {
						_hardin_tak = 12;// 全部回應
					} else {
						_hardin_tak += 1;// 未完成回應
					}
					break;

				case 5:
					if (all_ok()) {
						_hardin_tak = 12;// 全部回應
					} else {
						msg = "$7599";// 【歐林】 送別一下吧，按【alt + 2】就可以了。
						_hardin_tak = 6;// 未完成回應
					}
					break;

				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					if (all_ok()) {
						_hardin_tak = 12;// 全部回應
					} else {
						_hardin_tak += 1;// 未完成回應
					}
					break;

				case 11:
					if (all_ok()) {
						_hardin_tak = 12;// 全部回應
					} else {
						msg = "$8694";// 如果不需要最後檢查，請按[alt + 2]
						_hardin_tak = 1;// 未完成回應
					}
					break;

				case 12:
					// 召喚光圈 啟動傳送座標
					if (!_gfx_01) {
						// 哈汀傳送光圈
						L1SpawnUtil.spawn(_showid, 7392, 32726, 32724, _mapid, 0);
						L1SpawnUtil.spawn(_showid, 7392, 32716, 32846, _mapid, 0);// 裝飾

						// 全部光球
						L1SpawnUtil.spawn(_showid, 7480, 32668, 32817, _mapid, 0);// NO
																					// 1
																					// 上
						L1SpawnUtil.spawn(_showid, 7480, 32666, 32817, _mapid, 0);// NO
																					// 1
																					// 左
						L1SpawnUtil.spawn(_showid, 7480, 32668, 32819, _mapid, 0);// NO
																					// 1
																					// 右
						L1SpawnUtil.spawn(_showid, 7480, 32666, 32819, _mapid, 0);// NO
																					// 1
																					// 下

						// L1SpawnUtil.spawn(_showid, 7480, 32684, 32816,
						// _mapid, 0);// 裝飾
						// L1SpawnUtil.spawn(_showid, 7480, 32732, 32789,
						// _mapid, 0);// 裝飾

						L1SpawnUtil.spawn(_showid, 7480, 32712, 32793, _mapid, 0);// NO
																					// 2
																					// 上
						L1SpawnUtil.spawn(_showid, 7480, 32703, 32791, _mapid, 0);// NO
																					// 2
																					// 左
						L1SpawnUtil.spawn(_showid, 7480, 32710, 32803, _mapid, 0);// NO
																					// 2
																					// 右
						L1SpawnUtil.spawn(_showid, 7480, 32703, 32800, _mapid, 0);// NO
																					// 2
																					// 下

						L1SpawnUtil.spawn(_showid, 7480, 32809, 32837, _mapid, 0);// NO
																					// 4
																					// 上
						L1SpawnUtil.spawn(_showid, 7480, 32807, 32837, _mapid, 0);// NO
																					// 4
																					// 左
						L1SpawnUtil.spawn(_showid, 7480, 32809, 32839, _mapid, 0);// NO
																					// 4
																					// 右
						L1SpawnUtil.spawn(_showid, 7480, 32807, 32839, _mapid, 0);// NO
																					// 4
																					// 下

						final L1QuestUser quest = WorldQuest.get().get(_showid);
						// 召喚門
						L1SpawnUtil.spawnDoor(quest, 10014, 92, 32664, 32807, (short) _mapid, 0);

						L1SpawnUtil.spawnDoor(quest, 10015, 93, 32673, 32820, (short) _mapid, 1);// NO
																									// 1(4人達到指定位置)

						L1SpawnUtil.spawnDoor(quest, 10016, 92, 32725, 32795, (short) _mapid, 0);// NO
																									// 2(4人達到指定位置)
						L1SpawnUtil.spawnDoor(quest, 10017, 93, 32726, 32812, (short) _mapid, 1);// NO
																									// 2(4人達到指定位置)

						// NO 3 傳送光圈(4人達到指定位置)

						// NO 4 清光骷髏房骷髏
						L1SpawnUtil.spawnDoor(quest, 10020, 93, 32723, 32848, (short) _mapid, 1);// 小巴房前

						// 一般門
						L1SpawnUtil.spawnDoor(quest, 10019, 92, 32802, 32821, (short) _mapid, 0);// 禁開
						L1SpawnUtil.spawnDoor(quest, 10018, 93, 32705, 32816, (short) _mapid, 1);
						L1SpawnUtil.spawnDoor(quest, 10021, 88, 32681, 32797, (short) _mapid, 0);
						L1SpawnUtil.spawnDoor(quest, 10022, 88, 32699, 32803, (short) _mapid, 0);
						L1SpawnUtil.spawnDoor(quest, 10023, 89, 32740, 32788, (short) _mapid, 1);
						L1SpawnUtil.spawnDoor(quest, 10024, 89, 32750, 32793, (short) _mapid, 1);
						L1SpawnUtil.spawnDoor(quest, 10025, 89, 32766, 32791, (short) _mapid, 1);
						L1SpawnUtil.spawnDoor(quest, 10026, 88, 32803, 32832, (short) _mapid, 0);
						L1SpawnUtil.spawnDoor(quest, 10027, 88, 32799, 32844, (short) _mapid, 0);
						L1SpawnUtil.spawnDoor(quest, 10028, 88, 32803, 32862, (short) _mapid, 0);
						L1SpawnUtil.spawnDoor(quest, 10029, 89, 32795, 32870, (short) _mapid, 1);

						L1SpawnUtil.spawnDoor(quest, 10030, 89, 32740, 32872, (short) _mapid, 1);
						L1SpawnUtil.spawnDoor(quest, 10031, 89, 32736, 32872, (short) _mapid, 1);
						L1SpawnUtil.spawnDoor(quest, 10032, 89, 32732, 32872, (short) _mapid, 1);

						L1SpawnUtil.spawnDoor(quest, 10033, 89, 32759, 32847, (short) _mapid, 1);
						L1SpawnUtil.spawnDoor(quest, 10034, 89, 32672, 32858, (short) _mapid, 1);
						L1SpawnUtil.spawnDoor(quest, 10035, 88, 32808, 32792, (short) _mapid, 0);
						// 黑牆
						// 32703 32866 - 32711 32866
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32703, 32866, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32704, 32866, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32705, 32866, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32706, 32866, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32707, 32866, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32708, 32866, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32709, 32866, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32710, 32866, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32711, 32866, (short) _mapid, 0));
						// 32703 32872 - 32709 32872
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32703, 32872, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32704, 32872, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32705, 32872, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32706, 32872, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32707, 32872, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32708, 32872, (short) _mapid, 0));
						_doors.add(
								L1SpawnUtil.spawnDoor(quest, 10036, 7536, 32709, 32872, (short) _mapid, 0));

						// 箭孔
						L1SpawnUtil.spawn(70558, new L1Location(32674, 32856, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32678, 32856, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32680, 32856, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32682, 32856, _mapid), 4, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32683, 32857, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32683, 32859, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32683, 32861, _mapid), 6, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32671, 32864, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32671, 32865, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32673, 32868, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32673, 32869, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32672, 32873, _mapid), 6, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32670, 32863, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32666, 32863, _mapid), 4, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32721, 32842, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32721, 32834, _mapid), 6, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32737, 32845, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32735, 32845, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32733, 32845, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32731, 32845, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32729, 32845, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32727, 32845, _mapid), 4, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32734, 32840, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32732, 32840, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32730, 32840, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32728, 32840, _mapid), 4, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32729, 32832, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32734, 32832, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32742, 32832, _mapid), 4, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32760, 32828, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32751, 32828, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32749, 32828, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32747, 32828, _mapid), 4, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32760, 32833, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32754, 32842, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32754, 32855, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32760, 32863, _mapid), 6, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32744, 32860, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32736, 32860, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32733, 32858, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32733, 32858, _mapid), 4, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32729, 32858, _mapid), 4, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32803, 32841, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32803, 32838, _mapid), 6, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32815, 32827, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32829, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32831, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32833, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32835, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32837, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32839, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32841, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32843, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32845, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32815, 32847, _mapid), 6, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32759, 32800, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32759, 32801, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32759, 32802, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32759, 32803, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32759, 32804, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32759, 32805, _mapid), 6, _showid);

						L1SpawnUtil.spawn(70558, new L1Location(32744, 32806, _mapid), 6, _showid);
						L1SpawnUtil.spawn(70558, new L1Location(32744, 32807, _mapid), 6, _showid);
						_gfx_01 = true;
					}
					if (all_teleport()) {
						_hardin_tak = 13;// 下一階段
					}
					break;

				case 13:
					wmsg = "$7597: $8695";// 進入地監，會測試你的資格
					_user_02 = 0;
					_alt2_02 = 0;
					_hardin_tak = 14;// 下一階段
					break;

				case 14:// 第1個門未開啟
					if (DOOR_1) {
						_hardin_tak = 15;// 下一階段

					} else {
						if (check_loc1()) {
							outWindows1();
							// 第1個光圈
							L1SpawnUtil.spawn(_showid, 7392, 32667, 32818, _mapid, 0);
							DOOR_1 = true;
						}
						_door_01++;
						switch (_door_01) {
						case 5:
							wmsg = "$7597: $8696";// 都是些不厲害的對象，相信你能夠輕易通過測試
							break;
						case 10:
							wmsg = "$7597: $8697";// 還有，你們具備了解除或設置陷阱的條件
							break;
						case 15:
							wmsg = "$7597: $8698";// 腳板儘量做得顯眼…
							break;
						case 90:
							wmsg = "$7560: $7618";// 底下那些傢伙是笨蛋嗎?為什麼這麼慢呢！
							_mode_s++;
							break;
						case 120:
							wmsg = "$7597: $7683";// 這次是失敗！請都逃離！將此地封印！
							break;
						case 125:
							wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							break;
						case 240:
							wmsg = "$7597: $7683";// 7683 這次是失敗！請都逃離！將此地封印！
							_start = false;
							break;
						}
					}
					break;

				case 15:// 第2個門未開啟(2個)
					if (DOOR_2) {
						_hardin_tak = 16;// 下一階段

					} else {
						if (check_loc2()) {
							outWindows1();
							// 第2個光圈
							L1SpawnUtil.spawn(_showid, 7392, 32708, 32797, _mapid, 0);
							DOOR_2 = true;
						}
						_door_02++;
						switch (_door_02) {
						case 5:
							wmsg = "$7597: $8699";// 對我的審美觀不需要你的評價
							break;
						case 10:
							wmsg = "$7597: $8700";// 此外，好像還有什麼值得期待的，但是不要遲到啊
							break;
						case 15:
							wmsg = "$7597: $8701";// 小心！ 安全無法保證
							break;
						case 90:
							wmsg = "$7560: $7627";// 呼…這些笨蛋！可不可以快一點阿！不要再考驗我的耐心了…'
							_mode_s++;
							break;
						case 120:
							wmsg = "$7597: $7683";// 這次是失敗！請都逃離！將此地封印！
							break;
						case 125:
							wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							break;
						case 240:
							wmsg = "$7597: $7683";// 7683 這次是失敗！請都逃離！將此地封印！
							_start = false;
							break;
						}
					}
					break;

				case 16:// 第3個傳送圈尚未啟動
					if (DOOR_3) {
						_hardin_tak = 17;// 下一階段

					} else {
						if (check_loc3()) {
							outWindows1();
							// 第3個光圈
							L1SpawnUtil.spawn(_showid, 7392, 32808, 32838, _mapid, 0);
							L1SpawnUtil.spawn(_showid, 7392, 32788, 32822, _mapid, 0);
							DOOR_3 = true;
						}
						_door_03++;
						switch (_door_03) {
						case 5:
							wmsg = "$7597: $7649";// 沒有多少時間了，我們一定要讓邪惡氣息無法跑到外部流出去！
							break;
						case 10:
							wmsg = "$7597: $7650";// 趁異次元界變得不安全，才會造成惡勢力的傢伙蜂擁而入！
							break;
						case 15:
							wmsg = "$7597: $7651";// 請幫我準備一下！
							break;
						case 90:
							wmsg = "$7560: $7642";// 這些笨蛋似乎是要等到我生氣！哼哼！
							_mode_s++;
							break;
						case 120:
							wmsg = "$7597: $7683";// 這次是失敗！請都逃離！將此地封印！
							break;
						case 125:
							wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							break;
						case 240:
							wmsg = "$7597: $7683";// 7683 這次是失敗！請都逃離！將此地封印！
							_start = false;
							break;
						}
					}
					break;

				case 17:// 召喚骷髏
					if (DOOR_3) {
						_npclist.clear();
						final L1Location loc = new L1Location(32776, 32846, _mapid);
						for (int i = 0; i < _count_spawn; i++) {
							final int npcid = _spawn_02[_random.nextInt(_spawn_02.length)];
							final int rr = _random.nextInt(4) + 4;
							final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 0);
							if (mob != null) {
								add(mob);
							}
						}
						_hardin_tak = 18;// 下一階段
						_mode12 = 0;// 模式0
					}
					break;

				case 18:// 第4個門未開啟
					if (DOOR_4) {
						_hardin_tak = 19;// 下一階段

					} else {
						if (check_loc4()) {
							outWindows1();
							DOOR_4 = true;
						}
						_door_04++;
						switch (_door_04) {
						case 60:
							wmsg = "$7560: $7642";// 這些笨蛋似乎是要等到我生氣！哼哼！
							break;
						case 120:
							wmsg = "$7597: $7683";// 這次是失敗！請都逃離！將此地封印！
							break;
						case 125:
							wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							break;
						case 240:
							wmsg = "$7597: $7683";// 7683 這次是失敗！請都逃離！將此地封印！
							_start = false;
							break;
						}
					}
					break;

				case 19:
					if (_msgInLeader) {
						_hardin_tak = 20;// 下一階段
					}
					break;

				case 20:
					_door_04open++;
					if (DOOR_4OPEN) {
						if (_door_04open >= 15) {
							_hardin_tak = 21;// 下一階段
							_time_over = 0;
						}
					}
					break;

				case 21:
					_time_over++;
					if (_time_over == _sleep) {
						if (_cerenis_tak == 0) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = 91337;// 魔界的葛林
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							_hardin_tak = 22;// 進入葛林檢查
							_mode12 = 1;// 模式1
							wmsg = "$7597: $7832";// 7832
													// 呼…沒責任心的老傢伙…還說可以把事情交給他..結果…卻只有這樣嗎...

						} else {
							_hardin_tak = 24;// 進入1階段召喚(1/12)
							wmsg = "$7597: $7663";// 辛苦你們了。是個比預期好的成果。呵呵呵。
						}
						_time12 = 0;
						_time_over = 0;
					}
					break;

				case 22:// 葛林檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 23;// 任務失敗-黑門作業

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7639";// 這是什麼阿！！！
							break;
						case 30:
							wmsg = "$7597: $7702";// 什麼！這裡！是肉店嗎?
							break;
						case 120:
							wmsg = "$7597: $7682";// 7682 全都逃走吧！再這樣下去可能會沒命的！
							break;
						case 180:
							wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							break;
						case 240:
							wmsg = "$7597: $7683";// 7683 這次是失敗！請都逃離！將此地封印！
							_hardin_tak = 23;// 任務失敗
							break;
						}
					}
					break;

				case 23:// 任務失敗-傳出成員
					_start = false;
					break;

				case 24:// 進入1階段召喚(1/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 0) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_01[_random.nextInt(_attack_01.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							_hardin_tak = 25;// 進入1階段檢查
							_mode12 = 1;// 模式1
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();
					}
					break;

				case 25:// 進入1階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 26;// 進入2階段召喚(2/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7694";// 那些也是以部分計劃內容所行動啊。呼呼。。
							break;
						case 30:
							wmsg = "$7597: $7695";// 好吧，這都是那位計劃之中呢。。
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7693";// 停止呼吸，快逃離這邊！那熱氣將肺溶化之前。快！
							}
							_hardin_tak = 26;// 進入2階段召喚(2/12)
							break;
						}
					}
					break;

				case 26:// 進入2階段召喚(2/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 1) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_02[_random.nextInt(_attack_02.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							_hardin_tak = 27;// 進入2階段檢查
							_mode12 = 2;// 模式2
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8708";// 8708 第一個封印已解除 [1/12]
					}
					break;

				case 27:// 進入2階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 28;// 進入3階段召喚(3/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7589";// 這玩法是甚麼…
							break;
						case 30:
							wmsg = "$7597: $7590";// 所有事情都快煩死了！
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7681";// 我太高估你們了嗎…
							}
							_hardin_tak = 28;// 進入3階段召喚(3/12)
							break;
						}
					}
					break;

				case 28:// 進入3階段召喚(3/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 2) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_03[_random.nextInt(_attack_03.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							_hardin_tak = 29;// 進入3階段檢查
							_mode12 = 3;// 模式3
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8709";// 8709 第二個封印已解除 [2/12]
					}
					break;

				case 29:// 進入3階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 30;// 進入4階段召喚(4/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7588";// 消滅！全部都消滅吧！
							break;
						case 30:
							wmsg = "$7597: $7584";// 我決不怕火！決！不！
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7684";// 7684 啊！魔法陣快消失了！
							}
							_hardin_tak = 30;// 進入4階段召喚(4/12)
							break;
						}
					}
					break;

				case 30:// 進入4階段召喚(4/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 3) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_04[_random.nextInt(_attack_04.length)];
								final int rr = _random.nextInt(2) + 2;
								L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							_hardin_tak = 31;// 進入4階段檢查
							_mode12 = 4;// 模式4
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8710";// 8710 第三個封印已解除 [3/12]
					}
					break;

				case 31:// 進入4階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 32;// 進入5階段召喚(5/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7663";// 辛苦你們了。是個比預期好的成果。呵呵呵。
							break;
						case 30:
							wmsg = "$7597: $7645";// 呼…消失吧！！
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7683";// 7683 這次是失敗！請都逃離！將此地封印！
							}
							_hardin_tak = 32;// 進入5階段召喚(5/12)
							break;
						}
					}
					break;

				case 32:// 進入5階段召喚(5/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 4) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_05[_random.nextInt(_attack_05.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							_hardin_tak = 33;// 進入5階段檢查
							_mode12 = 5;// 模式5
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8704";// 8704 第四個封印已解除[4/12]
					}
					break;

				case 33:// 進入5階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 34;// 進入6階段召喚(6/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7700";// 為什麼這麼快就開始了呢！
							break;
						case 30:
							wmsg = "$7597: $7701";// 是誰在叫我?呵呵呵！
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7682";// 全都逃走吧！再這樣下去可能會沒命的！
							}
							_hardin_tak = 34;// 進入6階段召喚(6/12)
							break;
						}
					}
					break;

				case 34:// 進入6階段召喚(6/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 5) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_06[_random.nextInt(_attack_06.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							_hardin_tak = 35;// 進入6階段檢查
							_mode12 = 6;// 模式6
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8711";// 8711 第五個封印已解除 [5/12]
					}
					break;

				case 35:// 進入6階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 36;// 進入7階段召喚(7/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7684";// 啊！魔法陣快消失了！
							break;
						case 30:
							wmsg = "$7597: $7675";// 是阿，這也是他的意思…
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7685";// 請都逃離吧！將此地封印！
							}
							_hardin_tak = 36;// 進入7階段召喚(7/12)
							break;
						}
					}
					break;

				case 36:// 進入7階段召喚(7/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 6) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_07[_random.nextInt(_attack_07.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							_hardin_tak = 37;// 進入7階段檢查
							_mode12 = 7;// 模式7
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8712";// 8712 第六個封印已解除 [6/12]
					}
					break;

				case 37:// 進入7階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 38;// 進入8階段召喚(8/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7694";// 那些也是以部分計劃內容所行動啊。呼呼。。
							break;
						case 30:
							wmsg = "$7597: $7695";// 好吧，這都是那位計劃之中呢。。
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7693";// 停止呼吸，快逃離這邊！那熱氣將肺溶化之前。快！
							}
							_hardin_tak = 38;// 進入8階段召喚(8/12)
							break;
						}
					}
					break;

				case 38:// 進入8階段召喚(8/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 7) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_08[_random.nextInt(_attack_08.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							L1SpawnUtil.spawnRx(loc, 91285, _showid, 10, 240);// 91285
																				// 魔界的伊弗利特
							_hardin_tak = 39;// 進入8階段檢查
							_mode12 = 8;// 模式8
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8713";// 8713 第七個封印已解除 [7/12]
					}
					break;

				case 39:// 進入8階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 40;// 進入9階段召喚(9/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7589";// 這玩法是甚麼…
							break;
						case 30:
							wmsg = "$7597: $7590";// 所有事情都快煩死了！
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7681";// 我太高估你們了嗎…
							}
							_hardin_tak = 40;// 進入9階段召喚(9/12)
							break;
						}
					}
					break;

				case 40:// 進入9階段召喚(9/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 8) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_09[_random.nextInt(_attack_09.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							L1SpawnUtil.spawnRx(loc, 91293, _showid, 10, 240);// 91293
																				// 魔界的不死鳥
							_hardin_tak = 41;// 進入9階段檢查
							_mode12 = 9;// 模式9
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8706";// 8706 第八個封印解除了 [8/12]
					}
					break;

				case 41:// 進入9階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 42;// 進入10階段召喚(10/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7588";// 消滅！全部都消滅吧！
							break;
						case 30:
							wmsg = "$7597: $7584";// 我決不怕火！決！不！
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7684";// 7684 啊！魔法陣快消失了！
							}
							_hardin_tak = 42;// 進入10階段召喚(10/12)
							break;
						}
					}
					break;

				case 42:// 進入10階段召喚(10/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 9) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_10[_random.nextInt(_attack_10.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							L1SpawnUtil.spawnRx(loc, 91289, _showid, 10, 240);// 91289
																				// 魔界的幻象眼魔
							_hardin_tak = 43;// 進入10階段檢查
							_mode12 = 10;// 模式10
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8714";// 8714 第九個封印已解除 [9/12]
					}
					break;

				case 43:// 進入10階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 44;// 進入11階段召喚(11/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7663";// 辛苦你們了。是個比預期好的成果。呵呵呵。
							break;
						case 30:
							wmsg = "$7597: $7645";// 呼…消失吧！！
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7683";// 7683 這次是失敗！請都逃離！將此地封印！
							}
							_hardin_tak = 44;// 進入11階段召喚(11/12)
							break;
						}
					}
					break;

				case 44:// 進入11階段召喚(11/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 10) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_11[_random.nextInt(_attack_11.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							L1SpawnUtil.spawnRx(loc, 91292, _showid, 10, 240);// 91292
																				// 魔界的巨大牛人
							_hardin_tak = 45;// 進入11階段檢查
							_mode12 = 11;// 模式11
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8715";// 8715 第十個封印已解除 [10/12]
					}
					break;

				case 45:// 進入11階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 46;// 進入12階段召喚(12/12)

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7700";// 為什麼這麼快就開始了呢！
							break;
						case 30:
							wmsg = "$7597: $7701";// 是誰在叫我?呵呵呵！
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7682";// 全都逃走吧！再這樣下去可能會沒命的！
							}
							_hardin_tak = 46;// 進入12階段召喚(12/12)
							break;
						}
					}
					break;

				case 46:// 進入12階段召喚(12/12)
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 11) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_12[_random.nextInt(_attack_12.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							L1SpawnUtil.spawnRx(loc, 91298, _showid, 10, 240);// 91298
																				// 魔界的殭屍王
							_hardin_tak = 47;// 進入12階段檢查
							_mode12 = 12;// 模式12
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8716";// 8716 第十一個封印已解除 [11/12]
					}
					break;

				case 47:// 進入12階段檢查
					_time12++;
					if (_npclist.size() <= 0) {
						_hardin_tak = 48;// 進入最後階段

					} else {
						switch (_time12) {
						case 5:
							wmsg = "$7597: $7700";// 為什麼這麼快就開始了呢！
							break;
						case 30:
							wmsg = "$7597: $7701";// 是誰在叫我?呵呵呵！
							break;
						case 120:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7653";// 邪惡氣息又擁擠過來了！請準備！
							}
							break;
						case 180:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7811";// 就算是因為照這樣做而任務失敗的話，別氣餒！讓我給你打打氣吧！
							}
							break;
						case 240:
							if (_npclist.size() >= 4) {
								wmsg = "$7597: $7682";// 全都逃走吧！再這樣下去可能會沒命的！
							}
							_hardin_tak = 48;// 進入最後階段
							break;
						}
					}
					break;

				case 48:// 進入最後階段
					_time_over++;
					if (_time_over == _sleep) {
						if (_mode12 == 12) {
							_npclist.clear();
							L1Location loc = new L1Location(32707, 32846, _mapid);
							for (int i = 0; i < _count_spawn; i++) {
								switch (_random.nextInt(4)) {
								case 0:
									loc = _loc1;
									break;
								case 1:
									loc = _loc2;
									break;
								case 2:
									loc = _loc3;
									break;
								case 3:
									loc = _loc4;
									break;
								}
								final int npcid = _attack_12[_random.nextInt(_attack_12.length)];
								final int rr = _random.nextInt(2) + 2;
								final L1NpcInstance mob = L1SpawnUtil.spawnRx(loc, npcid, _showid, rr, 240);
								if (mob != null) {
									add(mob);
								}
							}
							_hardin_tak = 49;// 進入13階段檢查
							_mode12 = 13;// 模式13
							_time12 = 0;
						}
						_time_over = 0;
						outWindows2();

					} else {
						wmsg = "$8717";// 8717 最後一個封印已解除 [12/12]
					}
					break;

				case 49:
					_time12++;
					switch (_time12) {
					case 1:
						wmsg = "$7597: $8705";// 8705 睡覺醒了我… 我… 好累…
						break;
					case 5:
						wmsg = "$7597: $7654";// 請大家緊張一下吧！最終的恐懼就要登場了了！
						break;
					case 10:
						_spawn = checkBoss();// 召喚BOSS
						if (_spawn > 0) {
							if (_spawn >= 32) {
								_spawn = _spawn - 32;
								wmsg = "$7597: $7668";// 7668 【賽尼斯】終於來啦！
							}
							if (_spawn >= 16) {// 黑翼賽尼斯(秘譚)
								_spawn = _spawn - 16;// 黑翼賽尼斯
								wmsg = "$7597: $7660";// 7660
														// 阿…不是阿！【賽尼斯】怎麼會這樣呢！【歐林】是發生什麼事了嗎?
							}
						}
						break;
					case 15:
						if (_spawn > 0) {
							if (_spawn >= 8) {
								_spawn = _spawn - 8;
								wmsg = "$7597: $7674";// 7674 這個是你們的作品嗎?
							}
							if (_spawn >= 4) {
								_spawn = _spawn - 4;
								wmsg = "$7597: $7661";// 7661【火焰之影】！ 是…你的陰謀…
							}
						}
						break;
					case 20:
						if (_spawn > 0) {
							if (_spawn == 2) {
								wmsg = "$7597: $7689";// 7689 【哈汀】…像羊一樣的傢伙，太強了！
							}
						}
						break;
					case 35:
						if (_spawn > 0) {
							if (_spawn == 2) {
								wmsg = "$7597: $7705";// 7705 【巴風特】，請回到主人身邊吧。
							}
						}
						break;

					case 40:
						wmsg = "$7597: $7705";// 8703 哈汀：呼…終於擋住了。加油！
						break;

					case 300:
						_npclist.clear();
						break;
					}

					if (_npclist.size() <= 0) {
						if (_boss_a_death && _boss_b_death) {
							_hardin_tak = 50;// 進入黑門作業
						}
					}
					break;

				case 50:// 進入黑門作業
					_time_over++;
					switch (_time_over) {
					case 1:
						// 召喚小光圈
						spawn_light();
						wmsg = "$7597: $8707";// 8707 門開了嗎！！
						for (final L1DoorInstance door : _doors) {
							door.open();
						}
						outWindows2();
						break;
					case 15:
						wmsg = "$7597: $7687";// 7687 大家快逃啊！我將會把此地封印的！
						break;
					case 30:
						if (_dooropens.size() > 0) {
							for (final L1FieldObjectInstance door : _dooropens) {
								door.deleteMe();
							}
							_dooropens.clear();
						}
						for (final L1DoorInstance door : _doors) {
							door.close();
						}
						_hardin_tak = 51;// 隊員與地標重疊檢查
						break;
					}
					if ((_time_over >= 1) && (_time_over <= 15)) {
						// 開起黑門
						openBookDoor();
					} else if ((_time_over > 15) && (_time_over <= 29)) {
						// 關閉黑門
						closeBookDoor();
					}
					// 隊員與地標重疊檢查
					spawn_light_check();
					break;

				case 51:// 隊員與地標重疊檢查
					spawn_light_check();
					break;

				case 52:// 中央光圈的檢查
					spawn_light_check2();
					break;

				case 53:// 召喚禮物
					// System.out.println("召喚禮物");
					for (final L1PcInstance pc : _party.getMemberList()) {
						if (pc.getMapId() == _mapid) {
							final L1GroundInventory gInventory = World.get().getInventory(pc.getLocation());
							// 哈汀的秘密袋子
							final L1ItemInstance item = ItemTable.get().createItem(49314);
							item.setEnchantLevel(0);
							item.setCount(1);
							item.set_showId(_showid);
							gInventory.storeItem(item);

							// 將任務設置為結束
							QuestClass.get().endQuest(pc, Chapter01.QUEST.get_id());
						}
					}
					_hardin_tak = 54;
					break;

				case 54:// 禮物
					Thread.sleep(10 * 1000);
					_hardin_tak = 55;
					break;

				case 55:// 任務結束
					_start = false;
					break;
				}

				if (DOOR_3 && (_hardin_tak < 19)) {
					for (final L1PcInstance pc : _party.getMemberList()) {
						if (pc.getMapId() == _mapid) {
							if (!_party.isLeader(pc)) {// 非隊長
								if ((pc.getX() == 32808) && (pc.getY() == 32838)) {
									// 傳送位置
									L1Teleport.teleport(pc, 32788, 32822, (short) _mapid, 4, true);
								}
							}
						}
					}
				}

				if (msg != null) {
					outMsg(msg);
				}

				if (wmsg != null) {
					// 7650 賽尼斯 7579 哈汀
					outMsgWindows(wmsg);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * BOSS召喚規則<BR>
	 * 1:鐮刀死神的使者<BR>
	 * 2:巴風特<BR>
	 * 4:NPC火焰之影<BR>
	 * 8:NPC炎魔<BR>
	 * 16:黑翼賽尼斯(秘譚)<BR>
	 * 32:賽尼斯(秘譚)<BR>
	 */
	private int checkBoss() {
		int mode = 0;
		try {
			final L1Location loc_a = new L1Location(32711, 32842, _mapid);
			final L1Location loc_b = new L1Location(32712, 32846, _mapid);
			L1NpcInstance npc = null;
			L1NpcInstance s_npc = null;
			// 最終頭目-鐮刀死神的使者(秘譚)
			if ((_count_01 >= 4) && (_count_02 >= 4) && (_count_03 >= 4) && (_count_04 >= 4)
					&& (_count_05 >= 4) && (_count_06 >= 4) && (_count_07 >= 4) && (_count_08 >= 4)
					&& (_count_09 >= 4) && (_count_10 >= 4) && (_count_11 >= 4) && (_count_12 >= 4)) {
				npc = L1SpawnUtil.spawn(91290, loc_a, 5, _showid);// 91290
																	// 鐮刀死神的使者
				mode += 1;
				_log.info("副本編號: " + _showid + " 召喚 鐮刀死神的使者");

				// 最終頭目-巴風特(秘譚)
			} else if ((_count_01 == 0) && (_count_02 == 0) && (_count_03 == 0) && (_count_04 == 0)
					&& (_count_05 == 0) && (_count_06 == 0) && (_count_07 == 0) && (_count_08 == 0)
					&& (_count_09 == 0) && (_count_10 == 0) && (_count_11 == 0) && (_count_12 == 0)) {
				npc = L1SpawnUtil.spawn(91294, loc_a, 5, _showid);// 91294 巴風特
				mode += 2;
				_log.info("副本編號: " + _showid + " 召喚 巴風特");

			} else {
				npc = L1SpawnUtil.spawn(91294, loc_a, 5, _showid);// 91294 巴風特
				mode += 2;
				_log.info("副本編號: " + _showid + " 召喚 巴風特");
			}

			if (_cerenis_tak == 4) {// 黑翼賽尼斯(秘譚)
				s_npc = L1SpawnUtil.spawn(91295, loc_b, 5, _showid);// 91295
																	// 黑翼賽尼斯
				_log.info("副本編號: " + _showid + " 召喚 黑翼賽尼斯");
				mode += 16;
				if (_mode_s == 3) {
					final L1Location loc_c = new L1Location(32717, 32864, _mapid);
					// NPC炎魔
					L1SpawnUtil.spawn(91344, loc_c, 0, _showid);// 91344 炎魔
					_log.info("副本編號: " + _showid + " 召喚 炎魔NPC");
					mode += 8;
				}

			} else {// 賽尼斯(秘譚)
				s_npc = L1SpawnUtil.spawn(91296, loc_b, 5, _showid);// 91296 賽尼斯
				_log.info("副本編號: " + _showid + " 召喚 賽尼斯");
				s_npc.setHate(npc, 10);

				mode += 32;
				if (_mode_s == 3) {
					final L1Location loc_c = new L1Location(32717, 32864, _mapid);
					// NPC火焰之影
					L1SpawnUtil.spawn(91343, loc_c, 0, _showid);// 91343 火焰之影
					_log.info("副本編號: " + _showid + " 召喚 火焰之影NPC");
					mode += 4;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return mode;
	}

	/**
	 * 關閉黑門<BR>
	 * 32703 32866 - 32711 32866<BR>
	 * 32703 32872 - 32709 32872<BR>
	 */
	private void closeBookDoor() {
		for (final L1FieldObjectInstance door : _dooropens) {
			final int h = door.targetDirection(door.getHomeX(), door.getHomeY());
			setDoorMove(h, door);
		}
	}

	/**
	 * 開起黑門<BR>
	 * 32703 32866 - 32711 32866<BR>
	 * 32703 32872 - 32709 32872<BR>
	 */
	private void openBookDoor() {
		if (_dooropens.size() <= 0) {
			// 移動用
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32703, 32866, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32704, 32866, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32705, 32866, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32706, 32866, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32707, 32866, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32708, 32866, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32709, 32866, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32710, 32866, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32711, 32866, _mapid, 0));
			// 32703 32872 - 32709 32872
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32703, 32872, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32704, 32872, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32705, 32872, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32706, 32872, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32707, 32872, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32708, 32872, _mapid, 0));
			_dooropens.add(L1SpawnUtil.spawn(_showid, 7536, 32709, 32872, _mapid, 0));
		}

		for (final L1FieldObjectInstance door : _dooropens) {
			final int h = _random.nextInt(8);
			setDoorMove(h, door);
		}
	}

	/**
	 * 往指定面向移動1格(無障礙設置)
	 */
	private void setDoorMove(final int heading, final L1FieldObjectInstance door) {
		if (heading >= 0) {
			int locx = door.getX();
			int locy = door.getY();
			locx += _h_x[heading];
			locy += _h_y[heading];
			door.setHeading(5);

			door.setX(locx);
			door.setY(locy);
			door.broadcastPacketAll(new S_MoveCharPacket(door));
		}
	}

	/**
	 * 檢查第1個門是否達到啟動條件
	 * 
	 * @return
	 */
	private boolean check_loc1() {
		try {
			_alt2_02 = 0;
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					if (!_party.isLeader(pc)) {// 非隊長
						if ((pc.getX() == 32668) && (pc.getY() == 32817)) {// NO
																			// 1
																			// 上
							_alt2_02 += 1;
						}
						if ((pc.getX() == 32666) && (pc.getY() == 32817)) {// NO
																			// 1
																			// 左
							_alt2_02 += 1;
						}
						if ((pc.getX() == 32668) && (pc.getY() == 32819)) {// NO
																			// 1
																			// 右
							_alt2_02 += 1;
						}
						if ((pc.getX() == 32666) && (pc.getY() == 32819)) {// NO
																			// 1
																			// 下
							_alt2_02 += 1;
						}
					}
				}
			}
			if (_alt2_02 >= _user_count) {
				return true;// 全部定位
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 檢查第2個門是否達到啟動條件
	 * 
	 * @return
	 */
	private boolean check_loc2() {
		try {
			_alt2_02 = 0;
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					if (!_party.isLeader(pc)) {// 非隊長
						if ((pc.getX() == 32712) && (pc.getY() == 32793)) {// NO
																			// 2
																			// 上
							_alt2_02 += 1;
						}
						if ((pc.getX() == 32703) && (pc.getY() == 32791)) {// NO
																			// 2
																			// 左
							_alt2_02 += 1;
						}
						if ((pc.getX() == 32710) && (pc.getY() == 32803)) {// NO
																			// 2
																			// 右
							_alt2_02 += 1;
						}
						if ((pc.getX() == 32703) && (pc.getY() == 32800)) {// NO
																			// 2
																			// 下
							_alt2_02 += 1;
						}
					}
				}
			}
			if (_alt2_02 >= _user_count) {
				return true;// 全部定位
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 檢查第3個門是否達到啟動條件
	 * 
	 * @return
	 */
	private boolean check_loc3() {
		try {
			_alt2_02 = 0;
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					if (!_party.isLeader(pc)) {// 非隊長
						if ((pc.getX() == 32809) && (pc.getY() == 32837)) {// NO
																			// 4
																			// 上
							_alt2_02 += 1;
						}
						if ((pc.getX() == 32807) && (pc.getY() == 32837)) {// NO
																			// 4
																			// 左
							_alt2_02 += 1;
						}
						if ((pc.getX() == 32809) && (pc.getY() == 32839)) {// NO
																			// 4
																			// 右
							_alt2_02 += 1;
						}
						if ((pc.getX() == 32807) && (pc.getY() == 32839)) {// NO
																			// 4
																			// 下
							_alt2_02 += 1;
						}
					}
				}
			}
			if (_alt2_02 >= _user_count) {
				return true;// 全部定位
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 檢查第4個門是否達到啟動條件
	 * 
	 * @return
	 */
	private boolean check_loc4() {
		try {
			if (_npclist.size() <= 0) {
				return true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 傳送回說話之島
	 */
	private void outUser() {
		try {
			_start = false;
			if (_party.getNumOfMembers() <= 0) {
				final ConcurrentHashMap<Integer, L1Object> objs = World.get().getVisibleObjects(_mapid);
				for (final L1Object obj : objs.values()) {
					if (obj instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) obj;
						if ((pc.getMapId() == _mapid) && (pc.get_showId() == _showid)) {
							L1Teleport.teleport(pc, 32594, 32917, (short) 0, 4, true);
							// 將任務設置為結束
							QuestClass.get().endQuest(pc, Chapter01.QUEST.get_id());
						}
					}
				}

			} else {
				for (final L1PcInstance pc : _party.getMemberList()) {
					if (pc.getMapId() == _mapid) {
						L1Teleport.teleport(pc, 32594, 32917, (short) 0, 4, true);
					}
					// 將任務設置為結束
					QuestClass.get().endQuest(pc, Chapter01.QUEST.get_id());
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _user_02 = 0;
	private int _alt2_02 = 0;

	/**
	 * 隊員進入地監傳送 X:32725 Y:32723 X:32727 Y:32725
	 * 
	 * @return
	 */
	private boolean all_teleport() {
		try {
			_user_02 = 0;
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					_user_02++;
					if ((pc.getX() == 32726) && (pc.getY() == 32724)) {
						_alt2_02++;
						if (!_party.isLeader(pc)) {// 非隊長
							// 傳送位置
							L1Teleport.teleport(pc, 32664, 32790, (short) _mapid, 4, true);
						} else {
							L1Teleport.teleport(pc, 32732, 32930, (short) _mapid, 2, true);
						}

					}
				}
			}
			if (_user_02 == _alt2_02) {
				return true;// 全部傳送完成
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 檢察隊員回應(啟動第1個傳送光圈)
	 * 
	 * @return
	 */
	private boolean all_ok() {
		try {
			int user_01 = 0;
			int alt2_01 = 0;
			for (final L1PcInstance pc : _party.getMemberList()) {
				if (pc.getMapId() == _mapid) {
					user_01++;
					if (pc.get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
						alt2_01++;
					}
				}
			}
			if (user_01 == alt2_01) {
				return true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	private int _leaderTime = 0;// 賽尼斯時間計算

	/**
	 * 隊長檢查
	 */
	private void getLeader() {
		try {
			if (_msgInLeader) {// 隊長已經離開
				return;
			}
			if (_party.getLeader().isDead()) {// 隊長死亡
				_cerenis_tak = 0;
				return;
			}
			if (_party.getLeader().getMapId() != _mapid) {// 隊長離開副本
				_cerenis_tak = 0;
				return;
			}
			if (_cerenis == null) {
				for (final L1Object object : World.get().getVisibleObjects(_party.getLeader())) {
					if (object instanceof L1NpcInstance) {
						final L1NpcInstance npc = (L1NpcInstance) object;
						if (npc.getNpcId() == 91297) {// 賽尼斯
							_cerenis = npc;
						}
					}
				}

			} else {
				_leaderTime++;
				String msg = null;
				switch (_leaderTime) {
				case 4:
					msg = "$7607";// 不愧是，【歐林】來了。
					break;
				case 12:
					msg = "$7608";// 你的角色是從危險中保護我。總之應該沒甚麼事，【歐林】將會很快就無聊的。
					break;
				case 24:
					msg = "$7609";// 這陣子蠻無聊的，要不要聽我說話。
					_party.getLeader().set_actionId(-1);
					break;
				case 32:
					msg = "$7610";// 要的話就請用【alt + 2】，不要的話就用【alt + 4】來表示。了解嗎?
					break;
				case 40:
					if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
						_cerenis_tak = 1;
					} else if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Think) {// Alt+4
						msg = "$7614";// 那麼，從現在開始要正式開始吧。
						_cerenis_tak = 2;
					} else {
						_cerenis_tak = 0;
					}
					break;
				case 42:
					if (_cerenis_tak == 1) {
						msg = "$7611";// 這是個危險的地方，希望你能夠獲得外人所無法經易拿到的裝備。
					}
					break;
				case 44:
					if (_cerenis_tak == 1) {
						msg = "$7573";// 在這邊生活很久了！實在是難以置信！
					}
					break;
				case 46:
					if (_cerenis_tak == 1) {
						msg = "$7576";// 這件事到底含有什麼樣的意義呢?
					}
					break;
				case 48:
					if (_cerenis_tak == 1) {
						msg = "$7578";// 母親的名字叫做 【伊娃】…不過父親的名字…恩？叫什麼呢？
					}
					break;
				case 50:
					if (_cerenis_tak == 1) {
						msg = "$7579";// 我有喜歡的人了，不過是誰還是個秘密。
					}
					break;
				case 52:
					msg = "$7580";// 我來下令請出現吧！ 【魔法球！】， 呼呼…果然還是不行 ^^;
					break;
				case 54:
					msg = "$7581";// 生命之水！時光之風！將會讓我變 【成熟】！ … 呼… 就說嘛 ^^;;
					break;
				case 56:
					if (_cerenis_tak == 1) {
						if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
							msg = "$7613";// 那麼我就提供給你們只有你們才看的到的裝置，希望你們能夠好好使用。
							_cerenis_tak = 1;

						} else if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Think) {// Alt+4
							msg = "$7614";// 那麼，從現在開始要正式開始吧。
							_cerenis_tak = 2;
						} else {
							msg = "$7612";// 要是你能理解的話，希望能給點回應！
							_cerenis_tak = 0;
						}
					} else {
						msg = "$7612";// 要是你能理解的話，希望能給點回應！
					}
					_party.getLeader().set_actionId(-1);
					break;
				case 58:
					if (_cerenis_tak == 1) {
						msg = "$7583";// 【伊娃】的女兒絕對無法體會到口渴的感覺。
					}
					break;
				case 60:
					if (_cerenis_tak == 1) {
						msg = "$7585";// 難道沒有可以讓我開心的方法嗎?
					}
					break;
				case 62:
					if (_cerenis_tak == 1) {
						msg = "$7586";// 呼…要是今天過去的話，又會到來沒意義的日子吧？以明天的名稱…
					}
					break;
				case 64:
					if (_cerenis_tak == 1) {
						msg = "$7616";// 【哈汀】對於我有什麼想法嗎？【歐林】知道嗎？
					}
					_party.getLeader().set_actionId(-1);
					break;
				case 72:
					if (_cerenis_tak == 1) {
						if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
							msg = "$7615";// 那麼，希望你趕緊把事情搞定到這邊來吧。
							_cerenis_tak = 1;

						} else if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Think) {// Alt+4
							msg = "$7622";// 順利開始了嗎?以後也請多多指教。
							_cerenis_tak = 2;
						} else {
							msg = "$7617";// 【歐林】… 你已經忘記要怎麼做了嗎?
							_cerenis_tak = 0;
						}
					} else {
						msg = "$7617";// 【歐林】… 你已經忘記要怎麼做了嗎?
					}
					break;
				case 74:
					// 召喚妖魔
					L1SpawnUtil.spawn(_party.getLeader(), _spawn_01[_random.nextInt(_spawn_01.length)],
							_random.nextInt(4) + 4, 120);
					msg = "$7623";// 什麼阿！ 不就是【妖魔】他們嘛！ 之前變成白色的妖魔也來了！！ 【歐林】就拜託了！
					break;
				case 76:
					// 召喚妖魔
					for (int i = 0; i < 2; i++) {
						final int npcid = _spawn_01[_random.nextInt(_spawn_01.length)];
						final int rr = _random.nextInt(4) + 4;
						L1SpawnUtil.spawn(_party.getLeader(), npcid, rr, 120);
					}
					msg = "$7587";// 應該把這世間上的【妖魔】都給消滅！
					break;
				case 78:
					// 召喚妖魔
					for (int i = 0; i < 3; i++) {
						final int npcid = _spawn_01[_random.nextInt(_spawn_01.length)];
						final int rr = _random.nextInt(4) + 4;
						L1SpawnUtil.spawn(_party.getLeader(), npcid, rr, 120);
					}
					msg = "$7572";// 【歐！林！】該你上場了！
					break;
				case 80:
					msg = "$7577";// 使用【解毒術】的話，就可讓妖魔變白！！！
					break;
				case 82:
					// 召喚妖魔
					for (int i = 0; i < 4; i++) {
						final int npcid = _spawn_01[_random.nextInt(_spawn_01.length)];
						final int rr = _random.nextInt(4) + 4;
						L1SpawnUtil.spawn(_party.getLeader(), npcid, rr, 120);
					}
					break;
				case 84:
					// 召喚妖魔
					for (int i = 0; i < 5; i++) {
						final int npcid = _spawn_01[_random.nextInt(_spawn_01.length)];
						final int rr = _random.nextInt(4) + 4;
						L1SpawnUtil.spawn(_party.getLeader(), npcid, rr, 120);
					}
					break;
				case 88:
					msg = "$7618";// 底下那些傢伙是笨蛋嗎?為什麼這麼慢呢！
					_party.getLeader().set_actionId(-1);
					break;
				case 96:
					int count = 0;
					for (final L1Object object : World.get().getVisibleObjects(_party.getLeader())) {
						if (object instanceof L1MonsterInstance) {
							if (!((L1MonsterInstance) object).isDead()) {
								count++;
							}
						}
					}
					if (count >= 5) {
						if (_random.nextBoolean()) {
							msg = "$7624";// 【歐林】你因為愄懼，所以變的僵硬是嗎?！
						} else {
							msg = "$7574";// 【歐林】這個笨蛋
						}

					} else {
						if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
							msg = "$7620";// 什麼阿…是我太趕了嗎…真的是很抱歉 。。。'
							_cerenis_tak = 1;

						} else if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Think) {// Alt+4
							msg = "$7631";// 【歐林！】將打嗝停住吧！笨蛋！
							_cerenis_tak = 2;
						} else {
							msg = "$7633";// 【歐林！！！！！】，你！！
						}
					}
					break;
				case 104:
					msg = "$7625";// 底下那些傢伙還真的是給他有夠笨的！這些比烏龜還要遲鈍的笨蛋！
					_party.getLeader().set_actionId(-1);
					break;
				case 112:
					if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
						msg = "$7629";// 阿阿。。雖然我還有點耐心，但是我還是希望能再快一點…
						_cerenis_tak = 1;

					} else if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Think) {// Alt+4
						msg = "$7627";// 呼…這些笨蛋！可不可以快一點阿！不要再考驗我的耐心了…'
						_cerenis_tak = 2;

					} else {
						msg = "$7626";// 女生在生氣的時候！要是能看到任何反應就好了！
					}
					_party.getLeader().set_actionId(-1);
					break;
				case 120:
					if (_cerenis_tak == 1) {
						msg = "$7634";// 我這樣是不是更自私自利的樣子…'

					} else if (_cerenis_tak == 2) {
						msg = "$7635";// 很好！把守門人給處置掉，倒戈到我們這邊吧。
					}
					_party.getLeader().set_actionId(-1);
					break;
				case 128:
					msg = "$7636";// 呃…集中力減弱了…哼…恩…。
					_party.getLeader().set_actionId(-1);
					break;
				case 136:
					msg = "$7637";// 這是最後一個了。 【歐林】 給我一些能量吧！
					_party.getLeader().set_actionId(-1);
					break;
				case 140:
					msg = "$7638";// 【歐林！！！！！】
					_party.getLeader().set_actionId(-1);
					break;
				case 148:
					if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
						msg = "$7641";// 呼…是阿…就當你是答應了…
						_cerenis_tak = 1;

					} else if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Think) {// Alt+4
						msg = "$7639";// 這是什麼阿！！！
						_cerenis_tak = 2;

					} else {
						msg = "$7640";// 難道你們難以伸出援手嗎?！
					}
					_party.getLeader().set_actionId(-1);
					break;
				case 156:
					if (_cerenis_tak == 1) {
						msg = "$7643";// 【歐林】都已經準備好了， 先傳送給【哈汀】吧。
						_cerenis_tak = 1;

					} else if (_cerenis_tak == 2) {
						msg = "$7642";// 這些笨蛋似乎是要等到我生氣！哼哼！
						_cerenis_tak = 2;

					} else {
						msg = "$7644";// 【歐林】，是在渺視我嗎?
						_party.getLeader().set_actionId(-1);
						_cerenis_tak = 0;
					}
					break;
				case 164:
					if (_cerenis_tak == 1) {
						if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
							msg = "$7646";// 呼…對嘛，不要在那邊撒尿，消失吧！
							_cerenis_tak = 1;
						}

					} else if (_cerenis_tak == 2) {
						if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
							msg = "$7645";// 呼…消失吧！！
							_cerenis_tak = 2;
						}

					} else {
						if (_party.getLeader().get_actionId() == ActionCodes.ACTION_Cheer) {// Alt+2
							msg = "$7568"; // 【歐！林！】我會詛咒你的！
							_cerenis_tak = 4;
							_log.info("副本編號: " + _qid + "-" + _showid + " 達成黑翼賽尼斯條件!");

						} else {
							_cerenis_tak = 0;
						}
					}
					// 【歐林】也過來我們這邊了，儀式就要開始了，趕緊過來這邊吧！
					_party.getLeader().sendPackets(new S_PacketBoxGree(0x02, "$7647"));
					break;
				}
				if (_time > 164) {
					if (((_time % 6) == 0) && (_cerenis_tak == 1)) {
						final int[] m = new int[] { 7576, // 7576
															// 這件事到底含有什麼樣的意義呢?
								7578, // 7578 母親的名字叫做 【伊娃】…不過父親的名字…恩？叫什麼呢？
								7579, // 7579 我有喜歡的人了，不過是誰還是個秘密。
								7581, // 7581 生命之水！時光之風！將會讓我變 【成熟】！ … 呼… 就說嘛
										// ^^;;
								7582, // 7582 我不喜歡甜甜的東西。但是…血的味道…
								7583, // 7583 【伊娃】的女兒絕對無法體會到口渴的感覺。
								7584, // 7584 我決不怕火！決！不！
								7585, // 7585 難道沒有可以讓我開心的方法嗎?
								7586, // 7586 呼…要是今天過去的話，又會到來沒意義的日子吧？以明天的名稱…
								7589, // 7589 這玩法是甚麼…
								7590, // 7590 所有事情都快煩死了！
								7592, // 7592 應該有人會幫我填補空虛的一半。
								7594, // 7594 學生與笨蛋是相同的意思嗎?
								7595,// 7595 嗯…爸爸的形象…好像很巨大…的感覺？
						};
						msg = "$" + m[_random.nextInt(m.length)];
					}
				}
				if (DOOR_4OPEN) {
					if (!_msgInLeader) {
						for (final L1PcInstance pc : _party.getMemberList()) {
							if ((pc.getMapId() == _mapid) && !_party.isLeader(pc)) {
								// 傳送隊長位置
								L1Teleport.teleport(_party.getLeader(), pc.getX(), pc.getY(), (short) _mapid,
										2, true);
								// 傳送至隊友
								_msgInLeader = true;
							}
						}
					}
				}

				if (msg != null) {
					_party.getLeader().sendPackets(new S_NpcChat(_cerenis, msg));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
