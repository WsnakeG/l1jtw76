package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 密室鑰匙 40555
 */
public class SecretRoom_Key extends ItemExecutor {

	/**
	 *
	 */
	private SecretRoom_Key() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new SecretRoom_Key();
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
		final short mapid = 13;// 古魯丁地監 7樓

		if (pc.isKnight() && ((pc.getX() >= 32806) && (pc.getX() <= 32814))
				&& ((pc.getY() >= 32798) && (pc.getY() <= 32807)) && (pc.getMapId() == mapid)) {

			if (pc.isKnight()) {// 騎士
				// LV30任務已經完成
				if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
					// 没有任何事情发生。
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
				// 任務尚未開始
				if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
					// 没有任何事情发生。
					pc.sendPackets(new S_ServerMessage(79));

				} else {
					L1Teleport.teleport(pc, 32815, 32810, mapid, 5, false);
				}
			}

		} else {
			// 没有任何事情发生。
			pc.sendPackets(new S_ServerMessage(79));
		}
	}
}
