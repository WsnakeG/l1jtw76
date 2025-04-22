package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 李察<BR>
 * 70545<BR>
 * 說明:王族的信念 (王族45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Richard extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Richard.class);

	private Npc_Richard() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Richard();
	}

	@Override
	public int type() {
		return 1;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// LV45任務已經完成
				if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
					// 唉～越來越懷念亞丁王國以往的時光。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= CrownLv45_1.QUEST.get_questlevel()) {
					// 任務尚未開始
					if (!pc.getQuest().isStart(CrownLv45_1.QUEST.get_id())) {
						// 唉～越來越懷念亞丁王國以往的時光。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));

					} else {// 任務已經開始
						if (pc.getInventory().checkItem(40586)) { // 已經具有物品
																	// 王族徽章的碎片A(背叛的妖魔隊長)
							// 以前管理亞丁王國治安的負責人－麥
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard4"));

						} else {
							// 亞丁王國的榮耀
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard1"));
						}
					}

				} else {
					// 唉～越來越懷念亞丁王國以往的時光。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));
				}

			} else if (pc.isKnight()) {// 騎士
				// 唉～越來越懷念亞丁王國以往的時光。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));

			} else if (pc.isElf()) {// 精靈
				// 唉～越來越懷念亞丁王國以往的時光。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));

			} else if (pc.isWizard()) {// 法師
				// 唉～越來越懷念亞丁王國以往的時光。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 唉～越來越懷念亞丁王國以往的時光。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 唉～越來越懷念亞丁王國以往的時光。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 唉～越來越懷念亞丁王國以往的時光。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));

			} else {
				// 唉～越來越懷念亞丁王國以往的時光。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
