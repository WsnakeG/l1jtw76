package com.lineage.server.templates;

import com.lineage.server.datatables.lock.CharSkillReading;

/**
 * 人物技能紀錄
 * 
 * @author dexc
 */
public class L1UserSkillTmp {

	private int _char_obj_id;

	private int _skill_id;

	private String _skill_name;

	private int _is_active;

	private int _activetimeleft;

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

	public String get_skill_name() {
		return _skill_name;
	}

	public void set_skill_name(final String skill_name) {
		_skill_name = skill_name;
	}

	public int get_is_active() {
		return _is_active;
	}

	public void is_active(final int is_active) {
		CharSkillReading.get().setAuto(is_active, _char_obj_id, _skill_id);
		set_is_active(is_active);
	}

	public void set_is_active(final int is_active) {
		_is_active = is_active;
	}

	public int get_activetimeleft() {
		return _activetimeleft;
	}

	public void set_activetimeleft(final int activetimeleft) {
		_activetimeleft = activetimeleft;
	}
}
