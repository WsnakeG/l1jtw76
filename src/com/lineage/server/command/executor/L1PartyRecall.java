package com.lineage.server.command.executor;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGm;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 召喚隊伍(參數:人物名稱/選單)
 * 
 * @author dexc
 */
public class L1PartyRecall implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1PartyRecall.class);

	private L1PartyRecall() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1PartyRecall();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final L1PcInstance target = World.get().getPlayer(arg);

			if (target != null) {
				final L1Party party = target.getParty();
				if (party != null) {
					final int x = pc.getX();
					final int y = pc.getY() + 2;
					final short map = pc.getMapId();

					final List<L1PcInstance> pcs = party.getMemberList();

					if (pcs.isEmpty()) {
						return;
					}
					if (pcs.size() <= 0) {
						return;
					}

					for (final L1PcInstance pc2 : pcs) {
						try {
							L1Teleport.teleport(pc2, x, y, map, 5, true);
							pc2.sendPackets(new S_SystemMessage("管理員召喚!"));

						} catch (final Exception e) {
							_log.error("隊伍召喚異常", e);
						}
					}
				} else {
					pc.sendPackets(new S_SystemMessage(arg + " 不是一個隊伍成員!"));
				}

			} else {
				final int mode = 3;
				pc.sendPackets(new S_PacketBoxGm(pc, mode));
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
