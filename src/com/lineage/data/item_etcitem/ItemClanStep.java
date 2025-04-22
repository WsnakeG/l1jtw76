package com.lineage.data.item_etcitem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 血盟技能階級 升級道具
 * 
 * @author erics4179
 */

public class ItemClanStep extends ItemExecutor {
	private static final Log _log = LogFactory.getLog(ItemClanStep.class);

	public static ItemExecutor get() {
		return new ItemClanStep();
	}

	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (pc == null) {
			return;
		}
		if (item == null) {
			return;
		}
		if (pc.getClanid() == 0) {
			return;
		}

		if (pc.getClan().getClanStep() >= 10) {
			pc.sendPackets(new S_SystemMessage("\\aH血盟階級已經是最高階段，不能升階了！"));
			return;
		}

		pc.getClan().setClanStep(pc.getClan().getClanStep() + 1);
		ClanReading.get().updateClan(pc.getClan());
		pc.getInventory().removeItem(item, 1);
		pc.sendPackets(new S_SystemMessage("\\aE您的血盟階段已經獲得提升！"));
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_s));
	}

	private int _gfxid_s; // 成功特效編號

	@Override
	public void set_set(String[] set) {
		try {
			_gfxid_s = Integer.parseInt(set[1]);

		} catch (Exception e) {
		}
	}
}
