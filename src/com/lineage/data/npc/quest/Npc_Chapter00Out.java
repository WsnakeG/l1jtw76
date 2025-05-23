package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 輔助研究員<BR>
 * 81374<BR>
 * 說明:穿越時空的探險(秘譚)
 * 
 * @author dexc
 */
public class Npc_Chapter00Out extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Chapter00Out.class);

	private Npc_Chapter00Out() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Chapter00Out();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 和尤基好好談過了嗎？<
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_l_out00"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			boolean isCloseList = false;

			if (cmd.equalsIgnoreCase("a")) {// 「回到說話之島。」
				// 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);
				L1Teleport.teleport(pc, 32594, 32917, (short) 0, 4, true);
				isCloseList = true;

			}

			if (isCloseList) {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
			}

		} catch (final Exception e) {
			// 該訊息只有發生錯誤時才會顯示。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html05"));
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
