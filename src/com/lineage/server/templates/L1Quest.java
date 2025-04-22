package com.lineage.server.templates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * Quest(任務)設置 暫存
 * 
 * @author daien
 */
public class L1Quest {

	private static final Log _log = LogFactory.getLog(L1Quest.class);

	private int _id;// 任務編號

	private String _questname;// 任務NAMEID

	private String _questclass;// 任務CLASS

	private boolean _queststart;// 任務是否啟用

	private boolean _del;// 任務是否可以刪除

	private int _questlevel;// 任務可執行等級

	private int _difficulty;// 任務難度

	private String _note;// 任務說明

	private boolean _isCrown;// 王族可執行:1

	private boolean _isKnight;// 騎士可執行:2

	private boolean _isElf;// 精靈可執行:4

	private boolean _isWizard;// 法師可執行:8

	private boolean _isDarkelf;// 黑暗精靈可執行:16

	private boolean _isDragonKnight;// 龍騎士可執行:32

	private boolean _isIllusionist;// 幻術師可執行:64

	private boolean _isWarrior;// 戰士可執行:128

	/**
	 * 任務編號
	 * 
	 * @return
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * 任務編號
	 * 
	 * @param _id
	 */
	public void set_id(final int _id) {
		this._id = _id;
	}

	/**
	 * 任務NAMEID
	 * 
	 * @return
	 */
	public String get_questname() {
		return _questname;
	}

	/**
	 * 任務NAMEID
	 * 
	 * @param _questname
	 */
	public void set_questname(final String _questname) {
		this._questname = _questname;
	}

	/**
	 * 任務CLASS
	 * 
	 * @return
	 */
	public String get_questclass() {
		return _questclass;
	}

	/**
	 * 任務CLASS
	 * 
	 * @param _questclass
	 */
	public void set_questclass(final String _questclass) {
		this._questclass = _questclass;
	}

	/**
	 * 任務是否啟用
	 * 
	 * @return
	 */
	public boolean is_queststart() {
		return _queststart;
	}

	/**
	 * 任務是否啟用
	 * 
	 * @param _queststart
	 */
	public void set_queststart(final boolean _queststart) {
		this._queststart = _queststart;
	}

	/**
	 * 任務可執行等級
	 * 
	 * @return
	 */
	public int get_questlevel() {
		return _questlevel;
	}

	/**
	 * 任務可執行等級
	 * 
	 * @param _questlevel
	 */
	public void set_questlevel(final int _questlevel) {
		this._questlevel = _questlevel;
	}

	/**
	 * 任務步驟
	 * 
	 * @param _difficulty
	 */
	public void set_difficulty(final int _difficulty) {
		this._difficulty = _difficulty;
	}

	/**
	 * 任務步驟
	 * 
	 * @return
	 */
	public int get_difficulty() {
		return _difficulty;
	}

	/**
	 * 任務說明
	 * 
	 * @param _note
	 */
	public void set_note(final String _note) {
		this._note = _note;
	}

	/**
	 * 任務說明
	 * 
	 * @return
	 */
	public String get_note() {
		return _note;
	}

	private static final int _int8 = 128;// 戰士可執行:128
	private static final int _int7 = 64;// 幻術師可執行:64
	private static final int _int6 = 32;// 龍騎士可執行:32
	private static final int _int5 = 16;// 黑暗精靈可執行:16
	private static final int _int4 = 8;// 法師可執行:8
	private static final int _int3 = 4;// 精靈可執行:4
	private static final int _int2 = 2;// 騎士可執行:2
	private static final int _int1 = 1;// 王族可執行:1

	/**
	 * 任務可執行職業設置
	 * 
	 * @param questuser
	 */
	public void set_questuser(int questuser) {
		try {
			if (questuser >= _int8) {
				questuser -= _int8;
				_isWarrior = true;
			}
			if (questuser >= _int7) {
				// 幻術師可執行:64
				questuser -= _int7;
				_isIllusionist = true;
			}
			if (questuser >= _int6) {
				// 龍騎士可執行:32
				questuser -= _int6;
				_isDragonKnight = true;
			}
			if (questuser >= _int5) {
				// 黑暗精靈可執行:16
				questuser -= _int5;
				_isDarkelf = true;
			}
			if (questuser >= _int4) {
				// 法師可執行:8
				questuser -= _int4;
				_isWizard = true;
			}
			if (questuser >= _int3) {
				// 精靈可執行:4
				questuser -= _int3;
				_isElf = true;
			}
			if (questuser >= _int2) {
				// 騎士可執行:2
				questuser -= _int2;
				_isKnight = true;
			}
			if (questuser >= _int1) {
				// 王族可執行:1
				questuser -= _int1;
				_isCrown = true;
			}

			if (questuser > 0) {
				_log.error("任務可執行職業設定錯誤:餘數大於0 編號:" + _id);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 王族可執行:1
	 * 
	 * @return
	 */
	/*
	 * public boolean isCrown() { return _isCrown; } /** 騎士可執行:2
	 * @return
	 */
	/*
	 * public boolean isKnight() { return _isKnight; } /** 精靈可執行:4
	 * @return
	 */
	/*
	 * public boolean isElf() { return _isElf; } /** 法師可執行:8
	 * @return
	 */
	/*
	 * public boolean isWizard() { return _isWizard; } /** 黑暗精靈可執行:16
	 * @return
	 */
	/*
	 * public boolean isDarkelf() { return _isDarkelf; } /** 龍騎士可執行:32
	 * @return
	 */
	/*
	 * public boolean isDragonKnight() { return _isDragonKnight; } /** 幻術師可執行:64
	 * @return
	 */
	/*
	 * public boolean isIllusionist() { return _isIllusionist; }
	 */

	/**
	 * 可執行職業判斷
	 * 
	 * @param pc
	 * @return
	 */
	public boolean check(final L1PcInstance pc) {
		try {
			if (pc.isCrown() && _isCrown) {
				// 王族可執行:1
				return true;
			}
			if (pc.isKnight() && _isKnight) {
				// 騎士可執行:2
				return true;
			}
			if (pc.isElf() && _isElf) {
				// 精靈可執行:4
				return true;
			}
			if (pc.isWizard() && _isWizard) {
				// 法師可執行:8
				// System.out.println("法師可執行:8 - 任務編號" + _id);
				return true;
			}
			if (pc.isDarkelf() && _isDarkelf) {
				// 黑暗精靈可執行:16
				return true;
			}
			if (pc.isDragonKnight() && _isDragonKnight) {
				// 龍騎士可執行:32
				return true;
			}
			if (pc.isIllusionist() && _isIllusionist) {
				// 幻術師可執行:64
				return true;
			}
			if (pc.isWarrior() && _isWarrior) {
				return true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 任務是否可以刪除
	 * 
	 * @param del true:可以刪除 false:不可以刪除
	 */
	public void set_del(final boolean del) {
		_del = del;
	}

	/**
	 * 任務是否可以刪除
	 * 
	 * @return true:可以刪除 false:不可以刪除
	 */
	public boolean is_del() {
		return _del;
	}
}
