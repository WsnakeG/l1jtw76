package com.lineage.data.item_etcitem.mp;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BLUE_POTION;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 加速魔力回復藥水類型(時效增加量)<BR>
 * classname: UserMprBlue<BR>
 * <BR>
 * 設置對象:道具(etcitem)<BR>
 * <BR>
 * 設置範例:mp.UserMprBlue 600 190<BR>
 * 時效 動畫藍光(動畫設置小於等於0 不顯示動畫)<BR>
 */
public class UserMprBlue extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(UserMprBlue.class);

	/**
	 *
	 */
	private UserMprBlue() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new UserMprBlue();
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

				if (pc.hasSkillEffect(STATUS_BLUE_POTION)) {
					pc.killSkillEffectTimer(STATUS_BLUE_POTION);
				}

				// 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);

				final S_SkillSound sound = new S_SkillSound(pc.getId(), _gfxid);
				if (_gfxid > 0) { // 具備動畫
					pc.sendPacketsX8(sound);
				}

				pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_BLUEPOTION, _time));

				pc.setSkillEffect(STATUS_BLUE_POTION, _time * 1000);

				// 你感覺到魔力恢復速度加快。
				pc.sendPackets(new S_ServerMessage(1007));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _time;
	private int _gfxid;

	@Override
	public void set_set(final String[] set) {
		try {
			_time = Integer.parseInt(set[1]);

			if (_time <= 0) {
				_time = 600;
				_log.error("UserMpr 設置錯誤:時效小於等於0! 使用預設600");
			}

		} catch (final Exception e) {
		}
		try {
			_gfxid = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
	}
}
