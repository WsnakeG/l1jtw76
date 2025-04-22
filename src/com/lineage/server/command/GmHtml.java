package com.lineage.server.command;

import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.DeGlobalChatTable;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;

/**
 * GM管理選單<BR>
 * 
 * @author dexc
 */
public class GmHtml {

	private static final Log _log = LogFactory.getLog(GmHtml.class);

	private static final Random _random = new Random();

	// 執行GM
	private final L1PcInstance _pc;

	// 執行模式 0:PC清單 1:DE清單 2:封鎖清單
	private int _mode;

	// 線上玩家數量
	private int _users;

	// 暫存清單(DE)
	private TreeMap<Integer, L1DeInstance> _allDeList;

	// 暫存清單(PC)
	private TreeMap<Integer, L1PcInstance> _allPcList;

	// 暫存清單(封鎖)
	private TreeMap<Integer, String> _banList;

	// 暫存清單(封鎖)
	private TreeMap<Integer, String> _banTmpList;

	public GmHtml(final L1PcInstance pc) {
		_pc = pc;
		_pc.get_other().set_page(0);
		// GM管理狀態
		_pc.get_other().set_gmHtml(this);
	}

	/**
	 * GM管理選單
	 * 
	 * @param pc
	 * @param mode 0:PC清單 1:DE清單 2:封鎖清單
	 */
	public GmHtml(final L1PcInstance pc, final int mode) {
		_pc = pc;
		_pc.get_other().set_page(0);
		// GM管理狀態
		_pc.get_other().set_gmHtml(this);
		_users = World.get().getAllPlayers().size();

		_allDeList = new TreeMap<Integer, L1DeInstance>();
		_allPcList = new TreeMap<Integer, L1PcInstance>();
		_banList = new TreeMap<Integer, String>();
		_banTmpList = new TreeMap<Integer, String>();

		_mode = mode;

		// 加載MAP(DE)
		int keyDe = 0;
		for (final L1NpcInstance npc : WorldNpc.get().all()) {
			if (npc instanceof L1DeInstance) {
				final L1DeInstance de = (L1DeInstance) npc;
				_allDeList.put(new Integer(keyDe), de);
				keyDe++;
			}
		}

		// 加載MAP(PC)
		int keyPc = 0;
		for (final L1PcInstance tgpc : World.get().getAllPlayers()) {
			_allPcList.put(new Integer(keyPc), tgpc);
			keyPc++;
		}

		// 加載M封鎖
		int keyBan = 0;
		for (final String ban : LanSecurityManager.BANIPMAP.keySet()) {
			_banList.put(new Integer(keyBan), ban);
			keyBan++;
		}
		for (final String ban : LanSecurityManager.BANNAMEMAP.keySet()) {
			_banList.put(new Integer(keyBan), ban);
			keyBan++;
		}
	}

	/**
	 * 展示頁面
	 */
	public void show() {
		showPage(0);
	}

	/**
	 * 展示頁面
	 */
	public void show(final L1DeInstance de_tmp) {
		final L1DeInstance de = _pc.get_outChat();
		if (de != null) {
			final String[] type = new String[] { "王族", "騎士", "精靈", "法師", "黑妖", "龍騎", "幻術", "戰士" };
			final String[] sex = new String[] { "男", "女" };
			final String[] info = new String[] { de.getNameId(), type[de.get_deName().get_type()],
					sex[de.get_deName().get_sex()], String.valueOf(de.getLevel()), };
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_GmDE", info));
		}
	}

	/**
	 * 執行指令判斷
	 * 
	 * @param cmd
	 */
	public void action(final String cmd) {
		try {
			if (cmd.equals("up")) {// 上一頁
				final int page = _pc.get_other().get_page() - 1;
				showPage(page);

			} else if (cmd.equals("dn")) {// 下一頁
				final int page = _pc.get_other().get_page() + 1;
				showPage(page);

			} else if (cmd.startsWith("K")) {// 踢下線
				final int xcmd = Integer.parseInt(cmd.substring(1));
				startCmd(1, xcmd);

			} else if (cmd.startsWith("D")) {// 封號
				final int xcmd = Integer.parseInt(cmd.substring(1));
				startCmd(2, xcmd);

			} else if (cmd.startsWith("M")) {// 封MAC
				final int xcmd = Integer.parseInt(cmd.substring(1));
				startCmd(3, xcmd);

			} else if (cmd.startsWith("T")) {// 移動
				final int xcmd = Integer.parseInt(cmd.substring(1));
				startCmd(4, xcmd);

			} else if (cmd.startsWith("de")) {// 虛擬人物控制介面
				final int xcmd = Integer.parseInt(cmd.substring(2));
				action_to_de(xcmd);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void action_to_de(final int cmd) {
		try {
			final L1DeInstance de = _pc.get_outChat();
			if (de != null) {
				int petid = -1;
				int count = -1;
				int dollid = -1;
				int heading = -1;
				int polyid = -1;
				boolean move = false;
				if ((cmd >= 1) && (cmd <= 9)) {
					switch (de.getClassId()) {
					case 734:// 法師
					case 1186:
						break;
					default:
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "不是法師"));
						return;
					}
				}

				if ((cmd >= 10) && (cmd <= 17)) {
					switch (de.getClassId()) {
					case 138:// 精靈
					case 37:
						break;
					default:
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "不是精靈"));
						return;
					}
				}

				switch (cmd) {
				case 24:// 執行釣魚
				case 32:// 自動執行釣魚
					if (de.isShop()) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在商店模式"));
						return;
					}
					if (de.isFishing()) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "已經在釣魚了"));
						return;
					}
					if (cmd == 24) {// 執行釣魚
						// 必須先將人物移動到能釣魚位置
						de.start_fishing();

					} else if (cmd == 32) {// 自動執行釣魚
						de.start_fishingAI();
					}
					return;
				case 33:// 取消釣魚
					if (de.isShop()) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在商店模式"));
						return;
					}
					if (!de.isFishing()) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "並沒有在釣魚"));
						return;
					}
					de.stop_fishing();
					return;

				case 25:// 執行商店 - 必須在商店村之中
					switch (de.getMapId()) {
					case 340:// 古鲁丁商店村
					case 350:// 奇岩商店村
					case 360:// 欧瑞商店村
					case 370:// 银骑士商店村
						if (de.isShop()) {
							_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "已經在商店模式"));
							return;
						}
						de.start_shop();
						break;
					default:
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "這裡不是商店村"));
						break;
					}
					return;
				case 26:// 取消商店 - 必須在商店模式之中
					if (de.isFishing()) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在釣魚模式"));
						return;
					}
					if (!de.isShop()) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "並沒有在商店模式"));
						return;
					}
					de.stop_shop();
					return;

				case 27:// 執行自動喊話(買賣)
				case 28:// 執行自動喊話(廣播)
					if (de.get_chat() != null) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在自動喊話"));
						return;
					}
					final String chat = DeGlobalChatTable.get().getChat();
					de.set_chat(chat, cmd);
					return;
				case 43:// 停止執行自動喊話
					if (de.get_chat() == null) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "並沒有在自動喊話"));
						return;
					}
					de.set_chat(null, 0);
					return;

				case 29:// 順移後刪除虛擬人物
					// 傳送動畫
					de.broadcastPacketAll(new S_SkillSound(de.getId(), 169));
					de.deleteMe();
					_pc.sendPackets(new S_CloseList(_pc.getId()));
					return;
				case 30:// 直接刪除虛擬人物
					de.deleteMe();
					_pc.sendPackets(new S_CloseList(_pc.getId()));
					return;

				// 法師可執行
				case 1:// 巨大牛人
					petid = 81239;
					count = 1;
					break;
				case 2:// 黑豹
					petid = 81240;
					count = 1;
					break;
				case 6:// 變形怪
					petid = 81238;
					count = 1;
					break;
				case 3:// 魔熊
					petid = 81228;
					count = _random.nextInt(3) + 1;
					break;
				case 4:// 巨大守護螞蟻
					petid = 81231;
					count = _random.nextInt(3) + 1;
					break;
				case 5:// 食腐獸
					petid = 81236;
					count = _random.nextInt(3) + 1;
					break;
				case 7:// 火焰弓箭手
					petid = 81227;
					count = _random.nextInt(3) + 1;
					break;
				case 8:// 火焰戰士
					petid = 81226;
					count = _random.nextInt(3) + 1;
					break;
				case 9:// 地獄束縛犬
					petid = 81237;
					count = _random.nextInt(3) + 1;
					break;

				// 精靈可執行
				case 10:// 強力土之精靈
					petid = 81053;
					count = 1;
					break;
				case 11:// 強力火之精靈
					petid = 81050;
					count = 1;
					break;
				case 12:// 強力水之精 靈
					petid = 81051;
					count = 1;
					break;
				case 13:// 強力風之精靈
					petid = 81052;
					count = 1;
					break;
				case 14:// 土之精靈
					petid = 45306;
					count = 1;
					break;
				case 15:// 火之精靈
					petid = 45303;
					count = 1;
					break;
				case 16:// 水之精靈
					petid = 45304;
					count = 1;
					break;
				case 17:// 風之精靈
					petid = 45305;
					count = 1;
					break;

				// 執行召喚魔法娃娃
				case 18:// 肥肥
					dollid = 55000;
					break;
				case 19:// 小思克巴
					dollid = 55001;
					break;
				case 20:// 野狼寶寶
					dollid = 55002;
					break;
				case 21:// 奎斯坦修
					dollid = 55011;
					break;
				case 22:// 石頭高侖
					dollid = 55013;
					break;

				case 23:// 由設置的資料中隨機挑選一個
					final Object[] dolls = DollPowerTable.get().map().keySet().toArray();
					final Object doll = dolls[_random.nextInt(dolls.length)];
					dollid = (Integer) doll;
					break;

				case 31:// 執行變身
					final int[] polyids = new int[] { 6157, // 6157 死騎
							6160, // 6160 黑妞
							12490, // 12490 戰士(男)
							12494,// 12494 戰士(女)
							// 8913,// 8913 朱里安
							// 9003,// 9003 喬
							// 8900,// 8900 海露拜
							// 8817,// 8817 肯恩羅亨
							// 8812,// 8812 甘特
							// 8978, 8851, 8812, 8774, 4923,// 4923 黑騎士
							// 7341,// 7341 槍兵
							// 7341,// 7341 槍兵
							// 7341,// 7341 槍兵
							// 6276,// 6276 白金騎士
							// 6278,// 6278 白金巡守
							// 6275,// 6275 黃金巡守
							// 7959,// 天上騎士
					};
					polyid = polyids[_random.nextInt(polyids.length)];
					break;

				case 34:// 往面向移動1格
					move = true;
					break;

				// 0:左上 1:上 2:右上 3:右 4:右下 5:下 6:左下 7:左
				case 35:// 改變面向 上
					heading = 1;
					break;
				case 36:// 改變面向 右上
					heading = 2;
					break;
				case 37:// 改變面向 右
					heading = 3;
					break;
				case 38:// 改變面向 右下
					heading = 4;
					break;
				case 39:// 改變面向 下
					heading = 5;
					break;
				case 40:// 改變面向 左下
					heading = 6;
					break;
				case 41:// 改變面向 左
					heading = 7;
					break;
				case 42:// 改變面向 左上
					heading = 0;
					break;
				}

				if (de.isShop()) {
					_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在商店模式"));
					return;
				}
				if (de.isFishing()) {
					_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在釣魚模式"));
					return;
				}

				// 往面向移動1格
				if (move) {
					de.getMove().setDirectionMove(de.getHeading());
					de.setNpcSpeed();
					return;
				}

				// 設置新面向
				if (heading != -1) {
					de.setHeading(heading);
					de.broadcastPacketX8(new S_ChangeHeading(de));
					return;
				}

				// 設置變身
				if (polyid != -1) {
					L1PolyMorph.doPoly(de, polyid, 300, L1PolyMorph.MORPH_BY_ITEMMAGIC);
					return;
				}

				// 設置寵物
				if (petid != -1) {
					if (de.getPetList().size() > 0) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "已經有寵物"));
						return;
					}
					final L1Npc template = NpcTable.get().getTemplate(petid);
					if (template != null) {
						for (int i = 0; i < count; i++) {
							final L1SummonInstance summon = new L1SummonInstance(template, de);
							summon.set_currentPetStatus(1);
							summon.setMoveSpeed(1);
						}
					}
					de.broadcastPacketX8(new S_DoActionGFX(de.getId(), ActionCodes.ACTION_SkillBuff));
					return;
				}

				// 設置娃娃
				if (dollid != -1) {
					if (!de.getDolls().isEmpty()) {
						_pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "已經有娃娃"));
						return;
					}
					final L1Npc template = NpcTable.get().getTemplate(71082);
					if (template != null) {
						final L1Doll type = DollPowerTable.get().get_type(dollid);
						final L1DollInstance doll = new L1DollInstance(template, de, type);
						doll.setNpcMoveSpeed();
					}
					return;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 執行命令
	 * 
	 * @param mode 模式
	 * @param cmd 命令
	 */
	private void startCmd(final int mode, final int cmd) {
		try {
			int page = _pc.get_other().get_page();
			if (page > 0) {
				page *= 10;
			}

			final int xcmd = page + cmd;

			boolean isDe = false;
			if (_mode == 0) {// 展示PC
				switch (mode) {
				case 1:// 踢下線
					final L1PcInstance target_pc1 = _allPcList.get(xcmd);
					_pc.sendPackets(new S_ServerMessage(target_pc1.getName() + " 踢除下線。"));

					final L1PcInstance target_pcX1 = World.get().getPlayer(target_pc1.getName());
					if (target_pcX1 != null) {// 線上
						target_pcX1.getNetConnection().kick();
					}
					break;

				case 2:// 封號
					final L1PcInstance target_pc2 = _allPcList.get(xcmd);
					IpReading.get().add(target_pc2.getAccountName(), "GM命令:L1AccountBanKick 封鎖帳號");
					_pc.sendPackets(new S_ServerMessage(target_pc2.getName() + " 帳號封鎖。"));

					final L1PcInstance target_pcX2 = World.get().getPlayer(target_pc2.getName());
					if (target_pcX2 != null) {// 線上
						target_pcX2.getNetConnection().kick();
					}
					break;

				case 3:// 封MAC
					final L1PcInstance target_pc3 = _allPcList.get(xcmd);
					final L1PcInstance target_pcX3 = World.get().getPlayer(target_pc3.getName());

					if (target_pcX3 != null) {// 線上
						final StringBuilder macaddr = target_pcX3.getNetConnection().getMac();
						if (macaddr != null) {
							// 加入MAC封鎖
							IpReading.get().add(macaddr.toString(), "GM命令:L1PowerKick 封鎖");
							_pc.sendPackets(new S_ServerMessage(target_pcX3.getName() + " 封鎖MAC。"));
						}
						target_pcX3.getNetConnection().kick();

					} else {// 離線
						IpReading.get().add(target_pc3.getAccountName(), "GM命令:L1AccountBanKick 封鎖帳號");
						_pc.sendPackets(new S_ServerMessage(target_pc3.getName() + " (離線)帳號封鎖。"));
					}
					break;

				case 4:// 移動
					final L1PcInstance target_pc4 = _allPcList.get(xcmd);
					final L1PcInstance target_pcX4 = World.get().getPlayer(target_pc4.getName());

					if (target_pcX4 != null) {// 線上
						// 取回座標資料
						final L1Location loc = L1Location.randomLocation(target_pcX4.getLocation(), 1, 2,
								false);
						L1Teleport.teleport(_pc, loc.getX(), loc.getY(), target_pcX4.getMapId(),
								_pc.getHeading(), false);
						_pc.sendPackets(new S_ServerMessage("移動座標至指定人物身邊: " + target_pcX4.getName()));

					} else {
						// 73 \f1%0%d 不在線上。
						_pc.sendPackets(new S_ServerMessage(73, target_pc4.getName()));
					}
					break;
				}

			} else if (_mode == 1) {// 展示DE
				switch (mode) {
				case 1:// 踢下線
				case 2:// 封號
				case 3:// 封MAC
					isDe = true;
					break;

				case 4:// 移動
					final L1DeInstance target_de = _allDeList.get(xcmd);
					final L1Object obj = World.get().findObject(target_de.getId());
					if (obj != null) {// 未移除
						// 取回座標資料
						final L1Location loc = L1Location.randomLocation(target_de.getLocation(), 1, 2,
								false);
						L1Teleport.teleport(_pc, loc.getX(), loc.getY(), target_de.getMapId(),
								_pc.getHeading(), false);
						_pc.sendPackets(new S_ServerMessage("移動座標至指定虛擬人物身邊: " + target_de.getName()));

					} else {
						_pc.sendPackets(new S_ServerMessage("已經移除虛擬人物: " + target_de.getName()));
					}
					break;
				}

			} else if (_mode == 2) {// 展示封鎖
				switch (mode) {
				case 1:// 解除封鎖
					final String banInfo = _banList.get(xcmd);
					IpReading.get().remove(banInfo);
					_pc.sendPackets(new S_ServerMessage("解除封鎖: " + banInfo));
					break;
				}
			}

			if (isDe) {
				final L1DeInstance target_de = _allDeList.get(xcmd);
				final L1Object obj = World.get().findObject(target_de.getId());
				if (obj != null) {// 未移除
					target_de.deleteMe();
					_pc.sendPackets(new S_ServerMessage("刪除虛擬人物: " + target_de.getName()));

				} else {
					_pc.sendPackets(new S_ServerMessage("已經移除虛擬人物: " + target_de.getName()));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 顯示活動頁面
	 * 
	 * @param page 頁面
	 */
	private void showPage(int page) {
		try {
			// 全部頁面數量
			int allpage = 0;

			final StringBuilder stringBuilder = new StringBuilder();

			if (_mode == 0) {// 展示PC
				// 全部頁面數量
				allpage = _allPcList.size() / 10;
				if ((page > allpage) || (page < 0)) {
					page = 0;
				}

				if ((_allPcList.size() % 10) != 0) {
					allpage += 1;
				}

				_pc.get_other().set_page(page);// 設置頁面

				final int or = page * 10;
				// 寫入線上人數
				stringBuilder.append(String.valueOf(_users) + ",");

				int i = 0;// 檢查號
				for (final Integer key : _allPcList.keySet()) {
					if ((i >= or) && (i < (or + 10))) {
						final L1PcInstance tgpc = _allPcList.get(key);
						if (tgpc != null) {
							stringBuilder.append(tgpc.getName() + "(" + tgpc.getAccountName() + ") PcLv:"
									+ tgpc.getLevel() + ",");
						}
					}
					i++;
				}

			} else if (_mode == 1) {// 展示DE
				// 全部頁面數量
				allpage = _allDeList.size() / 10;
				if ((page > allpage) || (page < 0)) {
					page = 0;
				}

				if ((_allDeList.size() % 10) != 0) {
					allpage += 1;
				}

				_pc.get_other().set_page(page);// 設置頁面

				final int or = page * 10;
				// 寫入線上DE人數
				stringBuilder.append(String.valueOf(_users) + ",");

				int i = 0;// 檢查號
				for (final Integer key : _allDeList.keySet()) {
					if ((i >= or) && (i < (or + 10))) {
						final L1DeInstance tgde = _allDeList.get(key);
						if (tgde != null) {
							stringBuilder.append(tgde.getNameId() + " DeLv:" + tgde.getLevel() + ",");
						}
					}
					i++;
				}

			} else if (_mode == 2) {// 展示封鎖
				// 全部頁面數量
				allpage = _banList.size() / 10;
				if ((page > allpage) || (page < 0)) {
					page = 0;
				}

				if ((_banList.size() % 10) != 0) {
					allpage += 1;
				}

				_pc.get_other().set_page(page);// 設置頁面

				final int or = page * 10;

				int i = 0;// 檢查號
				for (final Integer key : _banList.keySet()) {
					if ((i >= or) && (i < (or + 10))) {
						final String banIp = _banList.get(key);
						if (banIp != null) {
							stringBuilder.append(banIp + ",");
						}
					}
					i++;
				}

			} else if (_mode == 3) {// 展示暫時封鎖
				// 全部頁面數量
				allpage = _banTmpList.size() / 10;
				if ((page > allpage) || (page < 0)) {
					page = 0;
				}

				if ((_banTmpList.size() % 10) != 0) {
					allpage += 1;
				}

				_pc.get_other().set_page(page);// 設置頁面

				final int or = page * 10;

				int i = 0;// 檢查號
				for (final Integer key : _banTmpList.keySet()) {
					if ((i >= or) && (i < (or + 10))) {
						final String banIp = _banTmpList.get(key);
						if (banIp != null) {
							stringBuilder.append(banIp + ",");
						}
					}
					i++;
				}
			}

			if (allpage >= (page + 1)) {
				final String[] clientStrAry = stringBuilder.toString().split(",");
				final int length = clientStrAry.length - 1;
				if (_mode == 2) {
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_GmE", clientStrAry));

				} else if (_mode == 3) {
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_GmE", clientStrAry));

				} else {
					if (length > 0) {
						_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_Gm" + length, clientStrAry));
					}
				}

			} else {
				// $6157 沒有可以顯示的項目
				_pc.sendPackets(new S_ServerMessage("沒有可以顯示的項目"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
