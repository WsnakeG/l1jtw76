package com.lineage.server.serverpackets;

/**
 * 魔法效果:詛咒
 * 
 * @author dexc
 */
public class S_Paralysis extends ServerBasePacket {

	private byte[] _byte = null;

	/** 你的身體完全麻痺了 */
	public static final int TYPE_PARALYSIS = 0x01;// 你的身體完全麻痺了

	/** 你的身體完全麻痺了 */
	public static final int TYPE_PARALYSIS2 = 0x02;// 你的身體完全麻痺了

	/** 睡眠狀態 */
	public static final int TYPE_SLEEP = 0x03;// 睡眠狀態

	/** 凍結狀態 */
	public static final int TYPE_FREEZE = 0x04;// 凍結狀態

	/** 衝擊之暈 */
	public static final int TYPE_STUN = 0x05;// 衝擊之暈

	/** 雙腳被困 */
	public static final int TYPE_BIND = 0x06;// 雙腳被困

	/** 解除傳送鎖定 */
	public static final int TYPE_TELEPORT_UNLOCK = 0x07;// 解除傳送鎖定

	/** 拘束移動 */
	public static final int TYPE_POWERGRIP = 26;

	/** 亡命之徒 */
	public static final int TYPE_DESPERADO = 30;

	/**
	 * 魔法效果:詛咒
	 * 
	 * @param type
	 * @param flag
	 */
	public S_Paralysis(final int type, final boolean flag) {
		writeC(S_OPCODE_PARALYSIS);
		switch (type) {
		case TYPE_PARALYSIS: // 你的身體完全麻痺了
			if (flag == true) {
				writeC(0x02);
			} else {
				writeC(0x03);
			}
			break;

		case TYPE_PARALYSIS2: // 你的身體完全麻痺了
			if (flag == true) {
				writeC(0x04);
			} else {
				writeC(0x05);
			}
			break;

		case TYPE_TELEPORT_UNLOCK: // 傳送鎖定解除
			if (flag == true) {
				writeC(0x06);
			} else {
				writeC(0x07);
			}
			break;

		case TYPE_SLEEP: // 睡眠狀態
			if (flag == true) {
				writeC(0x0a);// this.writeC(10);
			} else {
				writeC(0x0b);// this.writeC(11);
			}
			break;

		case TYPE_FREEZE: // 凍結狀態
			if (flag == true) {
				writeC(0x0c);// this.writeC(12);
			} else {
				writeC(0x0d);// this.writeC(13);
			}
			break;

		case TYPE_STUN: // 衝擊之暈
			if (flag == true) {
				writeC(0x16);// this.writeC(22);
			} else {
				writeC(0x17);// this.writeC(23);
			}
			break;

		case TYPE_BIND: // 雙腳被困
			if (flag == true) {
				writeC(0x18);// this.writeC(24);
			} else {
				writeC(0x19);// this.writeC(25);
			}
			break;

		case TYPE_POWERGRIP:
			if (flag) {
				writeC(26);
			} else {
				writeC(27);
			}
			break;

		case TYPE_DESPERADO:
			if (flag) {
				writeC(30);
			} else {
				writeC(31);
			}
			break;
		}
	}

	public S_Paralysis(final int type, final boolean flag, final int time) {
		writeC(S_OPCODE_PARALYSIS);
		switch (type) {
		case TYPE_PARALYSIS: // 你的身體完全麻痺了
			if (flag == true) {
				this.writeC(0x02);
				this.writeH(0x0000);
				this.writeH(time);
			} else {
				this.writeC(0x03);
				this.writeH(0x0000);
				this.writeH(0x0000);
			}
			break;

		case TYPE_PARALYSIS2: // 你的身體完全麻痺了
			if (flag == true) {
				this.writeC(0x04);
				writeH(0x0000);
				writeH(time);
			} else {
				this.writeC(0x05);
				this.writeH(0x0000);
				this.writeH(0x0000);
			}
			break;
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
