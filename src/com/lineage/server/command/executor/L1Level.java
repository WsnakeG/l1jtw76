package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.IntRange;
import com.lineage.server.utils.RangeInt;
import com.lineage.server.world.World;

/**
 * 變更指定人物等級
 * 
 * @author Roy 130813
 */
public class L1Level implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Level.class);

	private L1Level() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Level();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			final String char_name = tok.nextToken();
			int level = 50; // 預設等級
			if (tok.hasMoreTokens()) {
				level = Integer.parseInt(tok.nextToken());
			}
			L1PcInstance target = null;
			if (char_name.equalsIgnoreCase("me")) {
				target = pc;
			} else {
				target = World.get().getPlayer(char_name);
			}

			if (level == target.getLevel()) {
				return;
			}

			if (!IntRange.includes(level, 1, 127)) {
				pc.sendPackets(new S_SystemMessage("請在1-127範圍內指定"));
				return;
			}
			if (!RangeInt.includes(level, 1, ExpTable.MAX_LEVEL)) {
				pc.sendPackets(new S_SystemMessage("範圍限制 1~" + ExpTable.MAX_LEVEL));
				return;
			}
			target.setExp_Direct(ExpTable.getExpByLevel(level));

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage("請輸入 : " + cmdName + " 玩家ID、指定的等級。"));
		}
	}
}
