package com.lineage.data.npc.shop;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.ShopXSet;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemRestrictionsTable;
import com.lineage.server.datatables.ShopXTable;
import com.lineage.server.datatables.lock.DwarfShopReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_CnSRetrieve;
import com.lineage.server.serverpackets.S_CnSShopSellList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ShopS;

/**
 * 託售管理員<BR>
 * 70535
 * 
 * @author dexc
 */
public class Npc_ShopX extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_ShopX.class);

	private static final int _adenaid = 44070;

	private static final int _count = 200;// 可託售數量

	/**
	 *
	 */
	private Npc_ShopX() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_ShopX();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			pc.get_other().set_item(null);

			final String[] info = new String[] { String.valueOf(ShopXSet.ADENA),
					String.valueOf(ShopXSet.DATE), String.valueOf(ShopXSet.MIN),
					String.valueOf(ShopXSet.MAX) };

			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_x_1", info));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("s")) {// 我要進行道具出售
			cmd_s1(pc, npc);

		} else if (cmd.equalsIgnoreCase("i")) {// 查看目前其他人出售的道具
			pc.sendPackets(new S_CnSShopSellList(pc, npc));

		} else if (cmd.equalsIgnoreCase("l")) {// 我的出售紀錄
			cmd_1(pc, npc);

		} else if (cmd.equalsIgnoreCase("ma")) {// 決定價格
			cmd_ma(pc, npc, amount);

		} else if (cmd.equals("up")) {// 上一頁
			final int page = pc.get_other().get_page() - 1;
			showPage(pc, npc.getId(), page);

		} else if (cmd.equals("dn")) {// 下一頁
			final int page = pc.get_other().get_page() + 1;
			showPage(pc, npc.getId(), page);

		} else if (cmd.equals("over")) {// 我要取消託售!
			cmd_over(pc, npc);

		} else if (cmd.equals("no")) {// 算了在賣賣看!
			pc.setTempID(0);
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));

			// 數字選項
		} else if (cmd.matches("[0-9]+")) {
			final String pagecmd = pc.get_other().get_page() + cmd;
			update(pc, npc, Integer.parseInt(pagecmd));
		}
	}

	/**
	 * 我的出售紀錄
	 * 
	 * @param pc
	 * @param npc
	 */
	public static void cmd_1(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			pc.get_otherList().SHOPXMAP.clear();
			final Map<Integer, L1ShopS> temp = DwarfShopReading.get().getShopSMap(pc.getId());

			if (temp != null) {
				pc.get_otherList().SHOPXMAP.putAll(temp);
				temp.clear();
			}
			showPage(pc, npc.getId(), 0);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 決定價格
	 * 
	 * @param pc
	 * @param npc
	 */
	public static void cmd_ma(final L1PcInstance pc, final L1NpcInstance npc, final long amount) {
		try {

			// 取回貨幣數量
			final L1ItemInstance itemT = pc.getInventory().checkItemX(_adenaid, ShopXSet.ADENA);
			boolean isError = false;
			if (itemT == null) {
				// 337：\f1%0不足%s。 0_o"
				pc.sendPackets(new S_ServerMessage(337, "貨幣"));
				isError = true;
			}
			if (amount < ShopXSet.MIN) {
				isError = true;
			}
			if (amount > ShopXSet.MAX) {
				isError = true;
			}

			if (isError) {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				pc.get_other().set_item(null);
				return;
			}

			final L1ItemInstance item = pc.get_other().get_item();
			if (item == null) {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}
			if (item.isEquipped()) {// 使用中物件
				isError = true;
			}
			if (!item.isIdentified()) {// 未鑑定物品
				isError = true;
			}
			if (item.getItem().getMaxUseTime() != 0) {// 具有時間限制
				isError = true;
			}

			if (ShopXTable.get().getTemplate(item.getItem().getItemId()) != null) {// 不可托售物品
				isError = true;
			}

			if (item.getGamNo() != null) {// 賭票
				isError = true;
			}
			if (item.getEnchantLevel() < 0) {// 強化為負值
				isError = true;
			}
			if (item.getItem().getMaxChargeCount() != 0) {// 具有次數
				if (item.getChargeCount() <= 0) {// 已無次數
					isError = true;
				}
			}

			if (isError) {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				pc.get_other().set_item(null);
				return;
			}

			pc.get_other().set_item(null);

			final long time = ShopXSet.DATE * 24 * 60 * 60 * 1000;
			final Timestamp overTime = new Timestamp(System.currentTimeMillis() + time); // 到期時間
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String key = sdf.format(overTime);

			final String[] info = new String[] { item.getLogName(), String.valueOf(amount), key };
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_x_4", info));

			final L1ShopS shopS = new L1ShopS();
			shopS.set_id(DwarfShopReading.get().nextId());
			shopS.set_item_obj_id(item.getId());
			shopS.set_user_obj_id(pc.getId());
			shopS.set_adena((int) amount);
			shopS.set_overtime(overTime);
			shopS.set_end(0);

			final String outname = item.getNumberedName_to_String();// 修正NAMEID顯示異常(loli)

			shopS.set_none(outname);
			shopS.set_item(item);

			pc.getInventory().removeItem(itemT, ShopXSet.ADENA);// 移除貨幣
			pc.getInventory().removeItem(item);// 移除託售物件
			DwarfShopReading.get().insertItem(item.getId(), item, shopS);

			try {
				pc.save();
				pc.saveInventory();

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 我要取消託售
	 * 
	 * @param pc
	 * @param npc
	 */
	public static void cmd_over(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			final L1ShopS shopS = DwarfShopReading.get().getShopS(pc.getTempID());
			pc.setTempID(0);
			shopS.set_end(3);
			shopS.set_item(null);
			DwarfShopReading.get().updateShopS(shopS);
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 我要進行道具出售(全物件)
	 * 
	 * @param pc
	 * @param npc
	 */
	private static void cmd_s1(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			final Map<Integer, L1ShopS> allShopS = DwarfShopReading.get().allShopS();
			if (allShopS.size() >= _count) {
				// 75：\f1沒有多餘的空間可以儲存。
				pc.sendPackets(new S_ServerMessage(75));
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			// 取回貨幣數量
			final L1ItemInstance itemT = pc.getInventory().checkItemX(44070, ShopXSet.ADENA);
			if (itemT == null) {
				// 337：\f1%0不足%s。 0_o"
				pc.sendPackets(new S_ServerMessage(337, "貨幣"));
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			final List<L1ItemInstance> itemsx = new CopyOnWriteArrayList<L1ItemInstance>();

			final List<L1ItemInstance> items = pc.getInventory().getItems();
			for (final L1ItemInstance item : items) {
				if (ShopXTable.get().getTemplate(item.getItem().getItemId()) != null) {// 不可托售物品
					continue;
				}

				if (item.isEquipped()) {// 使用中物件
					continue;
				}
				if (!item.isIdentified()) {// 未鑑定物品
					continue;
				}
				if (item.getItem().getMaxUseTime() != 0) {// 具有時間限制
					continue;
				}
				if (item.get_time() != null) {// 具有時間限制
					continue;
				}

				if (item.getGamNo() != null) {// 賭票
					continue;
				}
				if (item.getEnchantLevel() < 0) {// 強化為負值
					continue;
				}
				if (item.getItem().getMaxChargeCount() != 0) {// 具有次數
					if (item.getChargeCount() <= 0) {// 已無次數
						continue;
					}
				}
				if (ItemRestrictionsTable.RESTRICTIONS.contains(Integer.valueOf(item.getItemId()))) {
					continue;
				}
				itemsx.add(item);
			}
			// System.out.println(" 加入清單:" + itemsx.size() + "/" +
			// itemList.size());
			pc.sendPackets(new S_CnSRetrieve(pc, npc.getId(), itemsx));
			allShopS.clear();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 我要進行道具出售(不可轉移排除)
	 * 
	 * @param pc
	 * @param npc
	 */
	public static void cmd_s2(final L1PcInstance pc, final L1NpcInstance npc) {
		try {

			final Map<Integer, L1ShopS> allShopS = DwarfShopReading.get().allShopS();
			if (allShopS.size() >= _count) {
				// 75：\f1沒有多餘的空間可以儲存。
				pc.sendPackets(new S_ServerMessage(75));
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			// 取回貨幣數量
			final L1ItemInstance itemT = pc.getInventory().checkItemX(44070, ShopXSet.ADENA);
			if (itemT == null) {
				// 337：\f1%0不足%s。 0_o"
				pc.sendPackets(new S_ServerMessage(337, "貨幣"));
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			final List<L1ItemInstance> itemsx = new CopyOnWriteArrayList<L1ItemInstance>();

			final List<L1ItemInstance> items = pc.getInventory().getItems();
			for (final L1ItemInstance item : items) {
				if (ShopXTable.get().getTemplate(item.getItem().getItemId()) != null) {// 不可托售物品
					continue;
				}

				if (!item.getItem().isTradable()) {// 不可轉移
					continue;
				}
				if (item.isEquipped()) {// 使用中物件
					continue;
				}
				if (!item.isIdentified()) {// 未鑑定物品
					continue;
				}
				if (item.getItem().getMaxUseTime() != 0) {// 具有時間限制
					continue;
				}
				if (item.get_time() != null) {// 具有時間限制
					continue;
				}

				if (item.getGamNo() != null) {// 賭票
					continue;
				}
				if (item.getEnchantLevel() < 0) {// 強化為負值
					continue;
				}
				if (item.getItem().getMaxChargeCount() != 0) {// 具有次數
					if (item.getChargeCount() <= 0) {// 已無次數
						continue;
					}
				}
				if (ItemRestrictionsTable.RESTRICTIONS.contains(Integer.valueOf(item.getItemId()))) {
					continue;
				}
				itemsx.add(item);
			}
			// System.out.println(" 加入清單:" + itemsx.size() + "/" +
			// itemList.size());
			pc.sendPackets(new S_CnSRetrieve(pc, npc.getId(), itemsx));
			allShopS.clear();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取回物品銷售結果
	 * 
	 * @param pc 人物
	 * @param npc NPC
	 * @param key 排序
	 */
	public static void update(final L1PcInstance pc, final L1NpcInstance npc, final int key) {
		final Map<Integer, L1ShopS> list = pc.get_otherList().SHOPXMAP;

		pc.setTempID(0);

		final L1ShopS shopS = list.get(key);
		switch (shopS.get_end()) {
		case 0:// 出售中
			pc.setTempID(shopS.get_item_obj_id());
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String overTime = sdf.format(shopS.get_overtime());// 到期時間

			final String[] info = new String[] {
					shopS.get_item().getLogName() + "(" + shopS.get_item().getCount() + ")",
					String.valueOf(shopS.get_adena()), overTime };
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_x_5", info));
			break;

		case 1:// 已售出未領回
			shopS.set_end(2);
			DwarfShopReading.get().updateShopS(shopS);
			CreateNewItem.createNewItem(pc, _adenaid, shopS.get_adena());

			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
			break;

		case 2:// 已售出已領回
				// 6158 該託售物品收入已領回
			pc.sendPackets(new S_ServerMessage(166, "該託售物品收入已領回"));

			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
			break;

		case 3:// 未售出未領回
			shopS.set_end(4);
			shopS.set_item(null);
			DwarfShopReading.get().updateShopS(shopS);
			final L1ItemInstance item = DwarfShopReading.get().allItems().get(shopS.get_item_obj_id());
			DwarfShopReading.get().deleteItem(shopS.get_item_obj_id());
			pc.getInventory().storeTradeItem(item);
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 获得0%。

			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
			break;

		case 4:// 未售出已領回
				// 6159 該託售物品已領回
			pc.sendPackets(new S_ServerMessage(166, "該託售物品已領回"));

			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
			break;
		}
		// 清空拍賣物品暫存
		pc.get_other().set_item(null);
	}

	/**
	 * 展示出售紀錄
	 * 
	 * @param pc
	 * @param npcobjid
	 * @param page
	 */
	public static void showPage(final L1PcInstance pc, final int npcobjid, int page) {
		final Map<Integer, L1ShopS> list = pc.get_otherList().SHOPXMAP;
		if (list == null) {
			return;
		}

		// 全部頁面數量
		int allpage = list.size() / 10;
		if ((page > allpage) || (page < 0)) {
			page = 0;
		}

		if ((list.size() % 10) != 0) {
			allpage += 1;
		}

		pc.get_other().set_page(page);// 設置頁面

		final int or = page * 10;
		// System.out.println("OR:"+or);

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append((page + 1) + "/" + allpage + ",");

		// 每頁顯示10筆(showId + 10)資料
		for (int key = or; key < (or + 10); key++) {
			final L1ShopS shopS = list.get(key);
			if (shopS != null) {
				stringBuilder.append(shopS.get_none() + " / " + shopS.get_adena() + ",");
				switch (shopS.get_end()) {
				case 0:// 出售中
					stringBuilder.append("出售中,");
					break;

				case 1:// 已售出未領回
					stringBuilder.append("已售出未領回,");
					break;

				case 2:// 已售出已領回
					stringBuilder.append("已售出已領回,");
					break;

				case 3:// 未售出未領回
					stringBuilder.append("未售出未領回,");
					break;

				case 4:// 未售出已領回
					stringBuilder.append("未售出已領回,");
					break;
				}

			} else {
				stringBuilder.append(" ,");// 無該編號 顯示為空
			}
		}

		if (allpage >= (page + 1)) {
			final String out = stringBuilder.toString();
			final String[] clientStrAry = out.split(",");
			pc.sendPackets(new S_NPCTalkReturn(npcobjid, "y_x_2", clientStrAry));

		} else {
			// $6157 沒有可以顯示的項目
			pc.sendPackets(new S_ServerMessage(166, "沒有可以顯示的項目"));
		}
	}
}
