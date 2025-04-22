package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_AllChannelsChat;
import com.lineage.server.world.World;

/**
 * 廣播卡 使用後 畫面上方會出現廣播訊息 世界頻道喊話(&)出現的字顏色正常白色 延遲發話間隔時間 5秒
 * 
 * @author terry0412
 */
public class BroadcastCard extends ItemExecutor {

	/**
	 *
	 */
	private BroadcastCard() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new BroadcastCard();
	}

	private final static int[] _codes = { 14, 7, 24, 13, 47, 4, 3, 53, 10, 1, 2, 11};
	
	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		final String s = pc.getText();
		
		pc.setText(null);
		
		if (s == null) {
			return;
		}
		
		if (s.length() <= 0) {
			return;
		}
		
		if (s.length() > 100) {
			return;
		}
					
		final int code = data[0];
		
		for (final int check : _codes) {
			if (check == code) {
				pc.getInventory().removeItem(item, 1);
				World.get().broadcastPacketToAll(new S_AllChannelsChat(pc, s, code));
				//pc.sendPacketsAll(new S_AllChannelsChat(pc, s, code));	
				break;
			}
		}
	}
}
