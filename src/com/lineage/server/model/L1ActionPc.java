package com.lineage.server.model;

import java.io.IOException;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nick.AutoControl.AutoAttackUpdate;
import nick.forMYSQL.ControlBuffNumber;
import nick.forMYSQL.ControlTeleport;
import nick.forMYSQL.ControlTeleportNumber;
import nick.forMYSQL.ControlTeleportTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.npc.teleport.Npc_Teleport;
import com.lineage.server.command.GMCommands;
import com.lineage.server.command.executor.L1CreateItem;
import com.lineage.server.datatables.CommandsTable;
import com.lineage.server.datatables.DropTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.datatables.QuestTable;
import com.lineage.server.datatables.lock.CharacterQuestReading;
import com.lineage.server.datatables.lock.SpawnBossReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.model.skill.skillmode.SUMMON_MONSTER;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_Bonusstats;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_Lock;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShopSellListCnX;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.templates.L1Command;
import com.lineage.server.templates.L1Quest;
import com.lineage.server.templates.L1TeleportLoc;
import com.lineage.server.world.World;

/**
 * 對話命令來自PC的執行與判斷
 * 
 * @author daien
 */
public class L1ActionPc {

	private static final Log _log = LogFactory.getLog(L1ActionPc.class);

	private final L1PcInstance _pc;

	/**
	 * 對話命令來自PC的執行與判斷
	 * 
	 * @param pc
	 *            執行者
	 */
	public L1ActionPc(final L1PcInstance pc) {
		_pc = pc;
	}

	/**
	 * 傳回執行命令者
	 * 
	 * @return
	 */
	public L1PcInstance get_pc() {
		return _pc;
	}

	/**
	 * 選單命令執行
	 * 
	 * @param cmd
	 * @param amount
	 */
	public void action(final String cmd, final long amount) {
		try {
			if (cmd.matches("[0-9]+")) {
				// 解除GM管理狀態
				_pc.get_other().set_gmHtml(null);
				// 展開召喚控制選單
				if (_pc.isSummonMonster()) {
					summonMonster(_pc, cmd);
					_pc.setShapeChange(false);
					_pc.setSummonMonster(false);
					return;
				}
			}

			// 展開變身控制選單
			if (_pc.isShapeChange()) {
				// 解除GM管理狀態
				_pc.get_other().set_gmHtml(null);
				L1PolyMorph.handleCommands(_pc, cmd);
				_pc.setShapeChange(false);
				_pc.setSummonMonster(false);
				return;
			}

			// GM選單不為空
			if (_pc.get_other().get_gmHtml() != null) {
				_pc.get_other().get_gmHtml().action(cmd);
				return;
			}

			// 解除GM管理狀態
			_pc.get_other().set_gmHtml(null);

			// 任務選單 FIXME
			if (cmd.equalsIgnoreCase("power")) {// 能力選取視窗
				// 判斷是否出現能力選取視窗
				if (_pc.power()) {
					_pc.sendPackets(new S_Bonusstats(_pc.getId()));
				}
			}
			
			if (AutoAttackUpdate.get().PcCommand(_pc, cmd)) {
				return;
			}

			// else if (cmd.equalsIgnoreCase("htmlDmgOpen")) {
			// _pc.set_is_attack_view(true);
			// _pc.sendPackets(new S_ServerMessage("\\aG已開啟了\\aI傷害顯示！"));
			// } else if (cmd.equalsIgnoreCase("htmlDmgClose")) {
			// _pc.set_is_attack_view(false);
			// _pc.sendPackets(new S_ServerMessage("\\aD已關閉了\\aE傷害顯示"));
			// }

			// else if (cmd.equalsIgnoreCase("htmlShop")) {// 道具商城
			// _pc.sendPackets(new S_ShopSellListCnX(_pc, _pc.getId()));
			// }
			final String[] aaa = ControlTeleportNumber.aaa.split(",");
			final String[] bbb = ControlTeleportNumber.bbb.split(",");
			final String[] ccc = ControlTeleportNumber.ccc.split(",");
			final String[] ddd = ControlTeleportNumber.ddd.split(",");
			final String[] eee = ControlTeleportNumber.eee.split(",");
			final String[] fff = ControlTeleportNumber.fff.split(",");
			final String[] ggg = ControlTeleportNumber.ggg.split(",");
			final String[] hhh = ControlTeleportNumber.hhh.split(",");
			final String[] iii = ControlTeleportNumber.iii.split(",");
			final String[] jjj = ControlTeleportNumber.jjj.split(",");
			final String[] kkk = ControlTeleportNumber.kkk.split(",");
			final String[] lll = ControlTeleportNumber.lll.split(",");
			final String[] mmm = ControlTeleportNumber.mmm.split(",");
			final String[] nnn = ControlTeleportNumber.nnn.split(",");
			final String[] ooo = ControlTeleportNumber.ooo.split(",");

			int[] locA = { Integer.valueOf(aaa[1]), Integer.valueOf(aaa[2]), Integer.valueOf(aaa[3]) };
			int[] locB = { Integer.valueOf(bbb[1]), Integer.valueOf(bbb[2]), Integer.valueOf(bbb[3]) };
			int[] locC = { Integer.valueOf(ccc[1]), Integer.valueOf(ccc[2]), Integer.valueOf(ccc[3]) };
			int[] locD = { Integer.valueOf(ddd[1]), Integer.valueOf(ddd[2]), Integer.valueOf(ddd[3]) };
			int[] locE = { Integer.valueOf(eee[1]), Integer.valueOf(eee[2]), Integer.valueOf(eee[3]) };
			int[] locF = { Integer.valueOf(fff[1]), Integer.valueOf(fff[2]), Integer.valueOf(fff[3]) };
			int[] locG = { Integer.valueOf(ggg[1]), Integer.valueOf(ggg[2]), Integer.valueOf(ggg[3]) };
			int[] locH = { Integer.valueOf(hhh[1]), Integer.valueOf(hhh[2]), Integer.valueOf(hhh[3]) };
			int[] locI = { Integer.valueOf(iii[1]), Integer.valueOf(iii[2]), Integer.valueOf(iii[3]) };
			int[] locJ = { Integer.valueOf(jjj[1]), Integer.valueOf(jjj[2]), Integer.valueOf(jjj[3]) };
			int[] locK = { Integer.valueOf(kkk[1]), Integer.valueOf(kkk[2]), Integer.valueOf(kkk[3]) };
			int[] locL = { Integer.valueOf(lll[1]), Integer.valueOf(lll[2]), Integer.valueOf(lll[3]) };
			int[] locM = { Integer.valueOf(mmm[1]), Integer.valueOf(mmm[2]), Integer.valueOf(mmm[3]) };
			int[] locN = { Integer.valueOf(nnn[1]), Integer.valueOf(nnn[2]), Integer.valueOf(nnn[3]) };
			int[] locO = { Integer.valueOf(ooo[1]), Integer.valueOf(ooo[2]), Integer.valueOf(ooo[3]) };

			
			
			if (cmd.equalsIgnoreCase("aaa")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(aaa[4]), Integer.valueOf(aaa[5]))) {
					L1Teleport.teleport(_pc, locA[0], locA[1], (short) locA[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("bbb")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(bbb[4]), Integer.valueOf(bbb[5]))) {
					L1Teleport.teleport(_pc, locB[0], locB[1], (short) locB[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("ccc")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(ccc[4]), Integer.valueOf(ccc[5]))) {
					L1Teleport.teleport(_pc, locC[0], locC[1], (short) locC[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("ddd")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(ddd[4]), Integer.valueOf(ddd[5]))) {
					L1Teleport.teleport(_pc, locD[0], locD[1], (short) locD[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("eee")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(eee[4]), Integer.valueOf(eee[5]))) {
					L1Teleport.teleport(_pc, locE[0], locE[1], (short) locE[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("fff")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(fff[4]), Integer.valueOf(fff[5]))) {
					L1Teleport.teleport(_pc, locF[0], locF[1], (short) locF[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("ggg")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(ggg[4]), Integer.valueOf(ggg[5]))) {
					L1Teleport.teleport(_pc, locG[0], locG[1], (short) locG[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("hhh")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(hhh[4]), Integer.valueOf(hhh[5]))) {
					L1Teleport.teleport(_pc, locH[0], locH[1], (short) locH[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("iii")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(iii[4]), Integer.valueOf(iii[5]))) {
					L1Teleport.teleport(_pc, locI[0], locI[1], (short) locI[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("jjj")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(jjj[4]), Integer.valueOf(jjj[5]))) {
					L1Teleport.teleport(_pc, locJ[0], locJ[1], (short) locJ[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("kkk")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(kkk[4]), Integer.valueOf(kkk[5]))) {
					L1Teleport.teleport(_pc, locK[0], locK[1], (short) locK[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("lll")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(lll[4]), Integer.valueOf(lll[5]))) {
					L1Teleport.teleport(_pc, locL[0], locL[1], (short) locL[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("mmm")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(mmm[4]), Integer.valueOf(mmm[5]))) {
					L1Teleport.teleport(_pc, locM[0], locM[1], (short) locM[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("nnn")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(nnn[4]), Integer.valueOf(nnn[5]))) {
					L1Teleport.teleport(_pc, locN[0], locN[1], (short) locN[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("ooo")) {
				if (_pc.getInventory().consumeItem(Integer.valueOf(ooo[4]), Integer.valueOf(ooo[5]))) {
					L1Teleport.teleport(_pc, locO[0], locO[1], (short) locO[2], 5, true);
				} else {
					_pc.sendPackets(new S_ServerMessage("傳送失敗, 道具不足"));
				}
			} else if (cmd.equalsIgnoreCase("bosstime_0")) {   //新增BOSS查詢
				final String[] info = new String[30];
				int num = 0;
				for (int id : SpawnBossReading.get().bossreid()) {
					if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 0) continue;
					if (num >= info.length) break;
					if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
						Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
						String time = sdf.format(date);
						if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
							info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
						} else {
							info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
						}
					} else {
						info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
					}
						num++;
					}
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_0", info));
			} else if (cmd.equalsIgnoreCase("bosstime_1")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			    	if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 1) continue;
			    	if (num >= info.length) break;
			    	if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			    		Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			    		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			    		String time = sdf.format(date);
		            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
		                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
		            } else {
		                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
		            }
			    	} else {
			    		info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			    	}
			    	num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_1", info));
			} else if (cmd.equalsIgnoreCase("bosstime_2")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 2) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_2", info));
			} else if (cmd.equalsIgnoreCase("bosstime_3")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 3) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_3", info));
			} else if (cmd.equalsIgnoreCase("bosstime_4")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 4) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_4", info));
			} else if (cmd.equalsIgnoreCase("bosstime_5")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 5) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_5", info));
			} else if (cmd.equalsIgnoreCase("bosstime_6")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 6) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_6", info));
			} else if (cmd.equalsIgnoreCase("bosstime_7")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 7) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_7", info));
			} else if (cmd.equalsIgnoreCase("bosstime_8")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 8) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_8", info));
			} else if (cmd.equalsIgnoreCase("bosstime_9")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 9) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_9", info));
			} else if (cmd.equalsIgnoreCase("bosstime_10")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 10) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_10", info));
			} else if (cmd.equalsIgnoreCase("bosstime_11")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 11) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_11", info));
			} else if (cmd.equalsIgnoreCase("bosstime_12")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 12) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_12", info));
			} else if (cmd.equalsIgnoreCase("bosstime_13")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 13) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_13", info));
			} else if (cmd.equalsIgnoreCase("bosstime_14")) {
			    final String[] info = new String[30];
			    int num = 0;
			    for (int id : SpawnBossReading.get().bossreid()) {
			        if (classifyMapId(SpawnBossReading.get().getTemplate(id).getMapId()) != 14) continue;
			        if (num >= info.length) break;
			        if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime() != null) {
			            Date date = new Date(SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis());
			            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
			            String time = sdf.format(date);
			            if (SpawnBossReading.get().getTemplate(id).get_nextSpawnTime().getTimeInMillis() < System.currentTimeMillis()) {
			                info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			            } else {
			                info[num] = "【★下次重生:" + time + "】" + SpawnBossReading.get().getTemplate(id).getName();
			            }
			        } else {
			            info[num] = "【已重生】" + SpawnBossReading.get().getTemplate(id).getName();
			        }
			        num++;
			    }
			    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosstime_14", info));
			}  //新增BOSS查詢結束

			// String [] haha = { aaa[4], bbb[4], ccc[4], ddd[4], eee[4], fff[4], ggg[4], hhh[4], iii[4], jjj[4], kkk[4], lll[4], mmm[4], nnn[4], ooo[4]};
			// String[] ASDASD = new String[haha.length];
			//
			// for (int i = 0; i < haha.length; i++) {
			// switch(Integer.valueOf(haha[i])){
			// case 40308:
			// ASDASD[i] = "金幣";
			// break;
			// case 44070:
			// ASDASD[i] = "元寶";
			// break;
			// }
			// }

			// for (int i = 0; i < haha.length; i++) {
			// if (Integer.valueOf(haha[i]) == 40308) {
			// ASDASD[i] = "金幣";
			// } else if (Integer.valueOf(haha[i]) == 44070) {
			// ASDASD[i] = "元寶";
			// }else{
			// ASDASD[i] = "??";
			// }
			// }

			String[] asda = { aaa[0], aaa[6], aaa[5], bbb[0], bbb[6], bbb[5], ccc[0], ccc[6], ccc[5], ddd[0], ddd[6], ddd[5], eee[0], eee[6], eee[5], fff[0], fff[6], fff[5], ggg[0],
					ggg[6], ggg[5], hhh[0], hhh[6], hhh[5], iii[0], iii[6], iii[5], jjj[0], jjj[6], jjj[5], kkk[0], kkk[6], kkk[5], lll[0], lll[6], lll[5], mmm[0], mmm[6], mmm[5], nnn[0],
					nnn[6], nnn[5], ooo[0], ooo[6], ooo[5] };
			// String[] asda = { aaa[0], aaa[4], aaa[5], bbb[0], bbb[4], bbb[5], ccc[0], ccc[4], ccc[5], ddd[0], ddd[4], ddd[5], eee[0], eee[4], eee[5], fff[0], fff[4], fff[5], ggg[0],
			// ggg[4], ggg[5], hhh[0], hhh[4], hhh[5], iii[0], iii[4], iii[5], jjj[0], jjj[4], jjj[5], kkk[0], kkk[4], kkk[5], lll[0], lll[4], lll[5], mmm[0], mmm[4], mmm[5], nnn[0],
			// nnn[4], nnn[5], ooo[0], ooo[4], ooo[5] };
			// _log.info(haha.length + ASDASD.length);
			// String[]asda ={aaa[0],bbb[0],ccc[0],ddd[0],eee[0],fff[0],ggg[0],hhh[0],iii[0],jjj[0],kkk[0],lll[0],mmm[0],nnn[0],ooo[0]};
			// _log.info(asda.length);
			if (cmd.equalsIgnoreCase("TC")) {
				String[] itemdatas = new String[asda.length];
				for (int i = 0; i < itemdatas.length; i++) {
					itemdatas[i] = asda[i];
				}
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "TC", itemdatas));
				// _log.info(itemdatas.length);
			}

			if (cmd.equalsIgnoreCase("htmlBuffSelf")) {
				final String[] ABSitem = ControlBuffNumber.SelfConsumeItems.split(",");
				boolean f = false;
				for (int i = 0; i < ABSitem.length - 1; i++) {
					if(_pc.getInventory().checkItem(Integer.valueOf(ABSitem[i]), Integer.valueOf(ABSitem[i + 1]))){
						_pc.getInventory().consumeItem(Integer.valueOf(ABSitem[i]), Integer.valueOf(ABSitem[i + 1]));
						f = true;
					}else{
						_pc.sendPackets(new S_ServerMessage("需求道具不足。"));
					}
				}
				if (f) {
					final String[] ABS = ControlBuffNumber.addBuffForSelf.split(",");
					for (int i = 0; i < ABS.length; i++) {
						new L1SkillUse().handleCommands(_pc, Integer.valueOf(ABS[i]), _pc.getId(), _pc.getX(), _pc.getY(), 0, 4);
					}
				}
			} else if (cmd.equalsIgnoreCase("htmlBuffParty")) {
				final String[] ABPitem = ControlBuffNumber.PartyConsumeItems.split(",");
				boolean f = false;
				for (int i = 0; i < ABPitem.length - 1; i++) {
					if(_pc.getInventory().checkItem(Integer.valueOf(ABPitem[i]), Integer.valueOf(ABPitem[i + 1]))){
					_pc.getInventory().consumeItem(Integer.valueOf(ABPitem[i]), Integer.valueOf(ABPitem[i + 1]));
					f = true;
					}else{
						_pc.sendPackets(new S_ServerMessage("需求道具不足。"));
					}
				}
				if (f) {
					final String[] ABP = ControlBuffNumber.addBuffForParty.split(",");
					final L1Party party = _pc.getParty();
					if (party == null) {
						_pc.sendPackets(new S_ServerMessage("你當前沒有隊伍。"));
						return;
					}
					for (L1PcInstance tgpc : World.get().getAllPlayers()) {
						if (party.isMember(tgpc)) {
							for (int i = 0; i < ABP.length; i++) {
								new L1SkillUse().handleCommands(tgpc, Integer.valueOf(ABP[i]), _pc.getId(), _pc.getX(), _pc.getY(), 0, 4);
							}
						}
					}
				}
			} else if (cmd.equalsIgnoreCase("htmlBuffClan")) {
				final String[] ABCitem = ControlBuffNumber.ClanConsumeItems.split(",");
				boolean f = false;
				for (int i = 0; i < ABCitem.length - 1; i++) {
					if(_pc.getInventory().checkItem(Integer.valueOf(ABCitem[i]), Integer.valueOf(ABCitem[i + 1]))){
					_pc.getInventory().consumeItem(Integer.valueOf(ABCitem[i]), Integer.valueOf(ABCitem[i + 1]));
					f = true;
					}else{
						_pc.sendPackets(new S_ServerMessage("需求道具不足。"));
					}
				}
				if (f) {
					final String[] ABC = ControlBuffNumber.addBuffForClan.split(",");
					final int Clanid = _pc.getClanid();
					if (Clanid == 0) {
						_pc.sendPackets(new S_ServerMessage("未加入血盟無法使用。"));
						return;
					}
					for (L1PcInstance tgpc : World.get().getAllPlayers()) {
						if (tgpc.getClanid() == _pc.getClanid()) {
							for (int i = 0; i < ABC.length; i++) {
								new L1SkillUse().handleCommands(tgpc, Integer.valueOf(ABC[i]), _pc.getId(), _pc.getX(), _pc.getY(), 0, 4);
							}
						}
					}
				}
			} else if (cmd.equalsIgnoreCase("htmlBuffAll")) {
				final String[] ABAitem = ControlBuffNumber.AllConsumeItems.split(",");
				boolean f = false;
				for (int i = 0; i < ABAitem.length - 1; i++) {
					if(_pc.getInventory().checkItem(Integer.valueOf(ABAitem[i]), Integer.valueOf(ABAitem[i + 1]))){
					_pc.getInventory().consumeItem(Integer.valueOf(ABAitem[i]), Integer.valueOf(ABAitem[i + 1]));
					f = true;
					}else{
						_pc.sendPackets(new S_ServerMessage("需求道具不足。"));
					}
				}
				if (f) {
					final String[] ABA = ControlBuffNumber.addBuffForAll.split(",");
					for (L1PcInstance tgpc : World.get().getAllPlayers()) {
							for (int i = 0; i < ABA.length; i++) {
								new L1SkillUse().handleCommands(tgpc, Integer.valueOf(ABA[i]), _pc.getId(), _pc.getX(), _pc.getY(), 0, 4);
							}
					}
				}
			}
			
			
			


			if (cmd.equalsIgnoreCase("htmlArrowOpen")) {
				_pc.set_showWearingCloudsArrows(true);
				_pc.sendPackets(new S_ServerMessage("\\aD已開啟了\\aI穿雲通知！"));
			} else if (cmd.equalsIgnoreCase("htmlArrowClose")) {
				_pc.set_showWearingCloudsArrows(false);
				_pc.sendPackets(new S_ServerMessage("\\aG已關閉了\\aE穿雲通知！"));
			}
			if (cmd.equalsIgnoreCase("index")) {// 任務查詢系統
				_pc.isWindows();

			} else if (cmd.equalsIgnoreCase("locerr01")) {// 解除人物卡點 帳號
				_pc.set_unfreezingTime(10);

			} else if (cmd.equalsIgnoreCase("locerr02")) {// 修正人物錯位 當前
				_pc.sendPackets(new S_Lock());

			} else if (cmd.equalsIgnoreCase("supertele01")) {// 查看全部任務
				if (_pc.getInventory().consumeItem(44070, 30000)) {
					L1Teleport.teleport(_pc, 32767, 32767, (short) 4, 5, true);
				} else {
					// 189：\f1金幣不足。
					_pc.sendPackets(new S_ServerMessage("傳送失敗，商城幣不足"));
				}
			} else if (cmd.equalsIgnoreCase("qt")) {// 查看執行中任務
				showStartQuest(_pc, _pc.getId());

			} else if (cmd.equalsIgnoreCase("quest")) {// 查看可執行任務
				showQuest(_pc, _pc.getId());

			} else if (cmd.equalsIgnoreCase("questa")) {// 查看全部任務
				showQuestAll(_pc, _pc.getId());

			} else if (cmd.equalsIgnoreCase("i")) {// 任務介紹
				final L1Quest quest = QuestTable.get().getTemplate(_pc.getTempID());
				_pc.setTempID(0);
				// 確認該任務存在
				if (quest == null) {
					return;
				}
				QuestClass.get().showQuest(_pc, quest.get_id());

			} else if (cmd.equalsIgnoreCase("d")) {// 任務回收
				final L1Quest quest = QuestTable.get().getTemplate(_pc.getTempID());
				_pc.setTempID(0);
				// 確認該任務存在
				if (quest == null) {
					return;
				}
				// 任務已經完成
				if (_pc.getQuest().isEnd(quest.get_id())) {
					questDel(quest);
					return;
				}
				// 任務尚未開始
				if (!_pc.getQuest().isStart(quest.get_id())) {
					// 很抱歉!!你並未開始執行這個任務!
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_q_not6"));
					return;
				}
				// 執行中 未完成任務
				questDel(quest);

			} else if (cmd.equalsIgnoreCase("dy")) {// 任務移除
				final L1Quest quest = QuestTable.get().getTemplate(_pc.getTempID());
				_pc.setTempID(0);
				// 確認該任務存在
				if (quest == null) {
					return;
				}
				// 任務已經完成
				if (_pc.getQuest().isEnd(quest.get_id())) {
					isDel(quest);
					return;
				}
				// 任務尚未開始
				if (!_pc.getQuest().isStart(quest.get_id())) {
					// 很抱歉!!你並未開始執行這個任務!
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_q_not6"));
					return;
				}
				// 執行中 未完成任務
				isDel(quest);

			} else if (cmd.equalsIgnoreCase("up")) {// 上一頁(管理)
				final int page = _pc.get_other().get_page() - 1;
				final L1ActionShowHtml show = new L1ActionShowHtml(_pc);
				show.showQuestMap(page);

			} else if (cmd.equalsIgnoreCase("dn")) {// 下一頁(管理)
				final int page = _pc.get_other().get_page() + 1;
				final L1ActionShowHtml show = new L1ActionShowHtml(_pc);
				show.showQuestMap(page);

			} else if (cmd.equalsIgnoreCase("q0")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 0;
				showPage(key);

			} else if (cmd.equalsIgnoreCase("q1")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 1;
				showPage(key);

			} else if (cmd.equalsIgnoreCase("q2")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 2;
				showPage(key);

			} else if (cmd.equalsIgnoreCase("q3")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 3;
				showPage(key);

			} else if (cmd.equalsIgnoreCase("q4")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 4;
				showPage(key);

			} else if (cmd.equalsIgnoreCase("q5")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 5;
				showPage(key);

			} else if (cmd.equalsIgnoreCase("q6")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 6;
				showPage(key);

			} else if (cmd.equalsIgnoreCase("q7")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 7;
				showPage(key);

			} else if (cmd.equalsIgnoreCase("q8")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 8;
				showPage(key);

			} else if (cmd.equalsIgnoreCase("q9")) {// 頁面內指定位置
				final int key = (_pc.get_other().get_page() * 10) + 9;
				showPage(key);

			}

			// else {
			// GMCommands.getInstance().handleCommands(_pc, cmd, false);
			// }

			else if (cmd.equalsIgnoreCase("htmlWeaponGfxOpen")) {
				_pc.set_send_weapon_gfxid(true);
				_pc.sendPackets(new S_ServerMessage("\\aD已開啟了\\aI武器特效！"));

			} else if (cmd.equalsIgnoreCase("htmlWeaponGfxClose")) {
				_pc.set_send_weapon_gfxid(false);
				_pc.sendPackets(new S_ServerMessage("\\aG已關閉了\\aE武器特效！"));

			} else if (cmd.equalsIgnoreCase("broadcast_open")) {
				_pc.set_broadcast(true);
				_pc.sendPackets(new S_ServerMessage("\\aD已開啟了\\aI公告通知！"));

			} else if (cmd.equalsIgnoreCase("broadcast_close")) {
				_pc.set_broadcast(false);
				_pc.sendPackets(new S_ServerMessage("\\aG已關閉了\\aE公告通知！"));

			} else if (cmd.equalsIgnoreCase("teleport_open")) {
				_pc.set_ask_teleport(true);
				_pc.sendPackets(new S_ServerMessage("\\aD已開啟了\\aI集傳通知！"));

			} else if (cmd.equalsIgnoreCase("teleport_close")) {
				_pc.set_ask_teleport(false);
				_pc.sendPackets(new S_ServerMessage("\\aG已關閉了\\aE集傳通知！"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 任務解除執行
	 * 
	 * @param quest
	 */
	private void questDel(final L1Quest quest) {
		try {
			if (quest.is_del()) {
				_pc.setTempID(quest.get_id());
				String over = null;
				// 該任務完成
				if (_pc.getQuest().isEnd(quest.get_id())) {
					over = "完成任務";// 完成任務!
				} else {
					over = _pc.getQuest().get_step(quest.get_id()) + " / " + quest.get_difficulty();
				}

				final String[] info = new String[] { quest.get_questname(), // 任務名稱
						Integer.toString(quest.get_questlevel()), // 任務等級
						over, // 任務進度
				// 額外說明
				};
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qi2", info));

			} else {
				// 任務不可刪除
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_q_not5"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 確定解除任務執行
	 * 
	 * @param quest
	 */
	private void isDel(final L1Quest quest) {
		try {
			if (quest.is_del()) {
				// 任務終止
				QuestClass.get().stopQuest(_pc, quest.get_id());

				CharacterQuestReading.get().delQuest(_pc.getId(), quest.get_id());
				final String[] info = new String[] { quest.get_questname(), // 任務名稱
						Integer.toString(quest.get_questlevel()), // 任務等級
				};
				// 刪除任務
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qi3", info));

			} else {
				// 任務不可刪除
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_q_not5"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 查看執行中任務
	 * 
	 * @param pc
	 * @param id
	 */
	public static void showStartQuest(final L1PcInstance pc, final int objid) {
		try {
			// 清空暫存任務清單
			pc.get_otherList().QUESTMAP.clear();

			int key = 0;
			for (int i = QuestTable.MINQID; i <= QuestTable.MAXQID; i++) {
				final L1Quest value = QuestTable.get().getTemplate(i);
				if (value != null) {
					// 該任務已經結束
					if (pc.getQuest().isEnd(value.get_id())) {
						continue;
					}
					// 執行中任務判斷
					if (pc.getQuest().isStart(value.get_id())) {
						pc.get_otherList().QUESTMAP.put(key++, value);
					}
				}
			}

			if (pc.get_otherList().QUESTMAP.size() <= 0) {
				// 很抱歉!!你並沒有任何執行中的任務!
				pc.sendPackets(new S_NPCTalkReturn(objid, "y_q_not7"));

			} else {
				final L1ActionShowHtml show = new L1ActionShowHtml(pc);
				show.showQuestMap(0);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 可執行任務
	 * 
	 * @param pc
	 * @param objid
	 */
	public static void showQuest(final L1PcInstance pc, final int objid) {
		try {
			// 清空暫存任務清單
			pc.get_otherList().QUESTMAP.clear();

			int key = 0;

			for (int i = QuestTable.MINQID; i <= QuestTable.MAXQID; i++) {
				final L1Quest value = QuestTable.get().getTemplate(i);
				if (value != null) {
					// 大於可執行等級
					if (pc.getLevel() >= value.get_questlevel()) {
						// 該任務已經結束
						if (pc.getQuest().isEnd(value.get_id())) {
							continue;
						}
						// 該任務已經開始
						if (pc.getQuest().isStart(value.get_id())) {
							continue;
						}
						// 可執行職業判斷
						if (value.check(pc)) {
							pc.get_otherList().QUESTMAP.put(key++, value);
						}
					}
				}
			}

			if (pc.get_otherList().QUESTMAP.size() <= 0) {
				// 很抱歉!!目前你的任務已經全部完成!
				pc.sendPackets(new S_NPCTalkReturn(objid, "y_q_not4"));

			} else {
				final L1ActionShowHtml show = new L1ActionShowHtml(pc);
				show.showQuestMap(0);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 全部任務
	 * 
	 * @param pc
	 * @param objid
	 */
	public static void showQuestAll(final L1PcInstance pc, final int objid) {
		try {
			// 清空暫存任務清單
			pc.get_otherList().QUESTMAP.clear();

			int key = 0;
			for (int i = QuestTable.MINQID; i <= QuestTable.MAXQID; i++) {
				final L1Quest value = QuestTable.get().getTemplate(i);
				if (value != null) {
					// 可執行職業判斷
					if (value.check(pc)) {
						pc.get_otherList().QUESTMAP.put(key++, value);
					}
				}
			}
			final L1ActionShowHtml show = new L1ActionShowHtml(pc);
			show.showQuestMap(0);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 展示指定任務進度資料
	 * 
	 * @param key
	 */
	private void showPage(final int key) {
		try {
			final L1Quest quest = _pc.get_otherList().QUESTMAP.get(key);
			_pc.setTempID(quest.get_id());
			String over = null;
			// 該任務完成
			if (_pc.getQuest().isEnd(quest.get_id())) {
				over = "完成任務";// 完成任務!
			} else {
				over = _pc.getQuest().get_step(quest.get_id()) + " / " + quest.get_difficulty();
			}

			final String[] info = new String[] { quest.get_questname(), // 任務名稱
					Integer.toString(quest.get_questlevel()), // 任務等級
					over, // 任務進度
					""// 額外說明
			};
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qi1", info));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 展開召喚控制選單
	 * 
	 * @param pc
	 * @param s
	 */
	private void summonMonster(final L1PcInstance pc, final String s) {
		try {
			final SkillMode skillMode = new SUMMON_MONSTER();
			skillMode.start(pc, s);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	private int classifyMapId(int mapId) {
	    if (mapId == 0) return 0;
	    if (mapId == 4) return 1;
	    if (mapId >= 7 && mapId <= 13) return 2;
	    if (mapId >= 59 && mapId <= 63) return 3;
	    if (mapId == 70) return 4;
	    if (mapId >= 72 && mapId <= 74) return 5;
	    if (mapId == 303) return 6;
	    if (mapId == 430) return 7;
	    if (mapId >= 452 && mapId <= 537) return 8;
	    if (mapId == 558) return 9;
	    if (mapId == 782) return 10;
	    if (mapId >= 1700 && mapId <= 1703) return 11;
	    if (mapId >= 3301 && mapId <= 3310) return 12;
	    if (mapId == 37 || mapId == 65 || mapId == 67) return 13;
	    return 14;
	}
}


