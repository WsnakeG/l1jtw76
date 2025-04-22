package com.lineage.data.npc.other;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_Teleport;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;

/**
 * @author terry0412
 */
public class Npc_Sandstorm extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Sandstorm.class);

	private final Random _random = new Random();

	/**
	 *
	 */
	private Npc_Sandstorm() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Sandstorm();
	}

	@Override
	public int type() {
		return 16;
	}

	@Override
	public void work(final L1NpcInstance npc) {
		final Work work = new Work(npc);
		work.getStart();
	}

	private class Work implements Runnable {

		private final L1NpcInstance _npc;

		private final int _spr;

		private final NpcWorkMove _npcMove;

		private int _counter;

		private Work(final L1NpcInstance npc) {
			_npc = npc;
			_spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
			_npcMove = new NpcWorkMove(npc);
		}

		/**
		 * 啟動線程
		 */
		public void getStart() {
			GeneralThreadPool.get().schedule(this, 300);
		}

		@Override
		public void run() {
			try {
				_npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Appear));
				Thread.sleep(SprTable.get().getMoveSpeed(_npc.getTempCharGfx(), ActionCodes.ACTION_Appear));

				_counter = _random.nextInt(11) + 5;

				Point point = new Point(
						_npc.getX() + ((_random.nextInt(10) + 5) * (_random.nextBoolean() ? 1 : -1)),
						_npc.getY() + ((_random.nextInt(10) + 5) * (_random.nextBoolean() ? 1 : -1)));

				boolean isWork = true;
				while (isWork) {
					Thread.sleep(_spr);

					searchTarget(_npc);

					_npcMove.actionStart(point);

					if (--_counter <= 0) {
						isWork = false;

						_npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Die));

						_npc.deleteMe();

					} else if (_npc.getLocation().isSamePoint(point)) {
						if (_random.nextInt(100) < 80) {
							point = new Point(
									_npc.getX()
											+ ((_random.nextInt(10) + 5) * (_random.nextBoolean() ? 1 : -1)),
									_npc.getY()
											+ ((_random.nextInt(10) + 5) * (_random.nextBoolean() ? 1 : -1)));
							_counter = _random.nextInt(11) + 5;

						} else {
							Thread.sleep((_random.nextInt(4) + 2) * 1000);
						}
					}
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * 附近目標搜尋
	 * 
	 * @param npc
	 */
	private final void searchTarget(final L1NpcInstance npc) {
		for (final L1PcInstance pc : World.get().getVisiblePlayer(npc, 1)) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}

			if (_loc == null) {
				// 不被傳送的機率
				if (_random.nextInt(100) < 15) {
					break;
				}

				final L1Location loc;
				switch (_random.nextInt(6)) {
				case 0: // 巨人區
					loc = new L1Location(32751, 33173, 4).randomLocation(200, true);
					break;
				case 1: // 遺忘之島
					loc = new L1Location(32751, 33173, 4).randomLocation(200, true);
					break;
				case 2: // 冰女洞穴
					loc = new L1Location(32774, 32909, 74).randomLocation(200, true);
					break;
				case 3: // 傲慢之塔 門口
					loc = new L1Location(34249, 33452, 4).randomLocation(200, true);
					break;
				case 4: // 海音地監 4樓 (海底)
					loc = new L1Location(32742, 32679, 63).randomLocation(200, true);
					break;
				default: // 火龍窟
					loc = new L1Location(33743, 32277, 4).randomLocation(200, true);
					break;
				}

				// 傳送鎖定 (有動畫) by terry0412
				pc.setTeleportX(loc.getX());
				pc.setTeleportY(loc.getY());
				pc.setTeleportMapId((short) loc.getMapId());
				// 送出鎖定封包
				pc.sendPackets(new S_Teleport(pc));
				break;

			} else {
				// 不被傳送的機率
				if (_random.nextInt(100) < 50) {
					break;
				}

				final L1Location loc = _loc.randomLocation(_range, true);

				// 傳送鎖定 (有動畫) by terry0412
				pc.setTeleportX(loc.getX());
				pc.setTeleportY(loc.getY());
				pc.setTeleportMapId((short) loc.getMapId());
				// 送出鎖定封包
				pc.sendPackets(new S_Teleport(pc));
				break;
			}
		}
	}

	private L1Location _loc;
	private int _range;

	@Override
	public void set_set(final String[] set) {
		try {
			if (set.length > 3) {
				_loc = new L1Location(Integer.parseInt(set[1]), Integer.parseInt(set[2]),
						Integer.parseInt(set[3]));
			}
		} catch (final Exception e) {
		}

		try {
			if (set.length > 4) {
				_range = Integer.parseInt(set[4]);
			}
		} catch (final Exception e) {
		}
	}
}
