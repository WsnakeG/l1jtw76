package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.ability.S_WeightStatus;

/**
 * 魔法效果:力量提升
 * 
 * @author dexc
 */
public class S_Strup extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:力量提升
	 * 
	 * @param pc 執行者
	 * @param type 增加值
	 * @param time 時間
	 */
	public S_Strup(final L1PcInstance pc, final int type, final int time) {
		// 0000: 3a b0 04 10 5a 05 9e 01 :...Z...
		writeC(S_OPCODE_STRUP);
		writeH(time);
		writeC(pc.getStr());
		writeC(pc.getInventory().getWeight240());
		writeC(type);
		pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int)pc.getMaxWeight(), pc.getInventory().getWeight(), (int)pc.getMaxWeight()));
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
