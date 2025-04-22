package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * GM指定地圖傳送(參數:地圖編號)
 * 
 * @author dexc
 */
public class L1ToMap implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1ToMap.class);

	private L1ToMap() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ToMap();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final Integer mapId = Integer.parseInt(arg);

			final L1Map mapData = L1WorldMap.get().getMap(mapId.shortValue());
			if (mapData == null) {
				_log.error("指定地圖不存在" + mapId);
				return;
			}
			final int x = mapData.getX();
			final int y = mapData.getY();
			final int height = mapData.getHeight();
			final int width = mapData.getWidth();

			final int newx = x + (height / 2);
			final int newy = y + (width / 2);

			final L1Location loc = new L1Location(newx, newy, mapId.intValue());

			final L1Location newLocation = loc.randomLocation(200, true);
			final int newX = newLocation.getX();
			final int newY = newLocation.getY();

			L1Teleport.teleport(pc, newX, newY, mapId.shortValue(), 5, true);

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
