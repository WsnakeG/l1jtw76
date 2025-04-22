package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 100%屬性強化卷 (參數:[屬性id] [屬性階級]) 使用後直接跳第XX階
 * 
 * @author terry0412
 */
public class AttrWeaponScroll extends ItemExecutor {

	/**
	 *
	 */
	private AttrWeaponScroll() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new AttrWeaponScroll();
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

		// 非武器類道具
		if (tgItem.getItem().getType2() != 1) {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}

		// 封印的装備
		if (tgItem.getBless() >= 128) {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}

		final int attrEnchantKind = tgItem.getAttrEnchantKind();
		final int attrEnchantLevel = tgItem.getAttrEnchantLevel();

		// 同屬性 且 本身階級超越卷軸
		if ((attrEnchantKind == _attr_id) && (attrEnchantLevel >= _stage)) {
			pc.sendPackets(new S_SystemMessage("武器本身階級已超過卷軸階級。"));
			return;
		}

		pc.getInventory().removeItem(item, 1);

		// 對\f1%0附加強大的魔法力量成功。
		pc.sendPackets(new S_ServerMessage(1410, tgItem.getLogName()));

		tgItem.setAttrEnchantKind(_attr_id);
		tgItem.setAttrEnchantLevel(_stage);

		// 修復武器屬性 儲存機制 by terry0412
		final int column = L1PcInventory.COL_ATTR_ENCHANT_KIND + L1PcInventory.COL_ATTR_ENCHANT_LEVEL;
		pc.getInventory().updateItem(tgItem, column);
		pc.getInventory().saveItem(tgItem, column);
	}

	private int _attr_id;

	private int _stage;

	@Override
	public void set_set(final String[] set) {
		try {
			_attr_id = Integer.parseInt(set[1]);
		} catch (final Exception e) {
		}
		try {
			_stage = Integer.parseInt(set[2]);
		} catch (final Exception e) {
		}
	}
}
