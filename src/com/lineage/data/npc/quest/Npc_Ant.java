package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv15_1;
import com.lineage.data.quest.CrownLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 搜查螞蟻<BR>
 * 70782<BR>
 * 說明:艾莉亞的請求 (王族30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Ant extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Ant.class);

	private Npc_Ant() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Ant();
	}

	@Override
	public int type() {
		return 1;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			boolean isTak = false;
			if (pc.getTempCharGfx() == 1037) {// 巨蟻
				isTak = true;
			}
			if (pc.getTempCharGfx() == 1039) {// 巨大兵蟻
				isTak = true;
			}
			if (!isTak) {
				// #$@$%#$%．．．#$%@#
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant2"));
				return;
			}
			if (pc.isCrown()) {// 王族
				// LV15任務未完成
				if (!pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
					// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));

				} else {
					// LV30任務已經完成
					if (pc.getQuest().isEnd(CrownLv30_1.QUEST.get_id())) {
						// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));

					} else {
						// 等級達成要求
						if (pc.getLevel() >= CrownLv30_1.QUEST.get_questlevel()) {
							// 任務尚未開始
							if (!pc.getQuest().isStart(CrownLv30_1.QUEST.get_id())) {
								// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));

							} else {// 任務已經開始
								// 去西邊有一群守門螞蟻正在觀察著變種螞蟻的出沒
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant1"));
							}

						} else {
							// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));
						}
					}
				}

			} else if (pc.isKnight()) {// 騎士
				// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));

			} else if (pc.isElf()) {// 精靈
				// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));

			} else if (pc.isWizard()) {// 法師
				// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));

			} else {
				// 你應該聽說過吧？某一天這裡忽然出現新的變種螞蟻開始威脅到我們的生存。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
