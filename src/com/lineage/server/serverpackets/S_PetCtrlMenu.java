package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * 寵物控制介面
 * 
 * @author daien
 */
public class S_PetCtrlMenu extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 寵物控制介面
	 * 
	 * @param cha 主人
	 * @param npc 寵物
	 * @param open 是否開起控制選單
	 */
	public S_PetCtrlMenu(final L1Character cha, final L1NpcInstance npc, final boolean open) {
		writeC(S_OPCODE_CHARRESET);
		writeC(0x0c);
		if (open) {
			writeH(0x03);
			writeD(0x00000000);
			writeD(npc.getId());
			writeD(0x00000000);
			writeH(npc.getX());
			writeH(npc.getY());
			writeS(npc.getName());

		} else {
			writeH(0x0000);
			writeD(0x00000001);
			writeD(npc.getId());
			writeS(null);
		}
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
		return getClass().getSimpleName();
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
