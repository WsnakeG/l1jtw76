package com.lineage.server.model.skill;

import com.lineage.server.model.L1Character;

/**
 * 技能攻擊目標狀態
 * 
 * @author daien
 */
public class TargetStatus {

	private L1Character _target = null;

	private boolean _isAction = false;

	private boolean _isSendStatus = false;

	private boolean _isCalc = true;

	public TargetStatus(final L1Character cha) {
		_target = cha;
	}

	public TargetStatus(final L1Character cha, final boolean flg) {
		_target = cha;
		_isCalc = flg;
	}

	/**
	 * 傳回目標
	 * 
	 * @return
	 */
	public L1Character getTarget() {
		return _target;
	}

	/**
	 * 是否命中
	 * 
	 * @return
	 */
	public boolean isCalc() {
		// System.out.println("是否命中:" + _isCalc);
		return _isCalc;
	}

	/**
	 * 設置為未命中
	 * 
	 * @param flg
	 */
	public void isCalc(final boolean flg) {
		// System.out.println("設置為未命中:" + flg);
		_isCalc = flg;
	}

	public void isAction(final boolean flg) {
		_isAction = flg;
	}

	public boolean isAction() {
		return _isAction;
	}

	public void isSendStatus(final boolean flg) {
		_isSendStatus = flg;
	}

	public boolean isSendStatus() {
		return _isSendStatus;
	}
}
