package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 娃娃各項能力設置抽象接口
 * 
 * @author daien
 */
public abstract class L1DollExecutor {

	/**
	 * 設置能力設定值
	 * 
	 * @param int1
	 * @param int2
	 * @param int3
	 */
	public abstract void set_power(int int1, int int2, int int3);

	/**
	 * 裝備娃娃效果
	 * 
	 * @param pc
	 * @return
	 */
	public abstract void setDoll(L1PcInstance pc);

	/**
	 * 解除娃娃效果
	 * 
	 * @param pc
	 * @return
	 */
	public abstract void removeDoll(L1PcInstance pc);

	/**
	 * 是否重新設置
	 * 
	 * @return
	 */
	public abstract boolean is_reset();
}