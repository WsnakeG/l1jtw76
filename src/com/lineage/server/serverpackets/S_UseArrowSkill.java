package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

/**
 * 物件攻擊(遠程-物理攻擊 PC/NPC共用)
 * 
 * @author dexc
 */
public class S_UseArrowSkill extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件攻擊 - <font color="#ff0000">命中</font>(遠程-物理攻擊 PC/NPC共用)
	 * 
	 * @param cha 執行者
	 * @param targetobj 目標OBJID
	 * @param spellgfx 遠程動畫編號
	 * @param x 目標X
	 * @param y 目標Y
	 * @param dmg 傷害力
	 */
	public S_UseArrowSkill(final L1Character cha, final int targetobj, final int spellgfx, final int x,
			final int y, final int dmg) {

		int aid = 1;
		// 外型編號改變動作
		switch (cha.getTempCharGfx()) {
		case 3860:// 妖魔弓箭手
			aid = 21;
			break;

		case 2716:// 古代亡靈
			aid = 19;
			break;
		}

		/*
		 * 0000: 5e 01 8e 24 bb 01 a4 6c 00 00 0a 00 05 52 01 00
		 * ^..$...l.....R.. 0010: 00 42 00 00 c3 83 e1 7e c1 83 e5 7e 00 00 00
		 * 85 .B.....~...~.... 0000: 5e 01 8e 24 bb 01 a4 6c 00 00 0d 00 05 52
		 * 01 00 ^..$...l.....R.. 0010: 00 42 00 00 c3 83 e1 7e c1 83 e5 7e 00
		 * 00 00 ee .B.....~...~.... 0000: 5e 01 8e 24 bb 01 3c 20 00 00 0b 00
		 * 05 52 01 00 ^..$..< .....R.. 0010: 00 42 00 00 c3 83 e1 7e c0 83 e5
		 * 7e 00 00 00 58 .B.....~...~...X
		 */
		writeC(S_OPCODE_ATTACKPACKET);
		writeC(aid);// 動作代號
		writeD(cha.getId());// 執行者OBJID
		writeD(targetobj);// 目標OBJID

		if (dmg > 0) {
			writeH(dmg); // 傷害值

		} else {
			writeH(0); // 傷害值
		}

		writeC(cha.getHeading());// 新面向

		// 以原子方式将当前值加 1。
		writeD(0x00000152);

		writeH(spellgfx);// 遠程動畫編號
		writeC(0x7f);// ??
		writeH(cha.getX());// 執行者X點
		writeH(cha.getY());// 執行者Y點
		writeH(x);// 目標X點
		writeH(y);// 目標Y點

		writeD(0x00000000);
		writeC(0x00);
		// this.writeC(0x00);
		// this.writeC(0x00);
		// this.writeC(0x00);
	}

	/**
	 * 物件攻擊 - <font color="#ff0000">未命中</font>(遠程-物理攻擊 PC/NPC共用) 空攻擊使用
	 * 
	 * @param cha 執行者
	 * @param spellgfx 遠程動畫編號
	 * @param x 目標X
	 * @param y 目標Y
	 */
	public S_UseArrowSkill(final L1Character cha, final int spellgfx, final int x, final int y) {

		int aid = 1;
		// 外型編號改變動作
		if (cha.getTempCharGfx() == 3860) {
			aid = 21;
		}
		writeC(S_OPCODE_ATTACKPACKET);
		writeC(aid);// 動作代號
		writeD(cha.getId());// 執行者OBJID
		writeD(0x00);// 目標OBJID
		writeH(0x00);// 傷害力
		writeC(cha.getHeading());// 新面向

		// 以原子方式将当前值加 1。
		writeD(0x00000152);

		writeH(spellgfx);// 遠程動畫編號
		writeC(0x7f);// ??
		writeH(cha.getX());// 執行者X點
		writeH(cha.getY());// 執行者Y點
		writeH(x);// 目標X點
		writeH(y);// 目標Y點

		writeD(0x00000000);
		writeC(0x00);
		// this.writeC(0x00);
		// this.writeC(0x00);
		// this.writeC(0x00);
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
