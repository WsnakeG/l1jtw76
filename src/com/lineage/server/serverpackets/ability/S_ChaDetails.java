package com.lineage.server.serverpackets.ability;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 魅力能力資訊
 * @author kyo
 *
 */
public class S_ChaDetails extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public S_ChaDetails() {
		
	}

	/**
	 * 魅力能力資訊
	 * @param type
	 */
	public S_ChaDetails(final int type) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E3);
		// 能力詳細資訊輸出類型
		// 2:能力資訊更新(創建角色點選初始化 角色進入遊戲初始化 升級點選決定能力初始化 角色點數重置初始化 等級提升1 等級提升10初始化) 
		// 4:能力資訊更新(設定能力屬性:角色點數重置 升級點能力)
		// 16:能力資訊更新(設定能力屬性:創建角色 角色點數重置)
		// 32:能力資訊更新(設定能力屬性:升級點選能力)
		this.writeInt32(1, type);// 能力詳細資訊輸出類型
		
		S_ChaDetails data = new S_ChaDetails();
		data.writeInt32(1, 1);// 魅力未知描述的能力增加
		
		this.writeByteArray(7, data.getContent());
		
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
