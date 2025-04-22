package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.WorldNpc;

/**
 * 要求讀取公佈欄/拍賣公告
 * 
 * @author dexc
 */
public class C_Board extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Board.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final int objId = readD();

			final L1NpcInstance npc = WorldNpc.get().map().get(objId);
			if (npc == null) {
				return;
			}

			final L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}

			if (npc.ACTION != null) {
				npc.ACTION.talk(pc, npc);
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
