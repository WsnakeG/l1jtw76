package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class L1AI implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1AI.class);

	private L1AI() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AI();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			if (Config.AICHECK) {
				Config.AICHECK = false;
			} else {
				Config.AICHECK = true;
			}
			pc.sendPackets(new S_SystemMessage("AI驗證狀態:" + Config.AICHECK));
		} catch (final Exception e) {
			_log.error("錯誤的命令格式: " + this.getClass().getSimpleName());
		}
		_log.warn("命令執行AI檢查目前狀態為: " + Config.AICHECK);
	}

}