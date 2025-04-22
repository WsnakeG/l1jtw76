package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 刪除產生動畫物件(參數:動畫編號)
 * 
 * @author dexc
 */
public class L1GfxIdDel implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1GfxIdDel.class);

	private L1GfxIdDel() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GfxIdDel();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			for (final L1Object object : World.get().getObject()) {
				if (object instanceof L1NpcInstance) {
					final L1NpcInstance npc = (L1NpcInstance) object;
					if (npc.getNpcId() == 50000) {
						npc.deleteMe();
					}
				}
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
