package com.lineage.server.serverpackets.ability;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 精神能力資訊
 * @author kyo
 *
 */
public class S_WisDetails extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public S_WisDetails() {
		
	}

	/**
	 * 精神能力資訊
	 * @param type
	 * @param mpr
	 * @param manaMpr
	 * @param mr
	 * @param mpUpRange
	 */
	public S_WisDetails(final int type, final int mpr, final int manaMpr, final int mr, final int[] mpUpRange) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E3);
		// 能力詳細資訊輸出類型
		// 2:能力資訊更新(創建角色點選初始化 角色進入遊戲初始化 升級點選決定能力初始化 角色點數重置初始化 等級提升1 等級提升10初始化) 
		// 4:能力資訊更新(設定能力屬性:角色點數重置 升級點能力)
		// 16:能力資訊更新(設定能力屬性:創建角色 角色點數重置)
		// 32:能力資訊更新(設定能力屬性:升級點選能力)
		this.writeInt32(1, type);// 能力詳細資訊輸出類型
		
		S_WisDetails data = new S_WisDetails();
		data.writeInt32(1, mpr);// MP回復
		data.writeInt32(2, manaMpr);// MP藥水回復
		data.writeInt32(3, mr);// 魔法防禦力
		data.writeInt32(4, mpUpRange[0]);// 升等MP最小增加量
		data.writeInt32(5, mpUpRange[1]);// 升等MP最大增加量
		
		this.writeByteArray(4, data.getContent());
		this.randomShort();
	}
	
	/**
	 * 精神能力資訊
	 * @param type
	 * @param mpr
	 * @param manaMpr
	 * @param mr
	 * @param mpUpRange
	 * @param addMp
	 */
	public S_WisDetails(final int type, final int mpr, final int manaMpr, final int mr, final int[] mpUpRange, int addMp) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E3);
		// 能力詳細資訊輸出類型
		// 2:能力資訊更新(創建角色點選初始化 角色進入遊戲初始化 升級點選決定能力初始化 角色點數重置初始化 等級提升1 等級提升10初始化) 
		// 4:能力資訊更新(設定能力屬性:角色點數重置 升級點能力)
		// 16:能力資訊更新(設定能力屬性:創建角色 角色點數重置)
		// 32:能力資訊更新(設定能力屬性:升級點選能力)
		this.writeInt32(1, type);// 能力詳細資訊輸出類型
		
		S_WisDetails data = new S_WisDetails();
		data.writeInt32(1, mpr);// MP回復
		data.writeInt32(2, manaMpr);// MP藥水回復
		data.writeInt32(3, mr);// 魔法防禦力
		data.writeInt32(4, mpUpRange[0]);// 升等MP最小增加量
		data.writeInt32(5, mpUpRange[1]);// 升等MP最大增加量
		data.writeInt32(6, addMp);// MP上限 只有在創造角色 以及 重置系統 以及 升級點選能力 才會寫入 公式未知
		
		this.writeByteArray(4, data.getContent());
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
