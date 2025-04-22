package com.lineage.data.item_etcitem.quest2;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 材料兌換機制<BR>
 * 自定義材料數量可以兌換相關道具 classname:quest2.weapon_up1 50 44070 1 441
 */
public class weapon_up1 extends ItemExecutor {

	private int _need_count; // 需求數量
	private int _create_itemid; // 滿足條件給予的道具編號
	private int _creat_count; // 給予的道具數量
	private int _gfxid_wu; // 特效編號

	/**
	 *
	 */
	private weapon_up1() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new weapon_up1();
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
		long count = item.getCount();
		if (count >= _need_count) {
			// 新增獲得道具後給予特效
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_wu));
			pc.getInventory().removeItem(item, _need_count);
			// 取得道具
			CreateNewItem.createNewItem(pc, _create_itemid, _creat_count);

		} else {
			// 337 \f1%0不足%s。
			pc.sendPackets(new S_ServerMessage(337, "所需的材料數量(" + (_need_count - count) + ")"));
		}
	}

	@Override
	public void set_set(String[] set) {
		_need_count = Integer.parseInt(set[1]);
		_create_itemid = Integer.parseInt(set[2]);
		_creat_count = Integer.parseInt(set[3]);
		_gfxid_wu = Integer.parseInt(set[4]);
	}
}