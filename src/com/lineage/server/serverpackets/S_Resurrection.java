package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件復活
 * 
 * @author dexc
 */
public class S_Resurrection extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件復活
	 * 
	 * @param target 被復活的人物
	 * @param use 使用復活的人物
	 * @param type
	 */
	public S_Resurrection(final L1PcInstance target, final L1Character use, final int type) {
		writeC(S_OPCODE_RESURRECTION);
		writeD(target.getId());// 被復活的對象
		writeC(type);
		writeD(use.getId());// 使用復活的人物
		writeD(target.getClassId());
	}

	/**
	 * 物件復活
	 * 
	 * @param target 被復活的對象
	 * @param use 使用復活的對象
	 * @param type
	 */
	public S_Resurrection(final L1Character target, final L1Character use, final int type) {
		writeC(S_OPCODE_RESURRECTION);
		writeD(target.getId());// 被復活的對象
		writeC(type);
		writeD(use.getId());// 使用復活的人物
		writeD(target.getGfxId());
	}

	/**
	 * 物件復活(測試封包用)
	 * 
	 * @param target
	 * @param opid
	 * @param type
	 */
	public S_Resurrection(final L1PcInstance target, final int opid, final int type) {
		writeC(opid);
		writeD(target.getId());// 被復活的對象
		writeC(type);
		writeD(target.getId());// 使用復活的人物
		writeD(target.getClassId());
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
