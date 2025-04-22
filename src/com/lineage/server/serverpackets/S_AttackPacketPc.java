package com.lineage.server.serverpackets;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件攻擊(PC 用)
 * 
 * @author dexc
 */
public class S_AttackPacketPc extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件攻擊 - <font color="#ff0000">命中</font>(PC 用 - 近距離)
	 * 
	 * @param pc 執行者
	 * @param target 目標
	 * @param type 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
	 * @param dmg 傷害力
	 */
	public S_AttackPacketPc(final L1PcInstance pc, final L1Character target, final int type, final int dmg) {
		/*
		 * 0000: 5e 01 be ac bf 01 a4 6c 00 00 01 00 04 00 00 00
		 * ^......l........ 0010: 00 00 44 00 01 00 aa 30 ..D....0 0000: 5e 01
		 * be ac bf 01 a4 6c 00 00 00 00 04 00 00 00 ^......l........ 0010: 00
		 * 00 39 38 00 00 40 97 ..98..@. 0000: 5e 01 be ac bf 01 3c 20 00 00 01
		 * 00 05 00 00 00 ^.....< ........ 0010: 00 00 f7 00 35 34 91 ba
		 * ....54..
		 */
		writeC(S_OPCODE_ATTACKPACKET);
		writeC(ActionCodes.ACTION_Attack);// ACTION_AltAttack
		writeD(pc.getId());
		writeD(target.getId());

		if (dmg > 0) {
			writeH(dmg); // 傷害值

		} else {
			writeH(0x00); // 傷害值
		}

		writeC(pc.getHeading());

		writeH(0x00); // target x
		writeH(0x00); // target y
		writeC(type); // 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
	}

	/**
	 * 物件攻擊 - <font color="#ff0000">未命中</font>(PC 用 - 近距離)
	 * 
	 * @param pc
	 * @param target
	 */
	public S_AttackPacketPc(final L1PcInstance pc, final L1Character target) {
		writeC(S_OPCODE_ATTACKPACKET);
		writeC(ActionCodes.ACTION_Attack);// ACTION_AltAttack
		writeD(pc.getId());
		writeD(target.getId());
		writeH(0x00); // damage
		writeC(pc.getHeading());

		writeH(0x00); // target x
		writeH(0x00); // target y
		writeC(0x00); // 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
	}

	/**
	 * 物件攻擊 - <font color="#ff0000">空擊</font>(PC 用 - 近距離)
	 * 
	 * @param pc
	 */
	public S_AttackPacketPc(final L1PcInstance pc) {
		writeC(S_OPCODE_ATTACKPACKET);
		writeC(ActionCodes.ACTION_Attack);// ACTION_AltAttack
		writeD(pc.getId());
		writeD(0x00);
		writeH(0x00); // damage
		writeC(pc.getHeading());

		writeH(0x00); // target x
		writeH(0x00); // target y
		writeC(0x00); // 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
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
