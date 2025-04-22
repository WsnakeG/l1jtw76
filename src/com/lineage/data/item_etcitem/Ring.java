package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 結婚戒指(銀)40901<br>
 * 結婚戒指(金)40902<br>
 * 結婚戒指(藍寶石)40903<br>
 * 結婚戒指(綠寶石)40904<br>
 * 結婚戒指(紅寶石)40905<br>
 * 結婚戒指(鑽石)40906<br>
 * 西瑪戒指40907<br>
 * 歐林戒指40908<br>
 */
public class Ring extends ItemExecutor {

	/**
	 *
	 */
	private Ring() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Ring();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		L1PcInstance partner = null;
		boolean partner_stat = false;
		// 不列入判斷的地形
		switch (pc.getMapId()) {
		default:
			// 補上缺少的判斷 by terry0412
			if (!pc.getMap().isEscapable()) {
				// 這附近的能量影響到瞬間移動。在此地無法使用瞬間移動。
				pc.sendPackets(new S_ServerMessage(647));
				return;
			}

			// 是副本專用地圖
			if (QuestMapTable.get().isQuestMap(pc.getMapId())) {
				return;
			}
			if (pc.getPartnerId() != 0) { // 結婚中
				partner = (L1PcInstance) World.get().findObject(pc.getPartnerId());
				if ((partner != null) && (partner.getPartnerId() != 0)
						&& (pc.getPartnerId() == partner.getId()) && (partner.getPartnerId() == pc.getId())) {
					partner_stat = true;
				}

			} else {
				pc.sendPackets(new S_ServerMessage(662)); // \f1你(你)目前未婚。
				return;
			}

			if (partner_stat) {
				final boolean castle_area = L1CastleLocation.checkInAllWarArea(partner.getX(), partner.getY(),
						partner.getMapId());
				if (((partner.getMapId() == 0) || (partner.getMapId() == 4) || (partner.getMapId() == 304))
						&& (castle_area == false)) {
					L1Teleport.teleport(pc, partner.getX(), partner.getY(), partner.getMapId(), 5, true);
				} else {
					pc.sendPackets(new S_ServerMessage(547)); // \f1你的情人在你无法传送前往的地区。
				}

			} else {
				pc.sendPackets(new S_ServerMessage(546)); // \f1你的情人目前不在线上。
			}
			break;
		}
	}
}
