package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>驚喜箱</font><BR>
 * FIXME 未完成
 * 
 * @author dexc
 */
public class Box_Happy1 extends ItemExecutor {

	/**
	 *
	 */
	private Box_Happy1() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Box_Happy1();
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

	}
}
