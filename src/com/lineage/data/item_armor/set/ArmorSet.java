package com.lineage.data.item_armor.set;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.ArmorSetTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1ArmorSets;

/**
 * 套裝設置抽象接口
 * 
 * @author daien
 */
public abstract class ArmorSet {

	private static final Log _log = LogFactory.getLog(ArmorSet.class);

	/**
	 * 套裝物品編號陣列
	 * 
	 * @param pc
	 */
	public abstract int[] get_ids();

	/**
	 * 傳回該套裝附加的效果陣列
	 * 
	 * @return
	 */
	public abstract int[] get_mode();

	/**
	 * 套裝完成效果
	 * 
	 * @param pc
	 */
	public abstract void giveEffect(L1PcInstance pc);

	/**
	 * 套裝解除效果
	 * 
	 * @param pc
	 */
	public abstract void cancelEffect(L1PcInstance pc);

	/**
	 * 套裝完成
	 * 
	 * @param pc
	 * @return
	 */
	public abstract boolean isValid(L1PcInstance pc);

	/**
	 * 是否為套裝中組件
	 * 
	 * @param id
	 * @return
	 */
	public abstract boolean isPartOfSet(int id);

	/**
	 * 是否裝備了相同界指2個
	 * 
	 * @param pc
	 * @return
	 */
	public abstract boolean isEquippedRingOfArmorSet(L1PcInstance pc);

	/**
	 * 全部套裝設置
	 * 
	 * @return
	 */
	public static HashMap<Integer, ArmorSet> getAllSet() {
		return _allSet;
	}

	// 全部套裝設置
	private static final HashMap<Integer, ArmorSet> _allSet = new HashMap<Integer, ArmorSet>();

	/**
	 * 設置資料初始化
	 */
	public static void load() {
		try {
			final L1PcInstance pc = new L1PcInstance();
			for (final L1ArmorSets armorSets : ArmorSetTable.get().getAllList()) {
				final int id = armorSets.getId();
				int[] gfxs = null;
				int times = 5;
				if (armorSets.get_gfxs() != null) {
					gfxs = armorSets.get_gfxs();
					times = armorSets.get_gfx_timesecs();
				}

				final ArmorSetImpl value = new ArmorSetImpl(id, getArray(id, armorSets.getSets()), gfxs);

				if (armorSets.getPolyId() != -1) {
					// 套裝效果:裝備者變形
					value.addEffect(new EffectPolymorph(armorSets.getPolyId()));
//					L1PolyMorph.doPoly(pc, armorSets.getPolyId(), 8000, 1);
				}

				// 攻擊與防禦

				if (armorSets.getAc() != 0) {
					// 套裝效果:防禦力增加
					value.addEffect(new EffectAc(armorSets.getAc()));
				}

				if (armorSets.getMr() != 0) {
					// 套裝效果:抗魔增加
					value.addEffect(new EffectMr(armorSets.getMr()));
				}

				// HP相關

				if (armorSets.getHp() != 0) {
					// 套裝效果:HP增加
					value.addEffect(new EffectHp(armorSets.getHp()));
				}

				if (armorSets.getHpr() != 0) {
					// 套裝效果:HP回復增加
					value.addEffect(new EffectHpR(armorSets.getHpr()));
				}

				// MP相關

				if (armorSets.getMp() != 0) {
					// 套裝效果:MP增加
					value.addEffect(new EffectMp(armorSets.getMp()));
				}

				if (armorSets.getMpr() != 0) {
					// 套裝效果:MP回復增加
					value.addEffect(new EffectMpR(armorSets.getMpr()));
				}

				// 4屬性(水屬性,風屬性,火屬性,地屬性)

				if (armorSets.getDefenseWater() != 0) {
					// 套裝效果:水屬性增加
					value.addEffect(new EffectDefenseWater(armorSets.getDefenseWater()));
				}

				if (armorSets.getDefenseWind() != 0) {
					// 套裝效果:風屬性增加
					value.addEffect(new EffectDefenseWind(armorSets.getDefenseWind()));
				}

				if (armorSets.getDefenseFire() != 0) {
					// 套裝效果:火屬性增加
					value.addEffect(new EffectDefenseFire(armorSets.getDefenseFire()));
				}

				if (armorSets.getDefenseEarth() != 0) {
					// 套裝效果:地屬性增加
					value.addEffect(new EffectDefenseEarth(armorSets.getDefenseEarth()));
				}

				// 六耐性

				if (armorSets.get_regist_stun() != 0) {
					// 套裝效果:暈眩耐性增加
					value.addEffect(new EffectRegist_Stun(armorSets.get_regist_stun()));
				}

				if (armorSets.get_regist_stone() != 0) {
					// 套裝效果:石化耐性增加
					value.addEffect(new EffectRegist_Stone(armorSets.get_regist_stone()));
				}

				if (armorSets.get_regist_sleep() != 0) {
					// 套裝效果:睡眠耐性增加
					value.addEffect(new EffectRegist_Sleep(armorSets.get_regist_sleep()));
				}

				if (armorSets.get_regist_freeze() != 0) {
					// 套裝效果:寒冰耐性增加
					value.addEffect(new EffectRegist_Freeze(armorSets.get_regist_freeze()));
				}

				if (armorSets.get_regist_sustain() != 0) {
					// 套裝效果:支撐耐性增加
					value.addEffect(new EffectRegist_Sustain(armorSets.get_regist_sustain()));
				}

				if (armorSets.get_regist_blind() != 0) {
					// 套裝效果:暗闇耐性增加
					value.addEffect(new EffectRegist_Blind(armorSets.get_regist_blind()));
				}

				// 六屬性(力量,敏捷,體質,精神,魅力,智力)

				if (armorSets.getStr() != 0) {
					// 套裝效果:力量增加
					value.addEffect(new EffectStat_Str(armorSets.getStr()));
				}

				if (armorSets.getDex() != 0) {
					// 套裝效果:敏捷增加
					value.addEffect(new EffectStat_Dex(armorSets.getDex()));
				}

				if (armorSets.getCon() != 0) {
					// 套裝效果:體質增加
					value.addEffect(new EffectStat_Con(armorSets.getCon()));
				}

				if (armorSets.getWis() != 0) {
					// 套裝效果:精神增加
					value.addEffect(new EffectStat_Wis(armorSets.getWis()));
				}

				if (armorSets.getCha() != 0) {
					// 套裝效果:魅力增加
					value.addEffect(new EffectStat_Cha(armorSets.getCha()));
				}

				if (armorSets.getIntl() != 0) {
					// 套裝效果:智力增加
					value.addEffect(new EffectStat_Int(armorSets.getIntl()));
				}

				// XXX
				if (armorSets.get_modifier_dmg() != 0) {
					// 套裝效果:套裝增加物理傷害
					value.addEffect(new Effect_Modifier_dmg(armorSets.get_modifier_dmg()));
				}

				if (armorSets.get_reduction_dmg() != 0) {
					// 套裝效果:套裝減免物理傷害
					value.addEffect(new Effect_Reduction_dmg(armorSets.get_reduction_dmg()));
				}

				if (armorSets.get_magic_modifier_dmg() != 0) {
					// 套裝效果:套裝增加魔法傷害
					value.addEffect(new Effect_Magic_modifier_dmg(armorSets.get_magic_modifier_dmg()));
				}

				if (armorSets.get_magic_reduction_dmg() != 0) {
					// 套裝效果:套裝減免魔法傷害
					value.addEffect(new Effect_Magic_reduction_dmg(armorSets.get_magic_reduction_dmg()));
				}

				if (armorSets.get_bow_modifier_dmg() != 0) {
					// 套裝效果:套裝增加弓的物理傷害
					value.addEffect(new Effect_Bow_modifier_dmg(armorSets.get_bow_modifier_dmg()));
				}
				
				// 161228 新增8特殊能力 
				if (armorSets.get_physicsDmgUp() != 0) {
					// 套裝效果:增加物理傷害 %
					value.addEffect(new Effect_PhysicsDmgUp(armorSets.get_physicsDmgUp()));
				}
				
				if (armorSets.get_magicDmgUp() != 0) {
					// 套裝效果:增加魔法傷害 %
					value.addEffect(new Effect_MagicDmgUp(armorSets.get_magicDmgUp()));
				}
				
				if (armorSets.get_physicsDmgDown() != 0) {
					// 套裝效果:增加物理傷害減免 %
					value.addEffect(new Effect_PhysicsDmgDown(armorSets.get_physicsDmgDown()));
				}
				
				if (armorSets.get_magicDmgDown() != 0) {
					// 套裝效果:增加魔法傷害減免 %
					value.addEffect(new Effect_MagicDmgDown(armorSets.get_magicDmgDown()));
				}
				
				if (armorSets.get_magicHitUp() != 0) {
					// 套裝效果:增加有害魔法命中 %
					value.addEffect(new Effect_MagicHitUp(armorSets.get_magicHitUp()));
				}
				
				if (armorSets.get_magicHitDown() != 0) {
					// 套裝效果:增加有害魔法抵抗 %
					value.addEffect(new Effect_MagicHitDown(armorSets.get_magicHitDown()));
				}
				
				if (armorSets.get_physicsDoubleHit() != 0) {
					// 套裝效果:增加物理傷害爆擊(1.5倍) %
					value.addEffect(new Effect_PhysicsDoubleHit(armorSets.get_physicsDoubleHit()));
				}
				
				if (armorSets.get_magicDoubleHit() != 0) {
					// 套裝效果:增加魔法傷害爆擊(1.5倍) %
					value.addEffect(new Effect_MagicDoubleHit(armorSets.get_magicDoubleHit()));
				}

				_allSet.put(armorSets.getId(), value);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			// 具有套裝設置的誤件 加入效果數字陣列
			ItemTable.get().se_mode();
		}
	}

	/**
	 * 將文字串 轉為數字陣列
	 * 
	 * @param id 套件編號
	 * @param s 轉換的字串
	 * @return
	 */
	private static int[] getArray(final int id, final String s) {
		// 根据给定正则表达式的匹配拆分此字符串。
		final String[] clientStrAry = s.split(",");
		final int[] array = new int[clientStrAry.length];
		try {
			for (int i = 0; i < clientStrAry.length; i++) {
				array[i] = Integer.parseInt(clientStrAry[i]);
			}

		} catch (final Exception e) {
			_log.error("編號:" + id + " 套件設置錯誤!!檢查資料庫!!", e);
		}
		return array;
	}

	/**
	 * 將文字串 轉為數字陣列
	 * 
	 * @param s
	 * @param sToken
	 * @return
	 */
	/*
	 * private static int[] getArray(final String s, final String sToken) {
	 * final StringTokenizer st = new StringTokenizer(s, sToken); //
	 * 计算在生成异常之前可以调用此 tokenizer 的 nextToken 方法的次数。当前位置没有提前。 final int size =
	 * st.countTokens(); String temp = null; final int[] array = new int[size];
	 * for (int i = 0; i < size; i++) { temp = st.nextToken(); array[i] =
	 * Integer.parseInt(temp); } return array; }
	 */
}