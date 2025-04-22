package com.lineage.server.timecontroller.pet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * PET SUMMON MP回復執行
 * 
 * @author dexc
 */
public class MprPet {

	private static final Log _log = LogFactory.getLog(MprPet.class);

	/**
	 * PET SUMMON MP自然回復判斷
	 * 
	 * @param npc
	 * @return
	 */
	public static boolean mpUpdate(final L1NpcInstance npc, final int time) {
		try {
			if (npc.getMaxHp() <= 0) {// 沒有HP設置
				return false;
			}

			if (npc.getMaxMp() <= 0) {// 沒有MP設置
				return false;
			}

			if (npc.isDead()) {// 死亡
				return false;
			}

			if (npc.destroyed()) {// 死亡
				return false;
			}

			if (npc.getCurrentHp() <= 0) {// 目前HP小於0
				return false;
			}

			if (npc.getCurrentMp() >= npc.getMaxMp()) {// MP已滿
				return false;
			}

			int mprInterval = npc.getNpcTemplate().get_mprinterval();
			if (mprInterval <= 0) {
				mprInterval = 20;
			}

			if ((time % mprInterval) == 0) {
				int mpr = npc.getNpcTemplate().get_mpr();
				if (mpr <= 0) {
					mpr = 1;
				}

				mprInterval(npc, mpr);
				return true;
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 執行回復MP
	 * 
	 * @param summon
	 * @param mpr
	 */
	private static void mprInterval(final L1NpcInstance npc, final int mpr) {
		try {
			if (npc.isMpRegenerationX()) {
				npc.setCurrentMp(npc.getCurrentMp() + mpr);
			}

		} catch (final Exception e) {
			_log.error("PET 執行回復MP發生異常", e);
			npc.deleteMe();
		}
	}
}
