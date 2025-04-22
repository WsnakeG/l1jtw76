package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.EchoServerTimer;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 重新啟用監聽
 */
public class L1Echo implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Echo.class);

	private L1Echo() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Echo();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			/*
			 * if (pc == null) { _log.warn("系統命令執行: " + cmdName + " " + arg +
			 * " 監聽端口設置作業。"); }
			 */
			if (arg.equalsIgnoreCase("stop")) {
				if (pc == null) {
					_log.warn("系統命令執行: 全部端口關閉監聽!");

				} else {
					pc.sendPackets(new S_ServerMessage(166, "全部端口關閉監聽!"));
				}
				EchoServerTimer.get().stopEcho();
				return;
			}
			final String info = "重新啟動服務器端口監聽!";
			_log.info(info);
			// 監聽端口啟動重置作業
			EchoServerTimer.get().reStart();
			if (pc != null) {
				pc.sendPackets(new S_ServerMessage(166, info));
			}

		} catch (final Exception e) {
			if (pc == null) {
				_log.error("錯誤的命令格式: " + this.getClass().getSimpleName());

			} else {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
			}

		} finally {
			_log.info("監聽端口設置作業完成!!");
		}
	}
}
