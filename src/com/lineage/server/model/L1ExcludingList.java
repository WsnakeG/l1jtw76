package com.lineage.server.model;

import java.util.ArrayList;

/**
 * 人物訊息拒絕清單
 * 
 * @author daien
 */
public class L1ExcludingList {

	private final ArrayList<String> _nameList = new ArrayList<String>();

	public String[] toArray() {
		return _nameList.toArray(new String[_nameList.size()]);
	}
	
	/**
	 * 加入人物訊息拒絕清單
	 * 
	 * @param name
	 */
	public void add(final String name) {
		_nameList.add(name);
	}

	/**
	 * 移出人物訊息拒絕名單
	 * 
	 * @param name 移除名稱
	 * @return 移除成功返回人物名稱<BR>
	 *         清單中無該物件返回null
	 */
	public String remove(final String name) {
		for (final String each : _nameList) {
			if (each.equalsIgnoreCase(name)) {
				_nameList.remove(each);
				return each;
			}
		}
		return null;
	}

	/**
	 * 指定人物訊息拒絕接收
	 * 
	 * @return true:拒絕 false:接收
	 */
	public boolean contains(final String name) {
		for (final String each : _nameList) {
			if (each.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 拒絕清單人數是否達到上限
	 * 
	 * @return true:是 false:否
	 */
	public boolean isFull() {
		return (_nameList.size() >= 50) ? true : false;
	}
}
