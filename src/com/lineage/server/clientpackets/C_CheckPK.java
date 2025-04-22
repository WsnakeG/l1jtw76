package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 要求查詢PK次數
 * 
 * @author daien
 */
public class C_CheckPK extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CheckPK.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			if (client == null) {
				return;
			}

			final L1PcInstance pc = client.getActiveChar();

			if (pc == null) {
				return;
			}

			final String count = String.valueOf(pc.get_PKcount());
			// 你的PK次數為%0次。
			pc.sendPackets(new S_ServerMessage(562, count));

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
