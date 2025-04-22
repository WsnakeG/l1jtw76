package com.lineage.data.npc.teleport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
//import com.lineage.server.serverpackets.S_PackBoxMaptime;
import com.lineage.server.serverpackets.S_PacketBoxGame;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1MapsLimitTime;
import com.lineage.server.templates.L1TeleportLoc;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;

/**
 * 傳送師 91053
 * 
 * @author dexc
 */
public class Npc_Teleport extends NpcExecutor {

	/**
	 * 傳送師
	 */
	private Npc_Teleport() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Teleport();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_0"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equals("up")) {
			final int page = pc.get_other().get_page() - 1;
			showPage(pc, npc, page);

		} else if (cmd.equals("dn")) {
			final int page = pc.get_other().get_page() + 1;
			showPage(pc, npc, page);

			// 消除其他地圖使用權
					} else if (cmd.equals("del")) {
						final Integer inMap = ServerUseMapTimer.MAP.get(pc);
						if (inMap != null) {
							pc.get_other().set_usemap(-1);
							pc.get_other().set_usemapTime(0);
							pc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIMECLEAR));
							//pc.sendPackets(new S_PackBoxMaptime(inMap));
							ServerUseMapTimer.MAP.remove(pc);
						}

			// 數字選項
		} else if (cmd.matches("[0-9]+")) {
			final String pagecmd = pc.get_other().get_page() + cmd;
			teleport(pc, npc, Integer.valueOf(pagecmd));

		} else {
			pc.get_other().set_page(0);
			final HashMap<Integer, L1TeleportLoc> teleportMap = NpcTeleportTable.get().get_teles(cmd);
			if (teleportMap != null) {
				if (teleportMap.size() <= 0) {
					// 1,447：目前暫不開放。
					pc.sendPackets(new S_ServerMessage(1447));
					return;
				}
				pc.get_otherList().teleport(teleportMap);
				showPage(pc, npc, 0);
			} else {
				// 1,447：目前暫不開放。
				pc.sendPackets(new S_ServerMessage(1447));
			}
		}
	}
	
	/**
	 * 執行傳送
	 * 
	 * @param pc
	 * @param npc
	 * @param key
	 */
	public static void teleport(final L1PcInstance pc, final Integer key) {
		// System.out.println("key:" + key);
		final Map<Integer, L1TeleportLoc> list = pc.get_otherList().teleportMap();
		final L1TeleportLoc info = list.get(key);

		// 151221 更改等級判斷寫入位置
		if (info.get_min() > pc.getLevel()) {
			pc.sendPackets(new S_ServerMessage("您目前的等級(" + pc.getLevel() + ")低於限制，無法進入！"));
			return;
		}

		if (info.get_max() < pc.getLevel()) {
			pc.sendPackets(new S_ServerMessage("您目前的等級(" + pc.getLevel() + ")超過限制，無法進入！"));
			return;
		}

		// 已經具有時間地圖使用權
		if (info.get_time() != 0) {
			final Integer inMap = ServerUseMapTimer.MAP.get(pc);
			if (inMap != null) {
				if (pc.get_other().get_usemap() == info.get_mapid()) {
					L1Teleport.teleport(pc, info.get_locx(), info.get_locy(), (short) info.get_mapid(), 5,
							true);

				} else {
					// 你必須先消除其它地圖使用權才能進入這裡！！
					pc.sendPackets(new S_ServerMessage("必須先消除其它地圖使用權才能進入這裡"));
				}
				return;
			}
		}

		boolean party = false;

		if (info.get_user() > 0) {
			if (!pc.isInParty()) {
				// 425：您並沒有參加任何隊伍。
				pc.sendPackets(new S_ServerMessage(425));
				return;
			}
			// 不是隊長
			if (!pc.getParty().isLeader(pc)) {
				// 你必須是隊伍的領導者。
				pc.sendPackets(new S_ServerMessage("你必須是隊伍的領導者"));
				return;
			}
			// 人數不足
			if (pc.getParty().getNumOfMembers() < info.get_user()) {
				// 本團隊地圖要求隊伍成員必須達到 %0人。
				pc.sendPackets(new S_ServerMessage("隊伍成員必須達到" + info.get_user() + "人"));
				return;
			}
			party = true;
		}

		final int itemid = info.get_itemid();
		final L1ItemInstance item = pc.getInventory().checkItemX(itemid, info.get_price());

		if (item != null) {
			// 地圖群組設置資料 (入場時間限制) by terry0412
			final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(info.get_mapid());
			if (mapsLimitTime != null) {
				final int order_id = mapsLimitTime.getOrderId();
				final int used_time = pc.getMapsTime(order_id);
				final int limit_time = mapsLimitTime.getLimitTime();

				// 允許時間已到
				if (used_time > limit_time) {
					pc.sendPackets(new S_ServerMessage("已超過該地圖的允許入場時間。"));
					return;

				}
			}

			if (info.get_time() != 0) {
				pc.get_other().set_usemap(info.get_mapid());
				ServerUseMapTimer.put(pc, info.get_time());
			}

			pc.getInventory().removeItem(item, info.get_price());// 刪除道具

			if (party) {
				final List<L1PcInstance> pcs = pc.getParty().getMemberList();

				if (pcs.isEmpty()) {
					return;
				}
				if (pcs.size() <= 0) {
					return;
				}

				for (final Iterator<L1PcInstance> iter = pcs.iterator(); iter.hasNext();) {
					final L1PcInstance tgpc = iter.next();
					if (info.get_time() != 0) {
						final Integer inMap = ServerUseMapTimer.MAP.get(tgpc);
						if (inMap != null) {
							tgpc.get_other().set_usemap(-1);
							tgpc.get_other().set_usemapTime(0);
							tgpc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIMECLEAR));
							//tgpc.sendPackets(new S_PackBoxMaptime(inMap));
							ServerUseMapTimer.MAP.remove(tgpc);
						}
						tgpc.get_other().set_usemap(info.get_mapid());
						ServerUseMapTimer.put(tgpc, info.get_time());
					}
					L1Teleport.teleport(tgpc, info.get_locx(), info.get_locy(), (short) info.get_mapid(), 5,
							true);
				}

			} else {
				L1Teleport.teleport(pc, info.get_locx(), info.get_locy(), (short) info.get_mapid(), 5, true);
			}

		} else {
			// 找回物品
			final L1Item itemtmp = ItemTable.get().getTemplate(itemid);
			pc.sendPackets(new S_ServerMessage(337, itemtmp.getNameId()));
		}
	}

	/**
	 * 執行傳送
	 * 
	 * @param pc
	 * @param npc
	 * @param key
	 */
	public static void teleport(final L1PcInstance pc, final L1NpcInstance npc, final Integer key) {
		// System.out.println("key:" + key);
		final Map<Integer, L1TeleportLoc> list = pc.get_otherList().teleportMap();
		final L1TeleportLoc info = list.get(key);

		// 151221 更改等級判斷寫入位置
		if (info.get_min() > pc.getLevel()) {
			pc.sendPackets(new S_ServerMessage("您目前的等級(" + pc.getLevel() + ")低於限制，無法進入！"));
			return;
		}

		if (info.get_max() < pc.getLevel()) {
			pc.sendPackets(new S_ServerMessage("您目前的等級(" + pc.getLevel() + ")超過限制，無法進入！"));
			return;
		}

		// 已經具有時間地圖使用權
		if (info.get_time() != 0) {
			final Integer inMap = ServerUseMapTimer.MAP.get(pc);
			if (inMap != null) {
				if (pc.get_other().get_usemap() == info.get_mapid()) {
					L1Teleport.teleport(pc, info.get_locx(), info.get_locy(), (short) info.get_mapid(), 5,
							true);

				} else {
					// 你必須先消除其它地圖使用權才能進入這裡！！
					pc.sendPackets(new S_ServerMessage("必須先消除其它地圖使用權才能進入這裡"));
				}
				return;
			}
		}

		boolean party = false;

		if (info.get_user() > 0) {
			if (!pc.isInParty()) {
				// 425：您並沒有參加任何隊伍。
				pc.sendPackets(new S_ServerMessage(425));
				return;
			}
			// 不是隊長
			if (!pc.getParty().isLeader(pc)) {
				// 你必須是隊伍的領導者。
				pc.sendPackets(new S_ServerMessage("你必須是隊伍的領導者"));
				return;
			}
			// 人數不足
			if (pc.getParty().getNumOfMembers() < info.get_user()) {
				// 本團隊地圖要求隊伍成員必須達到 %0人。
				pc.sendPackets(new S_ServerMessage("隊伍成員必須達到" + info.get_user() + "人"));
				return;
			}
			party = true;
		}

		final int itemid = info.get_itemid();
		final L1ItemInstance item = pc.getInventory().checkItemX(itemid, info.get_price());

		if (item != null) {
			// 地圖群組設置資料 (入場時間限制) by terry0412
			final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(info.get_mapid());
			if (mapsLimitTime != null) {
				final int order_id = mapsLimitTime.getOrderId();
				final int used_time = pc.getMapsTime(order_id);
				final int limit_time = mapsLimitTime.getLimitTime();

				// 允許時間已到
				if (used_time > limit_time) {
					pc.sendPackets(new S_ServerMessage("已超過該地圖的允許入場時間。"));
					return;

				}
			}

			if (info.get_time() != 0) {
				pc.get_other().set_usemap(info.get_mapid());
				ServerUseMapTimer.put(pc, info.get_time());
			}

			pc.getInventory().removeItem(item, info.get_price());// 刪除道具

			if (party) {
				final List<L1PcInstance> pcs = pc.getParty().getMemberList();

				if (pcs.isEmpty()) {
					return;
				}
				if (pcs.size() <= 0) {
					return;
				}

				for (final Iterator<L1PcInstance> iter = pcs.iterator(); iter.hasNext();) {
					final L1PcInstance tgpc = iter.next();
					if (info.get_time() != 0) {
						final Integer inMap = ServerUseMapTimer.MAP.get(tgpc);
						if (inMap != null) {
							tgpc.get_other().set_usemap(-1);
							tgpc.get_other().set_usemapTime(0);
							tgpc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIMECLEAR));
							//tgpc.sendPackets(new S_PackBoxMaptime(inMap));
							ServerUseMapTimer.MAP.remove(tgpc);
						}
						tgpc.get_other().set_usemap(info.get_mapid());
						ServerUseMapTimer.put(tgpc, info.get_time());
					}
					L1Teleport.teleport(tgpc, info.get_locx(), info.get_locy(), (short) info.get_mapid(), 5,
							true);
				}

			} else {
				L1Teleport.teleport(pc, info.get_locx(), info.get_locy(), (short) info.get_mapid(), 5, true);
			}

		} else {
			// 找回物品
			final L1Item itemtmp = ItemTable.get().getTemplate(itemid);
			pc.sendPackets(new S_ServerMessage(337, itemtmp.getNameId()));
		}
	}

	/**
	 * 展示指定頁面
	 * 
	 * @param pc
	 * @param npc
	 * @param page
	 */
	public static void showPage(final L1PcInstance pc, final L1NpcInstance npc, int page) {
		final Map<Integer, L1TeleportLoc> list = pc.get_otherList().teleportMap();

		// 全部頁面數量
		int allpage = list.size() / 10;
		if ((page > allpage) || (page < 0)) {
			page = 0;
		}

		if ((list.size() % 10) != 0) {
			allpage += 1;
		}

		pc.get_other().set_page(page);// 設置頁面

		final int showId = page * 10;// 要顯示的項目ID

		final StringBuilder stringBuilder = new StringBuilder();
		// 每頁顯示10筆(showId + 10)資料
		for (int key = showId; key < (showId + 10); key++) {
			final L1TeleportLoc info = list.get(key);
			if (info != null) {
				// 找回物品
				final L1Item itemtmp = ItemTable.get().getTemplate(info.get_itemid());
				if (itemtmp != null) {
					stringBuilder.append(info.get_name());
					if (info.get_user() > 0) {
						// 6793 (隊伍: 6794 人以上)
						stringBuilder.append("隊伍:" + info.get_user());
					}
					stringBuilder.append(
							" (" + itemtmp.getName() + "-" + Integer.toString(info.get_price()) + "),");
				}
			}
		}

		final String[] clientStrAry = stringBuilder.toString().split(",");
		if (allpage == 1) {
			// 核心要求顯示僅有一頁
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_1", clientStrAry));

		} else {
			if (page < 1) {// 無上一頁
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_3", clientStrAry));

			} else if (page >= (allpage - 1)) {// 無下一頁(吻合第一頁為0 所以 -1)
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_4", clientStrAry));

			} else {// 正常
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_2", clientStrAry));
			}
		}
	}
}
