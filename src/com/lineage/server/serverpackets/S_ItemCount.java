package com.lineage.server.serverpackets;

/**
 * 選取物品數量 (NPC道具交換)
 * 
 * @author dexc
 */
public class S_ItemCount extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選取物品數量 (NPC道具交換)
	 * 
	 * @param objId NPC OBJID
	 * @param max 可換數量上限
	 * @param cmd 命令
	 */
	public S_ItemCount(final int objId, final int max, final String cmd) {
		writeC(S_OPCODE_INPUTAMOUNT);
		writeD(objId);
		writeD(0x00000000);// ?
		writeD(0x00000000);// 數量初始質
		writeD(0x00000000);// 最低可換數量
		writeD(max);// 最高可換數量
		writeH(0x0000);// ?
		writeS("request");// HTML
		writeS(cmd);// 命令
	}

	/**
	 * 選取物品數量 (NPC道具交換)
	 * 
	 * @param objId NPC OBJID
	 * @param max 可換數量上限
	 * @param html 頁面
	 * @param cmd 命令
	 */
	public S_ItemCount(final int objId, final int max, final String html, final String cmd) {
		writeC(S_OPCODE_INPUTAMOUNT);
		writeD(objId);
		writeD(0x00000000);// ?
		writeD(0x00000000);// 數量初始質
		writeD(0x00000000);// 最低可換數量
		writeD(max);// 最高可換數量
		writeH(0x0000);// ?
		writeS(html);// HTML
		writeS(cmd);// 命令
	}

	public S_ItemCount(final int objId, final int min, final int max, final String html, final String cmd,
			final String[] data) {
		writeC(S_OPCODE_INPUTAMOUNT);
		writeD(objId);
		writeD(0x00000000);// ?
		writeD(min);// 數量初始質
		writeD(min);// 最低可換數量
		writeD(max);// 最高可換數量
		writeH(0x0000);// c
		writeS(html);// HTML
		writeS(cmd);// 命令
		if ((data != null) && (1 <= data.length)) {
			writeH(data.length); // 數量
			for (final String datum : data) {
				writeS(datum);
			}
		}
	}

	/**
	 * 選取物品數量 (銀行管理員)
	 * 
	 * @param objId NPC OBJID
	 * @param max 可換數量最小質
	 * @param max 可換數量最大質
	 * @param html 頁面
	 * @param cmd 命令
	 */
	public S_ItemCount(final int objId, final int min, final int max, final String html, final String cmd) {
		writeC(S_OPCODE_INPUTAMOUNT);
		writeD(objId);
		writeD(0x00000000);// ?
		writeD(min);// 數量初始質
		writeD(min);// 最低可換數量
		writeD(max);// 最高可換數量
		writeH(0x0000);// ?
		writeS(html);// HTML
		writeS(cmd);// 命令
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
