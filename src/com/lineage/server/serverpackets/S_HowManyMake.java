package com.lineage.server.serverpackets;

/**
 * 選取物品數量 (NPC道具交換數量)
 * 
 * @author dexc
 */
public class S_HowManyMake extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選取物品數量 (NPC道具交換-附加HTML)
	 * 
	 * @param objId
	 * @param max
	 * @param htmlId
	 */
	public S_HowManyMake(final int objId, final int max, final String htmlId) {
		writeC(S_OPCODE_INPUTAMOUNT);
		writeD(objId);
		writeD(0x00000000);// ?
		writeD(0x00000000);// 數量初始質
		writeD(0x00000000);// 最低可換數量
		writeD(max);// 最高可換數量
		writeH(0x0000);// ?
		writeS("request");// HTML
		writeS(htmlId);// 命令
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
