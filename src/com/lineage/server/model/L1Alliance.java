package com.lineage.server.model;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.world.WorldClan;

/**
 * 同盟系統
 * 
 * @author terry0412
 */
public final class L1Alliance {

	private static final Log _log = LogFactory.getLog(L1Alliance.class);

	private static final int MAX_SIZE = 4; // 同盟最大締結數量

	private final int _orderId; // 區隔用物件ID

	private final ArrayList<L1Clan> totalList;

	public L1Alliance(final int orderId, final L1Clan... clanList) {
		_orderId = orderId;

		// 建立容量為四單位的ArrayList
		totalList = new ArrayList<L1Clan>(MAX_SIZE);

		for (final L1Clan alliance : clanList) {
			totalList.add(alliance);
		}
	}

	/**
	 * 取回區隔用物件ID
	 * 
	 * @return
	 */
	public int getOrderId() {
		return _orderId;
	}

	public final ArrayList<L1Clan> getTotalList() {
		return totalList;
	}

	/**
	 * 檢查是否可以締結同盟
	 * 
	 * @param l1clan
	 * @return 如果可以締結:true, 反之:false
	 */
	public final boolean addAlliance(final L1Clan l1clan) {
		if (checkSize()) {
			return totalList.add(l1clan);
		}
		return false;
	}

	/**
	 * 檢查是否可以締結同盟
	 * 
	 * @return 如果可以締結:true, 反之:false
	 */
	public final boolean checkSize() {
		return totalList.size() < MAX_SIZE;
	}

	/**
	 * 對所有締結血盟線上成員發送封包 (遮蔽特定玩家)
	 * 
	 * @param excludingPcName
	 * @param packet
	 */
	public void sendPacketsAll(final String excludingPcName, final ServerBasePacket packet) {
		try {
			for (final L1Clan clan : totalList) {
				// 血盟不存在
				if ((clan == null) || (WorldClan.get().getClan(clan.getClanName()) == null)) {
					continue;
				}

				// 血盟線上成員
				for (final L1PcInstance listner : clan.getOnlineClanMember()) {
					// 人物訊息拒絕清單
					if (!listner.getExcludingList().contains(excludingPcName)) {
						listner.sendPackets(packet);
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
