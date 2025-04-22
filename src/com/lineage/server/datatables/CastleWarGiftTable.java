package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 攻城獎勵設置
 * 
 * @author dexc
 */
public class CastleWarGiftTable {

	private static final Log _log = LogFactory.getLog(CastleWarGiftTable.class);

	public static final Map<Integer, ArrayList<Gift>> _gifts = new HashMap<Integer, ArrayList<Gift>>();

	private static CastleWarGiftTable _instance;

	public static CastleWarGiftTable get() {
		if (_instance == null) {
			_instance = new CastleWarGiftTable();
		}
		return _instance;
	}

	private class Gift {

		private int _itemid;

		private int _count;

		private boolean _recover;

		private int _ac; // 防禦

		private int _hp; // 血量

		private int _mp; // 魔量

		private int _hpr; // 回血量

		private int _mpr; // 回魔量

		private int _str; // 力量

		private int _con; // 體質

		private int _dex; // 敏捷

		private int _wis; // 精神

		private int _cha; // 魅力

		private int _int; // 智力

		private int _sp; // 魔攻

		private int _mr; // 魔防

		private int _hit_modifier; // 近戰攻擊命中

		private int _dmg_modifier; // 近戰攻擊傷害

		private int _bow_hit_modifier; // 遠距攻擊命中

		private int _bow_dmg_modifier; // 遠距攻擊傷害

		private int _defense_water; // 水屬性防禦

		private int _defense_wind; // 風屬性防禦

		private int _defense_fire; // 火屬性防禦

		private int _defense_earth; // 地屬性防禦

		private int _regist_stun; // 昏迷耐性

		private int _regist_stone; // 石化耐性

		private int _regist_sleep; // 睡眠耐性

		private int _regist_freeze; // 寒冰耐性

		private int _regist_sustain; // 支撐耐性

		private int _regist_blind; // 闇黑耐性
	}

	/**
	 * 初始化載入
	 */
	public void load() {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `server_castle_war_gift`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int key = rs.getInt("castle_id");
				final int itemid = rs.getInt("itemid");
				final int count = rs.getInt("count");
				final boolean recover = rs.getBoolean("recover");

				final Gift e = new Gift();
				e._itemid = itemid;
				e._count = count;
				e._recover = recover;

				// 城堡額外附加能力 by terry0412
				e._ac = rs.getInt("ac");
				e._hp = rs.getInt("hp");
				e._mp = rs.getInt("mp");
				e._hpr = rs.getInt("hpr");
				e._mpr = rs.getInt("mpr");
				e._str = rs.getInt("str");
				e._con = rs.getInt("con");
				e._dex = rs.getInt("dex");
				e._wis = rs.getInt("wis");
				e._cha = rs.getInt("cha");
				e._int = rs.getInt("intel");
				e._sp = rs.getInt("sp");
				e._mr = rs.getInt("mr");
				e._hit_modifier = rs.getInt("hit_modifier");
				e._dmg_modifier = rs.getInt("dmg_modifier");
				e._bow_hit_modifier = rs.getInt("bow_hit_modifier");
				e._bow_dmg_modifier = rs.getInt("bow_dmg_modifier");
				e._defense_water = rs.getInt("defense_water");
				e._defense_wind = rs.getInt("defense_wind");
				e._defense_fire = rs.getInt("defense_fire");
				e._defense_earth = rs.getInt("defense_earth");
				e._regist_stun = rs.getInt("regist_stun");
				e._regist_stone = rs.getInt("regist_stone");
				e._regist_sleep = rs.getInt("regist_sleep");
				e._regist_freeze = rs.getInt("regist_freeze");
				e._regist_sustain = rs.getInt("regist_sustain");
				e._regist_blind = rs.getInt("regist_blind");

				ArrayList<Gift> list = _gifts.get(key);
				if (list == null) {
					list = new ArrayList<Gift>();
				}
				list.add(e);

				_gifts.put(key, list);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 是否具有攻城獎勵設置
	 * 
	 * @param objid
	 * @return
	 */
	public boolean isGift(final int key) {
		final ArrayList<Gift> list = _gifts.get(key);
		if (list == null) {
			return false;
		}
		return true;
	}

	/**
	 * 給予攻城獎勵
	 * 
	 * @param key
	 */
	public void get_gift(final int key) {
		L1Clan castle_clan = null;
		final ArrayList<Gift> list = _gifts.get(key);
		if (list == null) {
			return;
		}

		try {
			castle_clan = L1CastleLocation.castleClan(key);
			/*
			 * final Collection<L1Clan> allClan = WorldClan.get().getAllClans();
			 * // 不包含元素 if (allClan.isEmpty()) { return; } for (final
			 * Iterator<L1Clan> iter = allClan.iterator(); iter.hasNext();) {
			 * final L1Clan clan = iter.next(); if (clan.getCastleId() == key) {
			 * castle_clan = clan; // 設置血盟 } }
			 */

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			if (castle_clan != null) {
				for (final Iterator<Gift> iter = list.iterator(); iter.hasNext();) {
					final Gift gift = iter.next();
					if (gift._recover) { // 該物品設置回收
						recover_item(gift);
					}
					get_gift(castle_clan, gift);
				}
			}
		}
	}

	/**
	 * 給予物品
	 * 
	 * @param castle_clan
	 * @param itemid
	 * @param count
	 */
	private void get_gift(final L1Clan castle_clan, final Gift gift) {
		try {
			if (castle_clan.getOnlineClanMemberSize() > 0) {
				// 取回線上成員
				for (final L1PcInstance tgpc : castle_clan.getOnlineClanMember()) {
					final L1ItemInstance item = ItemTable.get().createItem(gift._itemid);
					if (item != null) {
						item.setCount(gift._count);
						// 加入背包
						tgpc.getInventory().storeItem(item);
						// 送出訊息
						tgpc.sendPackets(new S_ServerMessage("獲得攻城獎勵: " + item.getLogName()));
					}

					// 給予額外附加能力效果 by terry0412
					if (!tgpc.isCastleAbility(gift.hashCode())) {
						effectBuff(tgpc, gift, 1);
						tgpc.addCastleAbility(gift.hashCode());
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 回收其他人物該物品
	 * 
	 * @param itemid
	 */
	private void recover_item(final Gift gift) {
		try {
			// 全部線上人物
			final Collection<L1PcInstance> allpc = World.get().getAllPlayers();
			for (final Iterator<L1PcInstance> iter = allpc.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				final L1ItemInstance t1 = tgpc.getInventory().findItemId(gift._itemid);
				if (t1 != null) {
					if (t1.isEquipped()) {
						tgpc.getInventory().setEquipped(t1, false, false, false);
					}
					tgpc.getInventory().removeItem(t1);
					// \f1%0%s 消失。
					tgpc.sendPackets(new S_ServerMessage(158, t1.getLogName()));
				}

				// 移除額外附加能力效果 by terry0412
				if (tgpc.isCastleAbility(gift.hashCode())) {
					effectBuff(tgpc, gift, -1);
					tgpc.removeCastleAbility(gift.hashCode());
				}
			}

			// 刪除該物品全部數據
			CharItemsReading.get().del_item(gift._itemid);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private final void effectBuff(final L1PcInstance pc, final Gift value, final int negative) {
		pc.addAc(value._ac * negative);
		pc.addMaxHp(value._hp * negative);
		pc.addMaxMp(value._mp * negative);
		pc.addHpr(value._hpr * negative);
		pc.addMpr(value._mpr * negative);
		pc.addStr(value._str * negative);
		pc.addCon(value._con * negative);
		pc.addDex(value._dex * negative);
		pc.addWis(value._wis * negative);
		pc.addCha(value._cha * negative);
		pc.addInt(value._int * negative);
		pc.addSp(value._sp * negative);
		pc.addMr(value._mr * negative);
		pc.addHitup(value._hit_modifier * negative);
		pc.addDmgup(value._dmg_modifier * negative);
		pc.addBowHitup(value._bow_hit_modifier * negative);
		pc.addBowDmgup(value._bow_dmg_modifier * negative);

		pc.addWater(value._defense_water * negative);
		pc.addWind(value._defense_wind * negative);
		pc.addFire(value._defense_fire * negative);
		pc.addEarth(value._defense_earth * negative);

		pc.addRegistStun(value._regist_stun * negative);
		pc.addRegistStone(value._regist_stone * negative);
		pc.addRegistSleep(value._regist_sleep * negative);
		pc.addRegistFreeze(value._regist_freeze * negative);
		pc.addRegistSustain(value._regist_sustain * negative);
		pc.addRegistBlind(value._regist_blind * negative);
	}

	/**
	 * 登入遊戲給予附加能力 by terry0412
	 * 
	 * @param tgpc
	 */
	public final void login_gift(final L1PcInstance tgpc) {
		try {
			final L1Clan clan = tgpc.getClan();
			if ((clan != null) && (clan.getCastleId() > 0)) {
				final ArrayList<Gift> list = _gifts.get(clan.getCastleId());
				if (list == null) {
					return;
				}

				for (final Iterator<Gift> iter = list.iterator(); iter.hasNext();) {
					final Gift gift = iter.next();

					// 給予額外附加能力效果 by terry0412
					if (!tgpc.isCastleAbility(gift.hashCode())) {
						effectBuff(tgpc, gift, 1);
						tgpc.addCastleAbility(gift.hashCode());
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
