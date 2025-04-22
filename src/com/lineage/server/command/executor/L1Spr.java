package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 取回指定圖形速度設置(參數:圖形編號)
 * 
 * @author dexc
 */
public class L1Spr implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Spr.class);

	private L1Spr() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Spr();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {

			final int sprid = Integer.parseInt(arg);
			final int attack = SprTable.get().getAttackSpeed(sprid, 1);
			final int move = SprTable.get().getMoveSpeed(sprid, 0);
			// final int dmg = SprTable.get().getDmg(sprid);
			final int attack18 = SprTable.get().getDirSpellSpeed(sprid);
			final int attack19 = SprTable.get().getNodirSpellSpeed(sprid);
			final int attack30 = SprTable.get().getDirSpellSpeed(sprid);

			final String info = "sprid:" + sprid + "\n\r passispeed:" + move + "\n\r atkspeed:" + attack
					+ "\n\r atk_magic_speed:" + attack18 + "\n\r sub_magic_speed:" + attack19
					+ "\n\r sub_magic_speed30:" + attack30;

			if (pc == null) {
				_log.warn("系統命令執行: spr" + sprid + "\n\r" + info);

			} else {
				pc.sendPackets(new S_ServerMessage(166, info));
			}

		} catch (final Exception e) {
			if (pc == null) {
				_log.error("錯誤的命令格式: " + this.getClass().getSimpleName());

			} else {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
			}
		}
	}
}