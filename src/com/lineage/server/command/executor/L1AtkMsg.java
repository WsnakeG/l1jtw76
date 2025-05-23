package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 開啟/關閉 GM攻擊訊息
 * 
 * @author dexc
 */
public class L1AtkMsg implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1AtkMsg.class);

	private L1AtkMsg() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AtkMsg();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			if (ConfigAlt.ALT_ATKMSG) {
				ConfigAlt.ALT_ATKMSG = false;

			} else {
				ConfigAlt.ALT_ATKMSG = true;
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
