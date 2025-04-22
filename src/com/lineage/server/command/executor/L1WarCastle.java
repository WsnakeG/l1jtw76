package com.lineage.server.command.executor;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.world.WorldWar;

/**
 * 城堡戰爭控制
 * 
 * @author dexc
 */
public class L1WarCastle implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1WarCastle.class);

	private L1WarCastle() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1WarCastle();
	}

	/**
	 * 目前時間
	 * 
	 * @return
	 */
	public Calendar getRealTime() {
		final TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		final Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			int castleId = 0;
			String name = null;
			boolean start = false;
			final Calendar calendar = PerformanceTimer.getRealTime();
			if (arg.equalsIgnoreCase("stopall")) {// 全部戰爭清除(包含盟戰)
				final List<L1War> alllist = WorldWar.get().getWarList();
				if (!alllist.isEmpty()) {
					for (final L1War war : alllist) {
						if (war.get_castleId() == 0) {
							war.ceaseWar();

						} else {
							final L1Castle castle = CastleReading.get().getCastleTable(war.get_castleId());
							calendar.add(ConfigAlt.ALT_WAR_TIME_UNIT, -(ConfigAlt.ALT_WAR_TIME * 2));// 減

							castle.setWarTime(calendar);
							ServerWarExecutor.get().setWarTime(war.get_castleId(), calendar);
							ServerWarExecutor.get().setEndWarTime(war.get_castleId(), calendar);
						}
					}
				}

			} else if (arg.equalsIgnoreCase("stop1")) {// 肯特城
				castleId = L1CastleLocation.KENT_CASTLE_ID;
				calendar.add(Calendar.HOUR_OF_DAY, -4);// 減4小時
				name = "肯特城";

			} else if (arg.equalsIgnoreCase("stop2")) {// 妖魔城
				castleId = L1CastleLocation.OT_CASTLE_ID;
				calendar.add(Calendar.HOUR_OF_DAY, -4);// 減4小時
				name = "妖魔城";

			} else if (arg.equalsIgnoreCase("stop3")) {// 風木城
				castleId = L1CastleLocation.WW_CASTLE_ID;
				calendar.add(Calendar.HOUR_OF_DAY, -4);// 減4小時
				name = "風木城";

			} else if (arg.equalsIgnoreCase("stop4")) {// 奇巖城
				castleId = L1CastleLocation.GIRAN_CASTLE_ID;
				calendar.add(Calendar.HOUR_OF_DAY, -4);// 減4小時
				name = "奇岩城";

			} else if (arg.equalsIgnoreCase("stop5")) {// 海音城
				castleId = L1CastleLocation.HEINE_CASTLE_ID;
				calendar.add(Calendar.HOUR_OF_DAY, -4);// 減4小時
				name = "海音城";

			} else if (arg.equalsIgnoreCase("stop6")) {// 侏儒城
				castleId = L1CastleLocation.DOWA_CASTLE_ID;
				calendar.add(Calendar.HOUR_OF_DAY, -4);// 減4小時
				name = "侏儒城";

			} else if (arg.equalsIgnoreCase("stop7")) {// 亞丁城
				castleId = L1CastleLocation.ADEN_CASTLE_ID;
				calendar.add(Calendar.HOUR_OF_DAY, -4);// 減4小時
				name = "亞丁城";

			} else if (arg.equalsIgnoreCase("start1")) {// 肯特城
				castleId = L1CastleLocation.KENT_CASTLE_ID;
				start = true;
				name = "肯特城";

			} else if (arg.equalsIgnoreCase("start2")) {// 妖魔城
				castleId = L1CastleLocation.OT_CASTLE_ID;
				start = true;
				name = "妖魔城";

			} else if (arg.equalsIgnoreCase("start3")) {// 風木城
				castleId = L1CastleLocation.WW_CASTLE_ID;
				start = true;
				name = "風木城";

			} else if (arg.equalsIgnoreCase("start4")) {// 奇巖城
				castleId = L1CastleLocation.GIRAN_CASTLE_ID;
				start = true;
				name = "奇岩城";

			} else if (arg.equalsIgnoreCase("start5")) {// 海音城
				castleId = L1CastleLocation.HEINE_CASTLE_ID;
				start = true;
				name = "海音城";

			} else if (arg.equalsIgnoreCase("start6")) {// 侏儒城
				castleId = L1CastleLocation.DOWA_CASTLE_ID;
				start = true;
				name = "侏儒城";

			} else if (arg.equalsIgnoreCase("start7")) {// 亞丁城
				castleId = L1CastleLocation.ADEN_CASTLE_ID;
				start = true;
				name = "亞丁城";
			} else {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
				return;
			}

			if (castleId != 0) {
				final L1Castle castle = CastleReading.get().getCastleTable(castleId);
				castle.setWarTime(calendar);
				ServerWarExecutor.get().setWarTime(castleId, calendar);
				ServerWarExecutor.get().setEndWarTime(castleId, calendar);

				if (start) {
					if (pc != null) {
						pc.sendPackets(new S_ServerMessage(166, "準備啟動 " + name + " 攻城戰"));
					}
					_log.warn("準備啟動 " + name + " 攻城戰");

				} else {
					if (pc != null) {
						pc.sendPackets(new S_ServerMessage(166, "準備停止 " + name + " 攻城戰"));
					}
					_log.warn("準備停止 " + name + " 攻城戰");
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
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
