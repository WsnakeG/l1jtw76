package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.EchoServerTimer;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 關閉/開啟指定監聽端口(參數:stop/start 端口編號)
 * 
 * @author dexc
 */
public class L1Port implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Port.class);

	private L1Port() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Port();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			final String cmd = st.nextToken();
			final int key = Integer.valueOf(st.nextToken());

			if (cmd.equalsIgnoreCase("stop")) {
				_log.warn("系統命令執行: " + cmdName + " " + arg + " 關閉 指定監聽端口。");
				EchoServerTimer.get().stopPort(key);

			} else if (cmd.equalsIgnoreCase("start")) {
				_log.warn("系統命令執行: " + cmdName + " " + arg + " 開啟 指定監聽端口。");
				EchoServerTimer.get().startPort(key);
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
