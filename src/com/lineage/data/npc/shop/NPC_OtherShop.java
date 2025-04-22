package com.lineage.data.npc.shop;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 一般販賣NPC
 * 
 * @author dexc
 */
public class NPC_OtherShop extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(NPC_OtherShop.class);

	private static Random _random = new Random();

	/**
	 *
	 */
	private NPC_OtherShop() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new NPC_OtherShop();
	}

	@Override
	public int type() {
		return 1;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			final int htmlidr = _random.nextInt(6);
			final String htmlid = "yiwei_Shop" + htmlidr;
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
