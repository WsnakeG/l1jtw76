package com.lineage.server.serverpackets;

import java.util.HashMap;

import com.lineage.server.world.WorldClan;

/**
 * 戰爭訊息
 * 
 * @author daien
 */
public class S_PacketBoxWar extends ServerBasePacket {

	/**
	 * writeByte(id) writeShort(?): <font color=#00800>(639) %s的攻城戰開始。 </font>
	 * <BR>
	 * 1:Kent 2:Orc 3:WW 4:Giran 5:Heine 6:Dwarf 7:Aden 8:Diad 9:城名9 ...
	 */
	public static final int MSG_WAR_BEGIN = 0;

	/**
	 * writeByte(id) writeShort(?): <font color=#00800>(640) %s的攻城戰結束。 </font>
	 */
	public static final int MSG_WAR_END = 1;

	/**
	 * writeByte(id) writeShort(?): <font color=#00800>(641)
	 * %s的攻城戰正在進行中。 </font>
	 */
	public static final int MSG_WAR_GOING = 2;

	/**
	 * <font color=#00800>(642) 已掌握了城堡的主導權。 </font>
	 */
	public static final int MSG_WAR_INITIATIVE = 3;

	/**
	 * <font color=#00800>(643) 已佔領城堡。</font>
	 */
	public static final int MSG_WAR_OCCUPY = 4;

	/**
	 * writeInt(?) writeString(name) writeString(clanname):<br>
	 * <font color=#00800>(782) %s 血盟的 %s打敗了反王<br>
	 * (783) %s 血盟成為新主人。 </font>
	 */
	public static final int MSG_WIN_LASTAVARD = 30;

	/** 攻城戰結束訊息 */
	public static final int MSG_WAR_OVER = 79;

	private byte[] _byte = null;

	/**
	 * 戰爭訊息<BR>
	 * <font color=#00800> 攻城戰結束訊息<BR>
	 * </font>
	 * 
	 * @param value 被佔領城堡數量 1: 肯特城 2: 妖魔城 3: 風木城 4: 奇巖城 5: 海音城 6: 侏儒城 7: 亞丁城 8:
	 *            狄亞得要塞
	 * @param clanName 血盟名稱
	 */
	public S_PacketBoxWar() {
		final HashMap<Integer, String> map = WorldClan.get().castleClanMap();
		final int count = 7;// 要顯示的城堡數量
		writeC(S_OPCODE_PACKETBOX);
		writeC(MSG_WAR_OVER);
		writeC(count);
		for (int key = 1; key < count; key++) {
			final String clanName = map.get(key);
			if (clanName != null) {
				writeS(clanName);

			} else {
				writeS(" ");
			}
		}
		map.clear();
	}

	/**
	 * 戰爭訊息<BR>
	 * <font color=#00800> 已掌握了城堡的主導權。<BR>
	 * 已佔領城堡。<BR>
	 * </font>
	 * 
	 * @param subCode 訊息編號
	 */
	public S_PacketBoxWar(final int subCode) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);
	}

	/**
	 * 戰爭訊息<BR>
	 * <font color=#00800> %s的攻城戰開始。<BR>
	 * %s的攻城戰結束。<BR>
	 * %s的攻城戰正在進行中。<BR>
	 * </font>
	 * 
	 * @param subCode 訊息編號
	 * @param value 城堡編號
	 */
	public S_PacketBoxWar(final int subCode, final int value) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		writeC(value); // castle id
		writeH(0x0000); // ?
	}

	/**
	 * 戰爭訊息<BR>
	 * <font color=#00800> %s 血盟的 %s打敗了反王<BR>
	 * %s 血盟成為新主人。<BR>
	 * </font>
	 * 
	 * @param subCode 訊息編號
	 * @param id
	 * @param name 盟主名稱
	 * @param clanName 血盟名稱
	 */
	public S_PacketBoxWar(final int subCode, final int id, final String name, final String clanName) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(MSG_WIN_LASTAVARD);

		writeD(id); // クランIDか何か？
		writeS(name);
		writeS(clanName);
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
