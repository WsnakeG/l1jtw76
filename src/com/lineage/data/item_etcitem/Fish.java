package com.lineage.data.item_etcitem;

import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_1;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_2;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;

import java.util.Random;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_PacketBoxHpMsg;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 各种的鱼 鱈魚41298 虎斑帶魚41299 鮪魚41300 發紅光的魚41301 發綠光的魚41302 發藍光的魚41303 發白光的魚41304
 */
public class Fish extends ItemExecutor {

	/**
	 *
	 */
	private Fish() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Fish();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		final int itemId = item.getItemId();
		final Random random = new Random();
		int getItemId = 0;
		int getCount = 0;
		switch (itemId) {
		case (41298):// 鱈魚
			UseHeallingPotion(pc, 4, 189);
			break;

		case (41299):// 虎斑帶魚
			UseHeallingPotion(pc, 15, 194);
			break;

		case (41300): // 鮪魚
			UseHeallingPotion(pc, 35, 197);
			break;

		case (41301):// 發紅光的魚
			final int chance1 = random.nextInt(10);
			if ((chance1 >= 0) && (chance1 < 5)) {
				UseHeallingPotion(pc, 15, 189);

			} else if ((chance1 >= 5) && (chance1 < 9)) {
				getItemId = 40019;
				getCount = 1;

			} else if (chance1 >= 9) {
				final int gemChance = random.nextInt(3);
				if (gemChance == 0) {
					getItemId = 40045;
					getCount = 1;

				} else if (gemChance == 1) {
					getItemId = 40049;
					getCount = 1;

				} else if (gemChance == 2) {
					getItemId = 40053;
					getCount = 1;
				}
			}
			break;

		case (41302):// 發綠光的魚
			final int chance2 = random.nextInt(3);
			if ((chance2 >= 0) && (chance2 < 5)) {
				UseHeallingPotion(pc, 15, 189);

			} else if ((chance2 >= 5) && (chance2 < 9)) {
				getItemId = 40018;
				getCount = 1;

			} else if (chance2 >= 9) {
				final int gemChance = random.nextInt(3);
				if (gemChance == 0) {
					getItemId = 40047;
					getCount = 1;

				} else if (gemChance == 1) {
					getItemId = 40051;
					getCount = 1;

				} else if (gemChance == 2) {
					getItemId = 40055;
					getCount = 1;
				}
			}
			break;

		case (41303):// 發藍光的魚
			final int chance3 = random.nextInt(3);
			if ((chance3 >= 0) && (chance3 < 5)) {
				UseHeallingPotion(pc, 15, 189);

			} else if ((chance3 >= 5) && (chance3 < 9)) {
				getItemId = 40015;
				getCount = 1;

			} else if (chance3 >= 9) {
				final int gemChance = random.nextInt(3);
				if (gemChance == 0) {
					getItemId = 40046;
					getCount = 1;

				} else if (gemChance == 1) {
					getItemId = 40050;
					getCount = 1;

				} else if (gemChance == 2) {
					getItemId = 40054;
					getCount = 1;
				}
			}
			break;

		case (41304):// 發白光的魚
			final int chance = random.nextInt(3);
			if ((chance >= 0) && (chance < 5)) {
				UseHeallingPotion(pc, 15, 189);

			} else if ((chance >= 5) && (chance < 9)) {
				getItemId = 40021;
				getCount = 1;

			} else if (chance >= 9) {
				final int gemChance = random.nextInt(3);
				if (gemChance == 0) {
					getItemId = 40044;
					getCount = 1;

				} else if (gemChance == 1) {
					getItemId = 40048;
					getCount = 1;

				} else if (gemChance == 2) {
					getItemId = 40052;
					getCount = 1;
				}
			}
			break;
		}

		pc.getInventory().removeItem(item, 1);
		if (getCount != 0) {
			// 取得道具
			CreateNewItem.createNewItem(pc, getItemId, getCount);
		}

	}

	private void UseHeallingPotion(final L1PcInstance pc, int healHp, final int gfxid) {
		final Random _random = new Random();
		if (pc.hasSkillEffect(71) == true) { // 药水箱化术
			pc.sendPackets(new S_ServerMessage(698)); // 喉咙灼热，无法喝东西。
			return;
		}

		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		pc.sendPacketsX8(new S_SkillSound(pc.getId(), gfxid));

		healHp *= (_random.nextGaussian() / 5.0D) + 1.0D;
		if (pc.get_up_hp_potion() > 0) {
			healHp += (healHp * pc.get_up_hp_potion()) / 100;
		}
		if (pc.hasSkillEffect(POLLUTE_WATER)) {
			healHp = (healHp >> 1);
		}
		if (pc.hasSkillEffect(ADLV80_2_2)) {// 污濁的水流(水龍副本 回復量1/2倍)
			healHp = (healHp >> 1);
		}
		if (pc.hasSkillEffect(ADLV80_2_1)) {
			healHp *= -1;
		}
		if (healHp > 0) {
			// 你覺得舒服多了訊息
			pc.sendPackets(new S_PacketBoxHpMsg());
		}
		pc.setCurrentHp(pc.getCurrentHp() + healHp);
	}
}
