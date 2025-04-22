package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.ADDITIONAL_FIRE;
import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.AWAKEN_ANTHARAS;
import static com.lineage.server.model.skill.L1SkillId.AWAKEN_FAFURION;
import static com.lineage.server.model.skill.L1SkillId.AWAKEN_VALAKAS;
import static com.lineage.server.model.skill.L1SkillId.BOUNCE_ATTACK;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.CURE_POISON;
import static com.lineage.server.model.skill.L1SkillId.DETECTION;
import static com.lineage.server.model.skill.L1SkillId.DRAGON_SKIN;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.HEAL;
import static com.lineage.server.model.skill.L1SkillId.HOLY_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.ILLUSION_AVATAR;
import static com.lineage.server.model.skill.L1SkillId.ILLUSION_DIA_GOLEM;
import static com.lineage.server.model.skill.L1SkillId.ILLUSION_LICH;
import static com.lineage.server.model.skill.L1SkillId.ILLUSION_OGRE;
import static com.lineage.server.model.skill.L1SkillId.INSIGHT;
import static com.lineage.server.model.skill.L1SkillId.LIGHT;
import static com.lineage.server.model.skill.L1SkillId.MEDITATION;
import static com.lineage.server.model.skill.L1SkillId.MIRROR_IMAGE;
import static com.lineage.server.model.skill.L1SkillId.PATIENCE;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.SHIELD;
import static com.lineage.server.model.skill.L1SkillId.SILENCE;
import static com.lineage.server.model.skill.L1SkillId.SOLID_CARRIAGE;
import static com.lineage.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.TELEPORT;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.PolyTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.timecontroller.server.ServerWarExecutor;

/**
 * 要求使用技能
 * 
 * @author daien
 */
public class C_UseSkill extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_UseSkill.class);

	/*
	 * public C_UseSkill() { } public C_UseSkill(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	// 隱身狀態下可用的魔法
	private static final int[] _cast_with_invis = { HEAL, LIGHT, SHIELD,
			TELEPORT, HOLY_WEAPON, CURE_POISON, ENCHANT_WEAPON, DETECTION, 14,
			19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57, 60,
			61, 63, 67, 68, 69, 72, 73, 75, 78, 79, REDUCTION_ARMOR,
			BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100,
			101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114, 115, 116,
			117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149,
			150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169, 170,
			171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS,
			AWAKEN_FAFURION, AWAKEN_VALAKAS, MIRROR_IMAGE, ILLUSION_OGRE,
			ILLUSION_LICH, PATIENCE, ILLUSION_DIA_GOLEM, INSIGHT,
			ILLUSION_AVATAR };

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isDead()) { // 死亡
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			if (pc.isPrivateShop()) {// 商店村模式
				return;
			}

			if (pc.getInventory().getWeight240() >= 197) { // 重量過重
				pc.setTeleport(false);
				pc.sendPackets(new S_Paralysis(
						S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				// 316 \f1你攜帶太多物品，因此無法使用法術。
				pc.sendPackets(new S_ServerMessage(316));
				return;
			}

			if (!pc.getMap().isUsableSkill()) {
				pc.setTeleport(false);
				pc.sendPackets(new S_Paralysis(
						S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				// 563 \f1你無法在這個地方使用。
				pc.sendPackets(new S_ServerMessage(563));
				return;
			}

			// 技能延遲狀態
			if (pc.isSkillDelay()) {
				return;
			}

			boolean isError = false;

			// 變身限制
			final int polyId = pc.getTempCharGfx();
			final L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
			// 該變身無法使用魔法
			if ((poly != null) && !poly.canUseSkill()) {
				isError = true;
			}

			// 麻痺・凍結狀態
			if (pc.isParalyzed() && !isError) {
				isError = true;
			}

			// 下列狀態無法使用魔法(魔法封印)
			if (pc.hasSkillEffect(SILENCE) && !isError) {
				isError = true;
			}

			// 下列狀態無法使用魔法(封印禁地)
			if (pc.hasSkillEffect(AREA_OF_SILENCE) && !isError) {
				isError = true;
			}

			// 下列狀態無法使用魔法(沈黙毒素效果)
			if (pc.hasSkillEffect(STATUS_POISON_SILENCE) && !isError) {
				isError = true;
			}

			// 無法攻擊/使用道具/技能/回城的狀態
			if (pc.isParalyzedX() && !isError) {
				isError = true;
			}

			if (isError) {
				pc.setTeleport(false);
				pc.sendPackets(new S_Paralysis(
						S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				// 285 \f1在此狀態下無法使用魔法。
				pc.sendPackets(new S_ServerMessage(285));
				return;
			}

			// 加載封包內容
			final int row = readC();
			final int column = readC();

			// 計算使用的技能編號(>> 1: 除) (<< 1: 乘) 3=*8
			final int skillId = (row << 3) + column + 1;

			if (skillId > 239) {
				return;
			}
			if (skillId < 0) {
				return;
			}

			// 隱身狀態可用魔法限制
			if (pc.isInvisble() || pc.isInvisDelay()) {
				if (!isInvisUsableSkill(skillId)) {
					// 1003：透明狀態無法使用的魔法。
					pc.sendPackets(new S_ServerMessage(1003));
					return;
				}
			}

			// 技能合法判斷
			if (!pc.isSkillMastery(skillId)) {
				// _log.info(pc.getAccountName() + ":" + pc.getName() + "(" +
				// pc.getType() + ") 非法技能:" + skillId);
				return;
			}
			// 城戰旗幟內 - 技能魔法限制
			final int castleId = L1CastleLocation.getCastleIdByArea(pc);
			if (castleId != 0) {
				if (ServerWarExecutor.get().isNowWar(castleId)) {
					switch (skillId) {
					case 50:// 冰矛圍籬
					case 51:// 召喚術
					case 78:// 絕對屏障
					case 157:// 大弟屏障
						return;
					}
				}
			}

			// 檢查地圖使用權
			CheckUtil.isUserMap(pc);

			String charName = null;
			// String message = null;

			int targetId = 0;
			int targetX = 0;
			int targetY = 0;
			if (ConfigOther.CHECK_SPELL_INTERVAL) {
				int result;
				// FIXME 判斷有向及無向的魔法 (防加速偵測補加入魔法判斷) 2014/08/05 By Roy
				if (SkillsTable.get().getTemplate(skillId).getActionId() == ActionCodes.ACTION_SkillAttack) {
					result = pc.speed_Attack().checkInterval(
							AcceleratorChecker.ACT_TYPE.SPELL_DIR);
				} else {
					result = pc.speed_Attack().checkInterval(
							AcceleratorChecker.ACT_TYPE.SPELL_NODIR);
				}
				if (result == AcceleratorChecker.R_DISPOSED) {
					return;
				}
			}

			if (decrypt.length > 4) {// 可選擇對象
				switch (skillId) {
				case 116:// 呼喚盟友
				case 118:// 援護盟友
					charName = readS();
					break;

				case 113:// 精準目標 1600
					targetId = readD();
					targetX = readH();
					targetY = readH();
					pc.setText(readS());
					break;

				case 5:// 指定傳送 1100
					if (!L1BuffUtil.getUseSkillTeleport(pc)) {
						pc.setTeleport(false);
						pc.sendPackets(new S_Paralysis(
								S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						return;
					}
					
				case 69:// 集體傳送術 1200
					try {
						readH(); // MapID
						targetId = readD(); // Bookmark ID

					} catch (final Exception e) {
					}
					break;

				case 58:// 火牢 1100
				case 63:// 治癒能量風暴 1100
					targetX = readH();
					targetY = readH();
					break;

				case 87:// shock stun
					if ((pc.getWeapon() == null)
							|| (pc.getWeapon().getItem().getType() != 3)) {
						return;
					}
					targetId = readD();
					targetX = readH();
					targetY = readH();
					break;

				default:
					targetId = readD();
					targetX = readH();
					targetY = readH();
					break;
				}
			} else {
				switch (skillId) {
				case 91:// Counter Barrier
					if ((pc.getWeapon() == null)
							|| (pc.getWeapon().getItem().getType() != 3)) {
						return;
					}
					break;
				}
			}

			// 絕對屏障解除
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
				pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
				pc.startHpRegeneration();
				pc.startMpRegeneration();
			}

			// 冥想術解除
			pc.removeSkillEffect(MEDITATION);
			// pc.killSkillEffectTimer(MEDITATION);

			try {
				// 呼喚盟友/援護盟友
				if ((skillId == 116) || (skillId == 118)) {
					if (charName.isEmpty()) {
						// 輸入名稱為空
						return;
					}

					final L1PcInstance target = World.get().getPlayer(charName);

					if (target == null) {
						// 73 \f1%0%d 不在線上。
						pc.sendPackets(new S_ServerMessage(73, charName));
						return;
					}

					// 無法攻擊/使用道具/技能/回城的狀態 XXX
					/*
					 * if (target.isParalyzedX()) { return; }
					 */

					if (pc.getClanid() != target.getClanid()) {
						// 您只能邀請您血盟中的成員。
						pc.sendPackets(new S_ServerMessage(414));
						return;
					}

					targetId = target.getId();
					if (skillId == 116) {// 呼喚盟友
						// 移動せずに連続して同じクラン員にコールクランした場合、向きは前回の向きになる
						final int callClanId = pc.getCallClanId();
						if ((callClanId == 0) || (callClanId != targetId)) {
							pc.setCallClanId(targetId);
							pc.setCallClanHeading(pc.getHeading());
						}
					}
				}

				final L1SkillUse skilluse = new L1SkillUse();
				skilluse.handleCommands(pc, skillId, targetId, targetX,
						targetY, 0, L1SkillUse.TYPE_NORMAL);

				if (skillId == L1SkillId.DECREASE_WEIGHT || skillId == L1SkillId.REDUCE_WEIGHT) {
					// XXX 7.6 重量程度資訊
					pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int)pc.getMaxWeight(), pc.getInventory().getWeight(), (int)pc.getMaxWeight()));
				}
				
			} catch (final Exception e) {
				/*
				 * OutErrorMsg.put(this.getClass().getSimpleName(),
				 * "檢查 C_UseSkill 程式執行位置(核心管理者參考) SkillId: " + skillId, e);
				 */
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/**
	 * 該技能可否在隱身狀態使用
	 * 
	 * @param useSkillid
	 *            技能編號
	 * @return true:可 false:不可
	 */
	private boolean isInvisUsableSkill(final int useSkillid) {
		for (final int skillId : _cast_with_invis) {
			if (skillId == useSkillid) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}