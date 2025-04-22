package com.lineage.server.datatables;

import static com.lineage.server.ActionCodes.ACTION_AltAttack;
import static com.lineage.server.ActionCodes.ACTION_Attack;
import static com.lineage.server.ActionCodes.ACTION_AxeAttack;
import static com.lineage.server.ActionCodes.ACTION_AxeWalk;
import static com.lineage.server.ActionCodes.ACTION_BowAttack;
import static com.lineage.server.ActionCodes.ACTION_BowWalk;
import static com.lineage.server.ActionCodes.ACTION_ChainswordAttack;
import static com.lineage.server.ActionCodes.ACTION_ChainswordWalk;
import static com.lineage.server.ActionCodes.ACTION_ClawAttack;
import static com.lineage.server.ActionCodes.ACTION_ClawWalk;
import static com.lineage.server.ActionCodes.ACTION_DaggerAttack;
import static com.lineage.server.ActionCodes.ACTION_DaggerWalk;
import static com.lineage.server.ActionCodes.ACTION_Damage;
import static com.lineage.server.ActionCodes.ACTION_EdoryuAttack;
import static com.lineage.server.ActionCodes.ACTION_EdoryuWalk;
import static com.lineage.server.ActionCodes.ACTION_SkillAttack;
import static com.lineage.server.ActionCodes.ACTION_SkillBuff;
import static com.lineage.server.ActionCodes.ACTION_SpearAttack;
import static com.lineage.server.ActionCodes.ACTION_SpearWalk;
import static com.lineage.server.ActionCodes.ACTION_StaffAttack;
import static com.lineage.server.ActionCodes.ACTION_StaffWalk;
import static com.lineage.server.ActionCodes.ACTION_SwordAttack;
import static com.lineage.server.ActionCodes.ACTION_SwordWalk;
import static com.lineage.server.ActionCodes.ACTION_ThrowingKnifeAttack;
import static com.lineage.server.ActionCodes.ACTION_ThrowingKnifeWalk;
import static com.lineage.server.ActionCodes.ACTION_TwoHandSwordAttack;
import static com.lineage.server.ActionCodes.ACTION_TwoHandSwordWalk;
import static com.lineage.server.ActionCodes.ACTION_Walk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Command;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 圖形影格資料
 * 
 * @author dexc
 */
public class SprTable {

	private static final Log _log = LogFactory.getLog(SprTable.class);

	private static class Spr {
		private final Map<Integer, Integer> _moveSpeed = new HashMap<Integer, Integer>();

		private final Map<Integer, Integer> _attackSpeed = new HashMap<Integer, Integer>();

		private final Map<Integer, int[]> _frame = new HashMap<Integer, int[]>();

		private int _nodirSpellSpeed = 0;

		private int _dirSpellSpeed = 0;

		private int _dirSpellSpeed30 = 0;

		private int _dmg = 0;
	}

	private static final Map<Integer, Spr> _dataMap = new HashMap<Integer, Spr>();

	private static SprTable _instance;

	public static SprTable get() {
		if (_instance == null) {
			_instance = new SprTable();
		}
		return _instance;
	}

	/**
	 * spr_action
	 */
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Spr spr = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `spr_action`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int key = rs.getInt("spr_id");
				if (!_dataMap.containsKey(key)) {
					spr = new Spr();
					_dataMap.put(key, spr);
				} else {
					spr = _dataMap.get(key);
				}

				final int actid = rs.getInt("act_id");
				int frameCount = rs.getInt("framecount");
				if (frameCount < 0) {
					frameCount = 0;
				}
				int frameRate = rs.getInt("framerate");
				if (frameRate < 0) {
					frameRate = 0;
				}
				final int speed = calcActionSpeed(frameCount, frameRate);
				final int[] frame = { frameCount, frameRate };

				switch (actid) {
				case ACTION_Walk:
				case ACTION_SwordWalk:
				case ACTION_AxeWalk:
				case ACTION_BowWalk:
				case ACTION_SpearWalk:
				case ACTION_StaffWalk:
				case ACTION_DaggerWalk:
				case ACTION_TwoHandSwordWalk:
				case ACTION_EdoryuWalk:
				case ACTION_ClawWalk:
				case ACTION_ThrowingKnifeWalk:
				case ACTION_ChainswordWalk:
					spr._moveSpeed.put(actid, speed);
					break;

				case ACTION_Damage:
					spr._dmg = speed;
					break;

				case ACTION_SkillAttack:
					spr._dirSpellSpeed = speed;
					break;

				case ACTION_SkillBuff:
					spr._nodirSpellSpeed = speed;
					break;

				case ACTION_AltAttack:
					spr._dirSpellSpeed30 = speed;
					break;

				case ACTION_Attack:
				case ACTION_SwordAttack:
				case ACTION_AxeAttack:
				case ACTION_BowAttack:
				case ACTION_SpearAttack:
				case ACTION_StaffAttack:
				case ACTION_DaggerAttack:
				case ACTION_TwoHandSwordAttack:
				case ACTION_EdoryuAttack:
				case ACTION_ClawAttack:
				case ACTION_ThrowingKnifeAttack:
				case ACTION_ChainswordAttack:
					spr._attackSpeed.put(actid, speed);
					spr._frame.put(actid, frame);
					break;

				default:
					break;
				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入圖形影格資料數量: " + _dataMap.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * フレーム数とフレームレートからアクションの合計時間(ms)を計算して返す。
	 */
	private int calcActionSpeed(final int frameCount, final int frameRate) {
		return (int) (frameCount * 40 * (24D / frameRate));
	}

	public final boolean containsTripleArrowSpr(final int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid)._attackSpeed.containsKey(ACTION_BowAttack);
		}
		return false;
	}

	public Collection<String> getspr() {
		final ArrayList<String> list = new ArrayList<String>();
		for (final L1Command command : CommandsTable.get().getList()) {
			list.add(command.getName() + ": " + command.get_note());
		}
		return list;
	}

	/**
	 * 傳回攻擊速度
	 * 
	 * @param sprid
	 * @param actid
	 * @return 指定されたsprの攻撃速度(ms)
	 */
	public int getAttackSpeed(final int sprid, final int actid) {
		if (_dataMap.containsKey(sprid)) {
			if (_dataMap.get(sprid)._attackSpeed.containsKey(actid)) {
				return _dataMap.get(sprid)._attackSpeed.get(actid);

			} else if (actid == ACTION_Attack) {
				return 0;

			} else {
				return _dataMap.get(sprid)._attackSpeed.get(ACTION_Attack);
			}
		}
		return 0;
	}

	/**
	 * 傳回移動速度
	 * 
	 * @param sprid
	 * @param actid
	 * @return
	 */
	public int getMoveSpeed(final int sprid, final int actid) {
		if (_dataMap.containsKey(sprid)) {
			if (_dataMap.get(sprid)._moveSpeed.containsKey(actid)) {
				return _dataMap.get(sprid)._moveSpeed.get(actid);

			} else if (actid == ACTION_Walk) {
				return 0;

			} else {
				return _dataMap.get(sprid)._moveSpeed.get(ACTION_Walk);
			}
		}
		return 0;
	}

	/**
	 * 有方向技能速度
	 * 
	 * @param sprid
	 * @return
	 */
	public int getDirSpellSpeed(final int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid)._dirSpellSpeed;
		}
		return 0;
	}

	/**
	 * 無方向技能速度
	 * 
	 * @param sprid
	 * @return
	 */
	public int getNodirSpellSpeed(final int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid)._nodirSpellSpeed;
		}
		return 0;
	}

	/**
	 * NPC30動作技能速度
	 * 
	 * @param sprid
	 * @return
	 */
	public int getDirSpellSpeed30(final int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid)._dirSpellSpeed30;
		}
		return 0;
	}

	/**
	 * 受傷動作速度
	 * 
	 * @param sprid
	 * @return
	 */
	public int getDmg(final int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid)._dmg;
		}
		return 0;
	}

	public long spr_move_speed(final int tempCharGfx) {
		return 200;
	}

	public long spr_attack_speed(final int tempCharGfx) {
		return 200;
	}

	public long spr_skill_speed(final int tempCharGfx) {
		return 200;
	}

	public final boolean containsChainswordSpr(final int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid)._moveSpeed.containsKey(ACTION_ChainswordWalk);
		}
		return false;
	}

	/**
	 * 取回結構
	 * 
	 * @param sprid
	 * @param actid
	 * @return
	 */
	public int[] getFrame(final int sprid, final int actid) {
		if (_dataMap.containsKey(sprid)) {
			if (_dataMap.get(sprid)._attackSpeed.containsKey(actid)) {
				return _dataMap.get(sprid)._frame.get(actid);
			} else if (actid == ACTION_Attack) {
				return null;
			} else {
				return _dataMap.get(sprid)._frame.get(ACTION_Attack);
			}
		}
		return null;
	}
}
