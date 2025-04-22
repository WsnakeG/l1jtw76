package com.lineage.server.templates;

/**
 * 線上獎勵暫存
 * 
 * @author simlin
 */
public class T_OnlineGiftModel {

	private int _giftIndex;

	private int _time;

	private int _itemId;

	private int _itemCount;

	private String _note;

	public T_OnlineGiftModel(final int giftIndex, final int time, final int itemId, final int itemCount,
			final String note) {
		_giftIndex = giftIndex;
		_time = time;
		_itemId = itemId;
		_itemCount = itemCount;
		_note = note;
	}

	public int getGiftIndex() {
		return _giftIndex;
	}

	public void setGiftIndex(final int giftIndex) {
		_giftIndex = giftIndex;
	}

	public int getTime() {
		return _time;
	}

	public void setTime(final int time) {
		_time = time;
	}

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(final int itemId) {
		_itemId = itemId;
	}

	public int getItemCount() {
		return _itemCount;
	}

	public void setItemCount(final int itemCount) {
		_itemCount = itemCount;
	}

	public String getNote() {
		return _note;
	}

	public void setNote(final String note) {
		_note = note;
	}
}