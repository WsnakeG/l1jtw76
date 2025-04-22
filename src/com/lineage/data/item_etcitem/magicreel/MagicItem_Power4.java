package com.lineage.data.item_etcitem.magicreel;

import java.sql.Timestamp;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * [特殊技能道具] 施放後集體聖結界同畫面同血盟人員、 扣除道具、數量、特效編號、 使用道具不會消失開關
 * 
 * @author terry0412
 */
public class MagicItem_Power4 extends ItemExecutor {

	/**
	 *
	 */
	private MagicItem_Power4() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new MagicItem_Power4();
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
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}

		if (_consumeItemId != 0 && _consumeItemCount != 0
				&& !pc.getInventory().consumeItem(_consumeItemId, _consumeItemCount)) {
			pc.sendPackets(new S_SystemMessage("啟動媒介不足"));
			return;
		}

		// 設置延遲使用機制
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		item.setLastUsed(ts);
		pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
		pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);

		if (_isRemovable) {
			pc.getInventory().removeItem(item, 1);
		}

		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		// 送出特效封包
		pc.sendPacketsAll(new S_SkillSound(pc.getId(), _gfxId));

		pc.sendPackets(new S_SystemMessage("集體聖結界同畫面組隊與血盟人員"));

		// 聖結界
		final int skillid = L1SkillId.IMMUNE_TO_HARM;

		// 先對自己施放
		new L1SkillUse().handleCommands(pc, skillid, pc.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);

		final L1Party party = pc.getParty();

		// 對同畫面玩家檢查並施放
		for (L1PcInstance find_pc : World.get().getRecognizePlayer(pc)) {
			// 組隊與血盟人員
			if ((party != null && party.isMember(find_pc))
					|| (pc.getClanid() != 0 && pc.getClanid() == find_pc.getClanid())) {
				final L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(find_pc, skillid, find_pc.getId(), 0, 0, 0,
						L1SkillUse.TYPE_SPELLSC);
			}
		}
	}

	private int _consumeItemId;
	private int _consumeItemCount;

	private int _gfxId;

	private boolean _isRemovable;

	@Override
	public void set_set(String[] set) {
		try {
			_consumeItemId = Integer.parseInt(set[1]);
			_consumeItemCount = Integer.parseInt(set[2]);

			_gfxId = Integer.parseInt(set[3]);

			_isRemovable = Boolean.parseBoolean(set[4]);

		} catch (Exception e) {
		}
	}
}
