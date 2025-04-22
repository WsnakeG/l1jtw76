package com.lineage.server.command.executor;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.server.clientpackets.C_Who;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.SystemUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldCrown;
import com.lineage.server.world.WorldDarkelf;
import com.lineage.server.world.WorldDragonKnight;
import com.lineage.server.world.WorldElf;
import com.lineage.server.world.WorldIllusionist;
import com.lineage.server.world.WorldKnight;
import com.lineage.server.world.WorldWarrior;
import com.lineage.server.world.WorldWizard;

/**
 * 顯示線上實際人數
 * 
 * @author dexc
 */
public class L1Who implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Who.class);

	private L1Who() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Who();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final Collection<L1PcInstance> players = World.get().getAllPlayers();

			final String amount = String.valueOf(players.size());

			final int a = WorldCrown.get().map().size();// 1127：[王族]
			final int b = WorldKnight.get().map().size();// 1128：[騎士]
			final int c = WorldElf.get().map().size();// 1129：[妖精]
			final int d = WorldWizard.get().map().size();// 1130：[法師]
			final int e = WorldDarkelf.get().map().size();// 2503：[黑暗妖精]
			final int f = WorldDragonKnight.get().map().size();// 5889：[龍騎士]
			final int g = WorldIllusionist.get().map().size();// 5890：[幻術士]
			final int h = WorldWarrior.get().map().size();

			if (pc == null) {
				_log.warn("系統命令執行: who");
				_log.info("[王族]:" + a);
				_log.info("[騎士]:" + b);
				_log.info("[妖精]:" + c);
				_log.info("[法師]:" + d);
				_log.info("[黑妖]:" + e);
				_log.info("[龍騎]:" + f);
				_log.info("[幻術]:" + g);
				_log.info("[戰士]:" + h);
				_log.info("Server Ver: " + Config.VER);

			} else {
				pc.sendPackets(new S_ServerMessage("目前線上有: " + amount + "/" + C_Who.deCount()));
				pc.sendPackets(new S_ServerMessage("王族:" + a));
				pc.sendPackets(new S_ServerMessage("騎士:" + b));
				pc.sendPackets(new S_ServerMessage("妖精:" + c));
				pc.sendPackets(new S_ServerMessage("法師:" + d));
				pc.sendPackets(new S_ServerMessage("黑妖:" + e));
				pc.sendPackets(new S_ServerMessage("龍騎:" + f));
				pc.sendPackets(new S_ServerMessage("幻術:" + g));
				pc.sendPackets(new S_ServerMessage("戰士:" + h));

				pc.sendPackets(new S_ServerMessage(166, "Server Ver: " + Config.VER));
				pc.sendPackets(new S_ServerMessage(166, "Online: " + amount));
				pc.sendPackets(new S_ServerMessage(166, "Used Mem: " + SystemUtil.getUsedMemoryMB()));
				pc.sendPackets(new S_ServerMessage(166, "Thread Act: " + Thread.activeCount()));
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
