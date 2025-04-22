package com.lineage.server.clientpackets;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemRestrictionsTable;
import com.lineage.server.datatables.PetTypeTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1HierarchInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1PetType;
import com.lineage.server.world.World;

/**
 * 要求給予物品
 * 
 * @author daien
 */
public class C_GiveItem extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_GiveItem.class);

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

			if (pc.isPrivateShop()) { // 商店村模式
				return;
			}

			final int targetId = readD();
			final int x = readH();
			final int y = readH();

			final int itemObjId = readD();

			long count = readD();
			if (count > Integer.MAX_VALUE) {
				count = Integer.MAX_VALUE;
			}
			count = Math.max(0, count);

			final L1Object object = World.get().findObject(targetId);
			if ((object == null) || !(object instanceof L1NpcInstance)) {
				return;
			}

			// 不可丟到祭司身上
			if (object instanceof L1HierarchInstance) {
				return;
			}

			final L1NpcInstance target = (L1NpcInstance) object;
			if (!isNpcItemReceivable(target.getNpcTemplate())) {
				return;
			}
			// 目標的背包
			final L1Inventory targetInv = target.getInventory();

			final L1Inventory inv = pc.getInventory();
			final L1ItemInstance item = inv.getItem(itemObjId);

			if (item == null) {
				return;
			}
			if (item.getCount() <= 0) {
				return;
			}

			if (item.isEquipped()) {
				// \f1你不能夠將轉移已經裝備的物品。
				pc.sendPackets(new S_ServerMessage(141));
				return;
			}

			if (!item.getItem().isTradable()) {
				// \f1%0%d是不可轉移的…
				pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
				return;
			}
			if (item.getBless() >= 128) { // 封印装備
				// \f1%0%d是不可轉移的…
				pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
				return;
			}

			if (item.get_time() != null) {
				// \f1%0%d是不可轉移的…
				pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
				return;
			}
			if (ItemRestrictionsTable.RESTRICTIONS.contains(Integer.valueOf(item.getItemId()))) {
				// \f1%0%d是不可轉移的…
				pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
				return;
			}
			// 可丟給怪物的道具清單 by terry0412
			if ((pc.getAccessLevel() < 200) && !ConfigAlt.GIVE_ITEM_LIST.contains(item.getItemId())) {
				// \f1%0%d是不可轉移的…
				pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
				return;
			}

			final int pcx = pc.getX();
			final int pcy = pc.getY();
			if ((Math.abs(pcx - x) >= 3) || (Math.abs(pcy - y) >= 3)) {
				// \f1距離太遠不能給東西。
				pc.sendPackets(new S_ServerMessage(142));
				return;
			}

			// 寵物(已經召喚出來)
			for (final Object petObject : pc.getPetList().values()) {
				if (petObject instanceof L1PetInstance) {
					final L1PetInstance pet = (L1PetInstance) petObject;
					if (item.getId() == pet.getItemObjId()) {
						// \f1%0%d是不可轉移的…
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
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
			if (pc.getDoll(item.getId()) != null) {
				// 1,181：這個魔法娃娃目前正在使用中。
				pc.sendPackets(new S_ServerMessage(1181));
				return;
			}
			// 取回娃娃
			if (pc.get_power_doll() != null) {
				if (pc.get_power_doll().getItemObjId() == item.getId()) {
					// 1,181：這個魔法娃娃目前正在使用中。
					pc.sendPackets(new S_ServerMessage(1181));
					return;
				}
			}

			if (targetInv.checkAddItem(item, count) != L1Inventory.OK) {
				// 對方的負重太重，無法再給予。
				pc.sendPackets(new S_ServerMessage(942));
				return;
			}

			// 給予的物件
			final L1ItemInstance getItem = inv.tradeItem(item, count, targetInv);
			target.onGetItem(getItem);
			target.turnOnOffLight();
			pc.turnOnOffLight();
			WriteLogTxt.Recording("給予物品記錄", "人物:" + pc.getName() + "給予NPC（" + target.getNpcId() + "）物品"
					+ item.getLogName() + " ItmeID:" + item.getItemId() + " 物品OBJID:" + item.getId());
			// 寵物相關判斷
			final L1PetType petType = PetTypeTable.getInstance().get(target.getNpcTemplate().get_npcId());
			if ((petType == null) || target.isDead()) {
				return;
			}
			// 抓寵物的判斷
			if (getItem.getItemId() == petType.getItemIdForTaming()) {
				tamePet(pc, target);
			}
			// 給予的對象 是寵物
			if (target instanceof L1PetInstance) {
				final L1PetInstance tgPet = (L1PetInstance) target;
				// pc.sendPackets(new S_PetInventory(tgPet, true));

				// 寵物進化所需道具編號 by terry0412
				if (petType.canEvolve() && (getItem.getItemId() == petType.getItemIdForEvolving())
				// 移除進化所需道具
						&& targetInv.consumeItem(petType.getItemIdForEvolving(), 1)) {
					evolvePet(pc, tgPet);
					return; // added by terry0412
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	private final static String receivableImpls[] = new String[] { "L1Npc", // NPC
			"L1Monster", // 怪物
			"L1Guardian", // 守護神
			"L1Teleporter", // 傳送師
			"L1Guard"// 警衛
	};

	/**
	 * 是否是可以給予物品的物件
	 * 
	 * @param npc
	 * @return
	 */
	private boolean isNpcItemReceivable(final L1Npc npc) {
		for (final String impl : receivableImpls) {
			if (npc.getImpl().equals(impl)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 抓寵物的判斷
	 * 
	 * @param pc
	 * @param target
	 */
	private void tamePet(final L1PcInstance pc, final L1NpcInstance target) {
		if ((target instanceof L1PetInstance) || (target instanceof L1SummonInstance)) {
			return;
		}

		int petcost = 0;
		final Object[] petList = pc.getPetList().values().toArray();
		if (petList.length > 2) {
			// 489：你無法一次控制那麼多寵物。
			pc.sendPackets(new S_ServerMessage(489));
			return;
		}
		for (final Object pet : petList) {
			final int nowpetcost = ((L1NpcInstance) pet).getPetcost();
			petcost += nowpetcost;
		}

		int charisma = pc.getCha();
		if (pc.isCrown()) { // 君主
			charisma += 6;

		} else if (pc.isElf()) { // エルフ
			charisma += 12;

		} else if (pc.isWizard()) { // WIZ
			charisma += 6;

		} else if (pc.isDarkelf()) { // DE
			charisma += 6;

		} else if (pc.isDragonKnight()) { // ドラゴンナイト
			charisma += 6;

		} else if (pc.isIllusionist()) { // イリュージョニスト
			charisma += 6;
		}
		charisma -= petcost;

		if (charisma <= 0) {
			// 489：你無法一次控制那麼多寵物。
			pc.sendPackets(new S_ServerMessage(489));
			return;
		}

		final L1PcInventory inv = pc.getInventory();
		if (inv.getSize() < 180) {
			if (isTamePet(target)) {
				final L1ItemInstance petamu = inv.storeItem(40314, 1); // 項圈
				if (petamu != null) {
					new L1PetInstance(target, pc, petamu.getId());
					pc.sendPackets(new S_ItemName(petamu));
				}

			} else {
				// 馴養失敗。
				pc.sendPackets(new S_ServerMessage(324));
			}
		}
	}

	/**
	 * 進化寵物的判斷
	 * 
	 * @param pc
	 * @param pet
	 */
	private void evolvePet(final L1PcInstance pc, final L1PetInstance pet) {
		final L1PcInventory inv = pc.getInventory();
		final L1ItemInstance petamu = inv.getItem(pet.getItemObjId());

		// pet.getInventory().consumeItem(40070, 1);// 進化果實移除

		// 寵物可使用進化道具的進化等級 by terry0412
		final int level = ConfigAlt.PET_EVOLVE_LEVEL;
		/*
		 * if (pet.getNpcId() == 71019) {// 淘氣龍 level = 70; } if (pet.getNpcId()
		 * == 71020) {// 頑皮龍 level = 60; }
		 */

		if ((pet.getLevel() >= level) && // Lv30以上
				(pc == pet.getMaster()) && // 自分のペット
				(petamu != null)) {

			final L1ItemInstance highpetamu = inv.storeItem(40316, 1);// 項圈
			if (highpetamu != null) {
				pet.evolvePet(highpetamu.getId());
				pc.sendPackets(new S_ItemName(highpetamu));
				inv.removeItem(petamu, 1);
			}
		}
	}

	/**
	 * 抓取寵物成功的判斷
	 * 
	 * @param npc
	 * @return
	 */
	private boolean isTamePet(final L1NpcInstance npc) {
		boolean isSuccess = false;
		final int npcId = npc.getNpcTemplate().get_npcId();
		if (npcId == 45313) { // タイガー

			final Random random = new Random();

			// HPが1/3未満で1/16の確率
			if (((npc.getMaxHp() / 3) > npc.getCurrentHp()) && (random.nextInt(16) == 15)) {
				isSuccess = true;
			}

		} else {
			if ((npc.getMaxHp() / 3) > npc.getCurrentHp()) {
				isSuccess = true;
			}
		}

		// タイガー、ラクーン、紀州犬の子犬
		if ((npcId == 45313) || (npcId == 45044) || (npcId == 45711)) {
			if (npc.isResurrect()) { // RES後はテイム不可
				isSuccess = false;
			}
		}

		return isSuccess;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
