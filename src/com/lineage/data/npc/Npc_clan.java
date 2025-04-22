package com.lineage.data.npc;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldWar;

/**
 * 血盟執行人 70538
 * 
 * @author dexc
 */
public class Npc_clan extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_clan.class);

	private static final String[] _skillName = new String[] { "無", "狂暴", "寂靜",
			"魔擊", "消魔", };

	public static final String[] SKILLINFO = new String[] {
			"\\fR血盟狂暴的爆發力流遍全身!", "\\fR血盟寂靜的氣息壟罩著你!", "\\fR血盟魔擊的光緩緩的發亮!",
			"\\fR血盟消魔的光環保護著你!", };

	private static final int[][] _items = new int[][] { { ConfigAlt.Clanskillitem, ConfigAlt.Clanskillcount }, // 血盟榮耀之證
																		// x
																		// 1<br>
	};

	/**
	 *
	 */
	private Npc_clan() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_clan();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		final int clanid = pc.getClanid();
		// 王族角色
		if (pc.isCrown()) {
			// 不具有血盟
			if (clanid == 0) {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1"));

				// 已有血盟
			} else {
				// 不具有血盟技能
				if (!pc.getClan().isClanskill()) {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1b"));

				} else {
					final SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					final Timestamp time = pc.getClan().get_skilltime();
					// int index = time.toString().indexOf(".");
					final String[] times = new String[] { sdf.format(time) };
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1aL",
							times));
				}
			}

			// 非王族角色
		} else {
			// 已有血盟
			if (clanid != 0) {
				// 不具有血盟技能
				if (!pc.getClan().isClanskill()) {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1g"));

				} else {
					final Timestamp time = pc.getClan().get_skilltime();
					final int index = time.toString().indexOf(".");
					final String[] times = new String[] { time.toString()
							.substring(0, index) };
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1a",
							times));
				}

				// 不具有血盟
			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1c"));
			}
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc,
			final String cmd, final long amount) {
		String htmlid = null;
		boolean html = false;
		boolean updatePc = false;

		final int clanid = pc.getClanid();

		if (clanid == 0) {
			// 王族角色
			if (pc.isCrown()) {
				htmlid = "y_c1d";

			} else {
				htmlid = "y_c1e";
			}
		}

		if (htmlid != null) {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid));
			return;
		}

		if (cmd.equals("1")) {// 學習血盟技能
			if (pc.getClan().isClanskill()) {
				html = true;
				htmlid = "y_c3";

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1g"));
			}

		} else if (cmd.equals("2")) {// 創立血盟技能
			// 王族角色
			if (pc.isCrown()) {
				if (!pc.getClan().isClanskill()) {
					get_clanSkill(pc, npc);
				}

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1f"));
			}

		} else if (cmd.equals("3")) {// 解散血盟
			if (pc.isCrown()) {
				if (pc.getId() == pc.getClan().getLeaderId()) {
					delClan(pc);
					pc.sendPackets(new S_CloseList(pc.getId()));

				} else {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1f"));
				}
			}

		} else if (cmd.equals("a")) {// 狂暴(增加物理攻擊力)
			if (pc.getClan().isClanskill()) {
				pc.get_other().set_clanskill(1);
				updatePc = true;
			}

		} else if (cmd.equals("b")) {// 寂靜(增加物理傷害減免)
			if (pc.getClan().isClanskill()) {
				pc.get_other().set_clanskill(2);
				updatePc = true;
			}

		} else if (cmd.equals("c")) {// 魔擊(增加魔法攻擊力)
			if (pc.getClan().isClanskill()) {
				pc.get_other().set_clanskill(4);
				updatePc = true;
			}

		} else if (cmd.equals("d")) {// 消魔(增加魔法傷害減免)
			if (pc.getClan().isClanskill()) {
				pc.get_other().set_clanskill(8);
				updatePc = true;
			}
		}
		if (updatePc) {
			try {
				html = true;
				htmlid = "y_c3a";
				// 資料庫更新
				pc.save();

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		if (html) {
			final int clanMan = pc.getClan().getOnlineClanMemberSize();
			String skillName = "";
			String start1 = " ";
			String start2 = " ";
			String start3 = " ";
			String start4 = " ";
			switch (pc.get_other().get_clanskill()) {
			case 0:// 無
				skillName = _skillName[0];
				break;
			case 1:// 血盟線上人數 每增加4人 物理攻擊力+1
				skillName = _skillName[1];
				start1 = "啟用";
				break;
			case 2:// 血盟線上人數 每增加4人 物理傷害減免+1
				skillName = _skillName[2];
				start2 = "啟用";
				break;
			case 4:// 血盟線上人數 每增加4人 魔法攻擊力+1
				skillName = _skillName[3];
				start3 = "啟用";
				break;
			case 8:// 血盟線上人數 每增加4人 魔法傷害減免+1
				skillName = _skillName[4];
				start4 = "啟用";
				break;
			}
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid,
					new String[] { skillName, String.valueOf(clanMan), start1,
							start2, start3, start4, _skillName[1],
							_skillName[2], _skillName[3], _skillName[4], }));
		}
	}

	/**
	 * 創立血盟技能
	 * 
	 * @param pc
	 * @param npc
	 */
	private void get_clanSkill(final L1PcInstance pc, final L1NpcInstance npc) {
		// 道具不足的顯示清單
		final Queue<String> itemListX = new ConcurrentLinkedQueue<String>();

		if (!pc.isGm()) {
			// 檢查所需物品數量
			for (final int itemid[] : _items) {
				final L1ItemInstance item = pc.getInventory().checkItemX(
						itemid[0], itemid[1]);
				if (item == null) {
					final long countX = pc.getInventory().countItems(itemid[0]);
					final L1Item itemX2 = ItemTable.get()
							.getTemplate(itemid[0]);
					if (countX > 0) {
						itemListX.offer(itemX2.getNameId() + " ("
								+ (itemid[1] - countX) + ")");

					} else {
						itemListX.offer(itemX2.getNameId() + " (" + itemid[1]
								+ ")");
					}
				}
			}

			if (itemListX.size() > 0) {
				for (final Iterator<String> iter = itemListX.iterator(); iter
						.hasNext();) {
					final String tgitem = iter.next();// 返回迭代的下一个元素。
					// 337：\f1%0不足%s。 0_o"
					pc.sendPackets(new S_ServerMessage(337, tgitem));
				}
				itemListX.clear();
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			itemListX.clear();

			// 檢查所需物品數量
			for (final int itemid[] : _items) {
				final int ritemid = itemid[0];// 道具編號
				final int rcount = itemid[1];// 耗用數量
				final L1ItemInstance item = pc.getInventory().checkItemX(
						ritemid, rcount);
				if (item != null) {
					final long rcountx = pc.getInventory().removeItem(item,
							rcount);
					if (rcountx != rcount) {// 刪除道具
						// 關閉對話窗
						pc.sendPackets(new S_CloseList(pc.getId()));
						return;
					}
				}
			}
		}

		final long time = System.currentTimeMillis();// 目前時間豪秒

		final long x1 = 30 * 24 * 60 * 60;// 30天耗用秒數
		final long x2 = x1 * 1000;// 轉為豪秒
		final long upTime = x2 + time;// 目前時間 加上 30天

		// 目前時間
		final Timestamp ts = new Timestamp(upTime);

		pc.getClan().set_clanskill(true);
		pc.getClan().set_skilltime(ts);

		// 更新血盟資料
		ClanReading.get().updateClan(pc.getClan());
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c2a"));
	}

	/**
	 * 解散血盟
	 * 
	 * @param pc
	 */
	private void delClan(final L1PcInstance pc) {
		final L1Clan clan = pc.getClan();
		if (clan != null) {
			final int clan_id = clan.getClanId();
			final String player_name = pc.getName();
			final String clan_name = clan.getClanName();
			final String clan_member_name[] = clan.getAllMembers();

			final int castleId = clan.getCastleId();
			final int houseId = clan.getHouseId();

			if (castleId != 0) {
				// \f1擁有城堡與血盟小屋的狀態下無法解散血盟。
				pc.sendPackets(new S_ServerMessage(665));
				return;
			}
			if (houseId != 0) {
				// \f1擁有城堡與血盟小屋的狀態下無法解散血盟。
				pc.sendPackets(new S_ServerMessage(665));
				return;
			}

			for (final L1War war : WorldWar.get().getWarList()) {
				// 戰爭中
				if (war.checkClanInWar(clan_name)) {
					// \f1無法解散。
					pc.sendPackets(new S_ServerMessage(302));
					return;
				}
			}

			try {
				for (int i = 0; i < clan_member_name.length; i++) { // 血盟成員訊息發送
					final L1PcInstance online_pc = World.get().getPlayer(
							clan_member_name[i]);
					L1PcInstance tg_pc = null;
					boolean isReTitle = false;
					if (online_pc != null) { // 線上成員
						tg_pc = online_pc;

						online_pc.sendPacketsAll(new S_CharTitle(online_pc
								.getId()));
						isReTitle = true;

						// %1血盟的盟主%0%s解散了血盟。
						online_pc.sendPackets(new S_ServerMessage(269,
								player_name, clan_name));

					} else { // 離線成員
						tg_pc = CharacterTable.get().restoreCharacter(
								clan_member_name[i]);
						isReTitle = true;
					}

					if (tg_pc != null) {
						tg_pc.setClanid(0);
						tg_pc.setClanname("");
						tg_pc.setClanRank(0);

						if (isReTitle) {
							tg_pc.setTitle("");
						}

						tg_pc.save();// 資料庫更新
					}
				}
				// 資料刪除
				ClanEmblemReading.get().deleteIcon(clan_id);
				ClanReading.get().deleteClan(clan_name);

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
