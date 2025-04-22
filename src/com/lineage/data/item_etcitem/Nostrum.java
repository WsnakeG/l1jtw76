/*
 * Copyright (C) 2013 Nightwish790711 This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.lineage.data.item_etcitem;

import com.lineage.config.ConfigAlt;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 
 * 全能力藥水 Classname 後直接加入數字即可
 * 
 * @author Nightwish790711
 */
public class Nostrum extends ItemExecutor {
	public static ItemExecutor get() {
		return new Nostrum();
	}
	private Nostrum() {
	}

	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		final int a = Integer.parseInt(get_set()[1]);

		try {
			for (int i = pc.getBaseStr(); i < a; i++) {
				pc.addBaseStr(1);
				pc.setBonusStats(pc.getBonusStats() + 1);
			}

			for (int i = pc.getBaseDex(); i < a; i++) {
				pc.addBaseDex(1);
				pc.setBonusStats(pc.getBonusStats() + 1);
			}

			for (int i = pc.getBaseCon(); i < a; i++) {
				pc.addBaseCon(1);
				pc.setBonusStats(pc.getBonusStats() + 1);
			}

			for (int i = pc.getBaseWis(); i < a; i++) {
				pc.addBaseWis(1);
				pc.setBonusStats(pc.getBonusStats() + 1);
			}

			for (int i = pc.getBaseCha(); i < a; i++) {
				pc.addBaseCha(1);
				pc.setBonusStats(pc.getBonusStats() + 1);
			}

			for (int i = pc.getBaseInt(); i < a; i++) {
				pc.addBaseInt(1);
				pc.setBonusStats(pc.getBonusStats() + 1);
			}

			pc.sendPackets(new S_OwnCharStatus2(pc));
			pc.save(); // 儲存人物資料
			pc.getInventory().removeItem(item, 1);
			pc.sendPackets(new S_SystemMessage("\\aE使用全能力道具後，\\aG務必重新登入遊戲！"));
			// 161025 by Erics4179 新增全能力藥水使用後給予道具
			CreateNewItem.createNewItem(pc, ConfigAlt.Nostrumitem, ConfigAlt.Nostrumcount);
		} catch (final Exception e) {
		}
	}
}
