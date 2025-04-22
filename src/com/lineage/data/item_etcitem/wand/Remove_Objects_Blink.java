package com.lineage.data.item_etcitem.wand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 製作可回收怪物或npc的道具
 * 
 * @author dexc
 *
 */
public class Remove_Objects_Blink extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Remove_Objects_Blink.class);

	/**
	 *
	 */
	private Remove_Objects_Blink() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Remove_Objects_Blink();
	}

	/**
	 * 道具物件執行
	 * @param data 封包參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		try {
			if (!pc.isGm()) {
				pc.sendPackets(new S_SystemMessage("你不是管理者！刪除非法道具！"));
				pc.getInventory().removeItem(item, 1);
				return;
			}

			final int targObjId = data[0];

			final L1Object target = World.get().findObject(targObjId);

			if (target instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) target;
				if (!mob.isDead()) {
					mob.setDead(true);
					mob.setStatus(ActionCodes.ACTION_Die);
					mob.setCurrentHpDirect(0);
					mob.deleteMe();
					pc.sendPackets(new S_SystemMessage(mob.getName() + "已被您刪除了。"));
				}
			} else if (target instanceof L1NpcInstance) {
				final L1NpcInstance mob = (L1NpcInstance) target;
				mob.deleteMe();
				pc.sendPackets(new S_SystemMessage(mob.getName() + "已被您刪除了。"));
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
