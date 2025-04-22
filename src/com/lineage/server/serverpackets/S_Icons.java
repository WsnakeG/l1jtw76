package com.lineage.server.serverpackets;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.ADDITIONAL_FIRE;
import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
import static com.lineage.server.model.skill.L1SkillId.BURNING_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.CONCENTRATION;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_MIRROR;
import static com.lineage.server.model.skill.L1SkillId.DANCING_BLAZE;
import static com.lineage.server.model.skill.L1SkillId.DECAY_POTION;
import static com.lineage.server.model.skill.L1SkillId.DECREASE_WEIGHT;
import static com.lineage.server.model.skill.L1SkillId.DISEASE;
import static com.lineage.server.model.skill.L1SkillId.DRAGON_SKIN;
import static com.lineage.server.model.skill.L1SkillId.DRESS_EVASION;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FALL_DOWN;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static com.lineage.server.model.skill.L1SkillId.ERASE_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.FIRE_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.GUARD_BRAKE;
import static com.lineage.server.model.skill.L1SkillId.HORROR_OF_DEATH;
import static com.lineage.server.model.skill.L1SkillId.INSIGHT;
import static com.lineage.server.model.skill.L1SkillId.MAZU_STATUS;
import static com.lineage.server.model.skill.L1SkillId.MEDITATION;
import static com.lineage.server.model.skill.L1SkillId.MORTAL_BODY;
import static com.lineage.server.model.skill.L1SkillId.NATURES_TOUCH;
import static com.lineage.server.model.skill.L1SkillId.PANIC;
import static com.lineage.server.model.skill.L1SkillId.PATIENCE;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;
import static com.lineage.server.model.skill.L1SkillId.RESIST_FEAR;
import static com.lineage.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static com.lineage.server.model.skill.L1SkillId.STATUS_RIBRAVE;
import static com.lineage.server.model.skill.L1SkillId.STORM_EYE;
import static com.lineage.server.model.skill.L1SkillId.STORM_SHOT;
import static com.lineage.server.model.skill.L1SkillId.STRIKER_GALE;
import static com.lineage.server.model.skill.L1SkillId.VENOM_RESIST;
import static com.lineage.server.model.skill.L1SkillId.WEAKNESS;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHOT;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 圖示封包 (2014-10-05更新)
 * 
 * @author Nightwish790711
 */
public class S_Icons extends ServerBasePacket {
	
	private byte[] _byte = null;
	/**
	 * 讀取技能圖示 (進入遊戲時)
	 * 
	 * @param pc
	 * @param code
	 */
	public S_Icons(final L1PcInstance pc) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.ICONS1);

		byte[] data = new byte[0xcc];

		writeD(data.length);

		// 冥想術
		if (pc.hasSkillEffect(MEDITATION)) {
			data[0] = (byte) (pc.getSkillEffectTimeSec(MEDITATION) / 4);
		}

		// 魔法屏障
		if (pc.hasSkillEffect(COUNTER_MAGIC)) {
			data[1] = (byte) (pc.getSkillEffectTimeSec(COUNTER_MAGIC) / 4);
		}

		// 負重強化
		if (pc.hasSkillEffect(DECREASE_WEIGHT)) {
			data[3] = (byte) (pc.getSkillEffectTimeSec(DECREASE_WEIGHT) / 4);
		}

		// 藥水霜化
		if (pc.hasSkillEffect(DECAY_POTION)) {
			data[4] = (byte) (pc.getSkillEffectTimeSec(DECAY_POTION) / 4);
		}

		// 絕對屏障
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			data[5] = (byte) (pc.getSkillEffectTimeSec(ABSOLUTE_BARRIER) / 4);
		}

		// 毒性抵抗
		if (pc.hasSkillEffect(VENOM_RESIST)) {
			data[7] = (byte) (pc.getSkillEffectTimeSec(VENOM_RESIST) / 4);
		}

		// 弱化術
		if (pc.hasSkillEffect(WEAKNESS)) {
			data[8] = (byte) (pc.getSkillEffectTimeSec(WEAKNESS) / 4);
		}

		// 疾病術
		if (pc.hasSkillEffect(DISEASE)) {
			data[9] = (byte) (pc.getSkillEffectTimeSec(DISEASE) / 4);
		}

		// 火焰武器
		if (pc.hasSkillEffect(FIRE_WEAPON)) {
			data[10] = (byte) (pc.getSkillEffectTimeSec(FIRE_WEAPON) / 4);
			data[11] = (byte) (FIRE_WEAPON - 1);
		}
		// 烈炎氣息
		else if (pc.hasSkillEffect(DANCING_BLAZE)) {
			data[10] = (byte) (pc.getSkillEffectTimeSec(DANCING_BLAZE) / 4);
			data[11] = (byte) (DANCING_BLAZE - 1);
		}
		// 烈炎武器
		else if (pc.hasSkillEffect(BURNING_WEAPON)) {
			data[10] = (byte) (pc.getSkillEffectTimeSec(BURNING_WEAPON) / 4);
			data[11] = (byte) (BURNING_WEAPON - 1);
		}
		// 風之神射
		else if (pc.hasSkillEffect(WIND_SHOT)) {
			data[10] = (byte) (pc.getSkillEffectTimeSec(WIND_SHOT) / 4);
			data[11] = (byte) (WIND_SHOT - 1);
		}
		// 暴風之眼
		else if (pc.hasSkillEffect(STORM_EYE)) {
			data[10] = (byte) (pc.getSkillEffectTimeSec(STORM_EYE) / 4);
			data[11] = (byte) (STORM_EYE - 1);
		}
		// 暴風神射
		else if (pc.hasSkillEffect(STORM_SHOT)) {
			data[10] = (byte) (pc.getSkillEffectTimeSec(STORM_SHOT) / 4);
			data[11] = (byte) (STORM_SHOT - 1);
		}

		// 閃避提升
		if (pc.hasSkillEffect(DRESS_EVASION)) {
			data[17] = (byte) (pc.getSkillEffectTimeSec(DRESS_EVASION) / 4);
		}

		// 狂暴術
		if (pc.hasSkillEffect(BERSERKERS)) {
			data[18] = (byte) (pc.getSkillEffectTimeSec(BERSERKERS) / 4);
		}

		// 生命之泉
		if (pc.hasSkillEffect(NATURES_TOUCH)) {
			data[19] = (byte) (pc.getSkillEffectTimeSec(NATURES_TOUCH) / 4);
		}

		// 風之枷鎖
		if (pc.hasSkillEffect(WIND_SHACKLE)) {
			data[20] = (byte) (pc.getSkillEffectTimeSec(WIND_SHACKLE) / 4);
		}

		// 魔法消除
		if (pc.hasSkillEffect(ERASE_MAGIC)) {
			data[21] = (byte) (pc.getSkillEffectTimeSec(ERASE_MAGIC) / 4);
		}

		// 鏡反射
		if (pc.hasSkillEffect(COUNTER_MIRROR)) {
			data[22] = (byte) (pc.getSkillEffectTimeSec(COUNTER_MIRROR) / 4);
		}

		// 能量激發
		if (pc.hasSkillEffect(ADDITIONAL_FIRE)) {
			data[23] = (byte) (Math.min(pc.getSkillEffectTimeSec(ADDITIONAL_FIRE), 4080) / 16);
		}

		// 弱化屬性
		if (pc.hasSkillEffect(ELEMENTAL_FALL_DOWN)) {
			data[24] = (byte) (Math.min(pc.getSkillEffectTimeSec(ELEMENTAL_FALL_DOWN), 1020) / 4);
			data[25] = 0x03;
		}

		// 屬性之火
		if (pc.hasSkillEffect(ELEMENTAL_FIRE)) {
			data[26] = (byte) (pc.getSkillEffectTimeSec(ELEMENTAL_FIRE) / 4);
		}

		// 烈焰氣息
		if (pc.hasSkillEffect(DANCING_BLAZE)) {
			data[27] = (byte) (pc.getSkillEffectTimeSec(DANCING_BLAZE) / 4);
		}

		// 火焰之影
		// data[28] = (byte) 0xff;
		// data[29] = 0x00;

		// 精準射擊
		if (pc.hasSkillEffect(STRIKER_GALE)) {
			data[30] = (byte) (pc.getSkillEffectTimeSec(STRIKER_GALE) / 4);
		}

		// 烈焰之魂
		if (pc.hasSkillEffect(SOUL_OF_FLAME)) {
			data[31] = (byte) (pc.getSkillEffectTimeSec(SOUL_OF_FLAME) / 4);
		}

		// 污濁之水
		if (pc.hasSkillEffect(POLLUTE_WATER)) {
			data[32] = (byte) (pc.getSkillEffectTimeSec(POLLUTE_WATER) / 4);
		}

		// 料理 (全屬性抗性+10)
		// data[36] = (byte) 0xff;
		// data[37] = 0x00;

		// 象牙塔妙藥
		// data[42] = (byte) 0xff;
		// data[43] = 0x36; // 因為妙藥，身心都很輕鬆。提升體力回復量和魔力回復量。

		// 福利慎重藥水、慎重藥水 (秒數/16)
		// data[44] = (byte) 0xff;
		// data[45] = 0x00;

		// 經驗藥水時間
		// data[45] = (byte) 0xe1;
		// data[62] = (byte) 0x14;

		// 體力上限+50,體力恢復量+4 (秒數/16)
		// data[46] = (byte) 0xff;
		// data[47] = 0x00;

		// 150% ~ 250% 神力藥水
		// data[45] = (byte) 0xff;
		// data[62] = 50; // 狩獵經驗值將會增加。

		// 媽祖的祝福
		if (pc.hasSkillEffect(MAZU_STATUS)) {
			data[48] = (byte) (pc.getSkillEffectTimeSec(MAZU_STATUS) / 16);

			if (data[48] != 0) {
				data[49] = 0x2c; // 感受到媽祖的祝福。
			}
		}

		// 專注
		if (pc.hasSkillEffect(CONCENTRATION)) {
			data[52] = (byte) (pc.getSkillEffectTimeSec(CONCENTRATION) / 16);
		}

		// 洞察
		if (pc.hasSkillEffect(INSIGHT)) {
			data[53] = (byte) (pc.getSkillEffectTimeSec(INSIGHT) / 16);
		}

		// 恐慌
		if (pc.hasSkillEffect(PANIC)) {
			data[54] = (byte) (pc.getSkillEffectTimeSec(PANIC) / 16);
		}

		// 致命身軀
		if (pc.hasSkillEffect(MORTAL_BODY)) {
			data[55] = (byte) (pc.getSkillEffectTimeSec(MORTAL_BODY) / 4);
		}

		// 驚悚死神
		if (pc.hasSkillEffect(HORROR_OF_DEATH)) {
			data[56] = (byte) (pc.getSkillEffectTimeSec(HORROR_OF_DEATH) / 4);
		}

		// 恐懼無助
		if (pc.hasSkillEffect(RESIST_FEAR)) {
			data[57] = (byte) (pc.getSkillEffectTimeSec(RESIST_FEAR) / 4);
		}

		// 耐力
		if (pc.hasSkillEffect(PATIENCE)) {
			data[58] = (byte) (pc.getSkillEffectTimeSec(PATIENCE) / 4);
		}

		// 護衛毀滅
		if (pc.hasSkillEffect(GUARD_BRAKE)) {
			data[59] = (byte) (pc.getSkillEffectTimeSec(GUARD_BRAKE) / 4);
		}

		// 龍之護鎧
		if (pc.hasSkillEffect(DRAGON_SKIN)) {
			data[60] = (byte) (pc.getSkillEffectTimeSec(DRAGON_SKIN) / 16);
		}

		// 生命之樹果實
		if (pc.hasSkillEffect(STATUS_RIBRAVE)) {
			data[61] = (byte) (pc.getSkillEffectTimeSec(STATUS_RIBRAVE) / 4);
		}

		// 登錄遊戲的時間
		final int time = (int) (System.currentTimeMillis() / 1000);
		data[72] = (byte) (time & 0xff);
		data[73] = (byte) ((time >> 8) & 0xff);
		data[74] = (byte) ((time >> 16) & 0xff);
		data[75] = (byte) ((time >> 24) & 0xff);

		// 紫色殷海薩
		// data[129] = (byte) 0x13;
		// data[130] = (byte) 0x00;
		// data[131] = (byte) 0xff; // Time << 8

		try {
			writeByte(data);
		} finally {
			data = null;
		}
	}

	/**
	 * 圖示_4 - 建構式
	 * 
	 * @param type <br>
	 *            <b> 147<br>
	 *            154<br>
	 *            </b>
	 * @param mode <br>
	 *            <b> 0 = 因師徒關係，感受到能力的保佑，防禦力+1<br>
	 *            1 = 因師徒關係，感受到能力的保佑，防禦力+1、額外魔法防禦力+1<br>
	 *            2 = 因師徒關係，感受到能力的保佑，防禦力+1、額外魔法防禦力+1、所有屬性防禦力+2<br>
	 *            3 = 因師徒關係，感受到能力的保佑，防禦力+1、額外魔法防禦力+1、所有屬性防禦力+2、迴避率+1<br>
	 *            4 = 因師徒關係，感受到能力的保佑，防禦力+3<br>
	 *            5 = 因師徒關係，感受到能力的保佑，防禦力+3、額外魔法防禦力+3<br>
	 *            6 = 因師徒關係，感受到能力的保佑，防禦力+3、額外魔法防禦力+3、所有屬性防禦力+6<br>
	 *            7 = 因師徒關係，感受到能力的保佑，防禦力+3、額外魔法防禦力+3、所有屬性防禦力+6、迴避率+2<br>
	 *            10 = 近距離附加打擊/命中、魔法防禦、各屬性抵抗力、體力/魔力恢復、傷害減免、經驗值獎勵<br>
	 *            11 = 遠距離附加打擊/命中、魔法防禦、各屬性抵抗力、體力/魔力恢復、傷害減免、經驗值獎勵<br>
	 *            12 = 魔法攻擊、魔法防禦、各屬性抵抗力、體力/魔力恢復、傷害減免、經驗值獎勵<br>
	 *            13 = 傷害減免、經驗值獎勵<br>
	 *            16 = 料理 (防禦力+3)<br>
	 *            17 = 藍色-殷海薩<br>
	 *            18 = 暗隱術<br>
	 *            19 = 大地屏障<br>
	 *            20 = 擬似魔法武器<br>
	 *            21 = 地面障礙<br>
	 *            22 = 混亂<br>
	 *            23 = 闇盲咒術<br>
	 *            24 = 黑闇之影<br>
	 *            25 = 沉睡之影<br>
	 *            26 = 冰矛圍籬<br>
	 *            27 = 木乃伊的詛咒-身體被痲痺了<br>
	 *            28 = 沉睡之霧<br>
	 *            29 = 立方：和諧<br>
	 *            30 = 立方：燃燒<br>
	 *            31 = 立方：地裂<br>
	 *            32 = 毒咒<br>
	 *            33 = 暗影之牙<br>
	 *            34 = 魔法屏障<br>
	 *            35 = 鎧甲護持<br>
	 *            36 = 木乃伊的詛咒-身體已完全硬化<br>
	 *            38 = 料理 (獲得狩獵經驗+20%的效果)<br>
	 *            39 = 未知 (額外攻擊點數+2、攻擊命中率+2) 40 = 未知 (額外攻擊點數+3、攻擊命中率+3) 41 = 未知
	 *            (魔力回復+5、體力回復+5) 42 = 未知 (傷害減免+5) 43 = 未知 (體力上限+100、魔力上限+100)
	 *            50 = 破壞盔甲<br>
	 *            51 = 0階(鬥士)<br>
	 *            52 = 1階(鬥士)<br>
	 *            53 = 2階(鬥士)<br>
	 *            54 = 3階(鬥士)<br>
	 *            55 = 4階(鬥士)<br>
	 *            56 = 5階(鬥士)<br>
	 *            57 = 6階(鬥士)<br>
	 *            58 = 7階(鬥士)<br>
	 *            59 = 8階(鬥士)<br>
	 *            60 = 9階(鬥士)<br>
	 *            61 = 0階(弓手)<br>
	 *            62 = 1階(弓手)<br>
	 *            63 = 2階(弓手)<br>
	 *            64 = 3階(弓手)<br>
	 *            65 = 4階(弓手)<br>
	 *            66 = 5階(弓手)<br>
	 *            67 = 6階(弓手)<br>
	 *            68 = 7階(弓手)<br>
	 *            69 = 8階(弓手)<br>
	 *            70 = 9階(弓手)<br>
	 *            71 = 0階(賢者)<br>
	 *            72 = 1階(賢者)<br>
	 *            73 = 2階(賢者)<br>
	 *            74 = 3階(賢者)<br>
	 *            75 = 4階(賢者)<br>
	 *            76 = 5階(賢者)<br>
	 *            77 = 6階(賢者)<br>
	 *            78 = 7階(賢者)<br>
	 *            79 = 8階(賢者)<br>
	 *            80 = 9階(賢者)<br>
	 *            81 = 0階(衝鋒)<br>
	 *            82 = 1階(衝鋒)<br>
	 *            83 = 2階(衝鋒)<br>
	 *            84 = 3階(衝鋒)<br>
	 *            85 = 4階(衝鋒)<br>
	 *            86 = 5階(衝鋒)<br>
	 *            87 = 6階(衝鋒)<br>
	 *            88 = 7階(衝鋒)<br>
	 *            89 = 8階(衝鋒)<br>
	 *            90 = 9階(衝鋒)<br>
	 *            91 = 未知-經驗+20%<br>
	 *            92 = 藥水霜化術<br>
	 *            93 = 初心者小幫手<br>
	 *            94 = 中秋月兔<br>
	 *            95 = 中秋月兔<br>
	 *            96 = 月餅<br>
	 *            97 = 牛肉月餅<br>
	 *            98 = 未知-神聖力量<br>
	 *            99 = 丹特斯的氣息<br>
	 *            100 = 經驗加倍<br>
	 *            101 = 黑蛇的祝福<br>
	 *            102 = 靈魂昇華<br>
	 *            103 = 生命之泉<br>
	 *            104 = 鋼鐵防護<br>
	 *            105 = 未知 (增加體力上限、魔力上限、防禦力提升)<br>
	 *            106 = 衝擊士氣<br>
	 *            107 = 激勵士氣<br>
	 *            108 = 未知 [日本]EXP+5%、傷害減免+5<br>
	 *            109 = [料理] (體力上限+25、魔力上限+20、傷害減免+2)<br>
	 *            228 = [料理] (有MP 80與 MP恢覆率 4、近距離、遠距離、魔法命中率+3的效果)<br>
	 *            228 = [料理] (有MP 80與 MP恢覆率 4、近距離、遠距離、魔法命中率+3的效果)<br>
	 *            229 = [料理] (遠距離攻擊力+2與遠距離命中率+6、近距離及魔法命中率+3的效果)<br>
	 *            230 = [料理] (近距離攻擊力+2與近距離命中率+6、遠距離及魔法命中率+3的效果)<br>
	 *            231 = [料理] (魔法防禦+18與屬性防禦+15、近距離、遠距離、魔法命中率+3的效果)<br>
	 *            232 = [料理] (魔力上限+2與魔法命中率+6、近距離、遠距離攻擊命中率+3的效果)<br>
	 *            233 = [料理] (防禦+4與近距離、遠距離、魔法命中率+3的效果)<br>
	 *            234 = [料理] (狩獵時取得的經驗值些微增加、有近距離、遠距離、魔法命中率+3的效果)<br>
	 *            235 = 未知 (暫時可依神聖力量將周圍做淨化) 236 = [料理]
	 *            (狩獵時取得的經驗值些微增加、有近距離、遠距離、魔法命中率+3的效果)<br>
	 *            239 = [道具] (慎重藥水)<br>
	 *            240 = 未知 (英雄的保佑在所有物體上都添加了保護的力量)<br>
	 *            241 = [技能] (影之防護)<br>
	 *            242 = 未知 (暫時提昇移動與攻擊速度) [加速]<br>
	 *            243 = 未知 (暫時提昇移動與攻擊速度) [特殊加速]<br>
	 *            244 = 未知 (感受到性感男孩巧克力的魔法加持)<br>
	 *            245 = 未知 (感受到害羞男孩巧克力的魔法加持)<br>
	 *            246 = 未知 (感受到酷男孩巧克力的魔法加持)<br>
	 *            247 = 未知 (感受到聰明男孩巧克力的魔法加持)<br>
	 *            248 = 未知 (感受到酷炫男孩巧克力的魔法加持)<br>
	 *            249 = 未知 (感受到神秘男孩巧克力的魔法加持)<br>
	 *            250 = 任務 (懷錶的神秘力量)<br>
	 *            251 = [料理] (熱情雞尾酒效果)<br>
	 *            252 = [料理] (酸甜雞尾酒效果)<br>
	 *            253 = [料理] (清爽雞尾酒效果)<br>
	 *            255 = [道具] (守護者靈魂)<br>
	 *            </b>
	 * @param flag
	 */
	public S_Icons(final int type, final int mode, final int time) {
		// [S] id:238 (S_OPCODE_PACKETBOX) len:10 [2014-09-22 01:07:04]
		// 0000: ee 9a cc 01 eb 02 00 00 00 00 ..........

		writeC(S_OPCODE_PACKETBOX);
		writeC(type);

		if (type == 154) {
			writeH(time);
		} else {
			writeC(time & 0xff);
		}

		writeC(mode);
		writeC(0x01);
		writeD(0x00000000);
	}

	/*
	 * 卡司特之毒
	 */
	// [S] id:238 (S_OPCODE_PACKETBOX) len:6 [2014-10-06 10:26:08]
	// 0000: ee a1 06 78 00 01 ...x..

	// 蛇女之毒
	// [S] id:238 (S_OPCODE_PACKETBOX) len:6 [2014-10-06 10:52:34]
	// 0000: ee a1 01 32 00 03 ...2..

	/**
	 * 裝備弓 (有神聖武器)<br>
	 * [S] id:238 (S_OPCODE_PACKETBOX) len:10 [2014-10-05 04:17:16]<br>
	 * 0000: ee 9a b0 04 75 08 00 00 00 00 ....u.....
	 * <p>
	 * 卸下弓 (有神聖武器)<br>
	 * [S] id:238 (S_OPCODE_PACKETBOX) len:10 [2014-10-05 04:17:44]<br>
	 * 0000: ee 9a 00 00 75 08 00 00 00 00 ....u.....
	 * <p>
	 * 裝備弓 (有擬似魔法武器)<br>
	 * [S] id:238 (S_OPCODE_PACKETBOX) len:10 [2014-10-05 04:51:38]<br>
	 * 0000: ee 9a 08 07 eb 02 00 00 00 00 ..........
	 * <p>
	 * 卸下弓 (有擬似魔法武器)<br>
	 * [S] id:238 (S_OPCODE_PACKETBOX) len:10 [2014-10-05 04:52:56]<br>
	 * 0000: ee 9a 00 00 eb 02 00 00 00 00 ..........
	 * <p>
	 * 裝備刀 (有祝福武器)<br>
	 * [S] id:238 (S_OPCODE_PACKETBOX) len:10 [2014-10-05 04:49:15]<br>
	 * 0000: ee 9a b0 04 80 08 00 00 00 00 ..........
	 * <p>
	 * 卸下弓 (有祝福武器)<br>
	 * [S] id:238 (S_OPCODE_PACKETBOX) len:10 [2014-10-05 04:50:06]<br>
	 * 0000: ee 9a 00 00 80 08 00 00 00 00 ..........
	 * <p>
	 * 裝備黑暗棲林者盔甲 (有鎧甲護持)<br>
	 * [S] id:238 (S_OPCODE_PACKETBOX) len:10 [2014-10-05 05:06:56]<br>
	 * 0000: ee 9a 06 07 ec 02 00 00 00 00 ..........
	 * <p>
	 * 卸下黑暗棲林者盔甲 (有鎧甲護持)<br>
	 * [S] id:238 (S_OPCODE_PACKETBOX) len:10 [2014-10-05 05:08:42]<br>
	 * 0000: ee 9a 00 00 ec 02 00 00 00 00 ..........
	 * <p>
	 * 
	 * @param mode 種類
	 * @param time 時間
	 */
	public S_Icons(final int mode, final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x9a);
		writeH(time);
		writeC(mode);
		writeC(0x08);
		writeD(0x00000000);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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