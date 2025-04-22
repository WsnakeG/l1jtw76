package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 玩家資訊檢視卡
 * 
 * @author Roy
 */
public class Char_Desc_Card extends ItemExecutor {

	/**
	 *
	 */
	private Char_Desc_Card() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Char_Desc_Card();
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
		final int spellsc_objid = data[0];
		useCharDesc(pc, spellsc_objid, item);
	}

	private void useCharDesc(final L1PcInstance pc, final int spellsc_objid, final L1ItemInstance item) {

		// 對象OBJID

		final L1Object target = World.get().findObject(spellsc_objid);

		if (target != null) {

			if (_adena >= 0) {
				if (!pc.getInventory().consumeItem(_adena, _count)) {
					// \f1%0不足%s。
					pc.sendPackets(new S_SystemMessage("\\aG所需道具不足"));
					return;
				} else {
					pc.getInventory().removeItem(item, 1);
					pc.sendPackets(new S_ServerMessage("\\aG玩家" + pc.getName() + "資料獲取完成。"));
					pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));
				}
			}

			// 送出封包(動作)
			pc.sendPacketsX8(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Wand));

			if (target != null) {
				String msg0 = "";
				String msg1 = "";
				String msg2 = "";
				String msg3 = "";
				String msg4 = "";
				String msg5 = "";
				String msg6 = "";
				String msg7 = "";
				String msg8 = "";
				String msg9 = "";
				String msg10 = "";
				String msg11 = "";
				String msg12 = "";
				String msg13 = "";
				String msg14 = "";
				String msg15 = "";
				String msg16 = "";
				String msg17 = ""; // 當前武器
				String msg18 = "";
				String msg19 = "";
				String msg20 = "";
				String msg21 = "";
				String msg22 = "";
				String msg23 = "";
				String msg24 = "";
				String msg25 = "";
				String msg26 = "";
				String msg27 = "";
				String msg28 = "";
				String msg29 = "";
				String msg30 = "";
				String msg31 = "";
				String msg32 = "";
				String msg33 = "";
				String msg34 = "";
				String msg35 = "";
				String msg36 = "";
				String msg37 = "";
				if (target instanceof L1PcInstance) {
					final L1PcInstance target_pc = (L1PcInstance) target;

					final L1ItemInstance item17 = target_pc.getWeapon(); // 裝備武器

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
					msg1 = "" + target_pc.getLevel();
					msg2 = "" + target_pc.getMaxHp();
					msg3 = "" + +target_pc.getMaxMp();
					msg4 = "" + target_pc.getAc();
					msg5 = "" + target_pc.getEr();
					msg6 = "" + target_pc.getMr() + " %";
					msg7 = "" + target_pc.getFire() + " %";
					msg8 = "" + target_pc.getWater() + " %";
					msg9 = "" + target_pc.getWind() + " %";
					msg10 = "" + target_pc.getEarth() + " %";
					msg11 = "" + target_pc.getRegistStun() + " %";
					msg12 = "" + target_pc.getRegistStone() + " %";
					msg13 = "" + target_pc.getRegistSleep() + " %";
					msg14 = "" + target_pc.getRegistFreeze() + " %";
					msg15 = "" + target_pc.getRegistSustain() + " %";
					msg16 = "" + target_pc.getRegistBlind() + " %";
					msg30 = "" + target_pc.getPhysicsDmgUp() + " %"; // 物理傷害增加+%
					msg31 = "" + target_pc.getMagicDmgUp() + " %"; // 魔法傷害增加+%
					msg32 = "" + target_pc.getPhysicsDmgDown() + " %"; // 物理傷害減免+%
					msg33 = "" + target_pc.getMagicDmgDown() + " %"; // 魔法傷害減免+%
					msg34 = "" + target_pc.getMagicHitUp() + " %"; // 有害魔法成功率+%
					msg35 = "" + target_pc.getMagicHitDown() + " %"; // 抵抗有害魔法成功率+%
					msg36 = "" + target_pc.getPhysicsDoubleHit() + " %"; // 物理暴擊發動機率+%
																			// (發動後普攻傷害*1.5倍)
					msg37 = "" + target_pc.getMagicDoubleHit() + " %"; // 魔法暴擊發動機率+%
																		// (發動後技能傷害*1.5倍)
					if (target_pc.getInventory().getItemEquipped(2, 1) != null) {
						msg18 = "" + "+" + item18.getEnchantLevel() + " " + item18.getName();
					} else {
						msg18 = "" + "無裝備頭盔";
					}
					if (target_pc.getInventory().getItemEquipped(2, 2) != null) {
						msg19 = "" + "+" + item19.getEnchantLevel() + " " + item19.getName();
					} else {
						msg19 = "" + "無裝備盔甲";
					}
					if (target_pc.getInventory().getItemEquipped(2, 3) != null) {
						msg20 = "" + "+" + item20.getEnchantLevel() + " " + item20.getName();
					} else {
						msg20 = "" + "無裝備襯衣";
					}
					if (target_pc.getInventory().getItemEquipped(2, 4) != null) {
						msg21 = "" + "+" + item21.getEnchantLevel() + " " + item21.getName();
					} else {
						msg21 = "" + "無裝備斗篷";
					}
					if (target_pc.getInventory().getItemEquipped(2, 5) != null) {
						msg22 = "" + "+" + item22.getEnchantLevel() + " " + item22.getName();
					} else {
						msg22 = "" + "無裝備手套";
					}
					if (target_pc.getInventory().getItemEquipped(2, 6) != null) {
						msg23 = "" + "+" + item23.getEnchantLevel() + " " + item23.getName();
					} else {
						msg23 = "" + "無裝備長靴";
					}
					if (target_pc.getInventory().getItemEquipped(2, 7) != null) {
						msg24 = "" + "+" + item24.getEnchantLevel() + " " + item24.getName();
					} else {
						msg24 = "" + "無裝備盾牌";
					}
					if (target_pc.getInventory().getItemEquipped(2, 8) != null) {
						msg25 = "" + "+" + item25.getEnchantLevel() + " " + item25.getName();
					} else {
						msg25 = "" + "無裝備項鍊";
					}
					if (target_pc.getInventory().getItemEquipped(2, 9) != null) {
						msg26 = "" + "+" + item26.getEnchantLevel() + " " + item26.getName();
					} else {
						msg26 = "" + "無裝備戒指";
					}
					if (target_pc.getInventory().getItemEquipped(2, 12) != null) {
						msg29 = "" + "+" + item29.getEnchantLevel() + " " + item29.getName();
					} else {
						msg29 = "" + "無裝備耳環";
					}
					if (target_pc.getInventory().getItemEquipped(2, 10) != null) {
						msg27 = "" + "+" + item27.getEnchantLevel() + " " + item27.getName();
					} else {
						msg27 = "" + "無裝備腰帶";
					}
					if (target_pc.getInventory().getItemEquipped(2, 11) != null) {
						msg28 = "" + "+" + item28.getEnchantLevel() + " " + item28.getName();
					} else {
						msg28 = "" + "無裝備戒指";
					}
					if (target_pc.getWeapon() != null) {
						msg17 = "" + "+" + item17.getEnchantLevel() + " " + item17.getName();
					} else {
						msg17 = "" + "無裝備武器";
					}
					if (pc.getInventory() == null) {
						return;
					}

					/*
					 * if (!pc.isGm()) { pc.sendPackets(new
					 * S_ServerMessage("\\aG您無法對管理員進行偵測。")); return; }
					 */

					final String msg[] = { msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10,
							msg11, msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21,
							msg22, msg23, msg24, msg25, msg26, msg27, msg28, msg29, msg30, msg31, msg32,
							msg33, msg34, msg35, msg36, msg37 };
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ajplayer", msg));

				}
			}
		}
	}

	private int _adena;
	private int _count;
	private int _gfxid;

	@Override
	public void set_set(final String[] set) {
		try {
			_adena = Integer.parseInt(set[1]);
			_count = Integer.parseInt(set[2]);
			_gfxid = Integer.parseInt(set[3]);

		} catch (final Exception e) {
		}
	}
}