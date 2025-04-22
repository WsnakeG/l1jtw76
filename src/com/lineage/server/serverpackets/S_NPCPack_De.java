package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1DeInstance;

/**
 * 物件封包 - 虛擬人物
 * 
 * @author dexc
 */
public class S_NPCPack_De extends ServerBasePacket {

	private static final int STATUS_POISON = 1;
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	private static final int STATUS_BRAVE = 16;

	private byte[] _byte = null;

	/**
	 * 物件封包 - 虛擬人物
	 * 
	 * @param de
	 */
	public S_NPCPack_De(final L1DeInstance de) {
		int status = STATUS_PC;

		if (de.getPoison() != null) { // 毒状態
			if (de.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		if (de.isInvisble()) {
			status |= STATUS_INVISIBLE;
		}
		if (de.getBraveSpeed() == 1) {// 具有勇水狀態
			status |= STATUS_BRAVE;
		}

		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(de.getX());
		writeH(de.getY());
		writeD(de.getId());

		writeH(de.getTempCharGfx());

		writeC(de.getStatus());

		writeC(de.getHeading());
		// writeC(0); // makes char invis (0x01), cannot move. spells display
		writeC(de.getChaLightSize());
		writeC(de.getMoveSpeed());
		writeD(0x00000000); // exp
		writeH(de.getLawful());

		writeS(de.getNameId());
		writeS(de.getTitle());
		writeC(status); // 狀態
		writeD(de.getClanid());
		writeS(de.getClanname()); // クラン名
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
