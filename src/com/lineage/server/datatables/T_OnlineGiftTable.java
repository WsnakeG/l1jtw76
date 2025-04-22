package com.lineage.server.datatables;

import static com.lineage.server.model.skill.L1SkillId.ONLINE_GIFT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OnlineGift;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.T_OnlineGiftModel;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 線上獎勵資料庫
 * 
 * @author simlin
 */
public class T_OnlineGiftTable {

	private static final Log _log = LogFactory.getLog(ArmorSetTable.class);

	private static T_OnlineGiftTable _instance;

	private final ArrayList<T_OnlineGiftModel> _list = new ArrayList<T_OnlineGiftModel>();

	public static synchronized T_OnlineGiftTable get() {
		if (_instance == null) {
			_instance = new T_OnlineGiftTable();
		}
		return _instance;
	}

	private T_OnlineGiftTable() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM t_online_gift order by giftIndex");
			rs = pstm.executeQuery();

			T_OnlineGiftModel model = null;
			while (rs.next()) {
				final int giftIndex = rs.getInt("giftIndex");
				final int time = rs.getInt("time");
				final int itemId = rs.getInt("itemId");
				final int itemCount = rs.getInt("itemCount");
				final String note = rs.getString("note");
				model = new T_OnlineGiftModel(giftIndex, time, itemId, itemCount, note);
				_list.add(model);
			}

			pstm.close();

			pstm = con.prepareStatement("update characters set" + " onlineGiftIndex=0,onlineGiftWiatEnd=0;");
			pstm.executeUpdate();
			pstm = con.prepareStatement("delete from character_buff" + " where skill_id=?");
			pstm.setInt(1, ONLINE_GIFT);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入線上獎勵設置數量: " + _list.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 給予狀態
	 * 
	 * @param pc
	 */
	public void check(final L1PcInstance pc) {
		if (pc.isOnlineGiftWiatEnd()) {
			pc.sendPackets(new S_OnlineGift(pc.getOnlineGiftIndex() + 1));
		} else if (pc.hasSkillEffect(ONLINE_GIFT)) {
			pc.sendPackets(
					new S_OnlineGift(pc.getOnlineGiftIndex() + 1, pc.getSkillEffectTimeSec(ONLINE_GIFT), 1));
		} else {
			final int giftIndex = pc.getOnlineGiftIndex();
			if (giftIndex < _list.size()) {
				final T_OnlineGiftModel model = _list.get(giftIndex);
				pc.setSkillEffect(ONLINE_GIFT, model.getTime() * 1000);
				pc.sendPackets(new S_OnlineGift(pc.getOnlineGiftIndex() + 1,
						pc.getSkillEffectTimeSec(ONLINE_GIFT), 1));
			} else {
				pc.sendPackets(new S_OnlineGift(0, 0, 0));
			}
		}
	}

	/**
	 * 按鈕領取
	 * 
	 * @param pc
	 */
	public void receive(final L1PcInstance pc) {
		if (pc.isOnlineGiftWiatEnd()) {
			int giftIndex = pc.getOnlineGiftIndex();
			if (giftIndex < _list.size()) {
				T_OnlineGiftModel model = _list.get(giftIndex);
				gift(pc, pc.getInventory(), model.getItemId(), model.getItemCount());
				pc.setOnlineGiftWiatEnd(false);

				giftIndex++;
				pc.setOnlineGiftIndex(giftIndex);
				if (giftIndex < _list.size()) {
					model = _list.get(giftIndex);
					pc.setSkillEffect(ONLINE_GIFT, model.getTime() * 1000);
				}
				check(pc);
			}
		}
	}

	/**
	 * 產生物件
	 * 
	 * @param pc
	 * @param pcInv
	 * @param itemId
	 * @param count
	 */
	private void gift(final L1PcInstance pc, final L1PcInventory pcInv, final int itemId, final int count) {
		L1ItemInstance giveItemObj = null;
		giveItemObj = ItemTable.get().createItem(itemId);
		if (giveItemObj != null) {
			if (pcInv.checkAddItem(giveItemObj, count) == 0) {
				if (giveItemObj.isStackable()) {
					giveItemObj.setCount(count);
					giveItemObj.setIdentified(true);
					giveItemObj.setBless(1);
					pcInv.storeItem(giveItemObj);
				} else {
					for (int y = 0; y < (count * count); y++) {
						final L1ItemInstance itemTemp1 = ItemTable.get().createItem(itemId);
						itemTemp1.setCount(1);
						itemTemp1.setIdentified(true);
						itemTemp1.setEnchantLevel(0);
						itemTemp1.setBless(1);
						pcInv.storeItem(itemTemp1);
					}
				}
				pc.sendPackets(new S_ServerMessage(403, giveItemObj.getLogName()));
			} else {
				pc.sendPackets(new S_SystemMessage(
						"超過可攜帶物品數量,獲取物品[" + giveItemObj.getLogName() + "(" + count + ")]失敗!請截圖反饋至GM!"));
			}
		}
	}
}