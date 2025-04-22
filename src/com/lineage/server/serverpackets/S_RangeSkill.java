package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.skill.TargetStatus;

/**
 * 範圍魔法
 * 
 * @author dexc
 */
public class S_RangeSkill extends ServerBasePacket {

	private static AtomicInteger _sequentialNumber = new AtomicInteger(9000000);

	private byte[] _byte = null;

	public static final int TYPE_NODIR = 0;

	public static final int TYPE_DIR = 8;

	/**
	 * 範圍魔法
	 * 
	 * @param cha
	 * @param targetList
	 * @param spellgfx
	 * @param actionId
	 * @param type
	 */
	public S_RangeSkill(final L1Character cha, final ArrayList<TargetStatus> targetList, final int spellgfx,
			final int actionId, final int type) {
		// 0000: 07 12 be ac bf 01 c1 83 e4 7e 04 47 8f c2 01 ab
		// .........~.G....
		// 0010: 00 06 00 00 02 00 3c 20 00 00 13 00 a4 6c 00 00 ......<
		// .....l..
		// 0020: 10 00 00 1c 87 21 12 00 .....!..

		writeC(S_OPCODE_RANGESKILLS);
		writeC(actionId);

		writeD(cha.getId());
		writeH(cha.getX());
		writeH(cha.getY());

		switch (type) {
		case TYPE_NODIR:
			writeC(cha.getHeading());
			break;

		case TYPE_DIR:
			final int newHeading = calcheading(cha.getX(), cha.getY(), targetList.get(0).getTarget().getX(),
					targetList.get(0).getTarget().getY());
			cha.setHeading(newHeading);
			writeC(cha.getHeading());
			break;
		}

		writeD(_sequentialNumber.incrementAndGet()); // 番号がダブらないように送る。
		writeH(spellgfx);
		writeC(type); // 0:範囲 6:遠距離 8:範囲&遠距離
		writeH(0x0000);
		writeH(targetList.size());

		for (final TargetStatus target : targetList) {
			// System.out.println("TG: "+target.getTarget().getName() + "/" +
			// target.isCalc());
			writeD(target.getTarget().getId());
			if (target.isCalc()) {
				writeH(0x20);

			} else {
				writeH(0x00); // 0x00:無傷害 大於0傷害質
			}
		}
	}

	private static int calcheading(final int myx, final int myy, final int tx, final int ty) {
		int newheading = 0;
		if ((tx > myx) && (ty > myy)) {
			newheading = 3;
		}
		if ((tx < myx) && (ty < myy)) {
			newheading = 7;
		}
		if ((tx > myx) && (ty == myy)) {
			newheading = 2;
		}
		if ((tx < myx) && (ty == myy)) {
			newheading = 6;
		}
		if ((tx == myx) && (ty < myy)) {
			newheading = 0;
		}
		if ((tx == myx) && (ty > myy)) {
			newheading = 4;
		}
		if ((tx < myx) && (ty > myy)) {
			newheading = 5;
		}
		if ((tx > myx) && (ty < myy)) {
			newheading = 1;
		}
		return newheading;
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}
}