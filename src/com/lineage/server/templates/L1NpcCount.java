package com.lineage.server.templates;

/**
 * NPC 隊員資料暫存
 * 
 * @author daien
 */
public class L1NpcCount {

	private final int _id;

	private final int _count;

	public L1NpcCount(final int id, final int count) {
		_id = id;
		_count = count;
	}

	/**
	 * 隊員NPCID
	 * 
	 * @return
	 */
	public int getId() {
		return _id;
	}

	/**
	 * 隊員數量
	 * 
	 * @return
	 */
	public int getCount() {
		return _count;
	}

	public boolean isZero() {
		return (_id == 0) && (_count == 0);
	}
}
