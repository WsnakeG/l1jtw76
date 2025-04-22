package com.lineage.server.clientpackets;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.Shutdown;
import com.lineage.server.datatables.ItemRestrictionsTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1HierarchInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_DoActionShop;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1PrivateShopBuyList;
import com.lineage.server.templates.L1PrivateShopSellList;

/**
 * 要求開設個人商店
 * 
 * @author daien
 */
public class C_Shop extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Shop.class);

	/*
	 * public C_Shop() { } public C_Shop(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc == null) { // 角色為空
				return;
			}

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}

			if (pc.isDead()) { // 死亡
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			// 該地圖是否可擺設商店 by terry0412
			if ((pc.getMap().isUsableShop() <= 0)
					|| (ItemTable.get().getTemplate(pc.getMap().isUsableShop()) == null)) {
				// 無法在此開設個人商店。
				pc.sendPackets(new S_ServerMessage(876));
				return;
			}

			if (Shutdown.SHUTDOWN) {// 關機狀態不允許使用
				pc.sendPackets(new S_SystemMessage("目前服務器準備關機狀態，無法使用交易功能。"));
				return;
			}

			final ArrayList<L1PrivateShopSellList> sellList = pc.getSellList();
			final ArrayList<L1PrivateShopBuyList> buyList = pc.getBuyList();
			sellList.clear();
			buyList.clear();
			L1ItemInstance checkItem;
			boolean tradable = true;

			final int type = readC();
			if (type == 0) { // 開始
				final int sellTotalCount = readH();// 出售道具
				for (int i = 0; i < sellTotalCount; i++) {
					final int sellObjectId = readD();
					final int sellPrice = Math.max(0, readD());
					if (sellPrice <= 0) {
						_log.error("要求開設個人商店傳回金幣小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					final int sellCount = Math.max(0, readD());
					if (sellCount <= 0) {
						_log.error("要求開設個人商店傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					// 取引可能なアイテムかチェック
					checkItem = pc.getInventory().getItem(sellObjectId);
					if (!checkItem.getItem().isTradable()) {
						tradable = false;
						// 1497 此道具無法在[個人商店]上販售。
						pc.sendPackets(new S_ServerMessage(1497));
					}

					if (checkItem.get_time() != null) {
						// 1497 此道具無法在[個人商店]上販售。
						pc.sendPackets(new S_ServerMessage(1497));
						tradable = false;
					}

					if (checkItem.isEquipped()) {
						// \f1你不能夠將轉移已經裝備的物品。
						pc.sendPackets(new S_ServerMessage(141));
						return;
					}

					if (ItemRestrictionsTable.RESTRICTIONS.contains(Integer.valueOf(checkItem.getItemId()))) {
						// \f1%0%d是不可轉移的…
						pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getNameId()));
						return;
					}

					// 取回寵物列表
					final Object[] petlist = pc.getPetList().values().toArray();
					for (final Object petObject : petlist) {
						if (petObject instanceof L1PetInstance) {
							final L1PetInstance pet = (L1PetInstance) petObject;
							if (checkItem.getId() == pet.getItemObjId()) {
								tradable = false;
								// 1,187：寵物項鍊正在使用中。
								pc.sendPackets(new S_ServerMessage(1187));
								return;
							}
						}
						// 祭司
						if (petObject instanceof L1HierarchInstance) {
							final L1HierarchInstance hierarch = (L1HierarchInstance) petObject;
							pc.getPetList().remove(hierarch.getId());
							hierarch.deleteMe();
						}
					}

					// 取回娃娃
					if (pc.getDoll(checkItem.getId()) != null) {
						// 1,181：這個魔法娃娃目前正在使用中。
						pc.sendPackets(new S_ServerMessage(1181));
						return;
					}
					// 取回娃娃
					if (pc.get_power_doll() != null) {
						if (pc.get_power_doll().getItemObjId() == checkItem.getId()) {
							// 1,181：這個魔法娃娃目前正在使用中。
							pc.sendPackets(new S_ServerMessage(1181));
							return;
						}
					}

					final L1PrivateShopSellList pssl = new L1PrivateShopSellList();
					pssl.setItemObjectId(sellObjectId);
					pssl.setSellPrice(sellPrice);
					pssl.setSellTotalCount(sellCount);
					sellList.add(pssl);
				}

				final int buyTotalCount = readH();// 買入道具
				for (int i = 0; i < buyTotalCount; i++) {
					final int buyObjectId = readD();
					final int buyPrice = Math.max(0, readD());
					if (buyPrice <= 0) {
						_log.error("要求買入道具傳回金幣小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					final int buyCount = Math.max(0, readD());
					if (buyCount <= 0) {
						_log.error("要求買入道具傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					// 取引可能なアイテムかチェック
					checkItem = pc.getInventory().getItem(buyObjectId);

					if (checkItem.getCount() <= 0) {
						continue;
					}

					if (!checkItem.getItem().isTradable()) {
						tradable = false;
						// 1497 此道具無法在[個人商店]上販售
						pc.sendPackets(new S_ServerMessage(1497));
					}

					if (checkItem.getBless() >= 128) { // 封印的装備
						// 1497 此道具無法在[個人商店]上販售
						pc.sendPackets(new S_ServerMessage(1497));
						return;
					}

					if (checkItem.isEquipped()) {
						// \f1你不能夠將轉移已經裝備的物品。
						pc.sendPackets(new S_ServerMessage(141));
						return;
					}

					// 取回寵物列表
					final Object[] petlist = pc.getPetList().values().toArray();
					for (final Object petObject : petlist) {
						if (petObject instanceof L1PetInstance) {
							final L1PetInstance pet = (L1PetInstance) petObject;
							if (checkItem.getId() == pet.getItemObjId()) {
								tradable = false;
								// 1,187：寵物項鍊正在使用中。
								pc.sendPackets(new S_ServerMessage(1187));
								return;
							}
						}
						// 祭司
						if (petObject instanceof L1HierarchInstance) {
							final L1HierarchInstance hierarch = (L1HierarchInstance) petObject;
							if (checkItem.getId() == hierarch.getId()) {
								tradable = false;
								pc.sendPackets(
										new S_ServerMessage(166, checkItem.getItem().getName(), "無法交易。"));
								return;
							}
						}
					}

					// 取回娃娃
					if (pc.getDoll(checkItem.getId()) != null) {
						// 1,181：這個魔法娃娃目前正在使用中。
						pc.sendPackets(new S_ServerMessage(1181));
						return;
					}
					// 取回娃娃
					if (pc.get_power_doll() != null) {
						if (pc.get_power_doll().getItemObjId() == checkItem.getId()) {
							// 1,181：這個魔法娃娃目前正在使用中。
							pc.sendPackets(new S_ServerMessage(1181));
							return;
						}
					}

					final L1PrivateShopBuyList psbl = new L1PrivateShopBuyList();
					psbl.setItemObjectId(buyObjectId);
					psbl.setBuyPrice(buyPrice);
					psbl.setBuyTotalCount(buyCount);
					buyList.add(psbl);
				}
				if (!tradable) { // 取引不可能なアイテムが含まれている場合、個人商店終了
					sellList.clear();
					buyList.clear();
					pc.setPrivateShop(false);
					pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
					return;
				}

				if (pc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
					pc.removeSkillEffect(L1SkillId.SHAPE_CHANGE);
				}

				final byte[] chat = readByte();
				pc.setShopChat(chat);
				pc.setPrivateShop(true);
				pc.sendPacketsAll(new S_DoActionShop(pc.getId(), chat));
				int SelectedPolyNum = 0;
				try {
					SelectedPolyNum = Integer.parseInt(
							new String(chat, CLIENT_LANGUAGE_CODE).split("tradezone")[1].substring(0, 1));
				} catch (final Exception e) {
					e.printStackTrace();
				}
				// System.out.println("id"+SelectedPolyNum);
				L1PolyMorph.doPolyPraivateShop(pc, SelectedPolyNum);

			} else if (type == 1) { // 終了
				sellList.clear();
				buyList.clear();
				pc.setPrivateShop(false);
				pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
				L1PolyMorph.undoPolyPrivateShop(pc);
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
