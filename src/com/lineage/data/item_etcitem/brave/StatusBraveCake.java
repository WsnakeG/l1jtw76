package com.lineage.data.item_etcitem.brave;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Liquor;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 巧克力蛋糕類型<br>
 * classname: StatusBraveCake<br>
 * <br>
 * 設置對象:道具(etcitem)<br>
 * <br>
 * 設置範例:brave.StatusBraveCake 600 8031 127<br>
 * 巧克力蛋糕技能效果時間600秒 動畫8031 全職業可用<br>
 * <br>
 * 職業分類<br>
 * 王族可執行:1<br>
 * 騎士可執行:2<br>
 * 精靈可執行:4<br>
 * 法師可執行:8<br>
 * 黑暗精靈可執行:16<br>
 * 龍騎士可執行:32<br>
 * 幻術師可執行:64<br>
 * 戰士可執行:128<br>
 */
public class StatusBraveCake extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(StatusBraveCake.class);

	/**
	 *
	 */
	private StatusBraveCake() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new StatusBraveCake();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}

		if (L1BuffUtil.stopPotion(pc)) {
			if (check(pc)) {
				if (pc.hasSkillEffect(STATUS_BRAVE3)) {
					pc.killSkillEffectTimer(STATUS_BRAVE3);
				}

				// added by terry0412
				if (!_notConsume) {
					pc.getInventory().removeItem(item, 1);
				}

				// // 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);

				// 巧克力蛋糕效果(速度增加1.15)
				pc.sendPacketsAll(new S_Liquor(pc.getId(), 0x08));

				// 將發生神秘的奇蹟力量。
				pc.sendPackets(new S_ServerMessage(1065));

				final S_SkillSound sound = new S_SkillSound(pc.getId(), _gfxid);
				if (_gfxid > 0) { // 具備動畫
					pc.sendPacketsX8(sound);
				}

				pc.setSkillEffect(STATUS_BRAVE3, _time * 1000);

			} else { // \f1没有任何事情发生。
				pc.sendPackets(new S_ServerMessage(79));
			}
		}
	}

	private int _time = 600;
	private int _gfxid;

	private static final int _int8 = 128;// 戰士可執行:128
	private static final int _int7 = 64;// 幻術師可執行:64
	private static final int _int6 = 32;// 龍騎士可執行:32
	private static final int _int5 = 16;// 黑暗精靈可執行:16
	private static final int _int4 = 8;// 法師可執行:8
	private static final int _int3 = 4;// 精靈可執行:4
	private static final int _int2 = 2;// 騎士可執行:2
	private static final int _int1 = 1;// 王族可執行:1

	private boolean _isCrown;// 王族可執行:1

	private boolean _isKnight;// 騎士可執行:2

	private boolean _isElf;// 精靈可執行:4

	private boolean _isWizard;// 法師可執行:8

	private boolean _isDarkelf;// 黑暗精靈可執行:16

	private boolean _isDragonKnight;// 龍騎士可執行:32

	private boolean _isIllusionist;// 幻術師可執行:64

	private boolean _isWarrior;// 戰士可執行:128

	private boolean _notConsume; // 是否不消耗道具 by terry0412

	/**
	 * 可執行職業判斷
	 * 
	 * @param pc
	 * @return
	 */
	private boolean check(final L1PcInstance pc) {
		try {
			if (pc.isCrown() && _isCrown) {
				return true;
			}
			if (pc.isKnight() && _isKnight) {
				return true;
			}
			if (pc.isElf() && _isElf) {
				return true;
			}
			if (pc.isWizard() && _isWizard) {
				return true;
			}
			if (pc.isDarkelf() && _isDarkelf) {
				return true;
			}
			if (pc.isDragonKnight() && _isDragonKnight) {
				return true;
			}
			if (pc.isIllusionist() && _isIllusionist) {
				return true;
			}
			if (pc.isWarrior() && _isWarrior) {
				return true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 任務可執行職業設置
	 * 
	 * @param questuser
	 */
	private void set_use_type(int use_type) {
		try {
			if (use_type >= _int8) {
				use_type -= _int8;
				_isWarrior = true;
			}
			if (use_type >= _int7) {
				use_type -= _int7;
				_isIllusionist = true;
			}
			if (use_type >= _int6) {
				use_type -= _int6;
				_isDragonKnight = true;
			}
			if (use_type >= _int5) {
				use_type -= _int5;
				_isDarkelf = true;
			}
			if (use_type >= _int4) {
				use_type -= _int4;
				_isWizard = true;
			}
			if (use_type >= _int3) {
				use_type -= _int3;
				_isElf = true;
			}
			if (use_type >= _int2) {
				use_type -= _int2;
				_isKnight = true;
			}
			if (use_type >= _int1) {
				use_type -= _int1;
				_isCrown = true;
			}

			if (use_type > 0) {
				_log.error("StatusBraveCake 可執行職業設定錯誤:餘數大於0");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void set_set(final String[] set) {
		try {
			_time = Integer.parseInt(set[1]);

			if (_time <= 0) {
				_log.error("StatusBraveCake 設置錯誤:技能效果時間小於等於0! 使用預設600秒");
				_time = 600;
			}

		} catch (final Exception e) {
		}
		try {
			_gfxid = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
		try {
			final int user_type = Integer.parseInt(set[3]);
			set_use_type(user_type);

		} catch (final Exception e) {
		}
		// 是否不消耗道具 by terry0412
		try {
			if (set.length > 4) {
				_notConsume = Boolean.parseBoolean(set[4]);
			}

		} catch (final Exception e) {
		}
	}
}