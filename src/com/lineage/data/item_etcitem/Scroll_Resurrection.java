package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1TowerInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * <font color=#00800>復活卷軸</font><BR>
 * Scroll of Resurrection<BR>
 * <font color=#00800>復活卷軸(祝福)</font><BR>
 * Scroll of Resurrection<BR>
 * <font color=#00800>復活與永生之誓約書</font><BR>
 * Contract of Resurrection and Eternal Life<BR>
 * 
 * @author dexc
 */
public class Scroll_Resurrection extends ItemExecutor {

	/**
	 *
	 */
	private Scroll_Resurrection() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Scroll_Resurrection();
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
		// 對象OBJID
		final int targObjId = data[0];

		// 取得目標資料
		final L1Character target = (L1Character) World.get().findObject(targObjId);

		// 例外狀況:物件為空
		if (target == null) {
			return;
		}
		// 例外狀況:物件是自己
		if (target.getId() == pc.getId()) {
			return;
		}
		// 例外狀況:物件沒有死亡
		if ((target.getCurrentHp() > 0) && !target.isDead()) {
			return;
		}

		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 目標是死亡的
		if (target.isDead()) {
			if (target instanceof L1PcInstance) {
				final L1PcInstance targetPc = (L1PcInstance) target;

				if (World.get().getVisiblePlayer(targetPc, 0).size() > 0) {
					for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(targetPc, 0)) {
						if (!visiblePc.isDead()) {
							// 592 復活失敗，因為這個位置已被佔據
							pc.sendPackets(new S_ServerMessage(592));
							return;
						}
					}
				}

				if (pc.getMap().isUseResurrection()) {
					targetPc.setTempID(pc.getId());
					if (item.getItem().getBless() != 0) {
						// 321 是否要復活？ (Y/N)
						targetPc.sendPackets(new S_Message_YN(321));
					} else {
						// 322 是否要復活？ (Y/N)
						targetPc.sendPackets(new S_Message_YN(322));
					}
				} else {
					return;
				}

			} else if (target instanceof L1NpcInstance) {
				if (!(target instanceof L1TowerInstance)) {
					final L1NpcInstance npc = (L1NpcInstance) target;
					// 不允許復活
					if (npc.getNpcTemplate().isCantResurrect()) {
						return;
					}

					if ((npc instanceof L1PetInstance) && (World.get().getVisiblePlayer(npc, 0).size() > 0)) {
						for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(npc, 0)) {
							if (!visiblePc.isDead()) {
								// 592 復活失敗，因為這個位置已被佔據
								pc.sendPackets(new S_ServerMessage(592));
								return;
							}
						}
					}
					npc.resurrect(npc.getMaxHp() / 4);
					npc.setResurrect(true);
					npc.setDead(false);
				}
			}
		}
	}
}
