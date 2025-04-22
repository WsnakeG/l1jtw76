package com.lineage.server.serverpackets;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.list.PcLvSkillList;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 魔法購買清單(材料)
 * 
 * @author dexc
 */
public class S_SkillBuyItem extends ServerBasePacket {

	private static final Log _log = LogFactory.getLog(S_SkillBuyItem.class);

	private byte[] _byte = null;

	/**
	 * 魔法購買清單(材料)
	 * 
	 * @param pc
	 */
	public S_SkillBuyItem(final L1PcInstance pc, final L1NpcInstance npc) {
		// 0000: 01 01 00 17 00 00 00 ee ........

		final ArrayList<Integer> skillList = PcLvSkillList.scount(pc);

		final ArrayList<Integer> newSkillList = new ArrayList<Integer>();

		for (final Integer integer : skillList) {
			// 檢查是否已學習該法術
			if (!CharSkillReading.get().spellCheck(pc.getId(), (integer + 1))) {
				newSkillList.add(integer);
			}
		}

		if (newSkillList.size() <= 0) {
			this.writeC(S_OPCODE_SKILLBUYITEM); // XXX S_OPCODE_SKILLBUY_2 修改為 S_OPCODE_SKILLBUYITEM
			writeH(0x0000);

		} else {
			try {
				this.writeC(S_OPCODE_SKILLBUYITEM); // XXX S_OPCODE_SKILLBUY_2 修改為 S_OPCODE_SKILLBUYITEM
				writeH(newSkillList.size());
				for (final Integer integer : newSkillList) {
					writeD(integer);
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
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
