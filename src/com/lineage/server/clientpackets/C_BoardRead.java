package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.WorldNpc;

/**
 * 要求閱讀佈告欄訊息
 * 
 * @author dexc
 */
public class C_BoardRead extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_BoardRead.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final int objId = readD();
			final int topicNumber = readD();

			final L1NpcInstance npc = WorldNpc.get().map().get(objId);
			if (npc == null) {
				return;
			}

			final L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}

			if (npc.ACTION != null) {
				npc.ACTION.action(pc, npc, "r", topicNumber);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

}
