package com.lineage.server.model;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.serverpackets.S_War;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;

/**
 * 戰爭
 * 
 * @author daien
 */
public class L1War {

	private static final Log _log = LogFactory.getLog(L1War.class);

	private final ConcurrentHashMap<String, L1Clan> _attackList;// 攻擊方清單

	private String _attackClanName = null;// 攻擊方
	private String _defenceClanName = null;// 守衛方盟名稱
	private L1Clan _defenceClan = null;// 守衛方盟

	private int _warType = 0;// 戰爭類型 1:攻城戦 2:模擬戦
	private int _castleId = 0;// 城堡編號

	private boolean _isWarTimerDelete = false; // 戰爭終止 true:終止 false:尚未

	public L1War() {
		_attackList = new ConcurrentHashMap<String, L1Clan>(); // 攻擊方盟名稱
	}

	/**
	 * 本場戰爭-城堡編號
	 * 
	 * @return
	 */
	public int get_castleId() {
		return _castleId;
	}

	/**
	 * 本場戰爭-守衛方盟名稱
	 * 
	 * @return
	 */
	public String get_defenceClanName() {
		return _defenceClanName;
	}

	/**
	 * 本場戰爭-攻擊方盟名稱(盟戰)
	 * 
	 * @return
	 */
	public String get_attackClanName() {
		return _attackClanName;
	}

	/**
	 * 戰爭狀態
	 * 
	 * @return true:結束 false:進行中
	 */
	public boolean isWarTimerDelete() {
		return _isWarTimerDelete;
	}

	class SimWarTimer implements Runnable {
		public SimWarTimer() {
		}

		@Override
		public void run() {
			for (int loop = 0; loop < 240; loop++) { // 240分
				try {
					Thread.sleep(60000);

				} catch (final Exception exception) {
					break;
				}
				if (_isWarTimerDelete) { // 戰爭時間終止
					return;
				}
			}
			WorldWar.get().removeWar(L1War.this); // 戰爭數據刪除
			ceaseWar(_attackClanName, _defenceClanName); // 結束
			delete();
		}
	}

	/**
	 * 宣戰(建立戰爭數據初始化)
	 * 
	 * @param war_type 1:攻城戦 2:模擬戦
	 * @param attack_clan_name 宣戰盟
	 * @param defence_clan_name 被宣戰盟(攻城戰時 為城盟名稱)
	 */
	public void handleCommands(final int war_type, final String attack_clan_name,
			final String defence_clan_name) {
		try {
			_attackList.clear();// 清空

			_warType = war_type;// 紀錄模式
			_defenceClanName = defence_clan_name;// 防守方
			_defenceClan = WorldClan.get().getClan(_defenceClanName);

			addAttackClan(attack_clan_name);
			declareWar(attack_clan_name, defence_clan_name);
			switch (war_type) {
			case 1:// 攻城戦
				_castleId = getCastleId();
				break;

			case 2:// 模擬戦
				final SimWarTimer sim_war_timer = new SimWarTimer();
				GeneralThreadPool.get().execute(sim_war_timer); // 計時開始
				break;
			}
			WorldWar.get().addWar(this); // 加入世界戰爭清單

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 戰爭(城堡)
	 * 
	 * @param type
	 * @param attack_clan_name
	 */
	private void requestCastleWar(final int type, final String attack_clan_name) {
		if (attack_clan_name == null) {
			return;
		}
		try {
			final L1Clan attack_clan = WorldClan.get().getClan(attack_clan_name);
			if (attack_clan != null) {

				if (_defenceClan != null) {
					switch (type) {
					case 1: // 宣戰
						World.get().broadcastPacketToAll(new S_War(1, attack_clan_name, _defenceClanName));
						break;

					case 2: // 投降
						World.get().broadcastPacketToAll(new S_War(2, attack_clan_name, _defenceClanName));
						World.get().broadcastPacketToAll(new S_War(4, _defenceClanName, attack_clan_name));
						removeAttackClan(attack_clan_name);
						break;

					case 3: // 結束
						World.get().broadcastPacketToAll(new S_War(3, attack_clan_name, _defenceClanName));
						removeAttackClan(attack_clan_name);
						break;
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 戰爭(血盟)
	 * 
	 * @param type
	 * @param clan1_name
	 * @param clan2_name
	 */
	private void requestSimWar(final int type, final String clan1_name, final String clan2_name) {
		try {
			if ((clan1_name == null) || (clan2_name == null)) {
				return;
			}

			final L1Clan clan1 = WorldClan.get().getClan(clan1_name);
			if (clan1 == null) {
				return;
			}
			final L1Clan clan2 = WorldClan.get().getClan(clan2_name);
			if (clan2 == null) {
				return;
			}

			switch (type) {
			case 1: // 宣戰
				clan1.sendPacketsAll(new S_War(1, clan1_name, clan2_name));
				clan2.sendPacketsAll(new S_War(1, clan1_name, clan2_name));
				break;

			case 2: // 投降
				clan1.sendPacketsAll(new S_War(2, clan1_name, clan2_name));
				clan2.sendPacketsAll(new S_War(4, clan2_name, clan1_name));

				clan1.sendPacketsAll(new S_War(3, clan1_name, clan2_name));
				clan2.sendPacketsAll(new S_War(3, clan1_name, clan2_name));
				break;

			case 3: // 結束
				clan1.sendPacketsAll(new S_War(3, clan1_name, clan2_name));
				clan2.sendPacketsAll(new S_War(3, clan1_name, clan2_name));
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			switch (type) {
			case 2: // 投降
			case 3: // 結束
				WorldWar.get().removeWar(this); // 戰爭數據刪除
				_isWarTimerDelete = true;
				delete();
				break;
			}
		}
	}

	public void winCastleWar(final String clan_name) { // 王冠奪取,進攻方獲勝
		try {
			WorldWar.get().removeWar(this); // 戰爭數據刪除
			_isWarTimerDelete = true;

			final Set<String> clanList = getAttackClanList();
			if (!clanList.isEmpty()) {
				World.get().broadcastPacketToAll(new S_War(4, clan_name, _defenceClanName));// 231：%0
																							// 血盟贏了對
																							// %1
																							// 血盟的戰爭。

				for (final Iterator<String> iter = clanList.iterator(); iter.hasNext();) {
					final String enemy_clan_name = iter.next();
					if (!clan_name.equalsIgnoreCase(enemy_clan_name)) {
						World.get().broadcastPacketToAll(new S_War(4, _defenceClanName, enemy_clan_name));// 231：%0
																											// 血盟贏了對
																											// %1
																											// 血盟的戰爭。。
					}
				}
				for (final Iterator<String> iter = clanList.iterator(); iter.hasNext();) {
					final String enemy_clan_name = iter.next();
					// 227 %0 血盟與 %1血盟之間的戰爭結束了。
					World.get().broadcastPacketToAll(new S_War(3, _defenceClanName, enemy_clan_name));// 227：%0
																										// 血盟與
																										// %1血盟之間的戰爭結束了。
					_attackList.remove(enemy_clan_name);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			delete();
		}
	}

	public void ceaseCastleWar() { // 城堡戰爭時間終止,防禦方獲勝
		try {
			WorldWar.get().removeWar(this); // 戰爭數據刪除
			_isWarTimerDelete = true;

			final Set<String> clanList = getAttackClanList();
			if (!clanList.isEmpty()) {
				for (final Iterator<String> iter = clanList.iterator(); iter.hasNext();) {
					final String enemy_clan_name = iter.next();
					World.get().broadcastPacketToAll(new S_War(4, _defenceClanName, enemy_clan_name));// 231：%0
																										// 血盟贏了對
																										// %1
																										// 血盟的戰爭。。
				}

				for (final Iterator<String> iter = clanList.iterator(); iter.hasNext();) {
					final String enemy_clan_name = iter.next();
					World.get().broadcastPacketToAll(new S_War(3, _defenceClanName, enemy_clan_name));// 227：%0
																										// 血盟與
																										// %1血盟之間的戰爭結束了。
					_attackList.remove(enemy_clan_name);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			delete();
		}
	}

	/**
	 * 血盟 與 血盟 宣戰佈告
	 * 
	 * @param clan1_name 宣戰盟
	 * @param clan2_name 被宣戰盟
	 */
	public void declareWar(final String attack_clan_name, final String defence_clan_name) {
		try {
			if (getWarType() == 1) { // 攻城戦
				requestCastleWar(1, attack_clan_name);

			} else { // 模擬戦
				_attackClanName = attack_clan_name;// 攻擊方
				requestSimWar(1, attack_clan_name, defence_clan_name);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 228：%0 血盟向 %1 血盟投降了。
	 * 
	 * @param clan1_name
	 * @param clan2_name
	 */
	public void surrenderWar(final String clan1_name, final String clan2_name) {
		try {
			if (getWarType() == 1) {
				requestCastleWar(2, clan1_name);

			} else {
				requestSimWar(2, clan1_name, clan2_name);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 227：%0 血盟與 %1血盟之間的戰爭結束了
	 * 
	 * @param clan1_name
	 * @param clan2_name
	 */
	public void ceaseWar(final String clan1_name, final String clan2_name) {
		try {
			if (getWarType() == 1) {
				requestCastleWar(3, clan1_name);

			} else {
				requestSimWar(3, clan1_name, clan2_name);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 指令終止戰爭
	 */
	public void ceaseWar() {
		try {
			ceaseWar(_attackClanName, _defenceClanName);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 231：%0 血盟贏了對 %1 血盟的戰爭。
	 * 
	 * @param clan1_name
	 * @param clan2_name
	 */
	public void winWar(final String clan1_name, final String clan2_name) {
		try {
			if (getWarType() == 1) {
				requestCastleWar(4, clan1_name);

			} else {
				requestSimWar(4, clan1_name, clan2_name);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 是否為戰爭中血盟
	 * 
	 * @param clan_name 血盟名稱
	 * @return true:戰爭中血盟 false:非戰爭中血盟
	 */
	public boolean checkClanInWar(final String clan_name) {
		if (_isWarTimerDelete) {
			WorldWar.get().removeWar(this); // 戰爭數據刪除
			delete();
			return false;
		}
		boolean ret = false;
		// 防禦城堡血盟
		if (_defenceClanName.equalsIgnoreCase(clan_name)) {
			ret = true;

		} else {
			// 為進攻血盟
			ret = checkAttackClan(clan_name);
		}
		return ret;
	}

	/**
	 * 戰爭中血盟檢查
	 * 
	 * @param player_clan_name
	 * @param target_clan_name
	 * @return
	 */
	public boolean checkClanInSameWar(final String player_clan_name, final String target_clan_name) {
		boolean player_clan_flag = false;
		boolean target_clan_flag = false;

		if (_defenceClanName.equalsIgnoreCase(player_clan_name)) { // player_clan_name為防禦方
			player_clan_flag = true;
		} else {
			player_clan_flag = checkAttackClan(player_clan_name); // 檢查player_clan_name是否為進攻方
		}

		if (_defenceClanName.equalsIgnoreCase(target_clan_name)) { // target_clan_name為防禦方
			target_clan_flag = true;
		} else {
			target_clan_flag = checkAttackClan(target_clan_name); // 檢查target_clan_name是否為進攻方
		}

		if ((player_clan_flag == true) && (target_clan_flag == true)) {
			return true;

		} else {
			return false;
		}
	}

	/**
	 * 戰爭中對手血盟名稱取回
	 * 
	 * @param player_clan_name
	 * @return
	 */
	public String getEnemyClanName(final String player_clan_name) {
		if (_defenceClanName.equalsIgnoreCase(player_clan_name)) { // player_clan_name是防禦方
			final Set<String> clanList = getAttackClanList();
			if (!clanList.isEmpty()) {
				for (final Iterator<String> iter = clanList.iterator(); iter.hasNext();) {
					return iter.next();
				}
			}

		} else { // player_clan_name是攻擊方
			return _defenceClanName;
		}
		return null;
	}

	public void delete() {
		try {
			_log.info(_defenceClanName + " 戰爭終止完成 剩餘戰爭清單數量:" + WorldWar.get().getWarList().size());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			_attackList.clear();// 清空
			_attackClanName = null;// 攻擊方
			_defenceClanName = null;// 守衛方盟名稱
			_defenceClan = null;// 守衛方盟
			_warType = 0;// 戰爭類型 1:攻城戦 2:模擬戦
			_castleId = 0;// 城堡編號
			_isWarTimerDelete = true;// 戰爭終止 true:終止 false:尚未
		}
	}

	public int getWarType() {
		return _warType;
	}

	/**
	 * 加入進攻血盟
	 * 
	 * @param attack_clan_name
	 */
	public void addAttackClan(final String attack_clan_name) {
		final L1Clan attack_clan = WorldClan.get().getClan(attack_clan_name);
		if (attack_clan != null) {
			_attackList.put(attack_clan_name.toLowerCase(), attack_clan);
		}
	}

	/**
	 * 移除進攻血盟
	 * 
	 * @param attack_clan_name
	 */
	public void removeAttackClan(final String attack_clan_name) {
		if (_attackList.get(attack_clan_name.toLowerCase()) != null) {
			_attackList.remove(attack_clan_name.toLowerCase());
		}
	}

	/**
	 * 是否為進攻血盟
	 * 
	 * @param attack_clan_name
	 * @return
	 */
	public boolean checkAttackClan(final String attack_clan_name) {
		if (_attackList.get(attack_clan_name.toLowerCase()) != null) {
			return true;
		}
		return false;
	}

	/**
	 * 進攻血盟清單
	 * 
	 * @return
	 */
	public Set<String> getAttackClanList() {
		return _attackList.keySet();
	}

	/**
	 * 戰爭時-攻城戰中城堡編號
	 * 
	 * @return
	 */
	public int getCastleId() {
		switch (_warType) {
		case 1:// 攻城戦
			final L1Clan clan = WorldClan.get().getClan(_defenceClanName);
			if (clan != null) {
				final int castle_id = clan.getCastleId();
				return castle_id;
			}
			break;
		case 2:// 模擬戦
			break;
		}
		return 0;
	}

	/**
	 * 戰爭時-攻城戰中城堡數據
	 * 
	 * @return
	 */
	public L1Castle getCastle() {
		switch (_warType) {
		case 1:// 攻城戦
			final L1Clan clan = WorldClan.get().getClan(_defenceClanName);
			if (clan != null) {
				final int castle_id = clan.getCastleId();
				final L1Castle castle = CastleReading.get().getCastleTable(castle_id);
				return castle;
			}
			break;
		case 2:// 模擬戦
			break;
		}
		return null;
	}
}
