package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * @category 初始能力加成, 用於查看創腳色時初始能力的增幅
 */
public class S_InitialAbilityGrowth extends ServerBasePacket {

	private byte[] _byte = null;

	public S_InitialAbilityGrowth(final L1PcInstance pc) {

		final int Str = pc.getOriginalStr();// 力量
		final int Dex = pc.getOriginalDex();// 敏捷
		final int Con = pc.getOriginalCon();// 體質
		final int Wis = pc.getOriginalWis();// 精神
		final int Cha = pc.getOriginalCha();// 魅力
		final int Int = pc.getOriginalInt();// 智力
		final int[] growth = new int[6];

		// 王族
		if (pc.isCrown()) {
			final int Initial[] = { 13, 10, 10, 11, 13, 10 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// 法師
		if (pc.isWizard()) {
			final int[] Initial = { 8, 7, 12, 12, 8, 12 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// 騎士
		if (pc.isKnight()) {
			final int[] Initial = { 16, 12, 14, 9, 12, 8 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// 妖精
		if (pc.isElf()) {
			final int[] Initial = { 11, 12, 12, 12, 9, 12 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// 黑妖
		if (pc.isDarkelf()) {
			final int[] Initial = { 12, 15, 8, 10, 9, 11 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// 龍騎士
		if (pc.isDragonKnight()) {
			final int[] Initial = { 13, 11, 14, 12, 8, 11 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// 幻術師
		if (pc.isIllusionist()) {
			final int[] Initial = { 11, 10, 12, 12, 8, 12 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// 戰士
		if (pc.isWarrior()) {
			final int[] Initial = { 16, 13, 16, 7, 9, 10 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}

		buildPacket(pc, growth[0], growth[1], growth[2], growth[3], growth[4], growth[5]);
	}

	/**
	 * @param pc 腳色
	 * @param Str 力量
	 * @param Dex 敏捷
	 * @param Con 體質
	 * @param Wis 精神
	 * @param Cha 魅力
	 * @param Int 智力
	 */
	private void buildPacket(final L1PcInstance pc, final int Str, final int Dex, final int Con,
			final int Wis, final int Cha, final int Int) {
		final int write1 = (Int * 16) + Str;
		final int write2 = (Dex * 16) + Wis;
		final int write3 = (Cha * 16) + Con;
		writeC(S_OPCODE_CHARRESET);
		writeC(0x04);
		writeC(write1);// 智力&力量
		writeC(write2);// 敏捷&精神
		writeC(write3);// 魅力&體質
		writeC(0x00);
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
