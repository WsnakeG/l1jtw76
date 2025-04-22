package com.lineage.data.item_etcitem.doll;

import java.util.Iterator;
import java.util.List;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.WorldWar;

/**
 * (煉金)魔法娃娃
 * 
 * @author simlin
 */
public class Magic_Doll extends ItemExecutor {

	/**
	 *
	 */
	private Magic_Doll() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Magic_Doll();
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
		final int itemId = item.getItemId();
		final int itemobj = item.getId();

		if (pc.getDoll(itemobj) != null) {
			// 娃娃收回
			pc.getDoll(itemobj).deleteDoll();
			if (item.isInTimer()) {
				item.stopEquipmentTimer(pc);
			}
			return;
		}

		if (pc.getDolls().size() >= ConfigAlt.MAX_DOLL_COUNT) {
			pc.sendPackets(new S_ServerMessage(319));
			return;
		}

		if (!pc.getDolls().isEmpty()) {
			for (final Iterator<L1DollInstance> iter = pc.getDolls().values().iterator(); iter.hasNext();) {
				final L1DollInstance doll = iter.next();
				// for (L1DollInstance doll : pc.getDolls().values()) {
				if (pc.getInventory().getItem(doll.getItemObjId()).getItemId() == itemId) {
					pc.sendPackets(new S_ServerMessage("\\fY不能攜帶相同的娃娃"));
					return;
				}
			}
		}

		if (!ConfigOther.WAR_DOLL) {
			// 戰爭
			if (pc.getClan() != null) {
				boolean inWar = false;
				if (pc.getClan().getCastleId() != 0) {
					if (ServerWarExecutor.get().isNowWar(pc.getClan().getCastleId())) { // 戦争時間内
						inWar = true;
					}

				} else {
					final List<L1War> warList = WorldWar.get().getWarList(); // 全部戰爭清單
					for (final Iterator<L1War> iter = warList.iterator(); iter.hasNext();) {
						final L1War war = iter.next();
						if (war.checkClanInWar(pc.getClan().getClanName())) { // 戰爭中
							inWar = true;
							break;
						}
					}
				}

				if (inWar) {
					// 1531：加入血盟中或戰鬥中時，無法進行召喚魔法娃娃。
					pc.sendPackets(new S_ServerMessage(1531));
					return;
				}
			}
		}

		boolean iserror = false;
		final L1Doll type = DollPowerTable.get().get_type(itemId);
		if (type != null) {
			if (type.get_need() != null) {
				final int[] itemids = type.get_need();
				final int[] counts = type.get_counts();

				for (int i = 0; i < itemids.length; i++) {
					if (!pc.getInventory().checkItem(itemids[i], counts[i])) {
						final L1Item temp = ItemTable.get().getTemplate(itemids[i]);
						pc.sendPackets(new S_ServerMessage(337, temp.getNameId()));
						iserror = true;
					}
				}

				if (!iserror) {
					for (int i = 0; i < itemids.length; i++) {
						pc.getInventory().consumeItem(itemids[i], counts[i]);
					}
				}
			}

			if (!iserror) {
				if (item.getItem().getMaxUseTime() > 0) {
					if (item.getRemainingTime() <= 0) {
						pc.sendPackets(new S_ServerMessage(2788));
						return;
					}
					item.startEquipmentTimer(pc);
				}
				final L1Npc template = NpcTable.get().getTemplate(71082);
				new L1DollInstance(template, pc, itemobj, type, false);
			}
		}
	}
}
