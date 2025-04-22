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
public class S_SkillBuyItemCN extends ServerBasePacket {

	private static final Log _log = LogFactory.getLog(S_SkillBuyItemCN.class);

	private byte[] _byte = null;

	/**
	 * 魔法購買清單(材料)
	 * 
	 * @param pc
	 * @param npc
	 */
	public S_SkillBuyItemCN(final L1PcInstance pc, final L1NpcInstance npc) {
		ArrayList<Integer> skillList = null;

		if (pc.isCrown()) {// 王族
			skillList = PcLvSkillList.isCrown(pc);

		} else if (pc.isKnight()) {// 騎士
			skillList = PcLvSkillList.isKnight(pc);

		} else if (pc.isElf()) {// 精靈
			skillList = PcLvSkillList.isElf(pc);

		} else if (pc.isWizard()) {// 法師
			skillList = PcLvSkillList.isWizard(pc);

		} else if (pc.isDarkelf()) {// 黑妖
			skillList = PcLvSkillList.isDarkelf(pc);

		} else if (pc.isDragonKnight()) {// 龍騎
			skillList = PcLvSkillList.isDragonKnight(pc);

		} else if (pc.isIllusionist()) {// 幻術
			skillList = PcLvSkillList.isIllusionist(pc);

		} else if (pc.isWarrior()) {// 戰士
			skillList = PcLvSkillList.isWarrior(pc);
		}

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
				// 0000: 01 01 00 17 00 00 00 ee ........
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
