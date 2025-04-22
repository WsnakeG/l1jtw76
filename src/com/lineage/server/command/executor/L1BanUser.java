package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 外掛處分(參數:人物名稱 - 分鐘)
 * 
 * @author dexc
 */
public class L1BanUser implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1BanUser.class);

	private L1BanUser() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1BanUser();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {

		} catch (final Exception e) {
			if (pc != null) {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
			}
		}
	}
}
