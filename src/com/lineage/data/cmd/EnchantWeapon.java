package com.lineage.data.cmd;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRecord;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.WeaponSkillPowerTable;
import com.lineage.server.datatables.lock.LogEnchantReading;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.weaponskill.L1WeaponSkillType;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

public class EnchantWeapon extends EnchantExecutor {

	private static final Log _log = LogFactory.getLog(EnchantWeapon.class);

	/**
	 * 強化紀錄(失敗)
	 * 
	 * @param pc 執行者
	 * @param item 對象物件
	 */
	@Override
	public void failureEnchant(final L1PcInstance pc, final L1ItemInstance item) {
		switch (item.get_protect_type()) {
		case 1:
			item.set_protect_type(0);
			pc.sendPackets(new S_ServerMessage(1310));
			return;
		case 2:
			item.set_protect_type(0);
			item.setEnchantLevel(item.getEnchantLevel() - 1);
			pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.sendPackets(new S_ServerMessage(1310));
			return;
		case 3:
			item.set_protect_type(0);
			item.setEnchantLevel(0);
			pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.sendPackets(new S_ServerMessage(1310));
			return;
		}
		final StringBuilder s = new StringBuilder();

		if (ConfigRecord.LOGGING_BAN_ENCHANT) {
			LogEnchantReading.get().failureEnchant(pc, item);
		}

		// 未鑑定
		if (!item.isIdentified()) {
			s.append(item.getName());
		} else {
			String pm = "";
			if (item.getEnchantLevel() > 0) {
				pm = "+";
			}
			s.append(pm + item.getEnchantLevel() + " " + item.getName());
		}
		// 164 \f1%0%s 产生激烈的 %1 光芒，一会儿后就消失了。
		pc.sendPackets(new S_ServerMessage(164, s.toString(), "$252"));
		pc.getInventory().removeItem(item, item.getCount());
		_log.info("人物:" + pc.getName() + "點爆物品(武器)" + item.getItem().getName() + " 物品OBJID:" + item.getId());
		WriteLogTxt.Recording("武器強化失敗紀錄", "人物:" + pc.getName() + "點爆物品" + item.getItem().getName() + " 物品OBJID:" + item.getId());
	}

	/**
	 * 強化成功
	 * 
	 * @param pc 執行者
	 * @param item 對象物件
	 * @param i 強化質
	 */
	@Override
	public void successEnchant(final L1PcInstance pc, final L1ItemInstance item, final int i) {
		final StringBuilder s = new StringBuilder();
		final StringBuilder sa = new StringBuilder();
		final StringBuilder sb = new StringBuilder();

		// 未鑑定
		if (!item.isIdentified()) {
			s.append(item.getName());

		} else {
			s.append(item.getLogName());
		}

		switch (i) {
		case 0:
			// \f1%0%s %2 产生激烈的 %1 光芒，但是没有任何事情发生。
			pc.sendPackets(new S_ServerMessage(160, s.toString(), "$252", "$248"));
			return;

		case -1:
			sa.append("$246");// 黑色的
			sb.append("$247");// 一瞬間發出
			break;

		case 1: // '\001'
			sa.append("$245");// 藍色的
			sb.append("$247");// 一瞬間發出
			break;

		case 2: // '\002'
		case 3: // '\003'
			sa.append("$245");// 藍色的
			sb.append("$248");// 持續發出
			break;
		}

		if (item.get_protect_type() > 0) {
			item.set_protect_type(0);
		}

		// 161 \f1%0%s %2 %1 光芒。
		pc.sendPackets(new S_ServerMessage(161, s.toString(), sa.toString(), sb.toString()));

		final int oldEnchantLvl = item.getEnchantLevel();
		final int newEnchantLvl = oldEnchantLvl + i;
		if (oldEnchantLvl != newEnchantLvl) {
			if (newEnchantLvl >= ConfigOther.StrengthenWeapon) {// 強化值等於或超過9
				// 1,652：強化
				// 產生訊息封包 (強化成功)
				World.get().broadcastPacketToAll(new S_HelpMessage(pc.getName(),
						s.toString() + " " + sb.toString() + " " + sa.toString() + " $251"));
			}
			item.setEnchantLevel(newEnchantLvl);
			pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);

			if (item.isEquipped()) {
				boolean isSPMR = false;

				final int mr = item.getMr();
				if (mr != 0) {
					pc.addMr(i * item.getItem().getInfluenceMr());
					isSPMR = true;
				}

				final int influence_sp = item.getItem().getInfluenceSp();
				if (influence_sp != 0) {
					pc.addSp(i * influence_sp);
					isSPMR = true;
				}

				if (isSPMR) {
					pc.sendPackets(new S_SPMR(pc));
				}

				final int influence_hp = item.getItem().getInfluenceHp();
				if (influence_hp != 0) {
					pc.addMaxHp(i * influence_hp);
					pc.sendPackets(new S_HPUpdate(pc));
				}

				final int influence_mp = item.getItem().getInfluenceMp();
				if (influence_mp != 0) {
					pc.addMaxMp(i * influence_mp);
					pc.sendPackets(new S_MPUpdate(pc));
				}

				final int influence_dmgR = item.getItem().getInfluenceDmgR();
				if (influence_dmgR != 0) {
					pc.addDamageReductionByArmor(i * influence_dmgR);
				}

				final int influence_hitAndDmg = item.getItem().getInfluenceHitAndDmg();
				if (influence_hitAndDmg != 0) {
					pc.addHitup(i * influence_hitAndDmg);
					pc.addDmgup(i * influence_hitAndDmg);
				}

				final int influence_bowHitAndDmg = item.getItem().getInfluenceBowHitAndDmg();
				if (influence_bowHitAndDmg != 0) {
					pc.addBowHitup(i * influence_bowHitAndDmg);
					pc.addBowDmgup(i * influence_bowHitAndDmg);
				}
			}

			// weapon random (強化成功)
			final ArrayList<L1WeaponSkillType> weapon_list = WeaponSkillPowerTable.get()
					.getTemplate(item.getItemId());
			if (weapon_list != null) {
				final L1WeaponSkillType tmp = weapon_list.get(0);
				if (tmp != null) {
					item.set_random((double) tmp.random_for_show(item) / 10);
					if (World.get().getPlayer(item.get_char_objid()) != null) {
						World.get().getPlayer(item.get_char_objid()).sendPackets(new S_ItemStatus(item));
					}
				}
			}
		}
	}
}
