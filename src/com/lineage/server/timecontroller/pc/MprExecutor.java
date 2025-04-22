package com.lineage.server.timecontroller.pc;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_NONE;
import static com.lineage.server.model.skill.L1SkillId.CONCENTRATION;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_2_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_5_S;
import static com.lineage.server.model.skill.L1SkillId.MEDITATION;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BLUE_POTION;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.types.Point;

/**
 * PC MP回復執行
 * 
 * @author dexc
 */
public class MprExecutor {

	private static final Log _log = LogFactory.getLog(MprExecutor.class);

	// 技能 回復MP增加/減少 <技能編號, 影響質>
	private static final Map<Integer, Integer> _skill = new HashMap<Integer, Integer>();

	// 地圖 回復MP增加/減少 <地圖編號, 影響質>
	private static final Map<Short, Integer> _mapId = new HashMap<Short, Integer>();

	private static MprExecutor _instance;

	protected static MprExecutor get() {
		if (_instance == null) {
			_instance = new MprExecutor();
		}
		return _instance;
	}

	private MprExecutor() {
		// _skill.put(MEDITATION, 5);
		_skill.put(CONCENTRATION, 2);
		_skill.put(COOKING_1_2_N, 3);
		_skill.put(COOKING_1_2_S, 3);
		_skill.put(COOKING_2_4_N, 2);
		_skill.put(COOKING_2_4_S, 2);
		_skill.put(COOKING_3_5_N, 2);
		_skill.put(COOKING_3_5_S, 2);
		// _skill.put(STATUS_BLUE_POTION, 8);

		// 旅館
		_mapId.put((short) 16384, 3);
		_mapId.put((short) 16384, 3);
		_mapId.put((short) 16896, 3);
		_mapId.put((short) 17408, 3);
		_mapId.put((short) 17920, 3);
		_mapId.put((short) 18432, 3);
		_mapId.put((short) 18944, 3);
		_mapId.put((short) 19968, 3);
		_mapId.put((short) 19456, 3);
		_mapId.put((short) 20480, 3);
		_mapId.put((short) 20992, 3);
		_mapId.put((short) 21504, 3);
		_mapId.put((short) 22016, 3);
		_mapId.put((short) 22528, 3);
		_mapId.put((short) 23040, 3);
		_mapId.put((short) 23552, 3);
		_mapId.put((short) 24064, 3);
		_mapId.put((short) 24576, 3);
		_mapId.put((short) 25088, 3);
		// 城堡
		_mapId.put((short) 15, 10); // 肯特內城
		_mapId.put((short) 29, 10); // 風木城內城
		_mapId.put((short) 52, 10); // 奇岩內城
		_mapId.put((short) 64, 10); // 海音城堡
		// this._mapId.put(66, 10); // 侏儒洞穴
		_mapId.put((short) 300, 10); // 亞丁內城
	}

	/**
	 * PC MP回復執行 判斷
	 * 
	 * @param tgpc
	 * @return true:執行 false:不執行
	 */
	protected boolean check(final L1PcInstance tgpc) {
		try {
			// 人物為空
			if (tgpc == null) {
				return false;
			}
			// 人物登出
			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}
			// 中斷連線
			if (tgpc.getNetConnection() == null) {
				return false;
			}
			// 死亡
			if (tgpc.isDead()) {
				return false;
			}
			// 傳送狀態
			if (tgpc.isTeleport()) {
				return false;
			}
			// MP已滿
			if (tgpc.getCurrentMp() >= tgpc.getMaxMp()) {
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	protected void checkRegenMp(final L1PcInstance tgpc) {
		try {
			tgpc.set_mpRegenType(tgpc.mpRegenType() + tgpc.getMpRegenState());
			tgpc.setRegenState(REGENSTATE_NONE);

			if (tgpc.isRegenMp()) {
				regenMp(tgpc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static void regenMp(final L1PcInstance tgpc) {
		tgpc.set_mpRegenType(0);

		if (tgpc.getMapId() == 201) {// 法師試煉地監
			// 魔力不會自動回復
			return;
		}
		int baseMpr = 1;

		// 精神補正
		switch (tgpc.getWis()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
			baseMpr = 1;
			break;

		case 15:
		case 16:
			baseMpr = 2;
			break;

		default:
			baseMpr = 3;
			break;
		}

		// 技能補正
		if (!tgpc.getSkillisEmpty() && (tgpc.getSkillEffect().size() > 0)) {
			try {
				for (final Integer key : tgpc.getSkillEffect()) {
					if (key.equals(MEDITATION)) { // 冥想術 by terry0412
						// 時效期間不能移動、戰鬥、施展魔法，
						// 也不能被敵人攻擊，否則效果會消失。
						if (tgpc.getHpRegenState() != REGENSTATE_NONE) {
							// 冥想術解除
							tgpc.killSkillEffectTimer(MEDITATION);
							continue;
						}

						// 取得剩餘秒數
						final int sec = tgpc.getSkillEffectTimeSec(MEDITATION);

						// 未飲用「加速魔力回復藥水」：魔力回復量+5。
						if (!tgpc.hasSkillEffect(STATUS_BLUE_POTION)) {
							baseMpr += 5;

						} else {
							// 有飲用「加速魔力回復藥水」
							// 魔力回復量+5(+2xN)，倍數N的上限為8。
							// 會隨著時間+5、+7(5+2)、+9(5+2x2)~+21(5+2x8)。

							// 冥想術起始640秒, 每段區隔秒數為32秒
							baseMpr += 5 + (2 * Math.min((640 - sec) / 32, 8));
						}
					}

					if (key.equals(STATUS_BLUE_POTION)) { // 魔力回復藥水
						// 額外回魔量 = 精神 - 10 (修正 by terry0412)
						baseMpr += Math.max(tgpc.getWis() - 10, 1);

					} else {
						final Integer integer = _skill.get(key);
						if (integer != null) {
							baseMpr += integer.intValue();
						}
					}
				}

			} catch (final ConcurrentModificationException e) {
				// 技能取回發生其他線程進行修改
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		// 血盟小屋
		if (L1HouseLocation.isInHouse(tgpc.getX(), tgpc.getY(), tgpc.getMapId())) {
			baseMpr += ConfigOther.HOMEMPR;
		}

		// 地下盟屋
		if (L1HouseLocation.isInHouse(tgpc.getMapId())) {
			baseMpr += ConfigOther.HOMEMPR;
		}

		final Integer rmp = _mapId.get(tgpc.getMapId());
		if (rmp != null) {
			baseMpr += rmp.intValue();
		}

		// 世界樹
		if (tgpc.isElf()) {
			if (tgpc.getMapId() == 4) {
				if (tgpc.getLocation().isInScreen(new Point(33055, 32336))) {
					baseMpr += 3;
				}
			}
		}

		// 自訂地圖回魔
		if (tgpc.getMapId() == ConfigOther.CUSTOM_MAPID) {
			baseMpr += ConfigOther.CUSTOM_MPR;
		}

		if (tgpc.getOriginalMpr() > 0) { // オリジナルWIS MPR補正
			baseMpr += tgpc.getOriginalMpr();
		}

		int itemMpr = tgpc.getInventory().mpRegenPerTick();
		itemMpr += tgpc.getMpr();

		final int mpr = baseMpr + itemMpr;
		int newMp = tgpc.getCurrentMp() + mpr;

		newMp = Math.max(newMp, 0);

		tgpc.setCurrentMp(newMp);
	}
}
