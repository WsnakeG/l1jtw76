package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

public class Ajplayer extends ItemExecutor {
	public static ItemExecutor get() {
		return new Ajplayer();
	}

	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		int spellsc_objid = data[0];
		sneakpeek(pc, spellsc_objid, item);
	}

	private void sneakpeek(L1PcInstance pc, int targetId, L1ItemInstance item) {
		/** [原碼] 反 & 偷窺卡 */
		L1Object target = World.get().findObject(targetId);
		if (target != null) {
			String msg0 = ""; // 的偷窺內容
			String msg1 = ""; // 等級
			String msg2 = ""; // 體力
			String msg3 = ""; // 魔力
			String msg4 = ""; // 防禦
			String msg5 = ""; // 迴避
			String msg6 = ""; // 抗魔
			String msg7 = ""; // 抗火
			String msg8 = ""; // 抗水
			String msg9 = ""; // 抗風
			String msg10 = ""; // 抗地
			String msg11 = ""; // 當前武器
			String msg12 = "";// 力量
			String msg13 = "";// 體力
			String msg14 = "";// 敏捷
			String msg15 = "";// 精神
			String msg16 = "";// 智力
			String msg17 = "";// 魅力
			String msg18 = "";// 編號
			String msg19 = "";// 圖檔
			String msg20 = "";// 經驗
			String msg21 = "";// 正義
			String msg22 = "";// 魔攻
			String msg23 = "";// 當前裝備 頭盔
			String msg24 = "";// 盔甲
			String msg25 = "";// 內衣
			String msg26 = "";// 斗篷
			String msg27 = "";// 手套
			String msg28 = "";// 長靴
			String msg29 = "";// 盾牌
			String msg30 = "";// 項鍊
			String msg31 = "";// 腰帶
			String msg32 = "";// 耳環
			String msg33 = "";// 耳環
			String msg34 = "";// 耳環
			String msg35 = "";// 特殊八種能力
			String msg36 = "";// 特殊八種能力
			String msg37 = "";// 特殊八種能力
			String msg38 = "";// 特殊八種能力
			String msg39 = "";// 特殊八種能力
			String msg40 = "";// 特殊八種能力
			String msg41 = "";// 特殊八種能力
			String msg42 = "";// 特殊八種能力

			if (target instanceof L1PcInstance) {
				L1PcInstance target_pc = (L1PcInstance) target;

				if ((!target_pc.getInventory().checkItem(50104, 1)) || (pc.isGm())) {// 對象身上沒有反偷窺卡或是執行者是GM
					final L1ItemInstance item18 = target_pc.getInventory().getItemEquipped(2, 1);
					final L1ItemInstance item19 = target_pc.getInventory().getItemEquipped(2, 2);
					final L1ItemInstance item20 = target_pc.getInventory().getItemEquipped(2, 3);
					final L1ItemInstance item21 = target_pc.getInventory().getItemEquipped(2, 4);
					final L1ItemInstance item22 = target_pc.getInventory().getItemEquipped(2, 5);
					final L1ItemInstance item23 = target_pc.getInventory().getItemEquipped(2, 6);
					final L1ItemInstance item24 = target_pc.getInventory().getItemEquipped(2, 7);
					final L1ItemInstance item25 = target_pc.getInventory().getItemEquipped(2, 8);
					final L1ItemInstance item26 = target_pc.getInventory().getItemEquipped(2, 9);
					final L1ItemInstance item27 = target_pc.getInventory().getItemEquipped(2, 10);
					final L1ItemInstance item28 = target_pc.getInventory().getItemEquipped(2, 11);
					final L1ItemInstance item29 = target_pc.getInventory().getItemEquipped(2, 12);
					msg0 = target_pc.getName();
					msg1 = "" + target_pc.getLevel(); // 等級
					msg2 = "" + target_pc.getCurrentHp() + " / " + target_pc.getMaxHp(); // 體力
					msg3 = "" + target_pc.getCurrentMp() + " / " + +target_pc.getMaxMp(); // 魔力
					msg4 = "" + target_pc.getAc(); // 防禦
					msg5 = "" + target_pc.getEr(); // 迴避
					msg6 = "" + target_pc.getMr() + " %"; // 抗魔
					msg7 = "" + target_pc.getFire() + " %"; // 抗火
					msg8 = "" + target_pc.getWater() + " %"; // 抗水
					msg9 = "" + target_pc.getWind() + " %"; // 抗風
					msg10 = "" + target_pc.getEarth() + " %"; // 抗地
					msg12 = "" + target_pc.getStr();// 力量
					msg13 = "" + target_pc.getCon();// 體力
					msg14 = "" + target_pc.getDex();// 敏捷
					msg15 = "" + target_pc.getWis();// 精神
					msg16 = "" + target_pc.getInt();// 智力
					msg17 = "" + target_pc.getCha();// 魅力
					msg18 = "0";// 編號
					msg19 = "" + target_pc.getGfxId();// 圖檔
					msg20 = "" + target_pc.getExp();// 經驗
					msg21 = "" + target_pc.getLawful();// 正義
					msg22 = "" + target_pc.getSp();// 魔攻

					msg35 = "" + target_pc.getPhysicsDmgUp() + " %"; // 物理傷害增加+%
					msg36 = "" + target_pc.getMagicDmgUp() + " %"; // 魔法傷害增加+%
					msg37 = "" + target_pc.getPhysicsDmgDown() + " %"; // 物理傷害減免+%
					msg38 = "" + target_pc.getMagicDmgDown() + " %"; // 魔法傷害減免+%
					msg39 = "" + target_pc.getMagicHitUp() + " %"; // 有害魔法成功率+%
					msg40 = "" + target_pc.getMagicHitDown() + " %"; // 抵抗有害魔法成功率+%
					msg41 = "" + target_pc.getPhysicsDoubleHit() + " %"; // 物理暴擊發動機率+%
																			// (發動後普攻傷害*1.5倍)
					msg42 = "" + target_pc.getMagicDoubleHit() + " %"; // 魔法暴擊發動機率+%
																		// (發動後技能傷害*1.5倍)

					if (target_pc.getInventory().getItemEquipped(2, 1) != null) {
						msg23 = "" + "+" + item18.getEnchantLevel() + " " + item18.getName();
					} else {
						msg23 = "" + "無裝備頭盔";
					}
					if (target_pc.getInventory().getItemEquipped(2, 2) != null) {
						msg24 = "" + "+" + item19.getEnchantLevel() + " " + item19.getName();
					} else {
						msg24 = "" + "無裝備盔甲";
					}
					if (target_pc.getInventory().getItemEquipped(2, 3) != null) {
						msg25 = "" + "+" + item20.getEnchantLevel() + " " + item20.getName();
					} else {
						msg25 = "" + "無裝備襯衣";
					}
					if (target_pc.getInventory().getItemEquipped(2, 4) != null) {
						msg26 = "" + "+" + item21.getEnchantLevel() + " " + item21.getName();
					} else {
						msg26 = "" + "無裝備斗篷";
					}
					if (target_pc.getInventory().getItemEquipped(2, 5) != null) {
						msg27 = "" + "+" + item22.getEnchantLevel() + " " + item22.getName();
					} else {
						msg27 = "" + "無裝備手套";
					}
					if (target_pc.getInventory().getItemEquipped(2, 6) != null) {
						msg28 = "" + "+" + item23.getEnchantLevel() + " " + item23.getName();
					} else {
						msg28 = "" + "無裝備長靴";
					}
					if (target_pc.getInventory().getItemEquipped(2, 7) != null) {
						msg29 = "" + "+" + item24.getEnchantLevel() + " " + item24.getName();
					} else {
						msg29 = "" + "無裝備盾牌";
					}
					if (target_pc.getInventory().getItemEquipped(2, 8) != null) {
						msg30 = "" + "+" + item25.getEnchantLevel() + " " + item25.getName();
					} else {
						msg30 = "" + "無裝備項鍊";
					}
					if (target_pc.getInventory().getItemEquipped(2, 9) != null) {
						msg31 = "" + "+" + item26.getEnchantLevel() + " " + item26.getName();
					} else {
						msg31 = "" + "無裝備戒指";
					}
					if (target_pc.getInventory().getItemEquipped(2, 12) != null) {
						msg32 = "" + "+" + item29.getEnchantLevel() + " " + item29.getName();
					} else {
						msg32 = "" + "無裝備耳環";
					}
					if (target_pc.getInventory().getItemEquipped(2, 10) != null) {
						msg33 = "" + "+" + item27.getEnchantLevel() + " " + item27.getName();
					} else {
						msg33 = "" + "無裝備腰帶";
					}
					if (target_pc.getInventory().getItemEquipped(2, 11) != null) {
						msg34 = "" + "+" + item28.getEnchantLevel() + " " + item28.getName();
					} else {
						msg34 = "" + "無裝備戒指";
					}

					L1ItemInstance weapon = target_pc.getWeapon();
					if (weapon != null) {
						msg11 = "" + weapon.getLogName();
					} else {
						msg11 = "" + "無裝備武器";
					}

					String msg[] = { msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11,
							msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21, msg22,
							msg23, msg24, msg25, msg26, msg27, msg28, msg29, msg30, msg31, msg32, msg33,
							msg34, msg35, msg36, msg37, msg38, msg39, msg40, msg41, msg42 };

					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ajplayer", msg));
					pc.getInventory().removeItem(item, 1);
				} else {// 對象身上有反偷窺卡
					int deleitem = 50104;
					target_pc.getInventory().consumeItem(deleitem, 1);
					target_pc.sendPackets(new S_SystemMessage(pc.getName() + ":對你進行偷窺，但是沒有偷窺成功。"));
					pc.getInventory().removeItem(item, 1);
				}
			} else if (pc.isGm()) {// GM才可偷窺NPC或怪物
				L1NpcInstance target_npc = null;
				if (target instanceof L1MonsterInstance) {
					target_npc = (L1MonsterInstance) target;
				} else if (target instanceof L1NpcInstance) {
					target_npc = (L1NpcInstance) target;
				}

				msg0 = target_npc.getName(); // 怪物
				msg1 = "" + target_npc.getLevel(); // 等級
				msg2 = "" + target_npc.getCurrentHp() + " / " + target_npc.getMaxHp(); // 體力
				msg3 = "" + target_npc.getCurrentMp() + " / " + +target_npc.getMaxMp(); // 魔力
				msg4 = "" + target_npc.getAc(); // 防禦
				msg5 = "0"; // 迴避
				msg6 = "" + target_npc.getMr() + " %"; // 抗魔
				msg7 = "" + target_npc.getFire() + " %"; // 抗火
				msg8 = "" + target_npc.getWater() + " %"; // 抗水
				msg9 = "" + target_npc.getWind() + " %"; // 抗風
				msg10 = "" + target_npc.getEarth() + " %"; // 抗地
				msg11 = "砂鍋大的拳頭";
				msg12 = "" + target_npc.getStr();// 力量
				msg13 = "" + target_npc.getCon();// 體力
				msg14 = "" + target_npc.getDex();// 敏捷
				msg15 = "" + target_npc.getWis();// 精神
				msg16 = "" + target_npc.getInt();// 智力
				msg17 = "" + target_npc.getCha();// 魅力
				msg18 = "" + target_npc.getNpcId();// 編號
				msg19 = "" + target_npc.getGfxId();// 圖檔
				msg20 = "" + target_npc.getExp();// 經驗
				msg21 = "" + target_npc.getLawful();// 正義
				msg22 = "" + target_npc.getSp();// 魔攻
				String msg[] = { msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11,
						msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21, msg22 };
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ajplayer2", msg));
				pc.getInventory().removeItem(item, 1);
			} else {
				pc.sendPackets(new S_ServerMessage(79));
			}
		}
	}
}