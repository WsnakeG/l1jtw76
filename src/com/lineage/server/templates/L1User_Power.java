package com.lineage.server.templates;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 人物陣營紀錄資料
 * 
 * @author daien
 */
public class L1User_Power {

	private int _object_id;// 人物OBJID

	private int _c1_type;// 陣營

	private String _note;// 備註

	private L1Name_Power _power;// 能力

	/**
	 * 人物OBJID
	 * 
	 * @return
	 */
	public int get_object_id() {
		return _object_id;
	}

	/**
	 * 人物OBJID
	 * 
	 * @param _object_id
	 */
	public void set_object_id(final int _object_id) {
		this._object_id = _object_id;
	}

	/**
	 * 陣營
	 * 
	 * @return
	 */
	public int get_c1_type() {
		return _c1_type;
	}

	/**
	 * 陣營
	 * 
	 * @param _c1_type
	 */
	public void set_c1_type(final int _c1_type) {
		this._c1_type = _c1_type;
	}

	/**
	 * 備註
	 * 
	 * @return
	 */
	public String get_note() {
		return _note;
	}

	/**
	 * 備註
	 * 
	 * @param _note
	 */
	public void set_note(final String _note) {
		this._note = _note;
	}

	/**
	 * 設置能力
	 * 
	 * @param pc
	 * @param login 登入:true
	 */
	public void set_power(final L1PcInstance pc, final boolean login) {
		final int score = pc.get_other().get_score();
		final int lv = C1_Name_Type_Table.get().getLv(_c1_type, score);
		final L1Name_Power power = C1_Name_Type_Table.get().get(_c1_type, lv);
		final int record = pc.getQuest().get_step(L1PcQuest.QUEST_CAMP);
		if (_power != null) {
			if (power == null) {
				_power.get_c1_classname().remove_c1(pc);
				_power = null;

			} else {
				if (!_power.equals(power)) {
					_power.get_c1_classname().remove_c1(pc);
					power.get_c1_classname().set_c1(pc);

					if (power.get_c1_id() > record) {
						if (power.get_gift_box() > 0) {
							CreateNewItem.createNewItem(pc, power.get_gift_box(), 1);
						}
						pc.getQuest().set_step(L1PcQuest.QUEST_CAMP, power.get_c1_id());
					}
					_power = power;

				} else {
					if (login) {
						_power.get_c1_classname().set_c1(pc);
					}
				}
			}

		} else {
			_power = power;
			if (_power != null) {
				_power.get_c1_classname().set_c1(pc);
				if (record == 0) {
					pc.getQuest().set_step(L1PcQuest.QUEST_CAMP, _power.get_c1_id());
				}
			}
		}
	}

	/**
	 * 對應能力
	 */
	public L1Name_Power get_power() {
		return _power;
	}

	/**
	 * 對應階級
	 */
	public int get_power_lv() {
		return _power.get_c1_id();
	}

}
