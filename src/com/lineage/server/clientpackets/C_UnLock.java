package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Lock;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.utils.Teleportation;

/**
 * 要求座標異常重整
 * 
 * @author dexc
 */
public class C_UnLock extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_UnLock.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);
			// int i = 0;
			final int type = readC();
			// System.out.println("要求座標異常重整:"+type);
			final L1PcInstance pc = client.getActiveChar();
			if (type == 127) {
				final int oleLocx = pc.getX();
				final int oleLocy = pc.getY();
				// 設置原始座標
				pc.setOleLocX(oleLocx);
				pc.setOleLocY(oleLocy);
				// 送出座標異常
				pc.sendPackets(new S_Lock());

			} else {
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				pc.setTeleportX(pc.getOleLocX());
				pc.setTeleportY(pc.getOleLocY());
				pc.setTeleportMapId(pc.getMapId());
				pc.setTeleportHeading(pc.getHeading());
				// 傳送回原始座標
				Teleportation.teleportation(pc);
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
