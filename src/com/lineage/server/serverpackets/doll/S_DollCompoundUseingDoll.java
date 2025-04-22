package com.lineage.server.serverpackets.doll;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 
 * @author kyo
 *
 */
public class S_DollCompoundUseingDoll extends ServerBasePacket {
	
	private byte[] _byte = null;
	

	public S_DollCompoundUseingDoll(final int objid) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x0080);
		if (objid > 0) {
			this.writeInt32(1, objid);
		}
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
