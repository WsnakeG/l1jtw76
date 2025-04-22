package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * @author roy <BR>
 *         GM模式傳送 (右鍵點兩下使用)
 */
public class C_GMTeleport extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_GMTeleport.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		final L1PcInstance pc = client.getActiveChar();
		try {
			if (!pc.isGm()) {
				return;
			}
			// 資料載入
			read(decrypt);
			final int mapid = readH();
			final int x = readH();
			final int y = readH();
			L1Teleport.teleport(pc, x, y, (short) mapid, pc.getHeading(), false);
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
