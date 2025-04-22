package com.lineage.server.timecontroller.quest;

import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.quest.ADLv80_3;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_PacketBoxWindShackle;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_Weather;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;

/**
 * 風龍副本 - 處理時間軸
 * 
 * @author terry0412
 */
public final class AD80_3_Timer extends TimerTask {

	private static final Log _log = LogFactory.getLog(AD80_3_Timer.class);

	private static final Random _random = new Random();

	private ScheduledFuture<?> _timer;

	private int _qid = -1;

	// 計數器1
	private int _counter;

	// 計數器2
	private int _counter2;

	// 中心座標
	private L1Location _loc;

	public void start() {
		_qid = ADLv80_3.QUEST.get_id();
		_loc = new L1Location(32848, 32877, ADLv80_3.MAPID);
		final int timeMillis = 2000;
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			// 執行中任務副本
			final ArrayList<L1QuestUser> questList = WorldQuest.get().getQuests(_qid);
			// 不包含元素
			if (questList.isEmpty()) {
				return;
			}

			for (final Object object : questList.toArray()) {
				final L1QuestUser quest = (L1QuestUser) object;
				for (final L1NpcInstance npc : quest.npcList()) {
					if (npc.isDead()) {
						continue;
					}
					if ((npc.getNpcId() >= 97204) && (npc.getNpcId() <= 97209)) {

						if (++_counter >= 30) {
							_counter = 0;

							L1PcInstance find_pc = null;
							final ArrayList<L1PcInstance> playerList = World.get().getRecognizePlayer(npc);
							Collections.shuffle(playerList);
							for (final L1PcInstance pc : playerList) {
								if (pc.getLocation().getTileLineDistance(npc.getLocation()) <= 12) {
									find_pc = pc;
									break;
								}
							}
							if (find_pc != null) {
								final int time = 16;
								final ArrayList<L1PcInstance> pc_list = World.get().getVisiblePlayer(find_pc,
										6);
								pc_list.add(find_pc);
								for (final L1PcInstance pc : pc_list) {
									if (pc.hasSkillEffect(WIND_SHACKLE)) {
										continue;
									}
									pc.sendPacketsAll(new S_SkillSound(pc.getId(), 1799));
									pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), time));
									pc.setSkillEffect(WIND_SHACKLE, time * 1000);
									// 伴隨傷害 100~300
									pc.sendPacketsAll(
											new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Damage));
									pc.receiveDamage(pc, _random.nextInt(201) + 100, L1Skills.ATTR_WIND);
								}
							}
						}

						if (++_counter2 >= 45) {
							_counter2 = 0;

							boolean check_spawn = false;
							for (final L1NpcInstance check_npc : quest.npcList()) {
								if (check_npc.getNpcId() == 97210) {
									check_spawn = true;
									break;
								}
							}
							if (!check_spawn) {
								final L1Location loc = _loc.randomLocation(12, true);
								L1SpawnUtil.spawn(97210, loc, 0, quest.get_id());
							}
						}
					}
					if ((npc.getNpcId() >= 97204) && (npc.getNpcId() <= 97206)) {
						// 隨機變更
						if (_random.nextInt(100) < 5) {
							for (final L1PcInstance tgpc : quest.pcList()) {
								switch (_random.nextInt(3)) {
								case 0:
									npc.getNpcTemplate().set_weakAttr(4);
									tgpc.sendPackets(new S_PacketBoxGree(6));
									break;
								case 1:
									npc.getNpcTemplate().set_weakAttr(1);
									tgpc.sendPackets(new S_PacketBoxGree(7));
									break;
								case 2:
									npc.getNpcTemplate().set_weakAttr(8);
									tgpc.sendPackets(new S_PacketBoxGree(8));
									break;
								}
							}
						}
						// 停留在空中
						if (npc.getStatus() == ActionCodes.ACTION_AxeDamage) {
							if (npc.getSkyTime() >= 8) {
								npc.setSkyTime(0);
								for (final L1PcInstance tgpc : quest.pcList()) {
									tgpc.sendPackets(new S_Weather(World.get().getWeather()));
								}
								for (final L1PcInstance pc : World.get().getRecognizePlayer(npc)) {
									pc.sendPackets(new S_RemoveObject(npc));
									pc.removeKnownObject(npc);
								}
								final L1Location loc = npc.getLocation().randomLocation(12, true);
								npc.setLocation(loc);
								npc.setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_NONE);
								npc.setStatus(ActionCodes.ACTION_Walk);
								break;
							}
							npc.setSkyTime(npc.getSkyTime() + 1);
							final L1Location loc = _loc.randomLocation(12, false);
							final int effectId;
							final int range;
							if (_random.nextInt(100) < 70) {
								effectId = 10405;
								range = 3;
							} else {
								effectId = 10407;
								range = 5;
							}
							final S_EffectLocation packet = new S_EffectLocation(loc.getX(), loc.getY(),
									effectId);
							// 受傷處理
							for (final L1PcInstance tgpc : quest.pcList()) {
								if (tgpc.getLocation().isInScreen(loc)) {
									tgpc.sendPackets(packet);
								}
								if (tgpc.getLocation().getTileLineDistance(loc) < range) {
									tgpc.sendPacketsAll(
											new S_DoActionGFX(tgpc.getId(), ActionCodes.ACTION_Damage));
									tgpc.receiveDamage(tgpc, _random.nextInt(301) + 300, L1Skills.ATTR_WIND);
								}
							}
						} else if (_random.nextInt(100) < 5) {
							npc.allTargetClear();
							npc.setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
							npc.broadcastPacketAll(
									new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_AxeDamage));
							npc.setStatus(ActionCodes.ACTION_AxeDamage);
							npc.broadcastPacketAll(new S_NPCPack(npc));

							for (final L1PcInstance tgpc : quest.pcList()) {
								tgpc.sendPackets(new S_Weather(17));
							}
						}
					}
				}
			}
			questList.clear();

		} catch (final Exception e) {
			_log.error("風龍副本 傷害計能施放 時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final AD80_3_Timer ad80_3Timer = new AD80_3_Timer();
			ad80_3Timer.start();
		}
	}
}
