package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.server.ServerHomeTownTime;

/**
 * 啟用貢獻度系統(參數:daily(日處理)/monthly(月處理))
 * 
 * @author dexc
 */
public class L1HomeTown implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1HomeTown.class);

	private L1HomeTown() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1HomeTown();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			if (pc == null) {
				_log.warn("系統命令執行: " + cmdName + " " + arg + " 啟用貢獻度系統。");
			}
			final StringTokenizer st = new StringTokenizer(arg);
			final String para1 = st.nextToken();
			if (para1.equalsIgnoreCase("daily")) {
				ServerHomeTownTime.getInstance().dailyProc();

			} else if (para1.equalsIgnoreCase("monthly")) {
				ServerHomeTownTime.getInstance().monthlyProc();

			} else {
				throw new Exception();
			}

		} catch (final Exception e) {
			if (pc == null) {
				_log.error("錯誤的命令格式: " + this.getClass().getSimpleName());

			} else {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
			}
		}
	}
}
