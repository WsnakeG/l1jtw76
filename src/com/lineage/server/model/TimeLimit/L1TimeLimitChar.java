package com.lineage.server.model.TimeLimit;

/**
 * 限時商人個人限購暫存
 * 類名稱：L1TimeLimitChar<br>
 * 創建人:四海<br>
 * 修改時間：2018年9月7日 上午5:36:22<br>
 * 修改人:QQ:403471355<br>
 * 修改備註:<br>
 * @version 2.7c<br>
 */
public class L1TimeLimitChar {

	private static int _charobjid;
	
	public void set_charobjid(int i) {
		_charobjid = i;
	}
	
	public int get_charobjid() {
		return _charobjid;
	}
	
	private int _itemid;
	
	public void set_itemid(int i) {
		_itemid = i;
	}
	
	public int get_itemid() {
		return _itemid;
	}
}
