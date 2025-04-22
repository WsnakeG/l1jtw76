package com.lineage.data.item_etcitem.reel;

import com.lineage.config.ConfigOther;
import com.lineage.data.cmd.EnchantArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 百分百防具卷軸44069
 */
public class Zel_Percentage_Hundred extends ItemExecutor {

	/**
	 *
	 */
	private Zel_Percentage_Hundred() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Zel_Percentage_Hundred();
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
		// 對象OBJID
		final int targObjId = data[0];

		final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

		if (tgItem == null) {
			return;
		}

		boolean isErr = false;

		// 物品安定值
		final int safe_enchant = tgItem.getItem().get_safeenchant();

		// 取得物件觸發事件
		final int use_type = tgItem.getItem().getUseType();
		switch (use_type) {
		case 2:// 盔甲
		case 18:// T恤
		case 19:// 斗篷
		case 20:// 手套
		case 21:// 靴
		case 22:// 頭盔
		case 25:// 盾牌
		case 70:// 脛甲(褲子)
			if (safe_enchant < 0) { // 物品不可强化
				isErr = true;
			}
			break;

		default:
			isErr = true;
			break;
		}

		if (tgItem.getBless() >= 128) {// 封印的装備
			isErr = true;
		}

		if (isErr) {
			pc.sendPackets(new S_ServerMessage(79));// 没有任何事发生
			return;
		}

		if (tgItem.getEnchantLevel() < ConfigOther.ARMOR100) {
			pc.getInventory().removeItem(item, 1);
			final EnchantExecutor enchantExecutor = new EnchantArmor();

			final int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
			enchantExecutor.successEnchant(pc, tgItem, randomELevel);

		} else {
			// 2405 已達防具最大追加，沒有任何事情發生！
			pc.sendPackets(new S_ServerMessage("已達防具最大強化!"));
		}
	}
}