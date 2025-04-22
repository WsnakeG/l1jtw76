package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 6階段魔法骰子 41212
 */
public class DiceXil extends ItemExecutor {

	/**
	 *
	 */
	private DiceXil() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new DiceXil();
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
		int gfxid = 0;
		switch (item.getEnchantLevel()) {
		case 1:
			gfxid = 3204;
			break;
		case 2:
			gfxid = 3205;
			break;
		case 3:
			gfxid = 3206;
			break;
		case 4:
			gfxid = 3207;
			break;
		case 5:
			gfxid = 3208;
			break;
		case 6:
			gfxid = 3209;
			break;
		}

		if (gfxid != 0) {
			pc.sendPacketsAll(new S_SkillSound(pc.getId(), gfxid));
		}
	}
}
