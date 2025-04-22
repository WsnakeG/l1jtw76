package com.lineage.william;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ServerEtcItem;

/**
 * @author Roy 道具強化系統
 */

public class EtcItemForChar {
	private L1ServerEtcItem _etcitem;
	private L1PcInstance _pc;
	private static EtcItemForChar _instance;

	public static EtcItemForChar get(final L1PcInstance pc, final L1ServerEtcItem etcitem) {
		if (_instance == null) {
			_instance = new EtcItemForChar(pc, etcitem);
		} else {
			_instance._pc = pc;
			_instance._etcitem = etcitem;
		}
		return _instance;
	}

	private EtcItemForChar(final L1PcInstance pc, final L1ServerEtcItem etcitem) {
		_etcitem = etcitem;
		_pc = pc;
	}

	/** 狀態更新 */
	private void status(final boolean isAdd) {
		_pc.sendPackets(new S_OwnCharStatus2(_pc));
		_pc.sendPackets(new S_OwnCharAttrDef(_pc));
		_pc.sendPackets(new S_OwnCharStatus(_pc));
		_pc.sendPackets(new S_SPMR(_pc));
	}

	/** 登入時給予效果 */
	public void loginEffect() {
		_pc.addMaxHp(_etcitem.addMaxHP);
		_pc.setCurrentHp(_pc.getCurrentHp() + _etcitem.add_hpr);
		_pc.addMaxMp(_etcitem.addMaxMP);
		_pc.setCurrentMp(_pc.getCurrentMp() + _etcitem.add_mpr);
		_pc.addCha(_etcitem.add_cha);
		_pc.addCon(_etcitem.add_con);
		_pc.addDex(_etcitem.add_dex);
		_pc.addInt(_etcitem.add_int);
		_pc.addStr(_etcitem.add_str);
		_pc.addWis(_etcitem.add_wis);
		_pc.addSp(_etcitem.add_sp);
		_pc.addMr(_etcitem.m_def);
		_pc.addAc(_etcitem.add_ac);
		_pc.addBowDmgup(_etcitem.bow_dmg_modifier);
		_pc.addDmgup(_etcitem.dmg_modifier);
		_pc.add_double_dmg_chance(_etcitem.double_dmg_chance);
		_pc.add_magic_reduction_dmg(_etcitem.magic_reduction_dmg);
		_pc.add_reduction_dmg(_etcitem.reduction_dmg);
		// 新增能力道具系統 八種特殊能力 Erics4179 160829
		_pc.addPhysicsDmgUp(_etcitem.physicsDmgUp);
		_pc.addMagicDmgUp(_etcitem.magicDmgUp);
		_pc.addPhysicsDmgDown(_etcitem.physicsDmgDown);
		_pc.addMagicDmgDown(_etcitem.magicDmgDown);
		_pc.addMagicHitUp(_etcitem.magicHitUp);
		_pc.addMagicHitDown(_etcitem.magicHitDown);
		_pc.addPhysicsDoubleHit(_etcitem.physicsDoubleHit);
		_pc.addMagicDoubleHit(_etcitem.magicDoubleHit);
		// 新增幸運度 Erics4179 160901
		_pc.addInfluenceLuck (_etcitem.InfluenceLuck );
		status(true);
	}

	/** 給予效果 */
	public void giveEffect() {
		_pc.addMaxHp(_etcitem.addMaxHP);
		_pc.setCurrentHp(_pc.getCurrentHp() + _etcitem.add_hpr);
		_pc.addMaxMp(_etcitem.addMaxMP);
		_pc.setCurrentMp(_pc.getCurrentMp() + _etcitem.add_mpr);
		_pc.addCha(_etcitem.add_cha);
		_pc.addCon(_etcitem.add_con);
		_pc.addDex(_etcitem.add_dex);
		_pc.addInt(_etcitem.add_int);
		_pc.addStr(_etcitem.add_str);
		_pc.addWis(_etcitem.add_wis);
		_pc.addAc(_etcitem.add_ac);
		_pc.addSp(_etcitem.add_sp);
		_pc.addMr(_etcitem.m_def);
		_pc.addBowDmgup(_etcitem.bow_dmg_modifier);
		_pc.addDmgup(_etcitem.dmg_modifier);
		_pc.add_double_dmg_chance(_etcitem.double_dmg_chance);
		_pc.add_magic_reduction_dmg(_etcitem.magic_reduction_dmg);
		_pc.add_reduction_dmg(_etcitem.reduction_dmg);
		// 新增能力道具系統 八種特殊能力 Erics4179 160829
		_pc.addPhysicsDmgUp(_etcitem.physicsDmgUp);
		_pc.addMagicDmgUp(_etcitem.magicDmgUp);
		_pc.addPhysicsDmgDown(_etcitem.physicsDmgDown);
		_pc.addMagicDmgDown(_etcitem.magicDmgDown);
		_pc.addMagicHitUp(_etcitem.magicHitUp);
		_pc.addMagicHitDown(_etcitem.magicHitDown);
		_pc.addPhysicsDoubleHit(_etcitem.physicsDoubleHit);
		_pc.addMagicDoubleHit(_etcitem.magicDoubleHit);
		// 新增幸運度 Erics4179 160901
		_pc.addInfluenceLuck (_etcitem.InfluenceLuck );
		status(true);
	}

	/** 移除效果 */
	public void cancelEffect() {
		_pc.addMaxHp(-1 * _etcitem.addMaxHP);
		_pc.setCurrentHp(_pc.getCurrentHp() - _etcitem.add_hpr);
		_pc.addMaxMp(-1 * _etcitem.addMaxMP);
		_pc.setCurrentMp(_pc.getCurrentMp() - _etcitem.add_mpr);
		_pc.addCha(-1 * _etcitem.add_cha);
		_pc.addCon(-1 * _etcitem.add_con);
		_pc.addDex(-1 * _etcitem.add_dex);
		_pc.addInt(-1 * _etcitem.add_int);
		_pc.addStr(-1 * _etcitem.add_str);
		_pc.addWis(-1 * _etcitem.add_wis);
		_pc.addAc(-1 * _etcitem.add_ac);
		_pc.addSp(-1 * _etcitem.add_sp);
		_pc.addMr(-1 * _etcitem.m_def);
		_pc.addBowDmgup(-1 * _etcitem.bow_dmg_modifier);
		_pc.addDmgup(-1 * _etcitem.dmg_modifier);
		_pc.add_double_dmg_chance(-1 * _etcitem.double_dmg_chance);
		_pc.add_magic_reduction_dmg(-1 * _etcitem.magic_reduction_dmg);
		_pc.add_reduction_dmg(-1 * _etcitem.reduction_dmg);
		// 新增能力道具系統 八種特殊能力 Erics4179 160829
		_pc.addPhysicsDmgUp(-1 * _etcitem.physicsDmgUp);
		_pc.addMagicDmgUp(-1 * _etcitem.magicDmgUp);
		_pc.addPhysicsDmgDown(-1 * _etcitem.physicsDmgDown);
		_pc.addMagicDmgDown(-1 * _etcitem.magicDmgDown);
		_pc.addMagicHitUp(-1 * _etcitem.magicHitUp);
		_pc.addMagicHitDown(-1 * _etcitem.magicHitDown);
		_pc.addPhysicsDoubleHit(-1 * _etcitem.physicsDoubleHit);
		_pc.addMagicDoubleHit(-1 * _etcitem.magicDoubleHit);
		// 新增幸運度 Erics4179 160901
		_pc.addInfluenceLuck (-1 * _etcitem.InfluenceLuck );
		status(false);
		_pc.sendPackets(new S_SystemMessage("\\aD" + _etcitem.itemname + "效果結束。"));
	}
}
