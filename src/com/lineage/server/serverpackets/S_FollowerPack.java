package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1FollowerInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件封包 - 跟隨者
 * 
 * @author dexc
 */
public class S_FollowerPack extends ServerBasePacket {

	private static final int STATUS_POISON = 0x01;
	/*
	 * private static final int STATUS_INVISIBLE = 2; private static final int
	 * STATUS_PC = 4; private static final int STATUS_FREEZE = 8; private static
	 * final int STATUS_BRAVE = 16; private static final int STATUS_ELFBRAVE =
	 * 32; private static final int STATUS_FASTMOVABLE = 64; private static
	 * final int STATUS_GHOST = 128;
	 */

	private byte[] _byte = null;

	/**
	 * 物件封包 - 跟隨者
	 * 
	 * @param follower
	 * @param pc
	 */
	public S_FollowerPack(final L1FollowerInstance follower, final L1PcInstance pc) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(follower.getX());
		writeH(follower.getY());
		writeD(follower.getId());
		writeH(follower.getGfxId());
		writeC(follower.getStatus());
		writeC(follower.getHeading());
		writeC(follower.getChaLightSize());
		writeC(follower.getMoveSpeed());
		writeD(0x00000000);
		writeH(0x0000);
		writeS(follower.getNameId());
		writeS(follower.getTitle());
		int status = 0;
		if (follower.getPoison() != null) { // 毒状態
			if (follower.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		writeC(status); // 狀態
		writeD(0x00000000);
		writeS(null);
		writeS(null);

		writeC(0x00); // 物件分類

		writeC(0xff); // HP
		writeC(0x00);
		writeC(0x00);// LV
		writeC(0x00);
		writeC(0xff);
		writeC(0xff);
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
