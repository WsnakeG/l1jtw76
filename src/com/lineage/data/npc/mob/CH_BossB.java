package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CheckUtil;

/**
 * 魔法師．哈汀(故事)<BR>
 * 91295 黑翼賽尼斯<BR>
 * 91296 賽尼斯<BR>
 * 
 * @author dexc
 */
public class CH_BossB extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(CH_BossB.class);

	private CH_BossB() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new CH_BossB();
	}

	@Override
	public int type() {
		return 40;
	}

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			// 判斷主要攻擊者
			final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);

			if (pc != null) {
				if (pc.get_hardinR() != null) {
					pc.get_hardinR().boss_b_death();
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public void spawn(final L1NpcInstance npc) {
		try {
			switch (npc.getNpcId()) {
			case 91295:// 黑翼賽尼斯
				break;

			case 91296:// 賽尼斯
				final BossBR boss = new BossBR(npc);
				GeneralThreadPool.get().execute(boss);
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	class BossBR implements Runnable {

		private final L1NpcInstance _npc;

		public BossBR(final L1NpcInstance npc) {
			_npc = npc;
		}

		@Override
		public void run() {
			try {
				if (!_npc.isDead()) {
					Thread.sleep(4000);
					_npc.broadcastPacketAll(new S_NpcChat(_npc, "$7588"));// 7588
																			// 消滅！全部都消滅吧！
				}

				if (!_npc.isDead()) {
					Thread.sleep(4000);
					_npc.broadcastPacketAll(new S_NpcChat(_npc, "$7591"));// 7591
																			// 這種甜甜的味道是什麼呢？血？
				}

				if (!_npc.isDead()) {
					Thread.sleep(4000);
					_npc.broadcastPacketAll(new S_NpcChat(_npc, "$7593"));// 7593
																			// 我詛咒全部！
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
