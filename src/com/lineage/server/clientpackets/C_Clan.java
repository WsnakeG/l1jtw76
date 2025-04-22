package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Emblem;
import com.lineage.server.templates.L1EmblemIcon;

/**
 * 要求更新盟輝
 * 
 * @author DaiEn
 */
public class C_Clan extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Clan.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			final int emblemId = readD();
			int clanId = 0;
			for (final L1Clan clan : ClanReading.get().get_clans().values()) {
				if (clan.getEmblemId() == emblemId) {
					clanId = clan.getClanId();
					break;
				}
			}
			if (clanId <= 0) {
				return;
			}

			final L1EmblemIcon emblemIcon = ClanEmblemReading.get().get(clanId);
			if (emblemIcon != null) {
				pc.sendPackets(new S_Emblem(emblemId, emblemIcon.get_clanIcon()));
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
