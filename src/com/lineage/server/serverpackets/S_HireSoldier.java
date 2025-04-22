package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_HireSoldier extends ServerBasePacket {

	private byte[] _byte = null;

	// HTMLを開いているときにこのパケットを送るとnpcdeloy-j.htmlが表示される
	// OKボタンを押すとC_127が飛ぶ
	public S_HireSoldier(final L1PcInstance pc) {
		/*
		 * this.writeC(S_OPCODE_HIRESOLDIER); this.writeH(0x0000); // ?
		 * クライアントが返すパケットに含まれる this.writeH(0x0000); // ? クライアントが返すパケットに含まれる
		 * this.writeH(0x0000); // 雇用された傭兵の総数 this.writeS(pc.getName());
		 * this.writeD(0x00000000); // ? クライアントが返すパケットに含まれる this.writeH(0x0000);
		 * // 配置可能な傭兵数
		 */}

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
