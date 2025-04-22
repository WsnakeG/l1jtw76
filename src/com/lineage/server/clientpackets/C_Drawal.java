package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.world.WorldClan;

/**
 * 要求領出資金
 * 
 * @author daien
 */
public class C_Drawal extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Drawal.class);

	/*
	 * public C_Drawal() { } public C_Drawal(final byte[] abyte0, final
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

			@SuppressWarnings("unused")
			final int objid = readD();

			long count = readD();
			if (count > Integer.MAX_VALUE) {
				count = Integer.MAX_VALUE;
			}
			count = Math.max(0, count);

			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				final int castle_id = clan.getCastleId();
				if (castle_id != 0) {
					// 只有王族角色才可以提領資金 by terry0412
					if (!pc.isCrown()) {
						// 血盟君主才可使用此命令。
						pc.sendPackets(new S_ServerMessage(518));
						return;
					}
					// 不是血盟領導人
					if (pc.getId() != clan.getLeaderId()) {
						return;
					}

					final L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
					final L1ItemInstance item = ItemTable.get().createItem(L1ItemId.ADENA);

					// 避免被負數洗錢 by terry0412
					final long money = l1castle.getPublicMoney() - count;
					if ((item != null) && (money >= 0)) {
						l1castle.setPublicMoney(money);
						CastleReading.get().updateCastle(l1castle);
						if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
							pc.getInventory().storeItem(L1ItemId.ADENA, count);

						} /*
							 * else { World.get().getInventory( pc.getX(),
							 * pc.getY(), pc.getMapId()
							 * ).storeItem(L1ItemId.ADENA, count); }
							 */

						// \f1%0%s 給你 %1%o 。
						pc.sendPackets(new S_ServerMessage(143, "$457", "$4" + " (" + count + ")"));
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
