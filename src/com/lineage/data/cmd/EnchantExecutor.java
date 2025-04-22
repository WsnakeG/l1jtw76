package com.lineage.data.cmd;

import java.util.Random;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public abstract class EnchantExecutor {

	/**
	 * 強化失敗
	 * 
	 * @param pc 執行者
	 * @param item 對象物件
	 */
	public abstract void failureEnchant(L1PcInstance pc, L1ItemInstance item);

	/**
	 * 強化成功
	 * 
	 * @param pc 執行者
	 * @param item 對象物件
	 * @param i 強化質
	 */
	public abstract void successEnchant(L1PcInstance pc, L1ItemInstance item, int i);

	/**
	 * 傳回強化值
	 * 
	 * @param item 對象物件
	 * @param bless 執行物件 祝福狀態
	 * @return
	 */
	public int randomELevel(final L1ItemInstance item, final int bless) {
		int level = 0;
		switch (bless) {
		case 0:// 祝福
		case 128:
			if (item.getBless() < 3) {
				final Random random = new Random();
				final int i = random.nextInt(100) + 1;
				if (item.getEnchantLevel() <= 2) {
					if (i < 32) {
						level = 1;

					} else if ((i >= 33) && (i <= 76)) {
						level = 2;

					} else if ((i >= 77) && (i <= 100)) {
						level = 3;
					}

				} else if ((item.getEnchantLevel() >= 3) && (item.getEnchantLevel() <= 6)) {
					if (i < 50) {
						level = 2;

					} else {
						level = 1;
					}

				} else {
					level = 1;
				}
			}
			break;

		case 1:// 一般
		case 129:
			if (item.getBless() < 3) {
				level = 1;
			}
			break;

		case 2:// 詛咒
		case 130:
			if (item.getBless() < 3) {
				level = -1;
			}
			break;

		case 3:// 幻象
		case 131:
			if (item.getBless() == 3) {
				level = 1;
			}
			break;
		}
		return level;
	}

}
