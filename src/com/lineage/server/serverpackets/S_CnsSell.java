package com.lineage.server.serverpackets;

import com.lineage.data.event.ShopXSet;

/**
 * 選取物品數量 (賣出價格定義)
 * 
 * @author dexc
 */
public class S_CnsSell extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選取物品數量 (賣出價格定義)
	 * 
	 * @param objectId NPC OBJID
	 * @param htmlid HTML名稱
	 * @param command 命令
	 */
	public S_CnsSell(final int objectId, final String htmlid, final String command) {
		buildPacket(objectId, htmlid, command);
	}

	private void buildPacket(final int objectId, final String htmlid, final String command) {
		writeC(S_OPCODE_INPUTAMOUNT);
		writeD(objectId);
		writeD(0x00000000);// ?
		writeD(ShopXSet.MIN);// 數量初始質
		writeD(ShopXSet.MIN);// 最低可換數量
		writeD(ShopXSet.MAX);// 最高可換數量
		writeH(0x0000);// ?
		writeS(htmlid);// HTML
		writeS(command);// 命令
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
