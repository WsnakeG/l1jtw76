package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 套裝效果接口
 * 
 * @author daien
 */
public interface ArmorSetEffect {

	/**
	 * 套裝效果啟用
	 * 
	 * @param pc
	 */
	public void giveEffect(L1PcInstance pc);

	/**
	 * 套裝效果結束
	 * 
	 * @param pc
	 */
	public void cancelEffect(L1PcInstance pc);

	/**
	 * 套裝效果的值
	 */
	public int get_mode();

}
