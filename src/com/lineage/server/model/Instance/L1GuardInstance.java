package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1NpcTalkData;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 對象:警衛 控制項
 * 
 * @author daien
 */
public class L1GuardInstance extends L1NpcInstance {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1GuardInstance.class);

	/**
	 * 目標搜尋
	 */
	@Override
	public void searchTarget() {
		// 目標搜尋
		final L1PcInstance targetPlayer = searchTarget(this);
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	private static L1PcInstance searchTarget(final L1GuardInstance npc) {
		L1PcInstance targetPlayer = null;
		for (final L1PcInstance pc : World.get().getVisiblePlayer(npc)) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
			if ((pc.getCurrentHp() <= 0) || pc.isDead() || pc.isGm() || pc.isGhost()) {
				continue;
			}

			// 副本ID不相等
			if (npc.get_showId() != pc.get_showId()) {
				continue;
			}

			if (!pc.isInvisble() || npc.getNpcTemplate().is_agrocoi()) {
				if (pc.isWanted()) { // PKで手配中か
					targetPlayer = pc;
					break;
				}
			}
		}
		return targetPlayer;
	}

	public void setTarget(final L1PcInstance targetPlayer) {
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	// ターゲットがいない場合の処理
	@Override
	public boolean noTarget() {
		if (getLocation().getTileLineDistance(new Point(getHomeX(), getHomeY())) > 0) {
			if (_npcMove != null) {
				final int dir = _npcMove.moveDirection(getHomeX(), getHomeY());
				if (dir != -1) {
					_npcMove.setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));

				} else {
					teleport(getHomeX(), getHomeY(), 1);
				}
			}

		} else {
			if (World.get().getRecognizePlayer(this).size() == 0) {
				return true; // 周りにプレイヤーがいなくなったらＡＩ処理終了
			}
		}
		return false;
	}

	public L1GuardInstance(final L1Npc template) {
		super(template);
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			if (!isDead()) {
				if (getCurrentHp() > 0) {
					final L1AttackMode attack = new L1AttackPc(pc, this);
					if (attack.calcHit()) {
						attack.calcDamage();
					}
					attack.action();
					attack.commit();

				} else {
					final L1AttackMode attack = new L1AttackPc(pc, this);
					attack.calcHit();
					attack.action();
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		final int objid = getId();
		final L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(getNpcTemplate().get_npcId());
		final int npcid = getNpcTemplate().get_npcId();
		String htmlid = null;
		String[] htmldata = null;
		boolean hascastle = false;
		String clan_name = " ";
		String pri_name = " ";

		if (talking != null) {
			// キーパー
			if ((npcid == 70549) || // ケント城左外門キーパー
					(npcid == 70985)) { // ケント城右外門キーパー
				hascastle = checkHasCastle(player, L1CastleLocation.KENT_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if (npcid == 70656) { // ケント城内門キーパー
				hascastle = checkHasCastle(player, L1CastleLocation.KENT_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if ((npcid == 70600) || // オークの森外門キーパー
					(npcid == 70986)) {
				hascastle = checkHasCastle(player, L1CastleLocation.OT_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "orckeeper";
				} else {
					htmlid = "orckeeperop";
				}
			} else if ((npcid == 70687) || // ウィンダウッド城外門キーパー
					(npcid == 70987)) {
				hascastle = checkHasCastle(player, L1CastleLocation.WW_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if (npcid == 70778) { // ウィンダウッド城内門キーパー
				hascastle = checkHasCastle(player, L1CastleLocation.WW_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if ((npcid == 70800) || // ギラン城外門キーパー
					(npcid == 70988) || (npcid == 70989) || (npcid == 70990) || (npcid == 70991)) {
				hascastle = checkHasCastle(player, L1CastleLocation.GIRAN_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if (npcid == 70817) { // ギラン城内門キーパー
				hascastle = checkHasCastle(player, L1CastleLocation.GIRAN_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if ((npcid == 70862) || // ハイネ城外門キーパー
					(npcid == 70992)) {
				hascastle = checkHasCastle(player, L1CastleLocation.HEINE_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if (npcid == 70863) { // ハイネ城内門キーパー
				hascastle = checkHasCastle(player, L1CastleLocation.HEINE_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if ((npcid == 70993) || // ドワーフ城外門キーパー
					(npcid == 70994)) {
				hascastle = checkHasCastle(player, L1CastleLocation.DOWA_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if (npcid == 70995) { // ドワーフ城内門キーパー
				hascastle = checkHasCastle(player, L1CastleLocation.DOWA_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			} else if (npcid == 70996) { // アデン城内門キーパー
				hascastle = checkHasCastle(player, L1CastleLocation.ADEN_CASTLE_ID);
				if (hascastle) { // 城盟成員
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
			}

			// 近衛兵
			else if (npcid == 60514) { // ケント城近衛兵
				final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
				for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
					final L1Clan clan = iter.next();
					if (clan.getCastleId() // 城主クラン
					== L1CastleLocation.KENT_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "ktguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
			} else if (npcid == 60560) { // オーク近衛兵
				final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
				for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
					final L1Clan clan = iter.next();
					if (clan.getCastleId() // 城主クラン
					== L1CastleLocation.OT_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "orcguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
			} else if (npcid == 60552) { // ウィンダウッド城近衛兵
				final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
				for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
					final L1Clan clan = iter.next();
					if (clan.getCastleId() // 城主クラン
					== L1CastleLocation.WW_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "wdguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
			} else if ((npcid == 60524) || // ギラン街入り口近衛兵(弓)
					(npcid == 60525) || // ギラン街入り口近衛兵
					(npcid == 60529)) { // ギラン城近衛兵
				final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
				for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
					final L1Clan clan = iter.next();
					if (clan.getCastleId() // 城主クラン
					== L1CastleLocation.GIRAN_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "grguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
			} else if (npcid == 70857) { // ハイネ城ハイネ ガード
				final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
				for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
					final L1Clan clan = iter.next();
					if (clan.getCastleId() // 城主クラン
					== L1CastleLocation.HEINE_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "heguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
			} else if ((npcid == 60530) || // ドワーフ城ドワーフ ガード
					(npcid == 60531)) {
				final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
				for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
					final L1Clan clan = iter.next();
					if (clan.getCastleId() // 城主クラン
					== L1CastleLocation.DOWA_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "dcguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
			} else if ((npcid == 60533) || // アデン城 ガード
					(npcid == 60534)) {
				final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
				for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
					final L1Clan clan = iter.next();
					if (clan.getCastleId() // 城主クラン
					== L1CastleLocation.ADEN_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "adguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
			} else if (npcid == 81156) { // アデン偵察兵（ディアド要塞）
				final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
				for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
					final L1Clan clan = iter.next();
					if (clan.getCastleId() // 城主クラン
					== L1CastleLocation.DIAD_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "ktguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
			}

			// html表示パケット送信
			if (htmlid != null) { // htmlidが指定されている場合
				if (htmldata != null) { // html指定がある場合は表示
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));

				} else {
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}

			} else {
				if (player.getLawful() < -1000) { // プレイヤーがカオティック
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		}
	}

	public void onFinalAction() {

	}

	public void doFinalAction() {

	}

	@Override
	public void receiveDamage(L1Character attacker, final int damage) { // 攻撃でＨＰを減らすときはここを使用
		ISASCAPE = false;
		if ((getCurrentHp() > 0) && !isDead()) {
			if (damage >= 0) {
				/*
				 * if (!(attacker instanceof L1EffectInstance)) { // FWはヘイトなし
				 * this.setHate(attacker, damage); }
				 */
				if (attacker instanceof L1EffectInstance) { // 效果不列入目標
					// this.setHate(attacker, damage);

				} else if (attacker instanceof L1IllusoryInstance) { // 攻擊者是分身不列入目標(設置主人為目標)
					final L1IllusoryInstance ill = (L1IllusoryInstance) attacker;
					attacker = ill.getMaster();
					setHate(attacker, damage);

				} else {
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				removeSkillEffect(FOG_OF_SLEEPING);
			}

			onNpcAI();

			if ((attacker instanceof L1PcInstance) && (damage > 0)) {
				final L1PcInstance pc = (L1PcInstance) attacker;
				pc.setPetTarget(this);
			}

			final int newHp = getCurrentHp() - damage;
			if ((newHp <= 0) && !isDead()) {
				setCurrentHpDirect(0);
				setDead(true);
				setStatus(ActionCodes.ACTION_Die);
				final Death death = new Death(attacker);
				GeneralThreadPool.get().execute(death);
			}

			if (newHp > 0) {
				setCurrentHp(newHp);
			}

		} else if ((getCurrentHp() == 0) && !isDead()) {

		} else if (!isDead()) { // 念のため
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			final Death death = new Death(attacker);
			GeneralThreadPool.get().execute(death);
		}
	}

	@Override
	public void setCurrentHp(final int i) {
		final int currentHp = Math.min(i, getMaxHp());

		if (getCurrentHp() == currentHp) {
			return;
		}

		setCurrentHpDirect(currentHp);

		/*
		 * if (this.getMaxHp() > this.getCurrentHp()) {
		 * this.startHpRegeneration(); }
		 */
	}

	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(final L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);

			getMap().setPassable(getLocation(), true);

			broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));

			startChat(CHAT_TIMING_DEAD);

			setDeathProcessing(false);

			allTargetClear();

			startDeleteTimer(ConfigAlt.NPC_DELETION_TIME);
		}
	}

	private boolean checkHasCastle(final L1PcInstance pc, final int castleId) {
		boolean isExistDefenseClan = false;
		final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
		for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
			final L1Clan clan = iter.next();
			if (castleId == clan.getCastleId()) {
				isExistDefenseClan = true;
				break;
			}
		}
		if (!isExistDefenseClan) { // 城主クランが居ない
			return true;
		}

		if (pc.getClanid() != 0) { // クラン所属中
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				if (clan.getCastleId() == castleId) {
					return true;
				}
			}
		}
		return false;
	}
}
