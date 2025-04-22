package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 要求完成交易(個人)
 * 
 * @author daien
 */
public class C_TradeOK extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_TradeOK.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance player = client.getActiveChar();
			final L1PcInstance trading_partner = (L1PcInstance) World.get().findObject(player.getTradeID());
			if (trading_partner != null) {
				player.setTradeOk(true);

				if (player.getTradeOk() && trading_partner.getTradeOk()) {
					// (180 - 16)個未満ならトレード成立。
					// 本来は重なるアイテム（アデナ等）を既に持っている場合を考慮しないければいけない。
					if ((player.getInventory().getSize() < (180 - 20))
							&& (trading_partner.getInventory().getSize() < (180 - 20))) {
						final L1Trade trade = new L1Trade();
						trade.tradeOK(player);

					} else {
						// \f1一個角色最多可攜帶180個道具。
						player.sendPackets(new S_ServerMessage(263));
						// \f1一個角色最多可攜帶180個道具。
						trading_partner.sendPackets(new S_ServerMessage(263));
						final L1Trade trade = new L1Trade();
						trade.tradeCancel(player);
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
