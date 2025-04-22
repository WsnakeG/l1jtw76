package com.lineage.server.serverpackets;

/**
 * 角色資訊
 * 
 * @author dexc
 */
public class S_CharPacks extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色資訊
	 * 
	 * @param name
	 * @param clanName
	 * @param type
	 * @param sex
	 * @param lawful
	 * @param hp
	 * @param mp
	 * @param ac
	 * @param lv
	 * @param str
	 * @param dex
	 * @param con
	 * @param wis
	 * @param cha
	 * @param intel
	 */
	public S_CharPacks(final String name, final String clanName, final int type, final int sex,
			final int lawful, final int hp, final int mp, final int ac, final int lv, final int str,
			final int dex, final int con, final int wis, final int cha, final int intel, final int time) {
		writeC(S_OPCODE_CHARLIST);
		writeS(name);
		writeS(clanName);
		writeC(type);
		writeC(sex);
		writeH(lawful);
		writeH(hp);
		writeH(mp);

		if (ac > 0x0a) {// 10
			writeC(0x0a);// 10

		} else {
			writeC(ac);
		}

		if (lv > 0x7f) {// 127
			writeC(0x7f);// 127

		} else {
			writeC(lv);
		}

		if (str > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(str);
		}
		if (dex > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(dex);
		}
		if (con > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(con);
		}
		if (wis > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(wis);
		}
		if (cha > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(cha);
		}
		if (intel > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(intel);
		}

		// 大於0為GM權限
		writeC(0x00);
		writeD(time);
		final int checkcode = Math.min(lv, 127) ^ str ^ dex ^ con ^ wis ^ cha ^ intel;
		writeC(checkcode & 0xFF);// 12070601 add
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
