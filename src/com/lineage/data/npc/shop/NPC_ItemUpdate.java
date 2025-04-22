package com.lineage.data.npc.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ItemUpdateTable;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.CharShiftingReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_AddItem;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_DeleteInventoryItem;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PowerItemList;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemUpdate;
import com.lineage.server.world.World;

/**
 * 物品升級專員 DELETE FROM `npc` WHERE `npcid`='91141'; DELETE FROM `npc` WHERE
 * `npcid`='91142'; DELETE FROM `npc` WHERE `npcid`='91143'; INSERT INTO `npc`
 * VALUES ('91141', '物品升級專員', '物品升級專員', 'shop.NPC_ItemUpdate', '', 'L1Merchant',
 * '6757', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '1', '-1',
 * '-1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '1', '0', '0', '-1', '0', '0', '0', '0', '0'); INSERT
 * INTO `npc` VALUES ('91142', '物品升級專員', '物品升級專員', 'shop.NPC_ItemUpdate', '',
 * 'L1Merchant', '6757', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '',
 * '1', '-1', '-1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '1', '0', '0', '-1', '0', '0', '0', '0', '0');
 * INSERT INTO `npc` VALUES ('91143', '物品升級專員', '物品升級專員', 'shop.NPC_ItemUpdate',
 * '', 'L1Merchant', '6757', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '', '1', '-1', '-1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '-1', '0', '0', '0',
 * '0', '0');
 * 
 * @author daien
 */
public class NPC_ItemUpdate extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(NPC_ItemUpdate.class);

	private static final Random _random = new Random();

	/**
	 *
	 */
	private NPC_ItemUpdate() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new NPC_ItemUpdate();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.set_mode_id(0);
		pc.set_check_item(false);
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), L1ItemUpdate._html_01));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.matches("[0-9]+")) {
			if ((pc.get_mode_id() != 0) && !pc.get_check_item()) {
				check_item(pc, npc, cmd);
			}

		} else if (cmd.equalsIgnoreCase("up")) {// 選擇我要升級的裝備
			pc.set_mode_id(0);
			show_item(pc, npc);

		} else if (cmd.startsWith("ut")) {// 選擇我要升級的裝備
			if (pc.get_check_item()) {
				get_item(pc, npc, cmd);
			}
		}
	}

	private void get_item(final L1PcInstance pc, final L1NpcInstance npc, final String cmd) {
		try {
			final int index = Integer.parseInt(cmd.substring(2));

			final L1ItemInstance tgitem = pc.getInventory().getItem(pc.get_mode_id());
			if (tgitem == null) {
				_log.error("升級道具 對象 OBJID異常 背包中無該道具:" + pc.get_mode_id());
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}
			final ArrayList<L1ItemUpdate> list = ItemUpdateTable.get().get(tgitem.getItemId());

			final L1ItemUpdate newitem = list.get(index);
			if (newitem == null) {
				_log.error("升級道具 對象 ITEMID 找不到升級模組:" + tgitem.getItemId());
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			final int toid = newitem.get_toid();
			final int[] needids = newitem.get_needids();
			final int[] needcounts = newitem.get_needcounts();

			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, needids, needcounts) >= 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 收回任務需要物件 給予任務完成物件
				if (CreateNewItem.delItems(pc, needids, needcounts, 1)) {// 刪除需要物品
					// 合成機率 by terry0412
					if ((newitem.get_chance() > 0 // 初始值為0 則機率為100%成功
					) && (_random.nextInt(1000) >= newitem.get_chance())) {
						// 提示訊息
						pc.sendPackets(new S_SystemMessage("融合強化失敗..請再接再厲。"));
						return;
					}

					final L1Item l1item = ItemTable.get().getTemplate(toid);
					final String src_name = tgitem.getItem().getName();
					// 移除物品顯示
					pc.sendPackets(new S_DeleteInventoryItem(tgitem.getId()));
					tgitem.setItemId(toid);// 變更ITEMID
					tgitem.setItem(l1item);
					tgitem.setBless(l1item.getBless());
					// 更新數據
					CharItemsReading.get().updateItemId_Name(tgitem);

					// 還原顯示
					pc.sendPackets(new S_AddItem(tgitem));
					pc.sendPackets(new S_ServerMessage(403, tgitem.getLogName())); // 获得0%。

					// 道具成功升級公告 (可用%s對應玩家名稱, 第二個%s為升級後的道具名稱) by terry0412
					if ((ConfigAlt.ITEMS_UPDATE_MSG != null) && !ConfigAlt.ITEMS_UPDATE_MSG.isEmpty()) {
						World.get().broadcastServerMessage(
								String.format(ConfigAlt.ITEMS_UPDATE_MSG, pc.getName(), tgitem.getLogName()));
					}

					// 建立紀錄
					CharShiftingReading.get().newShifting(pc, 0, src_name, tgitem.getId(), tgitem.getItem(),
							tgitem, 0);
				}

			} else {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 顯示升級資訊
	 * 
	 * @param pc
	 * @param npc
	 * @param cmd
	 */
	private void check_item(final L1PcInstance pc, final L1NpcInstance npc, final String cmd) {
		try {
			final int index = Integer.parseInt(cmd);

			final L1ItemInstance tgitem = pc.getInventory().getItem(pc.get_mode_id());
			if (tgitem == null) {
				_log.error("升級道具 對象 OBJID異常 背包中無該道具:" + pc.get_mode_id());
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}
			final ArrayList<L1ItemUpdate> list = ItemUpdateTable.get().get(tgitem.getItemId());
			final L1ItemUpdate newitem = list.get(index);
			if (newitem == null) {
				_log.error("升級道具 對象 ITEMID 找不到升級模組:" + tgitem.getItemId());
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			final int toids = newitem.get_toid();
			final int[] needids = newitem.get_needids();
			final int[] needcounts = newitem.get_needcounts();

			final L1Item getitem = ItemTable.get().getTemplate(toids);
			String need = "";
			for (int i = 0; i < needids.length; i++) {
				final L1Item l1item = ItemTable.get().getTemplate(needids[i]);
				need += l1item.getName() + "(" + needcounts[i] + ")  ";
			}

			final String[] date = new String[] { tgitem.getItem().getName(), getitem.getName(), need };
			pc.set_check_item(true);
			pc.sendPackets(new S_ItemCount(npc.getId(), 1, 1, L1ItemUpdate._html_03, "ut" + index, date));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 顯示可升級裝備清單
	 * 
	 * @param pc
	 * @param npc
	 */
	private void show_item(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			final ArrayList<L1ItemInstance> list = new ArrayList<L1ItemInstance>();
			final List<L1ItemInstance> items = pc.getInventory().getItems();
			for (final L1ItemInstance item : items) {
				if (item.getItem().isStackable()) {
					continue;
				}
				if (item.isEquipped()) {// 使用中物件
					continue;
				}
				if (ItemUpdateTable.get().get(item.getItemId()) == null) {
					continue;
				}
				list.add(item);
			}

			if (list.size() <= 0) {
				pc.sendPackets(new S_ServerMessage("\\fR很抱歉您沒有符合條件的道具裝備。"));
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			pc.sendPackets(new S_PowerItemList(pc, npc.getId(), list));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
