package com.lineage.server.clientpackets;

import java.util.Calendar;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.InnTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcActionTable;
import com.lineage.server.datatables.lock.AuctionBoardReading;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.datatables.sql.AuctionBoardTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.model.npc.action.L1NpcAction;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1AuctionBoardTmp;
import com.lineage.server.templates.L1House;
import com.lineage.server.templates.L1Inn;
import com.lineage.server.world.World;

/**
 * 要求物件對話視窗數量選取結果
 * 
 * @author dexc
 */
public class C_Amount extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Amount.class);

	/*
	 * public C_Amount() { } public C_Amount(final byte[] abyte0, final ClientExecutor client) { super(abyte0); try { this.start(abyte0, client); } catch (final Exception e) {
	 * _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}
			if (pc.isDead()) { // 死亡
				return;
			}
			if (pc.isTeleport()) { // 傳送中
				return;
			}

			final int objectId = readD();// 對話物件OBJID

			int amount = Math.max(0, readD());// 數量
			if (amount <= 0) {
				return;
			}

			readC();// BYTE

			final String s = readS();// 命令文字

			if (amount > Integer.MAX_VALUE) {
				amount = Integer.MAX_VALUE;
			}

			final L1NpcInstance npc = (L1NpcInstance) World.get().findObject(objectId);
			if (npc == null) {
				return;
			}

			String s1 = "";
			String s2 = "";
			try {
				final StringTokenizer stringtokenizer = new StringTokenizer(s);
				s1 = stringtokenizer.nextToken();
				s2 = stringtokenizer.nextToken();

			} catch (final NoSuchElementException e) {
				s1 = "";
				s2 = "";
			}

			if (s1.equalsIgnoreCase("agapply")) { // 盟屋拍賣佈告出價
				final String pcName = pc.getName();
				final Collection<L1AuctionBoardTmp> boardList = AuctionBoardReading.get().getAuctionBoardTableList().values();
				for (final L1AuctionBoardTmp board : boardList) {
					if (pcName.equalsIgnoreCase(board.getBidder())) {
						// 523 已經參與其他血盟小屋拍賣。
						pc.sendPackets(new S_ServerMessage(523));
						return;
					}
				}

				final int houseId = Integer.valueOf(s2);
				final L1AuctionBoardTmp board = AuctionBoardReading.get().getAuctionBoardTable(houseId);
				if (board != null) {
					// 傳回目前售價
					final long nowPrice = board.getPrice();
					// 傳回目前競標者OBJID
					final int nowBidderId = board.getBidderId();

					// 檢查金幣
					final L1ItemInstance item = pc.getInventory().checkItemX(L1ItemId.ADENA, amount);
					if (item != null) {
						// 移除金幣
						if (pc.getInventory().consumeItem(L1ItemId.ADENA, amount)) {
							// 盟屋拍賣公告欄資料更新
							board.setPrice(amount);
							board.setBidder(pcName);
							board.setBidderId(pc.getId());
							AuctionBoardReading.get().updateAuctionBoard(board);

							if (nowBidderId != 0) {
								// 前競標者金額退回
								final L1PcInstance bidPc = (L1PcInstance) World.get().findObject(nowBidderId);
								if (bidPc != null) { // 人物在線上
									bidPc.getInventory().storeItem(L1ItemId.ADENA, nowPrice);
									bidPc.sendPackets(new S_ServerMessage(525, String.valueOf(nowPrice)));

								} else { // 人物不在線上
									CharItemsReading.get().getAdenaCount(nowBidderId, nowPrice);
								}
							}

						} else {
							// \f1金幣不足。
							pc.sendPackets(new S_ServerMessage(189));
						}

					} else {
						// \f1金幣不足。
						pc.sendPackets(new S_ServerMessage(189));
					}
				}

			} else if (s1.equalsIgnoreCase("agsell")) { // 出售
				if (npc.getNpcId() == 70535) {// 拍賣管理者
					if (npc.ACTION != null) {
						if (amount <= 0) {
							return;
						}
						npc.ACTION.action(pc, npc, s, amount);
						return;
					}
				}
				final int houseId = Integer.valueOf(s2);
				final AuctionBoardTable boardTable = new AuctionBoardTable();
				final L1AuctionBoardTmp board = new L1AuctionBoardTmp();
				if (board != null) {
					// 競売掲示板に新規書き込み
					board.setHouseId(houseId);
					final L1House house = HouseReading.get().getHouseTable(houseId);
					board.setHouseName(house.getHouseName());
					board.setHouseArea(house.getHouseArea());
					final TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
					final Calendar cal = Calendar.getInstance(tz);
					cal.add(Calendar.DATE, 5); // 5日後
					cal.set(Calendar.MINUTE, 0); // 分、秒は切り捨て
					cal.set(Calendar.SECOND, 0);
					board.setDeadline(cal);
					board.setPrice(amount);
					board.setLocation(house.getLocation());
					board.setOldOwner(pc.getName());
					board.setOldOwnerId(pc.getId());
					board.setBidder("");
					board.setBidderId(0);
					boardTable.insertAuctionBoard(board);

					house.setOnSale(true); // 競売中に設定
					house.setPurchaseBasement(true); // 地下盟屋設置為未購入
					HouseReading.get().updateHouse(house); // DBに書き込み
				}

			}

			// 旅館NPC
			int npcId = npc.getNpcId();
			if (npcId == 70070 || npcId == 70019 || npcId == 70075 || npcId == 70012 || npcId == 70031 || npcId == 70084 || npcId == 70065 || npcId == 70054 || npcId == 70096) {

				if (pc.getInventory().checkItem(L1ItemId.ADENA, (300 * amount))) { // 所需金幣
																					// =
																					// 鑰匙價格(300)
																					// *
																					// 鑰匙數量(amount)
					L1Inn inn = InnTable.getInstance().getTemplate(npcId, pc.getInnRoomNumber());
					if (inn != null) {
						Timestamp dueTime = inn.getDueTime();
						if (dueTime != null) { // 再次判斷房間租用時間
							Calendar cal = Calendar.getInstance();
							if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) { // 租用時間未到
								// 此房間被搶走了...
								pc.sendPackets(new S_NPCTalkReturn(npcId, ""));
								return;
							}
						}
						// 租用時間 4小時
						Timestamp ts = new Timestamp(System.currentTimeMillis() + (60 * 60 * 4 * 1000));
						// 登入旅館資料
						L1ItemInstance item = ItemTable.get().createItem(40312); // 旅館鑰匙
						if (item != null) {
							item.setKeyId(item.getId()); // 鑰匙編號
							item.setInnNpcId(npcId); // 旅館NPC
							item.setHall(pc.checkRoomOrHall()); // 判斷租房間 or 會議室
							item.setDueTime(ts); // 租用時間
							item.setCount(amount); // 鑰匙數量

							inn.setKeyId(item.getKeyId()); // 旅館鑰匙
							inn.setLodgerId(pc.getId()); // 租用人
							inn.setHall(pc.checkRoomOrHall()); // 判斷租房間 or 會議室
							inn.setDueTime(ts); // 租用時間
							// DB更新
							InnTable.getInstance().updateInn(inn);

							pc.getInventory().consumeItem(L1ItemId.ADENA, (300 * amount)); // 扣除金幣

							// 給予鑰匙並登入鑰匙資料
							L1Inventory inventory;
							if (pc.getInventory().checkAddItem(item, amount) == L1Inventory.OK) {
								inventory = pc.getInventory();
							} else {
								inventory = World.get().getInventory(pc.getLocation());
							}
							inventory.storeItem(item);

							if (InnKeyTable.checkey(item)) {// 鑰匙資料已存在
								InnKeyTable.DeleteKey(item);
								InnKeyTable.StoreKey(item);
							} else {
								InnKeyTable.StoreKey(item);
							}

							String itemName = (item.getItem().getName() + item.getInnKeyName());
							if (amount > 1) {
								itemName = (itemName + " (" + amount + ")");
							}
							pc.sendPackets(new S_ServerMessage(143, npc.getName(), itemName)); // \f1%0%s 給你 %1%o 。
							String[] msg = { npc.getName() };
							// 要一起使用房間的話，請把鑰匙給其他人，往旁邊的樓梯上去即可。
							pc.sendPackets(new S_NPCTalkReturn(npcId, "inn4", msg));

						}
					}
				} else {
					String[] msg = { npc.getName() };
					pc.sendPackets(new S_NPCTalkReturn(npcId, "inn3", msg)); // 對不起，你手中的金幣不夠哦！
				}

				if (npc.ACTION != null) {
					if (amount <= 0) {
						return;
					}
					npc.ACTION.action(pc, npc, s, amount);
					return;
				}

				final L1NpcAction action = NpcActionTable.getInstance().get(s, pc, npc);
				if (action != null) {
					final L1NpcHtml result = action.executeWithAmount(s, pc, npc, amount);
					if (result != null) {
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), result));
					}
					return;
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
