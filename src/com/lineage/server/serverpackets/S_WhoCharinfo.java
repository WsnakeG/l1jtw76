package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 伺服器訊息(行數/行數,附加字串)
 * 
 * @author dexc
 */
public class S_WhoCharinfo extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 伺服器訊息(行數/行數,附加字串)
	 * 
	 * @param cha
	 */
	public S_WhoCharinfo(final L1Character cha) {
		String lawfulness = "";
		final int lawful = cha.getLawful();
		if (lawful < 0) {
			lawfulness = "($1503)";// 邪惡者

		} else if ((lawful >= 0) && (lawful < 500)) {
			lawfulness = "($1502)";// 中立者

		} else if (lawful >= 500) {
			lawfulness = "($1501)";// 正義者
		}

		writeC(S_OPCODE_SERVERMSG);
		writeH(0x00a6);// 166
		writeC(0x01);

		// this.writeC(S_OPCODE_GLOBALCHAT);
		// this.writeC(0x08);

		String title = "";
		String clan = "";

		if (cha.getTitle().equalsIgnoreCase("") == false) {
			title = cha.getTitle() + " ";
		}

		String name = "";

		if (cha instanceof L1DeInstance) {
			final L1DeInstance de = (L1DeInstance) cha;
			name = de.getNameId();
			if (de.getClanid() > 0) {
				clan = "[" + de.getClanname() + "]";
			}

		} else if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			name = pc.getName();
			if (pc.getClanid() > 0) {
				clan = "[" + pc.getClanname() + "]";
			}
		}

		writeS(title + name + " " + lawfulness + " " + clan);
		// this.writeS(title + name + " " + lawfulness + " " + clan);
		// writeD(0x80157FE4);
		// this.writeD(0);
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
