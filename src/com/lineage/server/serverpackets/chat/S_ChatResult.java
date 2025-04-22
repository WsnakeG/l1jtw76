package com.lineage.server.serverpackets.chat;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 使用聊天頻道的結果
 * @author kyo
 *
 */
public class S_ChatResult extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * 使用聊天頻道的結果
	 * @param chat_index
	 * @param chat_type
	 * @param chat_text
	 * @param tell_target_name
	 * @param server_id
	 * @param result_type
	 */
	public S_ChatResult(final int chat_index, final int chat_type, final String chat_text, final String tell_target_name, final int server_id, final int result_type) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x0203);
		this.writeInt32(1, chat_index);
		this.writeInt32(2, chat_type);
		this.writeString(3, chat_text);
		this.writeString(4, tell_target_name);
		this.writeInt32(5, server_id);
		this.writeInt32(6, result_type);
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
