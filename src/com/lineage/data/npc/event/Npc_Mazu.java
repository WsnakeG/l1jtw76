package com.lineage.data.npc.event;

import static com.lineage.server.model.skill.L1SkillId.MAZU_STATUS;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 媽祖<BR>
 * 91100<BR>
 * 
 * @author simlin
 */
public class Npc_Mazu extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Mazu.class);

	/** 已經參加過的人員列表 */
	private static final Map<Integer, String> _playList = new HashMap<Integer, String>();

	private Npc_Mazu() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Mazu();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			final String ole = _playList.get(pc.getId());
			if (ole == null) {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_e_g_01"));

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_e_g_02"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			if (cmd.equalsIgnoreCase("0")) {// 雙手合十拜拜，吾將會給你最虔誠的媽祖祝福
				final String ole = _playList.get(pc.getId());
				if (ole == null) {
					pc.setSkillEffect(MAZU_STATUS, 2400 * 1000);
					// 媽祖祝福
					pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7321));
					_playList.put(pc.getId(), pc.getName());
				}
			}
			pc.sendPackets(new S_CloseList(pc.getId()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
