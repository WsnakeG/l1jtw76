package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1BoxInstance;

/**
 * @author terry0412
 */
public class S_BoxPack extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * @param egg
	 */
	public S_BoxPack(final L1BoxInstance box) {
		buildPacket(box);
	}

	private void buildPacket(final L1BoxInstance box) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(box.getX());
		writeH(box.getY());
		writeD(box.getId());
		writeH(box.getGfxId());
		writeC(box.getOpenStatus()); // 寶箱開啟狀態
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeD(0x00000001);
		writeH(0x0000);
		writeS(null);
		writeS(null);
		writeC(0x00); // 狀態
		writeD(0x00000000);
		writeS(null);
		writeS(null);

		writeC(0x00); // 物件分類

		writeC(0xff); // HP
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0xff);
		writeC(0xff);
		writeC(0x00); // added by terry0412
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
