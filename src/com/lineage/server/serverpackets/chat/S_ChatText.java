package com.lineage.server.serverpackets.chat;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 聊天頻道字串(7.6ADD)
 * @author kyo
 *
 */
public class S_ChatText extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * 聊天頻道字串
	 * @param chat_time
	 * @param chat_type
	 * @param chat_text
	 * @param chat_name
	 * @param server_id
	 * @param chat_objid
	 * @param chat_locX
	 * @param chat_locY
	 */
	public S_ChatText(final int chat_time, final int chat_type, final String chat_text, final String chat_name, final int server_id, int chat_objid, int chat_locX, int chat_locY) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x0204);
		this.writeInt32(1, chat_time);
		this.writeInt32(2, chat_type);
		this.writeString(3, chat_text);
		this.writeString(5, chat_name);
		this.writeInt32(6, server_id);
		this.writeInt32(7, chat_objid);
		this.writeInt32(8, chat_locX);
		this.writeInt32(9, chat_locY);
		this.randomShort();
	}
	
	/**
	 * 聊天頻道字串
	 * @param chat_time
	 * @param chat_type
	 * @param chat_text
	 * @param chat_name
	 * @param server_id
	 */
	public S_ChatText(final int chat_time, final int chat_type, final String chat_text, final String chat_name, final int server_id) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x0204);
		this.writeInt32(1, chat_time);
		this.writeInt32(2, chat_type);
		this.writeString(3, chat_text);
		this.writeString(5, chat_name);
		this.writeInt32(6, server_id);
		this.randomShort();
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName();
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
