package com.lineage.server.templates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.DatabaseFactoryLogin;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.GamblingSet;
// import com.lineage.data.event.PowerItemHoleSet;
import com.lineage.data.event.gambling.GamblingNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopTable;
// import com.lineage.server.datatables.lock.CharItemPowerHoleReading;
import com.lineage.server.datatables.lock.DwarfShopReading;
import com.lineage.server.datatables.lock.GamblingReading;
import com.lineage.server.datatables.lock.ServerCnInfoReading;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.event.GamblingTime;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 人物其他項清單列表
 * 
 * @author DaiEn
 */
public class L1PcOtherList {

	private static final Log _log = LogFactory.getLog(L1PcOtherList.class);

	private L1PcInstance _pc;

	public Map<Integer, L1ItemInstance> DELIST;// 虛擬商店買入清單(ORDERID/指定的物品)

	private Map<Integer, L1ShopItem> _cnList;// 購買奇怪的商人物品清單(ORDERID/指定的物品數據)

	private Map<Integer, L1ItemInstance> _cnSList;// 購買託售商人物品清單(ORDERID/指定的物品數據)

	private Map<Integer, GamblingNpc> _gamList;// 購買食人妖精競賽票清單(ORDERID/指定的參賽者數據)

	private Map<Integer, L1Gambling> _gamSellList;// 賣出食人妖精競賽票清單(物品OBJID/妖精競賽紀錄緩存)

	private Map<Integer, L1IllusoryInstance> _illusoryList;// 召喚分身清單(分身OBJID/分身數據)

	private Map<Integer, L1TeleportLoc> _teleport;// NPC傳送點緩存(傳點排序編號/傳點數據)

	private Map<Integer, Integer> _uplevelList;// 屬性重置清單(模式/增加數值總合)

	private Map<Integer, String[]> _shiftingList;// 裝備轉移人物清單(帳戶中人物排序編號/String[]{OBJID/人物名稱})

	private Map<Integer, L1ItemInstance> _sitemList;// 裝備交換清單(ORDERID/指定的物品)

	private Map<Integer, Integer> _sitemList2;// 裝備交換清單(ORDERID/指定的物品ITEMID)

	public Map<Integer, L1Quest> QUESTMAP;// 暫存任務清單

	public Map<Integer, L1ShopS> SHOPXMAP;// 暫存出售紀錄清單

	public ArrayList<Integer> ATKNPC;// 暫存需要攻擊的NPCID

	private int[] _is;// 暫存人物原始素質改變

	public Map<Integer, int[]> SHOPLIST;// 商品領回暫時清單

	public L1PcOtherList(final L1PcInstance pc) {
		_pc = pc;
		DELIST = new HashMap<Integer, L1ItemInstance>();

		_cnList = new HashMap<Integer, L1ShopItem>();
		_cnSList = new HashMap<Integer, L1ItemInstance>();
		_gamList = new HashMap<Integer, GamblingNpc>();
		_gamSellList = new HashMap<Integer, L1Gambling>();
		_illusoryList = new HashMap<Integer, L1IllusoryInstance>();

		_teleport = new HashMap<Integer, L1TeleportLoc>();
		_uplevelList = new HashMap<Integer, Integer>();
		_shiftingList = new HashMap<Integer, String[]>();
		_sitemList = new HashMap<Integer, L1ItemInstance>();
		_sitemList2 = new HashMap<Integer, Integer>();

		QUESTMAP = new HashMap<Integer, L1Quest>();
		SHOPXMAP = new HashMap<Integer, L1ShopS>();
		ATKNPC = new ArrayList<Integer>();
		SHOPLIST = new HashMap<Integer, int[]>();
	}

	/**
	 * 清空全部資料
	 */
	public void clearAll() {
		try {
			ListMapUtil.clear(DELIST);
			ListMapUtil.clear(_cnList);
			ListMapUtil.clear(_cnSList);
			ListMapUtil.clear(_gamList);
			ListMapUtil.clear(_gamSellList);
			ListMapUtil.clear(_illusoryList);
			ListMapUtil.clear(_teleport);
			ListMapUtil.clear(_uplevelList);
			ListMapUtil.clear(_shiftingList);
			ListMapUtil.clear(_sitemList);
			ListMapUtil.clear(_sitemList2);
			ListMapUtil.clear(QUESTMAP);
			ListMapUtil.clear(SHOPXMAP);
			ListMapUtil.clear(ATKNPC);
			ListMapUtil.clear(SHOPLIST);

			DELIST = null;// 虛擬商店買入清單
			_cnList = null;
			_cnSList = null;
			_gamList = null;
			_gamSellList = null;
			_illusoryList = null;
			_teleport = null;
			_uplevelList = null;
			_shiftingList = null;
			_sitemList = null;
			_sitemList2 = null;
			QUESTMAP = null;
			SHOPXMAP = null;
			ATKNPC = null;
			SHOPLIST = null;

			_is = null;

			_pc = null;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 裝備交換清單

	/**
	 * 傳回裝備交換清單(可換)
	 * 
	 * @return _sitemList2
	 */
	public Map<Integer, Integer> get_sitemList2() {
		return _sitemList2;
	}

	/**
	 * 加入裝備交換清單(可換)
	 * 
	 * @param key
	 * @param value
	 */
	public void add_sitemList2(final Integer key, final Integer value) {
		_sitemList2.put(key, value);
	}

	/**
	 * 清空裝備交換清單(可換)
	 */
	public void clear_sitemList2() {
		_sitemList2.clear();
	}

	// TODO 裝備交換清單

	/**
	 * 傳回裝備交換清單(準備)
	 * 
	 * @return _sitemList
	 */
	public Map<Integer, L1ItemInstance> get_sitemList() {
		return _sitemList;
	}

	/**
	 * 加入裝備交換清單(準備)
	 * 
	 * @param key
	 * @param value
	 */
	public void add_sitemList(final Integer key, final L1ItemInstance value) {
		_sitemList.put(key, value);
	}

	/**
	 * 清空裝備交換清單(準備)
	 */
	public void clear_sitemList() {
		_sitemList.clear();
	}

	// TODO 帳戶人物清單

	/**
	 * 傳回帳戶人物清單
	 * 
	 * @return _shiftingList
	 */
	public Map<Integer, String[]> get_shiftingList() {
		return _shiftingList;
	}

	/**
	 * 加入帳戶人物清單
	 * 
	 * @param key
	 * @param value
	 */
	public void add_shiftingList(final Integer key, final String[] value) {
		_shiftingList.put(key, value);
	}

	/**
	 * 移出帳戶人物清單
	 * 
	 * @param key
	 */
	public void remove_shiftingList(final Integer key) {
		_shiftingList.remove(key);
	}

	/**
	 * 讀取人物列表<BR>
	 * 將資料置入MAP中
	 */
	public void set_shiftingList() {
		try {
			_shiftingList.clear();
			Connection conn = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {

				conn = DatabaseFactory.get().getConnection();
				pstm = conn.prepareStatement("SELECT * FROM `characters` WHERE `account_name`=?");
				pstm.setString(1, _pc.getAccountName());
				rs = pstm.executeQuery();

				int key = 0;
				while (rs.next()) {
					final int objid = rs.getInt("objid");
					final String name = rs.getString("char_name");
					if (!name.equalsIgnoreCase(_pc.getName())) {
						key++;
						add_shiftingList(key, new String[] { String.valueOf(objid), name });
					}
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(conn);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO

	/**
	 * 傳回分身
	 * 
	 * @return _illusoryList
	 */
	public Map<Integer, L1IllusoryInstance> get_illusoryList() {
		return _illusoryList;
	}

	/**
	 * 加入分身清單
	 * 
	 * @param key
	 * @param value
	 */
	public void addIllusoryList(final Integer key, final L1IllusoryInstance value) {
		_illusoryList.put(key, value);
	}

	/**
	 * 移出分身清單
	 * 
	 * @param key
	 */
	public void removeIllusoryList(final Integer key) {
		try {
			if (_illusoryList == null) {
				return;
			}
			if (_illusoryList.get(key) != null) {
				_illusoryList.remove(key);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 傳送

	/**
	 * 傳送點緩存
	 * 
	 * @param teleportMap
	 */
	public void teleport(final HashMap<Integer, L1TeleportLoc> teleportMap) {
		try {
			ListMapUtil.clear(_teleport);
			_teleport.putAll(teleportMap);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 傳送點緩存
	 * 
	 * @return _teleport
	 */
	public Map<Integer, L1TeleportLoc> teleportMap() {
		return _teleport;
	}

	/**
	 * 賣出全部物品
	 * 
	 * @param sellallMap
	 */
	public void sellall(final Map<Integer, Integer> sellallMap) {
		try {
			int getprice = 0;
			for (final Integer integer : sellallMap.keySet()) {
				final L1ItemInstance item = _pc.getInventory().getItem(integer);
				if (item != null) {
					if (item.getBless() >= 128) { // 封印的装備
						continue;
					}
					final int key = item.getItemId();
					final int price = ShopTable.get().getPrice(key);
					final Integer count = sellallMap.get(integer);
					final long remove = _pc.getInventory().removeItem(integer, count);
					if (remove == count) {
						getprice += (price * count);
					}
				}
			}

			if (getprice > 0) {
				// 物品(金幣)
				final L1ItemInstance item = ItemTable.get().createItem(40308);
				item.setCount(getprice);
				createNewItem(item);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 購物清單

	/**
	 * 清空全部買入資料
	 */
	public void clear() {
		try {
			ListMapUtil.clear(_cnList);
			ListMapUtil.clear(_gamList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 奇岩賭場

	/**
	 * 複製賣出資料(清空舊資料)
	 * 
	 * @param sellList
	 */
	public void set_gamSellList(final Map<Integer, L1Gambling> sellList) {
		try {
			ListMapUtil.clear(_gamSellList);
			_gamSellList.putAll(sellList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 賣出食人妖精競賽票
	 * 
	 * @param element
	 * @param index
	 */
	public void get_sellGam(final int objid, final int count) {
		try {
			final L1Gambling element = _gamSellList.get(objid);
			if (element == null) {
				return;
			}
			final long countx = (long) (element.get_rate() * GamblingSet.GAMADENA) * count;
			final long remove = _pc.getInventory().removeItem(objid, count);
			if (remove == count) {
				final int outcount = element.get_outcount() - count;
				if (outcount < 0) {
					return;
				}
				element.set_outcount(outcount);
				GamblingReading.get().updateGambling(element.get_id(), outcount);
				// 奇岩賭場 下注使用物品編號(預設金幣40308)
				final L1ItemInstance item = ItemTable.get().createItem(GamblingSet.ADENAITEM);
				item.setCount(countx);
				createNewItem(item);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 加入購買食人妖精競賽票
	 * 
	 * @param element
	 * @param index
	 */
	public void add_gamList(final GamblingNpc element, final int index) {
		_gamList.put(new Integer(index), element);
	}

	/**
	 * 購買食人妖精競賽票
	 * 
	 * @param gamMap
	 */
	public void get_buyGam(final Map<Integer, Integer> gamMap) {
		try {
			for (final Integer integer : gamMap.keySet()) {
				final int index = integer;
				final int count = gamMap.get(integer);
				get_gamItem(index, count);
			}
			ListMapUtil.clear(_gamList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void get_gamItem(final int index, final int count) {
		try {
			if (count <= 0) {
				return;
			}
			final GamblingNpc element = _gamList.get(index);
			if (element == null) {
				return;
			}

			final int npcid = element.get_npc().getNpcId();// 比賽者NPCID
			final int no = GamblingTime.get_gamblingNo();// 比賽場次編號
			final long adena = GamblingSet.GAMADENA * count;// 需要數量
			final long srcCount = _pc.getInventory().countItems(GamblingSet.ADENAITEM);// 現有數量

			// 奇岩賭場 下注使用物品編號(預設金幣40308)檢查
			if (srcCount >= adena) {
				// 食人妖精競賽票
				final L1ItemInstance item = ItemTable.get().createItem(40309);
				// 容量重量確認
				if (_pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
					// 扣除奇岩賭場 下注使用物品編號(預設金幣40308)
					_pc.getInventory().consumeItem(GamblingSet.ADENAITEM, adena);

					item.setCount(count);
					item.setGamNo(no + "-" + npcid);
					createNewItem(item);
					element.add_adena(adena);

				} else {
					// \f1當你負擔過重時不能交易。
					_pc.sendPackets(new S_ServerMessage(270));
				}

			} else {
				final L1Item item = ItemTable.get().getTemplate(GamblingSet.ADENAITEM);
				final long nc = adena - srcCount;
				// 337：\f1%0不足%s。
				_pc.sendPackets(new S_ServerMessage(337, item.getNameId() + "(" + nc + ")"));
				// 337：\f1%0不足%s。
				// this._pc.sendPackets(new S_ServerMessage(189));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 託售管理員

	/**
	 * 加入購買託售管理員物品
	 */
	public void add_cnSList(final L1ItemInstance shopItem, final int index) {
		_cnSList.put(new Integer(index), shopItem);
	}

	/**
	 * 買入託售管理員物品
	 */
	public void get_buyCnS(final Map<Integer, Integer> cnMap) {
		try {
			final int itemid_cn = 44070;// 貨幣 44070
			for (final Integer integer : cnMap.keySet()) {
				final int count = cnMap.get(integer);
				if (count > 0) {
					// 取回賣出視窗對應排序編號物品
					final L1ItemInstance element = _cnSList.get(integer.intValue());
					final L1ShopS shopS = DwarfShopReading.get().getShopS(element.getId());
					if ((element != null) && (shopS != null)) {
						if (shopS.get_end() != 0) {// 物品非出售中
							continue;
						}
						if (shopS.get_item() == null) {// 物品設置為空
							continue;
						}
						// 取回貨幣數量
						final L1ItemInstance itemT = _pc.getInventory().checkItemX(itemid_cn,
								shopS.get_adena());
						if (itemT == null) {
							// 337：\f1%0不足%s。 0_o"
							_pc.sendPackets(new S_ServerMessage(337, "貨幣"));
							continue;
						}

						shopS.set_end(1);// 設置資訊為售出
						shopS.set_item(null);
						DwarfShopReading.get().updateShopS(shopS);
						DwarfShopReading.get().deleteItem(element.getId());

						_pc.getInventory().consumeItem(itemid_cn, shopS.get_adena());
						_pc.getInventory().storeTradeItem(element);
						_pc.sendPackets(new S_ServerMessage(403, element.getLogName())); // 获得0%。
						// createNewItem(element);
					}
				}
			}
			ListMapUtil.clear(_cnList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 奇怪的商人

	/**
	 * 加入購買奇怪的商人物品
	 */
	public void add_cnList(final L1ShopItem shopItem, final int index) {
		_cnList.put(new Integer(index), shopItem);
	}

	/**
	 * 買入奇怪的商人物品
	 */
	public void get_buyCn(final Map<Integer, Integer> cnMap) {
		try {
			for (final Integer integer : cnMap.keySet()) {
				final int index = integer;
				final int count = cnMap.get(integer);
				if (count > 0) {
					final L1ShopItem element = _cnList.get(index);
					if (element != null) {
						get_cnItem(element, count);
					}
				}
			}
			bonusCheck(_pc.get_consume_point());
			_pc.set_consume_point(0);
			_pc.set_temp_adena(0);
			ListMapUtil.clear(_cnList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 消費滿額
	 * 
	 * @param count
	 */
	private void bonusCheck(final long count) {
		if (count == 0) {
			return;
		}
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `shop_cn_bonus` ORDER BY `need_counts` DESC";
			ps = co.prepareStatement(sqlstr);
			rs = ps.executeQuery();

			final Timestamp now_time = new Timestamp(System.currentTimeMillis());
			while (rs.next()) {
				final Timestamp end_time = rs.getTimestamp("end_time");
				// 消費好禮 時間限制
				if ((end_time != null) && (now_time.after(end_time))) {
					continue;
				}
				// 消費好禮 贈禮條件
				final int need_counts = rs.getInt("need_counts");
				if (count < need_counts) {
					continue;
				}
				final int item_id = rs.getInt("bonus_item_id");
				final int counts = rs.getInt("bonus_item_counts");
				final L1ItemInstance item = ItemTable.get().createItem(item_id);
				if (item == null) {
					_log.error("給予物件失敗 原因: 指定編號物品不存在(" + item_id + ")");
				} else {
					if (item.isStackable()) {
						item.setCount(counts);
						item.setIdentified(true);
						_pc.getInventory().storeItem(item);
					} else {
						item.setIdentified(true);
						_pc.getInventory().storeItem(item);
					}
					_pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 获得0%。
				}
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
	}

	/**
	 * 賣出奇怪的商人物品
	 */
	public void get_sellCn(final Map<Integer, Integer> cnMap) {
		try {
			long adena_count = 0;
			for (final Integer integer : cnMap.keySet()) {
				final int objid = integer;
				final int count = cnMap.get(integer);
				if (count > 0) {
					final L1ItemInstance findItem = _pc.getInventory().getItem(objid);
					if ((findItem != null) && (findItem.getCount() >= count)) {
						final L1ShopItem element = _cnList.get(findItem.getId());
						if (element.getPurchasingPrice() <= 0) {
							continue;
						}
						if (element != null) {
							final long adenaCount = _pc.getInventory().removeItem(findItem, count)
									* element.getPurchasingPrice();

							adena_count += adenaCount;

							toGmMsg(findItem.getItem(), adenaCount, false);
						}
					}
				}
			}
			if (adena_count > 0) {
				// 獲得天寶
				CreateNewItem.createNewItem(_pc, _pc.get_temp_adena(), adena_count);
			}
			_pc.set_temp_adena(0);
			ListMapUtil.clear(_cnList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void get_cnItem(final L1ShopItem element, final int count) {
		try {
			if (_pc.get_temp_adena() == 0) {
				return;
			}
			final int itemid_cn = _pc.get_temp_adena();// 貨幣
			final int itemid = element.getItemId();// 物品編號
			final int getCount = element.getPackCount() * count;// 給予數量
			final long adenaCount = element.getPrice() * count;// 花費
			if (element.getPrice() <= 0) {
				return;
			}
			
			// 物品檢查(貨幣 44070)
			final long srcCount = _pc.getInventory().countItems(itemid_cn);
			// 修復寫法 (避免負數洗法) by terry0412
			if ((srcCount >= adenaCount) && _pc.getInventory().consumeItem(itemid_cn, adenaCount)) {
				// 找回物品
				final L1Item itemtmp = ItemTable.get().getTemplate(itemid);

				toGmMsg(itemtmp, adenaCount, true);

				if (itemtmp.isStackable()) {
					// 找回物品
					final L1ItemInstance item = ItemTable.get().createItem(itemid);
					item.setCount(getCount);
					createNewItem(item);

				} else {
					for (int i = 0; i < getCount; i++) {
						// 找回物品
						final L1ItemInstance item = ItemTable.get().createItem(itemid);
						item.setIdentified(true);
						/*
						 * if (PowerItemHoleSet.START) { // 凹槽誕生 switch
						 * (item.getItem().getUseType()) { case 1:// 武器 case
						 * 2:// 盔甲 case 18:// T恤 case 19:// 斗篷 case 20:// 手套
						 * case 21:// 靴 case 22:// 頭盔 case 25:// 盾牌 final
						 * L1ItemPowerHole_name power = new
						 * L1ItemPowerHole_name();
						 * power.set_item_obj_id(item.getId());
						 * power.set_hole_count(1); power.set_hole_1(0);
						 * power.set_hole_2(0); power.set_hole_3(0);
						 * power.set_hole_4(0); power.set_hole_5(0);
						 * item.set_power_name_hole(power);
						 * CharItemPowerHoleReading.get().storeItem(item.getId()
						 * , item.get_power_name_hole()); break; } }
						 */
						if (element.getEnchantLevel() != 0) {
							item.setEnchantLevel(element.getEnchantLevel());
						}
						createNewItem(item);
					}
				}
				// record, 44070
				if (_pc.get_temp_adena() == 44070) {
					_pc.set_consume_point(_pc.get_consume_point() + adenaCount);
				}

			} else {
				// 原始物件資料
				final L1Item tgItem = ItemTable.get().getTemplate(_pc.get_temp_adena());
				final long nc = adenaCount - srcCount;
				// 337：\f1%0不足%s。
				_pc.sendPackets(new S_ServerMessage(337, tgItem.getNameId() + "(" + nc + ")"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 後處理

	/**
	 * 通知GM
	 */
	private void toGmMsg(final L1Item itemtmp, final long adenaCount, final boolean buy) {
		try {
			ServerCnInfoReading.get().create(_pc, itemtmp, adenaCount);
			final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
			for (final L1PcInstance tgpc : allPc) {
				if (tgpc.isGm()) {
					final StringBuilder topc = new StringBuilder();
					if (buy) {
						topc.append(
								"人物:" + _pc.getName() + " 買入:" + itemtmp.getNameId() + " 花費天寶:" + adenaCount);
					} else {
						topc.append(
								"人物:" + _pc.getName() + " 賣出:" + itemtmp.getNameId() + " 獲得天寶:" + adenaCount);
						_log.info(topc.toString());
					}
					tgpc.sendPackets(new S_ServerMessage(166, topc.toString()));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 給予物件的處理
	 * 
	 * @param pc
	 * @param item
	 */
	private void createNewItem(final L1ItemInstance item) {
		try {
			_pc.getInventory().storeItem(item);
			_pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 获得0%。

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 屬性重置處理

	/**
	 * 屬性重置
	 * 
	 * @param key 模式<BR>
	 *            0 升級點數/萬能藥點數 可分配數量<BR>
	 *            1 力量 (原始)<BR>
	 *            2 敏捷 (原始)<BR>
	 *            3 體質 (原始)<BR>
	 *            4 精神 (原始)<BR>
	 *            5 智力 (原始)<BR>
	 *            6 魅力 (原始)<BR>
	 *            7 力量 +-<BR>
	 *            8 敏捷 +-<BR>
	 *            9 體質 +-<BR>
	 *            10 精神 +-<BR>
	 *            11 智力 +-<BR>
	 *            12 魅力 +-<BR>
	 *            13 目前分配點數模式 0:升級點數 1:萬能藥點數<BR>
	 * @param value 增加數值總合
	 */
	public void add_levelList(final int key, final int value) {
		_uplevelList.put(key, value);
	}

	/**
	 * 屬性重置清單
	 * 
	 * @return
	 */
	public Map<Integer, Integer> get_uplevelList() {
		return _uplevelList;
	}

	/**
	 * 指定數值參數
	 * 
	 * @param key
	 * @return
	 */
	public Integer get_uplevelList(final int key) {
		return _uplevelList.get(key);
	}

	/**
	 * 清空屬性重置處理清單
	 */
	public void clear_uplevelList() {
		ListMapUtil.clear(_uplevelList);
	}

	/**
	 * 暫存人物原始素質改變
	 * 
	 * @param is
	 */
	public void set_newPcOriginal(final int[] is) {
		_is = is;
	}

	/**
	 * 傳回暫存人物原始素質改變
	 * 
	 * @return
	 */
	public int[] get_newPcOriginal() {
		return _is;
	}
}
