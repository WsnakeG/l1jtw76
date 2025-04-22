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
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * HP恢復藥劑類型<BR>
 * classname: UserAddHp<BR>
 * <BR>
 * 設置對象:道具(etcitem)<BR>
 * <BR>
 * 設置範例:hp.UserAddHp 30 50 197 40308 1000<BR>
 * 恢復30~50點體力 特效(設置小於等於0 不顯示) 指定消耗道具(id, count)<BR>
 */
public class UserAddHp extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(UserAddHp.class);

	/**
	 *
	 */
	private UserAddHp() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new UserAddHp();
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
				// 指定消耗道具 by terry0412
				if (_consumeItemId > 0) {
					if (!pc.getInventory().consumeItem(_consumeItemId, _consumeItemCount)) {
						// \f1%0不足%s。
						pc.sendPackets(new S_SystemMessage("所需道具不足"));
						return;
					}

				} else {
					pc.getInventory().removeItem(item, 1);
				}

				// 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);

				final S_SkillSound sound = new S_SkillSound(pc.getId(), _gfxid);
				if (_gfxid > 0) { // 具備動畫
					pc.sendPacketsX8(sound);
				}

				int addhp = _min_hp;

				if (_max_addhp > 0) { // 具備最大值
					addhp += (int) (Math.random() * _max_addhp); // 隨機數字範圍
				}

				if (pc.get_up_hp_potion() > 0) { // 藥水使用HP恢復增加(1/100)
					addhp += ((addhp * pc.get_up_hp_potion()) / 100);
				}
				if (pc.hasSkillEffect(POLLUTE_WATER)) { // 污濁之水
					addhp = (addhp >> 1);
				}
				if (pc.hasSkillEffect(ADLV80_2_2)) { // 污濁的水流(水龍副本 回復量1/2倍)
					addhp = (addhp >> 1);
				}
				if (pc.hasSkillEffect(ADLV80_2_1)) { // 藥水侵蝕術(水龍副本 治療變為傷害)
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

	private int _min_hp;
	private int _max_addhp;
	private int _gfxid;

	// 指定消耗道具 by terry0412
	private int _consumeItemId;
	private int _consumeItemCount;

	@Override
	public void set_set(final String[] set) {
		try {
			_min_hp = Integer.parseInt(set[1]);

			if (_min_hp <= 0) {
				_min_hp = 1;
				_log.error("UserHpr 設置錯誤:最小恢復值小於等於0! 使用預設1");
			}

		} catch (final Exception e) {
		}
		try {
			final int max_hp = Integer.parseInt(set[2]);

			if (max_hp >= _min_hp) {
				_max_addhp = (max_hp - _min_hp) + 1;

			} else {
				_max_addhp = 0;
				_log.error("UserHpr 設置錯誤:最大恢復值小於最小恢復值!(" + _min_hp + " " + max_hp + ")");
			}

		} catch (final Exception e) {
		}
		try {
			_gfxid = Integer.parseInt(set[3]);

		} catch (final Exception e) {
		}
		try {
			_consumeItemId = Integer.parseInt(set[4]);
			_consumeItemCount = Integer.parseInt(set[5]);

		} catch (final Exception e) {
		}
	}
}
