package com.lineage.server.model.Instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1NpcTalkData;
import com.lineage.server.serverpackets.S_NPCPack_D;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;

/**
 * 對象:倉庫 控制項
 * 
 * @author dexc
 */
public class L1DwarfInstance extends L1NpcInstance {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1DwarfInstance.class);

	/**
	 * @param template
	 */
	public L1DwarfInstance(final L1Npc template) {
		super(template);
	}

	/**
	 * TODO 接觸資訊
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_NPCPack_D(this));
			// this.onNpcAI();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			final L1AttackMode attack = new L1AttackPc(pc, this);
			// attack.calcHit();
			attack.action();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance pc) {
		final int objid = getId();
		final L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(getNpcTemplate().get_npcId());
		final int npcId = getNpcTemplate().get_npcId();
		String htmlid = null;

		if (talking != null) {
			if (npcId == 60028) { // エル
				if (!pc.isElf()) {
					htmlid = "elCE1";
				}
			}

			if (htmlid != null) { // htmlidが指定されている場合
				pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			} else {
				if (pc.getLevel() < 5) {
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));

				} else {
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		}
	}

	@Override
	public void onFinalAction(final L1PcInstance pc, final String Action) {

		if (Action.equalsIgnoreCase("retrieve")) {
			// _log.finest("Retrive items in storage");
		} else if (Action.equalsIgnoreCase("retrieve-pledge")) {
			// _log.finest("Retrive items in pledge storage");

			if (pc.getClanname().equalsIgnoreCase(" ")) {
				// 208 \f1若想使用血盟倉庫，必須加入血盟。
				pc.sendPackets(new S_ServerMessage(208));

			} else {
				// _log.finest("pc is in a pledge");
			}
		}
	}
}
