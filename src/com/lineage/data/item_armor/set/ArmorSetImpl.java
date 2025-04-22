package com.lineage.data.item_armor.set;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 套裝使用的判斷
 * @author daien
 *
 */
public class ArmorSetImpl extends ArmorSet {

	private static final Log _log = LogFactory.getLog(ArmorSetImpl.class);
	
	// 套裝編號
	private final int _id;
	
	// 套裝物品編號陣列
	private final int _ids[];
	
	// 套裝效果組合
	private final ArrayList<ArmorSetEffect> _effects;
	
	// 套裝效果動畫
	private final int _gfxids[];

	/**
	 * 套裝的判斷設置
	 * @param ids 套裝物品編號陣列
	 */
	protected ArmorSetImpl(final int id, final int ids[], final int gfxids[]) {
		this._id = id;
		this._ids = ids;
		this._gfxids = gfxids;
		this._effects = new ArrayList<ArmorSetEffect>();
	}

	/**
	 * 套裝編號
	 * @return
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * 套裝物品編號陣列
	 * @return
	 */
	@Override
	public int[] get_ids() {
		return _ids;
	}
	
	/**
	 * 加入該套裝效果組合
	 * @param effect
	 */
	public void addEffect(final ArmorSetEffect effect) {
		this._effects.add(effect);
	}

	/**
	 * 移除該套裝效果組合
	 * @param effect
	 */
	public void removeEffect(final ArmorSetEffect effect) {
		this._effects.remove(effect);
	}
	
	/**
	 * 傳回該套裝附加的效果陣列
	 * @return
	 */
	@Override
	public int[] get_mode() {
		int[] mode = new int[31];
		for (final ArmorSetEffect effect : _effects) {
			// 六屬性(力量,敏捷,體質,精神,魅力,智力)

			if (effect instanceof EffectStat_Str) {
				// 套裝效果:力量增加
				mode[0] = effect.get_mode();
			}

			if (effect instanceof EffectStat_Dex) {
				// 套裝效果:敏捷增加
				mode[1] = effect.get_mode();
			}

			if (effect instanceof EffectStat_Con) {
				// 套裝效果:體質增加
				mode[2] = effect.get_mode();
			}

			if (effect instanceof EffectStat_Wis) {
				// 套裝效果:精神增加
				mode[3] = effect.get_mode();
			}

			if (effect instanceof EffectStat_Int) {
				// 套裝效果:智力增加
				mode[4] = effect.get_mode();
			}

			if (effect instanceof EffectStat_Cha) {
				// 套裝效果:魅力增加
				mode[5] = effect.get_mode();
			}
			
			// HP相關
			if (effect instanceof EffectHp) {
				// 套裝效果:HP增加
				mode[6] = effect.get_mode();
			}
			
			// MP相關
			if (effect instanceof EffectMp) {
				// 套裝效果:MP增加
				mode[7] = effect.get_mode();
			}
			
			if (effect instanceof EffectMr) {
				// 套裝效果:抗魔增加
				mode[8] = effect.get_mode();
			}
			
			if (effect instanceof EffectSp) {
				// SP(魔攻) XXX
				mode[9] = effect.get_mode();
			}
			
			if (effect instanceof EffectHaste) {
				// 加速效果 XXX
				mode[10] = effect.get_mode();
			}
			
			// 4屬性(水屬性,風屬性,火屬性,地屬性)

			if (effect instanceof EffectDefenseFire) {
				// 套裝效果:火屬性增加
				mode[11] = effect.get_mode();
			}

			if (effect instanceof EffectDefenseWater) {
				// 套裝效果:水屬性增加
				mode[12] = effect.get_mode();
			}

			if (effect instanceof EffectDefenseWind) {
				// 套裝效果:風屬性增加
				mode[13] = effect.get_mode();
			}

			if (effect instanceof EffectDefenseEarth) {
				// 套裝效果:地屬性增加
				mode[14] = effect.get_mode();
			}

			// 六耐性

			if (effect instanceof EffectRegist_Freeze) {
				// 套裝效果:寒冰耐性增加
				mode[15] = effect.get_mode();
			}

			if (effect instanceof EffectRegist_Stone) {
				// 套裝效果:石化耐性增加
				mode[16] = effect.get_mode();
			}

			if (effect instanceof EffectRegist_Sleep) {
				// 套裝效果:睡眠耐性增加
				mode[17] = effect.get_mode();
			}

			if (effect instanceof EffectRegist_Blind) {
				// 套裝效果:暗闇耐性增加
				mode[18] = effect.get_mode();
			}

			if (effect instanceof EffectRegist_Stun) {
				// 套裝效果:暈眩耐性增加
				mode[19] = effect.get_mode();
			}

			if (effect instanceof EffectRegist_Sustain) {
				// 套裝效果:支撐耐性增加
				mode[20] = effect.get_mode();
			}
			
			if (effect instanceof EffectHpR) {
				// 套裝效果:回血量增加
				mode[21] = effect.get_mode();
			}
			
			if (effect instanceof EffectMpR) {
				// 套裝效果:回魔量增加
				mode[22] = effect.get_mode();
			}
						
			if (effect instanceof Effect_Modifier_dmg) {
				// 套裝效果:套裝增加物理傷害
				mode[23] = effect.get_mode();
			}
			
			if (effect instanceof Effect_Reduction_dmg) {
				// 套裝效果:套裝減免物理傷害
				mode[24] = effect.get_mode();
			}
			
			if (effect instanceof Effect_Magic_modifier_dmg) {
				// 套裝效果:套裝增加魔法傷害
				mode[25] = effect.get_mode();
			}
			
			if (effect instanceof Effect_Magic_reduction_dmg) {
				// 套裝效果:套裝減免魔法傷害
				mode[26] = effect.get_mode();
			}
			
			if (effect instanceof Effect_Bow_modifier_dmg) {
				// 套裝效果:套裝增加弓的物理傷害
				mode[27] = effect.get_mode();
			}
			
			if (effect instanceof Effect_Hit_modifier) {
				// 套裝效果:套裝增加近距離命中率
				mode[28] = effect.get_mode();
			}
			
			if (effect instanceof Effect_Bow_Hit_modifier) {
				// 套裝效果:套裝增加遠距離命中率
				mode[29] = effect.get_mode();
			}
			
//			if (effect instanceof Effect_MagicCritical_chance) {
//				// 套裝效果:套裝增加魔法爆擊率
//				mode[30] = effect.get_mode();
//			}

			if (effect instanceof EffectAc) {
				 // 套裝效果:防禦
				mode[30] = effect.get_mode();
			}
			
		}
		return mode;
	}
	
	/**
	 * 套裝完成效果
	 * @param pc
	 */
	@Override
	public void giveEffect(final L1PcInstance pc) {
		try {
			for (final ArmorSetEffect effect : this._effects) {
				effect.giveEffect(pc);
			}
			// 套裝效果動畫
			if (_gfxids != null) {
				for (int gfx : _gfxids) {
					// 動畫效果
					pc.sendPacketsX8(
							new S_SkillSound(
									pc.getId(), gfx
									));
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * 套裝解除效果
	 * @param pc
	 */
	@Override
	public void cancelEffect(final L1PcInstance pc) {
		try {
			for (final ArmorSetEffect effect : this._effects) {
				effect.cancelEffect(pc);
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * 套裝完成
	 * @param pc
	 * @return
	 */
	@Override
	public final boolean isValid(final L1PcInstance pc) {
		return pc.getInventory().checkEquipped(this._ids);
	}

	/**
	 * 是否為套裝中組件
	 * @param id
	 * @return
	 */
	@Override
	public boolean isPartOfSet(final int id) {
		for (final int i : this._ids) {
			if (id == i) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否裝備了相同界指2個
	 * @param pc
	 * @return
	 */
	@Override
	public boolean isEquippedRingOfArmorSet(final L1PcInstance pc) {
		final L1PcInventory pcInventory = pc.getInventory();
		L1ItemInstance armor = null;
		boolean isSetContainRing = false;

		// 尋找套裝物件是否為戒指
		for (int id : _ids) {
			armor = pcInventory.findItemId(id);
			if (armor.getItem().getUseType() == 23) {//戒指
				isSetContainRing = true;
				break;
			}
	    }

		// 是否裝備了相同界指2個
		if ((armor != null) && isSetContainRing) {
			final int itemId = armor.getItem().getItemId();
			// 已經帶了 2個戒指
			if (pcInventory.getTypeEquipped(2, 9) == 2) {
					L1ItemInstance ring[] = new L1ItemInstance[2];
					ring = pcInventory.getRingEquipped();// 裝備中戒指陣列
					if ((ring[0].getItem().getItemId() == itemId)
							&& (ring[1].getItem().getItemId() == itemId)) {
						return true;
					}
			}
			// 已經帶了 3個戒指
	        if (pcInventory.getTypeEquipped(2, 9) == 3) {
	                L1ItemInstance ring[] = new L1ItemInstance[3];
	                ring = pcInventory.getRingEquipped();// 裝備中戒指陣列
	                if ((ring[0].getItem().getItemId() == itemId
	                                && ring[1].getItem().getItemId() == itemId)
	                || (ring[0].getItem().getItemId() == itemId
	                                && ring[2].getItem().getItemId() == itemId)                    
	                || (ring[1].getItem().getItemId() == itemId
	                                && ring[2].getItem().getItemId() == itemId)) {
	                	return true;
	                }
	        }     
	        // 已經帶了 4個戒指
	        if (pcInventory.getTypeEquipped(2, 9) == 4) {
	                L1ItemInstance ring[] = new L1ItemInstance[4];
	                ring = pcInventory.getRingEquipped();// 裝備中戒指陣列
	                if ((ring[0].getItem().getItemId() == itemId
	                                && ring[1].getItem().getItemId() == itemId)
	                || (ring[0].getItem().getItemId() == itemId
	                                && ring[2].getItem().getItemId() == itemId)
	                || (ring[0].getItem().getItemId() == itemId
	                                && ring[3].getItem().getItemId() == itemId)                      
	                || (ring[1].getItem().getItemId() == itemId
	                                && ring[2].getItem().getItemId() == itemId)
	                || (ring[1].getItem().getItemId() == itemId
	                                && ring[3].getItem().getItemId() == itemId)
	                || (ring[2].getItem().getItemId() == itemId
	                                && ring[3].getItem().getItemId() == itemId)) {
	                	return true;
	                }
	        } 
		}
		return false;
	}
}
