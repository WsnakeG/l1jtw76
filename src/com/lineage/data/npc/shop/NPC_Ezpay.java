package com.lineage.data.npc.shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.config.ConfigOther;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.EzpayReading;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.SQLUtil;

/**
 * 商品領取專員NPC<BR>
 * DELETE FROM `npc` WHERE `npcid`='70558' ; INSERT INTO `npc` VALUES ('70558',
 * '攻擊箭孔', '', '0', '', 'L1Bow', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '', '0', '-1', '-1', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '0',
 * '14', '0', '1', '0'); DELETE FROM `npc` WHERE `npcid`='70750' ; INSERT INTO
 * `npc` VALUES ('70750', '商品領取專員', '商品領取專員', 'shop.NPC_Ezpay', '',
 * 'L1Merchant', '6989', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '',
 * '0', '-1', '-1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '0', '14', '0', '1', '0');
 * DELETE FROM `server_event` WHERE `id`='34'; INSERT INTO `server_event` VALUES
 * ('34', '特殊領取專員(商品領取專員)', 'SpawnOtherSet', '1', 'true', '說明:啟動特殊領取專員以及召喚');
 * DELETE FROM `server_event_spawn` WHERE `eventid`='34'; DELETE FROM
 * `server_event_spawn` WHERE `id`='40329'; DELETE FROM `server_event_spawn`
 * WHERE `id`='40330'; INSERT INTO `server_event_spawn` VALUES (40329, 34,
 * '商品領取專員(騎士村)', 1, 70750, 0, 33083, 33406, 0, 0, 6, 0, 4, 0, 1); INSERT INTO
 * `server_event_spawn` VALUES (40330, 34, '商品領取專員(奇岩村)', 1, 70750, 0, 33430,
 * 32806, 0, 0, 4, 0, 4, 0, 1);
 * 
 * @author dexc
 */
public class NPC_Ezpay extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(NPC_Ezpay.class);

	/**
	 *
	 */
	private NPC_Ezpay() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new NPC_Ezpay();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_0"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (cmd.equals("up")) {
			final int page = pc.get_other().get_page() - 1;
			showPage(pc, npc, page);

		} else if (cmd.equals("dn")) {
			final int page = pc.get_other().get_page() + 1;
			showPage(pc, npc, page);

		} else if (cmd.equalsIgnoreCase("1")) {// 領取商品
			pc.get_otherList().SHOPLIST.clear();
			// 取回資料
			final Map<Integer, int[]> info = EzpayReading.get().ezpayInfo(pc.getAccountName().toLowerCase());
			if (info.size() <= 0) {
				// 無資料
				isCloseList = true;
				// 並沒有查詢到您的相關贊助記錄!!請您再仔細查詢一次。
				pc.sendPackets(new S_ServerMessage("\\aD並沒有查詢到您的相關商品記錄!!"));

			} else {
				pc.get_other().set_page(0);
				int index = 0;
				for (Integer key : info.keySet()) {
					int[] value = info.get(key);
					if (value != null) {
						pc.get_otherList().SHOPLIST.put(index, value);
						index++;
					}
				}
				showPage(pc, npc, 0);
			}

		} else if (cmd.equalsIgnoreCase("2")) {// 全部領取
			// 取回資料
			final Map<Integer, int[]> info = EzpayReading.get().ezpayInfo(pc.getAccountName().toLowerCase());
			if (info.size() <= 0) {
				// 無資料
				isCloseList = true;
				// 並沒有查詢到您的相關贊助記錄!!請您再仔細查詢一次。
				pc.sendPackets(new S_ServerMessage("\\aG並沒有查詢到您的相關商品記錄!!"));

			} else {
				for (Integer key : info.keySet()) {
					int[] value = info.get(key);
					int id = value[0];
					int itemid = value[1];
					int count = value[2];
					if (EzpayReading.get().update(pc.getAccountName(), id, pc.getName(),
							pc.getNetConnection().getIp().toString())) {
						// 滿額好禮
						checkBonus(pc, npc, count);

						// 首儲判斷
						if (ConfigOther.DOUBLE_GIFT) {
							if (!pc.getQuest().isEnd(L1PcQuest.QUEST_GETMONEY)) {// 任務編號請自訂
								pc.getQuest().set_step(L1PcQuest.QUEST_GETMONEY, 0);
								pc.getQuest().set_end(L1PcQuest.QUEST_GETMONEY);
								count *= 2;
							}
						}

						// 給予物品
						createNewItem(pc, npc, itemid, count);
						_log.fatal("帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName()
								+ " 領取交易序號:" + id + "(" + itemid + ") 數量:" + count + " 完成!!");

					} else {
						pc.sendPackets(new S_ServerMessage("\\aD領取失敗!!請聯繫線上GM!! ID:" + id));
						_log.fatal("帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName()
								+ " 領取交易序號:" + id + " 領取失敗!!");
						isCloseList = true;
					}
				}
			}
			isCloseList = true;

		} else {
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 展示指定頁面
	 * 
	 * @param pc
	 * @param npc
	 * @param page
	 */
	private static void showPage(final L1PcInstance pc, final L1NpcInstance npc, int page) {
		final Map<Integer, int[]> list = pc.get_otherList().SHOPLIST;

		// 全部頁面數量
		int allpage = list.size() / 10;
		if ((page > allpage) || (page < 0)) {
			page = 0;
		}

		if (list.size() % 10 != 0) {
			allpage += 1;
		}

		pc.get_other().set_page(page);// 設置頁面

		final int showId = page * 10;// 要顯示的項目ID

		final StringBuilder stringBuilder = new StringBuilder();
		// 每頁顯示10筆(showId + 10)資料
		for (int key = showId; key < showId + 10; key++) {
			final int[] info = list.get(key);
			if (info != null) {
				// 找回物品
				final L1Item itemtmp = ItemTable.get().getTemplate(info[1]);
				if (itemtmp != null) {
					stringBuilder.append(itemtmp.getName() + "(" + info[2] + "),");
				}
			}
		}
		final String[] clientStrAry = stringBuilder.toString().split(",");
		if (allpage == 1) {
			// 核心要求顯示僅有一頁
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_1", clientStrAry));

		} else {
			if (page < 1) {// 無上一頁
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_3", clientStrAry));

			} else if (page >= (allpage - 1)) {// 無下一頁(吻合第一頁為0 所以 -1)
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_4", clientStrAry));

			} else {// 正常
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_2", clientStrAry));
			}
		}
	}

	private static void createNewItem(final L1PcInstance pc, final L1NpcInstance npc, final int item_id,
			final long count) {
		try {
			if (pc == null) {
				return;
			}
			// 產生新物件
			final L1ItemInstance item = ItemTable.get().createItem(item_id);
			if (item != null) {
				item.setCount(count);
				item.setIdentified(true);

				pc.getInventory().storeItem(item);
				pc.sendPackets(new S_ServerMessage("\\fW" + npc.getNameId() + "給你" + item.getLogName())); // 获得0%。

			} else {
				_log.error("給予物件失敗 原因: 指定編號物品不存在(" + item_id + ")");
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void checkBonus(final L1PcInstance pc, final L1NpcInstance npc, final int count) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `shop_bonus` ORDER BY `need_counts` DESC";
			ps = co.prepareStatement(sqlstr);
			rs = ps.executeQuery();

			final Timestamp now_time = new Timestamp(System.currentTimeMillis());
			while (rs.next()) {
				final Timestamp end_time = rs.getTimestamp("end_time");
				// 滿額好禮 時間限制
				if ((end_time != null) && (now_time.after(end_time))) {
					continue;
				}
				// 滿額好禮 贈禮條件
				final int need_counts = rs.getInt("need_counts");
				if (count < need_counts) {
					continue;
				}
				final int item_id = rs.getInt("bonus_item_id");
				final int counts = rs.getInt("bonus_item_counts");
				final L1ItemInstance item = ItemTable.get().createItem(item_id);
				if (item == null) {
					_log.error("給予物件失敗 原因: 指定編號物品不存在(" + item_id + ")");
				} else {
					if (item.isStackable()) {
						item.setCount(counts);
						item.setIdentified(true);
						pc.getInventory().storeItem(item);
					} else {
						item.setIdentified(true);
						pc.getInventory().storeItem(item);
					}
					pc.sendPackets(new S_ServerMessage("\\aH" + npc.getNameId() + "給你" + item.getLogName())); // 获得0%。

					_log.fatal("帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName() + " 領取滿額好禮:("
							+ item_id + ") 數量:" + counts + " 完成!!");
				}
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
	}
}