package com.lineage.server.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.server.command.executor.L1CommandExecutor;
import com.lineage.server.datatables.CommandsTable;
import com.lineage.server.datatables.lock.ServerGmCommandReading;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillIconAura;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Command;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

/**
 * 管理者命令
 * 
 * @author dexc
 */
public class GMCommands {

	private static final Log _log = LogFactory.getLog(GMCommands.class);

	private static GMCommands _instance;

	private GMCommands() {
	}

	public static GMCommands getInstance() {
		if (_instance == null) {
			_instance = new GMCommands();
		}
		return _instance;
	}

	private String complementClassName(final String className) {
		// 当且仅当此字符串包含指定的 char 值序列时，返回 true。
		if (className.contains(".")) {
			return className;
		}

		// 傳回完整執行路徑
		return "com.lineage.server.command.executor." + className;
	}

	/**
	 * 系統命令執行
	 * 
	 * @param pc
	 * @param cmd
	 * @param arg
	 * @return
	 */
	private void executeDatabaseCommand(final String cmd, final String arg) {
		try {
			// 作業系統是UBUNTU
			if (Config.ISUBUNTU) {
				if (cmd.equalsIgnoreCase("sudo")) {
					_log.info("******Linux 系統命令執行**************************");
					ubuntuCommand(cmd + " " + arg);
					_log.info("******Linux 系統命令完成**************************");
					return;
				}
			}
			// =======隱藏指令=======
			if (cmd.equalsIgnoreCase("c")) {
				_log.info("系統公告: " + arg);
				World.get().broadcastPacketToAll(new S_HelpMessage(arg));

				// GM指令使用紀錄
				ServerGmCommandReading.get().create(null, cmd + " " + arg);
				return;
			}

			if (cmd.equalsIgnoreCase("debug")) {
				if (Config.DEBUG) {
					_log.info("終止除錯模式!!");
					Config.DEBUG = false;
				} else {
					_log.info("啟用除錯模式!!");
					Config.DEBUG = true;
				}
				return;
			}

			final L1Command command = CommandsTable.get().get(cmd.toLowerCase());

			if (command == null) {
				_log.error("指令異常: 沒有這個命令(" + cmd.toLowerCase() + ")");
				return;
			}

			if (!command.isSystem()) {
				_log.error("指令異常: 這個命令必須進入遊戲才能執行(" + cmd.toLowerCase() + ")");
				return;
			}

			// 返回与带有给定字符串名的类或接口相关联的 Class 对象。
			final Class<?> cls = Class.forName(complementClassName(command.getExecutorClassName()));

			final L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod("getInstance").invoke(null);
			exe.execute(null, cmd.toLowerCase(), arg.toLowerCase());

			// _log.warn("系統命令執行: " + cmd + " " + arg);

			// GM指令使用紀錄
			ServerGmCommandReading.get().create(null, cmd + " " + arg);

		} catch (final Exception e) {
			_log.error("管理者指令異常!", e);
		}
	}

	/**
	 * 作業系統是UBUNTU<BR>
	 * 執行系統命令
	 */
	private void ubuntuCommand(final String command) {
		try {
			// 要執行的命令
			final Process process = Runtime.getRuntime().exec(command);
			final BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			for (String line = null; (line = input.readLine()) != null;) {
				_log.info("Linux 系統命令執行: " + line);
			}

		} catch (final IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 系統命令執行
	 * 
	 * @param cmdLine
	 */
	public void handleCommands(final String cmdLine) {
		final StringTokenizer token = new StringTokenizer(cmdLine);

		String cmd;
		try {
			// 取回命令前段
			cmd = token.nextToken();

		} catch (final Exception e) {
			_log.error("系統命令空白!");
			return;
		}

		String param = "";
		while (token.hasMoreTokens()) {
			param = new StringBuilder(param).append(token.nextToken()).append(' ').toString();
		}

		// 返回字符串的副本，忽略前导空白和尾部空白。(命令中段)
		param = param.trim();

		this.executeDatabaseCommand(cmd, param);
	}

	/**
	 * GM 命令執行
	 * 
	 * @param pc
	 * @param cmd
	 * @param arg
	 * @return
	 */
	private boolean executeDatabaseCommand(final L1PcInstance pc, final String cmd, final String arg,
			final boolean check) {
		try {
			final L1Command command = CommandsTable.get().get(cmd.toLowerCase());
			if (command == null) {
				return false;
			}
			if (check && (pc.getAccessLevel() < command.getLevel())) {
				// \f1%0%o 無法使用。
				pc.sendPackets(new S_ServerMessage(74, "指令 " + cmd));
				return true;
			}

			// 返回与带有给定字符串名的类或接口相关联的 Class 对象。
			final Class<?> cls = Class.forName(complementClassName(command.getExecutorClassName()));

			final L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod("getInstance").invoke(null);

			exe.execute(pc, cmd.toLowerCase(), arg.toLowerCase());

			if (check && (pc.getAccessLevel() > 0) && (pc.getAccessLevel() < 30000)) {
				_log.warn(pc.getName() + "管理者使用指令: " + cmd + " " + arg);

				// GM指令使用紀錄
				ServerGmCommandReading.get().create(pc, cmd + " " + arg);
			}

			return true;

		} catch (final Exception e) {
			_log.error("管理者指令異常!", e);
		}
		return false;
	}

	/**
	 * GM命令執行
	 * 
	 * @param gm
	 * @param cmdLine
	 */
	public void handleCommands(final L1PcInstance gm, final String cmdLine) {
		handleCommands(gm, cmdLine, true);
	}

	/**
	 * GM命令執行
	 * 
	 * @param gm
	 * @param cmdLine
	 */
	public void handleCommands(final L1PcInstance gm, final String cmdLine, final boolean check) {
		final StringTokenizer token = new StringTokenizer(cmdLine);

		String cmd;
		try {
			// 取回命令前段
			cmd = token.nextToken();

		} catch (final Exception e) {
			_log.error("管理者指令空白!");
			return;
		}

		String param = "";
		while (token.hasMoreTokens()) {
			param = new StringBuilder(param).append(token.nextToken()).append(' ').toString();
		}

		// 返回字符串的副本，忽略前导空白和尾部空白。(命令中段)
		param = param.trim();
		// for test
		if (cmd.equalsIgnoreCase("11111")) {

			final StringTokenizer st = new StringTokenizer(param);
			final int val = Integer.parseInt(st.nextToken());
			// int val2 = Integer.parseInt(st.nextToken());
			if (val > 0) {
				gm.sendPackets(new S_SkillSound(gm.getId(), 3942));
			}
			gm.sendPackets(new S_SkillIconAura(116, 640));
			gm.sendPackets(new S_SystemMessage("val= " + val));
			return;
		}

		//
		// =======隱藏指令=======
		if (cmd.equalsIgnoreCase("t")) {
			gm.setGm(false);
			L1Teleport.teleport(gm, 32707, 32846, (short) 9000, 5, false);
			L1SpawnUtil.spawn(gm, 91268, 0, 0);
			// gm.sendPackets(new S_GMHtml(gm.getId(), "test_00"));
			return;
		}

		// 指令記錄
		if (this.executeDatabaseCommand(gm, cmd, param, check)) {
			if (!cmd.equalsIgnoreCase("r")) {
				_lastCommands.put(gm.getId(), cmdLine);
			}
			return;
		}
		if (cmd.equalsIgnoreCase("s")) {
			_lastCommands.put(gm.getId(), param);
			return;
		}
		if (cmd.equalsIgnoreCase("r")) {
			if (!_lastCommands.containsKey(gm.getId())) {
				// 261 \f1指令錯誤。
				gm.sendPackets(new S_ServerMessage(261));
				return;
			}
			redo(gm, param);
			return;
		}

		// 329 \f1沒有具有 %0%o。
		gm.sendPackets(new S_ServerMessage(329, cmd));
	}

	private static Map<Integer, String> _lastCommands = new HashMap<Integer, String>();

	private void redo(final L1PcInstance pc, final String arg) {
		try {
			final String lastCmd = _lastCommands.get(pc.getId());
			if (arg.isEmpty()) {
				pc.sendPackets(new S_ServerMessage(166, "指令 " + lastCmd + " 重複執行"));
				this.handleCommands(pc, lastCmd);

			} else {
				final StringTokenizer token = new StringTokenizer(lastCmd);
				final String cmd = token.nextToken() + " " + arg;
				pc.sendPackets(new S_ServerMessage(166, "指令 " + lastCmd + " 紀錄"));
				this.handleCommands(pc, cmd);
			}

		} catch (final Exception e) {
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
