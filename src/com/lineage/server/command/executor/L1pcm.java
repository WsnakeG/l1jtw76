package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.command.GmHtml;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * PC清單
 * 
 * @author dexc
 */
public class L1pcm implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1pcm.class);

	private L1pcm() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1pcm();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final GmHtml gmHtml = new GmHtml(pc, 0);
			gmHtml.show();

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
