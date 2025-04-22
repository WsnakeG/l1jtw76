package com.lineage.server.serverpackets;

import java.util.ArrayList;

public class S_PacketBoxGame extends ServerBasePacket {

	private byte[] _byte = null;

	/** 比賽視窗(倒數開始) */
	public static final int GAMESTART = 64;

	/** 開始正向計時 */
	public static final int TIMESTART = 65;

	/** 顯示資訊 */
	public static final int GAMEINFO = 66;

	/** 比賽視窗(倒數結束/停止計時) */
	public static final int GAMEOVER = 69;

	/** 移除比賽視窗 */
	public static final int GAMECLEAR = 70;

	/** 開始反向計時 */
	public static final int STARTTIME = 71;

	/** 移除開始反向計時視窗 */
	public static final int STARTTIMECLEAR = 72;

	/**
	 * 開始正向計時<br>
	 * 移除比賽視窗<br>
	 * 移除開始反向計時視窗<br>
	 * 
	 * @param subCode
	 */
	public S_PacketBoxGame(final int subCode) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case TIMESTART:// 開始正向計時
		case GAMECLEAR:// 移除比賽視窗
		case STARTTIMECLEAR:// 移除開始反向計時視窗
			break;
		}
	}

	/**
	 * 倒數開始0~10<br>
	 * 倒數結束0~10<br>
	 * 開始反向計時0~3600<br>
	 * 
	 * @param subCode
	 * @param value
	 */
	public S_PacketBoxGame(final int subCode, final int value) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case GAMESTART:// 倒數開始
		case GAMEOVER:// 倒數結束
			writeC(value); // 倒數時間 0~10
			break;

		case STARTTIME:// 開始反向計時
			writeH(value); // 0~3600
			break;
		}
	}

	/**
	 * 顯示資訊
	 * 
	 * @param list
	 */
	public S_PacketBoxGame(final ArrayList<StringBuilder> list) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(GAMEINFO);
		writeC(list.size());// 顯示筆數
		writeC(0x00);//
		writeC(0x00);//
		writeC(0x00);//

		if (list != null) {// 內容與標題
			for (final StringBuilder string : list) {
				writeS(string.toString());
			}
		}
	}

	/**
	 * 顯示資訊
	 * 
	 * @param type
	 * @param title
	 * @param list
	 */
	public S_PacketBoxGame(final StringBuilder title, final ArrayList<StringBuilder> list) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(GAMEINFO);
		writeC(list.size() + 1);// 顯示筆數
		writeC(0x00);//
		writeC(0x00);//
		writeC(0x00);//

		writeS(title.toString());// 標題
		if (list != null) {
			for (final StringBuilder c : list) {
				writeS(c.toString());
			}
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
