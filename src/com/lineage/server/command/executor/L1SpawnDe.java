package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.DeNameTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.DeName;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * 召喚虛擬人物(參數:數量 / 範圍)
 * 
 * @author dexc
 */
public class L1SpawnDe implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1SpawnDe.class);

	private L1SpawnDe() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SpawnDe();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			if (arg.startsWith("obj")) {// 指定虛擬人物編號
				final StringTokenizer tok = new StringTokenizer(arg);
				@SuppressWarnings("unused")
				final String obj = tok.nextToken();// OBJ指令
				final int objid = Integer.parseInt(tok.nextToken());
				int time = 0;
				try {
					time = Integer.parseInt(tok.nextToken());
				} catch (final Exception e) {
				}

				final int npcid = 81002;
				final L1Npc npc = NpcTable.get().getTemplate(npcid);
				if (npc != null) {
					final DeName de = DeNameTable.get().getDeName(objid);
					L1SpawnUtil.spawn(pc, de, time);
				}
				return;
			}
			if (arg.startsWith("s")) {// 指定虛擬人物直接執行商店
				final L1DeInstance de = L1SpawnUtil.spawn(pc, null, 0);
				de.start_shop();
				return;
			}

			final StringTokenizer tok = new StringTokenizer(arg);

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

			final int npcid = 81002;

			// 取回NPC資料
			final L1Npc npc = NpcTable.get().getTemplate(npcid);
			if (npc == null) {
				pc.sendPackets(new S_SystemMessage("找不到該npc。"));
				return;
			}

			if (count > 100) {
				pc.sendPackets(new S_SystemMessage("一次召喚數量不能超過100。"));
				return;
			}

			// 召喚數量大於20使用召喚線程
			if (count > 20) {
				final DeRunnable deRunnable = new DeRunnable(pc, npcid, randomrange, count);
				GeneralThreadPool.get().execute(deRunnable);

			} else {
				for (int i = 0; i < count; i++) {
					L1SpawnUtil.spawn(pc, npcid, randomrange, 0);
				}
			}

			final String msg = String.format("%s(%d) (%d) 召喚。 (範圍:%d)", npc.get_name(), npcid, count,
					randomrange);
			pc.sendPackets(new S_SystemMessage(msg));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}

	/**
	 * 執行召喚DE線程
	 * 
	 * @author daien
	 */
	private class DeRunnable implements Runnable {

		private final L1PcInstance _pc;

		private final int _npcid;

		private final int _randomrange;

		private final int _count;

		/**
		 * 執行召喚DE線程
		 * 
		 * @param pc 執行的GM
		 * @param npcid 召喚的NPC編號
		 * @param randomrange 召喚範圍
		 * @param count 召喚數量
		 */
		private DeRunnable(final L1PcInstance pc, final int npcid, final int randomrange, final int count) {
			_pc = pc;
			_npcid = npcid;
			_randomrange = randomrange;
			_count = count;
		}

		@Override
		public void run() {
			try {
				for (int i = 0; i < _count; i++) {
					if (_pc.getMapId() == 5300) {// 釣魚台
						L1SpawnUtil.spawn(_pc, null, 0);

					} else {
						L1SpawnUtil.spawn(_pc, _npcid, _randomrange, 0);
					}
					Thread.sleep(1);
				}

			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
