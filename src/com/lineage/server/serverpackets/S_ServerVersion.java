package com.lineage.server.serverpackets;

import com.lineage.config.Config;


public final class S_ServerVersion extends ServerBasePacket {
	private byte[] _byte = null;

	private static final int CLIENT_LANGUAGE = Config.CLIENT_LANGUAGE;

	public S_ServerVersion(final boolean check) {
		this.writeC(S_OPCODE_SERVERVERSION);
		this.writeB(check);
		this.writeC(0X06);
		this.writeD(0x090373d6); 
		this.writeD(0x090373d6);
		this.writeD(0x77fdd029);
		this.writeD(0x090373d0);
		// 下面註解為8.1C
		/*this.writeD(0x099908c8);
		this.writeD(0x099908c8);
		this.writeD(0x781bd67d);
		this.writeD(0x099908c8);*/
		this.writeD((int) (System.currentTimeMillis() / 1000));
		this.writeC(0x00);
		this.writeC(0x00);
		this.writeC(CLIENT_LANGUAGE); 
		this.writeD(0x7cff7d82);
		this.writeD((int) (System.currentTimeMillis() / 1000));
		this.writeD(0x08f5a69c);
		this.writeD(0x08f3f1e5);
		this.writeD(0x0901e36d);
		// 下面註解為8.1C
		/*this.writeD(0x08f5a69c);
		this.writeD(0x08f3f1e5);
		this.writeD(0x0901e36d);
		this.writeD(0x099739e5);
		this.writeD(0x09977c4d);*/
	}

	@Override
	public byte[] getContent() {
		return this.getBytes();
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
