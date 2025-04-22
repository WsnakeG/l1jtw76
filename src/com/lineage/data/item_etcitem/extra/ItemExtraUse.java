package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.CharacterEtcItemTable;
import com.lineage.server.datatables.ServerEtcItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ServerEtcItem;
import com.lineage.william.EtcItemForChar;

/**
 * 道具強化系統 ItemExtraUse <BR>
 * 
 * @author Roy
 */
public class ItemExtraUse extends ItemExecutor {

	/**
	 *
	 */
	private ItemExtraUse() {
	}

	public static ItemExecutor get() {
		return new ItemExtraUse();
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

		final int itemid = item.getItem().getItemId();

		if (ServerEtcItemTable.get().getItem(itemid) != null) {
			final L1ServerEtcItem etcitem = ServerEtcItemTable.get().getItem(itemid);
			final int gif = ServerEtcItemTable.get().getItem(itemid).gif;

			if (etcitem.itemtime > 0) {
				if (!pc.hasSkillEffect(10000000 + etcitem.itemid)) {

					if (etcitem.deleteafteruse) {
						pc.getInventory().consumeItem(itemid, 1);
					}

					pc.setSkillEffect(10000000 + etcitem.itemid, etcitem.itemtime * 1000);

					CharacterEtcItemTable.get().Add(pc, etcitem.itemid, etcitem.itemname, 0);

					pc.sendPackets(new S_SystemMessage(
							"\\aH賦予效果：" + etcitem.itemname + "，效果時間：" + etcitem.itemtime + "秒。"));

					final S_SkillSound sound = new S_SkillSound(pc.getId(), gif);
					pc.sendPacketsX8(sound);

					EtcItemForChar.get(pc, etcitem).giveEffect();
				} else {
					pc.sendPackets(new S_SystemMessage("\\aD已經具有 " + etcitem.itemname + " 了。"));
				}
			} else {
				if (etcitem.deleteafteruse) {
					pc.getInventory().consumeItem(itemid, 1);
				}

				CharacterEtcItemTable.get().Add(pc, etcitem.itemid, etcitem.itemname, 1);

				pc.sendPackets(new S_SystemMessage("\\aD使用 " + etcitem.itemname));

				final S_SkillSound sound = new S_SkillSound(pc.getId(), gif);
				pc.sendPacketsX8(sound);

				EtcItemForChar.get(pc, etcitem).giveEffect();
			}
			return;
		}
	}
}
