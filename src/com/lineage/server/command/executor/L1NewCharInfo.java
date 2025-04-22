package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 開啟/關閉 創人物公告(參數:開關)
 * 
 * @author terry0412
 */
public class L1NewCharInfo implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1NewCharInfo.class);

	// 是否啟動創人物公告的預設開關
	public static boolean new_char_info = true;

	private L1NewCharInfo() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1NewCharInfo();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			// 變更開關
			if (arg.equalsIgnoreCase("on")) {
				// 是否已開啟
				if (new_char_info != true) {
					new_char_info = true;
					pc.sendPackets(new S_SystemMessage("創人物公告已經成功開啟了!"));

				} else {
					pc.sendPackets(new S_SystemMessage("目前已經是開啟狀態。"));
				}

			} else if (arg.equalsIgnoreCase("off")) {
				// 是否已關閉
				if (new_char_info != false) {
					new_char_info = false;
					pc.sendPackets(new S_SystemMessage("創人物公告已經成功關閉了!"));

				} else {
					pc.sendPackets(new S_SystemMessage("目前已經是關閉狀態。"));
				}
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
