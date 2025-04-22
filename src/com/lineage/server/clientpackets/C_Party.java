package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Party;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 要求隊伍名單
 * 
 * @author daien
 */
public class C_Party extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Party.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			if (pc.isGhost()) {
				return;
			}

			final L1Party party = pc.getParty();
			if (pc.isInParty()) {
				pc.sendPackets(new S_Party("party", pc.getId(), party.getLeader().getName(),
						party.getMembersNameList()));

			} else {
				// 425 您並沒有參加任何隊伍。
				pc.sendPackets(new S_ServerMessage(425));

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
