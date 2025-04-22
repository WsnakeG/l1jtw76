package com.lineage.server.serverpackets;

/**
 * 選取物品數量 (賣出小屋)
 * 
 * @author dexc
 */
public class S_SellHouse extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選取物品數量 (賣出小屋)
	 * 
	 * @param objectId
	 * @param houseNumber
	 */
	public S_SellHouse(final int objectId, final String houseNumber) {
		buildPacket(objectId, houseNumber);
	}

	private void buildPacket(final int objectId, final String houseNumber) {
		writeC(S_OPCODE_INPUTAMOUNT);
		writeD(objectId);
		writeD(0); // ?
		writeD(100000);// 數量初始質
		writeD(100000);// 最低可換數量
		writeD(2000000000);// 最高可換數量
		writeH(0); // ?
		writeS("agsell");// HTML
		writeS("agsell " + houseNumber);// 命令
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
