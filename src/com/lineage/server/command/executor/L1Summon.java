package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;

/**
 * 召喚寵物(參數:npcid))
 * 
 * @author dexc
 */
public class L1Summon implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Summon.class);

	private L1Summon() {
	}

	public static L1Summon getInstance() {
		return new L1Summon();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			String nameid = tok.nextToken();

			// NPC編號
			int npcid = 0;
			try {
				npcid = Integer.parseInt(nameid);

			} catch (final NumberFormatException e) {
				npcid = NpcTable.get().findNpcIdByNameWithoutSpace(nameid);
				if (npcid <= 0) {
					pc.sendPackets(new S_ServerMessage(166, "錯誤的NPCID: " + npcid));
					return;
				}
			}

			// 召喚數量
			int count = 1;
			if (tok.hasMoreTokens()) {
				count = Integer.parseInt(tok.nextToken());
			}

			// 召喚數量大於5
			if (count > 5) {
				pc.sendPackets(new S_SystemMessage("寵物召喚數量一次禁止超過5隻。"));
				return;
			}

			final L1Npc npc = NpcTable.get().getTemplate(npcid);

			for (int i = 0; i < count; i++) {
				final L1PetInstance pet = new L1PetInstance(npc, pc);
				pet.setPetcost(0);
			}

			nameid = NpcTable.get().getTemplate(npcid).get_name();
			pc.sendPackets(new S_ServerMessage(166, nameid + "(ID:" + npcid + ") 數量:" + count + " 完成召喚"));

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
