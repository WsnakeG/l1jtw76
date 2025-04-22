package com.lineage.data.item_etcitem.extra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.sql.CharBuffTable;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1ItemBuff;
import com.lineage.server.templates.L1ServerEtcItem;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;

/**
 * 道具狀態系統
 *
 * @author jeLiu
 *
 */
public class ItemBuffTable {

	private static final Log _log = LogFactory.getLog(ItemBuffTable.class);

	public static final Map<Integer, L1ItemBuff> _list = new HashMap<Integer, L1ItemBuff>();

	public static final Map<Integer, ArrayList<Integer>> _type = new HashMap<Integer, ArrayList<Integer>>();
	
	private static final Map<Integer, L1ItemBuff> mapetcitem = Maps.newHashMap();

	private static ItemBuffTable _instance;

	public static ItemBuffTable get() {
		if (_instance == null) {
			_instance = new ItemBuffTable();
		}
		return _instance;
	}

//	private ItemBuffTable() {
//		load();
//	}

	public static void load() {
		PerformanceTimer timer = new PerformanceTimer();
		Connection co = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactory.get().getConnection();
			pstm = co.prepareStatement("SELECT * FROM `群版_item_buff`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int item_id = rs.getInt("item_id");
				final String startmsg = rs.getString("start_msg");
				final String stopmsg = rs.getString("stop_msg");
				final int viplevel = rs.getInt("vip_level");
				final int type = rs.getInt("type");
				final int type_mod = rs.getInt("type_mod");
				final int buff_time = rs.getInt("buff_time");
				final int buff_gfx = rs.getInt("buff_gfx");
				final int buff_iconid = rs.getInt("buff_iconid");
				final int buff_stringid = rs.getInt("buff_stringid");
				final int open_string = rs.getInt("open_string");
				final boolean buff_save = rs.getBoolean("buff_save");
				final int poly = rs.getInt("poly");
				final int str = rs.getInt("add_str");
				final int dex = rs.getInt("add_dex");
				final int con = rs.getInt("add_con");
				final int intel = rs.getInt("add_int");
				final int wis = rs.getInt("add_wis");
				final int cha = rs.getInt("add_cha");
				final int ac = rs.getInt("add_ac");
				final int hp = rs.getInt("add_hp");
				final int mp = rs.getInt("add_mp");
				final int hpr = rs.getInt("add_hpr");
				final int mpr = rs.getInt("add_mpr");
				final int mr = rs.getInt("add_mr");
				final int sp = rs.getInt("add_sp");
				final int dmg = rs.getInt("add_dmg");
				final int bow_dmg = rs.getInt("add_bow_dmg");
				final int hit = rs.getInt("add_hit");
				final int bow_hit = rs.getInt("add_bow_hit");
				final int dmg_r = rs.getInt("add_dmg_r");
				final int magic_r = rs.getInt("add_magic_r");
				final int fire = rs.getInt("add_fire");
				final int wind = rs.getInt("add_wind");
				final int earth = rs.getInt("add_earth");
				final int water = rs.getInt("add_water");
				final int stun = rs.getInt("add_stun");
				final int stone = rs.getInt("add_stone");
				final int sleep = rs.getInt("add_sleep");
				final int freeze = rs.getInt("add_freeze");
				final int sustain = rs.getInt("add_sustain");
				final int blind = rs.getInt("add_blind");
				final int pvpdmg = rs.getInt("add_pvpdmg");
				final int pvpdmg_r = rs.getInt("add_pvpdmg_r");
				final int exp = rs.getInt("add_exp");
				
				final boolean deleteafteruse = rs.getBoolean("deleteafteruse");
				final int double_dmg_chance = rs.getInt("double_dmg_chance");
				final int physicsDmgUp = rs.getInt("physicsDmgUp");
				final int magicDmgUp = rs.getInt("magicDmgUp");
				final int physicsDmgDown = rs.getInt("physicsDmgDown");
				final int magicDmgDown = rs.getInt("magicDmgDown");
				final int magicHitUp = rs.getInt("magicHitUp");
				final int magicHitDown = rs.getInt("magicHitDown");
				final int physicsDoubleHit = rs.getInt("physicsDoubleHit");
				final int magicDoubleHit = rs.getInt("magicDoubleHit");
				final int InfluenceLuck = rs.getInt("influence_luck");
				
				final L1ItemBuff vip = new L1ItemBuff();
				vip.setStartMsg(startmsg);
				vip.setStopMsg(stopmsg);
				vip.setVipLevel(viplevel);
				vip.set_type(type);
				vip.set_type_mod(type_mod);
				vip.set_buff_time(buff_time);
				vip.set_buff_gfx(buff_gfx);
				vip.set_buff_iconid(buff_iconid);
				vip.set_buff_stringid(buff_stringid);
				vip.set_open_string(open_string);
				vip.set_buff_save(buff_save);
				vip.set_poly(poly);
				vip.set_type(type);
				vip.set_str(str);
				vip.set_dex(dex);
				vip.set_con(con);
				vip.set_intel(intel);
				vip.set_wis(wis);
				vip.set_cha(cha);
				vip.set_ac(ac);
				vip.set_hp(hp);
				vip.set_mp(mp);
				vip.set_hpr(hpr);
				vip.set_mpr(mpr);
				vip.set_mr(mr);
				vip.set_sp(sp);
				vip.set_dmg(dmg);
				vip.set_bow_dmg(bow_dmg);
				vip.set_hit(hit);
				vip.set_bow_hit(bow_hit);
				vip.set_dmg_r(dmg_r);
				vip.set_magic_r(magic_r);
				vip.set_fire(fire);
				vip.set_wind(wind);
				vip.set_earth(earth);
				vip.set_water(water);
				vip.set_stun(stun);
				vip.set_stone(stone);
				vip.set_sleep(sleep);
				vip.set_freeze(freeze);
				vip.set_sustain(sustain);
				vip.set_blind(blind);
				vip.set_exp(exp);
				vip.set_pvpdmg(pvpdmg);
				vip.set_pvpdmg_r(pvpdmg_r);
				
				vip.set_deleteafteruse(deleteafteruse);
				vip.set_double_dmg_chance(double_dmg_chance);
				vip.set_physicsDmgUp(physicsDmgUp);
				vip.set_magicDmgUp(magicDmgUp);
				vip.set_physicsDmgDown(physicsDmgDown);
				vip.set_magicDmgDown(magicDmgDown);
				vip.set_magicHitUp(magicHitUp);
				vip.set_magicHitDown(magicHitDown);
				vip.set_physicsDoubleHit(physicsDoubleHit);
				vip.set_magicDoubleHit(magicDoubleHit);
				vip.set_InfluenceLuck(InfluenceLuck);
				
				_list.put(item_id, vip);
				mapetcitem.put(item_id, vip);
				ArrayList<Integer> map = _type.get(type);
				if (map == null) {
					map = new ArrayList<Integer>();
					map.add(item_id);
					_type.put(type, map);
				} else {
					map.add(item_id);
				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(co);
		}
//		_log.info("載入進階版效果道具數量: " + _list.size());
		_log.info("Load--->群版_item_buff設定, " + "資料共" + _list.size() + "筆, " + "花費(" + timer.get() + "ms) OK");
	}

	public boolean checkItem(final int item_id) {
		return _list.containsKey(item_id);
	}
	
	public L1ItemBuff getItem(final int itemid) {
		return mapetcitem.get(Integer.valueOf(itemid));
	}

	/**
	 * 道具顯示信息
	 * 
	 * @param item_id
	 * @return
	 */
	public L1ItemBuff getUseEX(final int item_id) {
		if (_list.isEmpty())
			return null;
		if (_list.containsKey(Integer.valueOf(item_id)))
			return _list.get(Integer.valueOf(item_id));
		else
			return null;
	}

	public void checkBuffSave(final L1PcInstance pc) {
		for (final Integer skillId : _list.keySet()) {
			if (_list.get(skillId).is_buff_save()) {
				final int timeSec = pc.getSkillEffectTimeSec(skillId.intValue());
				if (timeSec > 0) {
					final int poly_id = _list.get(skillId).get_poly();
					CharBuffTable.storeBuff(pc.getId(), skillId.intValue(), timeSec, poly_id);
				}
			}
		}
	}

	/**
	 * 增加效果
	 *
	 * @param pc
	 * @param item_id
	 * @param buff_time
	 * @return
	 */
	public boolean add(final L1PcInstance pc, final int item_id, final int buff_time) {

		if (!_list.containsKey(item_id)) {
			return false;
		}

		final L1ItemBuff value = _list.get(item_id);

		if (value.getVipLevel() != 0) { // 0不判斷VIP等級，0以上判斷VIP等級
			if (pc.get_vipLevel() < value.getVipLevel()) {
				pc.sendPackets(new S_ServerMessage("\\aDVIP(" + value.getVipLevel() + ")以上玩家才能使用此道具。"));
				return false;
			}
		}

		if (pc.hasSkillEffect(item_id)) {
			if (value.get_type_mod() == 0) { // 設0提示剩餘時間 其它為覆蓋效果
				//pc.sendPackets(new S_ServerMessage("\\aD道具狀態剩餘時間(秒): \\aE" + pc.getSkillEffectTimeSec(item_id)));
				return false;
			}
			pc.removeSkillEffect(item_id);
		}

		if (value.get_type() != 0) { // 0不判斷類別，0以上才判斷
			for (final Integer buff_id : _type.get(value.get_type())) {
				if (pc.hasSkillEffect(buff_id.intValue())) {
					if (value.get_type_mod() == 0) { // 設0提示剩餘時間 其它為覆蓋效果
						pc.sendPackets(new S_ServerMessage("\\aD同類道具狀態剩餘時間(秒): \\aE" + pc.getSkillEffectTimeSec(buff_id.intValue())));
						return false;
					}
					pc.removeSkillEffect(buff_id.intValue());
				}
			}
		}

		if (buff_time != 0) {
			pc.setSkillEffect(item_id, buff_time * 1000);
			if (value.get_buff_iconid() == 0 && value.get_buff_stringid() == 0) { // 有自訂的狀態圖示和訊息時不顯示
				pc.sendPackets(new S_ServerMessage("\\aD道具狀態剩餘時間(秒): \\aE" + pc.getSkillEffectTimeSec(item_id))); // 重登時
			}
		} else {
			if (value.get_buff_gfx() != 0) { // 特效
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), value.get_buff_gfx()));
			}
			pc.setSkillEffect(item_id, value.get_buff_time() * 1000);
		}

		if (value.get_buff_iconid() != 0 && value.get_buff_stringid() != 0) { // 自訂狀態圖示和訊息
			if (value.get_open_string() == 0) { // 有設狀態圖示和訊息的使用和登入是否顯示圖示裡面的訊息內容
				pc.sendPackets(new S_InventoryIcon(value.get_buff_iconid(), true, value.get_buff_stringid(), pc.getSkillEffectTimeSec(item_id))); // 重登不會有訊息
			} else {
				pc.sendPackets(new S_InventoryIcon(value.get_buff_iconid(), true, value.get_buff_stringid(), value.get_buff_stringid(), pc.getSkillEffectTimeSec(item_id))); // 重登有訊息
			}
		}

		final boolean status = false;

		boolean status2 = false;

		boolean spmr = false;

		boolean attr = false;

		final StringBuilder name = new StringBuilder();

		if (value.get_poly() != -1) { // 變身
			L1PolyMorph.doPoly(pc, value.get_poly(), value.get_buff_time(), 1);
		}

		if (value.get_hp() != 0) { // 體力上限
			pc.addMaxHp(value.get_hp());
			name.append(" 體力上限+");
			name.append(value.get_hp());
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
		}

		if (value.get_mp() != 0) { // 魔力上限
			pc.addMaxMp(value.get_mp());
			name.append(" 魔力上限+");
			name.append(value.get_mp());
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		}

		if (value.get_hpr() != 0) { // 體力回復量
			pc.addHpr(value.get_hpr());
			name.append(" 體力回復量+");
			name.append(value.get_hpr());
		}

		if (value.get_mpr() != 0) { // 魔力回復量
			pc.addMpr(value.get_mpr());
			name.append(" 魔力回復量+");
			name.append(value.get_mpr());
		}

		if (value.get_ac() != 0) { // 防禦
			pc.addAc(-value.get_ac());
			name.append(" 防禦+");
			name.append(value.get_ac());
			attr = true;
		}

		if (value.get_sp() != 0) { // 魔法攻擊力
			pc.addSp(value.get_sp());
			name.append(" 魔攻+");
			name.append(value.get_sp());
			spmr = true;
		}

		if (value.get_mr() != 0) { // 額外魔法防禦
			pc.addMr(value.get_mr());
			name.append(" 額外魔法防禦+");
			name.append(value.get_mr());
			spmr = true;
		}

		if (value.get_str() != 0) { // 力量
			pc.addStr(value.get_str());
			name.append(" 力量+");
			name.append(value.get_str());
			status2 = true;
		}

		if (value.get_dex() != 0) { // 敏捷
			pc.addDex(value.get_dex());
			name.append(" 敏捷+");
			name.append(value.get_dex());
			status2 = true;
		}

		if (value.get_con() != 0) { // 體質
			pc.addCon(value.get_con());
			name.append(" 體質+");
			name.append(value.get_con());
			status2 = true;
		}

		if (value.get_wis() != 0) { // 精神
			pc.addWis(value.get_wis());
			name.append(" 精神+");
			name.append(value.get_wis());
			status2 = true;
		}

		if (value.get_intel() != 0) { // 智力
			pc.addInt(value.get_intel());
			name.append(" 智力+");
			name.append(value.get_intel());
			status2 = true;
		}

		if (value.get_cha() != 0) { // 魅力
			pc.addCha(value.get_cha());
			name.append(" 魅力+");
			name.append(value.get_cha());
			status2 = true;
		}

		if (value.get_dmg() != 0) { // 近遠距離攻擊
			pc.addDmgup(value.get_dmg());
			name.append(" 近遠距離攻擊+");
			name.append(value.get_dmg());
		}

		if (value.get_bow_dmg() != 0) { // 遠距離攻擊力
			pc.addBowDmgup(value.get_bow_dmg());
			name.append(" 遠距離攻擊力+");
			name.append(value.get_bow_dmg());
		}

		if (value.get_hit() != 0) { // 近距離命中
			pc.addHitup(value.get_hit());
			name.append(" 近距離命中+");
			name.append(value.get_hit());
		}

		if (value.get_bow_hit() != 0) { // 遠距離命中
			pc.addBowHitup(value.get_bow_hit());
			name.append(" 遠距離命中+");
			name.append(value.get_bow_hit());
		}

		if (value.get_dmg_r() != 0) { // 物理傷害減免
			pc.addother_ReductionDmg(value.get_dmg_r());
			name.append(" 物理傷害減免+");
			name.append(value.get_dmg_r());
		}

		if (value.get_magic_r() != 0) { // 魔法傷害減免
			pc.add_magic_reduction_dmg(value.get_magic_r());
			name.append(" 魔法傷害減免+");
			name.append(value.get_magic_r());
		}

		if (value.get_water() != 0) { // 水屬性抗性
			pc.addWater(value.get_water());
			name.append(" 水屬性抗性+");
			name.append(value.get_water());
			attr = true;
		}

		if (value.get_fire() != 0) { // 火屬性抗性
			pc.addFire(value.get_fire());
			name.append(" 火屬性抗性+");
			name.append(value.get_fire());
			attr = true;
		}

		if (value.get_wind() != 0) { // 風屬性抗性
			pc.addWind(value.get_wind());
			name.append(" 風屬性抗性+");
			name.append(value.get_wind());
			attr = true;
		}

		if (value.get_earth() != 0) { // 地屬性抗性
			pc.addEarth(value.get_earth());
			name.append(" 地屬性抗性+");
			name.append(value.get_earth());
			attr = true;
		}

		if (value.get_stun() != 0) { // 昏迷耐性
			pc.addRegistStun(value.get_stun());
			name.append(" 昏迷耐性+");
			name.append(value.get_stun());
		}

		if (value.get_stone() != 0) { // 石化耐性
			pc.addRegistStone(value.get_stone());
			name.append(" 石化耐性+");
			name.append(value.get_stone());
		}

		if (value.get_sleep() != 0) { // 睡眠耐性
			pc.addRegistSleep(value.get_sleep());
			name.append(" 睡眠耐性+");
			name.append(value.get_sleep());
		}

		if (value.get_freeze() != 0) { // 寒冰耐性
			pc.addRegistFreeze(value.get_freeze());
			name.append(" 寒冰耐性+");
			name.append(value.get_freeze());
		}

		if (value.get_sustain() != 0) { // 支撐耐性
			pc.addRegistSustain(value.get_sustain());
			name.append(" 支撐耐性+");
			name.append(value.get_sustain());
		}

		if (value.get_blind() != 0) { // 暗黑耐性
			pc.addRegistBlind(value.get_blind());
			name.append(" 暗黑耐性+");
			name.append(value.get_blind());
		}

		if (value.get_pvpdmg() != 0) { // 增加PVP傷害
			pc.addPVPdmg(value.get_pvpdmg());
			name.append(" PVP傷害+");
			name.append(value.get_pvpdmg());
		}

		if (value.get_pvpdmg_r() != 0) { // 減免PVP傷害
			pc.addPVPdmgReduction(value.get_pvpdmg_r());
			name.append(" PVP減免+");
			name.append(value.get_pvpdmg_r());
		}

		if (value.get_exp() != 0) { // 狩獵經驗值
			pc.addExpRateToPc(value.get_exp());
			name.append(" 狩獵經驗值+");
			name.append(value.get_exp());
			name.append("% ");
		}
		
		if (value.get_double_dmg_chance() != 0) { // 雙擊效果
			pc.add_double_dmg_chance(1);
			name.append(" 獲得雙擊效果");
		}
		
		if (value.get_physicsDmgUp() != 0) { // 物理傷害增加+%
			pc.addPhysicsDmgUp(value.get_physicsDmgUp());
			name.append(" 物理傷害+");
			name.append(value.get_physicsDmgUp());
			name.append("% ");
		}
		
		if (value.get_magicDmgUp() != 0) { // 魔法傷害增加+%
			pc.addMagicDmgUp(value.get_magicDmgUp());
			name.append(" 魔法傷害+");
			name.append(value.get_magicDmgUp());
			name.append("% ");
		}
		
		if (value.get_physicsDmgDown() != 0) { // 物理傷害減免+%
			pc.addPhysicsDmgDown(value.get_physicsDmgDown());
			name.append(" 物理傷害減免+");
			name.append(value.get_physicsDmgDown());
			name.append("% ");
		}
		
		if (value.get_magicDmgDown() != 0) { // 魔法傷害減免+%
			pc.addMagicDmgDown(value.get_magicDmgDown());
			name.append(" 魔法傷害減免+");
			name.append(value.get_magicDmgDown());
			name.append("% ");
		}
		
		if (value.get_magicHitUp() != 0) { // 有害魔法成功率+%
			pc.addMagicHitUp(value.get_magicHitUp());
			name.append(" 有害魔法成功率+");
			name.append(value.get_magicHitUp());
			name.append("% ");
		}
		
		if (value.get_magicHitDown() != 0) { // 抵抗有害魔法成功率+%
			pc.addMagicHitDown(value.get_magicHitDown());
			name.append(" 抵抗有害魔法成功率+");
			name.append(value.get_magicHitDown());
			name.append("% ");
		}
		
		if (value.get_physicsDoubleHit() != 0) { // 物理暴擊發動機率+% (發動後普攻傷害*1.5倍)
			pc.addMagicDoubleHit(value.get_physicsDoubleHit());
			name.append(" 物理暴擊發動機率+");
			name.append(value.get_physicsDoubleHit());
			name.append("% ");
		}
		
		if (value.get_magicDoubleHit() != 0) { // 魔法暴擊發動機率+% (發動後技能傷害*1.5倍)
			pc.addPhysicsDoubleHit(value.get_magicDoubleHit());
			name.append(" 魔法暴擊發動機率+");
			name.append(value.get_magicDoubleHit());
			name.append("% ");
		}
		
		if (value.get_InfluenceLuck() != 0) { // 增加打寶幸運值
			pc.addInfluenceLuck(value.get_InfluenceLuck());
			name.append(" 增加打寶幸運值");
		}
		
		if (status) {
			pc.sendPackets(new S_OwnCharStatus(pc));
		} else {
			if (status2) {
				pc.sendDetails();
				pc.sendPackets(new S_OwnCharStatus2(pc));
			}
			if (attr) {
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		if (spmr) {
			pc.sendPackets(new S_SPMR(pc));
		}

		if (value.getStartMsg() != null) { // 空值時什麼都不顯示
			pc.sendPackets(new S_ServerMessage(value.getStartMsg())); // 自設訊息
		} else {
			pc.sendPackets(new S_ServerMessage(name.toString())); // 預設訊息
		}

		return true;
	}

	/**
	 * 移除效果
	 *
	 * @param pc
	 * @param item_id
	 */
	public void remove(final L1PcInstance pc, final int item_id) {

		final L1ItemBuff value = _list.get(item_id);

		final boolean status = false;

		boolean status2 = false;

		boolean spmr = false;

		boolean attr = false;

		final StringBuilder name = new StringBuilder();

		if (value.get_hp() != 0) { // 體力上限
			pc.addMaxHp(-value.get_hp());
			name.append(" 體力上限-");
			name.append(value.get_hp());
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
		}
		if (value.get_mp() != 0) { // 魔力上限
			pc.addMaxMp(-value.get_mp());
			name.append(" 魔力上限-");
			name.append(value.get_mp());
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		}
		if (value.get_hpr() != 0) { // 體力回復量
			pc.addHpr(-value.get_hpr());
			name.append(" 體力回復量-");
			name.append(value.get_hpr());
		}
		if (value.get_mpr() != 0) { // 魔力回復量
			pc.addMpr(-value.get_mpr());
			name.append(" 魔力回復量-");
			name.append(value.get_mpr());
		}
		if (value.get_ac() != 0) { // 防禦
			pc.addAc(value.get_ac());
			name.append(" 防禦-");
			name.append(value.get_ac());
			attr = true;
		}
		if (value.get_sp() != 0) { // 魔法攻擊力
			pc.addSp(-value.get_sp());
			name.append(" 魔攻-");
			name.append(value.get_sp());
			spmr = true;
		}
		if (value.get_mr() != 0) { // 額外魔法防禦
			pc.addMr(-value.get_mr());
			name.append(" 魔防-");
			name.append(value.get_mr());
			spmr = true;
		}
		if (value.get_str() != 0) { // 力量
			pc.addStr(-value.get_str());
			name.append(" 力量-");
			name.append(value.get_str());
			status2 = true;
		}
		if (value.get_dex() != 0) { // 敏捷
			pc.addDex(-value.get_dex());
			name.append(" 敏捷-");
			name.append(value.get_dex());
			status2 = true;
		}
		if (value.get_con() != 0) { // 體質
			pc.addCon(-value.get_con());
			name.append(" 體質-");
			name.append(value.get_con());
			status2 = true;
		}
		if (value.get_wis() != 0) { // 精神
			pc.addWis(-value.get_wis());
			name.append(" 精神-");
			name.append(value.get_wis());
			status2 = true;
		}
		if (value.get_intel() != 0) { // 智力
			pc.addInt(-value.get_intel());
			name.append(" 智力-");
			name.append(value.get_intel());
			status2 = true;
		}
		if (value.get_cha() != 0) { // 魅力
			pc.addCha(-value.get_cha());
			name.append(" 魅力-");
			name.append(value.get_cha());
			status2 = true;
		}
		if (value.get_dmg() != 0) { // 近遠距離攻擊
			pc.addDmgup(-value.get_dmg());
			name.append(" 近遠距離攻擊-");
			name.append(value.get_dmg());
		}
		if (value.get_bow_dmg() != 0) { // 遠距離攻擊力
			pc.addBowDmgup(-value.get_bow_dmg());
			name.append(" 遠距離攻擊力-");
			name.append(value.get_bow_dmg());
		}
		if (value.get_hit() != 0) { // 近距離命中
			pc.addHitup(-value.get_hit());
			name.append(" 近距離命中-");
			name.append(value.get_hit());
		}
		if (value.get_bow_hit() != 0) { // 遠距離命中
			pc.addBowHitup(-value.get_bow_hit());
			name.append(" 遠距離命中-");
			name.append(value.get_bow_hit());
		}
		if (value.get_dmg_r() != 0) { // 物理傷害減免
			pc.addother_ReductionDmg(-value.get_dmg_r());
			name.append(" 物理傷害減免-");
			name.append(value.get_dmg_r());
		}
		if (value.get_magic_r() != 0) { // 魔法傷害減免
			pc.add_magic_reduction_dmg(-value.get_magic_r());
			name.append(" 魔法傷害減免-");
			name.append(value.get_magic_r());
		}
		if (value.get_water() != 0) { // 水屬性抗性
			pc.addWater(-value.get_water());
			name.append(" 水屬性抗性-");
			name.append(value.get_water());
			attr = true;
		}
		if (value.get_fire() != 0) { // 火屬性抗性
			pc.addFire(-value.get_fire());
			name.append(" 火屬性抗性-");
			name.append(value.get_fire());
			attr = true;
		}
		if (value.get_wind() != 0) { // 風屬性抗性
			pc.addWind(-value.get_wind());
			name.append(" 風屬性抗性-");
			name.append(value.get_wind());
			attr = true;
		}
		if (value.get_earth() != 0) { // 地屬性抗性
			pc.addEarth(-value.get_earth());
			name.append(" 地屬性抗性-");
			name.append(value.get_earth());
			attr = true;
		}
		if (value.get_stun() != 0) { // 昏迷耐性
			pc.addRegistStun(-value.get_stun());
			name.append(" 昏迷耐性-");
			name.append(value.get_stun());
		}
		if (value.get_stone() != 0) { // 石化耐性
			pc.addRegistStone(-value.get_stone());
			name.append(" 石化耐性-");
			name.append(value.get_stone());
		}
		if (value.get_sleep() != 0) { // 睡眠耐性
			pc.addRegistSleep(-value.get_sleep());
			name.append(" 睡眠耐性-");
			name.append(value.get_sleep());
		}
		if (value.get_freeze() != 0) { // 寒冰耐性
			pc.addRegistFreeze(-value.get_freeze());
			name.append(" 寒冰耐性-");
			name.append(value.get_freeze());
		}
		if (value.get_sustain() != 0) { // 支撐耐性
			pc.addRegistSustain(-value.get_sustain());
			name.append(" 支撐耐性-");
			name.append(value.get_sustain());
		}
		if (value.get_blind() != 0) { // 暗黑耐性
			pc.addRegistBlind(-value.get_blind());
			name.append(" 暗黑耐性-");
			name.append(value.get_blind());
		}
		if (value.get_pvpdmg() != 0) { // 增加PVP傷害
			pc.addPVPdmg(-value.get_pvpdmg());
			name.append(" PVP傷害-");
			name.append(value.get_pvpdmg());
		}

		if (value.get_pvpdmg_r() != 0) { // 減免PVP傷害
			pc.addPVPdmgReduction(-value.get_pvpdmg_r());
			name.append(" PVP減免-");
			name.append(value.get_pvpdmg_r());
		}
		if (value.get_exp() != 0) { // 狩獵經驗值
			pc.addExpRateToPc(-value.get_exp());
			name.append(" 狩獵經驗值-");
			name.append(value.get_exp());
			name.append("% ");
		}
		
		if (value.get_double_dmg_chance() != 0) { // 雙擊效果
			pc.add_double_dmg_chance(-1);
			name.append(" 失去雙擊效果");
		}
		
		if (value.get_physicsDmgUp() != 0) { // 物理傷害增加+%
			pc.addPhysicsDmgUp(-value.get_physicsDmgUp());
			name.append(" 物理傷害-");
			name.append(value.get_physicsDmgUp());
			name.append("% ");
		}
		
		if (value.get_magicDmgUp() != 0) { // 魔法傷害增加+%
			pc.addMagicDmgUp(-value.get_magicDmgUp());
			name.append(" 魔法傷害-");
			name.append(value.get_magicDmgUp());
			name.append("% ");
		}
		
		if (value.get_physicsDmgDown() != 0) { // 物理傷害減免+%
			pc.addPhysicsDmgDown(-value.get_physicsDmgDown());
			name.append(" 物理傷害減免-");
			name.append(value.get_physicsDmgDown());
			name.append("% ");
		}
		
		if (value.get_magicDmgDown() != 0) { // 魔法傷害減免+%
			pc.addMagicDmgDown(-value.get_magicDmgDown());
			name.append(" 魔法傷害減免-");
			name.append(value.get_magicDmgDown());
			name.append("% ");
		}
		
		if (value.get_magicHitUp() != 0) { // 有害魔法成功率+%
			pc.addMagicHitUp(-value.get_magicHitUp());
			name.append(" 有害魔法成功率-");
			name.append(value.get_magicHitUp());
			name.append("% ");
		}
		
		if (value.get_magicHitDown() != 0) { // 抵抗有害魔法成功率+%
			pc.addMagicHitDown(-value.get_magicHitDown());
			name.append(" 抵抗有害魔法成功率-");
			name.append(value.get_magicHitDown());
			name.append("% ");
		}
		
		if (value.get_physicsDoubleHit() != 0) { // 物理暴擊發動機率+% (發動後普攻傷害*1.5倍)
			pc.addMagicDoubleHit(-value.get_physicsDoubleHit());
			name.append(" 物理暴擊發動機率-");
			name.append(value.get_physicsDoubleHit());
			name.append("% ");
		}
		
		if (value.get_magicDoubleHit() != 0) { // 魔法暴擊發動機率+% (發動後技能傷害*1.5倍)
			pc.addPhysicsDoubleHit(-value.get_magicDoubleHit());
			name.append(" 魔法暴擊發動機率-");
			name.append(value.get_magicDoubleHit());
			name.append("% ");
		}
		
		if (value.get_InfluenceLuck() != 0) { // 增加打寶幸運值
			pc.addInfluenceLuck(-value.get_InfluenceLuck());
			name.append(" 減少打寶幸運值");
		}

		if (status) {
			pc.sendPackets(new S_OwnCharStatus(pc));
		} else {
			if (status2) {
				pc.sendDetails();
				pc.sendPackets(new S_OwnCharStatus2(pc));
			}
			if (attr) {
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		if (spmr) {
			pc.sendPackets(new S_SPMR(pc));
		}

		if (value.getStopMsg() != null) { // 空值時什麼都不顯示
			pc.sendPackets(new S_ServerMessage(value.getStopMsg())); // 自設訊息
		} else {
			pc.sendPackets(new S_ServerMessage(name.toString())); // 預設訊息
		}
	}
}
