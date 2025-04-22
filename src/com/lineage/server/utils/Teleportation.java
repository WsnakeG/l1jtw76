package com.lineage.server.utils;

import static com.lineage.server.model.skill.L1SkillId.MEDITATION;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.HashSet;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_MapID;
import com.lineage.server.serverpackets.S_NPCPack_Doll;
import com.lineage.server.serverpackets.S_NPCPack_Pet;
import com.lineage.server.serverpackets.S_NPCPack_Summon;
import com.lineage.server.serverpackets.S_OtherCharPacks;
import com.lineage.server.serverpackets.S_OwnCharPack;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxWindShackle;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_VipShow;
import com.lineage.server.templates.L1MapsLimitTime;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 移動後座標更新
 * 
 * @author dexc
 */
public class Teleportation {

	private static final Log _log = LogFactory.getLog(Teleportation.class);

	private static Random _random = new Random();

	private Teleportation() {
	}

	/**
	 * 移動後座標更新
	 * 
	 * @param pc
	 */
	public static void teleportation(final L1PcInstance pc) {
		try {
			if (pc == null) {
				return;
			}

			if (pc.getOnlineStatus() == 0) {
				return;
			}

			if (pc.getNetConnection() == null) {
				return;
			}

			if (pc.isDead()) {
				return;
			}

			if (pc.isTeleport()) {
				return;
			}

			// 解除舊座標障礙宣告
			pc.getMap().setPassable(pc.getLocation(), true);

			short mapId = pc.getTeleportMapId();

			// 在歐林副本地圖內死亡, 副本結束後會將其傳回說島 by terry0412
			// 有一個S封包可以將死亡玩家強制退回角色選單, 待修正...
			if (pc.isDead() && (mapId != 9101)) {
				return;
			}

			int x = pc.getTeleportX();
			int y = pc.getTeleportY();
			final int head = pc.getTeleportHeading();

			// 玩家傳送前所在地圖 by terry0412
			final short pc_mapId = pc.getMapId();

			// 防止座標異常
			final L1Map map = L1WorldMap.get().getMap(mapId);

			if (!map.isInMap(x, y) && !pc.isGm()) {
				x = pc.getX();
				y = pc.getY();
				mapId = pc.getMapId();
			}

			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				if (clan.getWarehouseUsingChar() == pc.getId()) { // 使用血盟倉庫中
					clan.setWarehouseUsingChar(0); // 解除使用狀態
				}
			}

			World.get().moveVisibleObject(pc, mapId);
			// 設置座標資訊
			pc.setLocation(x, y, mapId);
			pc.setHeading(head);

			// 記錄移動前座標
			pc.setOleLocX(x);
			pc.setOleLocY(y);

			// 地圖水中狀態
			final boolean isUnderwater = pc.getMap().isUnderwater();

			// 更新所在地圖封包
			pc.sendPackets(new S_MapID(pc, pc.getMapId(), isUnderwater));

			// 地圖經驗加倍
			/*
			 * if (MapExpTable.get().get_level(pc.getMapId(), pc.getLevel())) {
			 * // 1,226：稍微增加狩獵的經驗值。 pc.sendPackets(new
			 * S_ServerMessage("\\fT您目前正在經驗加倍地圖!")); }
			 */

			// 發送人物資料給周邊物件
			if (!pc.isGhost() && !pc.isGmInvis() && !pc.isInvisble()) {
				pc.broadcastPacketAll(new S_OtherCharPacks(pc));
			}

			if (pc.isReserveGhost()) { // 鬼魂狀態解除
				pc.endGhost();
			}

			pc.sendPackets(new S_OwnCharPack(pc));

			pc.removeAllKnownObjects();
			pc.sendVisualEffectAtTeleport(); // クラウン、毒、水中等の視覚効果を表示
			pc.updateObject();
			// spr番号6310, 5641の変身中にテレポートするとテレポート後に移動できなくなる
			// 武器を着脱すると移動できるようになるため、S_CharVisualUpdateを送信する
			pc.sendPackets(new S_CharVisualUpdate(pc));

			pc.killSkillEffectTimer(MEDITATION);
			pc.setCallClanId(0); // コールクランを唱えた後に移動すると召喚無効

			/*
			 * subjects ペットとサモンのテレポート先画面内へ居たプレイヤー。
			 * 各ペット毎にUpdateObjectを行う方がコード上ではスマートだが、
			 * ネットワーク負荷が大きくなる為、一旦Setへ格納して最後にまとめてUpdateObjectする。
			 */
			final HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();
			subjects.add(pc);

			if (!pc.isGhost()) {
				// 可以攜帶寵物
				if (pc.getMap().isTakePets()) {
					// 寵物的跟隨移動
					for (final L1NpcInstance petNpc : pc.getPetList().values()) {
						// 主人身邊隨機座標取回
						final L1Location loc = pc.getLocation().randomLocation(3, false);
						int nx = loc.getX();
						int ny = loc.getY();
						if ((pc.getMapId() == 5125) || (pc.getMapId() == 5131) || (pc.getMapId() == 5132)
								|| (pc.getMapId() == 5133) || (pc.getMapId() == 5134)) { // 寵物戰戰場
							nx = (32799 + _random.nextInt(5)) - 3;
							ny = (32864 + _random.nextInt(5)) - 3;
						}

						// 設置副本編號
						petNpc.set_showId(pc.get_showId());

						teleport(petNpc, nx, ny, mapId, head);

						if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
							final L1SummonInstance summon = (L1SummonInstance) petNpc;
							pc.sendPackets(new S_NPCPack_Summon(summon, pc));

						} else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
							final L1PetInstance pet = (L1PetInstance) petNpc;
							pc.sendPackets(new S_NPCPack_Pet(pet, pc));
						}

						for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(petNpc)) {
							// 畫面內可見人物 認識更新
							visiblePc.removeKnownObject(petNpc);
							if (visiblePc.get_showId() == petNpc.get_showId()) {
								subjects.add(visiblePc);
							}
						}
					}

				} else {
					//
				}

				// 娃娃的跟隨移動
				if (!pc.getDolls().isEmpty()) {
					// 主人身邊隨機座標取回
					final L1Location loc = pc.getLocation().randomLocation(3, false);
					final int nx = loc.getX();
					final int ny = loc.getY();

					final Object[] dolls = pc.getDolls().values().toArray();
					for (final Object obj : dolls) {
						final L1DollInstance doll = (L1DollInstance) obj;
						teleport(doll, nx, ny, mapId, head);
						pc.sendPackets(new S_NPCPack_Doll(doll, pc));
						// 設置副本編號
						doll.set_showId(pc.get_showId());

						for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(doll)) {
							// 畫面內可見人物 認識更新
							visiblePc.removeKnownObject(doll);
							if (visiblePc.get_showId() == doll.get_showId()) {
								subjects.add(visiblePc);
							}
						}
					}
				}
				// 取回娃娃
				if (pc.get_power_doll() != null) {
					// 主人身邊隨機座標取回
					final L1Location loc = pc.getLocation().randomLocation(3, false);
					final int nx = loc.getX();
					final int ny = loc.getY();

					teleport(pc.get_power_doll(), nx, ny, mapId, head);
					pc.sendPackets(new S_NPCPack_Doll(pc.get_power_doll(), pc));
					// 設置副本編號
					pc.get_power_doll().set_showId(pc.get_showId());

					for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(pc.get_power_doll())) {
						// 畫面內可見人物 認識更新
						visiblePc.removeKnownObject(pc.get_power_doll());
						if (visiblePc.get_showId() == pc.get_power_doll().get_showId()) {
							subjects.add(visiblePc);
						}
					}
				}
			}

			for (final L1PcInstance updatePc : subjects) {
				updatePc.updateObject();
			}

			// 地圖編號前後有變動 by terry0412
			if (pc_mapId != mapId) {
				// 地圖群組設置資料 (入場時間限制)
				final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(mapId);
				if (mapsLimitTime != null) {
					final int order_id = mapsLimitTime.getOrderId();
					final int used_time = pc.getMapsTime(order_id);
					final int limit_time = mapsLimitTime.getLimitTime();

					if (used_time < limit_time) {
						pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_LIMIT_TIMER, limit_time - used_time));
					}
				}
			}

			pc.setTeleport(false);
			
			// 傳送鎖定解除
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

			if (pc.hasSkillEffect(WIND_SHACKLE)) {
				pc.sendPackets(
						new S_PacketBoxWindShackle(pc.getId(), pc.getSkillEffectTimeSec(WIND_SHACKLE)));
			}

			// 新增座標障礙宣告
			pc.getMap().setPassable(pc.getLocation(), false);
			pc.getPetModel();

			if (pc.get_vipLevel() > 0) {
				final S_VipShow vipShow = new S_VipShow(pc.getId(), pc.get_vipLevel());
				pc.sendPacketsAll(vipShow);
			}
			
			if (pc.hasSkillEffect(7021)) {
				Random _random = new Random();
				int rndxx = pc.getX() + _random.nextInt(20) - 10;
				int rndyy = pc.getY() + _random.nextInt(20) - 10;

				while (CheckUtil.checkPassable(pc, rndxx, rndyy, pc.getMapId())
						|| !pc.getMap().isInMap(rndxx, rndyy)
						|| pc.getMap().getOriginalTile(rndxx, rndyy) == 0
						|| pc.getX() == rndxx && pc.getY() == rndyy
						|| !pc.glanceCheck(rndxx, rndyy)
						|| !pc.getMap().isPassable(rndxx, rndyy, 0, null)
						|| !pc.getMap().isPassable(rndxx, rndyy, 1, null)
						|| !pc.getMap().isPassable(rndxx, rndyy, 2, null)
						|| !pc.getMap().isPassable(rndxx, rndyy, 3, null)
						|| !pc.getMap().isPassable(rndxx, rndyy, 4, null)
						|| !pc.getMap().isPassable(rndxx, rndyy, 5, null)
						|| !pc.getMap().isPassable(rndxx, rndyy, 6, null)
						|| !pc.getMap().isPassable(rndxx, rndyy, 7, null)) {
					rndxx = pc.getX() + _random.nextInt(20) - 10;
					rndyy = pc.getY() + _random.nextInt(20) - 10;
				}
				int[] xyz = { rndxx, rndyy };
				pc.set_aixyz(xyz);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 寵物的傳送
	 * 
	 * @param npc
	 * @param x
	 * @param y
	 * @param map
	 * @param head
	 */
	public static void teleport(final L1NpcInstance npc, final int x, final int y, final short map,
			final int head) {
		try {
			World.get().moveVisibleObject(npc, map);

			L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), true, 2);
			npc.setX(x);
			npc.setY(y);
			npc.setMap(map);
			npc.setHeading(head);
			L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), false, 2);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
