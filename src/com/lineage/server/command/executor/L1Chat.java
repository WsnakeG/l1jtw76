package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 廣播限制(參數:on(取消廣播限制)/off(設置廣播限制))
 * 
 * @author dexc
 */
public class L1Chat implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Chat.class);

	private L1Chat() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Chat();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			if (st.hasMoreTokens()) {
				final String flag = st.nextToken();
				String msg;
				if (flag.compareToIgnoreCase("on") == 0) {
					World.get().set_worldChatElabled(true);
					msg = "取消廣播限制。";

				} else if (flag.compareToIgnoreCase("off") == 0) {
					World.get().set_worldChatElabled(false);
					msg = "設置廣播限制。";

				} else {
					throw new Exception();
				}

				if (pc == null) {
					_log.warn("系統命令執行: " + cmdName + " " + arg + " " + msg);

				} else {
					pc.sendPackets(new S_SystemMessage(msg));
				}

			} else {
				String msg;
				if (World.get().isWorldChatElabled()) {
					msg = "目前未暫停廣播使用。.chat off 可以設置廣播限制。";

				} else {
					msg = "目前暫停廣播使用。.chat on 可以取消廣播限制。";
				}
				if (pc == null) {
					_log.warn("指令異常: " + msg);

				} else {
					pc.sendPackets(new S_SystemMessage(msg));
				}
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
