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
public class Npc_Ship extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Ship.class);

	/**
	 *
	 */
	private Npc_Ship() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Ship();
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

		private final L1QuestUser quest;

		private final Point[] _point;

		private Work(final L1NpcInstance npc) {
			_npc = npc;
			_spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
			_npcMove = new NpcWorkMove(npc);
			quest = WorldQuest.get().get(npc.get_showId());
			_point = new Point[] { new Point(_npc.getX(), 32811), new Point(_npc.getX(), 32813),
					new Point(_npc.getX(), 32825) };
		}

		public void getStart() {
			GeneralThreadPool.get().schedule(this, 10);
		}

		@Override
		public void run() {
			try {
				if ((quest == null) || (quest.get_orimR() == null)) {
					return;
				}

				int counter = 0;
				Point point = _point[counter];
				boolean isWork = true;
				while (isWork) {
					Thread.sleep(_spr);

					_npcMove.actionStart(point);

					if (_npc.getLocation().isSamePoint(point)) {
						quest.get_orimR().shipReturnStep(counter);
						if (counter == 0) {
							Thread.sleep(2000);
							point = _point[Math.min(++counter, _point.length)];
						} else if (counter == 1) {
							Thread.sleep(120000);
							quest.get_orimR().shipReturnStep(counter);
							point = _point[Math.min(++counter, _point.length)];
						} else if (counter == 2) {
							isWork = false;
							_npc.setreSpawn(false);
							_npc.deleteMe();
						}
					}
				}
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
