package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.timecontroller.event.ranking.RankingHeroTimer;

/**
 * 英雄風雲榜 80029 UPDATE `npc` SET `nameid`='英雄風雲榜' WHERE `npcid`='80029';#
 * 
 * @author dexc
 */
public class Npc_HeroRanking extends NpcExecutor {

	/**
	 * 英雄風雲榜
	 */
	private Npc_HeroRanking() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_HeroRanking();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_h_1"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		final String[] userName = new String[11];
		if (cmd.equals("c")) {// 王族風雲榜
			final String[] names = RankingHeroTimer.userNameC();
			for (int i = 0; i < names.length; i++) {
				userName[i] = names[i];
			}
			userName[10] = "王族";

		} else if (cmd.equals("k")) {// 騎士風雲榜
			final String[] names = RankingHeroTimer.userNameK();
			for (int i = 0; i < names.length; i++) {
				userName[i] = names[i];
			}
			userName[10] = "騎士";

		} else if (cmd.equals("e")) {// 精靈風雲榜
			final String[] names = RankingHeroTimer.userNameE();
			for (int i = 0; i < names.length; i++) {
				userName[i] = names[i];
			}
			userName[10] = "精靈";

		} else if (cmd.equals("w")) {// 法師風雲榜
			final String[] names = RankingHeroTimer.userNameW();
			for (int i = 0; i < names.length; i++) {
				userName[i] = names[i];
			}
			userName[10] = "法師";

		} else if (cmd.equals("d")) {// 黑妖風雲榜
			final String[] names = RankingHeroTimer.userNameD();
			for (int i = 0; i < names.length; i++) {
				userName[i] = names[i];
			}
			userName[10] = "黑暗精靈";

		} else if (cmd.equals("g")) {// 龍騎風雲榜
			final String[] names = RankingHeroTimer.userNameG();
			for (int i = 0; i < names.length; i++) {
				userName[i] = names[i];
			}
			userName[10] = "龍騎士";

		} else if (cmd.equals("i")) {// 幻術風雲榜
			final String[] names = RankingHeroTimer.userNameI();
			for (int i = 0; i < names.length; i++) {
				userName[i] = names[i];
			}
			userName[10] = "幻術師";

		} else if (cmd.equals("a")) {// 全職業風雲榜
			final String[] names = RankingHeroTimer.userNameAll();
			for (int i = 0; i < names.length; i++) {
				userName[i] = names[i];
			}
			userName[10] = "全職業";
		}

		if (userName != null) {
			final String htmlid = "y_h_2";
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid, userName));
		}
	}
}
