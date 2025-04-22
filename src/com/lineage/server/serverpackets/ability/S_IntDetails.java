package com.lineage.server.serverpackets.ability;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 智力能力資訊
 * @author kyo
 *
 */
public class S_IntDetails extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public S_IntDetails() {
		
	}

	/**
	 * 智力能力資訊
	 * @param type
	 * @param magicDmg
	 * @param magicHit
	 * @param magicCritical
	 * @param magicBonus
	 * @param magicConsumeReduction
	 */
	public S_IntDetails(final int type, final int magicDmg, final int magicHit, final int magicCritical, final int magicBonus, final int magicConsumeReduction) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E3);
		// 能力詳細資訊輸出類型
		// 2:能力資訊更新(創建角色點選初始化 角色進入遊戲初始化 升級點選決定能力初始化 角色點數重置初始化 等級提升1 等級提升10初始化) 
		// 4:能力資訊更新(設定能力屬性:角色點數重置 升級點能力)
		// 16:能力資訊更新(設定能力屬性:創建角色 角色點數重置)
		// 32:能力資訊更新(設定能力屬性:升級點選能力)
		this.writeInt32(1, type);// 能力詳細資訊輸出類型
		this.writeByteArray(3, buildInt(magicDmg, magicHit, magicCritical, magicBonus, magicConsumeReduction));
		this.randomShort();
	}
	
	/**
	 * 智力能力資訊
	 * @param magicDmg
	 * @param magicHit
	 * @param magicCritical
	 * @param magicBonus
	 * @param magicConsumeReduction
	 * @return
	 */
	private byte[] buildInt(final int magicDmg, final int magicHit, final int magicCritical, final int magicBonus, final int magicConsumeReduction) {
		S_IntDetails data = new S_IntDetails();
		data.writeInt32(1, magicDmg);// 魔法傷害
		data.writeInt32(2, magicHit);// 魔法命中
		data.writeInt32(3, magicCritical);// 魔法暴擊
		data.writeInt32(4, magicBonus);// 魔法加成(額外魔法點數MB)
		data.writeInt32(5, magicConsumeReduction);// MP消耗減少
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
