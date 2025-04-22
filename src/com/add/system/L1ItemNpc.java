/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package com.add.system;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.DatabaseFactory;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_GreenMessage;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.Random;
import com.lineage.server.world.World;

public class L1ItemNpc {

	private static Logger _log = Logger.getLogger(L1ItemNpc.class.getName());
	private static boolean NO_MORE_GET_DATA5 = false;
	private static ArrayList<int[]> aData5 = new ArrayList<int[]>();

	public static void forRequestItemUSe(ClientExecutor client,
			L1ItemInstance itemInstance) {
		L1PcInstance user = client.getActiveChar();
		int aTempData[] = null;
		int iX = user.getX();
		int iY = user.getY();
		int iMap = user.getMapId();
		int iX1 = 0;
		int iY1 = 0;
		int iX2 = 0;
		int iY2 = 0;
		short iMap2 = 0;
		boolean found = false;
		int itemid = itemInstance.getItemId();
		if (!NO_MORE_GET_DATA5) {
			NO_MORE_GET_DATA5 = true;
			getData5();
		}
		
		for (int i = 0; i < aData5.size(); i++) {
			aTempData = aData5.get(i);
			iX1 = aTempData[5];
			iY1 = aTempData[6];
			iX2 = aTempData[7];
			iY2 = aTempData[8];
			iMap2 = (short) aTempData[10];
			if (aTempData[1] == itemid) {
				if (user.getInventory().checkItem(aTempData[1], aTempData[2])) {
					try {
						if (iMap2 != -1) {
							if (iMap != iMap2) {
								user.sendPackets(new S_SystemMessage(
										"無法此在地圖使用。"));
								return;
							}
						}
						if (iX1 != -1 && iX2 != -1 && iY1 != -1 && iY2 != -1) {
							if (!(iX >= iX1 && iX <= iX2 && iY >= iY1 && iY <= iY2)) {
								user.sendPackets(new S_SystemMessage(
										"無法此在座標使用。"));
								return;
							}
						}
						if (aTempData[0] != 0) { // 職業判斷
							byte class_id = (byte) 0;
							String msg = "";
							if (user.isCrown()) { // 王族
								class_id = 1;
							} else if (user.isKnight()) { // 騎士
								class_id = 2;
							} else if (user.isWizard()) { // 法師
								class_id = 3;
							} else if (user.isElf()) { // 妖精
								class_id = 4;
							} else if (user.isDarkelf()) { // 黑妖
								class_id = 5;
							} else if (user.isDragonKnight()) { // 龍騎士
								class_id = 6;
							} else if (user.isIllusionist()) { // 幻術師
								class_id = 7;
							} else if (user.isWarrior()) { // 戰士
								class_id = 8;
							}
							switch (aTempData[0]) {
							case 1:
								msg = "王族";
								break;
							case 2:
								msg = "騎士";
								break;
							case 3:
								msg = "法師";
								break;
							case 4:
								msg = "妖精";
								break;
							case 5:
								msg = "黑暗妖精";
								break;
							case 6:
								msg = "龍騎士";
								break;
							case 7:
								msg = "幻術師";
								break;
							case 8:
								msg = "戰士";
								break;
							}
							if (aTempData[0] != class_id) { // 職業不符
								user.sendPackets(new S_ServerMessage(166, "職業必須是" + msg + "才能使用此道具"));
								return;
							}
						}
						for (L1Object obj : World.get().getObject()) {
							if (obj instanceof L1MonsterInstance) {
								L1MonsterInstance mob = (L1MonsterInstance) obj;
								if (mob != null && aTempData[12] != 0) {
									if (mob.getNpcTemplate().get_npcId() == aTempData[4]) {
										found = true;
										break;
									}
								}
							}
						}
						
						if (found) {
							user.sendPackets(new S_ServerMessage(79)); // \f1沒有任何事情發生。
						} else {
							mobspawn(user, aTempData[4], aTempData[9],
									aTempData[11], aTempData[13], aTempData[14]); // 召喚怪物
							
							String npcname = NpcTable.get().getNpcName(aTempData[4]);							
							World.get().broadcastPacketToAll(new S_GreenMessage(npcname + "出現了。"));
							
							//for (L1PcInstance listner : World.get().getVisiblePlayer(user, 50)) {//50格範圍內
								//if (user.get_showId() == listner.get_showId()) {//副本編號相等
									//listner.sendPackets(new S_GreenMessage(npcname + "出現了。"));
							    //}
							//}
							
						}
						
						if (aTempData[3] == 1 && !found) { // 召喚成功，移除道具
							for (int j = 0; j < aTempData[2]; j++) {
								L1ItemInstance item = user.getInventory()
										.findItemId(aTempData[1]);
								user.getInventory().removeItem(item.getId(), 1);
							}
						}
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				}
			}
		}
	}

	private static void getData5() {
		java.sql.Connection con = null;
		try {
			con = DatabaseFactory.get().getConnection();
			Statement stat = con.createStatement();
			ResultSet rset = stat.executeQuery("SELECT * FROM 道具召喚系統");
			int[] aReturn = null;
			if (rset != null)
				while (rset.next()) {
					aReturn = new int[15];
					aReturn[0] = rset.getInt("class");
					aReturn[1] = rset.getInt("materials");
					aReturn[2] = rset.getInt("counts");
					aReturn[3] = rset.getInt("destroy");
					aReturn[4] = rset.getInt("monster_id");
					aReturn[5] = rset.getInt("location_minx");
					aReturn[6] = rset.getInt("location_miny");
					aReturn[7] = rset.getInt("location_maxx");
					aReturn[8] = rset.getInt("location_maxy");
					aReturn[9] = rset.getInt("location_area");
					aReturn[10] = rset.getInt("map_id");
					aReturn[11] = rset.getInt("delete_time");
					aReturn[12] = rset.getInt("found");
					aReturn[13] = rset.getInt("spawn_x");
					aReturn[14] = rset.getInt("spawn_y");
					aData5.add(aReturn);
				}
			if (con != null && !con.isClosed())
				con.close();
		} catch (Exception ex) {

		}
	}
	
	private static void mobspawn(L1PcInstance pc, int npcId, int randomRange,
			int timeMillisToDelete, int spawnx, int spawny) {
		try {
			L1NpcInstance npc = NpcTable.get().newNpcInstance(npcId);
			npc.setId(IdFactory.get().nextId());
			npc.setMap(pc.getMapId());
			npc.set_showId(pc.get_showId());//設置副本編號
			
			if (randomRange == 0) {
				if ((spawnx > 0) && (spawny > 0)) {//具有指定出生座標
					npc.setX(spawnx);
					npc.setY(spawny);					
				} else {
					npc.getLocation().set(pc.getLocation());
					npc.getLocation().forward(pc.getHeading());					
				}
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(pc.getX() + Random.nextInt(randomRange)
							- Random.nextInt(randomRange));
					npc.setY(pc.getY() + Random.nextInt(randomRange)
							- Random.nextInt(randomRange));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation(), npc)) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);
				if (tryCount >= 50) {
					npc.getLocation().set(pc.getLocation());
					npc.getLocation().forward(pc.getHeading());
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(5);

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			int gfx = npc.getTempCharGfx();
			switch (gfx) {
				case 7548:
				case 7550:
				case 7552:
				case 7554:
				case 7585:
				case 7591:			
				case 7840:
				case 8096:
					  npc.broadcastPacketAll(new S_NPCPack(npc));
					  npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 11));
					  break;
				case 7539:
				case 7557:
				case 7558:
				case 7864:
				case 7869:
				case 7870:
				case 8036:
				case 8054:
				case 8055:
				for (L1PcInstance _pc : World.get().getVisiblePlayer(npc, 50)) {
			          if (npc.getTempCharGfx() == 7539) {
			            _pc.sendPackets(new S_ServerMessage(1570));
			          }
			          else if (npc.getTempCharGfx() == 7864) {
			            _pc.sendPackets(new S_ServerMessage(1657));
			          }
			          else if (npc.getTempCharGfx() == 8036) {
			            _pc.sendPackets(new S_ServerMessage(1755));
			          }
			        }
				  	npc.broadcastPacketAll(new S_NPCPack(npc));
			        npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 11)); 				      
			        break;
				case 145:
				case 3957:
				case 3969: 
				case 3989:
				case 3984:
				case 3547:
				case 3566:
				case 2158:
				case 7719:
				case 10071:
				case 11465:
				case 11467:
					npc.broadcastPacketAll(new S_NPCPack(npc));
					npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 4));				        
			        break;
				case 30:
					npc.broadcastPacketAll(new S_NPCPack(npc));
					npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 30));				        
			        break;
															
			}

			npc.turnOnOffLight();
				
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
			
			if (0 < timeMillisToDelete) {
				// 存在時間(秒)
				npc.set_spawnTime(timeMillisToDelete);
			}
			
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
