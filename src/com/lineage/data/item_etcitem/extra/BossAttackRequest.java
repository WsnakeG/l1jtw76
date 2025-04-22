package com.lineage.data.item_etcitem.extra;

import static com.lineage.server.model.skill.L1SkillId.POTION_OF_PURIFICATION;

import java.util.ArrayList;
import java.util.List;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;

/**
 * 淨化藥水 [bossIds] [timeSecs] [gfxId] 可對特定BOSS造成傷害的道具
 * 
 * @author terry0412
 */
public class BossAttackRequest extends ItemExecutor {

	/**
	 *
	 */
	private BossAttackRequest() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new BossAttackRequest();
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
		if ((pc == null) || (item == null)) {
			return;
		}

		// 已經有淨化魔物狀態
		if (pc.hasSkillEffect(POTION_OF_PURIFICATION)) {
			pc.sendPackets(new S_SystemMessage("你無法加持更多淨化魔物的力量。"));
			return;
		}

		// 刪除道具一個
		pc.getInventory().removeItem(item, 1);

		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		// 送出特效封包
		pc.sendPacketsAll(new S_SkillSound(pc.getId(), _gfxId));

		// 訊息提示
		final StringBuilder sbr = new StringBuilder();
		sbr.append("獲得淨化魔物的力量，允許對");

		for (final int bossId : _bossIds) {
			final L1Npc npc = NpcTable.get().getTemplate(bossId);
			if (npc != null) {
				sbr.append("[").append(npc.get_nameid()).append("]");
			}
		}
		sbr.append("造成傷害 (持續").append(_timeSecs).append("秒)");

		// 發送訊息封包
		pc.sendPackets(new S_SystemMessage(sbr.toString()));

		// 設置允許攻擊BOSS列表
		pc.set_allow_list(_bossIds);

		// 持續時間 (單位: 秒)
		pc.setSkillEffect(POTION_OF_PURIFICATION, _timeSecs * 1000);
	}

	private List<Integer> _bossIds;

	private int _timeSecs;

	private int _gfxId;

	@Override
	public void set_set(final String[] set) {
		try {
			final String[] str_list = set[1].split(",");

			final int size = str_list.length;

			// 配置記憶體空間
			_bossIds = new ArrayList<Integer>(size);

			// 搜尋元素
			for (final String str : str_list) {
				final int bossId = Integer.parseInt(str);
				_bossIds.add(bossId);

				final L1Npc npc = NpcTable.get().getTemplate(bossId);
				if ((npc != null) && !npc.is_attack_request()) {
					npc.set_attack_request(true);
				}
			}

			_timeSecs = Integer.parseInt(set[2]);
			_gfxId = Integer.parseInt(set[3]);

		} catch (final Exception e) {
		}
	}
}
