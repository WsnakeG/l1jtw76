package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 要求撿取物品
 * 
 * @author daien
 */
public class C_PickUpItem extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_PickUpItem.class);

	/*
	 * public C_PickUpItem() { } public C_PickUpItem(final byte[] abyte0, final
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

			if (pc.isPrivateShop()) { // 商店村模式
				return;
			}

			if (pc.isInvisble()) { // 隱身狀態
				return;
			}

			if (pc.isInvisDelay()) { // 隱身延遲
				return;
			}

			final int x = readH();
			final int y = readH();
			final int objectId = readD();
			long pickupCount = readD();
			if (pickupCount > Integer.MAX_VALUE) {
				pickupCount = Integer.MAX_VALUE;
			}
			pickupCount = Math.max(0, pickupCount);
			final L1Inventory groundInventory = World.get().getInventory(x, y, pc.getMapId());

			final L1Object object = groundInventory.getItem(objectId);
			if ((object != null) && !pc.isDead()) {
				final L1ItemInstance item = (L1ItemInstance) object;
				if (item.getCount() <= 0) {
					return;
				}
				if ((item.getItemOwnerId() != 0) && (pc.getId() != item.getItemOwnerId())) {
					// 道具取得失敗。
					pc.sendPackets(new S_ServerMessage(623));
					return;
				}
				if (pc.getLocation().getTileLineDistance(item.getLocation()) > 3) {
					return;
				}
				item.set_showId(-1);
				// 容量重量確認
				if (pc.getInventory().checkAddItem(item, pickupCount) == L1Inventory.OK) {
					if ((item.getX() != 0) && (item.getY() != 0)) {
						groundInventory.tradeItem(item, pickupCount, pc.getInventory());
						// 改變亮度
						pc.turnOnOffLight();

						// 改變面向
						pc.setHeading(pc.targetDirection(item.getX(), item.getY()));

						// 因應改變面向 使用物件攻擊封包送出動作以及面向
						// 不需要對自己送
						// pc.sendPackets(new S_ChangeHeading(pc));
						// 送出封包(動作)
						// pc.sendPacketsAll(new S_DoActionGFX(pc.getId(),
						// ActionCodes.ACTION_Pickup));
						// pc.sendPackets(new S_AttackPickUpItem(pc, objectId));
						if (!pc.isGmInvis()) {
							pc.broadcastPacketAll(new S_ChangeHeading(pc));
							// 送出封包(動作)
							pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Pickup));
							// pc.broadcastPacketAll(new S_AttackPickUpItem(pc,
							// objectId));
						}
						WriteLogTxt.Recording("拾取物品記錄",
								"人物:" + pc.getName() + "拾取 +" + item.getEnchantLevel() + " " + item.getName()
										+ "(" + item.getCount() + ")" + " ItmeID:" + item.getItemId()
										+ " 物品OBJID:" + item.getId());
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
