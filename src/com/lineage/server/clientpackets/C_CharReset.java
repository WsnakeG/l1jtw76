package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.data.event.BaseResetSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharReset;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.utils.CalcInitHpMp;
import com.lineage.server.utils.CalcStat;

/**
 * 要求人物重設
 * 
 * @author terry0412
 */
public class C_CharReset extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CharReset.class);

	/*
	 * public C_CharReset() { } public C_CharReset(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	private static long pre_time;

	// 新創角色能力最小值
	public final static int[] ORIGINAL_STR = new int[] { 13, 16, 11, 8, 12, 13, 11 };
	public final static int[] ORIGINAL_DEX = new int[] { 10, 12, 12, 7, 15, 11, 10 };
	public final static int[] ORIGINAL_CON = new int[] { 10, 14, 12, 12, 8, 14, 12 };
	public final static int[] ORIGINAL_WIS = new int[] { 11, 9, 12, 12, 10, 12, 12 };
	public final static int[] ORIGINAL_CHA = new int[] { 13, 12, 9, 8, 9, 8, 8 };
	public final static int[] ORIGINAL_INT = new int[] { 10, 8, 12, 12, 11, 11, 12 };
	// 新創角色點數分配配
	public final static int[] ORIGINAL_AMOUNT = new int[] { 8, 4, 7, 16, 10, 6, 10 };
	
	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			if ((pc == null) || !pc.isInCharReset()) {
				return;
			}

			final long now_time = System.currentTimeMillis();
			if (now_time < (pre_time + 400L)) {
				return;
			}
			pre_time = now_time;

			final int stage = readC();

			switch (stage) {
			case 0x01: // 0x01:初始化人物數質
			{
				final int str = readC();
				final int intel = readC();
				final int wis = readC();
				final int dex = readC();
				final int con = readC();
				final int cha = readC();
				// 檢查是否有異常能力值
				boolean isStatusError = false;
				
				final int originalStr = ORIGINAL_STR[pc.getType()];
				final int originalDex = ORIGINAL_DEX[pc.getType()];
				final int originalCon = ORIGINAL_CON[pc.getType()];
				final int originalWis = ORIGINAL_WIS[pc.getType()];
				final int originalCha = ORIGINAL_CHA[pc.getType()];
				final int originalInt = ORIGINAL_INT[pc.getType()];
				final int originalAmount = ORIGINAL_AMOUNT[pc.getType()];

				if ((str < originalStr) || (dex < originalDex) || (con < originalCon) || (wis < originalWis)
						|| (cha < originalCha) || (intel < originalInt)
						|| (str > (originalStr + originalAmount)) || (dex > (originalDex + originalAmount))
						|| (con > (originalCon + originalAmount)) || (wis > (originalWis + originalAmount))
						|| (cha > (originalCha + originalAmount))
						|| (intel > (originalInt + originalAmount))) {
					isStatusError = true;
				}
				final int statusAmount = str + intel + wis + dex + con + cha;
				if ((statusAmount != 75) || (isStatusError)) {
					return;
				}

				int hp = 0;
				int mp = 0;
				if (BaseResetSet.RETAIN != 0) {
					hp = ((pc.getMaxHp() * BaseResetSet.RETAIN) / 100);
					mp = ((pc.getMaxMp() * BaseResetSet.RETAIN) / 100);

				} else {
					hp = CalcInitHpMp.calcInitHp(pc);
					mp = CalcInitHpMp.calcInitMp(pc);
				}

				pc.sendPackets(new S_CharReset(pc, 1, hp, mp, 10, str, intel, wis, dex, con, cha));
				initCharStatus(pc, hp, mp, str, intel, wis, dex, con, cha);
				// CharacterTable.saveCharStatus(pc);
				break;
			}

			case 0x02: // 0x02:等級分配
			{
				final int type2 = readC();
				switch (type2) {
				case 0x00: // 提升1級
					if (pc.getTempLevel() >= pc.getTempMaxLevel()) {
						return;
					}
					setLevelUp(pc, 1);
					break;

				case 0x07: // 提升10級
					if (((pc.getTempMaxLevel() - pc.getTempLevel()) < 10) || (pc.getTempLevel() > 49)) {
						return;
					}
					if ((50 - pc.getTempLevel()) > 10) {
						setLevelUp(pc, 10);
					} else {
						setLevelUp(pc, 50 - pc.getTempLevel());
					}
					break;

				case 0x01:// 提升1級(力量)
				case 0x02:// 提升1級(智力)
				case 0x03:// 提升1級(精神)
				case 0x04:// 提升1級(敏捷)
				case 0x05:// 提升1級(體質)
				case 0x06:// 提升1級(魅力)
					if (!jdMethod_try(pc, type2) || (pc.getTempLevel() >= pc.getTempMaxLevel())) {
						return;
					}
					setLevelUp(pc, 1);
					break;

				case 0x08:// 完成
					if (!jdMethod_try(pc, readC())) {
						return;
					}
					if (pc.getElixirStats() > 0) {
						pc.sendPackets(new S_CharReset(pc.getElixirStats()));
						return;
					}
					saveNewCharStatus(pc);
					pc.setInCharReset(false);
					break;
				}
				break;
			}

			case 0x03: {
				final int str = readC();
				final int intel = readC();
				final int wis = readC();
				final int dex = readC();
				final int con = readC();
				final int cha = readC();
				final int statusAmount = str + intel + wis + dex + con + cha;
				if (!jdMethod_for(str) || !jdMethod_for(con) || !jdMethod_for(dex) || !jdMethod_for(wis)
						|| !jdMethod_for(intel) || !jdMethod_for(cha)) {
					return;
				}
				if ((75 + pc.getElixirStats() + pc.getBonusStats()) != statusAmount) {
					return;
				}
				pc.addBaseStr((byte) (str - pc.getBaseStr()));
				pc.addBaseInt((byte) (intel - pc.getBaseInt()));
				pc.addBaseWis((byte) (wis - pc.getBaseWis()));
				pc.addBaseDex((byte) (dex - pc.getBaseDex()));
				pc.addBaseCon((byte) (con - pc.getBaseCon()));
				pc.addBaseCha((byte) (cha - pc.getBaseCha()));
				saveNewCharStatus(pc);
				pc.setInCharReset(false);
				break;
			}

			default:
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	private boolean jdMethod_try(final L1PcInstance pc, final int d) {
		int attr = 1;
		switch (d) {
		case 1:
			attr += pc.getBaseStr();
			break;
		case 2:
			attr += pc.getBaseInt();
			break;
		case 3:
			attr += pc.getBaseWis();
			break;
		case 4:
			attr += pc.getBaseDex();
			break;
		case 5:
			attr += pc.getBaseCon();
			break;
		case 6:
			attr += pc.getBaseCha();
		}
		final int maxAttr = ConfigAlt.POWER < 35 ? 35 : ConfigAlt.POWER;
		if (attr > maxAttr) {
			return false;
		}

		switch (d) {
		case 1:
			pc.addBaseStr(1);
			break;
		case 2:
			pc.addBaseInt(1);
			break;
		case 3:
			pc.addBaseWis(1);
			break;
		case 4:
			pc.addBaseDex(1);
			break;
		case 5:
			pc.addBaseCon(1);
			break;
		case 6:
			pc.addBaseCha(1);
		}
		return true;
	}

	private boolean jdMethod_for(final int attr) {
		return attr <= Math.max(ConfigAlt.POWERMEDICINE, ConfigAlt.POWER);
	}

	private void saveNewCharStatus(final L1PcInstance pc) {
		if (pc.getOriginalAc() > 0) {
			pc.addAc(pc.getOriginalAc());
		}
		if (pc.getOriginalMr() > 0) {
			pc.addMr(0 - pc.getOriginalMr());
		}

		pc.refresh();
		pc.setCurrentHp(pc.getMaxHp());
		pc.setCurrentMp(pc.getMaxMp());

		/*
		 * if (pc.getTempMaxLevel() != pc.getLevel()) {
		 * pc.setLevel(pc.getTempMaxLevel());
		 * pc.setExp(ExpTable.getExpByLevel(pc.getTempMaxLevel())); }
		 */
		pc.setLevel(pc.getTempLevel());
		pc.setExp(ExpTable.getExpByLevel(pc.getTempLevel()));

		if (pc.getLevel() > 50) {
			pc.setBonusStats(pc.getLevel() - 50);
		} else {
			pc.setBonusStats(0);
		}
		pc.sendPackets(new S_OwnCharStatus(pc));

		final L1ItemInstance item = pc.getInventory().findItemId(49142); // 回憶蠟燭
		if (item != null) {
			try {
				pc.getInventory().removeItem(item, 1);
				pc.save(); // 資料存檔

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
			CharacterTable.saveCharStatus(pc); // 會引發出生點數異常 bug
		}
		L1Teleport.teleport(pc, 32628, 32772, (short) 4, 4, false);
	}

	private void initCharStatus(final L1PcInstance pc, final int hp, final int mp, final int str,
			final int intel, final int wis, final int dex, final int con, final int cha) {
		pc.addBaseMaxHp((short) (hp - pc.getBaseMaxHp()));
		pc.addBaseMaxMp((short) (mp - pc.getBaseMaxMp()));
		pc.addBaseStr((byte) (str - pc.getBaseStr()));
		pc.addBaseInt((byte) (intel - pc.getBaseInt()));
		pc.addBaseWis((byte) (wis - pc.getBaseWis()));
		pc.addBaseDex((byte) (dex - pc.getBaseDex()));
		pc.addBaseCon((byte) (con - pc.getBaseCon()));
		pc.addBaseCha((byte) (cha - pc.getBaseCha()));
		pc.addMr(-pc.getMr());
		pc.addDmgup(-pc.getDmgup());
		pc.addHitup(-pc.getHitup());

		// 更新人物素質 fixed by terry0412
		pc.setOriginalStr(str);
		pc.setOriginalCon(con);
		pc.setOriginalDex(dex);
		pc.setOriginalCha(cha);
		pc.setOriginalInt(intel);
		pc.setOriginalWis(wis);

		// 全屬性重置
		pc.refresh();
	}

	private boolean setLevelUp(final L1PcInstance pc, final int addLv) {
		if ((pc.getTempLevel() + addLv) > pc.getTempMaxLevel()) {
			return false;
		}
		pc.setTempLevel(pc.getTempLevel() + addLv);
		for (int i = 0; i < addLv; i++) {
			final short randomHp = CalcStat.calcStatHp(pc.getType(), pc.getBaseMaxHp(), pc.getBaseCon(),
					pc.getOriginalHpup());
			final short randomMp = CalcStat.calcStatMp(pc.getType(), pc.getBaseMaxMp(), pc.getBaseWis(),
					pc.getOriginalMpup());
			pc.addBaseMaxHp(randomHp);
			pc.addBaseMaxMp(randomMp);
		}
		final int newAc = CalcStat.calcAc(pc.getTempLevel(), pc.getBaseDex());
		pc.sendPackets(new S_CharReset(pc, pc.getTempLevel(), pc.getBaseMaxHp(), pc.getBaseMaxMp(), newAc,
				pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(),
				pc.getBaseCha()));
		return true;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
