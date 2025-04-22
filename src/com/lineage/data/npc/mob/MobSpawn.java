package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * NPC 死亡召喚指定NPC<BR>
 * 設置範例:<BR>
 * classname: mob.MobSpawn 設置範例: mob.MobSpawn 45601 400 60<BR>
 * 
 * @author dexc
 */
public class MobSpawn extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(MobSpawn.class);

	private MobSpawn() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new MobSpawn();
	}

	@Override
	public int type() {
		return 8;
	}

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			// 判斷主要攻擊者
			final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
			if (pc != null) {
				if (_npcid != 0) {
					final L1Npc l1npc = NpcTable.get().getTemplate(_npcid);
					if (l1npc == null) {
						_log.error("召喚NPC編號: " + _npcid + " 不存在!(mob.MobSpawn)");
						return pc;
					}
					final L1NpcInstance newnpc = L1SpawnUtil.spawnT(_npcid, npc.getX(), npc.getY(), npc.getMapId(), npc.getHeading(), _appeartime); // 召喚NPC後的出現時間
					newnpc.onNpcAI();
					// newnpc.turnOnOffLight();

					// 如有對話開始說話
					// newnpc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);

					pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	private int _npcid;
	private int _gfxid;
	private int _appeartime;

	@Override
	public void set_set(final String[] set) {
		try {
			_npcid = Integer.parseInt(set[1]);
			_gfxid = Integer.parseInt(set[2]);
			_appeartime = Integer.parseInt(set[3]);

		} catch (final Exception e) {
		}
	}
}
