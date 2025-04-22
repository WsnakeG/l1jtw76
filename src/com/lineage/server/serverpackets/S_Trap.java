package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1TrapInstance;

/**
 * 物件封包 - 陷阱(GM探查用)
 * 
 * @author DaiEn
 */
public class S_Trap extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件封包 - 陷阱(GM探查用)
	 * 
	 * @param trap
	 * @param name
	 */
	public S_Trap(final L1TrapInstance trap, final String name) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(trap.getX()); // X
		writeH(trap.getY()); // Y
		writeD(trap.getId()); // OBJID
		writeH(0x0007); // GFXID
		writeC(0x00); // 物件外觀屬性
		writeC(0x00); // 方向
		writeC(0x00); // 亮度 0:normal, 1:fast, 2:slow
		writeC(0x00); // 速度
		writeD(0x00000000); // 數量, 經驗值
		writeH(0x0000); // 正義質
		writeS(name); // 名稱
		writeS(null); // 封號
		writeC(0x00); // 狀態
		writeD(0x00000000); // 血盟OBJID
		writeS(null); // 血盟名稱
		writeS(null); // 主人名稱
		writeC(0x00); // 物件分類
		writeC(0xFF); // HP顯示
		writeC(0x00); // タルクック距離(通り)
		writeC(0x00); // LV
		writeC(0x00);
		writeC(0xFF);
		writeC(0xFF);

		/*
		 * this.writeC(S_OPCODE_CHARPACK); this.writeH(trap.getX());
		 * this.writeH(trap.getY()); this.writeD(trap.getId());
		 * this.writeH(0x07); // adena this.writeC(0x00); this.writeC(0x00);
		 * this.writeC(0x00); this.writeC(0x00); this.writeD(0x00);
		 * this.writeC(0x00); this.writeC(0x00); this.writeS(name);
		 * this.writeC(0x00); this.writeD(0x00); this.writeD(0x00);
		 * this.writeC(255); this.writeC(0x00); this.writeC(0x00);
		 * this.writeC(0x00); this.writeH(65535); // writeD(0x401799a);
		 * this.writeD(0); this.writeC(8); this.writeC(0);
		 */
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
