package com.lineage.data.item_etcitem.reel;

import java.util.Random;

import com.lineage.data.cmd.EnchantArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1ItemUpdata;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 奇安的卷軸40129<br>
 * 奇安的卷軸140129<br>
 */
public class GianScroll extends ItemExecutor {

	/**
	 *
	 */
	private GianScroll() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new GianScroll();
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

		// 安定值
		final int safe_enchant = tgItem.getItem().get_safeenchant();

		boolean isErr = false;

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

		// 物品已追加值
		final int enchant_level = tgItem.getEnchantLevel();
		final EnchantExecutor enchantExecutor = new EnchantArmor();
		int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
		pc.getInventory().removeItem(item, 1);

		boolean isEnchant = true;
		if (enchant_level < -6) {// 盔甲将会消失,最大可追加到-7
			isEnchant = false;

		} else if (enchant_level < safe_enchant) {// 安定值內
			isEnchant = true;

		} else {// 超出安定值

			final Random random = new Random();
			final int rnd = random.nextInt(100) + 1;
			int enchant_chance_armor;
			int enchant_level_tmp;

			if (safe_enchant == 0) { // 對防具安定直為0初始計算+2
				enchant_level_tmp = enchant_level + 2;

			} else {
				enchant_level_tmp = enchant_level;
			}

			if (enchant_level >= 9) {
				enchant_chance_armor = (int) L1ItemUpdata.enchant_armor_up9(enchant_level_tmp);

			} else {
				enchant_chance_armor = (int) L1ItemUpdata.enchant_armor_dn9(enchant_level_tmp);
			}

			if (rnd < enchant_chance_armor) {
				isEnchant = true;

			} else {
				if ((enchant_level >= 9) && (rnd < (enchant_chance_armor * 2))) {
					randomELevel = 0;

				} else {
					isEnchant = false;
				}
			}
		}
		if ((randomELevel <= 0) && (enchant_level > -6)) {
			isEnchant = true;
		}

		if (isEnchant) {// 成功
			enchantExecutor.successEnchant(pc, tgItem, randomELevel);

		} else {// 失敗
			enchantExecutor.failureEnchant(pc, tgItem);
		}
	}
}