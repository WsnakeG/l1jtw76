package com.lineage.data.npc.event;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.npc.teleport.Npc_Teleport;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.datatables.lock.CharacterC1Reading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1TeleportLoc;
import com.lineage.server.templates.L1User_Power;

/**
 * 陣營管理員<BR>
 * 60037<BR>
 * 
 * @author daien
 */
public class Npc_Camp extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Camp.class);

	private Npc_Camp() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Camp();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			String type = "無";
			String score = "0";
			if ((pc.get_c_power() != null) && (pc.get_c_power().get_c1_type() != 0)) {
				type = C1_Name_Table.get().get(pc.get_c_power().get_c1_type());
				score = String.valueOf(pc.get_other().get_score());
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_camp_01", new String[] { type, score }));

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_camp_00"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			boolean isClose = false;
			if (cmd.equalsIgnoreCase("exit01")) {// 退出目前陣營
				if (pc.get_c_power() != null) {
					if (pc.get_c_power().get_c1_type() == 0) {
						pc.sendPackets(new S_ServerMessage("\\aL您目前沒有陣營"));

					} else {
						pc.get_c_power().set_c1_type(0);
						pc.get_c_power().set_note("無");
						pc.get_other().set_score(0);

						CharacterC1Reading.get().updateCharacterC1(pc.getId(), 0, "無");

						pc.get_c_power().set_power(pc, false);
						// 改變顯示
						pc.sendPacketsAll(new S_ChangeName(pc, true));
						// 資料紀錄
						pc.save();
						pc.sendPackets(new S_ServerMessage("\\aL您已選擇退出目前的陣營"));
					}

				} else {
					pc.sendPackets(new S_ServerMessage("\\aL您目前沒有選擇加入任何陣營"));
				}
				isClose = true;

			} else if (cmd.equalsIgnoreCase("exit02")) {// 取消
				isClose = true;

			} else if (cmd.matches("[0-9]+")) {// 傳送-數字選項
				final String pagecmd = pc.get_other().get_page() + cmd;
				Npc_Teleport.teleport(pc, npc, Integer.valueOf(pagecmd));

			} else if (cmd.startsWith("add")) {// 加入-國度
				final String newString = cmd.substring(3);
				add_type(newString, pc);
				isClose = true;

			} else {// 傳送-國度
				pc.get_other().set_page(0);
				final HashMap<Integer, L1TeleportLoc> teleportMap = NpcTeleportTable.get().get_teles(cmd);
				if (teleportMap != null) {
					if (teleportMap.size() <= 0) {
						// 1,447：目前暫不開放。
						pc.sendPackets(new S_ServerMessage(1447));
						return;
					}
					pc.get_otherList().teleport(teleportMap);
					Npc_Teleport.showPage(pc, npc, 0);

				} else {
					// 1,447：目前暫不開放。
					pc.sendPackets(new S_ServerMessage(1447));
				}
			}

			if (isClose) {
				pc.sendPackets(new S_CloseList(pc.getId()));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void add_type(final String cmd, final L1PcInstance pc) {
		final int type = Integer.parseInt(cmd);
		final String typeName = C1_Name_Table.get().get(type);
		// 151221新增限制等級低於55以上不能加入陣營
		if (pc.getLevel() < ConfigOther.CAMPLEVEL) {
			pc.sendPackets(new S_ServerMessage("\\aG等級太低！您還不能選擇陣營.....。"));
			return;
		}
		if (pc.get_c_power() == null) {
			final L1User_Power power = new L1User_Power();
			power.set_object_id(pc.getId());
			power.set_c1_type(type);
			power.set_note(typeName);
			pc.set_c_power(power);
			CharacterC1Reading.get().storeCharacterC1(pc);
		} else {
			pc.get_c_power().set_c1_type(type);
			pc.get_c_power().set_note(typeName);
			CharacterC1Reading.get().updateCharacterC1(pc.getId(), type, typeName);
		}
		pc.get_c_power().set_power(pc, false);

		// 改變顯示
		pc.sendPacketsAll(new S_ChangeName(pc, true));
		// 151221新增加入陣營後給予特效顯示
		final S_SkillSound sound = new S_SkillSound(pc.getId(), ConfigOther.CAMPGFX);//12335
		pc.sendPackets(sound);
		pc.sendPackets(new S_ServerMessage("恭喜您已成功加入強大的陣營:\\aL " + typeName));
	}
}