package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1DoorInstance;

/**
 * 物件屬性(門)
 * 
 * @author dexc
 */
public class S_Door extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件屬性(門)
	 * 
	 * @param door
	 */
	public S_Door(final L1DoorInstance door) {
		buildPacket(door.getEntranceX(), door.getEntranceY(), door.getDirection(), door.getPassable());
	}

	/**
	 * 物件屬性(門)
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 * @param passable
	 */
	public S_Door(final int x, final int y, final int direction, final int passable) {
		buildPacket(x, y, direction, passable);
	}

	private void buildPacket(final int x, final int y, final int direction, final int passable) {
		writeC(S_OPCODE_ATTRIBUTE);
		writeH(x);
		writeH(y);
		writeC(direction); // ドアの方向 0: ／ 1: ＼
		writeC(passable);
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
