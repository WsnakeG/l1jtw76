package com.lineage.server.clientpackets;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_WarTime;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.world.WorldClan;

/**
 * 要求決定下次圍城時間
 * 
 * @author daien
 */
public class C_ChangeWarTime extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Buddy.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				final int castle_id = clan.getCastleId();
				if (castle_id != 0) { // 城主クラン
					final L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
					final Calendar cal = l1castle.getWarTime();
					pc.sendPackets(new S_WarTime(cal));
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
