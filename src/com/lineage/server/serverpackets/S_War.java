package com.lineage.server.serverpackets;

/**
 * 血盟戰爭訊息(編號,血盟名稱,目標血盟名稱)
 * 
 * @author dexc
 */
public class S_War extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 血盟戰爭訊息(編號,血盟名稱,目標血盟名稱)
	 * 
	 * @param type 編號 <BR>
	 *            1 : 226：%0 血盟向 %1血盟宣戰。 <BR>
	 *            2 : 228：%0 血盟向 %1 血盟投降了。 <BR>
	 *            3 : 227：%0 血盟與 %1血盟之間的戰爭結束了。 <BR>
	 *            4 : 231：%0 血盟贏了對 %1 血盟的戰爭。 <BR>
	 *            6 : 224：%0 血盟與 %1血盟成為同盟關係。 <BR>
	 *            7 : 225：毀掉%0 血盟與 %1血盟之間的同盟。 <BR>
	 *            8 : 235：\f1目前你的血盟與 %0 血盟交戰當中。 <BR>
	 * @param clan_name1 血盟名稱
	 * @param clan_name2 目標血盟名稱
	 */
	public S_War(final int type, final String clan_name1, final String clan_name2) {
		buildPacket(type, clan_name1, clan_name2);
	}

	private void buildPacket(final int type, final String clan_name1, final String clan_name2) {
		// 1 : 226：%0 血盟向 %1血盟宣戰。
		// 2 : %0 血盟向 %1 血盟投降了。
		// 3 : %0 血盟與 %1血盟之間的戰爭結束了。
		// 4 : 231：%0 血盟贏了對 %1 血盟的戰爭。
		// 6 : 224：%0 血盟與 %1血盟成為同盟關係。
		// 7 : 毀掉%0 血盟與 %1血盟之間的同盟。
		// 8 : \f1目前你的血盟與 %0 血盟交戰當中。

		writeC(S_OPCODE_WAR);
		writeC(type);
		writeS(clan_name1);
		writeS(clan_name2);
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
