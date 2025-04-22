package com.lineage.data.item_etcitem.extra;

import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemPowerHoleReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemPowerHole_name;

/**
 * 打洞槌 (XX% / 孔數上限5孔 / 失敗特效 / 成功特效)
 * 
 * @author terry0412
 */
public class HoleGavel extends ItemExecutor {

	private static final Random _random = new Random();

	/**
	 *
	 */
	private HoleGavel() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new HoleGavel();
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

		//// 目標對象不能是指定類型以外
		if (tgItem.getItem().getUseType() == 23 || tgItem.getItem().getUseType() == 24
				|| tgItem.getItem().getUseType() == 37 || tgItem.getItem().getUseType() == 40
				|| tgItem.getItem().getUseType() == 43 || tgItem.getItem().getUseType() == 44
				|| tgItem.getItem().getUseType() == 45 || tgItem.getItem().getUseType() == 47
				|| tgItem.getItem().getUseType() == 48) {
			pc.sendPackets(new S_SystemMessage("強烈警告：這只能使用在武器或防具上！"));
			return;
		}

		// 目標對象不能是道具 L1EtcItem
		if (tgItem.getItem().getType2() == 0) {
			pc.sendPackets(new S_SystemMessage("強烈警告：不能對道具使用，只能使用在武器或防具上！"));
			return;
		}

		// 判斷打洞過程
		L1ItemPowerHole_name power = tgItem.get_power_name_hole();

		if (power != null) {
			if (power.get_hole_count() >= _holeCount) {
				pc.sendPackets(new S_SystemMessage("\\aG可開啟的孔數已達上限..."));
				return;
			}
			pc.getInventory().removeItem(item, 1);

			if (_random.nextInt(100) >= _chance) {
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_e));
				pc.sendPackets(new S_SystemMessage("\\aD擴充失敗..."));
				return;
			}

			power.set_hole_count(power.get_hole_count() + 1);

			// 更新資料
			CharItemPowerHoleReading.get().updateItem(tgItem.getId(), tgItem.get_power_name_hole());

		} else {
			pc.getInventory().removeItem(item, 1);

			if (_random.nextInt(100) >= _chance) {
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_e));
				pc.sendPackets(new S_SystemMessage("\\aD擴充失敗..."));
				return;
			}

			power = new L1ItemPowerHole_name();
			power.set_item_obj_id(tgItem.getId());
			power.set_hole_count(power.get_hole_count() + 1);
			tgItem.set_power_name_hole(power);

			// 儲存資料
			CharItemPowerHoleReading.get().storeItem(tgItem.getId(), tgItem.get_power_name_hole());
		}
		pc.sendPackets(new S_ItemName(tgItem));
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_s));
		pc.sendPackets(new S_SystemMessage("\\aG一瞬間發出金黃色的光芒，已成功開孔!"));
	}

	private int _chance; // 打洞機率
	private int _holeCount; // 最大洞數上限
	private int _gfxid_e; // 失敗特效編號
	private int _gfxid_s; // 成功特效編號

	@Override
	public void set_set(String[] set) {
		try {
			_chance = Integer.parseInt(set[1]);
			_holeCount = Integer.parseInt(set[2]);
			_gfxid_e = Integer.parseInt(set[3]);
			_gfxid_s = Integer.parseInt(set[4]);

		} catch (Exception e) {
		}
	}
}
