package com.lineage.data.item_etcitem.quest;

import java.util.HashMap;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;

/**
 * 消滅之意志 49239
 */
public class CKEWLv50Teleport extends ItemExecutor {

	/**
	 *
	 */
	private CKEWLv50Teleport() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new CKEWLv50Teleport();
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
		if (pc.getMapId() != (short) CKEWLv50_1.MAPID) {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		final HashMap<Integer, L1Object> mapList = new HashMap<Integer, L1Object>();
		mapList.putAll(World.get().getVisibleObjects(CKEWLv50_1.MAPID));

		final int itemid = 49239;
		int i = 0;
		for (final L1Object tgobj : mapList.values()) {
			if (tgobj instanceof L1PcInstance) {
				final L1PcInstance tgpc = (L1PcInstance) tgobj;
				// 相同副本
				if (tgpc.get_showId() == pc.get_showId()) {
					if (tgpc.isCrown()) {// 王族
						i += 1;
					} else if (tgpc.isKnight()) {// 騎士
						i += 2;
					} else if (tgpc.isElf()) {// 精靈
						i += 4;
					} else if (tgpc.isWizard()) {// 法師
						i += 8;
					}
				}
			}
		}

		if (i == CKEWLv50_1.USER) {// 人數足
			for (final L1Object tgobj : mapList.values()) {
				if (tgobj instanceof L1PcInstance) {
					final L1PcInstance tgpc = (L1PcInstance) tgobj;
					// 相同副本
					if (tgpc.get_showId() == pc.get_showId()) {
						final L1ItemInstance reitem = tgpc.getInventory().findItemId(itemid);
						if (reitem != null) {
							// 165：\f1%0%s 強烈的顫抖後消失了。
							tgpc.sendPackets(new S_ServerMessage(165, reitem.getName()));
							tgpc.getInventory().removeItem(reitem);// 移除道具
						}

						if (tgpc.isCrown()) {// 王族
							// 傳送任務執行者
							L1Teleport.teleport(tgpc, 32790, 32773, (short) CKEWLv50_1.MAPID, 2, true);

						} else if (tgpc.isKnight()) {// 騎士
							// 傳送任務執行者
							L1Teleport.teleport(tgpc, 32788, 32773, (short) CKEWLv50_1.MAPID, 2, true);

						} else if (tgpc.isElf()) {// 精靈
							// 傳送任務執行者
							L1Teleport.teleport(tgpc, 32788, 32771, (short) CKEWLv50_1.MAPID, 2, true);

						} else if (tgpc.isWizard()) {// 法師
							// 傳送任務執行者
							L1Teleport.teleport(tgpc, 32788, 32775, (short) CKEWLv50_1.MAPID, 2, true);
						}
					}
				}
			}

		} else {// 人數不足
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			final L1QuestUser quest = WorldQuest.get().get(pc.get_showId());
			quest.endQuest();
		}
		mapList.clear();
	}
}
