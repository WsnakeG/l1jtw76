package com.lineage.data.event.SoulQueen;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.lineage.server.IdFactory;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.SoulTowerTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldItem;

/**
 * 屍魂塔副本 刷怪線程
 * 
 * @author jeLiu
 */
public class SoulTowerThread extends Thread {
	private static Logger _log = Logger.getLogger(SoulTowerThread.class.getName());
	short mapId;
	L1PcInstance pc;
	L1DoorInstance door0;
	ArrayList<L1NpcInstance> list = new ArrayList<L1NpcInstance>();

	public SoulTowerThread(final int mapId, final L1PcInstance pc) {
		this.mapId = (short) mapId;
		this.pc = pc;
	}

	@Override
	public void run() {
		System.out.println("屍魂塔副本線程開始 地圖：" + mapId + " 玩家：" + pc.getName());
		try {
			final long begin = System.currentTimeMillis();
			//L1Teleport.teleport(pc, 32869, 32923, mapId, 2, true);
			L1Teleport.teleport(pc, 32843, 32931, mapId, 2, true);
			Thread.sleep(100);
			pc.sendPackets(new S_PacketBox(S_PacketBox.SOULTOWERSTART, 1800));
			pc.sendPackets(new S_PacketBox(S_PacketBox.GUI_VISUAL_EFFECT, 2));
			pc.sendPackets(new S_PacketBox(S_PacketBox.GUI_VISUAL_EFFECT, 7));
			spawn(new L1Location(32846, 32849, mapId), 111481, 1);//詭異的商人^歐汀
			spawn(new L1Location(32801, 32812, mapId), 111481, 1);//詭異的商人^歐汀
			spawn(new L1Location(32756, 32872, mapId), 111481, 1);//詭異的商人^歐汀

			// 關卡1的門
			final L1DoorInstance door1 = spawnDoor(0, 12632, 32843, 32878, mapId, 0, 1, false, 32843, 32846);
			door1.close();
			//spawn(new L1Location(32849, 32923, mapId), 190031, 10);//屍魂的怨靈-黑暗鬥士
			spawn(new L1Location(32844, 32905, mapId), 111500, 6);//屍魂的怨靈-地獄犬
			spawn(new L1Location(32844, 32905, mapId), 111501, 6);//屍魂的怨靈-阿西塔基奧
			
			spawn(new L1Location(32849, 32923, mapId), 111493, 5);//屍魂的怨靈 (范德)
			spawn(new L1Location(32838, 32917, mapId), 111494, 5);//屍魂的怨靈 (士兵)
			spawn(new L1Location(32823, 32929, mapId), 111495, 5);//屍魂的怨靈 (荒龍)
			spawn(new L1Location(32849, 32923, mapId), 111496, 5);//屍魂的怨靈 (弓箭手)
			spawn(new L1Location(32842, 32927, mapId), 111497, 5);//屍魂的怨靈 (黑妖)
			spawn(new L1Location(32853, 32922, mapId), 111498, 5);//屍魂的怨靈 (蜥蜴人)
			spawn(new L1Location(32836, 32913, mapId), 111499, 5);//屍魂的怨靈 (強盜)

			Thread.sleep(10000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18344"));
			Thread.sleep(3000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18327"));
			Thread.sleep(3000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18328"));
			list = spawn(new L1Location(32843, 32894, mapId), 111502, 2);//屍魂的怨靈 (渾沌)-死掉變珠子

			isKillNpc(list);
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18329"));
			spawn(new L1Location(32843, 32886, mapId), 111502, 2);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32843, 32886, mapId), 111500, 6);//屍魂的怨靈-地獄犬
			list = spawn(new L1Location(32843, 32885, mapId), 111506, 1);//惡靈守門人-第一關卡守門人

			isKillNpc(list);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			door1.open();
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18338"));
			// 關卡2的門
			final L1DoorInstance door2 = spawnDoor(0, 6336, 32842, 32848, mapId, 0, 1, false, 32841, 32844);
			door2.close();
			Thread.sleep(3000);
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18347"));
			list = spawn(new L1Location(32844, 32862, mapId), 111502, 4);//屍魂的怨靈 (渾沌)-死掉變珠子
			list.addAll(spawn(new L1Location(32844, 32862, mapId), 111505, 4));//屍魂的怨靈-深紅飛龍
			list.addAll(spawn(new L1Location(32844, 32862, mapId), 111504, 4));//屍魂的怨靈-石怪
			spawn(new L1Location(32844, 32862, mapId), 111500, 6);//屍魂的怨靈-地獄犬

			isKillNpc(list);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18330"));
			spawn(new L1Location(32859, 32858, mapId), 111502, 2);//屍魂的怨靈 (渾沌)-死掉變珠子
			list = spawn(new L1Location(32859, 32858, mapId), 111507, 1);//執行者巴羅卡勒
			
			isKillNpc(list);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18333"));
			door2.open();
			// 關卡3的門
			final L1DoorInstance door3 = spawnDoor(0, 12754, 32820, 32812, mapId, 0, 1, false, 32811, 32812, 1);
			// 關卡4的門
			final L1DoorInstance door4 = spawnDoor(0, 12754, 32820, 32813, mapId, 0, 1, false, 32812, 32813, 1);
			// 關卡5的門
			final L1DoorInstance door5 = spawnDoor(0, 12754, 32820, 32814, mapId, 0, 1, false, 32813, 32814, 1);
			door3.close();
			door4.close();
			door5.close();
			Thread.sleep(5000);
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18331"));
			spawn(new L1Location(32846, 32814, mapId), 111505, 2);//屍魂的怨靈-深紅飛龍
			spawn(new L1Location(32846, 32814, mapId), 111500, 3);//屍魂的怨靈-地獄犬
			spawn(new L1Location(32846, 32814, mapId), 111502, 2);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32864, 32804, mapId), 111505, 2);//屍魂的怨靈-深紅飛龍
			spawn(new L1Location(32864, 32804, mapId), 111501, 3);//屍魂的怨靈-阿西塔基奧
			spawn(new L1Location(32864, 32804, mapId), 111502, 2);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32850, 32801, mapId), 111505, 2);//屍魂的怨靈-深紅飛龍
			spawn(new L1Location(32850, 32801, mapId), 111500, 3);//屍魂的怨靈-地獄犬
			spawn(new L1Location(32850, 32801, mapId), 111502, 2);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32831, 32799, mapId), 111505, 2);//屍魂的怨靈-深紅飛龍
			spawn(new L1Location(32831, 32799, mapId), 111501, 3);//屍魂的怨靈-阿西塔基奧
			spawn(new L1Location(32831, 32799, mapId), 111502, 2);//屍魂的怨靈 (渾沌)-死掉變珠子
			list = spawn(new L1Location(32833, 32809, mapId), 111502, 1);//屍魂的怨靈 (渾沌)-死掉變珠子

			isKillNpc(list);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			door3.open();
			door4.open();
			door5.open();
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18348"));
			// 關卡6的門
			final L1DoorInstance door6 = spawnDoor(0, 12711, 32790, 32815, mapId, 0, 1, false, 32814, 32818, 1);
			door6.close();
			spawn(new L1Location(32800, 32816, mapId), 111502, 2);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32800, 32816, mapId), 111504, 3);//屍魂的怨靈-石怪
			list = spawn(new L1Location(32800, 32816, mapId), 111508, 1);//混亂的威裡諾

			isKillNpc(list);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			door6.open();
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18340"));
			ArrayList<L1DoorInstance> doorlist = new ArrayList<L1DoorInstance>();
			for (int i = 32769; i <= 32777; i++) {
				final L1DoorInstance door7 = spawnDoor(0, 12754, i, 32829, mapId, 0, 1, false, i, i, 0);
				door7.close();
				doorlist.add(door7);
			}
			(door0 = spawnDoor(0, 12711, 32760, 32819, mapId, 0, 1, false, 32818, 32822, 1)).close();
			Thread.sleep(3000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18349"));
			spawn(new L1Location(32776, 32818, mapId), 111504, 4);//屍魂的怨靈-石怪
			spawn(new L1Location(32776, 32818, mapId), 111501, 4);//屍魂的怨靈-阿西塔基奧
			spawn(new L1Location(32776, 32818, mapId), 111502, 4);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32776, 32818, mapId), 111505, 4);//屍魂的怨靈-深紅飛龍
			list = spawn(new L1Location(32776, 32818, mapId), 111500, 4);//屍魂的怨靈-地獄犬

			isKillNpc(list);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			for (final L1DoorInstance door8 : doorlist) {
				door8.open();
			}
			doorlist = new ArrayList<L1DoorInstance>();
			for (int i = 32763; i <= 32776; i++) {
				final L1DoorInstance door7 = spawnDoor(0, 12754, i, 32843, mapId, 0, 1, false, i, i, 0);
				door7.close();
				doorlist.add(door7);
			}
			Thread.sleep(5000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18332"));
			spawn(new L1Location(32772, 32835, mapId), 111505, 4);//屍魂的怨靈-深紅飛龍
			spawn(new L1Location(32772, 32835, mapId), 111504, 4);//屍魂的怨靈-石怪
			spawn(new L1Location(32772, 32835, mapId), 111501, 4);//屍魂的怨靈-阿西塔基奧
			spawn(new L1Location(32772, 32835, mapId), 111502, 4);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32772, 32835, mapId), 111500, 4);//屍魂的怨靈-地獄犬

			Thread.sleep(15000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			for (final L1DoorInstance door8 : doorlist) {
				door8.open();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18333"));
			doorlist = new ArrayList<L1DoorInstance>();
			for (int i = 32749; i < 32751; i++) {
				// 火堆
				final L1DoorInstance door7 = spawnDoor(0, 12754, i, 32881, mapId, 0, 1, false, i, i, 0);
				door7.close();
				doorlist.add(door7);
			}
			spawn(new L1Location(32769, 32854, mapId), 111504, 2);//屍魂的怨靈-石怪
			spawn(new L1Location(32769, 32854, mapId), 111501, 2);//屍魂的怨靈-阿西塔基奧
			spawn(new L1Location(32769, 32854, mapId), 111502, 2);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32769, 32854, mapId), 111505, 2);//屍魂的怨靈-深紅飛龍
			spawn(new L1Location(32769, 32854, mapId), 111500, 2);//屍魂的怨靈-地獄犬
			list = spawn(new L1Location(32769, 32854, mapId), 111509, 1);//百獸王卡諾圖

			isKillNpc(list);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18341"));
			for (final L1DoorInstance door8 : doorlist) {
				door8.open();
			}
			final L1DoorInstance door9 = spawnDoor(0, 12711, 32769, 32905, mapId, 0, 1, false, 32904, 32908, 1);
			door9.close();
			Thread.sleep(10000);
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18334"));
			spawn(new L1Location(32753, 32898, mapId), 111504, 6);//屍魂的怨靈-石怪
			spawn(new L1Location(32753, 32898, mapId), 111501, 6);//屍魂的怨靈-阿西塔基奧
			spawn(new L1Location(32753, 32898, mapId), 111502, 6);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32753, 32898, mapId), 111505, 6);//屍魂的怨靈-深紅飛龍
			spawn(new L1Location(32753, 32898, mapId), 111500, 10);//屍魂的怨靈-地獄犬

			Thread.sleep(15000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18335"));
			list = spawn(new L1Location(32765, 32906, mapId), 111511, 1);//惡靈守門人-BOSS關卡守門人

			isKillNpc(list);
			door9.open();
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18342"));
			Thread.sleep(3000);
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18336"));
			spawn(new L1Location(32786, 32906, mapId), 111504, 4);//屍魂的怨靈-石怪
			spawn(new L1Location(32786, 32906, mapId), 111501, 4);//屍魂的怨靈-阿西塔基奧
			spawn(new L1Location(32786, 32906, mapId), 111502, 4);//屍魂的怨靈 (渾沌)-死掉變珠子
			spawn(new L1Location(32786, 32906, mapId), 111505, 4);//屍魂的怨靈-深紅飛龍
			spawn(new L1Location(32786, 32906, mapId), 111500, 4);//屍魂的怨靈-地獄犬

			Thread.sleep(10000);
			// 惡魔: 可笑! 讓我來親自消滅你!
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18337"));
			list = spawn(new L1Location(32786, 32906, mapId), 111512, 1);// 魔王安德雷亞

			isKillNpc(list);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.GUI_VISUAL_EFFECT, 2));
			pc.sendPackets(new S_PacketBox(S_PacketBox.GUI_VISUAL_EFFECT, 7));
			//pc.sendPackets(new S_SkillSound(pc.getId(), 10274));// 煙花
			final int usertime = (int) ((System.currentTimeMillis() - begin) / 1000);
			pc.sendPackets(new S_PacketBox(S_PacketBox.SOULTOWEREND, usertime));// 消滅完畢

			SoulTowerTable.getInstance().updateRank(pc, usertime);//更新排名

			// 惡魔: 呃啊啊!!! 我是不會這樣就倒下的!
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18343"));
			Thread.sleep(2000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			// 天使: 真的好以你為榮.
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18574"));
			Thread.sleep(2000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			// 天使: 用這個也可以使屍魂塔獲得封印.
			pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18575"));
			Thread.sleep(2000);
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
			}
			for (int i = 0; i < 10; i++) {
				// 倒數10秒後以傳送術移動至村莊
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "$" + (18576 + i)));
				Thread.sleep(1000);
			}
			quitSoulTower();
		} catch (final InterruptedException e) {
		}
	}

	/**
	 * 召喚NPC
	 * 
	 * @param loc
	 * @param npcid
	 * @param count
	 * @return
	 */
	private ArrayList<L1NpcInstance> spawn(final L1Location loc, final int npcid, final int count) {
		final ArrayList<L1NpcInstance> list = new ArrayList<L1NpcInstance>();
		if (count > 1) {
			for (int i = 0; i < count; i++) {
				list.add(spawnNpc(loc, npcid, 10));
			}
		} else {
			list.add(spawnNpc(loc, npcid, 1));
		}
		return list;
	}

	/**
	 * 是否在規定時間內清除怪物
	 * 
	 * @param list
	 * @return
	 */
	private int isKillNpc(final ArrayList<L1NpcInstance> list) {
		boolean isAllKill = true;
		while (isAllKill) {
			if (!isSoulTower()) {//判斷玩家是否在屍魂塔副本
				quitSoulTower();
				return -1;
			}
			boolean isAllDeath = false;
			for (final L1NpcInstance npc : list) {
				if (!npc.isDead()) {
					break;
				}
				isAllDeath = npc.isDead();
			}
			if (isAllDeath) {
				isAllKill = false;
				return 0;
			}
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				// TODO 自動生成的 catch 塊

			}
		}
		quitSoulTower();
		return -1;
	}

	/**
	 * 判斷玩家是否在屍魂塔副本
	 * 
	 * @return 不在副本中返回false
	 * @return 在副本中返回true
	 */
	private boolean isSoulTower() {
		if (pc == null) {
			return false;
		}
		if (pc.getOnlineStatus() == 0) {
			return false;
		}
		if (pc.getMapId() != mapId) {
			return false;
		}
		return true;
	}

	/**
	 * 退出屍魂塔副本
	 */
	private void quitSoulTower() {
		//刪除副本道具
		// 240977 下層雷擊爆彈 wand.Firestorml_Magic_Wand
		// 240978 下層旋風爆彈 wand.Purificationl_Magic_Wand
		// 240979 下層戰鬥強化卷軸 Battl_Reel
		// 240980 下層防禦強化卷軸 Battt_Reel
		// 240981 下層治癒藥水 hp.UserAddHp 10 20 189
		// 240982 下層強力治癒藥水 hp.UserAddHp 60 80 197
		// 240983 下層魔力藥水 mp.UserAddMp 10 25 190
		for (int i = 240977; i <= 240983; i++) {
			final L1ItemInstance[] itemlist = pc.getInventory().findItemsId(i);
			if (itemlist != null && itemlist.length > 0) {
				for (final L1ItemInstance item : itemlist) {
					pc.getInventory().removeItem(item);
				}
			}
		}

		L1ItemInstance item = pc.getInventory().findItemId(240967);// 屍魂幣
		if (item != null) {
			pc.getInventory().removeItem(item);
		}

		// 停止初始技能狀態
		if (pc.hasSkillEffect(L1SkillId.LOWER_FLOOR_GREATER_BATTLE_SCROLL)) {//下層戰鬥強化卷軸
			pc.removeSkillEffect(L1SkillId.LOWER_FLOOR_GREATER_BATTLE_SCROLL);
		}
		if (pc.hasSkillEffect(L1SkillId.LOWER_FLOOR_GREATER_DEFENSE_SCROLL)) {//下層防禦強化卷軸
			pc.removeSkillEffect(L1SkillId.LOWER_FLOOR_GREATER_DEFENSE_SCROLL);
		}

		if (pc != null && pc.getMapId() == mapId) {
			L1Teleport.teleport(pc, 33703, 32502, (short) 4, 5, true);
		}
		for (final L1ItemInstance obj : WorldItem.get().all()) {
			if (obj.getMapId() == mapId) {
				final L1Inventory groundInventory = World.get().getInventory(obj.getX(), obj.getY(), obj.getMapId());
				groundInventory.removeItem(obj);
			}
		}
		World.get().closeMap(mapId);
		L1SoulTower.get().mapStat[(mapId - 4001)] = false;
		System.out.println("屍魂塔副本線程結束 地圖：" + mapId);
		interrupt();
	}

	/**
	 * 召喚NPC
	 * 
	 * @param loc
	 * @param npcid
	 * @param randomRange
	 * @return
	 */
	private L1NpcInstance spawnNpc(final L1Location loc, final int npcid, final int randomRange) {
		final L1Npc l1npc = NpcTable.get().getTemplate(npcid);
		L1NpcInstance field = null;
		if (l1npc == null) {
			_log.warning("召喚的NPCID:" + npcid + "不存在");
			return null;
		}
		try {
			field = NpcTable.get().newNpcInstance(npcid);
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}
		field.setId(IdFactory.get().nextId());
		field.setMap((short) loc.getMapId());
		int tryCount = 0;
		do {
			tryCount++;
			field.setX(loc.getX() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
			field.setY(loc.getY() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
			if (field.getMap().isInMap(field.getLocation()) && field.getMap().isPassable(field.getX(), field.getY())) {
				break;
			}
			try {
				Thread.sleep(2);
			} catch (final InterruptedException e) {
			}
		} while (tryCount < 50);
		if (tryCount >= 50) {
			field.getLocation().set(loc);
		}
		field.setHomeX(field.getX());
		field.setHomeY(field.getY());
		field.setHeading(5);
		field.setLightSize(l1npc.getLightSize());
		// field.setLightSize(0);
		L1WorldMap.get().getMap((short) mapId).setPassable(field.getLocation(), false);
		World.get().storeObject(field);
		World.get().addVisibleObject(field);
		return field;
	}

	/**
	 * 召喚門
	 * 
	 * @param doorId
	 * @param gfxId
	 * @param locx
	 * @param locy
	 * @param mapid
	 * @param hp
	 * @param keeper
	 * @param isopening
	 * @param left_edge_location
	 * @param right_edge_location
	 * @return
	 */
	public L1DoorInstance spawnDoor(final int doorId, final int gfxId, final int locx, final int locy,
			final short mapid, final int hp, final int keeper, final boolean isopening, final int left_edge_location,
			final int right_edge_location) {
		return spawnDoor(doorId, gfxId, locx, locy, mapid, hp, keeper, isopening, left_edge_location,
				right_edge_location, 0);
	}

	/**
	 * 召喚門
	 * 
	 * @param doorId
	 * @param gfxId
	 * @param locx
	 * @param locy
	 * @param mapid
	 * @param hp
	 * @param keeper
	 * @param isopening
	 * @param left_edge_location
	 * @param right_edge_location
	 * @param direction
	 * @return
	 */
	public L1DoorInstance spawnDoor(final int doorId, final int gfxId, final int locx, final int locy,
			final short mapid, final int hp, final int keeper, final boolean isopening, final int left_edge_location,
			final int right_edge_location, final int direction) {

		for (final L1DoorInstance temp : DoorSpawnTable.get().getDoorList()) {
			if (temp.getMapId() == mapid && temp.getHomeX() == locx && temp.getHomeY() == locy) {
				return temp;
			}
		}

		L1DoorInstance door = null;
		try {
			door = (L1DoorInstance) NpcTable.get().newNpcInstance(81158); // 81158-門
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}
		door.setId(IdFactoryNpc.get().nextId());
		door.setDoorId(doorId);
		door.setGfxId(gfxId);
		door.setX(locx);
		door.setY(locy);
		door.setMap(mapid);
		door.setHomeX(locx);
		door.setHomeY(locy);
		door.setDirection(direction);
		door.setLeftEdgeLocation(left_edge_location);
		door.setRightEdgeLocation(right_edge_location);
		door.setMaxHp(hp);
		door.setCurrentHp(hp);
		door.setKeeperId(keeper);
		//door.setOpenStatus(ActionCodes.ACTION_Open);
		World.get().storeObject(door);
		World.get().addVisibleObject(door);
		DoorSpawnTable.get().addDoor(door);
		return door;
	}

}
