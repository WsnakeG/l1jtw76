package com.lineage.echo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.GetbackTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1FollowerInstance;
import com.lineage.server.model.Instance.L1HierarchInstance;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NewMaster;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

public class QuitGame {

	private static final Log _log = LogFactory.getLog(QuitGame.class);

	/**
	 * 人物離開遊戲的處理
	 * 
	 * @param pc
	 */
	public static void quitGame(final L1PcInstance pc) {
		if (pc == null) {
			return;
		}
		if (pc.isPrivateShop()) {
			pc.getSellList().clear();
			pc.getBuyList().clear();
			pc.setPrivateShop(false);
			pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
		}
		if (pc.getOnlineStatus() == 0) {
			return;
		}

		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan != null) {
			if (clan.getWarehouseUsingChar() == pc.getId()) { // 使用血盟倉庫中
				clan.setWarehouseUsingChar(0); // 解除使用狀態
			}
		}
		if (!pc.getPetList().isEmpty()) {
			final Object[] petList = pc.getPetList().values().toArray();
			// 寵物 召喚獸 消除
			if (petList != null) {
				remove_pet(pc, petList);
			}
		}

		try {
			if (!pc.getDolls().isEmpty()) {
				final Object[] dolls = pc.getDolls().values().toArray();
				for (final Object obj : dolls) {
					final L1DollInstance doll = (L1DollInstance) obj;
					if (doll != null) {
						doll.deleteDoll();
					}
				}
				pc.getDolls().clear();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			if (pc.get_power_doll() != null) {
				// 超級娃娃收回
				pc.get_power_doll().deleteDoll();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			if (!pc.get_otherList().get_illusoryList().isEmpty()) {
				// 分身消除
				final Object[] illList = pc.get_otherList().get_illusoryList().values().toArray();
				for (final Object obj : illList) {
					final L1IllusoryInstance ill = (L1IllusoryInstance) obj;
					if (ill != null) {
						ill.deleteMe();
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			// 清空特殊清單全部資料
			pc.get_otherList().clearAll();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			// 死亡狀態設置
			if (pc.isDead()) {
				final int[] loc = GetbackTable.GetBack_Location(pc, true);
				pc.setX(loc[0]);
				pc.setY(loc[1]);
				pc.setMap((short) loc[2]);
				pc.setCurrentHp(pc.getLevel());
				if (pc.get_food() > 40) {
					pc.set_food(40);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			// 交易終止
			if (pc.getTradeID() != 0) {
				final L1Trade trade = new L1Trade();
				trade.tradeCancel(pc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			// 決鬥終止
			if (pc.getFightId() != 0) {
				pc.setFightId(0);
				final L1PcInstance fightPc = (L1PcInstance) World.get().findObject(pc.getFightId());
				if (fightPc != null) {
					fightPc.setFightId(0);
					fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			// 移出隊伍
			if (pc.isInParty()) {
				pc.getParty().leaveMember(pc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			// 移出聊天隊伍
			if (pc.isInChatParty()) {
				pc.getChatParty().leaveMember(pc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			if (!pc.getFollowerList().isEmpty()) {
				// 跟隨者 主人離線 重新召喚
				final Object[] followerList = pc.getFollowerList().values().toArray();
				for (final Object obj : followerList) {
					final L1FollowerInstance follower = (L1FollowerInstance) obj;
					follower.setParalyzed(true);
					follower.spawn(follower.getNpcTemplate().get_npcId(), follower.getX(), follower.getY(),
							follower.getHeading(), follower.getMapId());
					follower.deleteMe();
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		try {
			// 移出各種處理清單
			pc.stopEtcMonitor();
			// _log.error("人物離開遊戲的處理-移出各種處理清單");
			// 解除登入狀態
			pc.setOnlineStatus(0);
			CharacterTable.updateOnlineStatus(pc);
			// _log.error("人物離開遊戲的處理-解除登入狀態");
			// 資料紀錄
			pc.save();
			// _log.error("人物離開遊戲的處理-資料紀錄");
			// 背包紀錄
			pc.saveInventory();
			// _log.error("人物離開遊戲的處理-背包紀錄");
			// 人物登出
			pc.logout();
			// _log.error("人物離開遊戲的處理-人物登出");

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
		}
	}

	private static void remove_pet(final L1PcInstance pc, final Object[] petList) {
		try {
			for (final Object obj : petList) {
				final L1NpcInstance petObject = (L1NpcInstance) obj;
				if (petObject != null) {
					if (petObject instanceof L1PetInstance) {
						final L1PetInstance pet = (L1PetInstance) petObject;
						// pet.dropItem(); // 2012-07-30 DEXC 變更人物離線寵物物品回到人物背包
						pet.collect(true);
						pc.removePet(pet);
						pet.deleteMe();
					}

					if (petObject instanceof L1HierarchInstance) { // 重登刪除祭司
						final L1HierarchInstance hierarch = (L1HierarchInstance) petObject;
						pc.getPetList().remove(hierarch.getId());
						hierarch.deleteMe();
					}

					if (petObject instanceof L1SummonInstance) {
						final L1SummonInstance summon = (L1SummonInstance) petObject;
						final S_NewMaster packet = new S_NewMaster(summon);
						for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(summon)) {
							if (visiblePc.equals(pc)) {
								continue;
							}
							visiblePc.sendPackets(packet);
						}
					}
				}
			}
			// 清空 寵物 召喚獸 清單
			pc.getPetList().clear();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
