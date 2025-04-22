package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.classes.L1ClassFeature;

/**
 * 人物屬性資訊
 * 
 * @author daien
 */
public class S_CharResetInfo extends ServerBasePacket {

	private byte[] _byte = null;

	public S_CharResetInfo(final L1PcInstance pc, final int type) {
		final int baseStr = L1ClassFeature.ORIGINAL_STR[pc.getType()];
		final int baseDex = L1ClassFeature.ORIGINAL_DEX[pc.getType()];
		final int baseCon = L1ClassFeature.ORIGINAL_CON[pc.getType()];
		final int baseWis = L1ClassFeature.ORIGINAL_WIS[pc.getType()];
		final int baseCha = L1ClassFeature.ORIGINAL_CHA[pc.getType()];
		final int baseInt = L1ClassFeature.ORIGINAL_INT[pc.getType()];
		
		
		final int originalStr = pc.getOriginalStr();
		final int originalDex = pc.getOriginalDex();
		final int originalCon = pc.getOriginalCon();
		final int originalWis = pc.getOriginalWis();
		final int originalCha = pc.getOriginalCha();
		final int originalInt = pc.getOriginalInt();
		
		
		final int upStr = originalStr - baseStr;
		final int upDex = originalDex - baseDex;
		final int upCon = originalCon - baseCon;
		final int upWis = originalWis - baseWis;
		final int upCha = originalCha - baseCha;
		final int upInt = originalInt - baseInt;
		
		
		writeC(S_OPCODE_CHARRESET);
		writeC(type);
		writeC((upInt << 4) + upStr);
		writeC((upDex << 4) + upWis);
		writeC((upCha << 4) + upCon);
		writeC(0x00);
		writeH(0x00);
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
