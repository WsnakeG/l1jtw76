/**
 * License THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). THE WORK IS PROTECTED
 * BY COPYRIGHT AND/OR OTHER APPLICABLE LAW. ANY USE OF THE WORK OTHER THAN AS
 * AUTHORIZED UNDER THIS LICENSE OR COPYRIGHT LAW IS PROHIBITED. BY EXERCISING
 * ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND AGREE TO BE BOUND BY THE
 * TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE MAY BE CONSIDERED TO BE A
 * CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED HERE IN CONSIDERATION
 * OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.command.executor;

import java.util.List;
import java.util.StringTokenizer;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * GM指令：刪除身上物品 usage：.left 10 (表示只留下身上的前 10 樣物品，其他的全刪除。)
 */
public class L1DeleteMySelfItem implements L1CommandExecutor {
	private L1DeleteMySelfItem() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1DeleteMySelfItem();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			final int count = Integer.parseInt(tok.nextToken());
			int i = 0;
			final List<L1ItemInstance> Items = pc.getInventory().getItems();
			for (final L1ItemInstance Item : Items) {
				i++;
				if (i > count) {
					pc.getInventory().deleteItem(Item);
				}
			}
			pc.sendPackets(new S_SystemMessage("\\aE已清除道具欄道具。"));
		} catch (final Exception e) {
			pc.sendPackets(new S_SystemMessage("請輸入 ." + cmdName + " 數字"));
		}
	}
}
