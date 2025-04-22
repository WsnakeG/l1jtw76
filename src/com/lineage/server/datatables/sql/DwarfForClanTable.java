package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.storage.DwarfForClanStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 血盟倉庫物件清單
 * 
 * @author dexc
 */
public class DwarfForClanTable implements DwarfForClanStorage {

	private static final Log _log = LogFactory.getLog(DwarfForClanTable.class);

	// 血盟倉庫物件清單 (血盟名稱) (物品清單)
	private static final Map<String, CopyOnWriteArrayList<L1ItemInstance>> _itemList = new ConcurrentHashMap<String, CopyOnWriteArrayList<L1ItemInstance>>();

	/**
	 * 預先加載
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		int i = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			// 加入三段式資料排序 by terry0412
			pstm = con.prepareStatement("SELECT * FROM `clan_warehouse` order by clan_name, item_id, id");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int objid = rs.getInt("id");
				final String clan_name = rs.getString("clan_name");

				final int clan_id = CharObjidTable.get().clanObjid(clan_name);
				if (clan_id != 0) {
					final int item_id = rs.getInt("item_id");
					// final String item_name = rs.getString("item_name");
					final long count = rs.getLong("count");
					// final int is_equipped = rs.getInt("is_equipped");
					final int enchantlvl = rs.getInt("enchantlvl");
					final int is_id = rs.getInt("is_id");
					final int durability = rs.getInt("durability");
					final int charge_count = rs.getInt("charge_count");
					final int remaining_time = rs.getInt("remaining_time");
					Timestamp last_used = null;
					try {
						last_used = rs.getTimestamp("last_used");
					} catch (final Exception e) {
						last_used = null;
					}
					final int bless = rs.getInt("bless");
					final int attr_enchant_kind = rs.getInt("attr_enchant_kind");
					final int attr_enchant_level = rs.getInt("attr_enchant_level");
					final String gamno = rs.getString("gamno");
					final String creater_name = rs.getString("creater_name");
					final int extra_random = rs.getInt("extra_random");

					final L1ItemInstance item = new L1ItemInstance();
					item.setId(objid);

					final L1Item itemTemplate = ItemTable.get().getTemplate(item_id);
					if (itemTemplate == null) {
						// 無該物品資料 移除
						errorItem(objid);
						continue;
					}
					item.setItem(itemTemplate);
					item.setCount(count);
					item.setEquipped(false);
					item.setEnchantLevel(enchantlvl);
					item.setIdentified(is_id != 0 ? true : false);
					item.set_durability(durability);
					item.setChargeCount(charge_count);
					item.setRemainingTime(remaining_time);
					item.setLastUsed(last_used);
					item.setBless(bless);
					item.setAttrEnchantKind(attr_enchant_kind);
					item.setAttrEnchantLevel(attr_enchant_level);
					item.setGamNo(gamno);
					item.set_creater_name(creater_name);
					item.set_extra_random(extra_random);

					addItem(clan_name, item);
					i++;

				} else {
					deleteItem(clan_name);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入血盟倉庫物件清單資料數量: " + _itemList.size() + "/" + i + "(" + timer.get() + "ms)");
	}

	/**
	 * 刪除錯誤物品資料
	 * 
	 * @param objid
	 */
	private static void errorItem(final int objid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM `clan_warehouse` WHERE `id`=?");
			pstm.setInt(1, objid);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 建立資料
	 * 
	 * @param accName
	 * @param item
	 */
	private static void addItem(final String clan_name, final L1ItemInstance item) {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(clan_name);
		if (list == null) {
			list = new CopyOnWriteArrayList<L1ItemInstance>();
			if (!list.contains(item)) {
				list.add(item);
			}

		} else {
			if (!list.contains(item)) {
				list.add(item);
			}
		}
		// 將物品加入世界
		if (World.get().findObject(item.getId()) == null) {
			World.get().storeObject(item);
		}
		_itemList.put(clan_name, list);
	}

	/**
	 * 刪除遺失資料
	 * 
	 * @param objid
	 */
	private static void deleteItem(final String clan_name) {
		final CopyOnWriteArrayList<L1ItemInstance> list = _itemList.remove(clan_name);
		if (list != null) {
			// 移出世界
			for (final L1ItemInstance item : list) {
				World.get().removeObject(item);
			}
		}

		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `clan_warehouse` WHERE `clan_name`=?");
			ps.setString(1, clan_name);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 傳回血盟倉庫數據
	 * 
	 * @return
	 */
	@Override
	public CopyOnWriteArrayList<L1ItemInstance> loadItems(final String clan_name) {
		final CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(clan_name);
		if (list != null) {
			return list;
		}
		return null;
	}

	/**
	 * 刪除血盟倉庫資料(完整)
	 * 
	 * @param account_name
	 */
	@Override
	public void delUserItems(final String clan_name) {
		deleteItem(clan_name);
	}

	/**
	 * 該血盟倉庫是否有指定數據
	 * 
	 * @param clan_name
	 * @param objid
	 * @param count
	 * @return
	 */
	@Override
	public boolean getUserItems(final String clan_name, final int objid, final int count) {
		final CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(clan_name);
		if (list != null) {
			if (list.size() <= 0) {
				return false;
			}
			for (final L1ItemInstance item : list) {
				if (item.getId() == objid) {
					if (item.getCount() >= count) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 加入血盟倉庫數據
	 */
	@Override
	public void insertItem(final String clan_name, final L1ItemInstance item) {
		addItem(clan_name, item);

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("INSERT INTO `clan_warehouse` SET `id`=?,"
					+ "`clan_name`=?,`item_id`= ?,`item_name`=?,`count`=?,"
					+ "`is_equipped`=0,`enchantlvl`=?,`is_id`=?,`durability`=?,"
					+ "`charge_count`=?,`remaining_time`=?,`last_used`=?,`bless`=?,"
					+ "`attr_enchant_kind`=?,`attr_enchant_level`=?,`gamno`=?,"
					+ "`creater_name`=?,`extra_random`=?");

			int i = 0;
			pstm.setInt(++i, item.getId());
			pstm.setString(++i, clan_name);
			pstm.setInt(++i, item.getItemId());
			pstm.setString(++i, item.getItem().getName());
			pstm.setLong(++i, item.getCount());
			pstm.setInt(++i, item.getEnchantLevel());
			pstm.setInt(++i, item.isIdentified() ? 1 : 0);
			pstm.setInt(++i, item.get_durability());
			pstm.setInt(++i, item.getChargeCount());
			pstm.setInt(++i, item.getRemainingTime());
			pstm.setTimestamp(++i, item.getLastUsed());
			pstm.setInt(++i, item.getBless());
			pstm.setInt(++i, item.getAttrEnchantKind());
			pstm.setInt(++i, item.getAttrEnchantLevel());
			pstm.setString(++i, item.getGamNo());
			pstm.setString(++i, item.get_creater_name());
			pstm.setInt(++i, item.get_extra_random());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 血盟倉庫資料更新(物品數量)
	 * 
	 * @param item
	 */
	@Override
	public void updateItem(final L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `clan_warehouse` SET `count`=? WHERE `id`=?");
			pstm.setLong(1, item.getCount());
			pstm.setInt(2, item.getId());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 血盟倉庫物品資料刪除
	 * 
	 * @param account_name
	 * @param item
	 */
	@Override
	public void deleteItem(final String clan_name, final L1ItemInstance item) {
		final CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(clan_name);
		if (list != null) {
			list.remove(item);

			Connection con = null;
			PreparedStatement pstm = null;
			try {
				con = DatabaseFactory.get().getConnection();
				pstm = con.prepareStatement("DELETE FROM `clan_warehouse` WHERE `id`=?");
				pstm.setInt(1, item.getId());
				pstm.execute();

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}
}
