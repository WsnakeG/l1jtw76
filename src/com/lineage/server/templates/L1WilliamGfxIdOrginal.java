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

import com.lineage.server.datatables.GfxIdOrginal;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;

// Referenced classes of package l1j.server.server.model:
// L1PcInstance

public class L1WilliamGfxIdOrginal {

	// 死亡是否不掉落經驗值
	public static boolean DeadExp(final int gfxId) {
		final L1WilliamGfxIdOrginal gfxIdOrginal = GfxIdOrginal.get().getTemplate(gfxId);
		if (gfxIdOrginal == null) {
			return false;
		}
		if (gfxIdOrginal.getDeadExp() == true) {
			return true;
		}
		return false;
	}

	// 變身編號是否無法被消除
	public static boolean Cancellation(final int gfxId) {
		final L1WilliamGfxIdOrginal gfxIdOrginal = GfxIdOrginal.get().getTemplate(gfxId);
		if (gfxIdOrginal == null) {
			return false;
		}
		if (gfxIdOrginal.getCancellation() == true) {
			return true;
		}
		return false;
	}

	// 變身編號附加的能力
	public static void getAddGfxIdOrginal(final L1PcInstance pc, final int gfxId) {
		final L1WilliamGfxIdOrginal gfxIdOrginal = GfxIdOrginal.get().getTemplate(gfxId);
		if (gfxIdOrginal == null) {
			return;
		}
		if (gfxIdOrginal.getAddStr() != 0) { // 力量
			pc.addStr(gfxIdOrginal.getAddStr());
		}
		if (gfxIdOrginal.getAddDex() != 0) { // 敏捷
			pc.addDex(gfxIdOrginal.getAddDex());
		}
		if (gfxIdOrginal.getAddCon() != 0) { // 體質
			pc.addCon(gfxIdOrginal.getAddCon());
		}
		if (gfxIdOrginal.getAddInt() != 0) { // 智力
			pc.addInt(gfxIdOrginal.getAddInt());
		}
		if (gfxIdOrginal.getAddWis() != 0) { // 精神
			pc.addWis(gfxIdOrginal.getAddWis());
		}
		if (gfxIdOrginal.getAddCha() != 0) { // 魅力
			pc.addCha(gfxIdOrginal.getAddCha());
		}
		if (gfxIdOrginal.getAddAc() != 0) { // 防禦
			pc.addAc(-gfxIdOrginal.getAddAc());
		}
		if (gfxIdOrginal.getAddMaxHp() != 0) { // HP
			pc.addMaxHp(gfxIdOrginal.getAddMaxHp());
		}
		if (gfxIdOrginal.getAddMaxMp() != 0) { // MP
			pc.addMaxMp(gfxIdOrginal.getAddMaxMp());
		}
		if (gfxIdOrginal.getAddHpr() != 0) { // 回血
			pc.addHpr(gfxIdOrginal.getAddHpr());
		}
		if (gfxIdOrginal.getAddMpr() != 0) { // 回魔
			pc.addMpr(gfxIdOrginal.getAddMpr());
		}
		if (gfxIdOrginal.getAddDmg() != 0) { // 進戰傷害
			pc.addDmgup(gfxIdOrginal.getAddDmg());
		}
		if (gfxIdOrginal.getAddHit() != 0) { // 進戰命中
			pc.addHitup(gfxIdOrginal.getAddHit());
		}
		if (gfxIdOrginal.getAddBowDmg() != 0) { // 遠戰傷害
			pc.addBowDmgup(gfxIdOrginal.getAddBowDmg());
		}
		if (gfxIdOrginal.getAddBowHit() != 0) { // 遠戰命中
			pc.addBowHitup(gfxIdOrginal.getAddBowHit());
		}
		if (gfxIdOrginal.getReduction_dmg() != 0) { // 減免物理傷害
			pc.addDamageReductionByArmor(gfxIdOrginal.getReduction_dmg());
		}
		if (gfxIdOrginal.getReduction_magic_dmg() != 0) { // 減免魔法傷害
			pc.addMagicDmgReduction(gfxIdOrginal.getReduction_magic_dmg());
		}
		if (gfxIdOrginal.getAddMr() != 0) { // 抗魔
			pc.addMr(gfxIdOrginal.getAddMr());
		}
		if (gfxIdOrginal.getAddSp() != 0) { // 魔攻
			pc.addSp(gfxIdOrginal.getAddSp());
		}
		if (gfxIdOrginal.getAddFire() != 0) { // 抗火屬性
			pc.addFire(gfxIdOrginal.getAddFire());
		}
		if (gfxIdOrginal.getAddWind() != 0) { // 抗風屬性
			pc.addWind(gfxIdOrginal.getAddWind());
		}
		if (gfxIdOrginal.getAddEarth() != 0) { // 抗地屬性
			pc.addEarth(gfxIdOrginal.getAddEarth());
		}
		if (gfxIdOrginal.getAddWater() != 0) { // 抗水屬性
			pc.addWater(gfxIdOrginal.getAddWater());
		}
		pc.sendPackets(new S_SPMR(pc));
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	// 變身編號效果移除附加的能力
	public static void getReductionGfxIdOrginal(final L1PcInstance pc, final int gfxId) {
		final L1WilliamGfxIdOrginal gfxIdOrginal = GfxIdOrginal.get().getTemplate(gfxId);
		if (gfxIdOrginal == null) {
			return;
		}
		if (gfxIdOrginal.getAddStr() != 0) { // 力量
			pc.addStr(-gfxIdOrginal.getAddStr());
		}
		if (gfxIdOrginal.getAddDex() != 0) { // 敏捷
			pc.addDex(-gfxIdOrginal.getAddDex());
		}
		if (gfxIdOrginal.getAddCon() != 0) { // 體質
			pc.addCon(-gfxIdOrginal.getAddCon());
		}
		if (gfxIdOrginal.getAddInt() != 0) { // 智力
			pc.addInt(-gfxIdOrginal.getAddInt());
		}
		if (gfxIdOrginal.getAddWis() != 0) { // 精神
			pc.addWis(-gfxIdOrginal.getAddWis());
		}
		if (gfxIdOrginal.getAddCha() != 0) { // 魅力
			pc.addCha(-gfxIdOrginal.getAddCha());
		}
		if (gfxIdOrginal.getAddAc() != 0) { // 防禦
			pc.addAc(gfxIdOrginal.getAddAc());
		}
		if (gfxIdOrginal.getAddMaxHp() != 0) { // HP
			pc.addMaxHp(-gfxIdOrginal.getAddMaxHp());
		}
		if (gfxIdOrginal.getAddMaxMp() != 0) { // MP
			pc.addMaxMp(-gfxIdOrginal.getAddMaxMp());
		}
		if (gfxIdOrginal.getAddHpr() != 0) { // 回血
			pc.addHpr(-gfxIdOrginal.getAddHpr());
		}
		if (gfxIdOrginal.getAddMpr() != 0) { // 回魔
			pc.addMpr(-gfxIdOrginal.getAddMpr());
		}
		if (gfxIdOrginal.getAddDmg() != 0) { // 進戰傷害
			pc.addDmgup(-gfxIdOrginal.getAddDmg());
		}
		if (gfxIdOrginal.getAddHit() != 0) { // 進戰命中
			pc.addHitup(-gfxIdOrginal.getAddHit());
		}
		if (gfxIdOrginal.getAddBowDmg() != 0) { // 遠戰傷害
			pc.addBowDmgup(-gfxIdOrginal.getAddBowDmg());
		}
		if (gfxIdOrginal.getAddBowHit() != 0) { // 遠戰命中
			pc.addBowHitup(-gfxIdOrginal.getAddBowHit());
		}
		if (gfxIdOrginal.getReduction_dmg() != 0) { // 減免物理傷害
			pc.addDamageReductionByArmor(-gfxIdOrginal.getReduction_dmg());
		}
		if (gfxIdOrginal.getReduction_magic_dmg() != 0) { // 減免魔法傷害
			pc.addMagicDmgReduction(-gfxIdOrginal.getReduction_magic_dmg());
		}
		if (gfxIdOrginal.getAddMr() != 0) { // 抗魔
			pc.addMr(-gfxIdOrginal.getAddMr());
		}
		if (gfxIdOrginal.getAddSp() != 0) { // 魔攻
			pc.addSp(-gfxIdOrginal.getAddSp());
		}
		if (gfxIdOrginal.getAddFire() != 0) { // 抗火屬性
			pc.addFire(-gfxIdOrginal.getAddFire());
		}
		if (gfxIdOrginal.getAddWind() != 0) { // 抗風屬性
			pc.addWind(-gfxIdOrginal.getAddWind());
		}
		if (gfxIdOrginal.getAddEarth() != 0) { // 抗地屬性
			pc.addEarth(-gfxIdOrginal.getAddEarth());
		}
		if (gfxIdOrginal.getAddWater() != 0) { // 抗水屬性
			pc.addWater(-gfxIdOrginal.getAddWater());
		}
		pc.sendPackets(new S_SPMR(pc));
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	private final int _gfxId;
	private final boolean _deadExp;
	private final boolean _cancellation;
	private final byte _addStr;
	private final byte _addDex;
	private final byte _addCon;
	private final byte _addInt;
	private final byte _addWis;
	private final byte _addCha;
	private final int _addAc;
	private final int _addMaxHp;
	private final int _addMaxMp;
	private final int _addHpr;
	private final int _addMpr;
	private final int _addDmg;
	private final int _addBowDmg;
	private final int _addHit;
	private final int _addBowHit;
	private final int _reduction_dmg;
	private final int _reduction_magic_dmg;
	private final int _addMr;
	private final int _addSp;
	private final int _addFire;
	private final int _addWind;
	private final int _addEarth;
	private final int _addWater;

	public L1WilliamGfxIdOrginal(final int gfxId, final boolean deadExp, final boolean cancellation,
			final byte addStr, final byte addDex, final byte addCon, final byte addInt, final byte addWis,
			final byte addCha, final int addAc, final int addMaxHp, final int addMaxMp, final int addHpr,
			final int addMpr, final int addDmg, final int addBowDmg, final int addHit, final int addBowHit,
			final int reduction_dmg, final int reduction_magic_dmg, final int addMr, final int addSp,
			final int addFire, final int addWind, final int addEarth, final int addWater) {
		_gfxId = gfxId;
		_deadExp = deadExp;
		_cancellation = cancellation;
		_addStr = addStr;
		_addDex = addDex;
		_addCon = addCon;
		_addInt = addInt;
		_addWis = addWis;
		_addCha = addCha;
		_addAc = addAc;
		_addMaxHp = addMaxHp;
		_addMaxMp = addMaxMp;
		_addHpr = addHpr;
		_addMpr = addMpr;
		_addDmg = addDmg;
		_addBowDmg = addBowDmg;
		_addHit = addHit;
		_addBowHit = addBowHit;
		_reduction_dmg = reduction_dmg;
		_reduction_magic_dmg = reduction_magic_dmg;
		_addMr = addMr;
		_addSp = addSp;
		_addFire = addFire;
		_addWind = addWind;
		_addEarth = addEarth;
		_addWater = addWater;
	}

	public int getGfxId() {
		return _gfxId;
	}

	public boolean getDeadExp() {
		return _deadExp;
	}

	public boolean getCancellation() {
		return _cancellation;
	}

	public byte getAddStr() {
		return _addStr;
	}

	public byte getAddDex() {
		return _addDex;
	}

	public byte getAddCon() {
		return _addCon;
	}

	public byte getAddInt() {
		return _addInt;
	}

	public byte getAddWis() {
		return _addWis;
	}

	public byte getAddCha() {
		return _addCha;
	}

	public int getAddAc() {
		return _addAc;
	}

	public int getAddMaxHp() {
		return _addMaxHp;
	}

	public int getAddMaxMp() {
		return _addMaxMp;
	}

	public int getAddHpr() {
		return _addHpr;
	}

	public int getAddMpr() {
		return _addMpr;
	}

	public int getAddDmg() {
		return _addDmg;
	}

	public int getAddBowDmg() {
		return _addBowDmg;
	}

	public int getAddHit() {
		return _addHit;
	}

	public int getAddBowHit() {
		return _addBowHit;
	}

	public int getReduction_dmg() {
		return _reduction_dmg;
	}

	public int getReduction_magic_dmg() {
		return _reduction_magic_dmg;
	}

	public int getAddMr() {
		return _addMr;
	}

	public int getAddSp() {
		return _addSp;
	}

	public int getAddFire() {
		return _addFire;
	}

	public int getAddWind() {
		return _addWind;
	}

	public int getAddEarth() {
		return _addEarth;
	}

	public int getAddWater() {
		return _addWater;
	}
}
