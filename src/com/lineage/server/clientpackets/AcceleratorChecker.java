package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;
import static com.lineage.server.model.skill.L1SkillId.STATUS_RIBRAVE;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.EnumMap;

import com.lineage.config.ConfigOther;
import com.lineage.server.datatables.LogAcceleratorTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 加速器檢測
 */
public class AcceleratorChecker {

	private final L1PcInstance _pc;

	private int move_injusticeCount;

	private int move_justiceCount;

	private static final int INJUSTICE_COUNT_LIMIT = ConfigOther.INJUSTICE_COUNT;

	private static final int JUSTICE_COUNT_LIMIT = ConfigOther.JUSTICE_COUNT;

	// 加大的允許範圍質
	private static double CHECK_STRICTNESS = (ConfigOther.CHECK_STRICTNESS - 5) / 100D;

	private static double CHECK_MOVESTRICTNESS = (ConfigOther.CHECK_MOVE_STRICTNESS - 5) / 100D;

	private static final double HASTE_RATE = 0.745; // 速度 * 1.33

	private static final double WAFFLE_RATE = 0.874; // 速度 * 1.15

	// private final HashMap<Integer, Long> _actTimers = new HashMap<Integer,
	// Long>();

	private int moveresult = R_OK;

	private long movenow = 0;

	private long moveinterval = 0;

	private int moverightInterval = 0;

	private int attackresult = R_OK;

	private long attacknow = 0;

	private long attackinterval = 0;

	private int attackrightInterval = 0;

	private final EnumMap<ACT_TYPE, Long> _actTimers = new EnumMap<ACT_TYPE, Long>(ACT_TYPE.class);

	private final EnumMap<ACT_TYPE, Long> _checkTimers = new EnumMap<ACT_TYPE, Long>(ACT_TYPE.class);

	public static enum ACT_TYPE {
		MOVE, ATTACK, SPELL_DIR, SPELL_NODIR
	}

	public static final int R_OK = 0;

	public static final int R_DETECTED = 1;// 紀錄

	public static final int R_DISPOSED = 2;// 執行

	public AcceleratorChecker(final L1PcInstance pc) {
		_pc = pc;
		move_injusticeCount = 0;
		move_justiceCount = 0;
		final long now = System.currentTimeMillis();
		for (final ACT_TYPE each : ACT_TYPE.values()) {
			_actTimers.put(each, now);
			_checkTimers.put(each, now);
		}
	}

	/**
	 * 斷開用戶
	 */
	public static void Setspeed() {
		CHECK_STRICTNESS = (ConfigOther.CHECK_STRICTNESS - 5) / 100D;
		CHECK_MOVESTRICTNESS = (ConfigOther.CHECK_MOVE_STRICTNESS - 5) / 100D;
	}

	/**
	 * 檢查是否行動是不合法間隔、做合適處理。
	 * 
	 * @param type - 檢查行動方式
	 * @return 沒有問題0、非法場合1、不正動作達到一定回數切斷玩家連線2。
	 */
	public int checkInterval(final ACT_TYPE type) {

		switch (type) {
		case MOVE:
			movenow = System.currentTimeMillis();
			moveinterval = movenow - _actTimers.get(type);
			moverightInterval = getRightInterval(type);
			moveinterval *= CHECK_MOVESTRICTNESS;
			if ((0 < moveinterval) && (moveinterval < moverightInterval)) {
				move_injusticeCount++;
				move_justiceCount = 0;
				if (move_injusticeCount >= INJUSTICE_COUNT_LIMIT) {
					doPunishment();
					moveresult = R_DISPOSED;
				} else {
					moveresult = R_DETECTED;
				}
			} else if (moveinterval >= moverightInterval) {
				move_justiceCount++;
				if (move_justiceCount >= JUSTICE_COUNT_LIMIT) {
					move_injusticeCount = 0;
					move_justiceCount = 0;
				}
				moveresult = R_OK;
			}
			_actTimers.put(type, movenow);
			return moveresult;
		default:
			attacknow = System.currentTimeMillis();
			attackinterval = attacknow - _actTimers.get(type);

			attackrightInterval = getRightInterval(type);
			attackinterval *= CHECK_STRICTNESS;
			if ((0 < attackinterval) && (attackinterval < attackrightInterval)) {
				attackresult = R_DISPOSED;
			} else if (attackinterval >= attackrightInterval) {
				attackresult = R_OK;
			} else {
				attackresult = R_DISPOSED;
			}
			_actTimers.put(type, attacknow);
			return attackresult;
		}
	}

	/**
	 * 加速器檢測的處罰
	 * 
	 * @param punishmaent
	 */
	private void doPunishment() {
		final int punishment_type = Math.abs(ConfigOther.PUNISHMENT_TYPE);
		final int punishment_time = Math.abs(ConfigOther.PUNISHMENT_TIME);
		final int punishment_mapid = Math.abs(ConfigOther.PUNISHMENT_MAP_ID);

		if (!_pc.isGm()) {// 一般檢測的處罰
			final int x = _pc.getX(), y = _pc.getY(), mapid = _pc.getMapId();// 座標
			switch (punishment_type) {
			case 0:// 強制斷線
				_pc.sendPackets(new S_SystemMessage("\\aG加速器檢測警告" + punishment_time + "秒後強制驅離。"));
				try {
					Thread.sleep(punishment_time * 1000);
				} catch (final Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
				_pc.sendPackets(new S_Disconnect());
				break;
			case 1:// 行動停止
				_pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
				_pc.sendPackets(new S_SystemMessage("\\aG加速器檢測警告" + punishment_time + "秒後解除您的行動。"));
				try {
					Thread.sleep(punishment_time * 1000);
				} catch (final Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
				_pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
				break;
			case 2:// 指定MAPへ転送（隔離）
				L1Teleport.teleport(_pc, 32698, 32857, (short) punishment_mapid, 5, false);
				_pc.sendPackets(new S_SystemMessage("\\aG加速器檢測警告" + punishment_time + "秒後傳送到地獄。"));
				try {
					Thread.sleep(punishment_time * 1000);
				} catch (final Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
				L1Teleport.teleport(_pc, x, y, (short) mapid, 5, false);
				break;
			case 3:
				final int[] Head = { 0, 1, 2, 3, 4, 5, 6, 7 };
				final int[] X = { x, x - 1, x - 1, x - 1, x, x + 1, x + 1, x + 1 };
				final int[] Y = { y + 1, y + 1, y, y - 1, y - 1, y - 1, y, y + 1 };
				for (int i = 0; i < Head.length; i++) {
					if (_pc.getHeading() == Head[i]) {
						L1Teleport.teleport(_pc, X[i], Y[i], (short) mapid, _pc.getHeading(), false);
						_pc.sendPackets(new S_SystemMessage("\\aG加速器檢測。"));
					}
					try {
						Thread.sleep(punishment_time * 1000);
					} catch (final Exception e) {
						System.out.println(e.getLocalizedMessage());
					}
				}
				break;
			}
		} else {
			// 遊戲管理員在遊戲中使用加速器檢測
			if (ConfigOther.DEBUG_MODE) {
				_pc.sendPackets(new S_SystemMessage("\\aD遊戲管理員在遊戲中使用加速器檢測中。"));
				move_injusticeCount = 0;

			}
		}
		if (ConfigOther.LOGGING_ACCELERATOR) {
			final LogAcceleratorTable logaccelerator = new LogAcceleratorTable();
			logaccelerator.storeLogAccelerator(_pc);
		}
	}

	/**
	 * 返回PC指定類型的狀態行動正確的時間間隔(ms)計算。
	 * 
	 * @param type - 動作種類
	 * @param _pc - 檢查PC
	 * @return 正確的時間間隔（ms）
	 */
	private int getRightInterval(final ACT_TYPE type) {
		int interval;

		// 動作判斷
		switch (type) {
		case ATTACK:
			interval = SprTable.get().getAttackSpeed(_pc.getTempCharGfx(), _pc.getCurrentWeapon() + 1);
			break;
		case MOVE:
			interval = SprTable.get().getMoveSpeed(_pc.getTempCharGfx(), _pc.getCurrentWeapon());
			break;
		case SPELL_DIR:
			interval = SprTable.get().getDirSpellSpeed(_pc.getTempCharGfx());
			break;
		case SPELL_NODIR:
			interval = SprTable.get().getNodirSpellSpeed(_pc.getTempCharGfx());
			break;
		default:
			return 0;
		}

		// 一段加速
		switch (_pc.getMoveSpeed()) {
		case 1: // 加速術
			interval *= HASTE_RATE;
			break;
		case 2: // 緩速術
			interval /= HASTE_RATE;
			break;
		default:
			break;
		}

		// 二段加速
		switch (_pc.getBraveSpeed()) {
		case 1: // 勇水
			interval *= HASTE_RATE; // 攻速、移速 * 1.33倍
			break;
		case 3: // 精靈餅乾 / 人物速度 x1.15(2段加速)
			interval *= WAFFLE_RATE; // 移速 * 1.15倍
			break;
		case 4: // 神聖疾走、風之疾走、行走加速 (移速 * 1.33倍)
			if (type.equals(ACT_TYPE.MOVE)) {
				interval *= HASTE_RATE;
			}
			break;
		case 6: // 血之渴望(攻速、走速* 1.33倍)
			if (type.equals(ACT_TYPE.ATTACK) && _pc.isFastAttackable()) {
				// 血之渴望 1.33
				interval *= HASTE_RATE * WAFFLE_RATE;
			}
			break;
		default:
			break;
		}

		// 生命之樹果實 / 移速 * 1.15倍
		if (_pc.hasSkillEffect(STATUS_RIBRAVE) && type.equals(ACT_TYPE.MOVE)) {
			interval *= WAFFLE_RATE;
		}
		// 三段加速 STATUS_BRAVE3 / 巧克力蛋糕
		if (_pc.hasSkillEffect(STATUS_BRAVE3)) { // 攻速、移速 * 1.15倍
			interval *= WAFFLE_RATE;
		}
		// 風之枷鎖 攻速or施法速度 /2倍
		if (_pc.hasSkillEffect(WIND_SHACKLE) && !type.equals(ACT_TYPE.MOVE)) {
			interval /= 2;
		}
		// 寵物競速例外
		if (_pc.getMapId() == 5143) {
			interval *= 0.1;
		}
		return interval;
	}
}