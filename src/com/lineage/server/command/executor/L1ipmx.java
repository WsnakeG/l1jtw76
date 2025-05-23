package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 封鎖清單
 * 
 * @author dexc
 */
public class L1ipmx implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1ipmx.class);

	private L1ipmx() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ipmx();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			// GmHtml gmHtml = new GmHtml(pc, 3);
			// gmHtml.show();
			pc.sendPackets(new S_ServerMessage("\\fV暫停使用"));

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
