package com.lineage.server.serverpackets;

import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.templates.L1EmblemIcon;

/**
 * 角色盟徽
 * 
 * @author dexc
 */
public class S_Emblem extends ServerBasePacket {

	private byte[] _byte = null;

	public S_Emblem(final int clanid) {
		final L1EmblemIcon emblemIcon = ClanEmblemReading.get().get(clanid);
		if (emblemIcon != null) {
			writeC(S_OPCODE_EMBLEM);
			writeD(clanid);
			final byte[] icon = emblemIcon.get_clanIcon();
			for (int i = 0; i < icon.length; i++) {
				writeP(icon[i]);
			}
		}
	}

	public S_Emblem(final L1EmblemIcon emblemIcon) {
		// System.out.println("S_OPCODE_EMBLEM");
		writeC(S_OPCODE_EMBLEM);
		writeD(emblemIcon.get_clanid());
		writeByte(emblemIcon.get_clanIcon());
		/*
		 * final byte[] icon = emblemIcon.get_clanIcon(); for (int i = 0 ; i <
		 * icon.length ; i++) { this.writeC(icon[i]); }
		 */
	}

	public S_Emblem(final int clanid, final byte[] clanIcon) {
		writeC(S_OPCODE_EMBLEM);
		writeD(clanid);
		writeByte(clanIcon);
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
