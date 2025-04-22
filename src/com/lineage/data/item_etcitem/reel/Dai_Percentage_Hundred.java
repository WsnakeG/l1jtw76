package com.lineage.data.item_etcitem.reel;

import com.lineage.config.ConfigOther;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.cmd.EnchantWeapon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 百分百武器卷軸44068
 */
public class Dai_Percentage_Hundred extends ItemExecutor {

	/**
	 *
	 */
	private Dai_Percentage_Hundred() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Dai_Percentage_Hundred();
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

		final int safe_enchant = tgItem.getItem().get_safeenchant();
		boolean isErr = false;

		// 取得物件觸發事件
		final int use_type = tgItem.getItem().getUseType();
		switch (use_type) {
		case 1:// 武器
			if (safe_enchant < 0) { // 物品不可强化
				isErr = true;
			}
			break;

		default:
			isErr = true;
			break;
		}

		final int weaponId = tgItem.getItem().getItemId();
		if ((weaponId >= 246) && (weaponId <= 255)) { // 物品不可强化
			isErr = true;
		}

		if (tgItem.getBless() >= 128) {// 封印的装備
			isErr = true;
		}

		if (isErr) {
			pc.sendPackets(new S_ServerMessage(79));// 没有任何事发生
			return;
		}

		if (tgItem.getEnchantLevel() < ConfigOther.WEAPON100) {
			pc.getInventory().removeItem(item, 1);

			final EnchantExecutor enchantExecutor = new EnchantWeapon();
			final int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
			enchantExecutor.successEnchant(pc, tgItem, randomELevel);

		} else {
			// 2401 已達武器最大追加，沒有任何事情發生！
			pc.sendPackets(new S_ServerMessage("已達武器最大強化!"));
		}
	}
}