package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBoxCharEr;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ability.S_BaseAbility;
import com.lineage.server.serverpackets.ability.S_ConDetails;
import com.lineage.server.serverpackets.ability.S_DexDetails;
import com.lineage.server.serverpackets.ability.S_IntDetails;
import com.lineage.server.serverpackets.ability.S_StrDetails;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.serverpackets.ability.S_WisDetails;
import com.lineage.server.world.World;

/**
 * 重置指定人物屬性(參數:對象/屬性(參考說明)/變更質)
 * 
 * @author dexc
 */
public class L1Status implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Status.class);

	private static final int _max_int = 20000;

	private L1Status() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Status();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			if (pc == null) {
				_log.warn("系統命令執行: " + cmdName + " " + arg + " 重置指定人物屬性。");
			}
			final StringTokenizer st = new StringTokenizer(arg);
			final String char_name = st.nextToken();
			final String param = st.nextToken();
			int value = Integer.parseInt(st.nextToken());

			final String e1 = "指令異常: 指定人物不在線上，這個命令必須輸入正確人物名稱才能執行。";
			L1PcInstance target = null;

			if (char_name.equalsIgnoreCase("me")) {
				if (pc == null) {
					_log.error(e1);
					return;
				}
				target = pc;

			} else {
				target = World.get().getPlayer(char_name);
			}

			if (target == null) {
				if (pc == null) {
					_log.error(e1);
					return;
				}
				// 73:\f1%0%d 不在線上。
				pc.sendPackets(new S_ServerMessage(73, char_name));
				return;
			}

			// -- not use DB --
			if (param.equalsIgnoreCase("AC")) {
				target.addAc((byte) (value - target.getAc()));

			} else if (param.equalsIgnoreCase("MR")) {
				target.addMr((short) (value - target.getMr()));

			} else if (param.equalsIgnoreCase("HIT")) {
				target.addHitup((short) (value - target.getHitup()));

			} else if (param.equalsIgnoreCase("DMG")) {
				target.addDmgup((short) (value - target.getDmgup()));
				// -- use DB --

			} else {
				if (param.equalsIgnoreCase("HP")) {
					if (value > _max_int) {
						value = _max_int;
					}
					int maxHP = value - target.getBaseMaxHp();
					if ((target.getBaseMaxHp() + maxHP) > _max_int) {
						maxHP = _max_int - target.getBaseMaxHp();
					}
					target.addBaseMaxHp((short) maxHP);
					target.setCurrentHpDirect(target.getMaxHp());

				} else if (param.equalsIgnoreCase("MP")) {
					if (value > _max_int) {
						value = _max_int;
					}
					int maxMP = value - target.getBaseMaxMp();
					if ((target.getBaseMaxMp() + maxMP) > _max_int) {
						maxMP = _max_int - target.getBaseMaxMp();
					}
					// int maxMP = RangeInt.ensure((short) (value -
					// target.getBaseMaxMp()), 1, 30000);
					target.addBaseMaxMp((short) maxMP);
					target.setCurrentMpDirect(target.getMaxMp());

				} else if (param.equalsIgnoreCase("LAWFUL") || param.equalsIgnoreCase("L")) {
					target.addLawful(value);

				} else if (param.equalsIgnoreCase("KARMA") || param.equalsIgnoreCase("K")) {
					target.addKarma(value);

				} else if (param.equalsIgnoreCase("GM")) {
					if (value > 200) {
						value = 200;
					}
					target.setAccessLevel((short) value);
					target.sendPackets(new S_SystemMessage("取得GM權限"));// 4:取得GM權限

				} else if (param.equalsIgnoreCase("STR")) {
					target.addBaseStr((byte) (value - target.getBaseStr()));
					// XXX 能力基本資訊-力量
					target.sendPackets(new S_StrDetails(2,
							L1ClassFeature.calcStrDmg(target.getStr(), target.getBaseStr()),
							L1ClassFeature.calcStrHit(target.getStr(), target.getBaseStr()),
							L1ClassFeature.calcStrDmgCritical(target.getStr(), target.getBaseStr()),
							L1ClassFeature.calcAbilityMaxWeight(target.getStr(), target.getCon())
							));
					
					// XXX 重量程度資訊
					target.sendPackets(new S_WeightStatus(target.getInventory().getWeight100(), target.getInventory().getWeight(), (int)target.getMaxWeight()));
					// XXX 純能力資訊
					target.sendPackets(new S_BaseAbility(target.getBaseStr(), target.getBaseInt(), target.getBaseWis(), target.getBaseDex(), target.getBaseCon(), target.getBaseCha()));
				} else if (param.equalsIgnoreCase("CON")) {
					target.addBaseCon((byte) (value - target.getBaseCon()));
					// XXX 能力基本資訊-體質
					target.sendPackets(new S_ConDetails(2,
							L1ClassFeature.calcConHpr(target.getCon(), target.getBaseCon()),
							L1ClassFeature.calcConPotionHpr(target.getCon(), target.getBaseCon()),
							L1ClassFeature.calcAbilityMaxWeight(target.getStr(), target.getCon()),
							L1ClassFeature.calcBaseClassLevUpHpUp(target.getType()) + L1ClassFeature.calcBaseConLevUpExtraHpUp(target.getType(), target.getBaseCon())
							));
					// XXX 重量程度資訊
					target.sendPackets(new S_WeightStatus(target.getInventory().getWeight100(), target.getInventory().getWeight(), (int)target.getMaxWeight()));
					// XXX 純能力資訊
					target.sendPackets(new S_BaseAbility(target.getBaseStr(), target.getBaseInt(), target.getBaseWis(), target.getBaseDex(), target.getBaseCon(), target.getBaseCha()));
				} else if (param.equalsIgnoreCase("DEX")) {
					target.addBaseDex((byte) (value - target.getBaseDex()));
					target.sendPackets(new S_PacketBoxCharEr(target));// XXX 角色迴避率更新
					target.resetBaseAc();
					// XXX 能力基本資訊-敏捷
					target.sendPackets(new S_DexDetails(2,
							L1ClassFeature.calcDexDmg(target.getDex(), target.getBaseDex()),
							L1ClassFeature.calcDexHit(target.getDex(), target.getBaseDex()),
							L1ClassFeature.calcDexDmgCritical(target.getDex(), target.getBaseDex()),
							L1ClassFeature.calcDexAc(target.getDex()),
							L1ClassFeature.calcDexEr(target.getDex())
							));
					// XXX 純能力資訊
					target.sendPackets(new S_BaseAbility(target.getBaseStr(), target.getBaseInt(), target.getBaseWis(), target.getBaseDex(), target.getBaseCon(), target.getBaseCha()));
				} else if (param.equalsIgnoreCase("INT")) {
					target.addBaseInt((byte) (value - target.getBaseInt()));
					// XXX 能力基本資訊-智力
					target.sendPackets(new S_IntDetails(2,
							L1ClassFeature.calcIntMagicDmg(target.getInt(), target.getBaseInt()),
							L1ClassFeature.calcIntMagicHit(target.getInt(), target.getBaseInt()),
							L1ClassFeature.calcIntMagicCritical(target.getInt(), target.getBaseInt()),
							L1ClassFeature.calcIntMagicBonus(target.getType(), target.getInt()),
							L1ClassFeature.calcIntMagicConsumeReduction(target.getInt())
							));
					// XXX 純能力資訊
					target.sendPackets(new S_BaseAbility(target.getBaseStr(), target.getBaseInt(), target.getBaseWis(), target.getBaseDex(), target.getBaseCon(), target.getBaseCha()));
				} else if (param.equalsIgnoreCase("WIS")) {
					target.addBaseWis((byte) (value - target.getBaseWis()));
					target.resetBaseMr();
					// XXX 能力基本資訊-精神
					target.sendPackets(new S_WisDetails(2,
							L1ClassFeature.calcWisMpr(target.getWis(), target.getBaseWis()),
							L1ClassFeature.calcWisPotionMpr(target.getWis(), target.getBaseWis()),
							L1ClassFeature.calcStatMr(target.getWis()) + L1ClassFeature.newClassFeature(target.getType()).getClassOriginalMr(),
							L1ClassFeature.calcBaseWisLevUpMpUp(target.getType(), target.getBaseWis())
							));
					// XXX 純能力資訊
					target.sendPackets(new S_BaseAbility(target.getBaseStr(), target.getBaseInt(), target.getBaseWis(), target.getBaseDex(), target.getBaseCon(), target.getBaseCha()));
				} else if (param.equalsIgnoreCase("CHA")) {
					target.addBaseCha((byte) (value - target.getBaseCha()));
					// XXX 純能力資訊
					target.sendPackets(new S_BaseAbility(target.getBaseStr(), target.getBaseInt(), target.getBaseWis(), target.getBaseDex(), target.getBaseCon(), target.getBaseCha()));
				} else if (param.equalsIgnoreCase("C1")) { // 變更陣營積分 by
															// terry0412
					target.addBaseCha((byte) (value - target.getBaseCha()));

					// 增加值
					final int value2 = Integer.parseInt(st.nextToken());

					// 取得陣營資料
					final int score = target.get_other().get_score() + value2;
					if (score > 0) {
						target.get_other().set_score(score);
					} else {
						target.get_other().set_score(0);
					}

					// 是否階級有變動
					final int lv = C1_Name_Type_Table.get().getLv(target.get_c_power().get_c1_type(),
							target.get_other().get_score());
					if (lv != target.get_c_power().get_power().get_c1_id()) {
						target.get_c_power().set_power(target, false);
						target.sendPackets(new S_ServerMessage(
								"\\fR階級變更:" + target.get_c_power().get_power().get_c1_name_type()));
						target.sendPacketsAll(new S_ChangeName(target, true));
					}

				} else {
					// final String e2 = "指令異常: 指令 " + param + " 不明確";
					// 5:指令異常: 指令 %s 不明確
					final String e2 = "指令異常: 指令 " + param + " 不明確";
					if (pc == null) {
						_log.error(e2);
						return;
					}
					pc.sendPackets(new S_SystemMessage(e2));
					return;
				}
				target.save(); // 資料存檔
			}
			target.sendPackets(new S_OwnCharStatus(target));

			// final String ok = target.getName() + " 的" + param + " 屬性 " +
			// value + " 變更完成。";
			// 6:%s 的 %s 屬性%i 變更完成。
			final String ok = target.getName() + "的" + param + "屬性" + value + "變更完成";
			if (pc == null) {
				_log.info(ok);
				return;
			}
			pc.sendPackets(new S_SystemMessage(ok));

		} catch (final Exception e) {
			if (pc == null) {
				_log.error("錯誤的命令格式: " + this.getClass().getSimpleName());

			} else {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
			}
		}
	}
}
