package com.lineage.data.item_etcitem.teleport;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharBookReading;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1BookMark;

/**
 * 魔法卷軸(指定傳送)40863<br>
 * 瞬间移动卷轴 40100 瞬间移动卷轴（祝福）140100
 */
public class Move_Reel extends ItemExecutor {

	/**
	 *
	 */
	private Move_Reel() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Move_Reel();
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
		// 記憶座標點排序
		final int btele = data[0];
		// 所在地圖編號
		// int mapID = data[1];

		// 所在位置 是否允許傳送
		final boolean isTeleport = pc.getMap().isTeleportable();
		if (!isTeleport) {
			// 647 這附近的能量影響到瞬間移動。在此地無法使用瞬間移動。
			pc.sendPackets(new S_ServerMessage(647));
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

		} else {
			final L1BookMark bookm = CharBookReading.get().getBookMark(pc, btele);

			// 取出記憶座標
			if (bookm != null) {
				// 刪除道具
				L1Teleport.teleport(pc, bookm.getLocX(), bookm.getLocY(), bookm.getMapId(), 5, true);
				pc.getInventory().removeItem(item, 1);

			} else {
				// 刪除道具
				L1Teleport.randomTeleport(pc, true);
				pc.getInventory().removeItem(item, 1);
			}
			// 絕對屏障解除
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
				pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
				pc.startHpRegeneration();
				pc.startMpRegeneration();
			}
			// pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,
			// false));
		}
	}
}