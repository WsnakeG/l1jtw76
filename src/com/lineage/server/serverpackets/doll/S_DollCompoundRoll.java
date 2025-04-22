package com.lineage.server.serverpackets.doll;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 
 * @author kyo
 *
 */
public class S_DollCompoundRoll extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public S_DollCompoundRoll() {
		
	}
	
	public S_DollCompoundRoll(int i, int level) {
		this.writeByteArray(4, new S_DollCompoundRoll(level).getContent());
	}
	
	public S_DollCompoundRoll(int level) {
		this.writeInt32(1, level);
		for (int index = 1; index <= 4; index++) {
			this.writeByteArray(2, buildSlot(index, level));
		}
		final int[][] arr1 = { { 1, 2 }, { 1, 3 }, { 1, 4 }, { 2, 3 }, { 2, 4 }, { 3, 4 } };
		for (final int[] position : arr1) {
			this.writeByteArray(3, buildArray(level, position));
		}
		final int[][] arr2 = { { 1, 2, 3 }, { 1, 2, 4 }, { 1, 3, 4 }, { 2, 3, 4 } };
		for (final int[] position : arr2) {
			this.writeByteArray(3, buildArray(level, position));
		}
		S_DollCompoundRoll m = new S_DollCompoundRoll();
		m.writeInt32(1, 1);
		m.writeInt32(1, 2);
		m.writeInt32(1, 3);
		m.writeInt32(1, 4);
		m.writeInt32(2, level + 1);
		if (level <= 3) {
			m.writeByteArray(3, new byte[] { (byte) 0x10, (byte) (level + 2)});
		}
		this.writeByteArray(3, m.getContent());
	}
	
	private byte[] buildSlot(int index, int level) {
		S_DollCompoundRoll m = new S_DollCompoundRoll();
		m.writeInt32(1, index);
		m.writeInt32(2, 0);
		m.writeInt32(3, level);
		return m.getContent();
	}
	
	private byte[] buildArray(int level, final int[] position) {
		S_DollCompoundRoll m = new S_DollCompoundRoll();
		for (int i = 0; i < position.length; i++) {
			m.writeInt32(1, position[i]);
		}
		m.writeInt32(2, level + 1);
		return m.getContent();
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
