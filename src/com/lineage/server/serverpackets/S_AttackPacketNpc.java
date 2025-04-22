package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * 物件攻擊(NPC用 - 近距離)
 * 
 * @author dexc
 */
public class S_AttackPacketNpc extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件攻擊 - <font color="#ff0000">命中</font>(NPC用 - 近距離)
	 * 
	 * @param npc 執行者
	 * @param target 目標
	 * @param type 動作編號
	 * @param dmg 傷害值
	 */
	public S_AttackPacketNpc(final L1NpcInstance npc, final L1Character target, final int type,
			final int dmg) {
		/*
		 * 0000: 5e 01 be ac bf 01 a4 6c 00 00 01 00 04 00 00 00
		 * ^......l........ 0010: 00 00 44 00 01 00 aa 30 ..D....0 0000: 5e 01
		 * be ac bf 01 a4 6c 00 00 00 00 04 00 00 00 ^......l........ 0010: 00
		 * 00 39 38 00 00 40 97 ..98..@. 0000: 5e 01 be ac bf 01 3c 20 00 00 01
		 * 00 05 00 00 00 ^.....< ........ 0010: 00 00 f7 00 35 34 91 ba
		 * ....54..
		 */
		writeC(S_OPCODE_ATTACKPACKET);
		writeC(type);
		writeD(npc.getId());// 執行者OBJID
		writeD(target.getId());// 被攻擊者OBJID

		if (dmg > 0) {
			this.writeH(dmg); // 傷害值

		} else {
			writeH(0x00); // 傷害值
		}
		// this.writeH(0x00); // 傷害值

		writeC(npc.getHeading()); // 執行者面向

		writeH(0x00); // target x
		writeH(0x00); // target y

		writeC(0x00); // 0x00:none 0x04:Claw 0x08:CounterMirror
	}

	/**
	 * 物件攻擊 - <font color="#ff0000">命中</font>(NPC用 - 近距離)
	 * 
	 * @param npc 執行者
	 * @param target 目標
	 * @param type 動作編號
	 * @param dmg 傷害值
	 */
	public S_AttackPacketNpc(final L1NpcInstance npc, final int targetid, final int type, final int dmg) {
		writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(type);
		this.writeD(npc.getId());// 執行者OBJID
		this.writeD(targetid);// 被攻擊者OBJID

		if (dmg > 0) {
			this.writeH(dmg); // 傷害值

		} else {
			this.writeH(0x00); // 傷害值
		}
		// this.writeH(0x00); // 傷害值

		this.writeC(npc.getHeading()); // 執行者面向

		this.writeH(0x00); // target x
		this.writeH(0x00); // target y

		this.writeC(0x00); // 0x00:none 0x04:Claw 0x08:CounterMirror
	}

	/**
	 * 物件攻擊 - <font color="#ff0000">未命中</font>(NPC用 - 近距離)
	 * 
	 * @param npc 執行者
	 * @param target 目標
	 * @param type 動作編號
	 */
	public S_AttackPacketNpc(final L1NpcInstance npc, final L1Character target, final int type) {
		writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(type);
		this.writeD(npc.getId());// 執行者OBJID
		this.writeD(target.getId());// 被攻擊者OBJID
		this.writeH(0x00); // 傷害值
		this.writeC(npc.getHeading()); // 執行者面向

		this.writeH(0x00); // target x
		this.writeH(0x00); // target y

		this.writeC(0x00); // 0x00:none 0x04:Claw 0x08:CounterMirror
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
