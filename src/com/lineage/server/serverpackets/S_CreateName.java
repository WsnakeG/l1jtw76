package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 製作者
 * 
 * @author Roy
 */
public class S_CreateName extends ServerBasePacket {

	private byte[] _byte = null;

	public S_CreateName(final L1ItemInstance item, final L1Character pc) {
		writeC(S_OPCODE_PACKETBOX);
		writeH(0x8e);
		writeD(item.getId());
		if (item.get_creater_name() == null) {
			writeS("");
		} else {
			writeS(item.get_creater_name());
		}
		writeC(44);
		// 記憶座標擴充水晶相關
		for (int i = 0; i < 44; i++) {
			writeS(pc.getName() + i);
			writeD(4);
			writeH(32865);
			writeH(33251);
		}
		writeH(0);
		writeC(0);
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
