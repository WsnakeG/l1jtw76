package com.lineage.server.serverpackets.ability;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 體質能力資訊
 * @author kyo
 *
 */
public class S_ConDetails extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public S_ConDetails() {
		
	}

	/**
	 * 體質能力資訊
	 * @param type
	 * @param hpr
	 * @param potionHpr
	 * @param maxWeight
	 * @param hpUp
	 */
	public S_ConDetails(final int type, final int hpr, final int potionHpr, final int maxWeight, final int hpUp) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E3);
		// 能力詳細資訊輸出類型
		// 2:能力資訊更新(創建角色點選初始化 角色進入遊戲初始化 升級點選決定能力初始化 角色點數重置初始化 等級提升1 等級提升10初始化) 
		// 4:能力資訊更新(設定能力屬性:角色點數重置 升級點能力)
		// 16:能力資訊更新(設定能力屬性:創建角色 角色點數重置)
		// 32:能力資訊更新(設定能力屬性:升級點選能力)
		this.writeInt32(1, type);// 能力詳細資訊輸出類型
		
		S_ConDetails data = new S_ConDetails();
		data.writeInt32(1, hpr);// HP回復
		data.writeInt32(2, potionHpr);// HP藥水恢復增加
		data.writeInt32(3, maxWeight);// 最大持有重量
		data.writeInt32(4, hpUp);// 升等HP增加
		
		this.writeByteArray(6, data.getContent());
		this.randomShort();
	}
	
	/**
	 * 體質能力資訊
	 * @param type
	 * @param hpr
	 * @param potionHpr
	 * @param maxWeight
	 * @param hpUp
	 * @param addHp
	 */
	public S_ConDetails(final int type, final int hpr, final int potionHpr, final int maxWeight, final int hpUp, final int addHp) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E3);
		// 能力詳細資訊輸出類型
		// 2:能力資訊更新(創建角色點選初始化 角色進入遊戲初始化 升級點選決定能力初始化 角色點數重置初始化 等級提升1 等級提升10初始化) 
		// 4:能力資訊更新(設定能力屬性:角色點數重置 升級點能力)
		// 16:能力資訊更新(設定能力屬性:創建角色 角色點數重置)
		// 32:能力資訊更新(設定能力屬性:升級點選能力)
		this.writeInt32(1, type);// 能力詳細資訊輸出類型
		
		S_ConDetails data = new S_ConDetails();
		data.writeInt32(1, hpr);// HP回復
		data.writeInt32(2, potionHpr);// HP藥水恢復增加
		data.writeInt32(3, maxWeight);// 最大持有重量
		data.writeInt32(4, hpUp);// 升等HP增加
		data.writeInt32(5, addHp);// HP上限
		
		this.writeByteArray(6, data.getContent());
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
