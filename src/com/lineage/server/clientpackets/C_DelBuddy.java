package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.BuddyTable;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 要求刪除朋友名單
 * 
 * @author daien
 */
public class C_DelBuddy extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_DelBuddy.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			final String charName = readS().toLowerCase();

			if (charName.isEmpty()) {
				return;
			}
			BuddyTable.getInstance().removeBuddy(pc.getId(), charName);

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
