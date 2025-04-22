package com.lineage.william;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigAlt;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * BOSS挑戰館
 */
public class BossRoom {

	private final ArrayList<L1PcInstance> _members = new ArrayList<L1PcInstance>();

	private static BossRoom _instance;

	public static BossRoom getInstance() {
		if (_instance == null) {
			_instance = new BossRoom();
		}
		return _instance;
	}

	public static final int STATUS_NONE = 0; // 閒置
	public static final int STATUS_READY = 1; // 等待中
	public static final int STATUS_PLAYING = 2; // 遊戲開始
	public static final int STATUS_CLEANING = 4; // 清潔中

	private static final int _minPlayer = ConfigAlt.BossPlayermin; // 最少玩家數
	private static final int _maxPlayer = ConfigAlt.BossPlayermax; // 最多玩家數

	private static final int _readytime = 60 * 1000; // 等待時間 60秒 + 倒數 = 總共70秒
	private static final int _cleartime = ConfigAlt.CleantimeSet * 1000; // 清潔時間
																			// 1小時

	private static final int _bossId1 = ConfigAlt.BossId1;
	private static final int _bossId2 = ConfigAlt.BossId2;
	private static final int _bossId3 = ConfigAlt.BossId3;
	private static final int _bossId4 = ConfigAlt.BossId4;
	private static final int _bossId5 = ConfigAlt.BossId5;
	private static final int _bossId6 = ConfigAlt.BossId6;
	private static final int _bossId7 = ConfigAlt.BossId7;
	private static final int _bossId8 = ConfigAlt.BossId8;
	private static final int _bossId9 = ConfigAlt.BossId9;
	private static final int _bossId10 = ConfigAlt.BossId10;

	private static int bossstep = 0;

	public String enterBossRoom(final L1PcInstance pc) {

		if (BossRoom.getInstance().getBossRoomStatus() == BossRoom.STATUS_CLEANING) {
			pc.sendPackets(new S_SystemMessage("\\aH目前挑戰館清潔重置中請稍後再來。"));
			return "";
		}

		if ((BossRoom.getInstance().getBossRoomStatus() == BossRoom.STATUS_PLAYING) && !isMember(pc)) {
			pc.sendPackets(new S_ServerMessage(1182)); // 遊戲已經開始了。
			return "";
		}

		if ((getMembersCount() >= _maxPlayer) && !isMember(pc)) {
			pc.sendPackets(new S_SystemMessage("\\aH挑戰館參與人數已達到飽和狀態無法進入。"));
			return "";
		}

		if (!isMember(pc) // 2014/07/09 by Roy 修正極可能造成BUG之寫法
				&& (ConfigAlt.BossIdItemId > 0) && (ConfigAlt.BossIdItemCount > 0)
				&& !pc.getInventory().checkItem(ConfigAlt.BossIdItemId, ConfigAlt.BossIdItemCount)) {
			pc.sendPackets(new S_SystemMessage("\\aH您的參賽所需道具數量或人數不足"));
			return "";
		} else {
			// 刪除道具
			pc.getInventory().consumeItem(ConfigAlt.BossIdItemId, ConfigAlt.BossIdItemCount);
			L1Teleport.teleport(pc, ConfigAlt.bosslocx, ConfigAlt.bosslocy, (short) ConfigAlt.bossmapid, pc.getHeading(), true);
			addMember(pc);
			return "";
		}
	}

	private void addMember(final L1PcInstance pc) {
		if (!_members.contains(pc)) {
			_members.add(pc);
		}
		if ((getMembersCount() == 1) && (getBossRoomStatus() == STATUS_NONE)) {
			GeneralThreadPool.get().execute(new runBossRoom());
		}
	}

	private class runBossRoom implements Runnable {

		@Override
		public void run() {

			try {

				setBossRoomStatus(STATUS_READY);
				bossstep = 0;
				sendMessage("★☆★ <<<60秒後>>> 開始 ★☆★");
				Thread.sleep(_readytime);

				sendMessage("★☆★ <<<10秒>>> 倒數★☆★");
				Thread.sleep(10 * 1000);

				sendMessage("★☆★ <<<5秒>>> 倒數★☆★");
				Thread.sleep(1000);

				sendMessage("倒數 4秒");
				Thread.sleep(1000);

				sendMessage("倒數 3秒");
				Thread.sleep(1000);

				sendMessage("倒數 2秒");
				Thread.sleep(1000);

				sendMessage("倒數 1秒");
				Thread.sleep(1000);

				if (checkPlayerCount()) {

					setBossRoomStatus(STATUS_PLAYING);
					spawnBoss(_bossId1, "1", "2");
					Thread.sleep(60 * 1000);

					spawnBoss(_bossId2, "2", "3");
					Thread.sleep(60 * 1000);

					spawnBoss(_bossId3, "3", "4");
					Thread.sleep(60 * 1000);

					spawnBoss(_bossId4, "4", "5");
					Thread.sleep(60 * 1000);

					spawnBoss(_bossId5, "5", "6");
					Thread.sleep(60 * 1000);

					spawnBoss(_bossId6, "6", "7");
					Thread.sleep(60 * 1000);

					spawnBoss(_bossId7, "7", "8");
					Thread.sleep(60 * 1000);

					spawnBoss(_bossId8, "8", "9");
					Thread.sleep(60 * 1000);

					spawnBoss(_bossId9, "9", "10");
					Thread.sleep(120 * 1000);

					spawnBoss(_bossId10, "10", "11");
					Thread.sleep(180 * 1000);

					spawnBoss(_bossId10, "10", null);
					Thread.sleep(300 * 1000);

					endBossRoom();
				}

				Thread.sleep(_cleartime);

				setBossRoomStatus(STATUS_NONE);

			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private boolean checkPlayerCount() {
		if (getMembersCount() < _minPlayer) {
			setBossRoomStatus(STATUS_CLEANING);
			sendMessage("人數不足 " + _minPlayer + " 人，所以強制關閉遊戲");
			for (final L1PcInstance pc : getMembersArray()) {
				if ((ConfigAlt.BossIdItemId > 0) && (ConfigAlt.BossIdItemCount > 0)) {
					pc.getInventory().storeItem(ConfigAlt.BossIdItemId, ConfigAlt.BossIdItemCount);
					pc.sendPackets(new S_SystemMessage(
							"\\aD無法順利進行，挑戰館退還您參賽的道具(" + ConfigAlt.BossIdItemCount + ")。"));
				}
				L1Teleport.teleport(pc, 33442, 32797, (short) 4, pc.getHeading(), true);
			}
			clearMembers();
			return false;
		}
		return true;
	}

	private void endBossRoom() {
		setBossRoomStatus(STATUS_CLEANING);
		sendMessage("挑戰館遊戲結束，請下次再來");
		for (final L1PcInstance pc : getMembersArray()) {
			L1Teleport.teleport(pc, 33442, 32797, (short) 4, pc.getHeading(), true);
		}
		clearMembers();
		clearColosseum();
	}

	private void clearColosseum() {
		for (final Object obj : World.get().getVisibleObjects(ConfigAlt.bossclearmap).values()) {
			if (obj instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) obj;
				if (!mob.isDead()) {
					mob.setDead(true);
					mob.setStatus(ActionCodes.ACTION_Die);
					mob.setCurrentHpDirect(0);
					mob.deleteMe();
				}
			} else if (obj instanceof L1Inventory) {
				final L1Inventory inventory = (L1Inventory) obj;
				inventory.clearItems();
			}
		}
	}

	private void spawnBoss(final int npcid, final String msg1, final String msg2) {
		if (msg1.equalsIgnoreCase("9")) { // 倒數第二關關卡畫面公告
			sendMessage("第 " + msg1 + " 關 [" + getBossName(npcid) + "] 3分鐘後開始第 " + msg2 + " 關");
		} else if (msg1.equalsIgnoreCase("10")) { // 最後一關關卡畫面公告
			sendMessage("最後一關 [" + getBossName(npcid) + "] 請努力撐下去，5分鐘後結束挑戰關卡活動");
		} else {
			sendMessage("第 " + msg1 + " 關卡 無界之王 [" + getBossName(npcid) + "] 開始，60秒後開始第 [" + msg2 + "] 關卡");
		} // 每一關關關卡畫面公告
		spawn(npcid);
	}

	private void spawn(final int npcid) {
		try {
			bossstep += 1;
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);
			npc.setId(IdFactory.get().nextId());
			npc.setMap((short) 5153);
			npc.setX(32638);
			npc.setY(32898);
			switch (bossstep) {
			case 1:
				npc.setX(ConfigAlt.BossXYZ01[0]);
				npc.setY(ConfigAlt.BossXYZ01[1]);
				npc.setMap((short) ConfigAlt.BossXYZ01[2]);
				break;
			case 2:
				npc.setX(ConfigAlt.BossXYZ02[0]);
				npc.setY(ConfigAlt.BossXYZ02[1]);
				npc.setMap((short) ConfigAlt.BossXYZ02[2]);
				break;
			case 3:
				npc.setX(ConfigAlt.BossXYZ03[0]);
				npc.setY(ConfigAlt.BossXYZ03[1]);
				npc.setMap((short) ConfigAlt.BossXYZ03[2]);
				break;
			case 4:
				npc.setX(ConfigAlt.BossXYZ04[0]);
				npc.setY(ConfigAlt.BossXYZ04[1]);
				npc.setMap((short) ConfigAlt.BossXYZ04[2]);
				break;
			case 5:
				npc.setX(ConfigAlt.BossXYZ05[0]);
				npc.setY(ConfigAlt.BossXYZ05[1]);
				npc.setMap((short) ConfigAlt.BossXYZ05[2]);
				break;
			case 6:
				npc.setX(ConfigAlt.BossXYZ06[0]);
				npc.setY(ConfigAlt.BossXYZ06[1]);
				npc.setMap((short) ConfigAlt.BossXYZ06[2]);
				break;
			case 7:
				npc.setX(ConfigAlt.BossXYZ07[0]);
				npc.setY(ConfigAlt.BossXYZ07[1]);
				npc.setMap((short) ConfigAlt.BossXYZ07[2]);
				break;
			case 8:
				npc.setX(ConfigAlt.BossXYZ08[0]);
				npc.setY(ConfigAlt.BossXYZ08[1]);
				npc.setMap((short) ConfigAlt.BossXYZ08[2]);
				break;
			case 9:
				npc.setX(ConfigAlt.BossXYZ09[0]);
				npc.setY(ConfigAlt.BossXYZ09[1]);
				npc.setMap((short) ConfigAlt.BossXYZ09[2]);
				break;
			case 10:
				npc.setX(ConfigAlt.BossXYZ10[0]);
				npc.setY(ConfigAlt.BossXYZ10[1]);
				npc.setMap((short) ConfigAlt.BossXYZ10[2]);
				break;
			}
			Thread.sleep(1);
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(4);

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);
			npc.turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
		} catch (final Exception e) {
		}
	}

	private String getBossName(final int npcId) {
		String BossName = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("select name from npc where npcid = ?");
			pstm.setInt(1, npcId);
			rs = pstm.executeQuery();
			if (rs.next()) {
				BossName = rs.getString("name");
			}
		} catch (final SQLException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return BossName;
	}

	private void sendMessage(final String msg) {
		for (final L1PcInstance pc : getMembersArray()) {
			if (pc.getMapId() == ConfigAlt.bossroommapid) {
				pc.sendPackets(new S_BlueMessage(166, "\\f3" + msg));
			}
		}
	}

	private int _BossRoomStatus = STATUS_NONE;

	private void setBossRoomStatus(final int i) {
		_BossRoomStatus = i;
	}

	private int getBossRoomStatus() {
		return _BossRoomStatus;
	}

	private void clearMembers() {
		_members.clear();
	}

	private boolean isMember(final L1PcInstance pc) {
		return _members.contains(pc);
	}

	private L1PcInstance[] getMembersArray() {
		return _members.toArray(new L1PcInstance[_members.size()]);
	}

	private int getMembersCount() {
		return _members.size();
	}
}