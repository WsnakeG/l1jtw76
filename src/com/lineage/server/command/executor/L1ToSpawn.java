package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.EventSpawnTable;
import com.lineage.server.datatables.NpcSpawnTable;
import com.lineage.server.datatables.QuesttSpawnTable;
import com.lineage.server.datatables.SpawnTable;
import com.lineage.server.datatables.lock.SpawnBossReading;
import com.lineage.server.model.L1Spawn;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 移動往指定召喚編號的NPC所在位置(參數:召喚表編號)
 * 
 * @author dexc
 */
public class L1ToSpawn implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1ToSpawn.class);

	private L1ToSpawn() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ToSpawn();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {

			final StringTokenizer st = new StringTokenizer(arg);
			final int id = Integer.parseInt(st.nextToken());

			// NPC召喚列表中物件
			L1Spawn spawn = NpcSpawnTable.get().getTemplate(id);

			// MOB召喚列表中物件
			if (spawn == null) {
				spawn = SpawnTable.get().getTemplate(id);
			}

			// EVENT NPC/MOB召喚列表中物件
			if (spawn == null) {
				spawn = EventSpawnTable.get().getTemplate(id);
			}

			// BOSS召喚列表中物件
			if (spawn == null) {
				spawn = SpawnBossReading.get().getTemplate(id);
			}

			// QUEST NPC/MOB召喚列表中物件
			if (spawn == null) {
				spawn = QuesttSpawnTable.get().getTemplate(id);
			}

			if (spawn != null) {
				L1Teleport.teleport(pc, spawn.getTmpLocX(), spawn.getTmpLocY(), spawn.getTmpMapid(), 5,
						false);
				pc.sendPackets(new S_SystemMessage("移動至指定召喚編號: " + id));

			} else {
				pc.sendPackets(new S_SystemMessage("沒有這個編號的召喚點: " + id));
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName(), e);
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
