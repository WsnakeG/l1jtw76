package com.lineage.server.clientpackets;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.ClanMembersTable;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ClanUpdate;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.WorldClan;

/**
 * 要求創立血盟
 * 
 * @author daien
 */
public class C_CreateClan extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CreateClan.class);

	/*
	 * public C_CreateClan() { } public C_CreateClan(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

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

			final String s = readS();

			final int i = s.length();

			if (i > 16) {
				// 98 \f1血盟的名稱太長。
				pc.sendPackets(new S_ServerMessage(98));
				return;
			}

			if (pc.isCrown()) { // 王族
				if (pc.getClanid() == 0) {
					final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
					for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
						final L1Clan clan = iter.next();
						if (clan.getClanName().equalsIgnoreCase(s)) {
							// \f1那個血盟名稱已經存在。
							pc.sendPackets(new S_ServerMessage(99));
							return;
						}
					}

					if (pc.getInventory().consumeItem(40308, 30000)) {
						final L1Clan clan = ClanReading.get().createClan(pc, s);
						ClanMembersTable.getInstance().newMember(pc);
						if (clan != null) {
							// 84 創立\f1%0 血盟。
							pc.sendPackets(new S_ServerMessage(84, s));
							// 創立血盟更新血盟數據
							pc.sendPackets(new S_ClanUpdate(pc.getId(), pc.getClanname(), pc.getClanRank()));
							// 加入建立CLAN OBJID資料
							CharObjidTable.get().addClan(clan.getClanId(), clan.getClanName());
						}

					} else {
						// 189：\f1金幣不足。
						pc.sendPackets(new S_ServerMessage(189));
					}

				} else {
					// 86 \f1已經創立血盟。
					pc.sendPackets(new S_ServerMessage(86));
				}

			} else {
				// 85 \f1王子和公主才可創立血盟。
				pc.sendPackets(new S_ServerMessage(85));
			}
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));

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
