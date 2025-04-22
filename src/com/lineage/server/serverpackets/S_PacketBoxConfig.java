package com.lineage.server.serverpackets;

import com.lineage.server.templates.L1Config;

/**
 * 人物快速鍵紀錄檔案(S_OPCODE_PACKETBOX)
 * 
 * @author dexc
 */
public class S_PacketBoxConfig extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * <font color=#00800>更新角色使用的快速鍵</font>
	 */
	public static final int CHARACTER_CONFIG = 0x29;// 41

	/**
	 * 人物快速鍵紀錄檔案
	 * 
	 * @param config
	 */
	public S_PacketBoxConfig(final L1Config config) {
		final int length = config.getLength();
		final byte data[] = config.getData();

		writeC(S_OPCODE_PACKETBOX);
		writeC(CHARACTER_CONFIG);// 41
		writeD(length);
		writeByte(data);
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
