package com.lineage.server.clientpackets;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;
import com.lineage.list.BadNamesList;
import com.lineage.server.IdFactory;
import com.lineage.server.command.executor.L1NewCharInfo;
import com.lineage.server.datatables.BeginnerTable;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.serverpackets.S_CharCreateStatus;
import com.lineage.server.serverpackets.S_NewCharPacket;
import com.lineage.server.serverpackets.S_PacketBoxGree;
//import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Account;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.CalcInitHpMp;
import com.lineage.server.utils.IntRange;
import com.lineage.server.world.World;

/**
 * 要求創造角色
 * 
 * @author simlin
 */
public class C_CreateChar extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CreateChar.class);

	/*// 各職業初始化屬性(王族, 騎士, 精靈, 法師, 黑妖, 龍騎士, 幻術師, 戰士)
	public static final int[] ORIGINAL_STR = new int[] { 13, 16, 11, 8, 12, 13, 11, 16 };
	public static final int[] ORIGINAL_DEX = new int[] { 10, 12, 12, 7, 15, 11, 10, 13 };
	public static final int[] ORIGINAL_CON = new int[] { 10, 14, 12, 12, 8, 14, 12, 16 };
	public static final int[] ORIGINAL_WIS = new int[] { 11, 9, 12, 12, 10, 12, 12, 7 };
	public static final int[] ORIGINAL_CHA = new int[] { 13, 12, 9, 8, 9, 8, 8, 9 };
	public static final int[] ORIGINAL_INT = new int[] { 10, 8, 12, 12, 11, 11, 12, 10 };*/
	// 各職業初始化可分配點數(王族, 騎士, 精靈, 法師, 黑妖, 龍騎士, 幻術師, 戰士)
	//public static final int[] ORIGINAL_AMOUNT = new int[] { 8, 4, 7, 16, 10, 6, 10, 4 };

	// 人物外型決定
	private static final int[][] CLASS_LIST = new int[][] {
			new int[] { 0, 61, 138, 734, 2786, 6658, 6671, 12490 }, // 男性
			new int[] { 1, 48, 37, 1186, 2796, 6661, 6650, 12494 } // 女性
	};

	// 出生地點座標
	private static final int[][] LOC_LIST = new int[][] { new int[] { 32684, 32870, 2005 },
			new int[] { 32686, 32867, 2005 }, new int[] { 32691, 32864, 2005 },
			new int[] { 32684, 32870, 2005 }, new int[] { 32686, 32867, 2005 },
			new int[] { 32691, 32864, 2005 }, new int[] { 32691, 32864, 2005 },
			new int[] { 32686, 32867, 2005 } };

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);
			final L1PcInstance pc = new L1PcInstance();
			String name = Matcher.quoteReplacement(readS());

			final L1Account account = client.getAccount();
			final int characterSlot = account.get_character_slot();
			final int maxAmount = ConfigAlt.DEFAULT_CHARACTER_SLOT + characterSlot;

			name = name.replaceAll("\\s", "");
			name = name.replaceAll("　", "");

			if (name.length() == 0) {
				client.out().encrypt(new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME));
				return;
			}

			// 名稱是否包含禁止字元
			if (!isInvalidName(name)) {
				client.out().encrypt(new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME));
				return;
			}

			// 檢查名稱是否以被使用
			if (CharObjidTable.get().charObjid(name) != 0) {
				client.out().encrypt(new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS));
				return;
			}

			// 已創人物數量
			final int countCharacters = client.getAccount().get_countCharacters();
			if (countCharacters >= maxAmount) {
				client.out().encrypt(new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT));
				return;
			}

			final int type = this.readC();

			final int sex = this.readC();
			
			final byte baseStr = (byte) this.readC();
			
			final byte baseDex = (byte) this.readC();
			
			final byte baseCon = (byte) this.readC();
			
			final byte baseWis = (byte) this.readC();
			
			final byte baseCha = (byte) this.readC();
			
			final byte baseInt = (byte) this.readC();
			
			pc.setName(name);
			pc.setType(type);
			pc.set_sex(sex);

			boolean isStatusError = false;
			final int originalStr = L1ClassFeature.ORIGINAL_STR[type];
			final int originalDex = L1ClassFeature.ORIGINAL_DEX[type];
			final int originalCon = L1ClassFeature.ORIGINAL_CON[type];
			final int originalWis = L1ClassFeature.ORIGINAL_WIS[type];
			final int originalCha = L1ClassFeature.ORIGINAL_CHA[type];
			final int originalInt = L1ClassFeature.ORIGINAL_INT[type];
			
			final int originalMaxStr = L1ClassFeature.ORIGINAL_MAXSTR[type];
			final int originalMaxDex = L1ClassFeature.ORIGINAL_MAXDEX[type];
			final int originalMaxCon = L1ClassFeature.ORIGINAL_MAXCON[type];
			final int originalMaxWis = L1ClassFeature.ORIGINAL_MAXWIS[type];
			final int originalMaxCha = L1ClassFeature.ORIGINAL_MAXCHA[type];
			final int originalMaxInt = L1ClassFeature.ORIGINAL_MAXINT[type];

			if (!IntRange.includes(baseStr, originalStr, originalMaxStr)
					|| !IntRange.includes(baseDex, originalDex, originalMaxDex)
					|| !IntRange.includes(baseCon, originalCon, originalMaxCon)
					|| !IntRange.includes(baseWis, originalWis, originalMaxWis)
					|| !IntRange.includes(baseCha, originalCha, originalMaxCha)
					|| !IntRange.includes(baseInt, originalInt, originalMaxInt)) {
				isStatusError = true;
			}
			
			if (isStatusError) {
				client.out().encrypt(new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT));
				return;
			}
			
			final int statusAmount = baseDex + baseCha + baseCon + baseInt + baseStr + baseWis;

			if (statusAmount != 75) {
				client.out().encrypt(new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT));
				return;
			}

			// 空身六能力
			pc.addBaseStr(baseStr);
			pc.addBaseDex(baseDex);
		    pc.addBaseCon(baseCon);
			pc.addBaseWis(baseWis);
			pc.addBaseCha(baseCha);
			pc.addBaseInt(baseInt);
			
			// 初始六能力
			pc.setOriginalStr(baseStr);
			pc.setOriginalDex(baseDex);
			pc.setOriginalCon(baseCon);
			pc.setOriginalWis(baseWis);
			pc.setOriginalCha(baseCha);
			pc.setOriginalInt(baseInt);
			
			client.getAccount().set_countCharacters(countCharacters + 1);
			client.out().encrypt(new S_CharCreateStatus(S_CharCreateStatus.REASON_OK));
			initNewChar(client, pc);

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/**
	 * 創造角色
	 * 
	 * @param client
	 * @param pc
	 */
	private static void initNewChar(final ClientExecutor client, final L1PcInstance pc) {
		try {
			final L1Account account = client.getAccount();
			pc.setId(IdFactory.get().nextId());
			final int classid = CLASS_LIST[pc.get_sex()][pc.getType()];

			pc.setClassId(classid);
			pc.setTempCharGfx(classid);
			pc.setGfxId(classid);

			int[] loc;

			// 角色出生座標 (格式: locx, locy, mapid) by terry0412
			if (ConfigAlt.NEW_CHAR_LOC != null) {
				loc = ConfigAlt.NEW_CHAR_LOC;

			} else {
				loc = LOC_LIST[pc.getType()];
			}

			pc.setX(loc[0]);
			pc.setY(loc[1]);
			pc.setMap((short) loc[2]);

			pc.setHeading(0);
			pc.setLawful(0);

			final int initHp = CalcInitHpMp.calcInitHp(pc);
			final int initMp = CalcInitHpMp.calcInitMp(pc);
			pc.addBaseMaxHp((short) initHp);
			pc.setCurrentHp((short) initHp);
			pc.addBaseMaxMp((short) initMp);
			pc.setCurrentMp((short) initMp);
			pc.resetBaseAc();
			pc.setTitle("");
			pc.setClanid(0);
			pc.setClanRank(0);
			pc.set_food(40);

			if (account.get_access_level() >= 200) {
				pc.setAccessLevel((short) account.get_access_level());
				pc.setGm(true);
				pc.setMonitor(false);

			} else {
				pc.setAccessLevel((short) 0);
				pc.setGm(false);
				pc.setMonitor(false);
			}

			pc.setGmInvis(false);
			pc.setExp(0);
			pc.setHighLevel(0);
			pc.setStatus(0);
			pc.setClanname("");
			pc.setBonusStats(0);
			pc.setElixirStats(0);
			pc.resetBaseMr();
			pc.setElfAttr(0);
			pc.set_PKcount(0);
			pc.setPkCountForElf(0);
			pc.setExpRes(0);
			pc.setPartnerId(0);
			pc.setOnlineStatus(0);
			pc.setHomeTownId(0);
			pc.setContribution(0);
			pc.setBanned(false);
			pc.setKarma(0);
			pc.setClanMemberNotes("備註");
			if (pc.isWizard()) {// 法師技能
				final int object_id = pc.getId();
				final L1Skills l1skills = SkillsTable.get().getTemplate(4); // EB
				final String skill_name = l1skills.getName();
				final int skill_id = l1skills.getSkillId();

				CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // 資料庫紀錄
			}

			// 紀錄人物帳號
			pc.setAccountName(client.getAccountName());
			// 初始化數值
			pc.refresh();

			client.out().encrypt(new S_NewCharPacket(pc));

			// 建立人物資料
			CharacterTable.get().storeNewCharacter(pc);
			// 紀錄人物初始化資料
			CharacterTable.saveCharStatus(pc);
			// 給予新手道具
			BeginnerTable.get().giveItem(pc);
			// 加入建立PC OBJID資料
			CharObjidTable.get().addChar(pc.getId(), pc.getName());

			// 創人物公告 by terry0412
			if (L1NewCharInfo.new_char_info && (ConfigAlt.CreateCharInfo != null)) {
				// 發送公告
				World.get().broadcastPacketToAll(
						new S_PacketBoxGree(0x02,String.format(ConfigAlt.CreateCharInfo, pc.getName())));
						//new S_SystemMessage(String.format(ConfigAlt.CreateCharInfo, pc.getName())));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static final String[] BANLIST = new String[] { "あ", "ア", "い", "イ", "う", "ウ", "え", "エ", "お", "オ",
			"か", "カ", "き", "キ", "く", "ク", "け", "ケ", "こ", "コ", "さ", "サ", "し", "シ", "す", "ス", "せ", "セ", "そ",
			"ソ", "た", "タ", "ち", "チ", "つ", "ツ", "て", "テ", "と", "ト", "な", "ナ", "に", "ニ", "ぬ", "ヌ", "ね", "ネ",
			"の", "ノ", "は", "ハ", "ひ", "ヒ", "ふ", "フ", "へ", "ヘ", "ほ", "ホ", "ま", "マ", "み", "ミ", "む", "ム", "め",
			"メ", "も", "モ", "や", "ヤ", "ゆ", "ユ", "よ", "ヨ", "ら", "ラ", "り", "リ", "る", "ル", "れ", "レ", "ろ", "ロ",
			"わ", "ワ", "を", "ヲ", "ん", "ン", "丶", "", "\ue6c1", "-", "/", "+", "*", "?", "!", "@", "#", "$",
			"%", "^", "&", "(", ")", "[", "]", "<", ">", "{", "}", ";", ":", "'", "\"", ",", ".", "~", "`", };

	public static boolean isInvalidName(final String name) {
		// int numOfNameBytes = 0;

		try {
			for (final String ban : BANLIST) {
				if (name.indexOf(ban) != -1) {
					return false;
				}
			}

			if (BadNamesList.get().isBadName(name)) {
				return false;
			}

			// 將字串轉為BYTE組 並取回BYTE長度
			final int numOfNameBytes = name.getBytes(Config.CLIENT_LANGUAGE_CODE).length;
			// 全形字服 5字 半形12字
			if ((5 < (numOfNameBytes - name.length())) || (12 < numOfNameBytes)) {
				return false;
			}
			return true;

		} catch (final UnsupportedEncodingException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			return false;
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
