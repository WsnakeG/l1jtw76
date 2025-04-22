package com.lineage.data.item_etcitem.extra;

import java.util.Random;

import com.lineage.config.ConfigAlt;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ExtraAttrWeaponTable;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1AttrWeapon;

/**
 * 屬性強化卷軸 (參數:[屬性id]) 衝過機率由DB控制
 * 
 * @author terry0412
 */
public class AttrWeaponScroll2 extends ItemExecutor {

	private static final Random _random = new Random();

	/**
	 *
	 */
	private AttrWeaponScroll2() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new AttrWeaponScroll2();
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
		int attrEnchantLevel = tgItem.getAttrEnchantLevel();

		// 不同屬性 則轉換成該卷軸的屬性
		if (attrEnchantKind != _attr_id) {
			attrEnchantLevel = 0;
		}

		final L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(_attr_id,
				attrEnchantLevel + 1);
		if ((attrWeapon == null) || (attrWeapon.getChance() <= 0)) {
			pc.sendPackets(new S_SystemMessage("已達該屬性最高階級。"));
			return;
		}

		pc.getInventory().removeItem(item, 1);

		if (_random.nextInt(100) < attrWeapon.getChance()) {
			// 對\f1%0附加強大的魔法力量成功。
			pc.sendPackets(new S_ServerMessage(1410, tgItem.getLogName()));

			tgItem.setAttrEnchantKind(_attr_id);
			tgItem.setAttrEnchantLevel(attrEnchantLevel + 1);

		} else {
			// 對\f1%0附加魔法失敗。
			pc.sendPackets(new S_ServerMessage(1411, tgItem.getLogName()));
			// 升階失敗，階位歸零 added by terry0412
			if (ConfigAlt.AttrSwitch) {
			tgItem.setAttrEnchantKind(0);
			tgItem.setAttrEnchantLevel(0);
		  }
		}

		// 修復武器屬性 儲存機制 by terry0412
		final int column = L1PcInventory.COL_ATTR_ENCHANT_KIND + L1PcInventory.COL_ATTR_ENCHANT_LEVEL;
		pc.getInventory().updateItem(tgItem, column);
		pc.getInventory().saveItem(tgItem, column);
	}

	private int _attr_id;

	@Override
	public void set_set(final String[] set) {
		try {
			_attr_id = Integer.parseInt(set[1]);
		} catch (final Exception e) {
		}
	}
}
