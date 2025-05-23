package com.lineage.server.timecontroller.pc;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_NONE;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_S;
import static com.lineage.server.model.skill.L1SkillId.NATURES_TOUCH;
import static com.lineage.server.model.skill.L1SkillId.STATUS_UNDERWATER_BREATH;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.types.Point;

/**
 * PC HP回復執行
 * 
 * @author dexc
 */
public class HprExecutor {

	private static final Log _log = LogFactory.getLog(HprExecutor.class);

	// 技能 回復HP增加/減少 <技能編號, 影響質>
	private static final Map<Integer, Integer> _skill = new HashMap<Integer, Integer>();

	// 地圖 回復HP增加 <地圖編號, 影響質>
	private static final Map<Integer, Integer> _mapIdU = new HashMap<Integer, Integer>();

	// 地圖 回復HP減少 <地圖編號, 影響質>
	private static final Map<Integer, Integer> _mapIdD = new HashMap<Integer, Integer>();

	private static HprExecutor _instance;

	protected static HprExecutor get() {
		if (_instance == null) {
			_instance = new HprExecutor();
		}
		return _instance;
	}

	private HprExecutor() {
		// 技能回復HP增加
		_skill.put(NATURES_TOUCH, 15);
		_skill.put(COOKING_1_5_N, 3);
		_skill.put(COOKING_1_5_S, 3);
		_skill.put(COOKING_2_4_N, 2);
		_skill.put(COOKING_2_4_S, 2);
		_skill.put(COOKING_3_6_N, 2);
		_skill.put(COOKING_3_6_S, 2);

		// 地圖回復HP增加
		// 旅館
		_mapIdU.put(16384, 5);
		_mapIdU.put(16384, 5);
		_mapIdU.put(16896, 5);
		_mapIdU.put(17408, 5);
		_mapIdU.put(17920, 5);
		_mapIdU.put(18432, 5);
		_mapIdU.put(18944, 5);
		_mapIdU.put(19968, 5);
		_mapIdU.put(19456, 5);
		_mapIdU.put(20480, 5);
		_mapIdU.put(20992, 5);
		_mapIdU.put(21504, 5);
		_mapIdU.put(22016, 5);
		_mapIdU.put(22528, 5);
		_mapIdU.put(23040, 5);
		_mapIdU.put(23552, 5);
		_mapIdU.put(24064, 5);
		_mapIdU.put(24576, 5);
		_mapIdU.put(25088, 5);

		// 城堡
		_mapIdU.put(15, 15);// 肯特內城
		_mapIdU.put(29, 15);// 風木城內城
		_mapIdU.put(52, 15);// 奇岩內城
		_mapIdU.put(64, 15);// 海音城堡
		// this._mapIdU.put(66, 15);// 侏儒洞穴
		_mapIdU.put(300, 15);// 亞丁內城

		// HP減少的MAP(任務MAP)
		_mapIdD.put(410, -10);// 魔族神殿
		_mapIdD.put(CKEWLv50_1.MAPID, -10);// 再生聖殿 1樓/2樓/3樓
		_mapIdD.put(DarkElfLv50_1.MAPID, -10);// 黑暗妖精試煉地監
	}

	/**
	 * PC HP回復執行 判斷
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

			// 人物在降低HP地圖中
			final Integer dhp = _mapIdD.get(new Integer(tgpc.getMapId()));
			if (dhp != null) {
				return true;
			}

			// 海底
			if (isUnderwater(tgpc)) {
				return true;
			}

			// HP已滿
			if (tgpc.getCurrentHp() >= tgpc.getMaxHp()) {
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	protected void checkRegenHp(final L1PcInstance tgpc) {
		try {
			tgpc.set_hpRegenType(tgpc.hpRegenType() + tgpc.getHpRegenState());
			tgpc.setRegenState(REGENSTATE_NONE);

			if (tgpc.isRegenHp()) {
				regenHp(tgpc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static void regenHp(final L1PcInstance tgpc) {
		tgpc.set_hpRegenType(0);
		int maxBonus = 1;

		// 等級大於11
		if (tgpc.getLevel() > 11) {
			if (tgpc.getCon() >= 14) {
				maxBonus = Math.min((tgpc.getCon() - 12), 14);// 取回最小
			}
		}

		int equipHpr = tgpc.getInventory().hpRegenPerTick();
		equipHpr += tgpc.getHpr();

		final Random random = new Random();
		int bonus = random.nextInt(maxBonus) + 1;

		// 技能補正
		if (!tgpc.getSkillisEmpty() && (tgpc.getSkillEffect().size() > 0)) {
			try {
				for (final Integer skillid : _skill.keySet()) {
					if (tgpc.hasSkillEffect(skillid)) {
						final Integer integer = _skill.get(skillid);
						if (integer != null) {
							bonus += integer;
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
			bonus += ConfigOther.HOMEHPR;
		}

		// 地下盟屋
		if (L1HouseLocation.isInHouse(tgpc.getMapId())) {
			bonus += ConfigOther.HOMEHPR;
		}

		final Integer rhp = _mapIdU.get(new Integer(tgpc.getMapId()));
		if (rhp != null) {
			// 地圖影響
			bonus += rhp.intValue();
		}

		// 世界樹
		if (tgpc.isElf()) {
			if (tgpc.getMapId() == 4) {
				if (tgpc.getLocation().isInScreen(new Point(33055, 32336))) {
					bonus += 5;
				}
			}
		}

		// 自訂地圖回血
		if (tgpc.getMapId() == ConfigOther.CUSTOM_MAPID) {
			bonus += ConfigOther.CUSTOM_HPR;
		}

		if (tgpc.getOriginalHpr() > 0) { // オリジナルCON HPR補正
			bonus += tgpc.getOriginalHpr();
		}

		boolean inLifeStream = false;

		// 治癒能量風暴
		if (isPlayerInLifeStream(tgpc)) {
			inLifeStream = true;
			bonus += 3;
		}

		int newHp = tgpc.getCurrentHp();
		newHp += (bonus + equipHpr);

		newHp = Math.max(newHp, 1);// 取回最大(修正裝備減少HP)

		// 海底
		if (isUnderwater(tgpc)) {
			newHp -= 20;
		}

		// 人物在降低HP地圖中
		final Integer dhp = _mapIdD.get(new Integer(tgpc.getMapId()));
		if (dhp != null) {
			if (!inLifeStream) {
				// 地圖影響
				bonus += dhp.intValue();
			}
		}

		newHp = Math.max(newHp, 0);

		tgpc.setCurrentHp(newHp);
	}

	private static boolean isUnderwater(final L1PcInstance pc) {
		if (pc.getInventory().checkEquipped(20207)) {// 深水長靴
			return false;
		}
		if (pc.hasSkillEffect(STATUS_UNDERWATER_BREATH)) {// 伊娃的祝福藥水效果
			return false;
		}
		if (pc.getInventory().checkEquipped(21048)// 修好的戒指
				&& pc.getInventory().checkEquipped(21049)// 修好的耳環
				&& pc.getInventory().checkEquipped(21050)// 修好的項鏈
		) {
			return false;
		}

		return pc.getMap().isUnderwater();
	}

	/**
	 * 法師技能(治癒能量風暴)
	 * 
	 * @param pc PC
	 * @return true PC在4格範圍內
	 */
	private static boolean isPlayerInLifeStream(final L1PcInstance pc) {
		for (final L1Object object : pc.getKnownObjects()) {
			if ((object instanceof L1EffectInstance) == false) {
				continue;
			}
			final L1EffectInstance effect = (L1EffectInstance) object;
			// 法師技能(治癒能量風暴)
			if (effect.getNpcId() == 81169) {
				if (effect.getLocation().getTileLineDistance(pc.getLocation()) < 4) {
					return true;
				}
			}
		}
		return false;
	}
}
