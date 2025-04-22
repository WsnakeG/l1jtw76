package com.lineage.server.templates;

/**
 * 掉落物品資料
 * 
 * @author daien
 */
public final class L1Drop {

	private final int _mobId;
	private final int _itemId;
	private final int _enchant_min; // 最小強化值 by terry0412
	private final int _enchant_max; // 最大強化值 by terry0412
	private final int _min;
	private final int _max;
	private final int _chance;

	public L1Drop(final int mobId, final int itemId, final int enchant_min, final int enchant_max,
			final int min, final int max, final int chance) {
		_mobId = mobId;
		_itemId = itemId;
		_enchant_min = enchant_min;
		_enchant_max = enchant_max;
		_min = min;
		_max = max;
		_chance = chance;
	}

	/**
	 * 機率
	 * 
	 * @return
	 */
	public int getChance() {
		return _chance;
	}

	/**
	 * 物品編號
	 * 
	 * @return
	 */
	public int getItemid() {
		return _itemId;
	}

	/**
	 * 最大強化值
	 * 
	 * @return
	 */
	public int getEnchantMax() {
		return _enchant_max;
	}

	/**
	 * 最小強化值
	 * 
	 * @return
	 */
	public int getEnchantMin() {
		return _enchant_min;
	}

	/**
	 * 最大數量
	 * 
	 * @return
	 */
	public int getMax() {
		return _max;
	}

	/**
	 * 最小數量
	 * 
	 * @return
	 */
	public int getMin() {
		return _min;
	}

	/**
	 * NPC編號
	 * 
	 * @return
	 */
	public int getMobid() {
		return _mobId;
	}
}
