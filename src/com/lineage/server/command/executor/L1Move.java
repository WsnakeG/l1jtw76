package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 移動至指定座標邊(參數:LOCX - LOCY - MAPID)
 * 
 * @author dexc
 */
public class L1Move implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Move.class);

	private L1Move() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Move();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			final int locx = Integer.parseInt(st.nextToken());
			final int locy = Integer.parseInt(st.nextToken());
			short mapid;
			if (st.hasMoreTokens()) {
				mapid = Short.parseShort(st.nextToken());

			} else {
				mapid = pc.getMapId();
			}

			L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
			pc.sendPackets(new S_SystemMessage("座標 " + locx + ", " + locy + ", " + mapid + " 執行移動。"));

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
