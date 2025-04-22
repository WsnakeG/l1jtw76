package com.lineage.data.item_etcitem.dominate;

import static com.lineage.server.model.skill.L1SkillId.DOMINATE_POWER_C;

import java.sql.Timestamp;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 武裝色霸氣 (參數:[機率受到物理攻擊會被反震暈隨機1~2秒] [效果持續多少秒] [特效編號 (每3秒1次)])
 * 
 * @author terry0412
 */
public class PowerItem3 extends ItemExecutor {

	/**
	 *
	 */
	private PowerItem3() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new PowerItem3();
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
		// 已經存在一種霸氣
		final int timeSec = L1BuffUtil.getDominatePower(pc);
		if (timeSec > 0) {
			pc.sendPackets(new S_SystemMessage("一次只能加持一種效果 (剩餘秒數:" + timeSec + ")"));
			return;
		}

		// 設置延遲使用機制
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		item.setLastUsed(ts);
		pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
		pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);

		if (_isRemovable) {
			pc.getInventory().removeItem(item, 1);
		}

		// 霸氣效果 暫存
		pc.setValue(_value1);
		// 特效編號
		pc.setEffectId(_value3);

		if (_value2 > 0) {
			pc.setSkillEffect(DOMINATE_POWER_C, _value2 * 1000);

			pc.sendPackets(new S_SystemMessage("你感到全身充滿強大的力量。"));
		}
	}

	private static int _value1;
	private static int _value2;
	private static int _value3;

	private static boolean _isRemovable;

	@Override
	public void set_set(final String[] set) {
		try {
			_value1 = Integer.parseInt(set[1]);
			_value2 = Integer.parseInt(set[2]);
			_value3 = Integer.parseInt(set[3]);
			_isRemovable = Boolean.parseBoolean(set[4]);
		} catch (final Exception e) {
		}
	}

	@Override
	public String[] get_set() {
		return new String[] { String.valueOf(_value1), String.valueOf(_value3) };
	}
}
