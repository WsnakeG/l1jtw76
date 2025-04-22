package com.lineage.data.item_etcitem.mp;

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
 * MP恢復藥劑類型<BR>
 * classname: UserAddMp<BR>
 * <BR>
 * 設置對象:道具(etcitem)<BR>
 * <BR>
 * 設置範例:mp.UserAddMp 7 12 190 40308 1000<BR>
 * 恢復7~12點魔力 特效(設置小於等於0 不顯示) 指定消耗道具(id, count)<BR>
 */
public class UserAddMp extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(UserAddMp.class);

	/**
	 *
	 */
	private UserAddMp() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new UserAddMp();
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

				int addmp = _min_mp;

				if (_max_addmp > 0) { // 具備最大值
					addmp += (int) (Math.random() * _max_addmp); // 隨機數字範圍
				}

				if (addmp > 0) {
					// 你的 %0%s 漸漸恢復。 ($1084 = 魔力)
					pc.sendPackets(new S_ServerMessage(338, "$1084"));
				}
				pc.setCurrentMp(pc.getCurrentMp() + addmp);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _min_mp;
	private int _max_addmp;
	private int _gfxid;

	// 指定消耗道具 by terry0412
	private int _consumeItemId;
	private int _consumeItemCount;

	@Override
	public void set_set(final String[] set) {
		try {
			_min_mp = Integer.parseInt(set[1]);

			if (_min_mp <= 0) {
				_min_mp = 1;
				_log.error("UserMpr 設置錯誤:最小恢復值小於等於0! 使用預設1");
			}

		} catch (final Exception e) {
		}
		try {
			final int max_hp = Integer.parseInt(set[2]);

			if (max_hp >= _min_mp) {
				_max_addmp = (max_hp - _min_mp) + 1;

			} else {
				_max_addmp = 0;
				_log.error("UserMpr 設置錯誤:最大恢復值小於最小恢復值!(" + _min_mp + " " + max_hp + ")");
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
