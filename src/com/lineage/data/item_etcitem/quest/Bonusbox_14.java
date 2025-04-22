package com.lineage.data.item_etcitem.quest;

import java.util.Random;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 第14階段獎勵箱子
 * 
 * @author terry0412
 */
public class Bonusbox_14 extends ItemExecutor {

	private final Random _random = new Random();

	/**
	 *
	 */
	private Bonusbox_14() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Bonusbox_14();
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
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		pc.getInventory().removeItem(item, 1);
		// 給予道具的種類數量 (可開到XX種)
		final int value = _random.nextInt(3) + 1;
		// 隨機賦予道具
		for (int i = 0; i < value; i++) {
			CreateNewItem.createNewItem(pc, bonus_list[_random.nextInt(bonus_list.length)], 1);
		}
	}

	// 給予的獎勵列表 (至少得放進1個元素)
	private final int[] bonus_list = new int[] {
			// 歐林的日記頁
			56216, 56217, 56218, 56219, 56220, 56221, 56222, 56223, 56224, 56225, 56226, 56227, 56228, 56229,
			56230, 56231, 56232, 56233, 49334, 49333, 49332, 49331, 49330, 49329, 49328, 49327, 56235, 49336,
			// XXX

	};
}
