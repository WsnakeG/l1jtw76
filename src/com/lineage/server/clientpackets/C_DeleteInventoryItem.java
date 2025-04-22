package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 要求刪除物品
 * 
 * @author daien
 */
public class C_DeleteInventoryItem extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_DeleteInventoryItem.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}
			final int itemcount = readD();
			for (int i = 0; i < itemcount; i++) {
				final int itemObjectId = readD();

				final L1ItemInstance item = pc.getInventory().getItem(itemObjectId);
				// 物品為空
				if (item == null) {
					return;
				}

				long itemCount = readD();
				if (itemCount <= 0) {
					itemCount = item.getCount();
				}

				if (itemCount > item.getCount()) {
					_log.info("人物:" + pc.getName() + " 刪除物品數量超過物品本身數量:(" + itemCount + " > " + item.getCount()
							+ ")");
					return;
				}

				// 執行人物不是GM
				if (!pc.isGm()) {
					if (item.getItem().isCantDelete()) {
						// 125 \f1你不能夠放棄此樣物品。
						pc.sendPackets(new S_ServerMessage(125));
						return;
					}
				}

				// 寵物
				final Object[] petlist = pc.getPetList().values().toArray();
				for (final Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						final L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							// 125 \f1你不能夠放棄此樣物品。
							pc.sendPackets(new S_ServerMessage(125));
							return;
						}
					}
				}

				// 取回娃娃
				if (pc.getDoll(item.getId()) != null) {
					// 1,181：這個魔法娃娃目前正在使用中。
					pc.sendPackets(new S_ServerMessage(1181));
					return;
				}
				// 取回娃娃
				if (pc.get_power_doll() != null) {
					if (pc.get_power_doll().getItemObjId() == item.getId()) {
						// 1,181：這個魔法娃娃目前正在使用中。
						pc.sendPackets(new S_ServerMessage(1181));
						return;
					}
				}

				if (item.isEquipped()) {
					// 125 \f1你不能夠放棄此樣物品。
					pc.sendPackets(new S_ServerMessage(125));
					return;
				}
				if (item.getBless() >= 128) { // 封印的装備
					// 210 \f1%0%d是不可轉移的…
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
					return;
				}

				_log.info("人物:" + pc.getName() + "刪除物品" + (item.getItem().getName() + "(" + itemCount + ")")
						+ " 物品OBJID:" + item.getId());
				pc.getInventory().removeItem(item, itemCount);
				pc.turnOnOffLight();
				WriteLogTxt.Recording("刪除物品记录", "人物:" + pc.getName() + "刪除物品" + item.getLogName() + " ItmeID:"
						+ item.getItemId() + " 物品OBJID:" + item.getId());
			}
		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
