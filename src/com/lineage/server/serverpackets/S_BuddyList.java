package com.lineage.server.serverpackets;

import com.lineage.server.world.World;


public class S_BuddyList extends ServerBasePacket {
	
	private byte[] _byte = null;

	public S_BuddyList(String... name) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x0151);
		this.writeInt32(1, 1);
		for (String str : name) {
			if (str.trim().length() == 0) {
				continue;
			}
			this.writeByteArray(2, new S_BuddyList(str).getContent());
		}
		this.randomShort();
	}

	public S_BuddyList(String name) {
		this.writeString(1, name);
		this.writeInt32(2, World.get().getPlayer(name) == null ? 0 : 1);
		this.writeString(3, "");
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
