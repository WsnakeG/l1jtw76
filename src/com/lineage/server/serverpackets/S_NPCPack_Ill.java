package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1IllusoryInstance;

/**
 * 物件封包 - 分身
 * 
 * @author dexc
 */
public class S_NPCPack_Ill extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件封包 - 分身
	 * 
	 * @param de
	 */
	public S_NPCPack_Ill(final L1IllusoryInstance de) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(de.getX());
		writeH(de.getY());
		writeD(de.getId());

		writeH(de.getTempCharGfx());

		writeC(de.getStatus());

		writeC(de.getHeading());
		writeC(de.getChaLightSize());
		writeC(de.getMoveSpeed());
		writeD(0x00000000); // exp
		writeH(de.getLawful());

		writeS(de.getNameId());
		writeS(de.getTitle());

		writeC(0x00); // 狀態
		writeD(0x00000000);
		writeS(null); // クラン名
		writeS(null); // ペッホチング？

		writeC(0x00); // 物件分類

		writeC(0xff); // HP
		writeC(0x00); // タルクック距離(通り)
		writeC(0x00); // PC = 0, Mon = Lv
		writeC(0x00); // ？
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
