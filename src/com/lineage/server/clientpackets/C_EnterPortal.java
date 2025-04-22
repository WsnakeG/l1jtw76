package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.DungeonTable;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 要求座標傳送
 * 
 * @author daien
 */
public class C_EnterPortal extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_EnterPortal.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}

			if (pc.isDead()) { // 死亡
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			final int locx = readH();
			final int locy = readH();

			if (pc.isTeleport()) {
				return;
			}
			// 執行座標移動
			DungeonTable.get().dg(locx, locy, pc.getMap().getId(), pc);

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
