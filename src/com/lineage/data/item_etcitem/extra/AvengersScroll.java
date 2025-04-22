package com.lineage.data.item_etcitem.extra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 復仇卷軸 點兩下後會顯示請輸入復仇的對象， 然後輸入玩家的遊戲ID會直接前往該玩家身邊 限定盟屋、內城以及某些正服無法直接前往的地區使用此道具無法前往
 * 
 * @author terry0412
 */
public class AvengersScroll extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(AvengersScroll.class);

	/**
	 *
	 */
	private AvengersScroll() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new AvengersScroll();
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
		if (item == null) { // 例外狀況:物件為空
			return;
		}

		if (pc == null) { // 例外狀況:人物為空
			return;
		}

		if (pc.isGhost()) { // 鬼魂模式
			return;
		}

		if (pc.isDead()) { // 死亡
			return;
		}

		if (pc.isTeleport()) { // 傳送中
			return;
		}

		if (pc.isPrivateShop()) { // 商店村模式
			pc.sendPackets(new S_ServerMessage("\\fT請先結束商店村模式!"));
			return;
		}

		// 扣除一張卷軸
		pc.getInventory().removeItem(item, 1);

		try {
			// 名稱
			pc.sendPackets(new S_SystemMessage("請輸入你想復仇的對象:"));
			pc.re_avenger(true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
