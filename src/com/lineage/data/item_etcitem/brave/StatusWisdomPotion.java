package com.lineage.data.item_etcitem.brave;

import static com.lineage.server.model.skill.L1SkillId.STATUS_WISDOM_POTION;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_PacketBoxWisdomPotion;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 慎重藥水類型<br>
 * classname: StatusWisdomPotion<br>
 * <br>
 * 設置對象:道具(etcitem)<br>
 * <br>
 * 設置範例:brave.StatusWisdomPotion 300 750 8<br>
 * 慎重藥水技能效果時間300秒 動畫750 法師可用<br>
 * <br>
 * 職業分類<br>
 * 王族可執行:1<br>
 * 騎士可執行:2<br>
 * 精靈可執行:4<br>
 * 法師可執行:8<br>
 * 黑暗精靈可執行:16<br>
 * 龍騎士可執行:32<br>
 * 幻術師可執行:64<br>
 */
public class StatusWisdomPotion extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(StatusWisdomPotion.class);

	/**
	 *
	 */
	private StatusWisdomPotion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new StatusWisdomPotion();
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
				pc.getInventory().removeItem(item, 1);

				// // 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);

				if (!pc.hasSkillEffect(STATUS_WISDOM_POTION)) {
					pc.addSp(2);
				}
				if (_gfxid > 0) {// 具備動畫
					pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));
				}

				pc.sendPackets(new S_PacketBoxWisdomPotion(_time));
				pc.setSkillEffect(STATUS_WISDOM_POTION, _time * 1000);

			} else { // \f1没有任何事情发生。
				pc.sendPackets(new S_ServerMessage(79));
			}
		}
	}

	private int _time = 300;
	private int _gfxid = 0;

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
				_log.error("StatusBrave 可執行職業設定錯誤:餘數大於0");
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
				_log.error("StatusWisdomPotion 設置錯誤:技能效果時間小於等於0! 使用預設300秒");
				_time = 300;
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
	}
}