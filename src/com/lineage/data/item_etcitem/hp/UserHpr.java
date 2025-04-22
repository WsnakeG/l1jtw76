package com.lineage.data.item_etcitem.hp;

import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_1;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_2;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * HP恢復藥劑類型(時效) classname: UserHpr 設置對象:道具(etcitem) 設置範例:hp.UserHpr 30 60 197
 * 恢復30點體力 60秒內 動畫白光(動畫設置小於等於0 不顯示動畫) FIXME 未完成
 */
public class UserHpr extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(UserHpr.class);

	/**
	 *
	 */
	private UserHpr() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new UserHpr();
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
		try {
			// 例外狀況:物件為空
			if (item == null) {
				return;
			}
			// 例外狀況:人物為空
			if (pc == null) {
				return;
			}
			if (L1BuffUtil.stopPotion(pc)) {
				pc.getInventory().removeItem(item, 1);

				// 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);

				final S_SkillSound sound = new S_SkillSound(pc.getId(), _gfxid);
				if (_gfxid > 0) {// 具備動畫
					pc.sendPacketsX8(sound);
				}

				int addhp = _min_hp;

				if (_max_addhp > 0) {// 具備最大質
					addhp += (int) (Math.random() * _max_addhp);// 隨機數字範圍
				}

				if (pc.get_up_hp_potion() > 0) {// 藥水使用HP恢復增加(1/100)
					addhp += ((addhp * pc.get_up_hp_potion()) / 100);
				}
				if (pc.hasSkillEffect(POLLUTE_WATER)) {// 污濁之水
					addhp = (addhp >> 1);
				}
				if (pc.hasSkillEffect(ADLV80_2_2)) {// 污濁的水流(水龍副本 回復量1/2倍)
					addhp = (addhp >> 1);
				}
				if (pc.hasSkillEffect(ADLV80_2_1)) {// 藥水侵蝕術(水龍副本 治療變為傷害)
					addhp *= -1;
				}
				if (addhp > 0) {
					// 你覺得舒服多了訊息
					pc.sendPackets(new S_ServerMessage(77));
				}
				pc.setCurrentHp(pc.getCurrentHp() + addhp);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _min_hp = 1;
	private int _max_addhp = 0;
	private int _gfxid = 0;

	@Override
	public void set_set(final String[] set) {
		try {
			_min_hp = Integer.parseInt(set[1]);

			if (_min_hp <= 0) {
				_log.error("UserHpr 設置錯誤:最小恢復質小於等於0! 使用預設1");
				_min_hp = 1;
			}

		} catch (final Exception e) {
		}
		try {
			final int max_hp = Integer.parseInt(set[2]);

			if (max_hp >= _min_hp) {
				_max_addhp = (max_hp - _min_hp) + 1;

			} else {
				_log.error("UserHpr 設置錯誤:最大恢復質小於最小恢復質!(" + _min_hp + " " + max_hp + ")");
				_max_addhp = 0;
			}

		} catch (final Exception e) {
		}
		try {
			_gfxid = Integer.parseInt(set[3]);

		} catch (final Exception e) {
		}
	}
}
