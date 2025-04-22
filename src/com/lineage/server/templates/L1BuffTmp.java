package com.lineage.server.templates;

/**
 * 保留技能紀錄
 * 
 * @author dexc
 */
public class L1BuffTmp {

	private int _char_obj_id;

	private int _skill_id;

	private int _remaining_time;

	private int _poly_id;

	public int get_char_obj_id() {
		return _char_obj_id;
	}

	public void set_char_obj_id(final int char_obj_id) {
		_char_obj_id = char_obj_id;
	}

	public int get_skill_id() {
		return _skill_id;
	}

	public void set_skill_id(final int skill_id) {
		_skill_id = skill_id;
	}

	public int get_remaining_time() {
		return _remaining_time;
	}

	public void set_remaining_time(final int remaining_time) {
		_remaining_time = remaining_time;
	}

	public int get_poly_id() {
		return _poly_id;
	}

	public void set_poly_id(final int poly_id) {
		_poly_id = poly_id;
	}

}
