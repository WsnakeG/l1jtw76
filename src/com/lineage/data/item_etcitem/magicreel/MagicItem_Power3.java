package com.lineage.data.item_etcitem.magicreel;

import static com.lineage.server.model.skill.L1SkillId.MAGIC_ITEM_POWER_C;

import java.sql.Timestamp;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * [特殊技能道具] 施放後xx秒內受到近距離傷害將以兩倍反擊傷害回去、 扣除道具、數量、特效編號、 使用道具不會消失開關
 * 
 * @author terry0412
 */
public class MagicItem_Power3 extends ItemExecutor {

	/**
	 *
	 */
	private MagicItem_Power3() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new MagicItem_Power3();
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
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}

		if (_consumeItemId != 0 && _consumeItemCount != 0
				&& !pc.getInventory().consumeItem(_consumeItemId, _consumeItemCount)) {
			pc.sendPackets(new S_SystemMessage("啟動媒介不足"));
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

		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		// 送出特效封包
		pc.sendPacketsAll(new S_SkillSound(pc.getId(), _gfxId));

		pc.setSkillEffect(MAGIC_ITEM_POWER_C, _timeSecs * 1000);

		pc.sendPackets(new S_SystemMessage(_timeSecs + "秒內受到近距離傷害將以兩倍反擊傷害回去"));
	}

	private int _timeSecs;

	private int _consumeItemId;
	private int _consumeItemCount;

	private int _gfxId;

	private boolean _isRemovable;

	@Override
	public void set_set(String[] set) {
		try {
			_timeSecs = Integer.parseInt(set[1]);

			_consumeItemId = Integer.parseInt(set[2]);
			_consumeItemCount = Integer.parseInt(set[3]);

			_gfxId = Integer.parseInt(set[4]);

			_isRemovable = Boolean.parseBoolean(set[5]);

		} catch (Exception e) {
		}
	}
}