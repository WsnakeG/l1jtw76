package com.lineage.data.item_etcitem.reel;

import java.util.Random;

import com.lineage.config.ConfigRate;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 44117 光之武器強化卷軸
 * 
 * @author loli
 */
public class ScrollEnchantS1Weapon extends ItemExecutor {

	/**
	 *
	 */
	private ScrollEnchantS1Weapon() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ScrollEnchantS1Weapon();
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

		// 目標物品
		final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

		if (tgItem == null) {
			return;
		}
		// 0:無属性 1:地 2:火 4:水 8:風 16:光 32:暗 64:聖 128:邪
		final int oldAttrEnchantKind = tgItem.getAttrEnchantKind();
		final int oldAttrEnchantLevel = tgItem.getAttrEnchantLevel();
		final int newAttrEnchantKind = 16;// 16:光
		boolean isErr = false;

		// 取得物件觸發事件
		final int use_type = tgItem.getItem().getUseType();
		switch (use_type) {
		case 1:// 武器
				// 相同屬性強化直大於3
			if (oldAttrEnchantKind == newAttrEnchantKind) {
				if (oldAttrEnchantLevel >= 3) {
					isErr = true;
				}
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
			pc.sendPackets(new S_ServerMessage(79)); // 没有发生任何事情。
			return;
		}

		pc.getInventory().removeItem(item, 1);

		final Random random = new Random();
		final int rnd = random.nextInt(100) + 1;

		if (ConfigRate.ATTR_ENCHANT_CHANCE >= rnd) {
			// 1410 對\f1%0附加強大的魔法力量成功。
			pc.sendPackets(new S_ServerMessage(1410, tgItem.getLogName()));

			int newAttrEnchantLevel = oldAttrEnchantLevel + 1;

			if (oldAttrEnchantKind != newAttrEnchantKind) {
				newAttrEnchantLevel = 1;
			}

			tgItem.setAttrEnchantKind(newAttrEnchantKind);
			pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ATTR_ENCHANT_KIND);
			pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ATTR_ENCHANT_KIND);

			tgItem.setAttrEnchantLevel(newAttrEnchantLevel);
			pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ATTR_ENCHANT_LEVEL);
			pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ATTR_ENCHANT_LEVEL);

		} else {
			// 1411 對\f1%0附加魔法失敗。
			pc.sendPackets(new S_ServerMessage(1411, tgItem.getLogName()));
		}
	}
}