package com.lineage.server.model;

//import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.LOWER_FLOOR_GREATER_BATTLE_SCROLL;
import static com.lineage.server.model.skill.L1SkillId.LOWER_FLOOR_GREATER_DEFENSE_SCROLL;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_SkillSound;
//import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.Teleportation;

/**
 * 傳送控制項
 * 
 * @author dexc
 */
public class L1Teleport {

	private static final Log _log = LogFactory.getLog(L1Teleport.class);

	// テレポートスキルの種類
	public static final int TELEPORT = 0;

	public static final int CHANGE_POSITION = 1;

	public static final int ADVANCED_MASS_TELEPORT = 2;

	public static final int CALL_CLAN = 3;

	// 順番にteleport(白), change position e(青), ad mass teleport e(赤), call clan(緑)
	public static final int[] EFFECT_SPR = { 169, 2235, 2236, 2281 };

	public static final int[] EFFECT_TIME = { 280, 440, 440, 1120 };

	private L1Teleport() {
	}

	/**
	 * 傳送控制項
	 * 
	 * @param pc
	 *            執行的人物
	 * @param loc
	 *            座標
	 * @param head
	 *            面向
	 * @param effectable
	 *            是否產生動畫
	 */
	public static void teleport(final L1PcInstance pc, final L1Location loc, final int head, final boolean effectable) {
		teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), head, effectable, TELEPORT);
	}

	/**
	 * 傳送控制項
	 * 
	 * @param pc
	 *            執行的人物
	 * @param loc
	 *            座標
	 * @param head
	 *            面向
	 * @param effectable
	 *            是否產生動畫
	 * @param skillType
	 *            動畫的光 (0:白),(1:青),(2:赤),(3:緑)
	 */
	public static void teleport(final L1PcInstance pc, final L1Location loc, final int head, final boolean effectable, final int skillType) {
		teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), head, effectable, skillType);
	}

	/**
	 * 傳送控制項
	 * 
	 * @param pc
	 *            執行的人物
	 * @param x
	 *            X座標
	 * @param y
	 *            Y座標
	 * @param mapId
	 *            地圖編號
	 * @param head
	 *            面向
	 * @param effectable
	 *            是否產生動畫
	 */
	public static void teleport(final L1PcInstance pc, final int x, final int y, final short mapid, final int head, final boolean effectable) {
		teleport(pc, x, y, mapid, head, effectable, TELEPORT);
	}

	/**
	 * 傳送控制項
	 * 
	 * @param pc
	 *            執行的人物
	 * @param x
	 *            X座標
	 * @param y
	 *            Y座標
	 * @param mapId
	 *            地圖編號
	 * @param head
	 *            面向
	 * @param effectable
	 *            是否產生動畫
	 * @param skillType
	 *            動畫的光 (0:白),(1:青),(2:赤),(3:緑)
	 */
	public static void teleport(final L1PcInstance pc, final int x, final int y, final short mapId, final int head, final boolean effectable, final int skillType) {
		// 保存寵物目前模式
		pc.setPetModel();

		// 動畫顯示
		if (effectable && ((skillType >= 0) && (skillType <= EFFECT_SPR.length))) {
			// 改變顯示(復原正常)
			// pc.sendPackets(new S_ChangeName(pc, false));
			// 傳送動畫
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), EFFECT_SPR[skillType]));

			try {
				Thread.sleep((int) (EFFECT_TIME[skillType] * 0.7));

			} catch (final Exception e) {
			}
		}

		pc.setTeleportX(x);
		pc.setTeleportY(y);
		pc.setTeleportMapId(mapId);
		pc.setTeleportHeading(head);
//		pc.sendPackets(pc.s_Teleport);
		Teleportation.teleportation(pc);
	}
	
	/*
	 * targetキャラクターのdistanceで指定したマス分前にテレポートする。指定されたマスがマップでない場合何もしない。
	 */
	public static void teleportToTargetFront(final L1Character cha, final L1Character target, final int distance) {
		int locX = target.getX();
		int locY = target.getY();
		final int heading = target.getHeading();
		final L1Map map = target.getMap();
		final short mapId = target.getMapId();

		// ターゲットの向きからテレポート先の座標を決める。
		switch (heading) {
		case 1:
			locX += distance;
			locY -= distance;
			break;

		case 2:
			locX += distance;
			break;

		case 3:
			locX += distance;
			locY += distance;
			break;

		case 4:
			locY += distance;
			break;

		case 5:
			locX -= distance;
			locY += distance;
			break;

		case 6:
			locX -= distance;
			break;

		case 7:
			locX -= distance;
			locY -= distance;
			break;

		case 0:
			locY -= distance;
			break;

		default:
			break;

		}

		if (map.isPassable(locX, locY, null)) {
			if (cha instanceof L1PcInstance) {
				teleport((L1PcInstance) cha, locX, locY, mapId, cha.getHeading(), true);

			} else if (cha instanceof L1NpcInstance) {
				((L1NpcInstance) cha).teleport(locX, locY, cha.getHeading());
			}
		}
	}

	/**
	 * 隨機執行移動
	 * 
	 * @param pc
	 * @param effectable
	 */
	public static void randomTeleport(final L1PcInstance pc, final boolean effectable) {
		try {
			// まだ本サーバのランテレ処理と違うところが結構あるような・・・
			final L1Location newLocation = pc.getLocation().randomLocation(200, true);
			final int newX = newLocation.getX();
			final int newY = newLocation.getY();
			final short mapId = (short) newLocation.getMapId();

			L1Teleport.teleport(pc, newX, newY, mapId, 5, effectable);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
