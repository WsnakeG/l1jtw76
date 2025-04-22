package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGm;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 殺死指定人物(參數:人物名稱/選單)
 * 
 * @author dexc
 */
public class L1Kill implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Kill.class);

	private L1Kill() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Kill();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			if (pc == null) {
				_log.warn("系統命令執行: " + cmdName + " " + arg + " 殺死指定人物。");
			}

			final L1PcInstance target = World.get().getPlayer(arg);

			if (target != null) {
				target.setCurrentHp(0);
				target.death(null);

			} else {
				if (pc == null) {
					_log.error("指令異常: 這個命令必須輸入人物名稱才能執行。");

				} else {
					final int mode = 8;
					pc.sendPackets(new S_PacketBoxGm(pc, mode));
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
