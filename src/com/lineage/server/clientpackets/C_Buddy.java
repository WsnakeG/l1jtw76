package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.BuddyTable;
import com.lineage.server.model.L1Buddy;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BuddyList;

/**
 * 要求查詢朋友名單
 * 
 * @author daien
 */
public class C_Buddy extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Buddy.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			final L1PcInstance pc = client.getActiveChar();
			
			final L1Buddy buddy = BuddyTable.getInstance().getBuddyTable(pc.getId());
			
			//pc.sendPackets(new S_Buddy(pc.getId(), buddy));
			pc.sendPackets(new S_BuddyList(buddy.getBuddyListNames()));

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
