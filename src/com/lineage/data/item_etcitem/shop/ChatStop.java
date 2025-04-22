package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 44162 禁言卡 UPDATE `etcitem` SET `use_type`='spell_buff',`max_charge_count`='0'
 * WHERE `item_id`='44162';#
 * 
 * @author dexc
 */
public class ChatStop extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(ChatStop.class);

	/**
	 *
	 */
	private ChatStop() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ChatStop();
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
		try {
			// 對象OBJID
			final int targObjId = data[0];
			final L1Object object = World.get().findObject(targObjId);
			if (object != null) {
				if (object instanceof L1PcInstance) {
					final L1PcInstance target = (L1PcInstance) object;

					if (target.isGm()) {
						pc.sendPackets(new S_ServerMessage(166, "你不能對GM使用禁言卡"));

					} else {
						if (!target.hasSkillEffect(L1SkillId.CHAT_STOP)) {
							// 刪除物件
							pc.getInventory().removeItem(item, 1);
							pc.sendPacketsX8(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Wand));

							target.setSkillEffect(L1SkillId.CHAT_STOP, 60 * 1000);

							target.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, 60));
							target.sendPackets(new S_ServerMessage(287, String.valueOf(1)));
							target.sendPackets(new S_ServerMessage(166, pc.getName() + "對你施展禁言卡"));
							pc.sendPackets(new S_ServerMessage(166, "對" + target.getName() + "施展禁言卡"));

						} else {
							pc.sendPackets(new S_ServerMessage(166, target.getName() + "已經在禁言狀態"));
						}
					}

				} else {
					// 79 沒有任何事情發生
					pc.sendPackets(new S_ServerMessage(79));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
