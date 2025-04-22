package com.lineage.server.serverpackets;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1DoorInstance;

/**
 * 物件封包 - 門
 * 
 * @author dexc
 */
public class S_DoorPack extends ServerBasePacket {

	private byte[] _byte = null;

	private static final int STATUS_POISON = 1;

	/**
	 * 物件封包 - 門
	 * 
	 * @param door
	 */
	public S_DoorPack(final L1DoorInstance door) {
		buildPacket(door);
	}

	private void buildPacket(final L1DoorInstance door) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(door.getX());
		writeH(door.getY());
		writeD(door.getId());
		writeH(door.getGfxId());
		final int doorStatus = door.getStatus();
		final int openStatus = door.getOpenStatus();

		if (door.isDead()) {
			writeC(doorStatus);
		} else if (openStatus == ActionCodes.ACTION_Open) {
			writeC(openStatus);
		} else if ((door.getMaxHp() > 1) && (doorStatus != 0)) {
			writeC(doorStatus);
		} else {
			writeC(openStatus);
		}

		writeC(0);
		writeC(0);
		writeC(0);
		writeD(1);
		writeH(0);
		writeS(null);
		writeS(null);

		int status = 0;
		if (door.getPoison() != null) {
			if (door.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}

		writeC(status);
		writeD(0);
		writeS(null);
		writeS(null);
		writeC(0);
		writeC(0xFF);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0xFF);
		writeC(0xFF);
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
