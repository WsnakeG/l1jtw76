package com.lineage.data.item_etcitem.extra;

import java.util.List;
import java.util.Random;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ExtraMagicStoneTable;
import com.lineage.server.datatables.lock.CharItemPowerHoleReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemPowerHole_name;
import com.lineage.server.templates.L1MagicStone;

/**
 * 寶石鑲嵌系統(DB自製)
 * 
 * @author terry0412
 */
public class MagicStone extends ItemExecutor {

	private static final Random _random = new Random();

	/**
	 *
	 */
	private MagicStone() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new MagicStone();
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

		if (tgItem.isEquipped()) {
			pc.sendPackets(new S_SystemMessage("你必須先解除物品裝備!"));
			return;
		}

		if (item.getId() == tgItem.getId() && item.getCount() < 2) {
			pc.sendPackets(new S_SystemMessage("合成寶石必須至少擁有兩顆..."));
			return;
		}

		// 寶石系統讀取資料
		final L1MagicStone magicStone = ExtraMagicStoneTable.getInstance().findStone(item.getItemId());
		if (magicStone == null) {
			return;
		}

		// 寶石合成機制
		final L1MagicStone magicStone2 = ExtraMagicStoneTable.getInstance().findStone(tgItem.getItemId());
		if (magicStone2 != null) {
			final List<L1MagicStone> nextStoneList = ExtraMagicStoneTable.getInstance()
					.nextStageStone(magicStone.getStage() + 1);
			if (!nextStoneList.isEmpty()) {
				pc.getInventory().removeItem(item, 1);
				pc.getInventory().removeItem(tgItem, 1);

				// 合成機率 = (A寶石 + B寶石) 除以3
				if (_random.nextInt(100) < (magicStone.getChance() + magicStone2.getChance()) / 3) {
					final L1MagicStone nextStone = nextStoneList.get(_random.nextInt(nextStoneList.size()));
					if (nextStone != null) {
						CreateNewItem.createNewItem(pc, nextStone.getItemId(), 1);
						return;
					}
				}
				pc.sendPackets(new S_SystemMessage("合成失敗..."));
				return;

			} else {
				pc.sendPackets(new S_SystemMessage("目前沒有更高的寶石階級可以合成!!"));
			}
			return;
		}

		// 判斷寶石鑲嵌對象
		if (magicStone.getUseType() > 0 && magicStone.getUseType() != tgItem.getItem().getType2()) {
			pc.sendPackets(new S_SystemMessage("寶石鑲嵌對象錯誤!!"));
			return;
		}

		// 判斷打洞過程
		final L1ItemPowerHole_name power = tgItem.get_power_name_hole();
		if (power == null) {
			pc.sendPackets(new S_SystemMessage("\\aG這個武器或防具還未開孔過喔！"));
			return;
		}

		// 計算剩餘孔數
		int remainHole = power.get_hole_count();
		if (power.get_hole_1() != 0) {
			final L1MagicStone tempStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_1());
			if (tempStone != null) {
				remainHole -= tempStone.getNeedHole();
			} else {
				power.set_hole_1(0);
			}
		}
		if (power.get_hole_2() != 0) {
			final L1MagicStone tempStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_2());
			if (tempStone != null) {
				remainHole -= tempStone.getNeedHole();
			} else {
				power.set_hole_2(0);
			}
		}
		if (power.get_hole_3() != 0) {
			final L1MagicStone tempStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_3());
			if (tempStone != null) {
				remainHole -= tempStone.getNeedHole();
			} else {
				power.set_hole_3(0);
			}
		}
		if (power.get_hole_4() != 0) {
			final L1MagicStone tempStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_4());
			if (tempStone != null) {
				remainHole -= tempStone.getNeedHole();
			} else {
				power.set_hole_4(0);
			}
		}
		if (power.get_hole_5() != 0) {
			final L1MagicStone tempStone = ExtraMagicStoneTable.getInstance().findStone(power.get_hole_5());
			if (tempStone != null) {
				remainHole -= tempStone.getNeedHole();
			} else {
				power.set_hole_5(0);
			}
		}

		// 鑲嵌不論成功或失敗都會吃掉的所須凹槽數
		final int needHole = magicStone.getNeedHole();
		// 判斷是否有足夠孔數鑲嵌
		if (remainHole < needHole) {
			pc.sendPackets(new S_SystemMessage("\\aG這個物品沒有足夠孔數  (該寶石所需孔數: " + needHole + ")"));
			return;
		}

		// 刪除寶石數量一個
		pc.getInventory().removeItem(item, 1);

		// 鑲嵌成功機率
		final int chance = magicStone.getChance();
		if (_random.nextInt(100) >= chance) {
			// 鑲嵌失敗是否扣除孔數
			if (magicStone.isDeleteHole()) {
				// 扣除孔數 (依照寶石而定)
				power.set_hole_count(power.get_hole_count() - needHole);

				// 更新道具顯示
				pc.sendPackets(new S_ItemStatus(tgItem));

				// 更新資料
				CharItemPowerHoleReading.get().updateItem(tgItem.getId(), tgItem.get_power_name_hole());
				// 失敗特效
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_e));
				pc.sendPackets(new S_SystemMessage("\\aL寶石鑲嵌失敗! (自動扣除孔數: " + needHole + ")"));
				return;
			}
			// 失敗特效
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_e));
			pc.sendPackets(new S_SystemMessage("\\aI寶石鑲嵌失敗! (沒有任何損失)"));
			return;
		}

		// 鑲嵌成功固定會扣除的孔數 (依照寶石而定)
		// power.set_hole_count(power.get_hole_count() - needHole + 1);

		// 鑲嵌對應道具編號
		final int itemId = magicStone.getItemId();
		if (power.get_hole_1() == 0) {
			power.set_hole_1(itemId);
		} else if (power.get_hole_2() == 0) {
			power.set_hole_2(itemId);
		} else if (power.get_hole_3() == 0) {
			power.set_hole_3(itemId);
		} else if (power.get_hole_4() == 0) {
			power.set_hole_4(itemId);
		} else if (power.get_hole_5() == 0) {
			power.set_hole_5(itemId);
		}
		// 更新道具顯示
		pc.sendPackets(new S_ItemStatus(tgItem));

		// 更新資料
		CharItemPowerHoleReading.get().updateItem(tgItem.getId(), tgItem.get_power_name_hole());
		// 成功特效
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_s));
		pc.sendPackets(new S_SystemMessage("\\aD寶石成功鑲嵌  (自動扣除孔數: " + needHole + ")"));
	}

	private int _gfxid_e; // 失敗特效編號
	private int _gfxid_s; // 成功特效編號

	@Override
	public void set_set(String[] set) {
		try {
			_gfxid_e = Integer.parseInt(set[1]);
			_gfxid_s = Integer.parseInt(set[2]);

		} catch (Exception e) {
		}
	}
}
