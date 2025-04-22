package com.lineage.server.command.executor;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * 召喚NPC(參數:NPCID - 數量 - 範圍)
 * 
 * @author dexc
 */
public class L1SpawnCmd implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1SpawnCmd.class);

	private L1SpawnCmd() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SpawnCmd();
	}

	private void sendErrorMessage(final L1PcInstance pc, final String cmdName) {
		final String errorMsg = cmdName + " npcid|name [數量] [範圍]。";
		pc.sendPackets(new S_SystemMessage(errorMsg));
	}

	/**
	 * 取回NPCID
	 * 
	 * @param nameId
	 * @return
	 */
	private int parseNpcId(final String nameId) {
		int npcid = 0;
		try {
			// 依照ID取回
			npcid = Integer.parseInt(nameId);

		} catch (final NumberFormatException e) {
			// 依照名稱取回
			npcid = NpcTable.get().findNpcIdByNameWithoutSpace(nameId);
		}
		return npcid;
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			final String nameId = tok.nextToken();

			// 召喚數量
			int count = 1;
			if (tok.hasMoreTokens()) {
				count = Integer.parseInt(tok.nextToken());
			}

			// 召喚範圍
			int randomrange = 0;
			if (tok.hasMoreTokens()) {
				randomrange = Integer.parseInt(tok.nextToken(), 10);
			}

			// 取回NPCID
			final int npcid = parseNpcId(nameId);

			// 取回NPC資料
			final L1Npc npc = NpcTable.get().getTemplate(npcid);
			if (npc == null) {
				pc.sendPackets(new S_SystemMessage("找不到該npc。"));
				return;
			}

			if (count > 1000) {
				pc.sendPackets(new S_SystemMessage("一次召喚數量不能超過1000。"));
				return;
			}

			// 召喚數量大於100使用召喚線程
			if (count > 100) {
				final SpawnRunnable spawnRunnable = new SpawnRunnable(pc, npcid, randomrange, count);
				GeneralThreadPool.get().execute(spawnRunnable);

			} else {
				for (int i = 0; i < count; i++) {
					L1SpawnUtil.spawn(pc, npcid, randomrange, 0);
				}
			}

			final String msg = String.format("%s(%d) (%d) 召喚。 (範圍:%d)", npc.get_name(), npcid, count,
					randomrange);
			pc.sendPackets(new S_SystemMessage(msg));

		} catch (final NoSuchElementException e) {
			sendErrorMessage(pc, cmdName);

		} catch (final NumberFormatException e) {
			sendErrorMessage(pc, cmdName);

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}

	/**
	 * 執行召喚NPC線程
	 * 
	 * @author daien
	 */
	private class SpawnRunnable implements Runnable {

		private final L1PcInstance _pc;

		private final int _npcid;

		private final int _randomrange;

		private final int _count;

		/**
		 * 執行召喚NPC線程
		 * 
		 * @param pc 執行的GM
		 * @param npcid 召喚的NPC編號
		 * @param randomrange 召喚範圍
		 * @param count 召喚數量
		 */
		private SpawnRunnable(final L1PcInstance pc, final int npcid, final int randomrange,
				final int count) {
			_pc = pc;
			_npcid = npcid;
			_randomrange = randomrange;
			_count = count;
		}

		@Override
		public void run() {
			try {
				// int x = 0;
				for (int i = 0; i < _count; i++) {
					L1SpawnUtil.spawn(_pc, _npcid, _randomrange, 0);
					Thread.sleep(1);
					// System.out.println(x++);
				}

			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
