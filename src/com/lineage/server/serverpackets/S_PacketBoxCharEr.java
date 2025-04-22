package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 角色迴避
 * @author admin
 *
 */
public class S_PacketBoxCharEr extends ServerBasePacket {
	
	private byte[] _byte = null;

	/**
	 * 更新角色的迴避率
	 * @param pc
	 */
	public S_PacketBoxCharEr(final L1PcInstance pc) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.UPDATE_ER);
		writeC(pc.getEr());
	}
	
	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName() + " [S->C 發送封包盒子(更新角色的迴避率)]";
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
