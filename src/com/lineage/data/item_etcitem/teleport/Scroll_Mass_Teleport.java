package com.lineage.data.item_etcitem.teleport;

import java.util.ArrayList;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharBookReading;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1BookMark;
import com.lineage.server.world.World;

/**
 * <font color=#00800>全體傳送術的卷軸</font><BR>
 * Scroll of Mass Teleport
 * 
 * @author dexc
 */
public class Scroll_Mass_Teleport extends ItemExecutor {

	/**
	 *
	 */
	private Scroll_Mass_Teleport() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Scroll_Mass_Teleport();
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
		// 所在地圖編號
		final int mapID = data[0];
		// 記憶座標點排序
		final int btele = data[1];

		// 所在位置 是否允許傳送
		final boolean isTeleport = L1WorldMap.get().getMap((short) mapID).isEscapable();
		if (!isTeleport) {
			// 647 這附近的能量影響到瞬間移動。在此地無法使用瞬間移動。
			pc.sendPackets(new S_ServerMessage(647));
			// 解除傳送鎖定
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

		} else {
			final L1BookMark bookm = CharBookReading.get().getBookMark(pc, btele);

			// 取出記憶座標
			if (bookm != null) {
				// 刪除道具
				pc.getInventory().removeItem(item, 1);
				// 取得3格範圍內物件相同副本ID物件
				final ArrayList<L1Object> objList = World.get().getVisiblePoint(pc.getLocation(), 3,
						pc.get_showId());

				for (final L1Object tgObj : objList) {
					if (tgObj instanceof L1PcInstance) {
						final L1PcInstance tgPc = (L1PcInstance) tgObj;
						if (tgPc.isDead()) {
							continue;
						}
						if (tgPc.getClanid() == 0) {
							continue;
						}
						// 血盟成員傳送
						if (tgPc.getClanid() == pc.getClanid()) {
							// 商店村模式
							if (!tgPc.isPrivateShop()) {
								// 解除魔法技能绝对屏障
								L1BuffUtil.cancelAbsoluteBarrier(tgPc);
								if (!tgPc.is_ask_teleport()) {
									L1Teleport.teleport(tgPc, bookm.getLocX(), bookm.getLocY(),
											bookm.getMapId(), 5, true);
								} else {
									tgPc.setTeleportX(bookm.getLocX());
									tgPc.setTeleportY(bookm.getLocY());
									tgPc.setTeleportMapId(bookm.getMapId());
									// 你的血盟成員想要傳送你。你答應嗎？(Y/N)
									tgPc.sendPackets(new S_Message_YN(748));
								}
							}
						}
					}
				}
				// 自身的傳送
				L1Teleport.teleport(pc, bookm.getLocX(), bookm.getLocY(), bookm.getMapId(), 5, true);

			} else {
				// 刪除道具
				pc.getInventory().removeItem(item, 1);

				// 取得座標值
				final L1Location newLocation = pc.getLocation().randomLocation(200, true);

				final int newX = newLocation.getX();
				final int newY = newLocation.getY();
				final short newMapId = (short) newLocation.getMapId();

				// 取得3格範圍內物件
				final ArrayList<L1Object> objList = World.get().getVisiblePoint(pc.getLocation(), 3,
						pc.get_showId());

				for (final L1Object tgObj : objList) {
					if (tgObj instanceof L1PcInstance) {
						final L1PcInstance tgPc = (L1PcInstance) tgObj;
						if (tgPc.isDead()) {
							continue;
						}
						if (tgPc.getClanid() == 0) {
							continue;
						}
						// 血盟成員傳送
						if (tgPc.getClanid() == pc.getClanid()) {
							// 商店村模式
							if (!tgPc.isPrivateShop()) {
								// 解除魔法技能绝对屏障
								L1BuffUtil.cancelAbsoluteBarrier(tgPc);
								if (!tgPc.is_ask_teleport()) {
									L1Teleport.teleport(tgPc, newX, newY, newMapId, 5, true);
								} else {
									tgPc.setTeleportX(newX);
									tgPc.setTeleportY(newY);
									tgPc.setTeleportMapId(newMapId);
									// 你的血盟成員想要傳送你。你答應嗎？(Y/N)
									tgPc.sendPackets(new S_Message_YN(748));
								}
							}
						}
					}
				}
				// 自身的傳送
				L1Teleport.teleport(pc, newX, newY, newMapId, 5, true);
			}

			// 解除魔法技能绝对屏障
			L1BuffUtil.cancelAbsoluteBarrier(pc);
		}
	}
}
