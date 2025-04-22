package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGm;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 封鎖IP/MAC(參數:人物名稱/選單)
 * 
 * @author dexc
 */
public class L1PowerKick implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1PowerKick.class);

	private L1PowerKick() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1PowerKick();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			String gmName = "";
			if (pc == null) {
				_log.warn("系統命令執行: " + cmdName + " " + arg + " 封鎖IP/MAC。");
				gmName = "系統命令";

			} else {
				gmName = pc.getName() + "命令";
			}

			// XXX 解除
			if (arg.indexOf("remove") != -1) {
				final StringTokenizer st = new StringTokenizer(arg);
				st.nextToken();
				final String ipaddr = st.nextToken();

				boolean isBan = false;// 已經是封鎖位置

				if (LanSecurityManager.BANIPMAP.containsKey(arg)) {
					isBan = true;
				}

				if (!isBan) {
					// 加入IP/MAC封鎖
					IpReading.get().remove(ipaddr);
					_log.warn("系統命令執行: " + cmdName + " " + ipaddr + " 解除封鎖IP/MAC。");
				}
				return;

				// XXX 封鎖
			} else if (arg.lastIndexOf(".") != -1) {
				if (!LanSecurityManager.BANIPMAP.containsKey(arg)) {
					// 加入IP封鎖
					IpReading.get().add(arg.toString(), gmName + ":L1PowerKick 封鎖IP");
				}
				return;
			}

			final L1PcInstance target = World.get().getPlayer(arg);

			if (target != null) {
				final ClientExecutor targetClient = target.getNetConnection();
				final String ipaddr = targetClient.getIp().toString();
				if (ipaddr != null) {
					if (!LanSecurityManager.BANIPMAP.containsKey(ipaddr)) {
						// 加入IP封鎖
						IpReading.get().add(ipaddr.toString(), gmName + ":L1PowerKick 封鎖IP");
					}
				}
				if (pc != null) {
					pc.sendPackets(new S_SystemMessage(target.getName() + " 封鎖IP/MAC。"));
				}
				targetClient.kick();

			} else {
				if (pc == null) {
					_log.error("指令異常: 這個命令必須輸入正確人物名稱 或是 IP/MAC位置才能執行。");

				} else {
					final int mode = 6;
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
