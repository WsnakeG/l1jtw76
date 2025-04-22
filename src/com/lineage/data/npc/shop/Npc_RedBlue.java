package com.lineage.data.npc.shop;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.world.World;
import com.lineage.data.event.RedBlueSet;
import com.lineage.config.ConfigOther;
import java.util.Random;

/**
 * 
 * 自創陣營戰系統管理員
 * 
 * @author Erics4179
 * 
 */

public class Npc_RedBlue extends NpcExecutor {

	private Npc_RedBlue() {
	}

	public static NpcExecutor get() {
		return new Npc_RedBlue();
	}

	public void broadcastPacketAll(final ServerBasePacket packet) {
		try {
			for (L1PcInstance pc : World.get().getAllPlayers()) {
				if (pc != null)
					pc.sendPackets(packet);
			}

		} catch (final Exception e) {
		}
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "join_redblue"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc,
			final String cmd, final long amount) {
		if (!RedBlueSet.START) {
			pc.sendPackets(new S_ServerMessage("\\aH目前活動尚未開放。"));
			return;
		}
		Random rnd = new Random();
		if (cmd.equalsIgnoreCase("join_redblue_room1")) {
			if (pc.get_redbluejoin() != 0) {
				pc.sendPackets(new S_ServerMessage("\\aD您已經報名過了：\\aG目前報名人數: "+RedBlueSet._room1pc.size()+" 人。"));
				return;
			}
			if (RedBlueSet.step1 > 0) {
				pc.sendPackets(new S_ServerMessage("\\aE活動已開始，目前無法報名。"));
				return;
			}
			if (RedBlueSet.step1 == 4 && RedBlueSet.cleartime1 > 0) {
				pc.sendPackets(new S_ServerMessage("清理中，請稍後"
						+ RedBlueSet.cleartime1 + "秒。"));
				return;
			}
			if (ConfigOther.RedBlueJoin_itemid != 0
					&& ConfigOther.RedBlueJoin_count != 0
					&& !pc.getInventory().checkItem(
							ConfigOther.RedBlueJoin_itemid,
							ConfigOther.RedBlueJoin_count)) {
				pc.sendPackets(new S_ServerMessage("\\aG報名道具不足。"));
				return;
			}
			if (pc.getLevel() < ConfigOther.RedBlueLv_min
					|| pc.getLevel() > ConfigOther.RedBlueLv_max) {
				pc.sendPackets(new S_ServerMessage("\\aG您的等級無法報名。"));
				return;
			}
			if (RedBlueSet._room1pc.size() < ConfigOther.RedBluePc_amount * 2) {
				RedBlueSet._room1pc.add(pc);
				int team = rnd.nextInt(2) + 1;
				if (team == 1) {
					if (RedBlueSet._room1red.size() < ConfigOther.RedBluePc_amount) {
						RedBlueSet._room1red.add(pc);
						pc.set_redbluejoin(11);
						pc.set_redblueroom(1);
						pc.set_redbluepoint(ConfigOther.RedBlueStart_point);
						if (ConfigOther.RedBlueJoin_itemid != 0
								&& ConfigOther.RedBlueJoin_count != 0) {
							pc.getInventory().consumeItem(
									ConfigOther.RedBlueJoin_itemid,
									ConfigOther.RedBlueJoin_count);
						}
						pc.sendPackets(new S_ServerMessage("\\aF已完成報名手續。"));
					} else if (RedBlueSet._room1blue.size() < ConfigOther.RedBluePc_amount) {
						RedBlueSet._room1blue.add(pc);
						pc.set_redbluejoin(12);
						pc.set_redblueroom(1);
						pc.set_redbluepoint(ConfigOther.RedBlueStart_point);
						if (ConfigOther.RedBlueJoin_itemid != 0
								&& ConfigOther.RedBlueJoin_count != 0) {
							pc.getInventory().consumeItem(
									ConfigOther.RedBlueJoin_itemid,
									ConfigOther.RedBlueJoin_count);
						}
						pc.sendPackets(new S_ServerMessage("\\aF已完成報名手續。"));
					} else
						pc.sendPackets(new S_ServerMessage("\\aD陣營戰 報名人數已滿。"));
				} else if (team == 2) {
					if (RedBlueSet._room1blue.size() < ConfigOther.RedBluePc_amount) {
						RedBlueSet._room1blue.add(pc);
						pc.set_redbluejoin(12);
						pc.set_redblueroom(1);
						pc.set_redbluepoint(ConfigOther.RedBlueStart_point);
						if (ConfigOther.RedBlueJoin_itemid != 0
								&& ConfigOther.RedBlueJoin_count != 0) {
							pc.getInventory().consumeItem(
									ConfigOther.RedBlueJoin_itemid,
									ConfigOther.RedBlueJoin_count);
						}
					} else if (RedBlueSet._room1red.size() < ConfigOther.RedBluePc_amount) {
						RedBlueSet._room1red.add(pc);
						pc.set_redbluejoin(11);
						pc.set_redblueroom(1);
						pc.set_redbluepoint(ConfigOther.RedBlueStart_point);
						if (ConfigOther.RedBlueJoin_itemid != 0
								&& ConfigOther.RedBlueJoin_count != 0) {
							pc.getInventory().consumeItem(
									ConfigOther.RedBlueJoin_itemid,
									ConfigOther.RedBlueJoin_count);
						}
					} else
						pc.sendPackets(new S_ServerMessage("\\aD陣營戰 報名人數已滿。"));
				}
			} else
				pc.sendPackets(new S_ServerMessage("\\aD陣營戰 報名人數已滿。"));
		} else if (cmd.equalsIgnoreCase("join_redblue_room2")) {
			if (pc.get_redbluejoin() != 0) {
				pc.sendPackets(new S_ServerMessage("\\aD您已經報名過了：\\aG目前報名人數: "+RedBlueSet._room1pc.size()+" 人。"));
				return;
			}
			if (RedBlueSet.step2 > 0) {
				pc.sendPackets(new S_ServerMessage("\\aE活動已開始，目前無法報名。"));
				return;
			}
			if (RedBlueSet.step2 == 4 && RedBlueSet.cleartime2 > 0) {
				pc.sendPackets(new S_ServerMessage("清理中，請稍後"
						+ RedBlueSet.cleartime2 + "秒。"));
				return;
			}
			if (ConfigOther.RedBlueJoin_itemid != 0
					&& ConfigOther.RedBlueJoin_count != 0
					&& !pc.getInventory().checkItem(
							ConfigOther.RedBlueJoin_itemid,
							ConfigOther.RedBlueJoin_count)) {
				pc.sendPackets(new S_ServerMessage("\\aG報名道具不足。"));
				return;
			}
			if (pc.getLevel() < ConfigOther.RedBlueLv_min
					|| pc.getLevel() > ConfigOther.RedBlueLv_max) {
				pc.sendPackets(new S_ServerMessage("\\aG您的等級無法報名。"));
				return;
			}
			if (RedBlueSet._room2pc.size() < ConfigOther.RedBluePc_amount * 2) {
				RedBlueSet._room2pc.add(pc);
				int team = rnd.nextInt(2) + 1;
				if (team == 1) {
					if (RedBlueSet._room2red.size() < ConfigOther.RedBluePc_amount) {
						RedBlueSet._room2red.add(pc);
						pc.set_redbluejoin(21);
						pc.set_redblueroom(2);
						pc.set_redbluepoint(ConfigOther.RedBlueStart_point);
						if (ConfigOther.RedBlueJoin_itemid != 0
								&& ConfigOther.RedBlueJoin_count != 0) {
							pc.getInventory().consumeItem(
									ConfigOther.RedBlueJoin_itemid,
									ConfigOther.RedBlueJoin_count);
						}
					} else if (RedBlueSet._room2blue.size() < ConfigOther.RedBluePc_amount) {
						RedBlueSet._room2blue.add(pc);
						pc.set_redbluejoin(22);
						pc.set_redblueroom(2);
						pc.set_redbluepoint(ConfigOther.RedBlueStart_point);
						if (ConfigOther.RedBlueJoin_itemid != 0
								&& ConfigOther.RedBlueJoin_count != 0) {
							pc.getInventory().consumeItem(
									ConfigOther.RedBlueJoin_itemid,
									ConfigOther.RedBlueJoin_count);
						}
					} else
						pc.sendPackets(new S_ServerMessage("\\aD陣營戰 報名人數已滿。"));
				} else if (team == 2) {
					if (RedBlueSet._room2blue.size() < ConfigOther.RedBluePc_amount) {
						RedBlueSet._room2blue.add(pc);
						pc.set_redbluejoin(22);
						pc.set_redblueroom(2);
						pc.set_redbluepoint(ConfigOther.RedBlueStart_point);
						if (ConfigOther.RedBlueJoin_itemid != 0
								&& ConfigOther.RedBlueJoin_count != 0) {
							pc.getInventory().consumeItem(
									ConfigOther.RedBlueJoin_itemid,
									ConfigOther.RedBlueJoin_count);
						}
						pc.sendPackets(new S_ServerMessage("\\aF已完成報名手續。"));
					} else if (RedBlueSet._room2red.size() < ConfigOther.RedBluePc_amount) {
						RedBlueSet._room2red.add(pc);
						pc.set_redbluejoin(21);
						pc.set_redblueroom(2);
						pc.set_redbluepoint(ConfigOther.RedBlueStart_point);
						if (ConfigOther.RedBlueJoin_itemid != 0
								&& ConfigOther.RedBlueJoin_count != 0) {
							pc.getInventory().consumeItem(
									ConfigOther.RedBlueJoin_itemid,
									ConfigOther.RedBlueJoin_count);
						}
						pc.sendPackets(new S_ServerMessage("\\aF已完成報名手續。"));
					} else
						pc.sendPackets(new S_ServerMessage("\\aD陣營戰 報名人數已滿。"));
				}
			} else
				pc.sendPackets(new S_ServerMessage("\\aD陣營戰 報名人數已滿。"));
		}
	}
}