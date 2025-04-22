package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.world.World;

/**
 * 毒蠍的毒液 (可將鑽地後的沙蟲挖起來)
 * 
 * @author terry0412
 */
public class Scorpion_Deadly_Poison extends ItemExecutor {

	/**
	 *
	 */
	private Scorpion_Deadly_Poison() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Scorpion_Deadly_Poison();
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
		// final int spellsc_objid = data[0];
		final int spellsc_x = data[1];
		final int spellsc_y = data[2];

		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		// 刪除道具一個
		pc.getInventory().removeItem(item, 1);

		// 與目標之間存在障礙物
		if (pc.glanceCheck(spellsc_x, spellsc_y) == false) {
			return;
		}

		// 取得變更前的面向
		// final int ori_heading = pc.getHeading();
		// 取得變更後的面向
		final int new_heading = pc.targetDirection(spellsc_x, spellsc_y);

		// 重新設置面向
		pc.setHeading(new_heading);
		// 更新物件面向
		pc.sendPacketsX10(new S_ChangeHeading(pc.getId(), new_heading));

		// 送出封包(動作)
		pc.sendPacketsX10(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Wand));

		// 發送`強力無所遁形術`的特效
		pc.sendPacketsX10(new S_SkillSound(pc.getId(), 3934));

		// 搜尋附近是否存在`Boss-沙蟲`
		// for (final L1Object obj : World.get().
		// getVisibleBoxObjects(pc, ori_heading, 10, 10)) {
		for (final L1Object obj : World.get().getVisibleObjects(pc)) {
			if (obj instanceof L1NpcInstance) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				// `沙蟲`圖檔編號
				if (npc.getGfxId() == 10071) {
					npc.appearOnGround(pc);
					break;
				}
			}
		}
	}
}
