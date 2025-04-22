package com.lineage.server.model.c1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;

/**
 * 陣營階級能力<BR>
 * HP相關<BR>
 * HP上限提高 參數：INT1(HP)<BR>
 * MP相關<BR>
 * MP上限提高 參數：INT2(MP)<BR>
 * 額外攻擊相關<BR>
 * 攻擊力提高 參數：INT3(額外攻擊)<BR>
 * 遠距離攻擊相關<BR>
 * 攻擊力提高 參數：INT4(遠距離攻擊+)<BR>
 * 傷害減免相關<BR>
 * 傷害減免提高 參數：INT5(傷害減免質+)<BR>
 * 經驗相關<BR>
 * 經驗質提高 參數：INT6(增加質 1/100)<BR>
 * 
 * @author erics4179
 */
public class C1_camppowerup extends C1Executor {

	private static final Log _log = LogFactory.getLog(C1_camppowerup.class);

	private int _int1;// 值1
	private int _int2;// 值2
	private int _int3;// 值3
	private int _int4;// 值4
	private int _int5;// 值5
	private int _int6;// 值6
	private int _int7;// 值7
	private int _int8;// 值8
	private int _int9;// 值9
	private int _int10;// 值10
	private int _int11;// 值11
	private int _int12;// 值12
	private int _int13;// 值13
	private int _int14;// 值14

	/**
	 * 效果:HP/MP增加、額外攻擊
	 */
	public C1_camppowerup() {
	}

	public static C1Executor get() {
		return new C1_camppowerup();
	}

	@Override
	public void set_power(final int int1, final int int2, final int int3, final int int4, final int int5,
			final int int6, final int int7, final int int8, final int int9, final int int10, final int int11,
			final int int12, final int int13, final int int14) {
		try {
			_int1 = int1;
			_int2 = int2;
			_int3 = int3;
			_int4 = int4;
			_int5 = int5;
			_int6 = int6;
			_int7 = int7;
			_int8 = int8;
			_int9 = int9;
			_int10 = int10;
			_int11 = int11;
			_int12 = int12;
			_int13 = int13;
			_int14 = int14;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void set_c1(final L1PcInstance pc) {
		try {
			pc.addMaxHp(_int1);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));

			pc.addMaxMp(_int2);
			pc.sendPackets(new S_MPUpdate(pc));

			pc.addDmgup(_int3);

			pc.addBowDmgup(_int4);

			pc.addDamageReductionByArmor(_int5);

			pc.set_expadd(_int6);

			pc.addPhysicsDmgUp(_int7);

			pc.addMagicDmgUp(_int8);

			pc.addPhysicsDmgDown(_int9);

			pc.addMagicDmgDown(_int10);

			pc.addMagicHitUp(_int11);

			pc.addMagicHitDown(_int12);

			pc.addPhysicsDoubleHit(_int13);

			pc.addMagicDoubleHit(_int14);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void remove_c1(final L1PcInstance pc) {
		try {
			pc.addMaxHp(-_int1);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));

			pc.addMaxMp(-_int2);
			pc.sendPackets(new S_MPUpdate(pc));

			pc.addDmgup(-_int3);

			pc.addBowDmgup(-_int4);

			pc.addDamageReductionByArmor(-_int5);

			pc.set_expadd(-_int6);

			pc.addPhysicsDmgUp(-_int7);

			pc.addMagicDmgUp(-_int8);

			pc.addPhysicsDmgDown(-_int9);

			pc.addMagicDmgDown(-_int10);

			pc.addMagicHitUp(-_int11);

			pc.addMagicHitDown(-_int12);

			pc.addPhysicsDoubleHit(-_int13);

			pc.addMagicDoubleHit(-_int14);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}