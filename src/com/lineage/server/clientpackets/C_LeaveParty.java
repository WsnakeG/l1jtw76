package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.quest.Chapter01;
import com.lineage.data.quest.Chapter02;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 要求脫離隊伍
 * 
 * @author daien
 */
public class C_LeaveParty extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_LeaveParty.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			// 副本任務進行中 by terry0412
			if ((pc.getMapId() == Chapter01.MAPID) || (pc.getMapId() == Chapter02.MAPID)) {
				return;
			}
			if (pc.isInParty()) {// 隊伍中
				pc.getParty().leaveMember(pc);

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
