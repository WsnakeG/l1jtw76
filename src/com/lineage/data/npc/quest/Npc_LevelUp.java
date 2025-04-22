package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.clientpackets.C_CharReset;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.CalcInitHpMp;
import com.lineage.server.utils.CalcStat;

/**
 * 英雄之魂 81004
 * 
 * @author dexc
 */
public class Npc_LevelUp extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_LevelUp.class);

	/**
	 * 轉生尊者
	 */
	private Npc_LevelUp() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_LevelUp();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "yuleX1_1", null));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		String[] info = null;
		if (cmd.equalsIgnoreCase("c")) {// 我想調整我的屬性
			final L1ItemInstance item = pc.getInventory().checkItemX(49142, 1);
			if (item != null) {
				pc.get_otherList().clear_uplevelList();
				info = showInfo(pc, 2);

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ySrc_E", info));
			}

		} else if (cmd.equalsIgnoreCase("s1")) {// 力量 +
			info = showInfoX(pc, 1);

		} else if (cmd.equalsIgnoreCase("s2")) {// 力量 -
			info = showInfoX(pc, 2);

		} else if (cmd.equalsIgnoreCase("d1")) {// 敏捷 +
			info = showInfoX(pc, 3);

		} else if (cmd.equalsIgnoreCase("d2")) {// 敏捷 -
			info = showInfoX(pc, 4);

		} else if (cmd.equalsIgnoreCase("c1")) {// 體質 +
			info = showInfoX(pc, 5);

		} else if (cmd.equalsIgnoreCase("c2")) {// 體質 -
			info = showInfoX(pc, 6);

		} else if (cmd.equalsIgnoreCase("w1")) {// 精神 +
			info = showInfoX(pc, 7);

		} else if (cmd.equalsIgnoreCase("w2")) {// 精神 -
			info = showInfoX(pc, 8);

		} else if (cmd.equalsIgnoreCase("i1")) {// 智力 +
			info = showInfoX(pc, 9);

		} else if (cmd.equalsIgnoreCase("i2")) {// 智力 -
			info = showInfoX(pc, 10);

		} else if (cmd.equalsIgnoreCase("h1")) {// 魅力 +
			info = showInfoX(pc, 11);

		} else if (cmd.equalsIgnoreCase("h2")) {// 魅力 -
			info = showInfoX(pc, 12);

		} else if (cmd.equalsIgnoreCase("x")) {// 我決定好了(出生點數)
			// 49142 回憶蠟燭
			final L1ItemInstance item = pc.getInventory().checkItemX(49142, 1);
			if (item != null) {
				final int elixirStats = pc.get_otherList().get_uplevelList(0);
				if (elixirStats > 0) {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "yuleX4",
							new String[] { String.valueOf(elixirStats) }));
					return;
				}
				// 技能解除/裝備解除
				Npc_LevelUp.stopSkill(pc);

				info = showInfo(pc, 0);

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ySrc_E", info));
			}

		} else if (cmd.equalsIgnoreCase("b")) {// 我決定好了(升級點數)
			// 49142 回憶蠟燭
			final L1ItemInstance item = pc.getInventory().checkItemX(49142, 1);
			if (item != null) {
				final int elixirStats = pc.get_otherList().get_uplevelList(0);
				if (elixirStats > 0) {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "yuleX4",
							new String[] { String.valueOf(elixirStats) }));
					return;
				}
				// 技能解除/裝備解除
				Npc_LevelUp.stopSkill(pc);

				info = showInfo(pc, 1);

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ySrc_E", info));
			}

		} else if (cmd.equalsIgnoreCase("d")) {// 我決定好了(萬能藥點數)
			// 49142 回憶蠟燭
			final L1ItemInstance item = pc.getInventory().checkItemX(49142, 1);
			if (item != null) {
				// 取回升級點數可分配剩餘數量
				final int elixirStats = pc.get_otherList().get_uplevelList(0);
				if (elixirStats > 0) {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "yuleX4",
							new String[] { String.valueOf(elixirStats) }));
					return;
				}
				// 技能解除/裝備解除
				Npc_LevelUp.stopSkill(pc);

				pc.getInventory().removeItem(item, 1);// 刪除道具

				final int hp = CalcInitHpMp.calcInitHp(pc);
				final int mp = CalcInitHpMp.calcInitMp(pc);

				final int baseStr = pc.getBaseStr();
				final int baseDex = pc.getBaseDex();
				final int baseCon = pc.getBaseCon();
				final int baseWis = pc.getBaseWis();
				final int baseInt = pc.getBaseInt();
				final int baseCha = pc.getBaseCha();

				pc.addBaseStr(-baseStr);
				pc.addBaseDex(-baseDex);
				pc.addBaseCon(-baseCon);
				pc.addBaseWis(-baseWis);
				pc.addBaseInt(-baseInt);
				pc.addBaseCha(-baseCha);

				final int originalStr = pc.get_otherList().get_uplevelList(1);
				final int originalDex = pc.get_otherList().get_uplevelList(2);
				final int originalCon = pc.get_otherList().get_uplevelList(3);
				final int originalWis = pc.get_otherList().get_uplevelList(4);
				final int originalInt = pc.get_otherList().get_uplevelList(5);
				final int originalCha = pc.get_otherList().get_uplevelList(6);

				final int addStr = pc.get_otherList().get_uplevelList(7);
				final int addDex = pc.get_otherList().get_uplevelList(8);
				final int addCon = pc.get_otherList().get_uplevelList(9);
				final int addWis = pc.get_otherList().get_uplevelList(10);
				final int addInt = pc.get_otherList().get_uplevelList(11);
				final int addCha = pc.get_otherList().get_uplevelList(12);

				pc.addBaseStr((byte) ((addStr + originalStr) - 1));
				pc.addBaseDex((byte) ((addDex + originalDex) - 1));
				pc.addBaseCon((byte) ((addCon + originalCon) - 1));
				pc.addBaseWis((byte) ((addWis + originalWis) - 1));
				pc.addBaseInt((byte) ((addInt + originalInt) - 1));
				pc.addBaseCha((byte) ((addCha + originalCha) - 1));

				// 變更原始素質設定
				pc.setOriginalStr(pc.get_otherList().get_newPcOriginal()[0]);
				pc.setOriginalDex(pc.get_otherList().get_newPcOriginal()[1]);
				pc.setOriginalCon(pc.get_otherList().get_newPcOriginal()[2]);
				pc.setOriginalWis(pc.get_otherList().get_newPcOriginal()[3]);
				pc.setOriginalInt(pc.get_otherList().get_newPcOriginal()[4]);
				pc.setOriginalCha(pc.get_otherList().get_newPcOriginal()[5]);

				pc.addMr(0 - pc.getMr());
				pc.addDmgup(0 - pc.getDmgup());
				pc.addHitup(0 - pc.getHitup());
				pc.addBaseMaxHp((short) (hp - pc.getBaseMaxHp()));
				pc.addBaseMaxMp((short) (mp - pc.getBaseMaxMp()));

				if (pc.getOriginalAc() > 0) {
					pc.addAc(pc.getOriginalAc());
				}

				if (pc.getOriginalMr() > 0) {
					pc.addMr(0 - pc.getOriginalMr());
				}

				// 全屬性重置
				pc.refresh();

				setHpMp(pc);

				pc.setCurrentHp(pc.getMaxHp());
				pc.setCurrentMp(pc.getMaxMp());

				// //////////////////////
				try {
					// 紀錄人物初始化資料
					CharacterTable.saveCharStatus(pc);

					pc.sendPackets(new S_OwnCharStatus2(pc));
					// 更新角色防禦屬性
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					// 更改人物状态
					pc.sendPackets(new S_OwnCharStatus(pc));
					// 更改人物魔法攻击与魔法防御
					pc.sendPackets(new S_SPMR(pc));
					pc.save();

				} catch (final Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7625));

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ySrc_E", info));
			}
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
			// 清空屬性重置處理清單
			pc.get_otherList().clear_uplevelList();
		}

		if (info != null) {
			final int xmode = pc.get_otherList().get_uplevelList(13);
			switch (xmode) {
			case 0:// 0:升級點數
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "yuleX2", info));
				break;

			case 1:// 1:萬能藥點數
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "yuleX2_1", info));
				break;

			case 2:// 2:人物出生數值
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ySrc_1", info));
				break;
			}
		}
	}

	/**
	 * 重新計算HP MP
	 * 
	 * @param pc 執行人物
	 */
	public static void setHpMp(final L1PcInstance pc) {
		for (int i = 0; i < pc.getLevel(); i++) {
			final short randomHp = CalcStat.calcStatHp(pc.getType(), pc.getBaseMaxHp(), pc.getBaseCon(),
					pc.getOriginalHpup());

			final short randomMp = CalcStat.calcStatMp(pc.getType(), pc.getBaseMaxMp(), pc.getBaseWis(),
					pc.getOriginalMpup());

			pc.addBaseMaxHp(randomHp);
			pc.addBaseMaxMp(randomMp);
		}
	}

	/**
	 * 開始選取點數分配
	 * 
	 * @param pc
	 * @param mode 1 力量 + 2 力量 - 3 敏捷 + 4 敏捷 - 5 體質 + 6 體質 - 7 精神 + 8 精神 - 9 智力
	 *            + 10 智力 - 11 魅力 + 12 魅力 -
	 * @return
	 */
	public static String[] showInfoX(final L1PcInstance pc, final int mode) {
		final int xmode = pc.get_otherList().get_uplevelList(13);
		int max[] = new int[] { 0, 0, 0, 0, 0, 0 };
		switch (xmode) {
		case 0:// 0:升級點
			max = new int[] { ConfigAlt.POWER, ConfigAlt.POWER, ConfigAlt.POWER, ConfigAlt.POWER,
					ConfigAlt.POWER, ConfigAlt.POWER };// ConfigAlt.POWER;
			break;

		case 1:// 1:萬能藥
			max = new int[] { ConfigAlt.POWERMEDICINE, ConfigAlt.POWERMEDICINE, ConfigAlt.POWERMEDICINE,
					ConfigAlt.POWERMEDICINE, ConfigAlt.POWERMEDICINE, ConfigAlt.POWERMEDICINE };// ConfigAlt.POWERMEDICINE;
			break;

		case 2:// 2:人物出生點數
			final int type = pc.getType();
			// 各職業初始化屬性可分配最大值(力量,敏捷,體質,精神,魅力,智力)
			switch (type) {
			case 0:// 0:王族
				max = new int[] { 20, 18, 18, 18, 18, 18 };
				break;

			case 1:// 1:騎士
				max = new int[] { 20, 16, 18, 13, 12, 16 };
				break;

			case 2:// 2:精靈
				max = new int[] { 18, 18, 18, 18, 18, 16 };
				break;

			case 3:// 3:法師
				max = new int[] { 20, 14, 18, 18, 18, 18 };
				break;

			case 4:// 4:黑妖
				max = new int[] { 18, 18, 18, 18, 18, 18 };
				break;

			case 5:// 5:龍騎士
				max = new int[] { 19, 16, 18, 17, 17, 14 };
				break;

			case 6:// 6:幻術師
				max = new int[] { 19, 16, 18, 18, 18, 18 };
				break;
			}

			break;
		}

		int elixirStats = pc.get_otherList().get_uplevelList(0);

		final int originalStr = pc.get_otherList().get_uplevelList(1);
		final int originalDex = pc.get_otherList().get_uplevelList(2);
		final int originalCon = pc.get_otherList().get_uplevelList(3);
		final int originalWis = pc.get_otherList().get_uplevelList(4);
		final int originalInt = pc.get_otherList().get_uplevelList(5);
		final int originalCha = pc.get_otherList().get_uplevelList(6);

		int addStr = pc.get_otherList().get_uplevelList(7);
		int addDex = pc.get_otherList().get_uplevelList(8);
		int addCon = pc.get_otherList().get_uplevelList(9);
		int addWis = pc.get_otherList().get_uplevelList(10);
		int addInt = pc.get_otherList().get_uplevelList(11);
		int addCha = pc.get_otherList().get_uplevelList(12);

		switch (mode) {
		case 1:// 力量 +
			elixirStats -= 1;
			if (elixirStats < 0) {
				return null;
			}
			addStr += 1;
			if ((addStr > (max[0] - originalStr)) || (addStr < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(7, addStr);
			break;

		case 2:// 力量 -
			elixirStats += 1;
			if (elixirStats < 0) {
				return null;
			}
			addStr -= 1;
			if ((addStr > (max[0] - originalStr)) || (addStr < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(7, addStr);
			break;

		case 3:// 敏捷 +
			elixirStats -= 1;
			if (elixirStats < 0) {
				return null;
			}
			addDex += 1;
			if ((addDex > (max[1] - originalDex)) || (addDex < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(8, addDex);
			break;

		case 4:// 敏捷 -
			elixirStats += 1;
			if (elixirStats < 0) {
				return null;
			}
			addDex -= 1;
			if ((addDex > (max[1] - originalDex)) || (addDex < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(8, addDex);
			break;

		case 5:// 體質 +
			elixirStats -= 1;
			if (elixirStats < 0) {
				return null;
			}
			addCon += 1;
			if ((addCon > (max[2] - originalCon)) || (addCon < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(9, addCon);
			break;

		case 6:// 體質 -
			elixirStats += 1;
			if (elixirStats < 0) {
				return null;
			}
			addCon -= 1;
			if ((addCon > (max[2] - originalCon)) || (addCon < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(9, addCon);
			break;

		case 7:// 精神 +
			elixirStats -= 1;
			if (elixirStats < 0) {
				return null;
			}
			addWis += 1;
			if ((addWis > (max[3] - originalWis)) || (addWis < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(10, addWis);
			break;

		case 8:// 精神 -
			elixirStats += 1;
			if (elixirStats < 0) {
				return null;
			}
			addWis -= 1;
			if ((addWis > (max[3] - originalWis)) || (addWis < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(10, addWis);
			break;

		case 9:// 智力 +
			elixirStats -= 1;
			if (elixirStats < 0) {
				return null;
			}
			addInt += 1;
			if ((addInt > (max[4] - originalInt)) || (addInt < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(11, addInt);
			break;

		case 10:// 智力 -
			elixirStats += 1;
			if (elixirStats < 0) {
				return null;
			}
			addInt -= 1;
			if ((addInt > (max[4] - originalInt)) || (addInt < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(11, addInt);
			break;

		case 11:// 魅力 +
			elixirStats -= 1;
			if (elixirStats < 0) {
				return null;
			}
			addCha += 1;
			if ((addCha > (max[5] - originalCha)) || (addCha < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(12, addCha);
			break;

		case 12:// 魅力 -
			elixirStats += 1;
			if (elixirStats < 0) {
				return null;
			}
			addCha -= 1;
			if ((addCha > (max[5] - originalCha)) || (addCha < 0)) {
				return null;
			}
			pc.get_otherList().add_levelList(12, addCha);
			break;
		}

		// 更新剩餘點數
		pc.get_otherList().add_levelList(0, elixirStats);

		return info(pc);
	}

	/**
	 * 初始化點選
	 * 
	 * @param pc 執行人物
	 * @param isBonus 0:升級點 1:萬能藥 2:人物出生點數
	 * @return
	 */
	public static String[] showInfo(final L1PcInstance pc, final int mode) {
		int elixirStats = 0;

		// 取回原始數值
		int originalStr = 0;
		int originalDex = 0;
		int originalCon = 0;
		int originalWis = 0;
		int originalInt = 0;
		int originalCha = 0;

		final int type = pc.getType();
		// 各職業初始化屬性(王族, 騎士, 精靈, 法師, 黑妖, 龍騎士, 幻術師)
		originalStr = C_CharReset.ORIGINAL_STR[type];
		originalDex = C_CharReset.ORIGINAL_DEX[type];
		originalCon = C_CharReset.ORIGINAL_CON[type];
		originalWis = C_CharReset.ORIGINAL_WIS[type];
		originalInt = C_CharReset.ORIGINAL_INT[type];
		originalCha = C_CharReset.ORIGINAL_CHA[type];

		switch (mode) {
		case 0:// 0:升級點
				// 加上升級屬性提升值
			if (pc.getBonusStats() > 0) {
				elixirStats += pc.getBonusStats();
			}

			// 升級屬性提升值重選(13-0)
			pc.get_otherList().add_levelList(13, 0);
			break;

		case 1:// 1:萬能藥
				// 加上萬能藥使用次數
			if (pc.getElixirStats() > 0) {
				elixirStats += pc.getElixirStats();
			}
			originalStr = pc.get_otherList().get_uplevelList(1);
			originalDex = pc.get_otherList().get_uplevelList(2);
			originalCon = pc.get_otherList().get_uplevelList(3);
			originalWis = pc.get_otherList().get_uplevelList(4);
			originalInt = pc.get_otherList().get_uplevelList(5);
			originalCha = pc.get_otherList().get_uplevelList(6);

			// 萬能藥使用次數重選(13-1)
			pc.get_otherList().add_levelList(13, 1);
			break;

		case 2:// 2:人物出生點數

			// 各職業初始化可分配點數(王族, 騎士, 精靈, 法師, 黑妖, 龍騎士, 幻術師)
			elixirStats = C_CharReset.ORIGINAL_AMOUNT[type];

			// 人物出生點數重選(13-2)
			pc.get_otherList().add_levelList(13, 2);
			break;
		}

		// 更新剩餘點數
		pc.get_otherList().add_levelList(0, elixirStats);

		switch (mode) {
		case 0:// 0:升級點
				// 取回上一階段(人物出生點數)所決定的點數
			final int addStrS = pc.get_otherList().get_uplevelList(7);
			final int addDexS = pc.get_otherList().get_uplevelList(8);
			final int addConS = pc.get_otherList().get_uplevelList(9);
			final int addWisS = pc.get_otherList().get_uplevelList(10);
			final int addIntS = pc.get_otherList().get_uplevelList(11);
			final int addChaS = pc.get_otherList().get_uplevelList(12);

			pc.get_otherList().add_levelList(1, originalStr + addStrS);// 力量
																		// (原始)
																		// +
																		// 上一階段(人物出生點數)所決定的點數
			pc.get_otherList().add_levelList(2, originalDex + addDexS);// 敏捷
																		// (原始)
																		// +
																		// 上一階段(人物出生點數)所決定的點數
			pc.get_otherList().add_levelList(3, originalCon + addConS);// 體質
																		// (原始)
																		// +
																		// 上一階段(人物出生點數)所決定的點數
			pc.get_otherList().add_levelList(4, originalWis + addWisS);// 精神
																		// (原始)
																		// +
																		// 上一階段(人物出生點數)所決定的點數
			pc.get_otherList().add_levelList(5, originalInt + addIntS);// 智力
																		// (原始)
																		// +
																		// 上一階段(人物出生點數)所決定的點數
			pc.get_otherList().add_levelList(6, originalCha + addChaS);// 魅力
																		// (原始)
																		// +
																		// 上一階段(人物出生點數)所決定的點數

			// 暫存人物原始素質改變
			pc.get_otherList()
					.set_newPcOriginal(new int[] { originalStr + addStrS, // 力量
																			// (原始)
																			// +
																			// 上一階段(人物出生點數)所決定的點數
							originalDex + addDexS, // 敏捷 (原始) +
													// 上一階段(人物出生點數)所決定的點數
							originalCon + addConS, // 體質 (原始) +
													// 上一階段(人物出生點數)所決定的點數
							originalWis + addWisS, // 精神 (原始) +
													// 上一階段(人物出生點數)所決定的點數
							originalInt + addIntS, // 智力 (原始) +
													// 上一階段(人物出生點數)所決定的點數
							originalCha + addChaS,// 魅力 (原始)
			});
			break;

		case 1:// 1:萬能藥
				// 取回上一階段(升級屬性提升值)所決定的點數
			final int addStrR = pc.get_otherList().get_uplevelList(7);
			final int addDexR = pc.get_otherList().get_uplevelList(8);
			final int addConR = pc.get_otherList().get_uplevelList(9);
			final int addWisR = pc.get_otherList().get_uplevelList(10);
			final int addIntR = pc.get_otherList().get_uplevelList(11);
			final int addChaR = pc.get_otherList().get_uplevelList(12);

			pc.get_otherList().add_levelList(1, originalStr + addStrR);// 力量
																		// (原始)
																		// +
																		// 上一階段(升級屬性提升值)所決定的點數
			pc.get_otherList().add_levelList(2, originalDex + addDexR);// 敏捷
																		// (原始)
																		// +
																		// 上一階段(升級屬性提升值)所決定的點數
			pc.get_otherList().add_levelList(3, originalCon + addConR);// 體質
																		// (原始)
																		// +
																		// 上一階段(升級屬性提升值)所決定的點數
			pc.get_otherList().add_levelList(4, originalWis + addWisR);// 精神
																		// (原始)
																		// +
																		// 上一階段(升級屬性提升值)所決定的點數
			pc.get_otherList().add_levelList(5, originalInt + addIntR);// 智力
																		// (原始)
																		// +
																		// 上一階段(升級屬性提升值)所決定的點數
			pc.get_otherList().add_levelList(6, originalCha + addChaR);// 魅力
																		// (原始)
																		// +
																		// 上一階段(升級屬性提升值)所決定的點數
			break;

		case 2:// 2:人物出生點數
			pc.get_otherList().add_levelList(1, originalStr);// 力量 (原始)
			pc.get_otherList().add_levelList(2, originalDex);// 敏捷 (原始)
			pc.get_otherList().add_levelList(3, originalCon);// 體質 (原始)
			pc.get_otherList().add_levelList(4, originalWis);// 精神 (原始)
			pc.get_otherList().add_levelList(5, originalInt);// 智力 (原始)
			pc.get_otherList().add_levelList(6, originalCha);// 魅力 (原始)
			break;
		}

		// 歸零 上一階段(升級屬性提升值)所決定的點數
		final int addStr = 0;
		pc.get_otherList().add_levelList(7, addStr);
		final int addDex = 0;
		pc.get_otherList().add_levelList(8, addDex);
		final int addCon = 0;
		pc.get_otherList().add_levelList(9, addCon);
		final int addWis = 0;
		pc.get_otherList().add_levelList(10, addWis);
		final int addInt = 0;
		pc.get_otherList().add_levelList(11, addInt);
		final int addCha = 0;
		pc.get_otherList().add_levelList(12, addCha);

		return info(pc);
	}

	/**
	 * 傳回顯示於HTML的文字
	 * 
	 * @param pc
	 * @return
	 */
	private static String[] info(final L1PcInstance pc) {
		String[] info = null;
		final int p1 = pc.get_otherList().get_uplevelList(0);// 剩餘可用點數

		final int p2 = pc.get_otherList().get_uplevelList(1);// 力量 (原始)
		final int p3 = pc.get_otherList().get_uplevelList(7);// 力量 +-

		final int p4 = pc.get_otherList().get_uplevelList(2);// 敏捷 (原始)
		final int p5 = pc.get_otherList().get_uplevelList(8);// 敏捷 +-

		final int p6 = pc.get_otherList().get_uplevelList(3);// 體質 (原始)
		final int p7 = pc.get_otherList().get_uplevelList(9);// 體質 +-

		final int p8 = pc.get_otherList().get_uplevelList(4);// 精神 (原始)
		final int p9 = pc.get_otherList().get_uplevelList(10);// 精神 +-

		final int p10 = pc.get_otherList().get_uplevelList(5);// 智力 (原始)
		final int p11 = pc.get_otherList().get_uplevelList(11);// 智力 +-

		final int p12 = pc.get_otherList().get_uplevelList(6);// 魅力 (原始)
		final int p13 = pc.get_otherList().get_uplevelList(12);// 魅力 +-

		final int xmode = pc.get_otherList().get_uplevelList(13);
		if (xmode == 2) {
			final int type = pc.getType();

			// 各職業初始化可分配點數(王族, 騎士, 精靈, 法師, 黑妖, 龍騎士, 幻術師)
			final int elixirStats = C_CharReset.ORIGINAL_AMOUNT[type];

			String nameid = "";
			// 各職業初始化屬性(王族, 騎士, 精靈, 法師, 黑妖, 龍騎士, 幻術師)
			switch (type) {
			case 0:// 0:王族
					// 1127：[王族]
				nameid = "$1127";
				break;

			case 1:// 1:騎士
					// 1128：[騎士]
				nameid = "$1128";
				break;

			case 2:// 2:精靈
					// 1129：[妖精]
				nameid = "$1129";
				break;

			case 3:// 3:法師
					// 1130：[法師]
				nameid = "$1130";
				break;

			case 4:// 4:黑妖
					// 2503：[黑暗妖精]
				nameid = "$2503";
				break;

			case 5:// 5:龍騎士
					// 5889：[龍騎士]
				nameid = "$5889";
				break;

			case 6:// 6:幻術師
					// 5890：[幻術士]
				nameid = "$5890";
				break;
			}
			info = new String[] { nameid, String.valueOf(elixirStats),

					String.valueOf(p1),

					String.valueOf(p2 < 10 ? "0" + p2 : p2), String.valueOf(p3 < 10 ? "0" + p3 : p3),

					String.valueOf(p4 < 10 ? "0" + p4 : p4), String.valueOf(p5 < 10 ? "0" + p5 : p5),

					String.valueOf(p6 < 10 ? "0" + p6 : p6), String.valueOf(p7 < 10 ? "0" + p7 : p7),

					String.valueOf(p8 < 10 ? "0" + p8 : p8), String.valueOf(p9 < 10 ? "0" + p9 : p9),

					String.valueOf(p10 < 10 ? "0" + p10 : p10), String.valueOf(p11 < 10 ? "0" + p11 : p11),

					String.valueOf(p12 < 10 ? "0" + p12 : p12), String.valueOf(p13 < 10 ? "0" + p13 : p13), };

		} else {
			info = new String[] { String.valueOf(p1),

					String.valueOf(p2 < 10 ? "0" + p2 : p2), String.valueOf(p3 < 10 ? "0" + p3 : p3),

					String.valueOf(p4 < 10 ? "0" + p4 : p4), String.valueOf(p5 < 10 ? "0" + p5 : p5),

					String.valueOf(p6 < 10 ? "0" + p6 : p6), String.valueOf(p7 < 10 ? "0" + p7 : p7),

					String.valueOf(p8 < 10 ? "0" + p8 : p8), String.valueOf(p9 < 10 ? "0" + p9 : p9),

					String.valueOf(p10 < 10 ? "0" + p10 : p10), String.valueOf(p11 < 10 ? "0" + p11 : p11),

					String.valueOf(p12 < 10 ? "0" + p12 : p12), String.valueOf(p13 < 10 ? "0" + p13 : p13), };
		}
		return info;
	}

	/**
	 * 技能解除/裝備解除
	 * 
	 * @param pc
	 */
	public static void stopSkill(final L1PcInstance pc) {
		// 使用牛的代號脫除全部裝備
		pc.getInventory().takeoffEquip(945);

		// 技能解除
		for (int skillNum = L1SkillId.SKILLS_BEGIN; skillNum <= L1SkillId.SKILLS_END; skillNum++) {
			if (L1SkillMode.get().isNotCancelable(skillNum) && !pc.isDead()) {
				continue;
			}
			pc.removeSkillEffect(skillNum);
		}

		pc.curePoison();
		pc.cureParalaysis();
		for (int skillNum = L1SkillId.STATUS_BEGIN; skillNum <= L1SkillId.STATUS_END; skillNum++) {
			pc.removeSkillEffect(skillNum);
		}

		// 料理解除
		for (int skillNum = L1SkillId.COOKING_BEGIN; skillNum <= L1SkillId.COOKING_END; skillNum++) {
			if (L1SkillMode.get().isNotCancelable(skillNum)) {
				continue;
			}
			pc.removeSkillEffect(skillNum);
		}

		if (pc.getHasteItemEquipped() > 0) {
			pc.setMoveSpeed(0);
			pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
		}

		pc.sendPacketsAll(new S_CharVisualUpdate(pc));
	}
}
