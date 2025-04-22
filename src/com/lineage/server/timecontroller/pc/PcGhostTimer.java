package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * PC 鬼魂模式處理 時間軸
 * 
 * @author dexc
 */
public class PcGhostTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(PcGhostTimer.class);

	private ScheduledFuture<?> _timer;

	protected static final Random _random = new Random();

	public void start() {
		final int timeMillis = 1100;// 1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> all = World.get().getAllPlayers();
			// 不包含元素
			if (all.isEmpty()) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				if (tgpc.isDead()) {
					continue;
				}

				if (tgpc.hasSkillEffect(L1SkillId.C3_WIND)) {// 風傷術
					wind_dmg(tgpc);

				} else if (tgpc.hasSkillEffect(L1SkillId.C3_EARTH)) {// 地氣術
					earth_dmg(tgpc);
				}

				// 非鬼魂狀態
				if (!tgpc.isGhost()) {
					continue;
				}

				int time = tgpc.get_ghostTime();
				time--;
				check(tgpc, time);
				Thread.sleep(1);
			}

		} catch (final Exception e) {
			_log.error("PC 鬼魂模式處理時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final PcGhostTimer pcHprTimer = new PcGhostTimer();
			pcHprTimer.start();
		}
	}

	private void earth_dmg(final L1PcInstance pc) {
		try {
			int damage = c3_power_dmg(pc, 4);
			for (final L1Object tgobj : World.get().getVisibleObjects(pc, 1)) {
				if (tgobj instanceof L1PcInstance) {
					final L1PcInstance tgpc = (L1PcInstance) tgobj;
					if (tgpc.isDead()) {
						continue;
					}
					// 排除同盟
					if (tgpc.getClanid() == pc.getClanid()) {
						if (tgpc.getClanid() != 0) {
							continue;
						}
					}
					// 排除安全區
					if (tgpc.getMap().isSafetyZone(tgpc.getLocation())) {
						continue;
					}
					final int resist = tgpc.getEarth();
					// 傷害增減
					if (resist > 0) {// 抵抗
						damage = c3_power_dmg_down(damage, Math.min(100, resist));

					} else if (resist < 0) {// 懼怕
						damage = c3_power_dmg_up(damage, Math.min(0, resist));
					}
					tgpc.receiveDamage(pc, damage, false, true);// 物理傷害
					// 受傷動作
					tgpc.sendPacketsX8(new S_DoActionGFX(tgpc.getId(), ActionCodes.ACTION_Damage));
				}

				if (tgobj instanceof L1MonsterInstance) {
					final L1MonsterInstance tgmob = (L1MonsterInstance) tgobj;
					if (tgmob.isDead()) {
						continue;
					}
					tgmob.receiveDamage(pc, damage, 1);// 地傷害
					// 受傷動作
					tgmob.broadcastPacketX8(new S_DoActionGFX(tgmob.getId(), ActionCodes.ACTION_Damage));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void wind_dmg(final L1PcInstance pc) {
		try {
			int damage = c3_power_dmg(pc, 3);
			for (final L1Object tgobj : World.get().getVisibleObjects(pc, 1)) {
				if (tgobj instanceof L1PcInstance) {
					final L1PcInstance tgpc = (L1PcInstance) tgobj;
					if (tgpc.isDead()) {
						continue;
					}
					// 排除同盟
					if (tgpc.getClanid() == pc.getClanid()) {
						if (tgpc.getClanid() != 0) {
							continue;
						}
					}
					// 排除安全區
					if (tgpc.getMap().isSafetyZone(tgpc.getLocation())) {
						continue;
					}
					final int resist = tgpc.getWind();
					// 傷害增減
					if (resist > 0) {// 抵抗
						damage = c3_power_dmg_down(damage, Math.min(100, resist));

					} else if (resist < 0) {// 懼怕
						damage = c3_power_dmg_up(damage, Math.min(0, resist));
					}
					tgpc.receiveDamage(pc, damage, false, true);// 物理傷害
					// 受傷動作
					tgpc.sendPacketsX8(new S_DoActionGFX(tgpc.getId(), ActionCodes.ACTION_Damage));
				}

				if (tgobj instanceof L1MonsterInstance) {
					final L1MonsterInstance tgmob = (L1MonsterInstance) tgobj;
					if (tgmob.isDead()) {
						continue;
					}
					tgmob.receiveDamage(pc, damage, 8);// 風傷害
					// 受傷動作
					tgmob.broadcastPacketX8(new S_DoActionGFX(tgmob.getId(), ActionCodes.ACTION_Damage));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 抵抗
	 * 
	 * @param resist
	 * @return
	 */
	private int c3_power_dmg_down(final int damage, final int resist) {
		final int r = 100 - resist;
		final int dmg = (damage * r) / 100;
		return Math.max(5, dmg);
	}

	/**
	 * 懼怕
	 * 
	 * @param resist
	 * @return
	 */
	private int c3_power_dmg_up(final int damage, final int resist) {
		final int dmg = damage - ((damage * resist) / 100);
		return Math.abs(dmg);
	}

	private int c3_power_dmg(final L1PcInstance pc, final int type) {
		int damage = 0;
		final int level = pc.getLevel();
		switch (type) {
		case 3:// 風傷術
			if ((level >= 50) && (level < 54)) {
				damage = random_dmg(20, 60);

			} else if ((level >= 55) && (level < 59)) {
				damage = random_dmg(40, 120);

			} else if ((level >= 60) && (level < 64)) {
				damage = random_dmg(60, 180);

			} else if ((level >= 65) && (level < 69)) {
				damage = random_dmg(80, 240);

			} else if ((level >= 70) && (level < 74)) {
				damage = random_dmg(100, 320);

			} else if ((level >= 75) && (level < 79)) {
				damage = random_dmg(120, 380);

			} else if ((level >= 80) && (level < 89)) {
				damage = random_dmg(140, 440);

			} else if ((level >= 90) && (level < 99)) {
				damage = random_dmg(160, 500);

			} else {
				damage = random_dmg(180, 550);
			}
			break;

		case 4:// 地氣術
			if ((level >= 50) && (level < 54)) {
				damage = random_dmg(20, 60);

			} else if ((level >= 55) && (level < 59)) {
				damage = random_dmg(40, 120);

			} else if ((level >= 60) && (level < 64)) {
				damage = random_dmg(60, 180);

			} else if ((level >= 65) && (level < 69)) {
				damage = random_dmg(80, 240);

			} else if ((level >= 70) && (level < 74)) {
				damage = random_dmg(100, 320);

			} else if ((level >= 75) && (level < 79)) {
				damage = random_dmg(120, 380);

			} else if ((level >= 80) && (level < 89)) {
				damage = random_dmg(140, 440);

			} else if ((level >= 90) && (level < 99)) {
				damage = random_dmg(160, 500);

			} else {
				damage = random_dmg(180, 550);
			}
			break;
		}
		return damage;
	}

	private int random_dmg(final int i, final int j) {
		return _random.nextInt(j - i) + i;
	}

	/**
	 * 檢查鬼魂模式時間
	 * 
	 * @param tgpc
	 * @param time
	 */
	private static void check(final L1PcInstance tgpc, final Integer time) {
		if (time > 0) {
			tgpc.set_ghostTime(time);
		} else {
			// 時間到
			tgpc.set_ghostTime(-1);

			// 未斷線移除狀態
			if (tgpc.getNetConnection() != null) {
				outPc(tgpc);
			}
		}
	}

	/**
	 * 離開鬼魂模式(傳送回出發點)
	 * 
	 * @param tgpc
	 */
	private static void outPc(final L1PcInstance tgpc) {
		try {
			if (tgpc != null) {
				tgpc.makeReadyEndGhost();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
