package com.lineage.server.serverpackets;

import java.io.IOException;

/**
 * 動態變更gfx封包
 * 
 * @author Roy
 */
public class S_PacketBoxUpdateGfxid extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PacketBoxUpdateGfxid(final int objid, final int gfxid) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(58);
		writeD(objid);
		writeH(gfxid);
	}

	@Override
	public byte[] getContent() throws IOException {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
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