package com.lineage.server.timecontroller.pet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * PET SUMMON HP回復執行
 * 
 * @author dexc
 */
public class HprPet {

	private static final Log _log = LogFactory.getLog(HprPet.class);

	/**
	 * PET SUMMON HP自然回復
	 * 
	 * @param npc
	 * @return
	 */
	public static boolean hpUpdate(final L1NpcInstance npc, final int time) {
		try {
			if (npc.getMaxHp() <= 0) {// 沒有HP設置
				return false;
			}

			if (npc.getCurrentHp() <= 0) {// 目前HP小於0
				return false;
			}

			if (npc.isDead()) {// 死亡
				return false;
			}

			if (npc.destroyed()) {// 死亡
				return false;
			}

			if (npc.getCurrentHp() >= npc.getMaxHp()) {// HP已滿
				return false;
			}

			int hprInterval = npc.getNpcTemplate().get_hprinterval();
			if (hprInterval <= 0) {
				hprInterval = 20;
			}

			if ((time % hprInterval) == 0) {
				int hpr = npc.getNpcTemplate().get_hpr();
				if (hpr <= 0) {
					hpr = 1;
				}

				hprInterval(npc, hpr);
				return true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 執行回復HP
	 * 
	 * @param npc
	 * @param hpr
	 */
	private static void hprInterval(final L1NpcInstance npc, final int hpr) {
		try {
			if (npc.isHpRegenerationX()) {
				npc.setCurrentHp(npc.getCurrentHp() + hpr);
			}

		} catch (final Exception e) {
			_log.error("PET 執行回復HP發生異常", e);
			npc.deleteMe();
		}
	}
}
