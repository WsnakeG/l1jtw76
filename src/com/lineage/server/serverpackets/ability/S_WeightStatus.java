package com.lineage.server.serverpackets.ability;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 負重程度資訊 7.6
 * @author kyo
 *
 */
public class S_WeightStatus extends ServerBasePacket {
	
	private byte[] _byte = null;

	/**
	 *  負重程度資訊 7.6
	 * @param stage
	 * @param invWeight
	 * @param maxWeight
	 */
	public S_WeightStatus(final int stage, final int invWeight, final int maxWeight) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E5);
		this.writeInt32(1, stage);// 負重百分比
		this.writeInt32(2, invWeight);// 背包重量
		this.writeInt32(3, maxWeight);// 角色最大負重量
		this.randomShort();
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName();
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
