package com.lineage.data.item_etcitem.shop;

import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemPowerUpdateTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemPowerHoleReading;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.datatables.lock.CharWeaponTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1ItemPowerUpdate;
import com.lineage.server.datatables.sql.CharWeaponTimeTable;
/**
 * desc & Roy 2014/08/22 (調整強化失敗會顯示特效) 強化升級石類型 EX: classname Power_Up_01 12319
 * 12335 (P-成功/U-失敗)
 */
public class Power_Up_01 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Power_Up_01.class);

	private static final Random _random = new Random();

	/**
	 *
	 */
	private Power_Up_01() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Power_Up_01();
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
				int steps = CharWeaponTimeTable.get_steps(tgItem.getId());
				pc.getInventory().removeItem(tgItem, 1);

				// CreateNewItem.createNewItem(pc, tginfo.get_itemid(), 1);

				// 產生新物件
				final L1ItemInstance tginfo_item = ItemTable.get().createItem(tginfo.get_itemid());
				if (tginfo_item != null) {
					tginfo_item.setIdentified(true);
					tginfo_item.setCount(1);

					// 保留數值
					tginfo_item.setEnchantLevel(tgItem.getEnchantLevel());
					tginfo_item.setIdentified(tgItem.isIdentified());
					tginfo_item.set_durability(tgItem.get_durability());
					tginfo_item.setChargeCount(tgItem.getChargeCount());
					tginfo_item.setRemainingTime(tgItem.getRemainingTime());
					tginfo_item.setLastUsed(tgItem.getLastUsed());
					tginfo_item.setBless(tgItem.getBless());
					tginfo_item.setAttrEnchantKind(tgItem.getAttrEnchantKind());
					tginfo_item.setAttrEnchantLevel(tgItem.getAttrEnchantLevel());
					tginfo_item.set_magic_weapon(tgItem.get_magic_weapon());
					// 古文字
					if (tgItem.get_power_name() != null) {
						tginfo_item.set_power_name(tgItem.get_power_name());
						// 新建資料
						CharItemPowerReading.get().storeItem(tginfo_item.getId(),
								tginfo_item.get_power_name());
						// 刪除資料
						CharItemPowerReading.get().delItem(tgItem.getId());
					}
					// 凹槽系統
					if (tgItem.get_power_name_hole() != null) {
						tginfo_item.set_power_name_hole(tgItem.get_power_name_hole());
						// 新建資料
						CharItemPowerHoleReading.get().storeItem(tginfo_item.getId(),
								tginfo_item.get_power_name_hole());
						// 刪除資料
						CharItemPowerHoleReading.get().delItem(tgItem.getId());
					}
					//測試魔法武器自定保留
					if (tgItem.get_magic_weapon() != null) {
						tginfo_item.set_magic_weapon(tginfo_item.get_magic_weapon());
						if(steps>=0){
							CharWeaponTimeTable.addTimeUP(tginfo_item.getId(), tginfo_item.get_time(),
								tginfo_item.get_magic_weapon().getItemId(),steps);
						}else{
							CharWeaponTimeReading.get().addTime(tginfo_item.getId(), tginfo_item.get_time(),
								tginfo_item.get_magic_weapon().getItemId());
						}
					}
					pc.getInventory().storeItem(tginfo_item);
					// 對\f1%0附加強大的魔法力量成功。
					pc.sendPackets(new S_ServerMessage(1410, tgItem.getName()));
					final S_SkillSound sound = new S_SkillSound(pc.getId(), _gfxid_s);
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
					pc.sendPackets(new S_ServerMessage(1411, tgItem.getName())); // 對\f1%0附加魔法失敗。
					final S_SkillSound sound = new S_SkillSound(pc.getId(), _gfxid_e);
					pc.sendPacketsX8(sound);
					break;

				case 1:// 1:強化失敗會倒退
					final L1ItemPowerUpdate ole1 = tmplist.get(order_id - 1);// 取回上一個排序資料
					pc.sendPackets(new S_ServerMessage(1411, tgItem.getName())); // 對\f1%0附加魔法失敗。
					pc.getInventory().removeItem(tgItem, 1);
					// 產生新物件
					final L1ItemInstance tginfo_item = ItemTable.get().createItem(tginfo.get_itemid());
					if (tginfo_item != null) {
						tginfo_item.setIdentified(true);
						tginfo_item.setCount(1);

						// 保留數值
						tginfo_item.setEnchantLevel(tgItem.getEnchantLevel());
						tginfo_item.setIdentified(tgItem.isIdentified());
						tginfo_item.set_durability(tgItem.get_durability());
						tginfo_item.setChargeCount(tgItem.getChargeCount());
						tginfo_item.setRemainingTime(tgItem.getRemainingTime());
						tginfo_item.setLastUsed(tgItem.getLastUsed());
						tginfo_item.setBless(tgItem.getBless());
						tginfo_item.setAttrEnchantKind(tgItem.getAttrEnchantKind());
						tginfo_item.setAttrEnchantLevel(tgItem.getAttrEnchantLevel());
					}
					CreateNewItem.createNewItem(pc, ole1.get_itemid(), 1);
					final S_SkillSound sound2 = new S_SkillSound(pc.getId(), _gfxid_e);
					pc.sendPacketsX8(sound2);
					break;

				case 2:// 2:強化失敗會消失
						// 164 \f1%0%s 产生激烈的 %1 光芒，一会儿后就消失了。銀色的
					pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
					pc.getInventory().removeItem(tgItem, 1);
					final S_SkillSound sound3 = new S_SkillSound(pc.getId(), _gfxid_e);
					pc.sendPacketsX8(sound3);
					break;

				case 3:// 強化失敗會倒退 或 強化失敗會消失
					if (_random.nextBoolean()) {// 強化失敗會倒退
						final L1ItemPowerUpdate ole2 = tmplist.get(order_id - 1);// 取回上一個排序資料
						pc.sendPackets(new S_ServerMessage(1411, tgItem.getName())); // 對\f1%0附加魔法失敗。
						pc.getInventory().removeItem(tgItem, 1);
						CreateNewItem.createNewItem(pc, ole2.get_itemid(), 1);
						final S_SkillSound sound4 = new S_SkillSound(pc.getId(), _gfxid_e);
						pc.sendPacketsX8(sound4);

					} else {// 強化失敗會消失
						// 164 \f1%0%s 产生激烈的 %1 光芒，一会儿后就消失了。銀色的
						pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
						pc.getInventory().removeItem(tgItem, 1);
						final S_SkillSound sound5 = new S_SkillSound(pc.getId(), _gfxid_e);
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
	public void set_set(final String[] set) {
		try {
			_gfxid_e = Integer.parseInt(set[1]);
			_gfxid_s = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
	}
}