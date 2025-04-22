package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGm;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 移動座標至指定人物身邊(參數:人物名稱/選單)
 * 
 * @author dexc
 */
public class L1ToPC implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1ToPC.class);

	private L1ToPC() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ToPC();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final L1PcInstance target = World.get().getPlayer(arg);

			if (target != null) {
				L1Teleport.teleport(pc, target.getX(), target.getY(), target.getMapId(), 5, false);
				// 設置副本編號
				pc.set_showId(target.get_showId());
				pc.sendPackets(new S_ServerMessage(166, "移動座標至指定人物身邊: " + arg));

			} else {
				final int mode = 1;
				pc.sendPackets(new S_PacketBoxGm(pc, mode));
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
