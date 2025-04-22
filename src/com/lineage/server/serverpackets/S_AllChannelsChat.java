package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 全頻聊天字串(全頻廣播器)
 * @author user
 *
 */
public final class S_AllChannelsChat extends ServerBasePacket {

	private byte[] _byte = null;
	/*
	全頻廣播器 使用流程
	[Client] opcode:12 info:C_OPCODE_USE_ITEM length:36/40 time:20:21:40.760 [cdsh]
	0000: 0c 10 be e5 00 a6 ac a6 ac a6 ac bb ee c5 e9 c2    ................
	0010: e0 b4 ab 7e b7 4e b5 db b1 4b a7 da 21 21 00 35    ...~.N...K..!!.5
	0020: 00 3a 95 1d                                        .:..

	[Server] opcode:211 info:S_OPCODE_EVENT len:3 time:20:21:40.769
	0000: d3 0a 5e                                           ..^

	[Server] opcode:205 info:S_OPCODE_DELETEINVENTORYITEM len:5 time:20:21:40.775
	0000: cd 10 be e5 00                                     .....

	[Server] opcode:33 info:S_OPCODE_MESSAGE len:37 time:20:21:40.786
	0000: 21 12 5b a9 6a a4 d2 5d 20 a6 ac a6 ac a6 ac bb    !.[.j..] .......
	0010: ee c5 e9 c2 e0 b4 ab 7e b7 4e b5 db b1 4b a7 da    .......~.N...K..
	0020: 21 21 00 35 00                                     !!.5.
	*/
	
	/**
	 * 全頻聊天字串(全頻廣播器)
	 * @param pc
	 * @param chat
	 * @param color
	 */
	public S_AllChannelsChat(final L1PcInstance pc, final String chat, final int color) {
		writeC(S_OPCODE_GLOBALCHAT);
		this.writeC(18);// type 0x12
		this.writeS("[" + pc.getName() + "] " + chat);// out text
		this.writeH(color);// color
	}
	
	/**
	 * 全頻聊天字串(全頻廣播器)
	 * @param chat
	 * @param color
	 */
	public S_AllChannelsChat(final String chat, final int color) {
		writeC(S_OPCODE_GLOBALCHAT);
		this.writeC(18);// type 0x12
		this.writeS(chat);// out text
		this.writeH(color);// color
	}
	
	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName();
	}

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
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