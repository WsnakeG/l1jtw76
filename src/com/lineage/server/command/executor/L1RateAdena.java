package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigRate;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 變更掉落金幣倍率(參數:倍率)
 * 
 * @author terry0412
 */
public class L1RateAdena implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1RateAdena.class);

	private L1RateAdena() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1RateAdena();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			final int rate = Integer.parseInt(st.nextToken(), 10);

			String msgid = null;

			if (pc == null) {
				_log.warn("系統命令執行: " + cmdName + " 變更掉落金幣倍率" + arg);
			}

			if ((int) ConfigRate.RATE_DROP_ADENA == rate) {
				if (pc == null) {
					_log.warn("目前掉落金幣倍率已經是: " + rate);

				} else {
					pc.sendPackets(new S_SystemMessage("目前掉落金幣倍率已經是:" + rate));
				}
				return;

			} else if (ConfigRate.RATE_DROP_ADENA < rate) {
				ConfigRate.RATE_DROP_ADENA = rate;
				msgid = "\\fY伺服器活動變更掉落金幣倍率為 " + ConfigRate.RATE_DROP_ADENA + "倍，請大家把握時間！";

			} else if (ConfigRate.RATE_DROP_ADENA > rate) {
				ConfigRate.RATE_DROP_ADENA = rate;
				msgid = "\\fY伺服器活動變更掉落金幣倍率為 " + ConfigRate.RATE_DROP_ADENA + "倍，祝大家遊戲愉快！";
			}

			if (msgid != null) {
				World.get().broadcastPacketToAll(new S_ServerMessage(msgid));
			}

			if (pc == null) {
				_log.warn("目前掉落金幣倍率變更為: " + rate);
			}

		} catch (final Exception e) {
			if (pc == null) {
				_log.error("錯誤的命令格式: " + this.getClass().getSimpleName());

			} else {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
				// \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
			}
		}
	}
}
