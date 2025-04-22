package com.lineage.data.npc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 每日任務<BR>
 * 
 * @author Roy
 */
public class Npc_GivePc extends NpcExecutor {
	private static final Log _log = LogFactory.getLog(Npc_GivePc.class);

	/** 已經參加過的人員列表 */
	private static final Map<Integer, String> _playList = new HashMap<Integer, String>();

	private Npc_GivePc() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_GivePc();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			String ole = _playList.get(pc.getId());
			if (ole == null) {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "artis_01"));

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "artis_02"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			if (cmd.equalsIgnoreCase("0")) {// 領取按鈕設定
				if (pc.getLevel() < _level) {
					pc.sendPackets(new S_ServerMessage("您的等級不夠，請繼續努力呦！"));
					return;
				}
				String ole = _playList.get(pc.getId());
				if (ole == null) {
					final L1ItemInstance item = ItemTable.get().createItem(_itemid); // 設定給予什麼道具
					if (pc.getInventory().checkAddItem(item, _item_count) == L1Inventory.OK) { // 道具數量
						item.setCount(_item_count); // 數量
						pc.getInventory().storeItem(item);
						_playList.put(pc.getId(), pc.getName());
						pc.sendPackets(new S_ServerMessage("獲得" + item.getLogName()));
					} else {
						pc.sendPackets(new S_ServerMessage("身上物品太多，請您整理下您的背包"));
					}
				}
			}
			pc.sendPackets(new S_CloseList(pc.getId()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _itemid;
	private int _item_count;
	private int _level;

	@Override
	public void set_set(String[] set) {
		try {
			_itemid = Integer.parseInt(set[1]);

			if (_itemid <= 0) {
				_itemid = 40308;
				_log.error("UserHpr 設置錯誤: 道具ID 預設給予 40308");
			}

		} catch (Exception e) {
		}
		try {
			_item_count = Integer.parseInt(set[2]);

			if (_item_count <= 0) {
				_item_count = 1;
				_log.error("UserHpr 設置錯誤: 道具數量 預設給予 1");
			}

		} catch (Exception e) {
		}
		try {
			_level = Integer.parseInt(set[3]);

			if (_level <= 0) {
				_level = 52;
				_log.error("UserHpr 設置錯誤: 角色等級 預設52級以下無法領取");
			}

		} catch (Exception e) {
		}

	}

}