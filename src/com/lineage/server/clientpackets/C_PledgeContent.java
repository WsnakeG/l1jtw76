package com.lineage.server.clientpackets;

import java.util.ArrayList;
import java.util.List;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.T_GameMallTable;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1DwarfForGameMallInventry;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PledgeUI;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShopItemRetrieList;
import com.lineage.server.templates.T_GameMallModel;
import com.lineage.server.templates.T_ShopWarehouseModel;
import com.lineage.server.world.World;

public class C_PledgeContent extends ClientBasePacket {

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		// 資料載入
		read(decrypt);
		final L1PcInstance pc = client.getActiveChar();

		final int data = readC();
		switch (data) {
		case 7:
			pc.sendPackets(T_GameMallTable.get().getPaketList());
			pc.updateGameMallMoney();
			break;
		case 8:
			final int un = readD();
			if (un == 1) {
				final int id = readD();
				readD();
				int buyCount = readD();
				int sumPrice = readD();
				readD();
				final int buyType = readC();

				if (buyType == 0) {
					final T_GameMallModel model = T_GameMallTable.get().getMallList(id);
					if (model == null) {
						pc.sendPackets(new S_ServerMessage(2746));
						return;
					}
					if ((model.getVipLevel() > 0) && (pc.get_vipLevel() <= 0)) {
						pc.sendPackets(new S_ServerMessage(2743));
						return;
					}

					buyCount = Math.min(Math.abs(buyCount), 100);
					sumPrice = buyCount * model.getPrice();
					if (Config.ISPOINT) {// 透過point
						int points = AccountReading.get().getPoints(pc.getAccountName());
						if (points < sumPrice) {
							pc.sendPackets(new S_ServerMessage(2742));
							return;
						}
						points -= sumPrice;
						AccountReading.get().setPoints(pc.getAccountName(), points);

					} else {// 透過44070
						if (!pc.getInventory().consumeItem(44070, sumPrice)) {
							pc.sendPackets(new S_ServerMessage(2742));
							return;
						}
					}

					final L1DwarfForGameMallInventry dwarfForGameMallInventory = pc.getDwarfForGameMall();
					synchronized (dwarfForGameMallInventory._key) {
						final L1ItemInstance itemTemp = model.getMallItem();
						if (itemTemp.isStackable()) {
							final T_ShopWarehouseModel shopWarehouseModel = new T_ShopWarehouseModel(
									IdFactory.get().nextId(), pc.getAccountName(), pc.getId(),
									itemTemp.getItemId(), itemTemp.getItem().getName(),
									(int) (itemTemp.getCount() * buyCount), itemTemp.getBless(),
									itemTemp.getEnchantLevel());
							pc.getDwarfForGameMall().insertOnLine(shopWarehouseModel);
						} else {
							T_ShopWarehouseModel shopWarehouseModel = null;
							for (int i = 0; i < buyCount; i++) {
								shopWarehouseModel = new T_ShopWarehouseModel(IdFactory.get().nextId(),
										pc.getAccountName(), pc.getId(), itemTemp.getItemId(),
										itemTemp.getItem().getName(), (int) itemTemp.getCount(),
										itemTemp.getBless(), itemTemp.getEnchantLevel());
								pc.getDwarfForGameMall().insertOnLine(shopWarehouseModel);
							}
						}
						pc.sendPackets(new S_ServerMessage(2745));
						pc.updateGameMallMoney();

						T_GameMallTable.get().insertMallRecord(pc.getId(), pc.getId(), itemTemp.getItemId(),
								itemTemp.getItem().getName(), buyCount, sumPrice);
					}

				} else if (buyType == 1) {
					final String friendName = readS();
					boolean friendIsOnline = true;

					final T_GameMallModel model = T_GameMallTable.get().getMallList(id);
					if (model == null) {
						pc.sendPackets(new S_ServerMessage(2746));
						return;
					}
					if ((model.getVipLevel() > 0) && (pc.get_vipLevel() <= 0)) {
						pc.sendPackets(new S_ServerMessage(2743));
						return;
					}

					buyCount = Math.min(Math.abs(buyCount), 100);
					sumPrice = buyCount * model.getPrice();
					if (Config.ISPOINT) {// 透過point
						int points = AccountReading.get().getPoints(pc.getAccountName());
						if (points < sumPrice) {
							pc.sendPackets(new S_ServerMessage(2742));
							return;
						}
						points -= sumPrice;
						AccountReading.get().setPoints(pc.getAccountName(), points);

					} else {// 透過44070
						if (!pc.getInventory().consumeItem(44070, sumPrice)) {
							pc.sendPackets(new S_ServerMessage(2742));
							return;
						}
					}

					L1PcInstance friend = World.get().getPlayer(friendName);
					L1DwarfForGameMallInventry dwarfForGameMallInventory = null;
					if (friend == null) {
						try {
							friend = CharacterTable.get().restoreCharacter(friendName);
						} catch (final Exception e) {
							e.printStackTrace();
						}
						friendIsOnline = false;
						dwarfForGameMallInventory = new L1DwarfForGameMallInventry(null);
					} else {
						dwarfForGameMallInventory = friend.getDwarfForGameMall();
					}

					synchronized (dwarfForGameMallInventory._key) {
						final L1ItemInstance itemTemp = model.getMallItem();
						if (itemTemp.isStackable()) {
							final T_ShopWarehouseModel shopWarehouseModel = new T_ShopWarehouseModel(
									IdFactory.get().nextId(), friend.getAccountName(), pc.getId(),
									itemTemp.getItemId(), itemTemp.getItem().getName(),
									(int) (itemTemp.getCount() * buyCount), itemTemp.getBless(),
									itemTemp.getEnchantLevel());
							if (friendIsOnline) {
								dwarfForGameMallInventory.insertOnLine(shopWarehouseModel);
							} else {
								dwarfForGameMallInventory.insertOffLine(shopWarehouseModel);
							}
						} else {
							T_ShopWarehouseModel shopWarehouseModel = null;
							for (int i = 0; i < buyCount; i++) {
								shopWarehouseModel = new T_ShopWarehouseModel(IdFactory.get().nextId(),
										friend.getAccountName(), pc.getId(), itemTemp.getItemId(),
										itemTemp.getItem().getName(), (int) itemTemp.getCount(),
										itemTemp.getBless(), itemTemp.getEnchantLevel());
								if (friendIsOnline) {
									dwarfForGameMallInventory.insertOnLine(shopWarehouseModel);
								} else {
									dwarfForGameMallInventory.insertOffLine(shopWarehouseModel);
								}
							}
						}
						pc.sendPackets(new S_ServerMessage(2745));
						pc.updateGameMallMoney();

						T_GameMallTable.get().insertMallRecord(pc.getId(), friend.getId(),
								itemTemp.getItemId(), itemTemp.getItem().getName(), buyCount, sumPrice);
					}

				} else if (buyType == 2) {
					final T_GameMallModel model = T_GameMallTable.get().getMallList(id);
					if (model == null) {
						pc.sendPackets(new S_ServerMessage(2746));
						return;
					}
					readS();

				} else if (buyType == 3) {
					readS();
				}
			}
			break;
		case 9:
			pc.sendPackets(new S_ShopItemRetrieList(pc));
			break;
		case 10:
			final int size = readD();
			if (size > 0) {
				final L1DwarfForGameMallInventry dwarfForGameMallInventory = pc.getDwarfForGameMall();
				synchronized (dwarfForGameMallInventory._key) {
					final List<T_ShopWarehouseModel> items = dwarfForGameMallInventory.getWareHouseList();
					final List<T_ShopWarehouseModel> giveItems = new ArrayList<T_ShopWarehouseModel>(size);
					final int[] allIndex = new int[size];
					int index = -1;
					for (int i = 0; i < size; i++) {
						index = readD();
						readD();
						if (index >= items.size()) {
							giveItems.clear();
							return;
						}
						allIndex[i] = index;
						giveItems.add(items.get(index));
					}
					L1ItemInstance item = null;
					final L1PcInventory pcInv = pc.getInventory();
					for (final T_ShopWarehouseModel model : giveItems) {
						if (pcInv.checkAddItem(model.getItemId(), model.getItemCount()) == 0) {
							item = ItemTable.get().createItem(model.getItemId());
							item.setCount(model.getItemCount());
							item.setEnchantLevel(model.getEnchantLevel());
							item.setBless(model.getItemBless());
							pcInv.storeItem(item);
							dwarfForGameMallInventory.deleteItems(model);
						}
					}
				}
			}
			break;
		case 15:// 寫入血盟公告
			if (pc.getClanid() == 0) {
				return;
			}
			if (pc.getClan().getLeaderId() != pc.getId()) {
				return;
			}
			/* 讀取公告字串封包 */
			final String announce = readS();
			/* 取出L1Clan物件 */
			final L1Clan clan = pc.getClan();
			/* 更新公告 */
			clan.setClanShowNote(announce);
			/* 更新L1Clan物件 */
			ClanReading.get().updateClan(clan);
			/* 送出血盟公告封包 */
			//pc.sendPackets(new S_PacketBox(S_PacketBox.HTML_PLEDGE_REALEASE_ANNOUNCE, announce));
			// 更新血盟公告數據包
			pc.sendPackets(new S_PledgeUI(clan, S_PledgeUI.ES_PLEDGE_NOTI));
			
			break;
		case 16:// 寫入個人備註
			if (pc.getClanid() == 0) {
				return;
			}
			// 讀取備註字串封包
			final String notes = readS();
			/* 更新角色備註資料 */
			pc.setClanMemberNotes(notes);
			/* 送出寫入備註更新封包 */
			//pc.sendPackets(new S_Pledge(pc.getName(), notes));
			// 更新血盟個人備註資訊
			pc.sendPackets(new S_PledgeUI(pc));
			break;

		default:
			break;
		}
	}
}
