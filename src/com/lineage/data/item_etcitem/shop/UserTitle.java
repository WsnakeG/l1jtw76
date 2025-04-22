package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 49537 封號卡 2014/07/25 by Roy-修正密碼卡與封號卡系統顯示文字內容錯誤 DELETE FROM `etcitem` WHERE
 * `item_id`='49537'; INSERT INTO `etcitem` VALUES ('49537', '封號卡',
 * 'shop.UserTitle', '封號卡', 'other', 'normal', 'paper', '0', '1863', '3963',
 * '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0');
 * 
 * @author daien
 */
public class UserTitle extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(UserTitle.class);

	/**
	 *
	 */
	private UserTitle() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new UserTitle();
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

		if (pc.is_retitle()) {
			pc.sendPackets(new S_ServerMessage("\\aC你不是要變更封號嗎?"));
			return;
		}

		if (!ConfigOther.CLANTITLE) {
			pc.sendPackets(new S_ServerMessage("\\aC尚未開放"));
			return;
		}

		if (pc.isPrivateShop()) {// 商店村模式
			pc.sendPackets(new S_ServerMessage("\\aC請先結束商店村模式!"));
			return;
		}

		try {
			pc.retitle(true);
			pc.sendPackets(new S_ServerMessage("\\aC輸入封號後直接按下ENTER"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}