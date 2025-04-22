package com.lineage.server.model.c1;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 陣營階級能力設置抽象接口
 * 
 * @author daien
 */
public abstract class C1Executor {

	/**
	 * 設置階級能力設定值
	 * 
	 * @param int1
	 * @param int2
	 * @param int3
	 * @param int4
	 * @param int5
	 * @param int6
	 */
	public abstract void set_power(int int1, int int2, int int3, int int4, int int5, int int6, int int7,
			int int8, int int9, int int10, int int11, int int12, int int13, int int14);

	/**
	 * 階級能力效果
	 * 
	 * @param pc
	 * @return
	 */
	public abstract void set_c1(L1PcInstance pc);

	/**
	 * 解除階級能力效果
	 * 
	 * @param pc
	 * @return
	 */
	public abstract void remove_c1(L1PcInstance pc);
}