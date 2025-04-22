package com.lineage.data.item_etcitem.shop;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 密碼修改卷軸 2014/07/25 by Roy-修正密碼卡與封號卡系統顯示文字內容錯誤 DELETE FROM `etcitem` WHERE
 * `item_id`='49538'; INSERT INTO `etcitem` VALUES ('49538', '密碼修改卷軸',
 * 'shop.ResetPassword', '密碼修改卷軸', 'other', 'normal', 'paper', '0', '205',
 * '3963', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0',
 * '0');
 */
public class ResetPassword extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(ResetPassword.class);

	public static final Random _random = new Random();

	/**
	 *
	 */
	private ResetPassword() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ResetPassword();
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
		try {
			// 例外狀況:物件為空
			if (item == null) {
				return;
			}
			// 例外狀況:人物為空
			if (pc == null) {
				return;
			}

			if (pc.is_repass() != 0) {
				pc.sendPackets(new S_ServerMessage("\\aE你不是要變更密碼嗎?"));
				return;
			}

			pc.repass(1);

			pc.getInventory().removeItem(item, 1);

			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_pass_01", new String[] { "\\aE請輸入您的舊密碼" }));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
