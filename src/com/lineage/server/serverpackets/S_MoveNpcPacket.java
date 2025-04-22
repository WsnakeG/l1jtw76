package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1MonsterInstance;

/**
 * 物件移動
 * 
 * @author dexc
 */
public class S_MoveNpcPacket extends ServerBasePacket {

	private byte[] _byte = null;

	/***
	 * 物件移動
	 * 
	 * @param npc
	 * @param x
	 * @param y
	 * @param heading
	 */
	public S_MoveNpcPacket(final L1MonsterInstance npc, final int x, final int y, final int heading) {
		// 0000: 3e d1 72 08 00 d3 83 e7 7e 02 80 9a 0f c3 0f b8
		// >.r.....~.......
		writeC(S_OPCODE_MOVEOBJECT);
		writeD(npc.getId());
		writeH(x);
		writeH(y);
		writeC(heading);

		writeC(0x80);
	}

	/***
	 * 物件移動
	 * 
	 * @param cha
	 */
	public S_MoveNpcPacket(final L1Character cha) {
		// 0000: 3e d1 72 08 00 d3 83 e7 7e 02 80 9a 0f c3 0f b8
		// >.r.....~.......
		writeC(S_OPCODE_MOVEOBJECT);
		writeD(cha.getId());
		writeH(cha.getX());
		writeH(cha.getY());
		writeC(cha.getHeading());

		writeC(0x80);
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
