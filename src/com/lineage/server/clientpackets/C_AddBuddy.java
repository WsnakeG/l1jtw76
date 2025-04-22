package com.lineage.server.clientpackets;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.BuddyTable;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.model.L1Buddy;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 要求新增好友
 * 
 * @author dexc
 */
public class C_AddBuddy extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_AddBuddy.class);

	/*
	 * public C_AddBuddy() { } public C_AddBuddy(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final String charName = readS().toLowerCase();

			final L1PcInstance pc = client.getActiveChar();

			final BuddyTable buddyTable = BuddyTable.getInstance();
			
			// 取回PC好友名單
			final L1Buddy buddyList = buddyTable.getBuddyTable(pc.getId());
			
			if (charName.equalsIgnoreCase(pc.getName())) {
				return;
			} else if (buddyList.containsName(charName)) {
				pc.sendPackets(new S_ServerMessage(1052, charName)); // %s
																		// は既に登録されています。
				return;
			}

			final int objid = CharObjidTable.get().charObjid(charName);
			if (objid != 0) {
				final String name = CharObjidTable.get().isChar(objid);
				
				buddyList.add(objid, name);
				buddyTable.addBuddy(pc.getId(), objid, name);
				
				//BuddyReading.get().addBuddy(pc.getId(), objid, name);
				return;
			}

			// 沒有叫%0的人。
			pc.sendPackets(new S_ServerMessage(109, charName));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}
}
