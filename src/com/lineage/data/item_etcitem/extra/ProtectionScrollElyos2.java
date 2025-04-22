package com.lineage.data.item_etcitem.extra;

import java.util.Random;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.data.cmd.EnchantArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 魔族防具保護卷軸
 * 
 * @author terry0412
 */
public class ProtectionScrollElyos2 extends ItemExecutor {

	/**
	 *
	 */
	private ProtectionScrollElyos2() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ProtectionScrollElyos2();
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

		// 目標對象必須是防具
		if (tgItem.getItem().getType2() != 2) {
			pc.sendPackets(new S_SystemMessage("\\aG使用對象錯誤，請確認清楚。"));
			return;
		}

		if (tgItem.getItem().get_safeenchant() < 0 // 安定值小於0
				|| tgItem.getBless() >= 128) { // 封印的装備
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}

		// 飾品無法使用
		final int use_type = tgItem.getItem().getUseType();
		if (use_type == 23 // 戒指
				|| use_type == 24 // 項鍊
				|| use_type == 37 // 腰帶
				|| use_type == 40) { // 耳環
			pc.sendPackets(new S_SystemMessage("飾品無法使用。"));
			return;
		}

		// 最高只能強化到 +10
		if (tgItem.getEnchantLevel() >= ConfigOther.ArmorSet) {
			pc.sendPackets(new S_SystemMessage("\\aG這件裝備已經超過最高強化值上限。"));
			return;
		}
		pc.getInventory().removeItem(item, 1);

		final Random random = new Random();
		final EnchantExecutor enchantExecutor = new EnchantArmor();
		// 強化成功機率 (強化值增加1)
		if (random.nextInt(100) < ConfigAlt.ELYOS2_ENCHANT_SUCCESS) {
			enchantExecutor.successEnchant(pc, tgItem, 1);
			// 強化失敗機率 (強化值歸零)
		} else if (random.nextInt(100) < ConfigAlt.ELYOS2_ENCHANT_FAILURE) {
			enchantExecutor.successEnchant(pc, tgItem, -tgItem.getEnchantLevel());
			// 強化失敗機率 (強化值倒扣1)
		} else if (random.nextInt(100) < ConfigAlt.ELYOS2_ENCHANT_FAILURE2) {
			enchantExecutor.successEnchant(pc, tgItem, -1);
		} else { // 沒有任何事情發生
			enchantExecutor.successEnchant(pc, tgItem, 0);
		}
	}
}
