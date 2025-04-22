package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 角色狀態<br>
 * 人物遊戲畫面人物資料視窗顯示用
 * 
 * @author dexc
 */
public class S_OwnCharStatus2 extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色狀態
	 * 
	 * @param pc
	 */
	public S_OwnCharStatus2(final L1PcInstance pc) {
		if (pc == null) {
			return;
		}
		buildPacket(pc);
	}

	private void buildPacket(final L1PcInstance pc) {
		writeC(S_OPCODE_OWNCHARSTATUS2);
		writeC(pc.getStr());
		writeC(pc.getInt());
		writeC(pc.getWis());
		writeC(pc.getDex());
		writeC(pc.getCon());
		writeC(pc.getCha());
		writeC(pc.getInventory().getWeight240());
	}

	/**
	 * 角色狀態測試
	 * 
	 * @param pc 測試GM
	 * @param str 測試力量
	 */
	public S_OwnCharStatus2(final L1PcInstance pc, final int str) {
		writeC(S_OPCODE_OWNCHARSTATUS2);
		writeC(str);
		writeC(pc.getInt());
		writeC(pc.getWis());
		writeC(pc.getDex());
		writeC(pc.getCon());
		writeC(pc.getCha());
		writeC(pc.getInventory().getWeight240());
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}
}
