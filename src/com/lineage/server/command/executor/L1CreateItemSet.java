package com.lineage.server.command.executor;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;

/**
 * 取得預設套件(參數:套裝編號)
 * 
 * @author dexc
 */
public class L1CreateItemSet implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1CreateItemSet.class);

	private L1CreateItemSet() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1CreateItemSet();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final int key = Integer.parseInt(arg);
			final HashMap<Integer, ArmorSet> map = ArmorSet.getAllSet();
			final ArmorSet armorSet = map.get(key);
			for (final int itemid : armorSet.get_ids()) {
				final L1Item temp = ItemTable.get().getTemplate(itemid);
				if (temp != null) {
					pc.getInventory().storeItem(itemid, 1);
					// 403:获得0%。
					pc.sendPackets(new S_ServerMessage(403, temp.getNameId() + "(ID:" + itemid + ")"));

				} else {
					_log.error("找不到指定編號物品:" + itemid + " 套裝編號:" + arg + " 執行的GM:" + pc.getName());
				}
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
