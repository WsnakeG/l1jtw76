package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGm;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 帳號封鎖(參數:帳號)
 * 
 * @author dexc
 */
public class L1AccountBanKick implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1AccountBanKick.class);

	private L1AccountBanKick() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AccountBanKick();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			String gmName = "";
			if (pc == null) {
				_log.warn("系統命令執行: " + cmdName + " 帳號封鎖:" + arg);
				gmName = "系統命令";

			} else {
				gmName = pc.getName() + "命令";
			}

			final L1PcInstance target = World.get().getPlayer(arg);

			if (target != null) {
				final String info = target.getName() + " 該人物帳號完成封鎖。";
				if (pc == null) {
					_log.warn(info);

				} else {
					pc.sendPackets(new S_SystemMessage(info));
				}
				start(target, gmName + ":L1AccountBanKick 封鎖帳號");

			} else {
				final boolean account = AccountReading.get().isAccount(arg);
				// 輸入資料是否為帳號
				if (account) {
					IpReading.get().add(arg, gmName + ":L1AccountBanKick 封鎖帳號");
					return;
				}
				if (pc == null) {
					_log.error("指令異常: 這個命令必須輸入正確帳號名稱才能執行。");

				} else {
					final int mode = 7;
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

	/**
	 * 加入封鎖帳號
	 * 
	 * @param target
	 * @param info
	 */
	private void start(final L1PcInstance target, final String info) {
		// 加入封鎖帳號
		IpReading.get().add(target.getAccountName(), info);
		target.getNetConnection().kick();
	}
}
