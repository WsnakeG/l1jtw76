package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.WorldNpc;

/**
 * 要求加入公佈欄訊息
 * 
 * @author daien
 */
public class C_BoardWrite extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_BoardWrite.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}

			if (pc.isDead()) { // 死亡
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			final int objId = readD();

			final L1NpcInstance npc = WorldNpc.get().map().get(objId);
			if (npc == null) {
				return;
			}

			final String title = readS();
			if (title.length() > 16) {
				pc.sendPackets(new S_ServerMessage(166, "標題過長"));
				return;
			}
			final String content = readS();
			if (title.length() > 1000) {
				pc.sendPackets(new S_ServerMessage(166, "內容過長"));
				return;
			}

			if (npc.ACTION != null) {
				pc.set_board_title(title);
				pc.set_board_content(content);
				npc.ACTION.action(pc, npc, "w", 0);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/*
	 * private static String currentTime() { final TimeZone tz =
	 * TimeZone.getTimeZone(Config.TIME_ZONE); final Calendar cal =
	 * Calendar.getInstance(tz); final int year = cal.get(Calendar.YEAR) - 2000;
	 * String year2; if (year < 10) { year2 = "0" + year; } else { year2 =
	 * Integer.toString(year); } final int Month = cal.get(Calendar.MONTH) + 1;
	 * String Month2 = null; if (Month < 10) { Month2 = "0" + Month; } else {
	 * Month2 = Integer.toString(Month); } final int date =
	 * cal.get(Calendar.DATE); String date2 = null; if (date < 10) { date2 = "0"
	 * + date; } else { date2 = Integer.toString(date); } return year2 + "/" +
	 * Month2 + "/" + date2; }
	 */

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

}
