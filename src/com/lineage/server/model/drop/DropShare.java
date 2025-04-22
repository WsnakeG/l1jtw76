package com.lineage.server.model.drop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigBoxMsg;
// import com.lineage.config.ConfigDropMsg;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemMsgTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.world.World;
//import com.lineage.server.templates.L1Drop;
import com.lineage.server.templates.L1DropMap;
/**
 * NPC掉落物品的分配
 * 
 * @author dexc
 */
public class DropShare implements DropShareExecutor {

	private static final Log _log = LogFactory.getLog(DropShare.class);

	// 正向
	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	private static final int class1 = 1;

	private static final int class2 = 2;

	private static final int class3 = 4;

	private static final int class4 = 8;

	private static final int class5 = 16;

	private static final int class6 = 32;

	private static final int class7 = 64;

	private static final int class8 = 128;
	
	private static final Map<Integer, Integer> classflg = new HashMap<Integer, Integer>();
	static {
		classflg.put(0, class1);
		classflg.put(1, class2);
		classflg.put(2, class3);
		classflg.put(3, class4);
		classflg.put(4, class5);
		classflg.put(5, class6);
		classflg.put(6, class7);
		classflg.put(7, class8);
	}
	
	/**
	 * 掉落物品的分配
	 * 
	 * @param npc 死亡的NPC
	 * @param acquisitorList
	 * @param hateList
	 */
	@Override
	public void dropShare(final L1NpcInstance npc, final ArrayList<L1Character> acquisitorList,
			final ArrayList<Integer> hateList) {
		final DropShareR dropShareR = new DropShareR(npc, acquisitorList, hateList);
		GeneralThreadPool.get().schedule(dropShareR, 0);
	}

	private class DropShareR implements Runnable {

		final L1NpcInstance _npc;
		final ArrayList<L1Character> _acquisitorList;
		final ArrayList<Integer> _hateList;

		private DropShareR(final L1NpcInstance npc, final ArrayList<L1Character> acquisitorList,
				final ArrayList<Integer> hateList) {
			_npc = npc;
			_acquisitorList = acquisitorList;
			_hateList = hateList;
		}

		@Override
		public void run() {
			try {
				// _log.info("NPC掉落物品的分配: " + _npc.getName());
				final L1Inventory inventory = _npc.getInventory();
				if (inventory == null) {
					return;
				}
				if (inventory.getSize() <= 0) {
					return;
				}
				if (_acquisitorList.size() != _hateList.size()) {
					return;
				}
				// ヘイトの合計を取得
				int totalHate = 0;
				L1Character acquisitor;
				for (int i = _hateList.size() - 1; i >= 0; i--) {
					acquisitor = _acquisitorList.get(i);

					if ((ConfigAlt.AUTO_LOOT == 2) // オートルーティング２の場合はサモン及びペットは省く
							&& ((acquisitor instanceof L1SummonInstance)
									|| (acquisitor instanceof L1PetInstance))) {
						_acquisitorList.remove(i);
						_hateList.remove(i);

					} else if ((acquisitor != null) && (acquisitor.getMapId() == _npc.getMapId())
							&& (acquisitor.getLocation()
									.getTileLineDistance(_npc.getLocation()) <= ConfigAlt.LOOTING_RANGE)) {
						totalHate += _hateList.get(i);

					} else {
						_acquisitorList.remove(i);
						_hateList.remove(i);
					}
				}

				// 掉落物品的分配
				L1Inventory targetInventory = null;
				L1PcInstance player;
				final Random random = new Random();
				int randomInt;
				int chanceHate;
				int itemId;
				final List<L1ItemInstance> list = inventory.getItems();

				if (list.isEmpty()) {
					return;
				}

				if (list.size() <= 0) {
					return;
				}
				for (final L1ItemInstance item : list) {
					if (item.get_power_name() != null) {
						CharItemPowerReading.get().storeItem(item.getId(), item.get_power_name());
					}
					itemId = item.getItemId();

					if ((item.getItem().getType2() == 0) && (item.getItem().getType() == 2)) { // 照明道具
						item.setNowLighting(false);
					}

					if (((ConfigAlt.AUTO_LOOT != 0) || (itemId == L1ItemId.ADENA)) && (totalHate > 0)) {
						randomInt = random.nextInt(totalHate);
						chanceHate = 0;
						boolean classok = true;
						for (int j = _hateList.size() - 1; j >= 0; j--) {
							Thread.sleep(1);
							chanceHate += _hateList.get(j);
							if (chanceHate > randomInt) {
								acquisitor = _acquisitorList.get(j);

								if (acquisitor instanceof L1PcInstance) {
								L1PcInstance players = (L1PcInstance) acquisitor;
										final int mapid = _npc.getMapId();
										HashMap<Integer, ArrayList<L1DropMap>> droplistX = SetDrop._droplistX.get(mapid);
										if (droplistX != null) {
										for (Object key : droplistX.keySet()) {
										ArrayList<L1DropMap> lists = droplistX.get(key);
										if (lists != null) {
										for (final L1DropMap drop : lists) {
											if(drop.getItemid()==itemId){
											final Integer flg = classflg.get(players.getType());
											
											if (flg != null) {
												classok = (0 != (drop.getclassis() & flg));
											}

											if(!classok){
											break;
											}
											}
										}
										}
											if(!classok){
											break;
											}
										}
										}
										if(!classok){
											continue;
										}
								}
								
								if (acquisitor.getInventory().checkAddItem(item,
										item.getCount()) == L1Inventory.OK) {
									targetInventory = acquisitor.getInventory();
									if (acquisitor instanceof L1PcInstance) {
										player = (L1PcInstance) acquisitor;

										// 具有隊伍
										if (player.isInParty()) {
											final Object[] pcs = player.getParty().getMemberList()
													.toArray();
											if (pcs.length <= 0) {
												return;
											}
											for (final Object obj : pcs) {
												if (obj instanceof L1PcInstance) {
													final L1PcInstance tgpc = (L1PcInstance) obj;
													// 813 隊員%2%s 從%0 取得 %1%o
													tgpc.sendPackets(
															new S_ServerMessage(813, _npc.getNameId(),
																	item.getLogName(), player.getName()));
												}

											}

										} else {
											// 143 \f1%0%s 給你 %1%o 。
											player.sendPackets(new S_ServerMessage(143, _npc.getNameId(),
													item.getLogName()));
										}

										if (ConfigBoxMsg.ISMSG) {
											if (ItemMsgTable.get().contains(item.getItemId())) {
												ConfigBoxMsg.msg(player.getName(), _npc.getNameId(),
														item.getLogName());
												// 打寶特效 2015/11/22
												final S_SkillSound sound = new S_SkillSound(player.getId(),
														441);
												player.sendPacketsX8(sound);
												WriteLogTxt.Recording("道具打寶記錄",
														"IP:(" + player.getNetConnection().getIp() + ")"
																+ "帳號:【" + player.getAccountName() + "】"
																+ "角色:【" + player.getName() + "】" + "經由NPC:【"
																+ _npc.getName() + "】" + "取得:【"
																+ item.getLogName() + "】" + "OBJID:【"
																+ item.getId() + "】");

											}
										}
									}
								} else {
									item.set_showId(_npc.get_showId());
									targetInventory = World.get().getInventory(acquisitor.getX(),
											acquisitor.getY(), acquisitor.getMapId()); // 持てないので足元に落とす
								}
								break;
							}
						}
						
						if(!classok){
							continue;
						}

					} else {
						final List<Integer> dirList = new ArrayList<Integer>();
						for (int j = 0; j < 8; j++) {
							dirList.add(j);
						}
						int x = 0;
						int y = 0;
						int dir = 0;
						do {
							if (dirList.size() == 0) {
								x = 0;
								y = 0;
								break;
							}
							randomInt = random.nextInt(dirList.size());
							dir = dirList.get(randomInt);
							dirList.remove(randomInt);

							x = HEADING_TABLE_X[dir];
							y = HEADING_TABLE_Y[dir];
							Thread.sleep(1);

						} while (!_npc.getMap().isPassable(_npc.getX(), _npc.getY(), dir, null));
						item.set_showId(_npc.get_showId());
						targetInventory = World.get().getInventory(_npc.getX() + x, _npc.getY() + y,
								_npc.getMapId());
						ListMapUtil.clear(dirList);
					}

					inventory.tradeItem(item, item.getCount(), targetInventory);
				}
				ListMapUtil.clear(list);
				// _npc.turnOnOffLight();

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				// 移除此 ArrayList 中的所有元素
				ListMapUtil.clear(_acquisitorList);
				ListMapUtil.clear(_hateList);
			}
		}
	}
}