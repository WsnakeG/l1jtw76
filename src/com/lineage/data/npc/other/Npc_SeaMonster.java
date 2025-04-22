package com.lineage.data.npc.other;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.WorldQuest;

/**
 * @author terry0412
 */
public class Npc_SeaMonster extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_SeaMonster.class);

	/**
	 *
	 */
	private Npc_SeaMonster() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_SeaMonster();
	}

	@Override
	public int type() {
		return 16;
	}

	private static final Point[][] _point = new Point[][] {
			{ new Point(32787, 32807), new Point(32828, 32807) },
			{ new Point(32791, 32794), new Point(32791, 32828) }, { new Point(32776, 32821) },
			{ new Point(32772, 32823) }, { new Point(32791, 32807), new Point(32789, 32828) },
			{ new Point(32801, 32802), new Point(32802, 32801), new Point(32804, 32801),
					new Point(32805, 32802), new Point(32805, 32804), new Point(32809, 32808),
					new Point(32828, 32808) } };

	@Override
	public void work(final L1NpcInstance npc) {
		final Work work = new Work(npc);
		work.getStart();
	}

	private class Work implements Runnable {

		private final L1NpcInstance _npc;

		private final int _spr;

		private final int _type;

		private final NpcWorkMove _npcMove;

		private final L1QuestUser quest;

		// private final Point[] _point;

		private Work(final L1NpcInstance npc) {
			_npc = npc;
			_spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
			_npcMove = new NpcWorkMove(npc);
			quest = WorldQuest.get().get(npc.get_showId());
			_type = npc.get_quest_id();
		}

		public void getStart() {
			GeneralThreadPool.get().schedule(this, 300);
		}

		@Override
		public void run() {
			try {
				if ((quest == null) || (quest.get_orimR() == null)) {
					return;
				}

				int counter = 0;
				Point point = _point[_type][counter];
				boolean isWork = true;
				while (isWork) {
					Thread.sleep(_spr);

					_npcMove.actionStart(point);

					if (_npc.getLocation().isSamePoint(point)) {
						if ((counter + 1) >= _point[_type].length) {
							isWork = false;
							_npc.setreSpawn(false);
							_npc.deleteMe();
						} else {
							point = _point[_type][++counter];
						}
					}
				}
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
