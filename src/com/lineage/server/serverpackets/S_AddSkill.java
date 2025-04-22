package com.lineage.server.serverpackets;

import java.util.Map;
import java.util.TreeMap;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Skills;

/**
 * 增加技能列表
 * 
 * @author simlin
 */
public class S_AddSkill extends ServerBasePacket {

	private byte[] _byte = null;

	// TODO 先暫時這樣取得正確的輸出精靈魔法屬性封包 日後再調整結構
	private static final Map<Integer, Integer> _elfAttrMap = new TreeMap<Integer, Integer>();
	
	static {
		_elfAttrMap.put(0, 0);// ElfAttr:0.無属性
		_elfAttrMap.put(1, 4);// ElfAttr:1.地属性
		_elfAttrMap.put(2, 1);// ElfAttr:2.火属性
		_elfAttrMap.put(4, 2);// ElfAttr:4.水属性
		_elfAttrMap.put(8, 3);// ElfAttr:8.風属性
	}
	
	/**
	 * 增加技能列表(單一技能)
	 * 
	 * @param pc 執行人物
	 * @param skillid 技能編號
	 */
	public S_AddSkill(final L1PcInstance pc, final int skillid) {
		final byte skill_list_values = 0x20;
		// 定義魔法名單
		final int[] skillList = new int[skill_list_values];
		// 取得魔法資料
		final L1Skills skill = SkillsTable.get().getTemplate(skillid);
		skillList[(skill.getSkillLevel() - 1)] += skill.getId();

		writeC(S_OPCODE_ADDSKILL);
		writeC(skill_list_values);

		for (final int element : skillList) {
			writeC(element);
		}

		if (pc != null) {
			pc.setSkillMastery(skillid);
		}
		// 精靈職業學習技能的屬性
		final Integer value = _elfAttrMap.get(pc.getElfAttr());
		this.writeC(value != null ? value : 0x00);
	}

	/**
	 * 增加技能列表(技能陣列)
	 * 
	 * @param pc
	 * @param skills
	 */
	public S_AddSkill(final L1PcInstance pc, final int[] skills) {

		//final int i6 = skills[4] + skills[5] + skills[6] + skills[7];
		//final int j6 = skills[8] + skills[9];

		writeC(S_OPCODE_ADDSKILL);

		/*if ((i6 > 0) && (j6 == 0)) {
			writeC(0x32);// 50
		} else if (j6 > 0) {
			writeC(0x64);// 100
		} else if ((i6 == 0) && (j6 == 0)) {
			writeC(0x20);// 32
		} else {
			writeC(0x16);
		}*/
		this.writeC(0x20);// Spell Group Size:32

		writeC(skills[0]);// 法師技能LV1
		writeC(skills[1]);// 法師技能LV2
		writeC(skills[2]);// 法師技能LV3
		writeC(skills[3]);// 法師技能LV4
		writeC(skills[4]);// 法師技能LV5
		writeC(skills[5]);// 法師技能LV6
		writeC(skills[6]);// 法師技能LV7
		writeC(skills[7]);// 法師技能LV8
		writeC(skills[8]);// 法師技能LV9
		writeC(skills[9]);// 法師技能LV10

		writeC(skills[10]);// 騎士技能1
		writeC(skills[11]);// 騎士技能2

		writeC(skills[12]);// 黑妖技能1
		writeC(skills[13]);// 黑妖技能2

		writeC(skills[14]);// 王族技能

		writeC(skills[15]);// un

		writeC(skills[16]);// 精靈技能 1
		writeC(skills[17]);// 精靈技能 2
		writeC(skills[18]);// 精靈技能 3
		writeC(skills[19]);// 精靈技能 4
		writeC(skills[20]);// 精靈技能 5
		writeC(skills[21]);// 精靈技能 6

		writeC(skills[22]);// 龍騎士技能 1
		writeC(skills[23]);// 龍騎士技能 2
		writeC(skills[24]);// 龍騎士技能 3

		writeC(skills[25]);// 幻術師 1
		writeC(skills[26]);// 幻術師 2
		writeC(skills[27]);// 幻術師 3

		writeC(skills[28]);// 戰士1

		// 補足其他未知的技能群組
		for(int i = 29; i < 32; i++) {
			this.writeC(0x00);
		}

		// 精靈職業學習技能的屬性
		final Integer value = _elfAttrMap.get(pc.getElfAttr());
		this.writeC(value != null ? value : 0x00);
		
		final int[] ix = new int[] { skills[0], skills[1], skills[2], skills[3], skills[4], // 法師技能
																							// 1
																							// ~
																							// 5
				skills[5], skills[6], skills[7], skills[8], skills[9], // 法師技能 6
																		// ~ 10

				skills[10], skills[11], // 騎士技能 1 ~ 2

				skills[12], skills[13], // 黑妖技能 1 ~ 2

				skills[14], // 王族技能

				skills[15],

				skills[16], skills[17], skills[18], skills[19], skills[20], skills[21], // 精靈技能
																						// 1
																						// ~
																						// 6

				skills[22], skills[23], skills[24], // 龍騎士技能 1 ~ 3
				skills[25], skills[26], skills[27], // 幻術師 1 ~ 3
				skills[28], skills[29] // 戰士1~2
		};

		for (int i = 0; i < ix.length; i++) {
			int type = ix[i];
			int rtType = 128;// 128
			int rt = 0;
			int skillid = -1;
			while (rt < 8) {// 每組8項技能
				if ((type - rtType) >= 0) {
					type -= rtType;
					switch (rtType) {
					case 128:// 128
						skillid = (i << 3) + 8;
						break;
					case 64:// 64
						skillid = (i << 3) + 7;
						break;
					case 32:// 32
						skillid = (i << 3) + 6;
						break;
					case 16:// 16
						skillid = (i << 3) + 5;
						break;
					case 8:// 8
						skillid = (i << 3) + 4;
						break;
					case 4:// 4
						skillid = (i << 3) + 3;
						break;
					case 2:// 2
						skillid = (i << 3) + 2;
						break;
					case 1:// 1
						skillid = (i << 3) + 1;
						break;
					}
					if (skillid != -1) {
						if (pc != null) {
							pc.setSkillMastery(skillid);
						}
					}
				}
				rt++;
				rtType = rtType >> 1;
			}
		}
	}

	/**
	 * 增加技能列表(技能群)
	 * 
	 * @param pc 執行人物 <BR>
	 * @param level1 法師技能LV1
	 * @param level2 法師技能LV2
	 * @param level3 法師技能LV3
	 * @param level4 法師技能LV4
	 * @param level5 法師技能LV5
	 * @param level6 法師技能LV6
	 * @param level7 法師技能LV7
	 * @param level8 法師技能LV8
	 * @param level9 法師技能LV9
	 * @param level10 法師技能LV10 <BR>
	 * @param knight1 騎士技能 1
	 * @param knight2 騎士技能 2 <BR>
	 * @param de1 黑妖技能1
	 * @param de2 黑妖技能2 <BR>
	 * @param royal 王族技能 <BR>
	 * @param un2 <BR>
	 * @param elf1 精靈技能1
	 * @param elf2 精靈技能2
	 * @param elf3 精靈技能3
	 * @param elf4 精靈技能4
	 * @param elf5 精靈技能5
	 * @param elf6 精靈技能6 <BR>
	 * @param k1 龍騎士技能1
	 * @param k2 龍騎士技能2
	 * @param k3 龍騎士技能3 <BR>
	 * @param l1 幻術師1
	 * @param l2 幻術師2
	 * @param l3 幻術師3 * @param wa1戰士1
	 * @param wa2戰士2
	 */
	public S_AddSkill(final L1PcInstance pc, final int level1, final int level2, final int level3,
			final int level4, final int level5, final int level6, final int level7, final int level8,
			final int level9, final int level10, final int knight1, final int knight2, final int de1,
			final int de2, final int royal, final int un, final int elf1, final int elf2, final int elf3,
			final int elf4, final int elf5, final int elf6, final int k1, final int k2, final int k3,
			final int l1, final int l2, final int l3, final int wa1, final int wa2) {

		//final int i6 = level5 + level6 + level7 + level8;
		//final int j6 = level9 + level10;
		writeC(S_OPCODE_ADDSKILL);

		/*if ((i6 > 0) && (j6 == 0)) {
			writeC(0x32);// 50
		} else if (j6 > 0) {
			writeC(0x64);// 100
		} else if ((i6 == 0) && (j6 == 0)) {
			writeC(0x20);// 32
		} else {
			writeC(0x16);
		}*/
		this.writeC(0x20);// Spell Group Size:32

		writeC(level1);// 法師技能LV1
		writeC(level2);// 法師技能LV2
		writeC(level3);// 法師技能LV3
		writeC(level4);// 法師技能LV4
		writeC(level5);// 法師技能LV5
		writeC(level6);// 法師技能LV6
		writeC(level7);// 法師技能LV7
		writeC(level8);// 法師技能LV8
		writeC(level9);// 法師技能LV9
		writeC(level10);// 法師技能LV10

		writeC(knight1);// 騎士技能1
		writeC(knight2);// 騎士技能2

		writeC(de1);// 黑妖技能1
		writeC(de2);// 黑妖技能2

		writeC(royal);// 王族技能

		writeC(un);// un

		writeC(elf1);
		writeC(elf2);
		writeC(elf3);
		writeC(elf4);
		writeC(elf5);
		writeC(elf6);

		writeC(k1);
		writeC(k2);
		writeC(k3);

		writeC(l1);
		writeC(l2);
		writeC(l3);

		writeC(wa1);

		// 補足其他未知的技能群組
		for(int i = 29; i < 32; i++) {
			this.writeC(0x00);
		}
		
		// 精靈職業學習技能的屬性
		final Integer value = _elfAttrMap.get(pc.getElfAttr());
		this.writeC(value != null ? value : 0x00);
		
		final int[] ix = new int[] { level1, level2, level3, level4, level5, level6, level7, level8, level9,
				level10, knight1, knight2, de1, de2, royal, un, elf1, elf2, elf3, elf4, elf5, elf6, k1, k2,
				k3, l1, l2, l3, wa1, wa2 };

		for (int i = 0; i < ix.length; i++) {
			int type = ix[i];
			int rtType = 128;// 128
			int rt = 0;
			int skillid = -1;
			while (rt < 8) {// 每組8項技能
				if ((type - rtType) >= 0) {
					type -= rtType;
					switch (rtType) {
					case 128:// 128
						skillid = (i << 3) + 8;
						break;
					case 64:// 64
						skillid = (i << 3) + 7;
						break;
					case 32:// 32
						skillid = (i << 3) + 6;
						break;
					case 16:// 16
						skillid = (i << 3) + 5;
						break;
					case 8:// 8
						skillid = (i << 3) + 4;
						break;
					case 4:// 4
						skillid = (i << 3) + 3;
						break;
					case 2:// 2
						skillid = (i << 3) + 2;
						break;
					case 1:// 1
						skillid = (i << 3) + 1;
						break;
					}
					if (skillid != -1) {
						if (pc != null) {
							pc.setSkillMastery(skillid);
						}
					}
				}
				rt++;
				rtType = rtType >> 1;
			}
		}
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

	@Override
	public byte[] getContent() { //20240901
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() { //20240901
		return this.getClass().getSimpleName();
	}
}
