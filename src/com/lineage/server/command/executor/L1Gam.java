package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.gambling.Gambling;
import com.lineage.data.event.gambling.GamblingNpc;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.event.GamblingTime;

/**
 * 奇岩賭場控制命令
 */
public class L1Gam implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Gam.class);

	private L1Gam() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Gam();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final Gambling gambling = GamblingTime.get_gambling();
			if (pc == null) {
				if (arg.equalsIgnoreCase("show")) {
					_log.info("系統預設:" + gambling.WIN + ":");
					for (final Integer key : gambling.get_allNpc().keySet()) {
						final GamblingNpc gam = gambling.get_allNpc().get(key);
						_log.info("跑道:" + gam.get_xId() + "/" + gam.get_npc().getName());
					}

				} else if (arg.equalsIgnoreCase("start")) {
					GamblingTime.set_status(true);
					_log.info("系統:允許比賽接續執行");

				} else if (arg.equalsIgnoreCase("stop")) {
					GamblingTime.set_status(false);
					_log.info("系統:中止比賽接續執行");

				} else if (arg.startsWith("set")) {
					final int xid = Integer.parseInt(arg.substring(4));
					if (xid > 4) {
						_log.info("錯誤!跑道編號為0~4");
						return;
					}
					gambling.WIN = xid;
					_log.info("變更設置:" + gambling.WIN);
					for (final Integer key : gambling.get_allNpc().keySet()) {
						final GamblingNpc gam = gambling.get_allNpc().get(key);
						_log.info("跑道:" + gam.get_xId() + "/" + gam.get_npc().getName());
					}
				}

			} else {
				if (arg.equalsIgnoreCase("show")) {
					pc.sendPackets(new S_ServerMessage(166, "系統預設:" + gambling.WIN));
					for (final Integer key : gambling.get_allNpc().keySet()) {
						final GamblingNpc gam = gambling.get_allNpc().get(key);
						pc.sendPackets(new S_ServerMessage(166,
								"跑道:" + gam.get_xId() + "/" + gam.get_npc().getName()));
					}

				} else if (arg.equalsIgnoreCase("start")) {
					GamblingTime.set_status(true);
					pc.sendPackets(new S_ServerMessage(166, "系統:允許比賽接續執行"));

				} else if (arg.equalsIgnoreCase("stop")) {
					GamblingTime.set_status(false);
					pc.sendPackets(new S_ServerMessage(166, "系統:中止比賽接續執行"));

				} else if (arg.startsWith("set")) {
					final int xid = Integer.parseInt(arg.substring(4));
					if (xid > 4) {
						pc.sendPackets(new S_ServerMessage(166, "錯誤!跑道編號為0~4"));
						return;
					}
					gambling.WIN = xid;
					pc.sendPackets(new S_ServerMessage(166, "變更設置:" + gambling.WIN));
					for (final Integer key : gambling.get_allNpc().keySet()) {
						final GamblingNpc gam = gambling.get_allNpc().get(key);
						pc.sendPackets(new S_ServerMessage(166,
								"跑道:" + gam.get_xId() + "/" + gam.get_npc().getName()));
					}

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
