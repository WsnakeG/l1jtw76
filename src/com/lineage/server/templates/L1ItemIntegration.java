/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */

package com.lineage.server.templates;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import com.lineage.DatabaseFactory;
import com.lineage.Server;
import com.lineage.data.event.PowerItemSet;
import com.lineage.server.datatables.ItemIntegrationTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemPowerHoleReading;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.datatables.lock.CharWeaponTimeReading;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import com.lineage.server.datatables.sql.CharWeaponTimeTable;
/**
 * william 融合系統
 * 
 * @author roy
 */
public class L1ItemIntegration {
	private static boolean NO_MORE_GET_DATA20 = false;

	public static void main(final String a[]) {
		while (true) {
			try {
				Server.main(null);
			} catch (final Exception ex) {
			}
		}
	}

	private L1ItemIntegration() {
	}

	public static void forItemIntegration(final L1PcInstance pc, final L1ItemInstance item,
			final L1ItemInstance itemG) {
		final int itemid = item.getItem().getItemId();
		ArrayList<?> aTempData = null;
		if (!NO_MORE_GET_DATA20) {
			NO_MORE_GET_DATA20 = true;
			ItemIntegrationTable.load();
		}
		for (int i = 0; i < ItemIntegrationTable.aData20.size(); i++) {
			aTempData = ItemIntegrationTable.aData20.get(i);
			if ((((Integer) aTempData.get(0)).intValue() == itemid)
					&& (itemG.getItem().getItemId() == ((Integer) aTempData.get(4)).intValue())) {
				// 身上物品太多無法合成
				if (pc.getInventory().getSize() >= 160) {
					pc.sendPackets(new S_ServerMessage("S_ServerMessage263"));
					return;
				}
				// 身上負重無法合成
				if (((pc.getInventory().getWeight() / pc.getMaxWeight()) * 100) > 90) {
					pc.sendPackets(new S_ServerMessage("S_ServerMessage82"));
					return;
				}
				// 職業判斷
				if (((Integer) aTempData.get(1)).intValue() != 0) {
					byte class_id = (byte) 0;
					String msg = "";
					if (pc.isCrown()) { // 王族
						class_id = 1;
					} else if (pc.isKnight()) { // 騎士
						class_id = 2;
					} else if (pc.isWizard()) { // 法師
						class_id = 3;
					} else if (pc.isElf()) { // 妖精
						class_id = 4;
					} else if (pc.isDarkelf()) { // 黑妖
						class_id = 5;
					} else if (pc.isDragonKnight()) { // 龍騎士
						class_id = 6;
					} else if (pc.isIllusionist()) { // 幻術士
						class_id = 7;
					}
					switch (((Integer) aTempData.get(1)).intValue()) {
					case 1:
						msg = "王族";
						break;
					case 2:
						msg = "騎士";
						break;
					case 3:
						msg = "法師";
						break;
					case 4:
						msg = "妖精";
						break;
					case 5:
						msg = "黑暗妖精";
						break;
					case 6:
						msg = "龍騎士";
						break;
					case 7:
						msg = "幻術士";
						break;
					}
					if ((((Integer) aTempData.get(1)).intValue() != class_id) && !pc.isGm()) { // 職業不符
						pc.sendPackets(new S_SystemMessage("你的職業無法使用" + msg + "的專屬道具。"));
						return;
					}
				}
				// 等級限制
				if ((((Integer) aTempData.get(2)).intValue() != 0) && !pc.isGm()) {
					if (pc.getLevel() < ((Integer) aTempData.get(2)).intValue()) {
						pc.sendPackets(new S_SystemMessage(
								"等級" + ((Integer) aTempData.get(2)).intValue() + "以上才可使用此道具。"));
						return;
					}
				}
				// 道具數量夠不夠
				if ((item.getCount() < ((Integer) aTempData.get(3)).intValue())
						&& (((Integer) aTempData.get(3)).intValue() != 0)) {
					final L1Item temp1 = ItemTable.get().getTemplate(itemid);
					pc.sendPackets(
							new S_ServerMessage(337,
									temp1.getName() + "("
											+ (((Integer) aTempData.get(3)).intValue()
													- pc.getInventory().countItems(temp1.getItemId()))
											+ ")"));
					return;
				}
				// 如果被融合道具不正確
				if (itemG.getItem().getItemId() != ((Integer) aTempData.get(4)).intValue()) {
					pc.sendPackets(new S_ServerMessage("S_ServerMessage79"));
					return;
				}
				// 且被融合道具數量不夠
				if (itemG.getCount() < ((Integer) aTempData.get(5)).intValue()) {
					final L1Item temp2 = ItemTable.get().getTemplate(((Integer) aTempData.get(4)).intValue());
					pc.sendPackets(
							new S_ServerMessage(337,
									temp2.getName() + "("
											+ (((Integer) aTempData.get(5)).intValue()
													- pc.getInventory().countItems(temp2.getItemId()))
											+ ")"));
					return;
				}
				// 檢查身上的物品
				if ((((int[]) aTempData.get(7) != null) && ((int[]) aTempData.get(8) != null))
						|| (((int[]) aTempData.get(7) == null) && ((int[]) aTempData.get(8) == null))) {
					final int[] materials = (int[]) aTempData.get(7);
					final int[] counts = (int[]) aTempData.get(8);
					boolean isCreate = true;
					// 檢查身上的物品
					if (((int[]) aTempData.get(7) != null) && ((int[]) aTempData.get(8) != null)) {
						for (int j = 0; j < materials.length; j++) {
							if (!pc.getInventory().checkItem(materials[j], counts[j])) {
								final L1Item temp = ItemTable.get().getTemplate(materials[j]);
								pc.sendPackets(
										new S_ServerMessage(337,
												temp.getName() + "(" + (counts[j]
														- pc.getInventory().countItems(temp.getItemId()))
														+ ")"));
								isCreate = false;
							}
						}
					}
					if (isCreate) { // 刪除確認的道具、並給予任務道具
						// 刪除材料
						if (((int[]) aTempData.get(7) != null) && ((int[]) aTempData.get(8) != null)) {
							for (int k = 0; k < materials.length; k++) {
								pc.getInventory().consumeItem(materials[k], counts[k]);
							}
						}
						// 刪除使用道具
						if (((Integer) aTempData.get(3)).intValue() != 0) {
							pc.getInventory().removeItem(item, ((Integer) aTempData.get(3)).intValue());
						}
						// 給予道具
						if (((int[]) aTempData.get(9) != null) && ((int[]) aTempData.get(10) != null)) {
							final int[] giveMaterials = (int[]) aTempData.get(9);
							final int[] giveCounts = (int[]) aTempData.get(10);
							final Random _random = new Random();
							if ((_random.nextInt(100) + 1) <= ((Integer) aTempData.get(6)).intValue()) { // 成功機率
								for (int l = 0; l < giveMaterials.length; l++) {
									final L1ItemInstance item2 = ItemTable.get().createItem(giveMaterials[l]);
									// 刪除被融合道具
									int steps = CharWeaponTimeTable.get_steps(itemG.getId());
									pc.getInventory().removeItem(itemG,
											((Integer) aTempData.get(5)).intValue());

									if (item2 != null) {
										// 保留數值
										item2.setEnchantLevel(itemG.getEnchantLevel());
										item2.setIdentified(itemG.isIdentified());
										item2.set_durability(itemG.get_durability());
										item2.setChargeCount(itemG.getChargeCount());
										item2.setRemainingTime(itemG.getRemainingTime());
										item2.setLastUsed(itemG.getLastUsed());
										item2.setBless(itemG.getBless());
										item2.setAttrEnchantKind(itemG.getAttrEnchantKind());
										item2.setAttrEnchantLevel(itemG.getAttrEnchantLevel());
										
										if (item2.isStackable()) {
											item2.setCount(giveCounts[l]);
										} else {
											item2.setCount(1);
										}
										// 古文字
										if (PowerItemSet.START && (itemG.get_power_name() != null)) {
											item2.set_power_name(itemG.get_power_name());
											CharItemPowerReading.get().storeItem(item2.getId(),
													item2.get_power_name());
											CharItemPowerReading.get().delItem(itemG.getId());
										}
										// 凹槽
										if (itemG.get_power_name_hole() != null) {
											item2.set_power_name_hole(itemG.get_power_name_hole());
											CharItemPowerHoleReading.get().storeItem(item2.getId(),
													item2.get_power_name_hole());
											CharItemPowerHoleReading.get().delItem(itemG.getId());
										}
										//測試魔法武器自定保留
										if (itemG.get_magic_weapon() != null) {
											item2.set_magic_weapon(itemG.get_magic_weapon());
											if(steps>=0){
												CharWeaponTimeTable.addTimeUP(item2.getId(), item2.get_time(),
													item2.get_magic_weapon().getItemId(),steps);
											}else{
												CharWeaponTimeReading.get().addTime(item2.getId(), item2.get_time(),
													item2.get_magic_weapon().getItemId());
											}
										}
										if (pc.getInventory().checkAddItem(item2,
												item2.getCount()) == L1Inventory.OK) {
											pc.getInventory().storeItem(item2);
										} else {
											World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId())
													.storeItem(item2);
										}
										// 針對資料庫的更新
										if ((itemG.getItem().getType2() == 0)
												&& (item2.getItem().getType2() == 0)) {// 道具
											if (itemG.getItem().getType() == 32) {
												upgradeWeapon(itemG.getId(), giveMaterials[l],
														ItemTable.get().getTemplate(
																((Integer) giveMaterials[l]).intValue())
																.getName());
											}
										} else if ((itemG.getItem().getType2() == 1)
												&& (item2.getItem().getType2() == 1)) {// 武器
											upgradeWeapon(itemG.getId(), giveMaterials[l],
													ItemTable.get()
															.getTemplate(
																	((Integer) giveMaterials[l]).intValue())
															.getName());
										} else if ((itemG.getItem().getType2() == 2)
												&& (item2.getItem().getType2() == 2)) {// 防具
											upgradeWeapon(itemG.getId(), giveMaterials[l],
													ItemTable.get()
															.getTemplate(
																	((Integer) giveMaterials[l]).intValue())
															.getName());
										}
									}
									// 顯示的內容
									if ((String) aTempData.get(11) != null) {
										pc.sendPackets(new S_SystemMessage((String) aTempData.get(11)));
									} else {
										pc.sendPackets(new S_ServerMessage(403, item2.getLogName()));
									}
									// 顯示特效
									if (((Integer) aTempData.get(12)).intValue() != 0) {
										pc.sendPackets(new S_SkillSound(pc.getId(),
												((Integer) aTempData.get(12)).intValue()));
										pc.broadcastPacketX8(new S_SkillSound(pc.getId(),
												((Integer) aTempData.get(12)).intValue()));
									}
								}
							} else {
								// 刪除被融合道具
								pc.getInventory().removeItem(itemG, ((Integer) aTempData.get(5)).intValue());
								pc.sendPackets(new S_ServerMessage("\\aG製作失敗，請再接再厲。"));
							}
						}
					}
				}
				break;
			}
		}
	}

	/**
	 * 資料庫更新
	 * 
	 * @param weapon_oid 舊世界編號
	 * @param new_weapon_id 新的流水號
	 * @param new_weapon_name 新的名稱
	 */
	public static void upgradeWeapon(final int weapon_oid, final int new_weapon_id,
			final String new_weapon_name) {
		java.sql.Connection con = null;
		try {
			con = DatabaseFactory.get().getConnection();
			final Statement stat = con.createStatement();
			try {
				stat.executeUpdate("update character_items set item_id='" + new_weapon_id + "',item_name='"
						+ new_weapon_name + "' where id ='" + weapon_oid + "'");
			} catch (final Exception eee) {
			}
			if ((con != null) && !con.isClosed()) {
				con.close();
			}
		} catch (final Exception ex) {
		}
	}

}
