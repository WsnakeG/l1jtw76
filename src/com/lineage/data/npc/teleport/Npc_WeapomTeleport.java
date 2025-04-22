package com.lineage.data.npc.teleport;

import java.util.Random;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 惡魔王領地傳送師 80023
 * 
 * @author dexc
 */
public class Npc_WeapomTeleport extends NpcExecutor {

	public static final Random _random = new Random();

	/**
	 * 惡魔王領地傳送師
	 */
	private Npc_WeapomTeleport() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_WeapomTeleport();
	}

	@Override
	public int type() {
		return 1;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		final int r = _random.nextInt(3);
		// 惡魔王領地傳送師
		if (r == 0) {
			L1Teleport.teleport(pc, 32557, 32863, (short) 5167, 6, true);

		} else if (r == 1) {
			L1Teleport.teleport(pc, 32723, 32799, (short) 5167, 0, true);

		} else if (r == 2) {
			L1Teleport.teleport(pc, 32606, 32745, (short) 5167, 4, true);

		}
	}
}
