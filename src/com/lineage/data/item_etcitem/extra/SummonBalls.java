package com.lineage.data.item_etcitem.extra;

import java.sql.Timestamp;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;

/**
 * 怪物召喚球 (參數:召喚怪物ID, 召喚數量, 持續召喚時間, 召喚單隻所需魅力, 使用後是否刪除)
 * 
 * @author terry0412
 */
public class SummonBalls extends ItemExecutor {

	/**
	 *
	 */
	private SummonBalls() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new SummonBalls();
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
		if (!pc.getMap().isRecallPets()) {
			// 在這附近無法召喚怪物。
			pc.sendPackets(new S_ServerMessage(353));
			return;
		}
		// 設置延遲使用機制
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		item.setLastUsed(ts);
		pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
		pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);

		int petcost = 0; // 寵物或召喚怪累積的魅力總數
		int mob_count = 0; // 檢查指定怪物存在數量

		for (final L1NpcInstance pet : pc.getPetList().values()) {
			// 目前寵物數量
			petcost += pet.getPetcost();
			// 怪物編號相同
			if (pet.getNpcId() == _mobId) {
				mob_count++;
			}
		}

		// 是否已經召喚足夠數量
		if (mob_count >= _mobCount) {
			pc.sendPackets(new S_SystemMessage("\\aD你已經召喚了足夠數量。"));
			return;
		}

		final int charisma = (pc.getCha() + 6) - petcost;
		final int summoncount = Math.min((charisma / _petCost) - mob_count, _mobCount);

		// 魅力值不足無法召喚但不扣除道具召喚次數 141201 Roy
		if ((pc.getCha() + 6) < _petCost) {
			pc.sendPackets(new S_SystemMessage("\\aG魅力不足無法召喚出傭兵。"));
			return;
		}

		// 使用後是否刪除
		if (_isRemovable) {
			// 若剩餘次數剩餘1次，再次使用後將自動刪除道具
			if (item.getChargeCount() > 1) {
				item.setChargeCount(item.getChargeCount() - 1);
				pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);

			} else {
				pc.getInventory().removeItem(item, 1);
			}
		}

		final L1Npc npcTemp = NpcTable.get().getTemplate(_mobId);
		for (int i = 0; i < summoncount; i++) {
			final L1SummonInstance summon = new L1SummonInstance(npcTemp, pc, _time);
			summon.setPetcost(_petCost);
		}
	}

	private int _mobId; // 召喚怪物ID

	private int _mobCount; // 召喚數量

	private int _time; // 持續召喚時間

	private int _petCost; // 召喚單隻所需魅力

	private boolean _isRemovable; // 使用後是否刪除

	@Override
	public void set_set(final String[] set) {
		try {
			_mobId = Integer.parseInt(set[1]);
			_mobCount = Integer.parseInt(set[2]);
			_time = Integer.parseInt(set[3]);
			_petCost = Integer.parseInt(set[4]);
			_isRemovable = Boolean.parseBoolean(set[5]);

		} catch (final Exception e) {
		}
	}
}
