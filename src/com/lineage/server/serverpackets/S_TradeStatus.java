package com.lineage.server.serverpackets;

/**
 * 交易狀態
 * 
 * @author dexc
 */
public class S_TradeStatus extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 交易狀態
	 * 
	 * @param type 0:交易完成 1:交易取消
	 */
	public S_TradeStatus(final int type) {
		writeC(S_OPCODE_TRADESTATUS);
		writeC(type); // 0:交易完成 1:交易取消
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
