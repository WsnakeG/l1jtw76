package com.lineage.server.serverpackets.ability;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 純能力值資訊
 * @author kyo
 *
 */
public class S_BaseAbility extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * 純能力資訊
	 * @param baseStr
	 * @param baseInt
	 * @param baseWis
	 * @param baseDex
	 * @param baseCon
	 * @param baseCha
	 */
	public S_BaseAbility(final int baseStr, final int baseInt, final int baseWis, final int baseDex, final int baseCon, final int baseCha) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01EA);
		this.writeInt32(1, baseStr);
		this.writeInt32(2, baseInt);
		this.writeInt32(3, baseWis);
		this.writeInt32(4, baseDex);
		this.writeInt32(5, baseCon);
		this.writeInt32(6, baseCha);
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
