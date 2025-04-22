package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.CharBookReading;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 要求刪除記憶座標
 * 
 * @author daien
 */
public class C_DeleteBookmark extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_DeleteBookmark.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final String bookmarkname = readS();

			if (!bookmarkname.isEmpty()) {
				final L1PcInstance pc = client.getActiveChar();
				CharBookReading.get().deleteBookmark(pc, bookmarkname);
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
