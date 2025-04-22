package com.lineage.data.item_etcitem.html;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 41019 拉斯塔巴德歷史書第1頁 41020 拉斯塔巴德歷史書第2頁 41021 拉斯塔巴德歷史書第3頁 41022 拉斯塔巴德歷史書第4頁 41023
 * 拉斯塔巴德歷史書第5頁 41024 拉斯塔巴德歷史書第6頁 41025 拉斯塔巴德歷史書第7頁 41026 拉斯塔巴德歷史書第8頁
 */
public class Lashistory extends ItemExecutor {

	/**
	 *
	 */
	private Lashistory() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Lashistory();
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

		// 取得道具編號
		final int itemId = item.getItem().getItemId();

		switch (itemId) {
		case 41019:// 拉斯塔巴德歷史書第1頁
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory1"));
			break;

		case 41020:// 拉斯塔巴德歷史書第2頁
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory2"));
			break;

		case 41021:// 拉斯塔巴德歷史書第3頁
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory3"));
			break;

		case 41022:// 拉斯塔巴德歷史書第4頁
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory4"));
			break;

		case 41023:// 拉斯塔巴德歷史書第5頁
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory5"));
			break;

		case 41024:// 拉斯塔巴德歷史書第6頁
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory6"));
			break;

		case 41025:// 拉斯塔巴德歷史書第7頁
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory7"));
			break;

		case 41026:// 拉斯塔巴德歷史書第8頁
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory8"));
			break;
		}
	}
}
