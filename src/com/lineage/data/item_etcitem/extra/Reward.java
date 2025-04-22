package com.lineage.data.item_etcitem.extra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Reward;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

public class Reward {
	private static final Log _log = LogFactory.getLog(Reward.class);
	private static ArrayList<L1Reward> _giveList = new ArrayList<L1Reward>();
	public static final String TOKEN = ",";

	private static Reward _instance;

	public static Reward get() {
		if (_instance == null) {
			_instance = new Reward();
		}
		return _instance;
	}

	/** 資料重讀 */
	public static void reloadReward() {
		_giveList.clear();
		getItemData();
	}

	private Reward() {
		getItemData();
	}

	/** 取得獎勵 */
	public static void getItem(final L1PcInstance pc) {
		for (final L1Reward reward : _giveList) {
			boolean isOK = false;
			if ((pc.getLevel() >= reward.get_level()) && (pc.getMeteLevel() >= reward.get_metelevel())) {
				if (pc.isCrown() && (reward.get_royal() == 1)) {
					isOK = true;
				}
				if (pc.isKnight() && (reward.get_knight() == 1)) {
					isOK = true;
				}
				if (pc.isWizard() && (reward.get_mage() == 1)) {
					isOK = true;
				}
				if (pc.isElf() && (reward.get_elf() == 1)) {
					isOK = true;
				}
				if (pc.isDarkelf() && (reward.get_darkelf() == 1)) {
					isOK = true;
				}
				if (pc.isDragonKnight() && (reward.get_DragonKnight() == 1)) {
					isOK = true;
				}
				if (pc.isIllusionist() && (reward.get_Illusionist() == 1)) {
					isOK = true;
				}
				if (pc.isWarrior() && (reward.get_Warrior() == 1)) {
					isOK = true;
				}
				if (isOK && (pc.getQuest().get_step(reward.get_quest_id()) < reward.get_quest_step())) {
					pc.getQuest().set_logstep(reward.get_quest_id(), reward.get_quest_step());
					final int[] itemid = (int[]) getArray(reward.getItem(), ",", 1);
					final int[] counts = (int[]) getArray(reward.get_count(), ",", 1);
					final int[] enchant = (int[]) getArray(reward.get_enchantlvl(), ",", 1);
					for (int j = 0; j < itemid.length; j++) {
						// 物品資料
						final L1Item temp = ItemTable.get().getTemplate(itemid[j]);
						if (temp != null) {
							if (temp.isStackable()) {
								// 可以堆疊的物品
								final L1ItemInstance item = ItemTable.get().createItem(itemid[j]);
								item.setEnchantLevel(enchant[j]);
								item.setCount(counts[j]);
								item.setIdentified(true);
								if (pc.getInventory().checkAddItem(item, counts[j]) == L1Inventory.OK) {
									pc.getInventory().storeItem(item);
									// 403:获得0%。
									pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
								}
							} else {
								// 不可以堆疊的物品
								if (counts[j] > 10) {
									pc.sendPackets(new S_SystemMessage("不可以堆疊的物品一次獲取數量禁止超過10"));
									break;
								}

								L1ItemInstance item = null;
								int createCount;
								for (createCount = 0; createCount < counts[j]; createCount++) {
									item = ItemTable.get().createItem(itemid[j]);
									item.setEnchantLevel(enchant[j]);
									item.setIdentified(true);

									if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
										pc.getInventory().storeItem(item);
									} else {
										break;
									}
								}
								if (createCount > 0) {
									// 403:获得0%。
									pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
								}
							}
						}
					}
					pc.sendPackets(new S_SystemMessage(reward.get_message()));
				}
			}

		}
	}

	private static void getItemData() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rset = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `extra_reward`");
			rset = ps.executeQuery();
			while (rset.next()) {
				final int level = rset.getInt("level");
				final int royal = rset.getInt("give_royal");
				final int knight = rset.getInt("give_knight");
				final int mage = rset.getInt("give_mage");
				final int elf = rset.getInt("give_elf");
				final int darkelf = rset.getInt("give_darkelf");
				final int DragonKnight = rset.getInt("give_DragonKnight");
				final int Illusionist = rset.getInt("give_Illusionist");
				final int Warrior = rset.getInt("give_Warrior");
				final String getItem = rset.getString("getItem");
				final String count = rset.getString("count");
				final String enchantlvl = rset.getString("enchantlvl");
				final int quest_id = rset.getInt("quest_id");
				final int quest_step = rset.getInt("quest_step");
				final String message = rset.getString("message");
				final int metelevel = rset.getInt("metelevel");
				final L1Reward reward = new L1Reward();
				reward.set_level(level);
				reward.set_royal(royal);
				reward.set_knight(knight);
				reward.set_mage(mage);
				reward.set_elf(elf);
				reward.set_darkelf(darkelf);
				reward.set_DragonKnight(DragonKnight);
				reward.set_Illusionist(Illusionist);
				reward.set_Warrior(Warrior);
				reward.set_getItem(getItem);
				reward.set_count(count);
				reward.set_enchantlvl(enchantlvl);
				reward.set_quest_id(quest_id);
				reward.set_quest_step(quest_step);
				reward.set_message(message);
				reward.set_metelevel(metelevel);
				_giveList.add(reward);
			}
		} catch (final Exception ex) {
			_log.info("讀取資料表失敗!");
		} finally {
			SQLUtil.close(cn);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入等級自動獎勵系統: " + _giveList.size() + "(" + timer.get() + "ms)");
	}

	private static Object getArray(final String s, final String sToken, final int iType) {
		final StringTokenizer st = new StringTokenizer(s, sToken);
		final int iSize = st.countTokens();
		String sTemp = null;
		if (iType == 1) { // int
			final int[] iReturn = new int[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				iReturn[i] = Integer.parseInt(sTemp);
			}
			return iReturn;
		}
		if (iType == 2) { // String
			final String[] sReturn = new String[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn[i] = sTemp;
			}
			return sReturn;
		}
		if (iType == 3) { // String
			String sReturn = null;
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn = sTemp;
			}
			return sReturn;
		}
		return null;
	}
}
