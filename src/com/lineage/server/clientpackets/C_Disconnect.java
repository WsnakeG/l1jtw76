package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;

/**
 * 要求離開遊戲
 * 
 * @author daien
 */
public class C_Disconnect extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Disconnect.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {

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
