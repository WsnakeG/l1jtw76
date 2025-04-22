package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.CKEW_LV50;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.ItemClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.CampSet;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemBoxTable;
import com.lineage.server.model.L1ItemDelay;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBoxItemLv;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Box;
import com.lineage.server.templates.L1EtcItem;

/**
 * 要求使用物品
 * 
 * @author daien
 */
public class C_ItemUSe extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ItemUSe.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			// 使用者
			final L1PcInstance pc = client.getActiveChar();

			// 鬼魂模式
			if (pc.isGhost()) {
				return;
			}

			// 例外狀況:人物死亡
			if (pc.isDead()) {
				return;
			}

			// 例外狀況:傳送鎖定狀態
			if (pc.isTeleport()) {
				pc.setTeleport(false);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				return;
			}

			// 使用物件的OBJID
			final int itemObjid = readD();

			// 取回使用物件
			final L1ItemInstance useItem = pc.getInventory().getItem(itemObjid);

			// 例外狀況:物件為空
			if (useItem == null) {
				return;
			}

			// 設置使用者OBJID
			useItem.set_char_objid(pc.getId());

			boolean isStop = false;

			// 再生聖殿 1樓/2樓/3樓
			if (pc.getMapId() == CKEWLv50_1.MAPID) {

			}

			// 例外狀況:該地圖不允許使用道具
			if (!pc.getMap().isUsableItem()) {
				// System.out.println("例外狀況:該地圖不允許使用道具");
				// 563 \f1你無法在這個地方使用。
				pc.sendPackets(new S_ServerMessage(563));
				isStop = true;
			}

			// 無法攻擊/使用道具/技能/回城的狀態
			if (pc.isParalyzedX() && !isStop) {
				isStop = true;
			}

			if (!isStop) {
				switch (pc.getMapId()) {
				case 22:// 傑瑞德的試煉地監
					switch (useItem.getItemId()) {
					case 30:// 紅騎士之劍
					case 40017:// 解毒藥水
						break;

					default:
						// 563 \f1你無法在這個地方使用。
						pc.sendPackets(new S_ServerMessage(563));
						isStop = true;
						break;
					}
				}
			}

			if (!L1BuffUtil.getUseItemAll(pc) && !isStop) {
				isStop = true;
			}

			if (pc.isPrivateShop() && !isStop) {// 商店村模式
				isStop = true;
			}

			// 取得物件觸發事件
			final int use_type = useItem.getItem().getUseType();

			if (isStop) {
				pc.setTeleport(false);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				return;
			}

			// 例外狀況:數量小於等於0
			/*
			 * if (useItem.getCount() <= 0) { return; }
			 */
			boolean isClass = false;// 是否具有CLASS
			final String className = useItem.getItem().getclassname();// 獨立執行項位置
			if (!className.equals("0")) {
				isClass = true;
			}

			if (pc.getCurrentHp() > 0) {
				int delay_id = 0;
				if (useItem.getItem().getType2() == 0) { // 種別：一般使用物品：normal
					delay_id = ((L1EtcItem) useItem.getItem()).get_delayid();
					// 502:道具禁止使用
					if (pc.hasItemDelay(L1ItemDelay.ITEM) == true) {
						return;
					}
				}

				if (delay_id != 0) {
					// 延遲作用中
					if (pc.hasItemDelay(delay_id) == true) {
						// System.out.println("延遲作用中");
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						return;
					}
				}

				// 例外狀況:數量異常
				if (useItem.getCount() <= 0) {
					// \f1沒有具有 %0%o。
					pc.sendPackets(new S_ServerMessage(329, useItem.getLogName()));
					return;
				}

				// 取回可用等級限制
				final int min = useItem.getItem().getMinLevel();
				final int max = useItem.getItem().getMaxLevel();

				// 例外狀況:等級不足
				if ((min != 0) && (min > pc.getLevel())) {// 等級不足
					pc.setTeleport(false);
					pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); // 修正傳送卡死問題 161110
					if (min < 50) {
						// 672 等級%d以上才可使用此道具。
						final S_PacketBoxItemLv toUser = new S_PacketBoxItemLv(min, 0);
						pc.sendPackets(toUser);

					} else {
						// 318 等級 %0以上才可使用此道具。
						final S_ServerMessage toUser = new S_ServerMessage(318, String.valueOf(min));
						pc.sendPackets(toUser);
					}
					return;
				}

				// 最低使用需求 (轉生次數) by terry0412
				if (pc.getMeteLevel() < useItem.getItem().getMeteLevel()) {
					pc.setTeleport(false);
					pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); // 修正傳送卡死問題 161110
					pc.sendPackets(new S_SystemMessage(useItem.getItem().getMeteLevel() + "轉以上才可使用此道具。"));
					return;
				}

				// 最高使用需求 (轉生次數) by terry0412
				if (pc.getMeteLevel() > useItem.getItem().getMeteLevelMAX()) {
					pc.setTeleport(false);
					pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); // 修正傳送卡死問題 161110
					pc.sendPackets(new S_SystemMessage(useItem.getItem().getMeteLevel() + "轉以下才可使用此道具。"));
					return;
				}

				// 例外狀況:等級過高
				if ((max != 0) && (max < pc.getLevel())) {// 等級過高
					pc.setTeleport(false);
					pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); // 修正傳送卡死問題 161110
					final S_PacketBoxItemLv toUser = new S_PacketBoxItemLv(0, max);
					pc.sendPackets(toUser);
					return;
				}
				//
				if (CampSet.CAMPSTART) {
					// 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用) by terry0412
					if (pc.get_c_power() != null) {
						final int use_camp = useItem.getItem().getCampSet();
						if (use_camp > 0) {
							// 陣營
							final int c1_type = pc.get_c_power().get_c1_type();
							if ((use_camp & c1_type) != c1_type) {
								pc.sendPackets(new S_SystemMessage("\\aI您並非該所屬陣營，無法使用該道具。"));
								return;
							}
							// 當退出陣營，C1_type為0時
							if (c1_type == 0) {
								pc.sendPackets(new S_SystemMessage("\\aE您還未選擇加入陣營，無法使用該道具。"));
								return;
							}
						}
					}
					// 從未加入陣營，陣營記錄該角色為null時
					if (pc.get_c_power() == null) {
						final int use_camp = useItem.getItem().getCampSet();
						if (use_camp != 0) {
							// 陣營
							pc.sendPackets(new S_SystemMessage("\\aE您尚未加入任何陣營，無法使用該道具。"));
							return;
						}
					}
				}

				// 判定職業 by terry0412
				if (!useItem(pc, useItem)) {
					return;
				}

				// 例外狀況:具有時間設置
				boolean isDelayEffect = false;
				if (useItem.getItem().getType2() == 0) {
					final int delayEffect = ((L1EtcItem) useItem.getItem()).get_delayEffect();
					// int delayEffect =
					// l1iteminstance.getItem().get_delayEffect();
					if (delayEffect > 0) {
						isDelayEffect = true;
						final Timestamp lastUsed = useItem.getLastUsed();
						if (lastUsed != null) {
							final Calendar cal = Calendar.getInstance();
							long useTime = (cal.getTimeInMillis() - lastUsed.getTime()) / 1000;
							if (useTime <= delayEffect) {
								// 轉換為需等待時間
								useTime = (delayEffect - useTime) / 60;
								// 時間數字 轉換為字串
								final String useTimeS = useItem.getLogName() + " " + String.valueOf(useTime);
								// 1139 %0 分鐘之內無法使用。
								pc.sendPackets(new S_ServerMessage(1139, useTimeS));
								return;
							}
						}
					}
				}

				// 取得物件觸發事件判斷
				switch (use_type) {
				case -11:// 对读取方法调用无法分类的物品
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -10:// 加速药水
					if (!L1BuffUtil.stopPotion(pc)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -9:// 技术书
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -8:// 料理书
					if (isClass) {
						try {
							final int[] newData = new int[2];
							newData[0] = readC();
							newData[1] = readC();
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case -7:// 增HP道具
					if (!L1BuffUtil.stopPotion(pc)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -6:// 增MP道具
					if (!L1BuffUtil.stopPotion(pc)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -4:// 項圈
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -3:// 飛刀
					pc.getInventory().setSting(useItem.getItemId());
					// 452 %0%s 被選擇了。
					pc.sendPackets(new S_ServerMessage(452, useItem.getLogName()));
					break;

				case -2:// 箭
					pc.getInventory().setArrow(useItem.getItemId());
					// 452 %0%s 被選擇了。
					pc.sendPackets(new S_ServerMessage(452, useItem.getLogName()));
					break;

				case -12:// 寵物用具
				case -5:// 食人妖精競賽票 / 死亡競賽票
				case -1:// 無法使用
					// 無法使用訊息
					pc.sendPackets(new S_ServerMessage(74, useItem.getLogName()));
					break;

				case 0:// 一般物品(直接施放)
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 1:// 武器
						// 武器禁止使用
					if (pc.hasItemDelay(L1ItemDelay.WEAPON) == true) {
						return;
					}
					if (pc.isWarrior()) {
						useWeaponForWarrior(pc, useItem);
					} else {
						useWeapon(pc, useItem);
					}
					break;

				case 2:// 盔甲
				case 18:// T恤
				case 19:// 斗篷
				case 20:// 手套
				case 21:// 靴
				case 22:// 頭盔
				case 23:// 戒指
				case 24:// 項鍊
				case 25:// 盾牌
				case 37:// 腰帶
				case 40:// 耳環
				case 44:// 副助道具
				case 43:// 輔助格子左
				case 45:// 輔助格子中
				case 47:// 輔助格子左
				case 70:// 脛甲
				case 49: // 49494949
				case 81:// 自訂不顯示欄位可裝備道具
				case 82:// 自訂不顯示欄位可裝備道具
				case 83:// 自訂不顯示欄位可裝備道具
				case 84:// 自訂不顯示欄位可裝備道具
				case 85:// 自訂不顯示欄位可裝備道具
				case 86:// 自訂不顯示欄位可裝備道具
				case 87:// 自訂不顯示欄位可裝備道具
				case 88:// 自訂不顯示欄位可裝備道具
				case 89:// 自訂不顯示欄位可裝備道具
				case 90:// 自訂不顯示欄位可裝備道具
				case 91:// 自訂不顯示欄位可裝備道具
				case 92:// 自訂不顯示欄位可裝備道具
				case 93:// 自訂不顯示欄位可裝備道具
					// 防具禁止使用
					if (pc.hasItemDelay(L1ItemDelay.ARMOR) == true) {
						return;
					}
					useArmor(pc, useItem);
					break;

				case 3:// 創造怪物魔杖(無須選取目標) (無數量:沒有任何事情發生)
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 4:// 希望魔杖(無須選取目標)(有數量:你想要什麼 / 無數量:沒有任何事情發生)
					break;

				case 5:// 魔杖類型(須選取目標)
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = readD();// 選取目標的OBJID
							newData[1] = readH();// X座標
							newData[2] = readH();// Y座標
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 6:// 瞬間移動卷軸
					if (!L1BuffUtil.getUseItemTeleport(pc)) {
						pc.setTeleport(false);
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						return;
					}
					if (isClass) {
						try {
							final int[] newData = new int[2];
							newData[1] = readH();
							newData[0] = readD();
							ItemClass.get().item(newData, pc, useItem);
							// pc.sendPackets(new
							// S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,
							// false));

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 7:// 鑑定卷軸
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = readD();// 選取物件的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 8:// 復活卷軸
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = readD();// 選取目標的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 9:// 傳送回家的卷軸 / 血盟傳送卷軸
					if (!L1BuffUtil.getUseItemTeleport(pc)) {
						pc.setTeleport(false);
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 10:// 照明道具
					// 取得道具編號
					if ((useItem.getRemainingTime() <= 0) && (useItem.getItemId() != 40004)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 14:// 請選擇一個物品(道具欄位) 燈油/磨刀石/膠水
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = readD();// 選取物件的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 15:// 哨子
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 16:// 變形卷軸
				case 61:// 戰鬥特化卷軸
					if (isClass) {
						final String cmd = readS();
						pc.setText(cmd);// 選取的變身命令
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 17:// 選取目標 (近距離)
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = readD();// 選取目標的OBJID
							newData[1] = readH();// X座標
							newData[2] = readH();// Y座標
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 27:// 對盔甲施法的卷軸
				case 26:// 對武器施法的卷軸
				case 46:// 飾品強化捲軸
					if (isClass) {
						try {
							final int[] newData = new int[1];
							// 選取目標的OBJID
							newData[0] = readD();
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 28:// 空的魔法卷軸
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = readC();
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 29:// 瞬間移動卷軸(祝福)
					if (!L1BuffUtil.getUseItemTeleport(pc)) {
						pc.setTeleport(false);
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						return;
					}
					if (isClass) {
						try {
							final int[] newData = new int[2];
							// 所在地圖編號
							newData[1] = readH();
							// 選取目標的OBJID
							newData[0] = readD();
							ItemClass.get().item(newData, pc, useItem);
							// pc.sendPackets(new
							// S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,
							// false));

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 30:// 選取目標 (Ctrl 遠距離)
					if (isClass) {
						try {
							final int obj = readD();// 選取目標的OBJID
							final int[] newData = new int[] { obj };
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 12:// 信紙
				case 31:// 聖誕卡片
				case 33:// 情人節卡片
				case 35:// 白色情人節卡片
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = readH();
							pc.setText(readS());
							pc.setTextByte(readByte());
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 13:// 信紙(打開)
				case 32:// 聖誕卡片(打開)
				case 34:// 情人節卡片(打開)
				case 36:// 白色情人節卡片(打開)
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 38:// 食物
					if (!L1BuffUtil.stopPotion(pc)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 39:// 選取目標 (遠距離)
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = readD();// 選取目標的OBJID
							// newData[1] = this.readH();// X座標
							// newData[2] = this.readH();// Y座標
							final String polyname = readS();
							pc.setpolyname(polyname);
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 42:// 釣魚杆
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = readH();// X座標
							newData[1] = readH();// Y座標
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 55:// 魔法娃娃成長藥劑
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = readD();// 選取物件的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 60:// 全頻廣播器
					/*if (isClass) {
						try {
							pc.setTextByte(readByte());
							ItemClass.get().item(null, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}*/
					if (isClass) {	
						try {
							final int[] newData = new int[1];
							final String text = this.readS();// 要廣播的字串		
							newData[0] = this.readH();// 選取廣播的色碼
							pc.setText(text);// 設置緩存字串
							ItemClass.get().item(newData, pc, useItem);	
						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 62:
				case 65:
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;
//				case 11:
//				case 41:
//				case 48:
//				case 49:
//				case 50:
//				case 51:
//				case 52:
//				case 53:
//				case 54:
//				case 56:
//				case 57:
//				case 58:
//				case 59:
//				case 63:
//				case 64:
//				case 66:
//				case 67:
//				case 68:
//				case 69:
//				case 71:
//				case 72:
//				case 73:
//				case 74:
//				case 75:
//				case 76:
//				case 77:
//				case 78:
//				case 79:
//				case 80:
//				case 81:
//				case 82:
//				case 83:
//				case 84:
//				case 85:
//				case 86:
//				case 87:
				default:// 測試
					_log.info("未處理的物品分類: " + use_type);
					break;
				}

				if ((useItem.getItem().getType2() == 0) && (use_type == 0)) { // 種別：一般道具
					// 取得道具編號
					final int itemId = useItem.getItem().getItemId();

					switch (itemId) {
					case 40308: // 金幣兌換金條--2014/09/01 by Roy新增金幣與金條間兌換
						// (兩種類型均改為other nomal gold)
						if (CreateNewItem.checkNewItem(pc, 40308, 100000000) < 1) {
							return;
						}
						pc.getInventory().consumeItem(40308, 100000000);
						CreateNewItem.createNewItem(pc, 44126, 1);
						break;

					case 44126: // 金條兌換金幣--2014/09/01 by Roy新增金幣與金條間兌換
								// (兩種類型均改為other nomal gold)
						CreateNewItem.createNewItem(pc, 40308, 100000000);
						pc.getInventory().consumeItem(44126, 1);
						break;
					case 40630:// 迪哥的舊日記
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "diegodiary"));
						break;

					case 40663:// 兒子的信
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "sonsletter"));
						break;

					case 40701:// 小藏寶圖
						if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 1) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "firsttmap"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 2) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapa"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 3) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapb"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 4) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapc"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 5) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapd"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 6) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmape"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 7) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapf"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 8) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapg"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 9) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmaph"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 10) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapi"));
						}
						break;

					case 41007:// 伊莉絲的命令書：靈魂之安息
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll"));
						break;

					case 41009:// 伊莉絲的命令書：同盟之意志
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll2"));
						break;

					case 41060:// 諾曼阿吐巴的信
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "nonames"));
						break;

					case 41061:// 妖精調查書：卡麥都達瑪拉
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kames"));
						break;

					case 41062:// 人類調查書：巴庫摩那魯加
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bakumos"));
						break;

					case 41063:// 精靈調查書：可普都達瑪拉
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bukas"));
						break;

					case 41064:// 妖魔調查書：弧鄔牟那魯加
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "huwoomos"));
						break;

					case 41065:// 死亡之樹調查書：諾亞阿吐巴
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noas"));
						break;

					case 41317:// 拉羅森的推薦書
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rarson"));
						break;

					case 41318:// 可恩的便條紙
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kuen"));
						break;

					case 41329:// 標本製作委託書
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "anirequest"));
						break;

					case 41340:// 傭兵團長多文的推薦書
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tion"));
						break;

					case 41356:// 波倫的資源清單
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rparum3"));
						break;

					}
				}

				if (!isClass && (useItem.getItem().getType() == 16) && (useItem.getItem().getType2() == 0)) { // treasure_box
					// 容量確認
					if (pc.getInventory().getSize() >= 160) {
						// \f1一個角色最多可攜帶180個道具。
						pc.sendPackets(new S_ServerMessage(263));
						return;
					}

					if (pc.getInventory().getWeight240() >= 180) {
						// 此物品太重了，所以你無法攜帶。
						pc.sendPackets(new S_ServerMessage(82));
						return;
					}

					if ((useItem.getItemId() == 60441) && !pc.getInventory().checkItem(60441, 3)) {
						return;
					}

					int size = 1;
					if ((useItem.getItemId() == 60441) || (useItem.getItemId() == 60440)) {
						size = readD();
						size = Math.abs(size);
						if (size > 10) {
							size = 10;
						}
					}

					for (int i = 0; i < size; i++) {
						if (((useItem.getItemId() == 60441) || (useItem.getItemId() == 60440))
								&& (pc.getLottery().getList().size() >= 127)) {
							pc.sendPackets(new S_ServerMessage(3025));
							break;
						}
						ArrayList<L1Box> list = null;
						try {
							list = ItemBoxTable.get().get(pc, useItem);
							if (list == null) {
								ItemBoxTable.get().get_all(pc, useItem);
							}
						} catch (final Exception e) {
							// TODO: handle exception
						} finally {
							list = null;
						}
					}
				}

				// 物件使用延遲設置
				if (isDelayEffect && !isClass) {
					final Timestamp ts = new Timestamp(System.currentTimeMillis());
					// 設置使用時間
					useItem.setLastUsed(ts);
					pc.getInventory().updateItem(useItem, L1PcInventory.COL_DELAY_EFFECT);
					pc.getInventory().saveItem(useItem, L1PcInventory.COL_DELAY_EFFECT);
				}

				try {
					// 分類道具使用延遲
					L1ItemDelay.onItemUse(client, useItem);

				} catch (final Exception e) {
					_log.error("分類道具使用延遲異常:" + useItem.getItemId(), e);
				}
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/**
	 * 武器防具的使用<BR>
	 * 
	 * @param pc
	 * @param useItem
	 * @return 該職業可用傳回:true
	 */
	private boolean useItem(final L1PcInstance pc, final L1ItemInstance useItem) {
		boolean isEquipped = false;
		// 職業與物件的使用限制
		if (pc.isCrown()) {// 王族
			if (useItem.getItem().isUseRoyal()) {
				isEquipped = true;
			}
		} else if (pc.isKnight()) {// 騎士
			if (useItem.getItem().isUseKnight()) {
				isEquipped = true;
			}
		} else if (pc.isElf()) {// 精靈
			if (useItem.getItem().isUseElf()) {
				isEquipped = true;
			}
		} else if (pc.isWizard()) {// 法師
			if (useItem.getItem().isUseMage()) {
				isEquipped = true;
			}
		} else if (pc.isDarkelf()) {// 黑暗精靈
			if (useItem.getItem().isUseDarkelf()) {
				isEquipped = true;
			}
		} else if (pc.isDragonKnight()) {// 龍騎士
			if (useItem.getItem().isUseDragonknight()) {
				isEquipped = true;
			}
		} else if (pc.isIllusionist()) {// 幻術師
			if (useItem.getItem().isUseIllusionist()) {
				isEquipped = true;
			}
		} else if (pc.isWarrior()) {// 戰士
			if (useItem.getItem().isUseWarrior()) {
				isEquipped = true;
			}
		}
		if (!isEquipped) {
			// \f1你的職業無法使用此裝備。
			pc.sendPackets(new S_ServerMessage(264));
		}
		return isEquipped;
	}

	/**
	 * 設置防具的裝備
	 * 
	 * @param pc
	 * @param armor
	 */
	private void useArmor(final L1PcInstance pc, final L1ItemInstance armor) {
		int itemid = armor.getItem().getItemId();
		final int type = armor.getItem().getType();
		final L1PcInventory pcInventory = pc.getInventory();
		boolean equipeSpace; // 装备栏是否有空位
		if (type == 9) { // type类型为9是戒指可戴2个,其他都只能戴1个
			if (!armor.isEquipped()) {
				if (pcInventory.getEquippedCountByItemId(armor.getItemId()) >= 2) {
					pc.sendPackets(new S_ServerMessage(3278));
					return;

				} else if (pcInventory.getEquippedCountByActivityItem() >= 2) {
					pc.sendPackets(new S_ServerMessage(3279));
					return;
				}
			}

			int count = 1;

			if ((pc.getRingsExpansion() & 1) == 1) {
				count++;
			}
			if ((pc.getRingsExpansion() & 2) == 2) {
				count++;
			}
			equipeSpace = pcInventory.getTypeEquipped(2, 9) <= count;

		} else if (type == 12 || type == 14) {
			equipeSpace = pcInventory.getTypeEquipped(2, type) <= 1;
		} else {
			equipeSpace = pcInventory.getTypeEquipped(2, type) <= 0;
		}
	
		// 各部位可使用裝備數判斷
		if (type == 9) { // 戒指
			equipeSpace = pcInventory.getTypeEquipped(2, 9) <= 3;
		} else if (type == 12) {// 耳環
			equipeSpace = pcInventory.getTypeEquipped(2, 12) <= 1;
		} else {
			equipeSpace = pcInventory.getTypeEquipped(2, type) <= 0;
		}
		
		// 防具穿戴
		if (equipeSpace && !armor.isEquipped()) { // 要安装的装备栏尚未安装物品
			// 76級欄位判斷
			if ((type == 9) && pcInventory.getTypeEquipped(2, 9) == 2) {
				if (pc.getQuest().get_step(58003) != 1 || pc.getLevel() < 76) {
//					pc.sendPackets(new S_ServerMessage(3253));
					pc.sendPackets(new S_ServerMessage("尚未開啟欄位或等級不足無法裝備。"));
					// \f1すでに何かを装備しています。
					return;
				}
			}

			// 81級欄位判斷
			if ((type == 9) && pcInventory.getTypeEquipped(2, 9) == 3) {
				if (pc.getQuest().get_step(58002) != 1 || pc.getLevel() < 81) {
//					pc.sendPackets(new S_ServerMessage(3253));
					pc.sendPackets(new S_ServerMessage("尚未開啟欄位或等級不足無法裝備。"));
					// \f1すでに何かを装備しています。
					return;
				}
			}

			// 相同戒指數量判斷
			if (pcInventory.getTypeAndItemIdEquipped(2, 9, itemid) == 2) {
//				pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
				pc.sendPackets(new S_ServerMessage("已到達可裝備的戒指數量上限。"));
				return;
			}

			// 耳環59級欄位判斷
			if ((type == 12) && pcInventory.getTypeEquipped(2, 12) == 1) {
				if (pc.getQuest().get_step(58001) != 1 || pc.getLevel() < 59) {
//					pc.sendPackets(new S_ServerMessage(3253));
					pc.sendPackets(new S_ServerMessage("尚未開啟欄位或等級不足無法裝備。"));
					// \f1すでに何かを装備しています。
					return;
				}
			}

			// 相同耳環數量判斷
			if (pcInventory.getTypeAndItemIdEquipped(2, 12, itemid) == 1) {
//				pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
				pc.sendPackets(new S_ServerMessage("已到達可裝備的耳環數量上限。"));
				return;
			}
			
			
			final int polyid = pc.getTempCharGfx();

			if (!L1PolyMorph.isEquipableArmor(polyid, type)) { // 不可此穿戴防具的变身形态下
				return;
			}

			if (((type == 13) && (pcInventory.getTypeEquipped(2, 7) >= 1 // 已经装备其他东西。
			)) || ((type == 7) && (pcInventory.getTypeEquipped(2, 13) >= 1))) {
				pc.sendPackets(new S_ServerMessage(124));
				return;
			}

			if ((type == 7) && (pc.getWeapon() != null)) { // 使用双手武器时无法使用盾
				if (pc.getWeapon().getItem().isTwohandedWeapon()) { // 双手武器
					// 129 \f1當你使用雙手武器時，無法裝備盾牌。
					pc.sendPackets(new S_ServerMessage(129));
					return;
				}
			}

			if (pc.isWarrior() && (type == 13) && (pc.getWeaponWarrior() != null)) {
				pc.sendPackets(new S_ServerMessage(124));
				return;
			}

			// if ((type == 3) && (pcInventory.getTypeEquipped(2, 4) >= 1)) { //
			// 穿着斗篷时不可穿内衣
			// // 126 \f1穿著%1 無法裝備 %0%o 。
			// pc.sendPackets(new S_ServerMessage(126, "$224", "$225"));
			// return;
			//
			// } else if ((type == 3) && (pcInventory.getTypeEquipped(2, 2) >=
			// 1)) { // 穿着盔甲时不可穿内衣
			// // 126 \f1穿著%1 無法裝備 %0%o 。
			// pc.sendPackets(new S_ServerMessage(126, "$224", "$226"));
			// return;
			//
			// } else if ((type == 2) && (pcInventory.getTypeEquipped(2, 4) >=
			// 1)) { // 穿着斗篷时不可穿盔甲
			// // 126 \f1穿著%1 無法裝備 %0%o 。
			// pc.sendPackets(new S_ServerMessage(126, "$226", "$225"));
			// return;
			// }

			// 物品穿戴成功解除技能：魔法屏障
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
				pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
				pc.startHpRegeneration();
				pc.startMpRegeneration();
			}
			pcInventory.setEquipped(armor, true);
			// 防具脱除
			} else if (armor.isEquipped()) { // 所选防具穿戴在身上
			if (armor.getItem().getBless() == 2) { // 呪われていた場合
				// 150 \f1你无法这样做。这个物品已经被诅咒了。
				pc.sendPackets(new S_ServerMessage(150));
				return;
			}			/*穿斗膨脫盔甲內衣
				else {
				if ((type == 3) && (pcInventory.getTypeEquipped(2, 2) >= 1)) { // 穿着盔甲时不能脱下内衣
					// 127 \f1你不能夠脫掉那個。
					pc.sendPackets(new S_ServerMessage(127));
					return;

				} else if (((type == 2) || (type == 3)) && (pcInventory.getTypeEquipped(2, 4) >= 1)) { // 穿着斗篷时不能脱下内衣
					// 127 \f1你不能夠脫掉那個。
					pc.sendPackets(new S_ServerMessage(127));
					return;
				}
				
				pcInventory.setEquipped(armor, false);
			}   					穿斗膨脫盔甲內衣*/				 
			/*
			 * 1:helm, 头盔 2:armor, 盔甲 3:T,内衣 4:cloak,斗篷 5:glove,手套 6:boots, 靴子
			 * 7:shield, 盾 8:amulet, 项链 9:ring, 戒指 10:belt, 腰带 11:ring2,
			 * 12:earring耳环 13:
			 */
		} else {
			if (armor.getItem().getUseType() == 23) {
				// 144 \f1你已經戴著二個戒指。
				pc.sendPackets(new S_ServerMessage(144));
				return;

			} else {
				// 124 \f1已經裝備其他東西。
				pc.sendPackets(new S_ServerMessage(124));
				return;
			}
		}

		// 更新HP,MP
		pc.setCurrentHp(pc.getCurrentHp());
		pc.setCurrentMp(pc.getCurrentMp());

		// 更新角色防禦屬性
		pc.sendPackets(new S_OwnCharAttrDef(pc));
		// 更改人物状态
		pc.sendPackets(new S_OwnCharStatus(pc));
		// 更改人物魔法攻击与魔法防御
		pc.sendPackets(new S_SPMR(pc));

	}

	/**
	 * 設置武器的裝備
	 * 
	 * @param pc
	 * @param weapon
	 */
	private void useWeapon(final L1PcInstance pc, final L1ItemInstance weapon) {
		switch (weapon.getItemId()) {
		case 65:// 天空之劍
		case 133:// 古代人的智慧
		case 191:// 水之豎琴
		case 192:// 水精靈之弓
			if ((pc.getMapId() != CKEWLv50_1.MAPID) && !pc.getWeapon().equals(weapon)) {
				// 563 \f1你無法在這個地方使用。
				pc.sendPackets(new S_ServerMessage(563));
				return;
			}
			break;

		default:
			if (pc.hasSkillEffect(CKEW_LV50)) {
				// 563 \f1你無法在這個地方使用。
				pc.sendPackets(new S_ServerMessage(563));
				return;
			}
			break;
		}
		final L1PcInventory pcInventory = pc.getInventory();
		if ((pc.getWeapon() == null) || !pc.getWeapon().equals(weapon)) { // 没有使用武器或使用武器与所选武器不同
			final int weapon_type = weapon.getItem().getType();
			final int polyid = pc.getTempCharGfx();

			if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) { // 不可使用此武器的变身形态下使用武器
				return;
			}
			if (weapon.getItem().isTwohandedWeapon() && (pcInventory.getTypeEquipped(2, 7) >= 1)) { // 使用了盾时不可再使用双手武器
				pc.sendPackets(new S_ServerMessage(128));
				return;
			}
		}
		// 解除魔法技能：绝对屏障
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
		}

		if (pc.getWeapon() != null) { // 已有裝備的狀態
			if (pc.getWeapon().getItem().getBless() == 2) {
				// 150 \f1你無法這樣做。這個物品已經被詛咒了。
				pc.sendPackets(new S_ServerMessage(150));
				return;
			}
			if (pc.getWeapon().equals(weapon)) {
				// 解除裝備
				pcInventory.setEquipped(pc.getWeapon(), false, false, false);
				return;

				// 武器交換
			} else {
				pcInventory.setEquipped(pc.getWeapon(), false, false, true);
			}

		}

		if (weapon.getItem().getBless() == 2) {
			// \f1%0%s 主動固定在你的手上！
			pc.sendPackets(new S_ServerMessage(149, weapon.getLogName()));
		}
		pcInventory.setEquipped(weapon, true, false, false);
	}

	private void useWeaponForWarrior(final L1PcInstance pc, final L1ItemInstance weapon) {
		final int weapon_type = weapon.getItem().getType();
		final int polyid = pc.getTempCharGfx();

		if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) {
			pc.sendPackets(new S_ServerMessage(2055, weapon.getName()));
			return;
		}
		final L1PcInventory pcInventory = pc.getInventory();

		if (pc.getWeapon() != null) { // 已經持有武器
			if (pc.getWeapon().equals(weapon)) { // 已經持有此武器&不為第二把斧頭
				pcInventory.setEquipped(pc.getWeapon(), false, false, false);
				if (pc.getWeaponWarrior() != null) {
					final L1ItemInstance nextWeapon = pc.getWeaponWarrior();
					pcInventory.setWarriorEquipped(nextWeapon, false, false);
					// 武器位移
					pcInventory.setEquipped(nextWeapon, true, false, false);
				}
				return;

			} else if ((pc.getWeaponWarrior() != null) && pc.getWeaponWarrior().equals(weapon)) {// 已經持有此武器&且為第二把斧頭
				pcInventory.setWarriorEquipped(pc.getWeaponWarrior(), false, false);
				pc.sendPacketsAll(new S_CharVisualUpdate(pc.getId(), pc.getCurrentWeapon()));
				return;

			} else if (pc.getWeaponWarrior() == null) { // 已經持有武器且沒裝備第二把斧頭
				if (pc.isSLAYER()) { // 技能可用雙斧
					if (pcInventory.getGarderEquipped(2, 13, 13) >= 1) { // 持有盾牌&臂甲
						pcInventory.setEquipped(pc.getWeapon(), false, false, true);
					} else if ((weapon.getItem().getType() != 6)
							|| (pc.getWeapon().getItem().getType() != 6)) { // 目前武器不為斧
						pcInventory.setEquipped(pc.getWeapon(), false, false, true);
					} else if (pc.getWeapon().getItem().isTwohandedWeapon()// 雙手斧不能雙持
							|| weapon.getItem().isTwohandedWeapon()) {
						pcInventory.setEquipped(pc.getWeapon(), false, false, true);
					} else {
						pcInventory.setWarriorEquipped(weapon, true, true);// 裝備雙手斧
						pc.sendPacketsAll(new S_CharVisualUpdate(pc.getId(), pc.getCurrentWeapon()));
						pc.sendPackets(new S_SkillSound(pc.getId(), 12534));
						return;
					}
				} else {
					pcInventory.setEquipped(pc.getWeapon(), false, false, true);
				}

			} else {// 已經持有武器且這不是斧頭
				if (weapon.getItem().getType() != 6) {
					// 已經裝備其他東西。
					pc.sendPackets(new S_ServerMessage(124));
					return;
				}
				pcInventory.setEquipped(pc.getWeapon(), false, false, true);
			}
		}
		pcInventory.setEquipped(weapon, true, false, false);
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
