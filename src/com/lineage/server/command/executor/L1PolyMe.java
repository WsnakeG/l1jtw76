package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * GM變身指令(參數:變身代號)
 * 
 * @author dexc
 */
public class L1PolyMe implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1PolyMe.class);

	private L1PolyMe() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1PolyMe();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final int polyid = Integer.parseInt(arg);

			try {
				L1PolyMorph.doPoly(pc, polyid, 7200, L1PolyMorph.MORPH_BY_GM);

			} catch (final Exception exception) {
				pc.sendPackets(new S_SystemMessage(".polyme [外型代號]"));
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
