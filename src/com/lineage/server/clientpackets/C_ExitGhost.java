package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 要求離開鬼魂模式
 * 
 * @author daien
 */
public class C_ExitGhost extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ExitGhost.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (!pc.isGhost()) {
				return;
			}

			pc.makeReadyEndGhost();

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