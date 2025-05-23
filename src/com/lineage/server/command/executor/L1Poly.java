package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 變身指令(參數:人物名稱 - 變身代號)
 * 
 * @author dexc
 */
public class L1Poly implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Poly.class);

	private L1Poly() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Poly();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			final String name = st.nextToken();
			final int polyid = Integer.parseInt(st.nextToken());

			final L1PcInstance tg = World.get().getPlayer(name);

			if (tg == null) {
				// 73 \f1%0%d 不在線上。
				pc.sendPackets(new S_ServerMessage(73, name));
			} else {
				try {
					L1PolyMorph.doPoly(tg, polyid, 7200, L1PolyMorph.MORPH_BY_GM);
				} catch (final Exception exception) {
					pc.sendPackets(new S_SystemMessage(".poly [人物名稱] [外型代號]"));
				}
			}
		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
