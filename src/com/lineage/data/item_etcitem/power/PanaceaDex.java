package com.lineage.data.item_etcitem.power;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 萬能藥(敏捷)40035<br>
 * 
 * @author dexc
 */
public class PanaceaDex extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(PanaceaDex.class);

	/**
	 *
	 */
	private PanaceaDex() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new PanaceaDex();
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
		if (pc.getBaseDex() < ConfigAlt.POWERMEDICINE) {
			if (pc.getElixirStats() < ConfigAlt.MEDICINE) {
				pc.addBaseDex((byte) 1); // DEX+1
				pc.resetBaseAc();
				pc.setElixirStats(pc.getElixirStats() + 1);
				pc.getInventory().removeItem(item, 1);
				pc.sendPackets(new S_OwnCharStatus2(pc));
				pc.sendDetails();
				try {
					pc.save();
				} catch (final Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}

			} else {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
			}

		} else {
			// \f1属性最大值只能到35。
			pc.sendPackets(new S_ServerMessage(481));
		}
	}
}
