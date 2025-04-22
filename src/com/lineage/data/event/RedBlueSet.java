package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

import java.util.Collection;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.ArrayList;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_PackBoxMaptime;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.serverpackets.S_Resurrection;
import com.lineage.server.serverpackets.S_CharVisualUpdate;

/**
 * 
 * 自創陣營戰統
 * 
 * @author Erics4179
 * 
 */

public class RedBlueSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(RedBlueSet.class);

	public static int count1 = 0;
	public static int count2 = 0;

	public static int step1 = 0;
	public static int step2 = 0;

	public static int alltime1 = ConfigOther.RedBlueTime_all;
	public static int cleartime1 = ConfigOther.RedBlueTime_clear;

	public static int alltime2 = ConfigOther.RedBlueTime_all;
	public static int cleartime2 = ConfigOther.RedBlueTime_clear;

	public static ArrayList<L1PcInstance> _room1pc = new ArrayList<L1PcInstance>();
	public static ArrayList<L1PcInstance> _room1red = new ArrayList<L1PcInstance>();
	public static ArrayList<L1PcInstance> _room1blue = new ArrayList<L1PcInstance>();
	public static ArrayList<L1PcInstance> _room1win = new ArrayList<L1PcInstance>();

	public static ArrayList<L1PcInstance> _room2pc = new ArrayList<L1PcInstance>();
	public static ArrayList<L1PcInstance> _room2red = new ArrayList<L1PcInstance>();
	public static ArrayList<L1PcInstance> _room2blue = new ArrayList<L1PcInstance>();
	public static ArrayList<L1PcInstance> _room2win = new ArrayList<L1PcInstance>();

	public static boolean START = false;

	private RedBlueSet() {
	}

	public static EventExecutor get() {
		return new RedBlueSet();
	}

	@Override
	public void execute(L1Event event) {
		try {
			START = true;

			RedBlueTimer time = new RedBlueTimer();
			time.start();

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void broadcastPacketRoom(final ServerBasePacket packet,
			ArrayList<L1PcInstance> _team) {
		try {
			for (int i = 0; i < _team.size(); i++) {
				L1PcInstance pc = _team.get(i);
				if (pc != null)
					pc.sendPackets(packet);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public int getPoint(ArrayList<L1PcInstance> _point) {
		int point = 0;
		for (int i = 0; i < _point.size(); i++) {
			L1PcInstance pc = _point.get(i);
			if (pc != null) {
				if (pc.get_redbluepoint() > 0) {
					point += pc.get_redbluepoint();
				}
			}
		}
		return point;
	}

	public void GameReset(ArrayList<L1PcInstance> _all) {
		try {
			for (int i = 0; i < _all.size(); i++) {
				L1PcInstance pc = _all.get(i);
				if (pc != null) {
					pc.set_redbluejoin(0);
					pc.set_redblueleader(0);
					pc.set_redbluepoint(0);
					pc.set_redblueroom(0);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void Resurrect(ArrayList<L1PcInstance> _all) {
		try {
			for (int i = 0; i < _all.size(); i++) {
				L1PcInstance pc = _all.get(i);
				if (pc != null) {
					if (pc.isDead()) {
						pc.resurrect(1);
						pc.setCurrentHp(1);
						pc.startHpRegeneration();
						pc.startMpRegeneration();
						pc.stopPcDeleteTimer();
						pc.sendPacketsAll(new S_Resurrection(pc, pc, 0));
						pc.sendPacketsAll(new S_CharVisualUpdate(pc));
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void OutRoom(ArrayList<L1PcInstance> _all) {
		try {
			for (int i = 0; i < _all.size(); i++) {
				L1PcInstance pc = _all.get(i);
				if (pc != null) {
					L1Teleport.teleport(pc, ConfigOther.RedBlueEnd_map[0],
							ConfigOther.RedBlueEnd_map[1],
							(short) ConfigOther.RedBlueEnd_map[2],
							pc.getHeading(), true);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void BonusRoom(ArrayList<L1PcInstance> _team) {
		try {
			for (int i = 0; i < _team.size(); i++) {
				L1PcInstance pc = _team.get(i);
				if (pc != null) {
					if (ConfigOther.RedBlueBonus_itemid != 0
							&& ConfigOther.RedBlueBonus_count != 0) {
						pc.getInventory().storeItem(
								ConfigOther.RedBlueBonus_itemid,
								ConfigOther.RedBlueBonus_count);
						pc.sendPackets(new S_ServerMessage("\\aH頒發 活動獎勵。"));
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private class RedBlueTimer extends TimerTask {

		private ScheduledFuture<?> _timer;

		public void start() {
			final int timeMillis = 1000;
			_timer = GeneralThreadPool.get().scheduleAtFixedRate(this,
					timeMillis, timeMillis);
		}

		@Override
		public void run() {
			try {
				Collection<L1PcInstance> all = World.get().getAllPlayers();
				if (all.isEmpty()) {
					return;
				}

				//
				if (step2 == 4) {
					count2++;
					cleartime2--;
					if (cleartime2 <= 0) {
						count2 = 0;
						step2 = 0;
						for (L1PcInstance talkto : all) {
							if (talkto != null) {
								talkto.sendPackets(new S_BlueMessage(166,
										"\\f2無界陣營戰 - 清理完畢"));
							}
						}
					}
				}

				if (step2 == 3) {
					count2++;
					if (count2 == 5) {
						broadcastPacketRoom(new S_BlueMessage(166, "\\f2復仇者聯盟"
								+ getPoint(_room2red) + " vs  " + "暗殺者組織"
								+ getPoint(_room2blue)), _room2pc);
					} else if (count2 == 10) {
						if (getPoint(_room2red) == getPoint(_room2blue)) {
							broadcastPacketRoom(new S_BlueMessage(166,
									"\\f2雙 方 陣 營 交 戰 平 手"), _room2pc);
						} else if (getPoint(_room2red) > getPoint(_room2blue)) {
							_room2win = _room2red;
							broadcastPacketRoom(new S_BlueMessage(166,
									"\\f2復仇者聯盟 獲勝"), _room2pc);
						} else if (getPoint(_room2red) < getPoint(_room2blue)) {
							_room2win = _room2blue;
							broadcastPacketRoom(new S_BlueMessage(166,
									"\\f2暗殺者組織 獲勝"), _room2pc);
						}
					} else if (count2 == 15) {
						BonusRoom(_room2win);
						broadcastPacketRoom(new S_BlueMessage(166, "\\f2頒發 活動獎勵"),
								_room2pc);
					} else if (count2 == 20) {
						count2 = 0;
						OutRoom(_room2pc);
						GameReset(_room2pc);
						alltime2 = ConfigOther.RedBlueTime_all;
						cleartime2 = ConfigOther.RedBlueTime_clear;
						_room2pc.clear();
						_room2red.clear();
						_room2blue.clear();
						_room2win.clear();
						step2 = 4;
					}
				}

				if (step2 == 2) {
					count2++;
					alltime2--;
					broadcastPacketRoom(new S_PackBoxMaptime(alltime2),
							_room2pc);
					if (alltime2 <= 0) {
						step2 = 3;
						count2 = 0;
						Resurrect(_room2pc);
						broadcastPacketRoom(new S_BlueMessage(166,
								"\\f2戰鬥結束，積分統計中..."), _room2pc);
					}
				}

				if (step2 == 1) {
					count2++;
					if (count2 == ConfigOther.RedBlueEffect_time) {
						count2 = 0;
						step2 = 2;
						broadcastPacketRoom(new S_Paralysis(
								S_Paralysis.TYPE_FREEZE, false), _room2pc);
					}
				}

				if (step2 == 0
						&& _room2pc.size() == ConfigOther.RedBluePc_amount * 2) {
					step2 = 1;
					for (L1PcInstance pc : _room2pc) {
						if (pc != null) {
							if (pc.get_redbluejoin() == 21) {
								pc.sendPackets(new S_ServerMessage("\\aG你被分配到 復仇者聯盟 隊伍。"));
								L1Teleport.teleport(pc,
										ConfigOther.RedBlueRed_map2[0],
										ConfigOther.RedBlueRed_map2[1],
										(short) ConfigOther.RedBlueRed_map2[2],
										pc.getHeading(), true);
							} else if (pc.get_redbluejoin() == 22) {
								pc.sendPackets(new S_ServerMessage("\\aI你被分配到 暗殺者組織 隊伍。"));
								L1Teleport
										.teleport(
												pc,
												ConfigOther.RedBlueBlue_map2[0],
												ConfigOther.RedBlueBlue_map2[1],
												(short) ConfigOther.RedBlueBlue_map2[2],
												pc.getHeading(), true);
							}
							pc.sendPackets(new S_Paralysis(
									S_Paralysis.TYPE_FREEZE, true));
						}
					}
					Random rnd = new Random();
					int dice = rnd.nextInt(ConfigOther.RedBluePc_amount);
					while (_room2red.get(dice) == null
							|| _room2blue.get(dice) == null) {
						dice = rnd.nextInt(ConfigOther.RedBluePc_amount);
					}
					_room2red.get(dice).set_redblueleader(1);
					_room2red.get(dice).sendPackets(
							new S_ServerMessage("\\aD你成為復仇者聯盟 隊長"));
					_room2blue.get(dice).set_redblueleader(2);
					_room2blue.get(dice).sendPackets(
							new S_ServerMessage("\\aD你成為暗殺者組織 隊長"));
					broadcastPacketRoom(new S_BlueMessage(166,
							"\\f2參賽人數已滿，活動即將開始"), _room2pc);
				}
				//
				if (step1 == 4) {
					count1++;
					cleartime1--;
					if (cleartime1 <= 0) {
						count1 = 0;
						step1 = 0;
						for (L1PcInstance talkto : all) {
							if (talkto != null) {
								talkto.sendPackets(new S_BlueMessage(166,
										"\\f2無界陣營戰 - 清理完畢"));
							}
						}
					}
				}

				if (step1 == 3) {
					count1++;
					if (count1 == 5) {
						broadcastPacketRoom(new S_BlueMessage(166, "\\f2復仇者聯盟"
								+ getPoint(_room1red) + " vs  " + "暗殺者組織"
								+ getPoint(_room1blue)), _room1pc);
					} else if (count1 == 10) {
						if (getPoint(_room1red) == getPoint(_room1blue)) {
							broadcastPacketRoom(new S_BlueMessage(166,
									"\\f2雙 方 陣 營 交 戰 平 手"), _room1pc);
						} else if (getPoint(_room1red) > getPoint(_room1blue)) {
							_room1win = _room1red;
							broadcastPacketRoom(new S_BlueMessage(166,
									"\\f2復仇者聯盟 獲勝"), _room1pc);
						} else if (getPoint(_room1red) < getPoint(_room1blue)) {
							_room1win = _room1blue;
							broadcastPacketRoom(new S_BlueMessage(166,
									"\\f2暗殺者組織 獲勝"), _room1pc);
						}
					} else if (count1 == 15) {
						BonusRoom(_room1win);
						broadcastPacketRoom(new S_BlueMessage(166, "\\f2頒發 活動獎勵"),
								_room1pc);
					} else if (count1 == 20) {
						count1 = 0;
						OutRoom(_room1pc);
						GameReset(_room1pc);
						alltime1 = ConfigOther.RedBlueTime_all;
						cleartime1 = ConfigOther.RedBlueTime_clear;
						_room1pc.clear();
						_room1red.clear();
						_room1blue.clear();
						_room1win.clear();
						step1 = 4;
					}
				}

				if (step1 == 2) {
					count1++;
					alltime1--;
					broadcastPacketRoom(new S_PackBoxMaptime(alltime1),
							_room1pc);
					if (alltime1 <= 0) {
						step1 = 3;
						count1 = 0;
						Resurrect(_room1pc);
						broadcastPacketRoom(new S_BlueMessage(166,
								"\\f2戰鬥結束，積分統計中..."), _room1pc);
					}
				}

				if (step1 == 1) {
					count1++;
					if (count1 == ConfigOther.RedBlueEffect_time) {
						count1 = 0;
						step1 = 2;
						broadcastPacketRoom(new S_Paralysis(
								S_Paralysis.TYPE_FREEZE, false), _room1pc);
					}
				}

				if (step1 == 0
						&& _room1pc.size() == ConfigOther.RedBluePc_amount * 2) {
					step1 = 1;
					for (L1PcInstance pc : _room1pc) {
						if (pc != null) {
							if (pc.get_redbluejoin() == 11) {
								pc.sendPackets(new S_ServerMessage("\\aG你被分配到 復仇者聯盟 隊伍。"));
								L1Teleport.teleport(pc,
										ConfigOther.RedBlueRed_map1[0],
										ConfigOther.RedBlueRed_map1[1],
										(short) ConfigOther.RedBlueRed_map1[2],
										pc.getHeading(), true);
							} else if (pc.get_redbluejoin() == 12) {
								pc.sendPackets(new S_ServerMessage("\\aI你被分配到 暗殺者組織 隊伍。"));
								L1Teleport
										.teleport(
												pc,
												ConfigOther.RedBlueBlue_map1[0],
												ConfigOther.RedBlueBlue_map1[1],
												(short) ConfigOther.RedBlueBlue_map1[2],
												pc.getHeading(), true);
							}
							pc.sendPackets(new S_Paralysis(
									S_Paralysis.TYPE_FREEZE, true));
						}
					}
					Random rnd = new Random();
					int dice = rnd.nextInt(ConfigOther.RedBluePc_amount);
					while (_room1red.get(dice) == null
							|| _room1blue.get(dice) == null) {
						dice = rnd.nextInt(ConfigOther.RedBluePc_amount);
					}
					_room1red.get(dice).set_redblueleader(1);
					_room1red.get(dice).sendPackets(
							new S_ServerMessage("\\aD你成為復仇者聯盟 隊長"));
					_room1blue.get(dice).set_redblueleader(2);
					_room1blue.get(dice).sendPackets(
							new S_ServerMessage("\\aD你成為暗殺者組織 隊長"));
					broadcastPacketRoom(new S_BlueMessage(166,
							"\\f2參賽人數已滿，活動即將開始"), _room1pc);
				}
			} catch (Exception e) {
				_log.error("無界陣營戰處理時間軸異常重啟", e);
				GeneralThreadPool.get().cancel(this._timer, false);
				RedBlueTimer time = new RedBlueTimer();
				time.start();
			}
		}
	}
}