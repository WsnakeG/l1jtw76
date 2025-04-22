package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 裝備保護卷軸
 * 
 * @author simlin
 */
public class EnchantProtecter extends ItemExecutor {

	/**
	 *
	 */
	private EnchantProtecter() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new EnchantProtecter();
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
		case 2:// 盔甲
		case 18:// T恤
		case 19:// 斗篷
		case 20:// 手套
		case 21:// 靴
		case 22:// 頭盔
		case 25:// 盾牌
		case 70:// 脛甲
			if (safe_enchant < 0) { // 物品不可保護
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

		if (tgItem.get_protect_type() > 0) {// 已經具有保護狀態
			isErr = true;
		}

		if (isErr) {
			// 此裝備無法使用裝備保護卷軸。
			pc.sendPackets(new S_ServerMessage(1309));
			return;
		}

		tgItem.set_protect_type(_type);
		pc.getInventory().removeItem(item, 1);
		pc.sendPackets(new S_ServerMessage(1308, tgItem.getLogName()));
	}

	private int _type = 0;

	@Override
	public void set_set(final String[] set) {
		try {
			_type = Integer.parseInt(set[1]);
		} catch (final Exception e) {
		}
	}
}