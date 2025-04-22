package com.lineage.server.serverpackets;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 魔法購買(金幣)
 * 
 * @author dexc
 */
public class S_SkillBuy extends ServerBasePacket {

	private static final Log _log = LogFactory.getLog(S_SkillBuy.class);

	private byte[] _byte = null;

	/**
	 * 魔法購買(金幣)
	 * 
	 * @param pc 學習者
	 * @param newSkillList 學習清單
	 */
	public S_SkillBuy(final L1PcInstance pc, final ArrayList<Integer> newSkillList) {
		try {
			if (newSkillList.size() <= 0) {
				writeC(S_OPCODE_SKILLBUY);
				writeH(0x0000);

			} else {
				writeC(S_OPCODE_SKILLBUY);
				writeD(6000);
				writeH(newSkillList.size());
				for (final Integer integer : newSkillList) {
					writeD(integer);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
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
