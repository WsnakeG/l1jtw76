package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.DeShopItem;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 虛擬商店買賣物品
 * 
 * @author dexc
 */
public class DeShopItemTable {

	private static final Log _log = LogFactory.getLog(DeShopItemTable.class);

	private static final ArrayList<DeShopItem> _itemList = new ArrayList<DeShopItem>();

	private static int _buyCount = 0;

	private static DeShopItemTable _instance;

	public static DeShopItemTable get() {
		if (_instance == null) {
			_instance = new DeShopItemTable();
		}
		return _instance;
	}

	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `de_shopds`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int id = rs.getInt("id");
				final int item_id = rs.getInt("item_id");
				int price = rs.getInt("price");
				if (price <= 0) {
					price = 1;
				}
				final int sellcount = rs.getInt("sellcount");
				final int buycount = rs.getInt("buycount");

				final int enchantlvl = rs.getInt("enchantlvl");
				if (check_item(item_id)) {
					final DeShopItem de = new DeShopItem();
					de.set_id(id);
					de.set_item_id(item_id);
					de.set_price(price);
					de.set_sellcount(sellcount);
					if (buycount > 0) {
						_buyCount += 1;
					}
					de.set_buycount(buycount);
					de.set_enchantlvl(enchantlvl);

					_itemList.add(de);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private boolean check_item(final int itemId) {
		final L1Item itemTemplate = ItemTable.get().getTemplate(itemId);
		if (itemTemplate == null) {
			// 無該物品資料 移除
			errorItem(itemId);
			return false;
		}
		return true;
	}

	/**
	 * 刪除錯誤物品資料
	 * 
	 * @param objid
	 */
	private static void errorItem(final int itemid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM `de_shopds` WHERE `item_id`=?");
			pstm.setInt(1, itemid);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private final static Random _random = new Random();

	/**
	 * 隨機虛擬商店買賣物品清單
	 * 
	 * @param deInstance
	 */
	public void getItems(final L1DeInstance deInstance) {
		final SetDeShopItem de_shop = new SetDeShopItem(deInstance);
		GeneralThreadPool.get().schedule(de_shop, 250);
	}

	class SetDeShopItem implements Runnable {

		private final L1DeInstance _deInstance;

		public SetDeShopItem(final L1DeInstance deInstance) {
			_deInstance = deInstance;
		}

		@Override
		public void run() {
			final Map<L1ItemInstance, Integer> sellList = new HashMap<L1ItemInstance, Integer>();
			final Map<Integer, int[]> buyList = new HashMap<Integer, int[]>();
			try {
				final int count = _random.nextInt(7) + 1;// 賣出物品數量

				// 加入賣出物品
				while (sellList.size() < count) {
					if (!_deInstance.isShop()) {
						break;
					}
					final int index = _random.nextInt(_itemList.size());
					final DeShopItem iter = _itemList.get(index);
					// 賣出數量大於0
					if (iter.get_sellcount() != 0) {
						boolean isError = false;
						for (final L1ItemInstance srcItem : sellList.keySet()) {
							if (iter.get_item_id() == srcItem.getItemId()) {
								isError = true;// 這東西已經在賣了
							}
						}
						if (!isError) {
							final L1ItemInstance item = ItemTable.get().createItem(iter.get_item_id());
							if (item != null) {
								// 物品可堆疊
								if (item.getItem().isStackable()) {
									item.setCount(_random.nextInt(iter.get_sellcount()) + 1);

								} else {
									item.setCount(iter.get_sellcount());
								}

								item.setIdentified(true);
								item.setBless(item.getItem().getBless());
								item.setEnchantLevel(iter.get_enchantlvl());

								int price = 0;
								if (iter.get_price() > 10000) {
									switch (_random.nextInt(11)) {
									case 0:
										price = (int) (iter.get_price() * 0.80);
										break;
									case 1:
										price = (int) (iter.get_price() * 0.82);
										break;
									case 2:
										price = (int) (iter.get_price() * 0.84);
										break;
									case 3:
										price = (int) (iter.get_price() * 0.86);
										break;
									case 4:
										price = (int) (iter.get_price() * 0.88);
										break;
									case 5:
										price = (int) (iter.get_price() * 0.90);
										break;
									case 6:
										price = (int) (iter.get_price() * 0.92);
										break;
									case 7:
										price = (int) (iter.get_price() * 0.94);
										break;
									case 8:
										price = (int) (iter.get_price() * 0.96);
										break;
									case 9:
										price = (int) (iter.get_price() * 0.90);
										break;
									default:
										price = iter.get_price();
										break;
									}
									String t = String.valueOf(price);
									t = t.substring(0, t.length() - 3) + "000";
									price = Integer.parseInt(t);

								} else {
									price = iter.get_price();
								}
								sellList.put(item, price);

								if (World.get().findObject(item.getId()) == null) {
									World.get().storeObject(item);
								}

								if (iter.get_buycount() > 0) {
									// 回收
									if (_random.nextInt(100) < 10) {
										final int itemid = iter.get_item_id();
										final L1Item src = ItemTable.get().getTemplate(itemid);
										if (src != null) {
											final int[] info = new int[3];
											if (iter.get_price() > 10000) {
												switch (_random.nextInt(11)) {
												case 0:
													info[0] = (int) (iter.get_price() * 0.60);
													break;
												case 1:
													info[0] = (int) (iter.get_price() * 0.62);
													break;
												case 2:
													info[0] = (int) (iter.get_price() * 0.64);
													break;
												case 3:
													info[0] = (int) (iter.get_price() * 0.66);
													break;
												case 4:
													info[0] = (int) (iter.get_price() * 0.68);
													break;
												case 5:
													info[0] = (int) (iter.get_price() * 0.70);
													break;
												case 6:
													info[0] = (int) (iter.get_price() * 0.72);
													break;
												case 7:
													info[0] = (int) (iter.get_price() * 0.74);
													break;
												case 8:
													info[0] = (int) (iter.get_price() * 0.76);
													break;
												case 9:
													info[0] = (int) (iter.get_price() * 0.78);
													break;
												default:
													info[0] = (int) (iter.get_price() * 0.79);
													break;
												}
												String t = String.valueOf(info[0]);
												t = t.substring(0, t.length() - 3) + "000";
												info[0] = Integer.parseInt(t);

											} else {
												info[0] = (int) (iter.get_price() * 0.70);
											}
											info[1] = iter.get_enchantlvl();
											info[2] = iter.get_buycount();
											buyList.put(itemid, info);
										}
									}
								}
							}
						}
					}
				}

				// 30%產生回收清單
				if ((_random.nextInt(100) < 30) && (_buyCount != 0)) {
					final int buyListcount = _random.nextInt(3) + 1;// 回收數量
					int i = 50;
					while (buyList.size() < buyListcount) {
						if (!_deInstance.isShop()) {
							break;
						}
						final int index = _random.nextInt(_itemList.size());
						final DeShopItem iter = _itemList.get(index);
						// 回收數量大於0
						if (iter.get_buycount() != 0) {
							boolean isError = false;
							for (final L1ItemInstance srcItem : sellList.keySet()) {
								if (iter.get_item_id() == srcItem.getItemId()) {
									isError = true;
								}
							}
							if (!isError) {
								final int itemid = iter.get_item_id();
								final L1Item src = ItemTable.get().getTemplate(itemid);
								if (src != null) {
									final int[] info = new int[3];
									if (iter.get_price() > 10000) {
										switch (_random.nextInt(11)) {
										case 0:
											info[0] = (int) (iter.get_price() * 0.60);
											break;
										case 1:
											info[0] = (int) (iter.get_price() * 0.62);
											break;
										case 2:
											info[0] = (int) (iter.get_price() * 0.64);
											break;
										case 3:
											info[0] = (int) (iter.get_price() * 0.66);
											break;
										case 4:
											info[0] = (int) (iter.get_price() * 0.68);
											break;
										case 5:
											info[0] = (int) (iter.get_price() * 0.70);
											break;
										case 6:
											info[0] = (int) (iter.get_price() * 0.72);
											break;
										case 7:
											info[0] = (int) (iter.get_price() * 0.74);
											break;
										case 8:
											info[0] = (int) (iter.get_price() * 0.76);
											break;
										case 9:
											info[0] = (int) (iter.get_price() * 0.78);
											break;
										default:
											info[0] = (int) (iter.get_price() * 0.79);
											break;
										}
										String t = String.valueOf(info[0]);
										t = t.substring(0, t.length() - 3) + "000";
										info[0] = Integer.parseInt(t);

									} else {
										info[0] = (int) (iter.get_price() * 0.70);
									}
									info[1] = iter.get_enchantlvl();
									info[2] = iter.get_buycount();
									buyList.put(itemid, info);
								}
							}

						} else {
							i--;
						}
						if (i <= 0) {
							_log.warn("找不到可以回收的物品，中斷物品搜尋！");
							break;
						}
					}
				}

				if (sellList.size() > 0) {
					_deInstance.sellList(sellList);
				}

				if (buyList.size() > 0) {
					_deInstance.buyList(buyList);
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				sellList.clear();
				buyList.clear();
			}
		}
	}
}
