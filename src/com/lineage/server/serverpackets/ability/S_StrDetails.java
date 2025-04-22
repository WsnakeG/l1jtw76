package com.lineage.server.serverpackets.ability;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 力量能力資訊
 * @author kyo
 *
 */
public class S_StrDetails extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public S_StrDetails() {
		
	}

	/**
	 * 力量能力資訊
	 * @param type
	 * @param dmg
	 * @param hit
	 * @param dmgCritical
	 * @param maxWeight
	 */
	public S_StrDetails(final int type, final int dmg, final int hit, final int dmgCritical, final int maxWeight) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E3);
		// 能力詳細資訊輸出類型
		// 2:能力資訊更新(創建角色點選初始化 角色進入遊戲初始化 升級點選決定能力初始化 角色點數重置初始化 等級提升1 等級提升10初始化) 
		// 4:能力資訊更新(設定能力屬性:角色點數重置 升級點能力)
		// 16:能力資訊更新(設定能力屬性:創建角色 角色點數重置)
		// 32:能力資訊更新(設定能力屬性:升級點選能力)
		this.writeInt32(1, type);// 能力詳細資訊輸出類型
		this.writeByteArray(2, buildSTR(dmg, hit, dmgCritical, maxWeight));
		this.randomShort();
	}
	
	/**
	 * 力量能力資訊
	 * @param dmg
	 * @param hit
	 * @param dmgCritical
	 * @param maxWeight
	 * @return
	 */
	private byte[] buildSTR(final int dmg, final int hit, final int dmgCritical, final int maxWeight) {
		S_StrDetails data = new S_StrDetails();
		data.writeInt32(1, dmg);// 近距離傷害
		data.writeInt32(2, hit);// 近距離命中
		data.writeInt32(3, dmgCritical);// 近距離暴擊
		data.writeInt32(4, maxWeight);// 負重能力
		return data.getContent();
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
