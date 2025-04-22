package com.lineage.data.npc.shop;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.data.event.VIPSet;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.VIPReading;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;

/**
 * VIP管理員<BR>
 * 91128
 * 
 * @author dexc
 */
public class Npc_VIP extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_VIP.class);

	/**
	 *
	 */
	private Npc_VIP() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_VIP();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		final Timestamp time = VIPReading.get().getOther(pc);

		if (time != null) {
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String key = sdf.format(time);
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_v2", new String[] { key }));

		} else {
			L1Item item = ItemTable.get().getTemplate(VIPSet.ITEMID);
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_v1", new String[] {
					String.valueOf(VIPSet.ADENA), item.getNameId(), String.valueOf(VIPSet.DATETIME) }));
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		int mapid = -1;
		int x = -1;
		int y = -1;
		int levelup = pc.getMeteLevel();// 轉生次數
		if (cmd.equalsIgnoreCase("v01")) {// 買1個月VIP(需要 1千個YiWei幣)
			final Timestamp time = VIPReading.get().getOther(pc);

			if (time != null) {
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				final String key = sdf.format(time);
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_v2", new String[] { key }));
				return;
			}

			// 取回貨幣數量
			final L1ItemInstance itemT = pc.getInventory().checkItemX(VIPSet.ITEMID, VIPSet.ADENA);
			if (itemT == null) {
				// 337：\f1%0不足%s。 0_o"
				pc.sendPackets(new S_ServerMessage("\\aG道具不足。"));

			} else {
				pc.getInventory().removeItem(itemT, VIPSet.ADENA);// 移除貨幣

				long timeNow = System.currentTimeMillis();// 目前時間豪秒

				long x1 = VIPSet.DATETIME * 24 * 60 * 60;// 30天耗用秒數
				long x2 = x1 * 1000;// 轉為豪秒
				long upTime = x2 + timeNow;// 目前時間 加上 30天

				// 到期時間
				final Timestamp value = new Timestamp(upTime);

				VIPReading.get().storeOther(pc.getId(), value);

				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				final String key = sdf.format(value);
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_v2", new String[] { key }));
				return;
			}

		} else if (cmd.equalsIgnoreCase("v02")) {// 地圖01
			if (pc.getMeteLevel() >= ConfigAlt._viplevel02) {
				mapid = ConfigAlt._vipmapid02;
				x = ConfigAlt._vipmap02_locx;
				y = ConfigAlt._vipmap02_locy;
			}

		} else if (cmd.equalsIgnoreCase("v03")) {// 地圖02
			if (pc.getMeteLevel() >= ConfigAlt._viplevel03) {
				mapid = ConfigAlt._vipmapid03;
				x = ConfigAlt._vipmap03_locx;
				y = ConfigAlt._vipmap03_locy;
			}

		} else if (cmd.equalsIgnoreCase("v04")) {// 地圖03
			if (pc.getMeteLevel() >= ConfigAlt._viplevel04) {
				mapid = ConfigAlt._vipmapid04;
				x = ConfigAlt._vipmap04_locx;
				y = ConfigAlt._vipmap04_locy;
			}

		} else if (cmd.equalsIgnoreCase("v05")) {// 地圖04
			if (pc.getMeteLevel() >= ConfigAlt._viplevel05) {
				mapid = ConfigAlt._vipmapid05;
				x = ConfigAlt._vipmap05_locx;
				y = ConfigAlt._vipmap05_locy;
			}

		} else if (cmd.equalsIgnoreCase("v06")) {// 地圖05
			if (pc.getMeteLevel() < ConfigAlt._viplevel06) {
				mapid = ConfigAlt._vipmapid06;
				x = ConfigAlt._vipmap06_locx;
				y = ConfigAlt._vipmap06_locy;
			}

		} else if (cmd.equalsIgnoreCase("v07")) {// 地圖06
			if (pc.getMeteLevel() < ConfigAlt._viplevel07) {
				mapid = ConfigAlt._vipmapid07;
				x = ConfigAlt._vipmap07_locx;
				y = ConfigAlt._vipmap07_locy;
			}
		}

		if (mapid != -1) {
			// System.out.println("mapid:"+mapid);
			final Timestamp time = VIPReading.get().getOther(pc);
			if (time != null) {
				// System.out.println("time:"+time.toString());
				// 目前時間
				final Timestamp ts = new Timestamp(System.currentTimeMillis());
				// System.out.println("ts:"+ts.toString());
				if (time.after(ts)) {
					teleport(pc, x, y, mapid);

				} else {
					// 移出清單
					VIPReading.get().delOther(pc.getId());
				}
			}
		} else {
			// 3052:你的轉生次數已經超過這個地圖的限制。
			pc.sendPackets(new S_ServerMessage("\\aG轉生(" + levelup + ")轉或身上道具不足無法為您傳送進入。"));
		}

		// 關閉對話窗
		pc.sendPackets(new S_CloseList(pc.getId()));
	}

	/**
	 * 傳送
	 * 
	 * @param pc
	 * @param x
	 * @param y
	 * @param mapid
	 */
	private void teleport(final L1PcInstance pc, final int x, final int y, final int mapid) {
		try {
			L1Location location = new L1Location(x, y, mapid);
			final L1Location newLocation = location.randomLocation(200, false);

			final int newX = newLocation.getX();
			final int newY = newLocation.getY();
			final short mapId = (short) newLocation.getMapId();

			L1Teleport.teleport(pc, newX, newY, mapId, 5, true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
