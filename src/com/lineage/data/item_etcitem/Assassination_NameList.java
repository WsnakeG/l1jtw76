package com.lineage.data.item_etcitem;

import static com.lineage.server.model.skill.L1SkillId.DE_LV30;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.DarkElfLv30_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldNpc;

/**
 * 暗殺名單(古魯丁村)40557<br>
 * 暗殺名單(奇巖村) 40558<br>
 * 暗殺名單(亞丁城鎮)40559<br>
 * 暗殺名單(風木村) 40560<br>
 * 暗殺名單(肯特村) 40561<br>
 * 暗殺名單(海音村) 40562<br>
 * 暗殺名單(燃柳村) 40563<br>
 */
public class Assassination_NameList extends ItemExecutor {

	/**
	 *
	 */
	private Assassination_NameList() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Assassination_NameList();
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
		// 任務已經完成
		if (pc.getQuest().isEnd(DarkElfLv30_1.QUEST.get_id())) {
			return;
		}

		// 不具備倫得施放的古代咒術
		if (!pc.hasSkillEffect(DE_LV30)) {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}

		final int itemId = item.getItemId();
		switch (itemId) {
		case (40557):// 暗殺名單(古魯丁村)
			if ((pc.getX() == 32620) && (pc.getY() == 32641) && (pc.getMapId() == 4)) {
				for (final L1NpcInstance npc : WorldNpc.get().all()) {
					if (npc.getNpcTemplate().get_npcId() == 45883) {
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				L1SpawnUtil.spawn(pc, 45883, 2, 300);

			} else {
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
			}
			break;

		case (40558):// 暗殺名單(奇巖村)
			if ((pc.getX() == 33513) && (pc.getY() == 32890) && (pc.getMapId() == 4)) {
				for (final L1NpcInstance npc : WorldNpc.get().all()) {
					if (npc.getNpcTemplate().get_npcId() == 45889) {
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				L1SpawnUtil.spawn(pc, 45889, 2, 300000);
			} else {
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
			}
			break;

		case (40559):// 暗殺名單(亞丁城鎮)
			if ((pc.getX() == 34215) && (pc.getY() == 33195) && (pc.getMapId() == 4)) {
				for (final L1NpcInstance npc : WorldNpc.get().all()) {
					if (npc.getNpcTemplate().get_npcId() == 45888) {
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				L1SpawnUtil.spawn(pc, 45888, 2, 300000);
			} else {
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
			}
			break;

		case (40560):// 暗殺名單(風木村)
			if ((pc.getX() == 32580) && (pc.getY() == 33260) && (pc.getMapId() == 4)) {
				for (final L1NpcInstance npc : WorldNpc.get().all()) {
					if (npc.getNpcTemplate().get_npcId() == 45886) {
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				L1SpawnUtil.spawn(pc, 45886, 2, 300000);
			} else {
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
			}
			break;

		case (40561):// 暗殺名單(肯特村)
			if ((pc.getX() == 33046) && (pc.getY() == 32806) && (pc.getMapId() == 4)) {
				for (final L1NpcInstance npc : WorldNpc.get().all()) {
					if (npc.getNpcTemplate().get_npcId() == 45885) {
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				L1SpawnUtil.spawn(pc, 45885, 2, 300000);
			} else {
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
			}
			break;

		case (40562):// 暗殺名單(海音村)
			if ((pc.getX() == 33447) && (pc.getY() == 33476) && (pc.getMapId() == 4)) {
				for (final L1NpcInstance npc : WorldNpc.get().all()) {
					if (npc.getNpcTemplate().get_npcId() == 45887) {
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				L1SpawnUtil.spawn(pc, 45887, 2, 300000);
			} else {
				pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			}
			break;

		case (40563):// 暗殺名單(燃柳村)
			if ((pc.getX() == 32730) && (pc.getY() == 32426) && (pc.getMapId() == 4)) {
				for (final L1NpcInstance npc : WorldNpc.get().all()) {
					if (npc.getNpcTemplate().get_npcId() == 45884) {
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				L1SpawnUtil.spawn(pc, 45884, 2, 300000);
			} else {
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
			}
			break;
		}
	}
}
