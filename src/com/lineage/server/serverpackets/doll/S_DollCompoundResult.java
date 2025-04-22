package com.lineage.server.serverpackets.doll;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 
 * @author kyo
 *
 */
public class S_DollCompoundResult extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public S_DollCompoundResult() {
		
	}

	public S_DollCompoundResult(final int type, final int objid, final int gfxid) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x007D);
		this.writeInt32(1, type);
		switch(type) {
		case 0:
		case 1:
		case 10:
			S_DollCompoundResult p = new S_DollCompoundResult();
			p.writeInt32(1, objid);
			p.writeInt32(2, gfxid);
			this.writeByteArray(2, p.getContent());
			break;
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
