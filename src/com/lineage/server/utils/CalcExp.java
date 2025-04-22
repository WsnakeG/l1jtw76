package com.lineage.server.utils;

import static com.lineage.server.model.skill.L1SkillId.COOKING_1_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_7_S;
import static com.lineage.server.model.skill.L1SkillId.EXP13;
import static com.lineage.server.model.skill.L1SkillId.EXP15;
import static com.lineage.server.model.skill.L1SkillId.EXP17;
import static com.lineage.server.model.skill.L1SkillId.EXP20;
import static com.lineage.server.model.skill.L1SkillId.EXP25;
import static com.lineage.server.model.skill.L1SkillId.EXP30;
import static com.lineage.server.model.skill.L1SkillId.EXP35;
import static com.lineage.server.model.skill.L1SkillId.EXP40;
import static com.lineage.server.model.skill.L1SkillId.EXP45;
import static com.lineage.server.model.skill.L1SkillId.EXP50;
import static com.lineage.server.model.skill.L1SkillId.EXP55;
import static com.lineage.server.model.skill.L1SkillId.EXP60;
import static com.lineage.server.model.skill.L1SkillId.EXP65;
import static com.lineage.server.model.skill.L1SkillId.EXP70;
import static com.lineage.server.model.skill.L1SkillId.EXP75;
import static com.lineage.server.model.skill.L1SkillId.EXP80;
import static com.lineage.server.model.skill.L1SkillId.MAZU_STATUS;
import static com.lineage.server.model.skill.L1SkillId.SEXP11;
import static com.lineage.server.model.skill.L1SkillId.SEXP13;
import static com.lineage.server.model.skill.L1SkillId.SEXP15;
import static com.lineage.server.model.skill.L1SkillId.SEXP17;
import static com.lineage.server.model.skill.L1SkillId.SEXP20;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.data.event.LeavesSet;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.MapExpTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_NPCPack_Pet;
import com.lineage.server.serverpackets.S_PacketBoxExp;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.world.World;

/**
 * 經驗值取得計算
 * 
 * @author dexc
 */
public class CalcExp {

	private static final Log _log = LogFactory.getLog(CalcExp.class);

	private CalcExp() {
	}

	public static void calcExp(final L1PcInstance srcpc, final int targetid,
			final ArrayList<?> acquisitorList, final ArrayList<Integer> hateList, final long exp) {
		try {
			int i = 0;
			double party_level = 0;
			double dist = 0;
			long member_exp = 0;
			int member_lawful = 0;
			final L1Object object = World.get().findObject(targetid);
			L1NpcInstance npc = null;

			if (object instanceof L1NpcInstance) {
				npc = (L1NpcInstance) object;

			} else {
				// object 不是 L1NpcInstance
				return;
			}

			// ヘイトの合計を取得
			L1Character acquisitor;
			int hate = 0;
			long acquire_exp = 0;
			int acquire_lawful = 0;
			long party_exp = 0;
			int party_lawful = 0;
			long totalHateExp = 0;
			int totalHateLawful = 0;
			long partyHateExp = 0;
			int partyHateLawful = 0;
			long ownHateExp = 0;

			if (acquisitorList.size() != hateList.size()) {
				return;
			}

			for (i = hateList.size() - 1; i >= 0; i--) {
				acquisitor = (L1Character) acquisitorList.get(i);
				hate = hateList.get(i);

				boolean isRemove = false;// 取消經驗質獎勵
				// 攻擊者是 分身
				if (acquisitor instanceof L1IllusoryInstance) {
					isRemove = true;
				}
				// 攻擊者是 技能物件
				if (acquisitor instanceof L1EffectInstance) {
					isRemove = true;
				}
				// 取消經驗質獎勵(該物件不分取經驗質)
				if (isRemove) {
					if (acquisitor != null) {
						acquisitorList.remove(i);
						hateList.remove(i);
					}
					continue;
				}

				if ((acquisitor != null) && !acquisitor.isDead()) {
					totalHateExp += hate;
					if (acquisitor instanceof L1PcInstance) {
						totalHateLawful += hate;
					}

				} else { // nullだったり死んでいたら排除
					acquisitorList.remove(i);
					hateList.remove(i);
				}
			}

			if (totalHateExp == 0) { // 取得者がいない場合
				return;
			}

			if ((object != null) && !(npc instanceof L1PetInstance) && !(npc instanceof L1SummonInstance)) {
				// int exp = npc.get_exp();
				if (!World.get().isProcessingContributionTotal() && (srcpc.getHomeTownId() > 0)) {
					final int contribution = npc.getLevel() / 10;
					srcpc.addContribution(contribution);
				}
				final int lawful = npc.getLawful();

				if (srcpc.isInParty()) { // 隊伍中
					// 隊伍經驗分配
					partyHateExp = 0;
					partyHateLawful = 0;
					for (i = hateList.size() - 1; i >= 0; i--) {
						acquisitor = (L1Character) acquisitorList.get(i);
						hate = hateList.get(i);
						if (acquisitor instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) acquisitor;
							if (pc == srcpc) {
								partyHateExp += hate;
								partyHateLawful += hate;

							} else if (srcpc.getParty().isMember(pc)) {
								partyHateExp += hate;
								partyHateLawful += hate;

							} else {
								if (totalHateExp > 0) {
									acquire_exp = ((exp * hate) / totalHateExp);
								}
								if (totalHateLawful > 0) {
									acquire_lawful = ((lawful * hate) / totalHateLawful);
								}
								addExp(pc, acquire_exp, acquire_lawful);
							}

						} else if (acquisitor instanceof L1PetInstance) {
							final L1PetInstance pet = (L1PetInstance) acquisitor;
							final L1PcInstance master = (L1PcInstance) pet.getMaster();
							if (master == srcpc) {
								partyHateExp += hate;

							} else if (srcpc.getParty().isMember(master)) {
								partyHateExp += hate;

							} else {
								if (totalHateExp > 0) {
									acquire_exp = ((exp * hate) / totalHateExp);
								}
								addExpPet(pet, acquire_exp);
							}

						} else if (acquisitor instanceof L1SummonInstance) {
							final L1SummonInstance summon = (L1SummonInstance) acquisitor;
							final L1PcInstance master = (L1PcInstance) summon.getMaster();
							if (master == srcpc) {
								partyHateExp += hate;

							} else if (srcpc.getParty().isMember(master)) {
								partyHateExp += hate;

							} else {
							}
						}
					}

					if (totalHateExp > 0) {
						party_exp = ((exp * partyHateExp) / totalHateExp);
					}

					if (totalHateLawful > 0) {
						party_lawful = ((lawful * partyHateLawful) / totalHateLawful);
					}

					// EXP、ロウフル配分

					// プリボーナス
					double pri_bonus = 0.0D;
					final L1PcInstance leader = srcpc.getParty().getLeader();
					if (leader.isCrown() && (srcpc.knownsObject(leader) || srcpc.equals(leader))) {
						pri_bonus = 0.059;
					}

					final Object[] pcs = srcpc.getParty().getMemberList().toArray();

					double pt_bonus = 0.0D;
					for (final Object obj : pcs) {
						if (obj instanceof L1PcInstance) {
							final L1PcInstance each = (L1PcInstance) obj;
							if (each.isDead()) {
								continue;
							}
							if (srcpc.knownsObject(each) || srcpc.equals(each)) {
								party_level += each.getLevel() * each.getLevel();
							}
							if (srcpc.knownsObject(each)) {
								pt_bonus += (ConfigAlt.PARTY_EXP_BONUS * 0.01);
							}
						}
					}
					party_exp = (long) (party_exp * (1 + pt_bonus + pri_bonus));

					// 自キャラクターとそのペット・サモンのヘイトの合計を算出
					if (party_level > 0) {
						dist = ((srcpc.getLevel() * srcpc.getLevel()) / party_level);
					}
					member_exp = (long) (party_exp * dist);
					member_lawful = (int) (party_lawful * dist);

					ownHateExp = 0;
					for (i = hateList.size() - 1; i >= 0; i--) {
						acquisitor = (L1Character) acquisitorList.get(i);
						hate = hateList.get(i);
						if (acquisitor instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) acquisitor;
							if (pc == srcpc) {
								ownHateExp += hate;
							}

						} else if (acquisitor instanceof L1PetInstance) {
							final L1PetInstance pet = (L1PetInstance) acquisitor;
							final L1PcInstance master = (L1PcInstance) pet.getMaster();
							if (master == srcpc) {
								ownHateExp += hate;
							}

						} else if (acquisitor instanceof L1SummonInstance) {
							final L1SummonInstance summon = (L1SummonInstance) acquisitor;
							final L1PcInstance master = (L1PcInstance) summon.getMaster();
							if (master == srcpc) {
								ownHateExp += hate;
							}
						}
					}
					// 自キャラクターとそのペット・サモンに分配
					if (ownHateExp != 0) { // 攻撃に参加していた
						for (i = hateList.size() - 1; i >= 0; i--) {
							acquisitor = (L1Character) acquisitorList.get(i);
							hate = hateList.get(i);
							if (acquisitor instanceof L1PcInstance) {
								final L1PcInstance pc = (L1PcInstance) acquisitor;
								if (pc == srcpc) {
									if (ownHateExp > 0) {
										acquire_exp = ((member_exp * hate) / ownHateExp);
									}
									addExp(pc, acquire_exp, member_lawful);
								}

							} else if (acquisitor instanceof L1PetInstance) {
								final L1PetInstance pet = (L1PetInstance) acquisitor;
								final L1PcInstance master = (L1PcInstance) pet.getMaster();
								if (master == srcpc) {
									if (ownHateExp > 0) {
										acquire_exp = ((member_exp * hate) / ownHateExp);
									}
									addExpPet(pet, acquire_exp);
								}

							} else if (acquisitor instanceof L1SummonInstance) {
							}
						}

					} else { // 攻撃に参加していなかった
						// 自キャラクターのみに分配
						addExp(srcpc, member_exp, member_lawful);
					}

					// パーティーメンバーとそのペット・サモンのヘイトの合計を算出

					for (final Object obj : pcs) {
						if (obj instanceof L1PcInstance) {
							final L1PcInstance tgpc = (L1PcInstance) obj;
							if (tgpc.isDead()) {
								continue;
							}
							if (srcpc.knownsObject(tgpc)) {
								if (party_level > 0) {
									dist = ((tgpc.getLevel() * tgpc.getLevel()) / party_level);
								}
								member_exp = (int) (party_exp * dist);
								member_lawful = (int) (party_lawful * dist);

								ownHateExp = 0;
								for (i = hateList.size() - 1; i >= 0; i--) {
									acquisitor = (L1Character) acquisitorList.get(i);
									hate = hateList.get(i);
									if (acquisitor instanceof L1PcInstance) {
										final L1PcInstance pc = (L1PcInstance) acquisitor;
										if (pc == tgpc) {
											ownHateExp += hate;
										}

									} else if (acquisitor instanceof L1PetInstance) {
										final L1PetInstance pet = (L1PetInstance) acquisitor;
										final L1PcInstance master = (L1PcInstance) pet.getMaster();
										if (master == tgpc) {
											ownHateExp += hate;
										}

									} else if (acquisitor instanceof L1SummonInstance) {
										final L1SummonInstance summon = (L1SummonInstance) acquisitor;
										final L1PcInstance master = (L1PcInstance) summon.getMaster();
										if (master == tgpc) {
											ownHateExp += hate;
										}
									}
								}
								// パーティーメンバーとそのペット・サモンに分配
								if (ownHateExp != 0) { // 攻撃に参加していた
									for (i = hateList.size() - 1; i >= 0; i--) {
										acquisitor = (L1Character) acquisitorList.get(i);
										hate = hateList.get(i);
										if (acquisitor instanceof L1PcInstance) {
											final L1PcInstance pc = (L1PcInstance) acquisitor;
											if (pc == tgpc) {
												if (ownHateExp > 0) {
													acquire_exp = ((member_exp * hate) / ownHateExp);
												}
												addExp(pc, acquire_exp, member_lawful);
											}

										} else if (acquisitor instanceof L1PetInstance) {
											final L1PetInstance pet = (L1PetInstance) acquisitor;
											final L1PcInstance master = (L1PcInstance) pet.getMaster();
											if (master == tgpc) {
												if (ownHateExp > 0) {
													acquire_exp = ((member_exp * hate) / ownHateExp);
												}
												addExpPet(pet, acquire_exp);
											}

										} else if (acquisitor instanceof L1SummonInstance) {
										}
									}

								} else { // 攻撃に参加していなかった
									// パーティーメンバーのみに分配
									addExp(tgpc, member_exp, member_lawful);
								}
							}
						}
					}

				} else { // パーティーを組んでいない
					// EXP、ロウフルの分配
					for (i = hateList.size() - 1; i >= 0; i--) {
						acquisitor = (L1Character) acquisitorList.get(i);
						hate = hateList.get(i);
						acquire_exp = ((exp * hate) / totalHateExp);
						if (acquisitor instanceof L1PcInstance) {
							if (totalHateLawful > 0) {
								acquire_lawful = ((lawful * hate) / totalHateLawful);
							}
						}

						if (acquisitor instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) acquisitor;
							addExp(pc, acquire_exp, acquire_lawful);

						} else if (acquisitor instanceof L1PetInstance) {
							final L1PetInstance pet = (L1PetInstance) acquisitor;
							addExpPet(pet, acquire_exp);

						} else if (acquisitor instanceof L1SummonInstance) {

						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 建立EXP 與 正義質 取得
	 * 
	 * @param pc
	 * @param exp
	 * @param lawful
	 */
	private static void addExp(final L1PcInstance pc, final long exp, final int lawful) {
		try {
			final int add_lawful = (int) (lawful * ConfigRate.RATE_LA) * -1;
			pc.addLawful(add_lawful);

			if (pc.getLevel() >= ExpTable.MAX_LEVEL) { // 已達最大等級終止計算
				return;
			}
			final double exp_rate = ConfigRate.RATE_XP;

			if (ConfigAlt.APPRENTICE_SWITCH) {
				if ((pc.getApprentice() != null) && (pc.getApprentice().getMaster().getId() != pc.getId())
						&& (pc.getApprentice().getMaster().getMapId() == pc.getMapId())) {
					final L1Party party = pc.getParty();
					if (party != null) {
						final int checkType = party.checkMentor(pc.getApprentice());
						if (checkType > 4) {
							double expBonus = exp * exp_rate;

							final double exppenalty = ExpTable.getPenaltyRate(
									pc.getApprentice().getMaster().getLevel(),
									pc.getApprentice().getMaster().getMeteLevel());
							// 目前等級可獲取的經驗值
							if (exppenalty < 1.0) {
								expBonus *= exppenalty;
							}
							// 每位徒弟可增加XX%經驗加成
							expBonus *= (ConfigAlt.APPRENTICE_EXP_BONUS / 100) * (checkType - 4);
							// 增加經驗
							pc.getApprentice().getMaster().addExp((long) expBonus);
						}
					}
				}
			}

			double addExp = exp;

			// 目前等級可獲取的經驗值
			final double exppenalty = ExpTable.getPenaltyRate(pc.getLevel(), pc.getMeteLevel());
			// 目前等級可獲取的經驗值
			if (exppenalty < 1D) {
				addExp *= exppenalty;
			}

			// 服務器經驗加倍
			if (exp_rate > 1.0) {
				addExp *= exp_rate;
			}

			if (LeavesSet.START) {
				if (pc.get_other().get_teaves_time_exp() > 0) {
					int add = (int) (addExp * 0.77);
					final int add2 = pc.get_other().get_teaves_time_exp() - add;
					if (add2 > 0) {
						pc.get_other().set_teaves_time_exp(add2);
						// 送出百分比
						pc.sendPackets(
								new S_PacketBoxExp(pc.get_other().get_teaves_time_exp() / LeavesSet.EXP));

					} else {
						add = pc.get_other().get_teaves_time_exp();
						pc.get_other().set_teaves_time_exp(0);
						// 解除
						pc.sendPackets(new S_PacketBoxExp());
					}
					addExp += add;
				}
			}

			addExp *= add(pc);

			pc.addExp((long) addExp);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * EXP增減
	 * 
	 * @param pc
	 * @return
	 */
	private static double add(final L1PcInstance pc) {
		try {
			double add_exp = 1.0D;
			if (pc.hasSkillEffect(MAZU_STATUS)) {// 媽祖祝福
				add_exp += 0.3;// 1.3 20151102
			}

			if (pc.getExpPoint() > 0) {
				add_exp += (double) pc.getExpPoint() / 100;
			}

			// 食物經驗值倍數/第一段經驗加倍
			double foodBonus = 0.0D;
			if (pc.hasSkillEffect(COOKING_1_7_N) || pc.hasSkillEffect(COOKING_1_7_S)) {
				foodBonus = 0.01;// 1.01
			}
			if (pc.hasSkillEffect(COOKING_2_7_N) || pc.hasSkillEffect(COOKING_2_7_S)) {
				foodBonus = 0.02;// 1.02
			}
			if (pc.hasSkillEffect(COOKING_3_7_N) || pc.hasSkillEffect(COOKING_3_7_S)) {
				foodBonus = 0.03;// 1.03
			}

			if (pc.hasSkillEffect(SEXP11)) {
				foodBonus = 0.10;
			}
			if (pc.hasSkillEffect(EXP13)) {
				foodBonus = 0.30;
			}
			if (pc.hasSkillEffect(EXP15)) {
				foodBonus = 0.50;
			}
			if (pc.hasSkillEffect(EXP17)) {
				foodBonus = 0.70;
			}
			if (pc.hasSkillEffect(EXP20)) {
				foodBonus = 1.00;// 2.0
			}
			if (pc.hasSkillEffect(EXP25)) {
				foodBonus = 1.50;// 2.5
			}
			if (pc.hasSkillEffect(EXP30)) {
				foodBonus = 2.00;// 3.0
			}
			if (pc.hasSkillEffect(EXP35)) {
				foodBonus = 2.50;
			}
			if (pc.hasSkillEffect(EXP40)) {
				foodBonus = 3.00;
			}
			if (pc.hasSkillEffect(EXP45)) {
				foodBonus = 3.50;
			}
			if (pc.hasSkillEffect(EXP50)) {
				foodBonus = 4.00;
			}
			if (pc.hasSkillEffect(EXP55)) {
				foodBonus = 4.50;
			}
			if (pc.hasSkillEffect(EXP60)) {
				foodBonus = 5.00;
			}
			if (pc.hasSkillEffect(EXP65)) {
				foodBonus = 5.50;
			}
			if (pc.hasSkillEffect(EXP70)) {
				foodBonus = 6.00;
			}
			if (pc.hasSkillEffect(EXP75)) {
				foodBonus = 6.50;
			}
			if (pc.hasSkillEffect(EXP80)) {
				foodBonus = 7.00;
			}

			// 食物經驗值倍數/第一段經驗加倍
			if (foodBonus > 0) {
				add_exp += foodBonus;
			}

			// 經驗值增加
			add_exp += pc.getExpAdd();

			// 第二段經驗加倍
			double s2_exp = 0.0D;
			if (pc.hasSkillEffect(SEXP13)) {
				s2_exp = 0.30;// 1.30
			}
			if (pc.hasSkillEffect(SEXP15)) {
				s2_exp = 0.50;// 1.50
			}
			if (pc.hasSkillEffect(SEXP17)) {
				s2_exp = 0.70;// 1.70
			}
			if (pc.hasSkillEffect(SEXP20)) {
				s2_exp = 1.00;// 2.00
			}

			if (s2_exp > 0) {
				add_exp += s2_exp;
			}

			// 轉生經驗稀釋
			double meta_exp = 0.0D;
			if (pc.getMeteAbility() != null) {
				meta_exp = (double) pc.getMeteAbility().getExpPenalty() / 100;
				add_exp -= meta_exp;
			}

			final int mapid = pc.getMapId();
			// 地圖經驗加倍
			if (MapExpTable.get().get_level(mapid, pc.getLevel())) {
				add_exp += (MapExpTable.get().get_exp(mapid) - 1.0D);
			}

			return add_exp;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 1.0D;
	}

	private static void addExpPet(final L1PetInstance pet, final long exp) {
		try {
			if (pet == null) {
				return;
			}
			if (pet.getPetType() == null) {
				return;
			}
			final L1PcInstance pc = (L1PcInstance) pet.getMaster();
			if (pc == null) {
				return;
			}
			final int petItemObjId = pet.getItemObjId();

			final int levelBefore = pet.getLevel();
			long totalExp = (long) ((exp * ConfigRate.RATE_XP) + pet.getExp());

			// 寵物最高等级限制 by terry0412
			final long maxExp = ExpTable.getExpByLevel(ConfigAlt.PET_MAX_LEVEL);
			if (totalExp > maxExp) {
				totalExp = maxExp - 1;
			}
			pet.setExp(totalExp);

			pet.setLevel(ExpTable.getLevelByExp(totalExp));

			final int expPercentage = ExpTable.getExpPercentage(pet.getLevel(), totalExp);

			final int gap = pet.getLevel() - levelBefore;
			for (int i = 1; i <= gap; i++) {
				final RangeInt hpUpRange = pet.getPetType().getHpUpRange();
				final RangeInt mpUpRange = pet.getPetType().getMpUpRange();
				pet.addMaxHp(hpUpRange.randomValue());
				pet.addMaxMp(mpUpRange.randomValue());
			}

			pet.setExpPercent(expPercentage);
			pc.sendPackets(new S_NPCPack_Pet(pet, pc));

			if (gap != 0) {
				final L1Pet petTemplate = PetReading.get().getTemplate(petItemObjId);
				if (petTemplate == null) {
					return;
				}
				petTemplate.set_exp((int) pet.getExp());
				petTemplate.set_level(pet.getLevel());
				petTemplate.set_hp(pet.getMaxHp());
				petTemplate.set_mp(pet.getMaxMp());
				PetReading.get().storePet(petTemplate); // 資料保存
				// \f1%0升級了。
				pc.sendPackets(new S_ServerMessage(320, pet.getName()));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}