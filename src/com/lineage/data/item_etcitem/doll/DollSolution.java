package com.lineage.data.item_etcitem.doll;

import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.ItemPowerUpdateTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1ItemPowerUpdate;

/**
 * 60219 魔法娃娃成長藥劑 EX: classname DollSolution 12319 12335 (e-失敗/s-成功) UPDATE
 * `etcitem` SET `classname`='doll.DollSolution',`use_type`='choice_doll' WHERE
 * `item_id`='60219';#魔法娃娃成長藥劑
 */
public class DollSolution extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(DollSolution.class);

	private static final Random _random = new Random();

	/**
	 *
	 */
	private DollSolution() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new DollSolution();
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
		try {
			// 對象OBJID
			final int targObjId = data[0];

			final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

			if (tgItem == null) {
				return;
			}

			// 取回物件屬性
			final String key = tgItem.getItemId() + "/" + item.getItemId();
			final L1ItemPowerUpdate info = ItemPowerUpdateTable.get().get(key);
			if (info == null) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}

			final L1Doll doll = DollPowerTable.get().get_type(tgItem.getItemId());
			if (doll == null) {
				// 2,477：只有魔法娃娃可以選擇。
				pc.sendPackets(new S_ServerMessage(2477));
				return;
			}

			if (pc.getDoll(tgItem.getId()) != null) {
				// 1,181：這個魔法娃娃目前正在使用中。
				pc.sendPackets(new S_ServerMessage(1181));
				return;
			}

			if (info.get_mode() == 4) {// 不能再強化
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}

			if (info.get_nedid() != item.getItemId()) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}

			// 同組物品清單
			final Map<Integer, L1ItemPowerUpdate> tmplist = ItemPowerUpdateTable.get().get_type_id(key);
			if (tmplist.isEmpty()) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}

			final int order_id = info.get_order_id();// 排序
			final L1ItemPowerUpdate tginfo = tmplist.get(order_id + 1);// 取回下一個排序資料
			if (tginfo == null) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}

			// 刪除卷軸
			pc.getInventory().removeItem(item, 1);

			if (_random.nextInt(1000) <= tginfo.get_random()) {
				// 強化成功
				pc.getInventory().removeItem(tgItem, 1);

				// 產生新物件
				final L1ItemInstance tginfo_item = ItemTable.get().createItem(tginfo.get_itemid());
				if (tginfo_item != null) {
					tginfo_item.setIdentified(true);
					tginfo_item.setCount(1);
					pc.getInventory().storeItem(tginfo_item);
					pc.sendPackets(new S_ServerMessage("\\aE獲得展新的 " + tginfo_item.getName()));
					S_SkillSound sound = new S_SkillSound(pc.getId(), _gfxid_s);
					pc.sendPacketsX8(sound);
					return;

				} else {
					_log.error("給予物件失敗 原因: 指定編號物品不存在(" + tginfo.get_itemid() + ")");
					return;
				}
			} else {
				// 強化失敗
				switch (info.get_mode()) {// 目前物品失敗時處理類型
				case 0:
					// \f1%0%s %2 产生激烈的 %1 光芒，但是没有任何事情发生。
					pc.sendPackets(new S_ServerMessage(160, tgItem.getLogName(), "$252", "$248"));
					S_SkillSound sound1 = new S_SkillSound(pc.getId(), _gfxid_e);
					pc.sendPacketsX8(sound1);
					break;

				case 1:// 1:強化失敗會倒退
					final L1ItemPowerUpdate ole1 = tmplist.get(order_id - 1);// 取回上一個排序資料
					pc.sendPackets(new S_ServerMessage("\\fR" + tgItem.getName() + "\\aG升級失敗!"));
					pc.getInventory().removeItem(tgItem, 1);
					S_SkillSound sound2 = new S_SkillSound(pc.getId(), _gfxid_e);
					pc.sendPacketsX8(sound2);
					CreateNewItem.createNewItem(pc, ole1.get_itemid(), 1);
					break;

				case 2:// 2:強化失敗會消失
						// 164 \f1%0%s 产生激烈的 %1 光芒，一会儿后就消失了。銀色的
					pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
					pc.getInventory().removeItem(tgItem, 1);
					S_SkillSound sound3 = new S_SkillSound(pc.getId(), _gfxid_e);
					pc.sendPacketsX8(sound3);
					break;

				case 3:// 強化失敗會倒退 或 強化失敗會消失
					if (_random.nextBoolean()) {// 強化失敗會倒退
						final L1ItemPowerUpdate ole2 = tmplist.get(order_id - 1);// 取回上一個排序資料
						pc.getInventory().removeItem(tgItem, 1);
						S_SkillSound sound4 = new S_SkillSound(pc.getId(), _gfxid_e);
						pc.sendPacketsX8(sound4);
						CreateNewItem.createNewItem(pc, ole2.get_itemid(), 1);

					} else {// 強化失敗會消失
						// 164 \f1%0%s 产生激烈的 %1 光芒，一会儿后就消失了。銀色的
						pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
						pc.getInventory().removeItem(tgItem, 1);
						S_SkillSound sound5 = new S_SkillSound(pc.getId(), _gfxid_e);
						pc.sendPacketsX8(sound5);
					}
					break;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _gfxid_e; // 失敗特效編號
	private int _gfxid_s; // 成功特效編號

	@Override
	public void set_set(String[] set) {
		try {
			_gfxid_e = Integer.parseInt(set[1]);
			_gfxid_s = Integer.parseInt(set[2]);

		} catch (Exception e) {
		}
	}
}