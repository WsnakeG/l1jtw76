package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.world.WorldClan;

/**
 * 要求變更領地稅率
 * 
 * @author daien
 */
public class C_TaxRate extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_TaxRate.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final int i = readD();
			final int j = readC();

			final L1PcInstance player = client.getActiveChar();
			if (i == player.getId()) {
				final L1Clan clan = WorldClan.get().getClan(player.getClanname());
				if (clan != null) {
					final int castle_id = clan.getCastleId();
					if (castle_id != 0) { // 城主クラン
						final L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
						if ((j >= 10) && (j <= 50)) {
							l1castle.setTaxRate(j);
							CastleReading.get().updateCastle(l1castle);
						}
					}
				}
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
