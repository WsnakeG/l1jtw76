package com.lineage.data.npc.quest2;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_DoActionGFX;

import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 屍魂魔法水晶 屍魂塔副本
 * 
 * @author TeX
 * 
 */
public class Npc_Conquestn extends NpcExecutor {
	private static final Log _log = LogFactory.getLog(Npc_Conquestn.class);

	private boolean _checkDead;

	public static NpcExecutor get() {
		return new Npc_Conquestn();
	}

	public int type() {
		return 8;
	}

	public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
		try {
			L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
			if (pc != null) {
				ArrayList<L1Character> targetList = npc.getHateList().toTargetArrayList();
				if (!targetList.isEmpty()) {
					for (L1Character cha : targetList) {
						if ((cha instanceof L1PcInstance)) {
							L1PcInstance find_pc = (L1PcInstance) cha;
							if (find_pc != null) {

							}
						}
					}

					for (L1Character cha : targetList) {
						if ((cha instanceof L1PcInstance)) {
							L1PcInstance find_pc = (L1PcInstance) cha;
							if ((!this._checkDead) || ((find_pc.getCurrentHp() > 0) && (!find_pc.isDead()))) {

							}
						}
					}

				}
			}
			ArrayList<L1Object> list = World.get().getVisibleObjects(pc, -1);
			if (list == null) {
				return pc;
			}
			for (L1Object object : list) {
				if ((object instanceof L1MonsterInstance)) {
					L1MonsterInstance dota = (L1MonsterInstance) object;
					// 只能對此NPC使用
					//if (((dota.getNpcId() >= 190029) && (dota.getNpcId() <= 190041)) || (dota.getNpcId() == 190044)) {
					if (((dota.getNpcId() >= 111493) && (dota.getNpcId() <= 111512)) || (dota.getNpcId() == 111485)) {
						dota.broadcastPacketAll(new S_DoActionGFX(dota.getId(), 2));
						dota.receiveDamage(pc, 300);
					}
				}
			}

			pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7771));

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
