package com.lineage.server.templates;

/**
 * 人物好友紀錄
 * 
 * @author dexc
 */
public class L1BuddyTmp {

	private int _char_id;

	private int _buddy_id;

	private String _buddy_name;

	public int get_char_id() {
		return _char_id;
	}

	public void set_char_id(final int char_id) {
		_char_id = char_id;
	}

	public int get_buddy_id() {
		return _buddy_id;
	}

	public void set_buddy_id(final int buddy_id) {
		_buddy_id = buddy_id;
	}

	public String get_buddy_name() {
		return _buddy_name;
	}

	public void set_buddy_name(final String buddy_name) {
		_buddy_name = buddy_name;
	}

}
