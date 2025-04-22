package com.lineage.data.npc.quest;

import java.util.List;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.event.ranking.RankingClanTimer;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;

/**
 * 血盟風雲榜 80028
 * 
 * @author dexc
 */
public class Npc_ClanRanking extends NpcExecutor {

	/**
	 * 血盟風雲榜
	 */
	private Npc_ClanRanking() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_ClanRanking();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		final String[] userName = RankingClanTimer.userName();
		final String[] out = new String[20];
		for (int i = 0; i < userName.length; i++) {
			if (!userName[i].equals(" ")) {
				out[i] = userName[i].replace(",", "(") + ")";
				out[i + 10] = "對" + out[i] + "宣戰";
			}
		}
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c_1", out));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (pc.getClanid() == 0) {// 沒有血盟
			// 1,064：不屬於血盟。
			pc.sendPackets(new S_ServerMessage(1064));
			return;
		}

		final L1Clan clan = pc.getClan();
		if (clan == null) { // 沒有血盟
			// 1,064：不屬於血盟。
			pc.sendPackets(new S_ServerMessage(1064));
			return;
		}

		if (!pc.isCrown()) {// 不是王族
			// 478：\f1只有王子和公主才能宣戰。
			pc.sendPackets(new S_ServerMessage(478));
			return;
		}

		if (clan.getLeaderId() != pc.getId()) {// 不是盟主
			// 518：血盟君主才可使用此命令。
			pc.sendPackets(new S_ServerMessage(518));
			return;
		}

		final String[] clanNameList = RankingClanTimer.userName();

		int i = -1;
		// 傳回數字選項
		if (cmd.matches("[0-9]+")) {
			i = Integer.valueOf(cmd).intValue();
		}

		if (i != -1) {
			final String tg = clanNameList[i];
			if (tg.equals(" ")) {
				return;
			}

			final String[] set = tg.split(",");
			final String tgClanName = set[0];
			final L1Clan clanX = WorldClan.get().getClan(tgClanName);

			if (clanX.getCastleId() != 0) {
				pc.sendPackets(new S_ServerMessage(166, "不能對傭有城堡的血盟宣戰!"));
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			if (clanX.getClanId() == pc.getClanid()) {// 自己血盟
				return;
			}

			final List<L1War> warList = WorldWar.get().getWarList(); // 全部戰爭訊息

			final String clanName = clan.getClanName();
			for (final L1War war : warList) {
				if (!war.checkClanInSameWar(clanName, tgClanName)) { // 已經宣戰
					return;
				}
				if (war.checkClanInWar(tgClanName)) { // 指定血盟已在戰爭中
					// 236 %0 血盟拒絕你的宣戰。
					pc.sendPackets(new S_ServerMessage(236, tgClanName));
					return;
				}
			}

			// 取回對方盟主資料
			final L1PcInstance enemyLeader = World.get().getPlayer(clanX.getLeaderName());

			if (enemyLeader == null) {
				// 218：\f1%0 血盟君主不在線上。
				pc.sendPackets(new S_ServerMessage(218, tgClanName));
				return;
			}

			enemyLeader.setTempID(pc.getId()); // 被宣戰方 保留對方ID
			// 217：%0 血盟向你的血盟宣戰。是否接受？(Y/N)
			enemyLeader.sendPackets(new S_Message_YN(217, clanName, pc.getName()));
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
